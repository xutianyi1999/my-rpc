package club.koumakan.rpc.message.entity;

import java.util.UUID;

public abstract class RequestMessage {

    private String callId;

    public RequestMessage() {
        this.callId = System.currentTimeMillis() + ":" + UUID.randomUUID().toString();
    }

    public String getCallId() {
        return callId;
    }
}
