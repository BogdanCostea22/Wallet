package ro.test.walleet.paytomate

import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import ro.test.walleet.common.runOnBackgroundThread
import ro.test.walleet.paytomate.model.WCCallbackState
import ro.test.walleet.paytomate.sdk.WCInteractor
import ro.test.walleet.paytomate.sdk.model.WCElrondOrder
import ro.test.walleet.paytomate.sdk.model.WCPeerMeta
import ro.test.walleet.paytomate.sdk.model.WCSession
import ro.test.walleet.ui.main.ApplicationWalletProvider
import ro.test.walleet.usecases.WCUseCase
import java.util.*

class PaytomatUseCase(
    private val applicationWalletProvider: ApplicationWalletProvider
) : WCUseCase {
    private lateinit var interactorWC: WCInteractor
    private val wcCallback = PaytomatCallback()
    val sessionEvents: StateFlow<WCCallbackState> = wcCallback.currentEvent

    override fun connectTo(sessionUri: String) {
        runOnBackgroundThread {
            val clientMeta = generateMetaPeer()

            val session: WCSession? = WCSession.fromURI(sessionUri)

            session?.let {
                interactorWC = WCInteractor(
                    session, clientMeta, Random().toString()
                )

                interactorWC.callbacks = wcCallback
                interactorWC.connect()
            } ?: run {
                wcCallback.forceConnectionFailed()
            }
        }
    }

    private fun generateMetaPeer() =
        WCPeerMeta(
            "Crypto Wallet",
            url = "https://www.computerworld.com/article/3389678/whats-a-crypto-wallet-and-does-it-manage-digital-currency.html"
        )

    override fun approveSession() {
        runOnBackgroundThread {
            val walletAddress = applicationWalletProvider.getAddressForConfiguration()
            val listOfAccounts = arrayOf(walletAddress)
            val chainId =
                applicationWalletProvider.getWalletConfiguration().coinType.blockchain().value()
            interactorWC.approveSession(listOfAccounts, chainId)
        }
    }

    override fun signTransaction(order: WCElrondOrder) {
        runOnBackgroundThread {
            val signedTransaction = applicationWalletProvider.signElrondTransaction(order)
//            val signature = signedtransaction.split(":").last().replace("\"", "")
//            val jsonRpcSignature = SignedTransaction(signature)
            interactorWC.approveRequest(order.id, signedTransaction)
        }
    }
}




