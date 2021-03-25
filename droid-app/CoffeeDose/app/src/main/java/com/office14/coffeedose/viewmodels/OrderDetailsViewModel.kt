package com.office14.coffeedose.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.office14.coffeedose.domain.entity.OrderDetailFull
import com.office14.coffeedose.domain.entity.Order
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.usecase.*
import com.office14.coffeedose.extensions.mutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class OrderDetailsViewModel @Inject constructor(
    application: Application,
    //private val ordersRepository: OrdersRepositoryImpl,
    private val confirmOrder : ConfirmOrder,
    private val clearOrderDetails : ClearOrderDetails,
    private val deleteOrderDetailsItem: DeleteOrderDetailsItem,
    private val getUnattachedOrderDetails: GetUnattachedOrderDetails,
    private val getCurrentQueueOrderByUser : GetCurrentQueueOrderByUser
    //private var orderDetailsRepository: OrderDetailsRepositoryImpl
) : BaseViewModel(application) {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    //val email = PreferencesRepositoryImpl.getUserEmail()!!
    //private val emailChannel = ConflatedBroadcastChannel<String>()
    //private val emailFlow = emailChannel.asFlow()

    private val _navigateOrderAwaiting = MutableLiveData<Boolean>()
    val navigateOrderAwaiting: LiveData<Boolean> = _navigateOrderAwaiting

    private val _forceLongPolling = MutableLiveData<Boolean>()
    val forceLongPolling: LiveData<Boolean> =  _forceLongPolling

    private val _unAttachedOrders : MutableLiveData<List<OrderDetailFull>> = mutableLiveData()
    val unAttachedOrders : LiveData<List<OrderDetailFull>> = _unAttachedOrders

    var comment: String? = null

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val isEmpty = Transformations.map(unAttachedOrders) {
        return@map it.isEmpty()
    }

    private val _hasOrderInQueue : MutableLiveData<Boolean> = mutableLiveData()
    val hasOrderInQueue : LiveData<Boolean> = _hasOrderInQueue

    init {
        getUnattachedOrderDetails()
        getHasOrderInQueue()
        //emailChannel.offer(email)
    }

    fun deleteOrderDetailsItem(item: OrderDetailFull) {
        deleteOrderDetailsItem(DeleteOrderDetailsItem.Params(item.orderDetailInner)){
            it.fold({::handleFailure},{})
        }
    }

    private fun getHasOrderInQueue(){
        fun handleNoOrder(failure:Failure){
            _hasOrderInQueue.value = false
        }
        fun handleOrder(order:Order){
            _hasOrderInQueue.value = true
        }
        getCurrentQueueOrderByUser(UseCaseBase.None()){
            it.mapLatest { either -> either.fold(::handleNoOrder,::handleOrder) }.launchIn(viewModelScope)
        }
    }

    private fun getUnattachedOrderDetails(){
        fun handleUpdate(list : List<OrderDetailFull>) {
            _unAttachedOrders.value = list
        }

        getUnattachedOrderDetails(UseCaseBase.None()){
            it.mapLatest { either ->
                either.fold(::handleFailure,::handleUpdate)
            }.launchIn(viewModelScope)
        }
    }

    private fun handleConfirmOrder(param : UseCaseBase.None){
        _forceLongPolling.value = true
        _navigateOrderAwaiting.value = true
    }

    fun confirmOrder() {

        fun handleConfirmFailure(failure: Failure){
            _errorMessage.value = failure.message
        }

        confirmOrder(ConfirmOrder.Params(comment?:"",::confirmOrder)){
            it.fold(::handleConfirmFailure,::handleConfirmOrder)
        }


       /* viewModelScope.launch {
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
                    PreferencesRepositoryImpl.getIdToken(),
                    email
                )

                orderDetailsRepository.updateAttachedOrderDetailsWithOrderId(
                    email,
                    newOrderId
                )

                _forceLongPolling.value = true

                _navigateOrderAwaiting.value = true
            } catch (responseEx: com.office14.coffeedose.data.network.HttpExceptionEx) {
                _errorMessage.value = responseEx.error.title
            } catch (ex: Exception) {
                if (ex.message?.contains("401") == true) {
                    _needLogin.value = true
                    _errorMessage.value =
                }
            }
        }*/
    }

    fun hideErrorMessage() {
        _errorMessage.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun clearOrderDetails() {
        clearOrderDetails(UseCaseBase.None()){
            it.fold({::handleFailure},{})
        }
    }

    fun doneNavigating() {
        _navigateOrderAwaiting.value = false
    }

    fun doneForceLongPolling() {
        _forceLongPolling.value = false
    }
}