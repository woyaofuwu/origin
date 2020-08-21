
package com.asiainfo.veris.crm.order.soa.person.busi.hintinfo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class DiscntManageBean extends CSBizBean
{

    static transient final Logger logger = Logger.getLogger(DiscntManageBean.class);

    public void insertDiscntInfo(IData data) throws Exception
    {
        Dao.insert("TD_B_DISCNT", data, Route.CONN_CRM_CEN);
    }

    public void upDiscntInfo(IData data) throws Exception
    {
        Dao.save("TD_B_DISCNT", data, new String[]
        { "DISCNT_CODE" }, Route.CONN_CRM_CEN);
    }

}
