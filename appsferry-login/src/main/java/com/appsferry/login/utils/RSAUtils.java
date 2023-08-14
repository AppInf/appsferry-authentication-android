package com.appsferry.login.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {
    private static final String RSA = "RSA";
    private static final int RSA_1024_ENCYPT_LEN = 117;

    public RSAUtils() {
    }

    public static KeyPair generateRSAKeyPair() {
        return generateRSAKeyPair(1024);
    }

    public static KeyPair generateRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keyLength);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static byte[] encrypt(byte[] bytes, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(1, publicKey);
            return cipher.doFinal(bytes);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt(byte[] bytes, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(2, privateKey);
            return cipher.doFinal(bytes);
        } catch (Exception var3) {
            return null;
        }
    }

    public static PrivateKey getPrivateKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static PublicKey getPublicKey(String modulus, String publicExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger bigIntModulus = new BigInteger(modulus);
        BigInteger bigIntPrivateExponent = new BigInteger(publicExponent);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String modulus, String privateExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger bigIntModulus = new BigInteger(modulus);
        BigInteger bigIntPrivateExponent = new BigInteger(privateExponent);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static PublicKey getPublicKey(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64Utils.decode(publicKeyStr, 0);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException var5) {
            throw new Exception("NoSuchAlgorithmException");
        } catch (InvalidKeySpecException var6) {
            throw new Exception("InvalidKeySpecException");
        } catch (NullPointerException var7) {
            throw new Exception("PublicKey Null");
        }
    }

    public static PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64Utils.decode(privateKeyStr, 0);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        } catch (NoSuchAlgorithmException var5) {
            throw new Exception("NoSuchAlgorithmException");
        } catch (InvalidKeySpecException var6) {
            throw new Exception("InvalidKeySpecException");
        } catch (NullPointerException var7) {
            throw new Exception("PrivateKey Nul");
        }
    }

    public static PublicKey getPublicKey(byte[] rsa) throws Exception {
        try {
            return getPublicKey(getDecodeString(rsa));
        } catch (IOException var2) {
            throw new Exception("IOException");
        } catch (NullPointerException var3) {
            throw new Exception("NullPointerException");
        }
    }

    public static PrivateKey getPrivateKey(InputStream in) throws Exception {
        try {
            return getPrivateKey(readKey(in));
        } catch (IOException var2) {
            throw new Exception("IOException");
        } catch (NullPointerException var3) {
            throw new Exception("NullPointerException");
        }
    }

    private static String readKey(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String readLine = null;
        StringBuilder sb = new StringBuilder();

        while ((readLine = br.readLine()) != null) {
            if (readLine.charAt(0) != '-') {
                sb.append(readLine);
                sb.append('\r');
            }
        }

        return sb.toString();
    }

    public static byte[] encryptRSA(byte[] src, String publicKeyString) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            PublicKey skeySpec = getPublicKey(publicKeyString);
            cipher.init(1, skeySpec);
            int srcLen = src.length;
            int srcOffset = 0;
            int encyptLen = 117;
            int outputSize = 0;
            byte[] buffer = new byte[2048];

            byte[] enBytes;
            do {
                if (srcOffset + 117 > srcLen) {
                    encyptLen = srcLen - srcOffset;
                }

                enBytes = cipher.doFinal(src, srcOffset, encyptLen);
                int tempLen = enBytes.length;
                if (outputSize + tempLen > buffer.length) {
                    byte[] newBuffer = new byte[buffer.length * 2];
                    System.arraycopy(buffer, 0, newBuffer, 0, outputSize);
                    buffer = newBuffer;
                }

                System.arraycopy(enBytes, 0, buffer, outputSize, tempLen);
                srcOffset += encyptLen;
                outputSize += tempLen;
            } while (srcOffset < srcLen);

            enBytes = new byte[outputSize];
            System.arraycopy(buffer, 0, enBytes, 0, outputSize);
            return enBytes;
        } catch (Exception var12) {
            var12.printStackTrace();
            return null;
        }
    }

    public static String encryUAC(String publicKey, byte[] data) {
        try {
            String str = new String(publicKey.getBytes("UTF-8"));
            String pubk = buildNewPubkey(str, false);
            return pubk;
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
            return "";
        }
    }

    public static String encryO(String content) {
        String originStr = buildNewPubkey(content, false);
        return buildNewPubkey(originStr, true);
    }

    public static String buildNewPubkey(String publicKey, boolean increment) {
        int pub_key_size = publicKey.length();
        int size = 0;
        int padding = 0;
        int pad_len = 0;
        pad_len = pad_len + 0;
        int arr_len = pub_key_size + pad_len;
        char[] charStr = new char[arr_len];
        char[] publicKeyChar = publicKey.toCharArray();

        for (int i = 0; i < pub_key_size; ++i) {
            char c = publicKeyChar[i];
            char new_c = transformChar(c, increment);
            charStr[i + padding] = new_c;
            ++size;
        }

        String strChar = String.valueOf(charStr);
        strChar = strChar.substring(0, pub_key_size + pad_len);
        return strChar;
    }

    private static char transformChar(char inChar, boolean increment) {
        byte inc;
        if (increment) {
            inc = 10;
        } else {
            inc = -10;
        }

        if (inChar >= 'a' && inChar <= 'z') {
            return (char) (97 + (inChar - 97 + inc + 26) % 26);
        } else {
            return inChar >= 'A' && inChar <= 'Z' ? (char) (65 + (inChar - 65 + inc + 26) % 26) : inChar;
        }
    }

    private static String getDecodeString(byte[] bytes) {
        byte[] tmp = new byte[bytes.length];
        for (int i = 0; i < bytes.length; ++i) {
            byte b = bytes[i];
            if (b < Byte.MAX_VALUE) {
                tmp[i] = (byte) (b + 1);
            } else {
                tmp[i] = Byte.MIN_VALUE;
            }
        }
        return new String(tmp, StandardCharsets.UTF_8);
    }
}
