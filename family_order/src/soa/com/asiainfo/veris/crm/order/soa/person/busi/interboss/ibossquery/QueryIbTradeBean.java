
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.ibossquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.biz.util.TimeUtil ;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interboss.IbTradeQry;

public class QueryIbTradeBean extends CSBizBean
{
	public IDataset qryIbTradeBySNAndReqTime(IData data, Pagination page) throws Exception
    {
        String iditemrange = data.getString("IDITEMRANGE");
        String reqtime = data.getString("REQTIME");
        String activitycode = data.getString("ACTIVITYCODE");
    	String bipcode = data.getString("BIPCODE");
        String sysdate = TimeUtil.getSysDate();
        boolean istoday = TimeUtil.compareTo(sysdate, reqtime)==0?true:false;
        IDataset result = new DatasetList();
        result = IbTradeQry.qryIbTradeBySNAndReqTime(iditemrange, reqtime, page, activitycode, bipcode, istoday);

        return result;
    }
	
	public IDataset qryTradeTypeList(IData data,Pagination page) throws Exception
	{
		String Rsrv_str6 = "1";
		return IbTradeQry.qryTradeTypeList(Rsrv_str6,page);
	}
	
	public IDataset qryTradelogbyTRANSIDO(IData data,Pagination page) throws Exception
	{
		String transido = data.getString("TRANSIDO");
		return IbTradeQry.qryTradelogbyTRANSIDO(transido,page);
	}
	
}
