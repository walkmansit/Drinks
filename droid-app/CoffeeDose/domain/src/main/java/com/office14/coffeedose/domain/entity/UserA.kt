package com.office14.coffeedose.domain.entity

import com.office14.coffeedose.domain.messages.Message

data class UserA(val email:String,val idToken:String){

    val isDefault = email == DEFAULT_EMAIL

    companion object {
        const val DEFAULT_EMAIL = "DEFAULT_EMAIL"

        fun default() = UserA(DEFAULT_EMAIL,Message.EMPTY)
    }
}