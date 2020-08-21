
package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IdentCodeAuthSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 身份凭证鉴权
     * 
     * @param pd
     * @param inparams
     *            必备参数SERIAL_NUMBER,IDENT_CODE, 可选参数:BUSINESS_CODE
     * @return X_RESULTCODE,X_RESULTINFO
     * @throws Exception
     */
    public IData identAuthUacp(IData input) throws Exception
    {
        IdentCodeAuthBean userIdentifyBean = BeanManager.createBean(IdentCodeAuthBean.class);
        return userIdentifyBean.identAuthUacp(input);
    }

    @Override
    public final void setTrans(IData input)
    {
        if ("SS.IdentCodeAuthSVC.identAuthUacp".equals(getVisit().getXTransCode()))
        {
            input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
        }
    }
}
