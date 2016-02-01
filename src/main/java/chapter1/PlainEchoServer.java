package chapter1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 简单的BIO Socket Server
 */
public class PlainEchoServer {
    public static void main(String[] args) throws IOException {
        PlainEchoServer plainEchoServer = new PlainEchoServer();
        System.out.println("PlainEchoServer start at 8888..." + "\n");
        plainEchoServer.serve(8888);
    }

    public void serve(int port) throws IOException {
        // #1 Bind server to port
        final ServerSocket socket = new ServerSocket(port);
        try {
            while (true) {
                // #2 Block until new client connection is accepted
                final Socket clientSocket = socket.accept();
                System.out.println("Accepted connection from " + clientSocket);
                // #3 Create new thread to handle client connection
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BufferedReader reader = null;
                        PrintWriter writer = null;
                        try {
                            reader = new BufferedReader(new InputStreamReader(
                                    clientSocket.getInputStream()));
                            writer = new PrintWriter(clientSocket.getOutputStream(), true);
                            // #4 Read data from client and write it back，while(true)，会一直读取，直到我们认为应该结束读取
                            while (true) {
                                String data = reader.readLine();
                                // 小议socket关闭：http://blog.whyun.com/posts/socket/
                                // Java Socket 编程：http://haohaoxuexi.iteye.com/blog/1979837
                                if(data == null) {
                                    break;
                                }
                                System.out.println("Accepted data " + data);
                                writer.println(data);
                                writer.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();

                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                                if(writer != null) {
                                    writer.close();
                                }
                                System.out.println("Server_Close：" + clientSocket + "\n");
                                clientSocket.close();
                            } catch (IOException ex) {
                                // ignore on close
                            }
                        }
                    }
                    // #5 Start thread
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}