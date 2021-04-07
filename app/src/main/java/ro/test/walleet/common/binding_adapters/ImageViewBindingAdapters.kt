package ro.test.walleet.common.binding_adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("onLoadFromUrl")
fun ImageView.loadImageFromUrl(url: String) {
    Glide
        .with(context)
        .load(url)
        .into(this);
}
