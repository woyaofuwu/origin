
package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CheckPsptVipPwdSVC extends CSBizService
{
    private static Logger logger = Logger.getLogger(CheckPsptVipPwdSVC.class);

    private static final long serialVersionUID = 1L;

    public IData CheckPsptVipPwd(IData input) throws Exception
    {
        CheckPsptVipPwdBean bean = (CheckPsptVipPwdBean) BeanManager.createBean(CheckPsptVipPwdBean.class);

        IData ret = bean.CheckPsptVipsPwd(input);
        return ret;
    }

}
