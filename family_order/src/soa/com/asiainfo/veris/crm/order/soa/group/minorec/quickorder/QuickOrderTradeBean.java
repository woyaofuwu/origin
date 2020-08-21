package com.asiainfo.veris.crm.order.soa.group.minorec.quickorder;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class QuickOrderTradeBean {
    
    public static IDataset qryTradeInfosByProductId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        params.put("CUST_ID", param.getString("CUST_ID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.SERIAL_NUMBER,T.TRADE_ID,T.USER_ID, T.CUST_ID, T.PRODUCT_ID, T.CUST_NAME,T.ACCEPT_DATE,T.SUBSCRIBE_STATE,C.GROUP_ID ");
        parser.addSQL(" FROM TF_B_TRADE T,TF_F_CUST_GROUP C,TF_F_USER U ");
        parser.addSQL(" WHERE T.BRAND_CODE = 'ESPG' ");
        parser.addSQL(" AND T.CUST_ID = C.CUST_ID ");
        parser.addSQL(" AND T.USER_ID=U.USER_ID ");
        parser.addSQL(" AND U.CONTRACT_ID IS NOT NULL ");
        parser.addSQL(" AND T.PRODUCT_ID =:PRODUCT_ID ");
        parser.addSQL(" AND T.CUST_ID =:CUST_ID ");
        parser.addSQL(" UNION ");
        parser.addSQL(" SELECT T.SERIAL_NUMBER,T.TRADE_ID,T.USER_ID, T.CUST_ID, T.PRODUCT_ID, T.CUST_NAME,T.ACCEPT_DATE,T.SUBSCRIBE_STATE,C.GROUP_ID ");
        parser.addSQL(" FROM TF_BH_TRADE T,TF_F_CUST_GROUP C,TF_F_USER U ");
        parser.addSQL(" WHERE T.BRAND_CODE = 'ESPG' ");
        parser.addSQL(" AND T.CUST_ID = C.CUST_ID ");
        parser.addSQL(" AND T.USER_ID=U.USER_ID ");
        parser.addSQL(" AND U.CONTRACT_ID IS NOT NULL ");
        parser.addSQL(" AND T.PRODUCT_ID =:PRODUCT_ID ");
        parser.addSQL(" AND T.CUST_ID =:CUST_ID ");
        parser.addSQL(" ORDER BY ACCEPT_DATE DESC ");
        IDataset results = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        if(IDataUtil.isNotEmpty(results)){
            qrySubscribeState(results);
        }
        return results;
    }
    
    public static IDataset qryTradeInfoByUserId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.SERIAL_NUMBER,T.TRADE_ID,T.USER_ID, T.CUST_ID, T.PRODUCT_ID, T.CUST_NAME,T.ACCEPT_DATE,T.SUBSCRIBE_STATE,C.GROUP_ID ");
        parser.addSQL(" FROM TF_B_TRADE T,TF_F_CUST_GROUP C ");
        parser.addSQL(" WHERE T.BRAND_CODE = 'ESPG' ");
        parser.addSQL(" AND T.CUST_ID = C.CUST_ID ");
        parser.addSQL(" AND T.SERIAL_NUMBER =:SERIAL_NUMBER ");
        parser.addSQL(" UNION ");
        parser.addSQL(" SELECT T.SERIAL_NUMBER,T.TRADE_ID,T.USER_ID, T.CUST_ID, T.PRODUCT_ID, T.CUST_NAME,T.ACCEPT_DATE,T.SUBSCRIBE_STATE,C.GROUP_ID ");
        parser.addSQL(" FROM TF_BH_TRADE T,TF_F_CUST_GROUP C ");
        parser.addSQL(" WHERE T.BRAND_CODE = 'ESPG' ");
        parser.addSQL(" AND T.CUST_ID = C.CUST_ID ");
        parser.addSQL(" AND T.SERIAL_NUMBER =:SERIAL_NUMBER ");
        parser.addSQL(" ORDER BY ACCEPT_DATE DESC ");
        IDataset results = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        if(IDataUtil.isNotEmpty(results)){
            qrySubscribeState(results);
        }
        return results;
    }
    
    /**
     * 工单状态转义
     * @param results
     * @throws Exception
     */
    public static void qrySubscribeState(IDataset results) throws Exception {
        for(Object obj: results) {
            IData data = (IData)obj;
            String subscribeState = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
                    { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[]
                    { "ESP_SUBSCRIBE_STATE", data.getString("SUBSCRIBE_STATE") });
            data.put("SUBSCRIBE_STATE", subscribeState);
        }
    }
    
    /**
     * 
    * @Title: qryMemberInfosTradeByUserId 
    * @Description: 根据useridA查询台账表中成员信息 
    * @param param
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年10月19日下午2:49:09
     */
    public static IDataset qryMemberInfosTradeByUserId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("USER_ID_A", param.getString("USER_ID_A"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.USER_ID_A,T.SERIAL_NUMBER_A,T.USER_ID_B,T.SERIAL_NUMBER_B  ");
        parser.addSQL(" FROM TF_B_TRADE_RELATION_BB T ");
        parser.addSQL(" WHERE T.USER_ID_A =:USER_ID_A ");
        IDataset results = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        return results;
    }
    
    /**
     * 
     * @Title: qryMemberInfosDataByUserId 
     * @Description: 根据useridA查询资料表中成员信息 
     * @param param
     * @return
     * @throws Exception IDataset
     * @author zhangzg
     * @date 2019年10月19日下午2:49:09
     */
    public static IDataset qryMemberInfosDataByUserId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("USER_ID_A", param.getString("USER_ID_A"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.USER_ID_A,T.SERIAL_NUMBER_A,T.USER_ID_B,T.SERIAL_NUMBER_B  ");
        parser.addSQL(" FROM TF_F_RELATION_BB T ");
        parser.addSQL(" WHERE T.USER_ID_A =:USER_ID_A ");
        IDataset results = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        return results;
    }

}
