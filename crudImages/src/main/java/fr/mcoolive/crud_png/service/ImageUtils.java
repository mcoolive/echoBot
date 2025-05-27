package fr.mcoolive.crud_png.service;

import java.util.Arrays;

public class ImageUtils {
    // PNG signature: 89 50 4E 47 0D 0A 1A 0A
    private static final byte[] PNG_SIGNATURE = {
            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
            (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
    };

    public static boolean isPng(byte[] data) {
        // Check if the data is long enough and starts with the PNG signature
        if (data == null || data.length < PNG_SIGNATURE.length) {
            return false;
        }

        // Compare the first bytes of the byte array with the PNG signature
        return Arrays.equals(PNG_SIGNATURE, 0, PNG_SIGNATURE.length, data, 0, PNG_SIGNATURE.length);
    }
}
