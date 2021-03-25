package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.OrderInfo
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.exception.RequireAuthHandler
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.OrdersRepository
import com.office14.coffeedose.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class GetLastOrderInfo @Inject constructor (
        private val ordersRepository : OrdersRepository,
        private val userRepository: UserRepository,
        private val authHandler: RequireAuthHandler
        ) : UseCaseFlow<OrderInfo, GetLastOrderInfo.Params>() {

    data class Params(val authCallBack:()->Unit)

    override suspend fun run(params: Params): Flow<Either<Failure, OrderInfo>> = userRepository.getCurrentUserAsFlow().mapLatest { user ->

        fun pushAuthRequired(failure: Failure){
            if (failure is Failure.AuthotizationRequired)
                authHandler.call(params.authCallBack)
        }

        if(user.isDefault)
            return@mapLatest Either.Left(Failure.NoData())

        val result = ordersRepository.getLastOrderInfo(user.idToken, user.email)
        result.fold(::pushAuthRequired) {}
        return@mapLatest result
    }.flowOn(Dispatchers.IO)
}