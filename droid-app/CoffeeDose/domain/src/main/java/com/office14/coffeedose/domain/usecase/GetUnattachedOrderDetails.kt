package com.office14.coffeedose.domain.usecase

import com.office14.coffeedose.domain.entity.OrderDetailFull
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseBase
import com.office14.coffeedose.domain.interactor.UseCaseFlow
import com.office14.coffeedose.domain.repository.OrderDetailsRepository
import com.office14.coffeedose.domain.repository.PreferencesRepository
import com.office14.coffeedose.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetUnattachedOrderDetails @Inject constructor (
        private val orderDetailsRepository: OrderDetailsRepository,
        private val userRepository: UserRepository
    ) : UseCaseFlow<List<OrderDetailFull>, UseCaseBase.None>() {

    override suspend fun run(params: None): Flow<Either<Failure, List<OrderDetailFull>>> = userRepository.getCurrentUserAsFlow().flatMapLatest { user ->
        orderDetailsRepository.unattachedOrderDetailsForUser(user.email).flowOn(Dispatchers.IO)
    }

}