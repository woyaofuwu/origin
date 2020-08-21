
package com.asiainfo.veris.crm.order.soa.person.busi.gprsdiscntchange;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GPRSDiscntChangeSVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(GPRSDiscntChangeSVC.class);

    private static final long serialVersionUID = 1L;

    public void checkUserGPRSInfo(IData userInfo) throws Exception
    {
        GPRSDiscntChangeBean gprsDiscntBean = (GPRSDiscntChangeBean) BeanManager.createBean(GPRSDiscntChangeBean.class);
        gprsDiscntBean.checkUserGPRSInfo(userInfo);
    }

    public IDataset qryAllGPRSDiscntInfos(IData userInfo) throws Exception
    {
        GPRSDiscntChangeBean gprsDiscntBean = (GPRSDiscntChangeBean) BeanManager.createBean(GPRSDiscntChangeBean.class);
        return gprsDiscntBean.getAllGPRSDiscnts(userInfo);
    }

    public IDataset qryUserGPRSDiscntInfos(IData userInfo) throws Exception
    {
        GPRSDiscntChangeBean gprsDiscntBean = (GPRSDiscntChangeBean) BeanManager.createBean(GPRSDiscntChangeBean.class);
        return gprsDiscntBean.getUserGPRSDiscnts(userInfo);
    }

}
