package com.office14.coffeedose.domain.repository

import com.office14.coffeedose.domain.entity.Addin
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseBase
import kotlinx.coroutines.flow.Flow

interface AddinsRepository {
    val addins: Flow<Either<Failure,List<Addin>>>
    suspend fun refreshAddins() : Either<Failure, UseCaseBase.None>
}