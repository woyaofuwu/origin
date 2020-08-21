
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class SentenceDealQry
{

    public static void dealSentenceByProc(IData input, String name, String[] paramName, IData paramValue, String routeId) throws Exception
    {
        Dao.callProc(name, paramName, paramValue, routeId);

        // 记录日志
        logSentence(input.getString("STMT_ID"), input.getString("STMT_TYPE"), name, paramValue);
    }

    public static int dealSentenceBySql(StringBuilder sql, IData data, String routeId) throws Exception
    {
        int returnCount = Dao.executeUpdate(sql, data.getData("IN_PARAM_DATA"), routeId);

        // 记录日志
        logSentence(data.getString("STMT_ID"), data.getString("STMT_TYPE"), data.getString("STMT_NAME"), data.getData("IN_PARAM_DATA"));

        return returnCount;
    }

    private static boolean logSentence(String stmtId, String stmtType, String stmtName, IData data) throws Exception
    {
        String tradeId = SeqMgr.getOperId();
        String acceptDate = SysDateMgr.getSysDate();
        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);

        IData insertData = new DataMap();

        insertData.put("LOG_ID", tradeId);
        insertData.put("ACCEPT_DATE", acceptDate);
        insertData.put("ACCEPT_MONTH", acceptMonth);

        insertData.put("STMT_ID", stmtId);
        insertData.put("STMT_TYPE", stmtType);
        insertData.put("STMT_NAME", stmtName);
        insertData.put("DATA", data);

        return Dao.insert("TL_F_DATA_MAINTAIN", insertData, Route.CONN_LOG);
    }

    public static IDataset querySentence(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select stmt_id,stmt_type,stmt_name,stmt_content,");
        parser.addSQL("to_char(START_TIME,'yyyy-MM-dd HH:mm:ss') START_TIME,to_char(END_TIME,'yyyy-MM-dd HH:mm:ss') END_TIME,");
        parser.addSQL("update_depart_id,update_staff_id,to_char(UPDATE_TIME,'yyyy-MM-dd HH:mm:ss') UPDATE_TIME,remark,rsrv_str1,rsrv_str2 ");
        parser.addSQL("from tb_b_data_maintain  ");
        parser.addSQL("where 1=1 ");
        parser.addSQL("and sysdate between start_time and  end_time ");
        parser.addSQL("and stmt_type = :STMT_TYPE ");
        parser.addSQL("and stmt_name like '%'||:STMT_NAME  ||'%' ");

        IDataset resultset = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0; i < resultset.size(); i++)
        {
            IData result = resultset.getData(i);
            result.put("STMT_TYPE_NAME", StaticUtil.getStaticValue("STMT_TYPE", result.getString("STMT_TYPE")));
        }
        return resultset;

    }

    public static IDataset showSentence(IData data) throws Exception
    {

        return Dao.qryByCode("TB_B_DATA_MAINTAIN", "SEL_BY_TYPE", data, Route.CONN_CRM_CEN);
    }
}
