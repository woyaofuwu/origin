
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.groupserialnumber;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public class GroupSerialNumberHttpHandler extends CSBizHttpHandler
{

    /**
     * 生成集团号码
     * 
     * @throws Exception
     */
    public void createGrpSn() throws Exception
    {
        IData inpara = getData();
        String productId = inpara.getString("PRODUCT_ID");
        String grpUserEparchyCode = inpara.getString("EPARCHY_CODE", "");
        String resTypeCode = inpara.getString("RES_TYPE_CODE", "");

        IData result = GroupProductUtilView.createGrpSn(this, productId, grpUserEparchyCode, resTypeCode);
        setAjax(result);

    }

}
