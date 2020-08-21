package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

import java.util.List;

public final class UpcCall
{
    public static IData queryVAProductPackage(IData inParams) throws Exception {
        IData result = UpcCallIntf.queryVAProductPackage(inParams);
        return  result;
    }

    public static IData queryVAProductActivity(IData inParams) throws Exception {
        IData result = UpcCallIntf.queryVAProductActivity(inParams);
        return  result;
    }

    public static IData searchProductList(IData inParams) throws Exception {
        IData result = UpcCallIntf.searchProductList(inParams);
        return  result;
    }

    public static IData queryProductList(IData inParams) throws Exception {
        IData result = UpcCallIntf.queryProductList(inParams);
        return result;
    }

    /**
     * *特征相关接口 *根据offer_id,FILED_NAME,VALUE 查询Text
     * 
     * @param offerType
     *            必填
     * @param offer
     *            必填
     * @param filedName
     *            必填
     * @param value
     *            必填
     * @throws Exception
     */

    public static IData queryChaSpecValByCond(String offerType, String offer, String filedName, String value) throws Exception
    {
        IDataset result = UpcCallIntf.queryChaSpecValByCond(offerType, offer, filedName, value);
        if (IDataUtil.isNotEmpty(result))
        {
            return result.getData(0);
        }
        else
        {
            return new DataMap();
        }
    }

    /**
     * *特征相关接口 *根据条件查询临时属性表
     * 
     * @param tempChaId
     *            必填
     * @param fromTableName
     *            必填
     * @param fieldName
     *            必填
     * @throws Exception
     */

    public static IDataset queryTempChaByCond(String tempChaId, String fromTableName, String fieldName) throws Exception
    {
        return UpcCallIntf.queryTempChaByCond(tempChaId, fromTableName, fieldName);
    }
    public static IDataset qryOffersByOfferIdWithPackageElementFilter(String offerType, String offerCode) throws Exception
    {
    	IDataset tempList= UpcCallIntf.qryOffersByOfferIdWithPackageElementFilter(offerType, offerCode);
    	if (IDataUtil.isNotEmpty(tempList))
        {
            for (int i = 0; i < tempList.size(); i++)
            {
                IData tempCha = tempList.getData(i);
                tempCha.put("ELEMENT_ID", tempCha.getString("OFFER_CODE"));
                tempCha.put("ELEMENT_NAME", tempCha.getString("OFFER_NAME"));
                tempCha.put("ELEMENT_TYPE_CODE", tempCha.getString("OFFER_TYPE"));
            }
        }
    	return tempList;
    }
    
    public static IData queryTempChaByCond(String tempChaId, String fromTableName) throws Exception
    {
        IData result = new DataMap();

        IDataset tempChas = UpcCallIntf.queryTempChaByCond(tempChaId, fromTableName, null);
        if (IDataUtil.isNotEmpty(tempChas))
        {
            for (int i = 0; i < tempChas.size(); i++)
            {
                IData tempCha = tempChas.getData(i);
                result.put(tempCha.getString("FIELD_NAME"), tempCha.getString("FIELD_VALUE"));
            }
        }

        return result;
    }
    
    public static IData queryTempChaByCond(String tempChaId, String fromTableName, String fieldName, String type) throws Exception
    {
        IData result = new DataMap();

        IDataset tempChas = UpcCallIntf.queryTempChaByCond(tempChaId, fromTableName, fieldName);
        if (IDataUtil.isNotEmpty(tempChas))
        {
            for (int i = 0; i < tempChas.size(); i++)
            {
                IData tempCha = tempChas.getData(i);
                if(StringUtils.equals(type, tempCha.getString("TYPE")))
                {
                	result.put(tempCha.getString("FIELD_NAME"), tempCha.getString("FIELD_VALUE"));
                }
            }
        }

        return result;
    }

    /**
     * *特征相关接口 *根据商品ID查询商品的结构属性及值
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param fieldName
     *            可填
     * @throws Exception
     */

    public static IDataset queryProductInfosByMode(String fieldName, String productMode) throws Exception
    {
        IDataset poDatas = qryOffersByComCha(fieldName, productMode);

        if (!poDatas.isEmpty())
        {
            for (int i = 0; i < poDatas.size(); i++)
            {
                poDatas.getData(i).put("PRODUCT_ID", poDatas.getData(i).getString("OFFER_CODE", ""));
                poDatas.getData(i).put("PRODUCT_NAME", poDatas.getData(i).getString("OFFER_NAME", ""));
            }
        }
        return poDatas;
    }

    /**
     * 根据 销售品构成值 查询销售品信息
     * 
     * @param fieldName
     * @param fieldValue
     * @return
     * @throws Exception
     */
    public static IDataset qryOffersByComCha(String fieldName, String fieldValue) throws Exception
    {
        return UpcCallIntf.qryOffersByComCha(fieldName, fieldValue);
    }

    /**
     * *特征相关接口 *根据商品ID查询商品的结构属性及值
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param fieldName
     *            可填
     * @throws Exception
     */

    public static IDataset queryOfferComChaByCond(String offerType, String offerCode, String fieldName) throws Exception
    {
        return UpcCallIntf.queryOfferComChaByCond(offerType, offerCode, fieldName);
    }

    /**
     * 特征相关接口 根据特牌查询品牌名称
     * 
     * @param brandCode
     * @return
     * @throws Exception
     */
    public static String queryBrandNameByChaVal(String brandCode) throws Exception
    {
        return UpcCallIntf.queryBrandNameByChaVal(brandCode);
    }

    /**
     * *特征相关接口 *根据条件查询商品的销售属性
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param fieldName
     *            可填
     * @param attrObj
     *            可填
     * @param valueType
     *            可填
     * @param mgmtDistrict
     *            可填
     * @throws Exception
     */

    public static IDataset queryOfferChaByCond(String offerType, String offerCode, String fieldName, String attrObj, String valueType, String mgmtDistrict) throws Exception
    {
        return UpcCallIntf.queryOfferChaByCond(offerType, offerCode, fieldName, attrObj, valueType, mgmtDistrict, null);
    }

    public static IDataset queryOfferChaByCond(String offerType, String offerCode, String fieldName, String attrObj, String valueType, String mgmtDistrict, Pagination pagination) throws Exception
    {
        return UpcCallIntf.queryOfferChaByCond(offerType, offerCode, fieldName, attrObj, valueType, mgmtDistrict, pagination);
    }

