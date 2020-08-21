
package com.asiainfo.veris.crm.order.web.frame.csview.group.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class CloseGrpMainRule extends CSBizHttpHandler
{
    /**
     * 闭合群/网外号码提交业务规则
     * 
     * @throws Exception
     */
    public void checkConfirmRule() throws Exception
    {
        IData conParams = getData();
        String deal_type = conParams.getString("X_DEAL");
        String user_id_a = conParams.getString("USER_ID_A");
        String user_id_b = conParams.getString("USER_ID_B");

        IData checkParam = new DataMap();
        checkParam.put("USER_ID_A", user_id_a);
        checkParam.put("USER_ID_B", user_id_b);
        checkParam.put("X_DEAL", deal_type);

        IDataset ruleResults = CSViewCall.call(this, "CS.chkCloseGrpMain", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }
    }
}
