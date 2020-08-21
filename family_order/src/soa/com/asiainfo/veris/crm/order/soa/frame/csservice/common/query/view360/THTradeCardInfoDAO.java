
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class THTradeCardInfoDAO
{
    /**
     * 国际漫游一卡多号查询(仅个人客户有)
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryTradeCardInfo(IData param) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" Select rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_str6, rsrv_str8, rsrv_str9 ");
        parser.addSQL(" From tf_b_trade_other Where 1=1 ");
        parser.addSQL(" And trade_id=:TRADE_ID ");
        parser.addSQL(" And ACCEPT_MONTH = :ACCEPT_MONTH ");
        return Dao.qryByParse(parser, Route.getJourDb());
    }

    /**
     * 国际漫游一卡多号查询(仅个人客户有)Cg
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryTradeCardInfoCg(IData param) throws Exception
    {
        String history_query_type = param.getString("HISTORY_QUERY_TYPE", "");
        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        String yearId = param.getString("QUERY_YEAR", "");
        if(StringUtils.isBlank(yearId)){
        	return new DatasetList();
        }
        String tableName = "TF_BHB_TRADE_OTHER_"+yearId;
        if(!"G".equals(history_query_type)){
        	tableName = "tf_b_trade_other";
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" Select rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_str6, rsrv_str8, rsrv_str9 ");
        parser.addSQL(" From "+tableName+" Where 1=1 ");
        parser.addSQL(" And trade_id=:TRADE_ID ");
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
