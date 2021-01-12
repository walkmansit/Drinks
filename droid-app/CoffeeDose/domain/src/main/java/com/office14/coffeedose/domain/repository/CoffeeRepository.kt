package com.office14.coffeedose.domain.repository

import com.office14.coffeedose.domain.entity.Coffee
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseBase
import kotlinx.coroutines.flow.Flow

interface CoffeeRepository {
    val drinks: Flow<Either<Failure,List<Coffee>>>
    suspend fun refreshDrinks() : Either<Failure, UseCaseBase.None>
}