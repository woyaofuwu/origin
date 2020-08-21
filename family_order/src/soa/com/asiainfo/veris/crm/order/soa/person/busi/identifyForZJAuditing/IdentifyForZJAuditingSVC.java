
package com.asiainfo.veris.crm.order.soa.person.busi.identifyForZJAuditing;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IdentifyForZJAuditingSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @Description：浙江稽核中心对登录的统一鉴权认证接口
	 * @param:@param input
	 * @param:@return
	 * @return IData
	 * @throws Exception 
	 * @throws
	 * @Author :MengQX
	 * @date :2018-10-29上午10:28:03
	 */
	public IData identify(IData input) throws Exception  {
		IData data = new DataMap();
		IdentifyForZJAuditingBean bean = BeanManager.createBean(IdentifyForZJAuditingBean.class);
        data = bean.identifyForZJAuditingCenter(input);
        return data;
	}


}
