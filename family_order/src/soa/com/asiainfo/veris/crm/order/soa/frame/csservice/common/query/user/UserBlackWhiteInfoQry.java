
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class UserBlackWhiteInfoQry
{

    /**
     * @Function: getBlackUserInfo
     * @Description: 查询用户黑名单信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:11:58 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getBlackUserInfo(String user_id, String group_id, String serial_number, String user_type_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("GROUP_ID", group_id);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("USER_TYPE_CODE", user_type_code);
        return Dao.qryByCodeParser("TF_F_USER_BLACKWHITE", "SEL_BY_BLACKUSER", param);
    }

    /**
     * @Function: getBlackWhitedata
     * @Description: 根据user_id groupid serviceid查询成员黑白名单信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:14:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getBlackWhitedata(String user_id, String group_id, String service_id) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", user_id);
        idata.put("GROUP_ID", group_id);
        idata.put("SERVICE_ID", service_id);
        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_USERID_GROUPID_SVCID", idata);
    }

    /**
     * todo getVisit().setRouteEparchyCode(eparchyCode);这种代码怎么处理
     *
     * @Function: getBlackWhitedataByGSS
     * @Description: 通过GROUP_ID、SERVICE_ID、SERIAL_NUMBER查询成员黑白名单信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:15:23 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getBlackWhitedataByGSS(IData params) throws Exception
    {

        String eparchyCode = params.getString("EPARCHY_CODE");
        // TODO getVisit().setRouteEparchyCode(eparchyCode);
        // return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_GROUPID_SERVICEID_SN", params, eparchyCode);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT * FROM TF_F_USER_BLACKWHITE ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL(" AND EC_USER_ID = :EC_USER_ID");
        parser.addSQL(" AND SERVICE_ID = :SERVICE_ID");
        parser.addSQL(" AND USER_TYPE_CODE = :USER_TYPE_CODE");
        parser.addSQL(" AND BIZ_CODE = :BIZ_CODE");
        parser.addSQL(" AND sysdate BETWEEN start_date AND end_date");
        return Dao.qryByParse(parser, eparchyCode);
    }

    /**
     * 通过EC_USER_ID、SERVICE_ID查询成员黑白名单信息
     *
     * @param params
     * @return IDataset
     * @throws Exception
     * @author chenlei
     */
    public static IDataset getBlackWhitedataByGSSAndUserType(String serial_number, String ec_user_id, String service_id, String user_type_code, String biz_code, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serial_number);
        data.put("EC_USER_ID", ec_user_id);
        data.put("SERVICE_ID", service_id);
        data.put("USER_TYPE_CODE", user_type_code);
        data.put("BIZ_CODE", biz_code);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT * FROM TF_F_USER_BLACKWHITE ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL(" AND EC_USER_ID = :EC_USER_ID");
        parser.addSQL(" AND SERVICE_ID = :SERVICE_ID");
        parser.addSQL(" AND USER_TYPE_CODE = :USER_TYPE_CODE");
        parser.addSQL(" AND BIZ_CODE = :BIZ_CODE");
        parser.addSQL(" AND sysdate BETWEEN start_date AND end_date");
        return Dao.qryByParse(parser, eparchyCode);
    }

    /**
     * @param user_id
     * @param ec_user_id
     * @return
     * @throws Exception
     */
    public static IDataset getBlackWhitedataByUserIdEcuserid(String user_id, String ec_user_id) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", user_id);
        idata.put("EC_USER_ID", ec_user_id);
        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_USERID_USERIDA", idata);
    }

    /**
     * todo getVisit().setRouteEparchyCode((String) AppCtx.getAttribute("MEM_EPARCHY_CODE")); 怎么处理
     *
     * @Function: getBlackWhitedataSet
     * @Description: 根据user_id groupid serviceid查询成员黑白名单信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午11:13:26 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getBlackWhitedataSet(String user_id, String group_id) throws Exception
    {

        // TODO getVisit().setRouteEparchyCode((String) AppCtx.getAttribute("MEM_EPARCHY_CODE"));
        IData idata = new DataMap();
        idata.put("USER_ID", user_id);
        idata.put("GROUP_ID", group_id);
        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_USERID_GROUPID", idata);
    }

    /**
     * todo getVisit().setRouteEparchyCode((String) AppCtx.getAttribute("MEM_EPARCHY_CODE")); 怎么处理
     *
     * @Function: getBlackWhitedatauserIdUserIdaSvcid
     * @Description: 根据user_id userIda serviceid查询成员黑白名单信息
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午11:37:47 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getBlackWhitedatauserIdUserIdaSvcid(String userId, String userIdA, String serviceId) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("EC_USER_ID", userIdA);
        idata.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_USERID_USERIDA_SVCID", idata);
    }

    /**
     * @description 查询黑白名单信息
     * @param serialNumber
     * @param groupId
     * @param serviceId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getBlackWhiteInfo(String serialNumber, String groupId, String serviceId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("GROUP_ID", groupId);
        data.put("SERVICE_ID", serviceId);

        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_SN_GRPID_SERID", data, eparchyCode);
    }

    public static IDataset getBlackWhiteInfoBySnEcuserid(String serialNumber, String ecuserId) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("EC_USER_ID", ecuserId);

        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_SN_ECUSERID", data);
    }

    public static IDataset getBlackWhiteInfoBySnEcuseridSerid(String serialNumber, String ecuserId, String serviceId) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("EC_USER_ID", ecuserId);
        data.put("SERVICE_ID", serviceId);

        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_SN_ECUSERID_SERID", data);
    }

    /**
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getBlackWhiteInfoByUserID(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCodeParser("TF_F_USER_BLACKWHITE", "SEL_BY_USERID1", param);

    }

    /**
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset getBlackWhiteInfoBySn(String serialNumber, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCodeParser("TF_F_USER_BLACKWHITE", "SEL_ALL_BY_SN", param, eparchyCode);

    }

    /**
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getBlackWhiteInfoByUserIData(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCodeParser("TF_F_USER_BLACKWHITE", "SEL_BY_USERID", param);
    }

    /**
     * @Function: getExistBlackWhiteList
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午11:40:00 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IData getExistBlackWhiteList(String userId, String serviceId, String groupId, String userTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("BIZ_NAME", "IMSBWLIST");
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        param.put("GROUP_ID", groupId);
        param.put("USER_TYPE_CODE", userTypeCode);
        String sqlStr = "SELECT B.RSRV_STR4, B.RSRV_STR5, B.START_DATE FROM TF_F_USER_BLACKWHITE B WHERE B.USER_ID =:USER_ID AND B.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) AND B.SERVICE_ID =:SERVICE_ID AND B.BIZ_NAME =:BIZ_NAME AND B.GROUP_ID =:GROUP_ID AND B.USER_TYPE_CODE =:USER_TYPE_CODE";
        SQLParser parser = new SQLParser(param);
        parser.addSQL(sqlStr);
        IDataset result = Dao.qryByParse(parser);
        if (!IDataUtil.isEmpty(result))
        {
            return result.getData(0);
        }
        return null;
    }

    /**
     * todo getVisit().setRouteEparchyCode( AppUtil.getAttribute("MEM_EPARCHY_CODE"));怎么处理
     *
     * @Function: getSEL_VALID_PLAT_BLACKWHITE
     * @Description: 根据user_id,svcid查询用户订购服务的 plartsvc实例化参数 @param userId 用户编码 @return IData 返回用户实例信息
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午11:40:51 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getSEL_VALID_PLAT_BLACKWHITE(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_VALID_PLAT_BLACKWHITE", param);
    }

    /*
     * ADC校讯通 非移动号码BLACKWHITE关系查询 liaolc 2014-04-17
     */
    public static IDataset getXxtBlackWhite(String outUserId, String grpUserId, String outSn) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", outUserId);// 非移动号码userId
        param.put("EC_USER_ID", grpUserId);// 集团userId
        param.put("SERIAL_NUMBER", outSn);// 非移动号码SN

        IDataset result = Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_USERID_USERIDA_SN", param);

        return result;
    }

    /*
     * ADC校讯通 非移动号码BLACKWHITE关系查询 liaolc 2014-04-17
     */
    public static IDataset getXxtBlackWhiteByBizCode(String outUserId, String grpUserId, String bizCode1, String bizCode2) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", outUserId);// 非移动号码userId
        param.put("EC_USER_ID", grpUserId);// 集团userId
        param.put("BIZ_CODE1", bizCode1);
        param.put("BIZ_CODE2", bizCode2);

        IDataset result = Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_USERID_USERIDA_BIZ", param);

        return result;
    }

    /*
     * ADC校讯通 非移动号码BLACKWHITE关系查询 liaolc 2014-04-17
     */
    public static IDataset getXxtBlackWhiteByUserIdBizCode2(String outUserId, String grpUserId, String bizCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", outUserId);// 移动号码userId
        param.put("EC_USER_ID", grpUserId);// 集团userId
        param.put("BIZ_CODE", bizCode);

        IDataset result = Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_USERID_ECUSERID_BIZ_CODE", param);

        return result;
    }

    /*
     * ADC校讯通 非移动号码BLACKWHITE关系查询 liaolc 2014-04-17
     */
    public static IDataset getXxtBlackWhiteByUserIdBizCodeOutSn(String userId, String grpUserId, String outSn, String bizCode1, String bizCode2) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);// 移动号码userId
        param.put("EC_USER_ID", grpUserId);// 集团userId
        param.put("SERIAL_NUMBER_A", outSn);// 异网号
        param.put("BIZ_CODE1", bizCode1);
        param.put("BIZ_CODE2", bizCode2);

        IDataset result = Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_USERIDA_FOR_XXT", param);

        return result;
    }
    
    /**
     * 作用：查询 TF_F_USER_BLACKWHITE表信息
     *
     * @param userId
     * @param serviceId
     * @param userTypeCode
     * @return TF_F_USER_BLACKWHITE某集团用户总数
     * @throws Exception
     */
    public static IDataset qryblackWhitecountByServiceIdEcUserId(String userId, String serviceId,String userTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EC_USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        param.put("USER_TYPE_CODE", userTypeCode);

        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_SERVICEID_ECUSERID", param);
 
    }
    
    /**
     * 作用：查询 TF_F_USER_BLACKWHITE表信息
     *
     * @param userId
     * @param serviceId
     * @param userTypeCode
     * @return TF_F_USER_BLACKWHITE某集团用户总数,一键注销时使用
     * @throws Exception
     */
    public static IDataset qryblackWhitecountByEcUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("EC_USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_COUNT_BY_ECUSERID", param);
 
    }
    
    /**
     * 作用：查询 TF_F_USER_BLACKWHITE表信息
     *
     * @param userId
     * @param serviceId
     * @param userTypeCode
     * @return TF_F_USER_BLACKWHITE某集团所有用户,一键注销时使用
     * @throws Exception
     */
    public static IDataset qryblackWhiteByEcUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("EC_USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_ECUSERID", param);
 
    }
    
    
    /**
     * 作用：查询 TF_F_USER_BLACKWHITE表信息
     *
     * @param userId
     * @param serviceId
     * @param userTypeCode
     * @return TF_F_USER_BLACKWHITE某集团所有用户,包括本网号码和异网号码
     * @throws Exception
     */
    public static IDataset qryblackWhiteAllByServiceIdEcUserId(String userId, String serviceId,String userTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EC_USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        param.put("USER_TYPE_CODE", userTypeCode);

        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_SERVICEID_ECUSERID_USERTYPE", param);
 
    }
    
    

    /**
     * 作用：查询 TF_F_USER_BLACKWHITE表信息
     *
     * @param userId
     * @param serviceId
     * @param userTypeCode
     * @return TF_F_USER_BLACKWHITE某集团用户总数
     * @throws Exception
     */
    public static IData qryblackWhiteByServiceIdEcUserId(String userId, String serviceId, String userTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EC_USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        param.put("USER_TYPE_CODE", userTypeCode);

        IDataset userattrs = Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_SERVICEID_ECUSERID", param);
        if (IDataUtil.isNotEmpty(userattrs))
        {
            return userattrs.getData(0);
        }
        else
        {
            return new DataMap();
        }
    }

    public static IDataset qryBlackWhiteBySnEcuserIdGropIdServId(String ecUserId, String serviceId, String sn) throws Exception
    {
        IData param = new DataMap();
        param.put("EC_USER_ID", ecUserId);
        param.put("SERVICE_ID", serviceId);
        param.put("SERIAL_NUMBER", sn);
        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_ECUSERID_SERID_SN", param);
    }

    public static IDataset QryGrpBWLists(IData data, Pagination pg) throws Exception
    {

        String userIdA = data.getString("USER_ID_A", "");
        String userTypeCode = data.getString("USER_TYPE_CODE", "");// IW-白名单;IB-黑名单;空-全部名单;
        if ("".equals(userIdA))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_85);
        }

        IData grpUserInfo = UserInfoQry.getGrpUserInfoByUserId(userIdA, "0", Route.CONN_CRM_CG);
        String eparchycode = grpUserInfo.getString("EPARCHY_CODE");
        IData inParam = new DataMap();
        inParam.put("EC_USER_ID", userIdA);
        inParam.put("USER_TYPE_CODE", userTypeCode);

        IDataset resInfos = new DatasetList();
        if ("IW".equals(userTypeCode) || "IB".equals(userTypeCode))
        {// IW-白名单

            resInfos = Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_ECUSERID_USERTYPE", inParam, Route.CONN_CRM_CG);

        }
        else
        {

            resInfos = Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_ECUSERID_IMS", inParam, Route.CONN_CRM_CG);
        }
        return resInfos;
    }

    /**
     * @Function: queryBlackWhiteInfo
     * @Description: 查询是否存在还未生效的黑名单
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午11:43:46 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset queryBlackWhiteInfo(String user_id, String group_id, String serial_number, String user_type_code, String oper_state) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("GROUP_ID", group_id);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("USER_TYPE_CODE", user_type_code);
        param.put("OPER_STATE", user_type_code);

        return Dao.qryByCodeParser("TF_F_USER_BLACKWHITE", "SEL_BY_BLACKWHITE_INFO", param);
    }

    /**
     * @Function: queryBlackWhiteInfoByUserId
     * @Description: 查询订购信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午11:44:27 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset queryBlackWhiteInfoByUserId(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);

        SQLParser parserOrd = new SQLParser(param);
        parserOrd
                .addSQL("Select ubw.EC_USER_ID,ubw.serv_code,ubw.biz_name,ubw.group_id,ubw.BIZ_CODE,ubw.Ec_Serial_Number,TO_CHAR(ubw.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,TO_CHAR(ubw.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,decode(ubw.USER_TYPE_CODE,'B','黑名单')||decode(ubw.USER_TYPE_CODE,'W','白名单')||decode(ubw.USER_TYPE_CODE,'S','订购关系')||decode(ubw.USER_TYPE_CODE,'L','限制次数白名单')||decode(ubw.USER_TYPE_CODE,'QB','全局白名单')||decode(ubw.USER_TYPE_CODE,'EB','全局黑名单') TRADE_TYPE  From tf_f_user_blackwhite  ubw ,td_s_static ss ");
        parserOrd.addSQL("Where ubw.user_id=:USER_ID  ");
        parserOrd.addSQL("And  Sysdate Between ubw.start_date And ubw.end_date ");
        parserOrd.addSQL("And ubw.EC_SERIAL_NUMBER=ss.Data_Id ");
        parserOrd.addSQL("And ss.type_id = 'GRP_FARMERCC' ");
        return Dao.qryByParse(parserOrd);
    }

    public static IDataset querySvcInfoByUserIdAndSvcId(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_USERID_SVCID", param);
    }

    public static IDataset querySvcInfoByUserIdAndSvcIdPf(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_USERID_SVCID_PF", param);
    }

    public static IDataset querySvcInfoByUserIdDate(String userId, String timePoint) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TIME_POINT", timePoint);

        return Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_BY_USERID_DATE", param);
    }


    /**
    * @Title: delCloudMASListByUserIdServCodeSN
    * @Description: 删除黑白名单表中行业网关云MAS的信息
    * @param userId
    * @param serialNumber
    * @param servCode
    * @throws Exception
    * @return void
    * @author chenkh
    * @time 2015年4月20日
    */
    public static void delCloudMASListByUserIdServCodeSN(String userId, String serialNumber, String servCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EC_USER_ID", userId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("SERV_CODE", servCode);
        StringBuilder sql = new StringBuilder(1000);
        sql.append("DELETE FROM TF_F_USER_BLACKWHITE WHERE EC_USER_ID = :EC_USER_ID AND SERIAL_NUMBER = :SERIAL_NUMBER AND SERV_CODE = :SERV_CODE ");
        Dao.executeUpdate(sql, param);
    }

    /**
    * @Title: insCloudMASList
    * @Description: 向黑白名单表中插入云MAS数据
    * @param instId
    * @param partitionId
    * @param userId
    * @param userTypeCode
    * @param serviceId
    * @param bizInCode
    * @param ecUserId
    * @param serialNumber
    * @param servCode
    * @param bizCode
    * @param startDate
    * @param endDate
    * @throws Exception
    * @return void
    * @author chenkh
    * @time 2015年4月20日
    */
    public static void insCloudMASList(String instId, String partitionId, String userId, String userTypeCode, String serviceId, String bizInCode, String ecUserId, String serialNumber, String servCode, String bizCode, String startDate, String endDate) throws Exception
    {
        IData param = new DataMap();
        param.put("INST_ID", instId);
        param.put("PARTITION_ID", instId);
        param.put("USER_ID", userId);
        param.put("USER_TYPE_CODE", userTypeCode);
        param.put("SERVICE_ID", serviceId);
        param.put("BIZ_IN_CODE", bizInCode);
        param.put("EC_USER_ID", ecUserId);
        param.put("SERV_CODE", servCode);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("BIZ_CODE", bizCode);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        StringBuilder sql = new StringBuilder(1000);
        sql.append("INSERT INTO TF_F_USER_BLACKWHITE ");
        sql.append("(PARTITION_ID, USER_ID, USER_TYPE_CODE, SERVICE_ID, BIZ_IN_CODE, BIZ_IN_CODE_A, EC_USER_ID, EC_SERIAL_NUMBER, SERV_CODE, SERIAL_NUMBER, GROUP_ID, BIZ_CODE, BIZ_NAME, BIZ_DESC, BOOK_DATE, CONTRACT_ID, PRICE, BILLING_TYPE, OPR_EFF_TIME, OPR_SEQ_ID, OPER_STATE, EXPECT_TIME, PLAT_SYNC_STATE, START_DATE, END_DATE, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3, INST_ID) ");
        sql.append("values (TO_NUMBER(:PARTITION_ID), TO_NUMBER(:USER_ID), TO_NUMBER(:USER_TYPE_CODE), TO_NUMBER(:SERVICE_ID), TO_NUMBER(:BIZ_IN_CODE), '', TO_NUMBER(:EC_USER_ID), '', :SERV_CODE, :SERIAL_NUMBER, '', :BIZ_CODE, '云MAS', null, null, null, null, '', null, null, '02', '', '1', to_date(:START_DATE, 'yyyymmddhh24:mi:sshh24:mi:ss'), to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'), to_date(:START_DATE, 'yyyymmddhh24:mi:sshh24:mi:ss'), 'IAGW', '', null, '0', '0', '0', '0', '0', null, null, null, null, null, null, null, null, null, '0', '0', TO_NUMBER(:INST_ID)) ");
        Dao.executeUpdate(sql, param);
    }

    /**
     * @Title: qryCloudMASListByUserIdBizCodeSN
     * @Description: 取行业网关云MAS业务是否在黑白名单表中存在信息
     * @param userId
     * @param serialNumber
     * @param servCode
     * @return
     * @throws Exception
     * @return IDataset
     * @author chenkh
     * @time 2015年4月13日
     */
     public static IDataset qryCloudMASListByUserIdServCodeSN(String userId, String serialNumber, String servCode) throws Exception
     {
         IData param = new DataMap();
         param.put("EC_USER_ID", userId);
         param.put("SERIAL_NUMBER", serialNumber);
         param.put("SERV_CODE", servCode);
         SQLParser parser = new SQLParser(param);
         parser.addSQL("SELECT T.GROUP_ID, T.SERIAL_NUMBER, T.BIZ_CODE, T.INST_ID");
         parser.addSQL(" FROM TF_F_USER_BLACKWHITE T ");
         parser.addSQL(" WHERE T.EC_USER_ID = :EC_USER_ID ");
         parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
         parser.addSQL(" AND T.SERV_CODE = :SERV_CODE ");
         parser.addSQL(" AND T.START_DATE < SYSDATE");
         parser.addSQL(" AND T.END_DATE > SYSDATE");
         return Dao.qryByParse(parser);
     }
     /**
      * @param userId
      * @return
      * @throws Exception
      */ 
     public static IDataset getBlackWhiteInfoBySerialNumBerIData(String SerialNumBerStr) throws Exception
     {
         IData param = new DataMap();
         param.put("SERIAL_NUMBER", SerialNumBerStr);

         return Dao.qryByCodeParser("TF_F_USER_BLACKWHITE", "SEL_BY_SERIALNUMBER", param);
     }



    /**
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset getBlackWhiteInfoByUseridASerialNumberIData(String userID,String ecUserId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userID);
        param.put("EC_USER_ID", ecUserId);
        param.put("SERVICE_ID", serviceId);
        return Dao.qryByCodeParser("TF_F_USER_BLACKWHITE", "SEL_BY_USERID_AND_ECUSERID_SERVICEID", param);
    }


}
