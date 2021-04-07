package ro.test.walleet.paytomate.sdk

sealed class WCError(message: String) : RuntimeException(message) {
    object BadServerResponse : WCError("Bad Response")
    object BadJSON : WCError("JSON malformed")
    object InvalidSession : WCError("Invalid session")
    object Unknown : WCError("Unknown")
}