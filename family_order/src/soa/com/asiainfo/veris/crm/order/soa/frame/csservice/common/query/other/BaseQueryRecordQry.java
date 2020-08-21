
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BaseQueryRecordQry
{
    /**
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static int AssistRequestArchival(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE  TF_B_ASSISTREQ_LOG ");
        parser.addSQL(" SET UPDATE_DEPART_ID = :UPDATE_DEPART_ID, ");
        parser.addSQL(" UPDATE_STAFF_ID=:UPDATE_STAFF_ID, ");
        parser.addSQL(" UPDATE_TIME = to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss'), ");
        parser.addSQL(" ASSISTREQ_STATUS = :ASSISTREQ_STATUS ");
        parser.addSQL(" WHERE INDICTSEQ =:INDICTSEQ ");
        int bool = Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
        return bool;
    }

    /**
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset AssistRequestLogQurey(IData param, Pagination page) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * FROM TF_B_ASSISTREQ_LOG WHERE 1=1 ");
        parser.addSQL(" AND INDICTSEQ = :INDICTSEQ ");
        parser.addSQL(" AND ASSISTREQ_TIME < SYSDATE ");
        parser.addSQL(" AND SERVICE_TYPE=:SERVICE_TYPE ");
        parser.addSQL(" AND ASSISTREQ_STATUS = :ASSISTREQ_STATUS ");
        return Dao.qryByParse(parser, page, Route.CONN_CRM_CEN);
    }

    /**
     * 一级客服批次改造
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static int AssistRequestReply(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE  TF_B_ASSISTREQ_LOG ");
        parser.addSQL(" SET ASSISTREQ_RSLT = :ASSISTREQ_RSLT, ");
        parser.addSQL(" REPLY_DEPT=:REPLY_DEPT, ");
        parser.addSQL(" REPLY_STAFF = :REPLY_STAFF, ");
        parser.addSQL(" REPLY_PHONE = :REPLY_PHONE, ");
        parser.addSQL(" REPLY_TIME = to_date(:REPLY_TIME,'yyyy-mm-dd hh24:mi:ss'), ");
        parser.addSQL(" ASSISTREQ_STATUS = :ASSISTREQ_STATUS, ");
        parser.addSQL(" UPDATE_DEPART_ID = :UPDATE_DEPART_ID, ");
        parser.addSQL(" UPDATE_STAFF_ID = :UPDATE_STAFF_ID, ");
        parser.addSQL(" UPDATE_TIME = to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss'), ");
        parser.addSQL(" ANSWER_FILEATTACH =  :ANSWER_FILEATTACH, ");
        parser.addSQL(" ANSWER_VEDIOATTACH = :ANSWER_VEDIOATTACH ");
        parser.addSQL(" WHERE INDICTSEQ =:INDICTSEQ ");
        int bool = Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
        return bool;
    }

    /**
     * 一级客服批次改造
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static boolean AssistRequestSend(IData param) throws Exception
    {

        return Dao.insert("TF_B_ASSISTREQ_LOG", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询文件下载路径
     * 
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryUrlPath(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select * from td_s_commpara a");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.param_attr= :PARAM_ATTR ");
        parser.addSQL(" and a.subsys_code='CSM' ");
        parser.addSQL(" and a.para_code3= :PARA_CODE3 ");
        IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        return dataset;
    }
}
