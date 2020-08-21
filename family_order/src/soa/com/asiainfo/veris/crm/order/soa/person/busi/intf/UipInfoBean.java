
package com.asiainfo.veris.crm.order.soa.person.busi.intf;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tradenetbook.TradeNetBookInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.NotePrintBean;

public class UipInfoBean extends CSBizBean
{

    public IData getEFormInfo(IData inparam) throws Exception
    {
        String tradeId = inparam.getString("TRADE_ID", "");
        IData result = new DataMap();
        if ("".equals(tradeId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的参数TRADE_ID不能为空！");
        }
        else
        {
            IDataset idata = TradeNetBookInfoQry.getEFormInfoByTradeId(tradeId);
            IData tradeDate=TradeNetBookInfoQry.getInfoByTradeId(tradeId);
            if (idata.size() < 1)
            {	
            	if(tradeDate==null){
            		CSAppException.apperr(CrmCommException.CRM_COMM_13, "100", "传入的参数TRADE_ID找不到对应的电子工单信息！");
            	}else {//只用于600/6800 查询时生成电子工单
            	tradeDate.put(Route.ROUTE_EPARCHY_CODE, tradeDate.getString("EPARCHY_CODE", getTradeEparchyCode()));
            	CSAppCall.call("CS.PrintNoteSVC.getPrintData", tradeDate);
            	result=getEFormInfo(inparam);
            	}
            }
            else if (idata.size() > 1)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_13, "100", "传入的参数TRADE_ID找到多个的电子工单信息！");
            }
            else
            {
                result.put("billid", idata.getData(0).getString("TRADE_ID", ""));
                result.put("brand_name", idata.getData(0).getString("BRAND_NAME", ""));
                result.put("brand_code", idata.getData(0).getString("BRAND_CODE", ""));
                result.put("work_name", idata.getData(0).getString("TRADE_STAFF_NAME", ""));
                result.put("work_no", idata.getData(0).getString("TRADE_STAFF_ID", ""));
                result.put("org_info", idata.getData(0).getString("ORG_INFO", ""));
                result.put("org_name", idata.getData(0).getString("ORG_NAME", ""));
                result.put("phone", idata.getData(0).getString("SERIAL_NUMBER", ""));
                result.put("serv_id", idata.getData(0).getString("USER_ID", ""));
                result.put("op_time", idata.getData(0).getString("ACCEPT_DATE", ""));
                // IDataset busi_list = new DatasetList();
                IData busi_info = new DataMap();

                busi_info.put("op_code", idata.getData(0).getString("TRADE_TYPE_CODE", ""));
                busi_info.put("sys_accept", idata.getData(0).getString("TRADE_ID", ""));
                busi_info.put("busi_detail", idata.getData(0).getString("RECEIPT_INFO1", "") + "##" + idata.getData(0).getString("RECEIPT_INFO2", "") + "##" + idata.getData(0).getString("RECEIPT_INFO3", "") + "##"
                        + idata.getData(0).getString("RECEIPT_INFO4", "") + "##" + idata.getData(0).getString("RECEIPT_INFO5", "") + "##" + idata.getData(0).getString("NOTICE_CONTENT", ""));

                result.putAll(busi_info);
                // busi_list.add(busi_info);
                // result.put("busi_list", busi_list.toWadeString());

                result.put("verify_mode", idata.getData(0).getString("VERIFY_MODE", ""));
                result.put("id_card", idata.getData(0).getString("ID_CARD", ""));
                result.put("cust_name", idata.getData(0).getString("CUST_NAME", ""));
                result.put("copy_flag", "");
                result.put("agm_flag", "");
            }
        }
        return result;
    }
    
    public IData queryIsPrint(IData inparam) throws Exception
    {
    	 IDataUtil.chkParam(inparam, "STAFF_ID"); 
    	
         IData result = new DataMap();
         result.put("X_RESULTCODE", "0000");
         result.put("X_RESULTINFO", "操作成功");
         
         IDataset infos = TradeNetBookInfoQry.queryCnotePrintTag(inparam);
         if(infos!=null && infos.size() >0){
        	 result.put("IS_PRINT", "0");
         }else{
        	 result.put("IS_PRINT", "1");
         }
         
    	return result;
    }
    
    public IData queryPrintCnote(IData inparam) throws Exception
    {
    	IDataUtil.chkParam(inparam, "TRADE_STAFF_ID"); 
    	IDataUtil.chkParam(inparam, "START_DATE"); 
    	IDataUtil.chkParam(inparam, "END_DATE"); 
    	
    	IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "操作成功");
        
    	String staffId = inparam.getString( "TRADE_STAFF_ID");
		String startDate = DateFormatUtils.format(SysDateMgr.string2Date(inparam.getString("START_DATE"), SysDateMgr.PATTERN_TIME_YYYYMMDD), SysDateMgr.PATTERN_STAND_YYYYMMDD);
		String endDate = DateFormatUtils.format(SysDateMgr.string2Date(inparam.getString("END_DATE"), SysDateMgr.PATTERN_TIME_YYYYMMDD), SysDateMgr.PATTERN_STAND_YYYYMMDD);
		String subscribeState = "Y";
		
		IDataset unPrintTrades = TradeInfoQry.qryTradesByStaffIdDate(staffId, startDate, endDate, subscribeState, inparam.getString("SERIAL_NUMBER"));
		for (int i = 0; i < unPrintTrades.size(); i++)
		{
			IData printsInfo = (IData) unPrintTrades.getData(i);
			printsInfo.put("TRADE_TYPE", UTradeTypeInfoQry.getTradeTypeName(printsInfo.getString("TRADE_TYPE_CODE", "")));
			
		}
			
		result.put("PRINT_LIST", unPrintTrades);
    	return result;
    }
    
    public IData dealPrintTag(IData inparam) throws Exception
    {
    	IDataUtil.chkParam(inparam, "TRADE_ID"); 
    	String tradeId = inparam.getString("TRADE_ID", "");
    	
    	IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "操作成功");
    	
    	IDataset tradeDatas = TradeInfoQry.getMainTradeByTradeId(tradeId);
    	if(IDataUtil.isNotEmpty(tradeDatas)){
    		String orderId = tradeDatas.first().getString("ORDER_ID");
    		NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
    		inparam.clear();
    		inparam.put("ORDER_ID", orderId);
    		inparam.put("ORDER_STATE", "0");
    		inparam.put("SUBSCRIBE_STATE", "0");
            bean.updateOrderState(inparam);
    	}else{
    		result.put("X_RESULTCODE", "1001");
            result.put("X_RESULTINFO", tradeId+"未查到该笔受理单！");
    	}
    	
