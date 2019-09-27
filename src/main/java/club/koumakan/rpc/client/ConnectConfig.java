package club.koumakan.rpc.client;

public class ConnectConfig {

    private String ipAddress;

    private int port;

    // 密钥
    private String key;

    // 重连次数, -1为永久
    private int retries;

    // 重连间隔
    private int sleepMs;

    public ConnectConfig(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getSleepMs() {
        return sleepMs;
    }

    public void setSleepMs(int sleepMs) {
        this.sleepMs = sleepMs;
    }
}
