
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class THTradeDiscntInfoDAO
{
    /**
     * 根据TRADE_ID从表tf_b_trade_discnt查询优惠信息
     * 
     * @param param
     * @param pagination
     * @return
     */
    public IDataset queryTradeDiscntInfo(IData param) throws Exception
    {
        String tradeId = param.getString("TRADE_ID", "");
        if (StringUtils.isBlank(tradeId))
        {
            return new DatasetList();
        }

        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select discnt_code, decode(spec_tag, '0','正常产品优惠','1','特殊优惠','2','关联优惠','') spec_tag, campn_id, start_date, end_date, modify_tag from tf_b_trade_discnt where 1=1 ");
        parser.addSQL(" and trade_id=:TRADE_ID");
        parser.addSQL(" and ACCEPT_MONTH = :ACCEPT_MONTH ");
        return Dao.qryByParse(parser, Route.getJourDb());
    }

    /**
     * 根据TRADE_ID从表tf_b_trade_discnt查询优惠信息 Cg
     * 
     * @param param
     * @param pagination
     * @return
     */
    public IDataset queryTradeDiscntInfoCg(IData param) throws Exception
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
        String tableName = "TF_BHB_TRADE_DISCNT_"+year_id;
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select discnt_code, decode(spec_tag, '0','正常产品优惠','1','特殊优惠','2','关联优惠','') spec_tag, campn_id, start_date, end_date, modify_tag from "+tableName+" where 1=1 ");
        parser.addSQL(" and trade_id=:TRADE_ID");
        return Dao.qryByParse(parser, Route.CONN_CRM_HIS);
    }
}
