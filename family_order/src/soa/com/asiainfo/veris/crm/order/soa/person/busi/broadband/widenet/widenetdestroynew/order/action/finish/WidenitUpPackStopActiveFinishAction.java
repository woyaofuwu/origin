package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.action.finish;  

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * 宽带拆机完工后，处理营销活动终止
 * @author liwei29
 * 
 */
public class WidenitUpPackStopActiveFinishAction implements ITradeFinishAction
{
	//private static Logger logger = Logger.getLogger(WidenitUpPackStopActiveFinishAction.class);
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
    	
    	
    	IData param = new DataMap();
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        
        if(serialNumber.contains("KD_")){
        	serialNumber=serialNumber.replace("KD_", "");
		}
        
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        String normalUserId = uca.getUserId();
        //根据user_id找到到期的营销活动
        IDataset speedUpSaleActives = UserSaleActiveInfoQry.querySaleInfoByUserIdAndCommpara(normalUserId);
        String newEparchyCode = mainTrade.getString("EPARCHY_CODE");
        String execTime = mainTrade.getString("ACCEPT_DATE");
        String tradeId = mainTrade.getString("TRADE_ID");
        //插到期处理表
        if (IDataUtil.isNotEmpty(speedUpSaleActives) && speedUpSaleActives.size() > 0){
        	
        	IData insparam = new DataMap();
        	
        	insparam.put("DEAL_ID", SeqMgr.getTradeId());
        	insparam.put("USER_ID", normalUserId);
            insparam.put("PARTITION_ID", normalUserId.substring(normalUserId.length() - 4));
            insparam.put("SERIAL_NUMBER", serialNumber);
            insparam.put("EPARCHY_CODE", newEparchyCode);
            insparam.put("IN_TIME", SysDateMgr.getSysTime());
            insparam.put("DEAL_STATE", "0");
            insparam.put("DEAL_TYPE", "WidenetStopSaleKDTS");
            insparam.put("EXEC_TIME", execTime);
            insparam.put("EXEC_MONTH", SysDateMgr.getMonthForDate(execTime));
            insparam.put("TRADE_ID", tradeId);
            
            param.put("USER_ID", normalUserId);
            param.put("SERIAL_NUMBER", serialNumber);
            insparam.put("DEAL_COND", param.toString());

            Dao.insert("TF_F_EXPIRE_DEAL", insparam); 
        	
        	
      	  }
        
     
    }
}
