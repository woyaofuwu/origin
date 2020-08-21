package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class QueryHSSUserStateBean extends CSBizBean{
	
	/**
	 * 调用ibossCall 查询信息
	 * @return
	 */
	public IDataset iBossQuery(IData data)throws Exception{
	    System.out.println("QueryHSSUserStateBeanxxxxxxxxxxxxxxxxx15 "+data);
        System.out.println("QueryHSSUserStateBeanxxxxxxxxxxxxxxxxxaaaaaaaaaaaa16 "+data);

		String kindId=data.getString("KIND_ID");
		IData param=data.getData("CONDITION_PARAM");
		param.put("KIND_ID", kindId);
		System.out.println("QueryHSSUserStateBeanxxxxxxxxxxxxxxxxx18 "+kindId);
		System.out.println("QueryHSSUserStateBeanxxxxxxxxxxxxxxxxx19 "+param);
        IDataset ds = IBossCall.dealInvokeUrl(kindId, "IBOSS6", param);
        System.out.println("QueryHSSUserStateBeanxxxxxxxxxxxxxxxxx21 "+ds);

		return ds;
		
	}
	
}
