
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class BatGrpMemCreateSVC extends GroupBatService
{

    private static final long serialVersionUID = -2806427628511184500L;

    private String service_name = "CS.CreateGroupMemberSvc.createGroupMember"; // 服务名

    private String getService_name()
    {
        return service_name;
    }

    protected void setService_name(String service_name)
    {
        this.service_name = service_name;
    }

    @Override
    protected void batInitialSub(IData batData) throws Exception
    {
        checkParam(batData);

        String productId = condData.getString("PRODUCT_ID");

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);

        if ("BOSG".equals(brandCode))
        {
            setService_name("CS.CreateBBossMemSVC.crtOrder"); // 子类可改
        }
        else if ("10005742".equals(productId) || "10009150".equals(productId))
        {
            setService_name("SS.CreateAdcGroupMemberSVC.crtOrder"); // 子类可改
        }
        else
        {
            setService_name("CS.CreateGroupMemberSvc.createGroupMember"); // 子类可改
        }

        svcName = getService_name();

        batData.put(BIZ_CTRL_TYPE, BizCtrlType.CreateMember);
    }

    @Override
    protected void batValidateSub(IData batData) throws Exception
    {

        String user_id = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID

        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

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

        // 判断成员号码是否为集团号码
        if (isGroupSerialNumber(serial_number))
        {
            CSAppException.apperr(GrpException.CRM_GRP_120, serial_number);
        }

        // 校验重复订购
        IData data = new DataMap();
        String user_id_b = getMebUcaData().getUserId(); // 成员user_id
        data.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        data.put("USER_ID", user_id);// 集团用户编码
        data.put("MEM_USER_ID", user_id_b);// 成员用户编码
        data.put("MEM_EPARCHY_CODE", getMebUcaData().getUser().getEparchyCode());// 成员用户地州

        boolean uuFlag = super.chkIsExitsRelation(data);// 存在UU关系返回true
        if (uuFlag)
        {
            CSAppException.apperr(GrpException.CRM_GRP_671, serial_number, user_id);
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
        svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "false"));
        svcData.put("REMARK", batData.getString("REMARK", "pushmail批量成员新增"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE", getVisit().getInModeCode()));

        String sms_flag = batData.getString("SMS_FLAG");
        svcData.put("IF_SMS", (StringUtils.isBlank(sms_flag) || StringUtils.equals("0", sms_flag)) ? "false" : "true");
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