    public static IDataset qryOfferChaSpecsByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.qryOfferChaSpecsByOfferId(offerType, offerCode);
    }

    /**
     * *特征相关接口 *根据条件查询商品的销售属性
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferChaByCond(String offerType, String offerCode) throws Exception
    {
        return queryOfferChaByCond(offerType, offerCode, null, null, null, null);
    }

    /**
     * *特征相关接口 *根据商品ID与FILED_NAME查询商品的结构属性及值
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferComChaByCond(String offerType, String offerCode) throws Exception
    {
        return queryOfferComChaByCond(offerType, offerCode, null);
    }

    /**
     * *特征相关 *根据商品ID与FILED_NAME查询商品的销售属性及值
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param fieldName
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferChaAndValByCond(String offerType, String offerCode, String attrObj, String fieldName) throws Exception
    {
        return UpcCallIntf.queryOfferChaAndValByCond(offerType, offerCode, attrObj, fieldName);
    }

    public static IDataset queryOfferChaAndValByCond(String offerType, String offerCode, String valueType, String attrObj, String fieldName) throws Exception
    {
        return UpcCallIntf.queryOfferChaAndValByCond(offerType, offerCode, valueType, attrObj, fieldName);
    }

    /**
     * *特征相关 *根据商品ID与特征规格ID 查询销售属性值
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param chaSpecId
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferChaValByCond(String offerType, String offerCode, String chaSpecId) throws Exception
    {
        return UpcCallIntf.queryOfferChaValByCond(offerType, offerCode, chaSpecId);
    }

    /**
     * 
     * @Title: qryOfferChaValByFieldNameOfferCodeAndOfferType
     * @Description: 查询商品销售规格属性值，根据规格名称和offerCode,OfferType
     * @param @param offerType
     * @param @param offerCode
     * @param @param fieldName
     * @param @return
     * @param @throws Exception 设定文件
     * @return IDataset 返回类型
     * @throws
     */
    public static IDataset qryOfferChaValByFieldNameOfferCodeAndOfferType(String offerType, String offerCode, String fieldName) throws Exception
    {
        return UpcCallIntf.qryOfferChaValByFieldNameOfferCodeAndOfferType(offerType, offerCode, fieldName);
    }

    /**
     * *商品相关接口 *根据组编码查询组关系
     * 
     * @param groupId
     *            必填
     * @param relType
     *            必填
     * @throws Exception
     */

    public static IDataset queryGroupRelByGroupId(String groupId, String relType) throws Exception
    {
        return UpcCallIntf.queryGroupRelByGroupId(groupId, relType);
    }

    /**
     * *商品相关接口 *根据组编码查询组信息
     * 
     * @param groupId
     *            必填
     * @param groupType
     *            必填
     * @throws Exception
     */

    public static IDataset queryGroupByCond(String groupId, String groupType) throws Exception
    {
        return UpcCallIntf.queryGroupByCond(groupId, groupType);
    }

    /**
     * *商品相关接口 *根据商品构成关系的商品编码和关系商品标识查询生失效方式
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param relOfferType
     *            必填
     * @param relOfferCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryComEnableModeBy2OfferId(String offerType, String offerCode, String relOfferType, String relOfferCode) throws Exception
    {
        return UpcCallIntf.queryComEnableModeBy2OfferId(offerType, offerCode, relOfferType, relOfferCode);
    }

    /**
     * *商品相关接口 *根据商品编码查询生失效方式
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryEnableModeRelByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryEnableModeRelByOfferId(offerType, offerCode);
    }

    /**
     * *商品相关接口 *根据上级品类查询下级品类
     * 
     * @param upCategoryId
     *            必填
     * @param recursion
     *            可填
     * @throws Exception
     */

    public static IDataset queryCateByUpCateId(String upCategoryId, String recursion) throws Exception
    {
        return UpcCallIntf.queryCateByUpCateId(upCategoryId, recursion);
    }

    /**
     * *商品相关接口 *根据上级品类查询下级品类
     * 
     * @param upCategoryId
     *            必填
     * @throws Exception
     */

    public static IDataset queryCateByUpCateId(String upCategoryId) throws Exception
    {
        return queryCateByUpCateId(upCategoryId, null);
    }

    /**
     * *商品相关接口 *根据品类编码查询商品
     * 
     * @param categoryId
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferByCateId(String categoryId) throws Exception
    {
        return UpcCallIntf.queryOfferByCateId(categoryId);
    }

    /**
     * *商品相关接口 *根据商品编码查询商品所挂的商品列表
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param relType
     *            可填
     * @param selectFlag
     *            可填
     * @param mgmtDistrict
     *            可填
     * @throws Exception
     */

    public static IData queryAllOffersByOfferId(String offerType, String offerCode, String relType, String selectFlag, String mgmtDistrict) throws Exception
    {
        return UpcCallIntf.queryAllOffersByOfferId(offerType, offerCode, null, relType, selectFlag, mgmtDistrict);
    }
    
    /**
     * *商品相关接口 *根据商品编码查询商品所挂的商品列表
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     *@param elementTypeCode
     *            可填
     * @param relType
     *            可填
     * @param selectFlag
     *            可填
     * @param mgmtDistrict
     *            可填
     * @throws Exception
     */

    public static IData queryAllOffersByOfferId(String offerType, String offerCode, String elementTypeCode, String relType, String selectFlag, String mgmtDistrict) throws Exception
    {
        return UpcCallIntf.queryAllOffersByOfferId(offerType, offerCode, elementTypeCode, relType, selectFlag, mgmtDistrict);
    }

    /**
     * *商品相关接口 *"根据offer_id,rel_offer_id,rel_type（包括构成关系,关联订购,关联取消等,具体枚举值可以再商量）查询对应的关系表,得出offer_id代表的销售品pm_enable_mode数据,如果查询不到关系型的时间配置,则查询offer_id商品本身的时间配置,如果rel_offer_id,rel_type未传,则直接查询offer_id商品本身的时间配置信息"
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param relOfferType
     *            必填
     * @param relOfferCode
     *            必填
     * @param relType
     *            可填
     * @throws Exception
     */

    public static IDataset queryOfferEnableModeByCond(String offerType, String offerCode, String relOfferType, String relOfferCode, String relType) throws Exception
    {
        return UpcCallIntf.queryOfferEnableModeByCond(offerType, offerCode, relOfferType, relOfferCode, relType);
    }

    /**
     * *商品相关接口 *
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferEnableModeByCond(String offerType, String offerCode) throws Exception
    {
        IDataset results = queryOfferEnableModeByCond(offerType, offerCode, null, null, null);

        return results;
    }

    /**
     * *商品相关接口 *根据商品ID查询商品构成关系并关联出商品
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferComRelOfferByOfferId(String offerType, String offerCode, String eparchyCode) throws Exception
    {
        return UpcCallIntf.queryOfferComRelOfferByOfferId(offerType, offerCode, eparchyCode);
    }

    public static IDataset queryOfferComRelOfferByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferComRelOfferByOfferId(offerType, offerCode, null);
    }
    
    public static IDataset queryOfferComRelOfferByOfferIdRelOfferType(String offerType, String offerCode, String relOfferType, String eparchyCode) throws Exception
    {
        IDataset result = new DatasetList();
        
        IDataset offerComRels = UpcCallIntf.queryOfferComRelOfferByOfferId(offerType, offerCode, eparchyCode);
        if(IDataUtil.isNotEmpty(offerComRels))
        {
            for(Object obj : offerComRels)
            {
                IData offerComRel = (IData) obj;
                if(offerComRel.getString("OFFER_TYPE").equals(relOfferType))
                {
                    result.add(offerComRel);
                }
            }
        }
        
        return result;
    }
    
    /**
     * *商品相关接口 *根据商品ID查询商品构成关系并关联出商品 这个接口与接口UPC.Out.OfferQueryFSV.queryOfferComRelOfferByOfferId相比，更侧重查REL_OFFER_ID的PM_OFFER信息
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset qryComRelOffersByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.qryComRelOffersByOfferId(offerType, offerCode);
    }

    /**
     * *商品相关接口 *根据商品ID查询商品关联关系并关联出商品
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param relType
     *            可填
     * @param selectFlag
     *            可填
     * @param queryCha
     *            可填
     * @throws Exception
     */

    public static IDataset queryOfferJoinRelAndOfferByOfferId(String offerType, String offerCode, String relType, String selectFlag, String queryCha) throws Exception
    {
        return UpcCallIntf.queryOfferJoinRelAndOfferByOfferId(offerType, offerCode, relType, selectFlag, queryCha);
    }

    /**
     * *商品相关接口 *根据商品ID查询商品标签
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferTagByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferTagByOfferId(offerType, offerCode);
    }

    /**
     * *商品相关接口 *根据商品ID查询商品标识码
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferCodeByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferCodeByOfferId(offerType, offerCode);
    }

    /**
     * *商品相关接口 *根据商品ID查询场景类型
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset querySceneTypeByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.querySceneTypeByOfferId(offerType, offerCode);
    }

    /**
     * *商品相关接口 *根据商品线编码查询商品线
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferLineByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferLineByOfferId(offerType, offerCode);
    }

    /**
     * *商品相关接口 *根据商品线编码查询商品列表
     * 
     * @param offerLineId
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferByOfferLineId(String offerLineId) throws Exception
    {
        return UpcCallIntf.queryOfferByOfferLineId(offerLineId);
    }

    /**
     * *商品相关接口 *根据多个品类编码查询品类与商品信息
     * 
     * @param categoryId
     *            必填
     * @param offerLineId
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferByMutiCateId(String categoryId, String offerLineId) throws Exception
    {
        return UpcCallIntf.queryOfferByMutiCateId(categoryId, offerLineId);
    }

    public static IDataset queryOffersByMultiCategory(String productId, String mgmtDistrict, String categoryId) throws Exception
    {
        return UpcCallIntf.queryOffersByMultiCategory(productId, mgmtDistrict, categoryId);
    }

    public static IDataset queryOffersByMultiCategory(String mainOfferId, String mgmtDistrict, String categoryId, String relType) throws Exception
    {
        return UpcCallIntf.queryOffersByMultiCategory(mainOfferId, mgmtDistrict, categoryId, relType);
    }

    public static IDataset queryOfferGroups(String productId) throws Exception
    {
        return UpcCallIntf.queryOfferGroups(productId);
    }

    /**
     * *商品相关接口 *根据标签查询商品信息
     * 
     * @param tagId
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferByTagId(String tagId) throws Exception
    {
        return UpcCallIntf.queryOfferByTagId(tagId);
    }

    /**
     * *商品相关接口 *根据组编码查询商品信息
     * 
     * @param groupId
     *            必填
     * @throws Exception
     */

    public static IDataset queryGroupComRelOfferByGroupId(String groupId) throws Exception
    {
        return UpcCallIntf.queryGroupComRelOfferByGroupId(groupId);
    }
    
    public static IDataset queryGroupComRelOfferByGroupId(String groupId, String eparchyCode) throws Exception
    {
        return UpcCallIntf.queryGroupComRelOfferByGroupId(groupId, eparchyCode);
    }

    public static IDataset qryOfferFromGroupByGroupIdOfferId(String groupId, String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.qryOfferFromGroupByGroupIdOfferId(groupId, offerType, offerCode, null);
    }
    
    public static IDataset qryOfferFromGroupByGroupIdOfferId(String groupId, String offerType, String offerCode, String queryCha) throws Exception
    {
        return UpcCallIntf.qryOfferFromGroupByGroupIdOfferId(groupId, offerType, offerCode, queryCha);
    }

    /**
     * *商品相关接口 *根据商品ID查询商品图片关系信息
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferPicByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferPicByOfferId(offerType, offerCode);
    }

    /**
     * *商品相关接口 *根据商品OFFER_ID和REL_OFFER_ID查询关联生失效方式
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param relOfferType
     *            必填
     * @param relOfferCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryJoinEnableModeBy2OfferId(String offerType, String offerCode, String relOfferType, String relOfferCode) throws Exception
    {
        return UpcCallIntf.queryJoinEnableModeBy2OfferId(offerType, offerCode, relOfferType, relOfferCode);
    }

    /**
     * *商品相关接口 *根据GROUP_ID和OFFER_ID查询组构成生失效方式
     * 
     * @param groupId
     *            必填
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryGroupComEnableModeByGroupIdOfferId(String groupId, String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryGroupComEnableModeByGroupIdOfferId(groupId, offerType, offerCode);
    }

    /**
     * *商品相关接口 *根据组编码查询组构成关系
     * 
     * @param groupId
     *            必填
     * @throws Exception
     */

    public static IDataset queryGroupComRelByGroupId(String groupId) throws Exception
    {
        return UpcCallIntf.queryGroupComRelByGroupId(groupId);
    }

    /**
     * *商品相关接口 *根据商品ID查询商品与组的关系
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferGroupRelOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferGroupRelOfferId(offerType, offerCode);
    }

    /**
     * *商品相关接口 *根据商品ID查询商品与组的关系
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferGroupRelOfferIdGroupId(String offerType, String offerCode, String groupId) throws Exception
    {
        return queryOfferGroupRelOfferIdGroupId(offerType, offerCode, groupId, null);
    }
    
    public static IDataset queryOfferGroupRelOfferIdGroupId(String offerType, String offerCode, String groupId, String queryCha) throws Exception
    {
        return UpcCallIntf.queryOfferGroupRelOfferIdGroupId(offerType, offerCode, groupId, queryCha);
    }

    /**
     * *商品相关接口 *根据商品ID与REL_TYPE查询商品规则关系
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param relType
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferRelByCond(String offerType, String offerCode, String relType) throws Exception
    {
        return UpcCallIntf.queryOfferRelByCond(offerType, offerCode, relType);
    }

    /**
     * *商品相关接口 *根据商品ID查询商品关联关系
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param relType
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferJoinRelByOfferId(String offerType, String offerCode, String relType) throws Exception
    {
        return UpcCallIntf.queryOfferJoinRelByOfferId(offerType, offerCode, relType);
    }
    
    public static IDataset queryOfferJoinRelByOfferId(String offerType, String offerCode, String relType, String eparchyCode) throws Exception
    {
        return UpcCallIntf.queryOfferJoinRelByOfferId(offerType, offerCode, relType, eparchyCode);
    }

    /**
     * 查询集团成员产品
     * 
     * @param grpOfferId
     * @return
     * @throws Exception
     */
    public static IDataset queryMebOffersByGrpOfferId(String grpOfferId) throws Exception
    {

        return queryOfferJoinRelByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, grpOfferId, UpcConst.PM_OFFER_JOIN_REL_TYPE_ECMEB);
    }

    /**
     * 查询动力一百、BBOSS的商品下的产品信息
     * 
     * @param grpOfferId
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpOffersByGrpOfferId(String grpOfferId) throws Exception
    {

        return queryOfferJoinRelByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, grpOfferId, UpcConst.PM_OFFER_JOIN_REL_TYPE_POWER100);
    }

    /**
     * *商品相关接口 *根据offerJoin里 rel_offer_id,rel_type 查询offerJoinRel表里 offerId 商品信息
     * 
     * @param relOfferType,offerCode,offerCode
     *            必填
     * @param relType
     *            必填
     * @throws Exception
     */
    public static IDataset queryOfferJoinRelByRelOfferId(String relOfferType, String relOfferCode, String relType) throws Exception
    {
        return UpcCallIntf.queryOfferJoinRelByRelOfferId(relOfferType, relOfferCode, relType);
    }

    /**
     * 根据成员产品查询对应的集团产品
     * 
     * @param mebOfferId
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpOfferByMebOfferId(String mebOfferId) throws Exception
    {

        return queryOfferJoinRelByRelOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, mebOfferId, UpcConst.PM_OFFER_JOIN_REL_TYPE_ECMEB);
    }

    /**
     * 根据产品查找商品信息
     * 
     * @param mebOfferId
     * @return
     * @throws Exception
     */
    public static IDataset queryBBossGrpOfferByMebOfferId(String mebOfferId) throws Exception
    {

        return queryOfferJoinRelByRelOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, mebOfferId, UpcConst.PM_OFFER_JOIN_REL_TYPE_POWER100);
    }

    /**
     * *商品相关接口 *根据商品ID查询商品构成关系
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferComRelByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferComRelByOfferId(offerType, offerCode);
    }

    /**
     * *商品相关接口 *根据商品ID查询商品
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param queryComCha
     *            可填
     * @throws Exception
     */

    public static IData queryOfferByOfferId(String offerType, String offerCode, String queryComCha) throws Exception
    {
        IDataset result = UpcCallIntf.queryOfferByOfferId(offerType, offerCode, queryComCha);
        if (IDataUtil.isNotEmpty(result))
        {
            return result.getData(0);
        }
        else
        {
            return null;
        }

    }

    /**
     * *商品相关接口 *根据商品ID查询商品
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     *            可填
     * @throws Exception
     */

    public static IData queryOfferByOfferId(String offerType, String offerCode) throws Exception
    {
        return queryOfferByOfferId(offerType, offerCode, null);
    }

    /**
     * *商品相关接口 *根据商品ID查询商品详情 包括生失效,定价 与组关系,销售属性
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param mgmtDistrict
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferDetailsByOfferId(String offerType, String offerCode, String mgmtDistrict) throws Exception
    {
        return UpcCallIntf.queryOfferDetailsByOfferId(offerType, offerCode, mgmtDistrict);
    }

    /**
     * *规则相关 *根据赠送编码查询赠送信息与赠送计划信息
     * 
     * @param giftId
     *            必填
     * @throws Exception
     */

    public static IDataset queryGiftByGiftId(String giftId) throws Exception
    {
        return UpcCallIntf.queryGiftByGiftId(giftId);
    }

    /**
     * *规则相关 *根据RULE_ID查询规则构成信息
     * 
     * @param ruleId
     *            必填
     * @throws Exception
     */

    public static IDataset queryRuleComRelByRuleId(String ruleId) throws Exception
    {
        return UpcCallIntf.queryRuleComRelByRuleId(ruleId);
    }

    /**
     * *规则相关 *根据RULE_ID查询规则信息
     * 
     * @param ruleId
     *            必填
     * @throws Exception
     */

    public static IDataset queryRuleByRuleId(String ruleId) throws Exception
    {
        return UpcCallIntf.queryRuleByRuleId(ruleId);
    }

    /**
     * *规则相关 *根据COND_ID查询条件信息
     * 
     * @param condId
     *            必填
     * @throws Exception
     */

    public static IDataset queryConditionByCondId(String condId) throws Exception
    {
        return UpcCallIntf.queryConditionByCondId(condId);
    }

    /**
     * *规则相关 *根据COND_ID查询条件实例信息
     * 
     * @param condValId
     *            必填
     * @throws Exception
     */

    public static IDataset queryCondValByCondValId(String condValId) throws Exception
    {
        return UpcCallIntf.queryCondValByCondValId(condValId);
    }

    /**
     * *规则相关 *根据OPERCODE查询操作符信息
     * 
     * @param operCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOperCodeParseByOperCode(String operCode) throws Exception
    {
        return UpcCallIntf.queryOperCodeParseByOperCode(operCode);
    }

    /**
     * *规则相关 *根据优惠编码查询优惠信息
     * 
     * @param policyId
     *            必填
     * @throws Exception
     */

    public static IDataset queryPolicyByPolicyId(String policyId) throws Exception
    {
        return UpcCallIntf.queryPolicyByPolicyId(policyId);
    }

    /**
     * *规则相关 *根据商品编码查询商品规则信息
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferRuleByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferRuleByOfferId(offerType, offerCode);
    }

    /**
     * *规则相关 *根据规则结论编码查询规则结论信息
     * 
     * @param ruleResultId
     *            必填
     * @throws Exception
     */

    public static IDataset queryRuleResultByRuleResultId(String ruleResultId) throws Exception
    {
        return UpcCallIntf.queryRuleResultByRuleResultId(ruleResultId);
    }

    /**
     * *规则相关 *根据销售资格编码查询销售资格信息
     * 
     * @param saleLimitId
     *            必填
     * @throws Exception
     */

    public static IDataset querySaleLimitBySaleLimitId(String saleLimitId) throws Exception
    {
        return UpcCallIntf.querySaleLimitBySaleLimitId(saleLimitId);
    }

    /**
     * *定价相关 *根据offer_id查询返回pm_price信息
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferPriceRelPriceByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferPriceRelPriceByOfferId(offerType, offerCode);
    }

    /**
     * *定价相关 *根据price_id查询返回pm_price_plan信息
     * 
     * @param priceId
     *            必填
     * @throws Exception
     */

    public static IDataset queryPricePricePlanRelPricePlanByPriceId(String priceId) throws Exception
    {
        return UpcCallIntf.queryPricePricePlanRelPricePlanByPriceId(priceId);
    }

    /**
     * *定价相关 *根据商品查询商品商品的定价
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferPriceRelByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferPriceRelByOfferId(offerType, offerCode);
    }

    /**
     * *定价相关 *根据定价计划编码查询定价计划
     * 
     * @param pricePlanId
     *            必填
     * @throws Exception
     */

    public static IDataset queryPricePlanByPlanId(String pricePlanId) throws Exception
    {
        return UpcCallIntf.queryPricePlanByPlanId(pricePlanId);
    }

    /**
     * *定价相关 *根据定价编码查询定价计划
     * 
     * @param priceId
     *            必填
     * @throws Exception
     */

    public static IDataset queryPricePricePlanRelByPriceId(String priceId) throws Exception
    {
        return UpcCallIntf.queryPricePricePlanRelByPriceId(priceId);
    }

    /**
     * *定价相关 *根据定价编码查询定价
     * 
     * @param priceId
     *            必填
     * @throws Exception
     */

    public static IDataset queryPriceByPriceId(String priceId) throws Exception
    {
        return UpcCallIntf.queryPriceByPriceId(priceId);
    }

    /**
     * *产品状态相关接口 *根据OFFER_ID,PROD_STATUS查询PM_PROD_STA
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param prodStatus
     *            必填
     * @throws Exception
     */

    public static IData queryProdStaByCond(String offerType, String offerCode, String prodStatus) throws Exception
    {
        IDataset result = UpcCallIntf.queryProdStaByCond(offerType, offerCode, prodStatus);
        if (IDataUtil.isNotEmpty(result))
        {
            return result.getData(0);
        }
        else
        {
            return null;
        }
    }

    /**
     * *产品相关 *根据产品条件查询产品状态
     * 
     * @param prodSpecId
     *            必填
     * @param prodStatus
     *            必填
     * @throws Exception
     */

    public static IDataset queryProdStaByProdIdAndProdStatus(String prodSpecId, String prodStatus) throws Exception
    {
        return UpcCallIntf.queryProdStaByProdIdAndProdStatus(prodSpecId, prodStatus);
    }

    /**
     * *产品相关 *根据offerID 查询产品表信息
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IData queryProdSpecByOfferId(String offerType, String offerCode) throws Exception
    {
        IDataset result = UpcCallIntf.queryProdSpecByOfferId(offerType, offerCode);
        if (IDataUtil.isNotEmpty(result))
        {
            return result.getData(0);
        }
        else
        {
            return null;
        }
    }

    /**
     * *产品相关 *根据商品ID查询产品ID
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryOfferProdRelByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferProdRelByOfferId(offerType, offerCode);
    }

    /**
     * *产品相关 *根据产品ID查询产品信息
     * 
     * @param prodSpecId
     *            必填
     * @throws Exception
     */

    public static IDataset queryProdSpecByProdId(String prodSpecId) throws Exception
    {
        return UpcCallIntf.queryProdSpecByProdId(prodSpecId);
    }

    /**
     * *标签相关 *根据品类编码查询标签信息
     * 
     * @param categoryId
     *            必填
     * @throws Exception
     */

    public static IDataset queryDivTagByCateId(String categoryId) throws Exception
    {
        return UpcCallIntf.queryDivTagByCateId(categoryId);
    }

    /**
     * *SP相关接口 *根据SP_CODE,BIZ_CODE,BIZ_TYPE_CODE查询企业信息
     * 
     * @param spCode
     *            必填
     * @param bizCode
     *            必填
     * @param bizTypeCode
     *            必填
     * @throws Exception
     */

    public static IDataset querySpServiceAndProdByCond(String spCode, String bizCode, String bizTypeCode, String serviceId) throws Exception
    {
        return UpcCallIntf.querySpServiceAndProdByCond(spCode, bizCode, bizTypeCode, serviceId);
    }
    
	/**
	 * *SP相关接口 *根据OFFER_CODE查询平台局数据信息，提供给IVR调用
	 * 
	 *            必填
	 * @throws Exception
	 */

    public static IDataset getPlatsvcCustSvc(String service_id) throws Exception
    {
        return UpcCallIntf.getPlatsvcCustSvc(service_id);
    }

    /**
     * *SP相关接口 *根据BIZ_TYPE_CODE,RSRV_STR1查询企业信息
     * 
     * @param bizTypeCode
     *            必填
     * @param rsrvStr1
     *            必填
     * @throws Exception
     */

    public static IDataset querySpServiceAndInfoByCond(String bizTypeCode, String rsrvStr1) throws Exception
    {
        return UpcCallIntf.querySpServiceAndInfoByCond(bizTypeCode, rsrvStr1);
    }

    /**
     * *SP相关接口 *根据SERVICE_ID,SP_CODE,BIZ_CODE,BIZ_NAME,BIZ_TYPE查询平台业务信息
     * 
     * @param serviceId
     *            必填
     * @param spCode
     *            必填
     * @param bizCode
     *            必填
     * @param bizName
     *            必填
     * @param bizType
     *            必填
     * @throws Exception
     */

    public static IDataset querySpServiceByCond(String serviceId, String spCode, String bizCode, String bizName, String bizType) throws Exception
    {
        return UpcCallIntf.querySpServiceByCond(serviceId, spCode, bizCode, bizName, bizType);
    }

    /**
     * *SP相关接口 *根据ID,CODE,NAME,TYPE查询企业信息表
     * 
     * @param spId
     *            必填
     * @param spCode
     *            必填
     * @param spName
     *            必填
     * @param spType
     *            必填
     * @throws Exception
     */

    public static IDataset querySpInfoByCond(String spId, String spCode, String spName, String spType) throws Exception
    {
        return UpcCallIntf.querySpInfoByCond(spId, spCode, spName, spType);
    }

    /**
     * 查询 td_m_sp_info add by hefeng *SP相关接口 *根据CODE,查询企业信息表的NAME
     * 
     * @param spCode
     *            必填
     * @throws Exception
     */

    public static IDataset querySpInfoNameByCond(String spCode) throws Exception
    {
        return UpcCallIntf.querySpInfoNameByCond(spCode);
    }

    /**
     * *SP相关接口 *根据offerid查询平台服务局数据信息
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @throws Exception
     */

    public static IDataset querySpServiceByOfferId(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.querySpServiceByOfferId(offerType, offerCode);
    }
    
    public static IDataset qrySpInfoCs(IData param) throws Exception
    {
        return UpcCallIntf.qrySpInfoCs(param);
    }
    
    public static IDataset updSpInfoCs(IData param) throws Exception
    {
        return UpcCallIntf.updSpInfoCs(param);
    }

    /**
     * *SP相关接口 *根据SP_SERVICE_ID,SP_CODE,BIZ_CODE,BIZ_TYPE_CODE查询企业信息
     * 
     *            必填
     * @param spCode
     *            必填
     * @param bizCode
     *            必填
     * @param bizTypeCode
     *            必填
     * @throws Exception
     */

    public static IDataset querySpServiceAndInfoAndParamByCond(String serviceId, String spCode, String bizCode, String bizTypeCode) throws Exception
    {
        return UpcCallIntf.querySpServiceAndInfoAndParamByCond(serviceId, spCode, bizCode, bizTypeCode);
    }

    /**
     * *SP相关接口 *根据PROD_SPEC_ID,BIZ_TYPE_CODE,OPER_CODE查询产品间关系表
     * 
     * @param prodSpecId
     *            必填
     * @param bizTypeCode
     *            必填
     * @param operCode
     *            必填
     * @throws Exception
     */

    public static IDataset queryProdSpecRelByCond(String prodSpecId, String bizTypeCode, String operCode) throws Exception
    {
        return UpcCallIntf.queryProdSpecRelByCond(prodSpecId, bizTypeCode, operCode);
    }

    /**
     * *SP相关接口 *根据ServiceId查询SP企业与业务参数等综合信息
     * 
     * @param serviceId
     *            必填
     * @throws Exception
     */

    public static IDataset querySpComprehensiveInfoByServiceId(String serviceId) throws Exception
    {
        return UpcCallIntf.querySpComprehensiveInfoByServiceId(serviceId);
    }

    /**
     * *SP相关接口 *根据SERVICE_ID查询企业综合信息
     * 
     * @param serviceId
     *            必填
     * @throws Exception
     */

    public static IDataset querySpServiceAndInfoAndParamByServiceId(String serviceId) throws Exception
    {
        return UpcCallIntf.querySpServiceAndInfoAndParamByServiceId(serviceId);
    }

    /**
     * *SP相关接口 *根据SERVICE_ID查询企业综合信息
     * 
     * @param serviceId
     *            必填
     * @throws Exception
     */

    public static IDataset querySpServiceByOfferId(String serviceId) throws Exception
    {
        return UpcCallIntf.querySpServiceByOfferId(serviceId);
    }

    /**
     * *BBOSS相关接口 *根据PORATENUMBER查询PORATEPLAN表
     * 
     * @param poratenumber
     *            必填
     * @throws Exception
     */

    public static IDataset queryPoratePlanByPorateNumber(String poratenumber) throws Exception
    {
        return UpcCallIntf.queryPoratePlanByPorateNumber(poratenumber);
    }

    /**
     * *BBOSS相关接口 *根据PospecNumber查询PM_PO表
     * 
     * @param pospecnumber
     *            必填
     * @throws Exception
     */

    public static IDataset queryPoByPospecNumber(String pospecnumber) throws Exception
    {
        return UpcCallIntf.queryPoByPospecNumber(pospecnumber);
    }

    /**
     * *BBOSS相关接口 *根据POSPECNUMBER,PORATENUMBER,RATEPLANID查询PORATEPLAN表
     * 
     * @param pospecnumber
     *            必填
     * @param poratenumber
     *            必填
     * @param rateplanid
     *            必填
     * @throws Exception
     */

    public static IDataset queryPoratePlanBySpecRatePlanId(String pospecnumber, String poratenumber, String rateplanid) throws Exception
    {
        return UpcCallIntf.queryPoratePlanBySpecRatePlanId(pospecnumber, poratenumber, rateplanid);
    }

    /**
     * *BBOSS相关接口 *根据RATEPLANID查询PORATEPLANICB表
     * 
     * @param rateplanid
     *            必填
     * @throws Exception
     */

    public static IDataset queryPoratePlanIcbByRatePlanId(String rateplanid) throws Exception
    {
        return UpcCallIntf.queryPoratePlanIcbByRatePlanId(rateplanid);
    }

    /**
     * *BBOSS相关接口 *根据PARAMETERNUMBER查询PORATEPLANICB表
     * 
     * @param parameternumber
     *            必填
     * @throws Exception
     */

    public static IDataset queryPoratePlanIcbByParameterNumber(String parameternumber) throws Exception
    {
        return UpcCallIntf.queryPoratePlanIcbByParameterNumber(parameternumber);
    }

    /**
     * *BBOSS相关接口 *根据PK查询PORATEPLANICB表
     * 
     * @param rateplanid
     *            必填
     * @param icbNo
     *            必填
     * @param parameterNo
     *            必填
     * @param parameternumber
     *            必填
     * @param parametername
     *            必填
     * @throws Exception
     */

    public static IDataset queryPoratePlanIcbByPk(String rateplanid, String icbNo, String parameterNo, String parameternumber, String parametername) throws Exception
    {
        return UpcCallIntf.queryPoratePlanIcbByPk(rateplanid, icbNo, parameterNo, parameternumber, parametername);
    }

    /**
     * *BBOSS相关接口 *根据ID 查询PM_PO表
     * 
     * @throws Exception
     */

    public static IDataset queryPoByValid() throws Exception
    {
        return UpcCallIntf.queryPoByValid();
    }

    /**
     * *BBOSS相关接口 *根据PRODUCTSPECNUMBER,POSPECNUMBER 查询PM_POPRODUCT表
     * 
     * @param productspecnumber
     *            必填
     * @param pospecnumber
     *            必填
     * @throws Exception
     */

    public static IDataset queryPoproductByProductSpecNumber(String productspecnumber, String pospecnumber) throws Exception
    {
        return UpcCallIntf.queryPoproductByProductSpecNumber(productspecnumber, pospecnumber);
    }

    /**
     * *BBOSS相关接口 *根据POSPECNUMBER 查询PM_POPRODUCT表
     * 
     * @param pospecnumber
     *            必填
     * @throws Exception
     */

    public static IDataset queryPoproductByPospecNumber(String pospecnumber) throws Exception
    {
        return UpcCallIntf.queryPoproductByPospecNumber(pospecnumber);
    }

    /**
     * *BBOSS相关接口 *根据PRODUCTSPECNUMBER 查询PM_POPRODUCTPLUS表
     * 
     * @param productspecnumber
     *            必填
     * @throws Exception
     */

    public static IDataset queryPoproductPlusbyProductSpecNumber(String productspecnumber) throws Exception
    {
        return UpcCallIntf.queryPoproductPlusbyProductSpecNumber(productspecnumber);
    }

    /**
     * *BBOSS相关接口 *根据BBOSS产品规格编码查询集团商品编码
     * 
     * @param productspecnumber
     *            必填
     * @throws Exception
     */

    public static String queryPospecnumberByProductspecnumber(String productspecnumber) throws Exception
    {
        return UpcCallIntf.queryPospecnumberByProductspecnumber(productspecnumber);
    }

    /**
     * *BBOSS相关接口 *BBOSS商品同步接口
     * 
     * @throws Exception
     */

    public static IDataset processBBossSyncInfo() throws Exception
    {
        return UpcCallIntf.processBBossSyncInfo();
    }

    /**
     * *BBOSS相关接口 *通过产品编号查询BBOSS产品名称
     * 
     * @param productspecnumber
     *            必填
     * @throws Exception
     */

    public static IDataset queryProductSpecNameByProductSpecNumber(String productspecnumber) throws Exception
    {
        return UpcCallIntf.queryProductSpecNameByProductSpecNumber(productspecnumber);
    }

    /**
     * *BBOSS相关接口 *通过商品编号查询BBOSS商品名称
     * 
     * @param pospecnumber
     *            必填
     * @throws Exception
     */

    public static IDataset queryPospecNameByPoSpecNumber(String pospecnumber) throws Exception
    {
        return UpcCallIntf.queryPospecNameByPoSpecNumber(pospecnumber);
    }

    /**
     * *BBOSS相关接口 *根据POSPECNUMBER查询PORATEPLAN表
     * 
     * @param pospecnumber
     *            必填
     * @throws Exception
     */

    public static IDataset queryPoratePlanByPospecNumber(String pospecnumber) throws Exception
    {
        return UpcCallIntf.queryPoratePlanByPospecNumber(pospecnumber);
    }

    // UPC.Out.ChaQueryFSV.queryOfferChaValByOfferIdAndMgmtDistict
    public static IDataset queryOfferChaValByOfferIdAndMgmtDistict(String serviceId, String eparchyCode) throws Exception
    {
        return UpcCallIntf.queryOfferChaValByOfferIdAndMgmtDistict(serviceId, eparchyCode);
    }

    public static IDataset querySpServiceByServType(String servType) throws Exception
    {
        return UpcCallIntf.querySpServiceByServType(servType);
    }

    public static IDataset querySpInfoBySpCodeAndBizTypeCodeAndSpName(String spCode, String spName, String bizTypeCode, Pagination pagination) throws Exception
    {
        return UpcCallIntf.querySpInfoBySpCodeAndBizTypeCodeAndSpName(spCode, spName, bizTypeCode, pagination);
    }

    public static IDataset querySpServiceAndOfferByCond(String serviceId, String serviceName, String bizTypeCode, String spCode, Pagination pagination) throws Exception
    {
        return UpcCallIntf.querySpServiceAndOfferByCond(serviceId, serviceName, bizTypeCode, spCode, pagination);
    }

    /**
     * 根据SPCODE,BIZTYPECODE,ORGDOMAIN查询TD_B_PLATSVC表
     * 
     * @param SpCode
     * @param BizTypeCode
     * @param OrgDomain
     *            SP_CODE为模糊查询
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public static IDataset querySpServiceBySpCodeAndBizTypeCodeAndOrgDomain(String SpCode, String BizTypeCode, String OrgDomain) throws Exception
    {
        return UpcCallIntf.querySpServiceBySpCodeAndBizTypeCodeAndOrgDomain(SpCode, BizTypeCode, OrgDomain);
    }

    public static IDataset querySpServiceBySpCodeAndBizCodeAndBizStateCode(String SpCode, String BizCode) throws Exception
    {
        return UpcCallIntf.querySpServiceBySpCodeAndBizCodeAndBizStateCode(SpCode, BizCode);
    }

    public static IDataset qryCataLogsByTypeRootLevel(String catalogType, String root, String catalogLevel) throws Exception
    {
        return UpcCallIntf.qryCataLogsByTypeRootLevel(catalogType, root, catalogLevel);
    }

    public static IDataset qryCatalogsByUpCatalogId(String upcatalogId) throws Exception
    {
        return UpcCallIntf.qryCatalogsByUpCatalogId(upcatalogId, null);
    }

    public static IDataset qryCatalogsByUpCatalogId(String upcatalogId, Pagination pagin) throws Exception
    {
        return UpcCallIntf.qryCatalogsByUpCatalogId(upcatalogId, pagin);
    }

    public static IDataset qryCatalogByCatalogId(String catalogId) throws Exception
    {
        return UpcCallIntf.qryCatalogByCatalogId(catalogId);
    }
    
    public static IData qryCatalogByCatalogIdAndUpCatalogId(String catalogId, String upCatalogId) throws Exception
    {
        IDataset catalogs =  UpcCallIntf.qryCatalogByCatalogId(catalogId);
        if(IDataUtil.isNotEmpty(catalogs))
        {
            for(Object obj : catalogs)
            {
                IData catalog = (IData) obj;
                String temUpCatalogId = catalog.getString("UP_CATALOG_ID");
                if(temUpCatalogId.equals(upCatalogId))
                {
                    return catalog;
                }
            }
        }
        
        return null;
    }

    public static IDataset qryCatalogByoffertIdAndupCatalogId(String upCatalogId, String offerCode, String offerType) throws Exception
    {
        return UpcCallIntf.qryCatalogByoffertIdAndupCatalogId(upCatalogId, offerCode, offerType);
    }

    public static IDataset queryAllOfferEnablesByOfferId(String productId) throws Exception
    {
        return UpcCallIntf.queryAllOfferEnablesByOfferId(productId);
    }

    public static IDataset queryAllOfferEnablesByOfferIdAndRelType(String productId, String relType) throws Exception
    {
        return UpcCallIntf.queryAllOfferEnablesByOfferIdAndRelType(productId, relType);
    }
    
    /**
     * 查询查询产品下所有的元素，包括已经失效的
     * @param productId
     * @param relType
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public static IDataset queryNeglectDateAllOfferEnablesByOfferId(String productId, String relType) throws Exception
    {
        return UpcCallIntf.queryNeglectDateAllOfferEnablesByOfferId(productId, relType);
    }

    /**
     * 
     * @Title: qryOffersByCatalogId
     * @Description: 查询指定目录ID下的商品
     * @param @param catalogId
     * @param @return
     * @param @throws Exception
     * @return IDataset
     * @throws
     * @author longtian3
     */
    public static IDataset qryOffersByCatalogId(String catalogId) throws Exception
    {
        IDataset results = UpcCallIntf.qryOffersByCatalogId(catalogId, null);
        if(IDataUtil.isNotEmpty(results)){
        	for (int i = 0; i < results.size(); i++)
            {
                IData result = results.getData(i);
                if(IDataUtil.isNotEmpty(result)){
                	result.put("PACKAGE_ID", result.getString("OFFER_CODE"));
                    result.put("PACKAGE_NAME", result.getString("OFFER_NAME"));
                    result.put("PRODUCT_ID", catalogId);
                    result.put("PACKAGE_DESC", result.getString("OFFER_NAME"));
                    result.put("RSRV_STR5", result.getString("RSRV_STR5"));
                }
            }
        }
        

        return results;
    }
    
    public static IDataset qryOffersByCatalogIdAll(String catalogId) throws Exception
    {

        IDataset results = UpcCallIntf.qryOffersByCatalogIdAll(catalogId, null);
        if(IDataUtil.isNotEmpty(results)){
        	for (int i = 0; i < results.size(); i++)
            {
                IData result = results.getData(i);
                if(IDataUtil.isNotEmpty(result)){
                	result.put("PACKAGE_ID", result.getString("OFFER_CODE"));
                    result.put("PACKAGE_NAME", result.getString("OFFER_NAME"));
                    result.put("PRODUCT_ID", catalogId);
                }
            }
        }
        
        return results;
    }

    public static IData queryCateByCateId(String catalogId) throws Exception
    {
        IDataset result = UpcCallIntf.queryCateByCateId(catalogId);
        if (IDataUtil.isNotEmpty(result))
        {
            return result.getData(0);
        }
        else
        {
            return null;
        }
    }

    /**
     * 
     * @Title: qryOfferFromSaleActiveByOfferId
     * @Description: 查询offerCode下的元素构成
     * @param @param offerCode
     * @param @param offerType
     * @param @return
     * @param @throws Exception
     * @return IDataset
     * @throws
     * @author longtian3
     */
    public static IDataset qryOfferFromSaleActiveByOfferId(String offerCode, String offerType) throws Exception
    {
        return UpcCallIntf.qryOfferFromSaleActiveByOfferId(offerCode, offerType);
    }

    public static IDataset queryTempChaByOfferTable(String offerType, String offerCode, String fromTableName) throws Exception
    {
        return UpcCallIntf.queryTempChaByOfferTable(offerType, offerCode, fromTableName);
    }

    public static IDataset queryTempChaByOfferTableField(String offerType, String offerCode, String fromTableName, String fieldName) throws Exception
    {
        return UpcCallIntf.queryTempChaByOfferTableField(offerType, offerCode, fromTableName, fieldName);
    }

    public static IDataset qryOffersByCatalogId(String catalogId, Pagination pg) throws Exception
    {
        return UpcCallIntf.qryOffersByCatalogId(catalogId, pg);
    }

    public static IDataset qryCatalogByOfferId(String offerCode, String offerType) throws Exception
    {
        return UpcCallIntf.qryCatalogByOfferId(offerCode, offerType);
    }

    public static IDataset qryRelOfferByComRelOfferIdRelOfferId(String offerCode, String offerType, String relOfferCode, String relOfferType) throws Exception
    {
        return UpcCallIntf.qryRelOfferByComRelOfferIdRelOfferId(offerCode, offerType, relOfferCode, relOfferType);
    }

    public static IDataset qryOffersBySpCond(String spCode, String bizCode, String bizTypeCode) throws Exception
    {
        return UpcCallIntf.qryOffersBySpCond(spCode, bizCode, bizTypeCode);
    }

    public static IDataset queryChaSpecByfieldNameAndvalueAndOfferId(String offerType, String offerCode, String fieldName, String value) throws Exception
    {
        return UpcCallIntf.queryChaSpecByfieldNameAndvalueAndOfferId(offerType, offerCode, fieldName, value);
    }

    public static IDataset queryChaSpecValByOfferId(String serviceId) throws Exception
    {
        return UpcCallIntf.queryChaSpecValByOfferId(serviceId);
    }

    /**
     * *获取所有品牌列表
     * 
     *            必填
     * @throws Exception
     */

    public static IDataset queryBrandList(String fieldName) throws Exception
    {
        return UpcCallIntf.queryBrandList(fieldName);
    }

    /**
     * 根据家庭offerid返回成员offer
     * 
     * @param offerId
     * @return
     * @throws Exception
     */

    public static IDataset queryMebOffersByTopOfferId(String offerId) throws Exception
    {
        IDataset membRoleOffers = new DatasetList();
        IDataset membGroups = queryOfferGroupRelOfferId("P", offerId);

        if (IDataUtil.isNotEmpty(membGroups))
        {
            for (int i = 0; i < membGroups.size(); i++)
            {
                IData membGroup = membGroups.getData(i);

                String groupId = membGroup.getString("GROUP_ID");
                IDataset membOffers = queryGroupComRelByGroupId(groupId);

                if (IDataUtil.isNotEmpty(membOffers))
                {
                    for (int j = 0; j < membOffers.size(); j++)
                    {
                        IData memberOffer = membOffers.getData(j);
                        /* memberOffer.put("TOP_SELECT_FLAG", membGroup.getString("SELECT_FLAG")); */
                        memberOffer.put("ROLE_CODE", membGroup.getString("ROLE_CODE"));
                        /*
                         * memberOffer.put("TOP_OFFER_ID", membGroup.getString("OFFER_ID")); memberOffer.put("OFFER_NAME", queryOfferNameByOfferId(memberOffer.getString("OFFER_ID"))); */
                        membRoleOffers.add(memberOffer);
                    }
                }
            }
        }

        return membRoleOffers;
    }

    /**
     * 返回对应家庭成员角色Offer
     * 
     * @param offerId
     * @param roleCode
     * @return
     * @throws Exception
     */
    public static IDataset queryMebOffersByTopOfferIdRole(String offerId, String roleCode) throws Exception
    {
        IDataset membRoleOffers = new DatasetList();
        IDataset memberOffers = queryMebOffersByTopOfferId(offerId);

        if (IDataUtil.isNotEmpty(memberOffers))
        {
            if (!StringUtils.isBlank(roleCode))
            {
                for (int i = 0; i < memberOffers.size(); i++)
                {
                    IData memberOffer = memberOffers.getData(i);
                    if (StringUtils.equals(roleCode, memberOffer.getString("ROLE_CODE")))
                    {
                        membRoleOffers.add(memberOffer);
                    }
                }
            }
        }

        return membRoleOffers;
    }

    /**
     * 返回对应家庭成员角色Offer
     * 
     * @param offerId
     * @param offerType
     * @return
     * @throws Exception
     */
    public static IDataset queryMebOffersByTopOfferIdOfferType(String offerId, String offerType) throws Exception
    {
        IDataset membRoleOffers = new DatasetList();
        IDataset memberOffers = queryMebOffersByTopOfferId(offerId);

        if (IDataUtil.isNotEmpty(memberOffers))
        {
            if (!StringUtils.isBlank(offerType))
            {
                for (int i = 0; i < memberOffers.size(); i++)
                {
                    IData memberOffer = memberOffers.getData(i);
                    if (StringUtils.equals(offerType, memberOffer.getString("OFFER_TYPE")))
                    {
                        membRoleOffers.add(memberOffer);
                    }
                }
            }
        }

        return membRoleOffers;
    }

    /**
     * 返回对应家庭成员角色特定类型Offer
     * 
     * @param offerId
     * @param roleCode
     * @param offerType
     * @return
     * @throws Exception
     */
    public static IDataset queryMebOffersByTopOfferIdRole(String offerId, String roleCode, String offerType) throws Exception
    {
        IDataset membRoleOffers = new DatasetList();
        IDataset memberOffers = queryMebOffersByTopOfferId(offerId);

        if (IDataUtil.isNotEmpty(memberOffers))
        {
            if (!StringUtils.isBlank(roleCode))
            {
                for (int i = 0; i < memberOffers.size(); i++)
                {
                    IData memberOffer = memberOffers.getData(i);
                    if (StringUtils.equals(roleCode, memberOffer.getString("ROLE_CODE")))
                    {
                        if (!StringUtils.isBlank(offerType))
                        {
                            if (StringUtils.equals(offerType, memberOffer.getString("OFFER_TYPE")))
                            {
                                String offerCode = memberOffer.getString("OFFER_CODE");
                                IData offer = queryOfferByOfferId(offerType, offerCode);
                                if (IDataUtil.isNotEmpty(offer))
                                {
                                    memberOffer.putAll(offer);
                                    if (StringUtils.equals(offerType, "D"))
                                    {
                                        memberOffer.put("DISCNT_CODE", offer.getString("OFFER_CODE"));
                                        memberOffer.put("DISCNT_NAME", offer.getString("OFFER_NAME"));
                                    }
                                    else if (StringUtils.equals(offerType, "S"))
                                    {
                                        memberOffer.put("SERVICE_ID", offer.getString("OFFER_CODE"));
                                        memberOffer.put("SERVICE_NAME", offer.getString("OFFER_NAME"));
                                    }
                                }
                                membRoleOffers.add(memberOffer);
                            }
                        }
                    }
                }
            }
        }
        return membRoleOffers;
    }

    /**
     * 返回productMode下全部某类型的元素
     * 
     * @param productMode
     * @param offerType
     * @return
     * @throws Exception
     */
    public static IDataset queryMembOffersByProdMode(String productMode, String offerType) throws Exception
    {
        IDataset memberOffers = new DatasetList();
        IDataset productList = UpcCall.queryProductInfosByMode("PRODUCT_MODE", productMode);
        if (IDataUtil.isNotEmpty(productList))
        {
            int size = productList.size();
            for (int i = 0; i < size; i++)
            {
                IData prodData = productList.getData(i);
                String productId = prodData.getString("PRODUCT_ID");

                IDataset discntList = queryMebOffersByTopOfferIdOfferType(productId, offerType);
                if (IDataUtil.isNotEmpty(discntList))
                {
                    memberOffers.addAll(discntList);
                }
            }
        }
        return memberOffers;
    }

    public static IDataset queryMembOffersByProdModeRole(String productMode, String offerType, String roleCode) throws Exception
    {
        IDataset memberOffers = new DatasetList();
        IDataset tempOffers = queryMembOffersByProdMode(productMode, offerType);
        if (IDataUtil.isNotEmpty(tempOffers))
        {
            int size = tempOffers.size();
            for (int i = 0; i < size; i++)
            {
                IData offer = tempOffers.getData(i);
                if (StringUtils.equals(roleCode, offer.getString("ROLE_CODE")))
                {
                    memberOffers.add(offer);
                }
            }
        }
        return memberOffers;
    }

    /**
     * 
     * @Title: qryOffersByCatalogId
     * @Description: 查询指定目录ID下的指定商品(查询产品下是否有此营销包)
     * @param @param catalogId
     * @param @return
     * @param @throws Exception
     * @return IDataset
     * @throws
     * @author longtian3
     */
    public static IDataset qryOffersByCatalogIdAndOfferId(String catalogId, String offerCode, String offerType) throws Exception
    {
        IDataset result = new DatasetList();
        IDataset offers = UpcCallIntf.qryOffersByCatalogId(catalogId, null);
        for (int i = 0; i < offers.size(); i++)
        {
            IData offer = offers.getData(i);
            String strOfferCode = offer.getString("OFFER_CODE");
            String strOfferType = offer.getString("OFFER_TYPE");
            if (offerCode.equals(strOfferCode) && offerType.equals(strOfferType))
            {
                offer.put("PACKAGE_ID", strOfferCode);
                offer.put("PACKAGE_NAME", offer.getString("OFFER_NAME"));
                offer.put("PRODUCT_ID", catalogId);
                result.add(offer);
                break;
            }
        }

        return result;
    }

    public static IData qryGiftByOfferIdSourceId(String offerType, String offerCode, String pricePlanId) throws Exception
    {
        IDataset results = UpcCallIntf.qryGiftByOfferIdSourceId(offerType, offerCode, pricePlanId);
        if (IDataUtil.isNotEmpty(results))
        {
            return results.getData(0);
        }
        else
        {
            return new DataMap();
        }
    }

    /**
     * 对应查询老系统td_b_platsvc_limit表数据，PM_OFFER_REL
     * 
     * @param offerCode
     * @param offerType
     * @param operCode
     * @param bizTypeCode
     * @param relType
     * @return
     * @throws Exception
     */
    public static IDataset qryOfferByRelOfferIdAndBizTypeCodeAndAndRelTypeAndOperCode(String offerCode, String offerType, String operCode, String bizTypeCode, String relType) throws Exception
    {
        return UpcCallIntf.qryOfferByRelOfferIdAndBizTypeCodeAndAndRelTypeAndOperCode(offerCode, offerType, operCode, bizTypeCode, relType);
    }

    /**
     * 对应查询老系统td_b_platsvc_limit表数据，PM_OFFER_REL
     * 
     * @param offerCode
     * @param offerType
     * @param bizTypeCode
     * @param operCode
     * @return
     * @throws Exception
     */
    public static IDataset qryOfferByOfferIdAndBizTypeCodeAndOperCode(String offerCode, String offerType, String bizTypeCode, String operCode) throws Exception
    {
        return UpcCallIntf.qryOfferByOfferIdAndBizTypeCodeAndOperCode(offerCode, offerType, bizTypeCode, operCode);
    }

    /**
     * 对应查询老系统td_b_platsvc表数据
     * 
     * @param offerCode
     * @param offerType
     * @param bizTypeCode
     * @param bizCode
     * @return
     * @throws Exception
     */
    public static IDataset querySpServiceParamByCond(String offerCode, String offerType, String spCode, String bizTypeCode, String bizCode) throws Exception
    {
        return UpcCallIntf.querySpServiceParamByCond(offerCode, offerType, spCode, bizTypeCode, bizCode);
    }

    /**
     * 
     * @Title: qryOfferComChaTempChaByCond
     * @Description: 查询OFFER其他字段信息
     * @param @param offerCode
     * @param @param offerType
     * @param @return
     * @param @throws Exception
     * @return IDataset
     * @throws
     * @author
     */
    public static IData qryOfferComChaTempChaByCond(String offerCode, String offerType) throws Exception
    {
        return UpcCallIntf.qryOfferComChaTempChaByCond(offerCode, offerType);
    }

    /**
     * 
     * @Title: qryOfferComChaTempChaByCond
     * @Description: 查询OFFER其他字段信息
     * @param @param offerCode
     * @param @param offerType
     * @param @return
     * @param @throws Exception
     * @return IDataset
     * @throws
     * @author
     */
    public static IDataset qryOfferGiftByExtGiftId(String extGiftId) throws Exception
    {
        return UpcCallIntf.qryOfferGiftByExtGiftId(extGiftId);
    }

    /**
     * 
     * @Description: 查询TD_B_ELEMENT_LIMIT表对应的PM_OFFER_REL，依赖互斥关系
     * @param offerCodeA
     * @param offerTypeA
     * @param offerCodeB
     * @param offerTypeB
     * @param limitTag
     * @return IDataset
     * @throws
     * @author
     */
    public static boolean hasSpecificOfferRelThisTwoOffer(String offerCodeA, String offerTypeA, String offerCodeB, String offerTypeB, String limitTag) throws Exception
    {
        return UpcCallIntf.hasSpecificOfferRelThisTwoOffer(offerCodeA, offerTypeA, offerCodeB, offerTypeB, limitTag);
    }

    /**
     * @Description
     * @param offerCode
     * @param offerType
     * @param showMode
     * @return
     * @throws Exception
     */
    public static IDataset qryOfferChaSpecByOfferIdShowMode(String offerCode, String offerType, String showMode) throws Exception
    {
        return UpcCallIntf.qryOfferChaSpecByOfferIdShowMode(offerCode, offerType, showMode);
    }

    /**
     * @Description 根据service_id查询TD_B_PLATSVC A, TD_M_SP_BIZ
     * @param offerCode
     * @param offerType
     * @return
     * @throws Exception
     */
    public static IDataset qrySpInfoByOfferId(String offerCode, String offerType) throws Exception
    {
        // UPC.Out.SpQueryFSV.qrySpInfoByOfferId
        return UpcCallIntf.qrySpInfoByOfferId(offerCode, offerType);
    }

    /**
     * @Description 查询pm_offer_rel(TD_B_PLATSVC_LIMIT)表
     * @param offerCode
     * @param offerType
     * @param bizTypeCode
     * @param operCode
     * @return
     * @throws Exception
     */
    public static IDataset qrySpRelByAnyOfferIdBizTypeCodeOperCode(String offerCode, String offerType, String bizTypeCode, String operCode) throws Exception
    {
        // UPC.Out.SpQueryFSV.qrySpRelByAnyOfferIdBizTypeCodeOperCode
        return UpcCallIntf.qrySpRelByAnyOfferIdBizTypeCodeOperCode(offerCode, offerType, bizTypeCode, operCode);
    }

    /**
     * @Description 根据NEW_SP_CODE ，NEW_BIZ_CODE查询TD_B_OFFICEDATA_RELATION
     * @param spCode
     * @param bizCode
     * @return
     * @throws Exception
     */
    public static IDataset qryNewSpOfficeData(String spCode, String bizCode) throws Exception
    {
        // UPC.Out.SpQueryFSV.qryNewSpOfficeData
        return UpcCallIntf.qryNewSpOfficeData(spCode, bizCode);
    }

    /**
     * @Description 根据OLD_SP_CODE ，OLD_BIZ_CODE查询TD_B_OFFICEDATA_RELATION
     * @param spCode
     * @param bizCode
     * @return
     * @throws Exception
     */
    public static IDataset qryOldSpOfficeData(String spCode, String bizCode) throws Exception
    {
        // UPC.Out.SpQueryFSV.qryOldSpOfficeData
        return UpcCallIntf.qryOldSpOfficeData(spCode, bizCode);
    }

    /**
     * 根据产品品牌查询品牌名称
     * 
     * @param brandCode
     * @return
     * @throws Exception
     *             duhj
     */
    public static IDataset queryProdSpecByBrandCodeAndProductMode(String brandCode) throws Exception
    {
        return UpcCallIntf.queryProdSpecByBrandCodeAndProductMode(brandCode);
    }

    /**
     * 查询产品信息
     * 
     * @param productId
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static IData qryTotalOfferInfoByOfferId(String offertType, String offerCode) throws Exception
    {
        return UpcCallIntf.qryTotalOfferInfoByOfferId(offertType, offerCode);
    }

    /**
     * 根据产品类型获取产品 关联td_s_product_trans 产品变更用
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryJoinRelOfferInfoByOfferIdCatalogId(String offertType, String offerCode, String catalogId) throws Exception
    {
        return UpcCallIntf.qryJoinRelOfferInfoByOfferIdCatalogId(offertType, offerCode, catalogId);
    }

    /**
     * @Description 根据offer_code查询(td_s_servicestate)表
     * @param offerCode
     * @param offerType
     * @param funcStatus
     * @return
     * @throws Exception
     */
    public static IDataset qryOfferFuncStaByAnyOfferIdStatus(String offerCode, String offerType, String funcStatus) throws Exception
    {
        return UpcCallIntf.qryOfferFuncStaByAnyOfferIdStatus(offerCode, offerType, funcStatus);
    }

    /**
     * @Description 根据offer_code查询(td_b_platsvc_param)表
     * @param spCode
     * @param bizCode
     * @param bizTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset querySpServiceParamByCond(String spCode, String bizCode, String bizTypeCode, Pagination page) throws Exception
    {
        return UpcCallIntf.querySpServiceParamByCond(spCode, bizCode, bizTypeCode, page);
    }

    /**
     * 对应查询老系统td_b_platsvc表数据 duhj 2017/03/13
     * 
     * @param offerCode
     * @param offerType
     * @param bizTypeCode
     * @param bizStateCode
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset queryPlatSvc(String offerCode, String offerType, String bizTypeCode, String bizStateCode) throws Exception
    {
        return UpcCallIntf.queryPlatSvc(offerCode, offerType, bizTypeCode, bizStateCode);
    }

    public static IDataset queryOfferRelByRelOfferIdAndRelType(String offerCode, String offerType, String relType) throws Exception
    {
        return UpcCallIntf.queryOfferRelByRelOfferIdAndRelType(offerCode, offerType, relType);
    }

    /**
     * 规则相关接口 依赖互斥
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     */

    public static IDataset queryOfferJoinRelBy2OfferIdRelType(String offerType, String offerCode, String relOfferType, String relOfferCode) throws Exception
    {
        return UpcCallIntf.queryOfferJoinRelBy2OfferIdRelType(offerType, offerCode, relOfferType, relOfferCode);
    }
    
    /**
     * 规则相关接口 查询主套餐变更内容信息2018/09/10-wangsc10
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     */

    public static IDataset qryRelOfferEnableByOfferIdRelOfferId(String offerType, String offerCode, String relOfferType, String relOfferCode) throws Exception
    {
        return UpcCallIntf.qryRelOfferEnableByOfferIdRelOfferId(offerType, offerCode, relOfferType, relOfferCode);
    }

    /**
     * 规则相关接口 依赖互斥
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     */

    public static IDataset queryOfferRelByOfferIdAndRelType(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferRelByOfferIdAndRelType(offerType, offerCode);
    }

    public static IDataset queryOfferRelByOfferIdAndRelType(String offerType, String offerCode, String relType) throws Exception
    {
        return UpcCallIntf.queryOfferRelByOfferIdAndRelType(offerType, offerCode, relType);
    }

    /**
     * 规则相关接口 依赖互斥
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     */

    public static IDataset queryOfferRelByRelOfferIdAndRelType(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferRelByRelOfferIdAndRelType(offerType, offerCode);
    }

    /**
     * 规则相关接口 依赖互斥
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     */
    public static IDataset qryOfferChaSpecsByOfferIdIsNull(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.qryOfferChaSpecsByOfferIdIsNull(offerType, offerCode);
    }

    /**
     * @Description 根据产品编码查询产品模型下【默认】优惠、服务
     * @param offerCode
     * @param offerType
     * @param funcStatus
     * @return
     * @throws Exception
     */
    public static IDataset qrySpecifyTypeOfferByOfferIdAndType(String offerType, String offerCode, String elementType) throws Exception
    {
        return UpcCallIntf.qrySpecifyTypeOfferByOfferIdAndType(offerType, offerCode, elementType);
    }

    /**
     * 查询销售品下必选包下的必选元素和默认元素
     * 
     * @param offerType
     * @param offerCode
     * @param elementTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryAtomOffersFromGroupByOfferId(String offerType, String offerCode, String elementTypeCode) throws Exception
    {
        return UpcCallIntf.qryAtomOffersFromGroupByOfferId(offerType, offerCode, elementTypeCode);

    }

    /**
     * 根据discntTypeCode查询用户特定类型的可选优惠
     * 
     * @param offerType
     *            不必填
     * @param offerCode
     *            不必填
     * @param discntTypeCode
     *            必填
     * @return
     * @throws Exception
     */
    public static IDataset queryPackageElementsByProductIdDisctypeCode(String offerType, String offerCode, String discntTypeCode) throws Exception
    {
        return UpcCallIntf.queryPackageElementsByProductIdDisctypeCode(offerType, offerCode, discntTypeCode);

    }

    public static IDataset qryOfferTempChasByCatalogIdOfferId(String offerType, String offerCode, String catalogId, String upCatalogId, String resId) throws Exception
    {
        return UpcCallIntf.qryOfferTempChasByCatalogIdOfferId(offerType, offerCode, catalogId, upCatalogId, resId);
    }

    public static IDataset qryOfferTempChasByCatalogId(String catalogId, String upCatalogId, String resId) throws Exception
    {
        return qryOfferTempChasByCatalogIdOfferId(null, null, catalogId, upCatalogId, resId);
    }

    public static IData queryTransProducts(String productId) throws Exception
    {
        return UpcCallIntf.queryTransProducts(productId);
    }

    public static IData queryProductsByCatalogId(String catalogId) throws Exception
    {
        return UpcCallIntf.queryProductsByCatalogId(catalogId);
    }

    /**
     * 
     * @Title: qryOfferPkgExtByOfferId
     * @Description: 查询TD_B_PACKAGE_EXT预留字段和生失效方式
     * @param @param offerCode
     * @param @return
     * @param @throws Exception
     * @return IData
     * @throws
     * @author longtian3
     */
    public static IData qryOfferPkgExtByOfferId(String offerCode) throws Exception
    {
        IDataset offerChas = queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, offerCode, "TD_B_PACKAGE_EXT");
        IData result = offerChas.getData(0);

        IDataset offerMode = queryOfferEnableModeByCond(BofConst.ELEMENT_TYPE_CODE_PACKAGE, offerCode);

        result.putAll(offerMode.getData(0));

        return result;
    }

    /**
     * @Description 根据offer_code查询(td_m_sp_info,td_m_sp_biz)表 guyan
     * @param spCode
     * @param bizCode
     * @param bizTypeCode
     * @param spStatus
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qrySpServiceSpInfo(String spCode, String bizCode, String bizTypeCode, String spStatus) throws Exception
    {
        return UpcCallIntf.qrySpServiceSpInfo(spCode, bizCode, bizTypeCode, spStatus);
    }

    /**
     * 
     * @Title: qryAllOffersByOfferIdWithForceTagDefaultTagFilter
     * @Description: 查询包下默认或必选元素
     * @param @param offerType
     * @param @param offerCode
     * @param @param forceTag
     * @param @param defaultTag
     * @param @return
     * @param @throws Exception
     * @return IDataset
     * @throws
     * @author longtian3
     */
    public static IDataset qryAllOffersByOfferIdWithForceTagDefaultTagFilter(String offerType, String offerCode, String forceTag, String defaultTag) throws Exception
    {
        return UpcCallIntf.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(offerType, offerCode, forceTag, defaultTag);
    }

    public static IDataset queryOfferInfoWithOfferChaByOfferCodes(String offerCodes, String offerTypes) throws Exception
    {
        return UpcCallIntf.queryOfferInfoWithOfferChaByOfferCodes(offerCodes, offerTypes);
    }

    public static IData queryOfferEnableMode(String productId, String groupId, String elementId, String elementType) throws Exception
    {
        return UpcCallIntf.queryOfferEnableMode(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, groupId, elementId, elementType);
    }

    public static IData querySaleOfferEnableMode(String packageId, String groupId, String elementId, String elementType) throws Exception
    {
        return UpcCallIntf.queryOfferEnableMode(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, groupId, elementId, elementType);
    }

    public static IDataset queryAtomOffersFromGroupByOfferIdType(String offerCode, String offerType) throws Exception
    {
        return UpcCallIntf.queryAtomOffersFromGroupByOfferIdType(offerCode, offerType);
    }

    public static IDataset qrySaleActiveCatalogByFactor(String condFactor3) throws Exception
    {
        return UpcCallIntf.qrySaleActiveCatalogByFactor(condFactor3);
    }

    public static IDataset qryTerminalSaleActiveCatalog() throws Exception
    {
        return UpcCallIntf.qryTerminalSaleActiveCatalog();
    }
    
    public static IDataset qryTerminalSaleActiveCatalogAll() throws Exception
    {
        return UpcCallIntf.qryTerminalSaleActiveCatalogAll();
    }
    
    public static IDataset qryTerminalSaleActiveOffer() throws Exception
    {
        return UpcCallIntf.qryTerminalSaleActiveOffer();
    }

    public static IDataset qryTerminalOffersByPkgExtRsrvstrCatalogId(String productId,String deviceTypeCode) throws Exception
    {
        return UpcCallIntf.qryTerminalOffersByPkgExtRsrvstrCatalogId(productId, deviceTypeCode);
    }
    
    public static IDataset qryOfferExtChasByCatalogIdOfferId(String productId) throws Exception
    {
        return UpcCallIntf.qryOfferExtChasByCatalogIdOfferId(productId);
    }

    /**
     * 根据offer_code获取 from_table_name 表的特定字段 StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_B_PRODUCT","PRODUCT_ID","RSRV_STR1",productId);
     * 
     * @author hefeng
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getTableNameValue(String from_table_name, String field_name, String offer_type, String offer_code) throws Exception
    {
        return UpcCallIntf.getTableNameValue(from_table_name, field_name, offer_type, offer_code);
    }

    /**
     * 根据父级目录查询子级目录 catalogLevel 可选择是第几级目录，可为空
     */
    public static IDataset queryLevelCatalogByUpCatalogId(String upCatalogId, String level) throws Exception
    {
        IDataset dataset = UpcCallIntf.queryLevelCatalogByUpCatalogId(upCatalogId);
        if (IDataUtil.isNotEmpty(dataset))
        {
            if (StringUtils.isNotBlank(level) && StringUtils.isNotEmpty(level))
            {
                for (int i = 0; i < dataset.size(); i++)
                {
                    String catalog_level = dataset.getData(i).getString("CATALOG_LEVEL");
                    if (!(catalog_level.equals(level)))
                    {
                        dataset.remove(i);
                        i--;
                    }
                }
            }
        }
        return dataset;
    }

    /**
     * 根据 packageId查询td_b_package_element下元素
     * 
     * @author hefeng
     * @param groupid
     * @return
     * @throws Exception
     */
    public static IDataset getPackageElementInfoByPorductIdElementId(String productId, String offer_type, String elementId, String element_type) throws Exception
    {
        return UpcCallIntf.getPackageElementInfoByPorductIdElementId(productId, offer_type, elementId, element_type);
    }
    
    /**
     * 根据offer_code获取 from_table_name 表字段 
     * 
     * @author hefeng
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset queryTempChasByChaIdTableName(String from_table_name, String offer_type, String offer_code) throws Exception
    {
        return UpcCallIntf.queryTempChasByChaIdTableName(from_table_name, offer_type, offer_code);
    }

    /**
     * duhj 查询A产品是否可以转换成B产品,对应原表 TD_S_PRODUCT_TRANS
     * 
     * @param offerCode
     * @param offerType
     * @param rel_offerCode
     * @param rel_offerType
     * @return
     * @throws Exception
     */
    public static IData queryOfferTransOffer(String offerCode, String offerType, String rel_offerCode, String rel_offerType) throws Exception
    {

        return UpcCallIntf.queryOfferTransOffer(offerCode, offerType, rel_offerCode, rel_offerType);
    }

    public static IDataset qryChildrenCatalogsByIdLevel(String catalogLevel, String catalogId) throws Exception
    {
        return UpcCallIntf.qryChildrenCatalogsByIdLevel(catalogLevel, catalogId);
    }

    public static IDataset qryOfferByOfferIdRelOfferId(String offerCode, String offerType, String relOfferCode, String relOfferType) throws Exception
    {
        return UpcCallIntf.qryOfferByOfferIdRelOfferId(offerCode, offerType, relOfferCode, relOfferType, null);
    }
    
    public static IDataset qryOfferByOfferIdRelOfferId(String offerCode, String offerType, String relOfferCode, String relOfferType, String queryCha) throws Exception
    {
        return UpcCallIntf.qryOfferByOfferIdRelOfferId(offerCode, offerType, relOfferCode, relOfferType, queryCha);
    }

    public static IDataset qryOfferExtChaByOfferId(String offerCode, String offerType, String fromTableName) throws Exception
    {
        return UpcCallIntf.qryOfferExtChaByOfferId(offerCode, offerType, fromTableName);
    }

    public static IDataset qryPricePlanInfoByOfferId(String offerCode, String offerType) throws Exception
    {
        return UpcCallIntf.qryPricePlanInfoByOfferId(offerCode, offerType);
    }
    
    public static IDataset qryServInfoByrsrvStr(String rsrvstr9) throws Exception
    {
        return UpcCallIntf.qryServInfoByrsrvStr(rsrvstr9);
        
    }
    
    /**BUG20190226160500产品变更短信提醒内容存在bug,产品内容有null值展示在短信里。具体见附件，请优化。wangsc10-20190228-根据SERVICE_ID查
     * @author 
     * @param SERVICE_ID
     * @return
     * @throws Exception
     */
    public static IDataset qryServInfoByServiceId(String serviceId) throws Exception
    {
        return UpcCallIntf.qryServInfoByServiceId(serviceId);
        
    }
    
    /**查询特殊包下面的元素，不是营销活动的包
     * @author hefeng
     * @param groupId
     * @return
     * @throws Exception
     */
    public static IDataset getElementInfoByGroupId(String groupId) throws Exception
    {
     return UpcCallIntf.getElementInfoByGroupId(groupId);
    }


    /**
     * 根据offer_code查询(TD_B_PLATSVC,TD_M_SP_INFO,TD_M_SP_BIZ)表 add by duhj
     * 
     * @param offerCode
     * @param offerType
     * @return
     * @throws Exception
     */
    public static IDataset qrySpServiceSpInfo(String offerCode, String offerType) throws Exception
    {
        return UpcCallIntf.qrySpServiceSpInfo(offerCode, offerType);
    }

    public static IDataset qryCatalogsByCatalogIdCatalogLevel(String catalogIdA, String catalogId, String catalogLevel) throws Exception
    {
        return UpcCallIntf.qryCatalogsByCatalogIdCatalogLevel(catalogIdA, catalogId, catalogLevel);
    }

    /**
     * add by duhj TD_B_ELEMENT_LIMIT表对应的PM_OFFER_REL，查询A元素所依赖元素
     * 
     * @param offerCodeA
     * @param offerTypeA
     * @param limitTag
     * @return
     * @throws Exception
     */
    public static IDataset qryOfferRelOfferByOfferIdWithRelTypeFilter(String offerCodeA, String offerTypeA, String limitTag) throws Exception
    {
        return UpcCallIntf.qryOfferRelOfferByOfferIdWithRelTypeFilter(offerCodeA, offerTypeA, limitTag);

    }

    /**
     * 
     * @Title: qryRelOfferExtChaByOfferIdRelOfferId
     * @Description: 查询TD_B_PACKAGE_ELEMENT拓展字段
     * @param @param offerCode
     * @param @param offerType
     * @param @param relOfferCode
     * @param @param relOfferType
     * @param @return
     * @param @throws Exception
     * @return IDataset
     * @throws
     * @author longtian3
     */
    public static IDataset qryRelOfferExtChaByOfferIdRelOfferId(String offerCode, String offerType, String relOfferCode, String relOfferType) throws Exception
    {
        return UpcCallIntf.qryRelOfferExtChaByOfferIdRelOfferId(offerCode, offerType, relOfferCode, relOfferType);
    }

    /**
     * 
     * @Title: qryOfferGiftsByOfferId
     * @Description: 根据OFFER_ID查询OFFER_GIFT
     * @param @param offerCode
     * @param @param offerType
     * @param @return
     * @param @throws Exception
     * @return IDataset
     * @throws
     * @author longtian3
     */
    public static IDataset qryOfferGiftsByOfferId(String offerCode, String offerType) throws Exception
    {
        return UpcCallIntf.qryOfferGiftsByOfferId(offerCode, offerType);
    }

    /**
     * 
     * @Title: qryOfferNamesByOfferTypesOfferCodes
     * @Description: 根据offer_code和offer_type的set批量查询offer_name
     * @param @param offer_type_code_list
     * @param @return
     * @param @throws Exception 设定文件
     * @return IDataset 返回类型
     * @throws
     */
    public static IDataset qryOfferNamesByOfferTypesOfferCodes(IDataset offer_type_code_list, String queryCatalogId) throws Exception
    {
        return UpcCallIntf.qryOfferNamesByOfferTypesOfferCodes(offer_type_code_list, queryCatalogId);
    }

    /**
     * 
     * @Title: qryOfferByOfferTypeOfferCode  
     * @Description: TODO(这里用一句话描述这个方法的作用)  
     * @param @param offerCode
     * @param @param offerType
     * @param @return
     * @param @throws Exception    设定文件  
     * @return IData    返回类型  
     * @throws
     */
    public static String qryOfferNameByOfferTypeOfferCode(String offerCode, String offerType) throws Exception
    {
        IDataset offer_type_code_list = new DatasetList();

        IData iData = new DataMap();
        iData.put("OFFER_CODE", offerCode);
        iData.put("OFFER_TYPE", offerType);
        offer_type_code_list.add(iData);

        IDataset results = UpcCallIntf.qryOfferNamesByOfferTypesOfferCodes(offer_type_code_list, null);
        if (IDataUtil.isEmpty(results))
        {
            return "";
        }

        return results.getData(0).getString("OFFER_NAME");
    }
    
    public static IDataset qryEnableModeInfoByRelObjectAndId(String relObj, String relObjId) throws Exception
    {
        return UpcCallIntf.qryEnableModeInfoByRelObjectAndId(relObj, relObjId);
    }
    
    public static IDataset queryGroupComRel(String groupId, String offerType, String offerCode) throws Exception{
    	return UpcCallIntf.queryGroupComRel(groupId, offerType, offerCode);
    }
    
