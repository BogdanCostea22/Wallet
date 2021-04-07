package ro.test.walleet.ui.wallet_action.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ro.test.walleet.databinding.DialogSessionApprovalBinding
import ro.test.walleet.paytomate.model.ApprovalSessionDetails

class ApproveSessionBottomSheet(
    private val sessionDetails: ApprovalSessionDetails,
    private val approveSessionFunction: () -> Unit,
    private val rejectSession: () -> Unit,
) : BottomSheetDialogFragment() {

    lateinit var binding: DialogSessionApprovalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSessionApprovalBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.sessionInformation = sessionDetails
        binding.approveSession = {
            approveSessionFunction()
            dismiss()
        }
        binding.rejectSession = {
            rejectSession()
            dismiss()
        }
        return binding.root
    }
}
