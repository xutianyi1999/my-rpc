package club.koumakan.rpc.core;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcFactoryCore {

    private final static boolean IS_LINUX = System.getProperty("os.name").contains("Linux");

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Class<? extends ServerSocketChannel> serverChannelClass;
    private Class<? extends SocketChannel> channelClass;

    public static RpcFactoryCore server() {
        RpcFactoryCore rpcFactoryCore = new RpcFactoryCore();

        if (IS_LINUX) {
            rpcFactoryCore
                    .setBossGroup(new EpollEventLoopGroup())
                    .setWorkerGroup(new EpollEventLoopGroup())
                    .setServerChannelClass(EpollServerSocketChannel.class);
        } else {
            rpcFactoryCore
                    .setBossGroup(new NioEventLoopGroup())
                    .setWorkerGroup(new NioEventLoopGroup())
                    .setServerChannelClass(NioServerSocketChannel.class);
        }
        return rpcFactoryCore;
    }

    public static RpcFactoryCore client() {
        RpcFactoryCore rpcFactoryCore = new RpcFactoryCore();

        if (IS_LINUX) {
            rpcFactoryCore
                    .setWorkerGroup(new EpollEventLoopGroup())
                    .setChannelClass(EpollSocketChannel.class);
        } else {
            rpcFactoryCore
                    .setWorkerGroup(new NioEventLoopGroup())
                    .setChannelClass(NioSocketChannel.class);
        }
        return rpcFactoryCore;
    }

    public static RpcFactoryCore serverAndClient() {
        RpcFactoryCore rpcFactoryCore = new RpcFactoryCore();

        if (IS_LINUX) {
            rpcFactoryCore
                    .setBossGroup(new EpollEventLoopGroup())
                    .setWorkerGroup(new EpollEventLoopGroup())
                    .setServerChannelClass(EpollServerSocketChannel.class)
                    .setChannelClass(EpollSocketChannel.class);
        } else {
            rpcFactoryCore
                    .setBossGroup(new NioEventLoopGroup())
                    .setWorkerGroup(new NioEventLoopGroup())
                    .setServerChannelClass(NioServerSocketChannel.class)
                    .setChannelClass(NioSocketChannel.class);
        }
        return rpcFactoryCore;
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public RpcFactoryCore setBossGroup(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
        return this;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public RpcFactoryCore setWorkerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
        return this;
    }

    public Class<? extends ServerSocketChannel> getServerChannelClass() {
        return serverChannelClass;
    }

    public RpcFactoryCore setServerChannelClass(Class<? extends ServerSocketChannel> serverChannelClass) {
        this.serverChannelClass = serverChannelClass;
        return this;
    }

    public Class<? extends SocketChannel> getChannelClass() {
        return channelClass;
    }

    public RpcFactoryCore setChannelClass(Class<? extends SocketChannel> channelClass) {
        this.channelClass = channelClass;
        return this;
    }
}
