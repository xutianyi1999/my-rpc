package club.koumakan.rpc.core.client;

public class ConnectConfig {

    private String ipAddress;

    private int port;
    // 重连次数, -1为永久
    private int retries = 0;
    // 重连间隔
    private int sleepMs = 0;

    public String getIpAddress() {
        return ipAddress;
    }

    public ConnectConfig setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ConnectConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public int getRetries() {
        return retries;
    }

    public ConnectConfig setRetries(int retries) {
        this.retries = retries;
        return this;
    }

    public int getSleepMs() {
        return sleepMs;
    }

    public ConnectConfig setSleepMs(int sleepMs) {
        this.sleepMs = sleepMs;
        return this;
    }
}
