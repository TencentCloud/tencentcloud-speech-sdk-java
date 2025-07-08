package com.tencent.core.ws;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsocketConnection implements Connection {

    private static Logger logger = LoggerFactory.getLogger(WebsocketConnection.class);
    Channel channel;
    long connectingLatency;
    long handshakeLatency;

    public WebsocketConnection(Channel channel) {
        this.channel = channel;
    }

    public WebsocketConnection(Channel channel, long connectingLatency, long handshakeLatency) {
        this.channel = channel;
        this.connectingLatency = connectingLatency;
        this.handshakeLatency = handshakeLatency;
    }

    @Override
    public String getChannelId() {
        if (channel != null) {
            return channel.id().toString();
        }
        return null;

    }

    @Override
    public boolean isActive() {
        if (channel != null && channel.isActive()) {
            return true;
        }
        return false;
    }

    @Override
    public long getConnectingLatency() {
        return connectingLatency;
    }

    @Override
    public long getHandshakeLatency() {
        return handshakeLatency;
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public void sendText(final String text) {
        if (channel != null && channel.isActive()) {
            logger.debug("thread:{},send:{}", Thread.currentThread().getId(), text);
            channel.writeAndFlush(new TextWebSocketFrame(text));
        }

    }

    @Override
    public void sendBinary(byte[] data) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(data)));
        }

    }

    @Override
    public void sendPing() {
        PingWebSocketFrame frame = new PingWebSocketFrame();
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(frame);
        }
    }
}
