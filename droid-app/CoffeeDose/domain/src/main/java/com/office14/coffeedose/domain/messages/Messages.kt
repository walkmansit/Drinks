package com.office14.coffeedose.domain.messages

import com.office14.coffeedose.domain.exception.Failure

object Message {

    const val EMPTY =""

    object OrderDetails {
        const val FinishCurrentOrderFirst = "Сначала закончите текущий заказ"
        const val AuthotizationRequered = "Необходима авторизация"
    }
}