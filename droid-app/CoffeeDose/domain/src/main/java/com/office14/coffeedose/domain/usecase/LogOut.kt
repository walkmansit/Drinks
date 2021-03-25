package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.UserA
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.UserRepository
import javax.inject.Inject

class LogOut @Inject constructor (private val userRepository: UserRepository) : UseCase<UseCaseBase.None, UseCaseBase.None>() {

    override suspend fun run(params: None): Either<Failure, None> {
        userRepository.setCurrentUser(UserA.default())
        return Either.Right(None())
    }
}