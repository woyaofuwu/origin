
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.realname;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryChkRealNameQry;

public class QueryChkRealNameBean extends CSBizBean
{

    /**
     * 功能：用户实名制信息查询 作者：GongGuang
     */
    public IDataset getUserRealNameInfo(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        IDataset dataSet = QueryChkRealNameQry.getUserRealNameInfo(serialNumber, page);
        return dataSet;
    }
    
    /**
     * 功能：用户实名制信息查询 作者：GongGuang
     */
    public IDataset getUserRealNameInfoValid(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        IDataset dataSet = QueryChkRealNameQry.getUserRealNameInfoValid(serialNumber, page);
        return dataSet;
    }
    
    /**
     * 功能：用户实名制信息查询 作者：GongGuang
     */
    public IDataset getUserRealNameInfoByUserId(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String userId = data.getString("USER_ID", "");
        IDataset dataSet =QueryChkRealNameQry.getUserRealNameInfoByUserId(userId, page);
        return dataSet;
    }
}
