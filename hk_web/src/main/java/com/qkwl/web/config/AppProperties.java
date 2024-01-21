package com.qkwl.web.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {


    /**
     * buglyAppId : 03232cadb5
     * weixinAppId : wx7cf17d7976148b8a
     * AppSecret : be6a69fdc2cc1fc4c8f2e10d4bb108f4
     * HC_KEFU_Key : 1485180815068932#kefuchannelapp53964
     * HC_KEFU_TenantId : 53964
     * HC_KEFU_IMID : kefuchannelimid_286096
     * HC_MQTT_ACCESSKEY : LTAIwt8xfEgylfte
     * HC_MQTT_SECRETKEY : ZYyR0ll33dirOOu3n5ngc0moz0ikF3
     * HC_MQTT_TOPIC_TRADE_PREFIX : HOTCOIN_HK_TOPIC_MKTINFO
     * HC_MQTT_TRADING_TOPIC_RT : HOTCOIN_HK_WEB_REAL_TIME_TRADE
     * Socket : {"des":"行情交易服务器地址","MarketSocket":{"prod":"","des":"行情socket"},"TradeSocket":{"prod":"","des":"交易socket"},"MQTTSocket":{"prod":"post-cn-4590re2i204.mqtt.aliyuncs.com","des":"MQTTsocket"}}
     * HttpsURL : {"hkAppURL":{"prod":"https://hkapp.hotcoinex.com/v1/","des":"hkApp地址"},"hkWebURL":{"prod":"https://hkapp.hotcoinex.com","des":"hkWeb地址"},"OtcURL":{"prod":"https://hkotcapi.hotcoinex.com","des":"OTC地址"},"H5URL":{"prod":"https://m.hotcoinex.com","des":"H5地址"},"ossServerURL":{"prod":"https://hkossupload.hotcoinex.com/oss_server/upload","des":"ossServer地址"}}
     * personList : [{"menuId":1,"title":"委单管理","des":"委单管理","iosImage":"","andriodImage":"","webImage":"","sourceImage":"","hasNeed":true},{"menuId":4,"title":"支付设置","des":"支付设置","iosImage":"","andriodImage":"","webImage":"","sourceImage":"","hasNeed":true},{"menuId":5,"title":"安全中心","des":"安全中心","iosImage":"","andriodImage":"","webImage":"","sourceImage":"","hasNeed":true},{"menuId":6,"title":"个人设置","des":"个人设置","iosImage":"","andriodImage":"","webImage":"","sourceImage":"","hasNeed":true},{"menuId":7,"title":"联系客服","des":"联系客服","iosImage":"","andriodImage":"","webImage":"","sourceImage":"","hasNeed":true},{"menuId":8,"title":"帮助中心","des":"帮助中心","iosImage":"","andriodImage":"","webImage":"","sourceImage":"","hasNeed":true},{"menuId":9,"title":"费率标准","des":"费率标准","iosImage":"","andriodImage":"","webImage":"","sourceImage":"","hasNeed":true},{"menuId":10,"title":"关于我们","des":"关于我们","iosImage":"","andriodImage":"","webImage":"","sourceImage":"","hasNeed":true}]
     * tabBarList : [{"menuId":1,"title":"首页","des":"首页","iosImage":"","andriodImage":"","sourceImage":"","sourceImageSel":"","hasNeed":true},{"menuId":2,"title":"行情","des":"行情","iosImage":"","andriodImage":"","sourceImage":"","sourceImageSel":"","hasNeed":true},{"menuId":3,"title":"OTC","des":"OTC","iosImage":"","andriodImage":"","sourceImage":"","sourceImageSel":"","hasNeed":true},{"menuId":4,"title":"交易","des":"交易","iosImage":"","andriodImage":"","sourceImage":"","sourceImageSel":"","hasNeed":true},{"menuId":5,"title":"资产","des":"资产","iosImage":"","andriodImage":"","sourceImage":"","sourceImageSel":"","hasNeed":true}]
     * tradeArea : [{"name":"USDT","topic":"HOTCOIN_HK_TOPIC_USDT_TRADE","code":"4","hasNeed":true},{"name":"GAVC","topic":"HOTCOIN_HK_TOPIC_GSET_TRADE","code":"1","hasNeed":true},{"name":"BTC","topic":"HOTCOIN_HK_TOPIC_BTC_TRADE","code":"2","hasNeed":true},{"name":"ETH","topic":"HOTCOIN_HK_TOPIC_ETH_TRADE","code":"3","hasNeed":true},{"name":"创新区","topic":"HOTCOIN_HK_TOPIC_INNOVATE_TRADE","code":"5","hasNeed":true},{"name":"自选","topic":"","code":"0","hasNeed":true}]
     * systemLanguage : [{"title":"","des":"语言设置","hasNeed":true,"group":[{"menuId":1,"title":"zh_CN","des":"中文简体","hasNeed":true},{"menuId":2,"title":"en","des":"English","hasNeed":true},{"menuId":3,"title":"ko","des":"한국어","hasNeed":true}]}]
     */

	private String version;
    //腾讯BuglyId
    private String buglyAppId;
    private String buglyAppIdAndroid;
    private String buglyAppIdIOS;
    //腾讯BuglyKey
    private String buglyAppKey;
    //微信接入AppId
    private String weixinAppId;
    //微信AppSecret
    private String appSecret;
    //环信客服Key
    private String hC_KEFU_Key;
    //环信客服id
    private String hC_KEFU_TenantId;
    //环信客服Im号
    private String hC_KEFU_IMID;
    //Mqtt的AccessKey
    private String hC_MQTT_ACCESSKEY;
    //Mqtt的SecretKey
    private String hC_MQTT_SECRETKEY;

    private String hC_MQTT_TOPIC_TRADE_PREFIX;
    private String hC_MQTT_TRADING_TOPIC_RT;
    private SocketBean socket;
    private HttpsURLBean httpsURL;
    private HttpsDataBean httpsData;
    private List<PersonListBean> personList;
    private List<TabBarListBean> tabBarList;
    private ArrayList<TradingArea> tradeArea;
    private List<SystemLanguageBean> systemLanguage;

    public ArrayList<TradingArea> getTradeArea() {
        return tradeArea;
    }

    public void setTradeArea(ArrayList<TradingArea> tradeArea) {
        this.tradeArea = tradeArea;
    }

    public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBuglyAppId() {
        return buglyAppId;
    }

    public void setBuglyAppId(String buglyAppId) {
        this.buglyAppId = buglyAppId;
    }

    public String getBuglyAppIdAndroid() {
		return buglyAppIdAndroid;
	}

	public void setBuglyAppIdAndroid(String buglyAppIdAndroid) {
		this.buglyAppIdAndroid = buglyAppIdAndroid;
	}

	public String getBuglyAppIdIOS() {
		return buglyAppIdIOS;
	}

	public void setBuglyAppIdIOS(String buglyAppIdIOS) {
		this.buglyAppIdIOS = buglyAppIdIOS;
	}

	public String getBuglyAppKey() {
		return buglyAppKey;
	}

	public void setBuglyAppKey(String buglyAppKey) {
		this.buglyAppKey = buglyAppKey;
	}

	public String getWeixinAppId() {
        return weixinAppId;
    }

    public void setWeixinAppId(String weixinAppId) {
        this.weixinAppId = weixinAppId;
    }

    public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	
	public String gethC_KEFU_Key() {
		return hC_KEFU_Key;
	}

	public void sethC_KEFU_Key(String hC_KEFU_Key) {
		this.hC_KEFU_Key = hC_KEFU_Key;
	}

	public String gethC_KEFU_TenantId() {
		return hC_KEFU_TenantId;
	}

	public void sethC_KEFU_TenantId(String hC_KEFU_TenantId) {
		this.hC_KEFU_TenantId = hC_KEFU_TenantId;
	}

	public String gethC_KEFU_IMID() {
		return hC_KEFU_IMID;
	}

	public void sethC_KEFU_IMID(String hC_KEFU_IMID) {
		this.hC_KEFU_IMID = hC_KEFU_IMID;
	}

	public String gethC_MQTT_ACCESSKEY() {
		return hC_MQTT_ACCESSKEY;
	}

	public void sethC_MQTT_ACCESSKEY(String hC_MQTT_ACCESSKEY) {
		this.hC_MQTT_ACCESSKEY = hC_MQTT_ACCESSKEY;
	}

	public String gethC_MQTT_SECRETKEY() {
		return hC_MQTT_SECRETKEY;
	}

	public void sethC_MQTT_SECRETKEY(String hC_MQTT_SECRETKEY) {
		this.hC_MQTT_SECRETKEY = hC_MQTT_SECRETKEY;
	}

	public String gethC_MQTT_TOPIC_TRADE_PREFIX() {
		return hC_MQTT_TOPIC_TRADE_PREFIX;
	}

	public void sethC_MQTT_TOPIC_TRADE_PREFIX(String hC_MQTT_TOPIC_TRADE_PREFIX) {
		this.hC_MQTT_TOPIC_TRADE_PREFIX = hC_MQTT_TOPIC_TRADE_PREFIX;
	}

	public String gethC_MQTT_TRADING_TOPIC_RT() {
		return hC_MQTT_TRADING_TOPIC_RT;
	}

	public void sethC_MQTT_TRADING_TOPIC_RT(String hC_MQTT_TRADING_TOPIC_RT) {
		this.hC_MQTT_TRADING_TOPIC_RT = hC_MQTT_TRADING_TOPIC_RT;
	}

	public SocketBean getSocket() {
		return socket;
	}

	public void setSocket(SocketBean socket) {
		this.socket = socket;
	}

	public HttpsURLBean getHttpsURL() {
		return httpsURL;
	}

	public void setHttpsURL(HttpsURLBean httpsURL) {
		this.httpsURL = httpsURL;
	}

	public HttpsDataBean getHttpsData() {
		return httpsData;
	}

	public void setHttpsData(HttpsDataBean httpsData) {
		this.httpsData = httpsData;
	}

	public List<PersonListBean> getPersonList() {
        return personList;
    }

    public void setPersonList(List<PersonListBean> personList) {
        this.personList = personList;
    }

    public List<TabBarListBean> getTabBarList() {
        return tabBarList;
    }

    public void setTabBarList(List<TabBarListBean> tabBarList) {
        this.tabBarList = tabBarList;
    }

    public List<SystemLanguageBean> getSystemLanguage() {
        return systemLanguage;
    }

    public void setSystemLanguage(List<SystemLanguageBean> systemLanguage) {
        this.systemLanguage = systemLanguage;
    }

    public static class SocketBean {
        /**
         * des : 行情交易服务器地址
         * MarketSocket : {"prod":"","des":"行情socket"}
         * TradeSocket : {"prod":"","des":"交易socket"}
         * MQTTSocket : {"prod":"post-cn-4590re2i204.mqtt.aliyuncs.com","des":"MQTTsocket"}
         */

        private String des;
        private MarketSocketBean marketSocket;
        private TradeSocketBean tradeSocket;
        private MQTTSocketBean mQTTSocket;

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public MarketSocketBean getMarketSocket() {
			return marketSocket;
		}

		public void setMarketSocket(MarketSocketBean marketSocket) {
			this.marketSocket = marketSocket;
		}

		public TradeSocketBean getTradeSocket() {
			return tradeSocket;
		}

		public void setTradeSocket(TradeSocketBean tradeSocket) {
			this.tradeSocket = tradeSocket;
		}

		public MQTTSocketBean getmQTTSocket() {
			return mQTTSocket;
		}

		public void setmQTTSocket(MQTTSocketBean mQTTSocket) {
			this.mQTTSocket = mQTTSocket;
		}


		public static class MarketSocketBean {
            /**
             * prod :
             * des : 行情socket
             */

            private String host;
            private String prod;
            private String test;
            private String dev;
            private String des;

            public String getHost() {
                return host;
            }

            public void setHost(String prod) {
                this.host = prod;
            }

            public String getProd() {
				return prod;
			}

			public void setProd(String prod) {
				this.prod = prod;
			}

			public String getTest() {
				return test;
			}

			public void setTest(String test) {
				this.test = test;
			}

			public String getDev() {
				return dev;
			}

			public void setDev(String dev) {
				this.dev = dev;
			}

			public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }
        }

        public static class TradeSocketBean {
            /**
             * prod :
             * des : 交易socket
             */

            private String host;
            private String prod;
            private String test;
            private String dev;
            private String des;

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public String getProd() {
				return prod;
			}

			public void setProd(String prod) {
				this.prod = prod;
			}

			public String getTest() {
				return test;
			}

			public void setTest(String test) {
				this.test = test;
			}

			public String getDev() {
				return dev;
			}

			public void setDev(String dev) {
				this.dev = dev;
			}

			public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }
        }

        public static class MQTTSocketBean {
            /**
             * prod : post-cn-4590re2i204.mqtt.aliyuncs.com
             * des : MQTTsocket
             */

            private String host;
            private String prod;
            private String test;
            private String dev;
            private String des;

            public String getHost() {
                return host;
            }

            public void setHost(String prod) {
                this.host = prod;
            }

            public String getProd() {
				return prod;
			}

			public void setProd(String prod) {
				this.prod = prod;
			}

			public String getTest() {
				return test;
			}

			public void setTest(String test) {
				this.test = test;
			}

			public String getDev() {
				return dev;
			}

			public void setDev(String dev) {
				this.dev = dev;
			}

			public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }
        }
    }

    public static class HttpsURLBean {
        /**
         * hkAppURL : {"prod":"https://hkapp.hotcoinex.com/v1/","des":"hkApp地址"}
         * hkWebURL : {"prod":"https://hkapp.hotcoinex.com","des":"hkWeb地址"}
         * OtcURL : {"prod":"https://hkotcapi.hotcoinex.com","des":"OTC地址"}
         * H5URL : {"prod":"https://m.hotcoinex.com","des":"H5地址"}
         * ossServerURL : {"prod":"https://hkossupload.hotcoinex.com/oss_server/upload","des":"ossServer地址"}
         */

        private HkAppURLBean hkAppURL;
        private HkWebURLBean hkWebURL;
        private OtcURLBean otcURL;
        private H5URLBean h5URL;
        private OssServerURLBean ossServerURL;
        private HotCoinURLBean hotCoinURL;

        public HkAppURLBean getHkAppURL() {
            return hkAppURL;
        }

        public void setHkAppURL(HkAppURLBean hkAppURL) {
            this.hkAppURL = hkAppURL;
        }

        public HkWebURLBean getHkWebURL() {
			return hkWebURL;
		}

		public void setHkWebURL(HkWebURLBean hkWebURL) {
			this.hkWebURL = hkWebURL;
		}
		
        public OtcURLBean getOtcURL() {
			return otcURL;
		}

		public void setOtcURL(OtcURLBean otcURL) {
			this.otcURL = otcURL;
		}

		public H5URLBean getH5URL() {
			return h5URL;
		}

		public void setH5URL(H5URLBean h5url) {
			h5URL = h5url;
		}

		public OssServerURLBean getOssServerURL() {
            return ossServerURL;
        }

        public void setOssServerURL(OssServerURLBean ossServerURL) {
            this.ossServerURL = ossServerURL;
        }

        public HotCoinURLBean getHotCoinURL() {
			return hotCoinURL;
		}

		public void setHotCoinURL(HotCoinURLBean hotCoinURL) {
			this.hotCoinURL = hotCoinURL;
		}

		public static class HkAppURLBean {
            /**
             * prod : https://hkapp.hotcoinex.com/v1/
             * des : hkApp地址
             */

            private String host;
            private String prod;
            private String test;
            private String dev;
            private String des;

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public String getProd() {
				return prod;
			}

			public void setProd(String prod) {
				this.prod = prod;
			}

			public String getTest() {
				return test;
			}

			public void setTest(String test) {
				this.test = test;
			}

			public String getDev() {
				return dev;
			}

			public void setDev(String dev) {
				this.dev = dev;
			}

			public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }
        }

        public static class HkWebURLBean {
            /**
             * prod : https://hkapp.hotcoinex.com
             * des : hkWeb地址
             */

            private String host;
            private String prod;
            private String test;
            private String dev;
            private String des;

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public String getProd() {
				return prod;
			}

			public void setProd(String prod) {
				this.prod = prod;
			}

			public String getTest() {
				return test;
			}

			public void setTest(String test) {
				this.test = test;
			}

			public String getDev() {
				return dev;
			}

			public void setDev(String dev) {
				this.dev = dev;
			}

			public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }
        }

        public static class OtcURLBean {
            /**
             * prod : https://hkotcapi.hotcoinex.com
             * des : OTC地址
             */

            private String host;
            private String prod;
            private String test;
            private String dev;
            private String des;

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public String getProd() {
				return prod;
			}

			public void setProd(String prod) {
				this.prod = prod;
			}

			public String getTest() {
				return test;
			}

			public void setTest(String test) {
				this.test = test;
			}

			public String getDev() {
				return dev;
			}

			public void setDev(String dev) {
				this.dev = dev;
			}

			public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }
        }

        public static class H5URLBean {
            /**
             * prod : https://m.hotcoinex.com
             * des : H5地址
             */

            private String host;
            private String prod;
            private String test;
            private String dev;
            private String des;

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public String getProd() {
				return prod;
			}

			public void setProd(String prod) {
				this.prod = prod;
			}

			public String getTest() {
				return test;
			}

			public void setTest(String test) {
				this.test = test;
			}

			public String getDev() {
				return dev;
			}

			public void setDev(String dev) {
				this.dev = dev;
			}

			public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }
        }

        public static class OssServerURLBean {
            /**
             * prod : https://hkossupload.hotcoinex.com/oss_server/upload
             * des : ossServer地址
             */

            private String host;
            private String prod;
            private String test;
            private String dev;
            private String des;

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public String getProd() {
				return prod;
			}

			public void setProd(String prod) {
				this.prod = prod;
			}

			public String getTest() {
				return test;
			}

			public void setTest(String test) {
				this.test = test;
			}

			public String getDev() {
				return dev;
			}

			public void setDev(String dev) {
				this.dev = dev;
			}

			public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }
        }
        
        public static class HotCoinURLBean {
        	/**
        	 *  host: https://hktest.hotcoinex.com
             *	des : HotCoin地址
        	 */
        	
            private String host;
            private String prod;
            private String test;
            private String dev;
            private String des;

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public String getProd() {
				return prod;
			}

			public void setProd(String prod) {
				this.prod = prod;
			}

			public String getTest() {
				return test;
			}

			public void setTest(String test) {
				this.test = test;
			}

			public String getDev() {
				return dev;
			}

			public void setDev(String dev) {
				this.dev = dev;
			}

			public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }
        } 
    }
    
    public static class HttpsDataBean {
    	
    	private NoticeBean notice;
    	private HomeRankListBean homeRankList;
    	private StarCoinListBean starCoinList;
    	private UserModifyPhontoBean userModifyPhonto;
    	private UserSetNickNameBean userSetNickName;
    	private UserSafeCenterBean userSafeCenter;
    	private UserBindPhoneBean userBindPhone;
    	private GoogleAuthBean googleAuth;
    	private SafetyVerifyBean safetyVerify;
    	private AuthorChineseBean authorChinese;
    	private UpdateVersionBean updateVersion;
    	private EmailBindCodeBean emailBindCode;
    	private EmailBindBean emailBind;
    	private UserAuthenticationBean userAuthentication;
    	private UpdatePhoneCheckBean updatePhoneCheck;
    	private UpdatePhoneBean updatePhone;
    	private VerificationPasswordBean verificationPassword;
    	private ModifyOtherLoginBean modifyOtherLogin;
    	private SetFishCodeBean setFishCode;
    	private ModifyFishCodeBean modifyFishCode;
    	private CurrencyBean currency;
    	private UserInfoBean UserInfo;
    	private LogoutBean logout;
    	private TradeInfoBean tradeInfo;
    	private UpdateConfigBean updateConfig;
    	
		public NoticeBean getNotice() {
			return notice;
		}

		public void setNotice(NoticeBean notice) {
			this.notice = notice;
		}

		public HomeRankListBean getHomeRankList() {
			return homeRankList;
		}

		public void setHomeRankList(HomeRankListBean homeRankList) {
			this.homeRankList = homeRankList;
		}

		public StarCoinListBean getStarCoinList() {
			return starCoinList;
		}

		public void setStarCoinList(StarCoinListBean starCoinList) {
			this.starCoinList = starCoinList;
		}

		public UserModifyPhontoBean getUserModifyPhonto() {
			return userModifyPhonto;
		}

		public void setUserModifyPhonto(UserModifyPhontoBean userModifyPhonto) {
			this.userModifyPhonto = userModifyPhonto;
		}

		public UserSetNickNameBean getUserSetNickName() {
			return userSetNickName;
		}

		public void setUserSetNickName(UserSetNickNameBean userSetNickName) {
			this.userSetNickName = userSetNickName;
		}

		public UserSafeCenterBean getUserSafeCenter() {
			return userSafeCenter;
		}

		public void setUserSafeCenter(UserSafeCenterBean userSafeCenter) {
			this.userSafeCenter = userSafeCenter;
		}

		public UserBindPhoneBean getUserBindPhone() {
			return userBindPhone;
		}

		public void setUserBindPhone(UserBindPhoneBean userBindPhone) {
			this.userBindPhone = userBindPhone;
		}

		public GoogleAuthBean getGoogleAuth() {
			return googleAuth;
		}

		public void setGoogleAuth(GoogleAuthBean googleAuth) {
			this.googleAuth = googleAuth;
		}

		public SafetyVerifyBean getSafetyVerify() {
			return safetyVerify;
		}

		public void setSafetyVerify(SafetyVerifyBean safetyVerify) {
			this.safetyVerify = safetyVerify;
		}

		public AuthorChineseBean getAuthorChinese() {
			return authorChinese;
		}

		public void setAuthorChinese(AuthorChineseBean authorChinese) {
			this.authorChinese = authorChinese;
		}

		public UpdateVersionBean getUpdateVersion() {
			return updateVersion;
		}

		public void setUpdateVersion(UpdateVersionBean updateVersion) {
			this.updateVersion = updateVersion;
		}

		public EmailBindCodeBean getEmailBindCode() {
			return emailBindCode;
		}

		public void setEmailBindCode(EmailBindCodeBean emailBindCode) {
			this.emailBindCode = emailBindCode;
		}

		public EmailBindBean getEmailBind() {
			return emailBind;
		}

		public void setEmailBind(EmailBindBean emailBind) {
			this.emailBind = emailBind;
		}

		public UserAuthenticationBean getUserAuthentication() {
			return userAuthentication;
		}

		public void setUserAuthentication(UserAuthenticationBean userAuthentication) {
			this.userAuthentication = userAuthentication;
		}

		public UpdatePhoneCheckBean getUpdatePhoneCheck() {
			return updatePhoneCheck;
		}

		public void setUpdatePhoneCheck(UpdatePhoneCheckBean updatePhoneCheck) {
			this.updatePhoneCheck = updatePhoneCheck;
		}

		public UpdatePhoneBean getUpdatePhone() {
			return updatePhone;
		}

		public void setUpdatePhone(UpdatePhoneBean updatePhone) {
			this.updatePhone = updatePhone;
		}

		public VerificationPasswordBean getVerificationPassword() {
			return verificationPassword;
		}

		public void setVerificationPassword(VerificationPasswordBean verificationPassword) {
			this.verificationPassword = verificationPassword;
		}

		public ModifyOtherLoginBean getModifyOtherLogin() {
			return modifyOtherLogin;
		}

		public void setModifyOtherLogin(ModifyOtherLoginBean modifyOtherLogin) {
			this.modifyOtherLogin = modifyOtherLogin;
		}

		public SetFishCodeBean getSetFishCode() {
			return setFishCode;
		}

		public void setSetFishCode(SetFishCodeBean setFishCode) {
			this.setFishCode = setFishCode;
		}

		public ModifyFishCodeBean getModifyFishCode() {
			return modifyFishCode;
		}

		public void setModifyFishCode(ModifyFishCodeBean modifyFishCode) {
			this.modifyFishCode = modifyFishCode;
		}

		public CurrencyBean getCurrency() {
			return currency;
		}

		public void setCurrency(CurrencyBean currency) {
			this.currency = currency;
		}

		public UserInfoBean getUserInfo() {
			return UserInfo;
		}

		public void setUserInfo(UserInfoBean userInfo) {
			UserInfo = userInfo;
		}

		public LogoutBean getLogout() {
			return logout;
		}

		public void setLogout(LogoutBean logout) {
			this.logout = logout;
		}

		public TradeInfoBean getTradeInfo() {
			return tradeInfo;
		}

		public void setTradeInfo(TradeInfoBean tradeInfo) {
			this.tradeInfo = tradeInfo;
		}

		public UpdateConfigBean getUpdateConfig() {
			return updateConfig;
		}

		public void setUpdateConfig(UpdateConfigBean updateConfig) {
			this.updateConfig = updateConfig;
		}

		public static class NoticeBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class HomeRankListBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class StarCoinListBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class UserModifyPhontoBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class UserSetNickNameBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class UserSafeCenterBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class UserBindPhoneBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class GoogleAuthBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class SafetyVerifyBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class AuthorChineseBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class UpdateVersionBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class EmailBindCodeBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class EmailBindBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class UserAuthenticationBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class UpdatePhoneCheckBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class UpdatePhoneBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class VerificationPasswordBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class ModifyOtherLoginBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class SetFishCodeBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class ModifyFishCodeBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class CurrencyBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class UserInfoBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class LogoutBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class TradeInfoBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    	
    	public static class UpdateConfigBean {
    		private String des;
    		private String type;
    		private String dir;
    		private String[] param;
    		
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getDir() {
				return dir;
			}
			public void setDir(String dir) {
				this.dir = dir;
			}
			public String[] getParam() {
				return param;
			}
			public void setParam(String[] param) {
				this.param = param;
			}
    	}
    }

    public static class PersonListBean {
        /**
         * menuId : 1
         * title : 委单管理
         * des : 委单管理
         * iosImage :
         * andriodImage :
         * webImage :
         * sourceImage :
         * hasNeed : true
         */

        private int menuId;
        private String title;
        private String des;
        private String image;
        private String sourceImage;
        private boolean isNet;
        private boolean hasNeed;

        public int getMenuId() {
            return menuId;
        }

        public void setMenuId(int menuId) {
            this.menuId = menuId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public boolean isNet() {
			return isNet;
		}

		public void setNet(boolean isNet) {
			this.isNet = isNet;
		}

		public String getSourceImage() {
            return sourceImage;
        }

        public void setSourceImage(String sourceImage) {
            this.sourceImage = sourceImage;
        }

        public boolean isHasNeed() {
            return hasNeed;
        }

        public void setHasNeed(boolean hasNeed) {
            this.hasNeed = hasNeed;
        }
    }

    public static class TabBarListBean {
        /**
         * menuId : 1
         * title : 首页
         * des : 首页
         * iosImage :
         * andriodImage :
         * sourceImage :
         * sourceImageSel :
         * hasNeed : true
         */

        private int menuId;
        private String title;
        private String des;
        private String image;
        private String sourceImage;
        private String sourceImageSel;
        private boolean isNet;
        private boolean hasNeed;

        public int getMenuId() {
            return menuId;
        }

        public void setMenuId(int menuId) {
            this.menuId = menuId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public boolean isNet() {
			return isNet;
		}

		public void setNet(boolean isNet) {
			this.isNet = isNet;
		}

		public String getSourceImage() {
            return sourceImage;
        }

        public void setSourceImage(String sourceImage) {
            this.sourceImage = sourceImage;
        }

        public String getSourceImageSel() {
            return sourceImageSel;
        }

        public void setSourceImageSel(String sourceImageSel) {
            this.sourceImageSel = sourceImageSel;
        }

        public boolean isHasNeed() {
            return hasNeed;
        }

        public void setHasNeed(boolean hasNeed) {
            this.hasNeed = hasNeed;
        }
    }

    public static class TradingArea {
    	/**
         * code : 3
         * name : ETH
         * topic: HOTCOIN_TOPIC_ETH_TRADE_TEST
         */

        private int code;
        private String name;
        private String topic;
        private boolean hasNeed;

        public boolean isHasNeed() {
            return hasNeed;
        }

        public void setHasNeed(boolean hasNeed) {
            this.hasNeed = hasNeed;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    
    public static class SystemLanguageBean {
        /**
         * title :
         * des : 语言设置
         * hasNeed : true
         * group : [{"menuId":1,"title":"zh_CN","des":"中文简体","hasNeed":true},{"menuId":2,"title":"en","des":"English","hasNeed":true},{"menuId":3,"title":"ko","des":"한국어","hasNeed":true}]
         */

        private String title;
        private String des;
        private boolean hasNeed;
        private List<GroupBean> group;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public boolean isHasNeed() {
            return hasNeed;
        }

        public void setHasNeed(boolean hasNeed) {
            this.hasNeed = hasNeed;
        }

        public List<GroupBean> getGroup() {
            return group;
        }

        public void setGroup(List<GroupBean> group) {
            this.group = group;
        }

        public static class GroupBean {
            /**
             * menuId : 1
             * title : zh_CN
             * des : 中文简体
             * hasNeed : true
             */

            private int menuId;
            private String title;
            private String des;
            private boolean hasNeed;

            public int getMenuId() {
                return menuId;
            }

            public void setMenuId(int menuId) {
                this.menuId = menuId;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }

            public boolean isHasNeed() {
                return hasNeed;
            }

            public void setHasNeed(boolean hasNeed) {
                this.hasNeed = hasNeed;
            }
        }
    }

}

