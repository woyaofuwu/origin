
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.unfinishtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUnfinishTradeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class QueryUnfinishTradeBean extends CSBizBean
{

    /**
     * 功能：未完工工单查询 作者：GongGuang
     */
    public IDataset queryUnfinishTrade(IData data, Pagination page) throws Exception
    {
        String tradeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String startDate = data.getString("START_DATE", "");
        String finishDate = data.getString("FINISH_DATE", "");
        String tradeTypeCode = "0";
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String tradeDepartId = data.getString("TRADE_DEPART_ID", "");
        String tradeStaffId = data.getString("TRADE_STAFF_ID", "");
        IDataset dataSet = QueryUnfinishTradeQry.queryUnfinishTrade(serialNumber, tradeDepartId, tradeStaffId, startDate, finishDate, tradeTypeCode, tradeEparchyCode, page);
        return dataSet;
    }
    
    /**
     * 功能：未完工工单台账轨迹查询 作者：chenwei6
     */
    public IDataset queryUnfinishTradeTrace(IData data) throws Exception
    {
    	String orderId = data.getString("ORDER_ID");
        IDataset dataSet = QueryUnfinishTradeQry.queryUnfinishTradeTrace(orderId);
        return dataSet;
    }
    
    
    /**
     * 功能：未完工工单台账轨迹查询 作者：chenwei6
     */
    public IDataset queryUnfinishPFTrace(IData data) throws Exception
    {
    	
    	/*IDataset result = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "PF_URL","0898");//获取参数表中配置的服开接口地址
    	String url = result.getData(0).getString("PARA_CODE1");
        IDataset dataSet = CSAppCall.call(url,"queryUnfinishTrade", data, true);*/
    	
    	IDataOutput dataOutput = CSAppCall.callNGPf("queryUnfinishTrade",data);
        return dataOutput.getData();
    }
    
}
