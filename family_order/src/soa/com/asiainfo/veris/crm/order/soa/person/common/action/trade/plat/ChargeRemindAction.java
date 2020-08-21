/**
 * 
 */
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;

/**
 */
public class ChargeRemindAction implements ITradeFinishAction
{

    /* (non-Javadoc)
     */
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
    	String tradeId = mainTrade.getString("TRADE_ID");
    	IDataset platSvcTradeDatas = TradePlatSvcInfoQry.getTradePlatSvcByTradeId(tradeId);
    	
        int size = platSvcTradeDatas.size();
        for (int i = 0; i < size; i++)
        {
//            PlatSvcTradeData pstd = platSvcTradeDatas.get(i);
        	IData pstd_data = platSvcTradeDatas.getData(i);
        	PlatSvcTradeData pstd = new PlatSvcTradeData(pstd_data);
            PlatOfficeData officeData = null;
            if (pstd.getPmd() == null)
            {
                officeData = PlatOfficeData.getInstance(pstd.getElementId());
            }
            else
            {
                officeData = ((PlatSvcData) pstd.getPmd()).getOfficeData();
            }
            String operCode = pstd.getOperCode();
            String inModeCode = CSBizBean.getVisit().getInModeCode();
            String pkgSeq = pstd.getPkgSeq();
            String oprSource = pstd.getOprSource();
            String spCode = officeData.getSpCode();
            String bizCode = officeData.getBizCode();
            String billType = officeData.getBillType();
            String servMode = officeData.getServMode();
            String feeModeTag = officeData.getFeeModeTag();
            String bizTypeCode = officeData.getBizTypeCode();
            String feeWarnTag = officeData.getFeeWarnTag();
//            String secondConfirm = officeData.getSecConfirmTag();// 0:订购时，不需要二次确认 1:订购时需要二次确认
            
            if(!operCode.equals("06")){
            	continue;
            }
            
            IDataset spTariffInfo = PlatSvcInfoQry.querySpTariff(spCode, bizCode, bizTypeCode);
            if(IDataUtil.isNotEmpty(spTariffInfo)){
//            if(billType.equals("2") && feeModeTag.equals("0")&& feeWarnTag.equals("1") && operCode.equals("06")){

	            IDataset smsConfigs = PlatSvcInfoQry.queryPlatSmsConfig(inModeCode, bizTypeCode, spCode, bizCode, operCode, oprSource, billType, servMode, feeModeTag);
	            if (IDataUtil.isNotEmpty(smsConfigs))
	            {
	                int smsSize = smsConfigs.size();
	                for (int j = 0; j < smsSize; j++)
	                {
	                    IData smsConfig = smsConfigs.getData(j);
	                    String eventType = smsConfig.getString("EVENT_TYPE");
	                    String smsType = smsConfig.getString("SMS_TYPE", "0");
	
	                    if (!PlatConstants.SMS_TYPE_SECOND_CONFIRM.equals(eventType))
	                    {
	                    	//newbilling 扣费提醒改造
                    		stringChargeremindTradeSms(mainTrade, smsType, pstd, officeData, pstd.getStartDate(), i+1);
                    		callchargeremindProc(mainTrade,i+1);
	                    }
	                }
	            }
            }
        }
        
    }

  //扣费提醒资料处理
    private void stringChargeremindTradeSms(IData mainTrade, String smsType, PlatSvcTradeData pstd, PlatOfficeData officeData, String startDate,int iSize) throws Exception
    {
//        IData smsData = getCommonSmsInfo(btd, smsType, "");
    	String tradeId = mainTrade.getString("TRADE_ID");
    	String smsTradeId = iSize+tradeId.substring(5);
    	
        IData param = new DataMap();
        param.put("TRADE_ID", smsTradeId);
        param.put("SEQ", tradeId.substring(12));
        param.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER"));
        param.put("SP_CODE", officeData.getSpCode());
        param.put("SERVICE_CODE", "0");
//        param.put("OPER_CODE", pstd.getOperCode());
        param.put("OPER_CODE", officeData.getBizCode());//这里插biz_code
//        param.put("START_DATE", SysDateMgr.getSysDateYYYYMMDD());
//        param.put("START_TIME", (SysDateMgr.getSysDateYYYYMMDDHHMMSS()).substring(8));
        String startTime = startDate.replaceAll("\\D", "");
        param.put("START_DATE", startTime.substring(0,8));
        param.put("START_TIME", startTime.substring(8));
        param.put("STATUS", "0");//??
        param.put("FILE_NO", smsTradeId);
        param.put("CONTENT", "");
        param.put("ACT_MODE", "0");
        Double fee = Double.parseDouble(officeData.getPrice());
        param.put("FEE",(int)(fee*1000));//直接取的fee单位是元
        param.put("SEND_TIME", "");
        param.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        
        param.put("FEE_FLAG", "1");
        param.put("SMS_TYPE", smsType);
        param.put("UPDATE_TIME", SysDateMgr.getSysTime());
        param.put("RSRV_STR1", "");
        param.put("RSRV_STR2", "");
        param.put("RSRV_STR3", "");
        param.put("RSRV_STR4", "");
        param.put("RSRV_STR5", "");
        param.put("USER_ID", mainTrade.getString("USER_ID"));
        param.put("CHARGE_TYPE", "6");
        param.put("CONTEXT_ID", "");
        param.put("DEDUCT_TYPE", officeData.getFeeModeTag());
        Dao.insert("TP_P_USER_CHARGEREMIND_IN", param);
    }
    
    //处理扣费提醒短信
    private void callchargeremindProc(IData mainTrade,int iSize) throws Exception{
    	
    	String paramName[] = {"v_filename", "v_resultcode", "v_resultinfo"};
    	IData paramValue = new DataMap();
    	String tradeId = mainTrade.getString("TRADE_ID");
    	paramValue.put("v_filename", iSize+tradeId.substring(5));
    	paramValue.put("v_resultcode", "");
    	paramValue.put("v_resultinfo", "");
    	
    	Dao.callProc("P_TP_F_USER_CHARGEREMIND_2", paramName, paramValue);
    	
    }
    
}
