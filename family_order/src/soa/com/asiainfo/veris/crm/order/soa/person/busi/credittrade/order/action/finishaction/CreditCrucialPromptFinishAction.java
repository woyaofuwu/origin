
package com.asiainfo.veris.crm.order.soa.person.busi.credittrade.order.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class CreditCrucialPromptFinishAction implements ITradeFinishAction
{

    public void dealAcctInfoFromQAM(String tradeTypeCode, IData data) throws Exception
    {
        if ("7506".equals(tradeTypeCode))
        {
            data.put("RSRV_NUM2", data.getString("ACCT_BALANCE"));

        }
        else if ("7507".equals(tradeTypeCode))
        {
            int flag = data.getInt("RSRV_STR7");
            String str7 = "";
            switch (flag)
            {
                case 10:
                    str7 = "首次使用产生流量";
                    break;
                case 1:
                    str7 = "不足50M(G3流量套餐)";
                    break;
                case 2:
                    str7 = "不足5M";
                    break;
                case 3:
                    str7 = "剩余0M";
                    break;
                case 4:
                    str7 = "已超出5M";
                    break;
                default:
                    str7 = "未获取到流量阀值";
            }
            data.put("RSRV_STR7", str7);

        }
        else if ("7101".equals(tradeTypeCode) || "7508".equals(tradeTypeCode))
        {
            data.put("RSRV_NUM1", data.getString("LAST_OWE_FEE"));
        }
    }

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
    	String tradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String userId = mainTrade.getString("USER_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String execTime = mainTrade.getString("ACCEPT_DATE");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        
        if( "7101".equals(tradeTypeCode) || "7508".equals(tradeTypeCode) ){
        	
        	/*IData paramProduct = new DataMap();

            paramProduct.put("DEAL_ID", SeqMgr.getTradeId());
            paramProduct.put("USER_ID", userId);
            paramProduct.put("PARTITION_ID", userId.substring(userId.length() - 4));
            paramProduct.put("SERIAL_NUMBER", serialNumber);
            paramProduct.put("EPARCHY_CODE", eparchyCode);
            paramProduct.put("IN_TIME", SysDateMgr.getSysTime());
            paramProduct.put("DEAL_STATE", "0");
            paramProduct.put("DEAL_TYPE", "CreditTradeExp");
            paramProduct.put("EXEC_TIME", execTime);
            paramProduct.put("EXEC_MONTH", SysDateMgr.getMonthForDate(execTime));
            paramProduct.put("TRADE_ID", tradeId);

            Dao.insert("TF_F_EXPIRE_DEAL", paramProduct);*/
        	
            return;
        }
        
        IData specTradeInfo = getSpecTradeInfo(mainTrade);
        if (IDataUtil.isEmpty(specTradeInfo))
        {
            return;
        }
        if ("7506".equals(tradeTypeCode))
        {
            IData acctData = AcctCall.getOweFeeByUserId(userId);
            specTradeInfo.putAll(acctData);
        }

        dealAcctInfoFromQAM(tradeTypeCode, specTradeInfo);
        
        String custId = mainTrade.getString("CUST_ID");
        String custName = mainTrade.getString("CUST_NAME");
        String custType = specTradeInfo.getString("CUST_TYPE");
        //String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        String brandCode = mainTrade.getString("BRAND_CODE");
        String useCustName = specTradeInfo.getString("USECUST_NAME");
        //String serialNumber = mainTrade.getString("SERIAL_NUMBER");

        IDataset result = null;
        /*if ("7101".equals(tradeTypeCode) || "7508".equals(tradeTypeCode))
        {
            // ITF_CRM_DownTimeJob客户欠费即将停机时刻工作生成接口
            String custManagerId = specTradeInfo.getString("CUST_MANAGER_ID");
            String rsrvNum1 = specTradeInfo.getString("RSRV_NUM1");
            String rsrvDate1 = specTradeInfo.getString("RSRV_DATE1");
            String rsrvDate2 = specTradeInfo.getString("RSRV_DATE2");
            result = SccCall.createDownTimeJob(serialNumber, userId, custId, custName, custType, eparchyCode, brandCode, useCustName, custManagerId, rsrvDate1, rsrvDate2, rsrvNum1);

        }else*/
        if ("7506".equals(tradeTypeCode))
        {
            // ITF_CRM_AddPaymentJob缴费提醒关键时刻工作生成接口
            String rsrvNum1 = specTradeInfo.getString("RSRV_NUM1");
            String rsrvNum2 = specTradeInfo.getString("RSRV_NUM2");
            result = SccCall.createPaymentJob(serialNumber, userId, custId, custName, custType, eparchyCode, brandCode, useCustName, rsrvNum1, rsrvNum2);
        }
        else if ("7507".equals(tradeTypeCode))
        {
            // ITF_CRM_GPRSThresholdJob即将达到gprs流量
            String custManagerId = specTradeInfo.getString("CUST_MANAGER_ID");
            String remindTag = specTradeInfo.getString("REMIND_TAG");
            String discntCode = specTradeInfo.getString("DISCNT_CODE");
            String discntName = specTradeInfo.getString("DISCNT_NAME");
            String rsrvStr7 = specTradeInfo.getString("RSRV_STR7");
            result = SccCall.createGPRSThresholdJob(serialNumber, userId, custId, custName, custType, eparchyCode, brandCode, useCustName, custManagerId, remindTag, discntCode, discntName, rsrvStr7);
        }

        if (IDataUtil.isNotEmpty(result))
        {
            String resultInfo = result.getData(0).getString("X_RESULTINFO");
            IData param = new DataMap();
            param.put("RSRV_STR1", "call MS result: " + resultInfo);
            param.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_RSRVSTR1_BY_ID", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        }
    }

    public IData getSpecTradeInfo(IData data) throws Exception
    {
        IData result = new DataMap();
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        String custId = data.getString("CUST_ID");
        String userId = data.getString("USER_ID");
        IDataset custVip = CustVipInfoQry.getCustVipInfoByCustId(custId, "0");
        // 非大客户 和 CUST_MANAGER_ID为空的大客户 不调客服接,跳出子流程
        if (IDataUtil.isNotEmpty(custVip) && StringUtils.isNotBlank(custVip.getData(0).getString("CUST_MANAGER_ID")))
        {
            IData vipInfo = custVip.getData(0);
            result.put("USECUST_NAME", vipInfo.getString("USECUST_NAME"));
            result.put("CUST_TYPE", vipInfo.getString("CUST_TYPE"));
            result.put("CUST_MANAGER_ID", vipInfo.getString("CUST_MANAGER_ID"));

            if ("7506".equals(tradeTypeCode))
            {
                IDataset userAttr = UserAttrInfoQry.getUserAttrByUserId(userId, "V6206V1");
                if (IDataUtil.isNotEmpty(userAttr))
                {
                    result.put("RSRV_NUM1", userAttr.getData(0).getString("ATTR_VALUE"));
                }
                else
                {
                    result.put("RSRV_NUM1", 10);
                }
            }
            else if ("7507".equals(tradeTypeCode))
            {
                IDataset userSvc = UserSvcInfoQry.getSvcUserId(userId, "650");
                if (IDataUtil.isNotEmpty(userSvc))
                {
                    result.put("REMIND_TAG", 1);
                }
                else
                {
                    result.put("REMIND_TAG", 0);
                }

                IDataset userDiscnt = UserDiscntInfoQry.getUserSpecDiscnt(userId, "5");
                if (IDataUtil.isNotEmpty(userDiscnt))
                {
                    result.put("DISCNT_CODE", userDiscnt.getData(0).getString("DISCNT_CODE"));
                    result.put("DISCNT_NAME", userDiscnt.getData(0).getString("DISCNT_NAME"));
                }
                else
                {
                    result.put("DISCNT_CODE", 0);
                    result.put("DISCNT_NAME", "【客户没有订购GPRS主套餐！】");
                }
            }
            else if ("7101".equals(tradeTypeCode))
            {
                // 普通用户走7101 单停时间即工单执行时间 双停时间是单停加上24小时
                String execTime = data.getString("EXEC_TIME");
                String stopTime = SysDateMgr.addDays(execTime, 1);

                result.put("RSRV_DATE1", execTime);
                result.put("RSRV_DATE2", stopTime);
            }
            else if ("7508".equals(tradeTypeCode))
            {
                // 金钻卡用户走7508 单双停时间相同 都是次月1日零点
                String execTime = data.getString("EXEC_TIME");
                String stopTime = SysDateMgr.getFirstDayOfNextMonth(execTime);
                if (stopTime.length() < 19)
                {
                    stopTime = stopTime + SysDateMgr.START_DATE_FOREVER;
                }
                result.put("RSRV_DATE1", stopTime);
                result.put("RSRV_DATE2", stopTime);
            }
        }
        return result;
    }

}
