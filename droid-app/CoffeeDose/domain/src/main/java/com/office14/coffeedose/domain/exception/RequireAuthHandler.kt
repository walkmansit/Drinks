package com.office14.coffeedose.domain.exception

import kotlinx.coroutines.flow.Flow

interface RequireAuthHandler {
    fun call(authcallback:()->Unit)
    fun getRequiredAsFlow() : Flow<()->Unit>
}