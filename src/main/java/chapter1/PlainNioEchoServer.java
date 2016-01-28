package chapter1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO Socket Server
 */
public class PlainNioEchoServer {
    public static void main(String[] args) throws IOException {
        PlainNioEchoServer plainNioEchoServer = new PlainNioEchoServer();
        System.out.println("PlainNioEchoServer start at 8888...");
        plainNioEchoServer.serve(8888);
    }

    public void serve(int port) throws IOException {
        System.out.println("Listening for connections on port " + port);
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        ServerSocket ss = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        // #1 Bind server to port
        ss.bind(address);
        serverChannel.configureBlocking(false);
        Selector selector = Selector.open();
        // #2 Register the channel with the selector to be interested in new
        // Client connections that get accepted
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            try {
                // #3 Block until something is selected
                selector.select();
            } catch (IOException ex) {
                ex.printStackTrace();
                // handle in a proper way
                break;
            }
            // #4 Get all SelectedKey instances
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                // #5 Remove the SelectedKey from the iterator
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        // #6 Accept the client connection
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from " + client.configureBlocking(false));
                        // #7 Register connection to selector and set ByteBuffer
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ,
                                ByteBuffer.allocate(100));
                    }
                    // #8 Check for SelectedKey for read
                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        // #9 Read data to ByteBuffer
                        client.read(output);
                    }
                    // #10 Check for SelectedKey for write
                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        output.flip();
                        // #11 Write data from ByteBuffer to channel
                        client.write(output);
                        output.compact();
                    }
                } catch (IOException ex) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                    }
                }
            }
        }
    }
}