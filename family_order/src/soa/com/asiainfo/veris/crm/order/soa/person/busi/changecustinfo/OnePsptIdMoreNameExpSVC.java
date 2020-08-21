
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo;  

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

/**
 * 一证多名客户资料变更处理类型
 */
public class OnePsptIdMoreNameExpSVC extends CSBizService
{

	public IDataset dealExpire(IData mainTrade) throws Exception
	{
	    
	    IData data = new DataMap();
	    UcaData uca = null;
	    uca = UcaDataFactory.getNormalUca(mainTrade.getString("SERIAL_NUMBER"));
        data.put("SERIAL_NUMBER",mainTrade.getString("SERIAL_NUMBER"));
        data.put("USER_ID", uca.getUserId());
        data.put("CUST_ID", uca.getCustId());

        
        IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", data);
        
        IData params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
        params.put("IS_REAL_NAME", "0");
        params.put("TRADE_TYPE_CODE", "60");
        params.put("IS_NEED_SMS", false);//不发送短信     
        params.putAll(data);
        params.put("REMARK", "AEE调用一证多名变更非实名");
        params.put("SKIP_RULE", "TRUE");
        
        //客户资料变更无责任人，要去掉
        params.remove("RSRV_STR2");
        params.remove("RSRV_STR3");
        params.remove("RSRV_STR4");
        params.remove("RSRV_STR5");      
        
        IDataset outparams = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);        

 
		return outparams;
	}
	
}
