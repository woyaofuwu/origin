
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradePoInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 查询集团工单
     * 
     * @author weixb3
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset orderInfoQry(IData param) throws Exception
    {
        return TradePoInfoQry.orderInfoQry(param.getString("OPERATIONSUBTYPEID"), param.getString("START_DATE"), param.getString("END_DATE"), getPagination());
    }

    /**
     * 查询成员工单
     * 
     * @author weixb3
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset orderMemQry(IData param) throws Exception
    {
        return TradePoInfoQry.orderMemQry(param.getString("START_DATE"), param.getString("END_DATE"), getPagination());
    }

}
