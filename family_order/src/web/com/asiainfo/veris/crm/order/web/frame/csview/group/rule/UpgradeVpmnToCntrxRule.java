
package com.asiainfo.veris.crm.order.web.frame.csview.group.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UpgradeVpmnToCntrxRule extends CSBizHttpHandler
{
    /**
     * 普通V网升级融合V网规则验证
     * 
     * @throws Exception
     */
    public void checkConfirmRule() throws Exception
    {
        IData conParams = getData();
        String productId = conParams.getString("PRODUCT_ID");
        String userId = conParams.getString("USER_ID");
        String serialNumber = conParams.getString("SERIAL_NUMBER");
        IData checkParam = new DataMap();
        checkParam.put("USER_ID", userId);
        checkParam.put("PRODUCT_ID", productId);
        checkParam.put("SERIAL_NUMBER", serialNumber);

        IDataset ruleResults = CSViewCall.call(this, "CS.chkVpmnToCntrx", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }
    }
}
