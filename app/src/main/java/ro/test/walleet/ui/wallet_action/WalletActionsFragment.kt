package ro.test.walleet.ui.wallet_action

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ro.test.walleet.common.extensions.showDialog
import ro.test.walleet.common.extensions.observe
import ro.test.walleet.databinding.FragmentWalletActivitiesBinding
import ro.test.walleet.paytomate.model.ApprovalSessionDetails
import ro.test.walleet.paytomate.sdk.model.WCElrondOrder
import ro.test.walleet.ui.main.WalletController
import ro.test.walleet.ui.wallet_action.bottom_sheets.ApproveSessionBottomSheet
import ro.test.walleet.ui.wallet_action.bottom_sheets.TransactionBottomSheet

class WalletActionsFragment : Fragment() {
    private val navigationArguments: WalletActionsFragmentArgs by navArgs()
    private val walletController: WalletController by activityViewModels()
    private val viewModel: WalletActionsViewModel by viewModels {
        val application = requireActivity().application
        WalletActionsViewModelFactory(application, walletController)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWalletActivitiesBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.initialize(navigationArguments.walletConnectUri)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    private fun setObservers() {
        observe(viewModel.onShowApprovalSessionDialog) { sessionDetails ->
            sessionDetails?.let {
                displayApprovalSessionDialog(it)
            }
        }

        observe(viewModel.receivedTransaction) { transaction ->
            transaction?.let {
                Log.i("DBG ELROND TRANSACTION", "${transaction.id} ${transaction.jsonrpc} ${transaction.params.data}")
                displayTransactionBottomSheet(it)
            }
        }
    }

    private fun displayApprovalSessionDialog(sessionDetails: ApprovalSessionDetails) {
        showDialog {
            ApproveSessionBottomSheet(
                sessionDetails,
                viewModel::approveCurrentSession,
                viewModel::rejectSession
            )
        }
    }

    private fun displayTransactionBottomSheet(order: WCElrondOrder) {
        showDialog {
            TransactionBottomSheet(
                order,
                viewModel::signTransaction,
                {}
            )
        }
    }

}
