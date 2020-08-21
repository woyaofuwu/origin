
package com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ManageForbiddenPointSVC extends CSBizService
{

    public IDataset addForbiddenData(IData data) throws Exception
    {
        ManageForbiddenPointBean bean = new ManageForbiddenPointBean();
        return bean.addForbiddenData(data);
    }

    public IDataset disableData(IData data) throws Exception
    {
        ManageForbiddenPointBean bean = new ManageForbiddenPointBean();
        return bean.disableData(data);
    }

    public IDataset queryForbiddenList(IData data) throws Exception
    {
        ManageForbiddenPointBean bean = new ManageForbiddenPointBean();
        return bean.queryForbiddenList(data, this.getPagination());
    }
}
