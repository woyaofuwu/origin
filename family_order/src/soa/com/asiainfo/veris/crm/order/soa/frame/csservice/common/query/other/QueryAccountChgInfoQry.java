
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;

/**
 * 功能：执行帐户变更查询 作者：GongGuang
 */
public class QueryAccountChgInfoQry extends CSBizBean
{
    /**
     * 执行帐户变更查询
     * 分库改造sql
     * duhj
     */
    public static IDataset queryAccountChgInfo(String tradeCityCode, String startDate, String endDate, String payModeCode, String startStaffId, String endStaffId, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_CITY_CODE", tradeCityCode);// 业务类型
        params.put("START_DATE", startDate);// 开始时间
        params.put("END_DATE", endDate);// 结束时间
        params.put("PAY_MODE_CODE", payModeCode);// 业务区
        params.put("START_STAFF_ID", startStaffId);// 开始工号
        params.put("END_STAFF_ID", endStaffId);// 结束工号
       
        IDataset result = TradeBhQry.queryAccountChgInfo(tradeCityCode,startDate,endDate,startStaffId,endStaffId,pagination);
        if(IDataUtil.isNotEmpty(result)){
        	for(int i=0;i<result.size();i++){
        		IData res=result.getData(i);
        		String custId=res.getString("CUST_ID");
                IDataset result2 = AcctInfoQry.queryAccountChgInfo(custId,payModeCode);
                if(IDataUtil.isNotEmpty(result)){
                	IData  data=result2.getData(0);
                	res.putAll(data);
                }else{
                	res.remove(i);
                	i--;
                }
        		
        	}
        }
        
        
        
        
        return result;
    }

    /**
     * 功能：查询付费类型
     */
    public static IDataset queryPayModes() throws Exception
    {
        IData params = new DataMap();
        SQLParser parser = new SQLParser(params);
        parser.addSQL("select t.PAY_MODE_CODE, t.PAY_MODE from td_s_paymode t where 1 = 1");
        parser.addSQL(" order by t.PAY_MODE_CODE");
        return Dao.qryByParse(parser);
    }

}
