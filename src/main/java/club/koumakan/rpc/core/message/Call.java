package club.koumakan.rpc.core.message;

import java.io.Serializable;
import java.util.UUID;

public class Call implements Serializable {

    public final String CALL_ID = UUID.randomUUID().toString();

    private Object data;

    private String functionCode;

    public Call(Object data, String functionCode) {
        this.data = data;
        this.functionCode = functionCode;
    }

    public Call setData(Object data) {
        this.data = data;
        return this;
    }

    public Object getData() {
        return data;
    }

    public String getFunctionCode() {
        return functionCode;
    }
}
