
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.mvalue;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class ExportQueryUserMvalue extends CSExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData param, Pagination paramPagination) throws Exception
    {
        super.transImpexpData(param);
        QueryUserMvalueBean bean = (QueryUserMvalueBean) BeanManager.createBean(QueryUserMvalueBean.class);
        return bean.queryMvalue(param, param.getString("TRADE_EPARCHY_CODE"));
    }

}
