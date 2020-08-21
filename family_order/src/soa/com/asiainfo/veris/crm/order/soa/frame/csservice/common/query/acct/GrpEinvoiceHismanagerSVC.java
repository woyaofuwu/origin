
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GrpEinvoiceHismanagerSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;
    /**
     * 查询电子发票历史信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryByGroupId(IData input) throws Exception
    {
//    	String custId = input.getString("CUST_ID");
//    	String startDate = input.getString("START_DATE");
//    	String endDate = input.getString("END_DATE");
        return  GrpEinvoiceBean.queryByGroupId(input);
    }
    public IDataset modifyEInvoiceTrade(IData input) throws Exception
    {

        return  GrpEinvoiceBean.modifyEInvoiceTrade(input);
    }
    
}
