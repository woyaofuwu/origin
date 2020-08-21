
package com.asiainfo.veris.crm.order.soa.group.grppaymark;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPtypeProductInfoQry;

public class BreakGrpPayMarkQry
{
    /**
     * @Description:根据cust_id查询标准集团下的产品
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryProductByCustId(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT T.USER_ID, ");
        parser.addSQL("       T.SERIAL_NUMBER, ");
        parser.addSQL("       T.CUST_ID, ");
        parser.addSQL("       P.PRODUCT_ID, ");
        parser.addSQL("       P.PRODUCT_MODE, ");
        parser.addSQL("       P.MAIN_TAG ");
        parser.addSQL("  FROM TF_F_USER T, TF_F_USER_PRODUCT P ");
        parser.addSQL(" WHERE T.CUST_ID = :CUST_ID ");
        parser.addSQL("   AND T.REMOVE_TAG = '0' ");
        parser.addSQL("   AND T.PARTITION_ID = P.PARTITION_ID ");
        parser.addSQL("   AND T.USER_ID = P.USER_ID ");
        parser.addSQL("   AND P.END_DATE > SYSDATE ");
        parser.addSQL("   AND NOT EXISTS ");
        parser.addSQL(" (SELECT 1 ");
        parser.addSQL("          FROM TD_S_COMMPARA A ");
        parser.addSQL("         WHERE A.SUBSYS_CODE = 'CSM' ");
        parser.addSQL("           AND A.PARAM_ATTR = 7357 ");
        parser.addSQL("           AND A.PARAM_CODE = TO_CHAR(P.PRODUCT_ID) ");
        parser.addSQL("           AND A.END_DATE > SYSDATE ");
        parser.addSQL("           AND (A.EPARCHY_CODE = '0898' OR A.EPARCHY_CODE = 'ZZZZ')) ");
        
        IDataset userInfos = Dao.qryByParse(parser);
        IDataset result = new DatasetList();
        if(IDataUtil.isNotEmpty(userInfos))
        {
        	for(int i=0; i<userInfos.size(); i++)
        	{
        		IData userInfo = userInfos.getData(i);
        		String productId = userInfo.getString("PRODUCT_ID");
        		if(UPtypeProductInfoQry.checkExisProductIdAndProductTypeCode(productId, "1400"))
        		{
        			result.add(userInfo);
        		}
        	}
        }
        
        return result;

    }
    
    /**
     * 不截止代付关系的分页查询
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryBreakGrpPayMarkInfo(IData param, Pagination pagination) 
    	throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT G.GROUP_ID GROUP_ID,");
        parser.addSQL("  G.CUST_NAME CUST_NAME,");
        parser.addSQL("  U.CUST_ID CUST_ID,");
        parser.addSQL("  U.USER_ID USER_ID,");
        parser.addSQL("  U.SERIAL_NUMBER SERIAL_NUMBER,");
        parser.addSQL("  TO_CHAR(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
        parser.addSQL("  TO_CHAR(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE");
        parser.addSQL(" FROM TF_F_USER_OTHER T, TF_F_USER U, TF_F_CUST_GROUP G");
        parser.addSQL("  	WHERE 1 = 1");
        parser.addSQL("  AND U.PARTITION_ID = T.PARTITION_ID");
        parser.addSQL("  AND U.USER_ID = T.USER_ID");
        parser.addSQL("  AND U.REMOVE_TAG = '0'");
        parser.addSQL("  AND U.CUST_ID = G.CUST_ID");
        parser.addSQL("  AND G.REMOVE_TAG = '0'");
        parser.addSQL("  AND U.SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL("  AND T.RSRV_VALUE_CODE = 'SGPR'");
        parser.addSQL("  AND T.END_DATE > SYSDATE");
        return Dao.qryByParse(parser, pagination);
    }
    
    /**
     * 不截止代付关系的导出
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset expBreakGrpPayMarkInfo(IData param, Pagination pagination) 
    	throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT G.GROUP_ID GROUP_ID,");
        parser.addSQL("  G.CUST_NAME CUST_NAME,");
        parser.addSQL("  U.CUST_ID CUST_ID,");
        parser.addSQL("  U.USER_ID USER_ID,");
        parser.addSQL("  U.SERIAL_NUMBER SERIAL_NUMBER,");
        parser.addSQL("  TO_CHAR(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
        parser.addSQL("  TO_CHAR(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE");
        parser.addSQL(" FROM TF_F_USER_OTHER T, TF_F_USER U, TF_F_CUST_GROUP G");
        parser.addSQL("  	WHERE 1 = 1");
        parser.addSQL("  AND U.PARTITION_ID = T.PARTITION_ID");
        parser.addSQL("  AND U.USER_ID = T.USER_ID");
        parser.addSQL("  AND U.REMOVE_TAG = '0'");
        parser.addSQL("  AND U.CUST_ID = G.CUST_ID");
        parser.addSQL("  AND G.REMOVE_TAG = '0'");
        parser.addSQL("  AND U.SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL("  AND T.RSRV_VALUE_CODE = 'SGPR'");
        parser.addSQL("  AND T.END_DATE > SYSDATE");
        return Dao.qryByParse(parser, pagination,Route.getCrmDefaultDb());
    }
    
}
