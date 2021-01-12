package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.defaultuser.DefaultUser
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.repository.PreferencesRepository
import javax.inject.Inject

class ClearOrderDetails @Inject constructor (private val orderDetailsRepository: OrderDetailsRepository,private val preferencesRepository: PreferencesRepository) : UseCase<UseCaseBase.None, UseCaseBase.None>() {
    override suspend fun run(params: None): Either<Failure, None> {
        val email = preferencesRepository.getUserEmail()
        if (email == DefaultUser.DEFAULT_EMAIL)
            orderDetailsRepository.deleteUnAttached()
        else
            orderDetailsRepository.deleteOrderDetailsByEmail(email!!)

        return Either.Right(None())
    }
}