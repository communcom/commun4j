package io.golos.commun4j.abi.writer.preprocessor.model;

import com.memtrip.eos.abi.writer.Abi;
import com.memtrip.eos.abi.writer.AccountNameCompress;

import io.golos.abi.writer.Abi;
import io.golos.abi.writer.AccountNameCompress;
import io.golos.commun4j.abi.writer.Abi;
import io.golos.commun4j.abi.writer.AccountNameCompress;

@Abi
public class PackedTransactionAuthorization {

    private final String actor;
    private final String permission;

    @AccountNameCompress
    public String actor() {
        return actor;
    }

    @AccountNameCompress
    public String permission() {
        return permission;
    }

    public PackedTransactionAuthorization(String actor, String permission) {
        this.actor = actor;
        this.permission = permission;
    }
}
