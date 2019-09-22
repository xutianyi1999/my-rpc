package club.koumakan.rpc.commons;

import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.client.Inactive;
import club.koumakan.rpc.client.ReconnectListenerEntity;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ClientContext {

    //client 回调监听
    Map<String, Callback> callbackMap = new ConcurrentHashMap<>();

    //client 连接中断监听
    Map<Channel, Inactive> inactiveMap = new ConcurrentHashMap<>();

    //client 重连成功监听
    Map<InetSocketAddress, ReconnectListenerEntity> reconnectListenerMap = new ConcurrentHashMap<>();
}
