
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserGrpMerchpDiscntInfoQry
{

    /**
     * 根据userId、merchSpecCode、productSpecCode、productDiscntCode查询TF_F_USER_GRP_MERCHP_DISCNT表信息 从集团库 查询用户订购优惠信息
     * 
     * @author ft
     * @param user_id
     * @param merch_spec_code
     * @param product_spec_code
     * @param product_discnt_code
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchpDiscntByUseridMerchScPrdouctScProductDc(String user_id, String merch_spec_code, String product_spec_code, String product_discnt_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("MERCH_SPEC_CODE", merch_spec_code);
        param.put("PRODUCT_SPEC_CODE", product_spec_code);
        param.put("PRODUCT_DISCNT_CODE", product_discnt_code);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select a.PARTITION_ID, ");
        parser.addSQL("to_char(a.USER_ID) USER_ID, ");
        parser.addSQL("a.MERCH_SPEC_CODE, ");
        parser.addSQL("a.PRODUCT_ORDER_ID, ");
        parser.addSQL("a.PRODUCT_OFFER_ID, ");
        parser.addSQL("a.PRODUCT_SPEC_CODE, ");
        parser.addSQL("a.PRODUCT_DISCNT_CODE, ");
        parser.addSQL("to_char(a.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL("to_char(a.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL("to_char(a.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL("a.UPDATE_STAFF_ID, ");
        parser.addSQL("a.UPDATE_DEPART_ID, ");
        parser.addSQL("a.REMARK, ");
        parser.addSQL("a.RSRV_NUM1, ");
        parser.addSQL("a.RSRV_NUM2, ");
        parser.addSQL("a.RSRV_NUM3, ");
        parser.addSQL("to_char(a.RSRV_NUM4) RSRV_NUM4, ");
        parser.addSQL("to_char(a.RSRV_NUM5) RSRV_NUM5, ");
        parser.addSQL("a.RSRV_STR1, ");
        parser.addSQL("a.RSRV_STR2, ");
        parser.addSQL("a.RSRV_STR3, ");
        parser.addSQL("a.RSRV_STR4, ");
        parser.addSQL("a.RSRV_STR5, ");
        parser.addSQL("to_char(a.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("to_char(a.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL("to_char(a.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL("a.RSRV_TAG1, ");
        parser.addSQL("a.RSRV_TAG2, ");
        parser.addSQL("a.RSRV_TAG3, ");
        parser.addSQL("a.INST_ID ");
        parser.addSQL("from TF_F_USER_GRP_MERCHP_DISCNT a ");
        parser.addSQL("where 1 = 1 ");
        parser.addSQL("and a.USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL("and (a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE or :MERCH_SPEC_CODE IS NULL) ");
        parser.addSQL("and (a.PRODUCT_SPEC_CODE = :PRODUCT_SPEC_CODE or ");
        parser.addSQL(":PRODUCT_SPEC_CODE IS NULL) ");
        parser.addSQL("and (a.PRODUCT_DISCNT_CODE = :PRODUCT_DISCNT_CODE or ");
        parser.addSQL(":PRODUCT_DISCNT_CODE IS NULL) ");
        parser.addSQL("and a.end_date > sysdate ");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }
}
