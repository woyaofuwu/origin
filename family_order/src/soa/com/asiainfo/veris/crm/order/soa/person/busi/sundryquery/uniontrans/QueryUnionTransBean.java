
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.uniontrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUnionTransQry;

public class QueryUnionTransBean extends CSBizBean
{
    /**
     * 功能：异网转接查询 作者：GongGuang
     */
    public IDataset queryUnionTrans(IData data, Pagination page) throws Exception
    {

        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String startSerailNumber = data.getString("START_SERIALNUMBER", "");
        String endSerailNumber = data.getString("END_SERIALNUMBER", "");
        IDataset dataSet = QueryUnionTransQry.queryUnionTrans(routeEparchyCode, startSerailNumber, endSerailNumber, page);
        return dataSet;
    }
}
