package ro.test.walleet.paytomate.model

import ro.test.walleet.paytomate.sdk.model.WCPeerMeta

class ApprovalSessionMapper {

    companion object {

        fun mapFom(wcPeerMeta: WCPeerMeta): ApprovalSessionDetails =
            ApprovalSessionDetails(
                name = wcPeerMeta.name,
                description = wcPeerMeta.description,
                chainUrl = wcPeerMeta.url,
                iconUrl = wcPeerMeta.icons.first(),
            )
    }
}