
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.accountchginfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryAccountChgInfoQry;

public class QueryAccountChgInfoBean extends CSBizBean
{
    /**
     * 功能：执行帐户变更查询 作者：GongGuang
     */
    public IDataset queryAccountChgInfo(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String tradeCityCode = data.getString("TRADE_CITY_CODE", "");
        String payModeCode = data.getString("PAY_MODE_CODE", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        String startStaffId = data.getString("START_STAFF_ID", "");
        String endStaffId = data.getString("END_STAFF_ID", "");
        IDataset dataSet = QueryAccountChgInfoQry.queryAccountChgInfo(tradeCityCode, startDate, endDate, payModeCode, startStaffId, endStaffId, routeEparchyCode, page);
        return dataSet;
    }

    /**
     * 查询业务区
     * 
     * @return
     * @throws Exception
     */
    public IDataset queryArea() throws Exception
    {

        return UAreaInfoQry.qryAreaByAreaLevel30();
    }

    /**
     * 查询付费模式
     * 
     * @return
     * @throws Exception
     */
    public IDataset queryPayModes() throws Exception
    {

        return QueryAccountChgInfoQry.queryPayModes();
    }
}
