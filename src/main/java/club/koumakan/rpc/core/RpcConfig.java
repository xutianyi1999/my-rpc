package club.koumakan.rpc.core;

import static club.koumakan.rpc.core.RpcFactory.ClassResolverType.weakCachingResolver;

public class RpcConfig {

    private RpcFactory.ClassResolverType classResolverType = weakCachingResolver;
    private boolean compression = false;
    private boolean noDelay = true;
    private String key;

    public RpcFactory.ClassResolverType getClassResolverType() {
        return classResolverType;
    }

    public RpcConfig setClassResolverType(RpcFactory.ClassResolverType classResolverType) {
        this.classResolverType = classResolverType;
        return this;
    }

    public boolean isCompression() {
        return compression;
    }

    public RpcConfig setCompression(boolean compression) {
        this.compression = compression;
        return this;
    }

    public boolean isNoDelay() {
        return noDelay;
    }

    public RpcConfig setNoDelay(boolean noDelay) {
        this.noDelay = noDelay;
        return this;
    }

    public String getKey() {
        return key;
    }

    public RpcConfig setKey(String key) {
        this.key = key;
        return this;
    }
}
