
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PersonPWChangeSVC extends CSBizService
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 用户密码重置或变更接口
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData userPwdModOrRst(IData inParam) throws Exception
    {
    	inParam=new DataMap(inParam.toString());
    	PersonPWChangeBean pcbean = BeanManager.createBean(PersonPWChangeBean.class);
        return pcbean.userPwdModOrRst(inParam);
    }
 }
