package com.asiainfo.veris.crm.order.soa.person.busi.createhytusertrade.order.action;  

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

/**
 * 同步TF_F_SHIP_INFO数据
 * @author Administrator
 *
 */
public class SyncDataAction implements ITradeFinishAction
{
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
    	
    	IData insparam = new DataMap();
    	insparam.put("INST_ID", SeqMgr.getInstId());
    	insparam.put("IS_OWNER_DISCNT",  mainTrade.getString("RSRV_STR3"));
    	insparam.put("USER_ID", mainTrade.getString("USER_ID"));
        insparam.put("SHIP_ID", mainTrade.getString("RSRV_STR1"));
        insparam.put("IS_OWNER", mainTrade.getString("RSRV_STR2"));
        insparam.put("DISCNT_CODE",  mainTrade.getString("RSRV_STR6"));
        insparam.put("START_DATE", mainTrade.getString("RSRV_STR4"));
        insparam.put("END_DATE",mainTrade.getString("RSRV_STR5"));
        insparam.put("IS_HYT_OPEN",mainTrade.getString("1"));
        Dao.insert("TF_F_SHIP_INFO", insparam); 
    }
}
