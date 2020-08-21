
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SimCardCheckSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 白卡校验，
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkEmptyCard(IData input) throws Exception
    {
        SimCardCheckBean bean = BeanManager.createBean(SimCardCheckBean.class);
        IDataset returnSet = new DatasetList();
        IData result = bean.checkEmptyCard(input);
        returnSet.add(result);
        return returnSet;
    }

    /**
     * sim卡校验，
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkSimCard(IData input) throws Exception
    {
        SimCardCheckBean bean = BeanManager.createBean(SimCardCheckBean.class);
        IDataset returnSet = new DatasetList();
        IData result = bean.checkSimCard(input);
        returnSet.add(result);
        return returnSet;
    }

    /**
     *sim 预占
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset preOccupySimCard(IData input) throws Exception
    {
        SimCardCheckBean bean = BeanManager.createBean(SimCardCheckBean.class);
        IDataset returnSet = new DatasetList();
        IData result = bean.preOccupySimCard(input);
        returnSet.add(result);
        return returnSet;
    }

}
