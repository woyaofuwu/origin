
package com.asiainfo.veris.crm.order.soa.person.busi.handgatheringfee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class HandGatheringFeeSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 登记手工收款补录
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset handGatheringFee(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        HandGatheringFeeBean bean = BeanManager.createBean(HandGatheringFeeBean.class);
        returnSet.add(bean.handGatheringFee(input));
        return returnSet;
    }

    /**
     * 打印手工收款补录
     * 
     * @param data
     * @throws Exception
     */
    public IDataset printHandGathering(IData input) throws Exception
    {
        HandGatheringFeeBean bean = BeanManager.createBean(HandGatheringFeeBean.class);
        return bean.printHandGathering(input);
    }

}
