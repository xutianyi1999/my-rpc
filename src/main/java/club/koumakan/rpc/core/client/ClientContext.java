package club.koumakan.rpc.core.client;

import club.koumakan.rpc.core.client.functional.Callback;
import club.koumakan.rpc.core.client.functional.Inactive;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ClientContext {

    //client 回调监听 key:CallId
    Map<String, Callback> callbackMap = new ConcurrentHashMap<>();

    //client 连接中断监听
    Map<Channel, Inactive> inactiveMap = new ConcurrentHashMap<>();
}