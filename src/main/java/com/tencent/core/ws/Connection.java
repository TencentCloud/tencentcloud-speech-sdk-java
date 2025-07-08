package com.tencent.core.ws;

/**
 * Connection
 */
public interface Connection {

    String getChannelId();

    long getConnectingLatency();

    long getHandshakeLatency();

    void close();

    void sendText(final String text);

    void sendBinary(final byte[] data);

    void sendPing();


    boolean isActive();

}
