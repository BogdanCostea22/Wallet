package ro.test.walleet.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ro.test.walleet.dapp_connection.WalletConnectionCallback
import ro.test.walleet.wallet.WalletKeyProviderImpl
import ro.test.walleet.wallet.model.WalletConfiguration
import wallet.core.jni.CoinType

class MainViewModel : ViewModel() {
    private val walletUseCase =
        WalletKeyProviderImpl(WalletConfiguration.StrongConfiguration(CoinType.ELROND))

    private val _seedPhrase = MutableLiveData<List<String>>()
    val seedPhrase: LiveData<List<String>>
        get() = _seedPhrase

    //Todo implement wallet initialization step

    init {
        val phrase = walletUseCase.generateSeedPhrase()
        Log.i("SEED_PHRASE_DBG", phrase.split(" ").size.toString())
        val seedPhrase =  walletUseCase.generateSeedPhrase()
        Log.i("DEVIATION_DBG", walletUseCase.generateAddressDeviation())
        val phraseWords = seedPhrase.split(" ")
        _seedPhrase.value = phraseWords

        observerForWalletConnectStateChanges()
    }

    private fun observerForWalletConnectStateChanges() {
        WalletConnectionCallback.currentState.onEach{

        }.launchIn(CoroutineScope(Dispatchers.IO))
    }
}
