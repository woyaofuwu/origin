
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.discntmodifyhisinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryDiscntModifyHisInfoQry;

public class QueryDiscntModifyHisInfoBean extends CSBizBean
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
     * 功能：惠变更历史查询 作者：GongGuang
     */
    public IDataset queryDiscntModifyHisInfo(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String startStaffId = data.getString("START_STAFF_ID", "");
        String endStaffId = data.getString("END_STAFF_ID", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        String discntCode = data.getString("DISCNT_CODE", "");
        String tradeCityCode = data.getString("TRADE_CITY_CODE", "");
        IDataset dataSet = QueryDiscntModifyHisInfoQry.queryDiscntModifyHisInfo(startStaffId, endStaffId, startDate, endDate, discntCode, tradeCityCode, page);
        if(IDataUtil.isNotEmpty(dataSet))
        {
        	for(int i = 0;i<dataSet.size();i++)
        	{
        		String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(dataSet.getData(i).getString("DISCNT_CODE"));
        		dataSet.getData(i).put("DISCNT_NAME", discntName);
        	}
        }
        return dataSet;
    }
}
