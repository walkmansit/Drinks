package com.office14.coffeedose.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.office14.coffeedose.domain.entity.Order
import com.office14.coffeedose.domain.entity.OrderInfo
import com.office14.coffeedose.extensions.mutableLiveData
import com.office14.coffeedose.plugins.PreferencesRepositoryImpl
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.usecase.GetCurrentQueueOrderByUser
import com.office14.coffeedose.domain.usecase.GetLastOrderInfo
import com.office14.coffeedose.domain.usecase.MarkOrderAsFinishedForUser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class OrderAwaitingViewModel @Inject constructor(
    application: Application,
    private val getCurrentQueueOrderByUser: GetCurrentQueueOrderByUser,
    private val getLastOrderInfo: GetLastOrderInfo,
    private val markOrderAsFinishedForUser: MarkOrderAsFinishedForUser
    //private val ordersRepository: OrdersRepositoryImpl
) : BaseViewModel(application) {


    private var orderId = mutableLiveData(-1)
    private val email = PreferencesRepositoryImpl.getUserEmail()!!

    private val emailChannel = ConflatedBroadcastChannel<String>()

    private val _queueOrderStatus : MutableLiveData<String> = mutableLiveData()
    val queueOrderStatus : LiveData<String> = _queueOrderStatus
        //ordersRepository.queueOrderStatus(emailChannel.asFlow()).asLiveData()

    private val _orderInfo = mutableLiveData<OrderInfo?>(null)
    var orderInfo : LiveData<OrderInfo?> = _orderInfo

    private val _order: MutableLiveData<Order?> = mutableLiveData()
    val order: LiveData<Order?> =_order


    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _navigateToCoffeeList = MutableLiveData<Boolean>()
    val navigateToCoffeeList: LiveData<Boolean>
        get() = _navigateToCoffeeList


    private val viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

        getCurrentQueueOrderWithStatusByUser()
        //if(email != EMPTY_STRING)
            emailChannel.offer(email)

        getOrderInfo()
    }

    private fun getCurrentQueueOrderWithStatusByUser(){

        fun noStatus(failure:Failure){
            _queueOrderStatus.value = "unknown status"
            _order.value = null
        }
        fun rightStatus(order: Order){
            _queueOrderStatus.value = order.statusCode
            _order.value = order
            getOrderInfo() //get load order info from api on last order update
        }

        getCurrentQueueOrderByUser(UseCaseBase.None()){
            it.mapLatest { either ->
                either.fold(::noStatus,::rightStatus)
            }.launchIn(viewModelScope)
        }
    }

    fun isRootVisible() : LiveData<Boolean> {
        val result = MediatorLiveData<Boolean>()

        val update = {
            result.value = _isLoading.value == false && _order.value != null
        }

        result.addSource(isLoading){ update.invoke() }
        result.addSource(orderInfo){ update.invoke() }

        return result
    }

    private fun getOrderInfo() {
        fun noInfo(failure:Failure){
            _orderInfo.value = null
            _isLoading.value = false
        }
        fun rightInfo(info:OrderInfo){
            _orderInfo.value = info
            _isLoading.value = false
        }
        getLastOrderInfo(GetLastOrderInfo.Params(::getOrderInfo)){
            it .mapLatest { either ->
                either.fold(::noInfo,::rightInfo)
            }
        }
    }

    /*private suspend fun getOrderId(): Int {
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
    }*/

    fun approve() {
        fun left(failure:Failure){
            //TODO show error
        }
        fun right(none: UseCaseBase.None){
            _navigateToCoffeeList.value = true
        }

        markOrderAsFinishedForUser(MarkOrderAsFinishedForUser.Params(email)){
            it.fold(::left,::right)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun doneNavigation() {
        _navigateToCoffeeList.value = false
    }

}