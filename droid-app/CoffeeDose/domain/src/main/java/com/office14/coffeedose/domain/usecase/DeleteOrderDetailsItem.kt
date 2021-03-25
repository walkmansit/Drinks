package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.OrderDetail
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import javax.inject.Inject

class DeleteOrderDetailsItem @Inject constructor (private val orderDetailsRepository: OrderDetailsRepository) : UseCase<UseCaseBase.None, DeleteOrderDetailsItem.Params>() {

    data class Params(val item:OrderDetail)

    override suspend fun run(params: Params): Either<Failure, None> {
        orderDetailsRepository.delete(params.item)
        return Either.Right(None())
    }
}