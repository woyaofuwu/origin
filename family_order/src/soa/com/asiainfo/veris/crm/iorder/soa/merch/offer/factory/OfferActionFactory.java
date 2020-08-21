package com.asiainfo.veris.crm.iorder.soa.merch.offer.factory;

import java.util.HashMap;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.iorder.soa.merch.offer.DisposeAction;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class OfferActionFactory
{
	private static Map<String, DisposeAction> classMap = new HashMap<String, DisposeAction>();

	static
	{
		try
		{
			IDataset datas = CommparaInfoQry.getCommparaByParaAttr("CSM", "1225", "ZZZZ");
			if (IDataUtil.isNotEmpty(datas))
			{
				IData data = datas.first();
				String className = data.getString("PARA_CODE17");
				String type = data.getString("PARAM_CODE");
				DisposeAction action = (DisposeAction) Class.forName(className).newInstance();
				classMap.put(type, action);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static DisposeAction getAction(String type) throws Exception
	{
		DisposeAction action = null;
		if (!classMap.containsKey(type))
		{
			IDataset datas = CommparaInfoQry.getCommparaAllCol("CSM", "1225", type, "ZZZZ");
			if (IDataUtil.isNotEmpty(datas))
			{
				IData data = datas.first();
				String className = data.getString("PARA_CODE17");
				action = (DisposeAction) Class.forName(className).newInstance();
				classMap.put(type, action);
			}
		}
		action = classMap.get(type);
		return action;
	}

}
