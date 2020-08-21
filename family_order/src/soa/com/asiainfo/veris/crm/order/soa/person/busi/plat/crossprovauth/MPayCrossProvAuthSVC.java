
package com.asiainfo.veris.crm.order.soa.person.busi.plat.crossprovauth;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class MPayCrossProvAuthSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IData mpayCrossProvAuth(IData param) throws Exception
    {
        MPayCrossProvAuthBean bean = (MPayCrossProvAuthBean) BeanManager.createBean(MPayCrossProvAuthBean.class);
        return bean.MPayCrossProvAuth(param);
    }

}
