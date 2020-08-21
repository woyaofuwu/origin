
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/*
 * * 获取tl_f_cust_person表中客户信息
 */
public class TradeCustPersonDAO
{
    /**
     * 根据条件查询客户信息
     * 
     * @param param
     * @return
     */
    public IDataset getContactInfos(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select CONTACT_TYPE_CODE,CONTACT,CONTACT_PHONE,POST_ADDRESS,POST_CODE,PHONE,FAX_NBR,EMAIL from tf_b_trade_cust_person where 1 = 1 ");
        parser.addSQL(" and TRADE_ID = :TRADE_ID ");
        parser.addSQL(" and CUST_ID = :CUST_ID ");
        parser.addSQL(" and ACCEPT_MONTH = :ACCEPT_MONTH ");
        return Dao.qryByParse(parser, Route.getJourDb());
    }

    /**
     * 根据条件查询客户联系信息Cg
     * 
     * @param param
     * @return
     */
    public IDataset getContactInfosCg(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select CONTACT_TYPE_CODE,CONTACT,CONTACT_PHONE,POST_ADDRESS,POST_CODE,PHONE,FAX_NBR,EMAIL from tl_f_cust_person where 1 = 1 ");
        parser.addSQL(" and log_id = :TRADE_ID ");
        parser.addSQL(" and CUST_ID = :CUST_ID ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * 根据条件查询客户信息
     * 
     * @param param
     * @return
     */
    public IDataset getCustPersonInfos(IData param) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from  tf_b_trade_cust_person ");
        parser.addSQL(" where TRADE_ID = :TRADE_ID");
        parser.addSQL(" and ACCEPT_MONTH = :ACCEPT_MONTH");
        return Dao.qryByParse(parser, Route.getJourDb());
    }

    /**
     * 根据条件查询客户信息
     * 
     * @param param
     * @return
     */
    public IDataset getCustPersonInfosByCg(IData param) throws Exception
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
        String tableName = "TF_BHB_TRADE_CUST_PERSON_"+year_id;
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from  "+tableName+" ");
        parser.addSQL(" where TRADE_ID = :TRADE_ID");
        return Dao.qryByParse(parser, Route.CONN_CRM_HIS);
    }
}
