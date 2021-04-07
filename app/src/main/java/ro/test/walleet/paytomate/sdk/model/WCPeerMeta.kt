package ro.test.walleet.paytomate.sdk.model

data class WCPeerMeta(
    val name: String,
    val url: String,
    val description: String = "",
    val icons: List<String> = emptyList()
)