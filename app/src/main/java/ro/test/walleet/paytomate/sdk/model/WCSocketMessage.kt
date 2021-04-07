package ro.test.walleet.paytomate.sdk.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ro.test.walleet.paytomate.sdk.util.guard
import java.lang.reflect.Type

data class WCEncryptionPayload(val data: String, val hmac: String, val iv: String) {

    companion object {
        fun messageFromJson(json: String, gson: Gson = Gson()): WCSocketMessage<WCEncryptionPayload>? = guard {
            val typeToken: Type = object : TypeToken<WCSocketMessage<WCEncryptionPayload>>() {}.type
            gson.fromJson<WCSocketMessage<WCEncryptionPayload>>(json, typeToken)
        }
    }

}

data class WCSocketMessage<T>(val topic: String, val type: MessageType, val payload: String) {

    enum class MessageType { pub, sub }

    fun getPayloadParsed(gson: Gson = Gson(), clazz: Class<T>): T? = guard {
        gson.fromJson(payload, clazz)
    }
}

