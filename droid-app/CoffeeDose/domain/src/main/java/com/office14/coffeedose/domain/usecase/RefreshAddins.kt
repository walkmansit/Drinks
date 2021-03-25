package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.AddinsRepository
import javax.inject.Inject

class RefreshAddins @Inject constructor (private val addinsRepository: AddinsRepository ) : UseCase<UseCaseBase.None, UseCaseBase.None>() {
    override suspend fun run(params: None): Either<Failure, None> = addinsRepository.refreshAddins()
}