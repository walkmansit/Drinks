package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.Order
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.OrdersRepository
import com.office14.coffeedose.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCurrentQueueOrderByUser @Inject constructor (
        private val ordersRepository: OrdersRepository,
        private val userRepository: UserRepository
        ) : UseCaseFlow<Order, UseCaseBase.None>() {
    override suspend fun run(params: None): Flow<Either<Failure, Order>> = userRepository.getCurrentUserAsFlow().flatMapLatest { user ->
        ordersRepository.getCurrentQueueOrderByUser(user.email).map { if (it == null) Either.Left(Failure.NoData()) else Either.Right(it) }
    }
}