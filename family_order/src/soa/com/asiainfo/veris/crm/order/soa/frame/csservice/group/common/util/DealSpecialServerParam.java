
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class DealSpecialServerParam
{
    /**
     * 根据 product_id,service_id查询ADC MAS 服务参数
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset loadSpecialServerParam(IData inparam) throws Exception
    {
        String productId = inparam.getString("PRODUCT_ID");
        String serviceId = inparam.getString("SERVICE_ID");

        IData svcCtrlInfo = GroupProductUtil.querySvcCtrlInfo(serviceId, productId);// 取服务控制参数
        if (IDataUtil.isEmpty(svcCtrlInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_495);
        }
        String scvctrlclass = svcCtrlInfo.getData("PlatSvcInfoSrc").getString("ATTR_VALUE");

        IDataset specialservParam = (IDataset) GrpInvoker.invoker(scvctrlclass, "getServiceParam", new Object[]
        { inparam }, new Class[]
        { IData.class });

        return specialservParam;
    }

}
