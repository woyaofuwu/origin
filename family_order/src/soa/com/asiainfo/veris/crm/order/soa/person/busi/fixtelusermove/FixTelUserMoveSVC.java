
package com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class FixTelUserMoveSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 证件号码校验
     * 
     * @author dengyong3
     * @param input
     * @throws Exception
     */
    public IDataset loadTradeInfo(IData input) throws Exception
    {
        FixTelUserMoveBean fixTelUserMoveBean = BeanManager.createBean(FixTelUserMoveBean.class);
        IData returnData = fixTelUserMoveBean.loadTradeInfo(input);
        IDataset dataset = new DatasetList();
        dataset.add(returnData);
        return dataset;
    }
}
