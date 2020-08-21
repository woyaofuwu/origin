
package com.asiainfo.veris.crm.order.soa.frame.bof.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.pkgconfig.PackageExtData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillTradeElementInfoUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillUserElementInfoUtil;

public final class BofQuery
{
    public static IDataset getAllProductModuleTrade() throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT *  ");
        sql.append("FROM TD_B_PRODUCTMODULE_TRADE ");
        sql.append("        WHERE STATE = '1' ");

        IDataset ids = Dao.qryBySql(sql, new DataMap(), Route.CONN_CRM_CEN);
        return ids;
    }

    public static IDataset getAllReqBuilder() throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT *  ");
        sql.append("FROM TD_B_REQUESTBUILDER ");
        sql.append("        WHERE STATE = '1' ");

        IDataset builders = Dao.qryBySql(sql, new DataMap(), Route.CONN_CRM_CEN);
        return builders;
    }

    public static IDataset getAllTrade() throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT *  ");
        sql.append("FROM TD_B_TRADECONFIG ");
        sql.append("        WHERE STATE = '1' ");

        IDataset ids = Dao.qryBySql(sql, new DataMap(), Route.CONN_CRM_CEN);
        return ids;
    }

    /**
     * 查询用户是否有集团彩讯
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getGrpColorInfoUser(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_B", userId);
        param.put("RELATION_TYPE_CODE", "73");
        return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDB", param, routeEparchyCode);
    }

    /**
     * 根据SERIAL_NUMBER,REMOVE_TAG查询用户信息
     */
    public static IDataset getImproperUserInfoBySn(String serialNumber, String routeEparchyCode) throws Exception
    {
        // 缓存无， 从数据库查
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("PARTITION_ID,USER_ID,CUST_ID,USECUST_ID,EPARCHY_CODE, ");
        sql.append("CITY_CODE,CITY_CODE_A,USER_PASSWD,USER_DIFF_CODE,USER_TYPE_CODE, ");
        sql.append("USER_TAG_SET,USER_STATE_CODESET,NET_TYPE_CODE,SERIAL_NUMBER,CONTRACT_ID, ");
        sql.append("ACCT_TAG,PREPAY_TAG,MPUTE_MONTH_FEE, ");
        sql
                .append("TO_CHAR(MPUTE_DATE,'YYYY-MM-DD HH24:MI:SS') MPUTE_DATE,TO_CHAR(FIRST_CALL_TIME,'YYYY-MM-DD HH24:MI:SS') FIRST_CALL_TIME,TO_CHAR(LAST_STOP_TIME,'YYYY-MM-DD HH24:MI:SS') LAST_STOP_TIME,TO_CHAR(CHANGEUSER_DATE,'YYYY-MM-DD HH24:MI:SS') CHANGEUSER_DATE,IN_NET_MODE, ");
        sql.append("TO_CHAR(IN_DATE,'YYYY-MM-DD HH24:MI:SS') IN_DATE,IN_STAFF_ID,IN_DEPART_ID,OPEN_MODE,TO_CHAR(OPEN_DATE,'YYYY-MM-DD HH24:MI:SS') OPEN_DATE, ");
        sql.append("OPEN_STAFF_ID,OPEN_DEPART_ID,DEVELOP_STAFF_ID,TO_CHAR(DEVELOP_DATE,'YYYY-MM-DD HH24:MI:SS') DEVELOP_DATE,DEVELOP_DEPART_ID, ");
        sql.append("DEVELOP_CITY_CODE,DEVELOP_EPARCHY_CODE,DEVELOP_NO,ASSURE_CUST_ID,ASSURE_TYPE_CODE, ");
        sql.append("TO_CHAR(ASSURE_DATE,'YYYY-MM-DD HH24:MI:SS') ASSURE_DATE,REMOVE_TAG,TO_CHAR(PRE_DESTROY_TIME,'YYYY-MM-DD HH24:MI:SS') PRE_DESTROY_TIME,TO_CHAR(DESTROY_TIME,'YYYY-MM-DD HH24:MI:SS') DESTROY_TIME,REMOVE_EPARCHY_CODE, ");
        sql.append("REMOVE_CITY_CODE,REMOVE_DEPART_ID,REMOVE_REASON_CODE,TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3, ");
        sql.append("RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3, ");
        sql.append("RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8, ");
        sql.append("RSRV_STR9,RSRV_STR10,TO_CHAR(RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
        sql.append("RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 ");
        sql.append("FROM TF_F_USER ");
        sql.append(" WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append("   AND REMOVE_TAG != '0' ");
        sql.append("   AND DESTROY_TIME = (SELECT MAX(DESTROY_TIME) ");
        sql.append("                         FROM TF_F_USER ");
        sql.append("                        WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append("                          AND REMOVE_TAG != '0') ");
        sql.append("   AND NOT EXISTS (SELECT 1 ");
        sql.append("          FROM TF_F_USER ");
        sql.append("         WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append("           AND REMOVE_TAG = '0') ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    /**
     * 根据包ID和地州信息查询包扩展信息的配置
     * 
     * @param packageId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static PackageExtData getPackageExtData(String packageId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        param.put("EPARCHY_CODE", eparchyCode);
        IDataset results = Dao.qryByCode("TD_B_PACKAGE_EXT", "SEL_ALL_BY_PK", param, Route.CONN_CRM_CEN);
        if (IDataUtil.isEmpty(results))
        {
            return null;
        }
        else
        {
            return new PackageExtData(results.getData(0));
        }
    }

    /**
     * 根据tabname,sqlref查询参数信息,提供分页
     */
    public static IDataset getParamInfoByAttr(String paramAttr, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SUBSYS_CODE", "CSM");
        cond.put("PARAM_ATTR", paramAttr);
        cond.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_PARAATTR", cond, Route.CONN_CRM_CEN);
    }

    /**
     * 查询平台服务的绑定属性
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset getPlatAttrByServiceId(String serviceId) throws Exception
    {

        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TD_B_PLATSVC_ATTR", "SEL_BY_SVCID", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询平台业务的绑定优惠配置
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset getPlatDiscntConfig(String serviceId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TD_B_PLATSVC_PARAM", "SEL_BY_PLATDISCNT_CONFIG", param, routeEparchyCode);
    }

    public static IDataset getPlatInfoByBizTypeCode(String bizTypeCode, String spCode, String bizCode) throws Exception
    {

    	/*IData param = new DataMap();
        param.put("BIZ_TYPE_CODE", bizTypeCode);
        param.put("BIZ_CODE", bizCode);
        param.put("SP_CODE", spCode);*/
        return UpcCall.querySpServiceAndProdByCond(spCode, bizCode, bizTypeCode, null);
        //return Dao.qryByCodeParser("TD_B_PLATSVC", "SEL_BY_SERVICE_ID", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据服务ID查询平台服务的局数据信息
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset getPlatInfoByServiceId(String serviceId) throws Exception
    {
        try{
        	return UpcCall.querySpServiceAndInfoAndParamByCond(serviceId, "", "", "");
        }catch(Exception e){
        	return new DatasetList();
        }
    }

    /**
     * 查询用户的依赖互斥配置
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    /*三代无用代码删除*/
    /*public static IDataset getPlatsvcLimit(String bizTypeCode, String serviceId, String operCode, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        param.put("BIZ_TYPE_CODE", bizTypeCode);
        param.put("OPER_CODE", operCode);
        return Dao.qryByCode("TD_B_PLATSVC_LIMIT", "SEL_LIMITSVC_BY_SID", param, routeEparchyCode);
    }*/

    /**
     * 查询是否走服务开通的配置
     * 
     * @param pd
     * @param serviceId
     * @param orgDomain
     * @param bizTypeCode
     * @param operCode
     * @param oprSource
     * @return
     * @throws Exception
     */
    public static IDataset getPlatSvcPfRule(String serviceId, String orgDomain, String bizTypeCode, String operCode, String oprSource) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        param.put("ORG_DOMAIN", orgDomain);
        param.put("BIZ_TYPE_CODE", bizTypeCode);
        param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        param.put("OPER_CODE", operCode);
        param.put("OPR_SOURCE", oprSource);
        return Dao.qryByCode("TD_B_PLATSVC_PFRULE", "SEL_PF_RULE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getReqBuilderByPK(String tradeTypeCode, String orderTypeCode, String inModeCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("TRADE_TYPE_CODE", tradeTypeCode);
        cond.put("ORDER_TYPE_CODE", orderTypeCode);
        cond.put("IN_MODE_CODE", inModeCode);
        IDataset builders = Dao.qryByCode("TD_B_REQUESTBUILDER", "SEL_REQUESTBUILDER", cond, Route.CONN_CRM_CEN);
        return builders;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getServElementByPk(String packageId, String elementId, String elementType) throws Exception
    {
        return UPackageElementInfoQry.getPackageElementInfoByPidEidEtype(packageId, elementType, elementId);
    }

    /**
     * 根据sp_code和biz_code捞取TD_B_SPPUNISH的SP处罚信息，再根据commpara表的3766参数看该业务的操作码是否也因违规而被处罚导致业务不能订购
     * 
     * @param spCode
     *            企业代码
     * @param bizCode
     *            业务代码
     * @param operCode
     *            操作码
     * @return 如果返回的结果集有值，则不抛错，否则抛出平台异常，SP暂停服务
     * @throws Exception
     */
    public static IDataset getSpPunish(String spCode, String bizCode, String operCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SP_CODE", spCode);
        param.put("OPERATOR_CODE", bizCode);
        IDataset spPunish = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SPPUNISH", param, null, Route.CONN_CRM_CEN);
        if (spPunish != null && spPunish.size() > 0)
        {
            param.clear();
            param.put("PARAM_CODE", spPunish.getData(0).getString("PUNISH_TYPE"));
            param.put("PARA_CODE1", operCode);
            return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_COMPARA", param, null, Route.CONN_CRM_CEN);
        }
        else
        {
            return null;
        }
    }

    public static IData getTradeCfgByCode(String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);

        StringBuilder sql = new StringBuilder(200);
        sql.append("SELECT *  ");
        sql.append("FROM TD_B_TRADECONFIG ");
        sql.append("        WHERE STATE = '1' ");
        sql.append("        AND TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");

        IDataset ids = Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
        return ids.size() > 0 ? ids.getData(0) : new DataMap();
    }

    public static IDataset getTradeProductByUserId(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID,A.ACCEPT_MONTH,TO_CHAR(A.USER_ID) USER_ID,TO_CHAR(A.USER_ID_A) USER_ID_A,A.PRODUCT_ID, ");
        sql.append("       A.PRODUCT_MODE,A.BRAND_CODE,A.OLD_PRODUCT_ID,A.OLD_BRAND_CODE,A.MAIN_TAG,TO_CHAR(A.INST_ID) INST_ID,TO_CHAR(A.CAMPN_ID) CAMPN_ID, ");
        sql.append("       TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG, ");
        sql.append("       TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK,A.RSRV_NUM1,A.RSRV_NUM2, ");
        sql.append("       A.RSRV_NUM3,TO_CHAR(A.RSRV_NUM4) RSRV_NUM4,TO_CHAR(A.RSRV_NUM5) RSRV_NUM5,A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3,A.RSRV_STR4,A.RSRV_STR5, ");
        sql.append("       TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
        sql.append("       A.RSRV_TAG1,A.RSRV_TAG2,A.RSRV_TAG3 ");
        sql.append("  FROM TF_B_TRADE_PRODUCT A, TF_B_TRADE B ");
        sql.append(" WHERE B.TRADE_ID = A.TRADE_ID ");
        sql.append(" AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append("   AND B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND B.SUBSCRIBE_TYPE != '600' ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids;
    }

    public static IDataset getTradeProductByUserId4Shopping(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID,A.ACCEPT_MONTH,TO_CHAR(A.USER_ID) USER_ID,TO_CHAR(A.USER_ID_A) USER_ID_A,A.PRODUCT_ID, ");
        sql.append("       A.PRODUCT_MODE,A.BRAND_CODE,A.OLD_PRODUCT_ID,A.OLD_BRAND_CODE,A.MAIN_TAG,TO_CHAR(A.INST_ID) INST_ID,TO_CHAR(A.CAMPN_ID) CAMPN_ID, ");
        sql.append("       TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG, ");
        sql.append("       TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK,A.RSRV_NUM1,A.RSRV_NUM2, ");
        sql.append("       A.RSRV_NUM3,TO_CHAR(A.RSRV_NUM4) RSRV_NUM4,TO_CHAR(A.RSRV_NUM5) RSRV_NUM5,A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3,A.RSRV_STR4,A.RSRV_STR5, ");
        sql.append("       TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
        sql.append("       A.RSRV_TAG1,A.RSRV_TAG2,A.RSRV_TAG3 ");
        sql.append("  FROM TF_B_TRADE_PRODUCT A, TF_B_TRADE B ");
        sql.append(" WHERE B.TRADE_ID = A.TRADE_ID ");
        sql.append(" AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append(" AND B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append(" and (A.RSRV_TAG3 <> 'D' or A.RSRV_TAG3 is null) ");
        sql.append(" AND B.SUBSCRIBE_STATE <> 'A' ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids;
    }

    /**
     * 查询用户是否有某服务的未完工工单
     * 
     * @param userId
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset getUncompleteTrade(String userId, String serviceId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT COUNT(1) ORDERNUM FROM TF_B_TRADE A,TF_B_TRADE_PLATSVC B ");
        sql.append("WHERE A.TRADE_ID =  B.TRADE_ID ");
        sql.append("AND B.ACCEPT_MONTH = A.ACCEPT_MONTH ");
        sql.append("AND A.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND B.SERVICE_ID  = TO_NUMBER(:SERVICE_ID) ");
        sql.append("AND A.TRADE_TYPE_CODE LIKE '37__' ");
        sql.append("AND A.TRADE_TYPE_CODE <> '3788' ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));

        return ids;
    }

    public static IDataset getUserAllProducts(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("PARTITION_ID,USER_ID,USER_ID_A,PRODUCT_ID,PRODUCT_MODE, ");
        sql.append("BRAND_CODE,INST_ID,CAMPN_ID,TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1, ");
        sql.append("RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1, ");
        sql.append("RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,TO_CHAR(RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3, ");
        sql.append("MAIN_TAG ");
        sql.append("FROM TF_F_USER_PRODUCT T ");
        sql.append(" WHERE  T.PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000)  ");
        sql.append("  AND T.USER_ID=TO_NUMBER(:USER_ID) ");
        sql.append("  AND END_DATE > SYSDATE ");
        sql.append("  AND END_DATE > START_DATE ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;

    }

    /**
     * 查询用户的平台业务优惠
     * 
     * @param userId
     * @param discntCode
     *            优惠编码，用逗号加单引号分隔成多个，如'20091018','20081030'
     * @return
     * @throws Exception
     */
    public static IDataset getUserPlatDiscnt(String userId, String discntCode, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DISCNT_CODE", discntCode);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("PARTITION_ID,USER_ID,USER_ID_A, ");
        sql.append("DISCNT_CODE,SPEC_TAG,RELATION_TYPE_CODE,INST_ID,CAMPN_ID, ");
        sql.append("TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID, ");
        sql.append("REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4, ");
        sql.append("RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4, ");
        sql.append("RSRV_STR5,TO_CHAR(RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,RSRV_TAG1, ");
        sql.append("RSRV_TAG2,RSRV_TAG3 ");
        sql.append("FROM TF_F_USER_DISCNT ");
        sql.append(" WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
        sql.append("   AND USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND END_DATE > SYSDATE ");
        sql.append("   AND DISCNT_CODE = :DISCNT_CODE ");
        sql.append("   AND END_DATE > SYSDATE ");
        sql.append("  AND END_DATE > START_DATE ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    /**
     * 获取用户所有end_date>sysdate的平台服务
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUserPlatSvc(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("PARTITION_ID,USER_ID,SERVICE_ID, ");
        sql.append("BIZ_STATE_CODE,TO_CHAR(FIRST_DATE,'YYYY-MM-DD HH24:MI:SS') FIRST_DATE,TO_CHAR(FIRST_DATE_MON,'YYYY-MM-DD HH24:MI:SS') FIRST_DATE_MON,GIFT_SERIAL_NUMBER,GIFT_USER_ID, ");
        sql.append("INST_ID,IN_CARD_NO,ENTITY_CARD_NO,TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1, ");
        sql.append("RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1, ");
        sql.append("RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6, ");
        sql.append("RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10,TO_CHAR(RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 ");
        sql.append("FROM TF_F_USER_PLATSVC ");
        sql.append(" WHERE USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("   AND END_DATE > SYSDATE ");
        sql.append("   AND BIZ_STATE_CODE <> 'E' ");
        sql.append("  AND END_DATE > START_DATE ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    /**
     * 查询用户某个平台服务的首次订购时间
     * 
     * @param pd
     * @param userId
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset getUserPlatSvcFirstDate(String userId, String serviceId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("       TO_CHAR(MIN(FIRST_DATE), 'YYYY-MM-DD HH24:MI:SS') FIRST_DATE ");
        sql.append("  FROM TF_F_USER_PLATSVC ");
        sql.append(" WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("   AND USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND SERVICE_ID = :SERVICE_ID ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    /**
     * 查询用户某个平台服务的本月首次订购时间
     * 
     * @param pd
     * @param userId
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset getUserPlatSvcFirstDateMon(String userId, String serviceId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT TO_CHAR(MIN(FIRST_DATE_MON), 'YYYY-MM-DD HH24:MI:SS') FIRST_DATE_MON ");
        sql.append("  FROM TF_F_USER_PLATSVC ");
        sql.append(" WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("   AND USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND SERVICE_ID = :SERVICE_ID ");
        sql.append("   AND START_DATE BETWEEN TRUNC(SYSDATE, 'MM') AND ");
        sql.append("       ADD_MONTHS(TRUNC(SYSDATE, 'MM'), 1) - 1 / 24 / 3600 ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    /**
     * 查询用户的服务
     * 
     * @param userId
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset getUserSvc(String userId, String serviceId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("PARTITION_ID,USER_ID,USER_ID_A, ");
        sql.append("SERVICE_ID,MAIN_TAG,INST_ID,CAMPN_ID,TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.append("TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK, ");
        sql.append("RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5, ");
        sql.append("RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5, ");
        sql.append("TO_CHAR(RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,RSRV_TAG1,RSRV_TAG2, ");
        sql.append("RSRV_TAG3,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9, ");
        sql.append("RSRV_STR10 ");
        sql.append("FROM TF_F_USER_SVC A ");
        sql.append("WHERE  A.USER_ID=:USER_ID ");
        sql.append("AND    A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
        sql.append("  AND    A.SERVICE_ID=TO_NUMBER(:SERVICE_ID) ");
        sql.append("  AND A.END_DATE>SYSDATE ");
        sql.append("  AND A.END_DATE > A.START_DATE ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    /**
     * 作用：根据USERID，查询有效的服务状态记录TF_F_USER_SVCSTATE::SEL_VALID_BY_USERID
     * 
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserValidSvcStateByUserId(String userId, String routeEparchyCode) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("INST_ID,PARTITION_ID,USER_ID,SERVICE_ID,MAIN_TAG, ");
        sql.append("STATE_CODE,TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3, ");
        sql.append("RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3, ");
        sql.append("RSRV_STR4,RSRV_STR5,TO_CHAR(RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
        sql.append("RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 ");
        sql.append("FROM TF_F_USER_SVCSTATE ");
        sql.append(" WHERE USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("   AND END_DATE > SYSDATE ");
        sql.append("  AND END_DATE > START_DATE ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    /**
     * 获取无线音乐会员的会员级别
     * 
     * @param pd
     * @param userId
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset getUserWirelessMusicMemberLevel(String userId, String serviceId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT INFO_VALUE FROM TF_F_USER_PLATSVC_ATTR A WHERE ");
        sql.append("A.PARTITION_ID =  MOD(TO_NUMBER(:USER_ID),10000)  ");
        sql.append("AND A.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND A.SERVICE_ID = TO_NUMBER(:SERVICE_ID) ");
        sql.append("AND A.INFO_CODE='302' ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    /**
     * 根据userid和remove_tag查询VIP信息
     * 
     * @param userId
     * @param removeTag
     * @param routeEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getVip(String userId, String removeTag, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("REMOVE_TAG", "0");

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT VIP_ID, to_char(CUST_ID) CUST_ID, to_char(USECUST_ID) USECUST_ID,");
        sql.append("      to_char(USER_ID) USER_ID, SERIAL_NUMBER, NET_TYPE_CODE, CUST_NAME,");
        sql.append("      USECUST_NAME, USEPSPT_TYPE_CODE, USEPSPT_ID, ");
        sql.append("      to_char(USEPSPT_END_DATE,'yyyy-mm-dd hh24:mi:ss') USEPSPT_END_DATE, ");
        sql.append("      USEPSPT_ADDR, USEPHONE, USEPOST_ADDR, EPARCHY_CODE, CITY_CODE, ");
        sql.append("      VIP_TYPE_CODE, VIP_CLASS_ID, LAST_VIP_TYPE_CODE, LAST_VIP_CLASS_ID,");
        sql.append("      to_char(CLASS_CHANGE_DATE,'yyyy-mm-dd hh24:mi:ss') CLASS_CHANGE_DATE,");
        sql.append("      VIP_TYPE_CODE_B, VIP_CLASS_ID_B, LAST_VIP_TYPE_CODE_B, LAST_VIP_CLASS_ID_B,");
        sql.append("      to_char(CLASS_CHANGE_DATE_B,'yyyy-mm-dd hh24:mi:ss') CLASS_CHANGE_DATE_B, ");
        sql.append("      VIP_CARD_NO, VIP_CARD_TYPE, VIP_CARD_PASSWD, VIP_CARD_STATE, VIP_CARD_SPELL, ");
        sql.append("      VIP_CARD_INFO, VIP_CARD_SEND_TYPE, ");
        sql.append("      to_char(VIP_CARD_SEND_DATE,'yyyy-mm-dd hh24:mi:ss') VIP_CARD_SEND_DATE, ");
        sql.append("      VIP_CARD_POST_ADDR, to_char(VIP_CARD_START_DATE,'yyyy-mm-dd hh24:mi:ss') VIP_CARD_START_DATE, ");
        sql.append("      to_char(VIP_CARD_END_DATE,'yyyy-mm-dd hh24:mi:ss') VIP_CARD_END_DATE, ");
        sql.append("      to_char(VIP_CARD_CHANGE_DATE,'yyyy-mm-dd hh24:mi:ss') VIP_CARD_CHANGE_DATE,");
        sql.append("      VIP_CARD_CHANGE_REASON, CUST_MANAGER_ID, CUST_MANAGER_ID_B, CUST_MANAGER_APPR,");
        sql.append("      to_char(ASSIGN_DATE,'yyyy-mm-dd hh24:mi:ss') ASSIGN_DATE, ASSIGN_STAFF_ID, ");
        sql.append("      JOIN_TYPE, to_char(JOIN_DATE,'yyyy-mm-dd hh24:mi:ss') JOIN_DATE, ");
        sql.append("      to_char(JOIN_DATE_B,'yyyy-mm-dd hh24:mi:ss') JOIN_DATE_B, JOIN_STAFF_ID, JOIN_DEPART_ID, ");
        sql.append("      to_char(IDENTITY_CHK_DATE,'yyyy-mm-dd hh24:mi:ss') IDENTITY_CHK_DATE, ");
        sql.append("      to_char(IDENTITY_CHK_SCORE,'yyyy-mm-dd hh24:mi:ss') IDENTITY_CHK_SCORE, IDENTITY_PRI, ");
        sql.append("      to_char(IDENTITY_EFF_DATE,'yyyy-mm-dd hh24:mi:ss') IDENTITY_EFF_DATE, ");
        sql.append("      to_char(IDENTITY_EXP_DATE,'yyyy-mm-dd hh24:mi:ss') IDENTITY_EXP_DATE, GROUP_ID, ");
        sql.append("      GROUP_CUST_NAME, to_char(MONTH_FEE) MONTH_FEE, HVALUE_TAG, to_char(CLUB_ID) CLUB_ID, ");
        sql.append("      VISIT_NUM, SVC_NUM, SVC_NUM_B, INNET_NUM, SVC_MODE_CODE, SVC_CYCLE_CODE, ");
        sql.append("      to_char(BIRTHDAY,'yyyy-mm-dd hh24:mi:ss') BIRTHDAY, BIRTHDAY_LUNAR, BIRTHDAY_FLAG,");
        sql.append("      APPROVAL_FLAG, APPROVAL_STAFF_ID, to_char(APPROVAL_TIME,'yyyy-mm-dd hh24:mi:ss') APPROVAL_TIME, ");
        sql.append("      APPROVAL_DESC, GROUP_BRAND_CODE, BRAND_CODE, PRODUCT_ID, USER_TYPE_CODE, USER_STATE_CODESET, ");
        sql.append("      to_char(OPEN_DATE,'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, to_char(FIRST_CALL_TIME,'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
        sql.append("      to_char(LAST_STOP_TIME,'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, to_char(SCORE_VALUE) SCORE_VALUE, ");
        sql.append("      CREDIT_CLASS, to_char(CREDIT_VALUE) CREDIT_VALUE, CTAG_SET, CHECK_NO, to_char(TRADE_ID) TRADE_ID, ");
        sql.append("      CANCEL_TAG, REMOVE_TAG, to_char(REMOVE_DATE,'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE, REMOVE_STAFF_ID,");
        sql.append("      REMOVE_REASON, EXEMPT_SCORE_TAG,to_char(SYNC_TIME,'yyyy-mm-dd hh24:mi:ss') SYNC_TIME, ");
        sql.append("      to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK ");
        sql.append("    FROM   TF_F_CUST_VIP ");
        sql.append("    WHERE   USER_ID=to_number(:USER_ID) ");
        sql.append("    AND     REMOVE_TAG = :REMOVE_TAG ");
        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    /**
     * 查询用户的某服务是否在某段时期内首次订购
     * 
     * @param userId
     * @param serviceId
     * @param timeType
     * @param timeValue
     * @return
     * @throws Exception
     */
    public static boolean isFirstPeriod(String userId, String serviceId, String timeType, String timeValue, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        param.put("TIME_TYPE", timeType);
        param.put("TIME_VALUE", timeValue);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT COUNT(*)  IS_FIRST                                                      ");
        sql.append("           FROM TF_F_USER_PLATSVC C                                            ");
        sql.append("          WHERE C.USER_ID = :USER_ID                                           ");
        sql.append("            AND C.SERVICE_ID = :SERVICE_ID                                     ");
        sql.append("            AND (  (:TIME_TYPE IS NULL) OR                                     ");
        sql.append("                   ( SYSDATE  <                                                ");
        sql.append("                   DECODE(:TIME_TYPE,                                          ");
        sql.append("                     '0', C.START_DATE +:TIME_VALUE,                           ");
        sql.append("                     '1',TRUNC( C.START_DATE +:TIME_VALUE ),                   ");
        sql.append("                     '2',ADD_MONTHS( C.START_DATE ,:TIME_VALUE ),              ");
        sql.append("                     '3',TRUNC(ADD_MONTHS( C.START_DATE ,:TIME_VALUE),'MM'),   ");
        sql.append("                     '4',ADD_MONTHS( C.START_DATE ,(:TIME_VALUE)*12),          ");
        sql.append("                     '5',TRUNC(ADD_MONTHS(C.START_DATE,(:TIME_VALUE)*12),'YY'), ");
        sql.append("                     SYSDATE                                                   ");
        sql.append("                   ) AND (C.BIZ_STATE_CODE='A' OR C.BIZ_STATE_CODE IS NULL))   ");
        sql.append("                 )                                                             ");

        IDataset result = Dao.qryBySql(sql, param, routeEparchyCode);

        if (result != null && result.size() > 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static IDataset queryAllParamFilter() throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);
        sql.append("select * from td_b_param_filter a ");
        sql.append("where a.state = '1' ");

        IDataset ids = Dao.qryBySql(sql, new DataMap(), Route.CONN_CRM_CEN);
        return ids;
    }

    public static IDataset queryAllProductModuleActions() throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT *  ");
        sql.append("FROM TD_B_PRODUCTMODULE_ACTION ");
        sql.append("        WHERE STATE = '1' ");

        IDataset ids = Dao.qryBySql(sql, new DataMap(), Route.CONN_CRM_CEN);
        return ids;
    }

    public static IDataset queryAllProductModuleRules() throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT *  ");
        sql.append("FROM TD_B_PRODUCTMODULE_RULE ");
        sql.append("        WHERE STATE = '1' ");

        IDataset ids = Dao.qryBySql(sql, new DataMap(), Route.CONN_CRM_CEN);
        return ids;
    }

    public static IDataset queryAllTradeActions() throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT *  ");
        sql.append("FROM TD_B_TRADEACTION ");
        sql.append("        WHERE STATE = '1' ");

        IDataset ids = Dao.qryBySql(sql, new DataMap(), Route.CONN_CRM_CEN);
        return ids;
    }

    public static IDataset queryCalElementDateActions(String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_TYPE_CODE", tradeTypeCode);

        IDataset set = Dao.qryByCode("TD_B_TRADEACTION", "SEL_ELEMENTCALDATE_ACTION", param, Route.CONN_CRM_CEN);
        return set;
    }

    public static IDataset queryCustFamily(String homeCustId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("HOME_CUST_ID", homeCustId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("A.HOME_CUST_ID, ");
        sql.append("A.PARTITION_ID, ");
        sql.append("A.HOME_ID, ");
        sql.append("A.HOME_NAME, ");
        sql.append("A.HOME_ADDRESS, ");
        sql.append("A.HOME_PHONE, ");
        sql.append("A.HOME_REGION, ");
        sql.append("A.CUST_NAME, ");
        sql.append("A.SERIAL_NUMBER, ");
        sql.append("A.HEAD_CUST_ID, ");
        sql.append("A.HEAD_TYPE_CODE, ");
        sql.append("A.HEAD_PSPT_ID, ");
        sql.append("A.MEMBER_NUM, ");
        sql.append("A.HOME_STATE, ");
        sql.append("A.HOME_CUST_SCORE, ");
        sql.append("A.HOME_POST_CODE, ");
        sql.append("A.FAX_NBR, ");
        sql.append("A.PHONE, ");
        sql.append("A.UNIFY_PAY_CODE, ");
        sql.append("A.WORK_ADDRESS, ");
        sql.append("A.WORK_NAME, ");
        sql.append("A.HAS_CAR, ");
        sql.append("A.HAS_CHILD, ");
        sql.append("A.CHILD_AGE, ");
        sql.append("A.HAS_FETION, ");
        sql.append("A.QQ, ");
        sql.append("A.MSN, ");
        sql.append("A.EMAIL, ");
        sql.append("A.INTEREST_CODE, ");
        sql.append("A.EPARCHY_CODE, ");
        sql.append("A.CITY_CODE, ");
        sql.append("TO_CHAR(A.IN_DATE,'YYYY-MM-DD HH24:MI:SS') IN_DATE, ");
        sql.append("A.IN_STAFF_ID, ");
        sql.append("A.IN_DEPART_ID, ");
        sql.append("A.REMOVE_TAG, ");
        sql.append("TO_CHAR(A.REMOVE_DATE,'YYYY-MM-DD HH24:MI:SS') REMOVE_DATE, ");
        sql.append("A.REMOVE_STAFF_ID, ");
        sql.append("A.REMOVE_DEPART_ID, ");
        sql.append("A.REMOVE_REASON, ");
        sql.append("TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("A.UPDATE_STAFF_ID, ");
        sql.append("A.UPDATE_DEPART_ID, ");
        sql.append("A.RSRV_NUM1, ");
        sql.append("A.RSRV_NUM2, ");
        sql.append("A.RSRV_NUM3, ");
        sql.append("A.RSRV_STR1, ");
        sql.append("A.RSRV_STR2, ");
        sql.append("A.RSRV_STR3, ");
        sql.append("A.RSRV_STR4, ");
        sql.append("A.RSRV_STR5, ");
        sql.append("A.RSRV_STR6, ");
        sql.append("A.RSRV_STR7, ");
        sql.append("A.RSRV_STR8, ");
        sql.append("A.RSRV_STR9, ");
        sql.append("A.RSRV_STR10, ");
        sql.append("TO_CHAR(A.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1, ");
        sql.append("TO_CHAR(A.RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        sql.append("TO_CHAR(A.RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
        sql.append("A.RSRV_TAG1, ");
        sql.append("A.RSRV_TAG2, ");
        sql.append("A.RSRV_TAG3 ");
        sql.append("FROM TF_F_CUST_FAMILY A ");
        sql.append("WHERE A.HOME_CUST_ID = TO_NUMBER(:HOME_CUST_ID) ");
        sql.append("AND REMOVE_TAG = '0'");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    public static IDataset queryDependTradeType(String tradeTypeCode, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("TRADE_TYPE_CODE", tradeTypeCode);
        cond.put("LIMIT_ATTR", "0");
        cond.put("LIMIT_TAG", "1");
        cond.put("EPARCHY_CODE", eparchyCode);
        IDataset result = Dao.qryByCode("TD_S_TRADETYPE_LIMIT", "SEL_BY_TRADETYPE_TAG", cond, Route.CONN_CRM_CEN);

        return result;
    }

    public static IDataset queryParamFiltersByPK(String xTransCode, String filterType) throws Exception
    {
        IData param = new DataMap();
        param.put("X_TRANS_CODE", xTransCode);
        param.put("FILTER_TYPE", filterType);

        IDataset set = Dao.qryByCode("TD_B_PARAM_FILTER", "SEL_BY_XTRANSCODE", param, Route.CONN_CRM_CEN);

        return set;
    }

    public static IDataset queryProductModuleActionsByPK(String elementTypeCode, String elementId, String tradeTypeCode, String orderTypeCode) throws Exception
    {
        String inModeCode = CSBizBean.getVisit().getInModeCode();
        IData param = new DataMap();
        param.put("ELEMENT_TYPE_CODE", elementTypeCode);
        param.put("ELEMENT_ID", elementId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("ORDER_TYPE_CODE", orderTypeCode);
        param.put("IN_MODE_CODE", inModeCode);

        IDataset set = Dao.qryByCode("TD_B_PRODUCTMODULE_ACTION", "SEL_PRODUCTACTION", param, Route.CONN_CRM_CEN);

        return set;
    }

    public static IDataset queryProductModuleRulesByPK(String elementTypeCode, String elementId, String tradeTypeCode, String orderTypeCode, String tag) throws Exception
    {
        String inModeCode = CSBizBean.getVisit().getInModeCode();
        IData param = new DataMap();
        param.put("ELEMENT_TYPE_CODE", elementTypeCode);
        param.put("ELEMENT_ID", elementId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("ORDER_TYPE_CODE", orderTypeCode);
        param.put("IN_MODE_CODE", inModeCode);
        param.put("TAG", tag);

        IDataset set = Dao.qryByCode("TD_B_PRODUCTMODULE_RULE", "SEL_PRODUCTRULE", param, Route.CONN_CRM_CEN);

        return set;
    }

    public static IDataset queryTradeAttrsByUserId(String userId, String routeEparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("Select to_char(A.TRADE_ID) TRADE_ID, ");
        sql.append("       A.ACCEPT_MONTH, ");
        sql.append("       to_char(A.USER_ID) USER_ID, ");
        sql.append("       A.INST_TYPE, ");
        sql.append("       to_char(A.INST_ID) INST_ID, ");
        sql.append("       A.ATTR_CODE, ");
        sql.append("       A.ATTR_VALUE, ");
        sql.append("       to_char(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("       to_char(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("       A.MODIFY_TAG, ");
        sql.append("       to_char(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("       A.UPDATE_STAFF_ID, ");
        sql.append("       A.UPDATE_DEPART_ID, ");
        sql.append("       A.REMARK, ");
        sql.append("       A.RSRV_NUM1, ");
        sql.append("       A.RSRV_NUM2, ");
        sql.append("       A.RSRV_NUM3, ");
        sql.append("       to_char(A.RSRV_NUM4) RSRV_NUM4, ");
        sql.append("       to_char(A.RSRV_NUM5) RSRV_NUM5, ");
        sql.append("       A.RSRV_STR1, ");
        sql.append("       A.RSRV_STR2, ");
        sql.append("       A.RSRV_STR3, ");
        sql.append("       A.RSRV_STR4, ");
        sql.append("       A.RSRV_STR5, ");
        sql.append("       to_char(A.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("       to_char(A.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("       to_char(A.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("       A.RSRV_TAG1, ");
        sql.append("       A.RSRV_TAG2, ");
        sql.append("       A.RSRV_TAG3, ");
        sql.append("       A.ELEMENT_ID, ");
        sql.append("       A.RELA_INST_ID ");
        sql.append("  From TF_B_TRADE_ATTR A, TF_B_TRADE B ");
        sql.append(" WHERE B.TRADE_ID = A.TRADE_ID ");
        sql.append(" AND B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append(" AND B.SUBSCRIBE_TYPE != '600'  ");

        IDataset ids = Dao.qryBySql(sql, data, Route.getJourDb(routeEparchyCode));
        return ids;
    }

    public static IDataset queryTradeAttrsByUserId4Shopping(String userId, String routeEparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_USER_4_SHOPPING", data, Route.getJourDb(routeEparchyCode));
    }

    public static IDataset queryTradeBeforeFinishActionsByPK(String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_TYPE_CODE", tradeTypeCode);

        IDataset set = Dao.qryByCode("TD_B_TRADEACTION", "SEL_TRADE_BEFORE_FINISH_ACTION", param, Route.CONN_CRM_CEN);
        return set;
    }

    public static IDataset queryTradeCancelFinishActionsByPK(String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_TYPE_CODE", tradeTypeCode);

        IDataset set = Dao.qryByCode("TD_B_TRADEACTION", "SEL_TRADE_CANCEL_FINISH_ACTION", param, Route.CONN_CRM_CEN);
        return set;
    }

    public static IDataset queryTradeDiscntsByUserId(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID,A.ACCEPT_MONTH,TO_CHAR(A.USER_ID) USER_ID,TO_CHAR(A.USER_ID_A) USER_ID_A, ");
        sql.append("       A.DISCNT_CODE,A.SPEC_TAG,A.RELATION_TYPE_CODE,TO_CHAR(A.INST_ID) INST_ID,TO_CHAR(A.CAMPN_ID) CAMPN_ID, ");
        sql.append("       TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG, ");
        sql.append("       TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK,A.RSRV_NUM1, ");
        sql.append("       A.RSRV_NUM2,A.RSRV_NUM3,TO_CHAR(A.RSRV_NUM4) RSRV_NUM4,TO_CHAR(A.RSRV_NUM5) RSRV_NUM5,A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3, ");
        sql.append("       A.RSRV_STR4,A.RSRV_STR5,TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        sql.append("       TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,A.RSRV_TAG1,A.RSRV_TAG2,A.RSRV_TAG3 ");
        sql.append("  FROM TF_B_TRADE_DISCNT A, TF_B_TRADE B ");
        sql.append(" WHERE B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append(" AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append("   AND A.TRADE_ID = B.TRADE_ID ");
        sql.append("   AND B.SUBSCRIBE_TYPE != '600' ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        
        FillTradeElementInfoUtil.fillTradeElementProductIdAndPackageId(ids);
        return ids;
    }

    public static IDataset queryTradeDiscntsByUserId4Shopping(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID,A.ACCEPT_MONTH,TO_CHAR(A.USER_ID) USER_ID,TO_CHAR(A.USER_ID_A) USER_ID_A, ");
        sql.append("       A.DISCNT_CODE,A.SPEC_TAG,A.RELATION_TYPE_CODE,TO_CHAR(A.INST_ID) INST_ID,TO_CHAR(A.CAMPN_ID) CAMPN_ID, ");
        sql.append("       TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG, ");
        sql.append("       TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK,A.RSRV_NUM1, ");
        sql.append("       A.RSRV_NUM2,A.RSRV_NUM3,TO_CHAR(A.RSRV_NUM4) RSRV_NUM4,TO_CHAR(A.RSRV_NUM5) RSRV_NUM5,A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3, ");
        sql.append("       A.RSRV_STR4,A.RSRV_STR5,TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        sql.append("       TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,A.RSRV_TAG1,A.RSRV_TAG2,A.RSRV_TAG3 ");
        sql.append("  FROM TF_B_TRADE_DISCNT A, TF_B_TRADE B ");
        sql.append(" WHERE B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append(" AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append(" AND A.TRADE_ID = B.TRADE_ID ");
        sql.append(" AND A.TRADE_ID = B.TRADE_ID ");
        sql.append(" and (A.RSRV_TAG3 <> 'D' or A.RSRV_TAG3 is null) ");
        sql.append(" AND B.SUBSCRIBE_STATE <> 'A' ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids;
    }

    public static IDataset queryTradeFinishActionsByPK(String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_TYPE_CODE", tradeTypeCode);

        IDataset set = Dao.qryByCode("TD_B_TRADEACTION", "SEL_TRADE_FINISH_ACTION", param, Route.CONN_CRM_CEN);
        return set;
    }
    
    public static IDataset queryPrintFinishActionsByPK(String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_TYPE_CODE", tradeTypeCode);

        IDataset set = Dao.qryByCode("TD_B_TRADEACTION", "SEL_PRINT_FINISH_ACTION", param, Route.CONN_CRM_CEN);
        return set;
    }

    public static IDataset queryTradeRegActionsByPK(String tradeTypeCode, String orderTypeCode) throws Exception
    {
        String inModeCode = CSBizBean.getVisit().getInModeCode();
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("ORDER_TYPE_CODE", orderTypeCode);
        param.put("IN_MODE_CODE", inModeCode);
        IDataset set = Dao.qryByCode("TD_B_TRADEACTION", "SEL_TRADEACTION", param, Route.CONN_CRM_CEN);
        return set;
    }

    public static IDataset queryTradeResByUserId(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(2500);
        sql.append("SELECT  ");
        sql.append("A.TRADE_ID, ");
        sql.append("A.ACCEPT_MONTH, ");
        sql.append("A.USER_ID, ");
        sql.append("A.USER_ID_A, ");
        sql.append("A.RES_TYPE_CODE, ");
        sql.append("A.RES_CODE, ");
        sql.append("A.IMSI, ");
        sql.append("A.KI, ");
        sql.append("A.INST_ID, ");
        sql.append("A.CAMPN_ID, ");
        sql.append("TO_CHAR(A.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.append("TO_CHAR(A.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        sql.append("A.MODIFY_TAG, ");
        sql.append("TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("A.UPDATE_STAFF_ID, ");
        sql.append("A.UPDATE_DEPART_ID, ");
        sql.append("A.REMARK, ");
        sql.append("A.RSRV_NUM1, ");
        sql.append("A.RSRV_NUM2, ");
        sql.append("A.RSRV_NUM3, ");
        sql.append("A.RSRV_NUM4, ");
        sql.append("A.RSRV_NUM5, ");
        sql.append("A.RSRV_STR1, ");
        sql.append("A.RSRV_STR2, ");
        sql.append("A.RSRV_STR3, ");
        sql.append("A.RSRV_STR4, ");
        sql.append("A.RSRV_STR5, ");
        sql.append("TO_CHAR(A.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1, ");
        sql.append("TO_CHAR(A.RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        sql.append("TO_CHAR(A.RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
        sql.append("A.RSRV_TAG1, ");
        sql.append("A.RSRV_TAG2, ");
        sql.append("A.RSRV_TAG3 ");
        sql.append("FROM TF_B_TRADE_RES A, TF_B_TRADE B ");
        sql.append("where A.TRADE_ID = B.TRADE_ID ");
        sql.append("AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append("AND B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND B.SUBSCRIBE_TYPE != '600' ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids;
    }

    public static IDataset queryTradeSaleActivesByUserId(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID,A.ACCEPT_MONTH,TO_CHAR(A.USER_ID) USER_ID,A.SERIAL_NUMBER,A.SERIAL_NUMBER_B,A.PRODUCT_MODE, ");
        sql.append("       A.PRODUCT_ID,A.PRODUCT_NAME,A.PACKAGE_ID,A.PACKAGE_NAME,TO_CHAR(A.CAMPN_ID) CAMPN_ID,A.CAMPN_CODE,A.CAMPN_NAME,A.CAMPN_TYPE,TO_CHAR(A.OPER_FEE) OPER_FEE, ");
        sql.append("       TO_CHAR(A.FOREGIFT) FOREGIFT,TO_CHAR(A.ADVANCE_PAY) ADVANCE_PAY,TO_CHAR(A.SCORE_CHANGED) SCORE_CHANGED,TO_CHAR(A.CREDIT_VALUE) CREDIT_VALUE, ");
        sql.append("       A.MONTHS,A.ACTOR_PSPT_ID,A.ACTOR_PSPT_TYPE_CODE,A.ACTOR_PHONE,A.ACTOR_NAME,A.PROCESS_TAG,TO_CHAR(A.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, ");
        sql.append("       A.TRADE_STAFF_ID,TO_CHAR(A.RELATION_TRADE_ID) RELATION_TRADE_ID,TO_CHAR(A.CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE, ");
        sql.append("       A.CANCEL_STAFF_ID,A.CANCEL_CAUSE,TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        sql.append("       A.MODIFY_TAG,TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK, ");
        sql.append("       A.RSRV_NUM1,A.RSRV_NUM2,A.RSRV_NUM3,TO_CHAR(A.RSRV_NUM4) RSRV_NUM4,TO_CHAR(A.RSRV_NUM5) RSRV_NUM5,A.RSRV_STR1,A.RSRV_STR2, ");
        sql.append("       A.RSRV_STR3,A.RSRV_STR4,A.RSRV_STR5,TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        sql.append("       TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,A.RSRV_TAG1,A.RSRV_TAG2,A.RSRV_TAG3,A.INST_ID ");
        sql.append("  FROM TF_B_TRADE_SALE_ACTIVE A, TF_B_TRADE B ");
        sql.append(" WHERE A.TRADE_ID = B.TRADE_ID ");
        sql.append(" AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append("   AND B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND B.SUBSCRIBE_TYPE != '600' ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids;
    }

    public static IDataset queryTradeSaleActivesByUserId4Shopping(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID,A.ACCEPT_MONTH,TO_CHAR(A.USER_ID) USER_ID,A.SERIAL_NUMBER,A.SERIAL_NUMBER_B,A.PRODUCT_MODE, ");
        sql.append("       A.PRODUCT_ID,A.PACKAGE_ID,TO_CHAR(A.CAMPN_ID) CAMPN_ID,A.CAMPN_CODE,A.CAMPN_NAME,A.CAMPN_TYPE,TO_CHAR(A.OPER_FEE) OPER_FEE, ");
        sql.append("       TO_CHAR(A.FOREGIFT) FOREGIFT,TO_CHAR(A.ADVANCE_PAY) ADVANCE_PAY,TO_CHAR(A.SCORE_CHANGED) SCORE_CHANGED,TO_CHAR(A.CREDIT_VALUE) CREDIT_VALUE, ");
        sql.append("       A.MONTHS,A.ACTOR_PSPT_ID,A.ACTOR_PSPT_TYPE_CODE,A.ACTOR_PHONE,A.ACTOR_NAME,A.PROCESS_TAG,TO_CHAR(A.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, ");
        sql.append("       A.TRADE_STAFF_ID,TO_CHAR(A.RELATION_TRADE_ID) RELATION_TRADE_ID,TO_CHAR(A.CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE, ");
        sql.append("       A.CANCEL_STAFF_ID,A.CANCEL_CAUSE,TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        sql.append("       A.MODIFY_TAG,TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK, ");
        sql.append("       A.RSRV_NUM1,A.RSRV_NUM2,A.RSRV_NUM3,TO_CHAR(A.RSRV_NUM4) RSRV_NUM4,TO_CHAR(A.RSRV_NUM5) RSRV_NUM5,A.RSRV_STR1,A.RSRV_STR2, ");
        sql.append("       A.RSRV_STR3,A.RSRV_STR4,A.RSRV_STR5,TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        sql.append("       TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,A.RSRV_TAG1,A.RSRV_TAG2,A.RSRV_TAG3,A.INST_ID ");
        sql.append("  FROM TF_B_TRADE_SALE_ACTIVE A, TF_B_TRADE B ");
        sql.append(" WHERE A.TRADE_ID = B.TRADE_ID ");
        sql.append(" AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append("   AND B.USER_ID = TO_NUMBER(:USER_ID) ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids;
    }

    public static IDataset queryTradeSaleDepositsByUserId(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID,A.ACCEPT_MONTH,TO_CHAR(A.USER_ID) USER_ID,TO_CHAR(A.USER_ID_A) USER_ID_A,A.PRODUCT_ID, ");
        sql.append("       A.PACKAGE_ID,A.DISCNT_GIFT_ID,A.A_DISCNT_CODE,A.DEPOSIT_TYPE,TO_CHAR(A.INST_ID) INST_ID,TO_CHAR(A.CAMPN_ID) CAMPN_ID, ");
        sql.append("       A.MONTHS,TO_CHAR(A.LIMIT_MONEY) LIMIT_MONEY,A.PAY_MODE,TO_CHAR(A.FEE) FEE,A.PAYMENT_ID,A.IN_DEPOSIT_CODE,A.OUT_DEPOSIT_CODE, ");
        sql.append("       TO_CHAR(A.RELATION_TRADE_ID) RELATION_TRADE_ID,A.MODIFY_TAG,TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.append("       TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID, ");
        sql.append("       A.UPDATE_DEPART_ID,A.REMARK,A.RSRV_NUM1,A.RSRV_NUM2,A.RSRV_NUM3,TO_CHAR(A.RSRV_NUM4) RSRV_NUM4,TO_CHAR(A.RSRV_NUM5) RSRV_NUM5, ");
        sql.append("       A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3,A.RSRV_STR4,A.RSRV_STR5,TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1, ");
        sql.append("       TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
        sql.append("       A.RSRV_TAG1,A.RSRV_TAG2,A.RSRV_TAG3 ");
        sql.append("  FROM TF_B_TRADE_SALE_DEPOSIT A, TF_B_TRADE B ");
        sql.append(" WHERE A.TRADE_ID = B.TRADE_ID ");
        sql.append(" AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append("   AND B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND B.SUBSCRIBE_TYPE != '600' ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids;
    }

    public static IDataset queryTradeSaleGoodsByUserId(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("A.TRADE_ID,A.ACCEPT_MONTH,A.USER_ID,A.SERIAL_NUMBER_B,A.PRODUCT_ID, ");
        sql.append("A.PACKAGE_ID,A.INST_ID,A.CAMPN_ID,A.GOODS_ID,A.GOODS_NAME, ");
        sql.append("A.GOODS_NUM,A.GOODS_VALUE,A.GOODS_STATE,A.RES_TAG,A.RES_TYPE_CODE, ");
        sql.append("A.RES_ID,A.RES_CODE,A.DEVICE_MODEL_CODE,A.DEVICE_MODEL,A.DEVICE_COST, ");
        sql.append("A.DEVICE_BRAND_CODE,A.DEVICE_BRAND,A.DESTROY_FLAG,A.GIFT_MODE,A.POST_NAME, ");
        sql.append("A.POST_ADDRESS,A.POST_CODE,A.RELATION_TRADE_ID,TO_CHAR(A.ACCEPT_DATE,'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE,TO_CHAR(A.CANCEL_DATE,'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE, ");
        sql.append("A.MODIFY_TAG,TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK, ");
        sql.append("A.RSRV_NUM1,A.RSRV_NUM2,A.RSRV_NUM3,A.RSRV_NUM4,A.RSRV_NUM5, ");
        sql.append("A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3,A.RSRV_STR4,A.RSRV_STR5, ");
        sql.append("A.RSRV_STR6,A.RSRV_STR7,A.RSRV_STR8,A.RSRV_STR9,A.RSRV_STR10, ");
        sql.append("TO_CHAR(A.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(A.RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(A.RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,A.RSRV_TAG1,A.RSRV_TAG2, ");
        sql.append("A.RSRV_TAG3 ");
        sql.append("FROM TF_B_TRADE_SALE_GOODS A, TF_B_TRADE B ");
        sql.append("WHERE A.TRADE_ID = B.TRADE_ID ");
        sql.append("AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append("AND B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND B.SUBSCRIBE_TYPE != '600' ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids;

    }

    public static IDataset queryTradeSvcsByUserId(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID,A.ACCEPT_MONTH,TO_CHAR(A.USER_ID) USER_ID,TO_CHAR(A.USER_ID_A) USER_ID_A, ");
        sql.append("       A.SERVICE_ID,A.MAIN_TAG,TO_CHAR(A.INST_ID) INST_ID,TO_CHAR(A.CAMPN_ID) CAMPN_ID,TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.append("       TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG,TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("       A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK,A.RSRV_NUM1,A.RSRV_NUM2,A.RSRV_NUM3,TO_CHAR(A.RSRV_NUM4) RSRV_NUM4, ");
        sql.append("       TO_CHAR(A.RSRV_NUM5) RSRV_NUM5,A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3,A.RSRV_STR4,A.RSRV_STR5,TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1, ");
        sql.append("       TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
        sql.append("       A.RSRV_TAG1,A.RSRV_TAG2,A.RSRV_TAG3 ");
        sql.append("  FROM TF_B_TRADE_SVC A, TF_B_TRADE B ");
        sql.append(" WHERE B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append(" AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append("   AND A.TRADE_ID = B.TRADE_ID ");
        sql.append("   AND B.SUBSCRIBE_TYPE != '600' ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids;
    }

    public static IDataset queryTradeSvcsByUserId4Shopping(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID,A.ACCEPT_MONTH,TO_CHAR(A.USER_ID) USER_ID,TO_CHAR(A.USER_ID_A) USER_ID_A, ");
        sql.append("       A.SERVICE_ID,A.MAIN_TAG,TO_CHAR(A.INST_ID) INST_ID,TO_CHAR(A.CAMPN_ID) CAMPN_ID,TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.append("       TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG,TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("       A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK,A.RSRV_NUM1,A.RSRV_NUM2,A.RSRV_NUM3,TO_CHAR(A.RSRV_NUM4) RSRV_NUM4, ");
        sql.append("       TO_CHAR(A.RSRV_NUM5) RSRV_NUM5,A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3,A.RSRV_STR4,A.RSRV_STR5,TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1, ");
        sql.append("       TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
        sql.append("       A.RSRV_TAG1,A.RSRV_TAG2,A.RSRV_TAG3 ");
        sql.append("  FROM TF_B_TRADE_SVC A, TF_B_TRADE B ");
        sql.append(" WHERE B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append(" AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append(" AND A.TRADE_ID = B.TRADE_ID ");
        sql.append(" and (A.RSRV_TAG3 <> 'D' or A.RSRV_TAG3 is null) ");
        sql.append(" AND B.SUBSCRIBE_STATE <> 'A' ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids;
    }
    
    public static IDataset queryTradeOfferRelsByUserId4Shopping(String userId, String routeEparchyCode) throws Exception{
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	StringBuilder sql = new StringBuilder();
    	sql.append(" SELECT A.TRADE_ID,A.ACCEPT_MONTH,A.OFFER_CODE,A.OFFER_TYPE,A.OFFER_INS_ID,A.USER_ID,A.GROUP_ID,A.REL_OFFER_CODE,A.REL_OFFER_TYPE,A.REL_OFFER_INS_ID,A.REL_USER_ID,A.REL_TYPE,TO_CHAR(A.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG,TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK,A.INST_ID");
    	sql.append(" FROM TF_B_TRADE_OFFER_REL A, TF_B_TRADE B");
    	sql.append(" WHERE A.REL_USER_ID = TO_NUMBER(:USER_ID)");
    	sql.append(" AND B.TRADE_ID = A.TRADE_ID");
    	sql.append(" AND B.USER_ID = TO_NUMBER(:USER_ID)");
    	sql.append(" AND B.ACCEPT_MONTH = A.ACCEPT_MONTH");
    	IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids; 
    }
    
    public static IDataset queryTradeOfferRelsByUserId(String userId, String routeEparchyCode) throws Exception{
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	StringBuilder sql = new StringBuilder();
    	sql.append(" SELECT A.TRADE_ID,A.ACCEPT_MONTH,A.OFFER_CODE,A.OFFER_TYPE,A.OFFER_INS_ID,A.USER_ID,A.GROUP_ID,A.REL_OFFER_CODE,A.REL_OFFER_TYPE,A.REL_OFFER_INS_ID,A.REL_USER_ID,A.REL_TYPE,TO_CHAR(A.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG,TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK,A.INST_ID");
    	sql.append(" FROM TF_B_TRADE_OFFER_REL A, TF_B_TRADE B");
    	sql.append(" WHERE A.REL_USER_ID = TO_NUMBER(:USER_ID)");
    	sql.append(" AND B.TRADE_ID = A.TRADE_ID");
    	sql.append(" AND B.USER_ID = TO_NUMBER(:USER_ID)");
    	sql.append(" AND B.ACCEPT_MONTH = A.ACCEPT_MONTH");
    	sql.append(" AND B.SUBSCRIBE_TYPE != '600' ");
    	IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids; 
    }
    
    public static IDataset queryTradePricePlanByUserId4Shopping(String userId, String routeEparchyCode) throws Exception{
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	StringBuilder sql = new StringBuilder();
    	sql.append(" SELECT A.TRADE_ID,A.ACCEPT_MONTH,A.USER_ID_A,A.USER_ID,A.PRICE_PLAN_ID,A.INST_ID,A.PRICE_PLAN_TYPE,A.OFFER_INS_ID,A.OFFER_TYPE,A.PRICE_ID,A.BILLING_CODE,A.FEE_TYPE,A.FEE_TYPE_CODE,A.FEE,A.SPEC_TAG,A.RELATION_TYPE_CODE,TO_CHAR(A.START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_CHAR(A.END_DATE,'YYYY-MM-DD HH24:MI:SS'),A.MODIFY_TAG,TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS'),A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK");
    	sql.append(" FROM TF_B_TRADE_PRICE_PLAN A, TF_B_TRADE B");
    	sql.append(" WHERE A.USER_ID = TO_NUMBER(:USER_ID)");
    	sql.append(" AND B.USER_ID = TO_NUMBER(:USER_ID)");
    	sql.append(" AND B.TRADE_ID = A.TRADE_ID");
    	sql.append(" AND B.ACCEPT_MONTH = A.ACCEPT_MONTH");
    	IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids; 
    }
    
    public static IDataset queryTradePricePlanByUserId(String userId, String routeEparchyCode) throws Exception{
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	StringBuilder sql = new StringBuilder();
    	sql.append(" SELECT A.TRADE_ID,A.ACCEPT_MONTH,A.USER_ID_A,A.USER_ID,A.PRICE_PLAN_ID,A.INST_ID,A.PRICE_PLAN_TYPE,A.OFFER_INS_ID,A.OFFER_TYPE,A.PRICE_ID,A.BILLING_CODE,A.FEE_TYPE,A.FEE_TYPE_CODE,A.FEE,A.SPEC_TAG,A.RELATION_TYPE_CODE,TO_CHAR(A.START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_CHAR(A.END_DATE,'YYYY-MM-DD HH24:MI:SS'),A.MODIFY_TAG,TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS'),A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK");
    	sql.append(" FROM TF_B_TRADE_PRICE_PLAN A, TF_B_TRADE B");
    	sql.append(" WHERE A.USER_ID = TO_NUMBER(:USER_ID)");
    	sql.append(" AND B.USER_ID = TO_NUMBER(:USER_ID)");
    	sql.append(" AND B.TRADE_ID = A.TRADE_ID");
    	sql.append(" AND B.ACCEPT_MONTH = A.ACCEPT_MONTH");
    	sql.append(" AND B.SUBSCRIBE_TYPE != '600' ");
    	IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids; 
    }

    public static IDataset queryTradeSvcsStateByUserId(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT A.TRADE_ID,A.ACCEPT_MONTH,TO_CHAR(A.INST_ID) INST_ID,A.USER_ID,A.SERVICE_ID,A.MAIN_TAG,A.STATE_CODE,TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.append("       TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG,A.UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID, ");
        sql.append("       A.REMARK,A.RSRV_NUM1,A.RSRV_NUM2,A.RSRV_NUM3,A.RSRV_NUM4,A.RSRV_NUM5,A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3,A.RSRV_STR4, ");
        sql.append("       A.RSRV_STR5,TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        sql.append("       TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,A.RSRV_TAG1,A.RSRV_TAG2,A.RSRV_TAG3 ");
        sql.append("  FROM TF_B_TRADE_SVCSTATE A, TF_B_TRADE B ");
        sql.append(" WHERE A.TRADE_ID = B.TRADE_ID ");
        sql.append(" AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append("   AND B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND B.SUBSCRIBE_TYPE != '600' ");
        //BUG20200102102340双状态数据修复
        sql.append("   AND B.TRADE_TYPE_CODE != '7232' ");
        //BUG20200102102340双状态数据修复
        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids;
    }

    public static IDataset queryTradeUndoFinishActionsByPK(String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_TYPE_CODE", tradeTypeCode);

        IDataset set = Dao.qryByCode("TD_B_TRADEACTION", "SEL_TRADE_UNDO_FINISH_ACTION", param, Route.CONN_CRM_CEN);
        return set;
    }

    /**
     * 查询用户所有的属性，包括已生效的和将生效的
     * 
     * @author anwx@asiainfo-linkage.com @ 2013-3-4
     * @param pd
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserAllAttr(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT PARTITION_ID,TO_CHAR(USER_ID) USER_ID,INST_TYPE,INST_ID,ATTR_CODE,ATTR_VALUE,TO_CHAR(START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.append("       TO_CHAR(END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID, ");
        sql.append("       UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,TO_CHAR(RSRV_NUM4) RSRV_NUM4,TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("       RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,TO_CHAR(RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1, ");
        sql.append("       TO_CHAR(RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
        sql.append("       RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,ELEMENT_ID,RELA_INST_ID ");
        sql.append("  FROM TF_F_USER_ATTR ");
        sql.append(" WHERE USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("   AND END_DATE > SYSDATE ");
        sql.append("  AND END_DATE > START_DATE ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    public static IDataset queryUserAllPlatSvc(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("PARTITION_ID,USER_ID,SERVICE_ID, ");
        sql.append("BIZ_STATE_CODE,TO_CHAR(FIRST_DATE,'YYYY-MM-DD HH24:MI:SS') FIRST_DATE,TO_CHAR(FIRST_DATE_MON,'YYYY-MM-DD HH24:MI:SS') FIRST_DATE_MON,GIFT_SERIAL_NUMBER,GIFT_USER_ID, ");
        sql.append("INST_ID,IN_CARD_NO,ENTITY_CARD_NO,TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1, ");
        sql.append("RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1, ");
        sql.append("RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6, ");
        sql.append("RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10,TO_CHAR(RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 ");
        sql.append("FROM TF_F_USER_PLATSVC ");
        sql.append("WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
        sql.append("AND	  USER_ID = :USER_ID ");
        sql.append("AND   END_DATE > SYSDATE ");
        sql.append("  AND END_DATE > START_DATE ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    /**
     * 查询用户所有的服务，包括已生效的和将生效的
     * 
     * @author anwx@asiainfo-linkage.com @ 2013-3-4
     * @param pd
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserAllSvc(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("US.PARTITION_ID,US.USER_ID,US.USER_ID_A, ");
        sql.append("US.SERVICE_ID,US.MAIN_TAG,US.INST_ID,US.CAMPN_ID,TO_CHAR(US.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.append("TO_CHAR(US.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(US.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,US.UPDATE_STAFF_ID,US.UPDATE_DEPART_ID,US.REMARK, ");
        sql.append("US.RSRV_NUM1,US.RSRV_NUM2,US.RSRV_NUM3,US.RSRV_NUM4,US.RSRV_NUM5, ");
        sql.append("US.RSRV_STR1,US.RSRV_STR2,US.RSRV_STR3,US.RSRV_STR4,US.RSRV_STR5, ");
        sql.append("TO_CHAR(US.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(US.RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(US.RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,US.RSRV_TAG1,US.RSRV_TAG2, ");
        sql.append("US.RSRV_TAG3,US.RSRV_STR6,US.RSRV_STR7,US.RSRV_STR8,US.RSRV_STR9, ");
        sql.append("US.RSRV_STR10 ");
        sql.append("FROM TF_F_USER_SVC US ");
        sql.append("WHERE US.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
        sql.append("AND US.USER_ID=TO_NUMBER(:USER_ID) ");
        sql.append("AND US.END_DATE>=SYSDATE ");
        sql.append("  AND US.END_DATE > US.START_DATE ");
        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
//        IDataset ids = Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_ALLSVC_BY_USERID", param, Route.getCrmDefaultDb());
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(ids, null, null);// 填充productId和packageId
        
        return ids;
    }
    
    public static IDataset queryUserAllOfferRelByUserId(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT PARTITION_ID,INST_ID,OFFER_CODE,OFFER_TYPE,OFFER_INS_ID,USER_ID,GROUP_ID,REL_OFFER_CODE,REL_OFFER_TYPE,REL_OFFER_INS_ID,REL_USER_ID,REL_TYPE,TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK");
        sql.append(" FROM TF_F_USER_OFFER_REL A");
        sql.append(" WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000)");
        sql.append(" AND A.REL_USER_ID = TO_NUMBER(:USER_ID)");
        sql.append(" AND A.END_DATE > SYSDATE");
//        sql.append(" AND A.END_DATE > A.START_DATE");
        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }
    
    public static IDataset queryNPUserAllOfferRelByUserId(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT PARTITION_ID,INST_ID,OFFER_CODE,OFFER_TYPE,OFFER_INS_ID,USER_ID,GROUP_ID,REL_OFFER_CODE,REL_OFFER_TYPE,REL_OFFER_INS_ID,REL_USER_ID,REL_TYPE,TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK");
        sql.append(" FROM TF_F_USER_OFFER_REL A");
        sql.append(" WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000)");
        sql.append(" AND A.REL_USER_ID = TO_NUMBER(:USER_ID)");
//        sql.append(" AND A.END_DATE > SYSDATE");
//        sql.append(" AND A.END_DATE > A.START_DATE");
        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }
    
    public static IDataset queryUserAllPricePlanByUserId(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT A.USER_ID_A,A.USER_ID,A.PRICE_PLAN_ID,A.INST_ID,A.PRICE_PLAN_TYPE,A.OFFER_INS_ID, A.OFFER_TYPE,A.PRICE_ID,A.BILLING_CODE,A.FEE_TYPE,A.FEE_TYPE_CODE,A.FEE,A.SPEC_TAG,A.RELATION_TYPE_CODE,TO_CHAR(A.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK");
        sql.append(" FROM TF_F_USER_PRICE_PLAN A");
        sql.append(" WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000)");
        sql.append(" AND A.USER_ID = TO_NUMBER(:USER_ID)");
        sql.append(" AND A.END_DATE > SYSDATE");
        sql.append(" AND A.END_DATE > A.START_DATE");
        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }
    
    public static IDataset queryUserPricePlanByOfferInsId(String offerInsId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("OFFER_INS_ID", offerInsId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT A.USER_ID_A,A.USER_ID,A.PRICE_PLAN_ID,A.INST_ID,A.PRICE_PLAN_TYPE,A.OFFER_INS_ID, A.OFFER_TYPE,A.PRICE_ID,A.BILLING_CODE,A.FEE_TYPE,A.FEE_TYPE_CODE,A.FEE,A.SPEC_TAG,A.RELATION_TYPE_CODE,TO_CHAR(A.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK");
        sql.append(" FROM TF_F_USER_PRICE_PLAN A");
        sql.append(" WHERE A.OFFER_INS_ID = TO_NUMBER(:OFFER_INS_ID)");
        sql.append(" AND A.END_DATE > SYSDATE");
        sql.append(" AND A.END_DATE > A.START_DATE");
        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    public static IDataset queryUserAllTradePlatSvc(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT ");
        sql.append("A.TRADE_ID,A.ACCEPT_MONTH,A.USER_ID,A.SERVICE_ID,A.BIZ_STATE_CODE, ");
        sql.append("TO_CHAR(A.FIRST_DATE, 'YYYY-MM-DD HH24:MI:SS') FIRST_DATE,TO_CHAR(A.FIRST_DATE_MON, 'YYYY-MM-DD HH24:MI:SS') FIRST_DATE_MON, ");
        sql.append("A.GIFT_SERIAL_NUMBER,A.GIFT_USER_ID,A.IN_CARD_NO,A.ENTITY_CARD_NO,A.INST_ID,A.OPER_CODE,A.OPR_SOURCE,A.SUBSCRIBE_ID, ");
        sql.append("A.PKGSEQ,A.UDSUM,A.INTF_TRADE_ID,A.IS_NEED_PF,A.ACTIVE_TAG,TO_CHAR(A.OPER_TIME, 'YYYY-MM-DD HH24:MI:SS') OPER_TIME,  ");
        sql.append("A.MODIFY_TAG,TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,       ");
        sql.append("TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK, ");
        sql.append("A.RSRV_NUM1,A.RSRV_NUM2,A.RSRV_NUM3,A.RSRV_NUM4,A.RSRV_NUM5,A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3,A.RSRV_STR4, ");
        sql.append("A.RSRV_STR5,A.RSRV_STR6,A.RSRV_STR7,A.RSRV_STR8,A.RSRV_STR9,A.RSRV_STR10,A.RSRV_DATE1,A.RSRV_DATE2, ");
        sql.append("A.RSRV_DATE3,A.RSRV_TAG1,A.RSRV_TAG2,A.RSRV_TAG3            ");
        sql.append("FROM TF_B_TRADE_PLATSVC A,TF_B_TRADE B ");
        sql.append("WHERE B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND   A.TRADE_ID = B.TRADE_ID ");
        sql.append(" AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        sql.append("   AND B.SUBSCRIBE_TYPE != '600' ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeEparchyCode));
        return ids;
    }

    public static IDataset queryUserAllValidDiscnt(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT UD.PARTITION_ID,UD.USER_ID,UD.USER_ID_A,  ");
        sql.append("       UD.DISCNT_CODE,UD.SPEC_TAG,UD.RELATION_TYPE_CODE,UD.INST_ID,UD.CAMPN_ID, ");
        sql.append("       TO_CHAR(UD.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(UD.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(UD.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("       UD.UPDATE_STAFF_ID,UD.UPDATE_DEPART_ID,UD.REMARK,UD.RSRV_NUM1,UD.RSRV_NUM2,UD.RSRV_NUM3,UD.RSRV_NUM4,UD.RSRV_NUM5,UD.RSRV_STR1,UD.RSRV_STR2, ");
        sql.append("       UD.RSRV_STR3,UD.RSRV_STR4,UD.RSRV_STR5,TO_CHAR(UD.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(UD.RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        sql.append("       TO_CHAR(UD.RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,UD.RSRV_TAG1,UD.RSRV_TAG2,UD.RSRV_TAG3 ");
        sql.append("  FROM TF_F_USER_DISCNT UD ");
        sql.append(" WHERE UD.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND UD.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("   AND UD.END_DATE > SYSDATE ");
        sql.append("  AND UD.END_DATE > UD.START_DATE ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(ids, null, null);// 填充productId和packageId
        
        return ids;
    }

    public static IDataset queryUserAllValidRes(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(2500);
        sql.append("SELECT  ");
        sql.append("PARTITION_ID,USER_ID,USER_ID_A,RES_TYPE_CODE,RES_CODE, ");
        sql.append("IMSI,KI,INST_ID,CAMPN_ID,to_char(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.append("to_char(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,to_char(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK, ");
        sql.append("RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5, ");
        sql.append("RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5, ");
        sql.append("to_char(RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,to_char(RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,to_char(RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,RSRV_TAG1,RSRV_TAG2, ");
        sql.append("RSRV_TAG3 ");
        sql.append("FROM TF_F_USER_RES ");
        sql.append("WHERE USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND END_DATE > SYSDATE ");
        sql.append("AND END_DATE > START_DATE");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    public static IDataset queryValidSaleActives(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PROCESS_TAG", "0");

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT PARTITION_ID,TO_CHAR(USER_ID) USER_ID,SERIAL_NUMBER,SERIAL_NUMBER_B,PRODUCT_MODE,PRODUCT_ID,PRODUCT_NAME,PACKAGE_ID,PACKAGE_NAME, ");
        sql.append("       TO_CHAR(CAMPN_ID) CAMPN_ID,CAMPN_CODE,CAMPN_NAME,CAMPN_TYPE,TO_CHAR(OPER_FEE) OPER_FEE,TO_CHAR(FOREGIFT) FOREGIFT, ");
        sql.append("       TO_CHAR(ADVANCE_PAY) ADVANCE_PAY,TO_CHAR(SCORE_CHANGED) SCORE_CHANGED,TO_CHAR(CREDIT_VALUE) CREDIT_VALUE, ");
        sql.append("       MONTHS,END_MODE,ACTOR_PSPT_ID,ACTOR_PSPT_TYPE_CODE,ACTOR_PHONE,ACTOR_NAME,APPR_STAFF_ID,TO_CHAR(APPR_TIME, 'YYYY-MM-DD HH24:MI:SS') APPR_TIME, ");
        sql.append("       OUT_NET_PHONE,CONTRACT_ID,PROCESS_TAG,TO_CHAR(ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE,TRADE_STAFF_ID, ");
        sql.append("       TO_CHAR(RELATION_TRADE_ID) RELATION_TRADE_ID,TO_CHAR(CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE,CANCEL_STAFF_ID, ");
        sql.append("       CANCEL_CAUSE,TO_CHAR(START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        sql.append("       TO_CHAR(UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2, ");
        sql.append("       RSRV_NUM3,TO_CHAR(RSRV_NUM4) RSRV_NUM4,TO_CHAR(RSRV_NUM5) RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5, ");
        sql.append("       RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10,RSRV_STR11,RSRV_STR12,RSRV_STR13,RSRV_STR14,RSRV_STR15, ");
        sql.append("       RSRV_STR16,RSRV_STR17,RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21,RSRV_STR22,RSRV_STR23,RSRV_STR24,RSRV_STR25, ");
        sql.append("       TO_CHAR(RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
        sql.append("       RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,INST_ID ");
        sql.append("FROM TF_F_USER_SALE_ACTIVE     ");
        sql.append("WHERE USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND PROCESS_TAG = :PROCESS_TAG ");
        sql.append("AND END_DATE >SYSDATE ");
        sql.append("  AND END_DATE > START_DATE ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }
    
    public static IDataset queryValidOnNetSaleActives(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PROCESS_TAG", "0");

        IDataset ids = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ONNET_SALE_ACTIVE_END", param);
        return ids;
    }

    public static IDataset queryValidSaleDeposits(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("PARTITION_ID,USER_ID,USER_ID_A,PRODUCT_ID,PACKAGE_ID, ");
        sql.append("DISCNT_GIFT_ID,A_DISCNT_CODE,DEPOSIT_TYPE,INST_ID,CAMPN_ID, ");
        sql.append("MONTHS,LIMIT_MONEY,PAY_MODE,FEE,PAYMENT_ID, ");
        sql.append("IN_DEPOSIT_CODE,OUT_DEPOSIT_CODE,RELATION_TRADE_ID,TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1, ");
        sql.append("RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1, ");
        sql.append("RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,TO_CHAR(RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 ");
        sql.append("FROM TF_F_USER_SALE_DEPOSIT ");
        sql.append(" WHERE USER_ID=TO_NUMBER(:USER_ID) ");
        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
        sql.append("AND END_DATE > SYSDATE ");
        sql.append("  AND END_DATE > START_DATE ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }

    public static IDataset queryValidSaleGoods(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("PARTITION_ID,USER_ID,SERIAL_NUMBER_B,PRODUCT_ID,PACKAGE_ID, ");
        sql.append("INST_ID,CAMPN_ID,GOODS_ID,GOODS_NAME,GOODS_NUM, ");
        sql.append("GOODS_VALUE,GOODS_STATE,RES_TAG,RES_TYPE_CODE,RES_ID, ");
        sql.append("RES_CODE,DEVICE_MODEL_CODE,DEVICE_MODEL,DEVICE_COST,DEVICE_BRAND_CODE, ");
        sql.append("DEVICE_BRAND,DESTROY_FLAG,GIFT_MODE,POST_NAME,POST_ADDRESS, ");
        sql.append("POST_CODE,RELATION_TRADE_ID,TO_CHAR(ACCEPT_DATE,'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE,TO_CHAR(CANCEL_DATE,'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE,TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2, ");
        sql.append("RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2, ");
        sql.append("RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7, ");
        sql.append("RSRV_STR8,RSRV_STR9,RSRV_STR10,TO_CHAR(RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 ");
        sql.append("FROM TF_F_USER_SALE_GOODS ");
        sql.append("WHERE USER_ID=TO_NUMBER(:USER_ID) ");
        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
        sql.append("AND GOODS_STATE = '0' ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        return ids;
    }
    /*********************************************归档存储过程转java相关的查询方法 start*******************************************************************************************/
    /**
     * 根据表名查询该表归档对应的实现类
     * @param tabName
     * @return
     * @throws Exception
     */
    public static IDataset qryArchImpClassByTabName(String tabName)throws Exception{
    	IData param = new DataMap();
    	param.put("TAB_NAME", tabName);
    	param.put("PROC_TYPE", "ARCH");
    	IDataset results = Dao.qryByCode("TD_B_TRADE_PROC_CONFIG", "QRY_BY_TAB_NAME", param,Route.CONN_CRM_CEN);
    	return results;
    }
    
    /**
     * 查询归档默认的实现类
     * @param tabName
     * @return
     * @throws Exception
     */
    public static IDataset qryDefaultArchImpClass()throws Exception{
    	IData param = new DataMap();
    	param.put("PROC_TYPE", "ARCH");
    	param.put("TAB_NAME", "DEFAULT_CLASS");
    	IDataset results = Dao.qryByCode("TD_B_TRADE_PROC_CONFIG", "QRY_BY_TAB_NAME", param,Route.CONN_CRM_CEN);
    	return results;
    }
    /**
     * 查询主台账
     * @param tradeId
     * @param acceptDate
     * @param cancelTag
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeInfo(String tradeId,String acceptMonth,String cancelTag)throws Exception{
    	IData param = new DataMap();
    	param.put("TRADE_ID", tradeId);
    	param.put("ACCEPT_MONTH", acceptMonth);
    	param.put("CANCEL_TAG", cancelTag);
    	return Dao.qryByCode("TF_B_TRADE", "QRY_BY_TRADE_ID_CANCEL_TAG", param,Route.getJourDbDefault());
    }
    /**
     * 根据表名查询归档的sql配置
     * @param tabName
     * @return
     * @throws Exception
     */
    public static IDataset getArchSqlByTabName(String tabName)throws Exception{
    	IData param = new DataMap();
    	param.put("TAB_NAME", tabName);
    	param.put("PROC_TYPE", "ARCH");
    	return Dao.qryByCode("TD_B_TRADE_PROC_SQL", "QRY_BY_TAB_NAME", param,Route.CONN_CRM_CEN);
    }
    /**
     * 
     * @param userId
     * @param partitionId
     * @param feeTypeCode
     * @return
     * @throws Exception
     */
    public static long getGiftCountByGiftCode(String userId,String partitionId,String feeTypeCode)throws Exception{
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	param.put("PARTITION_ID", partitionId);
    	param.put("FEE_TYPE_CODE", feeTypeCode);
    	IDataset results = Dao.qryByCode("TF_F_USER_FOREGIFT", "GET_COUNT_BY_GIFT_CODE",param);
    	return results.first().getLong("COUNT");
    }
    
    /**
     * BUG20190305093135限制物联网测试期产品套餐不能重复订购问题
     * @param userId
     * @param routeEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryUserAllValidDiscntover(String userId, String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT UD.PARTITION_ID,UD.USER_ID,UD.USER_ID_A,  ");
        sql.append("       UD.DISCNT_CODE,UD.SPEC_TAG,UD.RELATION_TYPE_CODE,UD.INST_ID,UD.CAMPN_ID, ");
        sql.append("       TO_CHAR(UD.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(UD.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(UD.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("       UD.UPDATE_STAFF_ID,UD.UPDATE_DEPART_ID,UD.REMARK,UD.RSRV_NUM1,UD.RSRV_NUM2,UD.RSRV_NUM3,UD.RSRV_NUM4,UD.RSRV_NUM5,UD.RSRV_STR1,UD.RSRV_STR2, ");
        sql.append("       UD.RSRV_STR3,UD.RSRV_STR4,UD.RSRV_STR5,TO_CHAR(UD.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(UD.RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        sql.append("       TO_CHAR(UD.RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,UD.RSRV_TAG1,UD.RSRV_TAG2,UD.RSRV_TAG3 ");
        sql.append("  FROM TF_F_USER_DISCNT UD ");
        sql.append(" WHERE UD.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND UD.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("   AND UD.END_DATE <= SYSDATE ");
        sql.append("  AND UD.END_DATE > UD.START_DATE ");

        IDataset ids = Dao.qryBySql(sql, param, routeEparchyCode);
        
        StringBuilder sqlH = new StringBuilder(1000);
        sqlH.append("SELECT UD.PARTITION_ID,UD.USER_ID,UD.USER_ID_A,  ");
        sqlH.append("       UD.DISCNT_CODE,UD.SPEC_TAG,UD.RELATION_TYPE_CODE,UD.INST_ID,UD.CAMPN_ID, ");
        sqlH.append("       TO_CHAR(UD.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(UD.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(UD.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sqlH.append("       UD.UPDATE_STAFF_ID,UD.UPDATE_DEPART_ID,UD.REMARK,UD.RSRV_NUM1,UD.RSRV_NUM2,UD.RSRV_NUM3,UD.RSRV_NUM4,UD.RSRV_NUM5,UD.RSRV_STR1,UD.RSRV_STR2, ");
        sqlH.append("       UD.RSRV_STR3,UD.RSRV_STR4,UD.RSRV_STR5,TO_CHAR(UD.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(UD.RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        sqlH.append("       TO_CHAR(UD.RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,UD.RSRV_TAG1,UD.RSRV_TAG2,UD.RSRV_TAG3 ");
        sqlH.append("  FROM TF_FH_USER_DISCNT UD ");
        sqlH.append(" WHERE UD.USER_ID = TO_NUMBER(:USER_ID) ");
        sqlH.append("   AND UD.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        //sqlH.append("   AND UD.END_DATE <= SYSDATE ");
        sqlH.append("  AND UD.END_DATE > UD.START_DATE ");

        IDataset idsH = Dao.qryBySql(sqlH, param, routeEparchyCode);
        ids.addAll(idsH);
        
        //FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(ids, null, null);// 填充productId和packageId
        
        return ids;
    }
    /********************************************end**********************************************************************************/
    /**
     * REQ201907010036产品、优惠预约办理在生效时触发提醒 by liangdg3
     * 根据userId,dealState,dealType查询
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryExpireDealByUserId(IData inparams) throws Exception{
		StringBuilder sql = new StringBuilder(1000);
		sql.append(" Select to_char(DEAL_ID) DEAL_ID, ");
		sql.append(" EXEC_MONTH, ");
		sql.append(" to_char(USER_ID) USER_ID, ");
		sql.append(" DEAL_COND, ");
		sql.append(" EXEC_TIME, ");
		sql.append(" IN_TIME, ");
		sql.append(" DEAL_RESULT, ");
		sql.append(" DEAL_COND, ");
		sql.append(" DEAL_TYPE, ");
		sql.append(" SERIAL_NUMBER, ");
		sql.append(" EPARCHY_CODE, ");
		sql.append(" PARTITION_ID, ");
		sql.append(" LAST_DEAL_TIME, ");
		sql.append(" DEAL_COUNT,REMARK, ");
		sql.append(" TRADE_ID ");
		sql.append(" FROM TF_F_EXPIRE_DEAL ");
		sql.append(" WHERE USER_ID=TO_NUMBER(:USER_ID) ");
		sql.append(" AND DEAL_STATE = :DEAL_STATE ");
		sql.append(" AND DEAL_TYPE = :DEAL_TYPE ");
		return Dao.qryBySql(sql, inparams);
    }
    /**
     * REQ201907010036产品、优惠预约办理在生效时触发提醒 by liangdg3
     * 根据tradeId,dealState,dealType查询
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryExpireDealByTradeId(IData inparams) throws Exception {
		StringBuilder sql = new StringBuilder(1000);
		sql.append(" Select to_char(DEAL_ID) DEAL_ID, ");
		sql.append(" EXEC_MONTH, ");
		sql.append(" to_char(USER_ID) USER_ID, ");
		sql.append(" DEAL_COND, ");
		sql.append(" EXEC_TIME, ");
		sql.append(" IN_TIME, ");
		sql.append(" DEAL_RESULT, ");
		sql.append(" DEAL_COND, ");
		sql.append(" DEAL_TYPE, ");
		sql.append(" SERIAL_NUMBER, ");
		sql.append(" EPARCHY_CODE, ");
		sql.append(" PARTITION_ID, ");
		sql.append(" LAST_DEAL_TIME, ");
		sql.append(" DEAL_COUNT,REMARK, ");
		sql.append(" TRADE_ID ");
		sql.append("FROM TF_F_EXPIRE_DEAL ");
		sql.append("WHERE TRADE_ID=TO_NUMBER(:TRADE_ID) ");
		sql.append("AND DEAL_STATE = :DEAL_STATE ");
		sql.append("AND DEAL_TYPE = :DEAL_TYPE ");
		return Dao.qryBySql(sql, inparams);
		
	}
    
    
    
    /**
     * by chenyw7
     * 根据serialNumber,dealState,dealType查询
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryExpireDealBySerialNumber(IData inparams) throws Exception{
		StringBuilder sql = new StringBuilder(1000);
		sql.append(" Select to_char(DEAL_ID) DEAL_ID, ");
		sql.append(" EXEC_MONTH, ");
		sql.append(" to_char(USER_ID) USER_ID, ");
		sql.append(" DEAL_COND, ");
		sql.append(" EXEC_TIME, ");
		sql.append(" IN_TIME, ");
		sql.append(" DEAL_RESULT, ");
		sql.append(" DEAL_COND, ");
		sql.append(" DEAL_TYPE, ");
		sql.append(" SERIAL_NUMBER, ");
		sql.append(" EPARCHY_CODE, ");
		sql.append(" PARTITION_ID, ");
		sql.append(" LAST_DEAL_TIME, ");
		sql.append(" DEAL_COUNT,REMARK, ");
		sql.append(" TRADE_ID ");
		sql.append(" FROM TF_F_EXPIRE_DEAL ");
		sql.append(" WHERE SERIAL_NUMBER= :SERIAL_NUMBER ");
		sql.append(" AND DEAL_STATE = :DEAL_STATE ");
		sql.append(" AND DEAL_TYPE = :DEAL_TYPE ");
		return Dao.qryBySql(sql, inparams);
    }
	 public static IDataset queryUserAllPricePlanByUserIdAndofferInsId(String userId, String userIdA) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);
        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT A.USER_ID_A,A.USER_ID,A.PRICE_PLAN_ID,A.INST_ID,A.PRICE_PLAN_TYPE,A.OFFER_INS_ID, A.OFFER_TYPE,A.PRICE_ID,A.BILLING_CODE,A.FEE_TYPE,A.FEE_TYPE_CODE,A.FEE,A.SPEC_TAG,A.RELATION_TYPE_CODE,TO_CHAR(A.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK");
        sql.append(" FROM TF_F_USER_PRICE_PLAN A");
        sql.append(" WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000)");
        sql.append(" AND A.USER_ID = TO_NUMBER(:USER_ID)");
        sql.append(" AND A.USER_ID_A = TO_NUMBER(:USER_ID_A)");
        sql.append("   AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        IDataset ids = Dao.qryBySql(sql, param);
        return ids;
    }
    
}
