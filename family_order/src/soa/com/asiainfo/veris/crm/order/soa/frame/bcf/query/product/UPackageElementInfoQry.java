
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.biz.cache.CrmCacheTablesCache;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;

public final class UPackageElementInfoQry
{

    /**
     * 查询包ID查包下的元素
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getPackageElementInfoByPackageId(String packageId, String eparchyCode) throws Exception
    {
        IDataset result = UpcCall.queryGroupComRelOfferByGroupId(packageId, eparchyCode);
        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0; i < result.size(); i++)
            {
                IData data = result.getData(i);
                data.put("ELEMENT_ID", data.getString("OFFER_CODE"));
                data.put("ELEMENT_TYPE_CODE", data.getString("OFFER_TYPE"));
                data.put("ELEMENT_NAME", data.getString("OFFER_NAME"));
                data.put("EXPLAIN", data.getString("DESCRIPTION"));
                data.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(data.getString("SELECT_FLAG")));
                data.put("DEFAULT_TAG", UpcConst.getDefaultTagForSelectFlag(data.getString("SELECT_FLAG")));
                data.put("REORDER", "");
            }
        }

        return result;
    }

    public static IDataset getPackageElementInfoByPackageId(String packageId) throws Exception
    {
        return getPackageElementInfoByPackageId(packageId, null);
    }

    /**
     * 查询包ID查包下的包信息元素
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getPackageElementByProductId(String product_id, String element_type_code, String element_id) throws Exception
    {
        IDataset result = UpcCall.getPackageElementInfoByPorductIdElementId(product_id, BofConst.ELEMENT_TYPE_CODE_PRODUCT, element_id, element_type_code);
        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0; i < result.size(); i++)
            {
                IData data = result.getData(i);
                data.put("PACKAGE_ID", data.getString("GROUP_ID"));
                data.put("ELEMENT_ID", data.getString("OFFER_CODE"));
                data.put("ELEMENT_NAME", data.getString("OFFER_NAME"));
                data.put("ELEMENT_TYPE_CODE", data.getString("OFFER_TYPE"));
            }
        }

        return result;
    }

    /**
     * @Description 根据产品编码、元素ID查询产品模型下【默认】优惠、服务
     * @author hefeng
     * @param offerCode
     * @param offerType
     * @param elementType
     * @return
     * @throws Exception
     */
    public static IDataset getPackageElementInfoByPorductIdElementId(String productId, String offer_type, String elementId, String element_type) throws Exception
    {
        IDataset result = UpcCall.getPackageElementInfoByPorductIdElementId(productId, offer_type, elementId, element_type);
        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0; i < result.size(); i++)
            {
                IData data = result.getData(i);
                data.put("PKG_FORCE_TAG", data.getString("FORCE_TAG"));
                data.put("ELEMENT_TYPE_CODE", data.getString("OFFER_TYPE"));
                data.put("ELEMENT_NAME", data.getString("OFFER_NAME"));
                data.put("MAIN_TAG", data.getString("IS_MAIN"));
            }
        }

        return result;
    }

    public static IDataset queryOfferForceElementsByProductId(String offerType, String offerCode, String elementType) throws Exception
    {
        IDataset results = UpcCall.qrySpecifyTypeOfferByOfferIdAndType(offerType, offerCode, elementType);
        String elementIdType = "OFFER_CODE";
        String offerNameType = "OFFER_NAME";
        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementType))
        {
            elementIdType = "DISCNT_CODE";
            offerNameType = "DISCNT_NAME";
        }
        else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementType))
        {
            elementIdType = "SERVICE_ID";
            offerNameType = "SERVICE_NAME";
        }
        if (IDataUtil.isNotEmpty(results))
        {
            for (int i = 0; i < results.size(); i++)
            {
                IData result = results.getData(i);
                result.put("PRODUCT_ID", offerCode);
                result.put(elementIdType, result.getString("OFFER_CODE", ""));
                result.put(offerNameType, result.getString("OFFER_NAME", ""));
                result.put("MAIN_TAG", result.getString("IS_MAIN", ""));
                result.put("ELEMENT_TYPE_CODE", elementType);
                if (StringUtils.isNotBlank(result.getString("GROUP_ID")))
                {
                    result.put("PACKAGE_ID", result.getString("GROUP_ID"));
                }
                else
                {
                    result.put("PACKAGE_ID", "-1");
                }
            }
        }
        return results;
    }

    /**
     * @param packageId
     * @param elementId
     * @param eType
     * @return
     * @throws Exception
     */
    public static IDataset getPackageElementInfoByPidEidEtype(String packageId, String eType, String elementId) throws Exception
    {
        return getPackageElementInfoByPidEidEtype(packageId, eType, elementId, null);
    }

    public static IDataset getPackageElementInfoByPidEidEtype(String packageId, String eType, String elementId, String queryCha) throws Exception
    {
        IDataset result = UpcCall.qryOfferFromGroupByGroupIdOfferId(packageId, eType, elementId, queryCha);
        return result;
    }

    public static IData queryElementEnableMode(String productId, String groupId, String elementId, String elementType) throws Exception
    {
        IData enableMode = UpcCall.queryOfferEnableMode(productId, groupId, elementId, elementType);
        if (IDataUtil.isEmpty(enableMode))
        {
            return null;
        }
        IData rst = new DataMap();
        rst.put("ENABLE_TAG", enableMode.getString("ENABLE_MODE"));
        rst.put("START_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_ENABLE_DATE"));
        rst.put("START_OFFSET", enableMode.getString("ENABLE_OFFSET"));
        rst.put("START_UNIT", enableMode.getString("ENABLE_UNIT"));
        rst.put("END_ENABLE_TAG", enableMode.getString("DISABLE_MODE"));
        rst.put("END_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_DISABLE_DATE"));
        rst.put("END_OFFSET", enableMode.getString("DISABLE_OFFSET"));
        rst.put("END_UNIT", enableMode.getString("DISABLE_UNIT"));
        rst.put("CANCEL_TAG", enableMode.getString("CANCEL_MODE"));
        rst.put("CANCEL_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_CANCEL_DATE"));
        rst.put("CANCEL_OFFSET", enableMode.getString("CANCEL_OFFSET"));
        rst.put("CANCEL_UNIT", enableMode.getString("CANCEL_UNIT"));
        return rst;
    }

    /**
     * 查询产品下的特定类型的可选优惠
     * 
     * @param offerType
     * @param offerCode
     * @param elementType
     * @return
     * @author hefeng
     * @throws Exception
     */
    public static IDataset queryPackageElementsByProductIdDisctypeCode(String productid, String discntTypeCode) throws Exception
    {
        IDataset result = UpcCall.queryPackageElementsByProductIdDisctypeCode(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productid, discntTypeCode);
        if (IDataUtil.isNotEmpty(result))
        {
            if (StringUtils.isEmpty(productid))
            {

                for (int i = 0; i < result.size(); i++)
                {
                    IData data = result.getData(i);
                    data.put("ELEMENT_TYPE_CODE", data.getString("OFFER_TYPE"));
                    data.put("DISCNT_NAME", data.getString("OFFER_NAME"));
                    data.put("DISCNT_CODE", data.getString("OFFER_CODE"));
                    data.put("DISCNT_EXPLAIN", data.getString("DESCRIPTION"));
                }
            }
        }

        return result;

    }

    /**
     * 根据包ID查询包下必选或默选元素信息（营销包）
     * 
     * @param packageId
     * @return
     * @throws Exception
     */
    public static IDataset queryForceDefaultElementByPackageId(String packageId, String forceTag, String defaultTag) throws Exception
    {
        IDataset result = null;

        // mc
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(CrmCacheTablesCache.class);
        String tabName = "";

        String versionOffer           ;
        String versionOfferComRel     ;
        String versionOfferJoinRel    ;
        String versionOfferGroupRel   ;
        String versionGroup           ;
        String versionGroupComRel     ;
        String versionExtCha          ;
        String versionEnableModeRel   ;
        String versionOfferEnableMode ;
        String versionOffeOfferGift   ;
        
        //
        tabName = "PM_OFFER";
        versionOffer = (String) cache.get(tabName);

        tabName = "PM_OFFER_COM_REL";
        versionOfferComRel = (String) cache.get(tabName);

        tabName = "PM_OFFER_JOIN_REL";
        versionOfferJoinRel = (String) cache.get(tabName);

        tabName = "PM_OFFER_GROUP_REL";
        versionOfferGroupRel = (String) cache.get(tabName);

        tabName = "PM_GROUP";
        versionGroup = (String) cache.get(tabName);

        tabName = "PM_GROUP_COM_REL";
        versionGroupComRel = (String) cache.get(tabName);

        tabName = "PM_EXT_CHA";
        versionExtCha = (String) cache.get(tabName);

        tabName = "PM_ENABLE_MODE_REL";
        versionEnableModeRel = (String) cache.get(tabName);

        tabName = "PM_ENABLE_MODE";
        versionOfferEnableMode = (String) cache.get(tabName);

        tabName = "PM_OFFER_GIFT";
        versionOffeOfferGift = (String) cache.get(tabName);

        StringBuilder sb = new StringBuilder(1000);

        sb.append("UPackageElementInfoQry.queryForceDefaultElementByPackageId").append(SysDateMgr.getSysDate("dd")).append("_").
        append(versionOffer).append("_").
        append(versionOfferComRel).append("_").
        append(versionOfferJoinRel).append("_").
        append(versionOfferGroupRel).append("_").
        append(versionGroup).append("_").
        append(versionGroupComRel).append("_").
        append(versionExtCha).append("_").
        append(versionEnableModeRel).append("_").
        append(versionOfferEnableMode).append("_").
        append(versionOffeOfferGift).append("_").
        append(packageId).append("_").
        append(forceTag).append("_").
        append(defaultTag);

        // get mc
        String cacheKey = sb.toString();

        IDataset resultCache = (IDataset) SharedCache.get(cacheKey);

        // if mc null
        if (!IDataUtil.isNull(resultCache))
        {
            // set ret
            result = resultCache;
        }
        else
        {
            result = new DatasetList();

            IDataset offers = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, forceTag, defaultTag);
            if (IDataUtil.isNotEmpty(offers))
            {
                for (int i = 0; i < offers.size(); i++)
                {
                    IData offer = offers.getData(i);
                    String offerType = offer.getString("OFFER_TYPE");
                    String offerCode = offer.getString("OFFER_CODE");
                    // 元素信息
                    offer.put("PACKAGE_ID", packageId);
                    offer.put("ELEMENT_TYPE_CODE", offer.getString("OFFER_TYPE"));
                    offer.put("ELEMENT_ID", offer.getString("OFFER_CODE"));
                    offer.put("ELEMENT_NAME", offer.getString("OFFER_NAME"));
                    offer.put("MODIFY_TAG", 0);

                    IData extChas = getPkgElemExtCha(offer.getString("REL_OBJECT_ID"), "TD_B_PACKAGE_ELEMENT", UpcConst.PM_OFFER_COM_REL_CHA_TYPE);
                    // IDataset extChas = UpcCall.qryRelOfferExtChaByOfferIdRelOfferId(packageId,
                    // BofConst.ELEMENT_TYPE_CODE_PACKAGE, offerCode, offerType);
                    if (IDataUtil.isNotEmpty(extChas))
                    {
                        offer.put("RSRV_STR1", extChas.getString("RSRV_STR1"));
                        offer.put("RSRV_TAG3", extChas.getString("RSRV_TAG3"));
                    }

                    // 生失效信息
                    String relObj = offer.getString("REL_OBJECT");
                    String relObjId = offer.getString("REL_OBJECT_ID");
                    IDataset enableInfos = UpcCall.qryEnableModeInfoByRelObjectAndId(relObj, relObjId);
                    IData enableInfo = enableInfos.getData(0);
                    offer.putAll(enableInfo);
                    offer.put("ENABLE_TAG", enableInfo.getString("ENABLE_MODE", ""));
                    offer.put("START_ABSOLUTE_DATE", enableInfo.getString("ABSOLUTE_ENABLE_DATE", ""));
                    offer.put("START_OFFSET", enableInfo.getString("ENABLE_OFFSET", ""));
                    offer.put("START_UNIT", enableInfo.getString("ENABLE_UNIT", ""));
                    offer.put("END_ENABLE_TAG", enableInfo.getString("DISABLE_MODE", ""));
                    offer.put("END_ABSOLUTE_DATE", enableInfo.getString("ABSOLUTE_DISABLE_DATE", ""));
                    offer.put("END_OFFSET", enableInfo.getString("DISABLE_OFFSET", ""));
                    offer.put("END_UNIT", enableInfo.getString("DISABLE_UNIT", ""));
                    offer.put("CANCEL_TAG", enableInfo.getString("CANCEL_MODE", ""));
                    offer.put("CANCEL_ABSOLUTE_DATE", enableInfo.getString("ABSOLUTE_CANCEL_DATE", ""));
                    offer.put("CANCEL_OFFSET", enableInfo.getString("CANCEL_OFFSET", ""));
                    offer.put("CANCEL_UNIT", enableInfo.getString("CANCEL_UNIT", ""));
                }
                
                result.addAll(offers);
            }

            // 直接根据OFFER_ID找PM_OFFER_GIFT
            IDataset offerGifts = UpcCall.qryOfferGiftsByOfferId(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
            for (Object obj : offerGifts)
            {
                IData giftData = (IData) obj;
                giftData.put("PACKAGE_ID", packageId);
                giftData.put("DEPOSIT_TYPE", giftData.getString("GIFT_TYPE"));
                giftData.put("MONTHS", giftData.getString("GIFT_CYCLE"));
                giftData.put("GIFT_USE_TAG", giftData.getString("GIFT_USE_TAG"));
                giftData.put("DISCNT_GIFT_ID", giftData.getString("EXT_GIFT_ID"));
                giftData.put("DISCNT_GIFT_NAME", giftData.getString("GIFT_NAME"));
                giftData.put("A_DISCNT_CODE", giftData.getString("GIFT_OBJ_ID"));
                giftData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT);
                giftData.put("ELEMENT_ID", giftData.getString("EXT_GIFT_ID"));
                giftData.put("ELEMENT_NAME", giftData.getString("GIFT_NAME"));
                giftData.put("DEFAULT_TAG", "1");
                giftData.put("FORCE_TAG", "1");
                giftData.put("MODIFY_TAG", 0);

                IData offerGiftCha = UpcCall.queryTempChaByCond(giftData.getString("REL_ID"), "TD_B_PACKAGE_ELEMENT");
                giftData.put("RSRV_STR1", offerGiftCha.getString("RSRV_STR1"));
                giftData.put("RSRV_TAG3", offerGiftCha.getString("RSRV_TAG3"));

                result.add(giftData);
            }
            
            // put mc
            SharedCache.set(cacheKey, result);
        }

        return result;
    }

    public static IDataset getCombineElementByGroupId(String groupId) throws Exception
    {
        IDataset pkgElements = UpcCall.queryGroupComRelByGroupId(groupId);

        return pkgElements;
    }

    /**
     * 查询特殊包下面的元素，不是营销活动
     * 
     * @author hefeng
     * @param groupId
     * @return
     * @throws Exception
     */
    public static IDataset getElementInfoByGroupId(String groupId) throws Exception
    {
        IDataset pkgElements = UpcCall.getElementInfoByGroupId(groupId);
        if (IDataUtil.isNotEmpty(pkgElements))
        {
            for (int i = 0; i < pkgElements.size(); i++)
            {
                IData data = pkgElements.getData(i);
                data.put("ELEMENT_TYPE_CODE", data.getString("OFFER_TYPE"));
                data.put("ELEMENT_NAME", data.getString("OFFER_NAME"));
                data.put("ELEMENT_ID", data.getString("OFFER_CODE"));
                data.put("ELEMENT_EXPLAIN", data.getString("OFFER_NAME"));
                data.put("START_DATE", data.getString("VALID_DATE"));
                data.put("END_DATE", data.getString("EXPIRE_DATE"));
                data.put("CANCEL_TAG", data.getString("CANCEL_MODE"));
                data.put("ENABLE_TAG", data.getString("ENABLE_MODE"));
                data.put("PACKAGE_ID", data.getString("GROUP_ID"));

            }
        }
        return pkgElements;
    }

    /**
     * @Title: querySaleElementEnableMode
     * @Description:
     * @param @param
     *            productId
     * @param @param
     *            groupId
     * @param @param
     *            elementId
     * @param @param
     *            elementType
     * @param @return
     * @param @throws
     *            Exception
     * @return IData
     * @throws @author
     *             longtian3
     */
    public static IData querySaleElementEnableMode(String packageId, String groupId, String elementId, String elementType) throws Exception
    {
        IData enableMode = UpcCall.querySaleOfferEnableMode(packageId, groupId, elementId, elementType);
        if (IDataUtil.isEmpty(enableMode))
        {
            return null;
        }
        IData rst = new DataMap();
        rst.put("ENABLE_TAG", enableMode.getString("ENABLE_MODE"));
        rst.put("START_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_ENABLE_DATE"));
        rst.put("START_OFFSET", enableMode.getString("ENABLE_OFFSET"));
        rst.put("START_UNIT", enableMode.getString("ENABLE_UNIT"));
        rst.put("END_ENABLE_TAG", enableMode.getString("DISABLE_MODE"));
        rst.put("END_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_DISABLE_DATE"));
        rst.put("END_OFFSET", enableMode.getString("DISABLE_OFFSET"));
        rst.put("END_UNIT", enableMode.getString("DISABLE_UNIT"));
        rst.put("CANCEL_TAG", enableMode.getString("CANCEL_MODE"));
        rst.put("CANCEL_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_CANCEL_DATE"));
        rst.put("CANCEL_OFFSET", enableMode.getString("CANCEL_OFFSET"));
        rst.put("CANCEL_UNIT", enableMode.getString("CANCEL_UNIT"));
        return rst;
    }

    /**
     * 根据营销包ID和元素ID查询元素的生失效方式，归属（包，构成）,默认必选等信息
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset queryElementInfoByPackageIdAndElementIdElemetnTypeCode(String packageId, String elementId, String elementTypeCode) throws Exception
    {
        return UpcCall.qryOfferByOfferIdRelOfferId(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE, elementId, elementTypeCode);

    }

    /**
     * @Title: qrySalePackagesByProdIdPkgIdAndElementTypeCode
     * @Description:
     * @param @param
     *            productId 营销产品ID
     * @param @param
     *            packageId 营销包ID
     * @param @param
     *            elementTypeCode 查询包下指定元素类型
     * @param @return
     * @param @throws
     *            Exception
     * @return IDataset
     * @throws @author
     *             longtian3
     */
    public static IDataset qrySalePackagesByProdIdPkgIdAndElementTypeCode(String productId, String packageId, String elementTypeCode) throws Exception
    {
        // 先查询产品下是否有包，没有就返回null
        IDataset offers = UpcCall.qryOffersByCatalogIdAndOfferId(productId, packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        if (IDataUtil.isEmpty(offers))
        {
            return null;
        }

        return SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, elementTypeCode);
    }

    public static IDataset queryElementInfosByElementData(IData element) throws Exception
    {
        IDataset pkgelementset = null;
        String packageId = element.getString("PACKAGE_ID", "");
        if (StringUtils.isBlank(packageId) || StringUtils.equals("0", packageId) || StringUtils.equals("-1", packageId))
        {
            pkgelementset = UProductElementInfoQry.queryElementInfosByProductIdAndElementIdElemetnTypeCode(element.getString("PRODUCT_ID", ""), element.getString("ELEMENT_ID", ""), element.getString("ELEMENT_TYPE_CODE", ""));
        }
        else
        {
            pkgelementset = PkgElemInfoQry.getServElementByPk(element.getString("PACKAGE_ID", ""), element.getString("ELEMENT_TYPE_CODE", ""), element.getString("ELEMENT_ID", ""));
        }

        return pkgelementset;
    }

    /**
     * @Title: getPackageElementExtChaInfoByPackageIdAndElementId @Description: 获取包元素关系表的备用字段 @param @param
     *         productId @param @return @param @throws Exception 设定文件 @return IData 返回类型 @throws
     */
    public static IData getPackageElementExtChaInfoByPackageIdAndElementId(String packageId, String elementId, String elementTypeCode) throws Exception
    {
        IDataset results = UpcCall.qryGroupComRelExtChaByGroupIdOfferId(packageId, elementId, elementTypeCode);
        if (IDataUtil.isEmpty(results))
        {
            return new DataMap();
        }
        return results.getData(0);
    }

    public static IData queryElementEnableModeByGroupId(String groupId, String elementId, String elementType) throws Exception
    {
        IDataset dataset = UpcCall.queryOfferEnableModeByGroupId(groupId, elementId, elementType);
        if (IDataUtil.isEmpty(dataset))
        {
            return null;
        }
        IData enableMode = dataset.getData(0);
        IData rst = new DataMap();
        rst.put("ENABLE_TAG", enableMode.getString("ENABLE_MODE"));
        rst.put("START_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_ENABLE_DATE"));
        rst.put("START_OFFSET", enableMode.getString("ENABLE_OFFSET"));
        rst.put("START_UNIT", enableMode.getString("ENABLE_UNIT"));
        rst.put("END_ENABLE_TAG", enableMode.getString("DISABLE_MODE"));
        rst.put("END_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_DISABLE_DATE"));
        rst.put("END_OFFSET", enableMode.getString("DISABLE_OFFSET"));
        rst.put("END_UNIT", enableMode.getString("DISABLE_UNIT"));
        rst.put("CANCEL_TAG", enableMode.getString("CANCEL_MODE"));
        rst.put("CANCEL_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_CANCEL_DATE"));
        rst.put("CANCEL_OFFSET", enableMode.getString("CANCEL_OFFSET"));
        rst.put("CANCEL_UNIT", enableMode.getString("CANCEL_UNIT"));
        return rst;
    }

    /**
     * 根据产品ID和元素ID查询元素的生、失效时间
     * 
     * @param productId
     * @param elementId
     * @return
     * @throws Exception
     */
    public static IDataset queryElementInfosByProductIdElementId(String productId, String elementId) throws Exception
    {

        IDataset pkgelementset = UProductElementInfoQry.queryElementInfosByProductIdAndElementIdElemetnTypeCode(productId, elementId, BofConst.ELEMENT_TYPE_CODE_DISCNT);

        if (IDataUtil.isEmpty(pkgelementset))
        {
            pkgelementset = UProductElementInfoQry.queryElementInfosByProductIdAndElementIdElemetnTypeCode(productId, elementId, BofConst.ELEMENT_TYPE_CODE_SVC);
        }
        return pkgelementset;
    }

    public static IData getElementInfo(String offerCode, String offerType, String relOfferCode, String relOfferType) throws Exception
    {
        IData pkgElem = null;

        IDataset offerInfos = UpcCall.qrySpecificOfferFromAllOffersByOfferId(offerCode, offerType, relOfferCode, relOfferType);
        if (IDataUtil.isNotEmpty(offerInfos))
        {
            for (Object obj : offerInfos)
            {
                IData offerInfo = (IData) obj;
                String flag = offerInfo.getString("FLAG");

                if ("PM_OFFER_JOIN_REL".equals(flag) || "PM_OFFER_COM_REL".equals(flag))
                {
                    String relId = offerInfo.getString("REL_OBJECT_ID");
                    IDataset chas = UpcCall.queryTempChaByCond(relId, "TD_B_PACKAGE_ELEMENT", null);
                    if (IDataUtil.isNotEmpty(chas))
                    {
                        for (int i = 0; i < chas.size(); i++)
                        {
                            IData cha = chas.getData(i);
                            if (UpcConst.PM_OFFER_COM_REL_CHA_TYPE.equals(cha.getString("TYPE")) || UpcConst.PM_OFFER_JOIN_REL_CHA_TYPE.equals(cha.getString("TYPE")))
                            {
                                offerInfo.put(cha.getString("FIELD_NAME"), cha.getString("FIELD_VALUE"));
                            }
                        }
                    }

                    pkgElem = offerInfo;
                    break;
                }
            }
        }

        return pkgElem;
    }

    public static IData getPkgElemExtCha(String extChaId, String fromTableName, String type) throws Exception
    {
        IData result = new DataMap();

        IDataset chas = UpcCall.queryTempChaByCond(extChaId, fromTableName, null);
        if (IDataUtil.isNotEmpty(chas))
        {
            for (int i = 0; i < chas.size(); i++)
            {
                IData cha = chas.getData(i);
                if (cha.getString("TYPE").equals(type))
                {
                    result.put(cha.getString("FIELD_NAME"), cha.getString("FIELD_VALUE"));
                }
            }
        }

        return result;
    }

    public static IDataset qryComJoinElementEnableMode(String offerCode, String offerType, String elementId, String elementTypeCode) throws Exception
    {
        IDataset result = UpcCall.queryJoinEnableModeBy2OfferId(offerType, offerCode, elementTypeCode, elementId);
        if (IDataUtil.isEmpty(result))
        {
            result = UpcCall.queryComEnableModeBy2OfferId(offerType, offerCode, elementTypeCode, elementId);
        }

        return result;
    }

    public static IDataset getElementsByElementTypeCode(String packageId, String elementTypeCode) throws Exception
	{
		IDataset allElements = getPackageElements(packageId);
		if(IDataUtil.isEmpty(allElements)){
			return null;
		}
		IDataset rst = new DatasetList();
		for(Object obj : allElements){
			IData element = (IData)obj;
			if(elementTypeCode.equals(element.getString("ELEMENT_TYPE_CODE"))){
				rst.add(element);
			}
		}
		return rst;
	}
    
    public static IDataset getPackageElements(String packageId) throws Exception
    {
        IDataset elements = new DatasetList();
        IDataset offers = UpcCall.qrySaleActiveInfo("K", packageId);
        if (IDataUtil.isNotEmpty(offers))
        {
        	IData cache = null;
            for (Object obj : offers)
            {
                IData offer = (IData) obj;

                String offerType = offer.getString("OFFER_TYPE");
                if ("|D|S|C|Z|".indexOf(offerType) > 0 && "B".equals(offer.getString("RSRV_TAG1")))
                {
                    continue;
                }

                String selectFlag = offer.getString("SELECT_FLAG");// 0必选，1可选默选，2可选
                if ("0".equals(selectFlag))
                {
                    offer.put("FORCE_TAG", "1");
                }
                else if ("1".equals(selectFlag))
                {
                    offer.put("FORCE_TAG", "0");
                    offer.put("DEFAULT_TAG", "1");
                }
                else if ("2".equals(selectFlag))
                {
                    offer.put("FORCE_TAG", "0");
                    offer.put("DEFAULT_TAG", "0");
                }

                String flag = offer.getString("FLAG");
                if ("PM_GROUP_COM_REL".equals(flag))
                {
                    // GROUP_ID,GROUP_NAME,GROUP_TYPE,DESCRIPTION
                    String groupType = offer.getString("GROUP_TYPE");
                    if ("0".equals(groupType))// 有最大最小元素限制的，包倒成了组，限制元素在GROUP_COM_REL里，GROUP_TYPE=0
                    {

                    }
                    else if ("9".equals(groupType))// 包中包结构
                    {
                    	if(IDataUtil.isEmpty(cache))
        				{
        					cache = new DataMap();
        				}
        				
        				String groupId = offer.getString("GROUP_ID");
        				if(IDataUtil.isEmpty(cache.getData(groupId)))
        				{
	            			String groupName = offer.getString("GROUP_NAME");
	            			String groupDesc = offer.getString("GROUP_DESCRIPTION");
	
	        				IData pkgElement = new DataMap();
	        				pkgElement.put("ELEMENT_ID", groupId);
	        				pkgElement.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PACKAGE);
	        				pkgElement.put("COMBINE_ID", groupId);
	        				pkgElement.put("COMBINE_NAME", groupName);
	        				pkgElement.put("COMBINE_DESC", groupDesc);
	        				pkgElement.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PACKAGE);
	        				pkgElement.put("OFFER_CODE", groupId);
	        				pkgElement.put("OFFER_NAME", groupName);
	        	    		
	                        IData groupCha = offer.getData("TD_B_PACKAGE");
	                        pkgElement.put("COMBINE_ENABLE_DESC", groupCha.getString("RSRV_STR1"));
	                        
	                        String groupSelectFlag = offer.getString("GROUP_SELECT_FLAG");//0必选，1可选默选，2可选
	                        if("0".equals(groupSelectFlag))
	                        {
	                        	pkgElement.put("FORCE_TAG", "1");
	                        }else if("1".equals(groupSelectFlag))
	                        {
	                        	pkgElement.put("FORCE_TAG", "0");
	                        	pkgElement.put("DEFAULT_TAG", "1");
	                        }else if("2".equals(groupSelectFlag))
	                        {
	                        	pkgElement.put("FORCE_TAG", "0");
	                        	pkgElement.put("DEFAULT_TAG", "0");
	                        }
	                        
	                        elements.add(pkgElement);
	                        cache.put(groupId, pkgElement);
	                        continue;
        				}
        				continue;
                    }
                }else if("PM_OFFER_GIFT".equals(flag))
                {
                	offer.put("PACKAGE_ID", packageId);
                	offer.put("DEPOSIT_TYPE", offer.getString("GIFT_TYPE"));
                	offer.put("MONTHS", offer.getString("GIFT_CYCLE"));
                	offer.put("GIFT_USE_TAG", offer.getString("GIFT_USE_TAG"));
                	offer.put("DISCNT_GIFT_ID", offer.getString("EXT_GIFT_ID"));
                    offer.put("DISCNT_GIFT_NAME", offer.getString("GIFT_NAME"));
                    offer.put("A_DISCNT_CODE", offer.getString("GIFT_OBJ_ID"));
                    offer.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT);
                    offer.put("ELEMENT_ID", offer.getString("EXT_GIFT_ID"));
                    offer.put("ELEMENT_NAME", offer.getString("GIFT_NAME"));
                }

                dealOfferComCha(offer, offerType);

                elements.add(offer);
            }
        }
        return elements;
    }

    private static void dealOfferComCha(IData param, String elementTypeCode) throws Exception
    {
        IData offerComCha = param.getData("PM_OFFER_COM_CHA");
        if (BofConst.ELEMENT_TYPE_CODE_SALEGOODS.equals(elementTypeCode))
        {
            param.put("GOODS_ID", param.getString("OFFER_CODE"));
            param.put("GOODS_NAME", param.getString("OFFER_NAME"));
            param.put("GOODS_EXPLAIN", param.getString("DESCRIPTION"));
        }
        else if (BofConst.ELEMENT_TYPE_CODE_CREDIT.equals(elementTypeCode))
        {
            param.put("CREDIT_GIFT_ID", param.getString("OFFER_CODE"));
            param.put("CREDIT_GIFT_EXPLAIN", param.getString("OFFER_NAME"));
            param.put("CREDIT_GIFT_MONTHS", offerComCha.getString("CREDIT_GIFT_MONTHS"));
            param.put("CREDIT_VALUE", offerComCha.getString("CREDIT_VALUE"));
        }
        else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
        {
            param.put("DISCNT_CODE", param.getString("OFFER_CODE"));
            param.put("DISCNT_NAME", param.getString("OFFER_NAME"));
            param.put("DISCNT_EXPLAIN", param.getString("DESCRIPTION"));
        }
        else if (BofConst.ELEMENT_TYPE_CODE_SCORE.equals(elementTypeCode))
        {
            param.put("SCORE_DEDUCT_ID", param.getString("OFFER_CODE"));
            param.put("PAYMENT_ID", offerComCha.getString("PAYMENT_ID"));
            param.put("DEPOSIT_TAG", offerComCha.getString("DEPOSIT_TAG", "0"));
            param.put("DEPOSIT_RATE", offerComCha.getString("DEPOSIT_RATE", "1"));
            param.put("SCORE_VALUE", offerComCha.getString("SCORE_VALUE"));
            param.put("SCORE_TYPE_CODE", offerComCha.getString("SCORE_TYPE_CODE"));
        }
        else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(elementTypeCode) || BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
        {
            param.put("SERVICE_ID", param.getString("OFFER_CODE"));
            param.put("SERVICE_NAME", param.getString("OFFER_NAME"));
        }

        param.put("MAIN_TAG", param.getString("IS_MAIN"));
        param.put("ELEMENT_TYPE_CODE", param.getString("OFFER_TYPE"));
        param.put("ELEMENT_ID", param.getString("OFFER_CODE"));

        if(IDataUtil.isNotEmpty(offerComCha))
        {
        	param.putAll(offerComCha);
        	param.remove("PM_OFFER_COM_CHA");
        }
    }
}
