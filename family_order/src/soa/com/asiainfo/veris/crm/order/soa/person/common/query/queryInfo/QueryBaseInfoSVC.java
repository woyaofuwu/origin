package com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryBaseInfoSVC extends CSBizService{
	
	private static final long serialVersionUID = 1L;

	public IData queryBaseInfo(IData input) throws Exception
    {
        QueryBaseInfoBean queryBaseInfoBean = BeanManager.createBean(QueryBaseInfoBean.class);
      
        return queryBaseInfoBean.queryBaseInfo(input);
    }
	public IData sendInterRoamMessage(IData input) throws Exception
    {
        QueryBaseInfoBean queryBaseInfoBean = BeanManager.createBean(QueryBaseInfoBean.class);

        return queryBaseInfoBean.sendInterRoamMessage(input);
    }
    
}
