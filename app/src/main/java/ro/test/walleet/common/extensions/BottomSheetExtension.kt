package ro.test.walleet.common.extensions

import androidx.fragment.app.DialogFragment

fun DialogFragment.executeActionAndDismissDialog(action: () -> Unit): () -> Unit =
    {
        action.invoke()
        dismiss()
    }
