
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class MonitorInfoManageSVC extends CSBizService
{

    /**
     * 骚扰电话人工审核停机 改造原过程p_csm_auto_phonestop
     * 
     * @Function: stopMobile
     * @Description: TODO
     * @date May 23, 2014 7:32:24 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset stopMobile(IData data) throws Exception
    {
        MonitorInfoManageBean bean = BeanManager.createBean(MonitorInfoManageBean.class);
        return bean.stopMobile(data);
    }

    public IDataset stopSms(IData data) throws Exception
    {
        MonitorInfoManageBean bean = BeanManager.createBean(MonitorInfoManageBean.class);
        return bean.stopSms(data);
    }
}
