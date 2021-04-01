package ro.test.walleet.wallet

import ro.test.walleet.wallet.model.TransactionData
import wallet.core.jni.CoinType

interface WalletKeyProvider {

    fun generateSeedPhrase(): String

    fun generateAddressDeviation(coinType: CoinType = CoinType.ELROND): String

    fun signTransaction(data: TransactionData)
}


