package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.Coffee
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.CoffeeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDrinks @Inject constructor (private val coffeeRepository : CoffeeRepository) : UseCaseFlow<List<Coffee>, UseCaseBase.None>() {
    override suspend fun run(params: None): Flow<Either<Failure, List<Coffee>>>  = coffeeRepository.drinks
}