package ro.test.walleet.common.binding_adapters

import android.view.View
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController

@BindingAdapter("onClickNavigate")
fun View.setNavigationOnClick(@IdRes navigationDirection: Int) {
    setOnClickListener {
        findNavController().navigate(navigationDirection)
    }
}