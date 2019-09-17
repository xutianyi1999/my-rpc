package club.koumakan.rpc;

import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.client.Inactive;
import club.koumakan.rpc.server.Listener;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcContext {

    //client 回调监听
    public static final Map<String, Callback> callbackMap = new ConcurrentHashMap<>();

    //client 连接中断监听
    public static final Map<Channel, Inactive> inactiveMap = new ConcurrentHashMap<>();

    //server 接收监听
    public static final Map<Integer, Listener> listenerMap = new ConcurrentHashMap<>();
}
