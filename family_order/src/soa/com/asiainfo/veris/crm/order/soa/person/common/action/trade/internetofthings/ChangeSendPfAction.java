package com.asiainfo.veris.crm.order.soa.person.common.action.trade.internetofthings;


import com.ailk.biz.util.StaticUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class ChangeSendPfAction implements ITradeAction
{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
		String pwlwTradeTypeCodeStr = StaticUtil.getStaticValue("PWLW_WAIT_TRADETYPE", "0");
		// TODO Auto-generated method stub
		String tradeTypeCode = btd.getTradeTypeCode();
		if(StringUtils.isNotBlank(pwlwTradeTypeCodeStr))
		{
			if(StringUtils.indexOf(pwlwTradeTypeCodeStr,"|"+tradeTypeCode+"|") != -1)
			{
				UcaData uca = btd.getRD().getUca();
				String brandCode = uca.getBrandCode();
				if(StringUtils.equals("PWLW", brandCode))
				{
					MainTradeData mtd = btd.getMainTradeData();
					mtd.setPfWait("1");
				}
			}
		}
	}

}
