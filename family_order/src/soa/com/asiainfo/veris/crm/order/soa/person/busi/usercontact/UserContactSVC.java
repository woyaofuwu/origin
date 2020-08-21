
package com.asiainfo.veris.crm.order.soa.person.busi.usercontact;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserContactSVC extends CSBizService
{

    /**
     * 用户接触开始、结束接口
     * 
     * @param pd
     * @param data
     *            必备参数SERIAL_NUMBER,REMOVE_TAG,USER_PASSWD,NET_TYPE_CODE,EPARCHY_CODE--归属地
     * @return X_CHECK_INFO,X_RESULTCODE,X_RESULTINFO
     * @throws Exception
     * @author lion_cn
     */
    public IData userContact(IData data) throws Exception
    {
        UserContactBean bean = BeanManager.createBean(UserContactBean.class);
        IData result = bean.userContactBean(data);
        return result;
    }
}
