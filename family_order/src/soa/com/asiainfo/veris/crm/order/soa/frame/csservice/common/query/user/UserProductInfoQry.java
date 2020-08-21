
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class UserProductInfoQry
{
    /**
     * 根据成员用户ID和成员产品ID查询集团用户ID
     * 
     * @param memberUserId
     *            成员用户ID
     * @param memberProductId
     *            成员产品ID
     * @return String 集团用户ID
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String getGroupUserIdFromUserProduct(String memberUserId, String memberProductId) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", memberUserId);
        param.put("PRODUCT_ID", memberProductId);
        IDataset result = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_PRODUCTID_USERID", param);
        if (!IDataUtil.isEmpty(result))
        {
            return result.getData(0).getString("USER_ID_A", "");
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_143, memberUserId, memberProductId);
            return null;
        }
    }

    public static IDataset getMainUserProductInfoByCstId(String cust_id, String product_id, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        param.put("PRODUCT_ID", product_id);

        return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRODUCTID", param, pagination, Route.CONN_CRM_CG);

    }

    /**
     * 根据产品类型查询用户已订购的产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getMebProdNoPriv(IData data) throws Exception
    {

        // TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_MEB_BY_PROD_NO_PRIV", data);
    }

    /**
     * 根据集团编码groupId关联查询出对应用户订购的所有产品 modify_liuxx3_20140515_01
     */
    public static IDataset getProductId(String groupId) throws Exception
    {

        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_PRODUCTID", param, Route.CONN_CRM_CG);
    }

    public static IDataset getProductInfo(String user_id, String user_id_a) throws Exception
    {
        IDataset dataset = getProductInfo(user_id, user_id_a, null);
        return dataset;
    }

    /**
     * @Function: getProductInfo
     * @Description: 获取产品信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:46:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getProductInfo(String user_id, String user_id_a, String routeId) throws Exception
    {
        if (StringUtils.isBlank(user_id))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "USER_ID不能为空");
        }

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_PRODUCT", "SEL_ALL_EXIST_BY_USERID", param, routeId);
        return dataset;
    }

    /**
     * @Function: getSEL_GROUP_MEMBER_ALLPRODUCT
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:47:07 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getSEL_GROUP_MEMBER_ALLPRODUCT(String user_id, String user_id_a, String eparchyCode) throws Exception
    {

        // TODO getVisit().setRouteEparchyCode(eparchyCode);
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_GROUP_MEMBER_ALLPRODUCT", param);
    }

    /**
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:50:42 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getSEL_PLUS_BY_USERID_USERIDA(String user_id, String user_id_a) throws Exception
    {
        return getSEL_PLUS_BY_USERID_USERIDA(user_id, user_id_a, null);
    }

    /**
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:51:16 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getSEL_PLUS_BY_USERID_USERIDA(String user_id, String user_id_a, String routeId) throws Exception
    {

        // getVisit().setRouteEparchyCode( eparchyCode);
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PLUS_BY_USERID_USERIDA", param, routeId);
    }

    /*
     * 根据USER_ID和PRODUCT_MODE和USER_ID_A查询
     */
    public static IDataset getSEL_USERPRODUCT_BYMOD(IData params, String eparchyCode) throws Exception
    {
        // TODO code_code表里没有
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_GRP_BYMOD", params);
    }

    public static IDataset getUserAllProducts(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_ALL_USER_PRODUCT", param);
    }

    /**
     * @Function: getUserMemberProductsByByProdType
     * @Description: 根据产品类型查询用户已订购的产品
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:54:15 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserMemberProductsByByProdType(String cust_id, String user_id, String user_id_a, String trade_staff_id) throws Exception
    {

        // TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        param.put("TRADE_STAFF_ID", trade_staff_id);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_MEB_BY_PROD_TYPE", param);
    }

    /**
     * @Function: getUserPlusProductByUserId
     * @Description 根据user_Id查询用户已订购的附加产品
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:55:55 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserPlusProductByUserId(String user_id, String trade_staff_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("TRADE_STAFF_ID", trade_staff_id);
        // TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PLUS_BY_USERID", param);
    }

    /**
     * @Function: getUserProductBykey
     * @Description: 根据USER_Id、USER_ID_A,PRODUCT_ID查询用户产品信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:57:15 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IData getUserProductBykey(String user_id, String product_id, String user_id_a, Pagination page) throws Exception
    {
        if (StringUtils.isBlank(user_id) || StringUtils.isBlank(product_id))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "USER_ID和PRODUCT_ID不能为空");
        }

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("PRODUCT_ID", product_id);
        param.put("USER_ID_A", user_id_a);
        IDataset userproducts = Dao.qryByCodeParser("TF_F_USER_PRODUCT", "SEL_USERPRODUCT_BYKEY", param, page);

        if (userproducts.size() > 0)
        {
            return userproducts.getData(0);
        }
        else
        {
            return null;
        }
    }

    /**
     * 根据开始时间，结束时间查询
     * 
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static IDataset getUserProductByStartEndDate(String userId, String userIdA, String startDate, String endDate) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("USER_ID_A", userIdA);
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_USERID_STARTDATE_ENDDATE", params);
    }

    /**
     * @Function: getUserProductByUserId
     * @Description: 根据user_Id查询用户已订购的主产品和附加产品 不包括营销活动产生的附加产品
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:58:39 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserProductByUserId(String user_id, String trade_staff_id, String eparchyCode) throws Exception
    {

        // getVisit().setRouteEparchyCode( eparchyCode);
        // TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("TRADE_STAFF_ID", trade_staff_id);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_USERID", param, eparchyCode);
    }

    /**
     * @Function: getUserProductByUserIdProductId
     * @Description: 查询用户当前生效产品
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-7-17 上午11:12:47 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-7-17 lijm3 v1.0.0 修改原因
     */
    public static IDataset getUserProductByUserIdProductId(String userId, String product_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", product_id);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_PRODUCTID_USERID", param);
    }
    
    /**
     * @author yanwu
     * @param userId
     * @param product_id
     * @return 
     * @throws Exception
     */
    public static IDataset getUserProductByUserIdEnd(String userId, String product_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", product_id);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_PRODUCTID_USERID_END", param);
    }

    /**
     * @Function: getUserProductByValue
     * @Description: 根据USER_Id、USER_ID_A,PRODUCT_ID查询用户产品信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:00:24 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserProductByValue(String user_id, String user_id_a, String product_mode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        param.put("PRODUCT_MODE", product_mode);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USERPRODUCT_BYMOD", param, page);
    }

    /**
     * @Function: GetUserProductInfo
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:02:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset GetUserProductInfo(String user_id, String user_id_a, String product_id, String product_mode, String eparchyCode) throws Exception
    {
        if (StringUtils.isBlank(user_id) || StringUtils.isBlank(product_id))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "USER_ID和PRODUCT_ID不能为空");
        }

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        param.put("PRODUCT_ID", product_id);
        param.put("PRODUCT_MODE", product_mode);
        // getVisit().setRouteEparchyCode( eparchyCode);
        return Dao.qryByCodeParser("TF_F_USER_PRODUCT", "SEL_BY_NOTNULL", param, eparchyCode);
    }
    
    /**
     * 
     * @Title: getUserProductInfoByUserIdAndInstId  
     * @Description: 根据userid和instid查询产品信息  
     * @param @param user_id
     * @param @param instId
     * @param @return
     * @param @throws Exception    设定文件  
     * @return IDataset    返回类型  
     * @throws
     */
    public static IDataset getUserProductInfoByUserIdAndInstId(String user_id, String instId) throws Exception
    {
        if (StringUtils.isBlank(user_id) || StringUtils.isBlank(instId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "USER_ID和INST_ID不能为空");
        }

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("INST_ID", instId);
        
        return Dao.qryByCodeParser("TF_F_USER_PRODUCT", "SEL_BY_USERID_INSTID", param);
    }

    /**
     * @description 判断客户是否订购了某个产品，根据cust_id,product_id去查询
     * @param cust_id
     * @param product_id
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getUserProductInfoByCstId(String cust_id, String product_id, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        param.put("PRODUCT_ID", product_id);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_CUSTID_PRODID", param, pagination, Route.CONN_CRM_CG);

    }

    /**
     * 通过用户ID,集团用户ID,产品ID查询用户产品表
     * 
     * @param userId
     * @param userIdA
     * @param product_id
     * @return
     * @throws Exception
     */
    public static IDataset getUserProductInfoByUserIdAndUserIdAProductId(String userId, String userIdA, String product_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);
        param.put("PRODUCT_ID", product_id);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_USERID_USERIDA_PRODID", param);
    }

    /**
     * 查询用户订购的宽带产品
     * 
     * @param userId
     * @param productMode
     * @return
     * @throws Exception
     */
    public static IDataset getUserWidenetProductInfo(String userId, String productMode) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("PRODUCT_MODE", productMode);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_USERID_MODE", params);
    }

    /**
     * @param userId
     * @param userIdA
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMebProduct(String userId, String userIdA) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_GROUP_MEMBER_ALLPRODUCT", param);
    }
    
    /**
     * @param userId
     * @param userIdA
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMebProductByUserIdUserIdaProductId(String userId, String userIdA, String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);
        param.put("PRODUCT_ID", productId);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_GROUP_MEMBER_PRODUCT", param);
    }
    
    /**
     * @author yanwu
     * @param userId
     * @param userIdA
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMebProductEnd(String userId, String userIdA) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_GROUP_MEMBER_PRODUCT_END", param);
    }

    /**
     * 根据userId查询产品表中的用户最近主产品信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryLasterMainProdInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID) USER_ID, TO_CHAR(USER_ID_A) USER_ID_A, ");
        sql.append("PRODUCT_ID, PRODUCT_MODE, BRAND_CODE, TO_CHAR(INST_ID) INST_ID, ");
        sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, ");
        sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3, MAIN_TAG ");
        sql.append("FROM TF_F_USER_PRODUCT T ");
        sql.append("WHERE T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND T.USER_ID = :USER_ID ");
        sql.append("AND T.MAIN_TAG = '1' ");
        sql.append("AND T.START_DATE =  ");
        sql.append("(SELECT MAX(B.START_DATE) ");
        sql.append("FROM TF_F_USER_PRODUCT B ");
        sql.append("WHERE B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND B.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND B.MAIN_TAG = '1' ");
        sql.append("AND END_DATE >START_DATE)");

        IDataset ids = Dao.qryBySql(sql, param);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData map = ids.getData(0);

        map.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(map.getString("PRODUCT_ID")));
        map.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(map.getString("BRAND_CODE")));

        return map;
    }

    /**
     * 根据userId查询历史产品表中的用户最近主产品信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryLasterMainProdInfoByUserIdFromHis(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID) USER_ID, TO_CHAR(USER_ID_A) USER_ID_A, ");
        sql.append("PRODUCT_ID, PRODUCT_MODE, BRAND_CODE, TO_CHAR(INST_ID) INST_ID, ");
        sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, ");
        sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3, MAIN_TAG ");
        sql.append("FROM TF_F_USER_PRODUCT_H T ");
        sql.append("WHERE T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND T.USER_ID = :USER_ID ");
        sql.append("AND T.MAIN_TAG = '1' ");
        sql.append("AND T.START_DATE =  ");
        sql.append("(SELECT MAX(B.START_DATE) ");
        sql.append("FROM TF_F_USER_PRODUCT_H B ");
        sql.append("WHERE B.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND B.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND B.MAIN_TAG = '1' ");
        sql.append("AND END_DATE >START_DATE)");

        IDataset ids = Dao.qryBySql(sql, param);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData map = ids.getData(0);

        map.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(map.getString("PRODUCT_ID")));
        map.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(map.getString("BRAND_CODE")));

        return map;
    }

    /**
     * @Function: qryUserPrdByGrpId
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:03:23 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset qryUserPrdByGrpId(String group_id) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", group_id);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT u.partition_id,u.user_id,p.group_id,u.serial_number,p.audit_state, ");
        parser.addSQL("        p.cust_name,u.open_date,t.product_id,t.product_name,t.start_date,t.end_date ");
        parser.addSQL(" FROM   tf_f_user u,tf_f_cust_group p,td_b_product t ");
        parser.addSQL(" WHERE  u.cust_id=p.cust_id ");
        parser.addSQL("        AND u.product_id=t.product_id ");
        parser.addSQL("        AND u.open_date < t.end_date ");
        parser.addSQL("        AND t.start_date+0<sysdate AND t.end_date+0>=sysdate ");
        parser.addSQL("        AND p.group_id= :GROUP_ID");
        parser.addSQL("        AND u.remove_tag='0' ");

        IDataset resIds = Dao.qryByParse(parser);

        return resIds;
    }

    /**
     * 根据用户ID查询有效的主产品信息 该方法根据END_DATE进行降序排列
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryMainProduct(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_MAIN_PRODUCT_BY_PK", param);
    }

    public static IDataset queryMainProductNow(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_MAINPROD_VALID", param);
    }

    public static IDataset queryNextProductBySn(String serialNumber, String removeTag) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("SERIAL_NUMBER", serialNumber);
        inParam.put("REMOVE_TAG", removeTag);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_NEXTPRODUCT", inParam);
    }

    /**
     * 根据USER_ID查询产品信息 接口用
     * 
     * @param userId
     * @author huangsl
     */
    public static IDataset queryProdInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PROD_INFO_BY_USER_ID", param);
    }
    
    public static IDataset queryIntfProdInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT T.PRODUCT_ID, ");
        sql.append("       TO_CHAR(T.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.append("       TO_CHAR(T.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE ");
        sql.append("  FROM TF_F_USER_PRODUCT T ");
        sql.append(" WHERE T.USER_ID = :USER_ID ");
        sql.append("   AND SYSDATE <= T.END_DATE ");
        sql.append("   AND NOT EXISTS (SELECT 1 ");
        sql.append("          FROM TD_S_COMMPARA C ");
        sql.append("         WHERE C.SUBSYS_CODE = 'CSM' ");
        sql.append("           AND C.PARAM_ATTR = '3' ");
        sql.append("           AND C.PARA_CODE1 = 'SJYYT' ");
        sql.append("           AND C.END_DATE > SYSDATE ");
        sql.append("           AND C.PARAM_CODE = T.PRODUCT_ID) ");


        return Dao.qryBySql(sql,param);
    }
    
    /**
     * 查询用户的主产品信息
     * 
     * @param userId
     * @return
     * @throws Exception
     *             wangjx 2013-7-11
     */
    public static IDataset queryProductByIdNext(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_USERID_NEXT", param);
    }

    /**
     * 根据用户ID查询有效的产品信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryProductByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_PRODUCT", "SEL_ALL_BY_USERID", param);
        return dataset;
    }

    public static IDataset queryProductByUserIdAndStartDate(String startDate, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("START_DATE", startDate);
        IDataset uproducts = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_USERID_STARTDATE", param);

        return uproducts;
    }

    /**
     * 根据用户ID分库查询有效的用户产品信息
     * 
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     *             wangjx 2013-8-1
     */
    public static IDataset queryProductByUserIdFromDB(String userId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_ALL_BY_USERID", param, routeId);
    }

    public static IDataset queryProuctByBrand(String brandCode) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("BRAND_CODE", brandCode);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PRODUCT_BY_BRAND_NEW", inParam, Route.CONN_CRM_CEN);
    }

    public static IDataset queryUserMainBrand(String userId) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_MAIN_BRAND", param);
    }

    /**
     * 查询用户的主产品信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserMainProduct(String userId) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_MAIN_PRODUCT", param);
    }

    /**
     * 查询用户的主产品信息
     * 
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserMainProduct(String userId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_MAIN_PRODUCT", param, routeId);
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryUserProduct(String userId) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_PRODUCT", "USER_PRODUCT_SEL", param);
    }

    /**
     * @Function: queryUserProductByUserId
     * @Description: 根据userId查询产品
     * @param: @param userId
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 3:57:18 PM Jul 25, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Jul 25, 2013 longtian3 v1.0.0 TODO:
     *        
     *        原sql拆分，查询TD_B_PRODUCT表改为调产商品接口  modify by duhj  2017/03/19
     */
    public static IDataset queryUserProductByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        IDataset results= Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PRODUCT_BY_USERID_NEW", param);//原sql SEL_PRODUCT_BY_USERID
                
        IData temp = new DataMap();
        String productId = "";
        if(IDataUtil.isNotEmpty(results)){
        	for (int i = 0; i < results.size(); i++) {
        		temp = results.getData(i);
        		productId = temp.getString("PRODUCT_ID");				
				IData  res=UProductInfoQry.getProductInfo(productId);
				if(IDataUtil.isEmpty(res)){
					results.remove(i);
					i--;
				}else{
					temp.put("PRODUCT_NAME", res.getString("PRODUCT_NAME"));
					temp.put("PRODUCT_EXPLAIN", res.getString("PRODUCT_EXPLAIN"));

				}
			}
        }
        
        return results;
    }
    
    
    public static IDataset queryUserProductByUserIdDate(String userId, String timePoint) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TIME_POINT", timePoint);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_UID_DATE", param);
    }
    
    /**
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUserTDProductByUserId(String userId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        IDataset userDiscnts = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_TD_PRODUCT_BY_USER_ID", inparams);
        return userDiscnts;
    }
    
    /**
     * 
     * @param custId
     * @return
     * @throws Exception
     */
    public static IDataset getGrpBBossProductByGrpCustId(String custId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("CUST_ID", custId);
        IDataset userProductInfo = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_GRPBBOSS_PRODUCT_BY_GRP_CUSTID", inparams);
        return userProductInfo;
    }
    
    public static IDataset queryAllUserValidMainProducts(String userId)throws Exception{
    	IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "QRY_USER_ALL_VALID_MAIN_PRODUCTS", param);
    	
    }
    
    public static IDataset queryUserValidMainProduct(String userId, String productId)throws Exception{
    	IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "QRY_USER_VALID_MAIN_PRODUCTS", param);
    }

    public static IDataset queryUserValidMainProduct_1(String userId)throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "QRY_USER_VALID_MAIN_PRODUCTS_1", param);
    }
    
    /**
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUserChangeXiangProductByUserId(String userId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        IDataset userDiscnts = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_CHANGEXIANG_SALE_BY_USER_ID", inparams);
        return userDiscnts;
    }
    
    /**
     * 
     * @param custId
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getGrpProductByGrpCustIdProID(String custId,String productId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("CUST_ID", custId);
        inparams.put("PRODUCT_ID", productId);
        IDataset userProductInfo = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_CUSTID_PRODID_FORIMS", inparams);
        return userProductInfo;
    }
    
    /**
     * 查询集团统一付费产品的信息
     * @param custId
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getGrpProductByCustIdForUPGP(String custId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("CUST_ID", custId);
        IDataset userProductInfo = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PRODUCT_BYCUSTID_FORUPGP", inparams);
        return userProductInfo;
    }
    
    public static IDataset queryUserMainProductByCommpara(String userId, String productId)throws Exception{
    	IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "QRY_USER_VALID_MAIN_PRODUCTSC", param);
    }

    public static IDataset isHasAnyBookProduct(String userId, String productId)throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "IS_BOOK_ANY_PRODUCT", param);
    }
    
    public static IDataset getuserProductByUserIdInstId(String userId, String instId)throws Exception
    {
    	IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_ID", instId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID) USER_ID, TO_CHAR(USER_ID_A) USER_ID_A, ");
        sql.append("PRODUCT_ID, PRODUCT_MODE, BRAND_CODE, TO_CHAR(INST_ID) INST_ID, ");
        sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, ");
        sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3, MAIN_TAG ");
        sql.append("FROM TF_F_USER_PRODUCT T ");
        sql.append("WHERE T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND T.USER_ID = :USER_ID ");
        sql.append("AND T.INST_ID = :INST_ID ");
        sql.append("AND END_DATE > START_DATE");

        IDataset ids = Dao.qryBySql(sql, param);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }
        return ids;
    }

    /**
     * 集团工作手机成员限制的查询
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryWorkPhoneGrpMebByUserId(String userId)throws Exception{
    	IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_USERID_FOR_WORKPHONEMEB", param);
    }


	 /**
     * k3
     * 关联用户和产品表获取user_id
     * @param serial_number
     * @return
     * @throws Exception
     */
    public static IDataset getUserIdBySn(String serial_number)throws Exception{
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", serial_number);
    	StringBuilder sql = new StringBuilder(1000);
    	sql.append("SELECT a.user_id FROM tf_f_user a,tf_f_user_product b ");
    	sql.append("WHERE a.SERIAL_NUMBER=:SERIAL_NUMBER ");
    	sql.append("AND a.user_id=b.user_id ");
    	sql.append("AND a.remove_tag='0' ");
    	sql.append("AND b.main_tag='1' ");
    	sql.append("AND b.product_id='7050' ");
    	sql.append("AND b.end_date > sysdate ");
    	return Dao.qryBySql(sql, param);
    }
    public static IDataset qryUserMainProdInfoByUserIdProductId(String userId, String productId) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_MAIN_PRODUCT_BY_USER_ID_PRODUCT_ID", param);
    }

    public static IDataset qryUserMainProdInfoByUserIdAndUserIdAProductId(String userId,String userIdA,String instId) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);
        param.put("INST_ID", instId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_MAIN_PRODUCT_BY_USER_ID_AND_USERIDA", param);
    }

    /**
     * 根据集团编码groupId关联查询出对应相关信息
     */
    public static IDataset getServceInfoForProductId(String groupId) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_MAIN_PRODUCT_INFO_BY_GROUP_ID", param);
    }

    public static IDataset getXxtValidProduct(String userId) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_XXT_VALID_PRODUCT_BY_USER_ID", param);
    }
	
	/**
     * @author liwei29
     * @param 查询预约宽带产品
     */
    
    public static IDataset queryWidenetFoward(String userId)throws Exception{
    	IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_USERID_FOR_WIDENETFOWARD", param);
    }

}
