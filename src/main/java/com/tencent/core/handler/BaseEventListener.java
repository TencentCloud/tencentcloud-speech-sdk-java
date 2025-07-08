package com.tencent.core.handler;

public abstract class BaseEventListener<T> {

    /**
     * 成功
     * @param t 结果
     */
    public void success(T t) {

    }

    /**
     * 失败
     * @param t 结果
     * @param e 注意有可能为空
     */
    public void fail(T t, Exception e) {

    }
}
