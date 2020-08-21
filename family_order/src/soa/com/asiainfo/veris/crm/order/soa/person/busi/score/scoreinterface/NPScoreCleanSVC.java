
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 积分清零接口服务
 *
 */
public class NPScoreCleanSVC extends CSBizService
{

    public IData getCommInfo(IData input) throws Exception
    {
        NPScoreCleanBean scoreDonateBean = (NPScoreCleanBean) BeanManager.createBean(NPScoreCleanBean.class);
        return scoreDonateBean.getCommInfo(input);
    }

}
