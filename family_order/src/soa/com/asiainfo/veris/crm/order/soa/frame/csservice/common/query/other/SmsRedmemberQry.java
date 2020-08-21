
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SmsRedmemberQry
{

    public static IDataset checkRedMemberIsExists(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("HN_SMS_REDMEMBER", "SEL_BY_SN1", param);
    }

    /**
     * 查询短信白名单
     * 
     * @param pd
     * @param cond
     * @return
     * @throws Exception
     */
    public static IDataset queryListByCodeCodeParser(IData cond, Pagination pagination) throws Exception
    {

        IDataset set = new DatasetList();

        String routeId = cond.getString(Route.ROUTE_EPARCHY_CODE);

        set = Dao.qryByCodeParser("HN_SMS_REDMEMBER", "SEL_BY_COND", cond, pagination, routeId);

        return set;

    }

    public static IDataset queryRedMenberByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("HN_SMS_REDMEMBER", "SEL_BY_USER_ID", param);
    }
}
