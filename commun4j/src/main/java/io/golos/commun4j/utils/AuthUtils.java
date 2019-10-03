package io.golos.commun4j.utils;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.spongycastle.crypto.digests.GeneralDigest;
import org.spongycastle.crypto.digests.RIPEMD160Digest;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import io.golos.commun4j.model.AuthType;


@SuppressWarnings("unused")
public class AuthUtils {
    private static final int HASH_BYTES_LENGTH = 4;
    private static String GOLOS_ADDRESS_PREFIX = "GLS";

    private AuthUtils() {
    }

    public static Map<AuthType, String> generatePublicWiFs(@Nonnull String login,
                                                           @Nonnull String password,
                                                           @Nonnull AuthType[] roles) {
        Map<AuthType, String> out = new HashMap<>();
        MessageDigest messageDigest256 = null;
        try {
            messageDigest256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        for (AuthType role : roles) {
            final String seed = login + role.toString() + password;
            final String brainKey = join(seed.trim().split("[\\s]+"), " ");
            byte[] hashSha256 = null;
            try {
                hashSha256 = messageDigest256.digest(brainKey.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final BigInteger bigInteger = new BigInteger(1, hashSha256);

            final byte[] pubBuff = ECKey.fromPrivate(bigInteger).getPubKey();
            byte[] addy = addAll(pubBuff, generateChecksumRipeMd160(pubBuff));
            out.put(role, GOLOS_ADDRESS_PREFIX + Base58.encode(addy));
        }
        return out;
    }

    public static Map<AuthType, String> generatePrivateWiFs(@Nonnull String login,
                                                            @Nonnull String password,
                                                            @Nonnull AuthType[] roles) {
        Map<AuthType, String> out = new HashMap<>();
        MessageDigest messageDigest256 = null;
        try {
            messageDigest256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        for (AuthType role : roles) {
            final String seed = login + role.toString() + password;
            final String brainKey = join(seed.trim().split("[\\s]+"), " ");
            byte[] hashSha256 = null;
            try {

                hashSha256 = messageDigest256.digest(brainKey.getBytes("UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final byte[] privateKey = addAll(new byte[]{(byte) 0x80}/*version is 128, for check*/, hashSha256);

            final byte[] privateWiF = addAll(privateKey, generateChecksumSha256(privateKey));


            out.put(role, Base58.encode(privateWiF));
        }
        return out;
    }

    public static byte[] generateChecksumSha256(@Nonnull byte[] in) {
        MessageDigest messageDigest256 = null;
        try {
            messageDigest256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] checksum = messageDigest256.digest(in);
        checksum = messageDigest256.digest(checksum);
        return subarray(checksum, 0, HASH_BYTES_LENGTH);
    }

    public static byte[] generateChecksumRipeMd160(@Nonnull byte[] in) {
        final GeneralDigest digest = new RIPEMD160Digest();
        digest.update(in, 0, in.length);
        byte[] checkSum = new byte[digest.getDigestSize()];
        digest.doFinal(checkSum, 0);

        return subarray(checkSum, 0, HASH_BYTES_LENGTH);
    }

    public static void checkPrivateWiF(@Nonnull String privateWiFKey) throws IllegalArgumentException {
        if (privateWiFKey.length() != 51)
            throw new IllegalArgumentException("key must be 51 chars length");

        final byte[] privateWifBytesAll = Base58.decode(privateWiFKey);
        if (privateWifBytesAll[0] != (byte) 0x80)
            throw new IllegalArgumentException("wrong version, must be 0x80");
        byte[] privateKeyBytes = subarray(privateWifBytesAll, 0, privateWifBytesAll.length - HASH_BYTES_LENGTH);
        final byte[] checkSum = subarray(privateWifBytesAll, privateWifBytesAll.length - HASH_BYTES_LENGTH, privateWifBytesAll.length);
        byte[] newCheckSum = generateChecksumSha256(privateKeyBytes);
        if (!Arrays.equals(checkSum, newCheckSum))
            throw new IllegalArgumentException("Invalid WIF key (checksum miss-match)");

        privateKeyBytes = subarray(privateKeyBytes, 1, privateKeyBytes.length);
        if (privateKeyBytes.length != 32)
            throw new IllegalArgumentException("key must be 32 bytes length");
    }

    public static void checkPublicWiF(@Nonnull String publicWifKey) throws IllegalArgumentException {
        if ((publicWifKey.length() - GOLOS_ADDRESS_PREFIX.length() != 50))
            throw new IllegalArgumentException("key without prefix must be 40 chars length");
        if (!publicWifKey.substring(0, GOLOS_ADDRESS_PREFIX.length()).equals(GOLOS_ADDRESS_PREFIX)) {
            throw new IllegalArgumentException("key without prefix must be " + GOLOS_ADDRESS_PREFIX);
        }
        publicWifKey = publicWifKey.substring(GOLOS_ADDRESS_PREFIX.length());
        final byte[] publicWifBytesWithHash = Base58.decode(publicWifKey);
        byte[] publicKeyBytes = subarray(publicWifBytesWithHash, 0, publicWifBytesWithHash.length - HASH_BYTES_LENGTH);
        final byte[] checkSum = subarray(publicWifBytesWithHash, publicWifBytesWithHash.length - HASH_BYTES_LENGTH, publicWifBytesWithHash.length);
        byte[] newCheckSum = generateChecksumRipeMd160(publicKeyBytes);
        if (!Arrays.equals(checkSum, newCheckSum))
            throw new IllegalArgumentException("Invalid WIF key (checksum miss-match)");
        if (publicKeyBytes.length != 33)
            throw new IllegalArgumentException("key must be 33 bytes length");
    }

    public static boolean isWiFsValid(@Nonnull String privateWiF, @Nonnull String publicWiF) {
        try {
            checkPrivateWiF(privateWiF);
            checkPublicWiF(publicWiF);
            byte[] privKeyBytesAll = Base58.decode(privateWiF);
            byte[] cleaned = subarray(privKeyBytesAll, 1, privKeyBytesAll.length - 4);
            byte[] publicKey = ECKey.fromPrivate(cleaned).getPubKey();

            byte[] pubPotential = Base58.decode(publicWiF.substring(3));
            byte[] potentialCleaned = subarray(pubPotential, 0, pubPotential.length - 4);
            return Arrays.equals(publicKey, potentialCleaned);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static <T> String join(T[] array, String separator) {
        return array == null ? null : join((Object[]) array, separator, 0, array.length);
    }

    private static <T> String join(List<T> list, String separator) {
        return list == null ? null : join(list.toArray(), separator, 0, list.size());
    }

    private static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        } else {
            int noOfItems = endIndex - startIndex;
            if (noOfItems <= 0) {
                return "";
            } else {
                StringBuilder buf = new StringBuilder(noOfItems * 16);

                for (int i = startIndex; i < endIndex; ++i) {
                    if (i > startIndex) {
                        buf.append(separator);
                    }

                    if (array[i] != null) {
                        buf.append(array[i]);
                    }
                }

                return buf.toString();
            }
        }

    }

    private static byte[] addAll(final byte[] array1, final byte... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        final byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    private static byte[] clone(final byte[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private static byte[] subarray(final byte[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        final int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return EMPTY_BYTE_ARRAY;
        }

        final byte[] subarray = new byte[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }
}

