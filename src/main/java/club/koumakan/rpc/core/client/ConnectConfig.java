package club.koumakan.rpc.core.client;

public class ConnectConfig {

    private String ipAddress;

    private int port;
    // 密钥
    private String key;
    // 重连次数, -1为永久
    private int retries = 0;
    // 重连间隔
    private int sleepMs = 0;

    public ConnectConfig(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public ConnectConfig(String ipAddress, int port, String key) {
        this(ipAddress, port);
        this.key = key;
    }

    public ConnectConfig(String ipAddress, int port, int retries, int sleepMs) {
        this(ipAddress, port);
        this.retries = retries;
        this.sleepMs = sleepMs;
    }

    public ConnectConfig(String ipAddress, int port, String key, int retries, int sleepMs) {
        this(ipAddress, port, key);
        this.retries = retries;
        this.sleepMs = sleepMs;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public String getKey() {
        return key;
    }

    public int getRetries() {
        return retries;
    }

    public int getSleepMs() {
        return sleepMs;
    }
}
