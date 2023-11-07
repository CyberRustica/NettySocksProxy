package io.github.hydrocarbon.handler;

import io.github.hydrocarbon.utils.SocksServerUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksMessage;
import io.netty.handler.codec.socksx.v5.*;

import java.util.logging.Logger;

/**
 * @author zouzhenfeng
 * @since 2023-11-07
 */
@ChannelHandler.Sharable
@SuppressWarnings("java:S6548")
public class SocksServerHandler extends SimpleChannelInboundHandler<SocksMessage> {

    private static final Logger log = Logger.getLogger(SocksServerHandler.class.getName());

    public static final SocksServerHandler INSTANCE = new SocksServerHandler();

    private SocksServerHandler() {}

    @Override
    @SuppressWarnings("java:S125")
    public void channelRead0(ChannelHandlerContext ctx, SocksMessage socksRequest) {
        switch (socksRequest.version()) {
            case SOCKS5:
                if (socksRequest instanceof Socks5InitialRequest) {
                    /*
                     * 鉴权支持
                     * ctx.pipeline().addFirst(new Socks5PasswordAuthRequestDecoder());
                     * ctx.write(new DefaultSocks5AuthMethodResponse(Socks5AuthMethod.PASSWORD));
                     */
                    ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
                    ctx.write(new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH));
                } else if (socksRequest instanceof Socks5PasswordAuthRequest) {
                    ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
                    ctx.write(new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS));
                } else if (socksRequest instanceof Socks5CommandRequest socks5CmdRequest) {
                    if (socks5CmdRequest.type() == Socks5CommandType.CONNECT) {
                        ctx.pipeline().addLast(new SocksServerConnectHandler());
                        ctx.pipeline().remove(this);
                        ctx.fireChannelRead(socksRequest);
                    } else {
                        ctx.close();
                    }
                } else {
                    ctx.close();
                }
                break;
            case UNKNOWN:
            default:
                ctx.close();
                break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        log.info("exception caught: " + throwable.getMessage());
        SocksServerUtils.closeOnFlush(ctx.channel());
    }
}
