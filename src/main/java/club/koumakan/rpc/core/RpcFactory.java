package club.koumakan.rpc.core;

import club.koumakan.rpc.core.client.ClientContext;
import club.koumakan.rpc.core.client.RpcClient;
import club.koumakan.rpc.core.client.functional.Callback;
import club.koumakan.rpc.core.client.handler.ClientAesDecoder;
import club.koumakan.rpc.core.client.handler.ClientAesEncoder;
import club.koumakan.rpc.core.client.handler.RpcClientHandler;
import club.koumakan.rpc.core.commons.CryptoUtils;
import club.koumakan.rpc.core.exception.CallbackTimeoutException;
import club.koumakan.rpc.core.message.Call;
import club.koumakan.rpc.core.server.RpcServer;
import club.koumakan.rpc.core.server.ServerContext;
import club.koumakan.rpc.core.server.handler.RpcServerHandler;
import club.koumakan.rpc.core.server.handler.ServerAesDecoder;
import club.koumakan.rpc.core.server.handler.ServerAesEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.compression.SnappyFrameDecoder;
import io.netty.handler.codec.compression.SnappyFrameEncoder;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static club.koumakan.rpc.core.client.ClientContext.callbackMap;
import static club.koumakan.rpc.core.commons.CryptoContext.DELIMITER;

public class RpcFactory {

    private final static Class CLAZZ = Call.class;
    private final static ObjectEncoder OBJECT_ENCODER = new ObjectEncoder();

    private static boolean isClientTaskStart = false;
    private static ScheduledFuture<?> scheduledFuture;

    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;
    private static Class<? extends ServerSocketChannel> serverChannelClass;
    private static Class<? extends SocketChannel> channelClass;
    private static int callbackTimeout = 10000;

    private static RpcFactory rpcFactory;

    private RpcFactory(RpcFactoryCore rpcFactoryCore) {
        bossGroup = rpcFactoryCore.getBossGroup();
        workerGroup = rpcFactoryCore.getWorkerGroup();
        serverChannelClass = rpcFactoryCore.getServerChannelClass();
        channelClass = rpcFactoryCore.getChannelClass();
    }

    public static RpcFactory build() {
        return build(RpcFactoryCore.serverAndClient());
    }

    public static RpcFactory build(RpcFactoryCore rpcFactoryCore) {
        if (rpcFactory == null) {
            rpcFactory = new RpcFactory(rpcFactoryCore);
        } else {
            System.out.println("Already built");
        }
        return rpcFactory;
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

    private static ServerBootstrap createServerBootstrap(RpcConfig rpcConfig) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(serverChannelClass)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, rpcConfig.isNoDelay())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        if (rpcConfig.isEncrypt()) {
                            pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, ch.alloc().buffer().writeLong(DELIMITER)))
                                    .addLast(
                                            new CombinedChannelDuplexHandler<>(
                                                    ServerAesDecoder.INSTANCE,
                                                    ServerAesEncoder.INSTANCE
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

    private static Bootstrap createBootstrap(RpcConfig rpcConfig) {
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(workerGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, rpcConfig.isNoDelay())
                .channel(channelClass)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        if (rpcConfig.isEncrypt()) {
                            pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, ch.alloc().buffer().writeLong(DELIMITER)))
                                    .addLast(
                                            new CombinedChannelDuplexHandler<>(
                                                    ClientAesDecoder.INSTANCE,
                                                    ClientAesEncoder.INSTANCE
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

    private static void autoRemoveCallback() {
        if (isClientTaskStart) {
            return;
        } else {
            isClientTaskStart = true;
        }

        final CallbackTimeoutException callbackTimeoutException = new CallbackTimeoutException();

        scheduledFuture = workerGroup.scheduleAtFixedRate(() -> {
            if (callbackMap.size() > 0) {
                Set<Map.Entry<String, Callback>> entries = callbackMap.entrySet();
                long currentTime = System.currentTimeMillis();

                for (Map.Entry<String, Callback> entry : entries) {
                    long sendTime = Long.parseLong(entry.getKey().split(":")[0]);

                    if (currentTime - sendTime >= callbackTimeout) {
                        entry.getValue().response(callbackTimeoutException, null);
                        callbackMap.remove(entry.getKey());
                    }
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public RpcServer createRpcServer() {
        return createRpcServer(new RpcConfig());
    }

    public RpcServer createRpcServer(RpcConfig rpcConfig) {
        return new RpcServer(createServerBootstrap(rpcConfig));
    }

    public RpcClient createRpcClient() {
        return createRpcClient(new RpcConfig());
    }

    public RpcClient createRpcClient(RpcConfig rpcConfig) {
        autoRemoveCallback();
        return new RpcClient(createBootstrap(rpcConfig));
    }

    public void destroy() {
        CryptoUtils.removeAll();
        isClientTaskStart = false;

        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }

        if (bossGroup != null) {
            ServerContext.listenerMap.clear();

            bossGroup.shutdownGracefully();
            bossGroup = null;
        }

        if (workerGroup != null) {
            ClientContext.callbackMap.clear();
            ClientContext.inactiveMap.clear();

            workerGroup.shutdownGracefully();
            workerGroup = null;
        }

        serverChannelClass = null;
        channelClass = null;
        callbackTimeout = 10000;
        rpcFactory = null;
    }

    public RpcFactory setCallbackTimeout(int callbackTimeout) {
        RpcFactory.callbackTimeout = callbackTimeout;
        return this;
    }

    public enum ClassResolverType {
        cacheDisabled,
        softCachingConcurrentResolver,
        softCachingResolver,
        weakCachingConcurrentResolver,
        weakCachingResolver
    }
}
