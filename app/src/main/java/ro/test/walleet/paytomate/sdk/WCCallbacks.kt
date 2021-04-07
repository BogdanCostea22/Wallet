package ro.test.walleet.paytomate.sdk

import ro.test.walleet.paytomate.sdk.model.WCBinanceOrder
import ro.test.walleet.paytomate.sdk.model.WCElrondOrder
import ro.test.walleet.paytomate.sdk.model.WCPeerMeta

interface WCCallbacks {

    /**
     * Notifies socket status update when callback is set, method triggers with current status
     */
    fun onStatusUpdate(status: Status)

    /**
     * Called when session request is asked from socket
     * Warning: Method called from
     */
    fun onSessionRequest(id: Long, peer: WCPeerMeta)

    /**
     * Called when binance order signature required
     */
    fun onBnbSign(id: Long, order: WCBinanceOrder<*>)


    fun onElrondSign(elrondOrder: WCElrondOrder)
}