package club.koumakan.rpc;

import club.koumakan.rpc.client.functional.Callback;
import club.koumakan.rpc.commons.ClientContext;
import club.koumakan.rpc.commons.CryptoUtils;
import club.koumakan.rpc.commons.ServerContext;
import club.koumakan.rpc.exception.RpcFactoryInitException;
import club.koumakan.rpc.handler.AesDecoder;
import club.koumakan.rpc.handler.AesEncoder;
import club.koumakan.rpc.handler.RpcClientHandler;
import club.koumakan.rpc.handler.RpcServerHandler;
import club.koumakan.rpc.message.entity.Call;
import club.koumakan.rpc.template.RpcClientTemplate;
import club.koumakan.rpc.template.RpcServerTemplate;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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

import static club.koumakan.rpc.ClassResolverType.*;
import static club.koumakan.rpc.commons.ClientContext.callbackMap;
import static club.koumakan.rpc.commons.CryptoContext.DELIMITER;

public class RpcFactory {

    private final static boolean IS_LINUX = System.getProperty("os.name").contains("Linux");
    private final static Class CLAZZ = Call.class;

    private static boolean SERVER_INIT = false;
    private static boolean CLIENT_INIT = false;

    private static boolean isClientTaskStart = false;
    private static ScheduledFuture<?> scheduledFuture;

    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;
    private static Class<? extends ServerSocketChannel> serverChannelClass;
    private static Class<? extends Channel> channelClass;

    private static final ObjectEncoder OBJECT_ENCODER = new ObjectEncoder();
    private static DelimiterBasedFrameDecoder delimiterBasedFrameDecoder;
    private static CombinedChannelDuplexHandler serverAesCodec;
    private static CombinedChannelDuplexHandler clientAesCodec;
    private static CombinedChannelDuplexHandler snappyFrameCodec;

    private RpcFactory() {
    }

    private static DelimiterBasedFrameDecoder getDelimiterBasedFrameDecoder() {
        if (delimiterBasedFrameDecoder == null) {
            delimiterBasedFrameDecoder = new DelimiterBasedFrameDecoder(
                    Integer.MAX_VALUE, ByteBufAllocator.DEFAULT.buffer().writeLong(DELIMITER)
            );
        }
        return delimiterBasedFrameDecoder;
    }

    private static CombinedChannelDuplexHandler getServerAesCodec() {
        if (serverAesCodec == null) {
            serverAesCodec = new CombinedChannelDuplexHandler<>(
                    new AesDecoder(true),
                    new AesEncoder(true)
            );
        }
        return serverAesCodec;
    }

    private static CombinedChannelDuplexHandler getClientAesCodec() {
        if (clientAesCodec == null) {
            clientAesCodec = new CombinedChannelDuplexHandler<>(
                    new AesDecoder(false),
                    new AesEncoder(false)
            );
        }
        return clientAesCodec;
    }

    public static void initServer() throws RpcFactoryInitException {
        if (SERVER_INIT || CLIENT_INIT) {
            throw new RpcFactoryInitException("Already initialized");
        }

        if (IS_LINUX) {
            bossGroup = new EpollEventLoopGroup();
            workerGroup = new EpollEventLoopGroup();
            serverChannelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            serverChannelClass = NioServerSocketChannel.class;
        }
        SERVER_INIT = true;
    }

    public static void initServer(EventLoopGroup bossGroup,
                                  EventLoopGroup workerGroup,
                                  Class<? extends ServerSocketChannel> serverChannelClass) throws RpcFactoryInitException {

        if (SERVER_INIT || CLIENT_INIT) {
            throw new RpcFactoryInitException("Already initialized");
        }

        RpcFactory.bossGroup = bossGroup;
        RpcFactory.workerGroup = workerGroup;
        RpcFactory.serverChannelClass = serverChannelClass;
        SERVER_INIT = true;
    }

    public static void initClient() throws RpcFactoryInitException {
        if (SERVER_INIT || CLIENT_INIT) {
            throw new RpcFactoryInitException("Already initialized");
        }

        if (IS_LINUX) {
            workerGroup = new EpollEventLoopGroup();
            channelClass = EpollSocketChannel.class;
        } else {
            workerGroup = new NioEventLoopGroup();
            channelClass = NioSocketChannel.class;
        }

        CLIENT_INIT = true;
    }

    public static void initClient(EventLoopGroup workerGroup, Class<? extends SocketChannel> channelClass) throws RpcFactoryInitException {
        if (SERVER_INIT || CLIENT_INIT) {
            throw new RpcFactoryInitException("Already initialized");
        }

        RpcFactory.workerGroup = workerGroup;
        RpcFactory.channelClass = channelClass;

        CLIENT_INIT = true;
    }

    public static RpcClientTemplate createClientTemplate(ClassResolverType classResolverType, boolean encrypt, boolean compression, boolean noDelay) throws RpcFactoryInitException {
        return new RpcClientTemplate(createBootstrap(getClassResolver(classResolverType), encrypt, compression, noDelay));
    }

