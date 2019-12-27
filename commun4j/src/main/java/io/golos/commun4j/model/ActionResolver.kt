package io.golos.commun4j.model

import io.golos.commun4j.abi.implementation.IAction
import io.golos.commun4j.abi.implementation.c.gallery.*
import io.golos.commun4j.abi.implementation.c.point.OpenArgsCPointStruct
import io.golos.commun4j.utils.toCyberName


fun getOpenArgsCPointStructIfActionSupportedForBalanceNotExistError(action: IAction): OpenArgsCPointStruct? {
    val ramPayer = "c".toCyberName()
    return when (action) {
        is CreateCGalleryAction -> OpenArgsCPointStruct(action.struct.message_id.author, action.struct.commun_code, action.struct.message_id.author)
        is UpdateCGalleryAction -> OpenArgsCPointStruct(action.struct.message_id.author, action.struct.commun_code, ramPayer)
        is RemoveCGalleryAction -> OpenArgsCPointStruct(action.struct.message_id.author, action.struct.commun_code, ramPayer)
        is UpvoteCGalleryAction -> OpenArgsCPointStruct(action.struct.message_id.author, action.struct.commun_code, ramPayer)
        is DownvoteCGalleryAction -> OpenArgsCPointStruct(action.struct.message_id.author, action.struct.commun_code, ramPayer)
        is UnvoteCGalleryAction -> OpenArgsCPointStruct(action.struct.message_id.author, action.struct.commun_code, ramPayer)
        is ReportCGalleryAction -> OpenArgsCPointStruct(action.struct.message_id.author, action.struct.commun_code, ramPayer)
        else -> null
    }
}