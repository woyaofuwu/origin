
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryDiscntModifyHisInfoQry extends CSBizBean
{
    public static IDataset queryDiscntModifyHisInfo(String startStaffId, String endStaffId, String startDate, String endDate, String discntCode, String tradeCityCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("START_STAFF_ID", startStaffId);
        params.put("END_STAFF_ID", endStaffId);
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);
        params.put("DISCNT_CODE", discntCode);
        params.put("TRADE_CITY_CODE", tradeCityCode);
        return Dao.qryByCode("TF_B_TRADE", "SEL_DISCNTHIS_BY_DATE_STAFFID_QRY_NEW", params, pagination, Route.getJourDb(CSBizBean.getTradeEparchyCode()));

    }

}
