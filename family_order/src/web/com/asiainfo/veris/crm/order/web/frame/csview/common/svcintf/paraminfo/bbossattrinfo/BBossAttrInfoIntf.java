
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.bbossattrinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class BBossAttrInfoIntf
{

    /**
     * 通过productId operType bizType 参数，查询BBOSS_ATTR表的参数信息
     * 
     * @param bc
     * @param productId
     * @param operType
     * @param bizType
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossAttrInfosByProductIdAndOperTypeBizType(IBizCommon bc, String productId, String operType, String bizType) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productId);
        inparam.put("OPERTYPE", operType);
        inparam.put("BIZTYPE", bizType);
        return CSViewCall.call(bc, "CS.BBossAttrQrySVC.qryBBossAttrByPospecOpertypeBiztype", inparam);
    }

}
