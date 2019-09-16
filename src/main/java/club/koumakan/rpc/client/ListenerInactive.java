package club.koumakan.rpc.client;

import club.koumakan.rpc.channel.Sender;

import java.net.InetSocketAddress;

public abstract class ListenerInactive {

    private boolean isAutoReconnect;

    public ListenerInactive(boolean isAutoReconnect) {
        this.isAutoReconnect = isAutoReconnect;
    }

    public abstract void inactive(InetSocketAddress ipAddress);

    public void successReconnect(Sender sender) {
    }

    public boolean isAutoReconnect() {
        return isAutoReconnect;
    }
}
