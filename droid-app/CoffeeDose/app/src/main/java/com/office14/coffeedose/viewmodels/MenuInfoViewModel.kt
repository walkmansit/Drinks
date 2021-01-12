package com.office14.coffeedose.viewmodels

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.coffeedose.R
import com.office14.coffeedose.domain.entity.Order
import com.office14.coffeedose.domain.entity.OrderStatus
import com.office14.coffeedose.domain.entity.User
import com.office14.coffeedose.plugins.PreferencesRepositoryImpl
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.usecase.GetCurrentQueueOrderByUser
import com.office14.coffeedose.extensions.mutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class MenuInfoViewModel @Inject constructor(
    application: Application,
    private val getCurrentQueueOrderByUser:GetCurrentQueueOrderByUser,
    //private val orderDetailsRepository: OrderDetailsRepositoryImpl,
    //private val ordersRepository: OrdersRepositoryImpl,
    //private val usersRepository: UsersRepositoryImpl
) : BaseViewModel(application) {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val pollingJob = Job()
    private val pollingScope = CoroutineScope(pollingJob + Dispatchers.Main)

    var email = PreferencesRepositoryImpl.getUserEmail()!!
    private val emailChannel = ConflatedBroadcastChannel<String>()
    private val emailFlow = emailChannel.asFlow()

    private var app: Application = application

    val orderDetailsCount = mutableLiveData(1)
    //val orderDetailsCount = orderDetailsRepository.unAttachedOrderDetailsCount(emailFlow).asLiveData()
    //private val unattachedOrderDetails = orderDetailsRepository.unAttachedOrderDetails(emailFlow).asLiveData()

    private var isPolling = false

    private val _order: MutableLiveData<Order?> = mutableLiveData()

    val order: LiveData<Order?> = _order
       /* Transformations.map(ordersRepository.getCurrentQueueOrderByUser(emailFlow).asLiveData()) {
            if (it != null) {
                if (!isPolling)
                    longPollingOrderStatus()
                return@map it
            }
            return@map null

        }*/

    init {
        getCurrentQueueOrderWithStatusByUser()
        //if (email != EMPTY_STRING)
            emailChannel.offer(email)

        refreshOrderDetailsByUser()
    }

    val currentOrderBadgeColor = Transformations.map(_order) {
        return@map when (it?.orderStatus) {
            OrderStatus.READY -> ContextCompat.getColor(app, R.color.color_green)
            OrderStatus.COOKING -> ContextCompat.getColor(app, R.color.color_yellow)
            OrderStatus.FAILED -> ContextCompat.getColor(app, R.color.color_red)
            else -> ContextCompat.getColor(app, R.color.color_black)
        }
    }

    private fun getCurrentQueueOrderWithStatusByUser(){

        fun left(failure: Failure){
            _order.value = null
        }
        fun right(order: Order){
            _order.value = order
        }

        getCurrentQueueOrderByUser(GetCurrentQueueOrderByUser.Params(emailChannel.asFlow())){
            it.mapLatest { either ->
                either.fold(::left,::right)
            }
        }
    }

    var user: User? = null

    fun refreshOrderDetailsByUser() {

        //TODO
        /*cancelJob()

        val oldEmail = email
        email = PreferencesRepositoryImpl.getUserEmail()!!

        viewModelScope.launch {

            if (email != EMPTY_STRING) {

                user = usersRepository.getUserByEmail(email!!)

                ordersRepository.getLastOrderForUserAndPutIntoDB(
                    PreferencesRepositoryImpl.getIdToken(),
                    email!!
                )

                if (oldEmail == EMPTY_STRING) {
                    val unattachedDetailsForUser =
                        orderDetailsRepository.unattachedOrderDetailsForUser(email!!)

                    val unattachedDetailsFree =
                        orderDetailsRepository.unattachedOrderDetailsWithoutUser()

                    if (unattachedDetailsFree.isNotEmpty()) {
                        if (unattachedDetailsForUser.isNotEmpty())
                            orderDetailsRepository.deleteOrderDetailsByEmail(email!!)

                        orderDetailsRepository.updateUnattachedOrderDetailsWithEmail(email!!)
                    }
                }
            } else {
                user = null
            }
        }

        if (email != EMPTY_STRING)
            restartLongPolling()*/
    }

    fun restartLongPolling() {
        if (isPolling)
            _job.cancel()

        longPollingOrderStatus()
    }

    private lateinit var _job: Job

    private fun longPollingOrderStatus() {

        //TODO
       /* isPolling = true

        _job = pollingScope.launch {

            while (isActive) {
                ordersRepository.refreshLastOrderStatus(
                    PreferencesRepositoryImpl.getIdToken()
                )
            }
        }

        order.observeForever {
            if (it?.statusName?.toLowerCase() == "ready") {
                cancelJob()
            }
        }*/
    }

    private fun cancelJob() {
        if (isPolling) {
            _job.cancel()
            isPolling = false
        }
    }

    fun updateUser() {
        //TODO
        /*viewModelScope.launch {
            user?.let {
                usersRepository.updateUser(it)
            }
        }*/
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        pollingJob.cancel()
    }

    fun updateFcmDeviceToken() {
        //TODO
      /*  viewModelScope.launch {
            ordersRepository.updateFcmDeviceToken(
                PreferencesRepositoryImpl.getDeviceID(),
                PreferencesRepositoryImpl.getFcmRegToken()!!,
                PreferencesRepositoryImpl.getIdToken()!!
            )
        }*/

    }

    fun deleteFcmDeviceTokenOnLogOut(deviceId: String, idToken: String) {
        //TODO
       /* viewModelScope.launch {
            ordersRepository.deleteFcmDeviceTokenOnLogOut(deviceId, idToken)
        }*/
    }
}
