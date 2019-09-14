package club.koumakan.rpc.server;

import club.koumakan.rpc.message.entity.RequestMessage;

public interface Listener<c extends RequestMessage> {

    void read(c requestMessage, Channel channel);
}
