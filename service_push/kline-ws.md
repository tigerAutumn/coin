
# hotcoin websocket行情数据API
* WebSocketAPI
   * 订阅K线数据
   * 订阅深度数据
   * 订阅实时交易数据

**服务器地址:bathpath:  ws://apiwebsocket.hotcoin.top**


### 订阅K线数据
---
##### 主题订阅

```
请求地址 /trade/kline   
请求数据 {"sub":"market.$symbol$.kline.$period$"}
```
> **参数说明**

参数  | 数据类型|是否必须|描述|备注
---|---|---|---|---
symbol| String | 是 | 交易代码|btc_gavc
period | String| 是 |k线周期|eg:1m,5m,15m,30m,1h,1d,1w,1mo

```
 例如：
    $symbol为某个交易对，如btc_usdt
    数据请求格式: /trade/kline?sub=market.btc_usdt.kline.1m

```

* 订阅成功后,服务器返回数据:
``` 
    {
         "status": "ok",
         "subbed": "market.btc_usdt.kline.1m",
         "ts": 1489474081631
    }
```     
* 每当数据有更新时，⾃动推送数据如下：
```
{
     "ch": "market.btc_usdt.kline.1m",
     "ts": 1489474082831,
     "data":
     [
       ["1490337840000","995.37","996.75","995.36","996.75","9.112"],
       ["1490337830000","995.37","996.75","995.36","996.75","9.112"]
     ]
 }
 
返回值说明:
[时间,开盘价,最高价,最低价,收盘价,成交量]
[String, String, String, String, String, String]
```
- [**建议修改**] > 数据字段列表

参数  | 数据类型|是否必填|描述|备注
---|---|---|---|---
ts| String | 是| 时间|时间有效范围[1501174800000, 2556115200000]
open | String|是|开盘价| 995.37
high | String|是|最高价|996.75
low | String|是|最低价| 995.36
close | String|是|收盘价|996.75
vol | String|是|交易量|9.112


### 订阅深度数据
---
##### 主题订阅

```
请求地址 /trade/depth    
请求数据 {"sub":"market.$symbol.trade.depth"}
```
> 参数说明

参数  | 数据类型|是否必须|描述|备注
---|---|---|---|---
symbol| String | 是| 交易代码|btc_gavc
```
 例如：
 symbol为某个交易对，如btcusdt
 数据请求格式:/trade/depth?sub=market.btc_gavc.trade.depth
```

* 订阅成功后,服务器返回数据:
``` 
    {
         "status": "ok",
         "subbed": "market.btc_gavc.trade.depth",
         "ts": 1489474081631
    }
```

* 每当深度数据有更新时，⾃动推送数据如下：
``` 
    {
        "ch": "market.btc_gavc.trade.depth",
        "ts": 1489474082831,
        "data": {
                "bids": [ ["9999.39","0.0098"],["9992.5947","0.056"]],
                "asks": [["10010.98","0.0099"],["10011.39","2"]]
                } 
    }
```
 
返回值说明:

参数  | 数据类型|是否必填|描述|备注
---|---|---|---|---
bids |[String,String]|是|【买方价格,买方深度量】| ["9999.39","0.0098"]
asks| [String,String] |是|【卖方价格,卖方深度度量】|["10010.98","0.0099"]



### 订阅实时交易数据
---
##### 订阅请求

```
请求地址 /trade/detail    
请求数据 {"sub":"market.$symbol.trade.detail"}
```
> 参数说明

参数  | 数据类型|是否必须|描述|备注
---|---|---|---|---
symbol| String | 是| 交易代码|btc_gavc
```
 例如：
 symbol为某个交易对，如btcusdt
 数据请求格式:/trade/detail?sub=market.btc_gavc.trade.detail
```

* 订阅成功后,服务器返回数据:
``` 
    {
         "status": "ok",
         "subbed": "market.btc_gavc.trade.detail",
         "ts": 1489474081631
    }
```     
* 每当数据有更新时，⾃动推送数据如下：
```
  {
        "ch": "market.btc_gavc.trade.detail",
         "ts": 1533265950234,
         "data": [
                    {
                         "amount": "0.0099",
                         "ts": "1533265950234",
                         "id": "146507451359183894799",
                         "price": "401.74",
                         "direction": "buy"
                     },
                     //more data here
                 ] 
    }
```   

返回值说明:

参数  | 数据类型|是否必填|默认值|描述|备注
---|---|---|---|---|---
amount | string|是|成交量| 0.0099
ts| String | 是| 成交时间|有效时间范围[1501174800000, 2556115200000]
id | string|是|唯一成交ID号|唯一成交ID
price | string|是|成交价格| 401.74
direction | string|是|买单【buy】或卖单【sell】|[buy/sell]

### 订阅涨跌幅
---
##### 主题订阅

```
请求地址 /trade/rankingList 
请求数据 {"sub":"market.tradeRankList"}
```
> **参数说明**

参数  | 数据类型|是否必须|默认值|描述|备注
---|---|---|---|---|---
sort| String | 否 |DESC| DESC/ASC|

```
 例如：
    数据请求格式: /trade/rankingList?sub=market.tradeRankList&sort=desc

```

* 订阅成功后,服务器返回数据:
``` 
    {
         "status": "ok",
         "subbed": "market.tradeRankList",
         "ts": 1489474081631
    }
```     
* 每当数据有更新时，⾃动推送数据如下：
```
{
     "ch": "market.tradeRankList",
     "ts": 1489474082831,
     "data":
     [
       ["1490337840000","995.37","996.75","995.36","996.75","9.112","1.2"],
       ["1490337830000","995.37","996.75","995.36","996.75","9.112","0.3"]
     ]
 }

返回值说明:
[时间,开盘价,最高价,最低价,收盘价,成交量,涨跌幅]
[String, String, String, String, String, String,String]
```


