package ro.test.walleet.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun runOnBackgroundThread(actions: suspend () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        actions.invoke()
    }
}
