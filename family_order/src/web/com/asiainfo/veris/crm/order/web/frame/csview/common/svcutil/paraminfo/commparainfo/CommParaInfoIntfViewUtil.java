
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.commparainfo.CommparaInfoIntf;

public class CommParaInfoIntfViewUtil
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
        return CommparaInfoIntf.qryCommParasByParamAttrAndEparchyCode(bc, subSysCode, paramAttr, eparchyCode);
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
        IDataset infosDataset = CommparaInfoIntf.qryCommParasByParamAttrAndParamCode(bc, subSysCode, paramAttr, paramCode);
        return infosDataset;
    }

    /**
     * 通过PARAM_ATTR、PARACODE4、EPARCHYCODE查询参数COMMPARA表
     * 
     * @param bc
     * @param subSysCode
     * @param paramAttr
     * @param paraCode4
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryCommParasByParamAttrAndParamCode4EparchyCode(IBizCommon bc, String subSysCode, String paramAttr, String paraCode4, String eparchyCode) throws Exception
    {
        return CommparaInfoIntf.qryCommParasByParamAttrAndParamCode4EparchyCode(bc, subSysCode, paramAttr, paraCode4, eparchyCode);

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
        IDataset infosDataset = CommparaInfoIntf.qryCommParasByParamAttrAndParamCodeEparchyCode(bc, subSysCode, paramAttr, paramCode, eparchyCode);
        return infosDataset;
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
        return CommparaInfoIntf.qryCommParasByParamAttrAndParamCodeParaCode1ParaCode6EparchyCode(bc, subSysCode, paramAttr, paramCode, paraCode1, paraCode6, eparchyCode);

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
     * @return
     * @throws Exception
     */
    public static IDataset qryCommParasByParamAttrAndParamCodeParamCode1EparchyCode(IBizCommon bc, String subSysCode, String paramAttr, String paramCode, String paramCode1, String eparchyCode) throws Exception
    {
        IDataOutput infosDataset = CommparaInfoIntf.qryCommParasByParamAttrAndParamCodeParamCode1EparchyCodePagination(bc, subSysCode, paramAttr, paramCode, paramCode1, eparchyCode, null);
        return infosDataset.getData();
    }

    /**
     * 通过PARAM_ATTR、PARAM_CODE、PARAM_CODE1、EPARCHY_CODE查询参数COMMPARA表,支持分页
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
        IDataOutput infosDataset = CommparaInfoIntf.qryCommParasByParamAttrAndParamCodeParamCode1EparchyCodePagination(bc, subSysCode, paramAttr, paramCode, paramCode1, eparchyCode, pagination);
        return infosDataset;
    }

    /**
     * 判断账期变更后的用户是否允许业务立即生效
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static boolean qryDiverImmediProductTagBooByGrpProductId(IBizCommon bc, String productId) throws Exception
    {
        boolean ifNatureProduct = false;
        IDataset specailProductset = qryCommParasByParamAttrAndParamCodeEparchyCode(bc, "CSM", "7014", productId, "ZZZZ");
        if (IDataUtil.isNotEmpty(specailProductset))
            ifNatureProduct = true;

        return ifNatureProduct;

    }

    /**
     * 判断产品是否必须自然用账期的用户才能办理(配置了数据的产品支持分散账期，没有配置的不支持分散账期)
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static boolean qryDiverNatureProductTagBooByGrpProductId(IBizCommon bc, String productId) throws Exception
    {
        boolean ifNatureProduct = true;
        IDataset specailProductset = qryCommParasByParamAttrAndParamCodeEparchyCode(bc, "CSM", "7013", productId, "ZZZZ");
        if (IDataUtil.isNotEmpty(specailProductset))
            ifNatureProduct = false;

        return ifNatureProduct;

    }

    /**
     * 集团产品集团付费时，默认为成员付费的账目列表
     * 
     * @param bc
     * @param grpProductId
     * @return
     * @throws Exception
     */
    public static IDataset qryPayItemsParamByGrpProductId(IBizCommon bc, String grpProductId) throws Exception
    {
        IDataset infosDataset = CommparaInfoIntf.qryPayItemsParamBySubCodeParamAttrAndAttrCode(bc, "CGM", "1", grpProductId);
        return infosDataset;
    }

    /**
     * 查询集团产品控制信息查询失败后配置的异常提示信息
     * 
     * @param bc
     * @param grpProductId
     * @return
     * @throws Exception
     */
    public static String qryProductCtrlErrMessageInfoByGrpProductId(IBizCommon bc, String grpProductId) throws Exception
    {
        IDataset infosDataset = qryCommParasByParamAttrAndParamCodeEparchyCode(bc, "CGM", "400", grpProductId, "ZZZZ");
        String errMessageInfo = "";
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            IData infoData = infosDataset.getData(0);
            if (IDataUtil.isNotEmpty(infoData))
            {
                errMessageInfo = infoData.getString("PARA_CODE5", "");
            }
        }
        return errMessageInfo;
    }

}
