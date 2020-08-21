
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.groupbookinginfo;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;

public class GroupBookingInfoHttpHandler extends CSBizHttpHandler
{

    public void getGroupBookingTagInfo() throws Exception
    {

        IData inpara = getData();
        String productId = inpara.getString("PRODUCT_ID");
        String busiType = inpara.getString("BUSI_TYPE");

        IData result = AttrBizInfoIntfViewUtil.qryBookingTagInfoByProductIdAndBusiType(this, productId, busiType);
        this.setAjax(result);

    }

}
