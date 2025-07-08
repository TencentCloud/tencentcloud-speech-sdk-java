package com.tencent.core.ws;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * netty handler
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private static Logger logger = LoggerFactory.getLogger(WebSocketClientHandler.class);

    public void setListener(ConnectionListener listener) {
        this.listener = listener;
    }

    ConnectionListener listener;

    private WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    public WebSocketClientHandler() {

    }

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        logger.debug("handler added channelid:{}", ctx.channel().id());
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.debug("channel active,id:{},{}", ctx.channel().id(), Thread.currentThread().getId());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (ctx.channel() != null) {
            logger.debug("channelInactive:" + ctx.channel().id());
        } else {
            logger.debug("channelInactive");
        }
        if (!handshaker.isHandshakeComplete()) {
            String errorMsg;
            if (ctx.channel() != null) {
                errorMsg = "channel inactive during handshake,connectionId:" + ctx.channel().id();
            } else {
                errorMsg = "channel inactive during handshake";
            }
            logger.debug(errorMsg);
            handshakeFuture.setFailure(new Exception(errorMsg));
        }
        if (listener != null) {
            listener.onClose(-1, "channelInactive");
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                FullHttpResponse response = (FullHttpResponse) msg;
                handshaker.finishHandshake(ch, response);
                handshakeFuture.setSuccess();
                logger.debug("WebSocket Client connected! response headers:{}", response.headers());
            } catch (WebSocketHandshakeException e) {
                FullHttpResponse res = (FullHttpResponse) msg;
                String errorMsg = String.format("WebSocket Client failed to connect,status:%s,reason:%s",
                        res.status(), res.content().toString(CharsetUtil.UTF_8));
                logger.error(errorMsg);
                handshakeFuture.setFailure(new Exception(errorMsg));
            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() +
                    ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            listener.onMessage(textFrame.text());
        } else if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binFrame = (BinaryWebSocketFrame) frame;
            listener.onMessage(binFrame.content().nioBuffer());
        } else if (frame instanceof PongWebSocketFrame) {
            logger.debug("WebSocket Client received pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            logger.debug("receive close frame");
            listener.onClose(((CloseWebSocketFrame) frame).statusCode(), ((CloseWebSocketFrame) frame).reasonText());
            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        logger.error("error", cause);
        ctx.close();
    }
}

