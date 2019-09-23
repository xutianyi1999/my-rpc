package club.koumakan.rpc.commons;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class Context {

    // 解密map
    public final static Map<InetSocketAddress, Cipher> decodeCipherMap = new ConcurrentHashMap<>();

    // 加密map
    public final static Map<InetSocketAddress, Cipher> encodeCipherMap = new ConcurrentHashMap<>();

    public static void addCipher(String key, InetSocketAddress inetSocketAddress) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        decodeCipherMap.put(inetSocketAddress, getCipher(key, DECRYPT_MODE));
        encodeCipherMap.put(inetSocketAddress, getCipher(key, ENCRYPT_MODE));
    }

    public static void removeCipher(InetSocketAddress inetSocketAddress) {
        decodeCipherMap.remove(inetSocketAddress);
        encodeCipherMap.remove(inetSocketAddress);
    }

    public static Cipher getCipher(String key, int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key.getBytes(StandardCharsets.UTF_8));

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, secretKey);
        return cipher;
    }
}
