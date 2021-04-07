package ro.test.walleet.paytomate.sdk

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ro.test.walleet.paytomate.sdk.util.WCBinanceOrderAdapter
import ro.test.walleet.paytomate.sdk.util.guard
import okhttp3.*
import okio.ByteString
import ro.test.walleet.paytomate.sdk.model.*
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class WCInteractor(private val session: WCSession, private val clientMeta: WCPeerMeta, private val clientId: String) :
    WebSocketListener() {

    companion object {
        var DEBUG: Boolean = false
    }

    private val gson = GsonBuilder()
        .registerTypeAdapter(WCBinanceOrder::class.java, WCBinanceOrderAdapter())
        .setLenient()
        .create()

    private var socket: WebSocket? = null
    private var handshakeId: Long = -1
    var currentStatus: Status = Status.DISCONNECTED
        set(value) {
            if (field == value) return
            field = value
            callbacks?.onStatusUpdate(value)
        }

    private var peerId: String? = null
    private var peerMeta: WCPeerMeta? = null

    var callbacks: WCCallbacks? = null
        set(value) {
            field = value
            value?.onStatusUpdate(currentStatus)
        }

    /**
     * Connect socket to provided bridge when socket connects, start handshake
     */
    fun connect() {
        if (currentStatus == Status.CONNECTED || currentStatus == Status.CONNECTING) return
        if (DEBUG) Log.d("<<SS", session.toString())
        currentStatus = Status.CONNECTING
        val client: OkHttpClient =
            OkHttpClient.Builder().pingInterval(15, TimeUnit.SECONDS).retryOnConnectionFailure(true).build()
        socket = client.newWebSocket(Request.Builder().url(session.bridge.toString()).build(), this)
    }

    /**
     * Disconnect from bridge
     */
    fun disconnect() {
        socket?.close(1000, null)
        socket = null
    }

    /**
     * Approve session start
     * @param accounts eligible for current session
     * @param chainId session chain id
     */
    fun approveSession(accounts: Array<String>, chainId: Int) {
        if (handshakeId <= 0) throw WCError.InvalidSession

        val result = WCApproveSessionResponse(true, chainId, accounts, clientId, clientMeta)
        val response: JSONRPCResponse<WCApproveSessionResponse> = JSONRPCResponse(handshakeId, result)
        encryptAndSend(gson.toJson(response))
    }

    /**
     * Reject session start
     * @param message rejection message
     */
    fun rejectSession(message: String = "Session Rejected") {
        if (handshakeId <= 0) throw WCError.InvalidSession

        val response = JSONRPCErrorResponse(handshakeId, JSONRPCError(-32000, message))
        encryptAndSend(gson.toJson(response))
    }

    /**
     * Disconnect session
     */
    fun killSession() {
        val result = WCSessionUpdateParam(false, null, null)
        val response: JSONRPCRequest<List<WCSessionUpdateParam>> =
            JSONRPCRequest(generateId(), WCEvent.SessionUpdate.eventName, listOf(result))
        encryptAndSend(gson.toJson(response))
        disconnect()
    }

    fun approveBnbOrder(id: Long, signed: WCBinanceOrderSignature) {
        approveRequest(id, gson.toJson(signed))
    }

    fun approveRequest(id: Long, result: String) {
        encryptAndSend(gson.toJson(JSONRPCResponse(id, result)))
    }

    fun rejectRequest(id: Long, message: String) {
        return encryptAndSend(gson.toJson(JSONRPCErrorResponse(id, JSONRPCError(-32000, message))))
    }


    //SOCKET CALLBACKS
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        currentStatus = Status.CONNECTED
        if (DEBUG) Log.d("<<SS", response.toString())
        subscribe(session.topic)
        subscribe(clientId)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        if (DEBUG) Log.d("<<SS", bytes.hex())
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        if (DEBUG) Log.d("<<SS", text)
        val message: WCSocketMessage<WCEncryptionPayload> = WCEncryptionPayload.messageFromJson(text, gson) ?: return
        guard {
            val payload: WCEncryptionPayload = message.getPayloadParsed(gson, WCEncryptionPayload::class.java) ?: return
            val decrypted: String = WCEncryptor.decrypt(payload, session.key).toString(Charset.defaultCharset())
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val json: Map<String, Any> = guard { gson.fromJson<Map<String, Any>>(decrypted, type) } ?: return
            val method: String = json["method"] as? String ?: return
            val event: WCEvent = WCEvent.provideEvent(method) ?: return
            handleEvent(event, message.topic, decrypted)
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        currentStatus = Status.FAILED_CONNECT
        if (DEBUG) Log.d("<<SS", response?.toString(), t)
        socket = null
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        currentStatus = Status.DISCONNECTED
        if (DEBUG) Log.d("<<SS", "Closing code = $code, reason = $reason")
        socket = null
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        currentStatus = Status.DISCONNECTED
        if (DEBUG) Log.d("<<SS", "Closing code = $code, reason = $reason")
        socket = null
    }
    //SOCKET CALLBACKS END

    /**
     * Send subscribe message with given topic, sends with empty payload
     * @param topic subscription topic
     */
    private fun subscribe(topic: String) {
        val message: WCSocketMessage<String> = WCSocketMessage(topic, WCSocketMessage.MessageType.sub, "")
        val data: String = gson.toJson(message)
        if (DEBUG) Log.d("<<SS", data)
        socket?.send(data)
    }

    /**
     * Send message with payload to defined topic
     * @param data payload in byte form
     */
    private fun encryptAndSend(data: String) {
        if (DEBUG) Log.d("<<SS", data)
        val payload: WCEncryptionPayload = WCEncryptor.encrypt(data.toByteArray(), session.key)
        val message: WCSocketMessage<WCEncryptionPayload> =
            WCSocketMessage(peerId ?: session.topic, WCSocketMessage.MessageType.pub, gson.toJson(payload))
        val json: String = gson.toJson(message)
        if (DEBUG) Log.d("<<SS", json)
        socket?.send(json)
    }

    private fun handleEvent(event: WCEvent, topic: String, decrypted: String) {
        if (DEBUG) Log.d("<<SS", "Event $event, topic $topic, decrypted $decrypted")
        when (event) {
            WCEvent.SessionRequest -> {
                val typeToken: Type = object : TypeToken<JSONRPCRequest<List<WCSessionRequestParam>>>() {}.type
                val request: JSONRPCRequest<List<WCSessionRequestParam>> =
                    guard { gson.fromJson<JSONRPCRequest<List<WCSessionRequestParam>>>(decrypted, typeToken) }
                        ?: throw WCError.BadJSON
                val params: WCSessionRequestParam = request.params.firstOrNull() ?: throw WCError.BadJSON
                handshakeId = request.id
                peerId = params.peerId
                peerMeta = params.peerMeta
                callbacks?.onSessionRequest(request.id, params.peerMeta)
            }
            WCEvent.SessionUpdate -> {
                val typeToken: Type = object : TypeToken<JSONRPCRequest<List<WCSessionUpdateParam>>>() {}.type
                val request: JSONRPCRequest<List<WCSessionUpdateParam>> =
                    guard { gson.fromJson<JSONRPCRequest<List<WCSessionUpdateParam>>>(decrypted, typeToken) }
                        ?: throw WCError.BadJSON
                val params: WCSessionUpdateParam = request.params.firstOrNull() ?: throw WCError.BadJSON
                if (!params.approved) disconnect()
            }
            WCEvent.BnbSign -> {
                guard {
                    val typeToken = object : TypeToken<JSONRPCRequest<List<WCBinanceOrder<*>>>>() {}.type
                    val request: JSONRPCRequest<List<WCBinanceOrder<*>>> = gson.fromJson(decrypted, typeToken)
                    callbacks?.onBnbSign(request.id, request.params.first())
                }
            }

            WCEvent.ElrondSign -> {
                val elrondOrder = Json.decodeFromString<WCElrondOrder>(decrypted)
                callbacks?.onElrondSign(elrondOrder)
            }
        }
    }
}