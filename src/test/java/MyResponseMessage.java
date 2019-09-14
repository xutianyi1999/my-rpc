import club.koumakan.rpc.message.entity.ResponseMessage;

import java.io.Serializable;

public class MyResponseMessage extends ResponseMessage implements Serializable {
    public MyResponseMessage(String callId) {
        super(callId);
    }
}
