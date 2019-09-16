package club.koumakan.rpc;

import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.server.Listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcContext {

    public static final Map<String, Callback> callbackMap = new ConcurrentHashMap<>();
    public static final Map<Integer, Listener> listenerMap = new ConcurrentHashMap<>();
}
