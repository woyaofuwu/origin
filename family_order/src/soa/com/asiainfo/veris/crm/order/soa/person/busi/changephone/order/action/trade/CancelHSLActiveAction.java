package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.action.trade;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;

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

public class CancelHSLActiveAction implements ITradeAction
{
    @Override 
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String endDiscntCode="4876001";
    	String tradeTypeCode = btd.getTradeTypeCode();
        /**
         * 符合的业务才继续
         * 对于过户100、改号143的用户，将未生效的【和石榴】优惠终止。对于已经生效的不做处理。
         * */ 
        String SerialNumber=btd.getRD().getUca().getSerialNumber();
        String userId=btd.getRD().getUca().getUserId(); 
        IData inparams=new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("END_DISCNT_CODE", endDiscntCode);
        IDataset userDiscnts=this.getHSLDiscnt(inparams);
        String toDay=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
	    if (userDiscnts != null && userDiscnts.size() > 0 ) {
	            for (int i = 0; i < userDiscnts.size(); i++) { 
	            	IData d1=userDiscnts.getData(i);
	            	DiscntTradeData userDiscnt=new DiscntTradeData(d1);
	            	userDiscnt.setModifyTag(BofConst.MODIFY_TAG_DEL);
	            	userDiscnt.setEndDate(toDay); 
	            	btd.add(SerialNumber, userDiscnt);
	            }
	    }
	}
    private IDataset getHSLDiscnt(IData inparams)throws Exception{
   	 
    	SQLParser parser = new SQLParser(inparams); 
        parser.addSQL(" select t.*");
        parser.addSQL(" from tf_f_user_discnt t ");
        parser.addSQL(" WHERE t.user_id = :USER_ID ");
        parser.addSQL(" AND partition_id = MOD(TO_NUMBER(:USER_ID),10000) ");
        parser.addSQL(" AND t.discnt_code = :END_DISCNT_CODE ");
        parser.addSQL(" AND sysdate<t.start_date ");
        parser.addSQL(" AND sysdate<t.end_date ");
        parser.addSQL(" AND t.start_date<t.end_date ");
        ; 
    	return Dao.qryByParse(parser);  
    }
}