
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BTradeInfoDAO
{
    /**
     * 查询受理信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryAcceptInfo(IData param) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select TRADE_TYPE_CODE, TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_CITY_CODE,TRADE_EPARCHY_CODE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,FEE_STATE,OPER_FEE,ADVANCE_PAY,FOREGIFT,CANCEL_TAG,SUBSCRIBE_STATE,FINISH_DATE,SERIAL_NUMBER TRADE_SERIAL_NUMBER, ");
        parser.addSQL(" ACCEPT_DATE,PROCESS_TAG_SET,REMARK from tf_b_trade where 1 = 1 ");
        parser.addSQL(" and TRADE_ID = :TRADE_ID and rownum < 2 ");
        parser.addSQL(" and ACCEPT_MONTH = :ACCEPT_MONTH ");
        return Dao.qryByParse(parser, Route.getJourDb());
    }

    /**
     * 查询受理信息 Cg
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryAcceptInfoCg(IData param) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select TRADE_TYPE_CODE, TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_CITY_CODE,TRADE_EPARCHY_CODE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,FEE_STATE,OPER_FEE,ADVANCE_PAY,FOREGIFT,CANCEL_TAG,SUBSCRIBE_STATE,FINISH_DATE,SERIAL_NUMBER TRADE_SERIAL_NUMBER, ");
        parser.addSQL(" ACCEPT_DATE,PROCESS_TAG_SET,REMARK from tf_b_trade where 1 = 1 ");
        parser.addSQL(" and TRADE_ID = :TRADE_ID and rownum < 2 ");
        return Dao.qryByParse(parser, Route.ROUTE_EPARCHY_CODE);
    }
}
