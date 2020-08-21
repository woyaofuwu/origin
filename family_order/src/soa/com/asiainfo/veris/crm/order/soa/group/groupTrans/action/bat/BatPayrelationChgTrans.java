
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayPlanInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;

public class BatPayrelationChgTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {

        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        condData.put("PRODUCT_ID", "6200"); // j2ee 测试写死

        String grpSn = IDataUtil.chkParam(condData, "SERIAL_NUMBER");// 集团用户服务号码
        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 查询成员用户信息
        IData memuserinfo = UserInfoQry.getUserInfoBySN(serial_number);

        if (IDataUtil.isEmpty(memuserinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        // 查询集团用户信息
        IData grpuserInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(grpSn);

        if (IDataUtil.isEmpty(grpuserInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_017, grpSn);
        }

        String grpUserId = grpuserInfo.getString("USER_ID");
        // 查询集团账户信息
        IData grpAcctData = GroupBatTransUtil.getGrpAcctData(grpUserId);

        String grpAcctId = grpAcctData.getString("ACCT_ID");
        String memUserId = memuserinfo.getString("USER_ID");

        // 信控判断
        String id = grpuserInfo.getString("PRODUCT_ID", "6200");
        String idType = "P";
        String attrObj = "PayRelChg";
        String attrCode = "EsopBat";
        IDataset attrBizs = AttrBizInfoQry.getBizAttr(id, idType, attrObj, attrCode, null);

        if (IDataUtil.isNotEmpty(attrBizs))
        {
            IDataset creditMemAcctInfo = PayRelaInfoQry.getPayrelationByUserIdAndAcctId(grpAcctId, memUserId, null);
            for (int i = 0, size = creditMemAcctInfo.size(); i < size; i++)
            {
                IData tempAcctInfo = creditMemAcctInfo.getData(i);
                if ("2".equals(tempAcctInfo.getString("ACT_TAG", "")))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_704);
                }
            }
        }

        //集团彩铃成员付费关系的批量变更的校验是否是成员
        IDataset uuInfos = RelaUUInfoQry.checkMemRelaByUserIdb(grpUserId,memUserId,"26",null);
        if(IDataUtil.isEmpty(uuInfos)){
            CSAppException.apperr(GrpException.CRM_GRP_904);
        }
        
        batData.put("GRP_USER_ID", grpUserId);
        batData.put("MEM_USER_ID", memUserId);
        batData.put("GRP_PRODUCT_ID", grpuserInfo.getString("PRODUCT_ID", "6200"));
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        String grpUserId = batData.getString("GRP_USER_ID");
        String memUserId = batData.getString("MEM_USER_ID");
        String productId = batData.getString("GRP_PRODUCT_ID");
        String selPlanType = IDataUtil.getMandaData(condData, "PLAN_TYPE");

        if ("G".equals(selPlanType))
        {
            String subsysCode = "CGM";
            String paramAttr = "1";
            String paramCode = productId;
            IDataset payItems = CommparaInfoQry.getPayItemsParam(subsysCode, paramAttr, paramCode, null); // TD_S_COMMPARA", "SEL_PAY_ITEM
            IDataset payitems1 = new DatasetList();
            if (IDataUtil.isNotEmpty(payItems))
            {
                for (int i = 0, size = payItems.size(); i < size; i++)
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
            for (int i = 0, size = groupPayPlans.size(); i < size; i++)
            {
                IData groupPayPlan = groupPayPlans.getData(i);

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
            for (int i = 0, size = payPlans.size(); i < size; i++)
            {
                IData payPlan = payPlans.getData(i);

                if (selPlanType.equals(payPlan.getString("PLAN_TYPE_CODE")))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_706, payPlan.getString("PLAN_TYPE_CODE"));
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
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("PLAN_INFO", planInfos); // 付费计划
    }

}
