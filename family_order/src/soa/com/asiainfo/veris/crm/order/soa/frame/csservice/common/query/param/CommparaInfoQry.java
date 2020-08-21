
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.biz.service.BizRoute;
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
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class CommparaInfoQry
{
    /**
     * @param inparams
     *            (PARAM_CODE/PARAM_VALUE/EPARCHY_CODE)
     * @return
     * @throws Exception
     * @author Liuyt3
     * @date 2011/1/12
     */
	private final static String COLOUMS="SUBSYS_CODE,PARAM_ATTR,PARAM_CODE,PARAM_NAME,PARA_CODE1,PARA_CODE2,PARA_CODE3,PARA_CODE4,PARA_CODE5,PARA_CODE6,PARA_CODE7,PARA_CODE8,PARA_CODE9,PARA_CODE10,PARA_CODE11,PARA_CODE12,PARA_CODE13,PARA_CODE14,PARA_CODE15,PARA_CODE16,PARA_CODE17,PARA_CODE18,PARA_CODE19,PARA_CODE20,PARA_CODE21,PARA_CODE22,PARA_CODE23,PARA_CODE24,PARA_CODE25,PARA_CODE26,PARA_CODE27,PARA_CODE28,PARA_CODE29,PARA_CODE30,START_DATE,END_DATE,EPARCHY_CODE,UPDATE_STAFF_ID,UPDATE_DEPART_ID,UPDATE_TIME,REMARK";
	
    public static IDataset get3527ComparaByCodeAndValue(String PARAM_CODE, String PARAM_VALUE, String EPARCHY_CODE, Pagination page) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("PARAM_CODE", PARAM_CODE);
        inparams.put("PARAM_VALUE", PARAM_VALUE);
        inparams.put("EPARCHY_CODE", EPARCHY_CODE);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_3527COMPARA_BY_CODEANDVALUE", inparams, page, Route.CONN_CRM_CEN);
    }

    public static IDataset getBetwenCommparaInfoByCode1(String subsysCode, String paramAttr, String paraCode1, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE1", paraCode1);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BETWEN_CODE1", param, Route.CONN_CRM_CEN);
    }

    /**
     * 获取渠道编码
     */
    public static IDataset getChlCode(IData param) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_CHLCHANNEL", param);
    }

    /**
     * 获取渠道名称/星级
     */
    public static IDataset getChlName(String para_code1) throws Exception
    {
        IData param = new DataMap();

        param.put("PARA_CODE1", para_code1);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_CHLCHANNEL_BY_CODE", param);
    }

    /**
     * 通过param_attr和subsys_code查询
     * 
     * @param subsysCode
     * @param paramAttr
     * @param eparchyCode
     * @return
     * @throws Exception
     * @author xiekl
     */
    public static IDataset getCommByParaAttr(String subsysCode, String paramAttr, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("SUBSYS_CODE", subsysCode);
        data.put("PARAM_ATTR", paramAttr);
        data.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAATTR", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getCommNetInfo(String subsysCode, String paramAttr, String paramCode) throws Exception
    {
        IData data = new DataMap();
        data.put("SUBSYS_CODE", subsysCode);
        data.put("PARAM_ATTR", paramAttr);
        data.put("PARAM_CODE", paramCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_SUBATTRPARAMCODE", data, Route.CONN_CRM_CEN);
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
    public static IDataset getCommpara(String subsysCode, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {

        // 查询td_s_commpara表
        IData param = new DataMap();

        if (paramCode == null || paramCode.equals(""))
        {
            param.put("SUBSYS_CODE", subsysCode);
            param.put("PARAM_ATTR", paramAttr);
            param.put("EPARCHY_CODE", eparchyCode);
            return Dao.qryByCode("TD_S_COMMPARA", "SEL_ONLY_BY_ATTR_ORDERED", param, Route.CONN_CRM_CEN);
        }
        else
        {
            param.put("SUBSYS_CODE", subsysCode);
            param.put("PARAM_ATTR", paramAttr);
            param.put("PARAM_CODE", paramCode);
            param.put("EPARCHY_CODE", eparchyCode);
            return Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
        }

    }

    /**
     * 查询commpara 表配置 查所有字段
     * 
     * @param strSubsysCode
     * @param iParamAttr
     * @param strParamCode
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaAllCol(String strSubsysCode, String iParamAttr, String strParamCode, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("PARAM_ATTR", iParamAttr);
        param.put("PARAM_CODE", strParamCode);
        param.put("EPARCHY_CODE", strEparchyCode);
        IDataset output = Dao.qryByCode("TD_S_COMMPARA", "SEL_ALL_COL", param, Route.CONN_CRM_CEN);
        // if (IDataUtil.isEmpty(output))
        // {
        // return null;
        // }
        // for (int row = 0, size = output.size(); row < size; row++)
        // {
        // IData info = output.getData(row);
        // String strCode = info.getString("PARA_CODE1");
        // if (StringUtils.isNotBlank(strCode))
        // {
        // String strName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_NOTEITEM", new String[]
        // { "NOTE_ITEM_CODE", "PARENT_ITEM_CODE" }, "NOTE_ITEM", new String[]
        // { strCode, "-1" });
        // info.put("PARA_CODE1_NAME", strName);
        // }
        //
        // }
        return output;
    }
    
    /**
     * 查询commpara 表配置 查所有字段
     * 
     * @param strSubsysCode
     * @param iParamAttr
     * @param paraCode1
     * @param strEparchyCode
     * @author wuwangfeng
     * @date 2020/5/13 21:30
     */
    public static IDataset getCommparaAllColRevert(String strSubsysCode, String iParamAttr, String strParaCode1, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("PARAM_ATTR", iParamAttr);
        param.put("PARA_CODE1", strParaCode1);
        param.put("EPARCHY_CODE", strEparchyCode);
        IDataset output = Dao.qryByCode("TD_S_COMMPARA", "SEL_ALL_COL_REVERT", param, Route.CONN_CRM_CEN);
        return output;
    }

    public static IDataset getCommparaAllColByParser(String strSubsysCode, String iParamAttr, String strParamCode, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("PARAM_ATTR", iParamAttr);
        param.put("PARAM_CODE", strParamCode);
        param.put("EPARCHY_CODE", strEparchyCode);

        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_ALL_COL", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qryUserSalePackage(String productId, String packageId, String beforeLevel, String afterLevel) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_CODE", productId);
        param.put("PARA_CODE1", packageId);
        param.put("BEFORE_LEVEL", beforeLevel);
        param.put("AFTER_LEVEL", afterLevel);

        IDataset userInfo = Dao.qryByCode("TD_S_COMMPARA", "SEL_USER_SALE_PACKAGE", param, Route.CONN_CRM_CEN);

        return userInfo;
    }

    // ======================================================================================

    /**
     * 查询td_s_commpara表，根据param_attr,param_code,para_code1,para_code2,para_code3,para_code4的条件查询，表中para_code1肯定有值，
     * 其它2到4可以为null
     * 
     * @param subsysCode
     * @param paramAttr
     * @param paramCode
     * @param paraCode1
     * @param paraCode2
     * @param paraCode3
     * @param paraCode4
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaBy1to4(String subsysCode, String paramAttr, String paramCode, String paraCode1, String paraCode2, String paraCode3, String paraCode4, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE2", paraCode2);
        param.put("PARA_CODE3", paraCode3);
        param.put("PARA_CODE4", paraCode4);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARA_CODE1to4", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据paraCode1的条件查询TF_SA_ACTION_CODE表
     * 
     * @param subsysCode
     * @param paramAttr
     * @param paraCode1
     * @param paraCode2
     * @param paraCode3
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaByAction(String paraCode1) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", paraCode1);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_188ACTIONCODE", param, Route.CONN_CRM_CEN);// 需要处理 sunxin
    }

    /**
     * 根据subsys_code和param_attr查询参数
     * 
     * @author mengjh
     * @param inparams
     * @param pagination
     * @return 参数配置
     * @throws Exception
     */
    public static IDataset getCommparaByAttrCode1(String subsysCode, String paramAttr, String paraCode1, String eparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE1", paraCode1);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAATTR_CODE1", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据subsys_code和param_attr查询参数
     * 
     * @author zhuyu
     * @param inparams
     * @param pagination
     * @return 参数配置
     * @throws Exception
     */
    public static IDataset getCommparaByAttrCode2(String subsysCode, String paramAttr, String paraCode2, String eparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE2", paraCode2);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAATTR_CODE2", param, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset getCommParaByBookDate(String subsys_code, String param_attr, String param_code, String para_code1, String eparchy_code, String book_date) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsys_code);
        param.put("PARAM_ATTR", param_attr);
        param.put("PARAM_CODE", param_code);
        param.put("PARA_CODE1", para_code1);
        param.put("EPARCHY_CODE", eparchy_code);
        param.put("BOOK_DATE", book_date);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARA_CODE1_2", param, Route.CONN_CRM_CEN);

    }

    /**
     * @param subSysCode
     * @param paramAttr
     * @param paraCode1
     * @param eparchyCode
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-22
     */
    public static IDataset getCommparaByCode1(String subSysCode, String paramAttr, String paraCode1, String eparchyCode) throws Exception
    {

        IData param = new DataMap();
        param.put("SUBSYS_CODE", subSysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE1", paraCode1);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARACODE1", param, Route.CONN_CRM_CEN);
    }
    
    /**
     * 通过SUBSYS_CODE、PARAM_ATTR、PARAM_CODE、PARA_CODE1获取参数信息
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @author wuwangfeng
     */
    public static IDataset getCommparaByCode23(String subSysCode, String paramAttr, String paramCode, String paraCode1) throws Exception
    {

        IData param = new DataMap();
        param.put("SUBSYS_CODE", subSysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE1", paraCode1);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_CODE1_BY_PARACODE1", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getCommparaByCode1(String subsysCode, String paramAttr, String paraCode, String paraCode1, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARAM_CODE", paraCode);

        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_CODE_PARACODE1", param, null, Route.CONN_CRM_CEN);
    }

    /**
     * 根据paramAttr,paraCode1,paraCode2,paraCode3的条件查询commpara表
     * 
     * @param subsysCode
     * @param paramAttr
     * @param paraCode1
     * @param paraCode2
     * @param paraCode3
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaByCode1to3(String subsysCode, String paramAttr, String paramCode, String paraCode1, String paraCode2, String paraCode3, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE2", paraCode2);
        param.put("PARA_CODE3", paraCode3);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARA_CODE1TO3", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据para_code4,para_code21查询配置
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @author Liuyt3
     * @date 2011/12/21
     */
    public static IDataset getCommparaByCode4Code21(String subsysCode, String paramAttr, String paraCode4, String paraCode21, String EPARCHY_CODE, Pagination pagination) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SUBSYS_CODE", subsysCode);
        inparams.put("PARAM_ATTR", paramAttr);
        inparams.put("PARA_CODE4", paraCode4);
        inparams.put("PARA_CODE21", paraCode21);
        inparams.put("EPARCHY_CODE", EPARCHY_CODE);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_PROVCOMMPARA_BY_PARA_CODE4", inparams, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * @param subSysCode
     * @param paramAttr
     * @param paramCode
     * @param paraCode1
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-21
     */
    public static IDataset getCommparaByCodeCode1(String subSysCode, String paramAttr, String paramCode, String paraCode1) throws Exception
    {

        IData param = new DataMap();
        param.put("SUBSYS_CODE", subSysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE1", paraCode1);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_CODE_PARACODE1", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getCommparaByCodeName(String subSysCode, String paramAttr, String paramCode, String paraName) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subSysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARAM_NAME", paraName);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_CODE_PARANAME", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据param_attr和param_code查询td_s_commpara表，且待比较的值>=para_code4且<para_code5的值
     * 
     * @param subsysCode
     * @param paramAttr
     * @param paramCode
     * @param compareValue
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaByCompare45(String subsysCode, String paramAttr, String compareValue, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("COMPARE_VALUE", compareValue);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAM_COMPARE45", param, Route.CONN_CRM_CEN);
    }

    // modify by xiaozb 20140325
    public static IDataset getCommparaByParaAttr(String subsysCode, String paramAttr, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("EPARCHY_CODE", strEparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAATTR", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getCommparaByParaCode(String subSysCode, String paramAttr, String paramCode, String paraCode2, String paraCode3, String paraCode5, String paraCode6) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subSysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE2", paraCode2);
        param.put("PARA_CODE3", paraCode3);
        param.put("PARA_CODE5", paraCode5);
        param.put("PARA_CODE6", paraCode6);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARACODE1_6", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询commpara 表配置 只查PARA_CODE1
     * 
     * @param strSubsysCode
     * @param iParamAttr
     * @param strParamCode
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaCode1(String strSubsysCode, String iParamAttr, String strParamCode, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("PARAM_ATTR", iParamAttr);
        param.put("PARAM_CODE", strParamCode);
        param.put("EPARCHY_CODE", strEparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ONLY_CODE1", param, Route.CONN_CRM_CEN);
    }

    /**
     * 通过SUBSYS_CODE、PARAM_ATTR、PARAM_CODE、PARA_CODE1、EPARCHY_CODE获取参数信息
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static IDataset getCommparaInfoBy16(String subsysCode, String paramAttr, String paramCode, String paraCode1, String paraCode6, String eparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE6", paraCode6);
        param.put("PARAM_CODE", paramCode);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAATTR_CODE16", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 通过SUBSYS_CODE、PARAM_ATTR、PARAM_CODE、PARA_CODE1、EPARCHY_CODE获取参数信息
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static IDataset getCommparaInfoBy5(String subsysCode, String paramAttr, String paramCode, String paraCode1, String eparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARAM_CODE", paramCode);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL3_PK_TD_S_COMMPARA", param, pagination, Route.CONN_CRM_CEN);
    }
    
    /**
     * 通过SUBSYS_CODE、PARAM_ATTR、PARAM_CODE、PARA_CODE1、PARA_CODE7、EPARCHY_CODE获取参数信息
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static IDataset getCommparaInfoBy7(String subsysCode, String paramAttr, String paramCode, String paraCode1, String paraCode7, String eparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE7", paraCode7);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_PK_TD_S_COMMPARA7", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 通过SUBSYS_CODE、PARAM_ATTR、PARAM_CODE、PARA_CODE1、PARAM_CODE4、EPARCHY_CODE获取参数信息
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     * @author lizu
     */
    public static IDataset getCommparaInfoBy6(String subsysCode, String paramAttr, String paramCode, String paraCode1, String paraCode4, String eparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE4", paraCode4);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_CODE1ANDCODE4", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 查询营改增配置
     * 
     * @param subsysCode
     * @param paramAttr
     * @param paraCode
     * @param paraCode1
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaInfoByCode(String subsysCode, String paramAttr, String paraCode, String paraCode1, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paraCode);
        param.put("PARA_CODE1", paraCode1);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARA_CODE1_1", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getCommparaInfoByCode2(String subsysCode, String paramAttr, String paraCode, String paraCode2, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paraCode);
        param.put("PARA_CODE2", paraCode2);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_CODE2", param, Route.CONN_CRM_CEN);
    }

    /**
     * 通过SUBSYS_CODE、PARAM_ATTR、PARAM_CODE、PARA_CODE3、EPARCHY_CODE获取参数信息
     * 
     * @author chenzm
     * @param subsysCode
     * @param paramAttr
     * @param paraCode
     * @param paraCode3
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaInfoByCode3(String subsysCode, String paramAttr, String paraCode, String paraCode3, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paraCode);
        param.put("PARA_CODE3", paraCode3);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL2_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
    }
    
    /**
     * 通过SUBSYS_CODE、PARAM_ATTR、PARAM_CODE、PARA_CODE3、EPARCHY_CODE获取参数信息
     * 
     * @author chenzm
     * @param subsysCode
     * @param paramAttr
     * @param paraCode
     * @param paraCode3
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaInfoByCode3A(String subsysCode, String paramAttr, String paraCode, String paraCode3, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paraCode);
        param.put("PARA_CODE3", paraCode3);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL2A_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getCommparaInfoByCode45(String subsysCode, String paramAttr, String paramCode, int deviceCost) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("DEVICE_COST", deviceCost);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_FREE_FEE", param, Route.CONN_CRM_CEN);
    }

    /**
     * 通过SUBSYS_CODE、PARAM_ATTR、PARA_CODE1、PARAM_CODE4、EPARCHY_CODE获取参数信息
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     * @author sunxin
     */
    public static IDataset getCommparaInfoByForceDiscnt(String subsysCode, String paramAttr, String paraCode1, String paraCode4, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARAM_CODE4", paraCode4);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "ExistForceDiscntGroup", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据paracode4和param_attr查TD_S_COMMPARA
     * 
     * @author shixb
     * @version 创建时间
     */
    public static IDataset getCommparaInfoByParacode4AndAttr(String subsysCode, String paramAttr, String paraCode4, String eparchyCodes) throws Exception
    {
        IData inparams = new DataMap();

        inparams.put("SUBSYS_CODE", subsysCode);
        inparams.put("PARAM_ATTR", paramAttr);
        inparams.put("PARA_CODE4", paraCode4);
        inparams.put("EPARCHY_CODE", eparchyCodes);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARACODE4", inparams, Route.CONN_CRM_CEN);
    }

    public static IDataset getCommparaByCode4(String subsysCode, String paramAttr, String paramCode, String paraCode4, String eparchyCodes) throws Exception
    {
        IData inparams = new DataMap();

        inparams.put("SUBSYS_CODE", subsysCode);
        inparams.put("PARAM_ATTR", paramAttr);
        inparams.put("PARAM_CODE", paramCode);
        inparams.put("PARA_CODE4", paraCode4);
        inparams.put("EPARCHY_CODE", eparchyCodes);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAMCODE_PARACODE4", inparams, Route.CONN_CRM_CEN);
    }

    /**
     * 查询PARA_CODE21的参数配置
     * 
     * @author mengjh
     * @param inparams
     * @return 参数配置
     * @throws Exception
     */
    public static IDataset getCommparaInfoByParam21(String subsysCode, String paramAttr, String paraCode1, String paraCode21, String eparchyCodes) throws Exception
    {
        IData inparams = new DataMap();

        inparams.put("SUBSYS_CODE", subsysCode);
        inparams.put("PARAM_ATTR", paramAttr);
        inparams.put("PARA_CODE1", paraCode1);
        inparams.put("PARA_CODE21", paraCode21);
        inparams.put("EPARCHY_CODE", eparchyCodes);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTR_PARA_CODE21", inparams, Route.CONN_CRM_CEN);
    }

    public static IDataset getCommparaInfoIPSvc(String subsys_code, String param_attr, String param_code, String param_code1) throws Exception
    {
        IData bizCode = new DataMap();
        bizCode.put("SUBSYS_CODE", subsys_code);
        bizCode.put("PARAM_ATTR", param_attr);
        bizCode.put("PARAM_CODE", param_code);
        bizCode.put("PARAM_CODE1", param_code1);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_NEWPK_TD_S_COMMPARA", bizCode);
    }

    public static IDataset getCommparaInfos(String subsysCode, String paramAttr, String paramCode) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("SUBSYS_CODE", subsysCode);
        inparams.put("PARAM_ATTR", paramAttr);
        inparams.put("PARAM_CODE", paramCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_SUBATTRPARAMCODE", inparams, Route.CONN_CRM_CEN);
    }

    public static IDataset getCommparaInfosByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_USER_NO_HAVE_BIZ", param);
    }

    public static IDataset getCommparaInfosByUserId(String userId, String paramCode, String paraCode3) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PARAM_CODE", userId);
        param.put("PARA_CODE3", userId);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_USERDISCNT_HAVE_ANYNONE", param);
    }

    /**
     * @author chenzm
     * @param userId
     * @param paraCode1
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaProductInfoByCode(String userId, String paraCode1) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PARA_CODE1", paraCode1);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_DATA_BY_PRODUCT_ID", param);
    }

    public static IDataset getCommParas(String subsys_code, String param_attr, String param_code, String para_code1, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsys_code);
        param.put("PARAM_ATTR", param_attr);
        param.put("PARAM_CODE", param_code);
        param.put("PARA_CODE1", para_code1);
        param.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTR_PARAMCODE1", param, Route.CONN_CRM_CEN);

    }
    
    /**
     * @author yanwu 是否是4G优惠
     * @param subsys_code
     * @param param_code
     * @param para_code1
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static IDataset getCheck4GUserCommRule(String subsys_code, String param_code, String para_code1, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsys_code);
        //param.put("PARAM_ATTR", param_attr);
        param.put("PARAM_CODE", param_code);
        param.put("PARA_CODE1", para_code1);
        param.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_4G_CommRule", param, Route.CONN_CRM_CEN);

    }

    /**
     * @param subsysCode
     * @param paramAttr
     * @param paramCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommPkInfo(String subsysCode, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {

        IData data = new DataMap();
        data.put("SUBSYS_CODE", subsysCode);
        data.put("PARAM_ATTR", paramAttr);
        data.put("PARAM_CODE", paramCode);
        data.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getDiscntBindPlatSvc(String tradeTypeCode, String discntCode, String modifyTag, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("DISCNT_CODE", discntCode);
        data.put("MODIFY_TAG", modifyTag);
        data.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_DISCNT_CODE_NEW", data);
    }

    public static IDataset getEnableCommparaInfoByCode1(String subsysCode, String paramAttr, String paramCode, String paraCode1, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE1", paraCode1);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ENABLE_CODE1", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getEnableCommparaInfoByCode12(String subsysCode, String paramAttr, String paramCode, String paraCode1, String paraCode2, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE2", paraCode2);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ENABLE_CODE12", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getEnableCommparaInfoByCode12New(String subsysCode, String paramAttr, String paramCode, String paraCode1, String paraCode2, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE2", paraCode2);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ENABLE_CODE12_NEW", param, Route.CONN_CRM_CEN);
    }
    
    public static IDataset getInfoByAttrAndCode(String paramAttr, String paramCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PRODUCT_ID", param);
    }

    public static IDataset getInfoParaCode1_2(String subsysCode, String paramAttr, String paramCode1, String paramCode2) throws Exception
    {
        IData data = new DataMap();
        data.put("SUBSYS_CODE", subsysCode);
        data.put("PARAM_ATTR", paramAttr);
        data.put("PARA_CODE1", paramCode1);
        data.put("PARA_CODE2", paramCode2);
        data.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_PARA_CODE3", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getInfoParaCode3(String subsysCode, String paramAttr, String paramCode) throws Exception
    {
        IData data = new DataMap();
        data.put("SUBSYS_CODE", subsysCode);
        data.put("PARAM_ATTR", paramAttr);
        data.put("PARA_CODE3", paramCode);
        data.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARA_CODE3", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getNpHolidayInfos(String subsysCode, String paramAttr, String paramCode, String inDate) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SUBSYS_CODE", subsysCode);
        inparams.put("PARAM_ATTR", paramAttr);
        inparams.put("PARAM_CODE", paramCode);
        inparams.put("IN_DATE", inDate);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTR_PARAMCODES", inparams, Route.CONN_CRM_CEN);
    }

    public static IDataset getNpSend(String subsysCode, String paramAttr, String paramCode, String eparchy_code, Pagination page) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("SUBSYS_CODE", subsysCode);
        inparams.put("PARAM_ATTR", paramAttr);
        inparams.put("PARAM_CODE", paramCode);
        inparams.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_NPSEND", inparams, page, Route.CONN_CRM_CEN);
    }

    public static IDataset getOnlyByAttr(String subSysCode, String paramAttr, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subSysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ONLY_BY_ATTR_ORDERED", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getOtherDiscntInfo(String serviceId) throws Exception
    {
        IData param = new DataMap();

        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "2690");
        param.put("PARAM_CODE", serviceId);

        IDataset comminfos = Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_SERVICE_ID_FOR_WAP", param, Route.CONN_CRM_CEN);
        for(int i = 0 ; i < comminfos.size() ; i ++)
        {
        	IData comminfo = comminfos.getData(i);
        	String paraCode1 = comminfo.getString("PARA_CODE1");
        	
        	IData upcData = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_DISCNT, paraCode1, null);
        	comminfo.put("ELEMENT_DESC", upcData.getString("DESCRIPTION"));
        	comminfo.put("ELEMENT_ID", paraCode1);
        }
        return comminfos;
    }

    /**
     * 查账目参敄1�7
     * 
     * @author shixb
     * @version 创建时间＄1�709-5-29 下午11:04:26
     */
    public static IDataset getPayItemsParam(String subsysCode, String paramAttr, String paramCode, Pagination pagination) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("SUBSYS_CODE", subsysCode);
        inparams.put("PARAM_ATTR", paramAttr);
        inparams.put("PARAM_CODE", paramCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_PAY_ITEM", inparams, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset getPlatAttrs(String platSvcId, String productId, String packageId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", platSvcId);
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ACTIVE_PLAT_ATTR", param, Route.CONN_CRM_CEN);
    }

    /**
     * @param sn
     * @param productId
     * @return
     * @throws Exception
     * @CREATE BY sunxin@2014-6-10
     */
    public static IDataset getProductPhoneLimit(String serialNumber, String productId) throws Exception
    {

        IData param = new DataMap();
        param.put("PHONECODE_S", serialNumber);
        param.put("PRODUCT_ID", productId);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_PRODUCT_PHONE_LIMIT", param, Route.CONN_CRM_CEN);
    }

    /**
     * @param sn
     * @param productId
     * @return
     * @throws Exception
     * @CREATE BY sunxin@2014-6-10
     */
    public static IDataset getProductPhoneLimit1(String serialNumber, String productId) throws Exception
    {

        IData param = new DataMap();
        param.put("PHONECODE_S", serialNumber);
        param.put("PRODUCT_ID", productId);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_PRODUCT_PHONE_LIMIT_2", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getSmsTemplate(String tradeId, String paramCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("PARAM_CODE", paramCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_SYMF20_DISCNT_SMS", param,Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryCommParaByLike(String sys, String attr, String code) throws Exception
    {
        IData params = new DataMap();
        params.put("SUBSYS_CODE", sys);
        params.put("PARAM_ATTR", attr);
        params.put("PARAM_CODE", code);// 处理意见

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_SUBATTRPARAMCODE_LIKE", params, Route.CONN_CRM_CEN);
    }

    /**
     * 获取已领取刮刮卡数量
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset qryGotGGcardNum(String userID, String param_code, String para_code1) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userID);
        data.put("PRODUCT_ID", param_code);
        data.put("PACKAGE_ID", para_code1);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_SALEACTIVE_GOTGGCARD_NUM", data);
    }

    /**
     * 营销活动获取刮刮卡信息 xiaobin
     * 
     * @throws Exception
     */
    public static IDataset qrySaleactiveGGcard(String userID) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userID);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_SALEACTIVE_GGCARD_NUM", data);
    }

    public static IDataset queryByRuleId(String ruleId) throws Exception
    {
        IData data = new DataMap();
        data.put("EPARCHY_CODE", "0898");
        data.put("RULE_ID", ruleId);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_GROUP_EXCHANGE_RULE", data);
    }

    /**
     * 查询手机电视配置信息
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryCmmbConfig() throws Exception
    {
        IData param = new DataMap();
        param.put("PARAM_ATTR", "3705");
        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        param.put("SUBSYS_CODE", "CSM");
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAATTR", param);
    }

    public static IDataset queryCommInfos(String paramAttr, String paramCode, String paramCode1, String paramCode2) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("PARAM_ATTR", paramAttr);
        inparams.put("PARAM_CODE", paramCode);
        inparams.put("PARA_CODE1", paramCode1);
        inparams.put("PARA_CODE2", paramCode2);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_IDENT_AUTH_LIST", inparams, Route.CONN_CRM_CEN);
    }

    public static IDataset queryCommparaByCodeAndName(String paramCode, String paramName, String paraCode1, String subsysCode, String paramAttr) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("PARAM_CODE", paramCode);
        inparams.put("PARAM_NAME", paramName);
        inparams.put("PARA_CODE1", paraCode1);
        inparams.put("SUBSYS_CODE", subsysCode);
        inparams.put("PARAM_ATTR", paramAttr);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_NAME_PARACODE1", inparams);
    }

    /**
     * add yangsh6
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset queryCommparaByParaCode(IData params) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_SMS_SEARCH", params, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品、包判断是否是无返还类活动
     * 
     * @param productId
     * @param packageId
     * @return
     * @throws Exception
     */
    public static boolean queryCommparaByParaCode1ParaAttr(String productId, String packageId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        param.put("PARAM_ATTR", "155");
        param.put("EPARCHY_CODE", "0898");

        IDataset dataset = Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_PRODUCT_PACKAGE", param, Route.CONN_CRM_CEN);
        if (IDataUtil.isEmpty(dataset))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static IDataset queryDestroyReason(String type, String parentId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TYPE", type);
        param.put("PARENT_ID", parentId);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_FOR_TREE_BY_PARENT_ID", param, Route.CONN_CRM_CEN);
    }

    /**
     * @Function: querySaleActiveFeeConfig()
     * @Description: 查是否有营销活动信息
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-1 下午8:22:04 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-1 yxd v1.0.0 修改原因
     */
    public static IDataset querySaleActiveFeeConfig() throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SUBSYS_CODE", "CSM");
        inparams.put("PARAM_ATTR", "3800");
        inparams.put("PARAM_CODE", "STBFEE");
        inparams.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA8", inparams, Route.CONN_CRM_CEN);
    }

    public static IDataset queryScoreExchangePlat(String itemId) throws Exception
    {
        IData param = new DataMap();
        param.put("ITEM_ID", itemId);

        IDataset commparaList = Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_SCORE_SVC", param, Route.CONN_CRM_CEN);
        //老sql查询td_b_platsvc表有效记录，该参数配置中的service_id均为有效记录
        IDataset resultList = new DatasetList();
        for(int i = 0 ; i < commparaList.size() ; i ++)
        {
        	IData commpara = commparaList.getData(i);
        	String serviceId = commpara.getString("PARA_CODE2");
        	
        	IDataset upcDatas = new DatasetList();
        	try{
        		upcDatas = UpcCall.querySpServiceAndProdByCond(null, null, null, serviceId);
        	}catch(Exception e){
        		
        	}
        	
        	
        	for(int j = 0 ; j < upcDatas.size() ; j ++)
        	{
        		IData upcData = upcDatas.getData(j);
        		String bizStateCode = upcData.getString("BIZ_STATE_CODE");
        		
        		if(StringUtils.equals("A", bizStateCode))
        		{
        			IData tempMap = new DataMap();
        			upcData.put("SERVICE_ID", serviceId);
        			tempMap.putAll(commpara);
        			tempMap.putAll(upcData);
        			
        			resultList.add(tempMap);
        		}
        	}
        }
        
        return resultList;
    }

    public static IDataset querySubscribeSvc(String paramCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PARAM_CODE", paramCode);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_SUBSCRIBE_SVC_PARAM", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryUserAvgPayFee(String userId, String monthNum) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("MONTHS_NUM", monthNum);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_USER_AVGPAYFEE_NG", param);
    }
    
    /**
     * 获取是否存在有效期PARA_CODE26---PARA_CODE27内PARAM_CODE为333配置的PARAM_VALUE(RULE_ID)记录,用作REQ201409230011需求的时间限定功能
     * 
     * @throws Exception
     */
    public static IDataset get333ComparaByCodeAndValue(String SUBSYS_CODE, String PARAM_ATTR, String PARAM_CODE, String EPARCHY_CODE) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("SUBSYS_CODE", SUBSYS_CODE);
        inparams.put("PARAM_ATTR", PARAM_ATTR);
        inparams.put("PARAM_CODE", PARAM_CODE);
        inparams.put("EPARCHY_CODE", EPARCHY_CODE);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_333COMPARA_BY_CODEANDVALUE", inparams, Route.CONN_CRM_CEN);
    }
    

    public static IDataset getDefaultOpenSvcId(String subsys_code,String param_attr,String param_code1) throws Exception{
    	IData param = new DataMap();
    	param.put("SUBSYS_CODE", subsys_code);
        param.put("PARAM_ATTR", param_attr);
        param.put("PARA_CODE1", param_code1);
    	return Dao.qryByCode("TD_S_COMMPARA", "SEL_DEFAULTOPENSVC", param);    	
    }
    
    public static IDataset getCommparaByCode2(String subsysCode, String paramAttr, String paraCode, String paraCode2, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE2", paraCode2);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_CODE_PARACODE2", param, null, Route.CONN_CRM_CEN);
    }
    
    public static IDataset getCommparaInfosByTradeTypeCode(String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PARAM_CODE", tradeTypeCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_TRADECLASS_BY_TRADETYPECODE", param);
    }
     //互联网电视bug新增---   
    public static IDataset getTradeDiscntbyDiscntcode(String subsysCode, String paramAttr, String paramCode) throws Exception
    {
        IData data = new DataMap();
        data.put("SUBSYS_CODE", subsysCode);
        data.put("PARAM_ATTR", paramAttr);
        data.put("PARA_CODE2", paramCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_TRADEDISCNTBYDISCNTCODE", data, Route.CONN_CRM_CEN);
    }


    public static IDataset getCommparaByParamcode(String strSubsysCode, String strParamCode, String strParaCode1, String strParaCode2) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("PARAM_CODE", strParamCode);
        param.put("PARA_CODE1", strParaCode1);
        param.put("PARA_CODE2", strParaCode2);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_PARAMCODE_AND_PARA_CODE1_2", param, Route.CONN_CRM_CEN);
    }
    

    //REQ201506020003 假日流量套餐开发需求 songlm 20150612
    public static IDataset getCommparaIsInActiveDate(String subsys_code, String param_attr, String param_code) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsys_code);
        param.put("PARAM_ATTR", param_attr);
        param.put("PARAM_CODE", param_code);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_PARAMCODE_ACTIVEDATE", param, Route.CONN_CRM_CEN);
    }
    
    
    public static IDataset queryCommparaInfoByParaCode2(String subsys_code, String param_attr, String para_code2) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsys_code);
        param.put("PARAM_ATTR", param_attr);
        param.put("PARA_CODE2", para_code2);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_DISCNT", param, Route.CONN_CRM_CEN);
    }
	
     public static IDataset queryComparaByAttrAndCode1(String subsysCode,String paramAttr,String paramCode,String paraCode1)
    	throws Exception{
    	IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARAM_CODE", paramCode);

        return Dao.qryByCodeParser("TD_S_COMMPARA", "QRY_COMPARAM_BY_PARA_CODE1", param, Route.CONN_CRM_CEN);
      }
     
     public static IDataset queryComparaByAttrAndCode1NEW(String subsysCode,String paramAttr,String paramCode,String paraCode1,String paraCode2)
    	throws Exception{
    	IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE2", paraCode2);

        return Dao.qryByCodeParser("TD_S_COMMPARA", "QRY_COMPARAM_BY_PARA_CODE2", param, Route.CONN_CRM_CEN);
      }
     
     public static IDataset getEnableCommparaInfoByCode19(String subsysCode, String paramAttr, String paramCode, String paraCode1, String paraCode9) throws Exception
     {
         IData param = new DataMap();
         param.put("SUBSYS_CODE", subsysCode);
         param.put("PARAM_ATTR", paramAttr);
         param.put("PARAM_CODE", paramCode);
         param.put("PARA_CODE1", paraCode1);
         param.put("PARA_CODE9", paraCode9);
         return Dao.qryByCode("TD_S_COMMPARA", "SEL_ENABLE_CODE_1_9", param, Route.CONN_CRM_CEN);
     }
     
     public static IDataset queryValidHolidayDiscnt()throws Exception{
    	 IData param = new DataMap();
    	 return Dao.qryByCode("TD_S_COMMPARA", "QRY_VALID_HOLIDAY_DISCNT", param, Route.CONN_CRM_CEN);
    	 
    }
    
    public static IDataset queryValidHolidayDiscntByDiscntId(String discntCode)throws Exception{
   	 IData param = new DataMap();
   	 param.put("DISCNT_CODE", discntCode);
   	 return Dao.qryByCode("TD_S_COMMPARA", "QRY_VALID_HOLIDAY_DISCNT", param, Route.CONN_CRM_CEN);
   	 
   }
   
   public static IDataset queryUnExpireMaxHolidayDiscnt()throws Exception{
	   IData param=new DataMap();
	   return Dao.qryByCode("TD_S_COMMPARA", "QRY_UNEXPIRE_LATEST_DISCNT", param, Route.CONN_CRM_CEN);
   }
   
   /**
    * 通过SUBSYS_CODE、PARAM_ATTR、PARAM_CODE、PARA_CODE1、EPARCHY_CODE获取参数信息
    * 
    * @param inparams
    * @return
    * @throws Exception
    * @author songxw
    */
   public static IDataset getCommparaInfoBy16(String subsysCode, String paramAttr, String paramCode, String paraCode1, String paraCode6, String eparchyCode) throws Exception
   {
       IData param = new DataMap();
       param.put("SUBSYS_CODE", subsysCode);
       param.put("PARAM_ATTR", paramAttr);
       param.put("PARA_CODE1", paraCode1);
       param.put("PARA_CODE6", paraCode6);
       param.put("PARAM_CODE", paramCode);
       param.put("EPARCHY_CODE", eparchyCode);
       return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAATTR_CODE16", param, Route.CONN_CRM_CEN);
   }
   
   public static IDataset queryCommparaSmsTemplate(String paramAttr, String paramCode)
	   		throws Exception{
	   IData param=new DataMap();
	   param.put("PARAM_ATTR", paramAttr);
	   param.put("PARAM_CODE", paramCode);
	   
	   return Dao.qryByCode("TD_S_COMMPARA", "QRY_COMMPARA_TEMPLATE_CONTENT", param, Route.CONN_CRM_CEN);
	   
   }
   public static IDataset getCodeCodeInfos(String tabName, String sqlRef, String paramCode1, String paramCode2, String tradeId) throws Exception
   {

       IData inparams = new DataMap();
       if(!"".equals(paramCode1))
       {
    	   inparams.put("PARAM_CODE1", paramCode1);
       }
       if(!"".equals(paramCode2))
       {
    	   inparams.put("PARAM_CODE2", paramCode2);
       }
       
       inparams.put("TRADE_ID", tradeId);
       if(!StringUtils.isEmpty(tabName))
       {
    	   if(tabName.startsWith("TF_B"))
    	   {
    		   return Dao.qryByCodeParser(tabName, sqlRef, inparams,Route.getJourDb(CSBizBean.getTradeEparchyCode())); 
    	   }
       }
       return Dao.qryByCodeParser(tabName, sqlRef, inparams);
   }
   
   /**
    * 通过SUBSYS_CODE、PARAM_ATTR、PARAM_CODE、PARA_CODE1、EPARCHY_CODE获取参数信息
    * 
    * @param inparams
    * @param pagination
    * @return
    * @throws Exception
    * @author xuyt
    */
   public static IDataset getCommparaInfoBy16(String subsysCode, String paramAttr, String paramCode, String paraCode6, String eparchyCode) throws Exception
   {
       IData param = new DataMap();
       param.put("SUBSYS_CODE", subsysCode);
       param.put("PARAM_ATTR", paramAttr);
       param.put("PARA_CODE6", paraCode6);
       param.put("PARAM_CODE", paramCode);
       param.put("EPARCHY_CODE", eparchyCode);
       return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAATTRCODE16", param, Route.CONN_CRM_CEN);
   }
   
   public static IDataset getCommparaInfoByCode5(String subsysCode, String paramAttr, String paraCode, String paraCode1, String paraCode5, String paraCode11, String eparchyCode) throws Exception
   {
       IData param = new DataMap();
       param.put("SUBSYS_CODE", subsysCode);
       param.put("PARAM_ATTR", paramAttr);
       param.put("PARAM_CODE", paraCode);
       param.put("PARA_CODE5", paraCode5);
       param.put("PARA_CODE1", paraCode1);
       param.put("PARA_CODE11", paraCode11);
       param.put("EPARCHY_CODE", eparchyCode);
       return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_CODE5", param, Route.CONN_CRM_CEN);
   }
   
   public static IDataset getCommparaInfoByAll(IData data) throws Exception
   {
       return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_ALLP", data, Route.CONN_CRM_CEN);
   }
   
   public static IDataset getCommparaInfoBy1To6(IData data) throws Exception
   {
       return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_ALLOS", data, Route.CONN_CRM_CEN);
   }
   
   public static IDataset get1234By5000(IData data) throws Exception
   {
       return Dao.qryByCode("TD_S_COMMPARA", "SEL_PARAM1234_BY_5000", data, Route.CONN_CRM_CEN);
   }

   /**
    * add by duhj
    * 2017/03/21
    * 查询是否订制飞信业务
    * @return
    * @throws Exception
    */
   public static IDataset queryUserFetion() throws Exception
   {
       IData param = new DataMap();
       return Dao.qryByCode("TD_S_COMMPARA", "SEL_USER_FETION", param);
   }
   
   /**
    * add by duhj
    * @return
    * @throws Exception
    */
   public static IDataset queryParacode2() throws Exception
   {
       IData param = new DataMap();
       return Dao.qryByCode("TD_S_COMMPARA", "SEL_USER_PARA_CODE2", param);
   }
   
   public static boolean isTradeMustPrint(String tradeTypeCode)throws Exception
   {
	   return IDataUtil.isNotEmpty(CommparaInfoQry.getCommparaAllCol("CSM","7890",tradeTypeCode,"0898"));
   }
   
   public static IDataset getCommparaInfoBy1To7(IData data) throws Exception
   {
       return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL1T7_PK_TD_S_COMMPARA", data, Route.CONN_CRM_CEN);
   }
   
   public static IDataset getCommparaInfoBy1To7(String sysCode, String paramAttr, String paramCode, String paraCode2, String paraCode3) throws Exception
   {
	   IData data = new DataMap();
	   data.put("SUBSYS_CODE", sysCode);
	   data.put("PARAM_ATTR", paramAttr);
	   data.put("PARAM_CODE", paramCode);
	   data.put("PARA_CODE2", paraCode2);
	   data.put("PARA_CODE3", paraCode3);
		
       return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL1T7_PK_TD_S_COMMPARA", data, Route.CONN_CRM_CEN);
   }
   
   /**
    * REQ201805160025_2018年海洋通业务办理开发需求
    * <br/>
    * @author  zhuoyingzhi
    * @date 20180611
    * @param subsysCode
    * @param paramAttr
    * @param paraCode2
    * @param paraCode3
    * @param eparchyCode
    * @return
    * @throws Exception
    */
   public static IDataset getCommparaInfoByattrAndCode2_3(String subsysCode, String paramAttr, String paraCode2, String paraCode3, String eparchyCode) throws Exception
   {
       IData param = new DataMap();
       param.put("SUBSYS_CODE", subsysCode);
       param.put("PARAM_ATTR", paramAttr);
       param.put("PARA_CODE2", paraCode2);//船员套餐 标识   1：船主     0：船员
       param.put("PARA_CODE3", paraCode3);
       param.put("EPARCHY_CODE", eparchyCode);
       return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL2_3_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
   }

