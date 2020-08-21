
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ChangePhonePreRegisterSVC extends CSBizService
{

    public IDataset checkSerialOldNew(IData input) throws Exception
    {

        ChangePhonePreRegisterBean bean = (ChangePhonePreRegisterBean) BeanManager.createBean(ChangePhonePreRegisterBean.class);

        IData data = bean.checkSerialOldNew(input);

        IDataset result = new DatasetList();
        result.add(data);
        return result;

    }
    
    public void SynPicId(IData input) throws Exception
    {
        ChangePhonePreRegisterBean bean = (ChangePhonePreRegisterBean) BeanManager.createBean(ChangePhonePreRegisterBean.class);
        bean.SynPicId(input);
    }
    

}
