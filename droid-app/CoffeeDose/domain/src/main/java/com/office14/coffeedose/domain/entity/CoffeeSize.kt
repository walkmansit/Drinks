package com.office14.coffeedose.domain.entity

data class CoffeeSize(
    var id: Int,
    var drinkId: Int,
    var volume: String,
    var name: String,
    var price: Int
)