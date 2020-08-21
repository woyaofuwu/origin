package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class PlatRelaFubaoDiscntAction implements ITradeAction
{
	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
		List<PlatSvcTradeData> pstds = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
		int size = pstds.size();
		for (int i = 0; i < size; i++)
		{
			PlatSvcTradeData pstd = pstds.get(i);
			IDataset userDiscnts = UserDiscntInfoQry.getFubaoDiscntByUSD(btd.getRD().getUca().getUserId(), btd.getTradeTypeCode(),pstd.getElementId());
			if (PlatConstants.OPER_CANCEL_ORDER.equals(pstd.getOperCode()) && IDataUtil.isNotEmpty(userDiscnts))
			{
				//是否用户已经订购了该优惠
            	String delDiscnts="84008844,84008845,84008846,84009838,84009839,84009840,84009841,"+userDiscnts.getData(0).getString("DISCNT_CODE", "");
                List<DiscntTradeData> discntTradeDatas = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(delDiscnts);

                if (discntTradeDatas.size() == 0)
                {
                    continue;
                }
                for (int k = 0; k < discntTradeDatas.size(); k++)
                {
    	        	DiscntTradeData delDiscntTD = discntTradeDatas.get(k).clone();
    	        	if(!userDiscnts.getData(0).getString("DISCNT_CODE", "").equals(delDiscntTD.getRsrvStr5()))
    	        	{
    	        		continue;
    	        	}
    	        	delDiscntTD.setEndDate(SysDateMgr.getLastDateThisMonth());
    	        	delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
    	        	delDiscntTD.setRemark("取消“咪咕流量福包”活动，终止绑定套餐！");				          				
    				btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTD);
            		
                }
			}
		}
	}


}
