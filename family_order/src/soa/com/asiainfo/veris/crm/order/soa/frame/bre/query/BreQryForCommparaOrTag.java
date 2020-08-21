
package com.asiainfo.veris.crm.order.soa.frame.bre.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;

/**
 * 参数查询
 * 
 * @author Administrator
 */
public class BreQryForCommparaOrTag extends BreBase
{

    /**
     * 查询commpara 表配置
     * 
     * @param strSubsysCode
     * @param iParamAttr
     * @param strParamCode
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommpara(String strSubsysCode, int iParamAttr, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("PARAM_ATTR", iParamAttr);
        param.put("EPARCHY_CODE", strEparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAATTR1", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询commpara 表配置
     * 
     * @param strSubsysCode
     * @param iParamAttr
     * @param strParamCode
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommpara(String strSubsysCode, int iParamAttr, String strParamCode, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("PARAM_ATTR", iParamAttr);
        param.put("PARAM_CODE", strParamCode);
        param.put("EPARCHY_CODE", strEparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询commpara 表配置
     * 
     * @param strSubsysCode
     * @param iParamAttr
     * @param strParamCode
     * @param strParaCode1
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommpara(String strSubsysCode, int iParamAttr, String strParamCode, String strParaCode1, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("PARAM_ATTR", iParamAttr);
        param.put("PARAM_CODE", strParamCode);
        param.put("PARA_CODE1", strParaCode1);
        param.put("EPARCHY_CODE", strEparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_NEWPK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询commpara 表配置
     * 
     * @param strSubsysCode
     * @param iParamAttr
     * @param strParamCode
     * @param strParaCode1
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommpara(String strSubsysCode, int iParamAttr, String strParamCode, String strParaCode1, String strParaCode2, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("PARAM_ATTR", iParamAttr);
        param.put("PARAM_CODE", strParamCode);
        param.put("PARA_CODE1", strParaCode1);
        param.put("PARA_CODE2", strParaCode2);
        param.put("EPARCHY_CODE", strEparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_INFO12_TD_S_COMMPARA_CODE", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询commpara 表配置
     * 
     * @param strSubsysCode
     * @param iParamAttr
     * @param strParamCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaBy3P(String strSubsysCode, String iParamAttr, String strParamCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SYBSYS_CODE", strSubsysCode);
        param.put("PARAM_ATTR", iParamAttr);
        param.put("PARAM_CODE", strParamCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTR_PARAM_CODE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getCommparaCode(String strSubsysCode, int iParamAttr, String strParamCode1, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("PARAM_ATTR", iParamAttr);
        param.put("PARA_CODE1", strParamCode1);
        param.put("EPARCHY_CODE", strEparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAATTR_CODE1", param, Route.CONN_CRM_CEN);
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
    public static IDataset getCommparaCode1(String strSubsysCode, int iParamAttr, String strParamCode, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("PARAM_ATTR", iParamAttr);
        param.put("PARAM_CODE", strParamCode);
        param.put("EPARCHY_CODE", strEparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ONLY_CODE1", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询commpara 表配置 仅查询PARA_CODE3
     * 
     * @param strSubsysCode
     * @param iParamAttr
     * @param strParamCode
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaPara3(String strSubsysCode, int iParamAttr, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("PARAM_ATTR", iParamAttr);
        param.put("EPARCHY_CODE", strEparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ONLY_CODE3_BY_PARAATTR1", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询td_s_tag表信息
     * 
     * @param strEparchyCode
     * @param strTagCode
     * @param strSubsysCode
     * @param strUseTag
     * @return
     * @throws Exception
     */
    public static IDataset getTagByTagCode(String strEparchyCode, String strTagCode, String strSubsysCode, String strUseTag) throws Exception
    {
        IData param = new DataMap();

        param.put("EPARCHY_CODE", strEparchyCode);
        param.put("TAG_CODE", strTagCode);
        param.put("SUBSYS_CODE", strSubsysCode);
        param.put("USE_TAG", strUseTag);

        return Dao.qryByCode("TD_S_TAG", "SEL_ALL_BY_TAGCODE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryCompara1010Info() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara131Info(String strForceTag) throws Exception
    {
        IData param = new DataMap();
        param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PK", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara135Info(String strForceTag) throws Exception
    {
        IData param = new DataMap();
        param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PK", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara151Info() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara155158InfoByParaCode() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara158Info() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara164Info() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_ATTR_BIZ", "SEL_BY_TRADE_TYPE_CODE1", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara2001Info(String strForceTag) throws Exception
    {
        IData param = new DataMap();
        param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PK", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara243Info() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PK", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara355Info(String strForceTag) throws Exception
    {
        IData param = new DataMap();
        param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PK", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara603Info() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara80Info() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara8859Info(String strForceTag) throws Exception
    {
        IData param = new DataMap();
        param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PK", param, "cen");// td_s_commpara

    }

    public static IDataset queryCompara9987Info() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset QueryDefProductPhoneBySn() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_DEFPRODUCT_PHONE", "SEL_BY_PHONE", param, "cen");// td_s_commpara

    }

    public static IDataset queryDiscntTpye(String discntTpyeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("DISCNT_TYPE_CODE", discntTpyeCode);
        return Dao.qryByCode("TD_B_DTYPE_DISCNT", "SEL_BY_PK", param, "cen");

    }

    public static IDataset queryElementLimit() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PK", param, "cen");// td_s_commpara

    }

    public static IDataset queryElementLimitNpInfo() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_ELEMENT_LIMIT_NP", "SEL_BY_PK", param, "cen");// td_s_commpara

    }

    // //查询是否集团业务
    public static IDataset queryIsGroupTradeType() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_ATTR_BIZ", "SEL_BY_TRADE_TYPE_CODE1", param, "cen");// td_s_commpara

    }

    public static IDataset queryPackageElementByeId(String strForceTag) throws Exception
    {
        IData param = new DataMap();
        param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("td_b_package_element", "SEL_BY_PK", param, "cen");

    }

    public static IDataset queryPackageExe() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset queryPackageExtInfo() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset queryPreTradeSaleactive() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset queryProdDiscntMember() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_PROD_DISCNT_MEMBER", "SEL_BY_PK", param, "cen");// td_s_commpara

    }

    public static IDataset queryProduct() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_PROD_DISCNT_MEMBER", "SEL_BY_PK", param, "cen");// td_s_commpara

    }

