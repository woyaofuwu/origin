
package com.asiainfo.veris.crm.order.web.frame.csview.group.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class CreateGrpMemberSaleRule extends CSBizHttpHandler
{

    /**
     * 集团产品成员营销baseinfo界面规则验证
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
        String mebSerialNumber = conParams.getString("MEB_SERIAL_NUMBER", "");

        IData checkParam = new DataMap();

        checkParam.put("CUST_ID", custId);
        checkParam.put("PRODUCT_ID", productId);
        checkParam.put("CHK_FLAG", "BaseInfo");
        checkParam.put("EPARCHY_CODE_B", eparchyCode);
        checkParam.put("USER_ID", userIdA);
        checkParam.put("USER_ID_B", mebUserId);
        checkParam.put("BRAND_CODE_B", brandCodeB);
        checkParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        checkParam.put("SERIAL_NUMBER", mebSerialNumber);

        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpMeberSale", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                setAjax(ruleResult);
        }

    }

    /**
     * 集团产品成员营销Productinfo界面规则验证
     * 
     * @throws Exception
     */
    public void checkProductInfoRule() throws Exception
    {
        IData conParams = getData();

        String productId = conParams.getString("PRODUCT_ID");
        String mebUserId = conParams.getString("USER_ID_B");
        String allSelectElements = conParams.getString("ALL_SELECTED_ELEMENTS");
        String eparchyCode = conParams.getString("EPARCHY_CODE_B");
        String mebSerialNumber = conParams.getString("MEB_SERIAL_NUMBER", "");
        String userIdA = conParams.getString("USER_ID");

        if (StringUtils.isEmpty(allSelectElements))
            return;

        IData checkParam = new DataMap();
        checkParam.put("PRODUCT_ID", productId);
        checkParam.put("CHK_FLAG", "ProductInfo");
        checkParam.put("SELECTED_USER_ID", mebUserId);
        checkParam.put("USER_ID", userIdA);
        checkParam.put("USER_ID_B", mebUserId);
        checkParam.put("ALL_SELECTED_ELEMENTS", new DatasetList(allSelectElements));
        checkParam.put("EPARCHY_CODE_B", eparchyCode);
        checkParam.put("SERIAL_NUMBER", mebSerialNumber);
        checkParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpMeberSale", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                setAjax(ruleResult);
        }

    }

}
