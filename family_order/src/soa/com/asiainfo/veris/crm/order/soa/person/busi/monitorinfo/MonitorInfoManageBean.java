
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MonitorInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.RedUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SmsRedmemberQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeSvcStateParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class MonitorInfoManageBean extends CSBizBean
{

    private String checkIsBlackUser(String userId, String sysTime, IData data) throws Exception
    {
        String remark = "";
        IDataset blackUser = UserBlackInfoQry.getBlackUserdata(userId);
        if (IDataUtil.isEmpty(blackUser))
        {
            remark = "用户状态异常插TL_B_BLACKUSER";
            IData param = new DataMap();
            param.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
            param.put("IN_TIME", data.getString("IN_TIME"));
            param.put("SERIAL_NUMBER", "86" + data.getString("SERIAL_NUMBER"));
            param.put("USER_ID", userId);
            param.put("PROCESS_TAG", "1");
            param.put("DATA_TYPE", data.getString("DATA_TYPE"));
            param.put("EXEC_TIME", sysTime);
            param.put("BEGIN_DATE", sysTime);
            param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            param.put("EFFECT_TAG", "1");
            param.put("REMARK", "黑名单用户");
            Dao.insert("TL_B_BLACKUSER", param, Route.CONN_CRM_CEN);
        }
        else
        {
            remark = "用户状态异常已经是黑名单用户";
        }
        return remark;
    }

    private int modifyMonitorFile(String processTag, String intfId, String month, String remark, String execTime, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("PROCESS_TAG", processTag);
        param.put("INTF_ID", intfId);
        param.put("MONTH", month);
        param.put("REMARK", "完成：" + remark);
        param.put("EXEC_TIME", execTime);
        param.put("RSRV_STR2", userId);

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("UPDATE TL_B_MONITORFILE A ");
        sBuilder.append("	SET A.PROCESS_TAG = :PROCESS_TAG,");
        sBuilder.append("    A.REMARK      = :REMARK,");
        sBuilder.append("    A.RSRV_STR2   = :USER_ID,");
        sBuilder.append("    A.EXEC_TIME   = SYSDATE");
        sBuilder.append(" WHERE A.INTF_ID = :INTF_ID AND A.MONTH = :MONTH");
        return Dao.executeUpdate(sBuilder, param);
    }

    public IDataset stopMobile(IData data) throws Exception
    {
        String intfid = data.getString("SERIAL_NUMBER");
        IDataset result = MonitorInfoQry.queryHarassPhoneByIntfID(intfid);
        if (IDataUtil.isNotEmpty(result))
        {
            IData info = result.getData(0);
            String serialNumber = info.getString("SERIAL_NUMBER");
            IDataset userInfos = UserInfoQry.qryUserInfoBySnAndBrandCode(serialNumber, info.getString("START_DATE"));
            if (IDataUtil.isNotEmpty(userInfos))
            {
                IData userInfo = userInfos.getData(0);
                if (!"0".equals(userInfo.getString("USER_STATE_CODESET")))
                {
                    // 用户资料不正常，置标志位为8，表示不符合停机要求
                    IData param = new DataMap();
                    param.put("PROCESS_TAG", "8");
                    param.put("RSRV_STR2", "user_state_codeset:" + userInfo.getString("USER_STATE_CODESET"));
                    param.put("INTF_ID", intfid);
                    param.put("MONTH", info.getString("MONTH"));
                    Dao.executeUpdateByCodeCode("TL_B_HARASSPHONE", "UPD_HARASSPHONE_BY_INTF_ID_MONTH", param);
                    return null;
                }
                IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
                if (IDataUtil.isNotEmpty(custInfo))
                {
                    // 调用局方停机接口
                    IData param = new DataMap();
                    param.put("SERIAL_NUMBER", serialNumber);
                    param.put("TRADE_TYPE_CODE", "136");
                    try
                    {
                        IDataset tradeData = CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", param);
                        String oradeId = "";
                        if (IDataUtil.isNotEmpty(tradeData))
                        {
                            oradeId = tradeData.getData(0).getString("ORDER_ID");
                        }

                        // 数据搬到历史表
                        param.clear();
                        param.put("INTF_ID", intfid);
                        param.put("MONTH", info.getString("MONTH"));
                        param.put("ORDER_ID", oradeId);

                        StringBuilder sBuilder = new StringBuilder();
                        sBuilder.append("INSERT INTO tl_bh_harassphone ");
                        sBuilder
                                .append("(SELECT INTF_ID, MONTH, IN_TIME, START_DATE, END_DATE, SERIAL_NUMBER, X_CALL_EDMPHONECODE, REASON_CODE, EXEC_TIME, PROCESS_TAG, TRADE_NUMBER, EFFECT_TIME, AUDIT_STAFF_ID, AUDIT_DEPART_ID, AUDIT_TIME, PROV_CODE, AREA_CODE, SERV_TYPE, ASP, HOME_TYPE, CALLED_TYPE, CITY_CODE, RSRV_STR1, RSRV_STR2, :ORDER_ID RSRV_STR3, REMARK ");
                        sBuilder.append("FROM tl_b_harassphone a WHERE a.INTF_ID = :INTF_ID AND MONTH = :MONTH)");
                        Dao.executeUpdate(sBuilder, param);

                        // StringBuilder stringBuilder = new StringBuilder();
                        // stringBuilder.append("DELETE FROM tl_b_harassphone a WHERE a.INTF_ID = :INTF_ID AND MONTH = :MONTH");
                        // Dao.executeUpdate(stringBuilder, param);
                        Dao.delete("TL_B_HARASSPHONE", param, new String[]
                        { "INTF_ID", "MONTH" });

                        return tradeData;
                    }
                    catch (Exception e)
                    {
                        IData temp = new DataMap();
                        temp.put("PROCESS_TAG", "8");
                        temp.put("RSRV_STR2", Utility.getBottomException(e).getMessage());
                        temp.put("INTF_ID", intfid);
                        temp.put("MONTH", info.getString("MONTH"));
                        Dao.executeUpdateByCodeCode("TL_B_HARASSPHONE", "UPD_HARASSPHONE_BY_INTF_ID_MONTH", temp);
                    }
                }
            }
            else
            {
                // 用户资料不正常，置标志位为8，表示不符合停机要求
                IData param = new DataMap();
                param.put("PROCESS_TAG", "8");
                param.put("INTF_ID", intfid);
                param.put("RSRV_STR2", "用户资料异常");
                param.put("MONTH", info.getString("MONTH"));
                Dao.executeUpdateByCodeCode("TL_B_HARASSPHONE", "UPD_HARASSPHONE_BY_INTF_ID_MONTH", param);
                return null;
            }
        }
        return null;
    }

    public IDataset stopSms(IData data) throws Exception
    {
        IDataset result = new DatasetList();
        String intfid = data.getString("SERIAL_NUMBER");
        IDataset dataset = MonitorInfoQry.queryUncheckSmsByIntfID(intfid);
        if (IDataUtil.isNotEmpty(dataset))
        {
            String remark = "";
            String sysTime = SysDateMgr.getSysTime();
            IData info = dataset.getData(0);
            String month = info.getString("MONTH");
            String serialNumber = info.getString("SERIAL_NUMBER");
            String processTag = info.getString("PROCESS_TAG");
            String startDate = info.getString("START_DATE");
            IDataset userInfos = UserInfoQry.qryUserInfoBySnAndBrandCode(serialNumber, startDate);
            if (IDataUtil.isNotEmpty(userInfos))
            {
                IData userInfo = userInfos.getData(0);
                String userId = userInfo.getString("USER_ID");
                IDataset tradeInfo = TradeInfoQry.qryTradeInfo(serialNumber);
                if (IDataUtil.isNotEmpty(tradeInfo) && "B".equals(processTag))
                {
                    // 查询是否黑名单用户
                    remark = checkIsBlackUser(userId, sysTime, info);
                    modifyMonitorFile("8", intfid, month, remark, sysTime, null);
                    return result;
                }

                IDataset stateParam = TradeSvcStateParamInfoQry.querySvcStateParamByOldStateCode("136", "0", userInfo.getString("USER_STATE_CODESET"));
                if (IDataUtil.isEmpty(stateParam))
                {
                    if ("B".equals(processTag))
                    {
                        remark = checkIsBlackUser(userId, sysTime, info);
                        modifyMonitorFile("8", intfid, month, remark, sysTime, null);
                        return result;
                    }
                }

                // 记录UserId
                StringBuilder sBuilder = new StringBuilder();
                sBuilder.append("UPDATE TL_B_MONITORFILE SET RSRV_STR1 = :USER_ID WHERE INTF_ID = :INTF_ID AND MONTH = :MONTH");
                IData iData = new DataMap();
                iData.put("USER_ID", userId);
                iData.put("INTF_ID", intfid);
                iData.put("MONTH", month);
                Dao.executeUpdate(sBuilder, iData);

                // 查询短信红名单,在短信红名单中没有的有效用户才进行停短信服务操作
                IDataset redMember = SmsRedmemberQry.queryRedMenberByUserId(userInfo.getString("USER_ID"));
                if (IDataUtil.isEmpty(redMember))
                {
                    // 绑定付费关系
                    IData payRela = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
                    if (IDataUtil.isEmpty(payRela))
                    {
                        remark = "绑定了集团付费关系";
                        modifyMonitorFile("8", intfid, month, remark, sysTime, null);
                        return result;
                    }

                    // 付费类型是现金
                    IDataset accInfo = AcctInfoQry.qryAcctInfoByCustIdAndPayMode(userInfo.getString("CUST_ID"), "0", "0");
                    if (IDataUtil.isEmpty(accInfo))
                    {
                        remark = "非现金付费类型";
                        modifyMonitorFile("8", intfid, month, remark, sysTime, null);
                        return result;
                    }

                    // 不是大客户
                    IDataset vipInfo = CustVipInfoQry.qryVipInfoByUserId(userId);
                    if (IDataUtil.isNotEmpty(vipInfo))
                    {
                        remark = "是大客户";
                        modifyMonitorFile("8", intfid, month, remark, sysTime, null);
                        return result;
                    }

                    // 不是BOSS红名单用户
                    IDataset redUser = RedUserInfoQry.qryRedUserByUserId(userId);
                    if (IDataUtil.isNotEmpty(redUser))
                    {
                        remark = "是红名单";
                        modifyMonitorFile("8", intfid, month, remark, sysTime, null);
                        return result;
                    }

                    // 不是黑名单用户
                    IDataset blackInfo = UserBlackInfoQry.getBlackUserdata(userId);
                    if (IDataUtil.isNotEmpty(blackInfo))
                    {
                        remark = "用户是黑名单";
                        modifyMonitorFile("8", intfid, month, remark, sysTime, null);
                        return result;
                    }

                    // 用户没有[0|语音呼叫]服务
                    IDataset svcInfo = UserSvcInfoQry.getSvcUserIdPf(userId, "0");
                    if (IDataUtil.isEmpty(svcInfo))
                    {
                        remark = "用户没有[0|语音呼叫]服务";
                        modifyMonitorFile("8", intfid, month, remark, sysTime, null);
                        return result;
                    }

                    if ("B".equals(processTag))
                    {
                        IData params = new DataMap();
                        params.put("SERIAL_NUMBER", serialNumber);
                        params.put("TRADE_TYPE_CODE", "136");
                        try
                        {
                            result = CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", params);
                            String tradeId = result.getData(0).getString("TRADE_ID");
                            remark = "'TradeOK！[" + tradeId + "]";
                        }
                        catch (Exception e)
                        {
                            remark = Utility.getBottomException(e).getMessage().toString();
                        }
                        modifyMonitorFile("D", intfid, month, remark, sysTime, null);
                    }
                    else
                    {// IV_COUNT1 > 0 AND IV_COUNT2 = 1 AND IV_COUNT3 = 1 AND IV_COUNT4 = 0 AND IV_COUNT5 = 0 AND
                        // TO_NUMBER(IV_TRADENUMBER) >= IV_FILTERNUMBER
                        remark = "需要人工审核";
                        modifyMonitorFile("A", intfid, month, remark, sysTime, null);
                    }
                    // else {
                    // remark = "无付费关系|非现金付费|是大客户|是红名单！";
                    // modifyMonitorFile("8", intfid, month, remark, sysTime, null);
                    // }
                }
                else
                {
                    remark = "用户是短信红名单！";
                    modifyMonitorFile("8", intfid, month, remark, sysTime, null);
                }
            }
            else
            {
                // 没有取得用户资料，置标志位为8，表示不符合停短信要求
                remark = "没有取得用户资料！";
                modifyMonitorFile("8", intfid, month, remark, sysTime, null);
            }
        }
        return result;
    }
}
