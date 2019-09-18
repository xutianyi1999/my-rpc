package club.koumakan.rpc;

import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.exception.RpcFactoryInitException;
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
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Map;
import java.util.Set;

import static club.koumakan.rpc.ClassResolverType.*;
import static club.koumakan.rpc.RpcContext.callbackMap;

public class RpcFactory {

    private final static boolean IS_LINUX = System.getProperty("os.name").contains("Linux");
    private final static Class CLAZZ = Call.class;

    private static boolean SERVER_INIT = false;
    private static boolean CLIENT_INIT = false;

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

    public static RpcClientTemplate createClientTemplate(ClassResolverType classResolverType) throws RpcFactoryInitException {
        return new RpcClientTemplate(createBootstrap(getClassResolver(classResolverType)));
    }

    public static RpcClientTemplate createClientTemplate() throws RpcFactoryInitException {
        return createClientTemplate(weakCachingResolver);
    }

    public static RpcServerTemplate createServerTemplate(ClassResolverType classResolverType) throws RpcFactoryInitException {
        return new RpcServerTemplate(createServerBootstrap(getClassResolver(classResolverType)));
    }

    public static RpcServerTemplate createServerTemplate() throws RpcFactoryInitException {
        return createServerTemplate(weakCachingResolver);
    }

    private static boolean isClearStart = false;

    private static ServerBootstrap createServerBootstrap(final ClassResolver classResolver) throws RpcFactoryInitException {
        if (!SERVER_INIT) {
            throw new RpcFactoryInitException("Not initialized");
        }

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(serverChannelClass)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast(new CombinedChannelDuplexHandler<>(
                                        new ObjectDecoder(classResolver),
                                        new ObjectEncoder()
                                ))
                                .addLast(new RpcServerHandler());
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

    private static Bootstrap createBootstrap(final ClassResolver classResolver) throws RpcFactoryInitException {
        if (!CLIENT_INIT && !SERVER_INIT) {
            throw new RpcFactoryInitException("Not initialized");
        }

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(workerGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(channelClass)
                .handler(new ChannelInitializer<SocketChannel>() {

                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast(new CombinedChannelDuplexHandler<>(
                                        new ObjectDecoder(classResolver),
                                        new ObjectEncoder()
                                ))
                                .addLast(new RpcClientHandler());
                    }
                });

        callbackClear();
        return bootstrap;
    }

    private static void callbackClear() {
        if (isClearStart) {
            return;
        } else {
            isClearStart = true;
        }

        new Thread(() -> {
            while (true) {
                if (callbackMap.size() > 0) {
                    Set<Map.Entry<String, Callback>> entries = callbackMap.entrySet();

                    for (Map.Entry<String, Callback> entry : entries) {
                        long sendTime = Long.parseLong(entry.getKey().split(":")[0]);

                        if (System.currentTimeMillis() - sendTime >= 60000) {
                            callbackMap.remove(entry.getKey());
                        }
                    }
                }

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
