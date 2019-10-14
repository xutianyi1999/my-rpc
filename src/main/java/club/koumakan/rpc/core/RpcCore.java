package club.koumakan.rpc.core;

import club.koumakan.rpc.core.client.ClientContext;
import club.koumakan.rpc.core.exception.RpcCoreException;
import club.koumakan.rpc.core.server.ServerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcCore {

    private final static boolean IS_LINUX = System.getProperty("os.name").contains("Linux");
    private static boolean isCreate = false;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Class<? extends ServerSocketChannel> serverChannelClass;
    private Class<? extends SocketChannel> channelClass;

    private static boolean isClient = false;
    private static boolean isServer = false;

    private RpcCore() throws RpcCoreException {
        checkIsCreated();
    }

    private static void checkIsCreated() throws RpcCoreException {
        if (isCreate) {
            throw new RpcCoreException("Already created");
        } else {
            isCreate = true;
        }
    }

    public static RpcCore server() throws RpcCoreException {
        RpcCore rpcCore = new RpcCore();

        if (IS_LINUX) {
            rpcCore
                    .setBossGroup(new EpollEventLoopGroup())
                    .setWorkerGroup(new EpollEventLoopGroup())
                    .setServerChannelClass(EpollServerSocketChannel.class);
        } else {
            rpcCore
                    .setBossGroup(new NioEventLoopGroup())
                    .setWorkerGroup(new NioEventLoopGroup())
                    .setServerChannelClass(NioServerSocketChannel.class);
        }

        isServer = true;
        return rpcCore;
    }

    public static RpcCore client() throws RpcCoreException {
        RpcCore rpcCore = new RpcCore();

        if (IS_LINUX) {
            rpcCore
                    .setWorkerGroup(new EpollEventLoopGroup())
                    .setChannelClass(EpollSocketChannel.class);
        } else {
            rpcCore
                    .setWorkerGroup(new NioEventLoopGroup())
                    .setChannelClass(NioSocketChannel.class);
        }

        isClient = true;
        return rpcCore;
    }

    public static RpcCore serverAndClient() throws RpcCoreException {
        RpcCore rpcCore = new RpcCore();

        if (IS_LINUX) {
            rpcCore
                    .setBossGroup(new EpollEventLoopGroup())
                    .setWorkerGroup(new EpollEventLoopGroup())
                    .setServerChannelClass(EpollServerSocketChannel.class)
                    .setChannelClass(EpollSocketChannel.class);
        } else {
            rpcCore
                    .setBossGroup(new NioEventLoopGroup())
                    .setWorkerGroup(new NioEventLoopGroup())
                    .setServerChannelClass(NioServerSocketChannel.class)
                    .setChannelClass(NioSocketChannel.class);
        }

        isClient = true;
        isServer = true;
        return rpcCore;
    }

    public RpcCore createInstance() throws RpcCoreException {
        RpcCore rpcCore = new RpcCore();
        isClient = true;
        isServer = true;
        return rpcCore;
    }

    public void destroy() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        if (isServer) {
            ServerContext.listenerMap.clear();
            isServer = false;
        }

        if (isClient) {
            ClientContext.inactiveMap.clear();
            ClientContext.callbackMap.clear();
            isClient = false;
        }
        isCreate = false;
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public RpcCore setBossGroup(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
        return this;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public RpcCore setWorkerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
        return this;
    }

    public Class<? extends ServerSocketChannel> getServerChannelClass() {
        return serverChannelClass;
    }

    public RpcCore setServerChannelClass(Class<? extends ServerSocketChannel> serverChannelClass) {
        this.serverChannelClass = serverChannelClass;
        return this;
    }

    public Class<? extends SocketChannel> getChannelClass() {
        return channelClass;
    }

    public RpcCore setChannelClass(Class<? extends SocketChannel> channelClass) {
        this.channelClass = channelClass;
        return this;
    }
}
