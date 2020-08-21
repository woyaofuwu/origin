
package com.asiainfo.veris.crm.order.web.frame.csview.group.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class GroupSalePayBeanRule extends CSBizHttpHandler
{
    public void checkBaseInfoRule() throws Exception
    {
        IData conParams = getData();

        IData checkParam = new DataMap();
        checkParam.put("CUST_ID", conParams.getString("CUST_ID"));
        checkParam.put("PRODUCT_ID", conParams.getString("PRODUCT_ID"));
        checkParam.put("SERIAL_NUMBER", conParams.getString("SERIAL_NUMBER"));
        checkParam.put("CHK_FLAG", "BaseInfo");

        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpSale", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }
    }
}
