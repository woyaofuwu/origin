package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * @author Administrator
 */
public class Check5GSVCAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		
		String userId = btd.getRD().getUca().getUserId();
		String serialNumber = btd.getRD().getUca().getSerialNumber();
		String tradeTypeCode = btd.getTradeTypeCode();
		//5G消息服务   84076654 
    	IDataset usersvcs = UserSvcInfoQry.getSvcUserId(userId, "84076654");
    	if(DataUtils.isNotEmpty(usersvcs))
    	{
    		//补换卡142，如果用户存在5G消息服务，在预留字段2赋值，服开根据补换卡142和预留字段2的值拆单给数指
    		if("142".equals(tradeTypeCode)){
    			btd.getMainTradeData().setRsrvStr2("5G_OPEN");
    		}else if("100".equals(tradeTypeCode)){//客户通过到营业前台办理过户业务，注销5G消息，生成相关的注销订单
    			for(int i=0;i<usersvcs.size();i++){
    	        	IData svcInfo=usersvcs.getData(i);
    	        	SvcTradeData TradeData = new SvcTradeData(svcInfo);
    	        	TradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
    	        	TradeData.setEndDate(SysDateMgr.getSysTime());
    	            btd.add(serialNumber, TradeData);        	  
            	}
    		}
        }
	}
}