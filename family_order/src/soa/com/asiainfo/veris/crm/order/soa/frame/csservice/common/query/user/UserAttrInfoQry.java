
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserAttrInfoQry
{

    /**
     * 获取手机支付属性信息
     *
     * @param userId
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset getAutoPayContractInfo(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_SVC_ID", param);
    }

    /**
     * todo getVisit().setRouteEparchyCode(eparchyCode);怎么处理 通过EC_USER_ID、ATTR_CODE查询成员黑白名单开关
     *
     * @param params
     * @return IDataset
     * @throws Exception
     * @author chenlei
     */
    public static IDataset getBwOpenTag(String user_id, String attr_code, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("ATTR_CODE", attr_code);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT * FROM TF_F_USER_ATTR ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND USER_ID = :USER_ID");
        parser.addSQL(" AND ATTR_CODE = :ATTR_CODE");
        parser.addSQL(" AND sysdate BETWEEN start_date AND end_date");
        return Dao.qryByParse(parser, eparchyCode);
    }

    /**
     * todo code_code 没有
     *
     * @Function: getExistAttrByCodeValue
     * @Description: 根据user_id,inst_id查TF_B_TRADE_ATTR
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:20:06 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getExistAttrByCodeValue(IData inparams) throws Exception
    {
        // TODO code_code 没有
        IDataset dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_EXIST_BETWEEN_GROUP", inparams);
        return dataset;
    }

    /**
     * 查出已经存在的群内组数据 TODO
     *
     * @param memberUserId
     *            成员用户id
     * @param teamId
     *            群组id
     * @param instId
     *            事务id PageData
     * @return IData 对应的群内组数据
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static IData getExistDataFromUserAttr(String memberUserId, String teamId, String instId) throws Exception
    {

        String sqlStr = "SELECT PARTITION_ID, USER_ID, INST_TYPE, INST_ID, ATTR_CODE, ATTR_VALUE, START_DATE, RSRV_STR1, RSRV_STR2 FROM TF_F_USER_ATTR WHERE USER_ID =:USER_ID AND INST_TYPE=:INST_TYPE AND INST_ID =:INST_ID AND SYSDATE BETWEEN START_DATE AND END_DATE AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND ATTR_CODE =:ATTR_CODE AND ATTR_VALUE =:ATTR_VALUE";
        IData param = new DataMap();
        param.put("USER_ID", memberUserId);
        param.put("ATTR_CODE", teamId);
        param.put("INST_ID", instId);
        param.put("INST_TYPE", "P");
        param.put("ATTR_VALUE", "ctrex_team");

        SQLParser parser = new SQLParser(param);
        parser.addSQL(sqlStr);
        IDataset result = Dao.qryByParse(parser);
        if (null != result && !result.isEmpty())
        {
            return result.getData(0);
        }
        return null;
    }

    /**
     * @Function: getUserAttr
     * @Description: 该函数的功能描述
     * @param:user_id,user_id,attr_code,Pagination
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:21:24 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getUserAttr(String user_id, String inst_type, String attr_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("INST_TYPE", inst_type);
        param.put("ATTR_CODE", attr_code);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  user_id,inst_type,inst_id,attr_code,attr_value,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,");
        parser.addSQL(" update_time,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,");
        parser.addSQL(" rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3");
        parser.addSQL(" FROM TF_F_USER_ATTR ur ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND ur.user_id = to_number(:USER_ID) ");
        parser.addSQL(" AND ur.inst_type = :INST_TYPE ");
        parser.addSQL(" AND ur.PARTITION_ID = MOD(to_number(:USER_ID), 10000) ");
        parser.addSQL(" AND ur.attr_code like '%' || :ATTR_CODE || '%' ");
        parser.addSQL(" AND ur.START_DATE<=SYSDATE ");
        parser.addSQL(" AND ur.END_DATE>SYSDATE ");
        return Dao.qryByParse(parser, pagination);
    }

    /*
     * @description 根据属性编码、属性组编号查询相关属性
     * @author xunyl
     * @date 2013-09-14
     */
    public static IDataset getUserAttrByAttrCode(String attrCode, String attrGroup, String userId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("ATTR_CODE", attrCode);
        inparams.put("RSRV_STR4", attrGroup);

        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_ATTRCODE_ATTRGROUP", inparams);
    }
    /**
     * 根据user_id查用户属性包括已失效的
     * 
     * @param user_id
     * @param attr_code
     * @return
     * @throws Exception
     */
    public static IDataset getUserAllAttrByUserIdAndAttrCode(String userId, String attrCode, String attrValue) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ATTR_CODE", attrCode);
        param.put("ATTR_VALUE", attrValue);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_ALL_BY_USERID_AND_ATTRCODE", param);
        return dataset;
    }

    /**
     * @Function: getUserAttrByInstID
     * @Description: 根据user_id查询对应的用户实例化资料信息 先查询,然后纵表数据 转横表数据 UserDom::USER_ATTR::TF_F_USER_ATTR::SEL_BY_PK
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:23:43 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getUserAttrByInstID(String userId, String instId) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("RELA_INST_ID", instId);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_PK", idata, Route.CONN_CRM_CG);
    }

    /**
     * @Function: getUserAttrByPK
     * @Description: 根据sql_ref, eparchy_code查询用户属性表信息
     * @param:USER_ID,INST_ID
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:28:19 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getUserAttrByPK(String user_id, String inst_id, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RELA_INST_ID", inst_id);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_PK", param, page);
    }

    public static IDataset getUserAttrByRelaInstId(String userId, String instId, String eparchyCode) throws Exception
    {
        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("RELA_INST_ID", instId);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_PK", idata, eparchyCode);
    }

    /**
     * xiekl 根据用户ID， RELA_INST_ID,ATTR_CODE查询属性
     *
     * @param userId
     * @param relaInstId
     * @param attrCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IData getUserAttrByRelaInstIdAndAttrCode(String userId, String relaInstId, String attrCode, String eparchyCode) throws Exception
    {
        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("RELA_INST_ID", relaInstId);
        idata.put("ATTR_CODE", attrCode);
        IDataset attrList = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_RELAINSTID_ATTRCODE", idata, eparchyCode);
        if (attrList != null && !attrList.isEmpty())
        {
            return attrList.getData(0);
        }
        else
        {
            return new DataMap();
        }
    }
    
    public static IDataset getUserAttrByRelaInstIdAndAttrCodeBook(String userId, String relaInstId, String attrCode, String eparchyCode) throws Exception
    {
        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("RELA_INST_ID", relaInstId);
        idata.put("ATTR_CODE", attrCode);
        IDataset attrList = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_RELAINSTID_ATTRCODE_BOOK", idata, eparchyCode);
        return attrList;
    }
    
    public static IData getUserAttrByRelaInstIdAndAttrCodeTWO(String userId, String relaInstId, String attrCode, String elementid, String eparchyCode) throws Exception
    {
        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("RELA_INST_ID", relaInstId);
        idata.put("ATTR_CODE", attrCode);
        idata.put("ELEMENT_ID", elementid);
        IDataset attrList = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_RELAINSTID_ATTRCODETWO", idata, eparchyCode);
        if (attrList != null && !attrList.isEmpty())
        {
            return attrList.getData(0);
        }
        else
        {
            return new DataMap();
        }
    }

    /**
     * @Function: getuserAttrBySvcId
     * @Description:集团 根据user_id,service_id查询用户订购的 实例化信息
     * @param:user_id,service_id
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:30:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getuserAttrBySvcId(String user_id, String service_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        IDataset userattrs = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_SERVICEID", param, Route.CONN_CRM_CG);
        return userattrs;
    }

    public static IDataset getuserAttrByUserIdSvcId(String user_id, String service_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        IDataset userattrs = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_SERVICEID", param);
        return userattrs;
    }
    
    /**
     * @Function: getuserAttrBySvcIdAndInstType
     * @Description: 根据user_id,service_id查询用户订购的 实例化信息
     * @param:user_id,service_id,inst_type
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:31:36 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getuserAttrBySvcIdAndInstType(String user_id, String service_id, String inst_type) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        param.put("INST_TYPE", inst_type);
        IDataset userattrs = Dao.qryByCode("TF_F_USER_ATTR", "SEL_GRP_BY_SERVICEID", param);
        return userattrs;
    }

    /**
     * @Function: getuserAttrBySvcIdAndInstTypeForGrp
     * @Description: 根据user_id,service_id查询用户订购的 实例化信息
     * @param:user_id,service_id,inst_type
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-8-09
     */
    public static IDataset getuserAttrBySvcIdAndInstTypeForGrp(String user_id, String service_id, String inst_type) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        param.put("INST_TYPE", inst_type);
        IDataset userattrs = Dao.qryByCode("TF_F_USER_ATTR", "SEL_GRP_BY_SERVICEID", param, Route.CONN_CRM_CG);
        return userattrs;
    }

    /*
     * 判断物联网的业务接入号是否重复
     */
    public static IDataset getUserAttrByTypeCodeValue(String instType, String attrCode, String attrValue) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("INST_TYPE", instType);
        inparam.put("ATTR_CODE", attrCode);
        inparam.put("ATTR_VALUE", attrValue);
        IDataset result = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_ATTRCODE_INST_TYPE", inparam);
        return result;
    }

    /**
     * @Function: getUserAttrByUserId
     * @Description: 根据USER_ID查询所有参数。
     * @param:user_id
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:35:36 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getUserAttrByUserId(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        IDataset dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID", param);
        return dataset;
    }

    /**
     * 根据user_id查用户属性
     *
     * @param user_id
     * @param attr_code
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrByUserId(String user_id, String attr_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("ATTR_CODE", attr_code);
        IDataset dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_ATTRCODE", param);
        return dataset;
    }

    /**
     * todo getVisit().setRouteEparchyCode(ConnMgr.CONN_CRM_CG);怎么处理
     *
     * @Function: getUserAttrByUserIda
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午2:54:20 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getUserAttrByUserIda(String userId) throws Exception
    {
        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID", idata, Route.CONN_CRM_CG);
    }

    public static IDataset getUserAttrByUserIdAndInstId(String xGetMode, String userId, String inst_id, String inst_type) throws Exception
    {
        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("INST_TYPE", inst_type);
        idata.put("INST_ID", inst_id);
        IDataset dataset = null;
        if ("1".equals(xGetMode))
        {
            dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERINST_NORMAL", idata);// 获取用户有效服务数据
        }
        else if ("2".equals(xGetMode))
        {
            dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERINST_CLOSE", idata);// 获取用户无效服务数据
        }
        else if ("3".equals(xGetMode))
        {
            dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERINST_ALL", idata);// 获取用户所有状态的服务数据
        }

        return dataset;
    }

    /**
     * 根据用户标识和产品标识获取IMS用户参数信息
     *
     * @param inparam
     *            [USER_ID,PRODUCT_ID]
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrByUserIdAndProductId(IData inparam) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_ATTR_VALUE_BY_USERID", inparam);
    }

    /**
     * 根据user_id或参数编码查用户属性
     *
     * @author liuxx3
     * @date 2014 -07-31
     */
    public static IDataset getUserAttrByUserIdc(String user_id, String attr_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("ATTR_CODE", attr_code);
        return Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_BY_ATTRCODE", param);

    }

    /**
     * @Function: getUserAttrByUserIdInstid
     * @Description: SEL_BY_USERID_INSTID2
     * @param:USER_ID,INST_TYPE,INST_ID
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v2.0.0
     * @author: updata
     * @date: 2019-12-18 14:57:05 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2019-12-18 updata v2.0.0 修改原因
     */
    public static IDataset getUserAttrByUserIdInstidForAll(String userId, String inst_type, String inst_id) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("INST_TYPE", inst_type);
        idata.put("RELA_INST_ID", inst_id);
        IDataset dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_INSTID2", idata);
        return dataset;
    }
    /**
     * @Function: getUserAttrByUserIdInstid
     * @Description: SEL_BY_USERID_INSTID
     * @param:USER_ID,INST_TYPE,INST_ID
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:37:05 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getUserAttrByUserIdInstid(String userId, String inst_type, String inst_id) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("INST_TYPE", inst_type);
        idata.put("RELA_INST_ID", inst_id);
        IDataset dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_INSTID", idata);
        return dataset;
    }

    /**
     * todo code_code 表里没有 SEL_BY_USERID_INSTID_EFF
     *
     * @author dingtl
     * @version 创建时间：2009-5-15 下午01:50:07
     */
    public static IDataset getUserAttrByUserIdInstidEff(IData inparams) throws Exception
    {

        IDataset dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_INSTID_EFF", inparams);
        return dataset;
    }

    /**
     * todo code_code 表里没有 SEL_BY_USERID_INSTID
     *
     * @author dingtl
     * @version 创建时间：2010-9-10 下午01:50:07
     */
    public static IDataset getUserAttrByUserIdInstidEndDate(IData inparams) throws Exception
    {

        IDataset dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_INSTID_ENDDATE", inparams);
        return dataset;
    }

    public static IDataset getUserAttrByUserIdInstidForGrp(String userId, String inst_type, String inst_id) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("INST_TYPE", inst_type);
        idata.put("RELA_INST_ID", inst_id);
        IDataset dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_INSTID", idata, Route.CONN_CRM_CG);
        return dataset;
    }

    /**
     * @Function: getUserAttrByUserInstType
     * @Description:从集团库 SEL_BY_ATTRCODE
     * @param:user_id,attr_code
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:40:55 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getUserAttrByUserInstType(String user_id, String attr_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("ATTR_CODE", attr_code);
        IDataset dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_ATTRCODE", param, Route.CONN_CRM_CG);
        return dataset;
    }

    /**
     * 根据用户属性编码查询用户属性
     *
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrInfoByAttrCode(String user_id, String attr_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("ATTR_CODE", attr_code);
        IDataset userattrset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_ATTRCODE", param);
        return userattrset;
    }

    /**
     * 原短信解析使用，跟SEL_BY_ATTRCODE不同
     *
     * @param user_id
     * @param attr_code
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrInfoByAttrCode2(String user_id, String attr_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("ATTR_CODE", attr_code);
        IDataset userattrset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_ATTR_CODE", param);
        return userattrset;
    }

    /**
     * 根据user_id查用户属性包括属性组
     *
     * @param user_id
     * @param attr_code
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrInfoByUserId(String user_id, String attr_code, String groupattr) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("ATTR_CODE", attr_code);
        param.put("RSRV_STR4", groupattr);
        IDataset dataset = new DatasetList();

        // 属性组查询
        if (StringUtils.isNotEmpty(groupattr))
        {
            dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_USERATTR_BY_PK", param);
        }
        else
        {
            dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_ATTRCODE", param);
        }

        return dataset;
    }

    /**
     * 根据服务编码集获取服务参数列表
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrs(IData data) throws Exception
    {
        SQLParser sqlParser = new SQLParser(data);
        sqlParser.addSQL("select INST_ID,partition_id, user_id, inst_type, inst_id, attr_code, attr_value, start_date, end_date, update_time, update_staff_id,");
        sqlParser.addSQL(" update_depart_id, remark, rsrv_num1, rsrv_num2, rsrv_num3, rsrv_num4, rsrv_num5, rsrv_str1, rsrv_str2, rsrv_str3,");
        sqlParser.addSQL(" rsrv_str4, rsrv_str5, rsrv_date1, rsrv_date2, rsrv_date3, rsrv_tag1, rsrv_tag2, rsrv_tag3");
        sqlParser.addSQL("  from tf_f_user_attr a");
        sqlParser.addSQL(" where a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
        sqlParser.addSQL(" and a.user_id = :USER_ID");
        sqlParser.addSQL(" and a.inst_id in(" + data.get("INSTIDS") + ")");
        sqlParser.addSQL(" and a.end_date > TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')");
        sqlParser.addSQL(" and a.inst_type=:INST_TYPE");

        return Dao.qryByParse(sqlParser);
    }

    public static IDataset getUserAttrSelByUserinst(String userId, String disInstId, String instType) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("INST_ID", disInstId);
        param.put("INST_TYPE", instType);

        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERINST", param);
    }

    /**
     * @Function: getUserAttrSingleByPK
     * @Description: 根据主键获取单条记录
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:43:42 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getUserAttrSingleByPK(String user_id, String attr_code, String inst_id, String inst_type) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("ATTR_CODE", attr_code);
        param.put("INST_ID", inst_id);
        param.put("INST_TYPE", inst_type);
        IDataset dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_PK_SINGLE", param);
        return dataset;
    }

    /**
     * @Function: getUserProductAttrByUP
     * @Description:根据USER_ID、PRODUCT_ID查询用户产品属性
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:45:17 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getUserProductAttrByUP(String user_id, String attr_code, String inst_type, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("PRODUCT_ID", attr_code);
        param.put("INST_TYPE", inst_type);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_P_USERID_PRODUCTID", param, page);
    }

    /**
     * 通过userid和userida查询成员用户订购的集团用户的产品属性
     *
     * @param user_id
     * @param userIdA
     * @return
     * @throws Exception
     */
    public static IDataset getUserProductAttrByUserIdAndUserIdA(String user_id, String userIdA, String instType) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", userIdA);
        param.put("INST_TYPE", instType);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_P_USERID_USERIDA", param);
    }

    /**
     * @Function: getUserProductAttrByUT
     * @Description: 该函数的功能描述
     *
     @param user_id
     *            用户标识
     * @param INST_TYPE
     *
     @param page
     *            分页参数
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:50:43 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getUserProductAttrByUT(String user_id, String inst_type, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("INST_TYPE", inst_type);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_P_USERID_TYPE", param, page);
    }

    /**
     * @Function: getUserProductAttrByUTForGrp
     * @Description: 集团用该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午8:55:32 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getUserProductAttrByUTForGrp(String user_id, String inst_type, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("INST_TYPE", inst_type);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_P_USERID_TYPE", param, page, Route.CONN_CRM_CG);
    }

    /**
     * @Function: getUserProductAttrValue
     * @Description: SEL_BY_P_USERID_TYPE_CODE
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:00:16 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getUserProductAttrValue(String user_id, String inst_type, String attr_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("INST_TYPE", inst_type);
        param.put("ATTR_CODE", attr_code);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_P_USERID_TYPE_CODE", param);
    }

    /**
     * 一级BBOSS业务成员用户清单查询--拆分子方法
     *
     * @author liuxx3
     * @date 2014 -07-04
     */
    public static IDataset qryBBossBizMeb(String userId, String status, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT decode(ua.attr_code,'1007','按金额','按比例') as attr_code, ");
        parser.addSQL("        ua.attr_value ");
        parser.addSQL("   FROM TF_F_USER_ATTR ua ");
        parser.addSQL("   WHERE 1=1 ");
        parser.addSQL("   AND ua.user_id = :USER_ID ");
        parser.addSQL("   AND ua.PARTITION_ID = MOD(to_number(:USER_ID), 10000) ");
        parser.addSQL("   AND ua.attr_code in('1007','1008') ");
        parser.addSQL("   AND ua.inst_type='P' ");// 1007和1008属性，只能2选1

        if ("Z".equals(status))// 销户
        {
            parser.addSQL("   AND sysdate>ua.start_date ");// 成员退订后，成员的参数会结束，立即结束
            parser.addSQL("   AND sysdate>ua.end_date ");
        }

        return Dao.qryByParse(parser, pagination);
    }

    /*
     * @description 根据用户编号，参数编号，组编号（ATTR_GROUP）查询参数资料
     * @author zhangcheng
     * @date 2013-08-20
     */
    public static IDataset qryBbossUserAttrForGroupNew(String userId, String attrGroup, String paramCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ATTR_CODE", paramCode);
        param.put("RSRV_STR4", attrGroup);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_ATTRCODE_ATTRGROUP", param);
    }

    /**
     * 查询用户属性信息
     *
     * @param userId
     * @param relaInstId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserAttrByUserRelaInstId(String userId, String relaInstId) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("RELA_INST_ID", relaInstId);

        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_ALL_EXIST_MEMBER", param);
    }

    public static IDataset queryAllExistMember(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_ALL_EXIST_MEMBER", param);
    }

    /**
     * 查询为资费查询资料表中的ICB参数
     *
     * @author liuxx3 2014 -07-04
     */
    public static IDataset queryDiscntAttrByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_DISPARAM_BY_USERID", param);
    }

    public static IDataset queryPlatvsAttrByUIdSId(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_PLATSVC_ATTR_BY_USERID", param);
    }

    /**
     * todo sql 写死在代码里
     *
     * @Function: queryStartDatabyUserAttr
     * @Description:集团 查询出TF_F_USER_ATTR表中的start_data时间
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:02:16 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset queryStartDatabyUserAttr(String user_id, String inst_type, String attr_code, String attr_value) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("INST_TYPE", inst_type);
        param.put("ATTR_CODE", attr_code);
        param.put("ATTR_VALUE", attr_value);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT PARTITION_ID, to_char(USER_ID) USER_ID, INST_TYPE, to_char(INST_ID) INST_ID, ATTR_CODE, ATTR_VALUE,  ");
        parser
                .addSQL(" to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, to_char(RSRV_NUM4) RSRV_NUM4, to_char(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, ");
        parser
                .addSQL(" RSRV_STR5, to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3, to_char(ELEMENT_ID) ELEMENT_ID ");

        parser.addSQL("  FROM TF_F_USER_ATTR  ");
        parser.addSQL(" WHERE USER_ID=TO_NUMBER(:USER_ID) ");
        parser.addSQL("  AND PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000)   ");
        parser.addSQL("   AND INST_TYPE=:INST_TYPE ");
        parser.addSQL("  and ATTR_CODE=:ATTR_CODE  ");
        parser.addSQL("   and ATTR_VALUE=:ATTR_VALUE ");
        parser.addSQL(" AND SYSDATE BETWEEN START_DATE AND END_DATE ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);

    }

    /**
     * @Function: queryUserAllAttrs
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:03:43 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset queryUserAllAttrs(String userId) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USER_ALL", param);
    }

    public static IDataset queryUserAllAttrs(String userId, String eparchyCode) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USER_ALL", param, eparchyCode);
    }

    /**
     * @Function: queryUserAttr
     * @Description:查询用户服务、优惠属性，用于前台展示
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:03:54 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset queryUserAttr(String userId) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_ATTR", "USER_ATTR_SEL", param);
    }

    /*
     * @function: queryUserAttrByAttrValue
     * @descrition: 根据属性编号和属性值查询属性资料表，判断该属性值之前是否被占用
     * @author: xunyl modify zhangcheng6
     * @date: 2013-06-13
     */
    public static IDataset queryUserAttrByAttrValue(String paramCode, String paramValue) throws Exception
    {
        IData param = new DataMap();
        param.put("ATTR_CODE", paramCode);
        param.put("ATTR_VALUE", paramValue);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_ATTRCODE_ATTRVALUE", param);
    }

    /**
     * @Function: queryUserDiscnt
     * @Description: 查询用户已有优惠，用于产品变更前台展示
     * @param
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:04:09 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset queryUserDiscnt(String userId) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_DISCNT", "USER_DISCNT_SEL", param);
    }

    /**
     * @Function: queryUserElement
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:04:25 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset queryUserElement(String userId) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_DISCNT", "USER_ELEMENT_SEL", param);
    }

    /**
     * @Function: queryUserPlatSvc
     * @Description: 查询用户已有平台服务，用于产品变更前台展示
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:05:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset queryUserPlatSvc(String userId) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_PLATSVC", "USER_PLATSVC_SEL", param);
    }

    /**
     * @Function: queryUserSvc
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:05:33 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset queryUserSvc(String userId) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SVC", "USER_SVC_SEL", param);
    }

	/**
	 * 获取学护卡参数表信息
	 * @return
	 * @author liaolc
	 * @date 2014-9-23
	 */
	public static IDataset getUserAttrbyUserIdInsttype(String userId ,String instType)throws Exception{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("INST_TYPE", instType);//需要激活的优惠开始时间标志
		SQLParser parser = new SQLParser(param);
		 parser.addSQL("SELECT to_char(USER_ID) USER_ID, INST_TYPE, to_char(INST_ID) INST_ID,to_char(RELA_INST_ID) RELA_INST_ID, ATTR_CODE, ATTR_VALUE, ");
		 parser.addSQL("to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, ");
		 parser.addSQL("UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, to_char(RSRV_NUM4) RSRV_NUM4, to_char(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, ");
		 parser.addSQL("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		 parser.addSQL("to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3 ");
		 parser.addSQL("FROM TF_F_USER_ATTR attr ");
		 parser.addSQL("WHERE 1=1 ");
		 parser.addSQL("AND USER_ID=TO_NUMBER(:USER_ID) ");
		 parser.addSQL("AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) ");
		 parser.addSQL("AND INST_TYPE=:INST_TYPE  ");
		 parser.addSQL("AND  SYSDATE  BETWEEN attr.start_date AND attr.end_date ");
		 parser.addSQL("AND exists (SELECT 1 FROM TF_F_USER_DISCNT discnt  WHERE discnt.USER_ID = attr.USER_ID  AND SYSDATE BETWEEN discnt.start_date AND discnt.end_date AND   discnt.INST_ID = attr.RELA_INST_ID  AND   discnt.DISCNT_CODE IN('32000001','32000002','32000003'))");
		return Dao.qryByParse(parser);
	}

	/**
	 * 查询用户已有用户属性
	 * @param userId
	 * @param reInstId
	 * @param instType
	 * @param attrCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserAttrByUserIdInstTypeAndId(String userId,String reInstId,String instType,String attrCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RELA_INST_ID", reInstId);
        param.put("INST_TYPE", instType);
        param.put("ATTR_CODE", attrCode);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_INSTTYPEANDID_ATTRCODE", param);
    }


	/**
	 * @param userId
	 * @param timePoint
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserAttrByUserIdDate(String userId,String timePoint) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TIME_POINT", timePoint);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_UID_DATE", param);
    }

	/**
     * 查询产品属性
     *
     * @return
     * @author chenyi
     * @date 2015-2-3
     */
    public static IDataset getUserAttrbyUserIdPro(String userId, String instType, String attrCode, String productId, String attrName, String acctMonth) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_TYPE", instType);
        param.put("ATTR_CODE", attrCode);
        param.put("PRODUCT_ID", productId);
        param.put("ATTR_NAME", attrName);
        
        String firstTime = SysDateMgr.decodeTimestamp(acctMonth, SysDateMgr.PATTERN_STAND);
        String lastTime = SysDateMgr.getAddMonthsLastDay(0, firstTime); 
        param.put("FIRST_TIME", firstTime);
        param.put("LAST_TIME", lastTime);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT to_char(USER_ID) USER_ID, INST_TYPE, to_char(INST_ID) INST_ID,to_char(RELA_INST_ID) RELA_INST_ID, ATTR_CODE, ATTR_VALUE, ");
        parser.addSQL("to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, ");
        parser.addSQL("UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, to_char(RSRV_NUM4) RSRV_NUM4, to_char(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, ");
        parser.addSQL("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3 ");
        parser.addSQL("FROM TF_F_USER_ATTR attr ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND USER_ID=TO_NUMBER(:USER_ID) ");
        parser.addSQL("AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) ");
        parser.addSQL("AND INST_TYPE=:INST_TYPE  ");
        parser.addSQL("AND ATTR_CODE=:ATTR_CODE  ");
        parser.addSQL("AND RSRV_STR3=:ATTR_NAME  ");
        parser.addSQL("AND attr.start_date < to_date(:LAST_TIME, 'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("AND attr.end_date >= to_date(:FIRST_TIME, 'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("AND exists (SELECT 1 FROM TF_F_USER_PRODUCT P  ");
        parser.addSQL("WHERE P.USER_ID = attr.USER_ID   AND   P.INST_ID = attr.RELA_INST_ID  ");
        parser.addSQL("AND P.start_date < to_date(:LAST_TIME, 'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("AND P.end_date >= to_date(:FIRST_TIME, 'yyyy-mm-dd hh24:mi:ss') ) ");
        return Dao.qryByParse(parser);
    }
    
    
    public static IDataset queryUserAttrNoEffect(String userId, String itemId, String idType, String attrCode)throws Exception{
    	IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ITEM_ID", itemId);
        param.put("ID_TYPE", idType);
        param.put("ATTR_CODE", attrCode);
        
        
        return Dao.qryByCodeParser("TF_F_USER_ATTR", "QRY_USER_UNEFFECT_ATTR", param);
    	
    }
    
    /**
     * @Description:通过资费ID查询资费属性信息
     * @author: chenmw3
     * @date: 2017-4-21 
     * @param userId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrByDiscntCode(String userId, String discntCode) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_TYPE", "D");
        param.put("DISCNT_CODE", discntCode);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_DISCNTCODE", param);
    }
    
    /**
     * @Description:通过资费ID、资费属性值查询资费属性信息
     * @author: chenmw3
     * @date: 2017-4-21 
     * @param userId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrByDiscntCodeAttrValue(String userId, String discntCode,String attrValue) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_TYPE", "D");
        param.put("DISCNT_CODE", discntCode);
        param.put("ATTR_VALUE", attrValue);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_DISCNTCODE_ATTRVALUE", param);
    }
    
    /**
     * @Description:通过资费生效时间范围查询资费属性信息
     * @author: chenmw3
     * @date: 2017-4-21 
     * @param userId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrByDiscntCodeAttrValueDateScope(String userId, String discntCode,String attrValue,String qryStartDate, String qryEndDate) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_TYPE", "D");
        param.put("DISCNT_CODE", discntCode);
        param.put("ATTR_VALUE", attrValue);
        param.put("QRY_START_DATE", qryStartDate);
        param.put("QRY_END_DATE", qryEndDate);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_DISCNTCODE_ATTRVALUE_DATESCOPE", param);
    }
    
    /**
     * @Description:通过资费当前时间查询资费属性信息
     * @author: chenmw3
     * @date: 2017-4-21 
     * @param userId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrByDiscntCodeAttrValueThisDate(String userId, String discntCode,String attrValue,String thisDate) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_TYPE", "D");
        param.put("DISCNT_CODE", discntCode);
        param.put("ATTR_VALUE", attrValue);
        param.put("THIS_DATE", thisDate);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_DISCNTCODE_ATTRVALUE_THISDATE", param);
    }
    
    /**
     * @Description:通过资费生效时间范围查询资费属性信息
     * @author: chenmw3
     * @date: 2017-4-21 
     * @param userId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrByDiscntCodeAttrCodeDateScope(String userId, String relaInstId, String discntCode,String attrCode,String qryStartDate, String qryEndDate) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RELA_INST_ID", relaInstId);
        param.put("INST_TYPE", "D");
        param.put("DISCNT_CODE", discntCode);
        param.put("ATTR_CODE", attrCode);
        param.put("QRY_START_DATE", qryStartDate);
        param.put("QRY_END_DATE", qryEndDate);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_DISCNTCODE_ATTRCODE_DATESCOPE", param);
    }

    /**
     * @Description:通过资费ID、资费属性值、资费属性值生效日期查询资费属性信息
     * @author: chenmw3
     * @date: 2017-4-21 
     * @param userId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrByDiscntCodeAttrCodeThisDateNoAttrValue(String userId, String discntCode,String attrCode,String attrValue, String thisDate) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_TYPE", "D");
        param.put("DISCNT_CODE", discntCode);
        param.put("ATTR_CODE", attrCode);
        param.put("ATTR_VALUE", attrValue);
        param.put("THIS_DATE", thisDate);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_DISCNTCODE_ATTRCODE_THISDATE_NOATTRVALUE", param);
    }
    
    /**
     * 查询集团的groupType属性值
     * @param userId
     * @param serviceId
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpUserAttrGroupTypeByUserId(String userId, String serviceId,String attrCode)
    	throws Exception {
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	param.put("SERVICE_ID", serviceId);
        param.put("ATTR_CODE", attrCode);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_ATTRCODE_SERVICEID_USDERID", param);
    }
    /**
     * 根据user_id查集团用户属性包括已失效的
     * 
     * @param user_id
     * @param attr_code
     * @return
     * @throws Exception
     */
    public static IDataset getGrpAttrInfoAttrCode(String userId, String attrCode, String instType) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ATTR_CODE", attrCode);
        param.put("INST_TYPE", instType);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_GRP_ATTRINFO_VAILD", param, Route.CONN_CRM_CG);
        return dataset;
    }

	public static IDataset getUserAttrForEsp(String userId,String relaInstId) throws Exception {
      	 IData idata = new DataMap();
           idata.put("USER_ID", userId);
           idata.put("RELA_INST_ID", relaInstId);
           IDataset dataset = Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_USERATTR_FOR_ESP", idata);
           return dataset;
    }
	
	/**
     * 查询用户已有属性
     * @param userId
     * @param reInstId
     * @param instType
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IDataset queryUserAttrByUserIdReInstId(String userId,String reInstId,String instType,String attrCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RELA_INST_ID", reInstId);
        param.put("INST_TYPE", instType);
        param.put("ATTR_CODE", attrCode);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_RELINSTID_ATTRCODE_LAST", param);
    }
	
	  /**
     * todo code_code 表里没有 SEL_BY_USERID_INSTID
     *
     * @author dingtl
     * @version 创建时间：2010-9-10 下午01:50:07
     */

    public static IDataset getUserAttrByUserIdInstidAndEndDate(String userId, String inst_type, String inst_id) throws Exception
    {

    	IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("INST_TYPE", inst_type);
        idata.put("RELA_INST_ID", inst_id);
        IDataset dataset = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_INSTID_ENDDATE2", idata);
        return dataset;
    }

    public static IDataset getUserAttrByUserIdInsetTypeAttrValue(String userId, String instType, String attrCode, String attrValue) throws Exception {
        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("INST_TYPE", instType);
        idata.put("ATTR_CODE", attrCode);
        idata.put("ATTR_VALUE", attrValue);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_BY_USER_ID_INST_TYPE_ATTR_CODE_VALUE", idata);
        return dataset;
    }

    public static IDataset getUserAttrByRelaInstIdLikeAttrCode(String userId, String instType, String relaInstId, String attrCode) throws Exception {
        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("INST_TYPE", instType);
        idata.put("RELA_INST_ID", relaInstId);
        idata.put("ATTR_CODE", attrCode);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_BY_USER_ID_RELA_INST_ID_TYPE_ATTR_CODE", idata);
        return dataset;
    }
    
    
    /**
     * 查询优惠属性
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset getUserLineInfoByUserId(IData input) throws Exception {

		return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_DISCNT_CODE", input);
	}

    public static IDataset getALLUserDiscntInfoByUserId(String userId, String rela_Inst_type) throws Exception {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RELA_INST_ID", rela_Inst_type);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * FROM TF_F_USER_ATTR t ");
        parser.addSQL(" WHERE T.USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL(" AND T.RELA_INST_ID = :RELA_INST_ID ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
    /**
     * 根据attr_code查询优惠属性
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset getUserLineInfoByUserIdAttrCode(IData input) throws Exception {

		return Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_ALLEXIST_BY_PK", input);
	}
    
    public static IDataset getUserLineInfoByRelaInstId(String userId,String instType,String instId) throws Exception {
    	IData input = new DataMap();
    	input.put("USER_ID", userId);
    	input.put("INST_TYPE", instType);
    	input.put("INST_ID", instId);
		return Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_BY_USERID_INSTID2", input);
	}

    /** 查询有效或即将生效的资费
     * @param userId
     * @param rela_Inst_type
     * @return
     * @throws Exception */
    public static IDataset getALLVaildUserDiscntInfoByUserId(String userId, String rela_Inst_type) throws Exception {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RELA_INST_ID", rela_Inst_type);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * FROM TF_F_USER_ATTR t ");
        parser.addSQL(" WHERE T.USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL(" AND T.RELA_INST_ID = :RELA_INST_ID ");
        parser.addSQL(" AND SYSDATE < T.END_DATE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
    public static IData getUserAttrByRelaInstIdAndAttrCode1(String userId, String relaInstId, String attrCode, String elementid, String eparchyCode) throws Exception
    {
        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("RELA_INST_ID", relaInstId);
        idata.put("ATTR_CODE", attrCode);
        idata.put("ELEMENT_ID", elementid);
        IDataset attrList = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_RELAINSTID_ATTRCODE1", idata, eparchyCode);
        if (attrList != null && !attrList.isEmpty())
        {
            return attrList.getData(0);
        }
        else
        {
            return new DataMap();
        }
    }

	public static IDataset getUserAttrByRelaInstIdLikeAttrCode(String userId, String userIdA) throws Exception {
        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("USER_ID_A", userIdA);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_DISPARAM_BY_USERID_AND_USERIDA", idata);
        return dataset;
    }
    public static IDataset getAttrInfoByUserIdToInstType(String userId, String instType,String relaInstId) throws Exception {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_TYPE", instType);
        param.put("RELA_INST_ID", relaInstId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * FROM TF_F_USER_ATTR t ");
        parser.addSQL(" WHERE T.USER_ID = :USER_ID");
        parser.addSQL(" AND T.INST_TYPE = :INST_TYPE ");
        parser.addSQL(" AND T.RELA_INST_ID = :RELA_INST_ID ");
        parser.addSQL(" AND SYSDATE < T.END_DATE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
}
