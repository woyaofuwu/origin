
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class AgentBusiSVC extends CSBizService
{

    /**
     * @Function: getAgentBusi
     * @Description: 查询代理商业务量报表
     * @param param
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月25日 上午11:19:26
     */
    public IDataset getAgentBusi(IData param) throws Exception
    {
        String departId = param.getString("DEPART_ID");
        String startDate = param.getString("START_DATE");
        String endDate = param.getString("END_DATE");
        return TradeInfoQry.getAgentBusiSumByDepartId(departId, startDate, endDate);
    }

    /**
     * @Function: getAgentSN
     * @Description: 代理商放号报表查询
     * @param param
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月25日 上午11:19:09
     */
    public IDataset getAgentSN(IData param) throws Exception
    {
        String departId = param.getString("DEPART_ID");
        String startDate = param.getString("START_DATE");
        String endDate = param.getString("END_DATE");
        return TradeInfoQry.getAgentReleaseSnSumByDepartId(departId, startDate, endDate);
    }
}
