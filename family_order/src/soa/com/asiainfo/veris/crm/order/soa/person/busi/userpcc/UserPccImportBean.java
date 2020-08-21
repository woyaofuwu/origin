
package com.asiainfo.veris.crm.order.soa.person.busi.userpcc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserPccImportBean extends CSBizBean
{

    public void batUserPccInfo(IData data) throws Exception
    {
        Dao.insert("TF_F_USER_PCC", data, Route.CONN_CRM_CEN);
    }

    public void batUserPccInfo(IDataset data) throws Exception
    {
        Dao.insert("TF_F_USER_PCC", data, Route.CONN_CRM_CEN);
    }

}
