
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.simcardmissbill;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QuerySimcardMissbillQry;

public class QuerySimcardMissbillBean extends CSBizBean
{
    /**
     * 功能：SIM卡漏账查询 作者：GongGuang
     */
    public IDataset querySimcardMissbill(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String cityCode = data.getString("CITY_CODE", "");
        String acceptDate = data.getString("ACCEPT_DATE", "").replace("-", "");
        IDataset dataSet = QuerySimcardMissbillQry.querySimcardMissbill(routeEparchyCode, acceptDate, cityCode, page);
        return dataSet;
    }

    public IDataset querySimcardType(IData data) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        IDataset dataSet = QuerySimcardMissbillQry.querySimcardType(routeEparchyCode);
        return dataSet;
    }
}
