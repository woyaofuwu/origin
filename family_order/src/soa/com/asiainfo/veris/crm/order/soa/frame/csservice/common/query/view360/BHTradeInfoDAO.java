
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BHTradeInfoDAO
{

    /* 去用户的密码校验方式2010-1-26 */
    public IData getIdentity(String tradeId, String tradeSerialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("serial_number", tradeSerialNumber);
        param.put("rsrv_str1", tradeId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select info_content from tl_b_identityauth_log ");
        parser.addSQL(" where serial_number =:serial_number and rsrv_str1=:rsrv_str1 and rownum <= 1 ");
        IDataset ds = Dao.qryByParse(parser);
        if (ds.size() > 0)
        {
            return ds.getData(0);
        }
        return null;
    }

    /**
     * 查询受理信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryAcceptInfo(IData param) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "").trim();
        if ("".equals(trade_id))
        {
        	return new DatasetList();
        }

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select TRADE_TYPE_CODE, TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_CITY_CODE,TRADE_EPARCHY_CODE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,FEE_STATE,OPER_FEE,ADVANCE_PAY,FOREGIFT,CANCEL_TAG,SUBSCRIBE_STATE,FINISH_DATE,SERIAL_NUMBER TRADE_SERIAL_NUMBER, ");
        parser.addSQL(" ACCEPT_DATE,PROCESS_TAG_SET,REMARK from tf_bh_trade where 1 = 1 ");
        parser.addSQL(" and TRADE_ID = :TRADE_ID");
        parser.addSQL(" and ACCEPT_MONTH = :ACCEPT_MONTH ");
        return Dao.qryByParse(parser, Route.getJourDb());
    }

    /**
     * 查询受理信息Cg
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryAcceptInfoCg(IData param) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "").trim();
        if ("".equals(trade_id))
        {
        	return new DatasetList();
        }
        String year_id = param.getString("QUERY_YEAR","");
        if(StringUtils.isBlank(year_id)){
        	return new DatasetList();
        }
        String tableName = "TF_BHB_TRADE_"+year_id;
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select TRADE_TYPE_CODE, TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_CITY_CODE,TRADE_EPARCHY_CODE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,FEE_STATE,OPER_FEE,ADVANCE_PAY,FOREGIFT,CANCEL_TAG,SUBSCRIBE_STATE,FINISH_DATE,SERIAL_NUMBER TRADE_SERIAL_NUMBER, ");
        parser.addSQL(" PROCESS_TAG_SET,REMARK from "+tableName+" where 1 = 1 ");
        parser.addSQL(" and TRADE_ID = :TRADE_ID");
        /*
         * parser.addSQL(" union all "); parser.addSQL(
         * " select TRADE_TYPE_CODE, TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_CITY_CODE,TRADE_EPARCHY_CODE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,FEE_STATE,OPER_FEE,ADVANCE_PAY,FOREGIFT,CANCEL_TAG,SUBSCRIBE_STATE,FINISH_DATE,SERIAL_NUMBER TRADE_SERIAL_NUMBER, "
         * ); parser.addSQL(" decode(substr(PROCESS_TAG_SET,20,1),'B','密码校验','E','身份证校验','身份证校验') CHECK_MOD,");
         * parser.addSQL(" PROCESS_TAG_SET,REMARK from tf_bh_trade_second where 1 = 1 ");
         * parser.addSQL(" and TRADE_ID = :TRADE_ID");
         */
        return Dao.qryByParse(parser, Route.CONN_CRM_HIS);
    }
}
