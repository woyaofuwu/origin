
package com.asiainfo.veris.crm.order.web.frame.csview.group.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class DestroyGroupMemberRule extends CSBizHttpHandler
{

    /**
     * 集团用户开户baseinfo界面规则验证
     * 
     * @throws Exception
     */
    public void checkBaseInfoRule() throws Exception
    {

        IData conParams = getData();
        String userIdA = conParams.getString("USER_ID");
        String mebSN = conParams.getString("SERIAL_NUMBER", "");
        IData checkParam = new DataMap();
        checkParam.put("CHK_FLAG", "BaseInfo");
        checkParam.put("USER_ID", userIdA);
        checkParam.put("SERIAL_NUMBER", mebSN);
        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpMebDestory", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

    /**
     * 单页面调用的验证方法，只验证提示和选择的规则，报错的规则在提交的服务中调用
     * 
     * @throws Exception
     */
    public void checkSimpeTipsRule() throws Exception
    {

        IData conParams = getData();
        String userIdA = conParams.getString("USER_ID");
        String mebSN = conParams.getString("SERIAL_NUMBER", "");
        IData checkParam = new DataMap();
        checkParam.put("USER_ID", userIdA);
        checkParam.put("SERIAL_NUMBER", mebSN);
        checkParam.put("TIPS_TYPE", "1|2");
        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpMebDestory", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

}
