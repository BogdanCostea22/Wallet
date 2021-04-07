package ro.test.walleet.paytomate.sdk.util

import com.google.gson.*
import ro.test.walleet.paytomate.sdk.model.WCBinanceCancelOrder
import ro.test.walleet.paytomate.sdk.model.WCBinanceOrder
import ro.test.walleet.paytomate.sdk.model.WCBinanceTradeOrder
import ro.test.walleet.paytomate.sdk.model.WCBinanceTransferOrder
import java.lang.reflect.Type


class WCBinanceOrderAdapter : JsonDeserializer<WCBinanceOrder<*>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WCBinanceOrder<*> {
        json ?: throw IllegalArgumentException("Cannot deserialize null")
        context ?: throw IllegalArgumentException("Cannot with nullable context")
        val jsonObj: JsonObject = json.asJsonObject
        val accountNumber: String = jsonObj.get("account_number").asString
        val chainId: String = jsonObj.get("chain_id").asString
        val data: String? = jsonObj.get("data").takeIf { !it.isJsonNull }?.asString
        val memo: String = jsonObj.get("memo").asString
        val msgs: JsonArray = jsonObj.get("msgs").asJsonArray
        val sequence: String = jsonObj.get("sequence").asString
        val source: String = jsonObj.get("source").asString

        var msgsTrade: List<WCBinanceTradeOrder.Message>? = null
        var msgsTrans: List<WCBinanceTransferOrder.Message>? = null
        var msgsCancel: List<WCBinanceCancelOrder.Message>? = null

        val firstMsg: JsonObject = msgs[0].asJsonObject
        if (firstMsg.has("id") && firstMsg.has("ordertype") && firstMsg.has("price")
            && firstMsg.has("quantity") && firstMsg.has("sender")
            && firstMsg.has("side") && firstMsg.has("symbol") && firstMsg.has("timeinforce")
        ) {
            //TRADE
            msgsTrade = msgs.map {
                context.deserialize<WCBinanceTradeOrder.Message>(
                    it,
                    WCBinanceTradeOrder.Message::class.java
                )
            }
        } else if (firstMsg.has("inputs") && firstMsg.has("outputs")) {
            //TRANSFER
            msgsTrans = msgs.map {
                context.deserialize<WCBinanceTransferOrder.Message>(
                    it,
                    WCBinanceTransferOrder.Message::class.java
                )
            }
        } else if (firstMsg.has("refid") && firstMsg.has("sender") && firstMsg.has("symbol")) {
            //CANCEL
            msgsCancel = msgs.map {
                context.deserialize<WCBinanceCancelOrder.Message>(
                    it,
                    WCBinanceCancelOrder.Message::class.java
                )
            }

        }

        return when {
            msgsTrade != null -> WCBinanceTradeOrder(accountNumber, chainId, data, memo, msgsTrade, sequence, source)
            msgsTrans != null -> WCBinanceTransferOrder(accountNumber, chainId, data, memo, msgsTrans, sequence, source)
            msgsCancel != null -> WCBinanceCancelOrder(accountNumber, chainId, data, memo, msgsCancel, sequence, source)
            else -> throw  IllegalArgumentException("Cannot decode unknown binance order")
        }
    }
}