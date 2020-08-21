
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserUnionPayInfoQry
{

    public static IDataset getUnionPayUser(IData param) throws Exception
    {

        // TODO code_code表里没有
        return Dao.qryByCode("TF_F_UNIONPAYUSER", "SEL_BY_SN", param, Route.CONN_CRM_CEN);

    }

}
