
package com.asiainfo.veris.crm.order.web.group.modifymemdata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * @author yifur
 */
public class ModifyMemDataFlowMainHttpHandler extends CSBizHttpHandler
{
    /**
     * submit
     * 
     * @param cycle
     * @throws Exception
     */
    public void submit() throws Exception
    {
        String payplanString = getData().getString("grpPayRels", "[]");
        if (StringUtils.isBlank(payplanString))
            payplanString = "[]";
        IDataset payPlan = new DatasetList(payplanString);

        IData inparam = new DataMap();

        inparam.put("USER_ID", getData().getString("GRP_USER_ID", ""));
        inparam.put("SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));
        inparam.put("REMARK", getData().getString("parm_REMARK"));
        inparam.put("PRODUCT_ID", getData().getString("GRP_PRODUCT_ID", ""));
        inparam.put("PLAN_INFO", payPlan);
        IDataset result = CSViewCall.call(this, "SS.ModifyMemDataSVC.crtTrade", inparam);
        this.setAjax(result);
    }
}
