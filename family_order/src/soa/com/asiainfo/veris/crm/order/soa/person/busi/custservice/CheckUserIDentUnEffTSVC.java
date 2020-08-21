package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CheckUserIDentUnEffTSVC extends CSBizService {
	
	/**
	 * 客户身份凭证有效期查询
	 * @param OPR_NUMB(操作的流水号),CONTACT_ID(全网客户接触ID),MSISDN(客户手机号码),IDENT_CODE(客户身份凭证)
	 * @return IDENT_UNEFFT(失效时间)
	 * @throws Exception
	 */
	public IData queryUserIdentUnEffT(IData input) throws Exception{
		IData data = new DataMap();
		CheckUserIDentUnEffTBean bean = (CheckUserIDentUnEffTBean) BeanManager.createBean(CheckUserIDentUnEffTBean.class);
		data = bean.queryUserIdentUnEffT(input);
		return data;
	}
	
	
	public final void setTrans(IData input){
		if ("6".equals(this.getVisit().getInModeCode())){  //渠道 ：热线
			if(!"".equals(input.getString("MSISDN", ""))){
	    		String serial_number =  input.getString("MSISDN", "");
	    		input.put("SERIAL_NUMBER",serial_number);
	    	}
		}
	}

}
