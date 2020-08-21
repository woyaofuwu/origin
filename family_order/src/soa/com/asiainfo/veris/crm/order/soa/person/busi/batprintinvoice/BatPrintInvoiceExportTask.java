
package com.asiainfo.veris.crm.order.soa.person.busi.batprintinvoice;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;

public class BatPrintInvoiceExportTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData input, Pagination pagination) throws Exception
    {
        BatPrintInvoiceBean bean = BeanManager.createBean(BatPrintInvoiceBean.class);
        return bean.queryPrintInfo(input, pagination);
    }

}
