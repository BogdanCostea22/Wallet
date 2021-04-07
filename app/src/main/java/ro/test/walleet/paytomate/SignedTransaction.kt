package ro.test.walleet.paytomate

import kotlinx.serialization.Serializable
import java.security.Signature

@Serializable
data class SignedTransaction (
    val signature: String
        )
