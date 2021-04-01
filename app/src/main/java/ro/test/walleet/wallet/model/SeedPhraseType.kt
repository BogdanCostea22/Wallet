package ro.test.walleet.wallet.model

enum class SeedPhraseType(val numberOfWords: Int) {
    EASY(128), STRONG(256)
}
