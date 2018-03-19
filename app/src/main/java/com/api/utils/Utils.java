package com.api.utils;

import com.api.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


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

    public static String executeScript(String script, String options, String [] params) {
        List<String> commands = new ArrayList<>();
        commands.add(Constant.PYTHON_CMD);
        commands.add(Constant.SCRIPTS_PATH + script);
        commands.add(options);
        commands.addAll(Arrays.asList(params));

        String execCmd = commands.stream().map(i -> i.toString()).collect(Collectors.joining(" "));
        StringBuffer output = new StringBuffer();
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(execCmd);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        } catch (Exception e) {
            output.setLength(0);
            output.append("Failed to execute script: " + execCmd);
            e.printStackTrace();
        } finally {
            return output.toString();
        }
    }
}
