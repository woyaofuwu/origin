
package com.asiainfo.veris.crm.order.web.frame.csview.group.common.util;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public class GroupEsopUtilView
{
    private static Logger log = Logger.getLogger(GroupEsopUtilView.class);

    /**
     * 从ESOP获取专线信息
     * 
     * @author liujy
     * @param bc
     * @param param
     * @return
     * @throws Exception
     */
    public static IData getEsopData(IBizCommon bc, IDataset eosDataset) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData eosData = eosDataset.getData(0);
        IData resultDataset = new DataMap();

        IData inputParam = new DataMap();
        inputParam.put("X_TRANS_CODE", "ITF_EOS_QcsGrpBusi");
        inputParam.put("X_SUBTRANS_CODE", "GetEosInfo");
        inputParam.put("NODE_ID", eosData.getString("NODE_ID", ""));
        inputParam.put("IBSYSID", eosData.getString("IBSYSID", ""));
        inputParam.put("SUB_IBSYSID", eosData.getString("SUB_IBSYSID", ""));
        inputParam.put("PRODUCT_ID", eosData.getString("PRODUCT_ID"));
        inputParam.put("OPER_CODE", "14");

        IDataset httResultSetDataset = CSViewCall.call(bc, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inputParam);
        log.error("RESULT_FRO_ESOP:" + httResultSetDataset.toString());

        if (null != httResultSetDataset && httResultSetDataset.size() > 0)
        {
            IData dataLine = httResultSetDataset.getData(0);
            IData data = dataLine.getData("DLINE_DATA");
            if (null != data && data.size() > 0)
            {
                resultDataset = mergeData(dataset, httResultSetDataset);
            }
        }

        return resultDataset;
    }

    /**
     * @description 从端对端获取产品业务参数
     * @author lijie9
     * @date May 8, 2011
     * @param pd
     * @param onlyStr
     *            ture时，value为字串形式，用于ADC/MAS类；false时，value为IData形式，用于标准产品参数页面
     * @return
     * @throws Exception
     */
    public static IData getEsopParams(IBizCommon bc, IData param, boolean onlyStr) throws Exception
    {
        IData info = new DataMap();
        IData eos = new DataMap();
        String ibsysid = param.getString("IBSYSID", "");
        String eosstr = param.getString("EOS", "");
        String serviceId = param.getString("SERVICE_ID", "");

        String subIbsysid = param.getString("SUB_IBSYSID", "");
        String nodeId = param.getString("NODE_ID", "");

        if (StringUtils.isNotEmpty(eosstr) && !"{}".equals(eosstr))
        {
            IDataset eosList = new DatasetList(eosstr);
            if (eosList != null && eosList.size() > 0)
            {
                eos = eosList.getData(0);
            }
        }

        IData inparams = new DataMap();
        inparams.put("NODE_ID", nodeId);
        inparams.put("IBSYSID", ibsysid);
        inparams.put("SUB_IBSYSID", subIbsysid);
        inparams.put("PRODUCT_ID", param.getString("PRODUCT_ID", ""));
        inparams.put("index", param.getString("index", ""));
        inparams.put("BRAND_CODE", param.getString("BRAND_CODE", ""));

        if (serviceId.equals(""))
            inparams.put("SERVICE_ID", serviceId);
        inparams.putAll(eos);

        if (!"".equals(inparams.getString("IBSYSID", "")))
        {
            info = getEsopValue(bc, inparams);
            if (!onlyStr)
            {
                info = IDataUtil.iDataA2iDataB(info, "ATTR_VALUE");
            }
        }
        return info;
    }

    /**
     * @description 从端对端获取产品业务参数,并根据配置进行转换
     * @author lijie9
     * @date May 8, 2011
     * @param pd
     * @param productId
     *            产品编码
     * @param ibsysid
     *            端对端流水号
     * @param busiType
     *            端对端业务类型
     * @return
     * @throws Exception
     */
    public static IData getEsopValue(IBizCommon bc, IData inParam) throws Exception
    {
        IData resultMap = new DataMap();

        // 获取MAS业务的短信彩信区别标志
        boolean masTag = false;
        IDataset masServiceDataset = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeParamCode1EparchyCode(bc, "CGM", "1214", inParam.getString("SERVICE_ID"), "service_id", inParam.getString("EPARCHY_CODE"));

        String masServiceTag = "";
        if (IDataUtil.isNotEmpty(masServiceDataset))
        {
            masServiceTag = masServiceDataset.getData(0).getString("PARA_CODE2");

            masTag = true;
        }

        // 获取td_s_comparam表esop与CRM参数名称对应关系，做一次转换；没有配置则不作转换。用于在前台页面显示
        // 获取配置的对应关系
        String pramCode = inParam.getString("PRODUCT_ID", "");
        if (masTag)
        { // 如果是MAS产品，则按服务取参数配置，如果是其它产品，则按产品取参数配置
            pramCode = inParam.getString("SERVICE_ID");
        }
        IDataset paramList = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeEparchyCode(bc, "CGM", "1212", pramCode, inParam.getString("EPARCHY_CODE"));

        // 获取端到端参数值
        inParam.put("TAG", masServiceTag);
        inParam.put("OPER_CODE", "12");

        // 海南bboss与esop约定操作类型
        String brandcode = inParam.getString("BRAND_CODE");
        if (("BOSG").equals(brandcode))
        {
            inParam.put("OPER_CODE", "16");
        }

        IData paramValue = CSViewCall.callone(bc, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inParam);
        if (IDataUtil.isEmpty(paramValue))
            CSViewException.apperr(GrpException.CRM_GRP_508, "接口返回数据为空");

        // 将端到端传入的所有非空值都带入，以防配置不全
        paramValue.put("SERVICE_CODE", "".equals(paramValue.getString("SERVICE_CODE", "")) ? paramValue.getString("SERVICE_ID", "") : paramValue.getString("SERVICE_CODE", ""));
        Iterator iterator = paramValue.keySet().iterator();
        String tempKey = "";
        while (iterator.hasNext())
        {
            tempKey = (String) iterator.next();
            if ("X_RSPCODE".equals(tempKey) || "X_RESULTINFO".equals(tempKey) || "X_RECORDNUM".equals(tempKey) || "X_RESULTCODE".equals(tempKey) || "X_RSPDESC".equals(tempKey))
                continue;
            if (!"".equals(paramValue.getString(tempKey, "")))
            {
                resultMap.put(tempKey, paramValue.getString(tempKey, ""));
            }
        }

        // 根据配置进行转换
        if (paramList != null && paramList.size() > 0)
        {
            for (int i = 0; i < paramList.size(); i++)
            {
                IData paramInfo = paramList.getData(i);
                String crmKey = paramInfo.getString("PARA_CODE2", "").trim();
                String d2dKey = paramInfo.getString("PARA_CODE3", "").trim();
                String value = paramValue.getString(d2dKey, "");
                if (!"".equals(crmKey) && !"".equals(d2dKey))
                {
                    if (!"".equals(value))
                    {
                        resultMap.put(crmKey, value);
                    }
                    // PARA_CODE4置1则为调试模式，取PARA_CODE5的值
                    if ("1".equals(paramInfo.getString("PARA_CODE4", "").trim()) && !"".equals(paramInfo.getString("PARA_CODE5", "").trim()))
                    {
                        resultMap.put(crmKey, paramInfo.getString("PARA_CODE5", "").trim());
                    }
                }
            }
        }
        return resultMap;
    }

    /**
     * 调用ESOP本地专线数据
     * 
     * @param pd
     * @param eosDataset
     * @return
     * @throws Exception
     */
    public static IData getLocalDlineEsopData(IBizCommon bc, IData eosData) throws Exception
    {

        IData result = new DataMap();

        IData inData = new DataMap();
        inData.put("X_TRANS_CODE", "ITF_EOS_QcsGrpBusi");
        inData.put("X_SUBTRANS_CODE", "GetEosInfo");
        inData.put("NODE_ID", eosData.getString("NODE_ID", ""));
        inData.put("IBSYSID", eosData.getString("IBSYSID", ""));
        inData.put("SUB_IBSYSID", eosData.getString("SUB_IBSYSID", ""));
        inData.put("OPER_CODE", "17"); // 获取本地专线信息
        // inData.putAll(pd.getData());
        IData httpResult = CSViewCall.callone(bc, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inData);
        if (IDataUtil.isEmpty(httpResult))
            CSViewException.apperr(GrpException.CRM_GRP_508);

        result = mergeLocalDlineData(bc, httpResult);
        return result;
    }

    /*
     * @description 封装ESOP过来的商品数据
     * @author xunyl
     * @date 2013-10-03
     */
    protected static IData makeMerchInfo(IData param) throws Exception
    {
        // 1- 定义商品信息
        IData goodInfo = new DataMap();

        // 2- 添加商品用户编号
        String userId = param.getString("USER_ID", "");
        goodInfo.put("USER_ID", userId);

        // 3- 添加发起方、落地方信息
        goodInfo.put("LOCATION", "SEND");

        // 4- 添加产品信息
        IDataset productInfoList = makeProductInfoList(param);
        goodInfo.put("PRODUCT_INFO", productInfoList);

        // 5- 返回商品信息
        return goodInfo;
    }

    /*
     * @description 拼装产品元素信息
     * @author xunyl
     * @date 2013-10-03
     */
    protected static IData makeProductElement(IBizCommon bc, IData param) throws Exception
    {
        // 1- 定义资费对象
        IData elementInfo = new DataMap();

        // 2- 获取产品编号、产品资费、资费操作编码、资费描述、产品资费参数属性代码、产品资费参数属性值、产品资费参数属性名
        IDataset productIdList = IDataUtil.getDatasetSpecl("PRSRV_STR10", param);
        IDataset productRateList = IDataUtil.getDataset("RSRV_STR8", param);
        IDataset actionCv2List = IDataUtil.getDataset("ACTION_CV2", param);
        IDataset pDiscntsParamCodeList = param.getDataset("RSRV_STR9");
        IDataset pDiscntsParamValueList = param.getDataset("RSRV_STR11");
        IDataset pDiscntsParamNameList = param.getDataset("RSRV_STR10");

        // 3- 循环产品参数编号，封装产品资费信息
        for (int i = 0; i < productIdList.size(); i++)
        {
            if (IDataUtil.isEmpty(productRateList))
            {
                continue;
            }
            IDataset productRateListElement = (IDataset) productRateList.get(i);
            IDataset actionCv2ListElement = (IDataset) actionCv2List.get(i);
            IDataset pDiscntsParamCodeListList = (IDataset) pDiscntsParamCodeList.get(i);
            IDataset pDiscntsParamNameListList = (IDataset) pDiscntsParamNameList.get(i);
            IDataset pDiscntsParamValueListList = (IDataset) pDiscntsParamValueList.get(i);

            IDataset productSet = new DatasetList();
            for (int j = 0; j <= productRateListElement.size(); j++)
            {
                String elementId = (String) productRateListElement.get(i);
                // 资费编码为空，退出当前操作
                if (StringUtils.isEmpty(elementId))
                {
                    continue;
                }

                // 资费编码在CRM表中不存在，退出当前操作
                String subProductId = (String) productIdList.get(i);
                IData inparam = new DataMap();
                inparam.put("PRODUCT_ID", subProductId);
                inparam.put("ELEMENT_ID", elementId);
                IDataset elements = CSViewCall.call(bc, "CS.ProductInfoQrySVC.getElementByProductIdElemId", inparam);
                if (elements == null || elements.size() == 0)
                {
                    CSViewException.apperr(ElementException.CRM_ELEMENT_242, elementId);
                }

                // 封装资费参数
                IDataset disParamCodeList = (IDataset) pDiscntsParamCodeListList.get(j);
                IDataset disParamNameList = (IDataset) pDiscntsParamNameListList.get(j);
                IDataset disParamValueList = (IDataset) pDiscntsParamValueListList.get(j);
                IDataset disntParamlist = new DatasetList();
                for (int k = 0; k < disParamCodeList.size(); k++)
                {
                    String disParamCode = (String) disParamCodeList.get(k);
                    String disParamName = (String) disParamNameList.get(k);
                    String disParamValue = (String) disParamValueList.get(k);

                    if (disParamCode != null && disParamCode.length() > 0)
                    {
                        IData disntParam = new DataMap();
                        disntParam.put("INST_TYPE", "D");
                        disntParam.put("ATTR_CODE", disParamCode);
                        disntParam.put("ATTR_VALUE", disParamValue);
                        disntParam.put("ATTR_NAME", disParamName);
                        disntParamlist.add(disntParam);
                    }
                }

                // 封装资费信息
                for (int l = 0; l < elements.size(); l++)
                {
                    IData element = elements.getData(l);
                    String proAction = actionCv2ListElement.get(j).toString();
                    if ("0".equals(proAction))
                    {
                        element.put("STATE", "DEL");
                        String cancelTag = element.getString("CANCEL_TAG");
                        String cancelAbsoluteDate = element.getString("CANCEL_ABSOLUTE_DATE");
                        element.put("END_DATE", SysDateMgr.cancelDate(cancelTag, cancelAbsoluteDate, null, null));
                    }
                    else if ("1".equals(proAction))
                    {
                        element.put("STATE", "ADD");
                        String enableTag = element.getString("ENABLE_TAG");
                        String startOffset = element.getString("START_OFFSET", "0");
                        String startUnit = element.getString("START_UNIT");
                        String startAbsoluteDate = element.getString("START_ABSOLUTE_DATE");
                        String startDate = SysDateMgr.startDate(enableTag, startAbsoluteDate, startOffset, startUnit);
                        element.put("START_DATE", startDate);
                        String endEnableTag = element.getString("END_ENABLE_TAG");
                        String endOffser = element.getString("END_OFFSET", "0");
                        String endUnit = element.getString("END_UNIT");
                        String endAbsoluteDate = element.getString("END_ABSOLUTE_DATE");
                        String endDate = SysDateMgr.endDate(startDate, endEnableTag, endAbsoluteDate, endOffser, endUnit);
                        element.put("END_DATE", endDate);
                    }
                    else
                    {
                        CSViewException.apperr(ElementException.CRM_ELEMENT_304, elementId);
                    }

                    element.put("ELEMENT_INDEX", l + 1);
                    if (elementId.equals(element.getString("ELEMENT_ID")))
                    {
                        if (disntParamlist.size() > 0)
                        {
                            element.put("DISCNT_PARAM", disntParamlist);
                            element.put("HAS_DISCNT_PARAM", "true");
                        }
                    }
                }

                IData selectedProductDiscnt = new DataMap();
                selectedProductDiscnt.put("PRODUCT_ID", subProductId);
                selectedProductDiscnt.put("PACKAGE_ID", elements.getData(0).getString("PACKAGE_ID"));
                selectedProductDiscnt.put("PRODUCT_MODE", elements.getData(0).getString("PRODUCT_MODE"));
                selectedProductDiscnt.put("ELEMENTS", elements);
                productSet.add(selectedProductDiscnt);
            }
            String subProductId = (String) productIdList.get(i);
            elementInfo.put(subProductId + "_" + i, productSet);
        }

        // 3- 返回产品参数对象
        return elementInfo;

    }

    /*
     * @description 封装ESOP过来的产品数据
     * @author xunyl
     * @date 2013-10-03
     */
    protected static IDataset makeProductInfoList(IData param) throws Exception
    {
        // 1- 定义产品数据集
        IDataset productInfoList = new DatasetList();

        // 2- 获取产品基本信息，包括产品编号、产品用户编号、产品操作代码
        IDataset productIdList = IDataUtil.getDatasetSpecl("PRSRV_STR10", param);
        IDataset subUserIdList = IDataUtil.getDatasetSpecl("RSRV_STR4", param);
        IDataset productsOperCodeList = IDataUtil.getDataset("RSRV_STR14", param);
        if (IDataUtil.isEmpty(productInfoList))
        {
            return productInfoList;
        }

        // 3- 循环产品编号，封装产品信息
        for (int i = 0; i < productIdList.size(); i++)
        {
            // 3-1 定义产品信息
            IData productInfo = new DataMap();

            // 3-2 添加产品编号
            String subProductId = (String) productIdList.get(i);
            productInfo.put("PRODUCT_ID", subProductId);

            // 3-3 添加产品用户编号
            String subUserId = "";
            if (!IDataUtil.isEmpty(subUserIdList))
            {
                subUserId = (String) subUserIdList.get(i);
                productInfo.put("USER_ID", subUserId);
            }

            // 3-4 添加产品下标
            productInfo.put("PRODUCT_INDEX", i);

            // 3-5 添加产品操作编号
            String subProductOper = (String) ((IDataset) productsOperCodeList.get(i)).get(0);
            productInfo.put("PRODUCT_OPER_CODE", subProductOper);

            // 3-6 添加单条产品信息至产品数据集
            productInfoList.add(productInfo);
        }

        // 4- 返回产品数据集
        return productInfoList;
    }

    /*
     * @description 拼装产品参数信息
     * @author xunyl
     * @date 2013-10-03
     */
    protected static IData makeProductParam(IData param) throws Exception
    {
        // 1- 定义产品参数对象
        IData paramInfo = new DataMap();

        // 2- 获取产品编号、产品用户编号、产品参数编号、产品参数名称、产品参数值、产品参数操作编码
        IDataset productIdList = IDataUtil.getDatasetSpecl("PRSRV_STR10", param);
        IDataset subUserIdList = IDataUtil.getDatasetSpecl("RSRV_STR4", param);
        IDataset productsParamCodeList = IDataUtil.getDataset("RSRV_STR15", param);
        IDataset productsParamValueList = IDataUtil.getDataset("RSRV_STR16", param);
        IDataset productsParamNameList = IDataUtil.getDataset("RSRV_STR17", param);
        IDataset productsParamOperList = IDataUtil.getDataset("RSRV_STR18", param);

        // 3- 循环产品参数编号，封装产品参数信息
        for (int i = 0; i < productIdList.size(); i++)
        {
            IDataset paramCodeList = (IDataset) productsParamCodeList.get(i);
            if (!(paramCodeList instanceof IDataset))
            {
                CSViewException.apperr(ParamException.CRM_PARAM_444);
            }
            IDataset paramValueList = (IDataset) productsParamValueList.get(i);
            if (!(paramValueList instanceof IDataset))
            {
                CSViewException.apperr(ParamException.CRM_PARAM_445);
            }
            IDataset paramNameList = (IDataset) productsParamNameList.get(i);
            if (!(paramNameList instanceof IDataset))
            {
                CSViewException.apperr(ParamException.CRM_PARAM_446);
            }
            IDataset paramOperList = (IDataset) productsParamOperList.get(i);
            if (!(paramOperList instanceof IDataset))
            {
                CSViewException.apperr(ParamException.CRM_PARAM_447);
            }

            IData productParamList = new DataMap();
            for (int j = 0; j < paramCodeList.size(); j++)
            {
                IData productParam = new DataMap();
                String code = (String) paramCodeList.get(j);
                String value = (String) paramValueList.get(j);
                String name = (String) paramNameList.get(j);
                String state = "1".equals((String) paramOperList.get(j)) ? "ADD" : "DEL";
                String subUserId = (String) subUserIdList.get(i);

                productParam.put("ATTR_CODE", code);
                productParam.put("ATTR_VALUE", value);
                productParam.put("ATTR_NAME", name);
                productParam.put("STATE", state);
                productParam.put("USER_ID", subUserId);
                productParamList.put(code + "_" + j, productParam);
            }

            String subProductId = (String) productIdList.get(i);
            paramInfo.put(subProductId + "_" + i, productParamList);
        }

        // 4- 返回产品参数对象
        return paramInfo;
    }

    /**
     * 解析专线数据
     * 
     * @param dataset
     * @param httpResult
     * @return
     * @throws Exception
     */
    protected static IData mergeData(IDataset dataset, IDataset httpResult) throws Exception
    {
        IData resultData = new DataMap();
        IDataset comData = new DatasetList();
        IDataset dataLineAttr = new DatasetList();

        if (null != httpResult && httpResult.size() > 0)
        {
            IData dataLine = httpResult.getData(0);
            if (null != dataLine && dataLine.size() > 0)
            {
                IData totalData = dataLine.getData("DLINE_DATA");
                IData commonData = totalData.getData("COMM_DATA_MAP");
                IDataset lineDataList = totalData.getDataset("LINE_DATA_LIST");

                // 公共数据
                for (int i = 0; i < commonData.size(); i++)
                {
                    IData attrValue = new DataMap();
                    String attr[] = commonData.getNames();
                    attrValue.put("ATTR_CODE", attr[i]);
                    attrValue.put("ATTR_VALUE", commonData.getString(attr[i]));
                    comData.add(attrValue);
                }

                // 专线数据
                for (int j = 0; j < lineDataList.size(); j++)
                {
                    IData data = lineDataList.getData(j);
                    IData attrValue = new DataMap();
                    for (int k = 0; k < data.size(); k++)
                    {
                        String attr[] = data.getNames();
                        attrValue.put(attr[k], data.getString(attr[k]));
                    }
                    dataLineAttr.add(attrValue);
                }
            }
        }

        resultData.put("COMMON_DATA", comData);
        resultData.put("DLINE_DATA", dataLineAttr);

        return resultData;
    }

    /**
     * 拼ESOP本地专线数据
     * 
     * @param dataset
     * @param httpResult
     * @return
     * @throws Exception
     */
    public static IData mergeLocalDlineData(IBizCommon bc, IData httpResult) throws Exception
    {

        IDataset set = httpResult.getDataset("DLINE_DATA");
        IData result = new DataMap();// 目前只有一条专线
        String AAddress = ""; // A端地址 省_市_县
        String ZAddress = ""; // Z端地址 省_市_县

        for (int i = 0; i < set.size(); i++)
        {
            IData data = set.getData(i);

            AAddress = data.getString("provinceA", "") + "_" + data.getString("cityA", "") + "_" + data.getString("areaA", "") + data.getString("countyA", "") + data.getString("villageA", "");
            ZAddress = data.getString("provinceZ", "") + "_" + data.getString("cityZ", "") + "_" + data.getString("areaZ", "") + data.getString("countyZ", "") + data.getString("villageZ", "");
            result.put("DLINE_TRADE_ID", data.getString("DLINE_TRADE_ID", ""));// 业务勘查序号
            result.put("PRODUCT_NO", data.getString("DLINE_PRODUCT_NO", "")); // 业务标识
            result.put("LINE_SPEED", StaticUtil.getStaticValue("LINE_SPEED_A", data.getString("bandwidth", "")));// 业务带宽(速率)

            // 数据专线
            result.put("A_UNIT_ADDRESS", AAddress); // A端地址 省_市_县
            result.put("Z_UNIT_ADDRESS", ZAddress); // Z端地址 省_市_县
            result.put("GROUPLINE_TYPE", data.getString("transferMode", "")); // 专线类型
            result.put("A_PORT_TYPE", data.getString("portARate", "")); // A端接入类型
            result.put("A_UNIT_PERSON", data.getString("portAContact", "")); // A端联系人
            result.put("A_UNIT_PHONE", data.getString("portAContactPhone", "")); // A端联系电话

            result.put("Z_PORT_TYPE", data.getString("portZRate", "")); // Z端接入类型
            result.put("Z_UNIT_PERSON", data.getString("portZContact", "")); // Z端联系人
            result.put("Z_UNIT_PHONE", data.getString("portZContactPhone", "")); // Z端联系电话

            // 互联网专线
            result.put("UNIT_ADDRESS", AAddress); // 互联网专线安装地址
            result.put("UNIT_NAME", data.getString("portAContact", "")); // 互联网专线联系人
            result.put("UNIT_PHONE", data.getString("portAContactPhone", "")); // 互联网专线联系电话

            // 成员USERID转换成SERIALNUMBER
            if (!"".equals(data.getString("DLINE_USERID", "")))
            {
                // IData tempPam = new DataMap();
                // tempPam.put("REMOVE_TAG", "0");
                // tempPam.put("USER_ID",data.getString("DLINE_USERID", ""));
                // tempPam.put("SQL_REF", "SEL_BY_USERID");
                // IDataset tempset = UserInfoQry.getUserInfo(pd,tempPam);
                // if (tempset == null || tempset.size() == 0)
                // {
                // common.error("获取用户资料失败[user_id =" + data.getString("DLINE_USERID", "") + "]");
                // }
                // IData tem = tempset.getData(0);
                // result.put("DLINE_SERNUMBER", tem.getString("SERIAL_NUMBER", "")); //用户号码
            }
            else
            {
                result.put("DLINE_SERNUMBER", ""); // 用户号码
            }
        }

        return result;
    }

    /**
     * @description 解析ESOP传过来的BBOSS业务串
     * @author xunyl
     * @date 2013-10-03
     */
    public static IData paraserEsopString(IBizCommon bc, IData param) throws Exception
    {
        // 1- 定义返回数据
        IData esopInfo = new DataMap();

        // 2- 添加商品编号
        String productId = param.getString("PRODUCT_ID");
        esopInfo.put("CURRENT_PRODUCT_ID", productId);

        // 3- 添加商品信息(包括产品信息)
        IData goodInfo = makeMerchInfo(param);
        esopInfo.put("PRODUCT_GOOD_INFO", goodInfo);

        // 4- 添加产品参数信息
        IDataset productInfoList = goodInfo.getDataset("PRODUCT_INFO");
        if (productInfoList.isEmpty())
        {
            return esopInfo;
        }
        IData paramInfo = makeProductParam(param);
        esopInfo.put("PRODUCT_PARAM", paramInfo);

        // 5- 添加产品元素信息
        IData elementInfo = makeProductElement(bc, param);
        esopInfo.put("PRODUCTS_ELEMENT", elementInfo);

        // 6- 返回数据
        return esopInfo;
    }
}
