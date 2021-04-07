package ro.test.walleet.paytomate.sdk

import ro.test.walleet.paytomate.sdk.model.WCEncryptionPayload
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import java.security.SecureRandom
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * created by Alex Ivanov on 2019-06-12.
 */
object WCEncryptor {
    private fun randomBytes(n: Int): ByteArray = ByteArray(n).apply {
        SecureRandom().nextBytes(this)
    }

    private fun computeHMAC(payload: String, iv: String, key: ByteArray): String {
        val data: ByteArray = Hex.decode(payload) + Hex.decode(iv)
        return HMac(SHA256Digest()).apply {
            init(KeyParameter(key))
            update(data, 0, data.size)
        }.let { hmac ->
            ByteArray(hmac.macSize).apply { hmac.doFinal(this, 0) }
        }.let {
            Hex.toHexString(it)
        }
    }

    fun encrypt(data: ByteArray, key: ByteArray): WCEncryptionPayload {
        val ivBytes: ByteArray = randomBytes(16)
        Security.addProvider(BouncyCastleProvider())
        val encryptedBytes: ByteArray = Cipher.getInstance("AES/CBC/PKCS7PADDING").apply {
            init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(ivBytes))
        }.run { doFinal(data) }

        val encryptedData: String = Hex.toHexString(encryptedBytes)
        val iv: String = Hex.toHexString(ivBytes)
        val hmac: String = computeHMAC(encryptedData, iv, key)
        return WCEncryptionPayload(encryptedData, hmac, iv)
    }

    fun decrypt(payload: WCEncryptionPayload, key: ByteArray): ByteArray {
        val computedHmac: String = computeHMAC(payload.data, payload.iv, key)

        if (computedHmac != payload.hmac) {
            throw WCError.BadServerResponse
        }

        Security.addProvider(BouncyCastleProvider())
        return Cipher.getInstance("AES/CBC/PKCS7PADDING").apply {
            init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(Hex.decode(payload.iv)))
        }.run { doFinal(Hex.decode(payload.data)) }
    }
}