
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.chlcomminfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryChlCommInfoQry;

public class QueryChlCommInfoBean extends CSBizBean
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
     * 功能：渠道通讯费补贴查询 作者：GongGuang
     */
    public IDataset queryChlCommInfo(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String cityCode = data.getString("CITY_CODE", "");
        String channelCode = data.getString("CHNL_CODE", "");
        String channelName = data.getString("CHNL_NAME", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        IDataset dataSet = QueryChlCommInfoQry.queryChlCommInfo(cityCode, channelCode, channelName, serialNumber, routeEparchyCode, page);
        return dataSet;
    }
}
