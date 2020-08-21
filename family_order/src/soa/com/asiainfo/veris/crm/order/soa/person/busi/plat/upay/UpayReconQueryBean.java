
package com.asiainfo.veris.crm.order.soa.person.busi.plat.upay;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.UpayReconQry;

public class UpayReconQueryBean extends CSBizBean
{
	public static IDataset queryUpayReconTotal(IData param, Pagination page) throws Exception
    {
        String startDate = param.getString("START_DATE");
        String endDate = param.getString("END_DATE");
        
        return UpayReconQry.qryUpayReconTotal(startDate, endDate, page);        
    }   
	
    public static IDataset queryUpayRecon(IData param, Pagination page) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String deaTag = param.getString("DEAL_TAG");
        String startDate = param.getString("START_DATE");
        String endDate = param.getString("END_DATE");
        String cutoffDay = param.getString("CUTOFF_DAY");
        
        return UpayReconQry.qryUpayReconList(serialNumber, deaTag, startDate, endDate, cutoffDay, page);        
    }  
    
    public static int updateUpayRecon(IData param) throws Exception
    {
        String dealTag = param.getString("DEAL_TAG");
        String rsrvStr1 = param.getString("RSRV_STR1");
        String rsrvStr2 = param.getString("RSRV_STR2");
        String tradeId = param.getString("TRADE_ID");
        
        return UpayReconQry.updateUpayRecon(dealTag, rsrvStr1, rsrvStr2, tradeId);        
    } 
}
