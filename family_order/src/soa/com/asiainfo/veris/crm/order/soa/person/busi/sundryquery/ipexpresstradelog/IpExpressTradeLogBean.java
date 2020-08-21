
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.ipexpresstradelog;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.IpExpressTradeLogQry;

/**
 * IP直通车工作记录查询
 * 
 * @author steven
 */
public class IpExpressTradeLogBean extends CSBizBean
{

    /**
     * '1','按固定号码查询'
     * 
     * @param data
     * @param page
     * @return
     * @throws Exception
     */
    public IDataset queryIPExpressLogByFixedNo(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String fixedNo = data.getString("PARA_CODE1", ""); // fixed number
        IDataset dataSet = IpExpressTradeLogQry.queryIPExpressLogByFixedNo(fixedNo, routeEparchyCode, page);
        return dataSet;
    }

    /**
     * '0','按手机号码查询
     * 
     * @param data
     * @param page
     * @return
     * @throws Exception
     */
    public IDataset queryIPExpressLogByMobileNo(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String mobileNumber = data.getString("PARA_CODE1", "");
        IDataset dataSet = IpExpressTradeLogQry.queryIPExpressLogByMobileNo(mobileNumber, routeEparchyCode, page);
        return dataSet;
    }

    /**
     * '2','按员工工号查询'
     * 
     * @param data
     * @param page
     * @return
     * @throws Exception
     */
    public IDataset queryIPExpressLogByStaffId(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String startDate = data.getString("PARA_CODE2", "");
        String endDate = data.getString("PARA_CODE3", "");
        String staffId = data.getString("PARA_CODE1", "");
        IDataset dataSet = IpExpressTradeLogQry.queryIPExpressLogByStaffId(staffId, startDate, endDate, routeEparchyCode, page);
        return dataSet;
    }
}
