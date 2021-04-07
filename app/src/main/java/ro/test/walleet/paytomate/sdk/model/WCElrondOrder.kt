package ro.test.walleet.paytomate.sdk.model

import kotlinx.serialization.Serializable


@Serializable
data class WCElrondOrder(
    val id: Long,
    val jsonrpc: String,
    val method: String,
    val params: WCElrondTransaction,
)
