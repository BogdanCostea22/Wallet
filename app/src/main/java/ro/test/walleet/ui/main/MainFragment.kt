package ro.test.walleet.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import ro.test.walleet.R
import ro.test.walleet.common.createChipFor
import ro.test.walleet.common.observe
import ro.test.walleet.dapp_connection.use_case.WCUseCaseImpl
import ro.test.walleet.databinding.FragmentMainBinding
import ro.test.walleet.ui.scanner.ScannerFragment

class MainFragment : Fragment() {
    private val viewModel by viewModels<MainViewModel>()
    lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setObservers()
        setObserversForNavigation()

        return binding.root
    }

    private fun setObserversForNavigation() {
        val navigationLiveData: LiveData<String> = findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData(ScannerFragment.CODE_BAR)!!
        observe(navigationLiveData) {
            findNavController().popBackStack()
            AlertDialog.Builder(requireContext())
                .setMessage(it)
                .setPositiveButton("OK") { dialog, _ ->
                    Log.i("DBG", it)
                    WCUseCaseImpl(requireActivity().application).reestablishedConnection(it)
                    dialog.dismiss()}
                .show()
        }
    }

    private fun setObservers() {
        observe(viewModel.seedPhrase) { words ->
            setPhraseWords(words)
        }
    }

    private fun setPhraseWords(words: List<String>) {
        val chipsForWords = words.map { createChipFor(it) }
        chipsForWords.forEach(::addChipToChipGroup)
    }

    private fun addChipToChipGroup(chip: Chip) {
        binding.seedPhraseChipGroup.addView(chip)
    }

    override fun onDestroy() {
        super.onDestroy()
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<String>("CODE_BAR")
    }
}