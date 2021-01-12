package com.office14.coffeedose.domain.entity

class User(
    val email: String,
    val displayName: String,
    val photoUrl: String,
    var idToken: String,
    var fcmToken: String?
)