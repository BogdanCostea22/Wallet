package ro.test.walleet.wallet

import ro.test.walleet.wallet.model.TransactionData
import wallet.core.jni.CoinType

object SigningTransactionMediator {

    fun signTransaction(coinType: CoinType, transactionData: TransactionData) {
        when(coinType) {
            CoinType.BITCOIN -> signBitcoinTransaction()
            CoinType.ETHEREUM -> signEthereumTransaction()
            CoinType.ELROND -> signElrondTransaction()
            else -> { // no-op
            }
        }
    }

    private fun signEthereumTransaction() {
//        val signerInput = Ethereum.SigningInput.newBuilder().apply {
//            this.chainId = ByteString.copyFrom(BigInteger("01").toByteArray())
//            this.gasPrice = BigInteger("d693a400", 16).toByteString() // decimal 3600000000
//            this.gasLimit = BigInteger("5208", 16).toByteString()     // decimal 21000
//            this.toAddress = dummyReceiverAddress
//            this.amount = BigInteger("0348bca5a16000", 16).toByteString()
//            this.privateKey = ByteString.copyFrom(secretPrivateKey.data())
//        }.build()
//        val output = AnySigner.sign(signerInput, CoinType.ETHEREUM, Ethereum.SigningOutput.parser())
//        println("Signed transaction: \n${signerOutput.encoded.toByteArray().toHexString()}")
    }

    private fun signElrondTransaction() {

    }

    private fun signBitcoinTransaction() {

    }

}