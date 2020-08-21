
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayPlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class BatPayrelationChgSVC extends GroupBatService
{
    private static final String SERVICE_NAME = "SS.ModifyMemDataSVC.crtTrade";

    /**
     * 作用： 批量初始化其他信息(子类继承)
     * 
     * @throws Exception
     */
    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;

        batData.put(BIZ_CTRL_TYPE, BizCtrlType.ModifyMember);
        condData.put("PRODUCT_ID", "6200"); // j2ee 测试写死
    }

    /**
     * 作用： 批量校验其他信息(子类实现)
     * 
     * @throws Exception
     */
    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        String grpSn = IDataUtil.chkParam(condData, "SERIAL_NUMBER");// 集团用户服务号码
        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
        // 校验集团用户信息
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", grpSn);
        chkGroupUCABySerialNumber(inparam);

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
        // 信控判断
        String id = getGrpUcaData().getProductId();
        String idType = "P";
        String attrObj = "PayRelChg";
        String attrCode = "EsopBat";
        IDataset attrBizs = AttrBizInfoQry.getBizAttr(id, idType, attrObj, attrCode, null);
        if (IDataUtil.isNotEmpty(attrBizs))
        {
            String grpAcctId = getGrpUcaData().getAcctId();
            String memUserId = getMebUcaData().getUserId();
            IDataset creditMemAcctInfo = PayRelaInfoQry.getPayrelationByUserIdAndAcctId(grpAcctId, memUserId, null);
            for (int i = 0; i < creditMemAcctInfo.size(); i++)
            {
                IData tempAcctInfo = creditMemAcctInfo.getData(i);
                if ("2".equals(tempAcctInfo.getString("ACT_TAG", "")))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_704);
                }
            }
        }
    }

    /**
     * 构造规则数据
     */
    @Override
    protected void builderRuleData(IData batData) throws Exception
    {
        super.builderRuleData(batData);
        ruleData.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        ruleData.put("RULE_BIZ_KIND_CODE", "chk");
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
        String grpUserId = getGrpUcaData().getUserId();
        String memUserId = getMebUcaData().getUserId();
        String productId = getGrpUcaData().getProductId();
        String selPlanType = IDataUtil.getMandaData(condData, "PLAN_TYPE");
        if ("G".equals(selPlanType))
        {
            String subsysCode = "CGM";
            String paramAttr = "1";
            String paramCode = productId;
            IDataset payItems = CommparaInfoQry.getPayItemsParam(subsysCode, paramAttr, paramCode, null); // TD_S_COMMPARA", "SEL_PAY_ITEM
            IDataset payitems1 = new DatasetList();
            if (payItems != null && payItems.size() > 0)
            {
                for (int i = 0; i < payItems.size(); i++)
                {
                    IData paycode = payItems.getData(i);
                    IData data = new DataMap();
                    data.put("PAYITEM_CODE", paycode.getString("PARA_CODE1"));
                    payitems1.add(data);
                }
            }
            // payItem.put("RSRV_STR2", payitems1);
        }
        IDataset planInfos = new DatasetList();
        // 集团付费计划
        IDataset groupPayPlans = UserPayPlanInfoQry.getGrpPayPlanByUserId(grpUserId, "-1");

        if (IDataUtil.isNotEmpty(groupPayPlans))
        {
            for (int i = 0; i < groupPayPlans.size(); i++)
            {
                IData groupPayPlan = (IData) groupPayPlans.get(i);

                if (selPlanType.equals(groupPayPlan.getString("PLAN_TYPE_CODE")))
                {
                    IData planInfo = new DataMap();
                    planInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    planInfo.put("PAY_TYPE_CODE", selPlanType);
                    planInfo.put("PLAN_TYPE", selPlanType);
                    planInfo.put("PAY_TYPE", groupPayPlan.getString("PLAN_NAME"));
                    planInfo.put("PLAN_ID", groupPayPlan.getString("PLAN_ID"));
                    planInfos.add(planInfo);
                    break;
                }
            }
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_705);
        }

        IDataset payPlans = UserPayPlanInfoQry.getUserPayPlanByUserId(memUserId, grpUserId, null);

        if (IDataUtil.isNotEmpty(payPlans))
        {
            for (int i = 0; i < payPlans.size(); i++)
            {
                IData payPlan = (IData) payPlans.get(i);

                if (selPlanType.equals(payPlan.getString("PLAN_TYPE_CODE")))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_706, payPlan.getString("PLAN_NAME"));
                }
                else
                {
                    payPlan.put("STATE", "DEL");
                    payPlan.put("PLAN_TYPE", payPlan.getString("PLAN_TYPE_CODE"));
                    IData planInfo = new DataMap();
                    planInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    planInfo.put("PAY_TYPE_CODE", payPlan.getString("PLAN_TYPE_CODE"));
                    planInfo.put("PLAN_TYPE", payPlan.getString("PLAN_TYPE_CODE"));
                    planInfo.put("PAY_TYPE", payPlan.getString("PLAN_NAME"));
                    planInfo.put("PLAN_ID", payPlan.getString("PLAN_ID"));
                    planInfos.add(planInfo);
                }
            }

        }

        svcData.put("USER_ID", grpUserId);
        svcData.put("PRODUCT_ID", productId);
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        svcData.put("PLAN_INFO", planInfos); // 付费计划
    }
}
