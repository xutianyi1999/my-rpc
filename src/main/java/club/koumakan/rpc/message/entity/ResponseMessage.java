package club.koumakan.rpc.message.entity;

import java.io.Serializable;

public abstract class ResponseMessage implements Serializable {

    private String callId;

    public ResponseMessage(String callId) {
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }
}
