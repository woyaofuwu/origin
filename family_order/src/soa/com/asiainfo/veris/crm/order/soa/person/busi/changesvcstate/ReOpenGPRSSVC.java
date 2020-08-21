
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ReOpenGPRSSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 检查是否可以办理GPRS开启业务
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void checkWhetherCanDoIt(IData userinfo) throws Exception
    {
        ReOpenGPRSBean bean = (ReOpenGPRSBean) BeanManager.createBean(ReOpenGPRSBean.class);
        bean.childWhetherItIsValid(userinfo);
    }

}
