package ro.test.walleet.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
        val phraseWords = seedPhrase.split(" ")
        _seedPhrase.value = phraseWords
    }
}
