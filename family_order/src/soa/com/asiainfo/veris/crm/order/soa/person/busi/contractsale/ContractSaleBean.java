
package com.asiainfo.veris.crm.order.soa.person.busi.contractsale;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;

public class ContractSaleBean extends CSBizBean
{

    public IData checkResInfo(IData input) throws Exception
    {
        String resCode = input.getString("RES_CODE", "");
        String staffId = input.getString("STAFF_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        IDataset terminalDataset = HwTerminalCall.getTerminalInfoByTerminalId(resCode, staffId, serialNumber);
        IData terminalData = terminalDataset.getData(0);
        if (!"0".equals(terminalData.getString("X_RESULTCODE")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_25, terminalData.getString("X_RESULTINFO"));
        }
        return terminalData;
    }
}
