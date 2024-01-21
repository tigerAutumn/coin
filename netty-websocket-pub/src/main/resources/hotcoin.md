# hotcoin.top 行情对接API

- WebSocketAPI
  - 订阅深度数据
  - 订阅实时交易数据
  - 订阅K线数据
  - 订阅分区数据
  - 订阅涨幅top10数据
  -订阅跌幅top10数据
  - 综合订阅接口
# 请求交互

  **服务器地址:bathpath:  ws://apiwebsocket.hotcoin.top**

## WebSocket Api 

### 心跳检测

```
客户端需要每30秒发送一次心跳给服务端,否则客户端会断开连接
发送心跳数据格式:{"ping":""}
```

### **1.订阅实时交易数据**

```
请求地址: /trade/detail   
请求数据: {"sub":"market.$symbol.trade.detail"}

其中，$symbol为某个交易对，如btc_usdt
订阅数据时,将json数据放在body中发送
数据请求格式: {"sub":"market.btc_usdt.trade.detail"}
```

- 订阅成功会返回成功消息:

  ```
      {
           "status": "ok",
           "subbed": "market.btc_usdt.trade.detail",
           "ts": 1489474081635
      }
  ```

- 每当有新交易发生时，推送内容如下

  ```
      {
          "ch": "market.btc_usdt.trade.detail",
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

```
返回值说明:

     amount(string): 成交数量
     ts(string): 成交时间
     id(string):唯一id
     price(string): 价格
     direction(string): 买或卖单(buy,sell)
```

### 2.订阅深度数据

```
请求地址: /trade/depth   
请求数据: {"sub":"market.$symbol.trade.depth"}

symbol为某个交易对，如btcusdt
订阅数据时,将json数据放在body中发送
数据请求格式:{"sub":"market.btc_usdt.trade.depth"}
```

- 订阅成功后,服务器返回数据:

  ```
      {
          "status": "ok",
          "subbed": "market.btc_usdt.trade.depth",
          "ts": 1489474081631
      }
  ```

- 每当深度数据有更新时，推送数据如下：

  ```
  {
      "ch": "market.btc_usdt.trade.depth",
      "ts": 1489474082831,
      "data": {
              "bids": [ ["9999.39","0.0098"],["9992.5947","0.056"]],
              "asks": [["10010.98","0.0099"],["10011.39","2"]],
              "last":"1000.0",
              "open":"1000.0",
              "cny":"7800"
              } 
  }
  
  
  
  返回值说明:
     bids([string, string]):买方深度
     asks([string, string]):卖方深度
     last string:最新价
     open string:开盘价
     cny  string:折算人民币价格
     
  ```

### 3.K线订阅

```
请求地址: /trade/kline   
请求数据: {"sub":"market.$symbol.kline.$period"}

symbol 为币对如:btcusdt
period 周期如下: 1m,5m,15m,30m,1h,1d,1w,1mo
订阅数据时,将json数据放在body中发送
数据请求格式:{"sub":"market.btc_usdt.kline.1m"}
```

- 订阅成功后,服务器返回数据:

  ```
      {
           "status": "ok",
           "subbed": "market.btc_usdt.kline.1m",
           "ts": 1489474081631
      }
  ```

- 每当数据有更新时，⾃动推送数据如下：

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
  [string, string, string, string, string, string]
  ```
  ### **订阅分区数据**
   ```
   1.请求地址: /trade/multiple
   2.请求数据: {"sub":"market.trade.$area.tickers"} 
   3.$area为分区名称,如下:gavc_area, btc_area, eth_area, usdt_area, innovate_area
   4.订阅数据:订阅数据时,将json数据放在body中,发送数据请求格式:{"sub":"market.trade.btc_area.tickers"}
  - 订阅成功后,服务器返回数据:
  
    ```
        {
             "status": "ok",
             "subbed": "market.trade.btc_area.tickers",
             "ts": 1489474081631
        }
    ```
  
  - 每当数据有更新时，⾃动推送数据如下：
  
    ```
    {
         "ch": "market.trade.btc_area.tickers",
         "ts": 1489474082831,
         "data":
         [{
             "buy": "1",
             "buyShortSymbol": "1",
             "buySymbol": "1",
             "cny": "1",
             "lever": "1",
             "last": "1",
             "change": "1",
             "sell": "1",
             "sellShortSymbol": "1",
             "sellSymbol": "1",
             "volume": "1",
             "tradeId": 1 
          }]
     }
     ```  
     ```  
     buy :买1价
     buyShortSymbol:币种名称如:BTC
     buySymbol:币种全称呼:BITCOIN
     cny:折合人民币价格
     lever:杠杆
     last:最新价
     change:涨跌幅
     sell:卖1价
     sellShortSymbol:币种名称如:BTC
     sellSymbol:币种全称
     volume:成交量
     tradeId:交易id
    
    
  ### **订阅涨跌幅数据**
  ```
   ```
      1.请求地址: /trade/multiple
      2.请求数据: {"sub":"market.trade.$s.tickers"} 
      3.$s为分区名称,如下:up,down
      4.订阅数据:订阅数据时,将json数据放在body中,发送数据请求格式:{"sub":"market.trade.up.tickers"}
    - 订阅成功后,服务器返回数据:
    - 每当数据有更新时，⾃动推送数据如下：
     
       ```
       {
            "ch": "market.trade.up.tickers",
            "ts": 1489474082831,
            "data":
            [{
                "buy": "1",
                "buyShortSymbol": "1",
                "buySymbol": "1",
                "cny": "1",
                "lever": "1",
                "last": "1",
                "change": "1",
                "sell": "1",
                "sellShortSymbol": "1",
                "sellSymbol": "1",
                "volume": "1",
                "tradeId": 1 
             }]
        }
        ```  
        ```  
        buy :买1价
        buyShortSymbol:币种名称如:BTC
        buySymbol:币种全称呼:BITCOIN
        cny:折合人民币价格
        lever:杠杆
        last:最新价
        change:涨跌幅
        sell:卖1价
        sellShortSymbol:币种名称如:BTC
        sellSymbol:币种全称
        volume:成交量
        tradeId:交易id
       
  ```
   ```
  ### **综合接口(只提供给内部使用)**
   ```
  ```
  1.请求地址: /trade/multiple
  2.请求数据: {"sub":"market.$symbol.kline.$period"} or {"sub":"market.$symbol.trade.depth"} or {"sub":"market.$symbol.trade.detail"}
  
  3.释义:symbol 为币对如:btc_usdt ,period 周期如下: 1m,5m,15m,30m,1h,1d,1w,1mo
  
  4.订阅数据:订阅数据时,将json数据放在body中,如订阅k线,发送数据请求格式:{"sub":"market.btc_usdt.kline.1m"}
  ```
```
- 每当数据有更新时，⾃动推送数据如下(数字编号为对应的接口编号)：
    1.如果是订阅的实时交易,参考实时交易接口数据结构(1)
    2.订阅的深度交易,参考深度接口数据结构(2)
    3.订阅的k线,参考k线接口数据结构(3)

- 取消订阅
    1.单个订阅取消:取消某个订阅时,将json数据放在body中,如取消订阅k线,发送数据请求格式:{"unsub":"market.btc_usdt.kline.1m"}

    2.取消所有订阅:取消所有订阅时,将json数据放在body中,发送数据请求格式:{"unsubAll":"unsubAll"}
    
```