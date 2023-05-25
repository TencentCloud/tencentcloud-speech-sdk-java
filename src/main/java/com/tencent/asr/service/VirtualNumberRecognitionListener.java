package com.tencent.asr.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.tencent.asr.model.VirtualNumberResponse;
import java.util.Date;

public abstract class VirtualNumberRecognitionListener {

    public String Id;

    public VirtualNumberRecognitionListener(String id) {
        this.Id = id;
    }

    // OnRecognitionStart implementation of VirtualNumberRecognitionListener
    public void onRecognitionStart(VirtualNumberResponse response) {
        String message = "OnRecognitionStart: ".concat(this.Id).concat(" ").concat(new Date().toString());
        System.out.println(message);
    }

    // OnRecognitionComplete implementation of VirtualNumberRecognitionListener
    public void onRecognitionComplete(VirtualNumberResponse response) {
        String message = "OnRecognitionComplete: ".concat(this.Id).concat(" ").concat(new Date().toString());
        System.out.println(message);
    }

    // OnFail implementation of VirtualNumberRecognitionListener
    public void onFail(VirtualNumberResponse response) {
        String message = "OnFail: ".concat(this.Id).concat(" ").concat(new Date().toString());
        System.out.println(message);
    }
}
