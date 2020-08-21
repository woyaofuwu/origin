
package com.asiainfo.veris.crm.order.web.group.rules;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class VpmnShortCodeRule extends CSBizHttpHandler
{

    /**
     * 集团VPMN成员短号变更界面规则验证
     * 
     * @throws Exception
     */
    public void checkShortCodeRule() throws Exception
    {

        IData conParams = getData();
        IData checkParam = new DataMap();
        checkParam.put("USER_ID", conParams.getString("USER_ID"));
        checkParam.put("SERIAL_NUMBER", conParams.getString("SERIAL_NUMBER"));
        checkParam.put("SHORT_CODE", conParams.getString("SHORT_CODE"));
        checkParam.put("MEB_EPARCHY_CODE", conParams.getString("MEB_EPARCHY_CODE", "0898"));
        checkParam.put(Route.ROUTE_EPARCHY_CODE, conParams.getString("MEB_EPARCHY_CODE", "0898"));
        IDataset ruleResults = CSViewCall.call(this, "CS.chkVpmnShortCode", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

}
