/*
 * Copyright (c) 2017-2018 THL A29 Limited, a Tencent company. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
