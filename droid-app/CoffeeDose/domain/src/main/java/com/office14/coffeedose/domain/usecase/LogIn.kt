package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.UserA
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.UserRepository
import javax.inject.Inject

class LogIn @Inject constructor (private val userRepository: UserRepository) : UseCase<UseCaseBase.None, LogIn.Params>() {

    data class Params(val email: String, val idToken:String)

    override suspend fun run(params: Params): Either<Failure, None> {
        userRepository.setCurrentUser(UserA(params.email,params.idToken))
        return Either.Right(None())
    }
}