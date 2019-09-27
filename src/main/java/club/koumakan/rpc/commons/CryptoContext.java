package club.koumakan.rpc.commons;

import io.netty.channel.Channel;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class CryptoContext {

    public final static long DELIMITER = 324435435;

    // 解密map
    public final static Map<Channel, Cipher> decryptMap = new ConcurrentHashMap<>();

    // 加密map
    public final static Map<Channel, Cipher> encryptMap = new ConcurrentHashMap<>();

    public static void addCipher(String key, Channel channel) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        decryptMap.put(channel, getCipher(key, DECRYPT_MODE));
        encryptMap.put(channel, getCipher(key, ENCRYPT_MODE));
    }

    public static void removeCipher(Channel channel) {
        decryptMap.remove(channel);
        encryptMap.remove(channel);
    }

    public static void removeAll() {
        decryptMap.clear();
        encryptMap.clear();
    }

    private static Cipher getCipher(String key, int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
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
