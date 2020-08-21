
package com.asiainfo.veris.crm.order.soa.group.modifymemdata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GrpModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayPlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr.CheckForGrp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupCycleUtil;

/**
 * TODO 执行集团用户资料修改逻辑
 * 
 * @author zouli (mailto:zouli@lianchuang.com)
 */
public class ModifyMemData extends MemberBean
{
    protected GrpModuleData moduleData = new GrpModuleData();

    public ModifyMemData()
    {

    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        this.makUcaForMebNormal(map);
    }

    /**
     * 集团成员付费计划
     * 
     * @throws Exception
     */
    public void actTradePayPlan() throws Exception
    {
        IDataset payPlans = new DatasetList();
        IDataset payRelations = new DatasetList();
        IDataset specPays = new DatasetList();
        IDataset advPayPlans = reqData.cd.getPayPlan();

        if (IDataUtil.isEmpty(advPayPlans))
        {
            return;
        }
        reqData.setIsChange(true);
        String productId = reqData.getGrpUca().getProductId();
        String memUserId = reqData.getUca().getUser().getUserId();
        String grpUserId = reqData.getGrpUca().getUser().getUserId();
        String grpAcctId = reqData.getGrpUca().getAcctId();
        String memAcctId = reqData.getUca().getAcctId();
        String memSn = reqData.getUca().getSerialNumber();
        String firstTimeNextMonth = DiversifyAcctUtil.getFirstTimeNextAcct(memUserId);
        String lastTimeThisMonth = DiversifyAcctUtil.getLastTimeThisAcctday(memUserId, null);
        // 本账期第一天 YYYYMMDD
        String firstDayThisAcct = DiversifyAcctUtil.getFirstDayThisAcct(memUserId);
        firstDayThisAcct = java.sql.Date.valueOf(firstDayThisAcct.split(" ")[0]).toString();
        firstDayThisAcct = firstDayThisAcct.replace("-", "").substring(0, 8);
        // 本账期最后一天 YYYYMMDD：付费关系表和成员代付关系表删除时给end_cycle_id赋值
        String lastDayThisAcct = SysDateMgr.getLastCycleThisMonth();
        // 下账期第一天 YYYYMMDD
        String firstDayNextAcct = DiversifyAcctUtil.getFirstDayNextAcct(memUserId);
        firstDayNextAcct = java.sql.Date.valueOf(firstDayNextAcct.split(" ")[0]).toString();
        firstDayNextAcct = firstDayNextAcct.replace("-", "").substring(0, 8);

        boolean hasNotDel = true; // 无删除操作true，有false；如果为true，则查出已有付费计划进行删除
        for (int i = 0, sz = advPayPlans.size(); i < sz; i++)
        {
            IData advPayPlan = advPayPlans.getData(i);
            String modifyTag = advPayPlan.getString("MODIFY_TAG", "");
            String payType = advPayPlan.getString("PLAN_TYPE_CODE");
            IData pData = StaticInfoQry.getStaticInfoByTypeIdDataId("PAYPLAN_PLANTYPE", advPayPlan.getString("PLAN_TYPE_CODE", "P"));// 查询付费计划配置数据，前台只传了PLAN_TYPE_CODE
            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
            {
                IData payPlan = new DataMap();
                payPlan.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                payPlan.put("PLAN_NAME", pData.getString("DATA_NAME", "")); // 付费计划名称
                payPlan.put("PLAN_DESC", pData.getString("DATA_NAME", "")); // 付费计划描述

                // 分散账期修改
                payPlan.put("START_DATE", diversifyBooking ? firstTimeNextMonth : this.getAcceptTime()); // 生效时间
                payPlan.put("END_DATE", SysDateMgr.getTheLastTime()); // 失效时间
                payPlan.put("PLAN_TYPE_CODE", advPayPlan.getString("PLAN_TYPE")); // 付费方式：P-个人付费；G-集团付费；
                payPlan.put("PLAN_ID", SeqMgr.getPlanId()); // 集团帐户 advPayPlan.getString("PLAN_ID")
                payPlan.put("USER_ID_A", grpUserId);

                payPlans.add(payPlan);
                // 集团统付、集团付
                if (advPayPlan.getString("PLAN_TYPE").equals("G"))
                {
                    String payItem = AcctInfoQry.getPayItemCode(productId);
                    if (payItem.equals("-1") || payItem.equals(productId))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_634);
                    }
                    // add by lixiuyu@20111110 统一付费业务副卡的号码办理集团统付业务时（包括所有的集团统付办理途径均要限制），要进行提示限制办理

                    IDataset relaDatas = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(reqData.getGrpUca().getUserId(), "56", "2");
                    if (IDataUtil.isNotEmpty(relaDatas))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_635, memSn);
                    }
                    
