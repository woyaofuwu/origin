
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.owedestroyforegift;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryOweDestroyForegiftQry;

public class QueryOweDestroyForegiftBean extends CSBizBean
{
    /**
     * 查询业务区
     * 
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset queryArea() throws Exception
    {

        return UAreaInfoQry.qryAreaByAreaLevel30();
    }

    /**
     * 功能：欠费拆机用户押金清单查询 作者：GongGuang
     */
    public IDataset queryOweDestroyForegift(IData data) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String areaCode = data.getString("AREA_CODE", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");

        IDataset dataSet = QueryOweDestroyForegiftQry.queryOweDestroyForegift(areaCode, startDate, endDate);
        return dataSet;
    }
}
