import club.koumakan.rpc.message.entity.ResponseMessage;

import java.io.Serializable;

public class MyResponseMessage extends ResponseMessage implements Serializable {
    public MyResponseMessage(String callId) {
        super(callId);
    }

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
