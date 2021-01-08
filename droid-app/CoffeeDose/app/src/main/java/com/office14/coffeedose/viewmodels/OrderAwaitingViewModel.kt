package com.office14.coffeedose.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.office14.coffeedose.domain.Order
import com.office14.coffeedose.domain.OrderInfo
import com.office14.coffeedose.extensions.mutableLiveData
import com.office14.coffeedose.repository.OrderDetailsRepository
import com.office14.coffeedose.repository.OrdersRepository
import com.office14.coffeedose.repository.PreferencesRepository
import com.office14.coffeedose.repository.PreferencesRepository.EMPTY_STRING
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.switchMap
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class OrderAwaitingViewModel @Inject constructor(
    application: Application,
    private val ordersRepository: OrdersRepository,
    private val ordersDetailsRepository: OrderDetailsRepository
) : AndroidViewModel(application) {


    private var orderId = mutableLiveData(-1)

    private val emailChannel = ConflatedBroadcastChannel<String>()

    val queueOrderStatus = ordersRepository.queueOrderStatus(emailChannel.asFlow()).asLiveData()

    var orderInfo = mutableLiveData<OrderInfo>(null)

    val order: LiveData<Order?> = ordersRepository.getCurrentQueueOrderByUser(emailChannel.asFlow()).asLiveData()
        /*Transformations.map(ordersRepository.getCurrentQueueOrderByUser(email)) {
            if (it != null) {
                return@map it
            }
            return@map null

        }*/


    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _navigateToCoffeeList = MutableLiveData<Boolean>()
    val navigateToCoffeeList: LiveData<Boolean>
        get() = _navigateToCoffeeList


    private val viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        val email = PreferencesRepository.getUserEmail()!!
        //if(email != EMPTY_STRING)
            emailChannel.offer(email)

        getOrderInfo()
    }

    fun isRootVisible() : LiveData<Boolean> {
        val result = MediatorLiveData<Boolean>()

        val update = {
            result.value = _isLoading.value == false && orderInfo.value != null
        }

        result.addSource(isLoading){ update.invoke() }
        result.addSource(orderInfo){ update.invoke() }

        return result
    }

    private fun getOrderInfo() {
        try {
            viewModelScope.launch {
                orderInfo.value =
                    ordersRepository.getLastOrderInfo(PreferencesRepository.getIdToken()!!)
                _isLoading.value = false
            }
        } catch (ex: Exception) {
            Log.d("OrderAwaitingViewModel.getOrderInfo", ex.message)
        }
    }

    private suspend fun getOrderId(): Int {
        var result: Int
        withContext(Dispatchers.IO) {
            val order = ordersRepository.getCurrentQueueOrderNormal()
            result = order!!.id
        }
        return result
    }

    private fun initOrderId() {
        viewModelScope.launch {
            val order = ordersRepository.getCurrentQueueOrderNormal()
            order?.let { orderId.value = it.id }
        }
    }

    fun approve() {
        //PreferencesRepository.saveLastOrderId(-1)
        //PreferencesRepository.saveNavigateToOrderAwaitFrag(false)
        viewModelScope.launch {
            ordersRepository.markAsFinishedForUser(emailChannel.value)
        }
        _navigateToCoffeeList.value = true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun doneNavigation() {
        _navigateToCoffeeList.value = false
    }

}