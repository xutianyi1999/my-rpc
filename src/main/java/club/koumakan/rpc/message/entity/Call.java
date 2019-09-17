package club.koumakan.rpc.message.entity;

import java.io.Serializable;
import java.util.UUID;

public class Call implements Serializable {

    public final String CALL_ID = System.currentTimeMillis() + ":" + UUID.randomUUID().toString();

    private Object data;

    private String functionCode;

    public Call(Object data, String functionCode) {
        this.data = data;
        this.functionCode = functionCode;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
