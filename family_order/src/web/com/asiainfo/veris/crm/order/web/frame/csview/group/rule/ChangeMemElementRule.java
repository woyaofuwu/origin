
package com.asiainfo.veris.crm.order.web.frame.csview.group.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class ChangeMemElementRule extends CSBizHttpHandler
{

    /**
     * 集团成员变更baseinfo界面规则验证
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

        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpMebChg", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

    /**
     * 集团成员退订Productinfo界面规则验证
     * 
     * @throws Exception
     */
    public void checkProductInfoRule() throws Exception
    {

        IData conParams = getData();
        String grpUserId = conParams.getString("USER_ID");
        String mebUserId = conParams.getString("USER_ID_B");
        String allSelectElements = conParams.getString("ALL_SELECTED_ELEMENTS");
        String mebSN = conParams.getString("SERIAL_NUMBER", "");
        if (StringUtils.isEmpty(allSelectElements))
            return;
        IData checkParam = new DataMap();
        checkParam.put("USER_ID", grpUserId);
        checkParam.put("CHK_FLAG", "ProductInfo");
        checkParam.put("SELECTED_USER_ID", mebUserId);
        checkParam.put("SELECTED_USER_ID_A", grpUserId);
        checkParam.put("ALL_SELECTED_ELEMENTS", new DatasetList(allSelectElements));
        checkParam.put("SERIAL_NUMBER", mebSN);
        checkParam.put("SKIP_FORCE_PACKAGE_FOR_PRODUCT", conParams.getString("SKIP_FORCE_PACKAGE_FOR_PRODUCT"));
        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpMebChg", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

}
