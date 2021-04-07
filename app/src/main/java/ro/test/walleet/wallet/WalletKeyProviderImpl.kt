package ro.test.walleet.wallet

import android.util.Log
import com.google.protobuf.ByteString
import org.komputing.khex.extensions.toHexString
import ro.test.walleet.paytomate.sdk.model.WCElrondOrder
import ro.test.walleet.wallet.model.TransactionData
import ro.test.walleet.wallet.model.WalletConfiguration
import wallet.core.java.AnySigner
import wallet.core.jni.HDWallet
import wallet.core.jni.proto.Elrond

class WalletKeyProviderImpl(private val walletConfiguration: WalletConfiguration) :
    WalletKeyProvider {
    companion object {
        private const val WALLET_NATIVE_LIBRARY_NAME = "TrustWalletCore"
    }

    private var wallet: HDWallet

    init {
        loadWalletNativeLibrary()
        val seedPhraseConfiguration = walletConfiguration.seedPhraseType.numberOfWords
        //Todo change this
        val mnemonic =
            "always giant jewel find impact mango act cheap step dove siege domain present blood target crop switch survey appear language stage nose flash tiger"
        wallet = HDWallet(mnemonic, "")
    }


    private fun loadWalletNativeLibrary() {
        System.loadLibrary(WALLET_NATIVE_LIBRARY_NAME)
    }

    override fun generateNewMnemonic(): String {
        return wallet.mnemonic()
    }

    override fun setSpecificMnemonic(mnemonic: String, passPhrase: String) {
        wallet = HDWallet(mnemonic, passPhrase)
    }

    override fun generateAddress(): String {
        return wallet.getAddressForCoin(walletConfiguration.coinType)
    }

    override fun signTransaction(data: TransactionData) {
        SigningTransactionMediator.signTransaction(walletConfiguration.coinType, data)
    }

    override fun signElrondTransaction(order: WCElrondOrder): String {
        val elrondTransaction = order.params
        val secretPrivateKey = wallet.getKeyForCoin(walletConfiguration.coinType)
        val receiverAddress = elrondTransaction.to
        val senderAddress = elrondTransaction.from

        val signerInput = Elrond.SigningInput.newBuilder().apply {
            val transaction = transactionBuilder.apply {
                receiver = receiverAddress
                sender = senderAddress
                gasLimit = elrondTransaction.gasLimit.toLong()
                gasPrice = elrondTransaction.gasPrice.toLong()
                chainId = elrondTransaction.chainId
                value = elrondTransaction.amount
                data = elrondTransaction.data
                nonce = elrondTransaction.nonce.toLong()
                version = elrondTransaction.version
            }.build()
            setTransaction(transaction)

            this.privateKey = ByteString.copyFrom(secretPrivateKey.data())

        }.build()

        Log.i("DBG Signed signature", "${signerInput.toString()}")

        val signerOutput =
            AnySigner.sign(signerInput, walletConfiguration.coinType, Elrond.SigningOutput.parser())

        Log.i("DBG Signed transaction", ": \n${signerOutput.encoded.toByteArray().toHexString()}")
        Log.i("DBG Signed signature", "${signerOutput.signature}")


        return signerOutput.encoded
    }

}



//    fun test() {
//        val wallet = HDWallet("256", "")
//        Log.i("DBG Test","Mnemonica ${wallet.mnemonic().toString()}")
//        Log.i("DBG Test","Seed ${wallet.seed()}")
//        Log.i("DBG Test Key", "${wallet.getAddressForCoin(CoinType.ELROND)}")
//        repeat(10) {
//            val wallet1 = HDWallet("256", "")
//            Log.i("DBG Test","Iteration1: $it, ${wallet1.getKeyForCoin(CoinType.ELROND).publicKeyEd25519.data()}")
//        }
//
//        val key = wallet.getKey(CoinType.ELROND, "m/44'/508'/0'/0'/0'")   // m/44'/60'/1'/0/0
//        val address = CoinType.ETHEREUM.deriveAddress(key)
//        Password.password = "erd1haqrcld8czhhjwdja7ascvef57j74xfzhmafkasscrnals28yzfqx5g2hp"
////        Password.password = AnyAddress(wallet.getAddressForCoin(CoinType.ELROND), CoinType.ELROND).data().toHex()
//        Log.i("DBG Test Key", address)
////        Log.i("DBG Final Test Key",
////            AnyAddress(wallet.getAddressForCoin(CoinType.ELROND), CoinType.ELROND).data().toHex()
////        )
//    }
//}
