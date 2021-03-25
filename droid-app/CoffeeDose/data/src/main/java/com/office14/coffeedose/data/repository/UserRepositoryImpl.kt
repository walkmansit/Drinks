package com.office14.coffeedose.data.repository

import com.office14.coffeedose.domain.entity.UserA
import com.office14.coffeedose.domain.repository.PreferencesRepository
import com.office14.coffeedose.domain.repository.UserRepository
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val preferencesRepository: PreferencesRepository) : UserRepository {

    private val userChannel = ConflatedBroadcastChannel<UserA>()

    init {
        userChannel.offer(UserA(preferencesRepository.getUserEmail(),preferencesRepository.getIdToken()))
    }

    override fun setCurrentUser(user: UserA) {
        userChannel.offer(user)
        preferencesRepository.saveUserEmail(user.email)
        preferencesRepository.saveIdToken(user.idToken)
    }

    override fun getCurrentUserAsFlow(): Flow<UserA> = userChannel.asFlow()
}