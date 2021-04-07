package ro.test.walleet.ui.wallet_action.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ro.test.walleet.common.extensions.executeActionAndDismissDialog
import ro.test.walleet.databinding.DialogTransactionRequestBinding
import ro.test.walleet.paytomate.sdk.model.WCElrondOrder

class TransactionBottomSheet(
    private val order: WCElrondOrder,
    private val signRequest: (WCElrondOrder) -> Unit,
    private val rejectRequest: (WCElrondOrder) -> Unit,
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogTransactionRequestBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.order = order
        binding.approveSession = executeActionAndDismissDialog { signRequest(order) }
        binding.rejectSession = executeActionAndDismissDialog { rejectRequest(order) }

        return binding.root
    }
}
