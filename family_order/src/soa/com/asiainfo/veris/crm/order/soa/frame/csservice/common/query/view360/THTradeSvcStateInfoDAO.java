
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class THTradeSvcStateInfoDAO
{
    /**
     * 根据TRADE_ID从表tf_b_trade_svc查询服务状态变化信息
     * 
     * @param param
     * @param pagination
     * @return
     */
    public IDataset queryTradeSvcStateInfo(IData param) throws Exception
    {
        String history_query_type = param.getString("HISTORY_QUERY_TYPE", "");

        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        String tableName = "tf_b_trade_svcstate";
        if("G".equals(history_query_type)){
        	String year_id = param.getString("QUERY_YEAR","");
        	if (StringUtils.isBlank(year_id))
            {
                return new DatasetList();
            }
        	tableName = "TF_BHB_TRADE_SVCSTATE_"+year_id;
        }
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select service_id, state_code, start_date, end_date,modify_tag from "+tableName+" where 1=1");
        parser.addSQL(" and trade_id=:TRADE_ID");
        parser.addSQL(" and ACCEPT_MONTH = :ACCEPT_MONTH ");
        if ("G".equals(history_query_type))
        {
            return Dao.qryByParse(parser, Route.CONN_CRM_HIS);
        }
        else
        {
            return Dao.qryByParse(parser, Route.getJourDb(param.getString(Route.ROUTE_EPARCHY_CODE)));
        }
    }
}
