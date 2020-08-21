
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.commparainfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class CommparaInfoIntf
{

    /**
     * 通过PARAM_ATTR、EPARCHY_CODE查询Commpara参数配置表
     * 
     * @param bc
     * @param subSysCode
     * @param paramAttr
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryCommParasByParamAttrAndEparchyCode(IBizCommon bc, String subSysCode, String paramAttr, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SUBSYS_CODE", subSysCode);
        inparam.put("PARAM_ATTR", paramAttr);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return CSViewCall.call(bc, "CS.CommparaInfoQrySVC.getCommparaInfoByAttr", inparam);
    }

    /**
     * 通过参数subsyscoe,paramattr,attrcode查询Commpara参数配置表
     * 
     * @param bc
     * @param subSysCode
     * @param paramAttr
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IDataset qryCommParasByParamAttrAndParamCode(IBizCommon bc, String subSysCode, String paramAttr, String paramCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SUBSYS_CODE", subSysCode);
        inparam.put("PARAM_ATTR", paramAttr);
        inparam.put("PARAM_CODE", paramCode);
        return CSViewCall.call(bc, "CS.CommparaInfoQrySVC.getCommparaInfoByBizCode", inparam);
    }

    /**
     * 通过PARAM_ATTR、PARACODE4、EPARCHYCODE查询参数COMMPARA表
     * 
     * @param bc
     * @param subSysCode
     * @param paramAttr
     * @param paramCode4
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryCommParasByParamAttrAndParamCode4EparchyCode(IBizCommon bc, String subSysCode, String paramAttr, String paramCode4, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SUBSYS_CODE", subSysCode);
        inparam.put("PARAM_ATTR", paramAttr);
        inparam.put("PARA_CODE4", paramCode4);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return CSViewCall.call(bc, "CS.CommparaInfoQrySVC.getCommparaInfoByParacode4AndAttr", inparam);
    }

    /**
     * 通过参数subsyscoe,paramattr,attrcode,eparchyCode查询Commpara参数配置表
     * 
     * @param bc
     * @param subSysCode
     * @param paramAttr
     * @param attrCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryCommParasByParamAttrAndParamCodeEparchyCode(IBizCommon bc, String subSysCode, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SUBSYS_CODE", subSysCode);
        inparam.put("PARAM_ATTR", paramAttr);
        inparam.put("PARAM_CODE", paramCode);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return CSViewCall.call(bc, "CS.CommparaInfoQrySVC.getCommpara", inparam);
    }

    /**
     * 通过PARAM_ATTR、PARAM_CODE、PARA_CODE1、PARA_CODE6、EPARCHY_CODE查询参数COMMPARA表
     * 
     * @param bc
     * @param subSysCode
     * @param paramAttr
     * @param paramCode
     * @param paraCode1
     * @param paraCode6
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryCommParasByParamAttrAndParamCodeParaCode1ParaCode6EparchyCode(IBizCommon bc, String subSysCode, String paramAttr, String paramCode, String paraCode1, String paraCode6, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SUBSYS_CODE", subSysCode);
        inparam.put("PARAM_ATTR", paramAttr);
        inparam.put("PARAM_CODE", paramCode);
        inparam.put("PARA_CODE1", paraCode1);
        inparam.put("PARA_CODE6", paraCode6);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return CSViewCall.call(bc, "CS.CommparaInfoQrySVC.getCommparaInfoBy16", inparam);
    }

    /**
     * 通过PARAM_ATTR、PARAM_CODE、PARAM_CODE1、EPARCHY_CODE查询参数COMMPARA表
     * 
     * @param bc
     * @param subSysCode
     * @param paramAttr
     * @param paramCode
     * @param paramCode1
     * @param eparchyCode
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataOutput qryCommParasByParamAttrAndParamCodeParamCode1EparchyCodePagination(IBizCommon bc, String subSysCode, String paramAttr, String paramCode, String paramCode1, String eparchyCode, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SUBSYS_CODE", subSysCode);
        inparam.put("PARAM_ATTR", paramAttr);
        inparam.put("PARAM_CODE", paramCode);
        inparam.put("PARA_CODE1", paramCode1);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return CSViewCall.callPage(bc, "CS.CommparaInfoQrySVC.getCommparaInfoBy5", inparam, pagination);
    }

    /**
     * 集团付费计划中的付费账目
     * 
     * @param bc
     * @param subSysCode
     * @param paramAttr
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IDataset qryPayItemsParamBySubCodeParamAttrAndAttrCode(IBizCommon bc, String subSysCode, String paramAttr, String paramCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SUBSYS_CODE", subSysCode);
        inparam.put("PARAM_ATTR", paramAttr);
        inparam.put("PARAM_CODE", paramCode);
        return CSViewCall.call(bc, "CS.CommparaInfoQrySVC.getPayItemsParam", inparam);
    }
}
