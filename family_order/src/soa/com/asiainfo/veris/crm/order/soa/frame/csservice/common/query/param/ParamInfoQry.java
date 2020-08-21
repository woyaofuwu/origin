
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;

public class ParamInfoQry
{

    public static IDataset checkStaffId(String staffId) throws Exception
    {

        IData param = new DataMap();
        param.put("PARA_CODE1", staffId);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_OTHER_BY_STAFFCODE", param);
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='2'
     * 
     * @param tradeId
     * @param serviceId
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkByDisOper(String tradeId, String discntCode, String modifyTag) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("DISCNT_CODE", discntCode);
        params.put("MODIFY_TAG", modifyTag);

        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "SELCNT_TRADEDSCNT_BY_DSCNTTAG", params);
        return dataset.getData(0).getInt("RECORDCOUNT") == 0 ? false : true;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='0'
     * 
     * @param tradeId
     * @param productId
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkByPrdOper(String tradeId, String productId, String modifyTag) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("PRODUCT_ID", productId);
        params.put("MODIFY_TAG", modifyTag);

        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "SELCNT_TRADEPROD_BY_PRODTAG", params);
        return dataset.getData(0).getInt("RECORDCOUNT") == 0 ? false : true;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='1'
     * 
     * @param tradeId
     * @param serviceId
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkBySvcOper(String tradeId, String serviceId, String modifyTag) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("SERVICE_ID", serviceId);
        params.put("MODIFY_TAG", modifyTag);

        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "SELCNT_TRADESVC_BY_SVCTAG", params);
        return dataset.getData(0).getInt("RECORDCOUNT") == 0 ? false : true;
    }

    public static boolean existDestroyRelaTime(String userId, String userIdB) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("USER_ID_B", userIdB);
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "ExistDestroyRelaTime", params);
        return dataset.getData(0).getInt("RECORDCOUNT") == 0 ? false : true;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='S'，splCheckBySql：TD_S_CPARAM-ExistsTradeMultiDiscnt
     * 
     * @param tradeId
     * @param eparchyCode
     * @param paramCode
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean existsTradeMultiDiscnt(String tradeId, String eparchyCode, String paramCode, String modifyTag) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        params.put("EPARCHY_CODE", eparchyCode);
        params.put("PARAM_CODE", paramCode);
        params.put("MODIFY_TAG", modifyTag);

        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "ExistsTradeMultiDiscnt", params);
        return dataset.getData(0).getInt("RECORDCOUNT") == 0 ? false : true;
    }

    /**
     * 是否存在用户优惠
     * 
     * @param userId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static boolean existsUserDiscnt(String userId, String discntCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DISCNT_CODE", discntCode);

        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "ExistsUserDiscnt", param);

        return (dataset.getData(0).getInt("RECORDCOUNT") > 0) ? true : false;
    }

    /**
     * 根据账户标识获取高级付费关系数量
     * 
     * @author liaoyi 2010-12-8
     * @param inparams
     *            查询所需参数(USER_ID)
     * @return IDataset 默认帐户资料
     * @throws Exception
     */
    public static IDataset getAdvPayRelationByAcctID(String ACCT_ID, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", ACCT_ID);
        return Dao.qryByCode("TD_S_CPARAM", "ExistPayRelationAdv", param, page, Route.CONN_CRM_CEN);
    }

    /**
     * 获取渠道用户优惠信息
     */
    public static IDataset getChlDiscntInfo(String userId) throws Exception
    {

        IData param = new DataMap();
        param.put("PARA_CODE1", userId);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_CHL_USERDISCNT", param);
    }

    /**
     * 获取用户CHNL信息
     */
    public static IDataset getChnlOtherInfo(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", "CHNL");
        param.put("PARA_CODE2", userId);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_CHANNEL_OTHER", param);
    }

    public static IDataset getChnlStaffId(String staffId) throws Exception
    {

        IData param = new DataMap();
        param.put("PARA_CODE1", staffId);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_CHL_CUMUINFO", param);
    }

    /**
     * TD一代吉祥号码管理规则
     */
    public static IDataset getCode1ForOpen(String code1) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", code1);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_CODECODE", param);
    }

    // TODO DevelopStaffBean中用到，开发人员去比较逻辑，看看是否能用getCommparaByCode方法替换
    public static IDataset getCommparaByAttrCode(String subsysCode, String paramAttr, String paramCode, String paramCode1, String eparchyCode) throws Exception
    {
        IData param = new DataMap();

        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARAM_CODE1", paramCode1);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_ATTR_CODE", param, Route.CONN_CRM_CEN);
    }

    // 获取获取用户通过统一积分平台兑换的自由优惠
    public static IDataset getCommparaByAttrParaCode(String subsysCode, String paramAttr, String paraCode1, String paraCode3) throws Exception
    {
        IData param = new DataMap();

        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE3", paraCode3);

        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_ATTR_PARA_CODE", param, Route.CONN_CRM_CEN);
    }

    /**
     * 获取commpara表参数
     * 
     * @param subsysCode
     * @param paramAttr
     * @param paramCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaByCode(String subsysCode, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {
        // 查询td_s_commpara表
        IData param = new DataMap();

        if (paramCode == null || paramCode.equals(""))
        {
            param.put("SUBSYS_CODE", subsysCode);
            param.put("PARAM_ATTR", paramAttr);
            param.put("EPARCHY_CODE", eparchyCode);
            return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_ONLY_BY_ATTR_ORDERED", param, Route.CONN_CRM_CEN);
        }
        else
        {
            param.put("SUBSYS_CODE", subsysCode);
            param.put("PARAM_ATTR", paramAttr);
            param.put("PARAM_CODE", paramCode);
            param.put("EPARCHY_CODE", eparchyCode);
            return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
        }
    }

    /**
     * 获取commpara表参数
     * 
     * @param pd
     * @param subsysCode
     * @param paramAttr
     * @param paramCode
     * @param eparchyCode
     * @throws Exception
     */
    public static IDataset getCommparaByCode1(String subSysCode, String paramAttr, String paramCode, String paraCode1, String eparchyCode) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("SUBSYS_CODE", subSysCode);
        qryParam.put("PARAM_ATTR", paramAttr);
        qryParam.put("PARAM_CODE", paramCode);
        qryParam.put("PARA_CODE1", paraCode1);
        qryParam.put("EPARCHY_CODE", eparchyCode);
        IDataset dataset = Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_COMMPARA_BY_PARACODE1", qryParam, Route.CONN_CRM_CEN);

        return dataset;
    }

    public static IDataset getCommparaByParamattr(String subsysCode, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
    }

    /**
     * 通过SUBSYS_CODE、PARAM_ATTR、PARAM_CODE、PARA_CODE1、EPARCHY_CODE获取参数信息
     * 
     * @param pd
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static IDataset getCommparaInfoBy5(String subsysCode, String paramAttr, String paramCode, String paraCode1, String eparchyCode) throws Exception
    {
        IData param = new DataMap();

        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr); // 类型
        param.put("PARAM_CODE", paramCode);//
        param.put("PARA_CODE1", paraCode1);// PRODUCT_ID
        param.put("EPARCHY_CODE", eparchyCode);

        IDataset dataset = Dao.qryByCode("TD_S_COMMPARA", "SEL3_PK_TD_S_COMMPARA", param);
        return dataset;
    }

    /**
     * 根据param_attr，subsys_code，PARAM_CODE查询参数
     * 
     * @author shixb
     * @version 创建时间：2009-5-29 下午07:29:03
     */
    public static IDataset getCommparaInfoByAttrAndParam(IData inparams) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTR_PARAM_CODE", inparams);
    }

    /**
     * 获取手机保障和手机损坏保障的区别
     * 
     * @param param
     *            PARAM_CODE PARA_CODE1 EPARCHY_CODE
     * @return
     * @throws Exception
     */
    public static IDataset getCparamInfo(String paramCode, String paramCode1, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE1", paramCode1);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_CPARAM", "ExistsValueLimit", param);
    }

    public static IDataset getCparamsBySnDays(String serialNumber, String days, String monthid) throws Exception
    {
        IData param = new DataMap();
        param.put("DAYS", days);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("MONTHID", monthid);
        return Dao.qryByCode("TD_S_CPARAM", "SEL_EXIST_SMS_RECORD_1", param);

    }

    public static IDataset getCparamsBySnDaysInModeCode(String serialNumber, String days, String inModeCode, String monthid) throws Exception
    {
        IData param = new DataMap();
        param.put("DAYS", days);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("IN_MODE_CODE", inModeCode);
        param.put("MONTHID", monthid);
        return Dao.qryByCode("TD_S_CPARAM", "SEL_EXIST_SMS_RECORD_2", param);

    }

    /**
     * 根据tabname,sqlref查询参数信息
     * 
     * @param bizContext
     * @param inparams
     * @param sType
     * @param sDefault
     * @return
     * @throws Exception
     * @author linyl3
     * @date: 2013-03-11
     */
    public static IData getCsmTagInfo(String eparchCode, String tagCode, String subsysCode, String useTag, String sType, String sDefault) throws Exception
    {
        if ("".equals(eparchCode))
        {
            eparchCode = CSBizBean.getVisit().getStaffEparchyCode();
        }

        if ("".equals(subsysCode))
        {
            subsysCode = "CSM";
        }

        if ("".equals(useTag))
        {
            useTag = "0";
        }

        // 用公共方法
        IData queryResult = new DataMap();

        IDataset ds = TagInfoQry.getTagInfoBySubSys(eparchCode, tagCode, useTag, subsysCode, null);
        if (ds == null || ds.size() < 1)
        {
            if ("0".equals(sType))
            {
                queryResult.put("TAG_CHAR", sDefault);
            }
            else if ("1".equals(sType))
            {
                queryResult.put("TAG_INFO", sDefault);
            }
            else if ("2".equals(sType))
            {
                queryResult.put("TAG_NUMBER", sDefault);
            }
            else if ("3".equals(sType))
            {
                if ("0".equals(sDefault))
                {
                    queryResult.put("TAG_DATE", SysDateMgr.getSysTime());
                }
                else
                {
                    queryResult.put("TAG_DATE", sDefault);
                }
            }
            else if ("4".equals(sType))
            {
                queryResult.put("TAG_SEQUID", sDefault);
            }
            else
            {
                return new DataMap();
            }

            return queryResult;
        }
        else
        {
            return ds.getData(0);
        }
    }

    /**
     * 获取账期
     * 
     * @param refDate
     * @return
     * @throws Exception
     */
    public static IDataset getCycleByDate(String refDate) throws Exception
    {
        IData param = new DataMap();
        param.put("REF_DATE", refDate);
        return Dao.qryByCode("TD_B_CYCLE", "SEL_BY_REF_DATE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getDefaultSvcCount(String custId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("SERVICE_ID", serviceId);
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "COUNT_USER_DEFAULTSVC", param);
        return dataset;
    }

    /**
     * 查询吉祥号码的费用
     */
    public static String getFeeBySpecSerailNumber(String serialNumber, String eparchyCode) throws Exception
    {
        String i = "0";
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("EPARCHY_CODE", eparchyCode);
        IDataset dataset = Dao.qryByCodeParser("TD_S_CPARAM", "SEL_FEE_BY_SN", params);
        if (IDataUtil.isNotEmpty(dataset))
        {
            i = dataset.getData(0).getString("PARA_CODE1");

        }
        return i;
    }

    public static IDataset getGroupKeyBySN(String custId, String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("SERIAL_NUMBER", serialNumber);
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "SEL_KEYMAN_BY_SN", param);
        return dataset;
    }

    /**
     * 检查集团用户是否申请销户
     * 
     * @author liaolc
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getGroupOut(IData data) throws Exception
    {

        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "IsCustProductTradeDestory", data);
        return dataset;
    }

    /**
     * @Function: getGroupUser
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-7-5 下午8:56:56 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-7-5 lijm3 v1.0.0 修改原因
     */
    public static IDataset getGroupUser(String user_id, String check_tag) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID_B", user_id);
        data.put("CHECK_TAG", check_tag);
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "IsGroupUser", data);
        return dataset;
    }

    /**
     * 根据账户标识获取集团普通付费关系数量
     * 
     * @author liaoyi 2011-7-21
     * @param inparams
     *            查询所需参数(USER_ID)
     * @return IDataset 默认帐户资料
     * @throws Exception
     */
    public static IDataset getGrpNorPayRelationByAcctID(String ACCT_ID, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", ACCT_ID);

        return Dao.qryByCode("TD_S_CPARAM", "ExistPayRelationNor", param, page, Route.CONN_CRM_CG);
    }

    /**
     * 获取配置集团预存兑奖比例 根据是否专线
     * 
     * @author fuzn 2011-12-6
     * @return 0
     * @throws Exception
     */
    public static IDataset getGrpPreSavePrizeRateByCon(String qtickType) throws Exception
    {
        IData data = new DataMap();
        data.put("PARAM_CODE", qtickType);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select param_code,para_code1,para_code2,para_code3 from td_s_commpara  ");
        parser.addSQL(" where subsys_code='CGM' and param_attr='1201' and param_code=:PARAM_CODE ");
        IDataset rstDataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        return rstDataset;
    }

    /**
     * 获取修改操作时HSS ifc templeId 配置数据
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getHSSConfigData4ModiByUserType(IData inparams) throws Exception
    {
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_PARACODE2", inparams, Route.CONN_CRM_CEN);
    }

    /**
     * 根据账户标识获取普通付费关系数量
     * 
     * @author liaoyi 2010-4-8
     * @param inparams
     *            查询所需参数(USER_ID)
     * @return IDataset 默认帐户资料
     * @throws Exception
     */
    public static IDataset getNorPayRelationByAcctID(IData inparams) throws Exception
    {
        return Dao.qryByCode("TD_S_CPARAM", "ExistPayRelationNor", inparams);
    }

    /**
     * 根据号码查询DEPART_ID
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-8-26
     */
    public static IDataset getNumberDepartInfo(String serialNumber) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        String sql = "SELECT CUMU_NBR,CUMU_ID,CUMU_NAME,MOBILE_NUM,ADDRESS,AREA_CODE,CUMU_DEPART_ID," + "PERSON_ID,STAFF_ID,CUMU_TYPE,CUMU_ATTR FROM CHNL_CU_CUMUINFO WHERE UPDATE_STATE = '0' AND MOBILE_NUM =:SERIAL_NUMBER";
        return Dao.qryBySql(new StringBuilder(sql), param);
    }

    /**
     * 获取办理业务的校验方式
     * 
     * @param tradeId
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset getParamForIdentifyAuth(String tradeId, String serialNumber) throws Exception
    {
        IData param = new DataMap();

        param.put("SERIAL_NUMBER", serialNumber);
        param.put("RSRV_STR1", tradeId);
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "SEL_BY_IDENTITYAUTH_LOG", param);
        return dataset;
    }

    public static IDataset getPlatsvcParam(String bizTypeCode) throws Exception
    {
        IData data = new DataMap();
        data.put("BIZ_TYPE_CODE", bizTypeCode);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT T.ORG_DOMAIN FROM td_b_platsvc_param T where T.BIZ_TYPE_CODE=:BIZ_TYPE_CODE");
        IDataset rstDataset = Dao.qryByParse(parser);
        return rstDataset;
    }

    /**
     * 获取打印配置
     * 
     * @param tradeTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset getPrintCfg(IData inparams) throws Exception
    {
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_SUBATTRCODECODE1", inparams, Route.CONN_CRM_CEN);
    }

    /**
     * 从td_s_commpara表获取手工补录收款功能需要调用的存储过程
     * 
     * @param subsysCode
     * @param paramAttr
     * @param paramCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProcNameByHandGatherFee(String subsysCode, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询BB关系 liaolc 2013-09-06
     * 
     * @author zhanghw
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getProductBB(IData data) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "IsGrpMebBB", data);
        return dataset;
    }

    /**
     * 查询uu关系
     * 
     * @author zhanghw
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getProductUU(IData data) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "IsGrpMebUU", data);
        return dataset;
    }

    public static IDataset getReachScore(IData inparams, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_REACHSCOREQRY", inparams, pagination);
    }

    public static IDataset getReachScore(IData inparams, Pagination pagination, String eparchyCode) throws Exception
    {
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_REACHSCOREQRY", inparams, pagination, eparchyCode);
    }

    /**
     * 获取commpara表参数
     * 
     * @author fuzn
     * @date 2013-03-07
     */
    public static IDataset getSelOnlyByAttrOrdered(String SUBSYS_CODE, String PARAM_ATTR, String EPARCHY_CODE, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", SUBSYS_CODE);
        param.put("PARAM_ATTR", PARAM_ATTR);
        param.put("EPARCHY_CODE", EPARCHY_CODE);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ONLY_BY_ATTR_ORDERED", param, page, Route.CONN_CRM_CEN);
    }

    /**
     * 获取Sim卡类型
     * 
     * @param subsysCode
     * @param paramAttr
     * @param paramCode
     * @param paramCode2
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getSimCardType(String subsysCode, String paramAttr, String paramCode, String paramCode2, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE1", paramCode2);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_CODE_PARACODE1", param);
    }

    /**
     * sql传递
     * 
     * @author sunxin
     */
    public static IDataset getSqlForOpen(String sql) throws Exception
    {
        IData data = new DataMap();

        SQLParser parser = new SQLParser(data);
        parser.addSQL(sql);
        IDataset rstDataset = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        return rstDataset;
    }

    /**
     * 根据标记对应的地州EPARCHY_CODE、标记编码TAG_CODE、子系统编码SUBSYS_CODE和标记对应的作用标志USE_TAG查看系统个性参数表
     * 
     * @author zhoubing
     * @param inparams
     * @return
     * @throws Exception
     */

    public static IDataset getTagInfoBySubSys(String TAG_CODE, String SUBSYS_CODE, String USE_TAG, String EPARCHY_CODE) throws Exception
    {
        IData param = new DataMap();
        param.put("TAG_CODE", TAG_CODE);
        param.put("SUBSYS_CODE", SUBSYS_CODE);
        param.put("USE_TAG", USE_TAG);
        param.put("EPARCHY_CODE", EPARCHY_CODE);
        return Dao.qryByCode("TD_S_TAG", "SEL_ALL_BY_TAGCODE", param, Route.CONN_CRM_CEN);
    }

    /**
     * 获取用户优惠信息
     */
    public static IDataset getUserDiscntInfo(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", userId);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_USERDISCNT", param);
    }

    public static IDataset getVipCount(String userId) throws Exception
    {
        IData inParams = new DataMap();
        inParams.put("USER_ID", userId);
        return Dao.qryByCodeParser("TD_S_CPARAM", "VIP_REMAIN_COUNT", inParams);
    }

    /**
     * 获取代理商套餐(VPMN JPA)信息
     */
    public static IDataset getVpmnJpaInfo(String userId) throws Exception
    {

        IData param = new DataMap();
        param.put("PARA_CODE1", userId);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_JPADISCNT", param);
    }

    /**
     * 获取写卡参数信息
     * 
     * @param subsysCode
     * @param paramAttr
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getWriteSimCardInfo(String subsysCode, String paramAttr, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTRPARAM_CODE", param);
    }

    /**
     * 是否铁通号码段内地州
     * 
     * @author lic
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean isExistLocalSerialnumber(String serialNumber) throws Exception
    {

        IData data = new DataMap();
        String numCode = "";
        if (ProvinceUtil.isProvince(ProvinceUtil.YUNN) || ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {
            numCode = serialNumber.substring(0, 3);
            data.put("PARENT_AREA_CODE", numCode);
            IDataset dataset = UAreaInfoQry.qryAreaByParentAreaCode(numCode);
            return dataset.size() > 0 ? true : false;
        }
        else if (ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
            numCode = serialNumber.substring(0, 3);
            if ("029".equals(numCode))
            {
                return true;
            }
            else
            {
                numCode = serialNumber.substring(0, 4);
                data.put("PARENT_AREA_CODE", numCode);
                IDataset dataset = UAreaInfoQry.qryAreaByParentAreaCode(numCode);
                return dataset.size() > 0 ? true : false;
            }
        }
        else if (ProvinceUtil.isProvince(ProvinceUtil.TJIN))
        {
            numCode = serialNumber.substring(0, 3);
            if ("022".equals(numCode))
            {
                return true;
            }
            else
            {
                numCode = serialNumber.substring(0, 4);
                data.put("PARENT_AREA_CODE", numCode);
                IDataset dataset = UAreaInfoQry.qryAreaByParentAreaCode(numCode);
                return dataset.size() > 0 ? true : false;
            }
        }
        return false;
    }

    public static boolean isExistsBW(String ecUserId, String mebServiceId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EC_USER_ID", ecUserId);
        param.put("SERVICE_ID", mebServiceId);

        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "IsUserBlackWhiteMeb", param, eparchyCode);

        return (dataset.getData(0).getInt("RECORDCOUNT") > 0) ? true : false;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='S'，splCheckBySql：TD_S_CPARAM-IsTradeOtherLike
     * 
     * @param rsrvValueCode
     * @param rsrvStr1
     * @param rsrvStr2
     * @param rsrvStr3
     * @param rsrvStr4
     * @param rsrvStr5
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean isTradeOtherLike(String tradeId, String rsrvValueCode, String rsrvStr1, String rsrvStr2, String rsrvStr3, String rsrvStr4, String rsrvStr5, String modifyTag) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("RSRV_VALUE_CODE", rsrvValueCode);
        params.put("RSRV_STR1", rsrvStr1);
        params.put("RSRV_STR2", rsrvStr2);
        params.put("RSRV_STR3", rsrvStr3);
        params.put("RSRV_STR4", rsrvStr4);
        params.put("RSRV_STR5", rsrvStr5);
        params.put("MODIFY_TAG", modifyTag);

        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "IsTradeOtherLike", params);
        return dataset.getData(0).getInt("RECORDCOUNT") == 0 ? false : true;
    }

    /**
     * 手机游戏消费记录 查询受理内容
     * 
     * @param operaType
     * @return
     * @throws Exception
     */
    public static IDataset qryAcceptContent(String operaType) throws Exception
    {
        IData param = new DataMap();

        param.put("OPERATE_CODE", operaType);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_MOBILE_OPERATE", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询TD_B_QRY_CONFIG表配置信息
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryTdBQryConfig() throws Exception
    {
        return Dao.qryByCode("TD_S_CPARAM", "SEL_QRY_CONFIG_BY_PRODUCTID", null, Route.CONN_CRM_CEN);
    }

    /**
     * 非boss收款查询用户全称
     * 
     * @param subsysCode
     * @param paramAttr
     * @param paramCode
     * @param paramName
     * @return
     * @throws Exception
     */
    public static IDataset queryPayName(String subsysCode, String paramAttr, String paramCode, String paramName) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARAM_NAME", paramName);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_PAY_NAME_LIST", param, Route.CONN_CRM_CEN);
    }

    /**
     * 宽带子账号开户 子账号个数限制
     * 
     * @param data
     * @return dataset
     * @throws Exception
     */
    public static IDataset querySubAcctCount(String subsysCode, String paramAttr, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("SUBSYS_CODE", subsysCode);
        data.put("PARAM_ATTR", paramAttr);
        data.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ONLY_BY_ATTR_ORDERED", data);
    }

    /**
     * 保存用户第一次回复的回馈结果，加下发号码作条件
     * 
     * @author zhuyu
     * @date 2014-6-26
     * @param pd
     * @param data
     * @throws Exception
     */
    public static int saveUserAnswer3(String revertSmsContent, String serialNumber, String replyNumber, String days) throws Exception
    {
        IData param = new DataMap();
        param.put("REVERT_SMS_CONTENT", revertSmsContent);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("REPLY_NUMBER", replyNumber);
        param.put("DAYS", days);
        return Dao.executeUpdateByCodeCode("TD_S_CPARAM", "UPD_TI_O_TRADE_SMS_3", param);
    }

    /**
     * 保存用户第二次回复的回馈结果，加下发号码作条件
     * 
     * @author zhuyu
     * @date 2014-6-26
     * @param pd
     * @param data
     * @throws Exception
     */
    public static int saveUserAnswer4(String revertSmsContent, String serialNumber, String replyNumber, String days) throws Exception
    {
        IData param = new DataMap();
        param.put("REVERT_SMS_CONTENT", revertSmsContent);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("REPLY_NUMBER", replyNumber);
        param.put("DAYS", days);
        return Dao.executeUpdateByCodeCode("TD_S_CPARAM", "UPD_TI_O_TRADE_SMS_4", param);
    }

    /**
     * @param tradeId
     * @param userId
     * @param eparchyCode
     * @param paraCode3
     * @param paraCode4
     * @return
     * @throws Exception
     */
    public static boolean selMultiCount(String tradeId, String userId, String eparchyCode, String paraCode3, String paraCode4) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("USER_ID", userId);
        params.put("EPARCHY_CODE", eparchyCode);
        params.put("PARA_CODE3", paraCode3);
        params.put("PARA_CODE4", paraCode4);

        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "SEL_MULTI_COUNT", params);
        return dataset.getData(0).getInt("RECORDCOUNT") == 0 ? false : true;
    }
    
    public static IDataset queryComminfoByParamCodeParaCode1And2(String paramAttr, String paramCode, String subsysCode, String paraCode1, String paraCode2, String eparchyCode)throws Exception
    {
    	IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE2", paraCode2);
        param.put("EPARCHY_CODE", eparchyCode);
        
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTR_CODE_PARACODE1AND2", param);
    }
}
