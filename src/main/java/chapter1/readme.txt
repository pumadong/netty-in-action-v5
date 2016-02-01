使用NIO，select()到读事件时，要处理4种情况：

1. channel还有数据，继续读。

2. channel中暂时没数据，但channel还没断开，这是读取到的数据个数为0，结束读，继续到select()处阻塞等待数据。

3. 另一端channel.close()关闭连接，这时候读channel返回的读取数是-1，表示已经到末尾，跟读文件到末尾时是一样的。既然已经结束了，就把对应的SelectionKey给cancel掉，表示selector不再监听这个channel上的读事件。并且关闭连接，本端channel.close()。

4. 另一端被强制关闭，也就是channel没有close()就被强制断开了，这时候本端会抛出一个IOException异常，要处理这个异常。



忠告

尽量不要尝试实现自己的nio框架，除非有经验丰富的工程师
尽量使用经过广泛实践的开源NIO框架Mina、Netty3、xSocket
尽量使用最新稳定版JDK
遇到问题的时候，也许你可以先看下java的bug database