
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.action.finish;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;

/**
 * 宽带产品变更，FTTB用户迁移提速活动终止
 */
public class WidenetMoveFTTBActiveEndAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String eparchy_code = mainTrade.getString("EPARCHY_CODE");
        String tradeId = mainTrade.getString("TRADE_ID");
        String orderId = mainTrade.getString("ORDER_ID");
    	if(serialNumber.startsWith("KD_")){
    		serialNumber = serialNumber.substring(3);
    	}
    	 IDataset comparas = BreQryForCommparaOrTag.getCommpara("CSM", 5890, "ZZZZ");
    	 if (IDataUtil.isNotEmpty(comparas) && comparas.size() > 0) {
    		 String productId = ((IData) comparas.get(0)).getString("PARAM_CODE");
    		 String packageId = ((IData) comparas.get(0)).getString("PARA_CODE1");
    		 
    		 IData params=new DataMap();
    		 params.put("SERIAL_NUMBER", serialNumber);
    		 params.put("PRODUCT_ID", productId);
    		 params.put("PACKAGE_ID", packageId);
    		 IDataset activeList =  Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_SERIALNUMBER_PRODUCTID", params); 
    		 
    		 if(!activeList.isEmpty() && activeList.size()>0){
    			 IData endActiveParam = new DataMap();
        		 endActiveParam.put("SERIAL_NUMBER", serialNumber);
        		 endActiveParam.put("PRODUCT_ID", productId);
        		 endActiveParam.put("PACKAGE_ID", packageId);
        		 endActiveParam.put("RELATION_TRADE_ID",  ((IData)activeList.get(0)).getString("RELATION_TRADE_ID"));
        		 endActiveParam.put("CHECK_MODE", "F");
        		 endActiveParam.put("CAMPN_TYPE",  ((IData)activeList.get(0)).getString("CAMPN_TYPE"));
        		 endActiveParam.put("RETURNFEE", "0");
        		 endActiveParam.put("YSRETURNFEE", "0");
        		 endActiveParam.put("TRUE_RETURNFEE_COST", "0");
        		 endActiveParam.put("TRUE_RETURNFEE_PRICE", "0");
        		 endActiveParam.put("END_DATE_VALUE", "0"); 

        		 endActiveParam.put("END_MONTH_LAST", "Y");
        		 endActiveParam.put("NO_TRADE_LIMIT", "TRUE");
        		 endActiveParam.put("SKIP_RULE", "TRUE");
        		 
        		String execTime = "";
        		String userId =  ((IData)activeList.get(0)).getString("USER_ID");
        		String startDate =  ((IData)activeList.get(0)).getString("START_DATE");
        		if(StringUtils.isNotBlank(startDate) && startDate.length() >= 10){
        			execTime = startDate.substring(0, 10);
        		}
        		 
        		IData insparam = new DataMap();
 	        	
 	        	insparam.put("DEAL_ID", SeqMgr.getTradeId());
 	        	insparam.put("USER_ID", userId);
 	            insparam.put("PARTITION_ID", userId.substring(userId.length() - 4));
 	            insparam.put("SERIAL_NUMBER", serialNumber);
 	            insparam.put("EPARCHY_CODE", eparchy_code);
 	            insparam.put("IN_TIME", SysDateMgr.getSysTime());
 	            insparam.put("DEAL_STATE", "0");
 	            insparam.put("DEAL_TYPE", "WidenetEndActive");
 	            insparam.put("EXEC_TIME", execTime);
 	            insparam.put("EXEC_MONTH", SysDateMgr.getMonthForDate(execTime));
 	            insparam.put("TRADE_ID", tradeId);
 	            
 	            insparam.put("DEAL_COND", endActiveParam.toString());
 	            //插入到期处理表
 	            Dao.insert("TF_F_EXPIRE_DEAL", insparam); 
    		 }
    		 
    		 
	        
    		 
         }
    }
}
