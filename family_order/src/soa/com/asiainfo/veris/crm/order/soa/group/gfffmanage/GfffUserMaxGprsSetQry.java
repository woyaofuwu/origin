
package com.asiainfo.veris.crm.order.soa.group.gfffmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPtypeProductInfoQry;

public class GfffUserMaxGprsSetQry
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
     * 查询集团流量自由充用户信息
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-29
     */
    public static IDataset qryGfffUserInfo(IData param, Pagination pagination) 
    	throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT G.GROUP_ID      GROUP_ID,");
        parser.addSQL("       G.CUST_NAME     CUST_NAME,");
        parser.addSQL("       U.CUST_ID       CUST_ID,");
        parser.addSQL("       U.USER_ID       USER_ID,");
        parser.addSQL("       U.SERIAL_NUMBER SERIAL_NUMBER");
        parser.addSQL("  FROM TF_F_USER_PRODUCT T, TF_F_USER U, TF_F_CUST_GROUP G");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL("   AND U.PARTITION_ID = T.PARTITION_ID");
        parser.addSQL("   AND U.USER_ID = T.USER_ID");
        parser.addSQL("   AND U.REMOVE_TAG = '0'");
        parser.addSQL("   AND U.CUST_ID = G.CUST_ID");
        parser.addSQL("   AND G.REMOVE_TAG = '0'");
        parser.addSQL("   AND U.SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL("   AND T.PRODUCT_ID = 7344");
        parser.addSQL("   AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE");
        return Dao.qryByParse(parser, pagination);
    }
    /**
     * 查询统付总流量上限值信息
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-29
     */
    public static IDataset qryGfffUserMaxGprsSetInfo(String userId) 
	throws Exception
	{
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
	    SQLParser parser = new SQLParser(param);
	    parser.addSQL("SELECT A.PARTITION_ID,");
	    parser.addSQL("       A.USER_ID,");
	    parser.addSQL("       A.RSRV_VALUE_CODE,");
	    parser.addSQL("       A.RSRV_VALUE,");
	    parser.addSQL("       A.INST_ID,");
	    parser.addSQL("       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
	    parser.addSQL("       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE");
	    parser.addSQL("  FROM TF_F_USER_OTHER A");
	    parser.addSQL(" WHERE A.PARTITION_ID = MOD(:USER_ID, 10000)");
	    parser.addSQL("   AND A.USER_ID = :USER_ID");
	    parser.addSQL("   AND A.RSRV_VALUE_CODE = 'GFFF_MAX'");
	    parser.addSQL("   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE");
	    return Dao.qryByParse(parser);
	}
    
    /**
     * 修改预留字段，只是为了做锁(解决并发问题)
     * @param data
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-30
     */
    public static  int updateForGfffMebGprsMaxLock() throws Exception
    {
    	IData param = new DataMap();
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE TF_F_USER_GRP_GFSP T SET T.TAG_STR = 'U',T.UPDATE_TIME = SYSDATE ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.TAG_ID = 'GFFFMEB_GPRSMAX' ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        return Dao.executeUpdate(parser);
    }
    /**
     * 获取集团用户当前已统付的流量值
     * @param userId
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-30
     */
    public static IDataset qryGfffUserNowGprsVal(String userId) throws Exception
	{
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
	    SQLParser parser = new SQLParser(param);
	    parser.addSQL("SELECT A.USER_ID, A.GFFF_TYPE, A.ALL_FEE");
	    parser.addSQL("  FROM TF_F_USER_GFFF_MAXGPRS A");
	    parser.addSQL(" WHERE A.USER_ID = :USER_ID");
	    parser.addSQL("   AND A.GFFF_TYPE='1'");
	    parser.addSQL("   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE");
	    return Dao.qryByParse(parser);
	}
    /**
     * 查询表tf_f_user_meb_cenpay汇总出集团用户当前统付的流量值
     * @param userId
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-30
     */
    public static IDataset qryGfffUserNowGprsValBySum(String userId) throws Exception
	{
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
	    SQLParser parser = new SQLParser(param);
	    parser.addSQL("SELECT NVL(SUM(NVL(A.LIMIT_FEE, 0)),0) ALL_FEE");
	    parser.addSQL("  FROM TF_F_USER_MEB_CENPAY A");
	    parser.addSQL(" WHERE A.PAY_TYPE = '1'");
	    parser.addSQL("   AND A.OPER_TYPE = '5'");
	    parser.addSQL("   AND A.PRODUCT_OFFER_ID in (7344)");
	    parser.addSQL("   AND A.MP_GROUP_CUST_CODE = :USER_ID");
	    parser.addSQL("   AND A.END_DATE > SYSDATE");
	    return Dao.qryByParse(parser);
	}
    /**
     * 修改记录的当前统付流量值
     * @param userId
     * @param mebLimitFee
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-30
     */
    public static void updateGfffUserNowGprsVal(String userId, String mebLimitFee) throws Exception
	{
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	param.put("LIMIT_FEE", mebLimitFee);
	    SQLParser parser = new SQLParser(param);
	    parser.addSQL("UPDATE TF_F_USER_GFFF_MAXGPRS A");
	    parser.addSQL("   SET A.ALL_FEE = A.ALL_FEE + TO_NUMBER(:LIMIT_FEE)");
	    parser.addSQL(" WHERE A.USER_ID = :USER_ID");
	    parser.addSQL("   AND A.GFFF_TYPE = '1'");
	    parser.addSQL("   AND A.END_DATE > SYSDATE");
	    Dao.executeUpdate(parser);
	}
    /**
     * 校验集团流量自由充限量统付上限值
     * @param grpUserId：集团产品用户ID
     * @param mebLimitFee：成员统付流量
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-30
     */
    public static void checkGfffMebGprsMax(String grpUserId, long mebLimitFee) throws Exception{
    	//查询流量统付上限阀值
    	IDataset otherInfos = qryGfffUserMaxGprsSetInfo(grpUserId);
    	long lGprsMax = 0;
    	if(IDataUtil.isNotEmpty(otherInfos)){
    		String gprsMax = otherInfos.getData(0).getString("RSRV_VALUE", "0");
    		lGprsMax = Long.valueOf(gprsMax);		//集团统付流量阀值(MB)
    	}
    	//加锁
    	updateForGfffMebGprsMaxLock();
    	//获取集团用户当前已统付的流量
    	long lNowGprsVal = 0;
    	IDataset gprsValInfos = qryGfffUserNowGprsVal(grpUserId);
    	if(IDataUtil.isNotEmpty(gprsValInfos)){
    		String sNowGprsVal = gprsValInfos.getData(0).getString("ALL_FEE", "0");		//当前记录统付量
    		lNowGprsVal = Long.valueOf(sNowGprsVal);									//当前记录统付量
    	}
    	//不存在则新增(第一次办理)
    	else{
    		//从资料表汇总当前统付的总流量
    		IDataset sumInfos = qryGfffUserNowGprsValBySum(grpUserId);
    		if(IDataUtil.isNotEmpty(sumInfos)){
        		String sSumGprsVal = sumInfos.getData(0).getString("ALL_FEE", "0");		
        		lNowGprsVal = Long.valueOf(sSumGprsVal);								
        	}
    		
    		IData maxData = new DataMap();
    		maxData.put("USER_ID", grpUserId);
    		maxData.put("GFFF_TYPE", "1");
    		maxData.put("ALL_FEE", lNowGprsVal);
    		maxData.put("START_DATE", SysDateMgr.getSysTime());
    		maxData.put("END_DATE", SysDateMgr.getLastDateThisMonth());		//有效期到当月底
    		maxData.put("INSERT_TIME", SysDateMgr.getSysTime());
    		maxData.put("UPDATE_TIME", SysDateMgr.getSysTime());
    		Dao.insert("TF_F_USER_GFFF_MAXGPRS", maxData);
    	}
    	
    	//校验算上当次办理的成员统付流量值是否超过阀值
    	if((mebLimitFee+lNowGprsVal) > lGprsMax){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "成员本次统付流量["+mebLimitFee+"M]加上集团用户当前统付的总量["+lNowGprsVal+"M]超过统付总流量阀值["+lGprsMax+"M]");
    	}
    	//校验通过则算上本次统付的流量
    	updateGfffUserNowGprsVal(grpUserId, mebLimitFee+"");
    	
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
    /**
     * 查询工号绑定的产品编码信息
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public static IDataset qryGfffStaffIdBindInfos(IData param, Pagination pagination) 
	throws Exception
	{
	    SQLParser parser = new SQLParser(param);
	    parser.addSQL("SELECT A.INST_ID,");
	    parser.addSQL("       A.TRADE_STAFF_ID,");
	    parser.addSQL("       A.SERIAL_NUMBER,");
	    parser.addSQL("       TO_CHAR(A.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME,");
	    parser.addSQL("       TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,");
	    parser.addSQL("       A.UPDATE_STAFF_ID");
	    parser.addSQL("  FROM TF_F_USER_GFFF_BINDGRPSN A");
	    parser.addSQL(" WHERE 1 = 1");
	    parser.addSQL("   AND A.TRADE_STAFF_ID = :TRADE_STAFF_ID");
	    parser.addSQL("   AND A.SERIAL_NUMBER = :SERIAL_NUMBER");
	    parser.addSQL(" ORDER BY A.UPDATE_TIME DESC");
	    return Dao.qryByParse(parser, pagination);
	}
    /**
     * 查询工号绑定产品编码关系（流量自由充接口校验用）
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public static IDataset qryGfffStaffIdBindInfosForChk(IData param) 
	throws Exception
	{
	    StringBuilder sql = new StringBuilder();
	    sql.append("SELECT A.INST_ID,");
	    sql.append("       A.TRADE_STAFF_ID,");
	    sql.append("       A.SERIAL_NUMBER,");
	    sql.append("       TO_CHAR(A.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME,");
	    sql.append("       TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,");
	    sql.append("       A.UPDATE_STAFF_ID");
	    sql.append("  FROM TF_F_USER_GFFF_BINDGRPSN A");
	    sql.append(" WHERE 1 = 1");
	    sql.append("   AND A.TRADE_STAFF_ID = :TRADE_STAFF_ID");
	    sql.append("   AND A.SERIAL_NUMBER = :SERIAL_NUMBER");
	    return Dao.qryBySql(sql, param);
	}
    
}
