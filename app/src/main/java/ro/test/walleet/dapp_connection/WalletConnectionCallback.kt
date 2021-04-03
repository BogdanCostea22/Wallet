package ro.test.walleet.dapp_connection

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.walletconnect.Session

object WalletConnectionCallback: Session.Callback {
    enum class ConnectionState {
        CONNECTED, APPROVED, DISCONNECTED, ERROR, CLOSED;
    }

    val currentState = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)

    override fun onMethodCall(call: Session.MethodCall) {
        Log.i("DBG Message", call.toString())
    }

    override fun onStatus(status: Session.Status) {
        Log.i("DBG Status", status.toString())
        currentState.value = when(status) {
            Session.Status.Approved -> ConnectionState.APPROVED
            Session.Status.Closed -> ConnectionState.CLOSED
            Session.Status.Connected -> ConnectionState.CONNECTED
            Session.Status.Disconnected -> ConnectionState.DISCONNECTED
            is Session.Status.Error -> ConnectionState.ERROR
        }
    }
}


fun main() {
    val uri = "wc:858096fa-1b04-4f19-a710-d256530becfb@1?bridge=https%3A%2F%2Fbridge.walletconnect.org&key=9073c08fbbdeb312a4b158f37c7d23deb8043708eb31f3db46fbdd73b3673403"
    val configuration = Session.Config.fromWCUri(uri)
    println(configuration.bridge)
    println(configuration.key)
    println(configuration.protocol)
    println(configuration.handshakeTopic)
    println(configuration.version)
}