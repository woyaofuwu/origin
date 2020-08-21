package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

public class WidenetChangeFinishAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
        String userId = mainTrade.getString("USER_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String tradeId = mainTrade.getString("TRADE_ID");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        String execTime = mainTrade.getString("EXEC_TIME");
        
        //如果用户是宽带用户，就插入到到期处理表当中  
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_"+serialNumber);
        if (IDataUtil.isNotEmpty(widenetInfos)){
          String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
          if (wideType.equals("1")||wideType.equals("2")||wideType.equals("3")||wideType.equals("5")){
        	  IData param = new DataMap();
        	  param.put("DEAL_ID", SeqMgr.getTradeId());
              param.put("USER_ID", userId);
              param.put("PARTITION_ID", userId.substring(userId.length() - 4));

              param.put("SERIAL_NUMBER", serialNumber);
              param.put("EPARCHY_CODE", eparchyCode);
              param.put("IN_TIME", SysDateMgr.getSysTime());
              param.put("DEAL_STATE", "0");
              param.put("DEAL_TYPE", "ChangePhoneWideNet");
              param.put("EXEC_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
              param.put("EXEC_MONTH", param.getString("EXEC_TIME").substring(5, 7));
              param.put("TRADE_ID", tradeId);
              Dao.insert("TF_F_EXPIRE_DEAL", param);
          }
      }
        
        
        
	}
}
