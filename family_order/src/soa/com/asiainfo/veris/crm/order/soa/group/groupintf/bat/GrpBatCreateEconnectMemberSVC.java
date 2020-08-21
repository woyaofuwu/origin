
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

/**
 * 集团E网接入点计费业务成员批量新增
 * 
 * @author liujy
 */

public class GrpBatCreateEconnectMemberSVC extends GroupBatService
{
    private static final long serialVersionUID = 1L;

    private boolean ctFlag = false;

    private static final String SERVICE_NAME = "CS.CreateGroupMemberSvc.createGroupMember";

    @Override
    protected void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;
        batData.put(BIZ_CTRL_TYPE, BizCtrlType.CreateMember);
    }

    @Override
    protected void batValidateSub(IData batData) throws Exception
    {

        String user_id = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID

        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        String cust_id = IDataUtil.chkParam(condData, "CUST_ID");// 集团客户编码

        // 校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", user_id);
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", serial_number);
        chkMemberUCABySerialNumber(inparam);

        // 判断服务号码状态
        if (!"0".equals(getMebUcaData().getUser().getUserStateCodeset()))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_471, serial_number);
        }
        /**
         * // 判断成员号码是否为集团号码 if (isGroupSerialNumber(serial_number)) { CSAppException.apperr(GrpException.CRM_GRP_120,
         * serial_number); }
         */

        // 校验互联网专线（GPON）资料是否存在
        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        param.put("PRODUCT_ID", "7051");
        IDataset userResult = CSAppCall.call("CS.UserInfoQrySVC.getUserInfoByCstIdProIdForGrp", param);

        if (userResult == null || userResult.size() == 0)
        {
            String groupId = userResult.getData(0).getString("GROUP_ID");
            CSAppException.apperr(GrpException.CRM_GRP_678, groupId, "集团E网接入点计费");
        }

    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("USER_ID", getGrpUcaData().getUserId());
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        svcData.put("MEM_ROLE_B", condData.getString("MEM_ROLE_B", "1"));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put("ELEMENT_INFO", new DatasetList(condData.getString("ELEMENT_INFO")));
        svcData.put("RES_INFO", new DatasetList("[]"));
        svcData.put("PRODUCT_PARAM_INFO", new DatasetList("[]"));
        svcData.put("PLAN_TYPE_CODE", condData.getString("PLAN_TYPE"));
        svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "true"));
        svcData.put("REMARK", batData.getString("REMARK", "集团E网接入点计费业务成员批量新增"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE", getVisit().getInModeCode()));

        // 处理费用
        IDataset payModeList = new DatasetList();
        payModeList = getPayMoneyList(batData);

        svcData.put("X_TRADE_PAYMONEY", payModeList);

        setFeeList(getOperFee(new DataMap()));

        IDataset feeSubList = getFeeSubList(batData);

        svcData.put("X_TRADE_FEESUB", feeSubList);
    }

    /**
     * 构造规则数据
     */
    @Override
    protected void builderRuleData(IData batData) throws Exception
    {
        super.builderRuleData(batData);

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

    /**
     * 校验参数必填
     * 
     * @param batData
     * @throws Exception
     */
    public void checkParam(IData batData) throws Exception
    {
        IDataUtil.chkParam(condData, "PLAN_TYPE");

        IDataUtil.chkParam(condData, "MEM_ROLE_B");

        IDataUtil.chkParam(condData, "PRODUCT_ID");

        IDataUtil.chkParam(condData, "ELEMENT_INFO");

        IDataUtil.chkParam(condData, "USER_ID");

        IDataUtil.chkParam(batData, "SERIAL_NUMBER");

    }
}
