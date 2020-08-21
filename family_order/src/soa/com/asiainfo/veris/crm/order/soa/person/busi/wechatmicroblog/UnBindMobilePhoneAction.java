
package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;

public class UnBindMobilePhoneAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
    	IData data = new DataMap();
    	//UcaData uca = btd.getRD().getUca();
    	
    	String serialNumber = mainTrade.getString("SERIAL_NUMBER");//uca.getSerialNumber();
    	String userId = mainTrade.getString("USER_ID");//uca.getUserId();
    	
    	IDataset dataset = UserIdentInfoQry.queryAccountBySerialNumber(userId, serialNumber);
    	if(IDataUtil.isEmpty(dataset)){
    		
		}else{
			data.put("OPR_NUMB", dataset.getData(0).getString("OPR_NUMB"));
        	data.put("MICRO_ACCOUNT", dataset.getData(0).getString("ACCOUNT"));
        	data.put("USER_TYPE", "01");
        	data.put("SERIAL_NUMBER", serialNumber);
        	data.put("USER_ID", userId);
        	
        	BindMobilePhoneBean bean = new BindMobilePhoneBean();
        	
        	bean.unBindMobilePhoneInform(data);
		}
    }

}
