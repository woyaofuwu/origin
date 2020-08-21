
package com.asiainfo.veris.crm.order.soa.group.modifypayrelation;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.InModeCodeUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr.CheckForGrp;

public class VgpoPayRelationChgBean extends MemberBean
{
    protected IData baseCommInfo = new DataMap();

    protected VgpoPayRelaChgReqData reqData = null;

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new VgpoPayRelaChgReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (VgpoPayRelaChgReqData) getBaseReqData();
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "3601";
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String startCycleId = map.getString("START_CYCLE_ID");
        String endCycleId = map.getString("END_CYCLE_ID");

        baseCommInfo.put("START_CYCLE_ID", startCycleId);
        baseCommInfo.put("END_CYCLE_ID", endCycleId);
        baseCommInfo.put("LIMIT_TYPE", map.getString("LIMIT_TYPE"));
        baseCommInfo.put("LIMIT", map.getString("LIMIT", "0"));
        baseCommInfo.put("COMPLEMENT_TAG", map.getString("COMPLEMENT_TAG"));

        baseCommInfo.put("ACCT_PRIORITY", map.getString("ACCT_PRIORITY"));
        baseCommInfo.put("PAYITEM_CODE", map.getString("PAYITEM_CODE"));
        baseCommInfo.put("ACCT_ID", map.getString("ACCT_ID"));
        baseCommInfo.put("USER_ID_A", map.getString("USER_ID"));
        baseCommInfo.put("OPER_TYPE", map.getString("OPER_TYPE"));

        // 此处逻辑为接口or批量使用,重新计算时间
        if (!InModeCodeUtil.isIntf(CSBizBean.getVisit().getInModeCode(), map.getString(GroupBaseConst.X_SUBTRANS_CODE), map.getString("BATCH_ID")))
        {
            return;
        }

        IData mebUserAcctDay = DiversifyAcctUtil.getUserAcctDay(reqData.getUca().getUserId());
        if (IDataUtil.isEmpty(mebUserAcctDay))
        {
            CSAppException.apperr(GrpException.CRM_GRP_657, reqData.getUca().getSerialNumber());
        }
        // 排除非自然月账期的情况,此处在tradeBaseBean中有统一校验,见checkMebDiversify方法

        // DiversifyAcctUtil.checkUserAcctDayWithWarn(mebUserAcctDay, sn, "1", true);
        // 获取用户账期分布
        String userAcctDayDistribution = DiversifyAcctUtil.userAcctDayDistribution(mebUserAcctDay, "1");

        // 如果START_CYCLE_ID为6位,则转为8位
        if (startCycleId.length() == 6)
        {
            startCycleId = startCycleId + "01";
        }
        // 如果END_CYCLE_ID为6位,则转为8位
        if (endCycleId.length() == 6)
        {
            // 获取本月末时间YYYY-MM-DD
            endCycleId = SysDateMgr.decodeTimestamp(endCycleId + "01", SysDateMgr.PATTERN_STAND_YYYYMMDD);
            endCycleId = SysDateMgr.getDateLastMonthSec(endCycleId);
            endCycleId = SysDateMgr.decodeTimestamp(endCycleId, SysDateMgr.PATTERN_TIME_YYYYMMDD);
        }

        // YYYYMMDD格式转化为YYYY-MM-DD格式
        String startCycleDate = startCycleId.substring(0, 4) + "-" + startCycleId.substring(4, 6) + "-" + startCycleId.substring(6, 8);

        String firstDate = AcctTimeEnvManager.getAcctTimeEnv().getFirstDate();
        String nextAcctDay = AcctTimeEnvManager.getAcctTimeEnv().getNextAcctDay();
        String acctDay = AcctTimeEnvManager.getAcctTimeEnv().getAcctDay();
        String startDate = AcctTimeEnvManager.getAcctTimeEnv().getStartDate();
        String nextStartDate = AcctTimeEnvManager.getAcctTimeEnv().getNextStartDate();
        String nextFirstDate = AcctTimeEnvManager.getAcctTimeEnv().getNextFirstDate();

