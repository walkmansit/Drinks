package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.Order
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCurrentQueueOrderByUser @Inject constructor (private val ordersRepository: OrdersRepository) : UseCaseFlow<Order, GetCurrentQueueOrderByUser.Params>() {

    data class Params(val emaiFlow: Flow<String>)

    override suspend fun run(params: GetCurrentQueueOrderByUser.Params): Flow<Either<Failure, Order>> =
        ordersRepository.getCurrentQueueOrderByUser(params.emaiFlow).map { if (it == null) Either.Left(Failure.NoData()) else Either.Right(it) }
}