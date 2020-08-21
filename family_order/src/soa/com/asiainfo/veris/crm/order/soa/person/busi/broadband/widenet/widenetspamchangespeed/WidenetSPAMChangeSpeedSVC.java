
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetspamchangespeed;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WidenetSPAMChangeSpeedSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 300M免费提速包，到期时，宽带速率降回原速率。
     */
    public IData revertFTTHrate(IData input) throws Exception
    {
        WidenetSPAMChangeSpeedBean widenetInterfaceBean = BeanManager.createBean(WidenetSPAMChangeSpeedBean.class);
        IData results = widenetInterfaceBean.revertFTTHrate();
        return results;
    }


}
