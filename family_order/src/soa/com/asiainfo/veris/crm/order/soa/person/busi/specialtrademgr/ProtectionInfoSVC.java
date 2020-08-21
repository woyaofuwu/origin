
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class ProtectionInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getForbidenInfo(IData input) throws Exception
    {

        IDataset rs = UserOtherInfoQry.getForbidenInfo(input.getString("USER_ID"), "2", "0");
        return rs;
    }

    public IDataset getUserAttrBySvcId(IData input) throws Exception
    {

        IDataset rs = UserAttrInfoQry.getuserAttrBySvcId(input.getString("USER_ID"), "3313");
        return rs;
    }

}
