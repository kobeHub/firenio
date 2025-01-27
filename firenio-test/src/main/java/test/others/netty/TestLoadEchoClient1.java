package test.others.netty;

import java.io.IOException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import test.test.ITestThread;
import test.test.ITestThreadHandle;

public class TestLoadEchoClient1 extends ITestThread {

    static final         int    core_size = 16;
    private static final byte[] req;
    private static final String reqS;

    static {
        int    len = 128;
        String s   = "hello server!";
        for (int i = 0; i < len; i++) {
            s += "hello server!";
        }
        reqS = s;
        req = s.getBytes();
    }

    private ChannelInboundHandlerAdapter eventHandleAdaptor = null;
    private ChannelFuture                f;

    private EventLoopGroup group = NettyUtil.newEventLoopGroup();

    public static void main(String[] args) throws IOException {

        int time = 1024 * 256;

        int core_size = 16;

        ITestThreadHandle.doTest(TestLoadEchoClient1.class, core_size, time / core_size);
    }

    @Override
    public void prepare() throws Exception {

        eventHandleAdaptor = new ChannelInboundHandlerAdapter() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                addCount(1024);
            }
        };

        Bootstrap b = new Bootstrap();
        b.group(group);
        b.channel(NettyUtil.newSocketChannel());
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

                pipeline.addLast("handler", eventHandleAdaptor);
            }
        });

        f = b.connect("localhost", 8300).sync();
    }

    @Override
    public void run() {
        int time1 = getTime();
        for (int i = 0; i < time1; i++) {
            f.channel().writeAndFlush(reqS);
        }
    }

    @Override
    public void stop() {
        group.shutdownGracefully();
    }
}