/**
 * @Description：查询大于paraCode1的配置
 * @param:@param param
 * @param:@return
 * @return IDataset
 * @throws Exception 
 * @throws
 * @Author :tanzheng
 * @date :2018-7-23下午04:06:24
 */
public static IDataset getMoreThanParaCode1(IData param) throws Exception {
	return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_MORETHEN_PARACODE1", param, Route.CONN_CRM_CEN);
}

/**
 * @Description：
 * @param string 
 * @param:@param string
 * @param:@param string2
 * @param:@param elementId
 * @param:@param string3
 * @param:@return
 * @return IDataset
 * @throws Exception 
 * @throws
 * @Author :tanzheng
 * @date :2018-8-6下午03:01:09
 */
public static IDataset getContainCode1(String subsysCode, String paramAttr,String paramCode,
		String elementId,String paraCode2, String eparchyCode) throws Exception {
	IData param = new DataMap();
	param.put("SUBSYS_CODE", subsysCode);
	param.put("PARAM_ATTR", paramAttr);
	param.put("PARAM_CODE", paramCode);
	param.put("PARA_CODE1", elementId);
	param.put("PARA_CODE2", paraCode2);
	param.put("EPARCHY_CODE", eparchyCode);
	return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_CONTAINS_PARA1", param, Route.CONN_CRM_CEN);
}

