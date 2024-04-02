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

public class WebsocketProfile {

    private int eventGroupThreadNum;

    private int handshakeTimeout;

    private boolean isCompression;

    private int connectTimeout;

    public int getHandshakeTimeout() {
        return handshakeTimeout;
    }

    public void setHandshakeTimeout(int handshakeTimeout) {
        this.handshakeTimeout = handshakeTimeout;
    }

    public boolean isCompression() {
        return isCompression;
    }

    public void setCompression(boolean compression) {
        isCompression = compression;
    }

    public int getEventGroupThreadNum() {
        return eventGroupThreadNum;
    }

    public void setEventGroupThreadNum(int eventGroupThreadNum) {
        this.eventGroupThreadNum = eventGroupThreadNum;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public static WebsocketProfile defaultWebsocketProfile() {
        WebsocketProfile profile = new WebsocketProfile();
        profile.setCompression(false);
        profile.setHandshakeTimeout(5000);
        profile.setEventGroupThreadNum(0);
        profile.setConnectTimeout(5000);
        return profile;
    }
}
