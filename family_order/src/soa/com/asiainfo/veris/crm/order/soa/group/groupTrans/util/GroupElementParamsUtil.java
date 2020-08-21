
package com.asiainfo.veris.crm.order.soa.group.groupTrans.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.check.ParamCheckBaseBean;

public class GroupElementParamsUtil
{
    public static IDataset qryElementParamByElementId(IData curProductParam, String elementId, String elementType, String operaType) throws Exception
    {

        // 获取配置的所有特定的产品参数(BBOSS产品除外)
        IDataset productParams = GroupElementParamsUtil.getConfigParam(elementId, operaType);
        // 根据PARA_CODE4到td_b_attr_itema表获取默认值(从td_b_attr_itema中获取，是为了避免重复配置初始值)
        IDataset initDs = qryElementParamByElmentId2IDataset(elementId, elementType, operaType);

        // 如果没有配置,则以默认值为准
        /*
         * if(IDataUtil.isEmpty(productParams) && IDataUtil.isNotEmpty(initDs)) return initDs;
         */

        // 默认值替换commpara中的配置参数值
        GroupElementParamsUtil.setParamInitValue(productParams, IDataUtil.hTable2StdSTable(initDs, "ATTR_CODE", "ATTR_INIT_VALUE"));

        // 将外围渠道传入的参数值置入取出的列表中
        GroupElementParamsUtil.setInParam(productParams, curProductParam);

        // 校验所有参数(防止配置错误),如果要节省校验时间,可以修改成只校验接口传入的参数
        ParamCheckBaseBean.checkAllParams(productParams);

        // 转换数据
        // 转换数据
        IDataset resultElementParams = new DatasetList();
        for (int i = 0, iSize = productParams.size(); i < iSize; i++)
        {
            IData elementConfig = productParams.getData(i);
            IData eleParamData = new DataMap();
            eleParamData.put("ATTR_CODE", elementConfig.getString("PARA_CODE1", ""));
            eleParamData.put("ATTR_VALUE", elementConfig.getString("PARA_CODE4", ""));
            resultElementParams.add(eleParamData);
        }

        return resultElementParams;
    }

    public static IData qryElementParamByElementIdADCMAS(IData curProductParam, String elementId, String elementType, String operaType, String productId, IData data) throws Exception
    {

        IData platSvcData = new DataMap();

        IDataset productParams = GroupElementParamsUtil.getConfigParam(elementId, operaType);
        // 海南ADC一卡通产品ID与包里面的服务ID一样
        if (productId.equals("10005743") && productId.equals(elementId))
        {
            if (IDataUtil.isNotEmpty(productParams))
            {
                productParams.removeAll(DataHelper.filter(productParams, "PARA_CODE1=FEE_CYCLE"));
                productParams.removeAll(DataHelper.filter(productParams, "PARA_CODE1=HIRE_FEE"));
            }
        }

        // 获取配置的通用参数
        setCommonParams(productParams, operaType);

        // 根据PARA_CODE4到td_b_attr_itema表获取默认值(从td_b_attr_itema中获取，是为了避免重复配置初始值)
        IData initDs = qryElementParamByElmentId(elementId, elementType, operaType);

        // 如果没有配置,则以默认值为准
        if (IDataUtil.isEmpty(productParams) && IDataUtil.isNotEmpty(initDs))
            return initDs;

        // 默认值替换commpara中的配置参数值
        GroupElementParamsUtil.setParamInitValue(productParams, initDs);

        IDataset platsvcParams = AttrItemInfoQry.getelementItemaByProductId("S", "9", productId, null);

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);

        if ("ADCG".equals(brandCode))
        {
            // 自动生成服务代码
            // 同一产品下的服务代码要一致
            if (StringUtils.isEmpty(data.getString("COMMON_BIN_IN_CODE")))
            {
                getAndSetBizInCode(productParams, data);
            }
        }

        setSpecialData(productParams, data);

        GroupElementParamsUtil.setInParam(productParams, curProductParam);

