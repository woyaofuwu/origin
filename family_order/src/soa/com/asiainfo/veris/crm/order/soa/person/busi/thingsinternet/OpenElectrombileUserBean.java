package com.asiainfo.veris.crm.order.soa.person.busi.thingsinternet;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;

public class OpenElectrombileUserBean {

	public static DiscntTradeData getUserDiscnt(UcaData uca) throws Exception
	{
		DiscntTradeData userDiscnt = null;
		IDataset discntList = CommparaInfoQry.getCommByParaAttr("CSM", "6001", "ZZZZ");
		for(int i = 0, size = discntList.size(); i < size; i++)
		{
			IData tmp = discntList.getData(i);
			List<DiscntTradeData> userDiscnts = uca.getUserDiscntByDiscntId(tmp.getString("PARAM_CODE"));
			if(userDiscnts != null && userDiscnts.size() > 0)
			{
				userDiscnt = userDiscnts.get(0);
				break;
			}
		}
		
		return userDiscnt;
	}
	
	public static PlatSvcTradeData getUserPlatSvc(UcaData uca) throws Exception
	{
		PlatSvcTradeData userPlatSvc = null;
		IDataset svcList = CommparaInfoQry.getCommByParaAttr("CSM", "5002", "ZZZZ");
		for(int i = 0, size = svcList.size(); i < size; i++)
		{
			IData tmp = svcList.getData(i);
			List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId(tmp.getString("PARAM_CODE"));
			if(userPlatSvcs != null && userPlatSvcs.size() > 0)
			{
				userPlatSvc = userPlatSvcs.get(0);
				break;
			}
		}
		
		return userPlatSvc;
	}
}
