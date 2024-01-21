package com.qkwl.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author jany
 * @Date 17-4-20
 */
@ConfigurationProperties(prefix = "aliyun.mq")
public class MQProperties {

    private String accessKey;
    private String secretKey;
    private String onsAddr;
    private final Pid pid = new Pid();
    private final Cid cid = new Cid();

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getOnsAddr() {
        return onsAddr;
    }

    public void setOnsAddr(String onsAddr) {
        this.onsAddr = onsAddr;
    }

    public Pid getPid() {
        return pid;
    }

    public Cid getCid() {
        return cid;
    }

    public static class Pid {
        private String entrustState;
        private String entrustReq;
        private String validate;
        private String score;
        private String userAction;
        private String adminAction;
        private String c2cEntrustStatus;
        private String commission;
        private String otc;
        private String closeout;
        
        public String getC2cEntrustStatus() {
			return c2cEntrustStatus;
		}

		public void setC2cEntrustStatus(String c2cEntrustStatus) {
			this.c2cEntrustStatus = c2cEntrustStatus;
		}

		public String getEntrustState() {
            return entrustState;
        }

        public void setEntrustState(String entrustState) {
            this.entrustState = entrustState;
        }

        public String getValidate() {
            return validate;
        }

        public void setValidate(String validate) {
            this.validate = validate;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getUserAction() {
            return userAction;
        }

        public void setUserAction(String userAction) {
            this.userAction = userAction;
        }

        public String getAdminAction() {
            return adminAction;
        }

        public void setAdminAction(String adminAction) {
            this.adminAction = adminAction;
        }

		public String getCommission() {
			return commission;
		}

		public void setCommission(String commission) {
			this.commission = commission;
		}

        public String getEntrustReq() {
            return entrustReq;
        }

        public void setEntrustReq(String entrustReq) {
            this.entrustReq = entrustReq;
        }
        public String getOtc() {
            return otc;
        }

        public void setOtc(String otc) {
            this.otc = otc;
        }

        public String getCloseout() {
            return closeout;
        }

        public void setCloseout(String closeout) {
            this.closeout = closeout;
        }
    }

    public static class Cid {
        private String entrustReq;
        private String entrustState;
        private String validate;
        private String score;
        private String userAction;
        private String adminAction;
        private String c2cEntrustStatus;
        private String commission;
        private String otc;
        private String closeout;

        public String getC2cEntrustStatus() {
			return c2cEntrustStatus;
		}

		public void setC2cEntrustStatus(String c2cEntrustStatus) {
			this.c2cEntrustStatus = c2cEntrustStatus;
		}
        public String getEntrustState() {
            return entrustState;
        }

        public void setEntrustState(String entrustState) {
            this.entrustState = entrustState;
        }

        public String getValidate() {
            return validate;
        }

        public void setValidate(String validate) {
            this.validate = validate;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getUserAction() {
            return userAction;
        }

        public void setUserAction(String userAction) {
            this.userAction = userAction;
        }

        public String getAdminAction() {
            return adminAction;
        }

        public void setAdminAction(String adminAction) {
            this.adminAction = adminAction;
        }

		public String getCommission() {
			return commission;
		}

		public void setCommission(String commission) {
			this.commission = commission;
		}

        public String getEntrustReq() {
            return entrustReq;
        }

        public void setEntrustReq(String entrustReq) {
            this.entrustReq = entrustReq;
        }
        public String getOtc() {
            return otc;
        }

        public void setOtc(String otc) {
            this.otc = otc;
        }

        public String getCloseout() {
            return closeout;
        }

        public void setCloseout(String closeout) {
            this.closeout = closeout;
        }
    }

}