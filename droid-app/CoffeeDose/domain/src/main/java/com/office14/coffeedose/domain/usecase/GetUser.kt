package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.UserA
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetUser @Inject constructor (private val userRepository: UserRepository) : UseCaseFlow<UserA, UseCaseBase.None>() {
    override suspend fun run(params: None): Flow<Either<Failure, UserA>> =
            userRepository.getCurrentUserAsFlow().mapLatest {
                Either.Right(it)
            }
}