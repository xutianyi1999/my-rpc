package club.koumakan.rpc.core.client;

import club.koumakan.rpc.core.client.functional.Inactive;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ClientContext {

    //client 回调监听, key: CallId
    Map<String, CallbackInfo> callbackMap = new ConcurrentHashMap<>();

    //client 连接中断监听, key: channelId
    Map<String, Inactive> inactiveMap = new ConcurrentHashMap<>();
}
