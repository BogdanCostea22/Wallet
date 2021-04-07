package ro.test.walleet.common.extensions

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

fun <T> Fragment.observe(liveData: LiveData<T>, action: (T) -> Unit) {
    liveData.observe(viewLifecycleOwner) { value ->
        value?.let {
            action.invoke(value)
        }
    }
}

fun Fragment.showDialog(dialog: DialogFragment) {
    val fragmentManager = requireActivity().supportFragmentManager
    dialog.show(fragmentManager, tag)
}

fun Fragment.showDialog(dialogFactory: () ->DialogFragment) {
    val fragmentManager = requireActivity().supportFragmentManager
    val dialog = dialogFactory.invoke()
    dialog.show(fragmentManager, tag)
}