        // 重新计算账期开始时间
        if (GroupBaseConst.UserDaysDistribute.TRUE.getValue().equals(userAcctDayDistribution))
        {
            // 获取传入时间startCycleDate的开始账期
            String firstDayThisAcct = SysDateMgr.getFirstCycleDayThisAcct(startCycleDate, acctDay, firstDate, startDate);
            startCycleId = firstDayThisAcct.replaceAll("-", "");

        }
        else if (GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue().equals(userAcctDayDistribution))
        {
            String firstDayThisAcct = mebUserAcctDay.getString("FIRST_DAY_NEXTACCT");
            int months = SysDateMgr.monthInterval(SysDateMgr.getNowCyc(), startCycleId.substring(0, 6));
            if (months > 1)
            {
                // 获取传入时间startCycleDate的开始账期
                firstDayThisAcct = SysDateMgr.getFirstCycleDayThisAcct(startCycleDate, nextAcctDay, nextFirstDate, nextStartDate);
            }
            startCycleId = firstDayThisAcct.replaceAll("-", "");
        }

        baseCommInfo.put("START_CYCLE_ID", startCycleId);
        baseCommInfo.put("END_CYCLE_ID", endCycleId);

        // 终止付费关系用到
        startCycleId = startCycleId.substring(0, 4) + "-" + startCycleId.substring(4, 6) + "-" + startCycleId.substring(6, 8);
        baseCommInfo.put("DEL_CYCLE_ID", SysDateMgr.addDays(startCycleId, -1).replaceAll("-", ""));

