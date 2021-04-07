package ro.test.walleet.paytomate.model

import ro.test.walleet.paytomate.sdk.model.WCElrondOrder

sealed class WCCallbackState {
    class Connecting : WCCallbackState() {}
    class Connected(): WCCallbackState() {}
    class Disconnected(): WCCallbackState() {}
    class FailedToConnect(): WCCallbackState() {}
    class ApprovalSession(val approvalSessionDetails: ApprovalSessionDetails): WCCallbackState()
    class ElrondTransaction(val transactionDetails: WCElrondOrder): WCCallbackState()
    class Error : WCCallbackState() {}
}