package chapter2;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * EchoServer
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        // #3 Create the EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // #4 Create the ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                // #5 Specify the use of an NIO transport Channel
                .channel(NioServerSocketChannel.class)  
                // #6 Set the socket address using the selected port
                .localAddress(new InetSocketAddress(port))  
                // #7 Add an EchoServerHandler to the Channel's ChannelPipeline
                .childHandler(new ChannelInitializer<SocketChannel>() { 
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new EchoServerHandler());
                    }
                });
            // #8 Bind the server; sync waits for the server to close
            ChannelFuture f = b.bind().sync();
            System.out.println(EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
            // #9 Close the channel and block until it is closed
            f.channel().closeFuture().sync();
            System.out.println(EchoServer.class.getName() + " close");
        } finally {
            // #10 Shutdown the EventLoopGroup, which releases all resources.
            group.shutdownGracefully().sync();
            System.out.println(EchoServer.class.getName() + " finally");
        }
    }

    public static void main(String[] args) throws Exception {
        
//         if (args.length != 1) {
//         System.err.println("Usage: " + EchoServer.class.getSimpleName() +
//         " <port>");
//         }
//         // #1 Set the port value (throws a NumberFormatException if the port
//         argument is malformed)
//         int port = Integer.parseInt(args[0]);
        
        int port = 8888;
        // #2 all the server's start() method.
        new EchoServer(port).start();
    }
}