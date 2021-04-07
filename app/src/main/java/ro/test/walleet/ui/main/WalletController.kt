package ro.test.walleet.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.test.walleet.paytomate.sdk.model.WCElrondOrder
import ro.test.walleet.wallet.WalletKeyProviderImpl
import ro.test.walleet.wallet.model.WalletConfiguration
import wallet.core.jni.CoinType


class WalletController: ViewModel(), ApplicationWalletProvider {
    private val walletConfiguration = WalletConfiguration.StrongConfiguration(CoinType.ELROND)
    private val walletUseCase = WalletKeyProviderImpl(walletConfiguration)

    private val _mnemonicPhrase = MutableLiveData<List<String>>()
    val mnemonicPhrase: LiveData<List<String>>
        get() = _mnemonicPhrase

    init {
        setupDefaultMenmonic()
        getAddressForConfiguration()
    }

    private fun setupDefaultMenmonic() {
        val mnemonic = walletUseCase.generateNewMnemonic()
        updateListOfWordsWith(mnemonic)
    }

    override fun getWalletConfiguration(): WalletConfiguration =
        walletConfiguration

    override fun getAddressForConfiguration() =
        walletUseCase.generateAddress()

    override fun signElrondTransaction(order: WCElrondOrder): String =
        walletUseCase.signElrondTransaction(order)

    fun setSpecificMnemonic(mnemonic: String) {
        walletUseCase.setSpecificMnemonic(mnemonic)
        updateListOfWordsWith(mnemonic)
    }

    private fun updateListOfWordsWith(newMnemonic: String) {
        val phraseWords = newMnemonic.split(" ")
        _mnemonicPhrase.value = phraseWords
    }
}
