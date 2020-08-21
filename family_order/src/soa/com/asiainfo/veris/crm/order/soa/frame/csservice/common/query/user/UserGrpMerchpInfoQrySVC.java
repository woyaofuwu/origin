package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserGrpMerchpInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * @description 根据集团用户编号和成员用户编号查询集团与成员用户关系表
     * @author xunyl
     * @date 2016-05-26
     */
    public IDataset qryMerchpInfoByProductOfferId(IData param) throws Exception
    {
        String productOfferId = param.getString("PRODUCT_OFFER_ID", "");

        return UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferId);
    }
}
