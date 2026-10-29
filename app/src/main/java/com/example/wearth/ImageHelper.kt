package com.example.wearth

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.R

object ImageHelper {
  @DrawableRes
  fun getDrawableId(context: Context, name: String): Int {
    return when (name) {
      "prod_vintage_tee" -> R.drawable.prod_vintage_tee
      "prod_denim_jacket" -> R.drawable.prod_denim_jacket
      "prod_striped_shirt" -> R.drawable.prod_striped_shirt
      "prod_cargo_pants" -> R.drawable.prod_cargo_pants
      "prod_corduroy_shirt" -> R.drawable.prod_corduroy_shirt
      "banner_thrift_rack" -> R.drawable.banner_thrift_rack
      "bg_welcome_fabric" -> R.drawable.bg_welcome_fabric
      "ic_wearth_logo" -> R.drawable.ic_wearth_logo
      else -> {
        val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
        if (resId != 0) resId else R.drawable.ic_wearth_logo
      }
    }
  }
}
