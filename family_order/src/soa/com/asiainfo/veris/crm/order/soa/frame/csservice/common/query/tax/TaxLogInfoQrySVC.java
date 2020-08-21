
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tax;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TaxLogInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset qryTaxLogForPrint(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");
        String startDate = input.getString("START_DATE");
        String endDate = input.getString("END_DATE");

        return TaxLogInfoQry.qryTaxLogForPrint(custId, startDate, endDate, getPagination());
    }
}
