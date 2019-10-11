package club.koumakan.rpc.core;

import static club.koumakan.rpc.core.RpcFactory.ClassResolverType.weakCachingResolver;

public class RpcConfig {

    private RpcFactory.ClassResolverType classResolverType = weakCachingResolver;
    private boolean encrypt = false;
    private boolean compression = false;
    private boolean noDelay = true;

    public RpcFactory.ClassResolverType getClassResolverType() {
        return classResolverType;
    }

    public RpcConfig setClassResolverType(RpcFactory.ClassResolverType classResolverType) {
        this.classResolverType = classResolverType;
        return this;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public RpcConfig setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
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
}
