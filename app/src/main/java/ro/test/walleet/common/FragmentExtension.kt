package ro.test.walleet.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.google.android.material.chip.Chip

fun <T> Fragment.observe(liveData: LiveData<T>, action: (T) -> Unit) {
    liveData.observe(viewLifecycleOwner) { value ->
        value?.let {
            action.invoke(value)
        }
    }
}


fun Fragment.createChipFor(value: String): Chip =
    Chip(requireContext()).apply {
        text = value
    }