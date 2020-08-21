package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.action;

import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import org.apache.log4j.Logger;

import java.util.List;

public class CheckDiscntServiceExistedAction implements ITradeAction{

	protected static Logger log = Logger.getLogger(CheckDiscntServiceExistedAction.class);
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {

		log.debug("=====CheckDiscntServiceExistedAction==begin===");
		String tradeTypeCode=btd.getTradeTypeCode();

		if(tradeTypeCode.equals("40")){//携入用户开户
			List<DiscntTradeData> userDiscnts = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
			List<SvcTradeData> userSvcs = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
			log.debug("=====CheckDiscntServiceExistedAction==userDiscnts="+userDiscnts+";userSvcs="+userSvcs);
			if(userDiscnts==null || userDiscnts.size()==0){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"携入开户套餐数据没有绑定！");
			}
			if(userSvcs==null || userSvcs.size()==0){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"携入开户服务数据没有绑定！");
			}
		}
	}
}