//        IDataset tradeDatas = TradeInfoQry.getMainTradeByTradeId(tradeId);
//        if(IDataUtil.isNotEmpty(tradeDatas)){
//        	IDataset trades = TradeInfoQry.getTradeInfobyOrd2(tradeDatas.first().getString("ORDER_ID"));
//        	for(int i=0 ;i<trades.size();i++){
//        		String subscribeState = trades.getData(i).getString("SUBSCRIBE_STATE"); 
//        		if("Y".equals(subscribeState)){
//        			IData param1 = new DataMap();
//    		        param1.put("DEAL_STATE", "0");
//    		        param1.put("DEAL_ID", trades.getData(i).getString("TRADE_ID"));
//    		        param1.put("DEAL_TAG", "0");
//    		        Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_DEALSTATE", param1, Route.getJourDb("0898"));
//    		        
//    		        param1.clear();
//    		        param1.put("ORDER_STATE", "0");
//    		        param1.put("ORDER_ID", tradeDatas.first().getString("ORDER_ID"));
//    		        param1.put("TRADE_TYPE_CODE", tradeDatas.first().getString("TRADE_TYPE_CODE"));
//    		        Dao.executeUpdateByCodeCode("TF_B_ORDER", "UPD_STATE_BY_ORDERID2", param1, Route.getJourDb("0898"));
//    		        
//    		        
//        		}
//        	}
//        }else{
//        	result.put("X_RESULTCODE", "1001");
//            result.put("X_RESULTINFO", tradeId+"未查到该笔受理单！");
//        }
        
    	return result;
    }
    
}
