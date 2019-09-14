package club.koumakan.rpc.message.entity;

import java.io.Serializable;
import java.util.UUID;

public abstract class RequestMessage implements Serializable {

    private String callId;

    public RequestMessage() {
        this.callId = System.currentTimeMillis() + ":" + UUID.randomUUID().toString();
    }

    public String getCallId() {
        return callId;
    }
}
