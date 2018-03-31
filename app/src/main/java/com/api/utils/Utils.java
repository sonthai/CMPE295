package com.api.utils;

import com.api.constant.Constant;
import com.api.database.domain.ProductDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
        return Cipher.getInstance("AES");
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

    public static String executeScript(String script, String flag, String urlImage) {
        List<String> commands = new ArrayList<>();
        commands.add(Constant.PYTHON_CMD);
        commands.add(Constant.SCRIPTS_PATH + script);
        commands.add(flag);
        commands.add("\"" + urlImage + "\"");

        String execCmd = commands.stream().map(i -> i.toString()).collect(Collectors.joining(" "));
        StringBuffer output = new StringBuffer();
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(execCmd);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
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

    public static String saveIncomingImage(String id, String data) {
        try {
            byte[] decodedImg = Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8));
            String filename = id + ".jpeg";
            Path destinationFile = Paths.get(Constant.IMAGE_PATH, filename);
            Files.write(destinationFile, decodedImg);
            return destinationFile.toString();
        } catch (IOException ex) {
            log.error("Failed to save image file  {}", ex);
            return Constant.UPLOAD_FAILED;
        }
    }

    public static List<ProductDao> createProductList(String filePath) {
        List<ProductDao> products = new ArrayList<>();
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(csvSplitBy);
                ProductDao product = new ProductDao();
                product.setId(Integer.valueOf(parts[0]));
                product.setProductName(parts[1]);
                product.setBrand(Integer.valueOf(parts[2]));
                product.setPrice(Double.valueOf(parts[3]));
                product.setImage(parts[4]);
                products.add(product);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return products;
        }
    }
}
