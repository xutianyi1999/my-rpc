package club.koumakan.rpc.core;

import club.koumakan.rpc.core.client.RpcClient;
import club.koumakan.rpc.core.client.handler.RpcClientHandler;
import club.koumakan.rpc.core.commons.CryptoUtils;
import club.koumakan.rpc.core.handler.aes.AesDecoder;
import club.koumakan.rpc.core.handler.aes.AesEncoder;
import club.koumakan.rpc.core.message.Call;
import club.koumakan.rpc.core.server.RpcServer;
import club.koumakan.rpc.core.server.handler.RpcServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.compression.SnappyFrameDecoder;
import io.netty.handler.codec.compression.SnappyFrameEncoder;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import javax.crypto.Cipher;

public class RpcFactory {

    private final static Class CLAZZ = Call.class;
    private final static ObjectEncoder OBJECT_ENCODER = new ObjectEncoder();

    private RpcFactory() {
    }

    public static RpcServer createRpcServer(RpcCore rpcCore) {
        return createRpcServer(rpcCore, new RpcConfig());
    }

    public static RpcServer createRpcServer(RpcCore rpcCore, RpcConfig rpcConfig) {
        return new RpcServer(createServerBootstrap(rpcCore, rpcConfig));
    }

    public static RpcClient createRpcClient(RpcCore rpcCore) {
        return createRpcClient(rpcCore, new RpcConfig());
    }

    public static RpcClient createRpcClient(RpcCore rpcCore, RpcConfig rpcConfig) {
        return new RpcClient(createBootstrap(rpcCore, rpcConfig));
    }

    private static ServerBootstrap createServerBootstrap(RpcCore rpcCore, RpcConfig rpcConfig) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(rpcCore.getBossGroup(), rpcCore.getWorkerGroup())
                .channel(rpcCore.getServerChannelClass())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, rpcConfig.isNoDelay())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        if (rpcConfig.getKey() != null) {
                            pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, ch.alloc().buffer().writeBytes(CryptoUtils.DELIMITER)))
                                    .addLast(
                                            new CombinedChannelDuplexHandler<>(
                                                    new AesDecoder(CryptoUtils.getCipher(rpcConfig.getKey(), Cipher.DECRYPT_MODE)),
                                                    new AesEncoder(CryptoUtils.getCipher(rpcConfig.getKey(), Cipher.ENCRYPT_MODE))
                                            )
                                    );
                        }

                        if (rpcConfig.isCompression()) {
                            pipeline.addLast(
                                    new CombinedChannelDuplexHandler<>(
                                            new SnappyFrameDecoder(),
                                            new SnappyFrameEncoder()
                                    )
                            );
                        }

                        pipeline.addLast(new CombinedChannelDuplexHandler<>(
                                new ObjectDecoder(getClassResolver(rpcConfig.getClassResolverType())),
                                OBJECT_ENCODER
                        ));
                        pipeline.addLast(RpcServerHandler.INSTANCE);
                    }
                });
        return serverBootstrap;
    }

    private static Bootstrap createBootstrap(RpcCore rpcCore, RpcConfig rpcConfig) {
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(rpcCore.getWorkerGroup())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, rpcConfig.isNoDelay())
                .channel(rpcCore.getChannelClass())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        if (rpcConfig.getKey() != null) {
                            pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, ch.alloc().buffer().writeBytes(CryptoUtils.DELIMITER)))
                                    .addLast(
                                            new CombinedChannelDuplexHandler<>(
                                                    new AesDecoder(CryptoUtils.getCipher(rpcConfig.getKey(), Cipher.DECRYPT_MODE)),
                                                    new AesEncoder(CryptoUtils.getCipher(rpcConfig.getKey(), Cipher.ENCRYPT_MODE))
                                            )
                                    );
                        }

                        if (rpcConfig.isCompression()) {
                            pipeline.addLast(
                                    new CombinedChannelDuplexHandler<>(
                                            new SnappyFrameDecoder(),
                                            new SnappyFrameEncoder()
                                    )
                            );
                        }

                        pipeline.addLast(
                                new CombinedChannelDuplexHandler<>(
                                        new ObjectDecoder(getClassResolver(rpcConfig.getClassResolverType())),
                                        OBJECT_ENCODER
                                )
                        );
                        pipeline.addLast(RpcClientHandler.INSTANCE);
                    }
                });
        return bootstrap;
    }

    private static ClassResolver getClassResolver(ClassResolverType classResolverType) {
        if (classResolverType == ClassResolverType.cacheDisabled) {
            return ClassResolvers.cacheDisabled(CLAZZ.getClassLoader());
        } else if (classResolverType == ClassResolverType.softCachingConcurrentResolver) {
            return ClassResolvers.softCachingConcurrentResolver(CLAZZ.getClassLoader());
        } else if (classResolverType == ClassResolverType.softCachingResolver) {
            return ClassResolvers.softCachingResolver(CLAZZ.getClassLoader());
        } else if (classResolverType == ClassResolverType.weakCachingConcurrentResolver) {
            return ClassResolvers.weakCachingConcurrentResolver(CLAZZ.getClassLoader());
        } else if (classResolverType == ClassResolverType.weakCachingResolver) {
            return ClassResolvers.weakCachingResolver(CLAZZ.getClassLoader());
        } else {
            return null;
        }
    }

    public enum ClassResolverType {
        cacheDisabled,
        softCachingConcurrentResolver,
        softCachingResolver,
        weakCachingConcurrentResolver,
        weakCachingResolver
    }
}
