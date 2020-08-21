package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class UMSpInfoQry {
	
	 /**
	  * 根据spcode 查询 SPname   
	  * @param spCode
	  * @return
	  * @throws Exception
	  */
	    public static String querySpInfoNameByCond(String spCode) throws Exception
	    {
	        IData param = new DataMap();
	        param=UpcCall.querySpInfoNameByCond(spCode).getData(0);
	         return   param.getString("SP_NAME");
	    }

}
