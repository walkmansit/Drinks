package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.CoffeeSize
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.SizesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSizes @Inject constructor (private val sizesRepository : SizesRepository) : UseCaseFlow<List<CoffeeSize>, GetSizes.Params>() {

    data class Params(val drinkId: Int)

    override suspend fun run(params: Params): Flow<Either<Failure, List<CoffeeSize>>> = sizesRepository.getSizes(params.drinkId)
}