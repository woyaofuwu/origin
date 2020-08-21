package com.asiainfo.veris.crm.order.soa.person.common.action.trade.ocs;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OcsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;

/**
 * Copyright: Copyright (c) 2015
 * 
 * @ClassName: GPRSOnlineCount
 * @Description: 获取【TF_B_TRADE_SVC】数据，根据条件筛选进入表【TF_B_TRADE_OCS】
 * @version: v1.0.0
 * @author: chenxy3
 * @date: 2015-01-13
 * 
 */

public class GPRSOnlineCountAction implements ITradeAction
{
    @Override 
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        /**
         * 符合的业务才继续
         * 开户(10)、批量预开户（500）、 产品变更(110)、销户（192）、VIP备卡激活（144）
		 * 增加携入开户(40)
         **/
        if (!StringUtils.equals("500", tradeTypeCode) && !StringUtils.equals("10", tradeTypeCode) && !StringUtils.equals("110", tradeTypeCode)
        		&& !StringUtils.equals("144", tradeTypeCode) && !StringUtils.equals("192", tradeTypeCode) && !StringUtils.equals("40", tradeTypeCode))
        {
            return;
        }
        List<SvcTradeData> tradeSVC = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        String SerialNumber=btd.getRD().getUca().getSerialNumber();
        
	    if (tradeSVC != null && tradeSVC.size() > 0 ) {
	            for (int i = 0; i < tradeSVC.size(); i++) { 
	            	if(StringUtils.equals("500", tradeTypeCode) || StringUtils.equals("10", tradeTypeCode) || StringUtils.equals("144", tradeTypeCode)
							|| StringUtils.equals("110", tradeTypeCode) || StringUtils.equals("40", tradeTypeCode)){
			            if ("22".equals(tradeSVC.get(i).getElementId()) && BofConst.MODIFY_TAG_ADD.equals(tradeSVC.get(i).getModifyTag()))
			            {
			            	IData id = new DataMap();
			                id.put("USER_ID", tradeSVC.get(i).getUserId()); 
			                id.put("BIZ_TYPE", "3");
			                
			                IDataset isExt = this.checkExistData(id);
			                if(isExt ==null || isExt.size()<1){
				                OcsTradeData otd=new OcsTradeData();
				                otd.setInstId(SeqMgr.getInstId());
				                otd.setUserId(tradeSVC.get(i).getUserId());
				                otd.setUserTypeCode("1");
				                otd.setMonitorType("1");
				                otd.setSerialNumber(SerialNumber);
				                otd.setEparchyCode(btd.getRD().getUca().getUserEparchyCode());
				                otd.setStatus("1");
				                otd.setBizType("3");
				                otd.setMonitorFlag("1");
				                otd.setMonitorRuleCode("0000");
				                otd.setStartDate(SysDateMgr.getSysTime());
				                otd.setEndDate(SysDateMgr.END_DATE_FOREVER);
				                otd.setModifyTag(BofConst.MODIFY_TAG_ADD);
				                otd.setRsrvStr1(btd.getRD().getUca().getUser().getCityCode());
				                btd.add(SerialNumber, otd);
			                }
			            } 
	            	}
	            	if(StringUtils.equals("110", tradeTypeCode) || StringUtils.equals("192", tradeTypeCode) ){
			            if ("22".equals(tradeSVC.get(i).getElementId()) && BofConst.MODIFY_TAG_DEL.equals(tradeSVC.get(i).getModifyTag()))
			            {  
			            	String strBatChId = btd.getRD().getBatchId();
			            	String strBatchOperType = btd.getRD().getPageRequestData().getString("BATCH_OPER_TYPE");
			            	if(!"".equals(strBatChId) && "SERVICECHGSPEC".equals(strBatchOperType))
			            	{
			            		continue;
			            	}
			            	
			                IData id = new DataMap();
			                id.put("USER_ID", tradeSVC.get(i).getUserId()); 
			                id.put("BIZ_TYPE", "3");
			                
			                IDataset isExt = this.checkExistData(id);
			                
			                if (isExt!=null && isExt.size()>0){
			                	OcsTradeData otd=new OcsTradeData();
				                otd.setInstId(isExt.getData(0).getString("INST_ID"));
				                otd.setUserId(tradeSVC.get(i).getUserId());
				                otd.setUserTypeCode(isExt.getData(0).getString("USER_TYPE_CODE"));
				                otd.setMonitorType(isExt.getData(0).getString("MONITOR_TYPE"));
				                otd.setSerialNumber(SerialNumber);
				                otd.setEparchyCode(isExt.getData(0).getString("EPARCHY_CODE"));
				                otd.setStatus(isExt.getData(0).getString("STATUS"));
				                otd.setBizType(isExt.getData(0).getString("BIZ_TYPE"));
				                otd.setMonitorFlag(isExt.getData(0).getString("MONITOR_FLAG"));
				                otd.setMonitorRuleCode(isExt.getData(0).getString("MONITOR_RULE_CODE"));
				                otd.setStartDate(isExt.getData(0).getString("START_DATE"));
				                otd.setEndDate(tradeSVC.get(i).getEndDate());
				                otd.setModifyTag(BofConst.MODIFY_TAG_DEL);
				                otd.setRsrvStr1(isExt.getData(0).getString("RSRV_STR1")); 
				                btd.add(SerialNumber, otd);
			                }
			            } 
	            	}
	            }
	     }
         
    }
    
    private IDataset checkExistData(IData inparams)throws Exception{
    	 
    	SQLParser parser = new SQLParser(inparams); 
        parser.addSQL(" select t.PARTITION_ID,t.INST_ID,  t.EPARCHY_CODE, t.CITY_CODE, t.MONITOR_TYPE, t.USER_TYPE_CODE, ");
        parser.addSQL(" t.STATUS, to_char(t.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, t.BIZ_TYPE, t.MONITOR_FLAG, ");
        parser.addSQL(" t.MONITOR_RULE_CODE ,t.RSRV_STR1 from TF_F_USER_OCS t ");
        parser.addSQL(" WHERE t.user_id = :USER_ID ");
        parser.addSQL(" AND  T.BIZ_TYPE = :BIZ_TYPE "); 
        parser.addSQL(" and sysdate between t.start_date and t.end_date "); 
    	return Dao.qryByParse(parser);  
    }
}