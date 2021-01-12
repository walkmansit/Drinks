package com.office14.coffeedose.domain.repository

import com.office14.coffeedose.domain.entity.Order
import com.office14.coffeedose.domain.entity.OrderDetailFull
import com.office14.coffeedose.domain.entity.OrderInfo
import com.office14.coffeedose.domain.entity.OrderStatus
import com.office14.coffeedose.domain.exception.Failure
import com.office14.coffeedose.domain.functional.Either
import com.office14.coffeedose.domain.interactor.UseCaseBase
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {
    fun getCurrentQueueOrderByUser(emailFlow: Flow<String>): Flow<Order?>
    fun getCurrentNotFinishedOrderByUser(email: String): Order?
    //fun queueOrderStatus(emailFlow: Flow<String>): Flow<OrderStatus>
    fun createOrder(orders: List<OrderDetailFull>, comment: String?, token: String?, email: String ): Either<Failure,Int>
    fun refreshLastOrderStatus(token: String?) : Either<Failure,UseCaseBase.None>
    fun markAsFinishedForUser(email: String)
    fun getLastOrderForUserAndPutIntoDB(token: String?, email: String) : Either<Failure, UseCaseBase.None>
    fun updateFcmDeviceToken(deviceId: String, fcmToken: String, idToken: String) :Either<Failure,UseCaseBase.None>
    fun deleteFcmDeviceTokenOnLogOut(deviceId: String, idToken: String):Either<Failure,UseCaseBase.None>
    fun getLastOrderInfo(token: String): Either<Failure,OrderInfo>
}