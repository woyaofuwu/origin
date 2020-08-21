package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;

public final class UProductElementInfoQry
{
    /**
     * @Title: getElementInfosByProductId
     * @Description: 根据产品ID查询其下面所有可订购元素
     * @param @param productId
     * @param @return
     * @param @throws Exception 设定文件
     * @return IDataset 返回类型
     * @throws
     */
    public static IDataset getElementInfosByProductId(String productId) throws Exception
    {
        
        return getElementInfosByProductIdAndElementTypeCode(productId, null);
    }

    /**
     * 查询产品下的可订购的特定类型元素列表
     * @param productId
     * @param elementTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset getElementInfosByProductIdAndElementTypeCode(String productId, String elementTypeCode) throws Exception
    {
        IData data = UpcCall.queryAllOffersByOfferId("P", productId, elementTypeCode, "2", "", BizRoute.getTradeEparchyCode());
        if (IDataUtil.isEmpty(data))
        {
            return null;
        }
        
        IDataset offerComRelList = data.getDataset("OFFER_COM_REL_LIST");
        IDataset offerJoinRelList = data.getDataset("OFFER_JOIN_REL_LIST");
        IDataset offerGroupRelList = data.getDataset("OFFER_GROUP_REL_LIST");

        IDataset resultList = new DatasetList();
        if (IDataUtil.isNotEmpty(offerComRelList))
        {
            for (int i = 0, size = offerComRelList.size(); i < size; i++)
            {
                IData offer = offerComRelList.getData(i);
                offer.put("MAIN_TAG", offer.getString("IS_MAIN"));
                offer.put("GROUP_ID", "0");
                offer.put("PACKAGE_ID", "0");
                offer.put("ELEMENT_ID", offer.getString("OFFER_CODE"));
                offer.put("ELEMENT_TYPE_CODE", offer.getString("OFFER_TYPE"));
                offer.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(offer.getString("SELECT_FLAG")));
                offer.put("DEFAULT_TAG", UpcConst.getDefaultTagForSelectFlag(offer.getString("SELECT_FLAG")));
                offer.put("ELEMENT_FORCE_TAG", offer.getString("FORCE_TAG"));
                offer.put("PACKAGE_FORCE_TAG", "1");// 构成是必选
                offer.put("PRODUCT_ID", productId);
                resultList.add(offer);
            }
        }

        if (IDataUtil.isNotEmpty(offerJoinRelList))
        {
            for (int i = 0, size = offerJoinRelList.size(); i < size; i++)
            {
                IData offer = offerJoinRelList.getData(i);
                offer.put("MAIN_TAG", "0");
                offer.put("GROUP_ID", "-1");
                offer.put("PACKAGE_ID", "-1");
                offer.put("ELEMENT_ID", offer.getString("OFFER_CODE"));
                offer.put("ELEMENT_TYPE_CODE", offer.getString("OFFER_TYPE"));
                offer.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(offer.getString("SELECT_FLAG")));
                offer.put("DEFAULT_TAG", UpcConst.getDefaultTagForSelectFlag(offer.getString("SELECT_FLAG")));
                offer.put("ELEMENT_FORCE_TAG", offer.getString("FORCE_TAG"));
                offer.put("PACKAGE_FORCE_TAG", "0");// 可选
                offer.put("PRODUCT_ID", productId);
                resultList.add(offer);
            }
        }

        if (IDataUtil.isNotEmpty(offerGroupRelList))
        {
            for (int i = 0, size = offerGroupRelList.size(); i < size; i++)
            {
                IData offerGroupRel = offerGroupRelList.getData(i);
                IDataset groupComRelList = offerGroupRel.getDataset("GROUP_COM_REL_LIST");

                if (ArrayUtil.isNotEmpty(groupComRelList))
                {
                    String packageForceTag = UpcConst.getForceTagForSelectFlag(offerGroupRel.getString("SELECT_FLAG"));
                    for (int j = 0, jsize = groupComRelList.size(); j < jsize; j++)
                    {
                        IData offer = groupComRelList.getData(j);

                        offer.put("MAIN_TAG", "0");
                        offer.put("PACKAGE_ID", offerGroupRel.getString("GROUP_ID"));
                        offer.put("ELEMENT_ID", offer.getString("OFFER_CODE"));
                        offer.put("ELEMENT_TYPE_CODE", offer.getString("OFFER_TYPE"));
                        offer.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(offer.getString("SELECT_FLAG")));
                        offer.put("DEFAULT_TAG", UpcConst.getDefaultTagForSelectFlag(offer.getString("SELECT_FLAG")));
                        offer.put("ELEMENT_FORCE_TAG", offer.getString("FORCE_TAG"));
                        offer.put("PACKAGE_FORCE_TAG", packageForceTag);// 取包的必选标记
                        offer.put("MIN_NUMBER", offerGroupRel.getString("MIN_NUM"));
                        offer.put("MAX_NUMBER", offerGroupRel.getString("MAX_NUM"));
                        offer.put("PRODUCT_ID", productId);
                        resultList.add(offer);
                    }
                }
            }
        }

        return resultList;
    }
    /**
     * 查询必选包下的必选元素信息
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset queryForceElementsByProductId(String productId) throws Exception
    {
        return  UpcCall.qryAtomOffersFromGroupByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "");
        
    }
    
    /**
     * 查询必选包下的必选服务和默认服务
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset queryForceSvcsByProductId(String productId) throws Exception
    {
        return  UpcCall.qryAtomOffersFromGroupByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, BofConst.ELEMENT_TYPE_CODE_SVC);
        
    }

    /**
     * 查询必选包下的必选资费和默认资费
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset queryForceDiscntsByProductId(String productId) throws Exception
    {
        return  UpcCall.qryAtomOffersFromGroupByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, BofConst.ELEMENT_TYPE_CODE_DISCNT);
        
    }

    /**
     * 查询必选包下的必选平台服务和默认平台服务
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset queryForcePlatSvcsByProductId(String productId) throws Exception
    {
        return  UpcCall.qryAtomOffersFromGroupByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);
        
    }

    /**
     * 根据产品ID和元素ID查询元素的生失效方式，归属（包，构成）,默认必选等信息
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset queryElementInfosByProductIdAndElementIdElemetnTypeCode(String productId, String elementId, String elementTypeCode) throws Exception
    {
        return  UpcCall.qryOfferByOfferIdRelOfferId(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT, elementId, elementTypeCode);
        
    }
    
    /**
     * 根据产品ID和元素ID查询元素的生失效方式，归属（包，构成）,默认必选等信息
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset queryElementInfosByProductIdAndElementIdElemetnTypeCode(String productId, String elementId, String elementTypeCode, String queryCha) throws Exception
    {
        return  UpcCall.qryOfferByOfferIdRelOfferId(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT, elementId, elementTypeCode, queryCha);
        
    }

    /**
     * 根据产品ID和元素ID查询元素的生失效方式，归属（包，构成）,默认必选等信息
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData queryElementInfoByProductIdAndElementIdElemetnTypeCode(String productId, String elementId, String elementTypeCode) throws Exception
    {
        IDataset results = UpcCall.qryOfferByOfferIdRelOfferId(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT, elementId, elementTypeCode);
        
        if (IDataUtil.isNotEmpty(results))
        {
            return results.first();
        }
        
        return null;
    }
  
    /**
     * 产品产品组下的元素信息（0 查询构成， -1 查询join）
     * @param productId
     * @param packageId
     * @return
     * @throws Exception
     */
    public static IDataset queryPackageElementsByProductIdPackageId(String productId,String packageId, String eparchyCode) throws Exception
    {
        IDataset result = new DatasetList();
        if(StringUtils.isEmpty(packageId))
        {
            return result;
        }
        
        if(packageId.equals("-1"))
        {
            if(StringUtils.isEmpty(productId))
            {
                return result;
            }
            else
            {
                IDataset elements = UpcCall.queryOfferJoinRelByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "2", eparchyCode);
                if(IDataUtil.isNotEmpty(elements))
                {
                    for(int i = 0,isize = elements.size(); i < isize; i++)
                    {
                        IData data = elements.getData(i);
                        IData element = new DataMap();
                        element.put("ELEMENT_ID", data.getString("OFFER_CODE"));
                        element.put("ELEMENT_TYPE_CODE", data.getString("OFFER_TYPE"));
                        element.put("ELEMENT_NAME", data.getString("OFFER_NAME"));
                        element.put("EXPLAIN", data.getString("DESCRIPTION"));
                        element.put("FORCE_TAG", "1");
                        element.put("DEFAULT_TAG", "1");
                        element.put("REORDER", "");
                        result.add(element);
                    }
                }
                return result;
            }
        }
        
