package com.asiainfo.veris.crm.order.soa.person.busi.choosenetwork;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ResChooseNetworkSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 国漫优选反馈结果
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getCommonParam(IData input) throws Exception
    {
    	ResChooseNetworkBean bean = (ResChooseNetworkBean)BeanManager.createBean(ResChooseNetworkBean.class);
        IDataset dataset = bean.getChooseNetworkInfo(input);
        return dataset;
    }
}