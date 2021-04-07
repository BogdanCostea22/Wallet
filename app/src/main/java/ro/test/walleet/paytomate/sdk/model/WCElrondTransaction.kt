package ro.test.walleet.paytomate.sdk.model

import kotlinx.serialization.Serializable

@Serializable
data class WCElrondTransaction(
    val nonce: Int,
    val from: String,
    val to: String,
    val amount: String,
    val gasPrice: String,
    val gasLimit: String,
    val data: String,
    val chainId: String,
    val version: Int,
)
