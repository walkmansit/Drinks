package com.office14.coffeedose.domain.entity

import com.office14.coffeedose.domain.entity.OrderStatus.Companion.getStatusByString

data class Order(
    var id: Int,
    val statusCode: String,
    val statusName: String,
    val orderNumber: String,
    val totalPrice: Int,
    val owner: String?,
    val isFinished: String
){
    val orderStatus = getStatusByString(statusCode)
}

enum class OrderStatus {
    NONE, COOKING, READY, FAILED;

    companion object {
        fun getStatusByString(status: String): OrderStatus {
            return when (status.toLowerCase()) {
                "cooking" -> COOKING
                "ready" -> READY
                else -> NONE
            }
        }
    }


}