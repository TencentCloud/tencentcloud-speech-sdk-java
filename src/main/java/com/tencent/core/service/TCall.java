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
