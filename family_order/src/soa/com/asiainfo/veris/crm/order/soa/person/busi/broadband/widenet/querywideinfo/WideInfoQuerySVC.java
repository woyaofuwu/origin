package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.querywideinfo;

import org.apache.log4j.Logger;

import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;

public class WideInfoQuerySVC extends CSBizService {
	private static final long serialVersionUID = 1L;
    private static Logger log=Logger.getLogger(WideInfoQuerySVC.class);
	
	/**
	 * SS.WideInfoQuerySVC.qryAlreadyDealtWideInfo 
	 * 宽带已办理信息查询
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData qryAlreadyDealtWideInfo(IData input) throws Exception {
		WideInfoQueryBean wideInfoQueryBean = BeanManager.createBean(WideInfoQueryBean.class);
        IData data = wideInfoQueryBean.qryAlreadyDealtWideInfo(input);
        return data;
	}
	
}
