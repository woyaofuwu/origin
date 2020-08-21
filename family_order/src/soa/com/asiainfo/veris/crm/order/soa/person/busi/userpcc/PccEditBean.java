
package com.asiainfo.veris.crm.order.soa.person.busi.userpcc;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPccInfoQry;

public class PccEditBean extends CSBizBean
{
    private static Logger logger = Logger.getLogger(PccEditBean.class);

    public IDataset getPccUserInfByUserId(IData param) throws Exception
    {

        return UserPccInfoQry.getPccUserInfByUserId(param.getString("USER_ID"), null);
    }

}
