
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ChangePhoneCancelSVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 2949880723188691637L;

    public IData changePhoneCancel(IData input) throws Exception
    {
        ChangePhoneCancelBean bean = BeanManager.createBean(ChangePhoneCancelBean.class);
        return bean.changePhoneCancel(input);
    }

    public IData queryChangePhoneInfo(IData input) throws Exception
    {
        ChangePhoneCancelBean bean = BeanManager.createBean(ChangePhoneCancelBean.class);
        return bean.queryChangePhoneInfo(input);
    }

    public void setTrans(IData input) throws Exception
    {
        // 没有传SERIAL_NUMBER，必须进行转换
        if ("SS.ChangePhoneCancelSVC.changePhoneCancel".equals(getVisit().getXTransCode()))
        {
            input.put("SERIAL_NUMBER", input.getString("NEW_SN", ""));
        }
    }
    
    public void cancelTrigger(IData input) throws Exception
    {
        ChangePhoneCancelBean bean = BeanManager.createBean(ChangePhoneCancelBean.class);
        bean.cancelTrigger(input);
    }
}
