
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.elementinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.discntinfo.DiscntInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.platsvcinfo.PlatSvcInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.svcinfo.SvcInfoIntfViewUtil;

public class ElementInfoIntfViewUtil
{

    /**
     * 通过元素ID 和元素类型 查询元素名称（目前翻译了三种类型 Z D S）
     * 
     * @param visit
     * @param elementId
     * @param elementTypeCode
     * @return
     * @throws Exception
     */
    public static String qryElementNameStrByElementIdAndElementTypeCode(IBizCommon bc, String elementId, String elementTypeCode) throws Exception
    {
        if (StringUtils.isBlank(elementTypeCode))
            return "";
        else if (elementTypeCode.equals("D"))
            return DiscntInfoIntfViewUtil.qryDiscntNameStrByDiscntCode(bc, elementId);
        else if (elementTypeCode.equals("S"))
            return SvcInfoIntfViewUtil.qryServiceNameStrByServiceId(bc, elementId);
        else if (elementTypeCode.equals("Z"))
            return PlatSvcInfoIntfViewUtil.qryServiceNameStrByServiceId(bc, elementId);
        else
            return "";
    }

}