    public static IDataset queryProductInfo() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset queryProductPackage(String discntTpyeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("DISCNT_TYPE_CODE", discntTpyeCode);
        return Dao.qryByCode("TD_B_DTYPE_DISCNT", "SEL_BY_PK", param, "cen");

    }

    public static IDataset queryProductPackageBypId(String strProductId, String strForceTag) throws Exception
    {
        IData param = new DataMap();
        param.put("FORCE_TAG", strForceTag);
        param.put("PRODUCT_ID", strProductId);
        return Dao.qryByCode("td_b_product_package", "SEL_BY_PK", param, "cen");

    }

    public static IDataset queryProductPackageByProductPackage() throws Exception
    {
        IData param = new DataMap();

        return Dao.qryByCode("TD_B_DTYPE_DISCNT", "SEL_BY_PK", param, "cen");

    }

    public static IDataset querySelByLimitG005() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset querySelCheckUserPrint() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset queryTagInfo() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset QueryTdBcopara() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_CPARAM", "ExistsPurchaseRB", param, "cen");// td_s_commpara

    }

    public static IDataset TradeBookByUserId() throws Exception
    {
        IData param = new DataMap();
        // param.put("FORECE_TAG", strForceTag);
        return Dao.qryByCode("TD_B_ATTR_BIZ", "SEL_BY_TRADE_TYPE_CODE1", param, "cen");// td_s_commpara

    }
}
