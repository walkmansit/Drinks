package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.defaultuser.DefaultUser
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.exception.RequireAuthHandler
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.functional.map
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.messages.Message
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.repository.OrdersRepository
import com.office14.coffeedose.domain.repository.PreferencesRepository
import javax.inject.Inject

class ConfirmOrder @Inject constructor (private val ordersRepository: OrdersRepository,
                                        private val orderDetailsRepository: OrderDetailsRepository,
                                        private val prefsRepository: PreferencesRepository,
                                        private val authHandler: RequireAuthHandler
                                        ) : UseCase<UseCaseBase.None, ConfirmOrder.Params>() {

    data class Params(val comment: String, val authCallBack:() -> Unit)

    override suspend fun run(params: Params): Either<Failure, None> {

        val email = prefsRepository.getUserEmail()
        fun pushAuthRequired(failure: Failure){
            if (failure is Failure.AuthotizationRequired)
                authHandler.call(params.authCallBack)
        }

        fun newOrderRight(newId:Int){
            orderDetailsRepository.updateAttachedOrderDetailsWithOrderId(email,newId)
        }


        if(email == DefaultUser.DEFAULT_EMAIL) return Either.Left(Failure.AuthotizationRequired(Message.OrderDetails.AuthotizationRequered))

        val order = ordersRepository.getCurrentNotFinishedOrderByUser(email)
        if (order != null) return Either.Left(Failure.DomainError(Message.OrderDetails.FinishCurrentOrderFirst))

        val ordersForAdd = orderDetailsRepository.unAttachedOrderDetailsStraight(email)
        val newOrderId = ordersRepository.createOrder(ordersForAdd,params.comment,prefsRepository.getIdToken(),email)
        newOrderId.fold(::pushAuthRequired,::newOrderRight)
        return newOrderId.map { None() }
    }

    /*
    * viewModelScope.launch {
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
                    _errorMessage.value = "Необходима авторизация"
                }
                /*else
                    _errorMessage.value = "Ошибка получения данных"*/
            }
        }*/
}