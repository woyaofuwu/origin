
package com.asiainfo.veris.crm.order.soa.group.modifyuserdata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GrpModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayPlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

/**
 * TODO 执行集团用户资料修改逻辑
 * 
 * @author zouli (mailto:zouli@lianchuang.com)
 */
public class ModifyUserData extends GroupBean
{
    protected GrpModuleData moduleData = new GrpModuleData();

    public ModifyUserData()
    {

    }

    /**
     * 集团付费计划
     * 
     * @throws Exception
     */
    public void actTradePayPlan() throws Exception
    {

        // IDataset advPayPlans = tradeData.getPayItemDesign();
        IData acctInfo = reqData.getUca().getAccount().toData();
        String productId = reqData.getUca().getProductId();
        if (IDataUtil.isNotEmpty(acctInfo))
        {
            IDataset payPlanList = reqData.cd.getPayPlan();

            if (IDataUtil.isEmpty(payPlanList))
            {
                return;
            }
            reqData.setIsChange(true);
            for (int i = 0, sz = payPlanList.size(); i < sz; i++)
            {
                IData payPlan = payPlanList.getData(i);
                String modifyTag = payPlan.getString("MODIFY_TAG", "");
                String payType = payPlan.getString("PLAN_TYPE_CODE");
                IData pData = StaticInfoQry.getStaticInfoByTypeIdDataId("PAYPLAN_PLANTYPE", payPlan.getString("PLAN_TYPE_CODE", "P"));// 查询付费计划配置数据，前台只传了PLAN_TYPE_CODE
                if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
                {
                    String plan_id = SeqMgr.getPlanId();
                    payPlan.put("PLAN_ID", plan_id); // 付费计划标识

                    payPlan.put("PLAN_NAME", pData.getString("DATA_NAME", "")); // 付费计划名称
                    payPlan.put("PLAN_DESC", pData.getString("DATA_NAME", "")); // 付费计划描述
                    payPlan.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    payPlan.put("START_DATE", getAcceptTime()); // 生效时间
                    payPlan.put("END_DATE", payPlan.getString("END_DATE", SysDateMgr.getTheLastTime())); // 失效时间

                    payPlan.put("PLAN_TYPE_CODE", payType); // 付费方式：P-个人付费；G-集团付费；C-定制；T-统付

                    // RSRV_STR2 保存账目项
                    if (payType.equals("G"))
                    {
                        payPlan.put("RSRV_STR2", AcctInfoQry.getPayItemCode(productId));
                    }
                    else if (payType.equals("T"))
                    {
                        payPlan.put("RSRV_STR2", "-1");
                    }

                    payPlan.put("RSRV_STR3", payPlan.getString("PLAN_MODE_CODE", "0"));

                }
                else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
                {
                    IData param = new DataMap();
                    param.put("USER_ID", reqData.getUca().getUserId());
                    param.put("USER_ID_A", "-1");
                    param.put("PLAN_TYPE_CODE", payType);
                    IDataset ds = UserPayPlanInfoQry.getUserPayPlanByPlanType(param);
                    if (IDataUtil.isNotEmpty(ds))
                    {
                        IData data = ds.getData(0);
                        payPlan.put("PLAN_ID", data.getString("PLAN_ID")); // 付费计划标识
                        payPlan.put("START_DATE", data.getString("START_DATE")); // 生效时间
                        payPlan.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        payPlan.put("END_DATE", getAcceptTime()); // 失效时间
                    }
                }
            }

            this.addTradeUserPayplan(payPlanList);

        }

    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        // 付费计划
        makReqDataPlanInfo();
        // 集团付费计划
        actTradePayPlan();

    }

    @Override
    protected final void initProductCtrlInfo() throws Exception
    {

        String productId = reqData.getUca().getProductId();

        getProductCtrlInfo(productId, BizCtrlType.ModifyUser);

    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);

        moduleData.getMoudleInfo(map);
    }

    private void makReqDataPlanInfo() throws Exception
    {
        IDataset payPlanList = moduleData.getPlanInfo();

        reqData.cd.putPayPlan(payPlanList);
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUcaForGrpNormal(map);
    }

    protected void modTradeData() throws Exception
    {
        super.modTradeData();
        // 无资料修改
        if (!reqData.getIsChange())
        {
            CSAppException.apperr(GrpException.CRM_GRP_368);
        }
    }

    @Override
    protected void setTradeUserPayplan(IData map) throws Exception
    {
        super.setTradeUserPayplan(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId())); // 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1")); // 用户标识A
    }

}
