package com.office14.coffeedose.viewmodels

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.coffeedose.R
import com.office14.coffeedose.domain.Order
import com.office14.coffeedose.domain.OrderStatus
import com.office14.coffeedose.domain.User
import com.office14.coffeedose.extensions.mutableLiveData
import com.office14.coffeedose.repository.OrderDetailsRepository
import com.office14.coffeedose.repository.OrdersRepository
import com.office14.coffeedose.repository.PreferencesRepository
import com.office14.coffeedose.repository.PreferencesRepository.EMPTY_STRING
import com.office14.coffeedose.repository.UsersRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class MenuInfoViewModel @Inject constructor(
    application: Application, private val orderDetailsRepository: OrderDetailsRepository,
    private val ordersRepository: OrdersRepository,
    private val usersRepository: UsersRepository
) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val pollingJob = Job()
    private val pollingScope = CoroutineScope(pollingJob + Dispatchers.Main)

    val email = mutableLiveData(EMPTY_STRING)
    private var app: Application = application

    val orderDetailsCount = orderDetailsRepository.unAttachedOrderDetailsCount(email)
    private val unattachedOrderDetails = orderDetailsRepository.unAttachedOrderDetails(email)

    private var isPolling = false

    val order: LiveData<Order> =
        Transformations.map(ordersRepository.getCurrentQueueOrderByUser(email)) {
            if (it != null) {
                if (!isPolling)
                    longPollingOrderStatus()
                return@map it
            }
            return@map null

        }

    init {
        refreshOrderDetailsByUser()
    }

    val currentOrderBadgeColor = Transformations.map(ordersRepository.queueOrderStatus(email)) {
        return@map when (it) {
            OrderStatus.READY -> ContextCompat.getColor(app, R.color.color_green)
            OrderStatus.COOKING -> ContextCompat.getColor(app, R.color.color_yellow)
            OrderStatus.FAILED -> ContextCompat.getColor(app, R.color.color_red)
            else -> ContextCompat.getColor(app, R.color.color_black)
        }
    }

    var user: User? = null

    fun refreshOrderDetailsByUser() {

        cancelJob()

        val oldEmail = email.value
        email.value = PreferencesRepository.getUserEmail()!!

        viewModelScope.launch {

            if (email.value != EMPTY_STRING) {

                user = usersRepository.getUserByEmail(email.value!!)

                ordersRepository.getLastOrderForUserAndPutIntoDB(
                    PreferencesRepository.getIdToken(),
                    email.value!!
                )

                if (oldEmail == EMPTY_STRING) {
                    val unattachedDetailsForUser =
                        orderDetailsRepository.unattachedOrderDetailsForUser(email.value!!)

                    val unattachedDetailsFree =
                        orderDetailsRepository.unattachedOrderDetailsWithoutUser()

                    if (unattachedDetailsFree.isNotEmpty()) {
                        if (unattachedDetailsForUser.isNotEmpty())
                            orderDetailsRepository.deleteOrderDetailsByEmail(email.value!!)

                        orderDetailsRepository.updateUnattachedOrderDetailsWithEmail(email.value!!)
                    }
                }
            } else {
                user = null
            }
        }

        if (email.value != EMPTY_STRING)
            restartLongPolling()
    }

    fun restartLongPolling() {
        if (isPolling)
            _job.cancel()

        longPollingOrderStatus()
    }

    private lateinit var _job: Job

    private fun longPollingOrderStatus() {

        isPolling = true

        _job = pollingScope.launch {

            while (isActive) {
                ordersRepository.refreshLastOrderStatus(
                    PreferencesRepository.getIdToken(),
                    email.value!!
                )
            }
        }

        order.observeForever {
            if (it?.statusName?.toLowerCase() == "ready") {
                cancelJob()
            }
        }
    }

    private fun cancelJob() {
        if (isPolling) {
            _job.cancel()
            isPolling = false
        }
    }

    fun updateUser() {
        viewModelScope.launch {
            user?.let {
                usersRepository.updateUser(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        pollingJob.cancel()
    }

    fun updateFcmDeviceToken() {
        viewModelScope.launch {
            ordersRepository.updateFcmDeviceToken(
                PreferencesRepository.getDeviceID(),
                PreferencesRepository.getFcmRegToken()!!,
                PreferencesRepository.getIdToken()!!
            )
        }

    }

    fun deleteFcmDeviceTokenOnLogOut(deviceId: String, idToken: String) {
        viewModelScope.launch {
            ordersRepository.deleteFcmDeviceTokenOnLogOut(deviceId, idToken)
        }
    }
}
