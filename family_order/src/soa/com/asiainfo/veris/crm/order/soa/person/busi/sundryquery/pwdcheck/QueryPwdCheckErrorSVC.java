
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.pwdcheck;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class QueryPwdCheckErrorSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset queryPwdCheckErrInfos(IData inparams) throws Exception
    {
        String serialNumber = inparams.getString("SERIAL_NUMBER");
        String startDate = inparams.getString("START_DATE");
        String endDate = inparams.getString("END_DATE");
        return UserOtherInfoQry.queryPwdCheckErrInfos(serialNumber, startDate, endDate, "MMEE", getPagination());
    }

}
