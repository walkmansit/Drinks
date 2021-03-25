package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.Addin
import com.office14.coffeedose.domain.entity.CoffeeSize
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest

class GetSummaryPrice : UseCaseFlow<String, GetSummaryPrice.Params>() {

    data class Params(val addInsFlow:Flow<List<Addin>>,val selectedSizeFlow:Flow<CoffeeSize>,val countFlow:Flow<Int>)

    override suspend fun run(params: Params): Flow<Either<Failure, String>> = params.addInsFlow
        .combine(params.selectedSizeFlow){ addins, size -> addins.map { it.price }.sum() + size.price }
        .combine(params.countFlow){ sum, count -> sum*count }
        .mapLatest { total -> Either.Right( "$total Р." )}
}