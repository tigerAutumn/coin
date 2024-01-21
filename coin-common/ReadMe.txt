
已支持币模式的主链币种配置：
1、coin_common依赖库：com.hotcoin.coin.enums 配置对应的模式支持的币种enum
2、com.hotcoin.coin.wallet.config 下新增***Configs配置初始化自动注入该模式下支持的主链服务



新增支持主链模式：
1、在com.hotcoin.coin.wallet.coins 下建立对应模式的java包
2、实现热模式下rpc功能的***Utils.java
3、实现 *** 主链模式的 BaseCoinService 接口
4、coin_common依赖库：com.hotcoin.coin.enums 配置对应的模式支持的币种enum
5、com.hotcoin.coin.wallet.config 下新增***Configs配置初始化自动注入该模式下支持的主链服务
6、新增扫块充币调度任务：
    com.hotcoin.coin.wallet.scheduler.deposit
7、新增主链模式币种归集调度任务：
    com.hotcoin.coin.wallet.scheduler.collection





