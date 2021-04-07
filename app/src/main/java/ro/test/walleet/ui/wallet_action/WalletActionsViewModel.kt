package ro.test.walleet.ui.wallet_action

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import ro.test.walleet.paytomate.PaytomatUseCase
import ro.test.walleet.paytomate.model.ApprovalSessionDetails
import ro.test.walleet.paytomate.model.WCCallbackState
import ro.test.walleet.paytomate.sdk.model.WCElrondOrder
import ro.test.walleet.ui.main.WalletController

class WalletActionsViewModel(
    application: Application,
    walletController: WalletController
) : AndroidViewModel(application) {
    val connectionStatus = MutableLiveData<String>()
    lateinit var walletConnectUri: String
    private val useCase = PaytomatUseCase(walletController)
    private val stateLiveData = useCase.sessionEvents.asLiveData(viewModelScope.coroutineContext)

    init {
        Log.i("DBG_WALLET_ACTIONS", "usecase:$useCase")
        Log.i("DBG_WALLET_ACTIONS", " ${useCase.sessionEvents.value}")
    }

    val onShowConnectingProgressOverlay: LiveData<Boolean> =
        Transformations.map(stateLiveData, ::validateConditionsForConnectingState)

    val isConnectButtonEnabled: LiveData<Boolean> =
        Transformations.map(stateLiveData, ::validateConditionsForConnectedState)

    val onShowApprovalSessionDialog: LiveData<ApprovalSessionDetails?> =
        Transformations.map(stateLiveData, ::getSessionDetailsFrom)

    val receivedTransaction: LiveData<WCElrondOrder?> = Transformations.map(stateLiveData, ::getTransactionFrom)


    fun initialize(walletUri: String) {
        walletConnectUri = walletUri
    }

    fun connect() {
        useCase.connectTo(walletConnectUri)
    }

    fun approveCurrentSession() {
        useCase.approveSession()
    }

    fun signTransaction(order: WCElrondOrder) {
        useCase.signTransaction(order)
    }

    fun rejectSession() {
    }

    private fun validateConditionsForConnectingState(state: WCCallbackState) =
        state is WCCallbackState.Connecting || state is WCCallbackState.Connected

    private fun validateConditionsForConnectedState(state: WCCallbackState) =
        state is WCCallbackState.Disconnected || state is WCCallbackState.FailedToConnect

    private fun getSessionDetailsFrom(wcCallbackState: WCCallbackState): ApprovalSessionDetails? =
        if (wcCallbackState is WCCallbackState.ApprovalSession)
            wcCallbackState.approvalSessionDetails
        else
            null

    private fun getTransactionFrom(wcCallbackState: WCCallbackState?): WCElrondOrder? =
        if(wcCallbackState is WCCallbackState.ElrondTransaction)
            wcCallbackState.transactionDetails
        else
            null

    override fun onCleared() {
        super.onCleared()
        Log.i("DBG_WALLET_ACTIONS", "On Cleared")
    }
}

//fun printChainId() {
//        val privateStringField: Field = WCSession::class.java.getDeclaredField("chainId")
//
//        privateStringField.isAccessible = true
//
//        val fieldValue = privateStringField.get(useCase.session) as Long?
//        Log.i("DBG","fieldValue = $fieldValue")
//}
