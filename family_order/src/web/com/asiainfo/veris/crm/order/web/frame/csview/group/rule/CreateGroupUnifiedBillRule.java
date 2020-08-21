
package com.asiainfo.veris.crm.order.web.frame.csview.group.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class CreateGroupUnifiedBillRule extends CSBizHttpHandler
{

    /**
     * 集团用户开户baseinfo界面规则验证
     * 
     * @throws Exception
     */
    public void checkBaseInfoRule() throws Exception
    {

        IData checkParam = getData();
        checkParam.put("CHK_FLAG", "BaseInfo");
        checkParam.put(Route.ROUTE_EPARCHY_CODE, checkParam.getString("EPARCHY_CODE", getTradeEparchyCode()));
        IDataset ruleResults = CSViewCall.call(this, "CS.chkCrtGrpUnifiedBill", this.createDataInput(checkParam).getData());

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

}
