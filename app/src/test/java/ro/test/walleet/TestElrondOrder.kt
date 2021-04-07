package ro.test.walleet

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import ro.test.walleet.paytomate.sdk.model.WCElrondOrder

class TestElrondOrder {

    @Test
    fun testElrondOrderMapping() {
            val order = """{"id":1617636706894273,"jsonrpc":"2.0","method":"erd_sign","params":{"nonce":0,"from":"erd1trjwfygru5cprnahenz9ad4lt3gtvtqes3wzntv0xkdlfhupyg0qjavycv","to":"erd1trjwfygru5cprnahenz9ad4lt3gtvtqes3wzntv0xkdlfhupyg0qjavycv","amount":"1000000000000000000","gasPrice":"1000000000","gasLimit":"71000","data":"tfkudiofougoug","chainId":"D","version":1}}"""
            val elrondOrder = Json{isLenient = false}.decodeFromString<WCElrondOrder>(order)
            assert(elrondOrder.method == "erd_sign")
    }
}