package com.office14.coffeedose.domain.repository

import com.office14.coffeedose.domain.entity.User

interface UsersRepository {
    fun getUserByEmail(email: String): User?
    fun updateUser(user: User)
}