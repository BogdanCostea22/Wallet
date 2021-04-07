package ro.test.walleet.usecases

import ro.test.walleet.paytomate.sdk.model.WCElrondOrder

interface WCUseCase {
    fun connectTo(sessionUri: String)
    fun approveSession()
    fun signTransaction(order: WCElrondOrder)
}
