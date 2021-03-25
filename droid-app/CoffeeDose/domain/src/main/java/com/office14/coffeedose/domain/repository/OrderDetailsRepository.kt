package com.office14.coffeedose.domain.repository

import com.office14.coffeedose.domain.entity.Addin
import com.office14.coffeedose.domain.entity.OrderDetail
import com.office14.coffeedose.domain.entity.OrderDetailFull
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import kotlinx.coroutines.flow.Flow

interface OrderDetailsRepository {
    fun unattachedOrderDetailsForUser(email: String): Flow<Either<Failure,List<OrderDetailFull>>>
    fun unAttachedOrderDetailsStraight(email: String): List<OrderDetailFull>
    fun unAttachedOrderDetails(): Flow<List<OrderDetailFull>>
    fun delete(oderDetails: OrderDetail)
    fun deleteUnAttached()
    fun updateUnattachedOrderDetailsWithEmail(newEmail: String,oldEmail: String) : Int
    fun updateAttachedOrderDetailsWithOrderId(email: String, orderId: Int) : Int
    fun deleteOrderDetailsByEmail(email: String)
    fun insertOrderDetailsAndAddIns(pair:  Pair<OrderDetail,List<Addin>>)
    fun updateCountWithOrderDetailsId(id: Int, count: Int)
}