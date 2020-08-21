package com.asiainfo.veris.crm.order.soa.person.busi.thingsinternet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class OpenElectrombileUserSVC extends CSBizService
{

	public IData loadInfo(IData data) throws Exception
	{
		IData rtData = new DataMap();
		String serialNumber = data.getString("SERIAL_NUMBER");
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);

		IDataset svcList = CommparaInfoQry.getCommByParaAttr("CSM", "5002", "ZZZZ");
		IDataset discntList = CommparaInfoQry.getCommByParaAttr("CSM", "6001", "ZZZZ");
		rtData.put("SVCLIST", svcList);
		rtData.put("DISCNTLIST", discntList);

		DiscntTradeData userDiscnt = OpenElectrombileUserBean.getUserDiscnt(uca);
		if (userDiscnt != null)
		{
			String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(userDiscnt.getDiscntCode());
			rtData.put("DISCNT_NAME", userDiscnt.getDiscntCode() + ":" + discntName);
		}

		PlatSvcTradeData userPlatSvc = OpenElectrombileUserBean.getUserPlatSvc(uca);
		if (userPlatSvc != null)
		{
			String svcName = "";
			IData map = PlatSvcInfoQry.queryPlatsvcByPk(userPlatSvc.getElementId());
			if (IDataUtil.isNotEmpty(map))
			{
				svcName = map.getString("SERVICE_NAME");
			}
			rtData.put("SVC_NAME", userPlatSvc.getElementId() + ":" + svcName);

			String bizStateCode = userPlatSvc.getBizStateCode();
			rtData.put("SVC_STATE", bizStateCode);
		}

		IDataset result = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(uca.getUserId(), "88", "2");
		if (IDataUtil.isNotEmpty(result))
		{
			IData tmp = result.getData(0);
			String oldSerialNumber = tmp.getString("SERIAL_NUMBER_A").substring(2);
			rtData.put("OLD_SERIAL_NUMBER", oldSerialNumber);
		}

		return rtData;
	}
}
