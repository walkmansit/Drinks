package com.office14.coffeedose.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.office14.coffeedose.domain.OrderDetailFull
import com.office14.coffeedose.extensions.mutableLiveData
import com.office14.coffeedose.network.HttpExceptionEx
import com.office14.coffeedose.repository.OrderDetailsRepository
import com.office14.coffeedose.repository.OrdersRepository
import com.office14.coffeedose.repository.PreferencesRepository
import com.office14.coffeedose.repository.PreferencesRepository.EMPTY_STRING
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class OrderDetailsViewModel @Inject constructor(
    application: Application, private val ordersRepository: OrdersRepository,
    private var orderDetailsRepository: OrderDetailsRepository
) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val email = PreferencesRepository.getUserEmail()!!
    private val emailChannel = ConflatedBroadcastChannel<String>()
    private val emailFlow = emailChannel.asFlow()

    private val _navigateOrderAwaiting = MutableLiveData<Boolean>()
    val navigateOrderAwaiting: LiveData<Boolean>
        get() = _navigateOrderAwaiting

    private val _forceLongPolling = MutableLiveData<Boolean>()
    val forceLongPolling: LiveData<Boolean>
        get() = _forceLongPolling

    val unAttachedOrders = orderDetailsRepository.unAttachedOrderDetails(emailFlow).asLiveData()

    var comment: String? = null

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _needLogin = MutableLiveData<Boolean>()
    val needLogIn: LiveData<Boolean>
        get() = _needLogin

    val isEmpty = Transformations.map(unAttachedOrders) {
        return@map it.isEmpty()
    }

    val hasOrderInQueue = Transformations.map(ordersRepository.getCurrentQueueOrderByUser(emailFlow).asLiveData()) {
        return@map it != null
    }

    init {
        //if (email != EMPTY_STRING)
            emailChannel.offer(email)
    }

    fun deleteOrderDetailsItem(item: OrderDetailFull) {
        viewModelScope.launch {
            orderDetailsRepository.delete(item.orderDetailInner)
        }
    }

    fun confirmOrder() {


        viewModelScope.launch {
            try {

                if (email != EMPTY_STRING) {
                    val order = ordersRepository.getCurrentNotFinishedOrderByUser(email!!)
                    if (order != null) {
                        _errorMessage.value = "Сначала закончите текущий заказ"
                        return@launch
                    }
                }

                val ordersForAdd =

                orderDetailsRepository.unAttachedOrderDetailsStraight(email)
                val newOrderId = ordersRepository.createOrder(
                    ordersForAdd,
                    comment,
                    PreferencesRepository.getIdToken(),
                    email
                )

                orderDetailsRepository.updateAttachedOrderDetailsWithOrderId(
                    email,
                    newOrderId
                )

                _forceLongPolling.value = true

                _navigateOrderAwaiting.value = true
            } catch (responseEx: HttpExceptionEx) {
                _errorMessage.value = responseEx.error.title
            } catch (ex: Exception) {
                if (ex.message?.contains("401") == true) {
                    _needLogin.value = true
                    _errorMessage.value = "Необходима авторизация"
                }
                /*else
                    _errorMessage.value = "Ошибка получения данных"*/
            }
        }
        //return true
    }

    fun doneLogin() {
        _needLogin.value = false
    }

    fun hideErrorMessage() {
        _errorMessage.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun clearOrderDetails() {
        viewModelScope.launch {
            if (email == EMPTY_STRING)
                orderDetailsRepository.deleteUnAttached()
            else
                orderDetailsRepository.deleteOrderDetailsByEmail(email!!)
        }
    }

    fun doneNavigating() {
        _navigateOrderAwaiting.value = false
    }

    fun doneForceLongPolling() {
        _forceLongPolling.value = false
    }
}