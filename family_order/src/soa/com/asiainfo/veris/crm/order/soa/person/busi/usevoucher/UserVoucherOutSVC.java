
package com.asiainfo.veris.crm.order.soa.person.busi.usevoucher;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserVoucherOutSVC extends CSBizService
{

    /**
     * 用户鉴权时间的延长处理
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData userVoucherHanghalf(IData data) throws Exception
    {
        UserVoucherOutBean bean = BeanManager.createBean(UserVoucherOutBean.class);

        return bean.userVoucherHanghalf(data);
    }
}
