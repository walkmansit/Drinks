package com.office14.coffeedose.domain.repository

import com.office14.coffeedose.domain.entity.UserA
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    fun setCurrentUser(user:UserA)
    fun getCurrentUserAsFlow() : StateFlow<UserA>
}