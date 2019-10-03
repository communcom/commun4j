package io.golos.commun4j.abi.writer.preprocessor.template;

import com.memtrip.eos.abi.writer.ByteWriter;
import com.memtrip.eos.abi.writer.Squishable;
import io.golos.commun4j.abi.writer.preprocessor.model.PackedTransactionAuthorization;
import io.golos.abi.writer.ByteWriter;
import io.golos.abi.writer.Squishable;
import io.golos.commun4j.abi.writer.ByteWriter;
import io.golos.commun4j.abi.writer.Squishable;

public class PackedTransactionAuthorizationSquishable implements Squishable<PackedTransactionAuthorization> {

    private final AbiBinaryGen abiBinaryGen;

    PackedTransactionAuthorizationSquishable(AbiBinaryGen abiBinaryGen) {
        this.abiBinaryGen = abiBinaryGen;
    }

    @Override
    public void squish(PackedTransactionAuthorization packedTransactionAuthorization, ByteWriter byteWriter) {
        byteWriter.putAccountName(packedTransactionAuthorization.actor());
        byteWriter.putName(packedTransactionAuthorization.permission());
    }
}
