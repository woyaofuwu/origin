package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DelayUserIDentSVC extends CSBizService {
	
	/**
	 * 客户身份凭证延时接口
	 * @param OPR_NUMB(操作流水)，CONTACT_ID(全网客户接触ID)，MSISDN，IDENT_CODE(客户身份凭证)
	 * @return IDENT_UNEFFT(失效时间)
	 * @author yf
	 * @throws Exception 
	 */
	public IData cancelUserIdentUnEffT(IData input) throws Exception{
		IData result = new DataMap();
		DelayUserIDentBean bean = (DelayUserIDentBean) BeanManager.createBean(DelayUserIDentBean.class);
		result = bean.cancelUserIdentUnEffT(input);
		return result;
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
