
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res.ResParaInfoQry;

public class UserResInfoQry
{
    /**
     * add by likai3
     * 
     * @param 为一个数组
     *            USER_ID_A、 USER_ID、RES_TYPE_CODE、RES_CODE、RES_KIND_CODE
     * @return
     * @throws Exception
     */
    public static IDataset checkRes(IData inparams) throws Exception
    {
        // TODO code_cdoe 表里没有

        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID_RESTYPE2", inparams);
    }

    public static IDataset checkUser4GUsimCard(String simTypeCode, String eparchyCode) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("PARA_ATTR", "8510");
        qryParam.put("PARA_CODE1", "TD_LTE_SIM_CARDTYPE_CODE");
        qryParam.put("PARA_CODE2", simTypeCode);
        qryParam.put("EPARCHY_CODE", eparchyCode);
        IDataset dataset = Dao.qryByCode("TD_M_ASSIGNPARA", "SEL_BY_USIMTYPECODE", qryParam, Route.CONN_CRM_CEN);
        return dataset;
    }

    public static IDataset getEmptyCardsByEmptyCardId(String empty_card_id) throws Exception
    {
        IData param = new DataMap();
        param.put("EMPTY_CARD_ID", empty_card_id);
        return Dao.qryByCode("TF_R_EMPTYCARD", "SEL_BY_EMPTYCARDID", param);
    }

    public static IDataset getRecordCountByUserId(String userId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TF_R_SIMCARD_USE", "SEL_BY_USERID_RES", param);
    }

    /**
     * @Function: getRecordNumByRescode
     * @Description: 根据USER_ID，查用户资源信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:13:02 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getRecordNumByRescode(String user_id, String start_date, String end_date) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_RESCODE_RECORD", param);
    }

    public static IDataset getResByUserIdResType(String user_id, String user_id_a, String res_type_code, String res_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        param.put("RES_TYPE_CODE", res_type_code);
        param.put("RES_CODE", res_code);
        IDataset idata = Dao.qryByCodeParser("TF_F_USER_RES", "SEL_BY_USERID_RESTYPE", param);
        return idata;
    }

    /**
     * @Function: getResByUserIdResType
     * @Description: 根据USER_ID AND ResType查询资源信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:14:34 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IData getResByUserIdResType(String user_id, String user_id_a, String res_type_code, String res_code, String eparchyCode) throws Exception
    {

        // getVisit().setRouteEparchyCode( eparchyCode);
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        param.put("RES_TYPE_CODE", res_type_code);
        param.put("RES_CODE", res_code);
        IDataset idata = Dao.qryByCodeParser("TF_F_USER_RES", "SEL_BY_USERID_RESTYPE", param, eparchyCode);
        return idata.size() > 0 ? idata.getData(0) : null;
    }

    /**
     * @Description:根据USER_ID, RES_TYPE, RES_TYPE_CODE查询资源
     * @author wusf
     * @date 2010-1-29
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getResInfoByUserIdRes(IData inparams) throws Exception
    {
        // TODO code_cdoe 表里没有

        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID_RES", inparams);
    }

    public static IDataset getSimCardNoEP(String sim_card_no, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("SIM_CARD_NO", sim_card_no);
        if (StringUtils.isNotEmpty(eparchy_code))
        {
            param.put("EPARCHY_CODE", eparchy_code);
        }
        return Dao.qryByCode("TF_R_SIMCARD_IDLE", "SEL_BY_SIMCARDNO_EP", param);
    }

    /**
     * 翻译资源状态
     */
    public static String getSimStateCodeName(String simStateCode) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_STATE_CODE", simStateCode);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT STATE_NAME FROM RES_STATE_DEF WHERE RES_TYPE_ID= '1' ");
        parser.addSQL(" and STATE_CODE =:RES_STATE_CODE ");
        IDataset res = Dao.qryByParse(parser, Route.CONN_RES);
        if (IDataUtil.isNotEmpty(res))
        {
            return res.getData(0).getString("RES_STATE_NAME");
        }
        return "";
    }

    /**
     * 翻译资源类型
     */
    public static String getSimTypeCodeName(String simTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_KIND_CODE", simTypeCode);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT RES_TYPE_NAME FROM RES_TYPE WHERE RES_TYPE_ID= '1' ");
        IDataset res = Dao.qryByParse(parser, Route.CONN_RES);
        if (IDataUtil.isNotEmpty(res))
        {
            return res.getData(0).getString("RES_TYPE_NAME");
        }
        return "";
    }

    /**
     * @Function: getUserProductRes
     * @Description: 查询一个用户定购某个集团用户的产品的资源信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:17:47 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserProductRes(String user_id, String user_id_a, Pagination pagination) throws Exception
    {
        if (StringUtils.isBlank(user_id))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "USER_ID不能为空");
        }

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);

        IDataset resList = Dao.qryByCodeParser("TF_F_USER_RES", "SEL_BY_USERID_USERIDA", param, pagination);
        if (IDataUtil.isNotEmpty(resList))
        {
            for (int i = 0, sz = resList.size(); i < sz; i++)
            {
                IData resData = resList.getData(i);
                String resType = ResParaInfoQry.getResTypeNameByResTypeCode(resData.getString("RES_TYPE_CODE"));
                resData.put("ELEMENT_NAME", resType);
            }

        }

        return resList;

    }

    /**
     * @Function: getUSERres
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:18:54 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUSERres(String user_id, String user_id_a, String res_type_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        param.put("RES_TYPE_CODE", res_type_code);

        SQLParser parser = new SQLParser(param);
        parser
                .addSQL(" select  USER_ID,USER_ID_A,RES_TYPE_CODE,RES_CODE,IMSI,KI,INST_ID, CAMPN_ID,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') end_date,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK   FROM TF_F_USER_RES us ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" and us.user_id = to_number(:USER_ID) ");
        parser.addSQL(" and us.user_id_a = to_number(:USER_ID_A) ");
        parser.addSQL(" AND us.PARTITION_ID=MOD(to_number(:USER_ID), 10000) ");
        parser.addSQL(" and us.RES_TYPE_CODE =:RES_TYPE_CODE ");
        parser.addSQL(" AND us.START_DATE<=SYSDATE ");
        parser.addSQL(" AND us.END_DATE>SYSDATE ");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * @function: queryDmByImei
     * @Function: queryUserSimInfo
     * @param : user_id
     * @param : rsrv_str1
     * @return
     * @throws : Exception
     * @author : zhenggang
     * @version : v1.0.0
     * @date : 2013-7-18 下午16:00:00 Modification History: Date Author Version Description
     *       ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserResByResCode(String imei) throws Exception
    {
        IData param = new DataMap();

        param.put("RES_TYPE_CODE", "F");
        param.put("RES_CODE", imei);

        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_RES_CODE", param);
    }

    /**
     * @param resCode
     * @param typeCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserResByResCode(String resCode, String typeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TYPE_CODE", typeCode);
        param.put("RES_CODE", resCode);

        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_RES_CODE", param);
    }

    /**
     * @function: checkUserInfo
     * @Function: queryUserSimInfo
     * @param : user_id
     * @param : rsrv_str1
     * @return
     * @throws : Exception
     * @author : zhenggang
     * @version : v1.0.0
     * @date : 2013-7-18 下午16:00:00 Modification History: Date Author Version Description
     *       ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserResByRsrvStr(String user_id, String rsrv_str1) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", user_id);
        param.put("RES_TYPE_CODE", "F");
        param.put("RSRV_STR1", rsrv_str1);

        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_RSRVSTR1", param);
    }

    public static IDataset getUserResBySelbySerialnremove(String serial_number, String res_type_code) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serial_number);
        data.put("RES_TYPE_CODE", res_type_code);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_SERIALNREMOVE", data);
    }
    public static IDataset getUserResBySelbySerialnremoveAcct(String serial_number, String res_type_code) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serial_number);
        data.put("RES_TYPE_CODE", res_type_code);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_SERIALNREMOVE_ACCT", data);
    }

    /**
     * @Function: getUserResByUserIdA
     * @Description: 作用：根据user_id和user_id_a查找这个用户占用的集团资源
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:20:41 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserResByUserIdA(String user_id, String user_id_a) throws Exception
    {

        return getUserProductRes(user_id, user_id_a, null);

    }

    /**
     * @Function: getUserResInfoByUserId
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:21:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserResInfoByUserId(String user_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID", param);
    }

    public static IDataset getUserResInfoByUserId(String user_id, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID", param, eparchyCode);
    }

    public static IDataset getUserResInfoByUserIdRestype(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID_RESTYPE", param);
    }

    /**
     * @Function: getUserResInfoByUserIdRestype
     * @Description: 根据USER_ID, USER_ID_A, RES_TYPE_CODE查询资源
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:22:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserResInfoByUserIdRestype(String user_id, String user_id_a, String res_type_code) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        param.put("RES_TYPE_CODE", res_type_code);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID_IMS", param);
    }

    public static IDataset getUserResInfosByUserIdResTypeCode(String user_id, String res_type_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RES_TYPE_CODE", res_type_code);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID_LAST_ONE", param);
    }

    /**
     * 根据UserId获取用户最近使用的资源信息，复机时使用
     * 
     * @param userId
     * @return
     * @throws Exception
     * @auth liuke
     */
    public static IDataset getUserResMaxDateByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_MAXDATE", param);
    }

    public static IDataset getUserResource(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        IDataset resInfos = Dao.qryByCode("TF_F_USER_RES", "SEL_FOR_SIMCARD", data);
        return resInfos;
    }

    public static IDataset getUserRres(String user_id, String user_id_a, String res_type_code, Pagination pagination) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("USER_ID_A", user_id_a);
        data.put("RES_TYPE_CODE", res_type_code);

        SQLParser parser = new SQLParser(data);

        parser
                .addSQL(" select  USER_ID,USER_ID_A,RES_TYPE_CODE,RES_CODE,IMSI,KI,INST_ID, CAMPN_ID,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') end_date,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK   FROM TF_F_USER_RES us ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" and us.user_id = to_number(:USER_ID) ");
        parser.addSQL(" and us.user_id_a = to_number(:USER_ID_A) ");
        parser.addSQL(" AND us.PARTITION_ID=MOD(to_number(:USER_ID), 10000) ");
        parser.addSQL(" and us.RES_TYPE_CODE =:RES_TYPE_CODE ");
        parser.addSQL(" AND us.START_DATE<=SYSDATE ");
        parser.addSQL(" AND us.END_DATE>SYSDATE ");

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset getUserSimCardInfoByCodeTypeAndIdForAllCrmDb(String sim_card_no, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("SIM_CARD_NO", sim_card_no);
        param.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCodeAllCrm("TF_R_SIMCARD_USE", "SEL_BY_SIM", param, true);
    }

    public static IDataset getUserSimCardInfoBySimcardno(String sim_card_no, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("SIM_CARD_NO", sim_card_no);
        if (StringUtils.isNotEmpty(eparchy_code))
        {
            param.put("EPARCHY_CODE", eparchy_code);
        }
        return Dao.qryByCode("TF_R_SIMCARD_USE", "SEL_USE_BY_SIMCARDNO", param);
    }

    /**
     * @Function: qrySetTopBoxByUserIdAndTag1()
     * @Description: 查询用户机顶盒信息
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-1 上午9:51:27 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-1 yxd v1.0.0 修改原因
     */
    public static IDataset qrySetTopBoxByUserIdAndTag1(String userId, String resTypeCode, String rsrvTag1) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RES_TYPE_CODE", resTypeCode);
        param.put("RSRV_TAG1", rsrvTag1); // J:机顶盒
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_UID_RESTYPE", param);
    }    
    
    
    /**
     * 通过串号查找资源信息
     * @param imsi
     * @return
     * @throws Exception
     */
    public static IDataset queryUserIMEI(String imsi) throws Exception
    {
        IData param = new DataMap();
        param.put("IMSI", imsi);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_IMSI", param);
    }

    /**
     * 通过USER_ID查询用户资源表的所有资料，并根据END_DATE降序排列
     * 
     * @param user_id
     * @param res_type_code
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-26
     */
    public static IDataset qryUserResByIdDescEd(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID_ALL", param);
    }

    /**
     * @Function: getUserResByUser
     * @Description: 根据user_id查找这个用户占用的集团资源
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:19:32 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset qryUserResByUserId(String user_id, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);

        IDataset resList = Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID", param, routeId);
        return resList;
    }

    /**
     * 查询资源信息
     * 
     * @param userId
     * @param userIdA
     * @param resTypeCode
     * @param resCode
     * @return
     * @throws Exception
     */
    public static IDataset qryUserResInfoByUserIdResType(String userId, String userIdA, String resTypeCode, String resCode) throws Exception
    {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(resTypeCode) || StringUtils.isBlank(resCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "USER_ID、RES_TYPE_CODE、RES_CODE不能为空");
        }

        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);
        param.put("RES_TYPE_CODE", resTypeCode);
        param.put("RES_CODE", resCode);

        return Dao.qryByCodeParser("TF_F_USER_RES", "SEL_BY_USERID_RESTYPE", param);
    }

    public static IDataset queryOneResAll(String userId, String resTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RES_TYPE_CODE", resTypeCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_RES", "SEL_USERID_ONERES_ALL", param);
        return dataset;
    }

    public static IDataset queryUserDiscntFor4GCheck(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_FOR4GCHECK", param);
        return dataset;
    }

    /**
     * 根据资源类型，查询用户当前有效的资源信息 （***********带表中所有列**************)
     * 
     * @param user_id
     * @param res_type_code
     * @return
     * @throws Exception
     */
    public static IDataset queryUserResByUserIdResType(String user_id, String res_type_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RES_TYPE_CODE", res_type_code);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID_ONE", param);
    }

    /**
     * @Function: queryUserSimInfo
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:24:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryUserSimInfo(String user_id, String res_type_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RES_TYPE_CODE", res_type_code);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID_RESTYPE_CODE", param);
    }

    /**
     * @Function: queryUserSimInfo
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:24:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryUserSimInfo(String user_id, String res_type_code, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RES_TYPE_CODE", res_type_code);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID_RESTYPE_CODE", param, routeId);
    }
    
    /**
     * 查询用户的历史资源信息
     * @param user_id
     * @param res_type_code
     * @return
     * @throws Exception
     */
    public static IDataset getUserHResInfosByUserIdResTypeCode(String user_id, String res_type_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RES_TYPE_CODE", res_type_code);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_H_USERID_LAST_ONE", param);
    }
    
    
    /**
     * @param user_id
     * @param time_point
     * @return
     * @throws Exception
     */
    public static IDataset getUserResInfosByUserIdDate(String user_id, String time_point) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("TIME_POINT", time_point);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_UID_DATE", param);
    }
    
    /**
     * 查询用户的IMSI
     * @param user_id
     * @return
     * @throws Exception
     */
    public static IDataset getUserIMSI(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID_SIMM", param);
    }
    
    
    /**
     * @Function: qrySetTopBoxByUserIdAndTag1()
     * @Description: 查询用户机顶盒信息
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-1 上午9:51:27 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-1 yxd v1.0.0 修改原因
     */
    public static IDataset qrySetTopBoxByUserIdAndTag1AllColumns(String userId, String resTypeCode, String rsrvTag1) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RES_TYPE_CODE", resTypeCode);
        param.put("RSRV_TAG1", rsrvTag1); // J:机顶盒
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_UID_RESTYPE_ALL_COLUMNS", param);
    }
    
    public static IDataset qrySetTopBoxByUserIdAndRsrv1AllColumns(String userId, String resTypeCode, String rsrvStr1) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RES_TYPE_CODE", resTypeCode);
        param.put("RSRV_STR1", rsrvStr1); // J:机顶盒
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_UID_RESTYPER_ALL_COLUMNS", param);
    }
    
    public static void saveUserRes(String tradeId)throws Exception{
    	IData param = new DataMap();
    	param.put("TRADE_ID", tradeId);
    	
    	Dao.executeUpdateByCodeCode("TF_B_TRADE_RES", "INS_ROLLBACK_TOPSET_BOX", param);
    }
    
    public static void saveRollBackUserRes(IData param)throws Exception{
    	Dao.executeUpdateByCodeCode("TF_B_TRADE_RES", "INS_ROLLBACK_TOPSETBOX_FROM_USER", param);
    }
    
    public static void updateTopsetboxTransfeeSign(String userId)throws Exception{
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	
    	Dao.executeUpdateByCodeCode("TF_F_USER_RES", "UPS_USER_RES_TOPSETBOX_TRANS_FEE", param);
    	
    }
    
    public static IDataset queryRollbackTopSetBox(String userId)throws Exception{
    	IData param=new DataMap();
    	param.put("USER_ID", userId);
    	
    	return Dao.qryByCode("TF_B_TRADE_RES", "SEL_ROLLBACK_TOPSET_BOX", param);
    }
    
    public static void delRollbackTopSetBox(String userId)throws Exception{
    	IData param=new DataMap();
    	param.put("USER_ID", userId);
    	
    	Dao.executeUpdateByCodeCode("TF_B_TRADE_RES", "DEL_ROLLBACK_TOPSETBOX", param);
    }
    
    public static void delTopsetboxOnline(String userId)throws Exception{
    	IData param=new DataMap();
    	param.put("USER_ID", userId);
    	
    	Dao.executeUpdateByCodeCode("TF_B_TRADE_RES", "DEL_TOPSETBOX_ONLINE", param);
    }
    
    public static void insertTopsetboxOnline(IData param)throws Exception{
    	Dao.executeUpdateByCodeCode("TF_B_TRADE_RES", "INS_TOPSETBOX_ONLINE", param);
    }
    
    public static IDataset qryTopsetboxOnline(String userId)throws Exception{
    	IData param=new DataMap();
    	param.put("USER_ID", userId);
    	
    	return Dao.qryByCode("TF_B_TRADE_RES", "QRY_TOPSETBOX_ONLINE_BY_USERID", param);
    }
    
    
    public static void updateTopsetboxOnlineIsback(String userId, String isBack)throws Exception{
    	IData param=new DataMap();
    	param.put("USER_ID", userId);
    	param.put("IS_BACK", isBack);
    	
    	Dao.executeUpdateByCodeCode("TF_B_TRADE_RES", "UPD_TOPSETBOX_ONLINE_ISBACK", param);
    }
    
    /**
     * 日期： 20160601
     * <br/>
     *  根据 userId查询tf_f_user_4gtag信息
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset get4GUserByUserId(String userId) throws Exception{
        //通过userid获取用户信息
    	IDataset userinfo=UserInfoQry.getUserInfoByUserIdTag(userId, "0");
    	if(IDataUtil.isNotEmpty(userinfo)){
    		 String serialNumber=userinfo.getData(0).getString("SERIAL_NUMBER","");
    		 IDataset user4Ginfo=UserInfoQry.getUser4GInfoBySerialNumber(serialNumber);
    		return user4Ginfo;
    	}
    	return null;
    }
    
    public static void insertIMSTopsetboxOnline(IData param)throws Exception{
    	Dao.executeUpdateByCodeCode("TF_F_USER_RES", "INS_USER_RES_ALL", param);
    }
    /**
     * @Function: getAllUserRes
     * @Description: 查询所有用的魔百和信息（魔百和到期自动化任务）
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     */
    public static IDataset getAllUserRes(IData param) throws Exception
    {
    	String resTypeCode = param.getString("RES_TYPE_CODE");
    	String userId = param.getString("USER_ID");
    	String rsrvTag1 = param.getString("RSRV_TAG1");
        IData input = new DataMap();
        param.put("USER_ID", userId);
        param.put("RES_TYPE_CODE", resTypeCode);
        param.put("RSRV_TAG1", rsrvTag1); // J:机顶盒
        
        SQLParser parser = new SQLParser(input);
        parser.addSQL(" select  USER_ID,USER_ID_A,RES_TYPE_CODE,RES_CODE,IMSI,KI,INST_ID, CAMPN_ID,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') end_date,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK   FROM TF_F_USER_RES us ,TF_F_USER u");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" and us.user_id = to_number(:USER_ID) ");
        parser.addSQL(" and us.RSRV_TAG1 = :RSRV_TAG1 ");
        parser.addSQL(" AND us.PARTITION_ID=MOD(to_number(:USER_ID), 10000) ");
        parser.addSQL(" AND us.USER_ID=u.USER_ID");
        parser.addSQL(" AND u.USER_ID");
        parser.addSQL(" and us.RES_TYPE_CODE =:RES_TYPE_CODE ");
        parser.addSQL(" AND us.START_DATE<=SYSDATE ");
        parser.addSQL(" AND us.END_DATE>SYSDATE ");
        return Dao.qryByParse(parser);
    }
}
