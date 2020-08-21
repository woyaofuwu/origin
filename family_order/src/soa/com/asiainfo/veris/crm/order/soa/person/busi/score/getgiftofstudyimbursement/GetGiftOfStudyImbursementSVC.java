
package com.asiainfo.veris.crm.order.soa.person.busi.score.getgiftofstudyimbursement;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GetGiftOfStudyImbursementSVC extends CSBizService
{
    public IData getCommInfo(IData input) throws Exception
    {
        GetGiftOfStudyImbursementBean GetGiftOfStudyImbursementBean = (GetGiftOfStudyImbursementBean) BeanManager.createBean(GetGiftOfStudyImbursementBean.class);
        return GetGiftOfStudyImbursementBean.getCommInfo(input);
    }
}
