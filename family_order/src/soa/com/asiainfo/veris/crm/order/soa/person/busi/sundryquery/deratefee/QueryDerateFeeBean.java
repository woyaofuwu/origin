
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.deratefee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryDerateFeeQry;

public class QueryDerateFeeBean extends CSBizBean
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

        return UAreaInfoQry.qryAreaByParentAreaCode("0898");
    }

    /**
     * 功能：减免费用查询 作者：GongGuang
     */
    public IDataset queryDerateFee(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String tradeType = data.getString("TRADE_TYPE", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        String areaCode = data.getString("AREA_CODE", "");
        String startStaffId = data.getString("START_STAFFID", "");
        String endStartId = data.getString("END_STAFFID", "");
        return QueryDerateFeeQry.queryDerateFee1(tradeType, startDate, endDate, areaCode, startStaffId, endStartId, routeEparchyCode, page);
    }

    /**
     * 功能：查询tradeTypeList 作者：GongGuang
     */
    public IDataset queryTradeTypeList(IData data, Pagination page) throws Exception
    {
        String eparchyCode = "0898";
        String subsysCode = "CSM";
        String paramAttr = "6015";
        String paramCode = "";
        if (paramCode == null || paramCode.equals(""))
        {
            return QueryDerateFeeQry.queryTradeTypeListOnlyByAttr(subsysCode, paramAttr, eparchyCode, page);
        }
        else
        {
            return QueryDerateFeeQry.queryTradeTypeListByBothAttrNameCode(subsysCode, paramAttr, paramCode, eparchyCode, page);
        }
    }
}
