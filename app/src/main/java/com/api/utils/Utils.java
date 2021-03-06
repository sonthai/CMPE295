package com.api.utils;

import com.api.constant.Constant;
import com.api.database.domain.ProductDao;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.iharder.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.security.Key;
import java.util.*;
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

    public static void executeScript(String script, Map<String, Object> params) {
        StringBuffer output = new StringBuffer();
        String execCmd = "";
        try {
            execCmd = getPythonCmd(script, params);

            Path scriptLog = Paths.get(Constant.SCRIPTS_PATH, "logs");
            if (!Files.exists(scriptLog)) {
                Files.createDirectory(scriptLog);
            }

            File out = new File(Paths.get(Constant.SCRIPTS_PATH, "logs", "out.txt").toString());
            File err = new File(Paths.get(Constant.SCRIPTS_PATH, "logs", "err.txt").toString());
            Process p = null;
            log.info("Cmd {}", execCmd);
            ProcessBuilder builder = new ProcessBuilder(new String[] {"sudo", "su", "ubuntu", "-c", execCmd});
            builder.redirectOutput(out);
            builder.redirectError(err);
            p = builder.start();
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
            log.info(output.toString());
        }
    }

    public static String removeImage(String imagePath) {
        String msg = "";
        try {
            Path filePath = Paths.get(imagePath);
            Files.deleteIfExists(filePath);
            msg = imagePath + " was removed successfully";
        } catch (NoSuchFileException e) {
            msg = "No such file/directory exists";
        }
        catch(DirectoryNotEmptyException e) {
            msg = "Directory is not empty.";
        }
        catch(IOException e) {
            msg = "Invalid permissions.";
        } finally {
            return msg;
        }
    }

    public static String saveIncomingImage(String id, String data) {
        try {
            String imageFilename = id + ".jpg";
            Path destinationFile = Paths.get(Constant.IMAGE_PATH, imageFilename);
            Base64.decodeToFile(data, destinationFile.toString());
            if (Files.exists(destinationFile)) {
                GroupPrincipal group = destinationFile.getFileSystem()
                                .getUserPrincipalLookupService()
                                .lookupPrincipalByGroupName("ubuntu");
                Files.getFileAttributeView(destinationFile, PosixFileAttributeView.class).setGroup(group);
                Set<PosixFilePermission> perms = Files.readAttributes(destinationFile,PosixFileAttributes.class).permissions();
                perms.add(PosixFilePermission.OWNER_READ);
                perms.add(PosixFilePermission.OWNER_WRITE);
                perms.add(PosixFilePermission.OWNER_EXECUTE);
                perms.add(PosixFilePermission.GROUP_READ);
                perms.add(PosixFilePermission.GROUP_WRITE);
                perms.add(PosixFilePermission.GROUP_EXECUTE);
                perms.add(PosixFilePermission.OTHERS_READ);
                perms.add(PosixFilePermission.OTHERS_EXECUTE);
                Files.setPosixFilePermissions(destinationFile, perms);
            }
            return imageFilename;
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

    private static String getPythonCmd(String script, Map<String, Object> params) {
        List<String> commands = new ArrayList<>();
        commands.add(Constant.PYTHON_CMD);
        commands.add(Paths.get(Constant.SCRIPTS_PATH, script).toString());
        commands.add(Constant.TOP_K_FLAG);
        commands.add(params.get(Constant.TOP_K_FLAG).toString());
        commands.add(Constant.VECTOR_PATH_PLAG);
        String vectorPath = "";
        if (StringUtils.isEmpty(params.get(Constant.GENDER))) {
            vectorPath = Paths.get(Constant.SCRIPTS_PATH, "image_vectors").toString() + "/";
        } else {
            vectorPath = Paths.get(Constant.SCRIPTS_PATH, "image_vectors", params.get(Constant.GENDER).toString()).toString();
        }
        commands.add("\"" + vectorPath + "\"");
        commands.add(Constant.IMAGE_FILE_FLAG);
        commands.add("\"" + params.get(Constant.IMAGE_FILE_FLAG).toString() + "\"");

        return  commands.stream().map(i -> i.toString()).collect(Collectors.joining(" "));
    }

    public static List<String> getSimilarProducts(String file) {
        List<String> productImageList = new ArrayList<>();
        try {
            byte[] jsonData = Files.readAllBytes(getProductJsonFilePath(file));
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<SimilarProduct>> mapType = new TypeReference<List<SimilarProduct>>() {};
            List<SimilarProduct> similarProducts = mapper.readValue(jsonData, mapType);
            productImageList = similarProducts.stream()
                        .map(p -> p.getFilename()).collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return  productImageList;
    }

    public static Path getProductJsonFilePath(String file) {
        return Paths.get(Constant.SCRIPTS_PATH, "nearest_neighbors", file + ".json");
    }

    private static class SimilarProduct {
        public String getFilename() {
            return filename;
        }

        public void setFilename(String fileName) {
            this.filename = fileName;
        }

        public double getSimilarity() {
            return similarity;
        }

        public void setSimilarity(double similarity) {
            this.similarity = similarity;
        }

        public SimilarProduct(){}

        private String filename;
        private double similarity;
    }
}
