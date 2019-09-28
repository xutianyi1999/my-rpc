package club.koumakan.rpc.commons;

import io.netty.channel.Channel;

import javax.crypto.Cipher;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface CryptoContext {

    long DELIMITER = 324435435;

    // 解密map
    Map<Channel, Cipher> decryptMap = new ConcurrentHashMap<>();

    // 加密map
    Map<Channel, Cipher> encryptMap = new ConcurrentHashMap<>();
}
