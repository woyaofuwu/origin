
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.baduserinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryBadUserInfoQry;

public class QueryBadUserInfoBean extends CSBizBean
{
    public IDataset queryBadUserInfoByBatchID(IData data, String batchID, Pagination page) throws Exception
    {

        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        IDataset dataSet = QueryBadUserInfoQry.queryBadUserInfoByBatchID(batchID, routeEparchyCode, page);
        return dataSet;
    }

    /**
     * 功能：不良信息号码用户资料查询 作者：GongGuang
     */
    public IDataset queryBadUserInfoBySN(IData data, Pagination page) throws Exception
    {

        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String serialNum = data.getString("SERIAL_NUMBER", "");
        IDataset dataSet = QueryBadUserInfoQry.queryBadUserInfoBySN(serialNum, routeEparchyCode, page);
        return dataSet;
    }
}
