package org.king2.check_service_manage.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 描述: AES 对称加密 用来替代原先的DES加密算法。
 *
 * 优缺点：对称加密算法是应用较早的加密算法，技术成熟。加密和解密用到的密钥是相同的，这种加密方式加密速度非常快，
 *        适合经常发送数据的场合。缺点是密钥的传输比较麻烦。常见的对称加密算法有：AES算法，DES算法，3DES算法等等。
 *
 * 提示：实际中，一般是通过RSA加密AES的密钥，传输到接收方，接收方解密得到AES密钥，然后发送方和接收方用AES密钥来通信。
 *
 * @author 刘梓江
 * @Date 2020/12/24 11:48
 */
public class AESUtil {

    /**
     * 字符编码
     */
    private static final String CHARACTER_ENCODING = "utf-8";

    /**
     * 生成密钥的基本字符
     */
    private static final String BASE_CHARACTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private AESUtil() {

    }

    /**
     * 生成随机密钥
     *
     * @return 随机密钥
     */
    public static String initKey() {
        return generateKeyOrIV();
    }

    /**
     * 生成初始向量
     *
     * @return 初始向量
     */
    public static String initIV() {
        return generateKeyOrIV();
    }

    /**
     * 生成随机密钥、初始向量
     */
    private static String generateKeyOrIV() {
        StringBuilder sBuilder = new StringBuilder();
        double r;
        for (int i = 0; i < 16; i++) {
            r = Math.random() * BASE_CHARACTER.length();
            sBuilder.append(BASE_CHARACTER.charAt((int) r));
        }
        return sBuilder.toString();
    }

    /**
     * 使用AES算法加密字符串
     *
     * @param data 需要加密的原文
     * @param key  密钥(16位字母、数字或符号)
     * @param iv   初始向量(16位字母、数字或符号)，使用CBC模式，需要一个向量iv，可增加加密算法的强度
     * @return 加密后进行Base64的密文
     * @throws Exception 加密失败
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        return java.util.Base64.getEncoder().encodeToString(encrypt(data.getBytes(CHARACTER_ENCODING), key, iv));
    }

    /**
     * 使用AES算法加密数据
     *
     * @param data 需要加密的数据
     * @param key  密钥(16位字母、数字或符号)
     * @param iv   初始向量(16位字母、数字或符号)，使用CBC模式，需要一个向量iv，可增加加密算法的强度
     * @return 加密后的数据
     * @throws Exception 加密失败
     */
    public static byte[] encrypt(byte[] data, String key, String iv) throws Exception {
        return crypto(Cipher.ENCRYPT_MODE, data, key, iv);
    }

    /**
     * 使用AES算法解密字符串
     *
     * @param data 需要解密的密文
     * @param key  密钥(16位字母、数字或符号)
     * @param iv   初始向量(16位字母、数字或符号)
     * @return 解密后的明文
     * @throws Exception 解密失败
     */
    public static String decrypt(String data, String key, String iv) throws Exception {
        byte[] decrypted = decrypt(Base64.getDecoder().decode(data), key, iv);
        return new String(decrypted, CHARACTER_ENCODING);
    }

    /**
     * 使用AES算法解密数据
     *
     * @param data 需要解密的数据
     * @param key  密钥(16位字母、数字或符号)
     * @param iv   初始向量(16位字母、数字或符号)
     * @return 解密后的数据
     * @throws Exception 解密失败
     */
    public static byte[] decrypt(byte[] data, String key, String iv) throws Exception {
        return crypto(Cipher.DECRYPT_MODE, data, key, iv);
    }

    /**
     * 加解密数据
     */
    private static byte[] crypto(int opmode, byte[] content, String key, String iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARACTER_ENCODING), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  // 算法/模式/补码方式
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(CHARACTER_ENCODING));
        cipher.init(opmode, keySpec, ivParameterSpec);
        return cipher.doFinal(content);
    }
}
