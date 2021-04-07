package ro.test.walleet.common.binding_adapters

import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

@BindingAdapter("onAddChipsToChipGroup")
fun ChipGroup.setChips(words: List<String>?) {
    words?.let {
        setPhraseWords(words)
    }
}

private fun ChipGroup.setPhraseWords(words: List<String>) {
    val chipsForWords = words.map { createChipFor(it) }
    chipsForWords.forEach(::addChipToChipGroup)
}

private fun ChipGroup.addChipToChipGroup(chip: Chip) {
    this.addView(chip)
}

fun ChipGroup.createChipFor(value: String): Chip =
    Chip(context).apply {
        text = value
    }
