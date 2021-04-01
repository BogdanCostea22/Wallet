package ro.test.walleet.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import ro.test.walleet.common.createChipFor
import ro.test.walleet.common.observe
import ro.test.walleet.databinding.FragmentMainBinding

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
        return binding.root
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
}