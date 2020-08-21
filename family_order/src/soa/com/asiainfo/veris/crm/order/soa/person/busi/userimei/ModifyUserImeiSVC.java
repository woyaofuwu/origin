/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.userimei;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @CREATED by gongp@2014-7-30 修改历史 Revision 2014-7-30 下午02:40:01
 */
public class ModifyUserImeiSVC extends CSBizService
{
    private static final long serialVersionUID = -3640271969981236529L;

    public IData modifyUserIMEI(IData data) throws Exception
    {
        ModifyUserImeiBean bean = (ModifyUserImeiBean) BeanManager.createBean(ModifyUserImeiBean.class);
        return bean.modifyUserIMEI(data);
    }

}
