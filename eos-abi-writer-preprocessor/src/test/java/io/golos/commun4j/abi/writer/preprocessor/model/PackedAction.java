package io.golos.commun4j.abi.writer.preprocessor.model;

import com.memtrip.eos.abi.writer.Abi;
import com.memtrip.eos.abi.writer.AccountNameCollectionCompress;
import com.memtrip.eos.abi.writer.AccountNameCompress;
import com.memtrip.eos.abi.writer.CollectionCompress;
import com.memtrip.eos.abi.writer.DataCompress;
import com.memtrip.eos.abi.writer.NameCompress;

import java.util.List;

import io.golos.abi.writer.Abi;
import io.golos.abi.writer.AccountNameCollectionCompress;
import io.golos.abi.writer.AccountNameCompress;
import io.golos.abi.writer.CollectionCompress;
import io.golos.abi.writer.DataCompress;
import io.golos.abi.writer.NameCompress;
import io.golos.commun4j.abi.writer.Abi;
import io.golos.commun4j.abi.writer.AccountNameCollectionCompress;
import io.golos.commun4j.abi.writer.AccountNameCompress;
import io.golos.commun4j.abi.writer.CollectionCompress;
import io.golos.commun4j.abi.writer.DataCompress;
import io.golos.commun4j.abi.writer.NameCompress;

@Abi
public class PackedAction {

    private final String account;
    private final String name;
    private final List<PackedTransactionAuthorization> authorization;
    private final String data;
    private final List<String> producers;

    @AccountNameCompress
    public String account() {
        return account;
    }

    @NameCompress
    public String name() {
        return name;
    }

    @CollectionCompress
    public List<PackedTransactionAuthorization> authorization() {
        return authorization;
    }

    @DataCompress
    public String data() {
        return data;
    }

    @AccountNameCollectionCompress
    public List<String> producers() {
        return producers;
    }

    public PackedAction(
        String account,
        String name,
        List<PackedTransactionAuthorization> authorization,
        String data,
        List<String> producers
    ) {
        this.account = account;
        this.name = name;
        this.authorization = authorization;
        this.data = data;
        this.producers = producers;
    }
}
