package com.office14.coffeedose.data.network

import com.office14.coffeedose.data.database.SizeDbo
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class SizeJso(
    var id: Int,
    var volume: String,
    var name: String,
    var price: Int
) {
    fun toDatabaseModel(drink: Int) = SizeDbo(
        id, drink, volume, name, price
    )
}

