
package com.asiainfo.veris.crm.order.web.frame.csview.group.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class ModMemPayRelaRule extends CSBizHttpHandler
{

    /**
     * 集团成员付费关系变更baseinfo界面规则验证
     * 
     * @throws Exception
     */
    public void checkBaseInfoRule() throws Exception
    {
        IData conParams = getData();
        String productId = conParams.getString("PRODUCT_ID");
        String custId = conParams.getString("CUST_ID");
        String eparchyCode = conParams.getString("EPARCHY_CODE_B");
        String userIdA = conParams.getString("USER_ID");
        String mebUserId = conParams.getString("USER_ID_B");
        String brandCodeB = conParams.getString("BRAND_CODE_B");
        IData checkParam = new DataMap();
        checkParam.put("CUST_ID", custId);
        checkParam.put("PRODUCT_ID", productId);
        checkParam.put("CHK_FLAG", "BaseInfo");
        checkParam.put("EPARCHY_CODE_B", eparchyCode);
        checkParam.put("USER_ID", userIdA);
        checkParam.put("USER_ID_B", mebUserId);
        checkParam.put("BRAND_CODE_B", brandCodeB);
        checkParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpMebPayMod", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

}
