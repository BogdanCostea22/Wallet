package ro.test.walleet.wallet.model

import wallet.core.jni.CoinType

sealed class WalletConfiguration (val coinType: CoinType, val seedPhraseType: SeedPhraseType) {
       class StrongConfiguration(coinType: CoinType): WalletConfiguration(coinType,
              SeedPhraseType.STRONG
       )
       class EasyConfiguration(coinType: CoinType): WalletConfiguration(coinType,
              SeedPhraseType.EASY
       )
}
