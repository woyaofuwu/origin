package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePostPersonUserBean;

/**
 * 修改邮寄号码状态
 * 
 * @author liusj
 */
public class DealPostCardAction implements ITradeAction
{

	public void executeAction(BusiTradeData btd) throws Exception
	{

		UcaData uca = btd.getRD().getUca();
		String serial_number = uca.getSerialNumber();

		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serial_number);
		CreatePostPersonUserBean CreatePostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
		IDataset postinfo = CreatePostPersonUserBean.getPostCardInfo(data);

		if (IDataUtil.isNotEmpty(postinfo))
		{
        	  IData returnData=  postinfo.getData(0);

			if ("0".equals(returnData.getString("STATE")))
			{
				IData input = new DataMap();

				input.put("SERIAL_NUMBER", serial_number);
        		input.put("STATE", "1");
        		input.put("OLD_STATE", "0");
				CreatePostPersonUserBean.updatePostCardInfo(input);

			}

		}

	}

}
