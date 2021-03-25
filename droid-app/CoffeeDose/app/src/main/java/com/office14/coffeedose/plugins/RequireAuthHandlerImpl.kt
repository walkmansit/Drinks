package com.office14.coffeedose.plugins

import com.office14.coffeedose.domain.exception.RequireAuthHandler
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class RequireAuthHandlerImpl : RequireAuthHandler {

    private val channel = ConflatedBroadcastChannel<() -> Unit>()

    override fun call(authcallback: () -> Unit) {
        channel.offer { authcallback }
    }

    override fun getRequiredAsFlow(): Flow<() -> Unit> = channel.asFlow()
}