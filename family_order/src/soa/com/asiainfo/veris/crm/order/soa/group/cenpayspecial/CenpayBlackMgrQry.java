
package com.asiainfo.veris.crm.order.soa.group.cenpayspecial;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


public class CenpayBlackMgrQry
{
    /**
     * 自由充产品黑名单用户管理分页查询
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryCenpayBlackList(IData param, Pagination pagination) 
    	throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT G.CUST_NAME, ");
        parser.addSQL(" T.PARTITION_ID, ");
        parser.addSQL(" TO_CHAR(T.USER_ID) USER_ID, ");
        parser.addSQL(" T.SERIAL_NUMBER, ");
        parser.addSQL(" TO_CHAR(T.CUST_ID) CUST_ID, ");
        parser.addSQL(" T.GROUP_ID, ");
        parser.addSQL(" TO_CHAR(T.INSERT_DATE, 'yyyy-mm-dd hh24:mi:ss') INSERT_DATE, ");
        parser.addSQL(" T.BLACK_TAG, ");
        parser.addSQL(" T.PRODUCT_ID, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" T.UPDATE_STAFF_ID, ");
        parser.addSQL(" T.REMARK, ");
        parser.addSQL(" T.RSRV_STR1, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL(" T.RSRV_TAG1 ");
        parser.addSQL(" FROM TF_F_CENPAY_BLACK_LIST T, TF_F_USER U, TF_F_CUST_GROUP G ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER  ");
        parser.addSQL(" AND T.BLACK_TAG = :BLACK_TAG ");
        parser.addSQL(" AND T.PARTITION_ID = U.PARTITION_ID(+) ");
        parser.addSQL(" AND T.USER_ID = U.USER_ID(+) ");
        parser.addSQL(" AND U.REMOVE_TAG(+) = '0' ");
        parser.addSQL(" AND T.CUST_ID = G.CUST_ID(+) ");
        parser.addSQL(" AND G.REMOVE_TAG(+) = '0' ");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 根据UserId查询记录
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryCenpayBlackByUserId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.PARTITION_ID, ");
        parser.addSQL(" TO_CHAR(T.USER_ID) USER_ID, ");
        parser.addSQL(" T.SERIAL_NUMBER, ");
        parser.addSQL(" TO_CHAR(T.CUST_ID) CUST_ID, ");
        parser.addSQL(" T.GROUP_ID, ");
        parser.addSQL(" TO_CHAR(T.INSERT_DATE, 'yyyy-mm-dd hh24:mi:ss') INSERT_DATE, ");
        parser.addSQL(" T.BLACK_TAG, ");
        parser.addSQL(" T.PRODUCT_ID, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" T.UPDATE_STAFF_ID, ");
        parser.addSQL(" T.REMARK, ");
        parser.addSQL(" T.RSRV_STR1, ");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL(" T.RSRV_TAG1 ");
        parser.addSQL(" FROM TF_F_CENPAY_BLACK_LIST T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("  AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("  AND T.USER_ID = :USER_ID ");
        parser.addSQL("  AND T.BLACK_TAG = :BLACK_TAG ");
        IDataset dataSet = Dao.qryByParse(parser);
        return dataSet;
    }
    
    /**
     * 根据user_id删除记录
     * @param userId
     * @throws Exception
     */
    public static int delCenpayBlackByUserId(String userId) 
    	throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        StringBuilder sql = new StringBuilder(1000);
        sql.append(" DELETE FROM TF_F_CENPAY_BLACK_LIST T ");
        sql.append(" 	WHERE ");
        sql.append(" 	T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append(" 	AND T.USER_ID = :USER_ID ");
        return Dao.executeUpdate(sql, param);
    }
    
    /**
     * 根据user_id修改记录
     * @param userId
     * @param blackTag
     * @return
     * @throws Exception
     */
    public static int updateCenpayBlackByUserId(String userId,String blackTag) 
    	throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("BLACK_TAG", blackTag);
        //param.put("UPDATE_TIME", SysDateMgr.getSysTime());
        param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        StringBuilder sql = new StringBuilder(1000);
        sql.append(" UPDATE TF_F_CENPAY_BLACK_LIST T ");
        sql.append(" SET T.BLACK_TAG = :BLACK_TAG ");
        sql.append(" 	,UPDATE_TIME = SYSDATE ");
        sql.append(" 	,UPDATE_STAFF_ID = :UPDATE_STAFF_ID");
        sql.append(" 	WHERE ");
        sql.append(" 	T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append(" 	AND T.USER_ID = :USER_ID ");
        return Dao.executeUpdate(sql, param);
    }
    
    /**
     * 新增黑名单用户
     * @param cenpayData
     * @return
     * @throws Exception
     */
    public static boolean  addCenpayBlack(IData cenpayData) 
    	throws Exception
    {
    	boolean resultFlag = false;
    	if(IDataUtil.isNotEmpty(cenpayData))
    	{
    		String userId = cenpayData.getString("USER_ID","");
    		if(StringUtils.isNotBlank(userId))
    		{
    			String partitionId = StrUtil.getPartition4ById(userId);
    			cenpayData.put("PARTITION_ID", partitionId);
    			cenpayData.put("INSERT_DATE", SysDateMgr.getSysTime());
    			resultFlag = Dao.insert("TF_F_CENPAY_BLACK_LIST", 
    					cenpayData, Route.getCrmDefaultDb());
    		}
    	}
    	return resultFlag;
    }
    
}
