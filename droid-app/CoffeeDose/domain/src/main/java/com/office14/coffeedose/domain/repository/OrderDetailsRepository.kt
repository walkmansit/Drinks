package com.office14.coffeedose.domain.repository

import com.office14.coffeedose.domain.entity.Addin
import com.office14.coffeedose.domain.entity.OrderDetail
import com.office14.coffeedose.domain.entity.OrderDetailFull
import kotlinx.coroutines.flow.Flow

interface OrderDetailsRepository {
    fun unattachedOrderDetailsForUser(email: String):List<OrderDetailFull>
    fun unattachedOrderDetailsWithoutUser(): List<OrderDetailFull>
    fun unAttachedOrderDetails(emailFlow: Flow<String>): Flow<List<OrderDetailFull>>
    fun unAttachedOrderDetailsStraight(email: String): List<OrderDetailFull>
    fun unAttachedOrderDetailsCount(emailFlow: Flow<String>): Flow<Int>
    fun delete(oderDetails: OrderDetail)
    fun deleteUnAttached()
    fun updateUnattachedOrderDetailsWithEmail(email: String) : Int
    fun updateAttachedOrderDetailsWithOrderId(email: String, orderId: Int) : Int
    fun deleteOrderDetailsByEmail(email: String)
    fun insertOrderDetailsAndAddIns(pair:  Pair<OrderDetail,List<Addin>>)
    fun updateCountWithOrderDetailsId(id: Int, count: Int)
}