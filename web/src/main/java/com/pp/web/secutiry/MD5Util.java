package com.pp.web.secutiry;

/**
 * Created by zhaopeng3 on 2017/2/4.
 */

import com.google.common.base.Charsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Static functions to simplifiy common {@link java.security.MessageDigest}
 * tasks. This class is thread safe.
 *
 * @author 99bill
 *
 */
public class MD5Util {
    // private static final String HEX_CHARS = "0123456789abcdef";

    private MD5Util() {
    }

    /**
     * Returns a MessageDigest for the given <code>algorithm</code>.
     *
     *            The MessageDigest algorithm name.
     * @return An MD5 digest instance.
     * @throws RuntimeException
     *             when a {@link java.security.NoSuchAlgorithmException} is
     *             caught
     */
    static MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculates the MD5 digest and returns the value as a 16 element
     * <code>byte[]</code>.
     *
     * @param data
     *            Data to digest
     * @return MD5 digest
     */
    public static byte[] md5(byte[] data) {
        return MD5Util.getDigest().digest(data);
    }

    /**
     * Calculates the MD5 digest and returns the value as a 16 element
     * <code>byte[]</code>.
     *
     * @param data
     *            Data to digest
     * @return MD5 digest
     */
    public static byte[] md5(String data) {
        return MD5Util.md5(data.getBytes(Charsets.UTF_8));
    }

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex
     * string.
     *
     * @param data
     *            Data to digest
     * @return MD5 digest as a hex string
     */
    public static String md5Hex(byte[] data) {
        return MD5Util.toHexString(MD5Util.md5(data));
    }

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex
     * string.
     *
     * @param data
     *            Data to digest
     * @return MD5 digest as a hex string
     */
    public static String md5Hex(String data) {
        return MD5Util.toHexString(MD5Util.md5(data));
    }

    public static String toHexString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (byte element : b) {
            sb.append("0123456789abcdef".charAt(element >>> 4 & 0xF));
            sb.append("0123456789abcdef".charAt(element & 0xF));
        }
        return sb.toString();
    }

    public static byte[] toByteArray(String s) {
        byte[] buf = new byte[s.length() / 2];
        int j = 0;
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) (Character.digit(s.charAt(j++), 16) << 4 | Character
                    .digit(s.charAt(j++), 16));
        }
        return buf;
    }

    public static String appendParam(String returnStr, String paramId,
                                     String paramValue) {
        if (!returnStr.equals("")) {
            if (!paramValue.equals("")) {
                returnStr = returnStr + "&" + paramId + "=" + paramValue;
            }
        } else if (!paramValue.equals("")) {
            returnStr = paramId + "=" + paramValue;
        }

        return returnStr;
    }
}

