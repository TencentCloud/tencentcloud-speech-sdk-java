package com.tencent.asr.service;

import com.tencent.core.model.GlobalConfig;
import com.tencent.core.service.ReportService;
import com.tencent.core.service.StatService;

/**
 * 事务管理
 */
public class TractionManager {

    private Long appId;

    public TractionManager(Long appId) {
        this.appId = appId;
    }

    public void beginTraction(String streamId) {
        if (GlobalConfig.ifOpenStat) {
            StatService.getAsrStatistics().getStreamNum().incrementAndGet();
            StatService.getAsrStatistics().getVoiceIdNum().incrementAndGet();
        }
        ReportService.ifLogMessage(streamId, "Open transaction:" + streamId, false);
    }

    public void endTraction(String streamId) {
        ReportService.ifLogMessage(streamId, "Close transaction:" + streamId, false);
    }

}
