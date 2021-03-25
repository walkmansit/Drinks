package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.repository.UserRepository
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class ClearOrderDetails @Inject constructor (
        private val orderDetailsRepository: OrderDetailsRepository,
        private  val userRepository: UserRepository
    ) : UseCase<UseCaseBase.None, UseCaseBase.None>() {
    override suspend fun run(params: None): Either<Failure, None> {
       userRepository.getCurrentUserAsFlow().collectLatest { user ->
           orderDetailsRepository.deleteOrderDetailsByEmail(user.email)
       }
       return Either.Right(None())
    }
}