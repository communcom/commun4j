package io.golos.commun4j.abi.writer.preprocessor.template;

import io.golos.commun4j.abi.writer.ByteWriter;
import io.golos.commun4j.abi.writer.Squishable;
import io.golos.commun4j.abi.writer.preprocessor.model.PackedAction;

public class PackedActionSquishable implements Squishable<PackedAction> {

    private final AbiBinaryGen abiBinaryGen;

    PackedActionSquishable(AbiBinaryGen abiBinaryGen) {
        this.abiBinaryGen = abiBinaryGen;
    }

    @Override
    public void squish(PackedAction packedAction, ByteWriter byteWriter) {
        byteWriter.putAccountName(packedAction.account());
        byteWriter.putName(packedAction.name());
        byteWriter.putAccountNameCollection(packedAction.producers());

        abiBinaryGen.compressCollectionPackedTransactionAuthorization(packedAction.authorization(), byteWriter);

        byteWriter.putData(packedAction.data());
    }
}
