
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeAccountDAO
{
    /**
     * 根据条件查询账户信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getAccountInfos(IData param, Pagination pagination) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TF_B_TRADE_ACCOUNT ");
        parser.addSQL(" where TRADE_ID = :TRADE_ID");
        parser.addSQL(" and ACCEPT_MONTH = :ACCEPT_MONTH");
        // parser.addSQL(" and CUST_ID = :CUST_ID");
        return Dao.qryByParse(parser, pagination, Route.getJourDb());

    }

    /**
     * 根据条件查询账户信息 Cg库
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getAccountInfosByCg(IData param, Pagination pagination) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        String year_id = param.getString("QUERY_YEAR","");
        if(StringUtils.isBlank(year_id)){
        	return new DatasetList();
        }
        String tableName = "TF_BHB_TRADE_ACCOUNT_"+year_id;
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from "+tableName+" ");
        parser.addSQL(" where TRADE_ID = :TRADE_ID");
        // parser.addSQL(" and CUST_ID = :CUST_ID");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_HIS);

    }
}
