package io.golos.commun4j.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class WriteUserToBlockchainRequest(val phone: String?,
                                            val identity: String?,
                                            val email: String?,
                                            val username: String,
                                            val userId: String,
                                            val publicOwnerKey: String,
                                            val publicActiveKey: String)

@JsonClass(generateAdapter = true)
internal class AppendReferralParent(val phone: String?, val identity: String?, val email: String?,
                                    val refferalId: String, val userId: String?)