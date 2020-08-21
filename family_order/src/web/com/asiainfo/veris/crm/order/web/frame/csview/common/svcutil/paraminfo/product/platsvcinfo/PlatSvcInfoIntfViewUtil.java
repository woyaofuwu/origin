
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.platsvcinfo;

import com.ailk.biz.view.IBizCommon;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public class PlatSvcInfoIntfViewUtil
{
    /**
     * 通过服务编码查询平台服务名称
     * 
     * @param visit
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static String qryServiceNameStrByServiceId(IBizCommon bc, String serviceId) throws Exception
    {
        return UpcViewCall.queryOfferNameByOfferId(bc, "Z", serviceId);
    }

}
