
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.AcctDateUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;
import com.asiainfo.veris.crm.order.soa.group.common.query.BookTradeSVC;

/**
 * 互联网专线GPON成员统一付费
 * 
 * @author liujy
 */
public class GrpBatVgpoPayrelationChgSVC extends GroupBatService
{
    private static final String SERVICE_NAME = "CS.ChangeMemElementSvc.changeMemElement";

    @Override
    protected void batInitialSub(IData batData) throws Exception
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void batValidateSub(IData batData) throws Exception
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        // TODO Auto-generated method stub

    }

    public void batExecs(IData batData) throws Exception
    {
        // 得到交易参数
        String batId = IDataUtil.chkParam(batData, "BATCH_ID");
        String tradeStaffId = IDataUtil.chkParam(condData, "TRADE_STAFF_ID");
        String tradeDepartId = IDataUtil.chkParam(condData, "TRADE_DEPART_ID");
        String tradeCityCode = IDataUtil.chkParam(condData, "TRADE_CITY_CODE");
        String tradeEparchyCode = IDataUtil.chkParam(condData, "TRADE_EPARCHY_CODE");

        // 得到业务参数
        String actId = IDataUtil.chkParam(batData, "ACCT_ID");
        String payItemCode = IDataUtil.chkParam(batData, "PAYITEM_CODE");
        String userIdA = IDataUtil.chkParam(batData, "USER_ID");
        String sn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");
        String operType = IDataUtil.chkParam(batData, "OPER_TYPE");

        // 设置操作员相关信息
        svcData.put("TRADE_STAFF_ID", tradeStaffId);
        svcData.put("TRADE_DEPART_ID", tradeDepartId);
        svcData.put("TRADE_CITY_CODE", tradeCityCode);
        svcData.put("EPARCHY_CODE", tradeEparchyCode);
        svcData.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
        svcData.put("LOGIN_EPARCHY_CODE", tradeEparchyCode);

        IData info = new DataMap();
        info.put("SERIAL_NUMBER", sn);
        info.put("OPER_TYPE", operType);
        info.put("ACCT_ID", actId);
        info.put("PAYITEM_CODE", payItemCode);
        info.put("LIMIT_TYPE", IDataUtil.chkParam(batData, "LIMIT_TYPE"));
        ;
        info.put("LIMIT", IDataUtil.chkParam(batData, "LIMIT7"));
        info.put("OPER_TYPE", operType);
        info.put("START_CYCLE_ID", IDataUtil.chkParam(batData, "START_CYCLE"));
        info.put("END_CYCLE_ID", IDataUtil.chkParam(batData, "END_CYCLE"));
        info.put("COMPLEMENT_TAG", IDataUtil.chkParam(batData, "COMPLEMENT_TAG"));
        info.put("USER_ID_A", userIdA);
        queryMemberInfo(info);

        // 分散账期修改 成员订购绑定和集团付费账户的付费关系,因为必须要求成员为自然月账期 add start
        if ("true".equals(DiversifyAcctUtil.getJudeAcctDayTag(info)))
        {
            String userId = "";
            IData mebUserAcctDay = DiversifyAcctUtil.getUserAcctDay(userId);
            if (mebUserAcctDay == null || mebUserAcctDay.size() == 0)
            {
                CSAppException.apperr(GrpException.CRM_GRP_756);
            }

            // 排除非自然月账期的情况
            DiversifyAcctUtil.checkUserAcctDayWithWarn(info, "", "", true);

            // 获取用户账期分布
            String userAcctDayDistribution = DiversifyAcctUtil.userAcctDayDistribution(mebUserAcctDay, "1");
            String startCycleId = info.getString("START_CYCLE_ID");
            String endCycleId = info.getString("END_CYCLE_ID");

            // 如果START_CYCLE_ID为6位,则转为8位
            if (startCycleId.length() == 6)
            {
                startCycleId = startCycleId + "01";
            }
            // 如果END_CYCLE_ID为6位,则转为8位
            if (endCycleId.length() == 6)
            {
                // 获取本月末时间YYYYMMDD
                info.put("END_CYCLE_ID", SysDateMgr.getLastDateThisMonth());
            }

            // YYYYMMDD格式转化为YYYY-MM-DD格式
            String startCycleDate = startCycleId.substring(0, 4) + "-" + startCycleId.substring(4, 6) + "-" + startCycleId.substring(6, 8);

            // 重新计算账期开始时间
            if (UserDaysDistribute.TRUE.getValue().equals(userAcctDayDistribution))
            {
                // 获取传入时间startCycleDate的开始账期
                String firstDayThisAcct = AcctDateUtils.getFirstCycleDayThisAcct(startCycleDate, mebUserAcctDay.getString("ACCT_DAY"), mebUserAcctDay.getString("FIRST_DATE"), mebUserAcctDay.getString("START_DATE"));
                info.put("START_CYCLE_ID", firstDayThisAcct.replaceAll("-", ""));

            }
            else if (UserDaysDistribute.FALSE_TRUE.getValue().equals(userAcctDayDistribution))
            {
                String firstDayThisAcct = mebUserAcctDay.getString("FIRST_DAY_NEXTACCT");
                int months = 0;
                // int months =
                // AcctDateUtils.monthInterval((SysDateMgr.getNowYear()+SysDateMgr.getCurMonth()).replaceAll("-",
                // ""),startCycleId.substring(0, 6));
                if (months > 1)
                {
                    // 获取传入时间startCycleDate的开始账期
                    firstDayThisAcct = AcctDateUtils.getFirstCycleDayThisAcct(startCycleDate, mebUserAcctDay.getString("NEXT_ACCT_DAY"), mebUserAcctDay.getString("NEXT_FIRST_DATE"), mebUserAcctDay.getString("NEXT_START_DATE"));
                }
                info.put("START_CYCLE_ID", firstDayThisAcct.replaceAll("-", ""));
            }
        }
        // 终止付费关系用到
        String startCycleId = info.getString("START_CYCLE_ID");
        startCycleId = startCycleId.substring(0, 4) + "-" + startCycleId.substring(4, 6) + "-" + startCycleId.substring(6, 8);
        // info.put("DEL_CYCLE_ID", AcctDateUtils.addDays(startCycleId, -1).replaceAll("-", ""));
        // add end

        IData param = new DataMap();

        // 查业务类型限制表
        param.clear();
        param.put("TRADE_TYPE_CODE", "3601");
        // param.put("USER_ID", td.getMemUserInfo().getString("USER_ID"));
        param.put("USER_ID", "");
        param.put("BRAND_CODE", "ZZZZ");
        param.put("LIMIT_ATTR", "0");
        param.put("LIMIT_TAG", "0");
        param.put("EPARCHY_CODE", "0898");

        BookTradeSVC bookTrade = new BookTradeSVC();
        IDataset tradeTypeLimitList = bookTrade.getTradeTypeLimit(param);

        if ((tradeTypeLimitList != null) && (tradeTypeLimitList.size() > 0))
        {
            CSAppException.apperr(GrpException.CRM_GRP_760);
        }

    }

    public void batPayRela(IData batData) throws Exception
    {
        IData data = null;
        String startcycleid = data.getString("START_CYCLE_ID");
        String payitemcode = data.getString("PAYITEM_CODE");
        boolean ifaddPay = true;

        IData param = new DataMap();
        param.put("USER_ID", svcData.getString("USER_ID"));
        String acctId = batData.getString("ACCT_ID");
        String eparchyCode = data.getString("EPARCHY_CODE");
        IDataset dataset = new DatasetList();
        IDataset acctInfos = AcctInfoQry.getPayRelaByUserEffected(param, eparchyCode);

        String operType = batData.getString("OPER_TYPE");
        if ("2".equals(operType))
        {
            if ((acctInfos != null) && (acctInfos.size() > 0))
            {
                for (int i = 0; i < acctInfos.size(); i++)
                {
                    IData acctInfo = (IData) acctInfos.get(i);
                    if (acctId.equals(acctInfo.getString("ACCT_ID")))
                    {
                        // 分散账期修改
                        acctInfo.put("END_CYCLE_ID", batData.getString("DEL_CYCLE_ID"));
                        acctInfo.put("STATE", "DEL");
                        dataset.add(acctInfo);
                    }
                }
            }
        }
        else
        {
            if ((acctInfos != null) && (acctInfos.size() > 0))
            {
                IDataset vpnpayitems = StaticUtil.getStaticList("GROUP_GPON_BAT_PAYPARAMCODE");
                for (int i = 0; i < acctInfos.size(); i++)
                {
                    boolean isvpnitem = false;
                    IData acctInfo = (IData) acctInfos.get(i);
                    if (!acctId.equals(acctInfo.getString("ACCT_ID")))
                        continue;
                    for (int j = 0; j < vpnpayitems.size(); j++)
                    {
                        IData dt = vpnpayitems.getData(j);
                        if (acctInfo.getString("PAYITEM_CODE").equals(dt.getString("KEY")))
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
                            acctInfo.put("END_CYCLE_ID", data.getString("DEL_CYCLE_ID"));
                            acctInfo.put("STATE", "MODI");
                            dataset.add(acctInfo);
                        }
                        else
                        {
                            ifaddPay = false;

                            acctInfo.put("END_CYCLE_ID", data.getString("END_CYCLE_ID"));
                            acctInfo.put("STATE", "MODI");
                            acctInfo.put("COMPLEMENT_TAG", data.getString("COMPLEMENT_TAG"));
                            acctInfo.put("LIMIT_TYPE", data.getString("LIMIT_TYPE"));
                            acctInfo.put("LIMIT", data.getString("LIMIT"));
                            dataset.add(acctInfo);
                        }

                    }

                }

            }

            if (ifaddPay)
            {
                IData info = new DataMap();

                info.put("USER_ID", svcData.getString("USER_ID"));
                info.put("ACCT_ID", data.getString("ACCT_ID"));
                info.put("PAYITEM_CODE", data.getString("PAYITEM_CODE"));
                info.put("ACCT_PRIORITY", "0");
                info.put("USER_PRIORITY", "0");
                info.put("BIND_TYPE", "0");
                info.put("ACT_TAG", "1");
                info.put("DEFAULT_TAG", "0");
                info.put("LIMIT_TYPE", data.getString("LIMIT_TYPE"));
                info.put("LIMIT", data.getString("LIMIT"));
                info.put("STATE", "ADD");
                info.put("START_CYCLE_ID", data.getString("START_CYCLE_ID"));
                info.put("END_CYCLE_ID", data.getString("END_CYCLE_ID"));
                info.put("COMPLEMENT_TAG", data.getString("COMPLEMENT_TAG"));

                String inst_id = SeqMgr.getInstId();
                info.put("INST_ID", inst_id);

                dataset.add(info);
            }

        }

    }

    public void batSpecialePay() throws Exception
    {
        IData data = null;
        String startcycleid = data.getString("START_CYCLE_ID");
        String payitemcode = data.getString("PAYITEM_CODE");
        boolean ifaddPay = true;

        IData param = new DataMap();
        param.put("USER_ID", svcData.getString("USER_ID"));
        param.put("USER_ID_A", data.getString("USER_ID_A"));
        String acctId = data.getString("ACCT_ID");

        IDataset dataset = new DatasetList();
        BookTradeSVC bookTrade = new BookTradeSVC();
        IDataset specPays = bookTrade.getSpecPayByUserIdA(param);

        String operType = data.getString("OPER_TYPE");
        if ("2".equals(operType))
        {
            if ((specPays != null) && (specPays.size() > 0))
                for (int i = 0; i < specPays.size(); i++)
                {
                    IData acctInfo = (IData) specPays.get(i);
                    if (acctId.equals(acctInfo.getString("ACCT_ID")))
                    {
                        // 分散账期修改
                        acctInfo.put("END_CYCLE_ID", data.getString("DEL_CYCLE_ID"));
                        acctInfo.put("STATE", "DEL");
                        dataset.add(acctInfo);
                    }
                }
        }
        else
        {
            if ((specPays != null) && (specPays.size() > 0))
            {
                for (int i = 0; i < specPays.size(); i++)
                {
                    IData acctInfo = (IData) specPays.get(i);
                    if (!acctId.equals(acctInfo.getString("ACCT_ID")))
                        continue;
                    if ((!acctInfo.getString("START_CYCLE_ID", "").equals(startcycleid)) || (!acctInfo.getString("PAYITEM_CODE", "").equals(payitemcode)))
                    {
                        // 分散账期修改
                        acctInfo.put("END_CYCLE_ID", data.getString("DEL_CYCLE_ID"));
                        acctInfo.put("STATE", "MODI");
                        dataset.add(acctInfo);
                    }
                    else
                    {
                        ifaddPay = false;
                        acctInfo.put("END_CYCLE_ID", data.getString("END_CYCLE_ID"));
                        acctInfo.put("STATE", "MODI");
                        acctInfo.put("COMPLEMENT_TAG", data.getString("COMPLEMENT_TAG"));
                        acctInfo.put("LIMIT_TYPE", data.getString("LIMIT_TYPE"));
                        acctInfo.put("LIMIT", data.getString("LIMIT"));
                        dataset.add(acctInfo);
                    }
                }
            }

            if (ifaddPay)
            {
                IData info = new DataMap();

                info.put("USER_ID", svcData.getString("USER_ID"));
                info.put("USER_ID_A", data.getString("USER_ID_A"));
                info.put("ACCT_ID", data.getString("ACCT_ID"));
                info.put("ACCT_ID_B", svcData.getString("ACCT_ID"));
                info.put("PAYITEM_CODE", data.getString("PAYITEM_CODE"));
                info.put("ACCT_PRIORITY", "0");
                info.put("USER_PRIORITY", "0");
                info.put("BIND_TYPE", "0");
                info.put("ACT_TAG", "1");
                info.put("DEFAULT_TAG", "0");
                info.put("LIMIT_TYPE", data.getString("LIMIT_TYPE"));
                info.put("LIMIT", data.getString("LIMIT"));
                info.put("STATE", "ADD");
                info.put("START_CYCLE_ID", data.getString("START_CYCLE_ID"));
                info.put("END_CYCLE_ID", data.getString("END_CYCLE_ID"));
                info.put("COMPLEMENT_TAG", data.getString("COMPLEMENT_TAG"));

                String inst_id = SeqMgr.getInstId();
                info.put("INST_ID", inst_id);

                dataset.add(info);
            }
        }

    }

    public void queryMemberInfo(IData param) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        IDataset userInfos = UserInfoQrySVC.getMofficeBySN(param);
        if ((userInfos != null) && (userInfos.size() > 0))
        {
            IData info = (IData) userInfos.get(0);
            data.put("REMOVE_TAG", "0");
            data.put("NET_TYPE_CODE", "00");
            UserInfoQrySVC userInfoQry = new UserInfoQrySVC();
            IDataset infos = userInfoQry.getUserInfoBySN(param);
            if ((infos == null) || (infos.size() == 0))
            {
                CSAppException.apperr(GrpException.CRM_GRP_257);
            }
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_696);
        }

        String operType = param.getString("OPER_TYPE");
        IData info = new DataMap();
        //
        info.put("USER_ID", "");
        if ("1".equals(operType))
        {
            // IDataset purchase = dao.queryPurchaseInfo(info);
            IDataset purchase = null;
            if (purchase.size() > 0)
            {
                CSAppException.apperr(GrpException.CRM_GRP_655);
            }
        }

        // info.put("CUST_ID", td.getMemUserInfo().getString("CUST_ID"));
        // GroupBaseBean.setDbConCode(pd, td.getMemUserInfo().getString("EPARCHY_CODE"));
        // IDataset custInfos = CustInfoQry.getCustInfoByCustId(pd, info);
        IDataset custInfos = null;
        if ((custInfos != null) && (custInfos.size() > 0))
        {
            // IData custInfo = (IData) custInfos.get(0);
            // td.setBaseDataInfo(TradeData.X_BASE_DATA.MEM_CUST_INFO, custInfo);
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_761);
        }
        // IDataset payrelationInfos = AcctInfoQry.getDefaultPayRelationByUserID( pd, info);
        IDataset payrelationInfos = null;
        if ((payrelationInfos != null) && (payrelationInfos.size() > 0))
        {
            // td.setBaseDataInfo(TradeData.X_BASE_DATA.MEM_ACCT_INFO,
            // (IData) payrelationInfos.get(0));
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_735);
        }

        // IDataset relationInfos = dao.queryRelationInfo(info);
        IDataset relationInfos = null;
        if ((relationInfos != null) && (relationInfos.size() > 0))
        {
            IData relationInfo = (IData) relationInfos.get(0);
            data.put("USER_ID", relationInfo.getString("USER_ID_A"));
            // payrelationInfos = AcctInfoQry.getDefaultPayRelationByUserID(pd,info);
            String acctIda = ((IData) payrelationInfos.get(0)).getString("ACCT_ID");
            // String acctId = td.getMemAcctInfo().getString("ACCT_ID");
            String acctId = "";
            if (acctIda.equals(acctId))
            {
                CSAppException.apperr(GrpException.CRM_GRP_656);
            }
        }
    }

    public static enum UserDaysDistribute
    {
        TRUE("0"), FALSE_TRUE("1"), TRUE_FALSE("2"), FALSE_FALSE("3"), FALSE("4");

        private UserDaysDistribute(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }

        private final String value;
    }
}
