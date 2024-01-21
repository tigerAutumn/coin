package com.qkwl.common.akka;

import com.qkwl.common.dto.mq.MQDeal;

import java.io.Serializable;

public interface DealtradeMessage {

    public static class DealFailed implements Serializable {
        private final String reason;
        private final MQDeal job;

        public DealFailed(String reason, MQDeal job) {
            this.reason = reason;
            this.job = job;
        }

        public String getReason() {
            return reason;
        }

        public MQDeal getJob() {
            return job;
        }

        @Override
        public String toString() {
            return "DealFailed(" + reason + ")";
        }
    }
    public static class DealResult implements  Serializable {
        private final String text;

        public DealResult(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return "DealResult(" + text + ")";
        }


    }
    public static final String BACKEND_REGISTRATION = "BackendRegistration";
}
