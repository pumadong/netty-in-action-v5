package chapter2;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * EchoClient
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // #1 Create bootstrap for client
            Bootstrap b = new Bootstrap();
            // #2 Specify EventLoopGroup to handle client events. NioEventLoopGroup is used, as the NIO-Transport should be used
            b.group(group)
                // #3 Specify channel type; use correct one for NIO-Transport
                .channel(NioSocketChannel.class)
                // #4 Set InetSocketAddress to which client connects
                .remoteAddress(new InetSocketAddress(host, port))
                // #5 Specify ChannelHandler, using ChannelInitializer, called once connection established and channel created
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        // #6 Add EchoClientHandler to ChannelPipeline that belongs to channel. 
                        // ChannelPipeline holds all ChannelHandlers of channel
                        ch.pipeline().addLast(new EchoClientHandler());
                    }
                });
            // 7 Connect client to remote peer; wait until sync() completes connect completes
            ChannelFuture f = b.connect().sync();
            System.out.println(EchoClient.class.getName() + " connect on " + f.channel().localAddress());
            // #8 Wait until ClientChannel closes. This will block.
            f.channel().closeFuture().sync();
            System.out.println(EchoClient.class.getName() + " close");
        } finally {
            // #9 Shut down bootstrap and thread pools; release all resources
            group.shutdownGracefully().sync();
            System.out.println(EchoClient.class.getName() + " finally");
        }
    }

    public static void main(String[] args) throws Exception {
        /*
        if (args.length != 2) {
            System.err.println("Usage: " + EchoClient.class.getSimpleName() + " <host> <port>");
            return;
        }
        // Parse options.
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        */
        new EchoClient("127.0.0.1", 8888).start();
    }
}