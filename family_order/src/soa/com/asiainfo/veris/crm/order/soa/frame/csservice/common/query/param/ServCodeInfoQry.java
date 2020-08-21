
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ServCodeInfoQry
{

    public static IDataset getExtend(String otherOperBeginExtend, String matchLength) throws Exception
    {
        IData param = new DataMap();
        param.put("OTHER_OPER_BEGIN_EXTEND", otherOperBeginExtend);
        param.put("MATCH_LENGTH", matchLength);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TD_B_OTHER_OPER_EXTEND i where 1 = 1 and i.other_oper_begin_extend=:OTHER_OPER_BEGIN_EXTEND");
        parser.addSQL(" and i.match_length=:MATCH_LENGTH");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset getServ(String beginSerialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("BEGIN_SERRIAL_NUMBER", beginSerialNumber);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TD_B_SERV_APP_EXTEND e where 1 = 1 and e.begin_serrial_number=:BEGIN_SERRIAL_NUMBER");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset getServCode(String servCodeBeginExtend, String beginMatchLength) throws Exception
    {// shenh
        IData param = new DataMap();
        param.put("SERVCODE_BEGIN_EXTEND", servCodeBeginExtend);
        param.put("BEGIN_MATCH_LENGTH", beginMatchLength);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TD_B_SERVCODE_SORT_RULE e where 1 = 1 and e.servcode_begin_extend=:SERVCODE_BEGIN_EXTEND");
        parser.addSQL(" and e.begin_match_length=:BEGIN_MATCH_LENGTH ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 取得垃圾短信服务代码分拣规则信息表
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getServCodeFull(String servCodeBeginExtend) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVCODE_BEGIN_EXTEND", servCodeBeginExtend);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TD_B_SERVCODE_SORT_RULE e where 1 = 1 and e.servcode_begin_extend=:SERVCODE_BEGIN_EXTEND");
        // parser.addSQL(" and e.begin_match_length=:BEGIN_MATCH_LENGTH ");
        parser.addSQL(" and e.begin_match_length=e.FULL_LENGTH ");
        // and e.full_length=:FULL_LENGTH
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
}
