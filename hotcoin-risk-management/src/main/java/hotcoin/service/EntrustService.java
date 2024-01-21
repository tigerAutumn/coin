package hotcoin.service;

import com.github.pagehelper.PageInfo;
import hotcoin.model.po.EntrustPo;

import java.util.List;

/**
 * ??&sect;?? service层
 *
 * @author hf
 * @date 2019-08-20 07:24:59
 * @since jdk 1.8
 */
public interface EntrustService {

    /*<AUTOGEN--BEGIN>*/

    PageInfo<EntrustPo> selectPaged();

    EntrustPo selectByPrimaryKey(Integer fid);

    Integer deleteByPrimaryKey(Integer fid);

    Integer insert(EntrustPo entrust);

    Integer insertSelective(EntrustPo entrust);

    Integer insertSelectiveIgnore(EntrustPo entrust);

    Integer updateByPrimaryKeySelective(EntrustPo entrust);

    Integer updateByPrimaryKey(EntrustPo entrust);

    Integer batchInsert(List<EntrustPo> list);

    Integer batchUpdate(List<EntrustPo> list);

    /**
     * 存在即更新
     *
     * @param entrust
     * @return
     */
    Integer upsert(EntrustPo entrust);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param entrust
     * @return
     */
    Integer upsertSelective(EntrustPo entrust);

    List<EntrustPo> query(EntrustPo entrust);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    int updateByfIdCAS(EntrustPo entrust);

    /*<AUTOGEN--END>*/

}
