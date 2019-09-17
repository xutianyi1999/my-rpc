import java.io.Serializable;

public class MyResponseMessage implements Serializable {

    private long content;

    public long getContent() {
        return content;
    }

    public void setContent(long content) {
        this.content = content;
    }
}
