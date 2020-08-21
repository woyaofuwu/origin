
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class VatPrintLogInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询集团可打的增值税台账记录及可打金额
     * 
     * @param inputData
     * @return
     * @throws Exception
     */
    public IDataset getTaxPrintInfosForGrp(IData inputData) throws Exception
    {
        String custId = inputData.getString("CUST_ID");
        String startDate = inputData.getString("START_DATE");
        String endDate = inputData.getString("END_DATE");

        return VatPrintLogInfoQry.getTaxPrintInfosForGrp(custId, startDate, endDate);
    }

    /**
     * 根据客户标志和发票号查询增值税发票打印日志[可返销]
     * 
     * @param inputData
     * @return
     * @throws Exception
     */
    public IDataset qryVatPrintLogForGrp(IData inputData) throws Exception
    {
        String ticketStateCode = inputData.getString("TICKET_STATE_CODE");
        String custId = inputData.getString("CUST_ID");
        String noteNo = inputData.getString("NOTE_NO");
        String startDate = inputData.getString("START_DATE");
        String endDate = inputData.getString("END_DATE");

        return VatPrintLogInfoQry.qryVatPrintLogForGrp(ticketStateCode, custId, noteNo, startDate, endDate, getPagination());
    }

}
