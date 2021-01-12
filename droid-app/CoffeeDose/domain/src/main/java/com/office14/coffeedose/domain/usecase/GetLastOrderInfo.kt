package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.Coffee
import com.office14.coffeedose.domain.entity.OrderInfo
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.CoffeeRepository
import com.office14.coffeedose.domain.repository.OrdersRepository
import com.office14.coffeedose.domain.repository.PreferencesRepository
import javax.inject.Inject

class GetLastOrderInfo @Inject constructor (private val ordersRepository : OrdersRepository,private val preferencesRepository: PreferencesRepository) : UseCase<OrderInfo, UseCaseBase.None>() {
    override suspend fun run(params: None): Either<Failure, OrderInfo> = ordersRepository.getLastOrderInfo(preferencesRepository.getIdToken())
}