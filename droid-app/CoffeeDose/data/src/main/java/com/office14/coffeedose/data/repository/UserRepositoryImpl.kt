package com.office14.coffeedose.data.repository

import com.office14.coffeedose.domain.entity.UserA
import com.office14.coffeedose.domain.repository.PreferencesRepository
import com.office14.coffeedose.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val preferencesRepository: PreferencesRepository) : UserRepository {

    private val _userFlow = MutableStateFlow(UserA(preferencesRepository.getUserEmail(),preferencesRepository.getIdToken()))

    override fun setCurrentUser(user: UserA) {
        _userFlow.tryEmit(user)
        preferencesRepository.saveUserEmail(user.email)
        preferencesRepository.saveIdToken(user.idToken)
    }

    override fun getCurrentUserAsFlow(): StateFlow<UserA> = _userFlow
}