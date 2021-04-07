package ro.test.walleet.ui.wallet_action

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ro.test.walleet.ui.main.WalletController

class WalletActionsViewModelFactory(
    private val application: Application,
    private val walletController: WalletController
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WalletActionsViewModel(application, walletController) as T
    }

}
