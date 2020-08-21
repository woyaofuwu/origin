
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeDataLineAttrInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 获取TF_F_USER_DATALINE数据
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryUserDatalineByProductNO(IData param) throws Exception
    {
        return TradeDataLineAttrInfoQry.qryUserDatalineByProductNO(param.getString("USER_ID"), param.getString("PRODUCT_NO"));
    }
    
    public IDataset qryAllUserDatalineByProductNO(IData param) throws Exception
    {
        return TradeDataLineAttrInfoQry.qryAllUserDatalineByProductNO(param.getString("PRODUCT_NO"));
    }
}
