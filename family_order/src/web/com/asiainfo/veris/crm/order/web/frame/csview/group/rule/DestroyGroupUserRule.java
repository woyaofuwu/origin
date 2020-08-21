
package com.asiainfo.veris.crm.order.web.frame.csview.group.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class DestroyGroupUserRule extends CSBizHttpHandler
{

    /**
     * 集团用户开户baseinfo界面规则验证
     * 
     * @throws Exception
     */
    public void checkBaseInfoRule() throws Exception
    {

        IData inparam = getData();

        IData checkParam = new DataMap();
        checkParam.put("CHK_FLAG", "BaseInfo");
        checkParam.put("CUST_ID", inparam.getString("CUST_ID"));
        checkParam.put("USER_ID", inparam.getString("USER_ID"));
        checkParam.put("PRODUCT_ID", inparam.getString("PRODUCT_ID"));
        checkParam.put("IF_BOOKING", inparam.getString("IF_BOOKING"));
        checkParam.put("EPARCHY_CODE", inparam.getString("EPARCHY_CODE"));
        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpUserDestroy", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

    /**
     * 单页面前台调用规则，只调用提示和选择的规则类型
     * 
     * @throws Exception
     */
    public void checkSimpleTipsRule() throws Exception
    {
        IData inparam = getData();

        IData checkParam = new DataMap();
        checkParam.put("TIPS_TYPE", "1|2");
        checkParam.put("CUST_ID", inparam.getString("CUST_ID"));
        checkParam.put("USER_ID", inparam.getString("USER_ID"));
        checkParam.put("PRODUCT_ID", inparam.getString("PRODUCT_ID"));
        checkParam.put("IF_BOOKING", inparam.getString("IF_BOOKING"));
        checkParam.put("EPARCHY_CODE", inparam.getString("EPARCHY_CODE"));
        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpUserDestroy", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }
    }

}
