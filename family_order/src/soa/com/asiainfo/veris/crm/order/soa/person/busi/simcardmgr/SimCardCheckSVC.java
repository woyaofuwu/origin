
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SimCardCheckSVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(SimCardCheckSVC.class);

    /**
     * 校验该卡是否具备换卡条件
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset verifySimCard(IData idata) throws Exception
    {
        SimCardCheckBean cardBean = (SimCardCheckBean) BeanManager.createBean(SimCardCheckBean.class);

        IDataset resultSet = new DatasetList();
        resultSet.add(cardBean.verifySimCard(idata));

        return resultSet;
    }
    
    public IDataset checkEmptyCard(IData idata) throws Exception
    {
        SimCardCheckBean cardBean = (SimCardCheckBean) BeanManager.createBean(SimCardCheckBean.class);
        return cardBean.checkEmptyCard(idata);
    }


}