                    //------add by chenzg@20161221---begin-----------
                    //判断该产品是否需要做欠费判断
                    IDataset paramDs = ParamInfoQry.getCommparaByCode("CSM", "1138", productId, "0898");
                    CheckForGrp.chkGrpUserIsOwnFee(reqData.getGrpUca().getUserId());
                    //------add by chenzg@20161221---end-------------

                    // 新增付费关系
                    IData data = new DataMap();
                    data.put("USER_ID", memUserId);
                    data.put("USER_ID_A", grpUserId);
                    data.put("ACCT_ID", grpAcctId);
                    data.put("ACCT_ID_B", memAcctId);
                    data.put("PAYITEM_CODE", payItem);
                    data.put("ACCT_PRIORITY", "0"); // 帐户优先级：当一个用户的某个帐目由多个帐户为其付费时的顺序
                    data.put("USER_PRIORITY", "0"); // 用户优先级：基于帐户做优惠时，作用在用户上按优先级进行
                    data.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
                    data.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
                    data.put("DEFAULT_TAG", "0"); // 默认标志
                    data.put("LIMIT_TYPE", "0"); // 限定方式：0-不限定，1-金额，2-比例
                    data.put("LIMIT", "0"); // 限定值
                    data.put("COMPLEMENT_TAG", "0"); // 是否补足：0-不补足，1-补足
                    // add by lixiuyu@20100906 判断用户是否有付费关系
                    IData inparams = new DataMap();
                    inparams.put("USER_ID", memUserId);
                    inparams.put("ACCT_ID", grpAcctId);// 取集团账户ID
                    inparams.put("PAYITEM_CODE", payItem);
                    // 分散账期修改
                    String startcycleid = diversifyBooking ? firstDayNextAcct : firstDayThisAcct;
                    inparams.put("START_CYCLE_ID", startcycleid);
                    GroupCycleUtil.dealPayRelaCycle(inparams);
                    IDataset results = PayRelaInfoQry.getPyrlByPk(inparams, null);
                    if (results != null && results.size() > 0)
                    {
                        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue()); // 状态属性：0-增加，1-删除，2-变更
                    }
                    else
                    {
                        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 状态属性：0-增加，1-删除，2-变更
                    }
                    // end by lixiuyu@20100906

                    data.put("INST_ID", SeqMgr.getInstId(reqData.getUca().getUserEparchyCode()));

                    // 分散账期修改
                    data.put("START_CYCLE_ID", startcycleid);
                    data.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());
                    payRelations.add(data);

                    // 代付表
                    data.put("ACCT_ID_B", memAcctId);
                    specPays.add(data);
                }

            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
            {
                hasNotDel = false;
                // 查询成员付费计划
                IDataset payPlanList = UserPayPlanInfoQry.getUserPayPlanByUserId(memUserId, grpUserId, null);

                if (IDataUtil.isEmpty(payPlanList))
                {
                    continue;
                }
                // 注销成员付费计划
                IData payPlan = payPlanList.getData(0);
                payPlan.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                // 分散账期修改
                payPlan.put("END_DATE", diversifyBooking ? lastTimeThisMonth : this.getAcceptTime());
                payPlans.add(payPlan);

                // 注销付费关系表和代付表
                if (advPayPlan.getString("PLAN_TYPE").equals("G"))
                {
                    // 查询代付表
                    IDataset specPayList = PayRelaInfoQry.getSpecPayByUserIdA(memUserId, grpUserId);
                    if (IDataUtil.isEmpty(specPayList))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_707, grpUserId, memUserId);
                    }

                    for (int j = 0; j < specPayList.size(); j++)
                    {
                        IData specPay = specPayList.getData(j);
                        specPay.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        specPay.put("END_CYCLE_ID", lastDayThisAcct);
                        // 删除代付表
                        specPays.add(specPay);

                        IData data = new DataMap();
                        data.put("USER_ID", specPay.getString("USER_ID"));
                        data.put("ACCT_ID", specPay.getString("ACCT_ID"));
                        data.put("PAYITEM_CODE", specPay.getString("PAYITEM_CODE"));
                        data.put("START_CYCLE_ID", specPay.getString("START_CYCLE_ID"));
                        IDataset payRelaList = PayRelaInfoQry.getPyrlByPk(data, null);

                        if (IDataUtil.isNotEmpty(payRelaList))
                        {
                            IData payRela = payRelaList.getData(0);
                            payRela.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            payRela.put("END_CYCLE_ID", lastDayThisAcct);
                            // 删除付费关系表
                            payRelations.add(payRela);
                        }
                    }
                }

            }
        }
        if (hasNotDel)
        {
            // 查询成员付费计划
            IDataset payPlanList = UserPayPlanInfoQry.getUserPayPlanByUserId(memUserId, grpUserId, null);
            if (IDataUtil.isNotEmpty(payPlanList))
            {
                // 注销成员付费计划
                IData hasPayPlan = payPlanList.getData(0);
                hasPayPlan.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                // 分散账期修改
                hasPayPlan.put("END_DATE", diversifyBooking ? lastTimeThisMonth : this.getAcceptTime());
                payPlans.add(hasPayPlan);

                // 注销付费关系表和代付表
                if ("G".equals(hasPayPlan.getString("PLAN_TYPE_CODE")))
                {
                    // 查询代付表
                    IDataset specPayList = PayRelaInfoQry.getSpecPayByUserIdA(memUserId, grpUserId);
                    if (IDataUtil.isEmpty(specPayList))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_707, grpUserId, memUserId);
                    }

                    for (int j = 0; j < specPayList.size(); j++)
                    {
                        IData specPay = specPayList.getData(j);
                        specPay.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        specPay.put("END_CYCLE_ID", lastDayThisAcct);
                        // 删除代付表
                        specPays.add(specPay);

                        IData data = new DataMap();
                        data.put("USER_ID", specPay.getString("USER_ID"));
                        data.put("ACCT_ID", specPay.getString("ACCT_ID"));
                        data.put("PAYITEM_CODE", specPay.getString("PAYITEM_CODE"));
                        data.put("START_CYCLE_ID", specPay.getString("START_CYCLE_ID"));
                        IDataset payRelaList = PayRelaInfoQry.getPyrlByPk(data, null);

                        if (IDataUtil.isNotEmpty(payRelaList))
                        {
                            IData payRela = payRelaList.getData(0);
                            payRela.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            payRela.put("END_CYCLE_ID", lastDayThisAcct);
                            // 删除付费关系表
                            payRelations.add(payRela);
                        }
                    }
                }
            }
        }
        this.addTradeUserPayplan(payPlans);
        this.addTradePayrelation(payRelations);
        this.addTradeUserSpecialepay(specPays);

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
        // 集团成员付费计划
        actTradePayPlan();

    }

    @Override
    protected final void initProductCtrlInfo() throws Exception
    {

        String productId = reqData.getGrpUca().getProductId();

        getProductCtrlInfo(productId, BizCtrlType.ModifyMember);

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

    protected void modTradeData() throws Exception
    {
        super.modTradeData();
        // 无资料修改
        if (!reqData.getIsChange())
        {
            CSAppException.apperr(GrpException.CRM_GRP_640);
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
