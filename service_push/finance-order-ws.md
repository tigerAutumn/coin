
# hotcoin websocket资产与订单API
* WebSocketAPI
   * 账户更新
   * 订单更新
   * 订单状态更新
   * 用户资产数据

**服务器地址:bathpath:  wss://apiwebsocket.hotcoin.top**

**请求数据**

Websocket服务器同时支持一次性请求数据（pull）。

当与Websocket服务器成功建立连接后，以下三个主题可供用户请求：

* accounts.list
* orders.list
* orders.detail

具体请求方式请见后文。

>**数据请求限频规则**

限频规则基于API key而不是连接。当请求频率超出限值时，Websocket客户端将收到"too many request"错误码。以下为各主题当前限频设定：

* accounts.list: once every 25 seconds
* orders.list AND orders.detail: once every 5 seconds

### **鉴权**

鉴权的请求参数说明

参数  | 数据类型|是否必须|描述|备注
---|---|---|---|---
AccessKeyId| String | 是 | API 访问密钥|您申请的 APIKEY 中的AccessKeye eg:2xxxxxx-99xxxxxx-84xxxxxx-7xxxx 
id | String| 是 |用户的apikey对一个的ID|eg:1234567890
SignatureMethod | String| 是 |签名方法|eg:HmacSHA256
SignatureVersion | String| 是 |签名协议的版本|eg:2
Timestamp | String| 是 |请求(UTC 时区)|eg:2017-05-11 16:22:06
Signature | String| 是 |签名计算结果|确保签名有效和未被篡改

>*==按照ASCII码的顺序对参数名进行排序(使用 UTF-8 编码，且进行了 URI 编码，十六进制字符必须大写，如‘:’会被编码为'%3A'，空格被编码为'%20')==。*
```
请求原始数据
AccessKeyId=e2xxxxxx-99xxxxxx-84xxxxxx-7xxxx 
SignatureMethod=HmacSHA256 
SignatureVersion=2 
Timestamp=2017-05-11%2015%3A19%3A30 
id=1234567890 

排序后的数据
AccessKeyId=e2xxxxxx-99xxxxxx-84xxxxxx-7xxxx 
id=1234567890 
SignatureMethod=HmacSHA256 
SignatureVersion=2 
Timestamp=2017-05-11%2015%3A19%3A30 

参数&连接
AccessKeyId=e2xxxxxx-99xxxxxx-84xxxxxx-7xxxx&id=1234567890&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2017-05-11T15%3A19%3A30 
```

```
GET\n 
hkapi.hotcoin.top\n 
/v1/order/cancel\n 
AccessKeyId=e2xxxxxx-99xxxxxx-84xxxxxx-7xxxx&id=1234567890&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2017-05-11%2015%3A19%3A30 

签名
进行签名的密钥（SecretKey）
b0xxxxxx-c6xxxxxx-94xxxxxx-dxxxx 

得到签名计算结果并进行 Base64编码 
4F65x5A2bLyMWVQj3Aqp+B4w+ivaA7n5Oi2SuYtCJ9o= 
```
>完整的http请求
```
https://hotcoin.top/v1/order/cancel? AccessKeyId=e2xxxxxx-99xxxxxx-84xxxxxx-7xxxx&id=1234567890&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2017-05-11T15%3A19%3A30&Signature=4F65x5A2bLyMWVQj3Aqp+B4w+ivaA7n5Oi2SuYtCJ9o= 
```

### 订单账户更新
---
##### 主题订阅

```
请求地址 /accounts
请求数据 {"sub":"accounts.$model$"}
```
> **请求参数说明**

参数  | 数据类型|是否必填|默认值|描述|备注
---|---|---|---|---|---
model| String |否 |0 |是否包含已冻结余额|1 to include frozen balance, 0 to not

```
 例如：
    $symbol为某个交易对，如btcusdt
    数据请求格式: /accounts?sub=accounts.0

```

> **返回结果参数说明**

