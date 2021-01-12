package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.CoffeeRepository
import com.office14.coffeedose.domain.repository.OrdersRepository
import javax.inject.Inject

class MarkOrderAsFinishedForUser @Inject constructor (private val ordersRepository: OrdersRepository) : UseCase<UseCaseBase.None, MarkOrderAsFinishedForUser.Params>() {

    data class Params(val email: String)

    override suspend fun run(params: Params): Either<Failure, None> {
        ordersRepository.markAsFinishedForUser(params.email)
        return Either.Right(None())
    }
}