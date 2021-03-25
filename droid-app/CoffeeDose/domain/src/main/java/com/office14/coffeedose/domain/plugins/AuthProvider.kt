package com.office14.coffeedose.domain.plugins

interface AuthProvider {
    fun trySighnIn()
    fun sighnOut()
}