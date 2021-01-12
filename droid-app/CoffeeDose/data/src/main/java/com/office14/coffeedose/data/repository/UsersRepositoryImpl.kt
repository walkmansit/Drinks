package com.office14.coffeedose.data.repository

import com.office14.coffeedose.data.database.UserDao
import com.office14.coffeedose.domain.entity.User
import com.office14.coffeedose.domain.repository.UsersRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(private val usersDao: UserDao) :
    UsersRepository {

    override fun getUserByEmail(email: String): User? = usersDao.getByEmail(email).firstOrNull()?.toDomainModel()


    override fun updateUser(user: User) = usersDao.insertAllUsers(com.office14.coffeedose.data.database.UserDbo(user))
}