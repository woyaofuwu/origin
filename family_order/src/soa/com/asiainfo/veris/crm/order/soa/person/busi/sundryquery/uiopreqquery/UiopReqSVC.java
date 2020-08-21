package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.uiopreqquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UiopReqSVC extends CSBizService{

	public IDataset queryList(IData inparams) throws Exception
    {
		UiopReqBean bean = BeanManager.createBean(UiopReqBean.class);
		IDataset results = bean.queryList(inparams, getPagination());
        return results;
    }
}
