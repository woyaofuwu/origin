
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/*
 * * 获取tl_f_cust_person表中客户信息
 */
public class THTradeScoreInfoDAO
{
    /**
     * 根据TRADE_ID从表f_b_trade_score查询此条台帐数据的积分交易明细
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     */
    public IDataset queryTradeScoreInfo(IData param) throws Exception
    {
        String history_query_type = param.getString("HISTORY_QUERY_TYPE", "");
        String trade_id = param.getString("TRADE_ID", "");
        String acceptMonth = StrUtil.getAcceptMonthById(trade_id);
        param.put("ACCEPT_MONTH", acceptMonth);

        if ("".equals(trade_id))
        {
            return new DatasetList();
        }   
        String tableName =  "tf_b_trade_score";
        if("G".equals(history_query_type)){
        	String year_id = param.getString("QUERY_YEAR","");
            if(StringUtils.isBlank(year_id)){
            	return new DatasetList();
            }
        	tableName ="TF_BHB_TRADE_SCORE_"+year_id;
        }
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" select decode(id_type,'0','用户积分','1','用户短信积分','2','VIP积分','') id_type,");
        parser.addSQL(" decode(score_type_code,'01','本年身份积分','02','本年年度积分','03','本年累计积分','') score_type_code,");
        parser.addSQL(" accept_month, ");
        parser.addSQL(" score, score_changed, value_changed, update_staff_id, update_time from "+tableName+" where 1=1");
        parser.addSQL(" and trade_id=:TRADE_ID ");
        parser.addSQL(" and accept_month =:ACCEPT_MONTH ");

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
