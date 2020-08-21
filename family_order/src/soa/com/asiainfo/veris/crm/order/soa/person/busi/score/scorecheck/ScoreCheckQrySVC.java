
package com.asiainfo.veris.crm.order.soa.person.busi.score.scorecheck; 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 积分对账汇总查询
 * 
 * @author chenxy3
 * @date 2015-04-20
 */
public class ScoreCheckQrySVC extends CSBizService
{

    /**
     * 积分对账汇总查询
     * 2015-04-20 chenxy3
     */
    public IDataset queryScoreTrade(IData input) throws Exception
    { 
        return ScoreCheckQryBean.queryScoreTrade(input,getPagination()); 
    }
    
    /**
     * 积分对账细节查询
     * 2015-04-20 chenxy3
     */
    public IDataset queryScoreTradeDet(IData input) throws Exception
    { 
        return ScoreCheckQryBean.queryScoreTradeDet(input); 
    }
    
}
