package ro.test.walleet.paytomate.model

import java.io.Serializable

data class ApprovalSessionDetails (
    val name: String,
    val chainUrl: String,
    val description: String,
    val iconUrl: String,
): Serializable
