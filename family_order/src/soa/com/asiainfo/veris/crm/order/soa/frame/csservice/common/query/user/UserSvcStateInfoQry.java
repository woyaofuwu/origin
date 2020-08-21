
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserSvcStateInfoQry
{

    /*
     * 根据SERVICE_ID 查询中 TF_F_USER_SVCSTATE, 并且状态 in ('0','E')
     */
    public static IDataset getAllBySvcIdStateCode(String serviceId, String routeId) throws Exception
    {
        IData params = new DataMap();
        params.put("SERVICE_ID", serviceId);

        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT INST_ID,PARTITION_ID,USER_ID,SERVICE_ID,MAIN_TAG,STATE_CODE,START_DATE,END_DATE ");
        parser.addSQL(" FROM TF_F_USER_SVCSTATE  ");
        parser.addSQL(" WHERE SERVICE_ID=:SERVICE_ID and STATE_CODE in ('0','E') and  sysdate between START_DATE and END_DATE");
        return Dao.qryByParse(parser, routeId);
    }

    /**
     * @Function: getUserLastStateByUserSvc
     * @Description: 通过用户标识获得用户服务状态信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:24:25 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserLastStateByUserSvc(String user_id, String service_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USER_SVC", param);
    }

    /**
     * @Function: getUserLastStateByUserSvc
     * @Description: 通过用户标识获得用户服务状态信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:24:25 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserLastStateByUserSvc(String user_id, String service_id, String routeId) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USER_SVC", param, routeId);
    }

    public static IDataset getUserLastStateByUserSvcPf(String user_id, String service_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USER_SVC_PF", param);
    }

    // 获取最近一次停机时间
    public static String getUserLastStopTime(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset lastTimeList = Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_USER_LASTSTOPTIME", param);
        if (lastTimeList.size() > 0)
        {
            return lastTimeList.getData(0).getString("START_DATE", "");
        }
        else
        {
            return "";
        }
    }

    /**
     * 查询用户主体服务状态
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getUserMainState(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_USER_VALID_MAINSVCSTATE", param);
    }

    /**
     * 作用：根据USERID，查询有效的服务状态记录TF_F_USER_SVCSTATE::SEL_BY_USERID_NOW
     * 
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserNowSvcStateByUserIdNow(String userId, String eparchyCode) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USERID_NOW", param, eparchyCode);
    }

    /**
     * @Function: getUserSvcStateBySvcId
     * @Description: 通过用户标识服务id获得有效的用户服务状态信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:26:00 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserSvcStateBySvcId(String user_id, String service_id, String state_code) throws Exception
    {
        return getUserSvcStateBySvcId(user_id, service_id, state_code, null);
    }

    public static IDataset getUserSvcStateBySvcId(String userId, String serviceId, String stateCode, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        param.put("STATE_CODE", stateCode);
        IDataset res = Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USER_SVC_STATE", param, routeId);
        return res;

    }

    /**
     * @Function: getUserSvcStateByUserId
     * @Description: 通过用户标识获得用户服务状态信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:26:41 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserSvcStateByUserId(String user_id, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_ALL_BY_USERID", param, pagination);
    }

    /**
     * @Function: getUserSvcStateInfoByUserId
     * @Description: 通过用户标识USERID获得用户语音状态信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:27:16 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserSvcStateInfoByUserId(String user_id, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USERID_MAINSVC", param, pagination);
    }

    /**
     * 根据服务编码集获取服务状态列表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getUserSvcStates(IData data) throws Exception
    {
        SQLParser sqlParser = new SQLParser(data);
        sqlParser.addSQL("select INST_ID,partition_id, user_id, service_id, main_tag, state_code, start_date, end_date, update_time, update_staff_id,");
        sqlParser.addSQL(" update_depart_id, remark, rsrv_num1, rsrv_num2, rsrv_num3, rsrv_num4, rsrv_num5, rsrv_str1, rsrv_str2, rsrv_str3,");
        sqlParser.addSQL(" rsrv_str4, rsrv_str5, rsrv_date1, rsrv_date2, rsrv_date3, rsrv_tag1, rsrv_tag2, rsrv_tag3");
        sqlParser.addSQL("  from tf_f_user_svcstate a");
        sqlParser.addSQL(" where a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
        sqlParser.addSQL(" and a.user_id = :USER_ID");
        sqlParser.addSQL(" and a.service_id in(" + data.get("SERVICEIDS") + ")");
        sqlParser.addSQL(" and a.end_date > TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')");

        return Dao.qryByParse(sqlParser);
    }

    /**
     * @Function: getUserValidMainSVCState
     * @Description: 通过用户标识获得有效的用户主体服务状态信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:27:54 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserValidMainSVCState(String user_id, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);

        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_USER_VALID_MAINSVCSTATE", param, pagination);
    }

    /**
     * 作用：根据USERID，查询有效的服务状态记录TF_F_USER_SVCSTATE::SEL_VALID_BY_USERID
     * 
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserValidSvcStateByUserId(String userId) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_VALID_BY_USERID", param);
    }

    /**
     * 获取有特殊标记的记录
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset querySpecTagInfos(String userId) throws Exception
    {
    	IDataset results=new DatasetList();
        IData param = new DataMap();
        param.put("USER_ID", userId);
        
        
        IDataset res= Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_SPC_TAG_BY_USERID", param);
        if(IDataUtil.isNotEmpty(res)){
        	results=res;
        }else{
            IDataset res2= Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_SPC_TAG_BY_USERID2", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
            if(IDataUtil.isNotEmpty(res2)){
                results=res2;

            }
        }
        

        
        return results;

        
    }

    /**
     * 查询用户的主体服务状态信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserMainTagScvState(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        // return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_USER_MAIN_SVCSTATE", param);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_USER_VALID_MAINSVCSTATE", param);
    }

    public static IDataset queryUserSvcStateByUserIdAndSvcList(String userId, StringBuilder svcList) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM TF_F_USER_SVCSTATE WHERE USER_ID = :USER_ID AND partition_id = MOD(TO_NUMBER(:USER_ID),10000) AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        sql.append("AND SERVICE_ID IN (").append(svcList).append(")");

        return Dao.qryBySql(sql, param);
    }

    /**
     * 根据userId查询用户服务状态
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserSvcStateInfo(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_USER_SVCSTATE", param);
    }

    public static IDataset queryUserValidMainSVCStateForWap(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_USER_VALID_MAINSVCSTATE_FOR_WAP", param);
    }
    
    
    public static IDataset queryUserSvcStateInfoByUidDate(String userId,  String timePoint) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TIME_POINT", timePoint);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_UID_DATE", param);
    }
    
    /**
     * 作用：根据USERID，查询下月有效的主体服务状态记录
     * 
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserNextMonthMainSvcStateByUId(String userId) throws Exception
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
        sql.append("   AND MAIN_TAG='1' ");
        sql.append("   AND END_DATE > LAST_DAY(TRUNC(SYSDATE)) + 1 - 1 / 24 / 3600 ");

        IDataset ids = Dao.qryBySql(sql, param);
        return ids;
    }

    
    /**
     * 作用：根据USERID，查询主体服务状态记录
     * 
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserMainSvcStateByUId(String userId) throws Exception
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
        sql.append("   AND MAIN_TAG='1' ");
        sql.append("   AND END_DATE > SYSDATE ");

        IDataset ids = Dao.qryBySql(sql, param);
        return ids;
    }
    
    /**
     * 
     * @param userId
     * @param serviceId
     * @param stateCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserLastNextStateByUserSvc(String userId, String serviceId,String stateCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        param.put("STATE_CODE", stateCode);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USER_SVC_LAST", param);
    }
    
    /**
     * 
     * @param userId
     * @param serviceId
     * @param stateCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserSvcBetweenStateByUserID(String userId, String serviceId,String stateCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        param.put("STATE_CODE", stateCode);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USER_SVCSTATE_BETWEEN", param);
    }
    
    public static IDataset getUserSvcStateByUserID(String userId, String serviceId,String stateCode,String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        param.put("STATE_CODE", stateCode);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USER_SVC_STOP", param,routeId);
    }
}
