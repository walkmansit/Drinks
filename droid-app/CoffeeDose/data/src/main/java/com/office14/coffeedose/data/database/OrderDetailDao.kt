package com.office14.coffeedose.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDetailDao {

    @Transaction
    @Query("select * from order_details where order_id is null")
    fun getUnAttachedDetails(): Flow<List<OrderDetailAndDrinkAndSize>>

    @Transaction
    @Query("select * from order_details where order_id is null and owner = :email")
    fun getUnAttachedDetailsForUserStraight(email: String): List<OrderDetailAndDrinkAndSize>

    @Transaction
    @Query("select * from order_details where order_id is null and owner is null")
    fun getUnAttachedDetailsWithoutUser(): Flow<List<OrderDetailAndDrinkAndSize>>

    @Transaction
    @Query("select * from order_details where order_id is null and owner is null")
    fun getUnAttachedDetailsWithoutUserStraight(): List<OrderDetailAndDrinkAndSize>

    @Transaction
    @Query("select * from order_details where order_id is null and owner = :email")
    fun getUnAttachedDetailsForUser(email: String): Flow<List<OrderDetailAndDrinkAndSize>>

    @Query("update order_details set count = :count where id = :id")
    fun updateCountWithOrderDetailsId(id: Int, count: Int)

    @Transaction
    @Query("select * from order_details where order_id = :orderId")
    fun getDetailsByOrderId(orderId: Int): Flow<List<OrderDetailAndDrinkAndSize>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOrderDetails(vararg orderDetails: OrderDetailDbo): List<Long>

    @Query("delete from order_details where order_id is null")
    fun deleteUnAttached()

    @Query("delete from order_details where owner = :email")
    fun deleteByEmail(email: String)

    //@Update
    //fun updateUnAttachedWithOrderId(orderDetails:List<Order>)

    /*@Query("delete from order_details where order_id = :orderId")
    fun deleteByOrderId(orderId:Int)*/

    @Delete
    fun delete(orderDetails: OrderDetailDbo)

    @Query("delete from order_details")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOrderDetailsToAddInsCross(vararg crossRefs: OrderDetailsAndAddinsCrossRef)

    @Transaction
    fun insertOrderDetailsAndAddIns(vararg pair: Pair<OrderDetailDbo,List<AddinDbo>>) {
        val newIds = insertAllOrderDetails(*pair.map { it.first }.toTypedArray())

        val crossRefsList: MutableList<OrderDetailsAndAddinsCrossRef> = mutableListOf()

        for ((index, odId) in newIds.withIndex()) {

            pair[index].second.forEach {
                crossRefsList.add(OrderDetailsAndAddinsCrossRef(odId.toInt(), it.id))
            }
        }

        insertAllOrderDetailsToAddInsCross(*crossRefsList.toTypedArray())
    }

    @Query("update order_details set owner = :newEmail where order_id is null and owner = :oldEmail ")
    fun updateUnattachedOrderDetailsWithEmail(newEmail: String,oldEmail: String): Int

    @Query("update order_details set order_id = :orderId where order_id is null and owner = :email ")
    fun updateAttachedOrderDetailsWithOrderId(email: String, orderId: Int): Int

    /* @Transaction
     fun updateUnattachedOrderDetailsWithEmail(email:String){
         val list = getUnAttachedDetailsWithoutOwner().value?.map { it.orderDetail }
         list?.let {
             it.forEach {
                 it.owner = email
             }
             insertAllOrderDetails(*it.toTypedArray())
         }
     }*/
}