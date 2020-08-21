
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class GrpBatCreateM2MMemSVC extends GroupBatService
{
    private static final long serialVersionUID = 1L;

    private static final String SERVICE_NAME = "CS.CreateGroupMemberSvc.createGroupMember";

    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;
        batData.put(BIZ_CTRL_TYPE, BizCtrlType.CreateMember);
    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        // 校验集团用户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", condData.getString("USER_ID"));
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        chkMemberUCABySerialNumber(inparam);

    }

    /**
     * 构造规则数据
     */
    @Override
    protected void builderRuleData(IData batData) throws Exception
    {
        ruleData.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        ruleData.put("RULE_BIZ_KIND_CODE", "GrpMebOrder");
        // 集团信息
        ruleData.put("PRODUCT_ID", getGrpUcaData().getProductId());
        ruleData.put("CUST_ID", getGrpUcaData().getCustId());
        ruleData.put("USER_ID", getGrpUcaData().getUserId());
        // 成员信息
        ruleData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        ruleData.put("USER_ID_B", getMebUcaData().getUserId());
        ruleData.put("BRAND_CODE_B", getMebUcaData().getBrandCode());
        ruleData.put("EPARCHY_CODE_B", getMebUcaData().getUser().getEparchyCode());
        ruleData.put("PRODUCT_ID_B", getMebUcaData().getProductId());
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("USER_ID", getGrpUcaData().getUserId());
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        svcData.put("MEM_ROLE_B", condData.getString("MEM_ROLE_B", "1"));
        svcData.put("PRODUCT_ID", IDataUtil.getMandaData(condData, "PRODUCT_ID"));
        svcData.put("ELEMENT_INFO", new DatasetList(condData.getString("ELEMENT_INFO")));
        svcData.put("PLAN_TYPE_CODE", condData.getString("PLAN_TYPE"));
        svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "false"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE", getVisit().getInModeCode()));

        // 构建产品参数信息
        IDataset productParamDataset = new DatasetList();
        svcData.put("PRODUCT_PARAM_INFO", productParamDataset);
    }
}
