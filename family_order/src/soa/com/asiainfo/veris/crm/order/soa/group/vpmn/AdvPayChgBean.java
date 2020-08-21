
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.AcctDateUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class AdvPayChgBean extends ChangeMemElement
{
    private static final Logger logger = Logger.getLogger(AdvPayChgBean.class);
    public AdvPayChgBean()
    {

    }

    private final IData baseCommInfo = new DataMap();
    private AdvPayChgReqData reqData = null;
    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new AdvPayChgReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (AdvPayChgReqData) getBaseReqData();
    }

    @Override
    protected void initProductCtrlInfo() throws Exception
    {
        // 跳过成员是否VPMN的验证
        // String productId = reqData.getGrpUca().getProductId();
        // getProductCtrlInfo(productId, BizCtrlType.ChangeMemberDis);
    }

    /**
     * 生成登记信息
     * 
     * @author xiajj
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author lixiuyu@20101115
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        setPayRela(); // 付费关系表
        specialePay(); // 代付关系表

        if ("V0HN001010".equals(reqData.getGrpUca().getSerialNumber()) && !"2".equals(reqData.getOPER_TYPE()))
        {
            actOtherTrade();
        }
    }

    /**
     * 生成付费关系子台帐
     * 
     * @throws Exception
     */

    public void setPayRela() throws Exception
    {
        // add by lim3
        String startcycleid = reqData.getSTART_CYCLE_ID();
        String payitemcode = reqData.getPAYITEM_CODE();
        boolean ifaddPay = true;
        // ifaddPay 标示是否需要新增payrelation数据 只要当前的优惠不与要变更的主键冲突（start_cycle_id,payitem_code）

        IData param = new DataMap();
        param.put("USER_ID", reqData.getUca().getUserId()); // 成员
        String acctId = reqData.getACCT_ID(); // 集团账户id
        String eparchyCode = reqData.getUca().getUserEparchyCode();
        IDataset dataset = new DatasetList();
        IDataset acctInfos = AcctInfoQry.getPayRelaByUserEffected(param, eparchyCode);// 查成员非默认付费关系
        String operType = reqData.getOPER_TYPE();
        if ("2".equals(operType))
        {
            if (IDataUtil.isNotEmpty(acctInfos))
            {
                for (int i = 0; i < acctInfos.size(); i++)
                {
                    IData acctInfo = (IData) acctInfos.get(i);
                    if (acctId.equals(acctInfo.getString("ACCT_ID")))
                    {
                        // 分散账期修改
                        acctInfo.put("END_CYCLE_ID", reqData.getDEL_CYCLE_ID());
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
                IDataset vpnpayitems = StaticUtil.getStaticList("GROUP_BAT_PAYPARAMCODE"); // VPMN账目
                for (int i = 0; i < acctInfos.size(); i++)
                {
                    // add by qiand 20100720
                    boolean isvpnitem = false; // 账目是否是VPMN账目
                    IData acctInfo = (IData) acctInfos.get(i);
                    if (acctId.equals(acctInfo.getString("ACCT_ID")))
                    {

                        if (IDataUtil.isEmpty(vpnpayitems))
                        {
                            break;
                        }
                        for (int j = 0; j < vpnpayitems.size(); j++)
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
                            if (!acctInfo.getString("START_CYCLE_ID", "").equals(startcycleid) || !acctInfo.getString("PAYITEM_CODE", "").equals(payitemcode))
                            {
                                // 现有有效的开始账期和当前账期不同时，插入两条记录
                                // 分散账期修改
                                acctInfo.put("END_CYCLE_ID", reqData.getDEL_CYCLE_ID());
                                acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                dataset.add(acctInfo);
                            }
                            else
                            {
                                ifaddPay = false;
                                // 现有有效账期和当前账期相同时，为防止互斥，插入一条更新记录
                                acctInfo.put("END_CYCLE_ID", reqData.getEND_CYCLE_ID());
                                acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                acctInfo.put("COMPLEMENT_TAG", reqData.getCOMPLEMENT_TAG());
                                acctInfo.put("LIMIT_TYPE", reqData.getLIMIT_TYPE());
                                acctInfo.put("LIMIT", reqData.getLIMIT());
                                dataset.add(acctInfo);
                            }
                        }
                    }
                }
            }
            if (ifaddPay)
            {
                IData info = new DataMap();

                info.put("USER_ID", reqData.getUca().getUserId());
                info.put("ACCT_ID", reqData.getACCT_ID());
                info.put("PAYITEM_CODE", reqData.getPAYITEM_CODE());
                info.put("ACCT_PRIORITY", "0");
                info.put("USER_PRIORITY", "0");
                info.put("BIND_TYPE", "0");
                info.put("ACT_TAG", "1");
                info.put("DEFAULT_TAG", "0");
                info.put("LIMIT_TYPE", reqData.getLIMIT_TYPE());
                info.put("LIMIT", reqData.getLIMIT());
                info.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                info.put("START_CYCLE_ID", reqData.getSTART_CYCLE_ID());
                info.put("END_CYCLE_ID", reqData.getEND_CYCLE_ID());
                info.put("COMPLEMENT_TAG", reqData.getCOMPLEMENT_TAG());
                info.put("INST_ID", SeqMgr.getInstId());

                dataset.add(info);
            }
        }

        addTradePayrelation(dataset);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setOPER_TYPE(map.getString("OPER_TYPE"));
        reqData.setLIMIT(map.getString("LIMIT", "0"));
        reqData.setLIMIT_TYPE(map.getString("LIMIT_TYPE"));
        reqData.setCOMPLEMENT_TAG(map.getString("COMPLEMENT_TAG"));
        reqData.setPAYITEM_CODE(map.getString("PAYITEM_CODE"));
        reqData.setACCT_ID(map.getString("ACCT_ID")); // 集团账户ID

        reqData.setSTART_CYCLE_ID(map.getString("START_CYCLE_ID"));
        reqData.setEND_CYCLE_ID(map.getString("END_CYCLE_ID"));
        //--------add by chenzg@20160829-REQ201608180003批量办理集团统付时新增用户订购短信和月结短信提醒功能的需求-begin---------
        String crmSmsOrder = map.getString("crmSmsOrder");
        reqData.setNeedSms(StringUtils.equals(crmSmsOrder, "1")?true:false);
        baseCommInfo.put("acctSmsOrder", map.getString("acctSmsOrder"));
        //--------add by chenzg@20160829-REQ201608180003批量办理集团统付时新增用户订购短信和月结短信提醒功能的需求-end---------
        
        makAcctCyleDate(); // 账期重算
    }

    /**
     * @description 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        if ("2".equals(reqData.getOPER_TYPE()))
        {
            data.put("REMARK", "统付的集团欠费，自动终止统一付费关系");
        }

    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        // 设置业务类型
        return "3601";
    }

    /**
     * 分散账期修改 成员订购绑定和集团付费账户的付费关系,因为必须要求成员为自然月账期
     * 
     * @throws Exception
     */
    public void makAcctCyleDate() throws Exception
    {
        IData mebUserAcctDay = DiversifyAcctUtil.getUserAcctDay(reqData.getUca().getUserId());
        if (IDataUtil.isEmpty(mebUserAcctDay))
        {
            // j2ee common.error("服务号码[" + sn + "]用户账期信息不存在");
            CSAppException.apperr(GrpException.CRM_GRP_657);
        }
        // 排除非自然月账期的情况
        DiversifyAcctUtil.checkUserAcctDayWithWarn(mebUserAcctDay, reqData.getUca().getSerialNumber(), "1", true);
        // 获取用户账期分布
        String userAcctDayDistribution = DiversifyAcctUtil.userAcctDayDistribution(mebUserAcctDay, "1");

        String startCycleId = reqData.getSTART_CYCLE_ID();
        String endCycleId = reqData.getEND_CYCLE_ID();
        // 如果START_CYCLE_ID为6位,则转为8位
        if (startCycleId.length() == 6)
        {
            startCycleId = startCycleId + "01";
        }
        // 如果END_CYCLE_ID为6位,则转为8位
        if (endCycleId.length() == 6)
        {
            // 获取本月末时间YYYYMMDD
            // info.put("END_CYCLE_ID", CycDualMgr.getLastCycleByDate(pd, endCycleId + "01"));
            endCycleId = SysDateMgr.decodeTimestamp(endCycleId + "01", SysDateMgr.PATTERN_STAND_YYYYMMDD);
            endCycleId = SysDateMgr.getDateLastMonthSec(endCycleId);
            endCycleId = SysDateMgr.decodeTimestamp(endCycleId, SysDateMgr.PATTERN_TIME_YYYYMMDD);
            reqData.setEND_CYCLE_ID(endCycleId);
        }
        // YYYYMMDD格式转化为YYYY-MM-DD格式
        String startCycleDate = startCycleId.substring(0, 4) + "-" + startCycleId.substring(4, 6) + "-" + startCycleId.substring(6, 8);

        // 重新计算账期开始时间
        if (GroupBaseConst.UserDaysDistribute.TRUE.getValue().equals(userAcctDayDistribution))
        {
            // 获取传入时间startCycleDate的开始账期
            String firstDayThisAcct = AcctDateUtils.getFirstCycleDayThisAcct(startCycleDate, mebUserAcctDay.getString("ACCT_DAY"), mebUserAcctDay.getString("FIRST_DATE"), mebUserAcctDay.getString("START_DATE"));
            // info.put("START_CYCLE_ID", firstDayThisAcct.replaceAll("-", ""));
            reqData.setSTART_CYCLE_ID(firstDayThisAcct.replaceAll("-", ""));

        }
        else if (GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue().equals(userAcctDayDistribution))
        {
            String firstDayThisAcct = mebUserAcctDay.getString("FIRST_DAY_NEXTACCT");
            int months = SysDateMgr.monthInterval(SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMM), startCycleId.substring(0, 6));
            if (months > 1)
            {
                // 获取传入时间startCycleDate的开始账期
                firstDayThisAcct = AcctDateUtils.getFirstCycleDayThisAcct(startCycleDate, mebUserAcctDay.getString("NEXT_ACCT_DAY"), mebUserAcctDay.getString("NEXT_FIRST_DATE"), mebUserAcctDay.getString("NEXT_START_DATE"));
            }
            // info.put("START_CYCLE_ID", firstDayThisAcct.replaceAll("-", ""));
            reqData.setSTART_CYCLE_ID(firstDayThisAcct.replaceAll("-", ""));
        }

        // 终止付费关系用到
        String startCycle = reqData.getSTART_CYCLE_ID();
        String tmpStartCycleId = startCycle.substring(0, 4) + "-" + startCycle.substring(4, 6) + "-" + startCycle.substring(6, 8);
        reqData.setDEL_CYCLE_ID(SysDateMgr.addDays(tmpStartCycleId, -1).replaceAll("-", ""));
        // add end

    }

    public void actOtherTrade() throws Exception
    {
        IData othe = new DataMap();
        othe.put("USER_ID", reqData.getGrpUca().getUserId());
        othe.put("RSRV_VALUE_CODE", "30");
        othe.put("RSRV_VALUE", "50");
        othe.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        othe.put("START_DATE", getAcceptTime());
        othe.put("END_DATE", SysDateMgr.getEndCycle20501231());
        othe.put("INST_ID", SeqMgr.getInstId());
        addTradeOther(othe);
    }

    /**
     * 集团代付费子表
     * 
     * @throws Exception
     */
    public void specialePay() throws Exception
    {
        String startcycleid = reqData.getSTART_CYCLE_ID();
        String payitemcode = reqData.getPAYITEM_CODE();
        String memUserId = reqData.getUca().getUserId();
        String grpUserId = reqData.getGrpUca().getUserId();
        boolean ifaddPay = true;
        // ifaddPay 标示是否需要新增payrelation数据 只要当前的优惠不与要变更的主键冲突（start_cycle_id,payitem_code）

        String acctId = reqData.getACCT_ID();

        IDataset dataset = new DatasetList();
        IDataset specPays = UserSpecialPayInfoQry.qryUserSpecialPay(memUserId, grpUserId); // 成员代付关系
        String operType = reqData.getOPER_TYPE();
        if ("2".equals(operType))
        {
            if (null != specPays && specPays.size() > 0)
            {
                for (int i = 0; i < specPays.size(); i++)
                {
                    IData acctInfo = (IData) specPays.get(i);
                    if (acctId.equals(acctInfo.getString("ACCT_ID")))
                    {
                        // 分散账期修改
                        acctInfo.put("END_CYCLE_ID", reqData.getDEL_CYCLE_ID());
                        acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        dataset.add(acctInfo);
                    }
                }
            }
        }
        else
        {
            if (null != specPays && specPays.size() > 0)
            {
                for (int i = 0; i < specPays.size(); i++)
                {
                    IData acctInfo = (IData) specPays.get(i);
                    if (acctId.equals(acctInfo.getString("ACCT_ID")))
                    {
                        acctInfo.put("RSRV_STR3", baseCommInfo.getString("acctSmsOrder", ""));// 月初话费短信提醒
                        if (!acctInfo.getString("START_CYCLE_ID", "").equals(startcycleid) || !acctInfo.getString("PAYITEM_CODE", "").equals(payitemcode))
                        {
                            // 分散账期修改
                            acctInfo.put("END_CYCLE_ID", reqData.getDEL_CYCLE_ID());
                            acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            dataset.add(acctInfo);
                        }
                        else
                        {
                            ifaddPay = false;
                            acctInfo.put("END_CYCLE_ID", reqData.getEND_CYCLE_ID());
                            acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            acctInfo.put("COMPLEMENT_TAG", reqData.getCOMPLEMENT_TAG());
                            acctInfo.put("LIMIT_TYPE", reqData.getLIMIT_TYPE());
                            acctInfo.put("LIMIT", reqData.getLIMIT());
                            dataset.add(acctInfo);
                        }
                    }
                }
            }
            if (ifaddPay == true)
            {
                IData info = new DataMap();

                info.put("USER_ID", memUserId);
                info.put("USER_ID_A", grpUserId);
                info.put("ACCT_ID", acctId); // 传过来的集团账户ID
                info.put("ACCT_ID_B", reqData.getUca().getAcctId()); // 成员账户id
                info.put("PAYITEM_CODE", reqData.getPAYITEM_CODE());
                info.put("ACCT_PRIORITY", "0");
                info.put("USER_PRIORITY", "0");
                info.put("BIND_TYPE", "0");
                info.put("ACT_TAG", "1");
                info.put("DEFAULT_TAG", "0");
                info.put("LIMIT_TYPE", reqData.getLIMIT_TYPE());
                info.put("LIMIT", reqData.getLIMIT());
                info.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                info.put("START_CYCLE_ID", reqData.getSTART_CYCLE_ID());
                info.put("END_CYCLE_ID", reqData.getEND_CYCLE_ID());
                info.put("COMPLEMENT_TAG", reqData.getCOMPLEMENT_TAG());
                info.put("INST_ID", SeqMgr.getInstId());
                info.put("RSRV_STR3", baseCommInfo.getString("acctSmsOrder", ""));// 月初话费短信提醒

                dataset.add(info);
            }
        }
        addTradeUserSpecialepay(dataset);
    }
}
