package com.office14.coffeedose.viewmodels

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.coffeedose.R
import com.office14.coffeedose.domain.defaultuser.DefaultUser
import com.office14.coffeedose.domain.entity.LastOrderStatus
import com.office14.coffeedose.domain.entity.Order
import com.office14.coffeedose.domain.entity.OrderStatus
import com.office14.coffeedose.plugins.PreferencesRepositoryImpl
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.exception.RequireAuthHandler
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.usecase.*
import com.office14.coffeedose.extensions.mutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MenuInfoViewModel @Inject constructor(
    application: Application,
    private val getCurrentQueueOrderByUser:GetCurrentQueueOrderByUser,
    private val refreshOrderDetailsOnUserLogIn: RefreshOrderDetailsOnUserLogIn,
    private val longPollingLastOrderStatus : LongPollingLastOrderStatus,
    private val getUnAttachedOrderDetailsCount: GetUnAttachedOrderDetailsCount,
    private val requireAuthHandler : RequireAuthHandler,
    private val logIn: LogIn,
    private val logOutUK: LogOut
    //private val orderDetailsRepository: OrderDetailsRepositoryImpl,
    //private val ordersRepository: OrdersRepositoryImpl,
    //private val usersRepository: UsersRepositoryImpl
) : BaseViewModel(application) {

    //private val viewModelJob = Job()
    //private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val pollingJob = Job()
    private val pollingScope = CoroutineScope(pollingJob + Dispatchers.Main)

    var email = PreferencesRepositoryImpl.getUserEmail()!!
    //private val emailChannel = ConflatedBroadcastChannel<String>()
    //private val emailFlow = emailChannel.asFlow()

    private var app: Application = application

    private val _orderDetailsCount : MutableLiveData<Int> = mutableLiveData(0)
    val orderDetailsCount : LiveData<Int> = _orderDetailsCount


    private val _authCallback : MutableLiveData<()->Unit> = mutableLiveData()
    val authCallback : LiveData<()->Unit> = _authCallback
    //val orderDetailsCount = orderDetailsRepository.unAttachedOrderDetailsCount(emailFlow).asLiveData()
    //private val unattachedOrderDetails = orderDetailsRepository.unAttachedOrderDetails(emailFlow).asLiveData()

    //private var isPolling = false

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
        getUnAttachedOrderDetailsCount()
        handleRequireAuthHandler()
        //if (email != EMPTY_STRING)
        //emailChannel.offer(email)

        startLongPollingLastOrderStatus()
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

        getCurrentQueueOrderByUser(UseCaseBase.None()){
            it.mapLatest { either ->
                either.fold(::left,::right)
            }.launchIn(viewModelScope)
        }
    }

    //var user: User? = null

    fun refreshOrderDetailsByUser(newEmail:String, oldEmail:String) {

        fun left(failure: Failure){

        }
        fun right(none: UseCaseBase.None){

        }

        //TODO
        //cancelJob()
        refreshOrderDetailsOnUserLogIn(RefreshOrderDetailsOnUserLogIn.Params(newEmail,oldEmail)){ either ->
            either.fold(::left,::right)
        }

        if (newEmail != DefaultUser.DEFAULT_EMAIL)
            startLongPollingLastOrderStatus()

        /*val oldEmail = email
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


    private fun startLongPollingLastOrderStatus(){

        fun left(failure:Failure){
            if (failure is Failure.NoData)
                cancelPollingJob()
        }

        fun right(status: LastOrderStatus){
            if (status.statusName.toLowerCase() == "ready")
                cancelPollingJob()
        }
        if(PreferencesRepositoryImpl.getUserEmail() != DefaultUser.DEFAULT_EMAIL)
            longPollingLastOrderStatus(LongPollingLastOrderStatus.Params(::startLongPollingLastOrderStatus)){
                it.mapLatest { either -> either.fold(::left,::right) }.launchIn(pollingScope)
            }
    }

    fun restartLongPolling() {
       cancelPollingJob()
        /*if (isPolling)
            _job.cancel()*/

        startLongPollingLastOrderStatus()
    }

    private fun getUnAttachedOrderDetailsCount(){

        fun right(count:Int){
            _orderDetailsCount.value = count
        }

        getUnAttachedOrderDetailsCount(UseCaseBase.None()){
            it.mapLatest { either ->
                either.fold({},::right)
            }.launchIn(viewModelScope)
        }
    }

    //private lateinit var _job: Job

    //private fun longPollingOrderStatus() {

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
    //}

    fun cancelPollingJob() {
        if (pollingJob.isActive)
            pollingJob.cancel()
        /*if (isPolling) {
            _job.cancel()
            isPolling = false
        }*/
    }

    fun logIn(newEmail: String, newIdToken:String){
        logIn(LogIn.Params(newEmail,newIdToken))
    }

    fun logOut(){
        logOutUK(UseCaseBase.None())
    }

    fun updateUser() {
        //TODO
        /*viewModelScope.launch {
            user?.let {
                usersRepository.updateUser(it)
            }
        }*/
    }

    private fun handleRequireAuthHandler(){
        requireAuthHandler.getRequiredAsFlow().mapLatest {
            _authCallback.value = it
        }.launchIn(viewModelScope)
    }


    override fun onCleared() {
        super.onCleared()
        //viewModelJob.cancel()
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
