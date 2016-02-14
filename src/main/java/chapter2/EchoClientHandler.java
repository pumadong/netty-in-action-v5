package chapter2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * EchoClient业务逻辑处理程序，原书的例程中，这个代码有一些Bug，我已经修复
 */
// #1 Annotate with @Sharable as it can be shared between channels
@Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    // 原书 bug fix : https://forums.manning.com/posts/list/33237.page
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // #2 Write message now that channel is connected
        ctx.write(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
        ctx.flush();
        System.out.println("Client send:" + "Netty rocks!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        // #3 Log received message as hexdump
        System.out.println("Client received:" + in.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // #4 Log exception and close channel
        cause.printStackTrace();
        ctx.close();
    }
}