package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * @description 该类用于查询IBOSS过来的全网报文数据(包括TF_TP_BBOSS_XML_INFO表和TF_TP_BBOSS_XML_CONTENT表)
 * @author xunyl
 * @date 2015-02-05
 *
 */
public class BbossDestroyInfoQry {

    /**
     * @description 获取需要注销的数据
     * @author xunyl
     * @date 2016-01-21
     */
    public static IDataset getUserInfoList()throws Exception{
        IData param = new DataMap();
        param.put("DESTROY_TYPE", "1");
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT ");
        parser.addSQL("M.USER_ID, ");
        parser.addSQL("M.DESTROY_TYPE, ");
        parser.addSQL("M.GROUP_ID, ");
        parser.addSQL("M.SERIAL_NUMBER, ");
        parser.addSQL("M.PRODUCT_ID, ");
        parser.addSQL("M.EPARCHY_CODE, ");
        parser.addSQL("M.REMARK, ");
        parser.addSQL("M.IN_MODE_CODE, ");
        parser.addSQL("M.ANTI_INTF_FLAG, ");
        parser.addSQL("M.DESTROY_TIME, ");
        parser.addSQL("M.UPDATE_TIEM, ");     
        parser.addSQL("M.RSRV_STR1, ");
        parser.addSQL("M.RSRV_STR2, ");
        parser.addSQL("M.RSRV_STR3, ");
        parser.addSQL("M.RSRV_STR4, ");
        parser.addSQL("M.RSRV_STR5, ");
        parser.addSQL("M.RSRV_STR6, ");
        parser.addSQL("M.RSRV_STR7, ");
        parser.addSQL("M.RSRV_STR8, ");
        parser.addSQL("M.RSRV_STR9, ");
        parser.addSQL("M.RSRV_STR10 ");
        parser.addSQL("FROM TF_TP_BBOSS_DESTROY_INFO M ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND M.DESTROY_TYPE = :DESTROY_TYPE ");
        parser.addSQL(" AND M.DESTROY_TIME < sysdate - 1");
        parser.addSQL(" AND M.RSRV_STR1 is null");

        return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
    }

}
