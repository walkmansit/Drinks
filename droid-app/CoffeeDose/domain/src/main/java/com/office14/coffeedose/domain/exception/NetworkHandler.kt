package com.office14.coffeedose.domain.exception

interface NetworkHandler {
    fun isNetworkAvailable() : Boolean
}