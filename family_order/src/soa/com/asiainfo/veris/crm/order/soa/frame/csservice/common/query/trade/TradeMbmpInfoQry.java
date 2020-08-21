
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeMbmpInfoQry extends CSBizBean
{
    // todo
    /**
     * 4:短信二次确认查询
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qrySmsSecondConfirmInfo(IData param, Pagination pagination) throws Exception
    {

        String START_DATE = null;// AppCtx.getData("cond", true).getString("cond_START_DATE");

        if (null != START_DATE && !START_DATE.trim().equals(""))
        {
            START_DATE += SysDateMgr.getFirstTime00000();
        }

        String END_DATE = null;// AppCtx.getData("cond", true).getString("cond_END_DATE");

        if (null != END_DATE && !END_DATE.trim().equals(""))
        {
            END_DATE += SysDateMgr.getEndTime235959();
        }

        String BIZ_STATE_CODE = null;// AppCtx.getParameter("BIZ_STATE_CODE_QueryType");
        String SERIAL_NUMBER = null;// AppCtx.getData("cond", true).getString("cond_QueryCondition");

        IData data = new DataMap();

        data.put("START_DATE", START_DATE);
        data.put("END_DATE", END_DATE);
        data.put("BIZ_STATE_CODE", BIZ_STATE_CODE);
        data.put("SERIAL_NUMBER", SERIAL_NUMBER);

        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT A.TRADE_ID,A.ACCEPT_MONTH,A.CANCEL_TAG,A.USER_ID,A.SERIAL_NUMBER,A.BIZ_TYPE_CODE,A.ORG_DOMAIN,A.OPR_SOURCE ,");
        parser.addSQL("       A.BIZ_STATE_CODE,TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG,A.REMARK,A.TRADE_STAFF_ID,A.TRADE_DEPART_ID,A.TRADE_TIME ");
        parser.addSQL(" FROM TF_B_TRADE_MBMP A ");
        parser.addSQL(" WHERE 1=1  ");
        parser.addSQL("     AND A.START_DATE >= to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("     AND A.END_DATE <= to_date(:END_DATE,'yyyy-mm-dd  hh24:mi:ss') ");
        parser.addSQL("     AND A.BIZ_STATE_CODE=:BIZ_STATE_CODE   ");
        parser.addSQL("     AND A.SERIAL_NUMBER=:SERIAL_NUMBER   ");

        return Dao.qryByParse(parser, pagination);
    }

    // todo
    /**
     * 集团二次短信内容查询
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset QuerySecondSmsInfo(String CUST_MANAGER_ID, String GROUP_ID, String TRADE_TYPE_CODE, String TRADE_ID, String BIZ_STATE_CODE, String SERIAL_NUMBER, String START_DATE_S, String START_DATE_E, String END_DATE_S,
            String END_DATE_E, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_MANAGER_ID", CUST_MANAGER_ID);
        param.put("GROUP_ID", GROUP_ID);
        param.put("TRADE_TYPE_CODE", TRADE_TYPE_CODE);
        param.put("TRADE_ID", TRADE_ID);
        param.put("BIZ_STATE_CODE", BIZ_STATE_CODE);
        param.put("SERIAL_NUMBER", SERIAL_NUMBER);
        param.put("START_DATE_S", START_DATE_S);
        param.put("START_DATE_E", START_DATE_E);
        param.put("START_DATE_S", END_DATE_S);
        param.put("START_DATE_S", END_DATE_E);

        String eparchy_code = CSBizBean.getVisit().getStaffEparchyCode();

        SQLParser parser = new SQLParser(param);
        parser
                .addSQL(" SELECT me.TRADE_ID,me.SERIAL_NUMBER,me.ACCEPT_MONTH,me.BIZ_STATE_CODE,me.START_DATE,me.END_DATE,GRP.GROUP_ID,GRP.CUST_MANAGER_ID,GRP.CUST_NAME,me.RSRV_STR1,me.RSRV_STR2,me.RSRV_STR3,me.RSRV_STR4,me.RSRV_STR5 , decode(trade.subscribe_state,null,decode(me.RSRV_STR2,'是','完工','未完工'),'未完工') TRADE_STATE   ");
        parser.addSQL(" FROM TF_B_TRADE_MBMP me, TF_F_CUST_GROUP grp,TF_B_TRADE trade ");
        parser.addSQL(" WHERE me.RSRV_STR5 = grp.Cust_Id AND grp.REMOVE_TAG ='0' ");
        parser.addSQL(" AND grp.CUST_MANAGER_ID = :CUST_MANAGER_ID ");
        parser.addSQL(" AND grp.GROUP_ID = :GROUP_ID ");
        parser.addSQL(" AND trade.TRADE_TYPE_CODE(+) = :TRADE_TYPE_CODE ");
        parser.addSQL(" AND me.TRADE_ID = :TRADE_ID ");
        parser.addSQL(" AND me.TRADE_ID =  trade.TRADE_ID(+) ");
        parser.addSQL(" AND me.BIZ_STATE_CODE = :BIZ_STATE_CODE ");
        parser.addSQL(" AND me.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND me.START_DATE >= TO_DATE( :START_DATE_S,'YYYY-MM-DD')");
        parser.addSQL(" AND me.START_DATE <= TO_DATE( :START_DATE_E,'YYYY-MM-DD')+1");
        parser.addSQL(" AND me.END_DATE >= TO_DATE( :END_DATE_S,'YYYY-MM-DD')");
        parser.addSQL(" AND me.END_DATE <= TO_DATE( :END_DATE_E,'YYYY-MM-DD')+1");

        return Dao.qryByParse(parser, pagination, eparchy_code);
    }
}
