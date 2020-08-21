
package com.asiainfo.veris.crm.order.web.frame.csview.group.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class CreateGroupMemberRule extends CSBizHttpHandler
{

    /**
     * 集团成员新增baseinfo界面规则验证
     * 
     * @throws Exception
     */
    public void checkBaseInfoRule() throws Exception
    {

        IData conParams = getData();
        String grpUserId = conParams.getString("USER_ID", "");
        String mebSN = conParams.getString("SERIAL_NUMBER", "");
        String addMeb = conParams.getString("IF_ADD_MEB", "false");
        String ifBooking = conParams.getString("IF_BOOKING", "false");
        IData conParam = new DataMap();

        conParam.put("CHK_FLAG", "BaseInfo");
        conParam.put("USER_ID", grpUserId);
        conParam.put("SERIAL_NUMBER", mebSN);
        conParam.put("IF_BOOKING", ifBooking);
        //判断和校园是否激活和是否实名
        conParam.put("PRODUCT_ID", conParams.getString("PRODUCT_ID","0"));
        
        if (addMeb.equals("true"))
        {
            return;
        }
        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpMebOrder", conParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

    public void checkGrpBatTipsRule() throws Exception
    {

        IData conParams = getData();
        String grpUserId = conParams.getString("USER_ID", "");
        String tipsType = conParams.getString("TIPS_TYPE", "1|2");
        String allselectedElements = conParams.getString("ALL_SELECTED_ELEMENTS");
        String actionId = conParams.getString("CHK_FLAG", "");
        String specCdtion = conParams.getString("SPEC_CDTION", "");

        if (StringUtils.isBlank(allselectedElements))
        {
            return;
        }

        IData conParam = new DataMap();
        conParam.put("USER_ID", grpUserId);
        conParam.put("TIPS_TYPE", tipsType);
        conParam.put("ALL_SELECTED_ELEMENTS", new DatasetList(allselectedElements));
        conParam.put("SELECTED_USER_ID_A", grpUserId);

        if (!actionId.equals(""))
        {
            conParam.put("CHK_FLAG", actionId);
        }

        if (!specCdtion.equals(""))
        {
            conParam.put("SPEC_CDTION", specCdtion);
        }

        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpBatMebOrder", conParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

    /**
     * 集团成员新增Productinfo界面规则验证
     * 
     * @throws Exception
     */
    public void checkProductInfoRule() throws Exception
    {

        IData conParams = getData();
        String mebUserId = conParams.getString("USER_ID_B", "-1");
        String allselectedElements = conParams.getString("ALL_SELECTED_ELEMENTS");
        String serail_number = conParams.getString("SERIAL_NUMBER");
        String grpUserId = conParams.getString("USER_ID");
        String addMeb = conParams.getString("IF_ADD_MEB", "false");
        String short_code = conParams.getString("SHORT_CODE");

        if (StringUtils.isBlank(allselectedElements))
            return;

        IData checkParam = new DataMap();
        checkParam.put("CHK_FLAG", "ProductInfo");
        checkParam.put("USER_ID", grpUserId);
        checkParam.put("ALL_SELECTED_ELEMENTS", new DatasetList(allselectedElements));
        checkParam.put("SELECTED_USER_ID", mebUserId);
        checkParam.put("SELECTED_USER_ID_A", grpUserId);
        checkParam.put("SERIAL_NUMBER", serail_number);
        checkParam.put("SHORT_CODE", short_code);

        if (addMeb.equals("true"))
        {
            return;
        }

        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpMebOrder", checkParam);

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
    public void checkSimpeTipsRule() throws Exception
    {

        IData conParams = getData();
        String grpUserId = conParams.getString("USER_ID", "");
        String mebSN = conParams.getString("SERIAL_NUMBER", "");
        String addMeb = conParams.getString("IF_ADD_MEB", "false");
        String ifBooking = conParams.getString("IF_BOOKING", "false");
        String tipsType = conParams.getString("TIPS_TYPE", "1|2");

        String actionId = conParams.getString("CHK_FLAG", "");
        String specCdtion = conParams.getString("SPEC_CDTION", "");
        IData conParam = new DataMap();

        conParam.put("USER_ID", grpUserId);
        conParam.put("SERIAL_NUMBER", mebSN);
        conParam.put("IF_BOOKING", ifBooking);
        conParam.put("TIPS_TYPE", tipsType);

        if (!actionId.equals(""))
        {
            conParam.put("CHK_FLAG", actionId);
        }

        if (!specCdtion.equals(""))
        {
            conParam.put("SPEC_CDTION", specCdtion);
        }

        if (addMeb.equals("true"))
        { // 暂时没有三户资料的用户屏蔽全部规则，后期如果有需要，可以针对不存在三户资料的用户增加规则（所以没有在js中直接截掉addmeb=true的情况）
            return;
        }
        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpMebOrder", conParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

}
