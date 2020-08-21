
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changepasswd;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

public class WidenetPswChgSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset loadChildInfo(IData input) throws Exception
    {
        return WidenetInfoQry.getUserWidenetInfo(input.getString("USER_ID"));
    }
}
