package io.golos.commun4j.model

internal class WriteUserToBlockchainRequest(val user: String,
                                            val owner: String,
                                            val active: String,
                                            val posting: String,
                                            val memo: String)