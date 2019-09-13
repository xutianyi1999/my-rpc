package club.koumakan.rpc.server;

import club.koumakan.rpc.message.entity.RequestMessage;

public interface Listener {

    void read(RequestMessage requestMessage, Channel channel);
}
