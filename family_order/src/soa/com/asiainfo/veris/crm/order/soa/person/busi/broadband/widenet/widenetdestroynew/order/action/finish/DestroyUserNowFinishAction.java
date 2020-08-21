package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.action.finish;  


import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 宽带特殊拆机完工后，处理营销活动终止
 * 
 * kangyt 2016-5-1
 * 
 * 修改历史 
 * 
 * 2016-6-2
 * 因为现在的营销活动结束时间是2050年，一个周期后就可以使用普通拆机了，
 * 所以普通拆机后也需要调用营销活动终止，否则活动就会一直有效
 * 
 */
public class DestroyUserNowFinishAction implements ITradeFinishAction
{
	private static Logger logger = Logger.getLogger(DestroyUserNowFinishAction.class);
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        IData param = new DataMap();
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        String newEparchyCode = mainTrade.getString("EPARCHY_CODE");
        String execTime = mainTrade.getString("ACCEPT_DATE");
        String SERIAL_NUMBER_A = "";
    	if(serialNumber.indexOf("KD_")>-1) {//宽带账号
    		if(serialNumber.split("_")[1].length()>11)
    			SERIAL_NUMBER_A = serialNumber;//商务宽带
    		else
    			SERIAL_NUMBER_A = serialNumber.split("_")[1];//个人账号
    	}
    	else {
    		if(serialNumber.length()>11)
    			SERIAL_NUMBER_A="KD_"+serialNumber;
    		else
    			SERIAL_NUMBER_A= serialNumber;
    	}
        IDataset ret = UserInfoQry.getUserinfo(SERIAL_NUMBER_A);
        if(IDataUtil.isEmpty(ret))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到对应的用户信息，请检查数据是否完整！["+SERIAL_NUMBER_A+"]");
        }
        userId = ret.getData(0).getString("USER_ID","");
        if("615".equals(tradeTypeCode)){
	        	IData insparam = new DataMap();
	        	
	        	insparam.put("DEAL_ID", SeqMgr.getTradeId());
	        	insparam.put("USER_ID", userId);
	            insparam.put("PARTITION_ID", userId.substring(userId.length() - 4));
	            insparam.put("SERIAL_NUMBER", SERIAL_NUMBER_A);
	            insparam.put("EPARCHY_CODE", newEparchyCode);
	            insparam.put("IN_TIME", SysDateMgr.getSysTime());
	            insparam.put("DEAL_STATE", "0");
	            insparam.put("DEAL_TYPE", "WidenetCancelActive");
	            insparam.put("EXEC_TIME", execTime);
	            insparam.put("EXEC_MONTH", SysDateMgr.getMonthForDate(execTime));
	            insparam.put("TRADE_ID", tradeId);
	            
	            param.put("USER_ID", userId);
	            param.put("SERIAL_NUMBER", SERIAL_NUMBER_A);
	            insparam.put("DEAL_COND", param.toString());

	            Dao.insert("TF_F_EXPIRE_DEAL", insparam); 
        }
    } 
}