/**查询 TD_B_ELEMENT_LIMIT
 * @author hefeng
 * @param elementA
 * @param elementB
 * @param element_type_codeA
 * @param element_type_codeB
 * @return
 * @throws Exception
 */
    public static IDataset queryRelByElementAElementB(String elementA, String elementB, String element_type_codeA,String element_type_codeB) throws Exception{
    	return UpcCallIntf.queryRelByElementAElementB(elementA, elementB, element_type_codeA, element_type_codeB);
    }
   
    public static IDataset qryOffersWithOfferTypeFilter(String offerType) throws Exception{
    	return UpcCallIntf.qryOffersWithOfferTypeFilter(offerType);
    }
    
    public static IDataset queryPlatSvc2(String spCode, String bizCode, String bizTypeCode, String bizStateCode) throws Exception
    {
        return UpcCallIntf.queryPlatSvc2(spCode, bizCode, bizTypeCode, bizStateCode);
    }
    public static IDataset qryOffersByBrandWithProductModeFilter(String brandCode,String productMode) throws Exception{
    	return UpcCallIntf.qryOffersByBrandWithProductModeFilter(brandCode,productMode);
    }
    
    public static IDataset queryTerrace(String spName, String bizName, String bizTypeCode, String maxRetrue) throws Exception
    {
    	return UpcCallIntf.queryTerrace(spName, bizName, bizTypeCode, maxRetrue);
    }
    
    /**
     * 查询平台业务信息通过SP_CODE,BIZ_CODE
     * 
     * @param spCode
     * @param bizCode
     * @return
     * @throws Exception
     */
    public static IDataset queryBizInfoBySpcodeBizCode(String spCode, String bizCode) throws Exception
    {
    	return UpcCallIntf.queryBizInfoBySpcodeBizCode(spCode, bizCode);
    }
    
    /**
     * 通过SP_NAME,BIZ_NAME模糊查询平台业务信息
     * 
     * @param spName
     * @param bizName
     * @return
     * @throws Exception
     */
    public static IDataset queryBizInfoBySpNameBizName(String spName, String bizName) throws Exception
    {
    	return UpcCallIntf.queryBizInfoBySpNameBizName(spName, bizName);
    }
    
    public static IDataset queryOfferRelInfoByTwoOfferOrInversionIfNecessary(String offerType, String offerCode, String relOfferType, String relOfferCode, String relType) throws Exception{
    	return UpcCallIntf.queryOfferRelInfoByTwoOfferOrInversionIfNecessary(offerType,offerCode,relOfferType,relOfferCode,relType);
    }
    
    public static IDataset qryNeglectDateOfferByOfferId(String offerType, String offerCode) throws Exception{
        return UpcCallIntf.qryNeglectDateOfferByOfferId(offerType, offerCode);
    }
    
    public static String queryPackageElementRsrv(String groupId, String offerType, String offerCode, String fieldName) throws Exception{
    	IDataset rst = UpcCallIntf.queryGroupComRel(groupId, offerType, offerCode);
    	if(IDataUtil.isEmpty(rst)){
    		return null;
    	}
    	
    	IData data = rst.getData(0);
    	String relId = data.getString("REL_ID");
    	IDataset result = UpcCallIntf.queryTempChaByCond(relId, "TD_B_PACKAGE_ELEMENT", fieldName);
    	if(IDataUtil.isEmpty(result)){
    		return null;
    	}
    	
    	return result.getData(0).getString("FIELD_VALUE");
    }
    
    public static IDataset queryOfferRelWithDiscntTypeFilter(String offerType, String offerCode, String relType, String discntTypeCode) throws Exception{
        return UpcCallIntf.queryOfferRelWithDiscntTypeFilter(offerType, offerCode, relType, discntTypeCode);
    }
    
    public static IDataset queryServiceByRsrvTag3(String offerType, String offerCode, String fromTableName, String fieldName) throws Exception
    {
    	return UpcCallIntf.queryServiceByRsrvTag3(offerType, offerCode, fromTableName, fieldName);
    }
    
    /**
     * 校园卡优惠信息查询
     * duhj
     * @param offerType
     * @param offerCode
     * @param discntTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset queryAllOffersByOfferIdWithDiscntTypeFilter(String offerType, String offerCode,String discntTypeCode) throws Exception{
        return UpcCallIntf.qryAllOffersByOfferIdWithDiscntTypeFilter(offerType, offerCode, discntTypeCode);
    }
    
    public static IDataset qryByServiceIdBillType(String offerType, String offerCode) throws Exception{
        return UpcCallIntf.qryByServiceIdBillType(offerType, offerCode);
    }
    
    //查询携号不能变更的商品表
    public static IDataset qryOfferLimitNpByOfferId(String offerType, String offerCode, String limitTag)throws Exception{
    	return UpcCallIntf.qryOfferLimitNpByOfferId(offerType, offerCode, limitTag);
    }
    
  //查询当前销售品下不能携转的元素（当前只比对了构成）
    public static IDataset qryOfferLimitNpOfferByOfferId(String offerType, String offerCode)throws Exception{
    	return UpcCallIntf.qryOfferLimitNpOfferByOfferId(offerType, offerCode);
    }
    
    public static IDataset qryGroupComRelExtChaByGroupIdOfferId(String groupId, String offerCode, String offerType) throws Exception
    {
        return UpcCallIntf.qryGroupComRelExtChaByGroupIdOfferId(groupId, offerCode, offerType);
    }
    
    /**
     * 查询绿色优惠
     * duhj
     * @param tableName
     * @param fieldName
     * @param fieldValue
     * @return
     * @throws Exception
     */
    public static IDataset qryOfferFromExtCha(String tableName, String fieldName, String fieldValue) throws Exception
    {
        return UpcCallIntf.qryOfferFromExtCha(tableName, fieldName, fieldValue);
    }
	
	public static IDataset qryOffersByOfferTypeLikeOfferName(String offerType, String offerCode, String offerName) throws Exception
    {
        return UpcCallIntf.qryOffersByOfferTypeLikeOfferName(offerType, offerCode, offerName);
    }
	
	public static IDataset qrySaleActiveCatalogs() throws Exception{
        return UpcCallIntf.qrySaleActiveCatalogs();
    }
	
	public static IDataset qryOffersByFixedCatalogId() throws Exception
	{
	    return UpcCallIntf.qryOffersByFixedCatalogId();
	}
	
	public static IDataset qryOfferCatalogByOfferId(String offerType, String offerCode) throws Exception
	{
		return UpcCallIntf.qryOfferCatalogByOfferId(offerType, offerCode);
	}
	
	public static IDataset qryOfferByOfferIdNameMode(String offerType, String offerCode, String name, String mode) throws Exception
	{
		return UpcCallIntf.qryOfferByOfferIdNameMode(offerType, offerCode, name, mode);
	}
	
	public static IDataset qrySaleActiveCompOfferPricPlan(String offerType, String offerCode, String relOfferType, String relOfferCode, String tradeTypeCode, String catalogId) throws Exception
	{
	    return UpcCallIntf.qrySaleActiveCompOfferPricPlan(offerType, offerCode, relOfferType, relOfferCode, tradeTypeCode, catalogId);
	}
	
	public static IDataset qryGroupInfoByGroupIdOfferId(String offerType, String offerCode, String groupId) throws Exception
	{
	    return UpcCallIntf.qryGroupInfoByGroupIdOfferId(offerType, offerCode, groupId);
	}
	
	public static IDataset qrySaleActiveOffersByResIdOfferIdCatalogId(String offerType, String offerCode, String catalogId, String upCatalogId, String resId) throws Exception
    {
        return UpcCallIntf.qrySaleActiveOffersByResIdOfferIdCatalogId(offerType, offerCode, catalogId, upCatalogId, resId);
    }
	
	public static IDataset querySpInfoServiceAndProdBySpBizInfo(String spCode)throws Exception
    {
        return UpcCallIntf.querySpInfoServiceAndProdBySpBizInfo(spCode);
    }
	
	public static IDataset qrySaleOfferTradeLimits(String catalogId, String offerCode)throws Exception
	{
	    return UpcCallIntf.qrySaleOfferTradeLimits(catalogId, offerCode);
	}
	
	public static IDataset queryExtChasOffers(String fromTableName, String fieldName, String fieldValue) throws Exception{
		return UpcCallIntf.queryExtChasOffers(fromTableName, fieldName, fieldValue);
	}
	
	public static IDataset qryGroupInfoByOfferIdAndGroupOfferId(String offerCode, String offerType, String relOfferCode, String relOfferType) throws Exception
	{
        return UpcCallIntf.qryGroupInfoByOfferIdAndGroupOfferId(offerCode, offerType, relOfferCode, relOfferType);
    }
	
	public static IDataset qrySaleTerminalLimit(String offerCode, String offerType, String catalogId, String terminalTypeCode, String terminalModeCode) throws Exception
    {
        return UpcCallIntf.qrySaleTerminalLimit(offerCode, offerType, catalogId, terminalTypeCode, terminalModeCode);
    }

	public static IDataset queryOfferEnableModeByGroupId(String groupId, String elementId, String elementType) throws Exception
    {
        return UpcCallIntf.queryOfferEnableModeByGroupId(groupId, elementId, elementType);
    }
	
	public static IDataset qryServiceRes(String offerCode, String offerType) throws Exception
	{         
        return UpcCallIntf.qryServiceRes(offerCode, offerType);
    }
	
	public static IDataset qryDynamicPrice(String offerCode, String offerType, String relOfferCode, String relOfferType, String tradeTypeCode, String inModeCode, String vipClassId, String groupId)throws Exception
	{
		return UpcCallIntf.qryDynamicPrice(offerCode, offerType, relOfferCode, relOfferType, tradeTypeCode, inModeCode, vipClassId, groupId);
	}
	
	public static IDataset qryDynamicPrice(String tradeTypeCode, String catalogId, String offerCode, String offerType, String relOfferCode, String relOfferType, String inModeCode) throws Exception
    {
        return UpcCallIntf.qryDynamicPrice(tradeTypeCode, catalogId, offerCode, offerType, relOfferCode, relOfferType, inModeCode);
    }
	
	public static IDataset qryContractMaterialOffer(String productMode, String paraCode, String eparchyCode) throws Exception 
	{
	    return UpcCallIntf.qryContractMaterialOffer(productMode, paraCode, eparchyCode);
	}
	
    public static IDataset qryOfferTax(String offerCode, String offerType, String relOfferCode, String relOfferType, String catalogId, String groupId, String relOfferId, String tradeTypeCode) throws Exception 
    {
	    return UpcCallIntf.qryOfferTax(offerCode, offerType, relOfferCode, relOfferType, catalogId, groupId, relOfferId, tradeTypeCode);
	}
    
    public static IDataset qrySpecificOfferFromAllOffersByOfferId(String offerCode, String offerType, String elementId, String elementTypeCode) throws Exception
    {
        return UpcCallIntf.qrySpecificOfferFromAllOffersByOfferId(offerCode, offerType, elementId, elementTypeCode);
    }
    
    public static IDataset qryStaticParam(String tableName, String cols, String keys, String values) throws Exception
    {
        return UpcCallIntf.qryStaticParam(tableName, cols, keys, values);
    }
    
    public static IDataset queryOfferNameByOfferId(String offerId)  throws Exception
    {
        return UpcCallIntf.queryOfferNameByOfferId(offerId);
    }
    
    public static IDataset qryOfferGroupRelByOfferIdGroupId(String groupId, String offerType, String offerCode) throws Exception {
		 
		return qryOfferGroupRelByOfferIdGroupId(groupId, offerType, offerCode);
	}
    
	public static IDataset qryOfferCatalogInfoByCatalogId(String catalogId, String fromTableName, String fieldName) throws Exception
	{
		return UpcCallIntf.qryOfferCatalogInfoByCatalogId(catalogId, fromTableName, fieldName);
	}

	public static IDataset saveSpInfoCs(IData param) throws Exception{
		return UpcCallIntf.saveSpInfoCs(param);
	}
	
	public static IDataset qryOfferChaByOfferId(String offerCode, String offerType, String qryOfferType) throws Exception
	{
		return UpcCallIntf.qryOfferChaByOfferId(offerCode, offerType, qryOfferType);
	}
	
	public static IDataset processBBossSyncInfo(IData data) throws Exception
	{
		return UpcCallIntf.processBBossSyncInfo(data);
	}
	public static IDataset processJkdtSyncInfo(IData data) throws Exception
	{
		return UpcCallIntf.processJkdtSyncInfo(data);
	}
	
	public static IDataset qryBureDataSpDtlAndBatImport(IData data) throws Exception
	{
		return UpcCallIntf.qryBureDataSpDtlAndBatImport(data);
	}
	
	public static IDataset qryBureDataBatImport(IData data) throws Exception
	{
		return UpcCallIntf.qryBureDataBatImport(data);
	}
	
	public static IDataset qrySaleActiveInfo(String offerType, String offerCode) throws Exception
	{
		return UpcCallIntf.qrySaleActiveInfo(offerType, offerCode);
	}
	
	public static IDataset qryOfferTempChasByCatalogId(String catalogId) throws Exception
	{
		return UpcCallIntf.qryOfferTempChasByCatalogId(catalogId);
	}
	
	public static IDataset qryOfferTempChasByCataIdAndCataName(String catalogId,String catalogName) throws Exception
	{
		return UpcCallIntf.qryOfferTempChasByCataIdAndCataName(catalogId,catalogName);
	}
	
	public static IDataset qryOfferTempChasByPackageName(String packageName) throws Exception
	{
		return UpcCallIntf.qryOfferTempChasByPackageName(packageName);
	}
	
	public static IDataset qryMountOfferByOfferId(String offerCode, String offerType) throws Exception
	{
		return UpcCallIntf.qryMountOfferByOfferId(offerCode, offerType);
	}
	
	public static IDataset qrySaleActiveProductByLabelId(String labelId) throws Exception
	{
		return UpcCallIntf.qrySaleActiveProductByLabelId(labelId);
	}
	
	public static IDataset qrySaleActivePackageByLabelId(String labelId) throws Exception
	{
		return UpcCallIntf.qrySaleActivePackageByLabelId(labelId);
	}
	
	public static IDataset qryOfferRelWithInverse(String offerCode, String offerType, String relType) throws Exception
	{
		return UpcCallIntf.qryOfferRelWithInverse(offerCode, offerType, relType);
	}
	
	public static IDataset qryDataInfos(String OfferCode, String OfferName) throws Exception
	{
		return UpcCallIntf.qryDataInfos(OfferCode, OfferName);
	}
	/**
	 * 根据OFFER_CODE offer_type查询商品名
	 * */
	public static IDataset queryOfferNameByOfferCodeAndType(String offerType, String offerCode) throws Exception
    {
        return UpcCallIntf.queryOfferNameByOfferCodeAndType(offerType, offerCode);
    }
	
	/**
	 * 根据OFFER_CODE offer_type查询商品名
	 * */
	public static IDataset qryOfferRelsByOfferCode1Code2(String offerCode_A,String offerCode_B,String offerType,String relType) throws Exception
    {
        return UpcCallIntf.qryOfferRelsByOfferCode1Code2(offerCode_A,offerCode_B,offerType,relType);
    }
	
    public static IDataset querySpServiceByIdAndBizStateCode(String offerCode, String offerType, String bizStateCode) throws Exception
    {
        return UpcCallIntf.querySpServiceByIdAndBizStateCode(offerCode, offerType, bizStateCode);
    }
    
    public static IDataset queryMiguCampInfoByServiceId(String memberType,String spServiceId,String startDate) throws Exception 
    {
        return UpcCallIntf.queryMiguCampInfoByServiceId(memberType,spServiceId,startDate);
    }
    
    public static IData queryOfferInfoByOfferId(String offerId, String queryComCha) throws Exception
    {
        return UpcCallIntf.queryOfferByOfferId(offerId, queryComCha);
    }
    
    public static IDataset queryOfferJoinRelByRelOfferIdRelType(String offerCode, String relOfferCode, String relType) throws Exception
    {
        return UpcCallIntf.queryOfferJoinRelByRelOfferIdRelType(offerCode, relOfferCode, relType);
    }
    
    public static IDataset queryMebGroupByOfferId(String offerId, String mgmtDistrict) throws Exception
    {
        return UpcCallIntf.queryMebGroupByOfferId(offerId, mgmtDistrict);
    }

	public static IDataset queryCampaignInfoByCampaignId(String campaignId,String startDate) throws Exception {
    	return UpcCallIntf.queryCampaignInfoByCampaignId(campaignId,startDate);
    }
	
	public static IDataset queryOfferInfoByOfferCodeAndOfferType(String offerCode, String offerType) throws Exception
    {
        return UpcCallIntf.queryOfferInfoByOfferCodeAndOfferType(offerCode, offerType);
    }
    public static IDataset queryOfferMappingByLocalNumber(String offerCode, String offerType) throws Exception
    {
        return UpcCallIntf.queryOfferMappingByLocalNumber(offerCode, offerType);
    }
    public static IDataset queryOfferMappingByBossNumber(String po_number, String type) throws Exception
    {
        return UpcCallIntf.queryOfferMappingByBossNumber( po_number,type);
    }
    public static IDataset queryPoProductPlusInfo(String productspecnumber, String productspeccharacternumber)  throws Exception
    {
        return UpcCallIntf.queryPoProductPlusInfo(productspecnumber, productspeccharacternumber);
    }

    public static IDataset queryOrderReconfirm(String commodityCode) throws Exception
    {
        return UpcCallIntf.queryOrderReconfirm(commodityCode);
    }
    public static IDataset querySepcReconfirmCond(IData input) throws Exception
    {
        return UpcCallIntf.querySepcReconfirmCond(input);
    }
    public static IDataset queryChannelReconfirm(String channelSource) throws Exception
    {
        return UpcCallIntf.queryChannelReconfirm(channelSource);
    }
	 public static IDataset queryOrderReconfirmByInternalCode(String internalCode) throws Exception{
        return UpcCallIntf.queryOrderReconfirmByInternalCode(internalCode);
    }
	//daidl
    public  static IDataset getRateplanByNumberAndType(String poSpecNumber,String ratetype,String ratePlanID) throws Exception{
        return UpcCallIntf.getRateplanByNumberAndType(poSpecNumber,ratetype,ratePlanID);
    } 
    
	public static IDataset queryOfferChaByOfferCode(String offerCode,String offerType) throws Exception
	{
		return UpcCallIntf.queryOfferChaByOfferCode(offerCode,offerType);
	}
	
	/**
     * 从UpcViewCall中迁移过来的，商品订购要用
     * 
     * *根据主商品查询标签信息
     * @author guohuan
     * @param groupId 必填
     * @param groupType 必填
     * @throws Exception
     */
     
    public static IDataset qryAllTagAndTagValueByOfferId(String offerCode, String offerType) throws Exception
    {
        IDataset result = UpcCallIntf.qryAllTagAndTagValueByOfferId(offerCode, offerType);
        
        return result;
    }
    
    /**
     * 从UpcViewCall中迁移过来的，商品订购要用
     * 
     * *根据产品，标签，标签值，品类查询下面的产品
     * @guohuan
     * @param groupId 必填
     * @param groupType 必填
     * @throws Exception
     */
     
    public static IDataset qryOfferByTagInfo(String offerCode, String offerType, String labelId, String labelKeyId, String categoryId) throws Exception
    {
        
        IDataset result = UpcCallIntf.qryOfferByTagInfo(offerCode, offerType, labelId, labelKeyId, categoryId);
        
        return result;
    }

    public static String getMustChoseElementByOfferCode(String productId, String userId, List<DiscntTradeData> discntTradeDataList) throws Exception {

        //获取必选元素
        IDataset newProductElements = UpcCall.queryOfferComRelOfferByOfferIdRelOfferType("P", productId,"D","0898");
        StringBuilder stringBuilder = new StringBuilder();
        //如果产品的构成不为空，如果构成为空就查必选组优惠
        if(IDataUtil.isNotEmpty(newProductElements)){
            for(Object temp :newProductElements){
                IData data2 = (IData)temp;
                stringBuilder.append(data2.getString("OFFER_CODE"));
                stringBuilder.append(",");
            }
        }else{
            //如果产品的构成不为空，如果构成为空就查必选组优惠
            IDataset groupList = UpcCall.queryOfferGroups(productId);
            for(Object temp :groupList){
                IData data2 = (IData)temp;
                String selectFlag = data2.getString("SELECT_FLAG");
                //如果是必选组
                if("0".equals(selectFlag)){
                    IDataset offerList = UpcCall.queryGroupComRelOfferByGroupId(data2.getString("GROUP_ID"), "");
                    for(Object temp2 :offerList){
                        IData data3 = (IData)temp2;
                        String discntCode = data3.getString("OFFER_CODE");
                        IDataset userDiscntList = UserDiscntInfoQry.getAllDiscntByUser(userId, discntCode);
                        if(IDataUtil.isNotEmpty(userDiscntList)){
                            stringBuilder.append(discntCode);
                            stringBuilder.append(",");
                        }
                    }
                    if(stringBuilder.length()<1){
                        for(Object temp2 :offerList){
                            IData data3 = (IData)temp2;
                            String discntCode = data3.getString("OFFER_CODE");
                            for(DiscntTradeData discntTradeData : discntTradeDataList){
                                if(discntCode.equals(discntTradeData.getDiscntCode())){
                                    stringBuilder.append(discntCode);
                                    stringBuilder.append(",");
                                }
                            }

                        }
                    }

                }
            }
        }
		String strTemp = stringBuilder.toString();
		String feepolicyId = "";
		if (strTemp.length() > 0) {
			feepolicyId = strTemp.substring(0, strTemp.length() - 1);
		}
		return feepolicyId;
    }

    /**
     * @Description: 产品关联的权益包查询
     * @Param: [offerCode]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/8 16:23
     */
    public static IDataset queryMainOfferRelaWelfareOffers(String offerCode) throws Exception
    {
        return UpcCallIntf.queryMainOfferRelaWelfareOffers(offerCode, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
    }
    /**
     * 订单归集接口
     *
     * @param offerCode
     * @param offerType
     * @return
     * @throws Exception
     */
    public static IDataset getOfferCataByProductId(String offerCode, String offerType,String upCatalogId,String catalogType) throws Exception {
        return UpcCallIntf.getOfferCataByProductId(offerCode, offerType,upCatalogId,catalogType);
    }

}

