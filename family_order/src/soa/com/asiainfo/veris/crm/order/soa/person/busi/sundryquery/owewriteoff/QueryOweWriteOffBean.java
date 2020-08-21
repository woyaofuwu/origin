
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.owewriteoff;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryOweWriteOffQry;

public class QueryOweWriteOffBean extends CSBizBean
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
     * 功能：移动电话欠费销号清单查询 作者：GongGuang
     */
    public IDataset queryOweWriteOff(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String areaCode = data.getString("AREA_CODE", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        String startSerialNumber = data.getString("START_SERIALNUMBER", "");
        String endSerialNumber = data.getString("END_SERIALNUMBER", "");
        IDataset dataSet = QueryOweWriteOffQry.queryOweWriteOff(areaCode, startDate, endDate, startSerialNumber, endSerialNumber, page);
        return dataSet;
    }
}
