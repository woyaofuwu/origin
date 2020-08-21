
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class TradeNpQry
{
	private static final transient Logger log = Logger.getLogger(TradeNpQry.class);
	
	//根据手机号码/授权码/授权码有效期查询用户查验信息
    public static IDataset qryUserNpCheckInfosBySn(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("AUTH_CODE", input.getString("AUTH_CODE"));
        param.put("AUTH_CODE_EXPIRED", input.getString("AUTH_CODE_EXPIRED"));
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_CHECK_BY_SN", param);

    }
	
	public static IDataset queryMobileDiscnt(String discntCode) throws Exception
	{
		IData param = new DataMap();
		param.put("DISCNTCODE", discntCode);
		return Dao.qryByCodeParser("TD_B_MOBILE_DISCNT", "SEL_ACTIVE_BY_DISCNT_CODE", param,Route.CONN_CRM_CEN);
	}
    public static IDataset getAllTradeNpBySn(String sn) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_BY_SN", param);
    }
    public static IDataset getAllInfoBySn(String sn, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", sn);
        params.put("REMOVE_TAG", "0");
        params.put("NET_TYPE_CODE", "00");
        return Dao.qryByCode("TF_F_USER", "SEL_ALL_INFO_BY_SERIALNUMBER", params, pagination);
    }
    public static IDataset getHisUserNpsByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_NP", "SEL_HISUSERNP_BY_USERID", param);
    }

    /**
     * @Function: getNpOutFailInfos
     * @Description: 用户携出申请历史查询 获取携出用户资料 携出生效失败列表
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-3-24 上午10:53:27 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-24 lijm3 v1.0.0 修改原因
     */
    public static IDataset getNpOutFailInfos(String serial_number, String start_date, String end_date, Pagination page) throws Exception
    {

        IData param1 = new DataMap();
        IData param2 = new DataMap();
        if(!"".equals(serial_number))
        {
        	param1.put("SERIAL_NUMBER", serial_number);
        }
        param2.put("START_DATE", start_date);
        param2.put("END_DATE", end_date);
        
        IDataset results = new DatasetList();
        IDataset tradeNpInfos =  Dao.qryByCode("TF_B_TRADE_NP", "SEL_3_BY_USERID_ACCEPT_DATE", param2, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        
        if(IDataUtil.isEmpty(tradeNpInfos))
        {	
        	return new DatasetList();
        }
        else
        {
        	for(int i=0;i<tradeNpInfos.size();i++)
        	{
        		param1.put("USER_ID", tradeNpInfos.getData(i).getString("USER_ID"));
                IDataset userInfos =  Dao.qryByCodeParser("TF_F_USER", "SEL_NPOUT_BY_SERIAL_NUMBER", param1);
        		if(IDataUtil.isNotEmpty(userInfos))
        		{
        			userInfos.getData(0).put("RESULT_MESSAGE", tradeNpInfos.getData(i).getString("RESULT_MESSAGE"));
        			userInfos.getData(0).put("ACCEPT_DATE", tradeNpInfos.getData(i).getString("ACCEPT_DATE"));
        			userInfos.getData(0).put("PORT_IN_NETID", tradeNpInfos.getData(i).getString("PORT_IN_NETID"));
        			results.add(userInfos.getData(0));
        		}
        		
        	}
        }
        return results;
    }

    /**
     * @Function: getNpOutInfos
     * @Description: 携转号码回访记录用到
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-4-3 下午1:00:00 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-3 lijm3 v1.0.0 修改原因
     */
    public static IDataset getNpOutInfos(String userId) throws Exception
    {
        StringBuilder sql = new StringBuilder(2000);

        sql.append(" SELECT U.USER_ID,");
        sql.append("  U.SERIAL_NUMBER,");
        sql.append(" P.BRAND_CODE,");
        sql.append(" P.PRODUCT_ID,");
        sql.append(" TO_CHAR(U.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') OPEN_DATE,");
        sql.append("  NVL(UC.CITY_CODE, U.CITY_CODE) CITY_CODE,");
        sql.append("  C.CUST_NAME,");
        sql.append("  V.VIP_CLASS_ID,");
        sql.append(" V.VIP_TYPE_CODE,");
        sql.append("  V.CUST_MANAGER_ID,");
        sql.append(" DECODE(U.USER_TAG_SET, '4', '是', '5', '是', '8', '是', '否') IS_SUCC,");
        sql.append(" U.CUST_ID USER_CUST_ID,");
        sql.append(" CG.USER_ID CUST_USER_ID,");
        sql.append(" UU.USER_ID_A,");
        sql.append(" DECODE(U.REMOVE_TAG, '7',TO_CHAR(U.DESTROY_TIME, 'YYYY-MM-DD HH24:MI:SS'),NULL) APPLY_DATE,");
        sql.append(" CG.MEMBER_KIND,");
        sql.append(" CG.GROUP_ID,");
        sql.append(" CG.GROUP_CUST_NAME");
        sql.append(" FROM TF_F_USER             U,");
        sql.append(" TF_F_USER_PRODUCT     P,");
        sql.append(" TF_F_CUSTOMER         C,");
        sql.append(" TF_F_USER_CITY        UC,");
        sql.append(" TF_F_CUST_VIP         V,");
        sql.append(" TF_F_CUST_GROUPMEMBER CG,");
        sql.append(" TF_F_RELATION_UU      UU");
        sql.append(" WHERE UC.USER_ID(+) = U.USER_ID");
        sql.append(" AND UC.END_DATE(+) > SYSDATE");
        sql.append(" AND C.CUST_ID = U.CUST_ID");
        sql.append(" AND C.PARTITION_ID = MOD(U.CUST_ID, 10000)");
        sql.append(" AND P.USER_ID = U.USER_ID");
        sql.append(" AND P.PARTITION_ID = MOD(U.USER_ID, 10000)");
        sql.append(" AND P.MAIN_TAG = '1'");
        sql.append(" AND V.CUST_ID(+) = U.CUST_ID");
        sql.append(" AND V.REMOVE_TAG(+) = '0'");
        sql.append(" AND (NVL(V.VIP_CLASS_ID, 'n') != '1' OR NVL(V.VIP_TYPE_CODE, 'n') != '0')");
        sql.append(" AND CG.MEMBER_CUST_ID(+) = U.CUST_ID");
        sql.append(" AND CG.REMOVE_TAG(+) = '0'");
        sql.append(" AND UU.RELATION_TYPE_CODE(+) = '20'");
        sql.append(" AND UU.END_DATE(+) > SYSDATE");
        sql.append(" AND UU.USER_ID_B(+) = U.USER_ID");
        sql.append(" AND UU.PARTITION_ID(+) = MOD(U.USER_ID, 10000)");
        sql.append(" AND U.USER_ID = :USER_ID");
        sql.append(" AND U.PARTITION_ID = MOD(:USER_ID, 10000)");
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryBySql(sql, param);
    }
	public static String getRecodeCountByNpSaveFlag(String serialNum) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNum);
        IDataset result = Dao.qryByCode("TF_B_TRADE_NP", "SEL_BY_NP_SAVEFLG", param,Route.getJourDb());
        
        return result.getData(0).getString("RECORDCOUNT");
    }
	
    /**
     * @Function: getNpOutInfos
     * @Description: 携转号码回访记录用到
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-4-3 下午1:05:53 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-3 lijm3 v1.0.0 修改原因
     */
    public static IDataset getNpOutInfos(String userId, String area_code) throws Exception
    {
        StringBuilder sql = new StringBuilder(2000);

        sql.append(" SELECT U.USER_ID,");
        sql.append("  U.SERIAL_NUMBER,");
        sql.append(" P.BRAND_CODE,");
        sql.append(" P.PRODUCT_ID,");
        sql.append(" TO_CHAR(U.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') OPEN_DATE,");
        sql.append("  NVL(UC.CITY_CODE, U.CITY_CODE) CITY_CODE,");
        sql.append("  C.CUST_NAME,");
        sql.append("  V.VIP_CLASS_ID,");
        sql.append(" V.VIP_TYPE_CODE,");
        sql.append("  V.CUST_MANAGER_ID,");
        sql.append(" DECODE(U.USER_TAG_SET, '4', '是', '5', '是', '8', '是', '否') IS_SUCC,");
        sql.append(" U.CUST_ID USER_CUST_ID,");
        sql.append(" CG.USER_ID CUST_USER_ID,");
        sql.append(" UU.USER_ID_A,");
        sql.append(" DECODE(U.REMOVE_TAG, '7',TO_CHAR(U.DESTROY_TIME, 'YYYY-MM-DD HH24:MI:SS'),NULL) APPLY_DATE,");
        sql.append(" CG.MEMBER_KIND,");
        sql.append(" CG.GROUP_ID,");
        sql.append(" CG.GROUP_CUST_NAME");
        sql.append(" FROM TF_F_USER             U,");
        sql.append(" TF_F_USER_PRODUCT     P,");
        sql.append(" TF_F_CUSTOMER         C,");
        sql.append(" TF_F_USER_CITY        UC,");
        sql.append(" TF_F_CUST_VIP         V,");
        sql.append(" TF_F_CUST_GROUPMEMBER CG,");
        sql.append(" TF_F_RELATION_UU      UU");
        sql.append(" WHERE UC.USER_ID(+) = U.USER_ID");
        sql.append(" AND UC.END_DATE(+) > SYSDATE");
        sql.append(" AND C.CUST_ID = U.CUST_ID");
        sql.append(" AND C.PARTITION_ID = MOD(U.CUST_ID, 10000)");
        sql.append(" AND P.USER_ID = U.USER_ID");
        sql.append(" AND P.PARTITION_ID = MOD(U.USER_ID, 10000)");
        sql.append(" AND P.MAIN_TAG = '1'");
        sql.append(" AND V.CUST_ID(+) = U.CUST_ID");
        sql.append(" AND V.REMOVE_TAG(+) = '0'");
        sql.append(" AND (NVL(V.VIP_CLASS_ID, 'n') != '1' OR NVL(V.VIP_TYPE_CODE, 'n') != '0')");
        sql.append(" AND CG.MEMBER_CUST_ID(+) = U.CUST_ID");
        sql.append(" AND CG.REMOVE_TAG(+) = '0'");
        sql.append(" AND UU.RELATION_TYPE_CODE(+) = '20'");
        sql.append(" AND UU.END_DATE(+) > SYSDATE");
        sql.append(" AND UU.USER_ID_B(+) = U.USER_ID");
        sql.append(" AND UU.PARTITION_ID(+) = MOD(U.USER_ID, 10000)");
        sql.append(" AND U.USER_ID = :USER_ID");
        sql.append(" AND U.PARTITION_ID = MOD(:USER_ID, 10000)");
        sql.append(" AND (NVL(UC.CITY_CODE, U.CITY_CODE) = :AREA_CODE OR 'HAIN' = :AREA_CODE)");

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("AREA_CODE", area_code);
        return Dao.qryBySql(sql, param);

    }

    /**
     * @Function: getNpOutRefuseInfos
     * @Description: 用户携出申请历史查询 获取携出用户资料 系统直接拒绝的纪录
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-3-24 上午10:55:54 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-24 lijm3 v1.0.0 修改原因
     */
    public static IDataset getNpOutRefuseInfos(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(2000);
        sql.append(" SELECT U.SERIAL_NUMBER,U.USER_ID,");
        sql.append(" P.BRAND_CODE,");
        sql.append(" P.PRODUCT_ID,");
        sql.append(" TO_CHAR(U.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') OPEN_DATE,");
        sql.append(" NVL(UC.CITY_CODE, U.CITY_CODE) CITY_CODE,");
        sql.append(" C.CUST_NAME,");
        sql.append(" V.VIP_CLASS_ID,");
        sql.append(" V.VIP_TYPE_CODE,");
        sql.append("  V.CUST_MANAGER_ID,");
        sql.append(" '否' IS_SUCC,");
        sql.append("  U.CUST_ID USER_CUST_ID,");
        sql.append(" U.USER_ID USER_USER_ID,");
        sql.append(" CG.USER_ID CUST_USER_ID,");
        sql.append(" UU.USER_ID_A,");
        sql.append(" NULL APPLY_DATE");
        sql.append(" FROM ");
        sql.append(" TF_F_USER             U,");
        sql.append(" TF_F_USER_PRODUCT     P,");
        sql.append(" TF_F_CUSTOMER         C,");
        sql.append(" TF_F_USER_CITY        UC,");
        sql.append(" TF_F_CUST_VIP         V,");
        sql.append(" TF_F_CUST_GROUPMEMBER CG,");
        sql.append(" TF_F_RELATION_UU      UU");
        sql.append(" WHERE UC.USER_ID(+) = U.USER_ID");
        sql.append(" AND UC.END_DATE(+) > SYSDATE");
        sql.append(" AND P.USER_ID = U.USER_ID");
        sql.append(" AND P.PARTITION_ID = MOD(U.USER_ID, 10000)");
        sql.append(" AND P.MAIN_TAG = '1'");
        sql.append(" AND C.CUST_ID = U.CUST_ID");
        sql.append(" AND U.REMOVE_TAG = '0'");
        sql.append(" AND C.PARTITION_ID = MOD(U.CUST_ID, 10000)");
        sql.append(" AND V.CUST_ID(+) = U.CUST_ID");
        sql.append(" AND V.REMOVE_TAG(+) = '0'");
        sql.append(" AND CG.MEMBER_CUST_ID(+) = C.CUST_ID");
        sql.append(" AND CG.REMOVE_TAG(+) = '0'");
        sql.append(" AND UU.RELATION_TYPE_CODE(+) = '20'");
        sql.append(" AND UU.END_DATE(+) > SYSDATE");
        sql.append(" AND UU.USER_ID_B(+) = U.USER_ID");
        sql.append(" AND UU.PARTITION_ID(+) = MOD(U.USER_ID, 10000)");
        sql.append(" AND U.USER_ID = :USER_ID");
        sql.append(" AND U.PARTITION_ID = MOD(:USER_ID, 10000)");
        return Dao.qryBySql(sql, param);

    }

    /**
     * @Function: getNpOutSuccInfo
     * @Description: 用户携出申请历史查询 获取携出用户资料 携出生效成功列表
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-3-24 上午10:47:15 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-24 lijm3 v1.0.0 修改原因
     */
    public static IDataset getNpOutSuccInfo(String serial_number, String start_date, String end_date, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("Select u.Serial_Number, P.Brand_Code, P.Product_Id, To_Char(u.Open_Date, 'YYYY-MM-DD HH24:MI:SS') Open_Date, Nvl(Uc.City_Code, u.City_Code) City_Code, c.Cust_Name, Np.Np_Tag, Np.Port_In_Netid, ");
        parser.addSQL(" decode(np.np_tag,'3',null,TO_CHAR(np.APPLY_DATE, 'YYYY-MM-DD HH24:MI:SS')) APPLY_DATE,nvl(np.rsrv_str2,TO_CHAR(np.APPLY_DATE, 'YYYY-MM-DD HH24:MI:SS')) accept_date, '是' Is_Succ, v.Vip_Type_Code, v.Vip_Class_Id, v.Cust_Manager_Id,");
        parser.addSQL(" u.Cust_Id User_Cust_Id, u.User_Id User_User_Id, Cg.User_Id Cust_User_Id, Uu.User_Id_a ");
        parser.addSQL(" From Tf_f_User_Np Np, Tf_f_User u,TF_F_USER_PRODUCT P, Tf_f_Customer c, Tf_f_User_City Uc, Tf_f_Cust_Vip v, Tf_f_Cust_Groupmember Cg, Tf_f_Relation_Uu Uu");
        parser.addSQL(" Where Np.User_Id = u.User_Id And u.Partition_Id = Mod(Np.User_Id, 10000) AND P.USER_ID = U.USER_ID AND P.PARTITION_ID = MOD(U.USER_ID,10000) AND P.MAIN_TAG='1' And c.Cust_Id = u.Cust_Id ");
        parser.addSQL(" AND P.END_DATE = (SELECT MAX(END_DATE) FROM UCR_CRM1.TF_F_USER_PRODUCT T WHERE T.USER_ID = U.USER_ID AND T.PARTITION_ID = MOD(U.USER_ID, 10000)) ");
        parser.addSQL(" And c.Partition_Id = Mod(u.Cust_Id, 10000) And v.Cust_Id(+) = u.Cust_Id And v.Remove_Tag(+) = '0' ");
        parser.addSQL(" And Np.Np_Tag In ('3', '4', '5', '8')");
        parser.addSQL(" And Cg.Member_Cust_Id(+) = c.Cust_Id And Cg.Remove_Tag(+) = '0' And Uu.Relation_Type_Code(+) = '20'");
        parser.addSQL(" And Uu.End_Date(+) > Sysdate And Uu.User_Id_b(+) = Np.User_Id And Uu.Partition_Id(+) = Mod(Np.User_Id, 10000) And Uc.User_Id(+) = u.User_Id And Uc.End_Date(+) > Sysdate");
        parser.addSQL(" AND u.SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL(" AND Np.APPLY_DATE >= TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))");
        parser.addSQL(" AND Np.APPLY_DATE < TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1 ");
        parser.addSQL(" order by Np.APPLY_DATE desc");

        return Dao.qryByParse(parser, page);
    }

    /**
     * @Function: getNpOutInfos
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-4-3 下午3:18:32 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-3 lijm3 v1.0.0 修改原因
     */
    public static IDataset getNpOweFeeInfos(String serial_number, String start_date, String end_date, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("Select u.Serial_Number,u.USER_ID, P.Brand_Code, P.Product_Id, u.open_Date, Nvl(Uc.City_Code, u.City_Code) City_Code,  c.Cust_Name, Np.Np_Tag, Np.Port_In_Netid, ");
        parser.addSQL(" TO_CHAR(np.port_out_date, 'YYYY-MM-DD HH24:MI:SS') PORT_OUT_DATE, TO_CHAR(np.APPLY_DATE, 'YYYY-MM-DD HH24:MI:SS') APPLY_DATE, v.Vip_Type_Code, v.Vip_Class_Id, v.Cust_Manager_Id, ");
        parser.addSQL(" u.Cust_Id User_Cust_Id,Cg.User_Id Cust_User_Id, Uu.User_Id_a, u.remove_tag, Np.PORT_OUT_NETID ");
        parser.addSQL(" From Tf_f_User_Np  Np, Tf_f_User  u,TF_F_USER_PRODUCT P,Tf_f_User_City  Uc, Tf_f_Customer  c,   Tf_f_Cust_Vip  v, Tf_f_Cust_Groupmember Cg,  Tf_f_Relation_Uu  Uu ");
        parser.addSQL(" Where Np.User_Id = u.User_Id   And u.Partition_Id = Mod(Np.User_Id, 10000)   and Uc.User_Id(+) = u.User_Id   And Uc.End_Date(+) > Sysdate   And c.Cust_Id = u.Cust_Id ");
        parser.addSQL(" And c.Partition_Id = Mod(u.Cust_Id, 10000)   And v.Cust_Id(+) = u.Cust_Id   And v.Remove_Tag(+) = '0' ");
        parser.addSQL(" and Nvl(v.vip_class_id, 'n') = '1'   and Nvl(v.vip_type_code, 'n') = '0' ");
        parser.addSQL(" and P.USER_ID= U.USER_ID AND P.MAIN_TAG='1' ");
        parser.addSQL(" And Np.Np_Tag ='4'   And Cg.Member_Cust_Id(+) = u.Cust_Id   And Cg.Remove_Tag(+) = '0' ");
        parser.addSQL(" And Uu.Relation_Type_Code(+) = '20'   And Uu.End_Date(+) > Sysdate   And Uu.User_Id_b(+) = u.User_Id And Uu.Partition_Id(+) = Mod(u.User_Id, 10000) ");
        parser.addSQL(" AND np.apply_date >=TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))  ");
        parser.addSQL(" AND np.apply_date < TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1 ");
        parser.addSQL(" AND u.serial_number = :SERIAL_NUMBER");

        parser.addSQL(" order by np.apply_date desc ");

        return Dao.qryByParse(parser, page);
    }

    public static IDataset getNpOweFeeInfos(String serial_number, String start_date, String end_date, String area_code, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        param.put("AREA_CODE", area_code);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("Select u.Serial_Number, u.USER_ID,P.Brand_Code, P.Product_Id, u.open_Date, Nvl(Uc.City_Code, u.City_Code) City_Code,  c.Cust_Name, Np.Np_Tag, Np.Port_In_Netid, ");
        parser.addSQL(" TO_CHAR(np.port_out_date, 'YYYY-MM-DD HH24:MI:SS') PORT_OUT_DATE, TO_CHAR(np.APPLY_DATE, 'YYYY-MM-DD HH24:MI:SS') APPLY_DATE, v.Vip_Type_Code, v.Vip_Class_Id, v.Cust_Manager_Id, ");
        parser.addSQL(" u.Cust_Id User_Cust_Id,Cg.User_Id Cust_User_Id, Uu.User_Id_a, u.remove_tag, Np.PORT_OUT_NETID ");
        parser.addSQL(" From Tf_f_User_Np  Np, Tf_f_User  u,TF_F_USER_PRODUCT P,Tf_f_User_City  Uc, Tf_f_Customer  c,   Tf_f_Cust_Vip  v, Tf_f_Cust_Groupmember Cg,  Tf_f_Relation_Uu  Uu ");
        parser.addSQL(" Where Np.User_Id = u.User_Id   And u.Partition_Id = Mod(Np.User_Id, 10000)   and Uc.User_Id(+) = u.User_Id   And Uc.End_Date(+) > Sysdate   And c.Cust_Id = u.Cust_Id ");
        parser.addSQL(" and P.USER_ID= U.USER_ID AND P.MAIN_TAG='1' ");
        parser.addSQL(" And c.Partition_Id = Mod(u.Cust_Id, 10000)   And v.Cust_Id(+) = u.Cust_Id   And v.Remove_Tag(+) = '0' ");
        parser.addSQL(" And Np.Np_Tag ='4'   And Cg.Member_Cust_Id(+) = u.Cust_Id   And Cg.Remove_Tag(+) = '0' ");
        parser.addSQL(" And Uu.Relation_Type_Code(+) = '20'   And Uu.End_Date(+) > Sysdate   And Uu.User_Id_b(+) = u.User_Id And Uu.Partition_Id(+) = Mod(u.User_Id, 10000)  ");
        parser.addSQL(" AND np.apply_date >=TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))  ");
        parser.addSQL(" AND np.apply_date < TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1 ");
        parser.addSQL(" AND u.serial_number = :SERIAL_NUMBER");

        parser.addSQL(" AND (NVL(uc.city_code, u.city_code) = :AREA_CODE OR  'HAIN' = :AREA_CODE)");
        parser.addSQL(" order by np.apply_date desc ");

        return Dao.qryByParse(parser, page);
    }

    public static IDataset getNpReturnSoaInfos(String isReturn, String month, String userId, String start_date, String end_date, Pagination page) throws Exception
    {

        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT ");
        sql.append(" DECODE(SUBSTR(NS.RESULT_INFO, 1, 12),'APPEXCEPTION','系统错误',NS.RESULT_INFO) RESULT_MESSAGE,");
        sql.append(" TO_CHAR(NS.CREATE_TIME, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE,");
        sql.append(" DECODE(NS.RSRV_STR1, '', '09998980', NS.RSRV_STR1) PORT_IN_NETID,");
        sql.append(" NS.RSRV_STR4 NP_REASON,");
        sql.append(" NS.RSRV_STR5 REMARK1,");
        sql.append(" NS.RSRV_STR6 NP_MEASURE,");
        sql.append(" NS.RSRV_STR7 REMARK2,");
        sql.append(" NS.MESSAGE_ID,");
        sql.append(" NS.MONTH,");
        sql.append(" NS.RSRV_STR8 UPDATE_STAFF_ID,");
        sql.append(" NS.RSRV_STR9 HAND_IN_TIME,");
        sql.append(" NS.UPDATE_TIME UPDATE_TIME,");
        sql.append(" NS.USER_ID");
        sql.append(" FROM TL_B_NPTRADE_SOA      NS ");
        sql.append(" WHERE NS.COMMAND_CODE = 'APPLY_REQ'");
        sql.append(" AND NS.USER_ID = :USER_ID");
        sql.append(" AND NS.CREATE_TIME >= TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))");
        sql.append(" AND (NS.CREATE_TIME < TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1 OR :END_DATE IS NULL)");
        sql.append(" AND NS.MONTH = :MONTH");
        if ("未回访".equals(isReturn))
        {
            sql.append(" and nvl(NS.rsrv_str4,'@') = '@'");
        }
        else if ("已回访".equals(isReturn))
        {
            sql.append(" and nvl(NS.rsrv_str4,'@') != '@'");
        }
        sql.append(" ORDER BY NS.CREATE_TIME DESC");

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("END_DATE", end_date);
        param.put("START_DATE", start_date);
        param.put("MONTH", month);
        return Dao.qryBySql(sql, param, page, Route.CONN_UIF);
    }

    public static IDataset getNpReturnSoaInfosByNpCode(String isReturn, String month, String npCode, String start_date, String end_date, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("NP_CODE", npCode);
        param.put("END_DATE", end_date);
        param.put("START_DATE", start_date);
        param.put("MONTH", month);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT ");
        parser.addSQL(" DECODE(SUBSTR(NS.RESULT_INFO, 1, 12),'APPEXCEPTION','系统错误',NS.RESULT_INFO) RESULT_MESSAGE,");
        parser.addSQL(" TO_CHAR(NS.CREATE_TIME, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE,");
        parser.addSQL(" DECODE(NS.RSRV_STR1, '', '09998980', NS.RSRV_STR1) PORT_IN_NETID,");
        parser.addSQL(" NS.RSRV_STR4 NP_REASON,");
        parser.addSQL(" NS.RSRV_STR5 REMARK1,");
        parser.addSQL(" NS.RSRV_STR6 NP_MEASURE,");
        parser.addSQL(" NS.RSRV_STR7 REMARK2,");
        parser.addSQL(" NS.MESSAGE_ID,");
        parser.addSQL(" NS.MONTH,");
        parser.addSQL(" NS.RSRV_STR8 UPDATE_STAFF_ID,");
        parser.addSQL(" NS.RSRV_STR9 HAND_IN_TIME,");
        parser.addSQL(" NS.UPDATE_TIME UPDATE_TIME,");
        parser.addSQL(" NS.USER_ID");
        parser.addSQL(" FROM TL_B_NPTRADE_SOA      NS ");
        parser.addSQL(" WHERE NS.COMMAND_CODE = 'APPLY_REQ'");
        parser.addSQL(" AND NS.NP_CODE = :NP_CODE");
        parser.addSQL(" AND NS.USER_ID IS NOT NULL");
        parser.addSQL(" AND NS.CREATE_TIME >= TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))");
        parser.addSQL(" AND (NS.CREATE_TIME <  TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1 OR  :END_DATE IS NULL) ");
        parser.addSQL(" AND NS.MONTH = :MONTH");
        if ("未回访".equals(isReturn))
        {
            parser.addSQL(" and nvl(NS.rsrv_str4,'@') = '@'");
        }
        else if ("已回访".equals(isReturn))
        {
            parser.addSQL(" and nvl(NS.rsrv_str4,'@') != '@'");
        }
        parser.addSQL(" ORDER BY NS.CREATE_TIME DESC");

        return Dao.qryByParse(parser, Route.CONN_UIF);
    }

    public static IDataset getNpSoaByUserIdStartDateEndDate(String npCode, String startDate, String endDate, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("NP_CODE", npCode);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT DECODE(SUBSTR(NS.RESULT_INFO, 1, 12),'APPEXCEPTION','系统错误',NS.RESULT_INFO) RESULT_MESSAGE,NS.CREATE_TIME ACCEPT_DATE,");
        parser.addSQL(" DECODE(NS.RSRV_STR1, '', '09998980', NS.RSRV_STR1) PORT_IN_NETID,NP_CODE,USER_ID");
        parser.addSQL(" FROM TL_B_NPTRADE_SOA NS");
        parser.addSQL(" WHERE NS.COMMAND_CODE = 'APPLY_REQ'");
        parser.addSQL(" AND NS.STATE = '011'");
        parser.addSQL(" AND NS.NP_CODE = :NP_CODE");
        parser.addSQL(" AND NS.USER_ID IS NOT NULL");
        parser.addSQL(" AND NS.CREATE_TIME >=TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))");
        parser.addSQL(" AND NS.CREATE_TIME <TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1");
        parser.addSQL(" ORDER BY NS.CREATE_TIME DESC ");
        return Dao.qryByParse(parser, page, Route.CONN_UIF);
    }

    public static IDataset getNpSoasBySnUserId(String serialNumber, String userId) throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);
        sql.append("select a.np_code SERIAL_NUMBER,a.create_time ACCEPT_DATE,a.Rsrv_Str4 NP_REASON,a.Rsrv_Str5 REMARK1,a.Rsrv_Str6 NP_MEASURE,a.Rsrv_Str7 REMARK2,a.MESSAGE_ID,a.MONTH from  TL_B_NPTRADE_SOA a");
        sql.append(" where a.np_code=:SERIAL_NUMBER");
        sql.append(" And a.user_id=:USER_ID");
        sql.append(" and a.Command_Code = 'APPLY_REQ' order by a.create_time desc");
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryBySql(sql, param, Route.CONN_UIF);

    }

    public static IDataset getTradeNp(String flowId, String cancelTag, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("FLOW_ID", flowId);
        param.put("CANCEL_TAG", cancelTag);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_NP_BY_FLOWID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getTradeNpAllBySn(String sn) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_NPALL_VIEW_BYSN", param);
    }

    public static IDataset getTradeNpByCsms(String sn) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_BY_SN_STATE_BOOKSENDTIME_CSMS", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getTradeNpByFlowId(String flowId) throws Exception
    {
        IData param = new DataMap();
        param.put("FLOW_ID", flowId);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_TRADENP_BY_FID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
    public static IDataset getTradeNpByFlowId2(String flowId) throws Exception
    {
        IData param = new DataMap();
        param.put("FLOW_ID", flowId);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_TRADENP_BY_FID_2", param,Route.getJourDb());
    }
    public static IDataset queryNpApplyInBySN(String messageId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", messageId);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_NPAPPLYIIN_BY_SN", param,Route.getJourDb());
    }
    public static IDataset getTradeNpBySelByAcceptDate(String rsrv_num1, String cancel_tag, String state, String trade_type_code, String araeCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_NUM1", rsrv_num1);
        param.put("CANCEL_TAG", cancel_tag);
        param.put("STATE", state);
        param.put("TRADE_TYPE_CODE", trade_type_code);
        param.put("AREA_CODE", araeCode);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_BY_ACCEPTDATE", param, page, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
    
    
    public static IDataset getTradeNpBySelByAcceptDate2(String user_id, String area_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("AREA_CODE", area_code);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_BY_ACCEPTDATE2", param);
    }

    public static IDataset getTradeNpBySelTradenpByIdd(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_TRADENP_BY_ID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getTradeNpBySnTradeTypeCode(String serialNumber, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_TRADENP_BY_NP_TRADETYPECODE", param,Route.getJourDb(BizRoute.getRouteId()));//modify by duhj 2017/4/19 修改接口报错
    }

    public static IDataset getTradeNpByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_BY_TRADE_ID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getTradeNpByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_NP", "SEL_BY_ID", param);
    }

    public static IDataset getTradeNpByUserIdTradeTypeCodeCancelTag(String userId, String tradeTypeCode, String cancelTag) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("CANCEL_TAG", cancelTag);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_BY_NPSTOP", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static int getTradeNpCountByTradeTypeCode(String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        IDataset ids = Dao.qryByCode("TF_B_TRADE_NP", "SEL_BY_TRADETYPECODE", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        if (IDataUtil.isNotEmpty(ids))
        {
            return ids.getData(0).getInt("NUM");
        }
        else
        {
            return 0;
        }
    }

    public static IDataset getTradeNpInfos(String tradeId, String acceptMonth) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", acceptMonth);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_TRADENP_BY_TID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getTradeNpsByMsgId(String msgId) throws Exception
    {
        IData param = new DataMap();
        param.put("MESSAGE_ID", msgId);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_TRADENP_BY_MID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getTradeNpsByRsrvstr5(String serialNumber, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_BY_RSRVSTR5", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getTradeNpsByUserIdTradeTypeCodeCancelTag(String userId, String tradeTypeCode, String cancelTag) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("CANCEL_TAG", cancelTag);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_BY_USER_B", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getUserAverageFeeByUserId(String userId, String months_num) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("MONTHS_NUM", months_num);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_USER_AVGPAYFEE_NG", param);
    }

    /**
     * @Function: getUserIntf
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-4-3 下午1:32:45 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-3 lijm3 v1.0.0 修改原因
     */
    public static IDataset getUserIntf(String userId) throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT to_char(user_id) user_id,TRADE_ATTR,RSRV_NUM1,RSRV_NUM2, RSRV_NUM3 ");
        sql.append(" FROM TF_F_USER_INTF ");
        sql.append(" Where 1=1 ");
        sql.append(" and user_id = TO_NUMBER(:USER_ID)   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)");
        sql.append(" and TRADE_ATTR = :TRADE_ATTR");

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TRADE_ATTR", "2");// 1:携号转网预警信息 2：用户签约信息
        return Dao.qryBySql(sql, param);

    }

    public static IDataset getUserNpBySn(String sn) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        return Dao.qryByCode("TF_F_USER_NP", "SEL_BY_SN", param);
    }

    public static IDataset getUserNpByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_NP", "SEL_BY_ID", param);
    }

    /**
     * 查询用户当前携转信息（从全国信息库）
     * 
     * @param serialNumber
     * @return IDataset
     * @throws Exception
     * @author zhouwu
     * @date 2014-05-21 10:15:11
     */
    public static IDataset getUserNpInfoAll(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        IDataset out = Dao.qryByCode("TF_B_TRADE_NP", "SEL_EFFECT_NPALL_BYSN", param);

        return out;
    }

    public static IDataset getValidTdNpsBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("MSISDN", serialNumber);

        return Dao.qryByCode("TD_NP", "SEL_VALID_BY_MISIDN", param);
    }

    public static IDataset getValidTradeNpBySn(String sn) throws Exception
    {   

        if (sn != null && sn.length()>4){
        	//号码段缓存
            if(log.isDebugEnabled()) log.debug("进入号码段缓存查询====");
             
            IReadOnlyCache cache = CacheFactory.getReadOnlyCache(MsisdnCache.class);
            String begin_msisdn =sn.substring(0,sn.length()-4)+"0000";
            
            if(log.isDebugEnabled()) log.debug("号码段缓存:begin_msisdn="+begin_msisdn);
            
            Object rf = cache.get(begin_msisdn);

            
            if( rf !=null){
            	if(log.isDebugEnabled()) log.debug("号码段缓存:缓存="+((IDataset) rf).toString());	
            	return (IDataset) rf;
            }
                    
        }
        if(log.isDebugEnabled()) log.debug("号码段缓存:从数据库取");	
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        return Dao.qryByCode("TF_B_TRADE_NP", "SEL_MSISDN_BYSN_SPEC", param, Route.CONN_CRM_CEN);
    }

	public static IDataset getBrandcode(String serialNumber) throws Exception
    {
    	  
    	IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		String sql= " select t.brand_code from TF_F_USER_PRODUCT t , tf_f_user u where u.user_id = t.user_id and  u.serial_number= :SERIAL_NUMBER" ; 
      
		IDataset ids = Dao.qryBySql(new StringBuilder(sql), param);
		
		return ids;
		
    }

    public static IDataset geytNpUserTradeInfos(String serial_number, String start_date, String end_date, String cancel_tag, String trade_type_code, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        param.put("CANCEL_TAG", cancel_tag);
        param.put("TRADE_TYPE_CODE", trade_type_code);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("Select To_Char(T.Trade_Id) Trade_Id, To_Char(T.User_Id) User_Id, T.Trade_Type_Code, T.Np_Service_Type, T.Serial_Number, ");
        parser.addSQL("	T.Flow_Id, T.Message_Id, T.Brc_Id, T.Msg_Cmd_Code, T.Md5, T.Port_Out_Netid, T.Port_In_Netid, T.Home_Netid,");
        parser.addSQL("	T.b_Np_Card_Type, T.a_Np_Card_Type, T.Cust_Name, T.Cred_Type, T.Pspt_Id, T.Phone, T.Actor_Cust_Name,");
        parser.addSQL("	T.Actor_Cred_Type, T.Actor_Pspt_Id, To_Char(T.Accept_Date, 'YYYY-MM-DD HH24:MI:SS') Accept_Date,");
        parser.addSQL("	To_Char(T.Np_Start_Date, 'YYYY-MM-DD HH24:MI:SS') Np_Start_Date,");
        parser.addSQL("	To_Char(T.Create_Time, 'YYYY-MM-DD HH24:MI:SS') Create_Time,");
        parser.addSQL("	To_Char(T.Book_Send_Time, 'YYYY-MM-DD HH24:MI:SS') Book_Send_Time, T.Send_Times, T.Result_Code, T.Result_Message,");
        parser.addSQL("	T.Error_Message, T.Cancel_Tag, T.State, T.Remark, T.Rsrv_Str1, T.Rsrv_Str2, T.Rsrv_Str3, T.Rsrv_Str4, T.Rsrv_Str5,");
        parser.addSQL("	Decode(T.Rsrv_Str1, '1', '已处理', '未处理') Dealtag,");
        parser.addSQL("	Decode(T.State, '000', '请求等待发送', '009', '请求正在发送', '011', '请求发送失败', '020', '响应结果-成功', '021', '响应结果-失败', '030','告知结果-成功', '031', '告知结果-失败', '040', '生效请求等待发送', '041', '生效请求发送失败', '050', '生效响应结果-成功', '051', '生效响应结果-失败', '060', '生效告知结果-成功', '061', '生效告知结果-失败', '100', '请求/指示正确接受', '130', '告知结果-成功', '131', '告知结果-失败', '未知') State_Name ");
        parser.addSQL("From Tf_b_Trade_Np T WHERE 1=1");
        parser.addSQL("	AND T.SERIAL_NUMBER=:SERIAL_NUMBER ");
        parser.addSQL("	AND T.ACCEPT_DATE>=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        parser.addSQL("	AND T.ACCEPT_DATE<=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        parser.addSQL("	AND T.CANCEL_TAG=:CANCEL_TAG ");
        parser.addSQL(" AND T.TRADE_TYPE_CODE=:TRADE_TYPE_CODE ");

        IDataset results=  Dao.qryByParse(parser, page, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        return results;
    }
    
    
    public static IDataset queryStateByUserid(String userid, Pagination page) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", userid);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("Select CG.MEMBER_KIND,CG.GROUP_ID,CG.GROUP_CUST_NAME, ");
		parser.addSQL("	U.Cust_Id User_Cust_Id, CG.User_Id Cust_User_Id, UU.User_Id_a ");
		parser.addSQL("From Tf_f_User U,Tf_f_Cust_Groupmember CG,Tf_f_Relation_Uu UU WHERE CG.REMOVE_TAG(+) = '0'");
		parser.addSQL("	AND CG.MEMBER_CUST_ID(+) = U.CUST_ID ");
		parser.addSQL("	AND CG.PARTITION_ID(+) = MOD(U.USER_ID, 10000) ");
		parser.addSQL("	AND UU.END_DATE(+) > SYSDATE ");
		parser.addSQL("	AND U.USER_ID=:USER_ID ");
		parser.addSQL("	AND U.PARTITION_ID = MOD (:USER_ID, 10000) ");
		parser.addSQL("	AND UU.USER_ID_B(+) = :USER_ID ");
		parser.addSQL("	AND UU.PARTITION_ID(+) = MOD(:USER_ID, 10000) ");
		parser.addSQL("	AND UU.RELATION_TYPE_CODE(+) = '20' ");
        
        IDataset results=  Dao.qryByParse(parser, page);
        
        return results;
    }

    public static IDataset queryGroupInfoByUseridCustid(String usercustid, String custuserid) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_CUST_ID", usercustid);
        param.put("CUST_USER_ID", custuserid);
        StringBuilder sql = new StringBuilder(1000);
        sql.append("Select c.GROUP_ID, c.CUST_NAME, c.CUST_MANAGER_ID, d.STAFF_NAME, d.SERIAL_NUMBER, c.CITY_CODE, e.AREA_NAME, a.CUST_ID, a.MEMBER_KIND, b.DATA_NAME ");
        sql.append("From Tf_f_Cust_Groupmember a, Td_s_Static b, Tf_f_Cust_Group c, Td_m_Staff d, Td_m_Area e ");
        sql.append(" Where");
        sql.append(" a.Member_Cust_Id = :USER_CUST_ID And a.Partition_Id = Mod(:CUST_USER_ID, 10000)");
        sql.append(" and a.Remove_Tag = '0' and b.Type_Id = 'GMB_MEMBERKIND' and a.Member_Kind = b.Data_Id And a.Cust_Id = c.Cust_Id And c.Cust_Manager_Id = d.Staff_Id And c.City_Code = e.Area_Code");
        return Dao.qryBySql(sql, param);
    }

    public static IDataset queryVPMNInfoByUserid(String userida, String useridb) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", userida);
        param.put("USER_ID_B", useridb);
        param.put("PUSER_ID_B", useridb);
        StringBuilder sql = new StringBuilder(1000);
        sql.append("Select a.USER_ID, a.VPN_NAME, a.VPN_NO, a.CUST_MANAGER, b.STAFF_ID, b.STAFF_NAME, b.SERIAL_NUMBER ");
        sql.append(" From Tf_f_User_Vpn a, Td_m_Staff b, Tf_f_Relation_Uu c");
        sql.append(" Where ");
        sql.append("  c.User_Id_a =:USER_ID_A ");
        sql.append(" and c.User_Id_b =:USER_ID_B And c.Partition_Id = Mod(:PUSER_ID_B, 10000)");
        sql.append(" And a.User_Id = c.User_Id_a And a.Partition_Id = Mod(c.User_Id_a, 10000) And c.Relation_Type_Code = '20' And c.End_Date > Sysdate And a.Cust_Manager = b.Staff_Id");
        return Dao.qryBySql(sql, param);
    }
    
    
    public static IDataset getReturnVisitForCrm(String serial_number,String start_date,String end_date,String area_code)throws Exception{
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        param.put("AREA_CODE", area_code);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT U.USER_ID, NP.FLOW_ID,");
        parser.addSQL("        U.SERIAL_NUMBER, ");
        parser.addSQL("        TO_CHAR(U.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') OPEN_DATE, ");
        parser.addSQL("        NVL(UC.CITY_CODE, U.CITY_CODE) CITY_CODE, ");
        parser.addSQL("        C.CUST_NAME, P.BRAND_CODE,P.PRODUCT_ID, ");
        parser.addSQL("        V.VIP_CLASS_ID, ");
        parser.addSQL("        V.VIP_TYPE_CODE, ");
        parser.addSQL("        V.CUST_MANAGER_ID, ");
        parser.addSQL("        DECODE(U.USER_TAG_SET, '4', '是', '5', '是', '8', '是', '否') IS_SUCC, ");
        parser.addSQL("        U.CUST_ID USER_CUST_ID, ");
        parser.addSQL("        CG.USER_ID CUST_USER_ID, ");
        parser.addSQL("        UU.USER_ID_A, ");
        parser.addSQL("        DECODE(U.REMOVE_TAG, ");
        parser.addSQL("               '7', ");
        parser.addSQL("               TO_CHAR(U.DESTROY_TIME, 'YYYY-MM-DD HH24:MI:SS'), ");
        parser.addSQL("               NULL) APPLY_DATE ");
        parser.addSQL("   FROM TF_B_TRADE_NP         NP, ");
        parser.addSQL("        TF_F_USER_PRODUCT     P, ");
        parser.addSQL("        TF_F_USER             U, ");
        parser.addSQL("        TF_F_CUSTOMER         C, ");
        parser.addSQL("        TF_F_USER_CITY        UC, ");
        parser.addSQL("        TF_F_CUST_VIP         V, ");
        parser.addSQL("        TF_F_CUST_GROUPMEMBER CG, ");
        parser.addSQL("        TF_F_RELATION_UU      UU ");
        parser.addSQL("  WHERE NP.TRADE_TYPE_CODE = 41 ");
        parser.addSQL("    AND UC.USER_ID(+) = U.USER_ID ");
        parser.addSQL("    AND UC.END_DATE(+) > SYSDATE ");
        parser.addSQL("    AND C.CUST_ID = U.CUST_ID ");
        parser.addSQL("    AND C.PARTITION_ID = MOD(U.CUST_ID, 10000) ");
        parser.addSQL("    AND U.USER_ID = NP.USER_ID ");
        parser.addSQL("    AND U.PARTITION_ID = MOD(NP.USER_ID, 10000) ");
        parser.addSQL("    AND P.USER_ID = NP.USER_ID ");
        parser.addSQL("    AND P.PARTITION_ID = MOD(NP.USER_ID, 10000) ");
        parser.addSQL("    AND P.MAIN_TAG = '1' ");
        parser.addSQL("    AND V.CUST_ID(+) = U.CUST_ID ");
        parser.addSQL("    AND V.REMOVE_TAG(+) = '0' ");
        parser.addSQL("    AND (NVL(V.VIP_CLASS_ID, 'n') != '1' OR NVL(V.VIP_TYPE_CODE, 'n') != '0') ");
        parser.addSQL("    AND CG.MEMBER_CUST_ID(+) = U.CUST_ID ");
        parser.addSQL("    AND CG.PARTITION_ID(+) = MOD(U.USER_ID, 10000) ");
        parser.addSQL("    AND CG.REMOVE_TAG(+) = '0' ");
        parser.addSQL("    AND UU.RELATION_TYPE_CODE(+) = '20' ");
        parser.addSQL("    AND UU.END_DATE(+) > SYSDATE ");
        parser.addSQL("    AND UU.USER_ID_B(+) = U.USER_ID ");
        parser.addSQL("    AND UU.PARTITION_ID(+) = MOD(U.USER_ID, 10000) ");
        parser.addSQL("    AND NP.ACCEPT_DATE >= ");
        parser.addSQL("        TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')) ");
        parser.addSQL("    AND (NP.ACCEPT_DATE < ");
        parser.addSQL("        TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1 OR ");
        parser.addSQL("        :END_DATE IS NULL) ");
        parser.addSQL("    AND (NVL(UC.CITY_CODE, U.CITY_CODE) = :AREA_CODE OR  'HAIN' = :AREA_CODE) ");
        parser.addSQL("    AND NP.SERIAL_NUMBER = :SERIAL_NUMBER ");
       return Dao.qryByParse(parser);
    }
    
    
    public static IDataset getReturnVisitForCrm(String serial_number,String start_date,String end_date)throws Exception{
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT U.USER_ID, NP.FLOW_ID");
        parser.addSQL("        U.SERIAL_NUMBER, ");
        parser.addSQL("        TO_CHAR(U.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') OPEN_DATE, ");
        parser.addSQL("        NVL(UC.CITY_CODE, U.CITY_CODE) CITY_CODE, ");
        parser.addSQL("        C.CUST_NAME, P.BRAND_CODE,P.PRODUCT_ID");
        parser.addSQL("        V.VIP_CLASS_ID, ");
        parser.addSQL("        V.VIP_TYPE_CODE, ");
        parser.addSQL("        V.CUST_MANAGER_ID, ");
        parser.addSQL("        DECODE(U.USER_TAG_SET, '4', '是', '5', '是', '8', '是', '否') IS_SUCC, ");
        parser.addSQL("        U.CUST_ID USER_CUST_ID, ");
        parser.addSQL("        CG.USER_ID CUST_USER_ID, ");
        parser.addSQL("        UU.USER_ID_A, ");
        parser.addSQL("        DECODE(U.REMOVE_TAG, ");
        parser.addSQL("               '7', ");
        parser.addSQL("               TO_CHAR(U.DESTROY_TIME, 'YYYY-MM-DD HH24:MI:SS'), ");
        parser.addSQL("               NULL) APPLY_DATE ");
        parser.addSQL("   FROM TF_B_TRADE_NP         NP, ");
        parser.addSQL("        TF_F_USER_PRODUCT     P, ");
        parser.addSQL("        TF_F_USER             U, ");
        parser.addSQL("        TF_F_CUSTOMER         C, ");
        parser.addSQL("        TF_F_USER_CITY        UC, ");
        parser.addSQL("        TF_F_CUST_VIP         V, ");
        parser.addSQL("        TF_F_CUST_GROUPMEMBER CG, ");
        parser.addSQL("        TF_F_RELATION_UU      UU ");
        parser.addSQL("  WHERE NP.TRADE_TYPE_CODE = 41 ");
        parser.addSQL("    AND UC.USER_ID(+) = U.USER_ID ");
        parser.addSQL("    AND UC.END_DATE(+) > SYSDATE ");
        parser.addSQL("    AND C.CUST_ID = U.CUST_ID ");
        parser.addSQL("    AND C.PARTITION_ID = MOD(U.CUST_ID, 10000) ");
        parser.addSQL("    AND U.USER_ID = NP.USER_ID ");
        parser.addSQL("    AND U.PARTITION_ID = MOD(NP.USER_ID, 10000) ");
        parser.addSQL("    AND P.USER_ID = NP.USER_ID ");
        parser.addSQL("    AND P.PARTITION_ID = MOD(NP.USER_ID, 10000) ");
        parser.addSQL("    AND P.MAIN_TAG = '1' ");
        parser.addSQL("    AND V.CUST_ID(+) = U.CUST_ID ");
        parser.addSQL("    AND V.REMOVE_TAG(+) = '0' ");
        parser.addSQL("    AND (NVL(V.VIP_CLASS_ID, 'n') != '1' OR NVL(V.VIP_TYPE_CODE, 'n') != '0') ");
        parser.addSQL("    AND CG.MEMBER_CUST_ID(+) = U.CUST_ID ");
        parser.addSQL("    AND CG.PARTITION_ID(+) = MOD(U.USER_ID, 10000) ");
        parser.addSQL("    AND CG.REMOVE_TAG(+) = '0' ");
        parser.addSQL("    AND UU.RELATION_TYPE_CODE(+) = '20' ");
        parser.addSQL("    AND UU.END_DATE(+) > SYSDATE ");
        parser.addSQL("    AND UU.USER_ID_B(+) = U.USER_ID ");
        parser.addSQL("    AND UU.PARTITION_ID(+) = MOD(U.USER_ID, 10000) ");
        parser.addSQL("    AND NP.ACCEPT_DATE >= ");
        parser.addSQL("        TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')) ");
        parser.addSQL("    AND (NP.ACCEPT_DATE < ");
        parser.addSQL("        TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1 OR ");
        parser.addSQL("        :END_DATE IS NULL) ");
        parser.addSQL("    AND NP.SERIAL_NUMBER = :SERIAL_NUMBER ");
       return Dao.qryByParse(parser);
    }
    
    public static IDataset getReturnVisitForUif(String isReturn,String flowId)throws Exception{
        
        
        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT DECODE(SUBSTR(NS.RESULT_INFO, 1, 12), ");
        sql.append("               'APPEXCEPTION', ");
        sql.append("               '系统错误', ");
        sql.append("               NS.RESULT_INFO) RESULT_MESSAGE, ");
        sql.append("        TO_CHAR(NS.CREATE_TIME, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, ");
        sql.append("        DECODE(NS.RSRV_STR1, '', '09998980', NS.RSRV_STR1) PORT_IN_NETID, ");
        sql.append("        NS.RSRV_STR4 NP_REASON, ");
        sql.append("        NS.RSRV_STR5 REMARK1, ");
        sql.append("        NS.RSRV_STR6 NP_MEASURE, ");
        sql.append("        NS.RSRV_STR7 REMARK2, ");
        sql.append("        NS.MESSAGE_ID, ");
        sql.append("        NS.MONTH, ");
        sql.append("        NS.RSRV_STR8 UPDATE_STAFF_ID, ");
        sql.append("        NS.RSRV_STR9 HAND_IN_TIME, ");
        sql.append("        NS.UPDATE_TIME UPDATE_TIME ");
        sql.append("   FROM TL_B_NPTRADE_SOA NS ");
        sql.append("  WHERE NS.COMMAND_CODE = 'APPLY_REQ' ");
        sql.append("    AND NS.FLOW_ID = :FLOW_ID ");
        if(isReturn.equals("未回访"))
        {
            sql.append(" and nvl(ns.rsrv_str4,'@') = '@'");
        }
        else if(isReturn.equals("已回访"))
        {
            sql.append(" and nvl(ns.rsrv_str4,'@') != '@'");
        }
        
        IData param = new DataMap();
        param.put("FLOW_ID", flowId);
        return Dao.qryBySql(sql, param, Route.CONN_UIF);
    }
    
    public static IDataset getNpReturnSoaInfosForUif(String isReturn, String npCode, String start_date, String end_date, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("END_DATE", end_date);
        param.put("START_DATE", start_date);
        param.put("NP_CODE", npCode);
        SQLParser parser = new SQLParser(param);
        
        parser.addSQL(" SELECT ");
        parser.addSQL(" DECODE(SUBSTR(NS.RESULT_INFO, 1, 12),'APPEXCEPTION','系统错误',NS.RESULT_INFO) RESULT_MESSAGE,");
        parser.addSQL(" TO_CHAR(NS.CREATE_TIME, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE,");
        parser.addSQL(" DECODE(NS.RSRV_STR1, '', '09998980', NS.RSRV_STR1) PORT_IN_NETID,");
        parser.addSQL(" NS.RSRV_STR4 NP_REASON,");
        parser.addSQL(" NS.RSRV_STR5 REMARK1,");
        parser.addSQL(" NS.RSRV_STR6 NP_MEASURE,");
        parser.addSQL(" NS.RSRV_STR7 REMARK2,");
        parser.addSQL(" NS.MESSAGE_ID,");
        parser.addSQL(" NS.MONTH,");
        parser.addSQL(" NS.RSRV_STR8 UPDATE_STAFF_ID,");
        parser.addSQL(" NS.RSRV_STR9 HAND_IN_TIME,");
        parser.addSQL(" NS.UPDATE_TIME UPDATE_TIME,");
        parser.addSQL(" NS.USER_ID, ");
        parser.addSQL(" NS.NP_CODE ");
        parser.addSQL(" FROM TL_B_NPTRADE_SOA      NS ");
        parser.addSQL(" WHERE NS.COMMAND_CODE = 'APPLY_REQ'");
        parser.addSQL(" AND NS.NP_CODE = :NP_CODE");
        parser.addSQL(" AND NS.CREATE_TIME >= TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))");
        parser.addSQL(" AND (NS.CREATE_TIME < TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1 OR :END_DATE IS NULL)");
        if ("未回访".equals(isReturn))
        {
            parser.addSQL(" and nvl(NS.rsrv_str4,'@') = '@'");
        }
        else if ("已回访".equals(isReturn))
        {
            parser.addSQL(" and nvl(NS.rsrv_str4,'@') != '@'");
        }
        parser.addSQL(" ORDER BY NS.CREATE_TIME DESC");

        return Dao.qryByParse(parser,Route.CONN_UIF);
    }
    
    
    public static IDataset getNpOutInfosBySn(String sn) throws Exception
    {
        StringBuilder sql = new StringBuilder(2000);

        sql.append(" SELECT U.USER_ID,");
        sql.append("  U.SERIAL_NUMBER,");
        sql.append(" P.BRAND_CODE,");
        sql.append(" P.PRODUCT_ID,");
        sql.append(" TO_CHAR(U.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') OPEN_DATE,");
        sql.append("  NVL(UC.CITY_CODE, U.CITY_CODE) CITY_CODE,");
        sql.append("  C.CUST_NAME,");
        sql.append("  V.VIP_CLASS_ID,");
        sql.append(" V.VIP_TYPE_CODE,");
        sql.append("  V.CUST_MANAGER_ID,");
        sql.append(" DECODE(U.USER_TAG_SET, '4', '是', '5', '是', '8', '是', '否') IS_SUCC,");
        sql.append(" U.CUST_ID USER_CUST_ID,");
        sql.append(" CG.USER_ID CUST_USER_ID,");
        sql.append(" UU.USER_ID_A,");
        sql.append(" DECODE(U.REMOVE_TAG, '7',TO_CHAR(U.DESTROY_TIME, 'YYYY-MM-DD HH24:MI:SS'),NULL) APPLY_DATE,");
        sql.append(" CG.MEMBER_KIND,");
        sql.append(" CG.GROUP_ID,");
        sql.append(" CG.GROUP_CUST_NAME");
        sql.append(" FROM TF_F_USER             U,");
        sql.append(" TF_F_USER_PRODUCT     P,");
        sql.append(" TF_F_CUSTOMER         C,");
        sql.append(" TF_F_USER_CITY        UC,");
        sql.append(" TF_F_CUST_VIP         V,");
        sql.append(" TF_F_CUST_GROUPMEMBER CG,");
        sql.append(" TF_F_RELATION_UU      UU");
        sql.append(" WHERE UC.USER_ID(+) = U.USER_ID");
        sql.append(" AND UC.END_DATE(+) > SYSDATE");
        sql.append(" AND C.CUST_ID = U.CUST_ID");
        sql.append(" AND C.PARTITION_ID = MOD(U.CUST_ID, 10000)");
        sql.append(" AND P.USER_ID = U.USER_ID");
        sql.append(" AND P.PARTITION_ID = MOD(U.USER_ID, 10000)");
        sql.append(" AND P.MAIN_TAG = '1'");
        sql.append(" AND V.CUST_ID(+) = U.CUST_ID");
        sql.append(" AND V.REMOVE_TAG(+) = '0'");
        sql.append(" AND (NVL(V.VIP_CLASS_ID, 'n') != '1' OR NVL(V.VIP_TYPE_CODE, 'n') != '0')");
        sql.append(" AND CG.MEMBER_CUST_ID(+) = U.CUST_ID");
        sql.append(" AND CG.REMOVE_TAG(+) = '0'");
        sql.append(" AND UU.RELATION_TYPE_CODE(+) = '20'");
        sql.append(" AND UU.END_DATE(+) > SYSDATE");
        sql.append(" AND UU.USER_ID_B(+) = U.USER_ID");
        sql.append(" AND UU.PARTITION_ID(+) = MOD(U.USER_ID, 10000)");
        sql.append(" AND U.SERIAL_NUMBER = :SERIAL_NUMBER");
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        return Dao.qryBySql(sql, param);
    }
    
    
    public static IDataset getNpOutInfosBySn(String sn, String area_code) throws Exception
    {
        StringBuilder sql = new StringBuilder(2000);

        sql.append(" SELECT U.USER_ID,");
        sql.append("  U.SERIAL_NUMBER,");
        sql.append(" P.BRAND_CODE,");
        sql.append(" P.PRODUCT_ID,");
        sql.append(" TO_CHAR(U.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') OPEN_DATE,");
        sql.append("  NVL(UC.CITY_CODE, U.CITY_CODE) CITY_CODE,");
        sql.append("  C.CUST_NAME,");
        sql.append("  V.VIP_CLASS_ID,");
        sql.append(" V.VIP_TYPE_CODE,");
        sql.append("  V.CUST_MANAGER_ID,");
        sql.append(" DECODE(U.USER_TAG_SET, '4', '是', '5', '是', '8', '是', '否') IS_SUCC,");
        sql.append(" U.CUST_ID USER_CUST_ID,");
        sql.append(" CG.USER_ID CUST_USER_ID,");
        sql.append(" UU.USER_ID_A,");
        sql.append(" DECODE(U.REMOVE_TAG, '7',TO_CHAR(U.DESTROY_TIME, 'YYYY-MM-DD HH24:MI:SS'),NULL) APPLY_DATE,");
        sql.append(" CG.MEMBER_KIND,");
        sql.append(" CG.GROUP_ID,");
        sql.append(" CG.GROUP_CUST_NAME");
        sql.append(" FROM TF_F_USER             U,");
        sql.append(" TF_F_USER_PRODUCT     P,");
        sql.append(" TF_F_CUSTOMER         C,");
        sql.append(" TF_F_USER_CITY        UC,");
        sql.append(" TF_F_CUST_VIP         V,");
        sql.append(" TF_F_CUST_GROUPMEMBER CG,");
        sql.append(" TF_F_RELATION_UU      UU");
        sql.append(" WHERE UC.USER_ID(+) = U.USER_ID");
        sql.append(" AND UC.END_DATE(+) > SYSDATE");
        sql.append(" AND C.CUST_ID = U.CUST_ID");
        sql.append(" AND C.PARTITION_ID = MOD(U.CUST_ID, 10000)");
        sql.append(" AND P.USER_ID = U.USER_ID");
        sql.append(" AND P.PARTITION_ID = MOD(U.USER_ID, 10000)");
        sql.append(" AND P.MAIN_TAG = '1'");
        sql.append(" AND V.CUST_ID(+) = U.CUST_ID");
        sql.append(" AND V.REMOVE_TAG(+) = '0'");
        sql.append(" AND (NVL(V.VIP_CLASS_ID, 'n') != '1' OR NVL(V.VIP_TYPE_CODE, 'n') != '0')");
        sql.append(" AND CG.MEMBER_CUST_ID(+) = U.CUST_ID");
        sql.append(" AND CG.REMOVE_TAG(+) = '0'");
        sql.append(" AND UU.RELATION_TYPE_CODE(+) = '20'");
        sql.append(" AND UU.END_DATE(+) > SYSDATE");
        sql.append(" AND UU.USER_ID_B(+) = U.USER_ID");
        sql.append(" AND UU.PARTITION_ID(+) = MOD(U.USER_ID, 10000)");
        sql.append(" AND U.SERIAL_NUMBER = :SERIAL_NUMBER");
        sql.append(" AND (NVL(UC.CITY_CODE, U.CITY_CODE) = :AREA_CODE OR 'HAIN' = :AREA_CODE)");

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        param.put("AREA_CODE", area_code);
        return Dao.qryBySql(sql, param);

    }
    /** 查询用户的TradeFee */
    public static IDataset queryNpSoaLog(String npCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("NP_CODE", npCode);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select a.result_info from TL_B_NPTRADE_SOA a ");
        parser.addSQL(" where a.command_code='APPLY_REQ' and a.message_type='H' and a.np_code = :NP_CODE and 1=2");
        parser.addSQL(" order by a.update_time desc");
        return Dao.qryByParse(parser, page, Route.CONN_UIF);
    }
    
    /**
     * by duhj 
     * 2017/4/19
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserIDNPByTradeid(String tradeId) throws Exception
    {

        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        StringBuilder sql = new StringBuilder(1000);
        sql.append("select user_id from TF_B_TRADE_NP where trade_id=:TRADE_ID ");
        return Dao.qryBySql(sql, param,Route.getJourDb(BizRoute.getRouteId()));
    }
    /**
	 * 根据受理时间查询携出工单
	 * @param startDate
	 * @param endDate
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset getNpOutTrade(String startDate, String endDate, String serialNumber, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("END_DATE", endDate);
        param.put("START_DATE", startDate);
        param.put("SERIAL_NUMBER", serialNumber);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT TO_CHAR(A.TRADE_ID) TRADE_ID, ");
        parser.addSQL(" TO_CHAR(A.USER_ID) USER_ID, ");
        parser.addSQL(" A.TRADE_TYPE_CODE, ");
        parser.addSQL(" A.NP_SERVICE_TYPE, ");
        parser.addSQL(" A.SERIAL_NUMBER, ");
        parser.addSQL("  A.FLOW_ID, ");
        parser.addSQL("  A.MESSAGE_ID, ");
        parser.addSQL(" A.BRC_ID, ");
        parser.addSQL(" A.MSG_CMD_CODE, ");
        parser.addSQL(" A.MD5, ");
        parser.addSQL(" A.PORT_OUT_NETID, ");
        parser.addSQL(" A.PORT_IN_NETID, ");
        parser.addSQL(" DECODE(SUBSTR(A.PORT_IN_NETID,0,3), '001', '电信', '002','移动','003','联通') PORT_IN_NETNAME, ");
        parser.addSQL(" A.HOME_NETID, ");
        parser.addSQL(" A.B_NP_CARD_TYPE, ");
        parser.addSQL(" A.A_NP_CARD_TYPE, ");
        parser.addSQL(" A.CUST_NAME, ");
        parser.addSQL(" A.CRED_TYPE, ");
        parser.addSQL(" A.PSPT_ID, ");
        parser.addSQL(" A.PHONE, ");
        parser.addSQL(" A.ACTOR_CUST_NAME, ");
        parser.addSQL(" A.ACTOR_CRED_TYPE, ");
        parser.addSQL(" A.ACTOR_PSPT_ID, ");
        parser.addSQL(" TO_CHAR(A.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, ");
        parser.addSQL(" TO_CHAR(A.NP_START_DATE, 'YYYY-MM-DD HH24:MI:SS') NP_START_DATE, ");
        parser.addSQL(" TO_CHAR(A.CREATE_TIME, 'YYYY-MM-DD HH24:MI:SS') CREATE_TIME, ");
        parser.addSQL(" TO_CHAR(A.BOOK_SEND_TIME, 'YYYY-MM-DD HH24:MI:SS') BOOK_SEND_TIME, ");
        parser.addSQL(" A.SEND_TIMES, ");
        parser.addSQL(" A.RESULT_CODE, ");
        parser.addSQL(" A.RESULT_MESSAGE, ");
        parser.addSQL(" A.ERROR_MESSAGE, ");
        parser.addSQL(" A.CANCEL_TAG, ");
        parser.addSQL(" A.STATE, ");
        parser.addSQL(" DECODE(A.STATE, '000', '请求等待发送', '009', '请求正在发送', '011', '请求发送失败', '020', '响应结果-成功', '021', '响应结果-失败', '030','告知结果-成功', '031', '告知结果-失败', '040', '生效请求等待发送', '041', '生效请求发送失败', '050', '生效响应结果-成功', '051', '生效响应结果-失败', '060', '生效告知结果-成功', '061', '生效告知结果-失败', '100', '请求/指示正确接受', '130', '告知结果-成功', '131', '告知结果-失败', '未知') STATE_NAME, ");
        parser.addSQL(" DECODE(A.RSRV_STR1, '1', '已处理', '未处理') DEALTAG, ");
        parser.addSQL(" A.REMARK, ");
        parser.addSQL(" A.RSRV_STR1, ");
        parser.addSQL(" A.RSRV_STR2, ");
        parser.addSQL(" A.RSRV_STR3, ");
        parser.addSQL(" A.RSRV_STR4, ");
        parser.addSQL(" A.RSRV_STR5 ");
        parser.addSQL(" FROM TF_B_TRADE_NP A ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND A.CANCEL_TAG='0' ");     
        parser.addSQL(" AND A.TRADE_TYPE_CODE='41' ");
        if (StringUtils.isNotEmpty(startDate))
        {
        	parser.addSQL(" AND A.ACCEPT_DATE>=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        }
        if (StringUtils.isNotEmpty(endDate))
        {
        	parser.addSQL(" AND A.ACCEPT_DATE<=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        }
        if (StringUtils.isNotEmpty(serialNumber))
        {
        	parser.addSQL(" AND A.SERIAL_NUMBER=:SERIAL_NUMBER ");
        }
        parser.addSQL(" ORDER BY ACCEPT_DATE DESC ");
        
        return Dao.qryByParse(parser, page, Route.getJourDb());
    }
	public static IDataset getCustAreaByNpTrade(IDataset trade_np_set,String trafficArea) throws Exception{
		if(IDataUtil.isEmpty(trade_np_set)) {
        	return new DatasetList();
        }
        String user_id_str = "";
		for(int i=0; i<trade_np_set.size(); i++) {
			if(i == 0) {
				user_id_str = trade_np_set.getData(i).getString("USER_ID");     
			}else {
				user_id_str = user_id_str + ","  + trade_np_set.getData(i).getString("USER_ID");
			}
		}
        IData param2 = new DataMap();
        param2.put("USER_ID_STR", user_id_str);
		param2.put("TRAFFIC_AREA", trafficArea);
        SQLParser parser2 = new SQLParser(param2);
        parser2.addSQL(" SELECT * from( ");
        parser2.addSQL(" SELECT t.USER_ID,t.CUSTMER_AREA,t.TRAFFIC_AREA, ROW_NUMBER() OVER(PARTITION BY t.user_id  ORDER BY t.cycle_day DESC  ) rn ");
        parser2.addSQL(" FROM TI_F_CUST_AREA_MAN_DAY t WHERE t.user_id  IN (select * from table(Cast(f_Str2List(:USER_ID_STR) as t_StrList)))  "); 
		parser2.addSQL(" )WHERE rn=1 ");
		if (StringUtils.isNotEmpty(trafficArea)) {
			parser2.addSQL(" AND TRAFFIC_AREA = :TRAFFIC_AREA ");
		}
        IDataset result = Dao.qryByParse(parser2);
        return result;
	}
	/*public static IDataset queryMobileDiscnt(String discntCode) throws Exception
    {
		return UpcCall.queryActiveMobileDiscntByDiscntCode(discntCode);
    }*/
	public static IDataset queryBeautifualNo(String serinumber) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", serinumber);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * FROM TF_R_MPHONECODE_USE T ");
        parser.addSQL(" WHERE T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND T.BEAUTIFUAL_TAG ='1' ");
        return Dao.qryByParse(parser,Route.CONN_RES);
    }
}
