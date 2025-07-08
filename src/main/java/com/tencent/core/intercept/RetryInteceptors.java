package com.tencent.core.intercept;

import com.tencent.core.service.ReportService;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class RetryInteceptors implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        try {
            ReportService.ifLogMessage("request URL", request.url().toString(), false);
            response = chain.proceed(request);
            //okHttp框架本身会对下面两种错误进行重拾
            //RouteException：所有网络连接失败的异常，包括IOException中的连接失败异常；
            //IOException：除去连接异常的其他的IO异常。
            if (!response.isSuccessful()) {
                response.close();
                ReportService.ifLogMessage(request.url().toString(), "Interceptor retry", true);
                response = chain.proceed(request);
            }
        } catch (SocketTimeoutException socketTimeoutException) {
            //超时重试
            closeResponse(response);
            ReportService.ifLogMessage(request.url().toString(), socketTimeoutException.getMessage(), true);
            response = chain.proceed(request);
        }
        return response;
    }

    private void closeResponse(Response response) {
        try {
            if (response != null) {
                if (response.body() != null) {
                    response.body().close();
                }
                response.close();
            }
        } catch (Exception e) {
            //ignore
        }

    }
}
