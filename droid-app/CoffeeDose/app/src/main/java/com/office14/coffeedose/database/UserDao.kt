package com.office14.coffeedose.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("select * from users")
    fun getAllUsers(): Flow<List<UserDbo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUsers(vararg users: UserDbo)

    @Query("delete from users")
    fun clear()

    @Query("select * from users where email = :email ")
    fun getByEmail(email: String): List<UserDbo>
}