/**
 * @Description：
 * @param:@param string
 * @param:@param string2
 * @param:@param psptTypeCode
 * @param:@param discntCode
 * @param:@param string3
 * @param:@return
 * @return IDataset
 * @throws Exception 
 * @throws
 * @Author :tanzheng
 * @date :2018-8-9上午10:54:47
 */
public static IDataset getContainCode3(String subsysCode, String paramAttr,
		String psptTypeCode, String discntCode, String eparchyCode) throws Exception {
	IData param = new DataMap();
	param.put("SUBSYS_CODE", subsysCode);
	param.put("PARAM_ATTR", paramAttr);
	param.put("PARA_CODE1", psptTypeCode);
	param.put("PARA_CODE3", discntCode);
	param.put("EPARCHY_CODE", eparchyCode);
	return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_CONTAINS_PARA1AND3", param, Route.CONN_CRM_CEN);
}  


	/**
	 *  查询是否包年数据
	 * @param subsysCode
	 * @param paramAttr
	 * @param paraCode
	 * @param paraCode1
	 * @param paraCode5
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 * by xuzh5 
	 */
   public static IDataset QueryIsBaoNian(String subsysCode, String paramAttr, String paraCode, String paraCode1 ,String paraCode5, String eparchyCode) throws Exception
   {
       IData param = new DataMap();
       param.put("SUBSYS_CODE", subsysCode);
       param.put("PARAM_ATTR", paramAttr);
       param.put("PARAM_CODE", paraCode);
       param.put("PARA_CODE1", paraCode1);
       param.put("PARA_CODE5", paraCode5);
       param.put("EPARCHY_CODE", eparchyCode);
       return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_IsBaoNian", param, Route.CONN_CRM_CEN);
   }
   public static IDataset getCommparaByParaAttrFeeCode(String subSysCode, String paramAttr, String eparchyCode , String feecode) throws Exception
   {
       IData data = new DataMap();
       data.put("SUBSYS_CODE", subSysCode);
       data.put("PARAM_ATTR", paramAttr);
       data.put("FEE_CODE_FEE", feecode);
       data.put("EPARCHY_CODE", eparchyCode);

       return Dao.qryByCode("TD_S_COMMPARA", "SEL_COMMPARA_BY_PARAATTR_FEECODE", data, Route.CONN_CRM_CEN);
   }
   
   /**
    * 根据paramAttr,paraCode4,paraCode5,paraCode6的条件查询commpara表
    * 
    * @param subsysCode
    * @param paramAttr
    * @param paraCode4
    * @param paraCode5
    * @param paraCode6
    * @param eparchyCode
    * @return
    * @throws Exception
    */
   public static IDataset getCommparaByCode4to6(String subsysCode, String paramAttr, String paramCode, String paraCode4, String paraCode5, String paraCode6, String eparchyCode) throws Exception
   {
       IData param = new DataMap();
       param.put("SUBSYS_CODE", subsysCode);
       param.put("PARAM_ATTR", paramAttr);
       param.put("PARAM_CODE", paramCode);
       param.put("PARA_CODE4", paraCode4);
       param.put("PARA_CODE5", paraCode5);
       param.put("PARA_CODE6", paraCode6);
       param.put("EPARCHY_CODE", eparchyCode);

       return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_PARA_CODE4TO6", param, Route.CONN_CRM_CEN);
   }
   
   
   //merch_duhj
   public static IDataset getCommparaAllColByCond(String string, String string2, String string3, String string4, String userEparchyCode)
   {
       // TODO Auto-generated method stub
       return null;
   }

   //merch_duhj
   public static IDataset getCommparaInfoByCode12(String string, String string2, String string3, String packageId, String inModeCode, String tradeEparchyCode)
   {
       // TODO Auto-generated method stub
       return null;
   }

    public static IDataset getcommparaByAttrCode1Code20(String subsysCode, String paramAttr, String paraCode1, String paraCode20, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE20", paraCode20);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_PARAM_ATTR_CODE1_CODE20", param, Route.CONN_CRM_CEN);
    }

    /**
	 * @description 根据入参查询TD_s_commpara 表，目前只支持等于查询.
	 * 注意参数TIME_LIMIT 是否需要时间限制，默认为需要，不需要则传入不为0的参数
	 * @param @param param 
	 * @param @return
	 * @return IDataset
	 * @author tanzheng
	 * @date 2019年5月10日
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	public static IDataset getCommparaInfoByPara(IData param) throws Exception {
		// TODO Auto-generated method stub
		SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  " );
        parser.addSQL(COLOUMS);
        parser.addSQL(" FROM TD_S_COMMPARA ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND SUBSYS_CODE=:SUBSYS_CODE");
        parser.addSQL(" AND PARAM_ATTR=:PARAM_ATTR");
        parser.addSQL(" AND PARAM_CODE=:PARAM_CODE");
        parser.addSQL(" AND PARAM_NAME=:PARAM_NAME");
        parser.addSQL(" AND PARA_CODE1=:PARA_CODE1");
        parser.addSQL(" AND PARA_CODE2=:PARA_CODE2");
        parser.addSQL(" AND PARA_CODE3=:PARA_CODE3");
        parser.addSQL(" AND PARA_CODE4=:PARA_CODE4");
        parser.addSQL(" AND PARA_CODE5=:PARA_CODE5");
        parser.addSQL(" AND PARA_CODE6=:PARA_CODE6");
        parser.addSQL(" AND PARA_CODE7=:PARA_CODE7");
        parser.addSQL(" AND PARA_CODE8=:PARA_CODE8");
        parser.addSQL(" AND PARA_CODE9=:PARA_CODE9");
        parser.addSQL(" AND PARA_CODE10=:PARA_CODE10");
        parser.addSQL(" AND PARA_CODE11=:PARA_CODE11");
        parser.addSQL(" AND PARA_CODE12=:PARA_CODE12");
        parser.addSQL(" AND PARA_CODE13=:PARA_CODE13");
        parser.addSQL(" AND PARA_CODE14=:PARA_CODE14");
        parser.addSQL(" AND PARA_CODE15=:PARA_CODE15");
        parser.addSQL(" AND PARA_CODE16=:PARA_CODE16");
        parser.addSQL(" AND PARA_CODE17=:PARA_CODE17");
        parser.addSQL(" AND PARA_CODE18=:PARA_CODE18");
        parser.addSQL(" AND PARA_CODE19=:PARA_CODE19");
        parser.addSQL(" AND PARA_CODE20=:PARA_CODE20");
        parser.addSQL(" AND PARA_CODE21=:PARA_CODE21");
        parser.addSQL(" AND PARA_CODE22=:PARA_CODE22");
        parser.addSQL(" AND PARA_CODE23=:PARA_CODE23");
        parser.addSQL(" AND PARA_CODE24=:PARA_CODE24");
        parser.addSQL(" AND PARA_CODE25=:PARA_CODE25");
        parser.addSQL(" AND PARA_CODE26=:PARA_CODE26");
        parser.addSQL(" AND PARA_CODE27=:PARA_CODE27");
        parser.addSQL(" AND PARA_CODE28=:PARA_CODE28");
        parser.addSQL(" AND PARA_CODE29=:PARA_CODE29");
        parser.addSQL(" AND PARA_CODE30=:PARA_CODE30");
        
        parser.addSQL(" AND EPARCHY_CODE=:EPARCHY_CODE");
        parser.addSQL(" AND UPDATE_STAFF_ID=:UPDATE_STAFF_ID");
        parser.addSQL(" AND UPDATE_DEPART_ID=:UPDATE_DEPART_ID");
        parser.addSQL(" AND UPDATE_TIME=:UPDATE_TIME");
        parser.addSQL(" AND REMARK=:REMARK");
        if("0".equals(param.getString("TIME_LIMIT","0"))){
        	parser.addSQL(" AND SYSDATE BETWEEN START_DATE AND END_DATE");
        }
        
        
        
        IDataset dataset = Dao.qryByParse(parser); 
		
		return dataset;
	}

	public static IDataset qryInterRoamPackageByParaCode6(String subsysCode,String paramAttr,String paraCode6) throws Exception{
    	IData param = new DataMap();
    	
    	param.put("SUBSYS_CODE", subsysCode);
    	param.put("PARAM_ATTR", paramAttr);
    	param.put("PARA_CODE6", paraCode6);
    	
    	return Dao.qryByCode("TD_S_COMMPARA", "SEL_COMMPARA_BY_PARACODE6", param,Route.CONN_CRM_CEN);
    }
	
}