        if ("2".equals(map.getString("OPER_TYPE", "")))
            map.put("REMARK", "统付的集团欠费，自动终止统一付费关系");
    }

    protected void makUca(IData map) throws Exception
    {
        super.makUcaForMebNormal(map); // 必须包含成员SERIAL_NUMBER和集团的USER_ID
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        actPayRela();

        actSpecialePayReg();

        String serialNumber = reqData.getGrpUca().getSerialNumber();
        String operType = baseCommInfo.getString("OPER_TYPE");
        if ("V0HN001010".equals(serialNumber) && !"2".equals(operType))
        {
            actOtherTrade();
        }
    }

    public void actOtherTrade() throws Exception
    {
        IData othe = new DataMap();
        othe.put("USER_ID", reqData.getGrpUca().getUserId());
        othe.put("RSRV_VALUE_CODE", "30");
        othe.put("RSRV_VALUE", "50");
        othe.put("STATE", "ADD");
        othe.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        othe.put("START_DATE", getAcceptTime());
        othe.put("END_DATE", SysDateMgr.getEndCycle20501231());
        othe.put("INST_ID", SeqMgr.getInstId());

        addTradeOther(othe);
    }

    /**
     * 生成付费关系子台帐
     */
    private void actPayRela() throws Exception
    {
        String startcycleid = baseCommInfo.getString("START_CYCLE_ID");
        String payitemcode = baseCommInfo.getString("PAYITEM_CODE");
        String operType = baseCommInfo.getString("OPER_TYPE");

        boolean ifaddPay = true;

        String acctId = reqData.getGrpUca().getAcctId();// 集团账户id //baseCommInfo.getString("ACCT_ID");
        String userId = reqData.getUca().getUserId();// 成员用户标识
        String eparchyCode = reqData.getUca().getUser().getEparchyCode();

        IData param = new DataMap();
        param.put("USER_ID", userId);

        IDataset dataset = new DatasetList();

        IDataset acctInfos = AcctInfoQry.getPayRelaByUserEffected(param, eparchyCode);

        if ("2".equals(operType))
        {
            if (IDataUtil.isNotEmpty(acctInfos))
            {
                for (int i = 0, size = acctInfos.size(); i < size; i++)
                {
                    IData acctInfo = acctInfos.getData(i);
                    if (acctId.equals(acctInfo.getString("ACCT_ID")))
                    {
                        // 分散账期修改
                        acctInfo.put("END_CYCLE_ID", baseCommInfo.getString("DEL_CYCLE_ID"));
                        acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        dataset.add(acctInfo);
                    }
                }
            }
        }
        else
        {
            if (IDataUtil.isNotEmpty(acctInfos))
            {
                IDataset vpnpayitems = StaticUtil.getStaticList("GROUP_GPON_BAT_PAYPARAMCODE");

                if (IDataUtil.isNotEmpty(vpnpayitems))
                {

                    for (int i = 0, size = acctInfos.size(); i < size; i++)
                    {
                        boolean isvpnitem = false;
                        IData acctInfo = acctInfos.getData(i);
                        if (!acctId.equals(acctInfo.getString("ACCT_ID")))
                            continue;

                        for (int j = 0, vsize = vpnpayitems.size(); j < vsize; j++)
                        {
                            IData dt = vpnpayitems.getData(j);
                            if (acctInfo.getString("PAYITEM_CODE").equals(dt.getString("DATA_ID")))
                            {
                                isvpnitem = true;
                                break;
                            }
                        }

                        if (isvpnitem)
                        {
                            if ((!acctInfo.getString("START_CYCLE_ID", "").equals(startcycleid)) || (!acctInfo.getString("PAYITEM_CODE", "").equals(payitemcode)))
                            {
                                // 分散账期修改
                                acctInfo.put("END_CYCLE_ID", baseCommInfo.getString("DEL_CYCLE_ID"));
                                acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                dataset.add(acctInfo);
                            }
                            else
                            {
                                ifaddPay = false;

                                acctInfo.put("END_CYCLE_ID", baseCommInfo.getString("END_CYCLE_ID"));
                                acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                acctInfo.put("COMPLEMENT_TAG", baseCommInfo.getString("COMPLEMENT_TAG"));
                                acctInfo.put("LIMIT_TYPE", baseCommInfo.getString("LIMIT_TYPE"));
                                acctInfo.put("LIMIT", baseCommInfo.getString("LIMIT"));
                                dataset.add(acctInfo);
                            }
                        }
                    }
                }
            }

            if (ifaddPay)
            {
                IData info = new DataMap();

                info.put("USER_ID", userId);
                info.put("ACCT_ID", acctId);
                info.put("PAYITEM_CODE", baseCommInfo.getString("PAYITEM_CODE"));
                info.put("ACCT_PRIORITY", "0");
                info.put("USER_PRIORITY", "0");
                info.put("BIND_TYPE", "0");
                info.put("ACT_TAG", "1");
                info.put("DEFAULT_TAG", "0");
                info.put("LIMIT_TYPE", baseCommInfo.getString("LIMIT_TYPE"));
                info.put("LIMIT", baseCommInfo.getString("LIMIT"));
                info.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                info.put("START_CYCLE_ID", baseCommInfo.getString("START_CYCLE_ID"));
                info.put("END_CYCLE_ID", baseCommInfo.getString("END_CYCLE_ID"));
                info.put("COMPLEMENT_TAG", baseCommInfo.getString("COMPLEMENT_TAG"));

                info.put("INST_ID", SeqMgr.getInstId());

                dataset.add(info);
            }

        }

        this.addTradePayrelation(dataset);
    }

    /**
     * 集团代付费子表
     * 
     * @throws Exception
     * @author xj
     */
    private void actSpecialePayReg() throws Exception
    {

        String startcycleid = baseCommInfo.getString("START_CYCLE_ID");
        String payitemcode = baseCommInfo.getString("PAYITEM_CODE");
        String operType = baseCommInfo.getString("OPER_TYPE");

        boolean ifaddPay = true;

        String userId = reqData.getUca().getUserId();
        String userIdA = reqData.getGrpUca().getUserId();
        String acctId = reqData.getGrpUca().getAcctId();

        IDataset specPays = UserSpecialPayInfoQry.qryUserSpecialPay(userId, userIdA);

        IDataset dataset = new DatasetList();

        if ("2".equals(operType))
        {
            if (IDataUtil.isNotEmpty(specPays))
            {
                for (int i = 0, size = specPays.size(); i < size; i++)
                {
                    IData acctInfo = specPays.getData(i);
                    if (acctId.equals(acctInfo.getString("ACCT_ID")))
                    {
                        // 分散账期修改
                        acctInfo.put("END_CYCLE_ID", baseCommInfo.getString("DEL_CYCLE_ID"));
                        acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        dataset.add(acctInfo);
                    }
                }
            }
        }
        else
        {
            if (IDataUtil.isNotEmpty(specPays))
            {
                for (int i = 0, size = specPays.size(); i < size; i++)
                {
                    IData acctInfo = specPays.getData(i);
                    if (!acctId.equals(acctInfo.getString("ACCT_ID")))
                        continue;

                    if ((!acctInfo.getString("START_CYCLE_ID", "").equals(startcycleid)) || (!acctInfo.getString("PAYITEM_CODE", "").equals(payitemcode)))
                    {
                        // 分散账期修改
                        acctInfo.put("END_CYCLE_ID", baseCommInfo.getString("DEL_CYCLE_ID"));
                        acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        dataset.add(acctInfo);
                    }
                    else
                    {
                        ifaddPay = false;
                        acctInfo.put("END_CYCLE_ID", baseCommInfo.getString("END_CYCLE_ID"));
                        acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        acctInfo.put("COMPLEMENT_TAG", baseCommInfo.getString("COMPLEMENT_TAG"));
                        acctInfo.put("LIMIT_TYPE", baseCommInfo.getString("LIMIT_TYPE"));
                        acctInfo.put("LIMIT", baseCommInfo.getString("LIMIT"));
                        dataset.add(acctInfo);
                    }
                }
            }

            if (ifaddPay)
            {
                IData info = new DataMap();

                info.put("USER_ID", userId); // 成员用户标识
                info.put("USER_ID_A", userIdA); // 集团用户标识
                info.put("ACCT_ID", acctId); // 集团账户标识
                info.put("ACCT_ID_B", reqData.getUca().getAcctId()); // 成员账户标识
                info.put("PAYITEM_CODE", baseCommInfo.getString("PAYITEM_CODE")); // 付费帐目编码
                info.put("ACCT_PRIORITY", "0");
                info.put("USER_PRIORITY", "0");
                info.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
                info.put("ACT_TAG", "1");
                info.put("DEFAULT_TAG", "0");
                info.put("LIMIT_TYPE", baseCommInfo.getString("LIMIT_TYPE")); // 限定方式：0-不限定，1-金额，2-比例
                info.put("LIMIT", baseCommInfo.getString("LIMIT")); // 限定值
                info.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                info.put("START_CYCLE_ID", baseCommInfo.getString("START_CYCLE_ID")); // 起始帐期
                info.put("END_CYCLE_ID", baseCommInfo.getString("END_CYCLE_ID")); // 终止帐期
                info.put("COMPLEMENT_TAG", baseCommInfo.getString("COMPLEMENT_TAG")); // 是否补足：0-不补足，1-补足

                info.put("INST_ID", SeqMgr.getInstId());

                dataset.add(info);
            }
        }

        this.addTradeUserSpecialepay(dataset);

    }

    /**
     * 重写调用规则校验
     */
    protected void chkTradeBefore(IData map) throws Exception
    {

        IData inData = new DataMap();
        inData.put("CHK_FLAG", "VGPOPayRelaAdv");
        inData.put("TRADE_TYPE_CODE", setTradeTypeCode());
        inData.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER"));
        inData.put("PRODUCT_ID", "-1");
        inData.put("USER_ID", reqData.getUca().getUserId());
        inData.put("CUST_ID", reqData.getGrpUca().getCustId());
        inData.put("ACCT_ID", reqData.getUca().getAcctId());
        inData.put("OPER_TYPE", baseCommInfo.getString("OPER_TYPE"));
        CheckForGrp.chkPayRelaAdvChg(inData);
    }

}
