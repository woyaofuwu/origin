
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.discntinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class DiscntInfoIntf
{

    /**
     * 优惠编码查询优惠信息
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryDiscntInfoByDisCode(IBizCommon bc, String discntCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("DISCNT_CODE", discntCode);
        return CSViewCall.call(bc, "CS.DiscntInfoQrySVC.getDiscntInfoByDisCode", inparam);
    }

}
