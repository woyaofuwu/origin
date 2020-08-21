
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userinfo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ChoosePhoneSVC extends CSBizService
{
    static transient final Logger logger = Logger.getLogger(ChoosePhoneSVC.class);

    private static final long serialVersionUID = 1L;

    public IDataset getChoosePhoneInfo(IData input) throws Exception
    {
        ChoosePhoneBean result = (ChoosePhoneBean) BeanManager.createBean(ChoosePhoneBean.class);

        IDataset infos = result.getChoosePhoneInfo(input, getPagination());

        return infos;
    }

}
