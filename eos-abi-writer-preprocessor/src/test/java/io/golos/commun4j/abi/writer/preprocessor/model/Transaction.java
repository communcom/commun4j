package io.golos.commun4j.abi.writer.preprocessor.model;

import java.util.List;

import io.golos.abi.writer.Abi;
import io.golos.abi.writer.AssetCompress;
import io.golos.abi.writer.BlockNumCompress;
import io.golos.abi.writer.BlockPrefixCompress;
import io.golos.abi.writer.ChainIdCompress;
import io.golos.abi.writer.ChildCompress;
import io.golos.abi.writer.CollectionCompress;
import io.golos.abi.writer.FloatCompress;
import io.golos.abi.writer.HexCollectionCompress;
import io.golos.abi.writer.PublicKeyCompress;
import io.golos.abi.writer.StringCollectionCompress;
import io.golos.abi.writer.TimestampCompress;
import io.golos.abi.writer.VariableUIntCompress;
import io.golos.commun4j.abi.writer.Abi;
import io.golos.commun4j.abi.writer.AssetCompress;
import io.golos.commun4j.abi.writer.BlockNumCompress;
import io.golos.commun4j.abi.writer.BlockPrefixCompress;
import io.golos.commun4j.abi.writer.ChainIdCompress;
import io.golos.commun4j.abi.writer.ChildCompress;
import io.golos.commun4j.abi.writer.CollectionCompress;
import io.golos.commun4j.abi.writer.FloatCompress;
import io.golos.commun4j.abi.writer.HexCollectionCompress;
import io.golos.commun4j.abi.writer.PublicKeyCompress;
import io.golos.commun4j.abi.writer.StringCollectionCompress;
import io.golos.commun4j.abi.writer.TimestampCompress;
import io.golos.commun4j.abi.writer.VariableUIntCompress;

@Abi
public class Transaction {

    private final long expiration;
    private final int refBlockNum;
    private final long refBlockPrefix;
    private final long maxNetUsageWords;
    private final long maxCpuUsageMs;
    private final long delaySec;
    private final float speed;
    private final String publicKey;
    private final String asset;
    private final String chainId;
    private final PackedAction singleAction;
    private final List<String> hexCollection;
    private final List<PackedAction> contextFreeActions;
    private final List<PackedAction> actions;
    private final List<String> transactionExtensions;
    private final List<String> signatures;
    private final List<String> contextFreeData;

    @TimestampCompress
    public long getExpiration() {
        return expiration;
    }

    @BlockNumCompress
    public int getRefBlockNum() {
        return refBlockNum;
    }

    @BlockPrefixCompress
    public long getRefBlockPrefix() {
        return refBlockPrefix;
    }

    @VariableUIntCompress
    public long getMaxNetUsageWords() {
        return maxNetUsageWords;
    }

    @VariableUIntCompress
    public long getMaxCpuUsageMs() {
        return maxCpuUsageMs;
    }

    @VariableUIntCompress
    public long getDelaySec() {
        return delaySec;
    }

    @FloatCompress
    public float getSpeed() {
        return speed;
    }

    @PublicKeyCompress
    public String getPublicKey() {
        return publicKey;
    }

    @AssetCompress
    public String getAsset() {
        return asset;
    }

    @ChainIdCompress
    public String getChainId() {
        return chainId;
    }

    @ChildCompress
    public PackedAction getSingleAction() {
        return singleAction;
    }

    @HexCollectionCompress
    public List<String> getHexCollection() {
        return hexCollection;
    }

    @CollectionCompress
    public List<PackedAction> getContextFreeActions() {
        return contextFreeActions;
    }

    @CollectionCompress
    public List<PackedAction> getActions() {
        return actions;
    }

    @StringCollectionCompress
    public List<String> getTransactionExtensions() {
        return transactionExtensions;
    }

    public List<String> getSignatures() {
        return signatures;
    }

    public List<String> getContextFreeData() {
        return contextFreeData;
    }

    public Transaction(
        long expiration,
        int refBlockNum,
        long refBlockPrefix,
        long maxNetUsageWords,
        long maxCpuUsageMs,
        long delaySec,
        float speed,
        String publicKey,
        String asset,
        String chainId,
        PackedAction singleAction,
        List<String> hexCollection,
        List<PackedAction> contextFreeActions,
        List<PackedAction> actions,
        List<String> transactionExtensions,
        List<String> signatures,
        List<String> contextFreeData
    ) {
        this.expiration = expiration;
        this.refBlockNum = refBlockNum;
        this.refBlockPrefix = refBlockPrefix;
        this.maxNetUsageWords = maxNetUsageWords;
        this.maxCpuUsageMs = maxCpuUsageMs;
        this.delaySec = delaySec;
        this.speed = speed;
        this.publicKey = publicKey;
        this.asset = asset;
        this.chainId = chainId;
        this.singleAction = singleAction;
        this.hexCollection = hexCollection;
        this.contextFreeActions = contextFreeActions;
        this.actions = actions;
        this.transactionExtensions = transactionExtensions;
        this.signatures = signatures;
        this.contextFreeData = contextFreeData;
    }
}
