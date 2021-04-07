package ro.test.walleet.common.binding_adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("onSetDescription", "onSetValue")
fun TextView.setDescription(description: String, value: String) {
    text = "$description: $value"
}