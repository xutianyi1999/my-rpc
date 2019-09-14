import club.koumakan.rpc.message.entity.RequestMessage;

import java.io.Serializable;

public class MyRequestMessage extends RequestMessage implements Serializable {

    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
