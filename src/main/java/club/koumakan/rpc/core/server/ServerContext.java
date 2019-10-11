package club.koumakan.rpc.core.server;

import club.koumakan.rpc.core.server.functional.Listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ServerContext {

    //server 接收监听 key1:port key2:functionCode
    Map<Integer, Map<String, Listener>> listenerMap = new ConcurrentHashMap<>();
}