参数  | 数据类型|描述|备注
---|---|---|---
event| String |  资产变化通知|资产变化通知相关事件说明，比如订单创建(order.place) 、订单成交(order.match)、订单成交退款（order.refund)、订单撤销(order.cancel) 、点卡抵扣交易手续费（order.fee-refund)、杠杆账户划转（margin.transfer)、借贷本金（margin.loan)、借贷计息（margin.interest)、归还借贷本金利息(margin.repay)、其他资产变化(other)
account-id | String| 账户 id|
currency|String|币种
type|String|账户类型|账户类型, 交易子账户（trade),借贷子账户（loan），利息子账户（interest)
balance|String|账户余额|账户余额 (当订阅mode=0时，该余额为可用余额；当订阅mode=1时，该余额为总余额）


* 订阅成功后,服务器返回数据:
``` 
    {
         "status": "ok",
         "subbed": "accounts.0",
         "ts": 1489474081631
    }
```     
* 每当数据有更新时，⾃动推送数据如下：
```
 {
        "ch": "accounts.0",
         "ts": 1533265950234,
         "data": [
                    {
                         "event": "0.0099",
                         "ts": "1533265950234",
                         "account-id": "146507451359183894799",
                         "currency": "BTC",
                         "type": "trade",
                         "balance": "0.01"
                     }
                 ] 
    }
```


###  订单
---
##### 主题订阅

```
请求地址 /orders
请求数据 {"sub":"orders.$symbol"}
```
> **请求参数说明**

参数  | 数据类型|是否必须|描述|备注
---|---|---|---|---
symbol| String | 是| 交易代码|btcgavc

```
 例如：
    $symbol为某个交易对，如btcusdt
    数据请求格式: /orders?sub=orders.btcgavc

```

> **返回结果参数说明**

参数  | 数据类型|描述|备注
---|---|---|---
matchId| String | 最近撮合编号|（当order-state = submitted, canceled, partial-canceled时，match-id 为消息序列号；当order-state = filled, partial-filled 时，match-id 为最近撮合编号。）
orderId | String| 订单id|
symbol|String|交易币对
orderState|String|订单状态|submitted, partical-filled, cancelling, filled, canceled, partial-canceled
price|String|最新价（当order-state = submitted 时，price 为订单价格；当order-state = canceled, partial-canceled 时，price 为零；当order-state = filled, partial-filled 时，price 为最近成交价。当role = taker，且该订单同时与多张对手方订单撮合时，price 为多笔成交均价。）
direction|String|买单【buy】或卖单【sell】|[buy/sell]
filledAmt|String|最近成交数量
filledCashAmt|String|最近成交数额
unfilledAmt|String|最近未成交数量（当order-state = submitted 时，unfilled-amount 为原始订单量；当order-state = canceled OR partial-canceled 时，unfilled-amount 为未成交数量；当order-state = filled 时，如果 order-type = buy-market，unfilled-amount 可能为一极小值；如果order-type <> buy-market 时，unfilled-amount 为零；当order-state = partial-filled AND role = taker 时，unfilled-amount 为未成交数量；当order-state = partial-filled AND role = maker 时，unfilled-amount 为零。（后续将支持此场景下的未成交量，时间另行通知。））



* 订阅成功后,服务器返回数据:
``` 
    {
         "status": "ok",
         "subbed": "orders.htusdt",
         "ts": 1489474081631
    }
```     
* 每当数据有更新时，⾃动推送数据如下：
```
 {
  "ts": 1522856623232,
  "topic": "orders.htusdt",  
  "data": {
    "unfilledAmt": "0.000000000000000000",
    "filledAmt": "5000.000000000000000000",
    "filledCashAmt": "8301.357280000000000000",
    "price": "1.662100000000000000",
    "orderId": 2039498445,
    "symbol": "htusdt",
    "matchId": 94984,
    "direction": "buy|sell",
    "orderState": "filled"
  }
}
```

###  用户资产数据
---
##### 主题订阅

```
请求地址 /accounts
请求数据 {"sub":"accounts.list"}
```
> **请求参数说明**

参数  | 数据类型|是否必须|描述|备注
---|---|---|---|---
cid| String | 是| 用户唯一id|
sub|String|是|accounts.list|

```
 例如：
    $symbol为某个交易对，如btcusdt
    数据请求格式: /orders?id=123&sub=orders.btcgavc

```

> **返回结果参数说明**

参数  | 数据类型|描述|备注
---|---|---|---
id| String | 账户id|
type | String| 账户类型|
state|String|账户状态
list|String|账户列表|

list结构

参数|数据类型|描述|备注
---|---|---|---
currency|String|子账户比重
type|String|子账户类型
balance|String|子账户余额



* 订阅成功后,服务器返回数据:
``` 
    {
         "status": "ok",
         "subbed": "orders.htusdt",
         "ts": 1489474081631
    }
```     
* 每当数据有更新时，⾃动推送数据如下：
```
 {
      "topic": "accounts.list",
      "cid": "40sG903yz80oDFWr",
      "err-code": 0,
      "ts": 1489474082831,
      "data": [
        {
          "id": 419013,
          "type": "spot",
          "state": "working",
          "list": [
            {
              "currency": "usdt",
              "type": "trade",
              "balance": "500009195917.4362872650"
            },
            {
              "currency": "usdt",
              "type": "frozen",
              "balance": "9786.6783000000"
            }
          ]
        },
        {
          "id": 35535,
          "type": "point",
          "state": "working",
          "list": [
            {
              "currency": "eth",
              "type": "trade",
              "balance": "499999894616.1302471000"
            },
            {
              "currency": "eth",
              "type": "frozen",
              "balance": "9786.6783000000"
            }
          ]
        }
      ]
    }
```





