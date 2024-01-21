# otc_api

1-10 oct广告相关接口

1. POST /otc/common/tradeIndex
    resquest params(query): 
		side: SELL        required = true     string
		coinId: 1   	  required = true     int
		currentPage: 1    required = false    int
		numPerPage: 10    required = false    int
		currencyId: 1     required = false    int
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_244ECAB0A7C242A3B99DA667EC489953  required = true
		platform: 1       required = false
	  
	  response:
  {
    "code": 200,
    "msg": "成功",
    "time": 1556187898573,
    "data": {
        "total": 1,
        "page": 1,
        "list": [
             {
                "id": 400038,
                "userId": 1100016,
                "status": 1,          //广告状态 1 上架中，2 交易中，3 过期，4 下架中
                "isFrozen": 0,        //是否冻结 0 否 1 是
                "side": "string",     // BUY |SELL
                "coinId": 1,       // 交易币种id
                "currencyId": 1,   // 支付货币id
                "priceType": 1,    // 价格类型 1 浮动价格，2 固定价格
                "floatMarket": 1,  // 浮动市场 1 平均 2 币安 3 火币
                "priceRate": 1.0000000000,  // 价格比例，报价=市场价*价格比例
                "acceptablePrice": 0E-10,   // 可接受价格
                "fixedPrice": 0E-10,        // 固定价格
                "volume": 100.0000,			// 总数量
                "visiableVolume": 100.0000, // 可用数量
                "tradingVolume": 0.0000,    // 成交数量
                "frozenVolume": 0.0000,     //冻结数量 
                "feeRate": 0E-10,			// 费率
                "bankinfoFirstId": 1,       //支付方式1
                "bankinfoSecondId": 0,  //支付方式2
                "bankinfoThirdId": 0,   //支付方式3
                "minAmount": 1.0000000000, // 最小限额
                "maxAmount": 20.0000000000, // 最大限额
                "maxPaytime": 15,  			// 最大付款时间
                "description": "string",          //广告说明
                "greetings": "string",           //问候语
                "tag": "string",					//结束语
                "maxProcessing": 0,      // 最大处理订单数
                "successCount": 0,       // 必须成交次数
                "createTime": 1556186533000, // 创建时间
                "overdueTime": 1557396133000, // 失效时间
                "updateTime": 1556186533000,  // 更新时间
                "bankinfoType": 0,        // 支付方式
                "amount": 0,             // 额度
                "nickname": "string",    // 用户昵称
                "photo": string,   		// 用户头像
                "cmpOrders": 0,         //交易笔数
                "applauseRate": 0,   // 好评率
                "price": 1.00,       // 广告价格
                "coinName": "BTC",  //交易币种名称
                "currencyName": "CNY",// 法币名称
                "payIcons": [  // 支付图片
                    "string"
                ],
                "payIds": [0] array,  // 支付id
                "pay": [
                {
                    "id": 1,
                    "chineseName": "string",  //支付名称
                    "picture": "string",      // 支付图标
                    "status": int,            //状态
                    "statusString": "string",  //状态
                    "type": 0,                //类型
                    "typeString": "string",   // 类型
                    "englishName": "string",  // 英文简称
                    "createTime": "string"    // 创建时间
                } 
                "coinIcon": "string",   // 币种图标
                "isLimited": 0,  // 是否受限
                "premiumRate": 0,  // 溢价比例
                "currencyChineseName": "string"  //法币中文名称
            }
          
        ]
        ]
    }
}


