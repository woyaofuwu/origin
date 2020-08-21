
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserBadnessInfoQry
{

    /**
     * 查询集团投诉信息
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryGroupAdcMasComplaintsInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT INFO_RECV_ID, ");
        parser.addSQL("        KF_CALL_IN_SERIAL_NUMBER, ");
        parser.addSQL("        KF_CALL_OUT_SERIAL_NUMBER, ");
        parser.addSQL("        REPORT_SERIAL_NUMBER, ");
        parser.addSQL("        REPORT_CUST_NAME, ");
        parser.addSQL("        REPORT_CUST_LEVEL, ");
        parser.addSQL("        REPORT_BRAND_CODE, ");
        parser.addSQL("        SERV_REQUEST_TYPE, ");
        parser.addSQL("        IMPORTANT_LEVEL, ");
        parser.addSQL("        REPORT_CUST_PROVINCE, ");
        parser.addSQL("        RECV_PROVINCE, ");
        parser.addSQL("        EPARCHY_CODE, ");
        parser.addSQL("        TARGET_PROVINCE, ");
        parser.addSQL("        REPORT_TIME, ");
        parser.addSQL("        RECV_TIME, ");
        parser.addSQL("        BADNESS_INFO, ");
        parser.addSQL("        BADNESS_INFO_PROVINCE, ");
        parser.addSQL("        REPORT_TYPE_CODE, ");
        parser.addSQL("        REPEAT_REPORT, ");
        parser.addSQL("        VRIFY_INFO, ");
        parser.addSQL("        RECV_STAFF_ID, ");
        parser.addSQL("        RECV_IN_TYPE, ");
        parser.addSQL("        STICK_LIST, ");
        parser.addSQL("        RECORD_FILE_LIST, ");
        parser.addSQL("        OPERATE_STEP, ");
        parser.addSQL("        STATE, ");
        parser.addSQL("        SORT_RESULT_TYPE, ");
        parser.addSQL("        FINISH_DATE, ");
        parser.addSQL("        DEAL_DATE, ");
        parser.addSQL("        CONTACT_SERIAL_NUMBER, ");
        parser.addSQL("        DEAL_RAMARK, ");
        parser.addSQL("        DEAL_LIMIT, ");
        parser.addSQL("        RSRV_STR1, ");
        parser.addSQL("        RSRV_STR2, ");
        parser.addSQL("        RSRV_STR3 ");
        parser.addSQL("   FROM TF_F_BADNESS_INFO K ");
        parser.addSQL(" WHERE K.BADNESS_INFO = :SERIAL_NUMBER ");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }
}
