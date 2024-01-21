package hotcoin.service;

/**
 * @ProjectName: hotcoin-risk-management
 * @Package: hotcoin.service
 * @ClassName: RealTimeTradeStatService
 * @Author: hf
 * @Description:
 * @Date: 2019/8/17 11:12
 * @Version: 1.0
 */
public interface RealTimeTradeStatService {
    /**
     * 查询钱包
     */
    void processWalletInfo();

    void test(Integer userId, Integer coinId);
}
