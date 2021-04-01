package ro.test.walleet.wallet

import ro.test.walleet.wallet.model.TransactionData
import ro.test.walleet.wallet.model.WalletConfiguration
import wallet.core.jni.CoinType
import wallet.core.jni.HDWallet

class WalletKeyProviderImpl(private val walletConfiguration: WalletConfiguration) : WalletKeyProvider {
    companion object {
        private const val WALLET_NATIVE_LIBRARY_NAME = "TrustWalletCore"
    }
    private val wallet: HDWallet

    init {
        loadWalletNativeLibrary()
        val seedPhraseConfiguration = walletConfiguration.seedPhraseType.numberOfWords
        wallet = HDWallet(seedPhraseConfiguration, "")
    }


    private fun loadWalletNativeLibrary() {
        System.loadLibrary(WALLET_NATIVE_LIBRARY_NAME)
    }

    override fun generateSeedPhrase(): String {
        return wallet.mnemonic()
    }

    override fun generateAddressDeviation(coinType: CoinType): String =
        wallet.getAddressForCoin(coinType)

    override fun signTransaction(data: TransactionData) {
        SigningTransactionMediator.signTransaction(walletConfiguration.coinType, data)
    }
}



