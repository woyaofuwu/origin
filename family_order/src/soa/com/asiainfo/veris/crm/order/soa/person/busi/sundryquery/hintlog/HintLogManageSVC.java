
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.hintlog;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class HintLogManageSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset queryHintLog(IData input) throws Exception
    {
        HintLogManageBean hintLog = (HintLogManageBean) BeanManager.createBean(HintLogManageBean.class);

        IDataset infos = hintLog.queryHintLog(input, getPagination());

        return infos;
    }

}
