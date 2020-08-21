
package com.asiainfo.veris.crm.order.soa.group.internetofthings;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/***
 * 物联网实例查询
 * 
 * @author
 */
public class GrpWlwInstancePfQuery
{

	/**
	 * 
	 * @param userId
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
    public static IData queryGrpWlwSubsIdByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT USER_ID, SUBS_ID, INST_TYPE, INST_ID, PROD_INST_ID FROM TF_F_INSTANCE_PF ");
        parser.addSQL(" WHERE USER_ID = :USER_ID AND INST_TYPE = 'U' ");
        IDataset result = Dao.qryByParse(parser);
        if (result != null && !result.isEmpty())
        {
            return result.getData(0);
        }
        return null;
    }
    
    /**
     * 
     * @param userId
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset queryOneUserSvcInstancePf(String userId,String serviceId) throws Exception
	{
    	IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT A.USER_ID, ");
        parser.addSQL("       C.INST_ID, ");
        parser.addSQL("       C.INST_TYPE, ");
        parser.addSQL("       B.PARA_CODE1, ");
        parser.addSQL("       B.PARA_CODE2, ");
        parser.addSQL("       A.SERVICE_ID ");
        parser.addSQL("  FROM TF_F_USER_SVC A, TF_F_INSTANCE_PF C, TD_S_COMMPARA B ");
        parser.addSQL(" WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL("	  AND A.USER_ID = :USER_ID ");
        parser.addSQL("	  AND A.END_DATE  > TRUNC(LAST_DAY(SYSDATE)+1) ");
        parser.addSQL("   AND A.SERVICE_ID = :SERVICE_ID ");
        parser.addSQL("   AND TO_CHAR(A.SERVICE_ID) = B.PARAM_CODE ");
        parser.addSQL("   AND B.PARAM_ATTR = 9014 ");
        parser.addSQL("   AND C.INST_ID = A.INST_ID ");
        parser.addSQL("   AND C.INST_TYPE = 'P' ");
        return Dao.qryByParse(parser);
	}
    
    /**
     * 
     * @param userID
     * @return
     * @throws Exception
     */
    public static IDataset queryAllSvcInstancePf(String userID) throws Exception
	{
    	IData param = new DataMap();
        param.put("USER_ID", userID);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT T.INST_ID INST_ID, ");
        parser.addSQL("       T.SERVICE_ID SERVICE_ID, ");
        parser.addSQL("       TO_CHAR(T.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        parser.addSQL("       TO_CHAR(T.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        parser.addSQL("       A.PARAM_CODE PARAM_CODE, ");
        parser.addSQL("       A.PARA_CODE1 PARA_CODE1, ");
        parser.addSQL("       A.PARA_CODE2 PARA_CODE2, ");
        parser.addSQL("       A.PARA_CODE3 PARA_CODE3, ");
        parser.addSQL("       B.INST_TYPE INST_TYPE, ");
        parser.addSQL("       B.PROD_INST_ID PROD_INST_ID ");
        parser.addSQL("  FROM TF_F_USER_SVC T, TD_S_COMMPARA A, TF_F_INSTANCE_PF B ");
        parser.addSQL(" WHERE T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL("   AND T.USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL("   AND T.END_DATE > SYSDATE ");
        parser.addSQL("   AND A.SUBSYS_CODE = 'CSM' ");
        parser.addSQL("   AND A.PARAM_ATTR = 9014 ");
        parser.addSQL("   AND A.EPARCHY_CODE IN ('ZZZZ', '0898') ");
        parser.addSQL("   AND A.END_DATE > SYSDATE ");
        parser.addSQL("   AND TO_CHAR(T.SERVICE_ID) = A.PARAM_CODE ");
        parser.addSQL("   AND B.INST_ID = T.INST_ID ");
        parser.addSQL("   AND B.INST_TYPE = 'S' ");
        parser.addSQL("UNION ");
        parser.addSQL("SELECT T.INST_ID INST_ID, ");
        parser.addSQL("       T.DISCNT_CODE SERVICE_ID, ");
        parser.addSQL("       TO_CHAR(T.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        parser.addSQL("       TO_CHAR(T.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        parser.addSQL("       A.PARAM_CODE PARAM_CODE, ");
        parser.addSQL("       A.PARA_CODE1 PARA_CODE1, ");
        parser.addSQL("       A.PARA_CODE2 PARA_CODE2, ");
        parser.addSQL("       A.PARA_CODE3 PARA_CODE3, ");
        parser.addSQL("       B.INST_TYPE INST_TYPE, ");
        parser.addSQL("       B.PROD_INST_ID PROD_INST_ID ");
        parser.addSQL("  FROM TF_F_USER_DISCNT T, TD_S_COMMPARA A, TF_F_INSTANCE_PF B ");
        parser.addSQL(" WHERE T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL("   AND T.USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL("   AND T.END_DATE > SYSDATE ");
        parser.addSQL("   AND A.SUBSYS_CODE = 'CSM' ");
        parser.addSQL("   AND A.PARAM_ATTR = 9013 ");
        parser.addSQL("   AND A.EPARCHY_CODE IN ('ZZZZ', '0898') ");
        parser.addSQL("   AND A.END_DATE > SYSDATE ");
        parser.addSQL("   AND TO_CHAR(T.DISCNT_CODE) = A.PARAM_CODE ");
        parser.addSQL("   AND B.INST_ID = T.INST_ID ");
        parser.addSQL("   AND B.INST_TYPE = 'S' ");
        return Dao.qryByParse(parser);
	}
    
}
