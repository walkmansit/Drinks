package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.OrderDetailFull
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetUnattachedOrderDetails @Inject constructor (private val orderDetailsRepository: OrderDetailsRepository) : UseCaseFlow<List<OrderDetailFull>, GetUnattachedOrderDetails.Params>() {

    data class Params(val emailFlow: Flow<String>)

    override suspend fun run(params: Params): Flow<Either<Failure, List<OrderDetailFull>>>  =
        orderDetailsRepository.unAttachedOrderDetails(params.emailFlow).combine(params.emailFlow){
                orders, email -> orders.filter { it.owner == email }
        }.mapLatest { Either.Right(it) }
            //.map { Either.Right(it) }

            /*.combine(emailFlow) {
                    orders, email -> orders.filter { it.orderDetail.owner == email }.map { it.toDomainModel() }
            }*/
}