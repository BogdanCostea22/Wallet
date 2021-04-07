package ro.test.walleet.ui.main

import ro.test.walleet.paytomate.sdk.model.WCElrondOrder
import ro.test.walleet.wallet.model.WalletConfiguration

interface ApplicationWalletProvider {
    fun getWalletConfiguration(): WalletConfiguration
    fun getAddressForConfiguration(): String
    fun signElrondTransaction(order: WCElrondOrder): String
}
