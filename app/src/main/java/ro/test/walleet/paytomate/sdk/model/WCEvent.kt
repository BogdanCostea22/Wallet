package ro.test.walleet.paytomate.sdk.model

enum class WCEvent(val eventName: String) {
    SessionRequest("wc_sessionRequest"),
    SessionUpdate("wc_sessionUpdate"),
    ExchangeKey("wc_exchangeKey"),

    BnbSign("bnb_sign"),
    ElrondSign("erd_sign"),
    BnbTransactionConfirm("bnb_tx_confirmation");

    companion object {
        fun provideEvent(eventName: String): WCEvent? = values().firstOrNull { it.eventName == eventName }
    }
}