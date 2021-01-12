package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCase
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.repository.SizesRepository
import javax.inject.Inject

class RefreshSizes @Inject constructor (private val sizesRepository: SizesRepository) : UseCase<UseCaseBase.None, RefreshSizes.Params>() {
    data class Params(val drinkId: Int)

    override suspend fun run(params: Params): Either<Failure, None> = sizesRepository.refreshSizes(params.drinkId)
}