    public static RpcClientTemplate createClientTemplate() throws RpcFactoryInitException {
        return createClientTemplate(weakCachingResolver, false, false, true);
    }

    public static RpcServerTemplate createServerTemplate(ClassResolverType classResolverType, boolean encrypt, boolean compression, boolean noDelay) throws RpcFactoryInitException {
        return new RpcServerTemplate(createServerBootstrap(getClassResolver(classResolverType), encrypt, compression, noDelay));
    }

    public static RpcServerTemplate createServerTemplate() throws RpcFactoryInitException {
        return createServerTemplate(weakCachingResolver, false, false, true);
    }

    private static CombinedChannelDuplexHandler getSnappyFrameCodec() {
        if (snappyFrameCodec == null) {
            snappyFrameCodec = new CombinedChannelDuplexHandler<>(
                    new SnappyFrameDecoder(),
                    new SnappyFrameEncoder()
            );
        }
        return snappyFrameCodec;
    }

    private static void clientContextReset() {
        ClientContext.callbackMap.clear();
        ClientContext.inactiveMap.clear();
    }

    private static void serverContextReset() {
        ServerContext.listenerMap.clear();
    }

    public static void destroy() {
        if (CLIENT_INIT) {
            clientContextReset();
            CryptoUtils.removeAll();
        } else {
            serverContextReset();
            clientContextReset();
            CryptoUtils.removeAll();
        }

        SERVER_INIT = false;
        CLIENT_INIT = false;
        isClientTaskStart = false;

        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
            bossGroup = null;
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }

        serverChannelClass = null;
        channelClass = null;
        delimiterBasedFrameDecoder = null;
        serverAesCodec = null;
        clientAesCodec = null;
        snappyFrameCodec = null;
    }

    private static ServerBootstrap createServerBootstrap(final ClassResolver classResolver, boolean encrypt, boolean compression, boolean noDelay) throws RpcFactoryInitException {
        if (!SERVER_INIT) {
            throw new RpcFactoryInitException("Not initialized");
        }

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(serverChannelClass)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, noDelay)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        if (encrypt) {
                            pipeline.addLast(getDelimiterBasedFrameDecoder())
                                    .addLast(getServerAesCodec());
                        }

                        if (compression) {
                            pipeline.addLast(getSnappyFrameCodec());
                        }

                        pipeline.addLast(new CombinedChannelDuplexHandler<>(
                                new ObjectDecoder(classResolver),
                                OBJECT_ENCODER
                        ));
                        pipeline.addLast(RpcServerHandler.INSTANCE);
                    }
                });
        return serverBootstrap;
    }

    private static ClassResolver getClassResolver(ClassResolverType classResolverType) {
        if (classResolverType == cacheDisabled) {
            return ClassResolvers.cacheDisabled(CLAZZ.getClassLoader());
        } else if (classResolverType == softCachingConcurrentResolver) {
            return ClassResolvers.softCachingConcurrentResolver(CLAZZ.getClassLoader());
        } else if (classResolverType == softCachingResolver) {
            return ClassResolvers.softCachingResolver(CLAZZ.getClassLoader());
        } else if (classResolverType == weakCachingConcurrentResolver) {
            return ClassResolvers.weakCachingConcurrentResolver(CLAZZ.getClassLoader());
        } else if (classResolverType == weakCachingResolver) {
            return ClassResolvers.weakCachingResolver(CLAZZ.getClassLoader());
        } else {
            return null;
        }
    }

    private static void clientTask() {
        if (isClientTaskStart) {
            return;
        } else {
            isClientTaskStart = true;
        }

        scheduledFuture = workerGroup.scheduleAtFixedRate(() -> {
            if (callbackMap.size() > 0) {
                Set<Map.Entry<String, Callback>> entries = callbackMap.entrySet();
                long currentTime = System.currentTimeMillis();

                for (Map.Entry<String, Callback> entry : entries) {
                    long sendTime = Long.parseLong(entry.getKey().split(":")[0]);

                    if (currentTime - sendTime >= 60000) {
                        callbackMap.remove(entry.getKey());
                    }
                }
            }
        }, 0, 10000, TimeUnit.MILLISECONDS);
    }

    private static Bootstrap createBootstrap(final ClassResolver classResolver, boolean encrypt, boolean compression, boolean noDelay) throws RpcFactoryInitException {
        if (!CLIENT_INIT && !SERVER_INIT) {
            throw new RpcFactoryInitException("Not initialized");
        }

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(workerGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, noDelay)
                .channel(channelClass)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        if (encrypt) {
                            pipeline.addLast(getDelimiterBasedFrameDecoder())
                                    .addLast(getClientAesCodec());
                        }

                        if (compression) {
                            pipeline.addLast(getSnappyFrameCodec());
                        }

                        pipeline.addLast(
                                new CombinedChannelDuplexHandler<>(
                                        new ObjectDecoder(classResolver),
                                        OBJECT_ENCODER
                                )
                        );
                        pipeline.addLast(RpcClientHandler.INSTANCE);
                    }
                });

        clientTask();
        return bootstrap;
    }
}
