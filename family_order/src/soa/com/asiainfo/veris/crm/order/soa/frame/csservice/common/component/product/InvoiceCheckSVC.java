
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class InvoiceCheckSVC extends CSBizService
{

    /**
     * 发票号码校验
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkInvoice(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        IData data = new DataMap();
        String invoiceCode = input.getString("INVOICE_CODE");
        IDataset result = UserOtherInfoQry.getInvoiceInfo(invoiceCode, "FG");
        data.put("INVOICE_CODE", invoiceCode);
        if (IDataUtil.isNotEmpty(result))
        {
            data.put("RESULT_INFO", "已经存在，请重新输入发票号码!");
            data.put("RESULT_CODE", "1");
        }
        else
            data.put("RESULT_CODE", "0");
        returnSet.add(data);
        return returnSet;
    }

}
