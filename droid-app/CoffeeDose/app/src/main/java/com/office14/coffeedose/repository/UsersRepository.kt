package com.office14.coffeedose.repository

import com.office14.coffeedose.database.UserDao
import com.office14.coffeedose.database.UserDbo
import com.office14.coffeedose.domain.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UsersRepository @Inject constructor(private val usersDao: UserDao) {

    suspend fun getUserByEmail(email: String): User?  = withContext(Dispatchers.IO) {
        usersDao.getByEmail(email).firstOrNull()?.toDomainModel()
    }

    fun updateUser(user: User) {
        usersDao.insertAllUsers(UserDbo(user))
    }
}