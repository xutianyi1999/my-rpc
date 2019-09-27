package club.koumakan.rpc;

import club.koumakan.rpc.client.ReconnectListenerEntity;
import club.koumakan.rpc.client.functional.Callback;
import club.koumakan.rpc.client.functional.ReconnectListener;
import club.koumakan.rpc.commons.ClientContext;
import club.koumakan.rpc.commons.CryptoContext;
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
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static club.koumakan.rpc.ClassResolverType.*;
import static club.koumakan.rpc.commons.ClientContext.callbackMap;
import static club.koumakan.rpc.commons.ClientContext.reconnectListenerMap;
import static club.koumakan.rpc.commons.CryptoContext.DELIMITER;

public class RpcFactory {

    private final static boolean IS_LINUX = System.getProperty("os.name").contains("Linux");
    private final static Class CLAZZ = Call.class;

    private static boolean SERVER_INIT = false;
    private static boolean CLIENT_INIT = false;

    private static boolean isClientTaskStart = false;
    private static Timer timer;

    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;
    private static Class<? extends ServerSocketChannel> serverChannelClass;
    private static Class<? extends Channel> channelClass;

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

    public static void initClient(EventLoopGroup workerGroup, Class<? extends Channel> channelClass) throws RpcFactoryInitException {
        if (SERVER_INIT || CLIENT_INIT) {
            throw new RpcFactoryInitException("Already initialized");
        }

        RpcFactory.workerGroup = workerGroup;
        RpcFactory.channelClass = channelClass;

        CLIENT_INIT = true;
    }

    public static RpcClientTemplate createClientTemplate(ClassResolverType classResolverType, boolean isEncrypt) throws RpcFactoryInitException {
        return new RpcClientTemplate(createBootstrap(getClassResolver(classResolverType), isEncrypt));
    }

    public static RpcClientTemplate createClientTemplate() throws RpcFactoryInitException {
        return createClientTemplate(weakCachingResolver, false);
    }

    public static RpcServerTemplate createServerTemplate(ClassResolverType classResolverType, boolean isEncrypt) throws RpcFactoryInitException {
        return new RpcServerTemplate(createServerBootstrap(getClassResolver(classResolverType), isEncrypt));
    }

    public static RpcServerTemplate createServerTemplate() throws RpcFactoryInitException {
        return createServerTemplate(weakCachingResolver, false);
    }

    public static void destroy() {
        if (CLIENT_INIT) {
            clientContextReset();
            CryptoContext.removeAll();
        } else {
            serverContextReset();
            clientContextReset();
            CryptoContext.removeAll();
        }

        SERVER_INIT = false;
        CLIENT_INIT = false;
        isClientTaskStart = false;

        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
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
    }

    private static void clientContextReset() {
        ClientContext.callbackMap.clear();
        ClientContext.inactiveMap.clear();
        ClientContext.reconnectListenerMap.clear();
    }

    private static void serverContextReset() {
        ServerContext.listenerMap.clear();
    }

    private static ServerBootstrap createServerBootstrap(final ClassResolver classResolver, boolean isEncrypt) throws RpcFactoryInitException {
        if (!SERVER_INIT) {
            throw new RpcFactoryInitException("Not initialized");
        }

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(serverChannelClass)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        if (isEncrypt) {
                            pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, ch.alloc().buffer().writeLong(DELIMITER)))
                                    .addLast(
                                            new CombinedChannelDuplexHandler<>(
                                                    new AesDecoder(true),
                                                    new AesEncoder(true)
                                            )
                                    );
                        }

                        pipeline.addLast(new CombinedChannelDuplexHandler<>(
                                new ObjectDecoder(classResolver),
                                new ObjectEncoder()
                        ));
                        pipeline.addLast(new RpcServerHandler());
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

    private static Bootstrap createBootstrap(final ClassResolver classResolver, boolean isEncrypt) throws RpcFactoryInitException {
        if (!CLIENT_INIT && !SERVER_INIT) {
            throw new RpcFactoryInitException("Not initialized");
        }

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(workerGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(channelClass)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        if (isEncrypt) {
                            pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, ch.alloc().buffer().writeLong(DELIMITER)))
                                    .addLast(
                                            new CombinedChannelDuplexHandler<>(
                                                    new AesDecoder(false),
                                                    new AesEncoder(false)
                                            )
                                    );
                        }

                        pipeline.addLast(
                                new CombinedChannelDuplexHandler<>(
                                        new ObjectDecoder(classResolver),
                                        new ObjectEncoder()
                                )
                        );
                        pipeline.addLast(new RpcClientHandler());
                    }
                });

        clientTask();
        return bootstrap;
    }

    private static void clientTask() {
        if (isClientTaskStart) {
            return;
        } else {
            isClientTaskStart = true;
        }

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (callbackMap.size() > 0) {
                    Set<Map.Entry<String, Callback>> entries = callbackMap.entrySet();
                    long time = System.currentTimeMillis();

                    for (Map.Entry<String, Callback> entry : entries) {
                        long sendTime = Long.parseLong(entry.getKey().split(":")[0]);

                        if (time - sendTime >= 60000) {
                            callbackMap.remove(entry.getKey());
                        }
                    }
                }
            }
        }, 0, 10000);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (reconnectListenerMap.size() > 0) {
                    Set<Map.Entry<InetSocketAddress, ReconnectListenerEntity>> entries = reconnectListenerMap.entrySet();

                    for (Map.Entry<InetSocketAddress, ReconnectListenerEntity> entry : entries) {
                        InetSocketAddress inetSocketAddress = entry.getKey();

                        ReconnectListener reconnectListener = entry.getValue().getReconnectListener();
                        RpcClientTemplate rpcClientTemplate = entry.getValue().getRpcClientTemplate();

                        rpcClientTemplate.connect(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort(), (throwable, sender) -> {
                            if (throwable == null) {
                                reconnectListenerMap.remove(inetSocketAddress);
                                reconnectListener.execute(sender);
                            }
                        });
                    }
                }
            }
        }, 0, 30000);
    }
}
