package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserGrpMerchMebDisQry {
    /**
     * chenyi
     * 查询用户订购流量叠加包 
     * @param userId
     * @return
     * @throws Exception
     */

    public static IDataset qryMebDisInfoByUid(String userId,String firstTime,String lastTime,String productOffid) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("LAST_TIME", lastTime);
        param.put("FIRST_TIME", firstTime);
        param.put("PRODUCT_OFFER_ID", productOffid);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT   PARTITION_ID, ");
        parser.addSQL("USER_ID, ");
        parser.addSQL("SERIAL_NUMBER, ");
        parser.addSQL("PRODUCT_OFFER_ID, ");
        parser.addSQL("PRODUCT_SPEC_CODE, ");
        parser.addSQL("PRODUCT_DISCNT_CODE, ");
        parser.addSQL("START_DATE, ");
        parser.addSQL("END_DATE, ");
        parser.addSQL("UPDATE_TIME, ");
        parser.addSQL("UPDATE_STAFF_ID, ");
        parser.addSQL("UPDATE_DEPART_ID, ");
        parser.addSQL("REMARK, ");
        parser.addSQL("RSRV_NUM1, ");
        parser.addSQL("RSRV_NUM2, ");
        parser.addSQL("RSRV_NUM3, ");
        parser.addSQL("RSRV_NUM4, ");
        parser.addSQL("RSRV_NUM5, ");
        parser.addSQL("RSRV_STR1, ");
        parser.addSQL("RSRV_STR2, ");
        parser.addSQL("RSRV_STR3, ");
        parser.addSQL("RSRV_STR4, ");
        parser.addSQL("RSRV_STR5, ");
        parser.addSQL("RSRV_DATE1, ");
        parser.addSQL("RSRV_DATE2, ");
        parser.addSQL("RSRV_DATE3, ");
        parser.addSQL("RSRV_TAG1, ");
        parser.addSQL("RSRV_TAG2, ");
        parser.addSQL("RSRV_TAG3, ");
        parser.addSQL("INST_ID, ");
        parser.addSQL("MEMBERORDERNUMBER ");
        parser.addSQL("FROM TF_F_USER_GRP_MERCH_MB_DIS DIS ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL("AND USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL("AND DIS.START_DATE < to_date(:LAST_TIME, 'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("AND DIS.START_DATE > to_date(:FIRST_TIME, 'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("AND DIS.PRODUCT_OFFER_ID = TO_NUMBER(:PRODUCT_OFFER_ID) ");
        return Dao.qryByParse(parser);
    }
    
    /**
	 * 国际流量统付首次激活修改截止时间
	 * @param endDate
	 * @param userId
	 * @param productSpecId
	 * @param productDiscntCode
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public static int updateEndDateByUserIdProductSpecCodeProductDiscntCode(String endDate,String userId,String productSpecCode,String productDiscntCode,String routeId) throws Exception{
        IData param = new DataMap();
        param.put("END_DATE", endDate);
        param.put("USER_ID", userId);
        param.put("PRODUCT_SPEC_CODE", productSpecCode);
        param.put("PRODUCT_DISCNT_CODE", productDiscntCode);
        return Dao.executeUpdateByCodeCode("TF_F_USER_GRP_MERCH_MB_DIS", "UPD_ENDDATE_BY_USERID_PRODUCTSPECCODE_PRODUCTDISCNTCODE", param,routeId);
    }
}
