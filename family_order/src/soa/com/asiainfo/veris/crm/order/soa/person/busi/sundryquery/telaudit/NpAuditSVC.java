
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.telaudit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class NpAuditSVC extends CSBizService
{

    public IData getNpAuditInfo(IData param) throws Exception
    {
        NpAuditBean bean = (NpAuditBean) BeanManager.createBean(NpAuditBean.class);
        return bean.getNpAuditInfo(param);
    }

    public IDataset getNpAuditInfos(IData param) throws Exception
    {
        NpAuditBean bean = (NpAuditBean) BeanManager.createBean(NpAuditBean.class);
        return bean.getNpAuditInfos(param, getPagination());
    }

}
