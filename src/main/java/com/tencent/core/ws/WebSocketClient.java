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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.tencent.core.ws.Constant.HEADER_TOKEN;

public class WebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

    private WebsocketProfile websocketProfile;

    private Bootstrap bootstrap = new Bootstrap();

    private EventLoopGroup eventLoopGroup;

    private int port;

    private SslContext sslCtx;

    public WebSocketClient(final String uriStr, WebsocketProfile profile) throws Exception {
        this.websocketProfile = profile;
        eventLoopGroup = new NioEventLoopGroup(profile.getEventGroupThreadNum());
        URI websocketURI = new URI(uriStr);
        port = getURIPort(websocketURI);
        bootstrap.option(ChannelOption.TCP_NODELAY, true).group(eventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                if (sslCtx != null) {
                    p.addLast(sslCtx.newHandler(ch.alloc(), websocketURI.getHost(), 443));
                }
                if (websocketProfile.isCompression()) {
                    p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192), WebSocketClientCompressionHandler.INSTANCE);
                } else {
                    p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192));
                }

                p.addLast("hookedHandler", new WebSocketClientHandler());

            }
        });

    }


    public Connection connect(ConnectionProfile connectionProfile, ConnectionListener listener, int connectionTimeout,int maxFramePayloadLength) throws Exception {
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout);
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        httpHeaders.set(Constant.HEADER_AUTHORIZATION, connectionProfile.getSign());
        httpHeaders.set(Constant.HEADER_HOST, connectionProfile.getHost());
        if (connectionProfile.getToken() != null && !Objects.equals(connectionProfile.getToken(), "")) {
            httpHeaders.set(HEADER_TOKEN, connectionProfile.getToken());
        }
        URI websocketURI = new URI(connectionProfile.getUrl());
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, null, true, httpHeaders, maxFramePayloadLength);
        long start = System.currentTimeMillis();
        Channel channel = bootstrap.connect(websocketURI.getHost(), port).sync().channel();
        long connectingTime = System.currentTimeMillis() - start;
        logger.debug("websocket channel is established after sync,connectionId:{} ,use {}", channel.id(), connectingTime);
        WebSocketClientHandler handler = (WebSocketClientHandler) channel.pipeline().get("hookedHandler");
        handler.setListener(listener);
        handler.setHandshaker(handshaker);
        handshaker.handshake(channel);
        start = System.currentTimeMillis();
        waitHandshake(handler.handshakeFuture(), channel);
        long handshakeTime = System.currentTimeMillis() - start;
        logger.debug("websocket connection is established after handshake,connectionId:{},use {}", channel.id(), handshakeTime);
        return new WebsocketConnection(channel, connectingTime, handshakeTime);
    }

    public void shutdown() {
        eventLoopGroup.shutdownGracefully();
    }

    private void waitHandshake(ChannelFuture handshakeFuture, Channel channel) throws Exception {
        if (handshakeFuture.await(websocketProfile.getHandshakeTimeout(), TimeUnit.SECONDS)) {
            return;
        }
        // 握手超时后关闭连接
        if (channel.isActive()) {
            channel.close();
        }
        if (handshakeFuture.cause() != null) {
            throw new Exception("Handshake timeout!", handshakeFuture.cause());
        } else {
            throw new Exception("Handshake timeout!");
        }
    }

    private int getURIPort(URI websocketURI) throws SSLException {
        final boolean ssl = "wss".equalsIgnoreCase(websocketURI.getScheme());
        int port = websocketURI.getPort();
        if (ssl) {
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            if (port == -1) {
                port = 443;
            }
        } else {
            if (port == -1) {
                port = 80;
            }
        }
        return port;
    }
}
