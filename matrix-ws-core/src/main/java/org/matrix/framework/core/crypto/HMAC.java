package org.matrix.framework.core.crypto;

import org.matrix.framework.core.util.AssertUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * http://en.wikipedia.org/wiki/Message_authentication_code
 */
public class HMAC {
    private byte[] key;
    private Hash hash = Hash.MD5;

    public byte[] digest(String message) {
        AssertUtils.assertNotNull(key, "key should not be null");
        try {
            String algorithm = "Hmac" + hash.value;
            Mac mac = Mac.getInstance(algorithm);
            SecretKey secretKey = new SecretKeySpec(key, algorithm);
            mac.init(secretKey);
            return mac.doFinal(message.getBytes(Charset.defaultCharset()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setSecretKey(byte[] key) {
        this.key = key;
    }

    public void setHash(Hash hash) {
        this.hash = hash;
    }

    public static enum Hash {
        MD5("MD5"), SHA1("SHA1"), SHA512("SHA512");

        Hash(String value) {
            this.value = value;
        }

        final String value;
    }
}