        //查询构成
        if(packageId.equals("0"))
        {
            IDataset elements = UpcCall.queryOfferComRelOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, eparchyCode);
            if(IDataUtil.isNotEmpty(elements))
            {
                for(int i = 0,isize = elements.size(); i < isize; i++)
                {
                    IData data = elements.getData(i);
                    IData element = new DataMap();
                    element.put("ELEMENT_ID", data.getString("OFFER_CODE"));
                    element.put("ELEMENT_TYPE_CODE", data.getString("OFFER_TYPE"));
                    element.put("ELEMENT_NAME", data.getString("OFFER_NAME"));
                    element.put("EXPLAIN", data.getString("DESCRIPTION"));
                    element.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(data.getString("SELECT_FLAG")));
                    element.put("DEFAULT_TAG", UpcConst.getDefaultTagForSelectFlag(data.getString("SELECT_FLAG")));
                    element.put("REORDER", "");
                    result.add(element);
                }
            }
            return result;
        }
        
        return UPackageElementInfoQry.getPackageElementInfoByPackageId(packageId, eparchyCode);
    }
    
    public static IDataset queryPackageElementsByProductIdPackageId(String productId,String packageId) throws Exception
    {
        return queryPackageElementsByProductIdPackageId(productId, packageId, null);
    }
    
    /**
     * 
     * @Title: queryElementInfosByProductIdAndPackageIdAndElementId  
     * @Description: 根据产品id，包id和元素id查询元素信息 
     * @param @param productId
     * @param @param packageId
     * @param @param elementId
     * @param @param elementTypeCode
     * @param @param queryComCha 是否查备用字段
     * @param @return
     * @param @throws Exception    设定文件  
     * @return IData    返回类型  
     * @throws
     */
    public static IData queryElementInfoByProductIdAndPackageIdAndElementId(String productId, String packageId, String elementId, String elementTypeCode, String queryComCha) throws Exception
    {
        IData pkgelement = new DataMap();
        IDataset pkgelementset = null;
        if (StringUtils.isBlank(packageId) || StringUtils.equals("0", packageId) || StringUtils.equals("-1", packageId))
        {
            pkgelementset = UProductElementInfoQry.queryElementInfosByProductIdAndElementIdElemetnTypeCode(productId, elementId, elementTypeCode, queryComCha);
        }
        else 
        {
            pkgelementset = PkgElemInfoQry.getServElementByPk(packageId, elementTypeCode, elementId, queryComCha);
        }
        
        if (IDataUtil.isNotEmpty(pkgelementset))
        {
            pkgelement = pkgelementset.first();
        }
        
        return pkgelement;
    }
}
