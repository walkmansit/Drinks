package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.LastOrderStatus
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.exception.RequireAuthHandler
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.OrdersRepository
import com.office14.coffeedose.domain.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class LongPollingLastOrderStatus @Inject constructor (
        private val ordersRepository: OrdersRepository,
        private val userRepository: UserRepository,
        private val authHandler: RequireAuthHandler
        )
    : UseCaseFlow<LastOrderStatus, LongPollingLastOrderStatus.Params>() {

    data class Params(val authCallBack:() -> Unit)

    private fun getLastOrderFlowForIdToken(idToken: String,authCallBack:() -> Unit) :Flow<Either<Failure, LastOrderStatus>> = flow {
        fun pushAuthRequired(failure: Failure){
            if (failure is Failure.AuthotizationRequired)
                authHandler.call(authCallBack)
        }

        while (true) {
            val result = ordersRepository.refreshLastOrderStatus(idToken)
            result.fold(::pushAuthRequired) {}
            delay(5000)
            emit(result)
        }
    }

    override suspend fun run(params: Params): Flow<Either<Failure, LastOrderStatus>> = userRepository.getCurrentUserAsFlow().flatMapLatest {
        user -> getLastOrderFlowForIdToken(user.idToken,params.authCallBack)
    }
}