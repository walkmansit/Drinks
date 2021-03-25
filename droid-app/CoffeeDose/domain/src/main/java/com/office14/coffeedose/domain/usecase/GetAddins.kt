package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.Addin
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.AddinsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAddins @Inject constructor (private val addinsRepository: AddinsRepository) : UseCaseFlow<List<Addin>, UseCaseBase.None>() {
    override suspend fun run(params: None): Flow<Either<Failure, List<Addin>>>  = addinsRepository.addins.flowOn(Dispatchers.IO)
}
