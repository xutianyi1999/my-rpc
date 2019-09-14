package club.koumakan.rpc.message.entity;

import java.io.Serializable;
import java.util.UUID;

public class Call implements Serializable {

    public final String CALL_ID = System.currentTimeMillis() + ":" + UUID.randomUUID().toString();

    private Object data;

    public Call(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
