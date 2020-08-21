
package com.asiainfo.veris.crm.order.web.group.modifyuserdata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * @author yifur
 */
public class ModifyUserDataFlowMainHttpHandler extends CSBizHttpHandler
{
    /**
     * submit
     * 
     * @param cycle
     * @throws Exception
     */
    public void submit() throws Exception
    {
        String payplanString = getData().getString("PAYPLAN_INFOS", "[]");
        IDataset payPlan = new DatasetList(payplanString);
        IData svcData = new DataMap();
        svcData.put("USER_ID", getData().getString("GRP_USER_ID", ""));
        svcData.put("PRODUCT_ID", getData().getString("GRP_PRODUCT_ID", ""));
        svcData.put("REMARK", getData().getString("param_REMARK", ""));
        svcData.put(Route.USER_EPARCHY_CODE, getData().getString("GRP_USER_EPARCHYCODE"));
        svcData.put("PLAN_INFO", payPlan);
        IDataset result = CSViewCall.call(this, "SS.ModifyUserDataSVC.crtTrade", svcData);
        this.setAjax(result);
    }
}
