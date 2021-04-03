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
import ro.test.walleet.dapp_connection.WalletConnectionCallback
import java.io.File

class WCUseCaseImpl(
    val application: Application
) : WCUseCase {
    private lateinit var storage: FileWCSessionStore
    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory())
        .build()
    private val okHtttp: OkHttpClient = OkHttpClient.Builder().build()

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

    override fun reestablishedConnection(key: String) {
        //Todo should remove this name ?
        val config = Session.Config.fromWCUri(key)
        val session = WCSession(
            config,
            MoshiPayloadAdapter(moshi),
            storage,
            OkHttpTransport.Builder(okHtttp, moshi),
            Session.PeerMeta(name = "Example App")
        )
        session.offer()
        session.addCallback(WalletConnectionCallback)
        session.approvedAccounts()
    }
}