        // 校验所有参数(防止配置错误),如果要节省校验时间,可以修改成只校验接口传入的参数
        ParamCheckBaseBean.checkAllParams(productParams);

        // 校验成功以后，进行格式转换，组织到ELEMENTS:SERV_PARAM:PLATSVC中
        // 平台参数
        platSvcData = IDataUtil.replaceIDataKeyAddPrefix(IDataUtil.hTable2StdSTable(productParams, "PARA_CODE1", "PARA_CODE4"), "pam_");

        // 其他参数,海南没有other,molist为null,不需要处理
        /*
         * IDataset otherParamList = qryElementParamByElmentId2IDataset(elementId,"K",operaType);
         * if(IDataUtil.isEmpty(otherParamList))//海南没有other,molist为null,不需要处理 return;
         */

        return platSvcData;
    }

    private static void setSpecialData(IDataset productParams, IData data) throws Exception
    {
        for (int row = 0, iSize = productParams.size(); row < iSize; row++)
        {
            IData specialData = productParams.getData(row);
            if (specialData.getString("PARA_CODE1", "").equals("MODIFY_TAG"))
            {
                specialData.put("PARA_CODE4", "0");
                continue;
            }
            if (specialData.getString("PARA_CODE1", "").equals("EC_BASE_IN_CODE"))
            {
                specialData.put("PARA_CODE4", data.getString("BIZ_IN_CODE"));
                continue;
            }
            if (specialData.getString("PARA_CODE1", "").equals("PLAT_SYNC_STATE"))
            {
                specialData.put("PARA_CODE4", "1");
                continue;
            }
            if (specialData.getString("PARA_CODE1", "").equals("ADMIN_NUM"))
            {
                specialData.put("PARA_CODE4", data.getString("GROUP_MGR_SN"));
                continue;
            }
            if (specialData.getString("PARA_CODE1", "").equals("BIZ_IN_CODE"))
            {
                specialData.put("PARA_CODE4", data.getString("BIZ_IN_CODE"));
                continue;
            }
        }

    }

    private static void getAndSetBizInCode(IDataset productParams, IData data) throws Exception
    {
        if (IDataUtil.isEmpty(productParams))
            return;

        String siSvrCodeHead = "";
        String accessMode = "";
        String bizInCode = "";
        int bizNum = -1;

        for (int i = 0, iSize = productParams.size(); i < iSize; i++)
        {
            String paraCode1 = productParams.getData(i).getString("PARA_CODE1", "");
            String paraCode4 = productParams.getData(i).getString("PARA_CODE4", "");
            if ("SVR_CODE_HEAD".equals(paraCode1) && StringUtils.isNotEmpty(paraCode4))
            {
                siSvrCodeHead = paraCode4;
            }
            if ("ACCESS_MODE".equals(paraCode1) && StringUtils.isNotEmpty(paraCode4))
            {
                accessMode = paraCode4;
            }
            if ("BIZ_IN_CODE".equals(paraCode1) && StringUtils.isNotEmpty(paraCode4))
            {
                bizNum = i;
            }
        }

        if (StringUtils.isEmpty(siSvrCodeHead))
            CSAppException.apperr(GrpException.CRM_GRP_713, "没有配置服务代码头SVR_CODE_HEAD,无法生成服务号!");

        int len = 14 - siSvrCodeHead.length();

        for (int i = 0, iSize = 1000; i < iSize; i++)
        {

            String extendCode = SeqMgr.getGrpMolist();

            bizInCode = siSvrCodeHead + extendCode.substring(extendCode.length() - len, extendCode.length());

            IDataset bizCodeExist = UserGrpInfoQry.getDumpIdByajax(bizInCode, data.getString("GROUP_ID"));

            if (IDataUtil.isEmpty(bizCodeExist))
                productParams.getData(bizNum).put("PARA_CODE4", bizInCode);
            data.put("BIZ_IN_CODE", bizInCode);
            data.put("COMMON_BIN_IN_CODE", bizInCode);
            break;
        }
    }

    public static IData qryElementParamByElmentId(String elementId, String idType, String operMode) throws Exception
    {
        IData param = new DataMap();
        if ("CrtUs".equals(operMode))
        {
            param.put("ATTR_OBJ", "0");
        }
        else if ("CrtMb".equals(operMode))
        {
            param.put("ATTR_OBJ", "1");
        }
        param.put("ID", elementId);
        param.put("ID_TYPE", idType);
        // param.put("ATTR_OBJ", "0");
        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        IDataset attrItemList = AttrItemInfoQry.getAttrItemAByIDTO(param, null);

        IData productParamData = IDataUtil.hTable2StdSTable(attrItemList, "ATTR_CODE", "ATTR_INIT_VALUE");

        return productParamData;
    }

    public static IDataset qryElementParamByElmentId2IDataset(String elementId, String idType, String operMode) throws Exception
    {
        IData param = new DataMap();
        if ("CrtUs".equals(operMode))
        {
            param.put("ATTR_OBJ", "0");
        }
        else if ("CrtMb".equals(operMode))
        {
            param.put("ATTR_OBJ", "1");
        }
        param.put("ID", elementId);
        param.put("ID_TYPE", idType);
        // param.put("ATTR_OBJ", "0");
        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        IDataset attrItemList = AttrItemInfoQry.getAttrItemAByIDTO(param, null);

        return attrItemList;
    }

    public static void setInParam(IDataset configParams, IData inParam) throws Exception
    {
        if (IDataUtil.isNotEmpty(configParams) && IDataUtil.isNotEmpty(inParam))
        {
            for (int i = 0, iSize = configParams.size(); i < iSize; i++)
            {
                IData param = configParams.getData(i);
                String paramCode = param.getString("PARA_CODE1");
                // 将外围渠道传入的参数值置入取出的列表中。PARA_CODE6,设定是否可以被外围渠道修改。
                if ("1".equals(param.getString("PARA_CODE6", "")) && !"".equals(inParam.getString(paramCode, "")))
                {
                    param.put("PARA_CODE4", inParam.getString(paramCode));
                }
            }
        }
    }

    // 用默认值替换commpara中的配置参数值
    public static void setParamInitValue(IDataset params, IData initData) throws Exception
    {
        if (IDataUtil.isNotEmpty(params) && initData != null)
        {
            for (int i = 0, iSize = params.size(); i < iSize; i++)
            {
                String attrCode = params.getData(i).getString("PARA_CODE1");
                if ("ITEMA".equals(params.getData(i).getString("PARA_CODE4", "")))
                {
                    params.getData(i).put("PARA_CODE4", initData.getString(attrCode, ""));
                }
            }
        }
    }

    public static IDataset getConfigParam(String paramCode, String operMode) throws Exception
    {
        IDataset configParams = CommparaInfoQry.getCommPkInfo("CGM", "1216", paramCode, CSBizBean.getUserEparchyCode());
        // if ("CrtUs".equals(operMode) || "CrtMb".equals(operMode)){
        configParams = DataHelper.filter(configParams, "PARA_CODE10=" + operMode);

        return configParams;
    }

    public static void setCommonParams(IDataset params, String operMode) throws Exception
    {
        // 获取配置的通用参数,加入到platsvc服务参数中
        IDataset commParams = DataHelper.filter(params, "PARA_CODE1=0");
        if (commParams != null && commParams.size() > 0)
        {
            String commStr = "";
            for (int i = 0; i < commParams.size(); i++)
            {
                commStr = commParams.getData(i).getString("PARA_CODE4", "");
                if (!"".equals(commStr))
                {
                    IDataset tempDs = getConfigParam(commStr, operMode);
                    if (IDataUtil.isNotEmpty(tempDs))
                    {
                        // 将通用参数的PARAM_CODE置为产品或服务ID
                        for (int j = 0; j < tempDs.size(); j++)
                        {
                            tempDs.getData(j).put("PARAM_CODE", commParams.getData(i).getString("PARAM_CODE"));
                        }
                        params.addAll(tempDs);
                    }
                }
            }
            // 去除通用的配置
            params.removeAll(commParams);
        }

    }
}
