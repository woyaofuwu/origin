
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.returnalert;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class ReturnAlertManageBean extends CSBizBean
{

    public IDataset queryReturnAlert(IData data, Pagination pagination) throws Exception
    {
        String discntCode = data.getString("DISCNT_CODE");
        String rsrvdate1 = data.getString("RSRV_DATE1");

        IDataset alerts = UserOtherInfoQry.getPlatsvcShl(discntCode, rsrvdate1, pagination);
        return alerts;
    }

}
