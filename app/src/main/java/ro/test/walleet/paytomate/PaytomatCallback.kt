package ro.test.walleet.paytomate

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ro.test.walleet.paytomate.model.ApprovalSessionMapper
import ro.test.walleet.paytomate.model.WCCallbackState
import ro.test.walleet.paytomate.sdk.Status
import ro.test.walleet.paytomate.sdk.WCCallbacks
import ro.test.walleet.paytomate.sdk.model.WCBinanceOrder
import ro.test.walleet.paytomate.sdk.model.WCElrondOrder
import ro.test.walleet.paytomate.sdk.model.WCPeerMeta

class PaytomatCallback(): WCCallbacks {
    private val _currentEvent = MutableStateFlow<WCCallbackState>(WCCallbackState.Disconnected())
    val currentEvent: StateFlow<WCCallbackState> = _currentEvent

    override fun onBnbSign(id: Long, order: WCBinanceOrder<*>) {
      //no-op
    }

    override fun onElrondSign(elrondOrder: WCElrondOrder) {
        _currentEvent.value = WCCallbackState.ElrondTransaction(elrondOrder)
    }

    override fun onSessionRequest(id: Long, peer: WCPeerMeta) {
        val approvalRequestDetails = ApprovalSessionMapper.mapFom(peer)
        _currentEvent.value = WCCallbackState.ApprovalSession(approvalRequestDetails)
    }

    override fun onStatusUpdate(status: Status) {
        Log.i("DBG Status", status.toString())
        _currentEvent.value = when(status) {
            Status.CONNECTING -> WCCallbackState.Connecting()
            Status.CONNECTED -> WCCallbackState.Connected()
            Status.DISCONNECTED -> WCCallbackState.Disconnected()
            Status.FAILED_CONNECT -> WCCallbackState.FailedToConnect()
            else -> WCCallbackState.Error()
        }
    }

    fun forceConnectionFailed() {
        _currentEvent.value = WCCallbackState.FailedToConnect()
    }
}
fun main() {

}
//Todo implement in interactor
//interator.approveSession(
//arrayOf(Password.password),
//528
//)