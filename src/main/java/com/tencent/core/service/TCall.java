package com.tencent.core.service;

import java.io.IOException;

public interface TCall<T> {
    @Deprecated
    void after();

    /**
     * 1.0.4版本后不再使用该方法，替换为stop方法
     *
     * @return 是否结束
     */
    Boolean end();

    /**
     * 1.0.4版本后不再使用该方法，替换为write方法
     *
     * @param stream
     * @return TCall
     * @throws IOException
     */
    TCall execute(T stream) throws IOException;
}
