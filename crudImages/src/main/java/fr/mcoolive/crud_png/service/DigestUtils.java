package fr.mcoolive.crud_png.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils {
    private static final MessageDigest MD5_DIGEST = getNewMd5MessageDigest();

    public static String md5sum(byte[] data) {
        final byte[] bytes = md5digest(data);
        return bytesToString(bytes);
    }

    public static byte[] md5digest(byte[] data) {
        MessageDigest digest = null;
        try {
            digest = (MessageDigest) MD5_DIGEST.clone();
        } catch (CloneNotSupportedException e) {
            // fallback: create a new instance each time
            digest = getNewMd5MessageDigest();
        }
        return digest.digest(data);
    }

    private static MessageDigest getNewMd5MessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // This should never happen in standard Java environments
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    /** Convert byte array to hex string. */
    private static String bytesToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            // Convert each byte to 2-digit hex
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

}
