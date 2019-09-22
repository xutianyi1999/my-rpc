package club.koumakan.rpc.commons;

import club.koumakan.rpc.server.Listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ServerContext {

    //server 接收监听
    Map<Integer, Map<String, Listener>> listenerMap = new ConcurrentHashMap<>();
}
