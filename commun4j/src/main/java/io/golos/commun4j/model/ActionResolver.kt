package io.golos.commun4j.model

import io.golos.commun4j.abi.implementation.IAction
import io.golos.commun4j.abi.implementation.c.gallery.*
import io.golos.commun4j.abi.implementation.c.point.OpenArgsCPointStruct
import io.golos.commun4j.abi.implementation.c.point.OpenCPointStruct
import io.golos.commun4j.utils.toCyberName


fun getOpenArgsCPointStructIfActionSupportedForBalanceNotExistError(action: IAction): OpenCPointStruct? {
    val ramPayer = "c".toCyberName()
    return when (action) {
        is CreateCGalleryAction -> OpenCPointStruct(action.struct.message_id.author, action.struct.commun_code, action.struct.message_id.author)
        is UpdateCGalleryAction -> OpenCPointStruct(action.struct.message_id.author, action.struct.commun_code, ramPayer)
        is RemoveCGalleryAction -> OpenCPointStruct(action.struct.message_id.author, action.struct.commun_code, ramPayer)
        is UpvoteCGalleryAction -> OpenCPointStruct(action.struct.message_id.author, action.struct.commun_code, ramPayer)
        is DownvoteCGalleryAction -> OpenCPointStruct(action.struct.message_id.author, action.struct.commun_code, ramPayer)
        is UnvoteCGalleryAction -> OpenCPointStruct(action.struct.message_id.author, action.struct.commun_code, ramPayer)
        is ReportCGalleryAction -> OpenCPointStruct(action.struct.message_id.author, action.struct.commun_code, ramPayer)
        else -> null
    }
}