2. POST /otc/common/merchantIndex.html

   resquest params(query): 
		side: SELL        required = true
		userid: 0   	  required = false
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_244ECAB0A7C242A3B99DA667EC489953  required = true
		platform: 1       required = false
		
		
   response:
	{
    "code": 200,
    "msg": "成功",
    "time": 1556189236222,
    "data": {
        "list": [
             {
                "id": 400038,
                "userId": 1100016,
                "status": 1,          //广告状态 1 上架中，2 交易中，3 过期，4 下架中
                "isFrozen": 0,        //是否冻结 0 否 1 是
                "side": "string",     // BUY |SELL
                "coinId": 1,       // 交易币种id
                "currencyId": 1,   // 支付货币id
                "priceType": 1,    // 价格类型 1 浮动价格，2 固定价格
                "floatMarket": 1,  // 浮动市场 1 平均 2 币安 3 火币
                "priceRate": 1.0000000000,  // 价格比例，报价=市场价*价格比例
                "acceptablePrice": 0E-10,   // 可接受价格
                "fixedPrice": 0E-10,        // 固定价格
                "volume": 100.0000,			// 总数量
                "visiableVolume": 100.0000, // 可用数量
                "tradingVolume": 0.0000,    // 成交数量
                "frozenVolume": 0.0000,     //冻结数量 
                "feeRate": 0E-10,			// 费率
                "bankinfoFirstId": 1,       //支付方式1
                "bankinfoSecondId": 0,  //支付方式2
                "bankinfoThirdId": 0,   //支付方式3
                "minAmount": 1.0000000000, // 最小限额
                "maxAmount": 20.0000000000, // 最大限额
                "maxPaytime": 15,  			// 最大付款时间
                "description": "string",          //广告说明
                "greetings": "string",           //问候语
                "tag": "string",					//结束语
                "maxProcessing": 0,      // 最大处理订单数
                "successCount": 0,       // 必须成交次数
                "createTime": 1556186533000, // 创建时间
                "overdueTime": 1557396133000, // 失效时间
                "updateTime": 1556186533000,  // 更新时间
                "bankinfoType": 0,        // 支付方式
                "amount": 0,             // 额度
                "nickname": "string",    // 用户昵称
                "photo": string,   		// 用户头像
                "cmpOrders": 0,         //交易笔数
                "applauseRate": 0,   // 好评率
                "price": 1.00,       // 广告价格
                "coinName": "BTC",  //交易币种名称
                "currencyName": "CNY",// 法币名称
                "payIcons": [  // 支付图片
                    "https://hotcoin-hk-static.oss-cn-hongkong.aliyuncs.com/hotcoin/upload/bankIcon/Bank.png"
                ],
                "payIds": [0] array,  // 支付id
                "pay": [
                {
                    "id": 1,
                    "chineseName": "string",  //支付名称
                    "picture": "string",      // 支付图标
                    "status": 0,            //状态
                    "statusString": "string",  //状态
                    "type": 0,                //类型
                    "typeString": "string",   // 类型
                    "englishName": "string",  // 英文简称
                    "createTime": "string"    // 创建时间
                } 
                "coinIcon": "string",   // 币种图标
                "isLimited": 0,  // 是否受限
                "premiumRate": 0,  // 溢价比例
                "currencyChineseName": "string"  //法币中文名称
            }
          
        ]
    }
}
     

3. post /otc/common/manageIndex.html
 
   resquest params(query): 
		side: SELL        required = true
		currentPage: 0    required = true
		currencyId: 0    required = false
		coinId: 0   	  required = false
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_244ECAB0A7C242A3B99DA667EC489953  required = true
		platform: 1       required = false
		
		
response:	
		{
    "code": 200,
    "msg": "成功",
    "time": 1556189394762,
    "data": {
        "total": 30,
        "page": 1,
        "list": [
             {
                "id": 400038,
                "userId": 1100016,
                "status": 1,          //广告状态 1 上架中，2 交易中，3 过期，4 下架中
                "isFrozen": 0,        //是否冻结 0 否 1 是
                "side": "string",     // BUY |SELL
                "coinId": 1,       // 交易币种id
                "currencyId": 1,   // 支付货币id
                "priceType": 1,    // 价格类型 1 浮动价格，2 固定价格
                "floatMarket": 1,  // 浮动市场 1 平均 2 币安 3 火币
                "priceRate": 1.0000000000,  // 价格比例，报价=市场价*价格比例
                "acceptablePrice": 0E-10,   // 可接受价格
                "fixedPrice": 0E-10,        // 固定价格
                "volume": 100.0000,			// 总数量
                "visiableVolume": 100.0000, // 可用数量
                "tradingVolume": 0.0000,    // 成交数量
                "frozenVolume": 0.0000,     //冻结数量 
                "feeRate": 0E-10,			// 费率
                "bankinfoFirstId": 1,       //支付方式1
                "bankinfoSecondId": 0,  //支付方式2
                "bankinfoThirdId": 0,   //支付方式3
                "minAmount": 1.0000000000, // 最小限额
                "maxAmount": 20.0000000000, // 最大限额
                "maxPaytime": 15,  			// 最大付款时间
                "description": "string",          //广告说明
                "greetings": "string",           //问候语
                "tag": "string",					//结束语
                "maxProcessing": 0,      // 最大处理订单数
                "successCount": 0,       // 必须成交次数
                "createTime": 1556186533000, // 创建时间
                "overdueTime": 1557396133000, // 失效时间
                "updateTime": 1556186533000,  // 更新时间
                "bankinfoType": 0,        // 支付方式
                "amount": 0,             // 额度
                "nickname": "string",    // 用户昵称
                "photo": string,   		// 用户头像
                "cmpOrders": 0,         //交易笔数
                "applauseRate": 0,   // 好评率
                "price": 1.00,       // 广告价格
                "coinName": "BTC",  //交易币种名称
                "currencyName": "CNY",// 法币名称
                "payIcons": [  // 支付图片
                    "https://hotcoin-hk-static.oss-cn-hongkong.aliyuncs.com/hotcoin/upload/bankIcon/Bank.png"
                ],
                "payIds": [0],  // 支付id
                "pay": "string",     // 支付信息
                "coinIcon": "string",   // 币种图标
                "isLimited": 0,  // 是否受限
                "premiumRate": 0,  // 溢价比例
                "currencyChineseName": "string"  //法币中文名称
            }
           
        ]
    }
}


4. post /otc/common/checkAdvert.html 

   resquest params(query): 
		id : 0	required=true
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_244ECAB0A7C242A3B99DA667EC489953  required = true
		platform: 1       required = false
		
	response:	
	{
    "code": 200,
    "msg": "成功",
    "time": 1556189712753,
    "data": {
        "amountDigit": 4,
        "countDigit": 4,
        "advert": {
                "id": 400038,
                "userId": 1100016,
                "status": 1,          //广告状态 1 上架中，2 交易中，3 过期，4 下架中
                "isFrozen": 0,        //是否冻结 0 否 1 是
                "side": "string",     // BUY |SELL
                "coinId": 1,       // 交易币种id
                "currencyId": 1,   // 支付货币id
                "priceType": 1,    // 价格类型 1 浮动价格，2 固定价格
                "floatMarket": 1,  // 浮动市场 1 平均 2 币安 3 火币
                "priceRate": 1.0000000000,  // 价格比例，报价=市场价*价格比例
                "acceptablePrice": 0E-10,   // 可接受价格
                "fixedPrice": 0E-10,        // 固定价格
                "volume": 100.0000,			// 总数量
                "visiableVolume": 100.0000, // 可用数量
                "tradingVolume": 0.0000,    // 成交数量
                "frozenVolume": 0.0000,     //冻结数量 
                "feeRate": 0E-10,			// 费率
                "bankinfoFirstId": 1,       //支付方式1
                "bankinfoSecondId": 0,  //支付方式2
                "bankinfoThirdId": 0,   //支付方式3
                "minAmount": 1.0000000000, // 最小限额
                "maxAmount": 20.0000000000, // 最大限额
                "maxPaytime": 15,  			// 最大付款时间
                "description": "string",          //广告说明
                "greetings": "string",           //问候语
                "tag": "string",					//结束语
                "maxProcessing": 0,      // 最大处理订单数
                "successCount": 0,       // 必须成交次数
                "createTime": 1556186533000, // 创建时间
                "overdueTime": 1557396133000, // 失效时间
                "updateTime": 1556186533000,  // 更新时间
                "bankinfoType": 0,        // 支付方式
                "amount": 0,             // 额度
                "nickname": "string",    // 用户昵称
                "photo": string,   		// 用户头像
                "cmpOrders": 0,         //交易笔数
                "applauseRate": 0,   // 好评率
                "price": 1.00,       // 广告价格
                "coinName": "BTC",  //交易币种名称
                "currencyName": "CNY",// 法币名称
                "payIcons": [  // 支付图片
                    "https://hotcoin-hk-static.oss-cn-hongkong.aliyuncs.com/hotcoin/upload/bankIcon/Bank.png"
                ],
                "payIds": [0] array,  // 支付id
                "pay": [
                {
                    "id": 1,
                    "chineseName": "string",  //支付名称
                    "picture": "string",      // 支付图标
                    "status": int,            //状态
                    "statusString": "string",  //状态
                    "type": 0,                //类型
                    "typeString": "string",   // 类型
                    "englishName": "string",  // 英文简称
                    "createTime": "string"    // 创建时间
                } 
                "coinIcon": "string",   // 币种图标
                "isLimited": 0,  // 是否受限
                "premiumRate": 0,  // 溢价比例
                "currencyChineseName": "string"  //法币中文名称
            },
        "otcAccountBalance": 201.5250600000      // 账户余额
    }
}



5. POST /otc/common/releaseIndex.index 查看广告
    resquest params(query):
	
    coinId: 1				required = true
	currencyId: 1			required = true
	token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
	platform: 1				required = true

	response :
	{
		"code": 200,
		"msg": "成功",
		"time": 1556242126839,
		"data": {
			"existenceTime": 14,
			"marketPrice": [
				1.00,
				1.00,
				1.00
			],
			"maxBuyPrice": 1.00,
			"minTradeAmount": 500,
			"minSellPrice": 100.00,
			"accountBalance": 0.0000,
			"feeRate": "0.0%"
		}
}

6.post /otc/common/updateAdvert.html 
     
   resquest params(query): 
		side: "string"				required = true
		coinId: 1     				required = true
		currencyId: 1       		required = false
		maxProcessing: 9    		required = false
		successCount: 0     		required = false
		priceType: 1        		required = false
		premiumRate: 12     		required = false
		bankinfoIds: [1,2,3]  		required = false
		floatMarket: 3      		required = false
		fixedPrice: 0       		required = false
		description: "string"  		required = false
		note:     "string"          required = false
		volume: 3           		required = false
		minAmount: 0      			required = false
		maxAmount: 0  		 		required = false
		greetings: "string"     	required = false
		tag: "string"           	required = false
		acceptablePrice: 0 			required = false
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_A6EBC0BE16514A19BC79FEA7A2C173D2  required = true
		platform: 1  required = false
   
response:	
	{
    "code": 200,
    "msg": "成功",
    "time": 1556258044909,
    "data": {
        "marketPrice": [
            1.0000,
            1.0000,
            1.0000
        ],
        "maxBuyPrice": 0.0000,
        "minSellPrice": 0.0000,
        "advert": {
            "id": 400038,
                "userId": 1100016,
                "status": 1,          //广告状态 1 上架中，2 交易中，3 过期，4 下架中
                "isFrozen": 0,        //是否冻结 0 否 1 是
                "side": "string",     // BUY |SELL
                "coinId": 1,       // 交易币种id
                "currencyId": 1,   // 支付货币id
                "priceType": 1,    // 价格类型 1 浮动价格，2 固定价格
                "floatMarket": 1,  // 浮动市场 1 平均 2 币安 3 火币
                "priceRate": 1.0000000000,  // 价格比例，报价=市场价*价格比例
                "acceptablePrice": 0E-10,   // 可接受价格
                "fixedPrice": 0E-10,        // 固定价格
                "volume": 100.0000,			// 总数量
                "visiableVolume": 100.0000, // 可用数量
                "tradingVolume": 0.0000,    // 成交数量
                "frozenVolume": 0.0000,     //冻结数量 
                "feeRate": 0E-10,			// 费率
                "bankinfoFirstId": 1,       //支付方式1
                "bankinfoSecondId": 0,  //支付方式2
                "bankinfoThirdId": 0,   //支付方式3
                "minAmount": 1.0000000000, // 最小限额
                "maxAmount": 20.0000000000, // 最大限额
                "maxPaytime": 15,  			// 最大付款时间
                "description": "string",          //广告说明
                "greetings": "string",           //问候语
                "tag": "string",					//结束语
                "maxProcessing": 0,      // 最大处理订单数
                "successCount": 0,       // 必须成交次数
                "createTime": 1556186533000, // 创建时间
                "overdueTime": 1557396133000, // 失效时间
                "updateTime": 1556186533000,  // 更新时间
                "bankinfoType": 0,        // 支付方式
                "amount": 0,             // 额度
                "nickname": "string",    // 用户昵称
                "photo": string,   		// 用户头像
                "cmpOrders": 0,         //交易笔数
                "applauseRate": 0,   // 好评率
                "price": 1.00,       // 广告价格
                "coinName": "BTC",  //交易币种名称
                "currencyName": "CNY",// 法币名称
                "payIcons": [  // 支付图片
                    "https://hotcoin-hk-static.oss-cn-hongkong.aliyuncs.com/hotcoin/upload/bankIcon/Bank.png"
                ],
                "payIds": [0],  // 支付id
                "pay": [
                {
                    "id": 1,
                    "chineseName": "string",  //支付名称
                    "picture": "string",      // 支付图标
                    "status": int,            //状态
                    "statusString": "string",  //状态
                    "type": 0,                //类型
                    "typeString": "string",   // 类型
                    "englishName": "string",  // 英文简称
                    "createTime": "string"    // 创建时间
                } 
                "coinIcon": "string",   // 币种图标
                "isLimited": 0,  // 是否受限
                "premiumRate": 0,  // 溢价比例
                "currencyChineseName": "string"  //法币中文名称
            },
        "accountBalance": 201.5250  //账户余额
    }
}
	
	
7. post /otc/common/userPaymentList.html  // 支付列表

	resquest params(query): 
	
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_A6EBC0BE16514A19BC79FEA7A2C173D2  required = true
		platform: 1  required = false
		
		response:	
		{
			"code": 200,
			"msg": "成功",
			"time": 1556190582991,
			"data": {
				"list": [
					{
						"id": 1,
						"chineseName": "string",  //支付名称
						"picture": "string",      // 支付图标
						"status": int,            //状态
						"statusString": "string",  //状态
						"type": 0,                //类型
						"typeString": "string",   // 类型
						"englishName": "string",  // 英文简称
						"createTime": "string"    // 创建时间
					}
				]}}

8. post /otc/common/releaseAdvert.html  发布广告
   resquest params(query): 
		id: 200046         required = false			Int
		side: SELL		   required = true          string
		coinId: 52         required = true			Int
		currencyId: 2      required = true			Int
		priceType: 1	   required = true    		Int
		floatMarket: 3     required = false         Int
		premiumRate: 37384 required = false         Int
		acceptablePrice: 1 required = false         Int
		fixedPrice:        required = false         Int
		volume: 1          required = true			Int
		bankinfoIds        required = true          string
		minAmount: 2	   required = true			Int
		minAmount: 0 	   required = true			Int
		description:       required = false         string
		note:  		   	   required = false			string
		greetings:         required = false			string
		tag:               required = false			string
		maxProcessing:     required = false   		int
		successCount: 1    required = true			int
	    token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B
	    platform: 1
	  
   response:
   
   {
		"code": 200,
		"msg": "ok",
		"time": 1556258387808,
		"data": null
   }
   
9.  post /otc/common/putOffAdvert.html 下架广告
      resquest params(query): 
	  
		  id :0   required = true
		  token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
		  platform: 1			required = true
		  
		 response:	 
	  {
		"code": 200,
		"msg": "成功",
		"time": 1556266458178,
		"data": {
			
		}
}
10.post /otc/common/activityAdvert.html 激活广告
	resquest params(query): 
	
 id: 100047		int 	required = true
	token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
	platform: 1			required = true
	
 response:
	{
		"code": 200,
		"msg": "成功",
		"time": 1556257964344,
		"data": {
			
		}
	}
	
11. POST /otc/common/systemArgs  系统参数

    resquest params(query): 
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_244ECAB0A7C242A3B99DA667EC489953  required = true
		platform: 1   required = false
    
	response:
	
    {
    "code": 200,
    "msg": "成功",
    "time": 1556183645896,
    "data": {
        "coinTypeList": [
            {
                "id": 1,
                "name": "string",        
                "type": 2, 
                "shortname": "string",   // 简称
                "weblogo": "https://hotcoin-hk-static.oss-cn-hongkong.aliyuncs.com/hotcoin/upload/coin/7c9b159170d149aca00458caccdeb6b5BTC.png",
                "applogo": "https://hotcoin-hk-static.oss-cn-hongkong.aliyuncs.com/hotcoin/upload/coin/7c9b159170d149aca00458caccdeb6b5BTC.png",
                "symbol": "string",
                "isWithdraw": true,   // 是否提现
                "isRecharge": true,   //是否充值
                "status": 1,         // 状态
                "confirmations": 1,  // 充值到账确认数
                "isUseMemo": false, // 是否使用地址标签
                "countDigit": 4,  // 交易对的数量精度
                "amountDigit": 2, // 交易对的金额精度
                "withdraw": true, // 提现
                "recharge": true  //  充值
            }
        ],
        "currencyList": [
            {
                "id": 2,
                "chineseName": "美元", 
                "englishName": "USD",
                "currencyCode": "USD",
                "status": 1,
                "createTime": 1544086362000
            }
        ],
        "paymentList": [
            {
						"id": 1,
						"chineseName": "string",  //支付名称
						"picture": "string",      // 支付图标
						"status": int,            //状态
						"statusString": "string",  //状态
						"type": 0,                //类型
						"typeString": "string",   // 类型
						"englishName": "string",  // 英文简称
						"createTime": "string"    // 创建时间
            }
        ]
    }
}

12. POST  /otc/order.html otc下单
 resquest params(query): 
 
	adId: 400038         required = true
	totalAmount: 20		 required = true
	payment: 1			 required = true
	token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
	platform: 1          required = false
	
response:
  

13. POST /otc/payOrder.html  //支付订单

    resquest params(query):
		adId: 400038    required = true   // 广告id
		totalAmount: 1  required = true   // 金额
		payment: 1      required = true   // 支付类型
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_FEDB2578A96141FB934D264AC675AAB6  required = true
		platform: 1     required = false
	
	response: 
	{
		"code": 200,
		"msg": "成功",
		"time": 1556261329126,
		"data": {
			"orderId": 700210,
			"type": "BUY"   
		}
    }
	
14. POST /otc/confirmOrder.html  //确认订单
    
	resquest params(query):
	
	orderId:  0    int  required=true 
	token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_FEDB2578A96141FB934D264AC675AAB6  required = true
	platform: 1     required = false
	
	response: 
	{
		"code": 200,
		"msg": "成功",
		"time": 1556262207657,
		"data": ""
	}
	
15  POST /otc/cancelOrder.html  //取消订单
	resquest params(query):
	
orderId:  0    int  required=true 
	token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_FEDB2578A96141FB934D264AC675AAB6  required = true
	platform: 1     required = false
	
 response: 
	{
		"code": 200,
		"msg": "成功",
		"time": 1556262207657,
		"data": ""
	}
	
	
16 POST /otc/orderDetail.html  //订单详情
	resquest params(query): 
	
orderId: 600112   	required = true
	token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B required = true
	platform: 1         required = false
	
response: 
	{
    "code": 200,
    "msg": "成功",
    "time": 1556244758875,
    "data": {
        "adUser": {
            "nickname": "18093025855",
            "adUserId": 900734,
            "photo": "string",
            "btcAmount": 0.01428600,
            "cmpOrders": 1
        },
        "note": "string",
        "currencyName": "CNY",
        "appealUserId": "string",
        "description": "string",
        "payment": {
            "bankNumber": "string",
            "englishName": "string",
            "address": "string",
            "chineseName": "string",
            "bankName": "string",
            "qrcodeImg": "string",
            "type": 1,
            "picture": "string",
            "realname": "string"
        },
        "coinName": "GAVC", 
        "order": {
            "userEvaluation": false,
            "userEvaluationType": 0,
            "adUserEvaluation": false,
            "buyerId": 900734,
            "extendTime": false,
            "limitTime": 1554989824000,
            "statusStringColor": "black",
            "sellerId": 37384,
            "price": 1.0000,
            "adUserId": 37384,
            "payment": 1,
            "id": 600112,
            "amount": 10.00000000,
            "orderNo": "1116330572315234304",
            "adFee": 0.0000,  //广告手续费
            "adUserEvaluationType": 0,
            "maxPaytime": 15,
            "updateTime": 1554988923000,
            "userId": 900734,
            "totalAmount": 10.0000,
            "adId": 100040,
            "createTime": 1554988923000,
            "statusString": "string",
            "status": 4
        }
    }
}

17.POST /otc/listOrder.html  //订单列表
  resquest params(query): 
	sideType:         required = false
	coinId: 		  required = false
	currencyId: 	  required = false
	status: 		  required = false
	page: 1			  required = false
	token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B
	platform: 1
	
response:
	{
    "code": 200,
    "msg": "成功",
    "time": 1556244990573,
    "data": {
        "pageNum": 1,
        "pageSize": 10,
        "size": 10,
        "orderBy": null,
        "startRow": 1,
        "endRow": 10,
        "total": 89,
        "pages": 9,
        "list": [
            {
                "id": 600112,
                "orderNo": "1116330572315234304",
                "adId": 100040,
                "adFee": 0E-8,
                "userId": 900734,
                "adUserId": 37384,
                "amount": 10.00000000,
                "price": 1.00,
                "totalAmount": 10.00,
                "status": 4,
                "statusString": "string",
                "statusStringColor": "black",
                "remark": "string",
                "limitTime": 1554989824000,
                "createTime": 1554988923000,
                "updateTime": 1554988923000,
                "appealTime": "string",
                "payment": 1,
                "bankInfoId": [1,2],
                "side": "SELL",
                "sideType": "SELL",
                "nickName": "18093025855",
                "coinName": "GAVC",
                "currencyName": "CNY",
                "otcPayment": {
						"id": 1,
						"chineseName": "string",  //支付名称
						"picture": "string",      // 支付图标
						"status": int,            //状态
						"statusString": "string",  //状态
						"type": 0,                //类型
						"typeString": "string",   // 类型
						"englishName": "string",  // 英文简称
						"createTime": "string"    // 创建时间
                },
                "adNickname": "string",      
                "userEvaluationType": 0,
                "adUserEvaluationType": 0,
                "adUserPhoto": "string",
                "buyerId": 900734,
                "sellerId": 37384,
                "userPhoto": "string",
                "extendTime": false,
                "userEvaluation": false,
                "adUserEvaluation": false
            }  
        ],
        "firstPage": 1,
        "prePage": 0,
        "nextPage": 2,
        "lastPage": 8,
        "isFirstPage": true,
        "isLastPage": false,
        "hasPreviousPage": false,
        "hasNextPage": true,
        "navigatePages": 8,
        "navigatepageNums": [
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8
        ]
    }
}

18.POST /otc/countOrder.html  //获取用户未完成订单

resquest params(query): 
	
token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B
	platform: 1
	
 response:
	   {
			"code": 200,
			"msg": "成功",
			"time": 1556262826263,
			"data": 2
		}
	
19.POST /otc/extendOrder.html   //延长订单付款时间

   resquest params(query):
	    orderId: 700212       required = true
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_FEDB2578A96141FB934D264AC675AAB6  required = true
		platform: 1           required = false
	   response:
	   {
			"code": 200,
			"msg": "成功",
			"time": 1556262826263,
			"data": ""
		}
20. POST /otc/appealOrder.html   //申诉订单    
	
	resquest params(query):
	orderId: 1           required = true
	type: 1				 required = true
	content: 1			 required = false
	token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
	platform: 1          required = false
	
	response:
	{
		"code": 200,
		"msg": "成功",
		"time": 1556262580491,
		"data": ""
	}
	
21.POST /otc/appealCancel.html //取消申诉订单

 resquest params(query):
	orderId: 1           required = true
	token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
	platform: 1          required = false
	
 response:
	{
		"code": 200,
		"msg": "成功",
		"time": 1556262623991,
		"data": ""
	}
22.POST /otc/evaluate.html //提交评价
 resquest params(query):
	orderId: 500069      required = true
	type: 2 			 required = true  1.好评 2中评 3.差评
	token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
	platform: 1          required = false
	
 response: 
	{
    "code": 200,
    "msg": "成功",
    "time": 1556246589903,
    "data": ""
	}

23.POST /otc/orderStatus.html //获取订单状态
resquest params(query):
	orderId: 500069      required = true
	token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
	platform: 1          required = false
	
response: 
	{
		"code": 200,
		"msg": "成功",
		"time": 1556246591060,
		"data": 3
	}
	
24. POST /otc/orderUserInfo.html //获取订单聊天信息

	resquest params(query):
		orderId: 500069      required = true
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
		platform: 1          required = false
		
		response: 
			{
		"code": 200,
		"msg": "成功",
		"time": 1556246933545,
		"data": {
			"orderUser": {
				"nickname": "string",
				"photo": "string",
				"id": 37384
			},
			"adUser": {
				"nickname": "18093025855",
				"photo": "string",
				"id": 900734
			}
		}
	}
	
25. POST /otc/otc_user_details.html  获取商户详情

	resquest params(query):
		userId: 37384      required = true
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
		platform: 1 	   required = false
		
		response: 
		
		{
		"code": 200,
		"msg": "成功",
		"time": 1556246924116,
		"data": {
			"id": 112944,
			"userId": 37384,
			"succAmt": 0.0216760000000000,
			"cmpOrders": 25,
			"goodEvaluation": 8,
			"badEvaluation": 0,
			"winAppeal": 0,
			"sumAppeal": 0,
			"otcUserType": 1,
			"otcUserTypeString": "string",
			"nickname": "string",
			"hasrealvalidate": true,  //是否实名认证
			"istelephonebind": true, // 是否绑定手机
			"ismailbind": true,     //是否开启邮箱绑定
			"googlebind": true,    // 是否谷歌绑定
			"photo": "string",
			"registertime": 1531273749000,
			"applauseRate": "100.00%",
			"createTime": 1545999997000,
			"updateTime": 1545999997000
    }
    }

26. POST /otc/currency/userWalletTotal.html  获取用户币币账户余额

	resquest params(query):
		coinId: 37384      required = true
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
		platform: 1 	   required = false
		
		response:  
		
		{
		"code": 200,
		"msg": "成功",
		"time": 1556246933545,
		"data": {
			total :20000
		}
		}
		
27.  POST /otc/currency/balance.html  获取用户otc资产

	resquest params(query):
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
		platform: 1 	   required = false
		
	response:  
		
	{
			"code": 200,
			"msg": "成功",
			"time": 1556248800994,
			"data": {
				"totalAssets": 817.55840000,
				"netAssets": 817.55840000,
				"userWalletList": [
					{
						"id": 1500003,
						"uid": 37384,
						"coinId": 52,
						"total": 201.5250600000,
						"frozen": 0E-10,
						"borrow": 0E-10,
						"ico": 0E-10,
						"gmtCreate": 1546004293000,  //创建时间
						"gmtModified": 1555039956000,  // 修改时间
						"version": 0,   
						"webLogo": "string",
						"isUseMemo": false,
						"loginName": "string",
						"nickName": "string",
						"realName": "string",
						"coinName": "string", 
						"shortName": "string"  //eg. USDT
					}
				],
				"btcAssets": 0.0231606554
			}
}
		
		
28. POST /otc/currency/transfer.html  //用户币币账户余额与otc账户划转

	resquest params(query):
		type: 0            required = true
		amount: 0          required = true
		coinType: 2   	   required = true
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
		platform: 1 	   required = false 
		
		response:
		{
			"code": 200,
			"msg": "成功",
			"time": 1556268934081,
			"data": ""
		}
		
29. POST /otc/currency/transferList.html  用户otc账户划转记录

		resquest params(query):
		type: 1 	       required = true 
		coinId: 1 	       required = true 
		begindate: string  required = false 
		enddate:   string  required = false 
		currentPage: 1 	   required = false 
		pageSize: 10 	   required = false 
		token: BCACCOUNT_LOGIN_249e0710272ffdc8a91dd80b2e24eb2d_57D73D59260A43CA8DDC66A8B0436D0B  required = true
		platform: 1 	   required = false 
	
			
		response:
		{
			"code": 200,
			"msg": "成功",
			"time": 1556268539669,
			"data": {
				"currentPage": 1,
				"list": {
					"totalRows": 1,
					"pageSize": 10,
					"currentPage": 1,
					"totalPages": 1,
					"data": [
						{
							"id": 2100010,
							"amount": 1000.0000000000,
							"userId": 37384,
							"otherUserId": 0,
							"coinId": 1,
							"createTime": 1556261953000,
							"type": 1,
							"version": "string",
							"webLogo": "string",
							"fee": 0,
							"isUseMemo": false,
							"loginName": "string",
							"nickName": "string",
							"realName": "string",
							"coinName": "比特币",
							"shortName": "BTC",
							"amountIn": 0,        //用于资产平衡统计
							"amountOut": 0,      // 用于资产平衡统计
							"amountFrozen": 0,
							"transferName": "string"
						}
					],
					"pagin": "string"
				}
    }
}