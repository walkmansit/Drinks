package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import javax.inject.Inject

class GetUnAttachedOrderDetailsCount @Inject constructor (
        private val orderDetailsRepository: OrderDetailsRepository,
        private val userRepository: UserRepository
        ) : UseCaseFlow<Int, UseCaseBase.None>() {

    override suspend fun run(params: None): Flow<Either<Failure, Int>> = orderDetailsRepository.unAttachedOrderDetails().combine(userRepository.getCurrentUserAsFlow()){
        list, user -> Either.Right( list.filter { it.owner == user.email }.sumBy { it.count })
    }.conflate()

}