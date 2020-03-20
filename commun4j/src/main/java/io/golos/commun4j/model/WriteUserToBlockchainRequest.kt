package io.golos.commun4j.model

internal class WriteUserToBlockchainRequest(val phone: String?,
                                            val identity: String?,
                                            val username: String,
                                            val userId: String,
                                            val publicOwnerKey: String,
                                            val publicActiveKey: String)