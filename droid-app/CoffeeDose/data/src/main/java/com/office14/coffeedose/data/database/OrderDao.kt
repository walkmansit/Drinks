package com.office14.coffeedose.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("select * from orders")
    fun getAll(): Flow<List<OrderDbo>>

    @Query("select * from orders where finished = 'false'")
    fun getAllNotFinished(): Flow<List<OrderDbo>>

    @Query("select * from orders where owner = :email")
    fun getAllForUser(email: String): Flow<List<OrderDbo>>

    @Query("select * from orders where owner = :email and finished = 'false'")
    fun getAllForUserNotFinishedStraight(email: String): List<OrderDbo>

    @Query("select * from orders where id = :orderId")
    fun getById(orderId: Int): Flow<List<OrderDbo>>

    @Query("select * from orders where id = :orderId")
    fun getByIdStraight(orderId: Int): List<OrderDbo>

    @Query("select * from orders where id = :orderId and owner = :email")
    fun getByIdAndOwner(orderId: Int, email: String): Flow<List<OrderDbo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOrders(vararg orders: OrderDbo)

    @Delete
    fun delete(order: OrderDbo)

    @Query("delete from orders where owner = :email")
    fun deleteByUser(email: String)

    @Query("update orders set finished = 'true' where owner = :email")
    fun markAsFinishedForUser(email: String)

    @Query("delete from orders")
    fun clear()

    @Query("update orders set status_code = :statusCode, status_name = :statusName where id = :id")
    fun updateStatusCodeAndNameById(id: Int, statusCode: String, statusName: String)
}