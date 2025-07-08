package com.tencent.core.handler;

public interface RealTimeEventListener<T, M> {

    M translation(T response);
}
