
package com.asiainfo.veris.crm.order.soa.group.esop.transfer;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryStaffTransferSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset qryTransferInfosRecords(IData param) throws Exception
    {

        IDataset staffTransferInfoList = QueryStaffTransferBean.qryTransferInfosRecords(param, getPagination());

        return staffTransferInfoList;
    }
}
