package com.office14.coffeedose.domain.repository

import com.office14.coffeedose.domain.entity.CoffeeSize
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseBase
import kotlinx.coroutines.flow.Flow

interface SizesRepository {
    fun getSizes(drinkId: Int) : Flow<Either<Failure, List<CoffeeSize>>>
    suspend fun refreshSizes(drinkId: Int) : Either<Failure,UseCaseBase.None>
}