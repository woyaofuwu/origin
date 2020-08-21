package com.asiainfo.veris.crm.order.soa.group.stopgroupbroadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.changeuserelement.ChangeGrpWideNetSvcElement;

public class ChangeGrpWideNetStateSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 创建集团宽带信控停开机订单
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtTrade(IData inParam) throws Exception
    {
        ChangeGrpWideNetSvcElement bean = new ChangeGrpWideNetSvcElement();

        return bean.crtTrade(inParam);
    }
    
}
