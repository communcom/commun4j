package io.golos.commun4j.utils;


import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

import io.golos.commun4j.core.crypto.EosPrivateKey;
import io.golos.commun4j.core.crypto.signature.PrivateKeySigning;

public class StringSigner {
    public static String signString(@NotNull String stringToSign,
                                    @NotNull String privateActiveKey) {
        return new PrivateKeySigning().sign(
                stringToSign.getBytes(StandardCharsets.UTF_8),
                new EosPrivateKey(privateActiveKey)
        );
    }

}
