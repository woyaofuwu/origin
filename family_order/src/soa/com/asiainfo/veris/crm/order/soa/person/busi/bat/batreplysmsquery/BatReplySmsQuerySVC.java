package com.asiainfo.veris.crm.order.soa.person.busi.bat.batreplysmsquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.unfinishtrade.QueryUnfinishTradeBean;

public class BatReplySmsQuerySVC extends CSBizService{

	  public IDataset batReplySmsQuery(IData data) throws Exception
	    {
		  BatReplySmsQueryBean bean = (BatReplySmsQueryBean) BeanManager.createBean(BatReplySmsQueryBean.class);
	        return bean.batReplySmsQuery(data, getPagination());
	    }
	
}
