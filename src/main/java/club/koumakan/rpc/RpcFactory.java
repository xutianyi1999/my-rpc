package club.koumakan.rpc;

import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.handler.RpcClientHandler;
import club.koumakan.rpc.handler.RpcServerHandler;
import club.koumakan.rpc.message.entity.Call;
import club.koumakan.rpc.template.RpcClientTemplate;
import club.koumakan.rpc.template.RpcServerTemplate;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.channel.EventLoopGroup;
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

    private static boolean init = false;

    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;
    private static Class<? extends ServerSocketChannel> serverChannelClass;
    private static Class<? extends Channel> channelClass;

    public static void serverInit() {
        if (init) {
            return;
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
        init = true;
    }

    public static void serverInit(EventLoopGroup bossGroup,
                                  EventLoopGroup workerGroup,
                                  Class<? extends ServerSocketChannel> serverChannelClass) {

        if (init) {
            return;
        }

        RpcFactory.bossGroup = bossGroup;
        RpcFactory.workerGroup = workerGroup;
        RpcFactory.serverChannelClass = serverChannelClass;
        init = true;
    }

    public static void clientInit() {
        if (init) {
            return;
        }

        if (IS_LINUX) {
            workerGroup = new EpollEventLoopGroup();
            channelClass = EpollSocketChannel.class;
        } else {
            workerGroup = new NioEventLoopGroup();
            channelClass = NioSocketChannel.class;
        }

        callbackClear();
        init = true;
    }

    public static void clientInit(EventLoopGroup workerGroup, Class<? extends Channel> channelClass) {
        if (init) {
            return;
        }

        RpcFactory.workerGroup = workerGroup;
        RpcFactory.channelClass = channelClass;

        callbackClear();
        init = true;
    }

    public static RpcClientTemplate createClientTemplate(ClassResolverType classResolverType) {

        return new RpcClientTemplate(createBootstrap(getClassResolver(classResolverType)));
    }

    public static RpcClientTemplate createClientTemplate() {
        return createClientTemplate(weakCachingResolver);
    }

    public static RpcServerTemplate createServerTemplate(ClassResolverType classResolverType) {
        return new RpcServerTemplate(createServerBootstrap(getClassResolver(classResolverType)));
    }

    public static RpcServerTemplate createServerTemplate() {
        return createServerTemplate(weakCachingResolver);
    }

    private static Bootstrap createBootstrap(final ClassResolver classResolver) {
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(workerGroup)
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
        return bootstrap;
    }

    private static ServerBootstrap createServerBootstrap(final ClassResolver classResolver) {
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

    private static void callbackClear() {
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
