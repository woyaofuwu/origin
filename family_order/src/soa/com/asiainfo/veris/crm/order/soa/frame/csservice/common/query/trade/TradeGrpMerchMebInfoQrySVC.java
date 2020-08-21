
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeGrpMerchMebInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * @Description:一级BBOSS业务成员签约关系订购状态查询
     * @author weixb3
     * @date 2013/6/20
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryBBossBizMebQy(IData param) throws Exception
    {
        return TradeGrpMerchMebInfoQry.qryBBossBizMebQy(param.getString("PRODUCT_OFFER_ID", ""), param.getString("SERIAL_NUMBER", ""), param.getString("EC_SERIAL_NUMBER", ""), param.getString("STATE", ""), param.getString("GROUP_ID", ""),
                getPagination());
    }

}
