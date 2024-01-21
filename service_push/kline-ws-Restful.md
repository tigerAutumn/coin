
# hotcoin Restful请求
* Restful
    * 获取交易易所支持的所有交易易对
    * K线最新交易结果

**服务器地址:bathpath:  http://restfulapi.hotcoin.top**

### 获取交易所支持的所有交易易对
```
 访问地址：/common/symbols
```
* 返回结果：
```
{
"code" : "200",
"data": [
            {
            "base-currency": "eth",
            "quote-currency": "usdt",
            "symbol": "eth_usdt"
            },
            {
            "base-currency": "etc",
            "quote-currency": "gavc",
            "symbol": "etc_gavc"
            }
            //more data
        ]
}
```
结果说明：

参数  | 数据类型|是否必填|描述|备注
---|---|---|---|---
base-currency| String | 是| 基础币种|eth
quote-currency | String|是|计价币种| usdt
symbol | String|是|交易对表示| eth_usdt


### 获取交易所最新的K线历史数据（300条）
```
 访问地址： /common/kline
```
> **参数说明**

参数  | 数据类型|是否必须|描述|备注
---|---|---|---|---
symbol| String | 是 | 交易代码|btc_gavc
period | String| 是 |k线周期|eg:1m,5m,15m,30m,1h,1d,1w,1mo
limit | String |否 | 默认条数 | 默认 300 最大 300



* 返回结果：
```
{
     "code" : "200",
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
