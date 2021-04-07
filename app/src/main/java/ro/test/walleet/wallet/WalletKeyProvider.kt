package ro.test.walleet.wallet

import ro.test.walleet.paytomate.sdk.model.WCElrondOrder
import ro.test.walleet.wallet.model.TransactionData
import wallet.core.jni.CoinType

interface WalletKeyProvider {

    fun generateNewMnemonic(): String

    fun setSpecificMnemonic(mnemonic: String, passPhrase: String = "")

    fun generateAddress(): String

    fun signTransaction(data: TransactionData)

    fun signElrondTransaction(order: WCElrondOrder): String
}


