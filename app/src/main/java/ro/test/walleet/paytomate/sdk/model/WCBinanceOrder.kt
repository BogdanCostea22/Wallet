package ro.test.walleet.paytomate.sdk.model

import com.google.gson.annotations.SerializedName

data class WCBinanceOrderSignature(val signature: String, val publicKey: String)

data class WCBinanceTxConfirmParam(val ok: Boolean, val errorMsg: String?)

data class WCBinanceTradePair(val from: String, val to: String) {
    companion object {
        fun fromStr(str: String): WCBinanceTradePair? {
            val splitted: List<String> = str.split("_")
            if (splitted.size < 2) return null
            val from: String = splitted[0].split("-").firstOrNull() ?: return null
            val to: String = splitted[1].split("-").firstOrNull() ?: return null
            return WCBinanceTradePair(from, to)
        }
    }
}

open class WCBinanceOrder<T>(
    @SerializedName("account_number") val accountNumber: String,
    @SerializedName("chain_id") val chainId: String,
    val data: String?,
    val memo: String,
    val msgs: List<T>,
    val sequence: String,
    val source: String
)

class WCBinanceTradeOrder(
    accountNumber: String,
    chainId: String,
    data: String?,
    memo: String,
    msgs: List<Message>,
    sequence: String,
    source: String
) : WCBinanceOrder<WCBinanceTradeOrder.Message>(accountNumber, chainId, data, memo, msgs, sequence, source) {
    data class Message(
        val id: String,
        @SerializedName("ordertype") val orderType: Int,
        val price: Int,
        val quantity: Long,
        val sender: String,
        val side: Int,
        val symbol: String,
        @SerializedName("timeinforce") val timeInForce: Int
    )
}

class WCBinanceTransferOrder(
    accountNumber: String,
    chainId: String,
    data: String?,
    memo: String,
    msgs: List<Message>,
    sequence: String,
    source: String
) : WCBinanceOrder<WCBinanceTransferOrder.Message>(accountNumber, chainId, data, memo, msgs, sequence, source) {
    data class Message(
        val inputs: List<Item>,
        val outputs: List<Item>
    ) {
        data class Coin(val amount: Long, val denom: String)
        data class Item(val address: String, val coins: List<Coin>)
    }
}

class WCBinanceCancelOrder(
    accountNumber: String,
    chainId: String,
    data: String?,
    memo: String,
    msgs: List<Message>,
    sequence: String,
    source: String
) : WCBinanceOrder<WCBinanceCancelOrder.Message>(accountNumber, chainId, data, memo, msgs, sequence, source) {
    data class Message(val refid: String, val sender: String, val symbol: String)
}