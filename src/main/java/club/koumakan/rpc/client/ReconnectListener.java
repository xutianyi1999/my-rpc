package club.koumakan.rpc.client;

import club.koumakan.rpc.channel.Sender;

@FunctionalInterface
public interface ReconnectListener {

    void execute(Sender sender);
}
