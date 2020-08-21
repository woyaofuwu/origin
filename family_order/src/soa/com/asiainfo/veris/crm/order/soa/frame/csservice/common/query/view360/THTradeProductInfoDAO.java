
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class THTradeProductInfoDAO
{
    /**
     * 根据TRADE_ID从表tf_b_trade_product查询产品变化信息
     * 
     * @param param
     * @param pagination
     * @return
     */
    public IDataset queryTradeProductInfo(IData param) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select old_product_id, old_brand_code,product_id,brand_code, decode(product_mode,'00','基本产品','01','附加产品','') product_mode,start_date,end_date, modify_tag ");
        parser.addSQL(" from tf_b_trade_product where 1=1");
        parser.addSQL(" and trade_id=:TRADE_ID");
        parser.addSQL(" and ACCEPT_MONTH = :ACCEPT_MONTH ");
        return Dao.qryByParse(parser, Route.getJourDb());
    }

    /**
     * 根据TRADE_ID从表tf_b_trade_product查询产品变化信息Cg
     * 
     * @param param
     * @param pagination
     * @return
     */
    public IDataset queryTradeProductInfoCg(IData param) throws Exception
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
        String tableName = "TF_BHB_TRADE_PRODUCT_"+year_id;
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select old_product_id, old_brand_code,product_id,brand_code, decode(product_mode,'00','基本产品','01','附加产品','') product_mode,start_date,end_date, modify_tag ");
        parser.addSQL(" from "+tableName+" where 1=1");
        parser.addSQL(" and trade_id=:TRADE_ID");
        return Dao.qryByParse(parser, Route.CONN_CRM_HIS);
    }
}
