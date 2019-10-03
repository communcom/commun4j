package io.golos.commun4j.abi.writer.preprocessor.template;

import io.golos.commun4j.abi.writer.ByteWriter;
import io.golos.commun4j.abi.writer.bytewriter.DefaultByteWriter;
import io.golos.commun4j.abi.writer.compression.CompressionFactory;
import io.golos.commun4j.abi.writer.compression.CompressionType;
import io.golos.commun4j.abi.writer.preprocessor.model.PackedAction;
import io.golos.commun4j.abi.writer.preprocessor.model.PackedTransactionAuthorization;
import io.golos.commun4j.abi.writer.preprocessor.model.Transaction;
import io.golos.commun4j.core.hex.DefaultHexWriter;
import io.golos.commun4j.core.hex.HexWriter;

import java.util.List;

public class AbiBinaryGen {

    private final ByteWriter byteWriter;
    private final HexWriter hexWriter;
    private final CompressionType compressionType;

    private final TransactionSquishable transactionSquishable;
    private final PackedActionSquishable packedActionSquishable;
    private final PackedTransactionAuthorizationSquishable packedTransactionAuthorizationSquishable;

    public AbiBinaryGen(CompressionType compressionType) {
        this(
            new DefaultByteWriter(512),
            new DefaultHexWriter(),
            compressionType
        );
    }

    public AbiBinaryGen(ByteWriter byteWriter, HexWriter hexWriter, CompressionType compressionType) {
        this.byteWriter = byteWriter;
        this.hexWriter = hexWriter;
        this.compressionType = compressionType;

        transactionSquishable = new TransactionSquishable(this);
        packedActionSquishable = new PackedActionSquishable(this);
        packedTransactionAuthorizationSquishable = new PackedTransactionAuthorizationSquishable(this);
    }

    public byte[] toBytes() {
        return new CompressionFactory(compressionType)
            .create()
            .compress(byteWriter.toBytes());
    }

    public String toHex() {
        byte[] compressedBytes = toBytes();
        return hexWriter.bytesToHex(compressedBytes, 0, compressedBytes.length, null);
    }

    public AbiBinaryGen squishTransaction(Transaction transaction) {
        transactionSquishable.squish(transaction, byteWriter);
        return this;
    }

    public AbiBinaryGen compressCollectionTransaction(
        List<Transaction> transactionList, ByteWriter byteWriter) {
        byteWriter.putVariableUInt(transactionList.size());
        for (Transaction transaction : transactionList) {
            transactionSquishable.squish(transaction, byteWriter);
        }
        return this;
    }

    public AbiBinaryGen compressPackedAction(PackedAction packedaction) {
        packedActionSquishable.squish(packedaction, byteWriter);
        return this;
    }

    public AbiBinaryGen squishCollectionPackedAction(
        List<PackedAction> packedactionList, ByteWriter byteWriter) {
        byteWriter.putVariableUInt(packedactionList.size());
        for (PackedAction packedaction : packedactionList) {
            packedActionSquishable.squish(packedaction, byteWriter);
        }
        return this;
    }

    public AbiBinaryGen compressPackedTransactionAuthorization(
        PackedTransactionAuthorization packedtransactionauthorization) {
        packedTransactionAuthorizationSquishable
            .squish(packedtransactionauthorization, byteWriter);
        return this;
    }

    public AbiBinaryGen compressCollectionPackedTransactionAuthorization(
        List<PackedTransactionAuthorization> packedtransactionauthorizationList,
        ByteWriter byteWriter) {
        byteWriter.putVariableUInt(packedtransactionauthorizationList.size());
        for (PackedTransactionAuthorization packedtransactionauthorization :
            packedtransactionauthorizationList) {
            packedTransactionAuthorizationSquishable
                .squish(packedtransactionauthorization, byteWriter);
        }
        return this;
    }
}
