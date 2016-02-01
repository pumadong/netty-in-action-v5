package chapter1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

/**
 * NIO Socket Server 2
 */
public class PlainNio2EchoServer {
    public static void main(String[] args) throws IOException {
        PlainNio2EchoServer plainNio2EchoServer = new PlainNio2EchoServer();
        System.out.println("PlainNio2EchoServer start at 8888...");
        plainNio2EchoServer.serve(8888);
    }
    public void serve(int port) throws IOException {
        System.out.println("Listening for connections on port " + port + "\n");
        final AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(port);
        // #1 Bind Server to port
        serverChannel.bind(address);
        final CountDownLatch latch = new CountDownLatch(1);
        // #2 Start to accept new Client connections. Once one is accepted the
        // CompletionHandler will get called.
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(final AsynchronousSocketChannel channel, Object attachment) {
                System.out.println("Accepted connection from " + channel);
                // #3 Again accept new Client connections
                serverChannel.accept(null, this);
                ByteBuffer buffer = ByteBuffer.allocate(100);
                // #4 Trigger a read operation on the Channel, the given
                // CompletionHandler will be notified once something was read
                channel.read(buffer, buffer, new EchoCompletionHandler(channel));
            }

            @Override
            public void failed(Throwable throwable, Object attachment) {
                try {
                    // #5 Close the socket on error
                    serverChannel.close();
                } catch (IOException e) {
                    // ingnore on close
                } finally {
                    latch.countDown();
                }
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private final class EchoCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousSocketChannel channel;

        EchoCompletionHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void completed(Integer result, ByteBuffer buffer) {
            
            // 更多参考:
            // 关于关闭服务器端连接：http://www.cnblogs.com/549294286/p/3947751.html
            // Java NIO开发需要注意的陷阱：http://www.cnblogs.com/pingh/p/3224990.html
            // Java NIO服务器，远程主机强迫关闭了一个现有的连接：http://blog.csdn.net/abc_key/article/details/29295569
            
            if(result == -1) {
                try {
                    System.out.println("Server_Close：" + channel + "\n");
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            } 
            System.out.print("Accepted data " + new String(buffer.array()));
            buffer.flip();
            // #6 Trigger a write operation on the Channel, the given
            // CompletionHandler will be notified once something was written
            channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer buffer) {
                    if (buffer.hasRemaining()) {
                        // #7 Trigger again a write operation if something is
                        // left in the ByteBuffer
                        channel.write(buffer, buffer, this);
                    } else {
                        buffer.compact();
                        // #8 Trigger a read operation on the Channel, the given
                        // CompletionHandler will be notified once something was
                        // read
                        channel.read(buffer, buffer, EchoCompletionHandler.this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        // ingnore on close
                    }
                }
            });
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            try {
                channel.close();
            } catch (IOException e) {
                // ingnore on close
            }
        }
    }
}