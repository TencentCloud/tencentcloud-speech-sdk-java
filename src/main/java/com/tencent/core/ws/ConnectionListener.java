package com.tencent.core.ws;

import java.nio.ByteBuffer;

/**
 * ConnectionListener 连接监听器
 */
public interface ConnectionListener {

    /**
     * Invoked once the connection to the remote URL has been established.
     */
    void onOpen();

    /**
     * Invoked after the connection was closed.
     *
     * @param closeCode the RFC 6455 status code
     * @param reason    a string description for the reason of the close
     */
    void onClose(int closeCode, String reason);

    /**
     * Invoked on arrival of a text message.
     *
     * @param message the text message.
     */
    void onMessage(String message);

    /**
     * Invoked on arrival of a binary message.
     *
     * @param message the binary message.
     */
    void onMessage(ByteBuffer message);
}
