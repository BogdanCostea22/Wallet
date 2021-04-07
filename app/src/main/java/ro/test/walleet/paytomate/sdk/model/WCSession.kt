package ro.test.walleet.paytomate.sdk.model

import android.net.Uri
import ro.test.walleet.paytomate.sdk.util.guard
import org.bouncycastle.util.encoders.Hex

data class WCSession(val topic: String, val version: String, val bridge: Uri, val key: ByteArray) {

    companion object {
        fun fromURI(from: String): WCSession? {
            if (!from.startsWith("wc:")) return null
            val uri: Uri = guard { Uri.parse(from.replace("wc:", "wc://")) } ?: return null
            val topic: String = uri.userInfo ?: return null
            val version: String = uri.host ?: return null
            val bridge: Uri = guard { Uri.parse(uri.getQueryParameter("bridge") ?: return null) } ?: return null
            val key: ByteArray = Hex.decode(uri.getQueryParameter("key") ?: return null)
            return WCSession(topic, version, bridge, key)
        }
    }
}
