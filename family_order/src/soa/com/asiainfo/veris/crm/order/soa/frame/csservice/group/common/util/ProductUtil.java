
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.PackagePrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.SvcPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class ProductUtil extends CSBizBean
{

    // 根据INST_ID组装优惠属性
    public static void assembleDiscntParam(IDataset userSvcs, IData groupAttr) throws Exception
    {

        Iterator instidIter = groupAttr.keySet().iterator();
        while (instidIter.hasNext())
        {
            String _instid = (String) instidIter.next();
            int svccount = userSvcs.size();
            for (int i = 0; i < svccount; i++)
            {
                IData userelement = userSvcs.getData(i);
                String instid = userelement.getString("INST_ID");
                if (_instid.equals(instid))
                {
                    userelement.put("DISCNT_PARAM", groupAttr.getDataset(_instid));
                    break;
                }
            }
        }
    }

    // 根据INST_ID组装服务属性
    public static void assembleSvcParam(IDataset userSvcs, IData groupAttr) throws Exception
    {

        Iterator instidIter = groupAttr.keySet().iterator();
        while (instidIter.hasNext())
        {
            String _instid = (String) instidIter.next();
            int svccount = userSvcs.size();
            for (int i = 0; i < svccount; i++)
            {
                IData userelement = userSvcs.getData(i);
                String instid = userelement.getString("INST_ID");
                if (_instid.equals(instid))
                {
                    userelement.put("SERV_PARAM", groupAttr.getDataset(_instid));
                    break;
                }
            }
        }
    }

    // /**
    // * 遍历包中的元素 将其分类到服务、资费等多个IDataset中 便于显示
    // *
    // * @param datas
    // * @return
    // * @throws Exception
    // */
    // public static IDataset cleanUpForDiscntPackage(IDataset datas, IData params) throws Exception
    // {
    //
    // IDataset discntElements = new DatasetList();
    // for (int i = 0; i < datas.size(); i++)
    // {
    // IData dataTemp = (IData) datas.get(i);
    // dataTemp.put("ELEMENT_ID", dataTemp.getString("DISCNT_CODE"));
    // dataTemp.put("ELEMENT_NAME", dataTemp.getString("DISCNT_NAME"));
    // dataTemp.put("ELEMENT_INDEX", "1");
    // dataTemp.put("ENABLE_TAG", "0");
    // dataTemp.put("END_ENABLE_TAG", "0");
    // dataTemp.put("ELEMENT_TYPE_CODE", "D");
    // dataTemp.put("CANCEL_TAG", "1");
    // dataTemp.put("PRODUCT_MODE", params.getString("PRODUCT_MODE"));
    // dataTemp.put("MAIN_TAG", "0");
    // dataTemp.put("FORCE_TAG", "0");
    // dataTemp.put("TRADE_FEE", "0");
    // dataTemp.put("CHECKED", "checked");
    // dataTemp.put("END_DATE", "2050-00-00");
    // params.put("NEED_DISCNT_PARAM", "false");
    // IData elementLimit = ProductUtil.getElementLimitByEleId(dataTemp.getString("ELEMENT_ID"), "D",
    // BizRoute.getRouteId()getRoute());
    // if (elementLimit != null)
    // dataTemp.putAll(elementLimit);
    // if ("false".equals(params.getString("NEED_DISCNT_PARAM")))
    // {
    // dataTemp.put("HAS_DISCNT_PARAM", "false");
    // }
    // else
    // {
    // // 判断是否有服务参数
    // IData discntParamInfo = ProductUtil.getElementParam(dataTemp.getString("ELEMENT_ID"), "D");
    // if (discntParamInfo.size() == 0)
    // {
    // // 该服务没有参数
    // dataTemp.put("HAS_DISCNT_PARAM", "false");
    // }
    // else
    // {
    // dataTemp.put("HAS_DISCNT_PARAM", "true");
    // }
    // }
    // // 加入product_mode
    // dataTemp.put("PRODUCT_MODE", params.getString("PRODUCT_MODE"));
    // discntElements.add(dataTemp);
    // }
    // return discntElements;
    // }

    /**
     * 遍历包中的元素 将其分类到服务IDataset中 便于显示
     * 
     * @author Zhujm
     * @param datas
     * @return
     * @throws Exception
     */
    public static IDataset cleanUpForServPackage(IDataset datas, IData params) throws Exception
    {

        IDataset datasetTemp = new DatasetList();
        IDataset servElements = new DatasetList();
        for (int i = 0; i < datas.size(); i++)
        {
            IData dataTemp = (IData) datas.get(i);
            dataTemp.put("ELEMENT_ID", dataTemp.getString("SERVICE_ID"));
            dataTemp.put("ELEMENT_NAME", dataTemp.getString("SERVICE_NAME"));
            dataTemp.put("ELEMENT_INDEX", "1");
            dataTemp.put("ENABLE_TAG", "0");
            dataTemp.put("END_ENABLE_TAG", "0");
            dataTemp.put("ELEMENT_TYPE_CODE", "S");
            dataTemp.put("CANCEL_TAG", "0");
            dataTemp.put("PRODUCT_MODE", params.getString("PRODUCT_MODE"));
            dataTemp.put("MAIN_TAG", "0");
            dataTemp.put("FORCE_TAG", "0");
            dataTemp.put("TRADE_FEE", "0");
            params.put("NEED_SERV_PARAM", "false");
            if ("false".equals(params.getString("NEED_SERV_PARAM")))
            {
                dataTemp.put("HAS_SERV_PARAM", "false");
            }
            else
            {
                // 判断是否有服务参数
                IData servParamInfo = ProductUtil.getElementParam(dataTemp.getString("ELEMENT_ID"), "S");
                if (servParamInfo.size() == 0)
                {
                    // 该服务没有参数
                    dataTemp.put("HAS_SERV_PARAM", "false");
                }
                else
                {
                    dataTemp.put("HAS_SERV_PARAM", "true");
                }
            }
            // 加入product_mode
            dataTemp.put("PRODUCT_MODE", params.getString("PRODUCT_MODE"));
            // 默认每行3个服务元素
            if ((i + 1) % 3 == 0)
            {
                datasetTemp.add(dataTemp);
                servElements.add(datasetTemp);
                datasetTemp = new DatasetList();
            }
            else
            {
                datasetTemp.add(dataTemp);
            }
        }
        if (datasetTemp.size() > 0)
        {
            for (int i = datasetTemp.size(); i < 3; i++)
            {
                IData tmp = new DataMap();
                datasetTemp.add(tmp);
            }
            servElements.add(datasetTemp);
        }
        return servElements;
    }

    /*
     * 合成 CallNetType字段
     */
    public static String comCallNetTypeField(IData paramData)
    {

        String strTag;
        String strNewTag = "";
        strTag = paramData.getString("CALL_NET_TYPE1", "");
        strNewTag = (strTag == null) ? "0" : "1";
        strTag = paramData.getString("CALL_NET_TYPE2", "");
        strNewTag += (strTag == null) ? "0" : "1";
        strTag = paramData.getString("CALL_NET_TYPE3", "");
        strNewTag += (strTag == null) ? "0" : "1";
        strTag = paramData.getString("CALL_NET_TYPE4", "");
        strNewTag += (strTag == null) ? "0" : "1";
        return strNewTag;
    }

    /*
     * 合成 FeeType字段
     */
    public static String comFeeTypeField(IData paramData)
    {

        String strTag;
        String strNewTag = "0";
        strTag = paramData.getString("LIMFEE_TYPE_CODE1", "");
        strNewTag = (strTag == null) ? "0" : "1";
        strTag = paramData.getString("LIMFEE_TYPE_CODE2", "");
        strNewTag += (strTag == null) ? "0" : "1";
        strTag = paramData.getString("LIMFEE_TYPE_CODE3", "");
        strNewTag += (strTag == null) ? "0" : "1";
        strTag = paramData.getString("LIMFEE_TYPE_CODE4", "");
        strNewTag += (strTag == null) ? "0" : "1";
        return strNewTag;
    }

    /**
     * 生成调用规则的数据
     * 
     * @return
     * @throws Exception
     */
    public static IData createRuleData() throws Exception
    {

        String tradeDataStr = "";
        if (tradeDataStr == null)
        {
            return new DataMap();
        }

        IData result = new DataMap();
        String eparchyCode = CSBizBean.getTradeEparchyCode();
        result.put("TRADE_EPARCHY_CODE", eparchyCode);
        // result.put("USER_ID", td.getUserInfo().getString("USER_ID"));
        // result.put("BRAND_CODE", td.getUserInfo().getString("BRAND_CODE"));
        // result.put("PRODUCT_ID", td.getUserInfo().getString("PRODUCT_ID"));
        result.put("PRODUCT_ID_NEW", "");
        result.put("PACKAGE_ID_NEW", "");
        String strVipClass = "";// td.getUserInfo().getString("VIP_TYPE_CODE", "") +
        // td.getUserInfo().getString("CLASS_ID", "");
        if ("".equals(strVipClass))
        {
            strVipClass = "***";
        }
        result.put("VIP_LEVEL", strVipClass);
        result.put("VIP_TYPE", strVipClass);
        // result.put("OPEN_DATE", td.getUserInfo().getString("OPEN_DATE"));
        result.put("BRAND_NO", "***");
        return result;
    }

    /**
     * 用于深度clone对象
     * 
     * @param obj
     * @return
     * @throws Exception
     */
    public static Object deepCopy(Object obj) throws Exception
    {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        // 将流序列化成对象
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }

    // /**
    // * 查询主产品下默认或必选的附加产品
    // *
    // * @param productId
    // * @return
    // * @throws Exception
    // */
    // public static IDataset getDefaultPlusProductByProdId(String productId, String eparchyCode) throws Exception
    // {
    //
    // IDataset tmp = null;
    // // 从缓存中获取
    // // EHCache cache = EHCacheManager.getInstance().getCache("PLUS_PRODUCT");
    // // String cache_key = productId + "_" + eparchyCode;
    // // if(cache != null){
    // // ICacheElement element = cache.get(cache_key);
    // // if(element != null) {
    // // log.debug("******************getPlusProductByProdId get cache!
    // // productId=" + productId + " eparchyCode=" + eparchyCode);
    // // tmp = (IDataset)element.getValue();
    // // }
    // // }
    // //
    // // if (tmp == null){
    // // IData temp = new DataMap();
    // // temp.put("EPARCHY_CODE", eparchyCode);
    // // temp.put("PRODUCT_ID", productId);
    // // temp.put("TRADE_STAFF_ID", AppUtil.getStaffId());
    // // temp.put("TRADE_EPARCHY_CODE", eparchyCode);
    // //
    // // ProductInfoQry bean = new ProductInfoQry();
    // // tmp = bean.getPlusProductByProdId( temp);
    // // tmp = (tmp==null||tmp.size()==0)?new DatasetList():tmp;
    // //
    // // if(cache != null && tmp!=null && tmp.size()>0) cache.put(cache_key,
    // // tmp);
    // // }
    // //
    // // IDataset temp = (IDataset)deepCopy(tmp);
    // //
    // // temp = getPlusProductWithStaffPriv(temp);
    // IData temp = new DataMap();
    // temp.put("EPARCHY_CODE", eparchyCode);
    // temp.put("PRODUCT_ID", productId);
    // temp.put("TRADE_STAFF_ID", getVisit().getStaffId());
    // temp.put("TRADE_EPARCHY_CODE", eparchyCode);
    // ProductInfoQry bean = new ProductInfoQry();
    // tmp = bean.getDefaultPlusProductByProdId(temp);
    // tmp = (tmp == null || tmp.size() == 0) ? new DatasetList() : tmp;
    // IDataset tmp2 = seFilterEnableProduct(tmp);
    // return tmp2;
    // }
    //
    // public static IDataset getDiscntElementByPackage(String packageId, String eparchyCode) throws Exception
    // {
    //
    // return getDiscntElementByPackage(packageId, null, eparchyCode, true);
    // }

    // /**
    // * 根据package_id 查询包中的资费元素
    // *
    // * @param packageId
    // * @return
    // * @throws Exception
    // */
    // public static IDataset getDiscntElementByPackage(String packageId, String userId, String eparchyCode, boolean
    // privForEle) throws Exception
    // {
    //
    // // 从缓存中获取
    // // EHCache cache =
    // // EHCacheManager.getInstance().getCache("DISCNT_ELE_BY_PACK_ID");
    // // String cache_key = packageId + "_" + eparchyCode;
    // // IDataset elements=null;
    // // if(cache != null){
    // // ICacheElement element = cache.get(cache_key);
    // // if(element != null) {
    // // log.debug("******************getDiscntElementByPackage get cache!
    // // packageId=" + packageId + " eparchyCode" + eparchyCode);
    // // elements = (IDataset)element.getValue();
    // // }
    // // }
    // //
    // // if (elements==null){
    // // //查询数据
    // // ProductInfoQry bean = new ProductInfoQry();
    // // IData data = new DataMap();
    // // data.put("PACKAGE_ID", packageId);
    // // data.put("EPARCHY_CODE", eparchyCode);
    // // data.put("TRADE_STAFF_ID", AppUtil.getStaffId());
    // //
    // // elements = ProductInfoQry.getDiscntElementByPackage( data);
    // //
    // // if(cache != null && elements!=null && elements.size()>0)
    // // cache.put(cache_key, elements);
    // // }
    // //
    // // IDataset tmpElements = (IDataset)deepCopy(elements);
    // ProductInfoQry bean = new ProductInfoQry();
    // IData data = new DataMap();
    // data.put("PACKAGE_ID", packageId);
    // data.put("EPARCHY_CODE", eparchyCode);
    // data.put("TRADE_STAFF_ID", getVisit().getStaffId());
    // if (userId == null)
    // {
    // data.put("USER_ID", "0000000000000000");
    // }
    // else
    // {
    // data.put("USER_ID", userId);
    // }
    // IDataset elements = null;
    // if (privForEle)
    // {
    // elements = ProductInfoQry.getDiscntElementByPackage(data);
    // }
    // else
    // {
    // elements = bean.getDiscntElementByPackageNoPriv(data);
    // }
    // IDataset tmp2 = seFilterEnableElement(elements);
    // return tmp2;
    // }

    @SuppressWarnings("unchecked")
    public static IDataset getDiscntElementByPackageForSpec(String packageId, String userId, String eparchyCode, boolean privForEle) throws Exception
    {

        ProductInfoQry bean = new ProductInfoQry();
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        if (userId == null)
        {
            data.put("USER_ID", "0000000000000000");
        }
        else
        {
            data.put("USER_ID", userId);
        }
        IDataset elements = null;// bean.getDiscntElementByPackageForSpec(
        // data);
        IDataset tmp2 = seFilterEnableElement(elements);
        return tmp2;
    }

    // /**
    // * 根据SERVICE_ID查询产品类型信息
    // *
    // * @param elementId
    // * @return
    // * @throws Exception
    // */
    // public static IData getDiscntInfo(String elementId) throws Exception
    // {
    //
    // // 从缓存中获取
    // // EHCache cache = EHCacheManager.getInstance().getCache("TD_B_DISCNT");
    // /*
    // * hy j2ee if (cache != null) { ICacheElement element = cache.get(elementId); if (element != null) { //
    // * log.debug("******************getDiscntInfo get cache! // elementId=" + elementId); return (IData)
    // * deepCopy(element.getValue()); } }
    // */
    // // 查询数据
    // ProductInfoQry bean = new ProductInfoQry();
    // IData data = new DataMap();
    // data.put("DISCNT_CODE", elementId);
    // IData servInfo = bean.getDiscntInfo(data);
    // /*
    // * hy j2ee if (cache != null) cache.put(elementId, servInfo);
    // */
    // return (IData) deepCopy(servInfo);
    // }

    // /**
    // * 根据element_id查询元素依赖互斥关系的信息 暂只获取limit_tag=4的数据
    // *
    // * @param elementId
    // * @param eparchyCode
    // * @return
    // * @throws Exception
    // */
    // public static IData getElementLimitByEleId(String elementId, String elementTypeCode, String eparchyCode) throws
    // Exception
    // {
    //
    // IData data = null;
    // // 从缓存中获取
    // // EHCache cache = EHCacheManager.getInstance().getCache("TD_B_ELEMENT_LIMIT");
    // String cache_key = elementId + "_" + eparchyCode;
    // /*
    // * hy j2ee if (cache != null) { ICacheElement element = cache.get(cache_key); if (element != null) { //
    // * log.debug("******************getElementLimitByEleId get // cache!"); data = (IData) element.getValue(); } }
    // */
    // if (data == null)
    // {
    // IData temp = new DataMap();
    // temp.put("ELEMENT_ID_A", elementId);
    // temp.put("EPARCHY_CODE", eparchyCode);
    // temp.put("ELEMENT_TYPE_CODE_A", elementTypeCode);
    // ProductInfoQry bean = new ProductInfoQry();
    // data = bean.getElementLimitByEleId(temp);
    // data = data == null ? new DataMap() : data;
    // /*
    // * hy j2ee if (cache != null) cache.put(cache_key, data);
    // */
    // }
    // return (IData) deepCopy(data);
    // }

    public static IData getElementLimitTag2(String elementId, String elementTypeCode, String eparchyCode) throws Exception
    {

        IData param = new DataMap();
        param.put("ELEMENT_ID_A", elementId);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("ELEMENT_TYPE_CODE_A", elementTypeCode);
        param.put("ELEMENT_ID_B", "22");
        param.put("ELEMENT_TYPE_CODE_B", "S");

        // return elementLimitSet.size() > 0 ? elementLimitSet.getData(0) : null;

        CSAppException.apperr(CrmCommException.CRM_COMM_103, "不允许用DAO.ryxxx");

        return null;
    }

    public static IData getElementParam(String elementId, String elementType) throws Exception
    {

        return getElementParam(elementId, elementType, CSBizBean.getVisit().getStaffEparchyCode());
    }

    /**
     * 获取服务参数 第一次获取被载入
     * 
     * @param elementId
     * @return
     * @throws Exception
     */
    public static IData getElementParam(String elementId, String elementType, String eparchyCode) throws Exception
    {

        // 从缓存中获取
        // EHCache cache = EHCacheManager.getInstance().getCache("ELEMENT_PARAM");
        /*
         * hy j2ee if (cache != null) { ICacheElement element = cache.get(elementId + "|" + elementType); if (element !=
         * null) { // log.debug("******************getElementParam get cache! // elementId=" + elementId + "
         * elementType=" + elementType); return (IData) deepCopy(element.getValue()); } }
         */
        // 查询数据
        ProductInfoQry bean = new ProductInfoQry();
        IData data = new DataMap();
        data.put("ELEMENT_ID", elementId);
        data.put("ID_TYPE", elementType);
        data.put("EPARCHY_CODE", eparchyCode);
        // IDataset servParamInfo = bean.getServParam(data);
        IDataset elementParamAndOptions = bean.getElementParamAndOptions(elementType, elementId, null);
        IData tmp = new DataMap();
        // if (servParamInfo!=null && servParamInfo.size()>0){
        // tmp.put("SERV_PARAM", servParamInfo);
        // }
        if (elementParamAndOptions != null && elementParamAndOptions.size() > 0)
        {
            tmp.put("ELEMENT_PARAM_OPTIONS", elementParamAndOptions);
        }
        /*
         * hy j2ee if (cache != null) cache.put(elementId + "|" + elementType, tmp);
         */
        return (IData) deepCopy(tmp);
    }

    // /**
    // * 根据TRADE_TYPE_CODE,PRODUCT_ID,PACKAGE_ID,ELEMENT_ID,EPARCHY_CODE查询产品类型信息
    // *
    // * @param packageId
    // * @return
    // * @throws Exception
    // */
    // public static IDataset getFeeByEle(String typeTypeCode, String productId, String packageId, String elementId,
    // String eparchyCode) throws Exception
    // {
    //
    // IData fees = null;
    // if (isProvince(PROVINCE.XINJ))
    // {
    // IData temp = new DataMap();
    // temp.put("EPARCHY_CODE", eparchyCode);
    // temp.put("TRADE_TYPE_CODE", typeTypeCode);
    // temp.put("PRODUCT_ID", productId);
    // temp.put("PACKAGE_ID", packageId);
    // temp.put("ELEMENT_ID", elementId);
    // ProductInfoQry bean = new ProductInfoQry();
    // IDataset feeInfos = bean.getFeeByEparchyCode(eparchyCode);
    // fees = new DataMap();
    // for (int i = 0; i < feeInfos.size(); i++)
    // {
    // IData tmp = feeInfos.getData(i);
    // IData tmp3 = new DataMap();
    // IDataset tmp2 = fees.getDataset(tmp.getString("TRADE_TYPE_CODE") + "_" + tmp.getString("PRODUCT_ID") + "_" +
    // tmp.getString("PACKAGE_ID") + "_" + tmp.getString("ELEMENT_ID"));
    // tmp2 = tmp2 == null ? new DatasetList() : tmp2;
    // tmp3.put("FEE", tmp.getString("FEE"));
    // tmp3.put("FEE_MODE", tmp.getString("FEE_MODE"));
    // tmp3.put("FEE_TYPE_CODE", tmp.getString("FEE_TYPE_CODE"));
    // tmp3.put("RULE_BIZ_KIND_CODE", tmp.getString("RULE_BIZ_KIND_CODE"));
    // tmp3.put("VIP_CLASS_ID", tmp.getString("VIP_CLASS_ID"));
    // tmp2.add(tmp3);
    // fees.put(tmp.getString("TRADE_TYPE_CODE") + "_" + tmp.getString("PRODUCT_ID") + "_" + tmp.getString("PACKAGE_ID")
    // + "_" + tmp.getString("ELEMENT_ID"), tmp2);
    // }
    // }
    // else
    // {
    // // 从缓存中获取
    // // EHCache cache = EHCacheManager.getInstance().getCache("TD_B_PRODUCT_TRADEFEE");
    // String cache_key = eparchyCode;
    // /*
    // * hy j2ee if (cache != null) { ICacheElement element = cache.get(cache_key); if (element != null) { //
    // * log.debug("******************getFeeByEle get cache!"); fees = (IData) element.getValue(); } }
    // */
    // if (fees == null)
    // {
    // ProductInfoQry bean = new ProductInfoQry();
    // IDataset feeInfos = bean.getFeeByEparchyCode(eparchyCode);
    // fees = new DataMap();
    // for (int i = 0; i < feeInfos.size(); i++)
    // {
    // IData tmp = feeInfos.getData(i);
    // IData tmp3 = new DataMap();
    // IDataset tmp2 = fees.getDataset(tmp.getString("TRADE_TYPE_CODE") + "_" + tmp.getString("PRODUCT_ID") + "_" +
    // tmp.getString("PACKAGE_ID") + "_" + tmp.getString("ELEMENT_ID"));
    // tmp2 = tmp2 == null ? new DatasetList() : tmp2;
    // tmp3.put("FEE", tmp.getString("FEE"));
    // tmp3.put("FEE_MODE", tmp.getString("FEE_MODE"));
    // tmp3.put("FEE_TYPE_CODE", tmp.getString("FEE_TYPE_CODE"));
    // tmp3.put("RULE_BIZ_KIND_CODE", tmp.getString("RULE_BIZ_KIND_CODE"));
    // tmp3.put("VIP_CLASS_ID", tmp.getString("VIP_CLASS_ID"));
    // tmp2.add(tmp3);
    // fees.put(tmp.getString("TRADE_TYPE_CODE") + "_" + tmp.getString("PRODUCT_ID") + "_" + tmp.getString("PACKAGE_ID")
    // + "_" + tmp.getString("ELEMENT_ID"), tmp2);
    // }
    // /*
    // * hy j2ee if (cache != null && feeInfos != null) cache.put(cache_key, fees);
    // */
    // }
    // }
    // IDataset result = fees.getDataset(typeTypeCode + "_" + productId + "_" + packageId + "_" + elementId);
    // result = result == null ? new DatasetList() : result;
    // for (int i = 0; i < result.size(); i++)
    // {
    // IData tradeFee = result.getData(i);
    // if (tradeFee.getString("RULE_BIZ_KIND_CODE") != null)
    // {
    // // 需要调用规则获取费用
    // IData iparam = new DataMap();
    // String tradeData = AppCtx.getAttribute("tradeData", "");
    // TradeData td = null;
    // if (tradeData == null || "".equals(tradeData))
    // {
    // td = new TradeData();
    // }
    // else
    // {
    // td = new TradeData(tradeData);
    // }
    // // iparam.put("VIP_CLASS_ID",
    // // tradeFee.getString("VIP_CLASS_ID",""));
    // if (td.getVipInfo() != null)
    // {
    // iparam.put("VIP_TYPE_CODE", td.getVipInfo().getString("VIP_TYPE_CODE", ""));
    // iparam.put("VIP_CLASS_ID", td.getVipInfo().getString("VIP_CLASS_ID", ""));
    // }
    // if (td.getUserInfo() != null)
    // {
    // iparam.put("OPEN_DATE", td.getUserInfo().getString("OPEN_DATE", ""));
    // iparam.put("BRAND_CODE", td.getUserInfo().getString("BRAND_CODE", ""));
    // iparam.put("PRODUCT_ID", td.getUserInfo().getString("PRODUCT_ID", ""));
    // iparam.put("USER_ID", td.getUserInfo().getString("USER_ID", ""));
    // }
    // iparam.put("RULE_BIZ_KIND_CODE", tradeFee.getString("RULE_BIZ_KIND_CODE", ""));
    // iparam.put("TRADE_DATA", td);
    // IDataset feelist = TradeFeeMgr.TradeOperFee(iparam);
    // if (feelist != null && feelist.size() > 0)
    // {
    // IData fee = feelist.getData(0);
    // tradeFee.put("FEE", fee.get("FEE"));
    // }
    // }
    // }
    // return (IDataset) deepCopy(result);
    // }

    public static IData getGprsSvc(String userId) throws Exception
    {

        IDataset gprsDataset = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(userId, "22");
        return gprsDataset.size() > 0 ? gprsDataset.getData(0) : null;
    }

    // // 属性默认值自动匹配
    // public static void getMatchItemValue(IDataset ids, String match) throws Exception
    // {
    //
    // if (ids == null)
    // {
    // return;
    // }
    // String strMatchFlag = "";
    // String strMatchAttrCode = "";
    // String strMatchAttrValue = "";
    // IData idsMatch = new DataMap();
    // // 设置匹配规则参数值
    // if ("initCrtMb".equals(match))
    // {
    // idsMatch.put("USER_ID_B", AppCtx.getAttribute("MEM_USER_ID", "")); // 成员用户标识
    // idsMatch.put("PRODUCT_ID", AppCtx.getAttribute("PRODUCT_ID", "")); // 集团产品ID
    // idsMatch.put("USER_ID", AppCtx.getAttribute("USER_ID", "")); // 集团用户标识
    // }
    // // 循环取下面的所有itema
    // for (int row = 0; row < ids.size(); row++)
    // {
    // IData data = ids.getData(row);
    // // 取自动匹配标记
    // strMatchFlag = data.getString("RSRV_STR1", "");
    // // 是否自动匹配，1自动匹配
    // if ("1".equals(strMatchFlag))
    // {
    // strMatchAttrCode = data.getString("ATTR_CODE", "");
    // idsMatch.put("MATCH_ATTR_CODE", strMatchAttrCode);
    // // call 规则进行费用匹配
    // SaleActiveMgr.getFixFeeMatch(idsMatch);
    // // 取匹配后的费用
    // strMatchAttrValue = idsMatch.getString("MATCH_ATTR_INIT_VALUE", "");
    // // 是否已匹配
    // if (!"".equals(strMatchAttrValue) && !"-1".equals(strMatchAttrValue))
    // {
    // // 如果匹配到，将默认值替换为匹配值
    // data.put("ATTR_INIT_VALUE", strMatchAttrValue);
    // }
    // }
    // }
    // }

    /**
     * 查询集团产品下的成员附加产品
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getMebProduct(String productId) throws Exception
    {
        return ProductInfoQry.getMebProduct(productId);
        // IDataset tmp = seFilterEnableProduct(datas);
    }

    /**
     * 查询集团定制的成员附加产品
     * 
     * @param productId
     * @param userId
     *            集团USER_ID
     * @return
     * @throws Exception
     */
    public static IDataset getMemProductsByProdId(String productId, String userId, String eparchyCode) throws Exception
    {

        IData temp = new DataMap();
        temp.put("EPARCHY_CODE", eparchyCode);
        temp.put("PRODUCT_ID", productId);
        temp.put("USER_ID", userId);
        temp.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        ProductInfoQry bean = new ProductInfoQry();
        IDataset datas = bean.getMemProductsByProdId(productId, userId);
        // datas = getPlusProductWithStaffPriv(datas);
        IDataset tmp2 = seFilterEnableProduct(datas);
        return tmp2;
    }

    /**
     * 根据package_id 查询包信息
     * 
     * @param packageId
     * @return
     * @throws Exception
     */
    public static IData getPackageByPK(String packageId) throws Exception
    {

        // 从缓存中获取
        // EHCache cache = EHCacheManager.getInstance().getCache("TD_B_PACKAGE_BY_PK");
        /*
         * if (cache != null) { ICacheElement element = cache.get(packageId); if (element != null) { //
         * log.debug("******************getPackageByPK get cache! // packageId=" + packageId); return (IData)
         * deepCopy(element.getValue()); } }
         */
        IData temp = new DataMap();
        temp.put("PACKAGE_ID", packageId);
        ProductInfoQry bean = new ProductInfoQry();
        IData packageInfo = PkgInfoQry.getPackageByPK(packageId);
        /*
         * if (cache != null && packageInfo != null) cache.put(packageId, packageInfo);
         */
        return (IData) deepCopy(packageInfo);
    }

    /**
     * 查询产品、产品包的关联关系,包括过时的包配置
     * 
     * @param productId
     * @return
     * @throws Exception
     * @author zhouquan
     */
    public static IDataset getPackageByProduct(String productId, String eparchyCode) throws Exception
    {

        return getPackageByProduct(productId, null, eparchyCode, true);
    }

    /**
     * 查询产品模型中产品中的包
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getPackageByProduct(String productId, String userId, String eparchyCode, boolean privForPack) throws Exception
    {

        // 从缓存中获取
        // EHCache cache =
        // EHCacheManager.getInstance().getCache("TD_B_PACKAGE_BY_PRODUCT_ID");
        // String cache_key = productId + "_" + eparchyCode;
        //		
        // IDataset packages = null;
        //		
        // if(cache != null){
        // ICacheElement element = cache.get(cache_key);
        // if(element != null) {
        // log.debug("******************getPackageByProduct get cache!
        // productId=" + productId + " eparchyCode=" + eparchyCode);
        // packages = (IDataset)element.getValue();
        // }
        // }
        //		
        // if (packages==null){
        // IData temp = new DataMap();
        // temp.put("EPARCHY_CODE", eparchyCode);
        // temp.put("PRODUCT_ID", productId);
        //			
        // ProductInfoQry bean = new ProductInfoQry();
        // packages = bean.getPackageByProduct( temp);
        //			
        // if(cache != null && packages!=null && packages.size()>0)
        // cache.put(cache_key, packages);
        // }
        //		
        // IDataset datas = (IDataset)deepCopy(packages);
        // datas = getPackageWithStaffPriv(datas);
        IData temp = new DataMap();
        temp.put("EPARCHY_CODE", eparchyCode);
        temp.put("TRADE_EPARCHY_CODE", eparchyCode);
        temp.put("PRODUCT_ID", productId);
        temp.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        if (userId == null)
        {
            temp.put("USER_ID", "0000000000000000");
        }
        else
        {
            temp.put("USER_ID", userId);
        }
        ProductInfoQry bean = new ProductInfoQry();
        IDataset packages = null;
        if (privForPack)
        {
            packages = bean.getPackageByProduct(productId, temp.getString("USER_ID"), eparchyCode);

            // 增加权限 add by zhouwu
            PackagePrivUtil.filterPackageListByPriv(CSBizBean.getVisit().getStaffId(), packages);
        }
        else
        {
            packages = bean.getPackageByProductNoPriv(productId, temp.getString("USER_ID"), eparchyCode);
        }
        // IDataset tmp2 = seFilterEnablePackage(packages);
        return packages;
    }

    /**
     * 查询集团定制的成员附加产品中定制的包
     * 
     * @param productId
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getPackageByProductFromGrpPck(String productId, String userId) throws Exception
    {

        IData temp = new DataMap();
        temp.put("PRODUCT_ID", productId);
        temp.put("USER_ID", userId);
        temp.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        ProductInfoQry bean = new ProductInfoQry();
        IDataset datas = bean.getPackageByProductFromGrpPck(productId, userId);
        // datas = getPackageWithStaffPriv(datas);
        IDataset tmp2 = seFilterEnablePackage(datas);
        return tmp2;
    }

    /**
     * 查询用户已选择的包
     * 
     * @return
     * @throws Exception
     */
    public static IDataset getPackagesByUserProd(String productId, String userId) throws Exception
    {

        ProductInfoQry bean = new ProductInfoQry();

        return bean.getPackagesByUserProd(productId, userId);
    }

    public static IDataset getPlatSvcByPackage(String packageId, String eparchyCode) throws Exception
    {

        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("EPARCHY_CODE", eparchyCode);
        CSAppException.apperr(CrmCommException.CRM_COMM_103, "不允许用DAO.ryxxx");

        return null;
    }

    public static IDataset getPlatSvcElementByPackage(String packageId, String eparchyCode) throws Exception
    {

        return getPlatSvcElementByPackage(packageId, null, eparchyCode);
    }

    /**
     * 根据package_id 查询包中的平台服务元素
     * 
     * @param packageId
     * @return
     * @throws Exception
     */
    public static IDataset getPlatSvcElementByPackage(String packageId, String userId, String eparchyCode) throws Exception
    {

        // 从缓存中获取
        // EHCache cache =
        // EHCacheManager.getInstance().getCache("PLATSVC_ELE_BY_PACK_ID");
        // String cache_key = packageId ;
        // if(cache != null){
        // ICacheElement element = cache.get(cache_key);
        // if(element != null) {
        // log.debug("******************getPlatSvcElementByPackage get cache!
        // packageId=" + packageId);
        // return (IDataset)deepCopy(element.getValue());
        // }
        // }
        //		
        // //查询数据
        // ProductInfoQry bean = new ProductInfoQry();
        // IData data = new DataMap();
        // data.put("PACKAGE_ID", packageId);
        // data.put("EPARCHY_CODE",eparchyCode);
        //		
        // IDataset elements = bean.getPlatSvcElementByPackage( data);
        //		
        // if(cache != null && elements!=null && elements.size()>0)
        // cache.put(cache_key, elements);
        ProductInfoQry bean = new ProductInfoQry();

        IDataset elements = bean.getPlatSvcElementByPackage(packageId);
        return elements;
        // return (IDataset)deepCopy(elements);
    }

    /**
     * 查询主产品下关联的附加产品
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getPlusProductByProdId(String productId, String eparchyCode) throws Exception
    {

        IDataset tmp = null;
        // 从缓存中获取
        // EHCache cache = EHCacheManager.getInstance().getCache("PLUS_PRODUCT");
        // String cache_key = productId + "_" + eparchyCode;
        // if(cache != null){
        // ICacheElement element = cache.get(cache_key);
        // if(element != null) {
        // log.debug("******************getPlusProductByProdId get cache!
        // productId=" + productId + " eparchyCode=" + eparchyCode);
        // tmp = (IDataset)element.getValue();
        // }
        // }
        //		
        // if (tmp == null){
        // IData temp = new DataMap();
        // temp.put("EPARCHY_CODE", eparchyCode);
        // temp.put("PRODUCT_ID", productId);
        // temp.put("TRADE_STAFF_ID", AppUtil.getStaffId());
        // temp.put("TRADE_EPARCHY_CODE", eparchyCode);
        //			
        // ProductInfoQry bean = new ProductInfoQry();
        // tmp = bean.getPlusProductByProdId( temp);
        // tmp = (tmp==null||tmp.size()==0)?new DatasetList():tmp;
        //			
        // if(cache != null && tmp!=null && tmp.size()>0) cache.put(cache_key,
        // tmp);
        // }
        //		
        // IDataset temp = (IDataset)deepCopy(tmp);
        //		
        // temp = getPlusProductWithStaffPriv(temp);
        IData temp = new DataMap();
        temp.put("EPARCHY_CODE", eparchyCode);
        temp.put("PRODUCT_ID", productId);
        temp.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        temp.put("TRADE_EPARCHY_CODE", eparchyCode);
        ProductInfoQry bean = new ProductInfoQry();
        tmp = bean.getPlusProductByProdId(eparchyCode, productId);
        tmp = (tmp == null || tmp.size() == 0) ? new DatasetList() : tmp;
        IDataset tmp2 = seFilterEnableProduct(tmp);
        return tmp2;
    }

    /**
     * 查询产品信息
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData getProductInfo(String productId) throws Exception
    {

        // 从缓存中获取
        // EHCache cache = EHCacheManager.getInstance().getCache("TD_B_PRODUCT_BY_PK");
        /*
         * if (cache != null) { ICacheElement element = cache.get(productId); if (element != null) { //
         * log.debug("******************getProductInfo get cache! // productId=" + productId); return (IData)
         * deepCopy(element.getValue()); } }
         */
        IData temp = new DataMap();
        temp.put("PRODUCT_ID", productId);
        ProductInfoQry bean = new ProductInfoQry();
        IData productInfo = UProductInfoQry.qryProductByPK(productId);
        /*
         * if (cache != null && productInfo != null) cache.put(productId, productInfo);
         */
        return (IData) deepCopy(productInfo);
    }

    /**
     * 查询产品、产品包的关联关系
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData getProductPackageRel(String productId, String packageId, String eparchyCode, boolean noPriv) throws Exception
    {

        // 从缓存中获取
        // EHCache cache =
        // EHCacheManager.getInstance().getCache("TD_B_PRODUCT_PACKAGE");
        // String cache_key = productId + "_" + packageId + "_" + eparchyCode;
        // if(cache != null){
        // ICacheElement element = cache.get(cache_key);
        // if(element != null) {
        // log.debug("******************getProductPackageRel get cache!
        // productId=" + productId + " packageId=" + packageId + " eparchyCode="
        // + eparchyCode);
        // return (IData)deepCopy(element.getValue());
        // }
        // }
        IData temp = new DataMap();
        temp.put("EPARCHY_CODE", eparchyCode);
        temp.put("PRODUCT_ID", productId);
        temp.put("PACKAGE_ID", packageId);
        temp.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        ProductInfoQry bean = new ProductInfoQry();
        IData info = null;
        if (noPriv)
        {
            info = bean.getProductPackageRelNoPriv(packageId, productId, eparchyCode);
        }
        else
        {
            info = bean.getProductPackageRel(packageId, productId, eparchyCode);
        }
        // if(cache != null && info!=null) cache.put(productId, info);
        // return (IData)deepCopy(info);
        return info;
    }

    /**
     * 网上售卡根据产品类型查询产品信息For person
     * 
     * @param productTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeForForNetSaleCardPerson(String productTypeCode, String eparchyCode, boolean noPriv) throws Exception
    {

        IDataset tmp = null;
        ProductInfoQry bean = new ProductInfoQry();
        if (noPriv)
        {
            tmp = bean.getProductsByTypeForNetSaleCardPersonNoPriv(productTypeCode, eparchyCode);
        }
        else
        {
            tmp = bean.getProductsByTypeForNetSaleCardPerson(productTypeCode, eparchyCode);
        }
        IDataset tmp2 = seFilterEnableProduct(tmp);
        return tmp2;
    }

    /**
     * 根据产品类型查询产品信息 For group
     * 
     * @param productTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeForGroup(String productTypeCode, String eparchyCode) throws Exception
    {

        IDataset tmp = null;
        // 从缓存中获取
        // EHCache cache =
        // EHCacheManager.getInstance().getCache("TD_S_PRODUCT_BY_PRODUCT_TYPE_CODE_FOR_GROUP");
        // String cache_key = productTypeCode + "_" + eparchyCode;
        // if(cache != null){
        // ICacheElement element = cache.get(cache_key);
        // if(element != null) {
        // log.debug("******************getProductsByTypeForGroup get cache!
        // productTypeCode=" + productTypeCode);
        // tmp = (IDataset)deepCopy(element.getValue());
        // }
        // }
        //		
        // if (tmp == null){
        // IData temp = new DataMap();
        // temp.put("PRODUCT_TYPE_CODE", productTypeCode);
        // temp.put("TRADE_EPARCHY_CODE", eparchyCode);
        // temp.put("TRADE_STAFF_ID", AppUtil.getStaffId());
        // ProductInfoQry bean = new ProductInfoQry();
        // tmp = bean.getProductsByTypeForGroup( temp,null);
        //			
        // if(cache != null && tmp!=null && tmp.size()>0) cache.put(cache_key,
        // tmp);
        // }
        //		
        // IDataset temp = (IDataset)deepCopy(tmp);
        // temp = getProductWithStaffPriv(temp);\
        ProductInfoQry bean = new ProductInfoQry();
        tmp = bean.getProductsByTypeForGroup(productTypeCode, eparchyCode, null);
        IDataset tmp2 = seFilterEnableProduct(tmp);
        return tmp2;
    }

    /**
     * 根据产品类型查询产品信息 For person
     * 
     * @param productTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeForPerson(String productTypeCode, String eparchyCode, boolean noPriv) throws Exception
    {

        IDataset tmp = null;
        // 从缓存中获取
        // EHCache cache =
        // EHCacheManager.getInstance().getCache("TD_S_PRODUCT_BY_PRODUCT_TYPE_CODE_FOR_PERSON");
        // String cache_key = productTypeCode + "_" + eparchyCode;
        // if(cache != null){
        // ICacheElement element = cache.get(cache_key);
        // if(element != null) {
        // log.debug("******************getProductsByTypeForPerson get cache!
        // productTypeCode=" + productTypeCode);
        // tmp = (IDataset)element.getValue();
        // }
        // }
        //		
        // if (tmp == null){
        // IData temp = new DataMap();
        // temp.put("PRODUCT_TYPE_CODE", productTypeCode);
        // temp.put("TRADE_EPARCHY_CODE", eparchyCode);
        // temp.put("TRADE_STAFF_ID", AppUtil.getStaffId());
        // ProductInfoQry bean = new ProductInfoQry();
        // tmp = bean.getProductsByTypeForPerson( temp);
        //			
        // if(cache != null && tmp!=null && tmp.size()>0) cache.put(cache_key,
        // tmp);
        // }
        //		
        // IDataset temp = (IDataset)deepCopy(tmp);
        // temp = getProductWithStaffPriv(temp);
        ProductInfoQry bean = new ProductInfoQry();
        if (noPriv)
        {
            tmp = bean.getProductsByTypeForPersonNoPriv(productTypeCode, eparchyCode);
        }
        else
        {
            tmp = bean.getProductsByTypeForPerson(productTypeCode, eparchyCode);
        }
        IDataset tmp2 = seFilterEnableProduct(tmp);
        return tmp2;
    }

    /**
     * 网上售卡在产品变更时 查询某个产品类型下有那些可供转换的产品
     * 
     * @param productTypeCode
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeWithNetSaleCardTrans(String productTypeCode, String productId, String eparchyCode, boolean noPriv) throws Exception
    {

        IDataset tmp = null;
        IData temp = new DataMap();
        temp.put("PRODUCT_TYPE_CODE", productTypeCode);
        temp.put("PRODUCT_ID", productId);
        temp.put("EPARCHY_CODE", eparchyCode);
        temp.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        ProductInfoQry bean = new ProductInfoQry();
        if (noPriv)
        {
            tmp = bean.getProductsByTypeWithNetSaleCardTransNoPriv(productId, productTypeCode, eparchyCode);
        }
        else
        {
            tmp = bean.getProductsByTypeWithNetSaleCardTrans(productId, productTypeCode, eparchyCode);
        }
        IDataset tmp2 = seFilterEnableProduct(tmp);
        return tmp2;
    }

    /**
     * 在产品变更时 查询某个产品类型下有那些可供转换的产品
     * 
     * @param productTypeCode
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeWithTrans(String productTypeCode, String productId, String eparchyCode, boolean noPriv) throws Exception
    {

        IDataset tmp = null;
        // 从缓存中获取
        // EHCache cache =
        // EHCacheManager.getInstance().getCache("TD_S_PRODUCT_BY_PRODUCT_TYPE_CODE_WITH_TRANS");
        // String cache_key = productTypeCode + "_" + productId + "_" +
        // eparchyCode;
        // if(cache != null){
        // ICacheElement element = cache.get(cache_key);
        // if(element != null) {
        // log.debug("******************getProductsByTypeWithTrans get cache!
        // productTypeCode=" + productTypeCode + " productId=" + productId + "
        // eparchyCode=" + eparchyCode);
        // tmp = (IDataset)deepCopy(element.getValue());
        // }
        // }
        //
        // if (tmp == null){
        //		
        // IData temp = new DataMap();
        // temp.put("PRODUCT_TYPE_CODE", productTypeCode);
        // temp.put("PRODUCT_ID", productId);
        // temp.put("EPARCHY_CODE", eparchyCode);
        // temp.put("TRADE_STAFF_ID", AppUtil.getStaffId());
        // ProductInfoQry bean = new ProductInfoQry();
        // tmp = bean.getProductsByTypeWithTrans( temp);
        //			
        // if(cache != null && tmp!=null && tmp.size()>0) cache.put(cache_key,
        // tmp);
        // }
        //		
        // IDataset temp = (IDataset)deepCopy(tmp);
        // temp = getProductWithStaffPriv(temp);

        ProductInfoQry bean = new ProductInfoQry();
        if (noPriv)
        {
            tmp = bean.getProductsByTypeWithTransNoPriv(productId, productTypeCode, eparchyCode);
        }
        else
        {
            tmp = bean.getProductsByTypeWithTrans(productId, productTypeCode, eparchyCode);
        }
        IDataset tmp2 = seFilterEnableProduct(tmp);
        return tmp2;
    }

    @SuppressWarnings("unchecked")
    public static IDataset getProductsForSpec(String productTypeCode, String productId, String eparchyCode, boolean privForProd) throws Exception
    {

        IData temp = new DataMap();
        temp.put("PRODUCT_TYPE_CODE", productTypeCode);
        temp.put("PRODUCT_ID", productId);
        temp.put("EPARCHY_CODE", eparchyCode);
        temp.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        ProductInfoQry bean = new ProductInfoQry();
        return bean.getProductsByType(productTypeCode);
    }

    /**
     * 查询产品类型
     * 
     * @param productTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsType(String productTypeCode) throws Exception
    {

        // 从缓存中获取
        // EHCache cache = EHCacheManager.getInstance().getCache("TD_S_PRODUCT_TYPE_BY_PARENT");
        /*
         * if (cache != null) { ICacheElement element = cache.get(productTypeCode); if (element != null) { //
         * log.debug("******************getProductsType get cache! // productTypeCode=" + productTypeCode); return
         * (IDataset) deepCopy(element.getValue()); } }
         */
        ProductInfoQry bean = new ProductInfoQry();
        IDataset tmp = bean.getProductsType(productTypeCode, null);
        tmp = tmp == null ? new DatasetList() : tmp;
        /*
         * if (cache != null) { cache.put(productTypeCode, tmp); }
         */
        return (IDataset) deepCopy(tmp);
    }

    /**
     * 根据PRODUCT_TYPE_CODE查询产品类型信息
     * 
     * @param productTypeCode
     * @return
     * @throws Exception
     */
    public static IData getProductTypeByCode(String productTypeCode) throws Exception
    {

        // 从缓存中获取
        // EHCache cache = EHCacheManager.getInstance().getCache("TD_S_PRODUCT_TYPE");
        /*
         * if (cache != null) { ICacheElement element = cache.get(productTypeCode); if (element != null) { //
         * log.debug("******************getProductTypeByCode get cache! // productTypeCode=" + productTypeCode); return
         * (IData) deepCopy(element.getValue()); } }
         */
        ProductInfoQry bean = new ProductInfoQry();
        IData tmp = bean.getProductTypeByCode(productTypeCode);
        tmp = tmp == null ? new DataMap() : tmp;
        /*
         * if (cache != null) cache.put(productTypeCode, tmp);
         */
        return (IData) deepCopy(tmp);
    }

    public static IDataset getServElementByPackage(String packageId) throws Exception
    {

        return getServElementByPackage(packageId, null, true);
    }

    /**
     * 根据package_id 查询包中的服务元素
     * 
     * @param packageId
     * @return
     * @throws Exception
     */
    public static IDataset getServElementByPackage(String packageId, String userId, boolean privForEle) throws Exception
    {

        // 从缓存中获取
        // EHCache cache =
        // EHCacheManager.getInstance().getCache("SERV_ELE_BY_PACK_ID");
        // String cache_key = packageId ;
        // IDataset elements=null;
        // if(cache != null){
        // ICacheElement element = cache.get(cache_key);
        // if(element != null) {
        // log.debug("******************getServElementByPackage get cache!
        // packageId=" + packageId);
        // elements = (IDataset)element.getValue();
        // }
        // }
        //		
        // if (elements==null){
        // //查询数据
        // ProductInfoQry bean = new ProductInfoQry();
        // IData data = new DataMap();
        // data.put("PACKAGE_ID", packageId);
        //			
        // elements = ProductInfoQry.getServElementByPackage( data);
        //			
        // if(cache != null && elements!=null && elements.size()>0)
        // cache.put(cache_key, elements);
        // }
        //		
        // IDataset tmpElements = (IDataset)deepCopy(elements);
        ProductInfoQry bean = new ProductInfoQry();
        if (userId == null)
        {
            userId = "0000000000000000";
        }

        IDataset elements = null;
        if (privForEle)
        {
            elements = ProductInfoQry.getServElementByPackage(packageId, userId);

            // 增加权限 add by zhouwu
            SvcPrivUtil.filterSvcListByPriv(CSBizBean.getVisit().getStaffId(), elements);
        }
        else
        {
            elements = bean.getServElementByPackageNoPriv(packageId, userId);
        }
        // IDataset tmp2 = seFilterEnableElement(elements);
        return elements;
    }

    /**
     * 根据SERVICE_ID查询产品类型信息
     * 
     * @param elementId
     * @return
     * @throws Exception
     */
    public static IData getServInfo(String elementId) throws Exception
    {

        // 从缓存中获取
        // EHCache cache = EHCacheManager.getInstance().getCache("TD_B_SERVICE");
        /*
         * hy j2ee if (cache != null) { ICacheElement element = cache.get(elementId); if (element != null) { //
         * log.debug("******************getServInfo get cache! // elementId=" + elementId); return (IData)
         * deepCopy(element.getValue()); } }
         */
        // 查询数据
        IData servInfo = USvcInfoQry.qryServInfoBySvcId(elementId);
        /*
         * hy j2ee if (cache != null) cache.put(elementId, servInfo);
         */
        return (IData) deepCopy(servInfo);
    }

    /**
     * 获取用户服务信息, 必需要USER_ID,PRODUCT_ID,PACKAGE_ID,SERVICE_ID,不需要USER_ID_A,INST_ID
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getUserDiscntByPk(IData data) throws Exception
    {

        String product_id = data.getString("PRODUCT_ID");
        String user_id = data.getString("USER_ID");
        String package_id = data.getString("PACKAGE_ID");
        String discnt_code = data.getString("DISC");
        return ProductInfoQry.getUserDiscntByPk(product_id, user_id, package_id, discnt_code);
    }

    /**
     * 根据user_Id查询用户已订购的附加产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserPlusProductByUserId(String userId) throws Exception
    {

        ProductInfoQry bean = new ProductInfoQry();
        IDataset datas = bean.getUserPlusProductByUserId(userId);
        // datas = getPlusProductWithStaffPriv(datas);
        IDataset tmp2 = seFilterEnableProduct(datas);
        return tmp2;
    }

    /**
     * 根据user_Id查询用户已订购的主产品和附加产品 不包括营销活动产生的附加产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserProductByUserId(String userId) throws Exception
    {

        ProductInfoQry bean = new ProductInfoQry();
        return bean.getUserProductByUserId(userId, null);
    }

    /**
     * 获取用户服务信息, 必需要USER_ID,PRODUCT_ID,PACKAGE_ID,SERVICE_ID,不需要USER_ID_A,INST_ID
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getUserServByPk(IData data) throws Exception
    {

        String user_id = data.getString("USER_ID", "");
        String user_id_a = data.getString("USER_ID_A", "");
        String product_id = data.getString("PRODUCT_ID", "");
        String package_id = data.getString("PACKAGE_ID", "");
        String service_id = data.getString("SERVICE_ID", "");
        String inst_id = data.getString("INST_ID", "");
        return ProductInfoQry.getUserServByPk(user_id, user_id_a, product_id, package_id, service_id, inst_id);
    }

    /**
     * 根据package_id 查询包中的宽带资费元素
     * 
     * @param packageId
     * @return
     * @throws Exception
     * @author chenzm
     */
    public static IDataset getWideNetDiscntElementByPackage(String packageId, String userId, String eparchyCode, boolean privForEle) throws Exception
    {

        ProductInfoQry bean = new ProductInfoQry();

        if (userId == null)
        {
            userId = "0000000000000000";
        }
        IDataset elements = null;
        if (privForEle)
        {
            elements = PkgElemInfoQry.getDiscntElementByPackage(packageId, userId);
        }
        else
        {
            // elements = bean.getWideNetDiscntElementByPackageNoPriv( data);
        }
        IDataset tmp2 = seFilterEnableElement(elements);
        return tmp2;
    }

    /**
     * 查询产品类型
     * 
     * @param productTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset getWidenetProductsType(String productTypeCode) throws Exception
    {

        // 从缓存中获取
        // EHCache cache = EHCacheManager.getInstance().getCache("TD_S_PRODUCT_SPEC_BY_PARENT");
        /*
         * if (cache != null) { ICacheElement element = cache.get(productTypeCode); if (element != null) { return
         * (IDataset) deepCopy(element.getValue()); } }
         */

        ProductInfoQry bean = new ProductInfoQry();
        IDataset tmp = bean.getWidenetProductsType(productTypeCode, null);
        tmp = tmp == null ? new DatasetList() : tmp;
        /*
         * if (cache != null) { cache.put(productTypeCode, tmp); }
         */
        return (IDataset) deepCopy(tmp);
    }

    /**
     * 根据PRODUCT_TYPE_CODE查询宽带产品类型信息
     * 
     * @param productTypeCode
     * @return
     * @throws Exception
     */
    public static IData getWidenetProductTypeByCode(String productTypeCode) throws Exception
    {

        // 从缓存中获取
        // EHCache cache = EHCacheManager.getInstance().getCache("TD_S_PRODUCT_SPEC");
        /*
         * if (cache != null) { ICacheElement element = cache.get(productTypeCode); if (element != null) {
         * log.debug("******************getProductTypeByCode get cache! productTypeCode=" + productTypeCode); return
         * (IData) deepCopy(element.getValue()); } }
         */

        ProductInfoQry bean = new ProductInfoQry();
        IData tmp = bean.getWidenetProductTypeByCode(productTypeCode);
        tmp = tmp == null ? new DataMap() : tmp;
        /*
         * if (cache != null) cache.put(productTypeCode, tmp);
         */
        return (IData) deepCopy(tmp);
    }

    /**
     * 根据两个不同的日期获取相差的年数
     * 
     * @param firstDate
     * @param secrondDate
     * @return
     * @throws Exception
     */
    public static int getYearsBetweenDate(Date firstDate, Date secrondDate) throws Exception
    {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDate);
        int firstDay = calendar.get(Calendar.YEAR);
        calendar.setTime(secrondDate);
        int secondDay = calendar.get(Calendar.YEAR);
        return secondDay - firstDay;
    }

    // 根据INST_ID分组用户属性
    public static IData groupUserAttrsByInstid(IDataset userAttrs) throws Exception
    {

        int count = userAttrs.size();
        if (count > 0)
        {
            DataHelper.sort(userAttrs, "INST_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        }
        String _instid = "";
        IData groupAttr = new DataMap();
        for (int i = 0; i < count; i++)
        {
            IData userAttr = userAttrs.getData(i);
            String instid = userAttr.getString("INST_ID");
            if (_instid.equals(instid))
            {
                IDataset attrs = groupAttr.getDataset(instid);
                IData attr = new DataMap();
                attr.put("ATTR_CODE", userAttr.getString("ATTR_CODE"));
                attr.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
                attrs.add(attr);
            }
            else
            {
                IDataset attrs = new DatasetList();
                IData attr = new DataMap();
                attr.put("ATTR_CODE", userAttr.getString("ATTR_CODE"));
                attr.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
                attrs.add(attr);
                groupAttr.put(instid, attrs);
            }
            _instid = instid;
        }
        return groupAttr;
    }

    // 根据包ID分组用户已有元素
    public static IDataset groupUserElementsByPackage(IDataset elements) throws Exception
    {

        // 在组织成以包为单位的数据前先按照产品ID，包ID排序
        DataHelper.sort(elements, "PRODUCT_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND, "PACKAGE_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        IDataset _userpackages = new DatasetList();
        IData _packages = new DataMap();
        String _productid = "";
        String _packageid = "";
        int count = elements.size();
        for (int i = 0; i < count; i++)
        {
            IData element = elements.getData(i);
            String productid = element.getString("PRODUCT_ID");
            String packageid = element.getString("PACKAGE_ID");
            String productmode = element.getString("PRODUCT_MODE");
            if (_productid.equals(productid) && _packageid.equals(packageid))
            {
                IDataset userpackage = _packages.getDataset("ELEMENTS");
                userpackage.add(element);
            }
            else
            {
                _packages = new DataMap();
                _packages.put("PRODUCT_ID", productid);
                _packages.put("PRODUCT_MODE", productmode);
                _packages.put("PACKAGE_ID", packageid);
                IDataset userpackage = new DatasetList();
                userpackage.add(element);
                _packages.put("ELEMENTS", userpackage);
                if (!_packages.isEmpty())
                {
                    _userpackages.add(_packages);
                }
                _productid = productid;
                _packageid = packageid;
            }
        }
        return _userpackages;
    }

    // 给用户元素建立索引
    public static IDataset indexUserElements(IDataset elements) throws Exception
    {

        DataHelper.sort(elements, "ELEMENT_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        int index = 1; // 元素索引
        String elementIdTag = ""; // 标记上一步处理的元素ID
        for (int j = 0; j < elements.size(); j++)
        {
            IData element = elements.getData(j);
            String elementId = element.getString("ELEMENT_ID");
            if (!elementId.equals(elementIdTag))
            {
                index = 1; // 一旦和上次处理的元素ID不等，则设为初始值
                element.put("ELEMENT_INDEX", index);
            }
            else
            {
                ++index; // 如果和上次处理的元素ID相等，则索引递增
                element.put("ELEMENT_INDEX", index);
            }
            elementIdTag = elementId; // 标记为刚处理的元素ID
        }
        return elements;
    }

    public static IDataset seFilterEnableElement(IDataset datas) throws Exception
    {
        IData tempData = createRuleData();
        tempData.put("ELEMENTS", datas);
        // SaleActiveMgr.seFilteerEnableElement(tempData);
        return tempData.getDataset("RESULT");
    }

    /**
     * 根据员工权限获取能看到的产品集
     */
    // public static IDataset getProductWithStaffPriv(IDataset
    // products) throws Exception{
    // if ("SUPERUSR".equals(AppUtil.getStaffId())) return products;
    //		
    // IDataset tmp2 = new DatasetList();
    //		
    // IData productPrivMap =
    // getVisit().getExpandData().getData("PRODUCT_PRIV");
    //		
    // for (int i = 0; i < products.size(); i++) {
    // IData product = products.getData(i);
    // if ( productPrivMap.containsKey(product.getString("PRODUCT_ID"))){
    // tmp2.add(product);
    // }
    //			
    // }
    //		
    // return tmp2;
    // // return products;
    // }
    /**
     * 根据员工权限获取能看到的附加产品集
     */
    // public static IDataset getPlusProductWithStaffPriv(IDataset
    // products) throws Exception{
    // if ("SUPERUSR".equals(AppUtil.getStaffId())) return products;
    //		
    // IDataset tmp2 = new DatasetList();
    //		
    // IData plusProductPrivMap =
    // getVisit().getExpandData().getData("PLUS_PRODUCT_PRIV");
    //		
    // for (int i = 0; i < products.size(); i++) {
    // IData product = products.getData(i);
    // if ( plusProductPrivMap.containsKey(product.getString("PRODUCT_ID"))){
    // tmp2.add(product);
    // }
    //			
    // }
    //		
    // return tmp2;
    // // return products;
    // }
    /**
     * 根据员工权限获取能看到的包
     */
    // public static IDataset getPackageWithStaffPriv(IDataset
    // packages) throws Exception{
    // if ("SUPERUSR".equals(AppUtil.getStaffId())) return packages;
    //		
    // IDataset tmp2 = new DatasetList();
    //		
    // IData packagePrivMap =
    // getVisit().getExpandData().getData("PACKAGE_PRIV");
    //		
    // for (int i = 0; i < packages.size(); i++) {
    // IData thepackage = packages.getData(i);
    // if ( packagePrivMap.containsKey(thepackage.getString("PACKAGE_ID"))){
    // tmp2.add(thepackage);
    // }
    //			
    // }
    //		
    // return tmp2;
    // }
    /**
     * 调用规则 获取可以选择的产品、包、元素
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset seFilterEnablePackage(IDataset datas) throws Exception
    {

        IData tempData = createRuleData();
        tempData.put("ELEMENTS", datas);
        // SaleActiveMgr.seFilterEnablePackage(tempData);
        return tempData.getDataset("RESULT");
    }

    public static IDataset seFilterEnableProduct(IDataset datas) throws Exception
    {

        IData tempData = createRuleData();
        tempData.put("ELEMENTS", datas);
        // SaleActiveMgr.seFilterEnableProduct(tempData);
        return tempData.getDataset("RESULT");
    }
}
