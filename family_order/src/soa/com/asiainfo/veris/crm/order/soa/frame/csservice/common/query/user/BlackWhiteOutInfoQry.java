
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BlackWhiteOutInfoQry
{

    /**
     * @Function:  BlackWhiteOut
     * @Description: 查询黑白名单同步数据
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: 
     * @date: 2014-12-05
     * 
     */
    public static IDataset getBlackWhiteOutBySnSycType(String serial_number) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serial_number);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT T.USER_ID,T.SERIAL_NUMBER,T.GROUP_ID,T.CUST_NAME, ");
        parser.addSQL(" T.SERV_CODE,T.SERV_CODE,T.BIZ_CODE,T.USER_TYPE_CODE,T.PROVINCE_CODE,T.PROVINCE ");
        parser.addSQL(" FROM TF_F_BLACKWHITE_OUT T ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND T.SYNC_TYPE = '02' ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    public static IDataset getBlackWhiteOutInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT TRADE_ID,ACCEPT_MONTH,USER_ID,SERIAL_NUMBER,GROUP_ID,CUST_NAME,SERV_CODE,BIZ_CODE,USER_TYPE_CODE, ");
        parser.addSQL(" OPER_STATE,PROVINCE_CODE,PROVINCE,SYNC_TYPE,STATUS,STATUS_CODE,START_DATE,END_DATE,UPDATE_TIME, ");
        parser.addSQL(" UPDATE_STAFF_ID,UPDATE_DEPART_ID,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3, ");
        parser.addSQL(" RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 ");
        parser.addSQL(" FROM TF_F_BLACKWHITE_OUT T ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND SERIAL_NUMBER=:SERIAL_NUMBER ");
        parser.addSQL(" AND GROUP_ID=:GROUP_ID ");
        parser.addSQL(" AND SERV_CODE=:SERV_CODE ");
        parser.addSQL(" AND BIZ_CODE=:BIZ_CODE ");
        parser.addSQL(" AND PROVINCE_CODE=:PROVINCE_CODE ");
        parser.addSQL(" AND SYNC_TYPE = :SYNC_TYPE ");
        parser.addSQL(" AND sysdate BETWEEN START_DATE AND END_DATE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    
    public static void insertBlackWhiteOut(IData data ) throws Exception
    {
        Dao.insert("TF_F_BLACKWHITE_OUT", data, Route.CONN_CRM_CEN);
    }
}
