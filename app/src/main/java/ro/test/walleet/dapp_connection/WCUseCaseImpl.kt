package ro.test.walleet.dapp_connection.use_case

import android.app.Application
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.walletconnect.Session
import org.walletconnect.impls.FileWCSessionStore
import org.walletconnect.impls.MoshiPayloadAdapter
import org.walletconnect.impls.OkHttpTransport
import org.walletconnect.impls.WCSession
import ro.test.walleet.paytomate.sdk.model.WCElrondOrder
import ro.test.walleet.ui.main.ApplicationWalletProvider
import ro.test.walleet.usecases.WCUseCase

import java.io.File
import java.util.*

class WCUseCaseImpl(
    private val walletProvider: ApplicationWalletProvider,
    private val application: Application,
) : WCUseCase {
    private lateinit var storage: FileWCSessionStore
    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val okHtttp: OkHttpClient = OkHttpClient.Builder().build()
    lateinit var session: WCSession
    init {
        initializeFileStore()
    }

    private fun initializeFileStore() {
        val cacheDir = application.cacheDir
        storage = FileWCSessionStore(
            File(cacheDir, "session_store.json").apply { createNewFile() },
            moshi
        )
    }

    fun approveSession(accounts: String) {
        session.approve(accounts = listOf(accounts), 528)
    }

    override fun connectTo(sessionUri: String) {
        val config = Session.Config.fromWCUri(sessionUri)
        session = WCSession(
            config,
            MoshiPayloadAdapter(moshi),
            storage,
            OkHttpTransport.Builder(okHtttp, moshi),
            Session.PeerMeta(name = "Example App", url="https://github.com/WalletConnect/kotlin-walletconnect-lib"),
            Random().nextInt().toString()
        )
        session.addCallback(WalletConnectionCallback)
        session.offer()
    }

    override fun approveSession() {
        val accounts = listOf(walletProvider.getAddressForConfiguration())
        val chainId = walletProvider.getWalletConfiguration().coinType.blockchain().value().toLong()
        session.approve(accounts, chainId)
    }

    override fun signTransaction(order: WCElrondOrder) {
        TODO("Not yet implemented")
    }
}

object WalletConnectionCallback: Session.Callback {
    override fun onMethodCall(call: Session.MethodCall) {
        Log.i("DBG", "WEBSOCKET")
    }

    override fun onStatus(status: Session.Status) {
        Log.i("DBG", "${status.toString()}")
    }

}