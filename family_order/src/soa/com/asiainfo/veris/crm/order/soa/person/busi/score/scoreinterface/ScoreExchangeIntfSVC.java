
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 积分转赠接口服务
 * 
 * @author zhouwu
 * @date 2014-07-02 17:11:52
 */
public class ScoreExchangeIntfSVC extends CSBizService
{

    /**
     * 积分商城库存查询（给IVR提供）
     * 
     * @param pd
     * @param inparam
     * @return
     * @throws Exception
     * @author zhouwu
     */
    public IData selectRES(IData input) throws Exception
    {
        ScoreExchangeIntfBean bean = new ScoreExchangeIntfBean();
        return bean.selectRES(input);
    }

}
