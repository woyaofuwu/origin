
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class AdcCamponSvrcodeInfoQry
{

    /**
     * 通过SERV_CODE查询预占信息
     *
     * @author chenyr
     * @param svrCode
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qryAdcCamponSvrcodeInfo(IData param) throws Exception
    {
        IData data = new DataMap();
        data.put("SVR_CODE", param.getString("SVR_CODE", ""));
        data.put("CUST_MANAGER_ID", param.getString("CUST_MANAGER_ID", ""));

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select svr_code,oper_code,to_char(start_date, 'YYYY-MM-DD') start_date,to_char(end_date, 'YYYY-MM-DD') end_date,cust_manager_id,calling_type_code,depart_id,staff_id ");
        parser.addSQL("FROM  TF_F_SVRCODECAMPON_ADC t ");
        parser.addSQL("WHERE t.OPER_CODE = '0' ");
        parser.addSQL("AND t.SVR_CODE =:SVR_CODE ");
        parser.addSQL("AND t.CUST_MANAGER_ID = :CUST_MANAGER_ID ");
        parser.addSQL("AND t.END_DATE > sysdate ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * 通过SERV_CODE查询预占信息
     *
     * @author chenyr
     * @param svrCode
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qryAdcCamponSvrcodeInfoByKey(IData param) throws Exception
    {
        IData data = new DataMap();
        data.put("SVR_CODE", param.getString("SVR_CODE", ""));
        data.put("CUST_MANAGER_ID", param.getString("CUST_MANAGER_ID", ""));
        data.put("START_DATE", param.getString("START_DATE", ""));
        data.put("END_DATE", param.getString("END_DATE", ""));

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select svr_code,oper_code,to_char(start_date, 'YYYY-MM-DD') start_date,to_char(end_date, 'YYYY-MM-DD') end_date,cust_manager_id,calling_type_code,depart_id,staff_id ");
        parser.addSQL("FROM  TF_F_SVRCODECAMPON_ADC t ");
        parser.addSQL("WHERE t.OPER_CODE = '0' ");
        parser.addSQL("AND t.SVR_CODE =:SVR_CODE ");
        parser.addSQL("AND t.CUST_MANAGER_ID = :CUST_MANAGER_ID ");
        parser.addSQL("AND t.START_DATE > to_date(:START_DATE, 'YYYY-MM-DD hh24:mi:ss') ");
        parser.addSQL("AND t.END_DATE < to_date(:END_DATE, 'YYYY-MM-DD hh24:mi:ss') ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * 预占吉祥服务号码
     *
     * @param data
     * @throws Exception
     */
    public static void insertCamponServCode(IData data) throws Exception
    {
        Dao.insert("TF_F_SVRCODECAMPON_ADC", data, Route.CONN_CRM_CG);
    }

    /**
     * 取消预占
     *
     * @param data
     * @throws Exception
     */
    public static int cancelCamponServcode(IData data) throws Exception {
            SQLParser parser = new SQLParser(data);
            parser.addSQL("update TF_F_SVRCODECAMPON_ADC T");
            parser.addSQL(" set T.OPER_CODE = '1', ");
            parser.addSQL(" T.END_DATE = sysdate ");
            parser.addSQL(" where 1=1");
            parser.addSQL(" AND T.SVR_CODE=:SVR_CODE");
            parser.addSQL(" AND T.CUST_MANAGER_ID=:CUST_MANAGER_ID");

            return Dao.executeUpdate(parser);
    }

    /**
     * 取消预占
     *
     * @param data
     * @throws Exception
     */
    public static int usedCamponServcode(IData data) throws Exception {
            SQLParser parser = new SQLParser(data);
            parser.addSQL("update TF_F_SVRCODECAMPON_ADC T");
            parser.addSQL(" set T.END_DATE = to_date('20501231', 'YYYYMMDD') ");
            parser.addSQL(" where 1=1");
            parser.addSQL(" AND T.SVR_CODE=:SVR_CODE");

            return Dao.executeUpdate(parser);
    }
}
