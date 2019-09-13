package club.koumakan.rpc.message.entity;

public abstract class ResponseMessage {

    private String callId;

    public ResponseMessage(String callId) {
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }
}
