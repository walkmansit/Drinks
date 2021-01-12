package com.office14.coffeedose.entity

import com.office14.coffeedose.domain.entity.Addin

data class AddinView private constructor(
    val id: Int,
    val name: String,
    val description: String,
    val photoUrl: String,
    val price: Int,
    var isSelected: Boolean



){
    fun toDomainModel() : Addin = Addin(id,name,description,photoUrl,price)

    companion object{
        fun fromAddin(addIn:Addin) : AddinView{
            return AddinView(
                addIn.id,
                addIn.name,
                addIn.description,
                addIn.photoUrl,
                addIn.price,false
            )
        }
    }
}
