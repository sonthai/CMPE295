package com.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Arrays;

@Component
public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    private static String key = "Bar12345Bar12345";

    private Key getAesKey() throws Exception {
        return new SecretKeySpec(Arrays.copyOf(key.getBytes("UTF-8"), 16), "AES");
    }

    private Cipher getMutual() throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        return cipher;
    }

    public String encrypt(String text) throws Exception {
        Cipher cipher = getMutual();
        cipher.init(Cipher.ENCRYPT_MODE, getAesKey());
        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return new String(encrypted);
    }

    public String decrypt(String encrypted) throws Exception {
        Cipher cipher = getMutual();
        cipher.init(Cipher.DECRYPT_MODE, getAesKey());
        return new String(cipher.doFinal(encrypted.getBytes()));
    }
}
