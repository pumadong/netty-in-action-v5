package chapter2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * EchoServer业务逻辑处理程序
 */
// #1 Annotate with @Sharable to share between channels
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Server received:" + ((ByteBuf)msg).toString(CharsetUtil.UTF_8));
        // #2 Write the received messages back . Be aware that this will not
        // flush the messages to the remote peer yet.
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // #3 Flush all previous written messages (that are pending) to the
        // remote peer, and close the channel after the operation is complete.
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // #4 Log exception
        cause.printStackTrace();
        // #5 Close channel on exception
        ctx.close();
    }
}