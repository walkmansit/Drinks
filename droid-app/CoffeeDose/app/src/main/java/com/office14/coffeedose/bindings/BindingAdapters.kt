package com.office14.coffeedose.bindings

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.coffeedose.R
import com.office14.coffeedose.domain.OrderStatus
import com.office14.coffeedose.extensions.setBooleanVisibility

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        //val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUrl)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .centerCrop()
            .into(imgView)
    }
}

@BindingAdapter("orderStatusTextColor")
fun orderStatusTextColor(view: TextView, status: OrderStatus) {

    val color = when (status) {
        OrderStatus.READY -> ContextCompat.getColor(view.context, R.color.color_green)
        OrderStatus.COOKING -> ContextCompat.getColor(view.context, R.color.color_yellow)
        OrderStatus.FAILED -> ContextCompat.getColor(view.context, R.color.color_red)
        else -> ContextCompat.getColor(view.context, R.color.color_black)
    }

    view.setTextColor(color)
}

@BindingAdapter("isVisible")
fun popularityIcon(view: View, visible: Boolean) {
    view.setBooleanVisibility(visible)
}


@BindingAdapter("app:hideIfZero")
fun hideIfZero(view: View, number: Int) {
    view.visibility = if (number == 0) View.GONE else View.VISIBLE
}
