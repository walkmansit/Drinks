package com.office14.coffeedose.domain.entity

import com.office14.coffeedose.domain.defaultuser.DefaultUser
import com.office14.coffeedose.domain.messages.Message

data class UserA(val email:String,val idToken:String){
    val idDefault = email == DefaultUser.DEFAULT_EMAIL

    companion object {
        const val DEFAULT_EMAIL = "DEFAULT_EMAIL"

        fun default() = UserA(DEFAULT_EMAIL,Message.EMPTY)
    }
}