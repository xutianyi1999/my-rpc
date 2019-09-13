package club.koumakan.rpc;

import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.handler.RpcClientHandler;
import club.koumakan.rpc.handler.RpcServerHandler;
import club.koumakan.rpc.message.entity.RequestMessage;
import club.koumakan.rpc.message.entity.ResponseMessage;
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

    private final static boolean isLinux = System.getProperty("os.name").contains("Linux");

    private static boolean init = false;

    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;
    private static Class<? extends ServerSocketChannel> serverChannelClass;
    private static Class<? extends Channel> channelClass;

    public static void serverInit() {
        if (init) {
            return;
        }

        if (isLinux) {
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

        if (isLinux) {
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

    public static RpcClientTemplate createClientTemplate(Class<? extends ResponseMessage> responseMessageClass,
                                                         ClassResolverType classResolverType) {

        return new RpcClientTemplate(createBootstrap(getClassResolver(responseMessageClass, classResolverType)));
    }

    public static RpcClientTemplate createClientTemplate(Class<? extends ResponseMessage> responseMessageClass) {
        return createClientTemplate(responseMessageClass, weakCachingResolver);
    }


    public static RpcServerTemplate createServerTemplate(Class<? extends RequestMessage> requestMessageClass,
                                                         ClassResolverType classResolverType) {
        return new RpcServerTemplate(createServerBootstrap(getClassResolver(requestMessageClass, classResolverType)));
    }

    public static RpcServerTemplate createServerTemplate(Class<? extends RequestMessage> requestMessageClass) {
        return createServerTemplate(requestMessageClass, weakCachingResolver);
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

    private static ClassResolver getClassResolver(Class clazz, ClassResolverType classResolverType) {
        if (classResolverType == cacheDisabled) {
            return ClassResolvers.cacheDisabled(clazz.getClassLoader());
        } else if (classResolverType == softCachingConcurrentResolver) {
            return ClassResolvers.softCachingConcurrentResolver(clazz.getClassLoader());
        } else if (classResolverType == softCachingResolver) {
            return ClassResolvers.softCachingResolver(clazz.getClassLoader());
        } else if (classResolverType == weakCachingConcurrentResolver) {
            return ClassResolvers.weakCachingConcurrentResolver(clazz.getClassLoader());
        } else if (classResolverType == weakCachingResolver) {
            return ClassResolvers.weakCachingResolver(clazz.getClassLoader());
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
