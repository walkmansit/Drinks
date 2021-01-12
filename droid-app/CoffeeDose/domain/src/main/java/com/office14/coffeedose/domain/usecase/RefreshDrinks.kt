package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.CoffeeRepository
import javax.inject.Inject



class RefreshDrinks @Inject constructor (private val coffeeRepository : CoffeeRepository) : UseCase<UseCaseBase.None, UseCaseBase.None>() {
    override suspend fun run(params: None): Either<Failure, None> = coffeeRepository.refreshDrinks()
}