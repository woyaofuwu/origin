package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.requestdata.ServiceOperReqData;

public class SubscribeInfiniteDiscntRegAction implements ITradeAction{

	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {	
		//如果是激活操作
		ServiceOperReqData req = (ServiceOperReqData) btd.getRD();
		String operCode=req.getOperCode();
		
		//如果不是恢复操作，就终止
		if(operCode==null||(operCode!=null&&!operCode.equals("05"))){
			return ;
		}
		
		//如果是月初的恢复，也终止
		IData pageData=btd.getRD().getPageRequestData();
		if(IDataUtil.isNotEmpty(pageData)){
			String expireActive=pageData.getString("EXPIRE_ACTIVE","");
			if(expireActive.equals("1")){
				return ;
			}
		}
		
		
		UcaData uca=btd.getRD().getUca();
		String userId=uca.getUserId();
		String curMonth=SysDateMgr.getCurMonth();
		
		boolean isNeedSubscribe=false;
		
		//查询本月是否存在15G 暂停上网服务的情况
		IDataset operData=TradeInfoQry.query130Oper(curMonth, userId, "22", "04", "0");
		if(IDataUtil.isNotEmpty(operData)){
			isNeedSubscribe=true;
		}
		
		if(isNeedSubscribe){
			IDataset discntData=UserDiscntInfoQry.getAllDiscntByUser(userId, "4700");
			
			if(IDataUtil.isEmpty(discntData)){
				DiscntTradeData discntTrade=new DiscntTradeData();
				discntTrade.setUserId(userId);
				discntTrade.setUserIdA("-1");
				discntTrade.setPackageId("-1");
				discntTrade.setProductId("-1");
				discntTrade.setElementId("4700");
				discntTrade.setElementType("D");
				discntTrade.setSpecTag("0");
				discntTrade.setInstId(SeqMgr.getInstId());
				discntTrade.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
				discntTrade.setEndDate(SysDateMgr.getLastDateThisMonth());
				discntTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
				discntTrade.setRemark("15G封顶重启上网功能自动绑定");
				
				btd.add(uca.getSerialNumber(), discntTrade);
			}
			
		}

    }
	
	
}
