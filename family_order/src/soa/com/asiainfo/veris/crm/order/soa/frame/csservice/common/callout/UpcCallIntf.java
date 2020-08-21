package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import org.apache.log4j.Logger;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

public final class UpcCallIntf {


	public static IData queryVAProductPackage(IData inParams) throws Exception {
		IData result = call("UPC.Out.ICMOnlineFSV.queryVAProductPackage", inParams);
        return result;
	}

	public static IData queryVAProductActivity(IData inParams) throws Exception {
		IData result = call("UPC.Out.ICMOnlineFSV.queryVAProductActivity", inParams);
        return result;
	}

	public static IData queryProductList(IData inParams) throws Exception {
		IData result = call("UPC.Out.ICMOnlineFSV.queryProductList", inParams);
		return result;
	}

	public static IData searchProductList(IData inParams) throws Exception {
		IData result = call("UPC.Out.ICMOnlineFSV.searchProductList", inParams);
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

	public static IDataset queryChaSpecValByCond(String offerType, String offer, String filedName, String value) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER", offer);
		input.put("FILED_NAME", filedName);
		input.put("VALUE", value);
		IData result = call("UPC.Out.ChaQueryFSV.queryChaSpecValByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * 特征相关接口 根据特牌查询品牌名称
	 * 
	 * @param brandCode
	 * @return
	 * @throws Exception
	 */
	public static String queryBrandNameByChaVal(String brandCode) throws Exception {
		IData input = new DataMap();

		input.put("BRAND_CODE", brandCode);
		IData result = call("UPC.Out.ChaQueryFSV.queryBrandNameByChaVal", input);
		return result.getString("BRAND_NAME");

	}

	/**
	 * 根据 销售品构成值 查询销售品信息
	 * 
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryOffersByComCha(String fieldName, String fieldValue) throws Exception {
		IData input = new DataMap();
		input.put("FIELD_NAME", fieldName);
		input.put("FIELD_VALUE", fieldValue);
		IData result = call("UPC.Out.OfferQueryFSV.qryOffersByComCha", input);
		IDataset poDatas = result.getDataset("OUTDATA");

		return poDatas;
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

	public static IDataset queryTempChaByCond(String tempChaId, String fromTableName, String fieldName) throws Exception {
		IData input = new DataMap();

		input.put("EXT_CHA_ID", tempChaId);
		input.put("FROM_TABLE_NAME", fromTableName);
		input.put("FIELD_NAME", fieldName);
		IData result = call("UPC.Out.ChaQueryFSV.queryTempChaByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferComChaByCond(String offerType, String offerCode, String fieldName) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		if (StringUtils.isNotEmpty(fieldName)) {
			input.put("FIELD_NAME", fieldName);
		}
		IData result = call("UPC.Out.ChaQueryFSV.queryOfferComChaByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferChaByCond(String offerType, String offerCode, String fieldName, String attrObj, String valueType, String mgmtDistrict, Pagination pagination) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		if (StringUtils.isNotEmpty(fieldName)) {
			input.put("FIELD_NAME", fieldName);
		}
		if (StringUtils.isNotEmpty(attrObj)) {
			input.put("ATTR_OBJ", attrObj);
		}
		if (StringUtils.isNotEmpty(valueType)) {
			input.put("VALUE_TYPE", valueType);
		}
		if (StringUtils.isNotEmpty(mgmtDistrict)) {
			input.put("MGMT_DISTRICT", mgmtDistrict);
		}
		IData result = call("UPC.Out.ChaQueryFSV.queryOfferChaByCond", input, pagination);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *特征相关 *根据商品ID与FILED_NAME查询商品的销售属性及值
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 * @param attrObj
	 *            必填
	 * @param fieldName
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryOfferChaAndValByCond(String offerType, String offerCode, String attrObj, String fieldName) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("ATTR_OBJ", attrObj);
		input.put("FIELD_NAME", fieldName);
		IData result = call("UPC.Out.ChaQueryFSV.queryOfferChaAndValByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *特征相关 *根据商品ID与FILED_NAME查询商品的销售属性及值
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 * @param attrObj
	 *            必填
	 * @param fieldName
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryOfferChaAndValByCond(String offerType, String offerCode, String valueType, String attrObj, String fieldName) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("VALUE_TYPE", valueType);
		input.put("ATTR_OBJ", attrObj);
		input.put("FIELD_NAME", fieldName);
		IData result = call("UPC.Out.ChaQueryFSV.queryOfferChaAndValByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferChaValByCond(String offerType, String offerCode, String chaSpecId) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("CHA_SPEC_ID", chaSpecId);
		IData result = call("UPC.Out.ChaQueryFSV.queryOfferChaValByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryOfferChaValByFieldNameOfferCodeAndOfferType(String offerType, String offerCode, String fieldName) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("FIELD_NAME", fieldName);
		IData result = call("UPC.Out.ChaQueryFSV.qryOfferChaValByFieldNameOfferCodeAndOfferType", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryGroupRelByGroupId(String groupId, String relType) throws Exception {
		IData input = new DataMap();

		input.put("GROUP_ID", groupId);
		input.put("REL_TYPE", relType);
		IData result = call("UPC.Out.GroupQueryFSV.queryGroupRelByGroupId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryGroupByCond(String groupId, String groupType) throws Exception {
		IData input = new DataMap();

		input.put("GROUP_ID", groupId);
		if (StringUtils.isNotEmpty(groupType)) {
			input.put("GROUP_TYPE", groupType);
		}
		IData result = call("UPC.Out.GroupQueryFSV.queryGroupByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryComEnableModeBy2OfferId(String offerType, String offerCode, String relOfferType, String relOfferCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("REL_OFFER_TYPE", relOfferType);
		input.put("REL_OFFER_CODE", relOfferCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryComEnableModeBy2OfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryEnableModeRelByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryEnableModeRelByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *根据商品编码查询拓展属性和必选元素
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 * @throws Exception
	 */

	public static IDataset qryOffersByOfferIdWithPackageElementFilter(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.qryOffersByOfferIdWithPackageElementFilter", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryCateByUpCateId(String upCategoryId, String recursion) throws Exception {
		IData input = new DataMap();

		input.put("UP_CATEGORY_ID", upCategoryId);
		if (StringUtils.isNotEmpty(recursion)) {
			input.put("RECURSION", recursion);
		}
		IData result = call("UPC.Out.CateQueryFSV.queryCateByUpCateId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *商品相关接口 *根据品类编码查询商品
	 * 
	 * @param categoryId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryOfferByCateId(String categoryId) throws Exception {
		IData input = new DataMap();

		input.put("CATEGORY_ID", categoryId);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferByCateId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *商品相关接口 *根据商品编码查询商品所挂的商品列表(对应以前根据产品ID查询产品下所有包和元素)
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 * @param elementTypeCode
	 *            必填
	 * @param relType
	 *            可填
	 * @param selectFlag
	 *            可填
	 * @param mgmtDistrict
	 *            可填
	 * @throws Exception
	 */

	public static IData queryAllOffersByOfferId(String offerType, String offerCode, String elementTypeCode, String relType, String selectFlag, String mgmtDistrict) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		if (StringUtils.isNotEmpty(relType)) {
			input.put("REL_TYPE", relType);
		}
		if (StringUtils.isNotEmpty(elementTypeCode)) {
			input.put("SPECIFIC_OFFER_TYPE", elementTypeCode);
		}
		if (StringUtils.isNotEmpty(selectFlag)) {
			input.put("SELECT_FLAG", selectFlag);
		}
		if (StringUtils.isNotEmpty(mgmtDistrict)) {
			input.put("MGMT_DISTRICT", mgmtDistrict);
		}
		IData result = call("UPC.Out.OfferQueryFSV.queryAllOffersByOfferId", input);
		IData data = result.getData("OUTDATA");

		return data;
	}

	/**
	 * *商品相关接口 *"根据offer_id,rel_offer_id,rel_type（包括构成关系,关联订购,关联取消等,具体枚举值可以再商量）查询对应的关系表,得出offer_id代表的销售品pm_enable_mode数据,如果查询不到关系型的时间配置,则查询offer_id商品本身的时间配置,如果rel_offer_id,rel_type未传,则直接查询offer_id商品本身的时间配置信息"
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 * @param relOfferType
	 *            可填
	 * @param relOfferCode
	 *            可填
	 * @param relType
	 *            可填
	 * @throws Exception
	 */

	public static IDataset queryOfferEnableModeByCond(String offerType, String offerCode, String relOfferType, String relOfferCode, String relType) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		if (StringUtils.isNotEmpty(relOfferType)) {
			input.put("REL_OFFER_TYPE", relOfferType);
		}
		if (StringUtils.isNotEmpty(relOfferCode)) {
			input.put("REL_OFFER_CODE", relOfferCode);
		}
		if (StringUtils.isNotEmpty(relType)) {
			input.put("REL_TYPE", relType);
		}
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferEnableModeByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferComRelOfferByOfferId(String offerType, String offerCode, String eparchyCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("MGMT_DISTRICT", eparchyCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferComRelOfferByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryOfferComRelOfferByOfferId(String offerType, String offerCode) throws Exception {
		return queryOfferComRelOfferByOfferId(offerType, offerCode, null);
	}

	/**
	 * *商品相关接口 *根据商品ID查询商品构成关系并关联出商品
	 * 这个接口与接口UPC.Out.OfferQueryFSV.queryOfferComRelOfferByOfferId相比
	 * ，更侧重查REL_OFFER_ID的PM_OFFER信息
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 * @throws Exception
	 */

	public static IDataset qryComRelOffersByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.qryComRelOffersByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferJoinRelAndOfferByOfferId(String offerType, String offerCode, String relType, String selectFlag, String queryCha) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		if (StringUtils.isNotEmpty(relType)) {
			input.put("REL_TYPE", relType);
		}
		if (StringUtils.isNotEmpty(selectFlag)) {
			input.put("SELECT_FLAG", selectFlag);
		}
		if (StringUtils.isNotEmpty(queryCha)) {
			input.put("QUERY_CHA", queryCha);
		}
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferJoinRelAndOfferByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferTagByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.TagQueryFSV.queryOfferTagByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferCodeByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferCodeByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset querySceneTypeByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.CateQueryFSV.querySceneTypeByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferLineByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferLineByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *商品相关接口 *根据商品线编码查询商品列表
	 * 
	 * @param offerLineId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryOfferByOfferLineId(String offerLineId) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_LINE_ID", offerLineId);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferByOfferLineId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferByMutiCateId(String categoryId, String offerLineId) throws Exception {
		IData input = new DataMap();

		input.put("CATEGORY_ID", categoryId);
		input.put("OFFER_LINE_ID", offerLineId);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferByMutiCateId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryOffersByMultiCategory(String productId, String mgmtDistrict, String categoryId) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", productId);
		data.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
		data.put("MGMT_DISTRICT", mgmtDistrict);
		data.put("CATEGORY_ID", categoryId);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferByMutiCateId", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset queryOffersByMultiCategory(String productId, String mgmtDistrict, String categoryId, String relType) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", productId);
		data.put("OFFER_TYPE", "P");
		data.put("MGMT_DISTRICT", mgmtDistrict);
		data.put("CATEGORY_ID", categoryId);
		data.put("REL_TYPE", relType);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferByMutiCateId", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset queryOfferGroups(String productId) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", productId);
		data.put("OFFER_TYPE", "P");
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferGroupRelOfferId", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	/**
	 * *商品相关接口 *根据标签查询商品信息
	 * 
	 * @param tagId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryOfferByTagId(String tagId) throws Exception {
		IData input = new DataMap();

		input.put("TAG_ID", tagId);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferByTagId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *商品相关接口 *根据组编码查询商品信息
	 * 
	 * @param groupId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryGroupComRelOfferByGroupId(String groupId, String eparchyCode) throws Exception {
		IData input = new DataMap();

		input.put("GROUP_ID", groupId);
		input.put("MGMT_DISTRICT", eparchyCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryGroupComRelOfferByGroupId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryGroupComRelOfferByGroupId(String groupId) throws Exception {
		return queryGroupComRelOfferByGroupId(groupId, null);
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

	public static IDataset queryOfferPicByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferPicByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryJoinEnableModeBy2OfferId(String offerType, String offerCode, String relOfferType, String relOfferCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("REL_OFFER_TYPE", relOfferType);
		input.put("REL_OFFER_CODE", relOfferCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryJoinEnableModeBy2OfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryGroupComEnableModeByGroupIdOfferId(String groupId, String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("GROUP_ID", groupId);
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryGroupComEnableModeByGroupIdOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}
	
	//查询套餐时间策略
	public static IDataset queryGroupComEnableModeByGroupIdOfferId(String groupId,String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("GROUP_ID", groupId);
		input.put("OFFER_ID", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryGroupComEnableModeByGroupIdOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * 查询包内某个特定元素的信息（生失效方式+最大最小+必选标记）
	 * 
	 * @param groupId
	 * @param offerType
	 * @param offerCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryOfferFromGroupByGroupIdOfferId(String groupId, String offerType, String offerCode, String queryCha) throws Exception {
		IData input = new DataMap();

		input.put("GROUP_ID", groupId);
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		if (StringUtils.isNotEmpty(queryCha)) {
			input.put("QUERY_CHA", queryCha);
		}
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferFromGroupByGroupIdOfferId", input);
		IData data = result.getData("OUTDATA");

		IDataset results = new DatasetList();
		if (IDataUtil.isNotEmpty(data)) {
			results.add(data);
		}

		return results;
	}

	/**
	 * *商品相关接口 *根据组编码查询组构成关系
	 * 
	 * @param groupId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryGroupComRelByGroupId(String groupId) throws Exception {
		IData input = new DataMap();

		input.put("GROUP_ID", groupId);
		IData result = call("UPC.Out.OfferQueryFSV.queryGroupComRelByGroupId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferGroupRelOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferGroupRelOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		if (IDataUtil.isNotEmpty(dataset)) {
			for (int i = 0, isize = dataset.size(); i < isize; i++) {
				IData packageInfo = dataset.getData(i);

				packageInfo.put("PRODUCT_ID", offerCode);
				packageInfo.put("PACKAGE_ID", packageInfo.getString("GROUP_ID"));
				packageInfo.put("PACKAGE_NAME", packageInfo.getString("GROUP_NAME"));
				packageInfo.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(packageInfo.getString("SELECT_FLAG")));
				packageInfo.put("DEFAULT_TAG", UpcConst.getDefaultTagForSelectFlag(packageInfo.getString("SELECT_FLAG")));
				packageInfo.put("PACKAGE_TYPE_CODE", packageInfo.getString("GROUP_TYPE"));
				packageInfo.put("LIMIT_TYPE", "");
				packageInfo.put("MIN_NUMBER", packageInfo.getString("MIN_NUM"));
				packageInfo.put("MAX_NUMBER", packageInfo.getString("MAX_NUM"));
				packageInfo.put("RSRV_STR1", "");
			}
		}

		return dataset;
	}

	/**
	 * *商品相关接口 *根据商品ID,组ID查询商品与组的关系
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryOfferGroupRelOfferIdGroupId(String offerType, String offerCode, String groupId, String queryCha) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("GROUP_ID", groupId);
		if (StringUtils.isNotEmpty(queryCha)) {
			input.put("QUERY_CHA", queryCha);
		}
		IData result = call("UPC.Out.OfferQueryFSV.qryGroupByOfferIdGroupId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferRelByCond(String offerType, String offerCode, String relType) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("REL_TYPE", relType);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferRelByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferJoinRelByOfferId(String offerType, String offerCode, String relType, String eparchyCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("REL_TYPE", relType);
		input.put("MGMT_DISTRICT", eparchyCode);
		IData result = call("UPC.Out.OfferQueryFSV.qryRelOffersByJoinRelOffer", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryOfferJoinRelByOfferId(String offerType, String offerCode, String relType) throws Exception {
		return queryOfferJoinRelByOfferId(offerType, offerCode, relType, null);
	}

	/**
	 * *商品相关接口 *根据offerJoin里 rel_offer_id,rel_type 查询offerJoinRel表里 offerId 商品信息
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 * @param relType
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryOfferJoinRelByRelOfferId(String relOfferType, String relOfferCode, String relType) throws Exception {
		IData input = new DataMap();

		input.put("REL_OFFER_TYPE", relOfferType);
		input.put("REL_OFFER_CODE", relOfferCode);
		input.put("REL_TYPE", relType);
		IData result = call("UPC.Out.OfferQueryFSV.qryOffersByJoinRelRelOffer", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferComRelByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferComRelByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferByOfferId(String offerType, String offerCode, String queryComCha) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		if (StringUtils.isNotEmpty(queryComCha)) {
			input.put("QUERY_COM_CHA", queryComCha);
		}
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *商品相关接口 *根据商品ID查询商品
	 *
	 * @param offerId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryOfferByOfferId(String offerId) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_ID", offerId);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferDetailsByOfferId(String offerType, String offerCode, String mgmtDistrict) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("MGMT_DISTRICT", mgmtDistrict);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferDetailsByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *规则相关 *根据赠送编码查询赠送信息与赠送计划信息
	 * 
	 * @param giftId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryGiftByGiftId(String giftId) throws Exception {
		IData input = new DataMap();

		input.put("GIFT_ID", giftId);
		IData result = call("UPC.Out.GiftQueryFSV.queryGiftByGiftId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *规则相关 *根据RULE_ID查询规则构成信息
	 * 
	 * @param ruleId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryRuleComRelByRuleId(String ruleId) throws Exception {
		IData input = new DataMap();

		input.put("RULE_ID", ruleId);
		IData result = call("UPC.Out.RuleQueryFSV.queryRuleComRelByRuleId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *规则相关 *根据RULE_ID查询规则信息
	 * 
	 * @param ruleId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryRuleByRuleId(String ruleId) throws Exception {
		IData input = new DataMap();

		input.put("RULE_ID", ruleId);
		IData result = call("UPC.Out.RuleQueryFSV.queryRuleByRuleId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *规则相关 *根据COND_ID查询条件信息
	 * 
	 * @param condId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryConditionByCondId(String condId) throws Exception {
		IData input = new DataMap();

		input.put("COND_ID", condId);
		IData result = call("UPC.Out.RuleQueryFSV.queryConditionByCondId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *规则相关 *根据COND_ID查询条件实例信息
	 * 
	 * @param condValId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryCondValByCondValId(String condValId) throws Exception {
		IData input = new DataMap();

		input.put("COND_VAL_ID", condValId);
		IData result = call("UPC.Out.RuleQueryFSV.queryCondValByCondValId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *规则相关 *根据OPERCODE查询操作符信息
	 * 
	 * @param operCode
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryOperCodeParseByOperCode(String operCode) throws Exception {
		IData input = new DataMap();

		input.put("OPER_CODE", operCode);
		IData result = call("UPC.Out.RuleQueryFSV.queryOperCodeParseByOperCode", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *规则相关 *根据优惠编码查询优惠信息
	 * 
	 * @param policyId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPolicyByPolicyId(String policyId) throws Exception {
		IData input = new DataMap();

		input.put("POLICY_ID", policyId);
		IData result = call("UPC.Out.RuleQueryFSV.queryPolicyByPolicyId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferRuleByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.RuleQueryFSV.queryOfferRuleByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *规则相关 *根据规则结论编码查询规则结论信息
	 * 
	 * @param ruleResultId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryRuleResultByRuleResultId(String ruleResultId) throws Exception {
		IData input = new DataMap();

		input.put("RULE_RESULT_ID", ruleResultId);
		IData result = call("UPC.Out.RuleQueryFSV.queryRuleResultByRuleResultId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *规则相关 *根据销售资格编码查询销售资格信息
	 * 
	 * @param saleLimitId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset querySaleLimitBySaleLimitId(String saleLimitId) throws Exception {
		IData input = new DataMap();

		input.put("SALE_LIMIT_ID", saleLimitId);
		IData result = call("UPC.Out.RuleQueryFSV.querySaleLimitBySaleLimitId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferPriceRelPriceByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.PriceQueryFSV.queryOfferPriceRelPriceByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *定价相关 *根据price_id查询返回pm_price_plan信息
	 * 
	 * @param priceId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPricePricePlanRelPricePlanByPriceId(String priceId) throws Exception {
		IData input = new DataMap();

		input.put("PRICE_ID", priceId);
		IData result = call("UPC.Out.PriceQueryFSV.queryPricePricePlanRelPricePlanByPriceId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferPriceRelByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.PriceQueryFSV.queryOfferPriceRelByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *定价相关 *根据定价计划编码查询定价计划
	 * 
	 * @param pricePlanId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPricePlanByPlanId(String pricePlanId) throws Exception {
		IData input = new DataMap();

		input.put("PRICE_PLAN_ID", pricePlanId);
		IData result = call("UPC.Out.PriceQueryFSV.queryPricePlanByPlanId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *定价相关 *根据定价编码查询定价计划
	 * 
	 * @param priceId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPricePricePlanRelByPriceId(String priceId) throws Exception {
		IData input = new DataMap();

		input.put("PRICE_ID", priceId);
		IData result = call("UPC.Out.PriceQueryFSV.queryPricePricePlanRelByPriceId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *定价相关 *根据定价编码查询定价
	 * 
	 * @param priceId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPriceByPriceId(String priceId) throws Exception {
		IData input = new DataMap();

		input.put("PRICE_ID", priceId);
		IData result = call("UPC.Out.PriceQueryFSV.queryPriceByPriceId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryProdStaByCond(String offerType, String offerCode, String prodStatus) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("PROD_STATUS", prodStatus);
		IData result = call("UPC.Out.ProdQueryFSV.queryProdStaByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryProdStaByProdIdAndProdStatus(String prodSpecId, String prodStatus) throws Exception {
		IData input = new DataMap();

		input.put("PROD_SPEC_ID", prodSpecId);
		input.put("PROD_STATUS", prodStatus);
		IData result = call("UPC.Out.ProdQueryFSV.queryProdStaByProdIdAndProdStatus", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryProdSpecByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.ProdQueryFSV.queryProdSpecByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryOfferProdRelByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.ProdQueryFSV.queryOfferProdRelByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *产品相关 *根据产品ID查询产品信息
	 * 
	 * @param prodSpecId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryProdSpecByProdId(String prodSpecId) throws Exception {
		IData input = new DataMap();

		input.put("PROD_SPEC_ID", prodSpecId);
		IData result = call("UPC.Out.ProdQueryFSV.queryProdSpecByProdId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *标签相关 *根据品类编码查询标签信息
	 * 
	 * @param categoryId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryDivTagByCateId(String categoryId) throws Exception {
		IData input = new DataMap();

		input.put("CATEGORY_ID", categoryId);
		IData result = call("UPC.Out.TagQueryFSV.queryDivTagByCateId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset querySpServiceAndProdByCond(String spCode, String bizCode, String bizTypeCode, String serviceId) throws Exception {
		IData input = new DataMap();

		input.put("SP_CODE", spCode);
		input.put("BIZ_CODE", bizCode);
		input.put("BIZ_TYPE_CODE", bizTypeCode);
		input.put("OFFER_CODE", serviceId);
		input.put("OFFER_TYPE", "Z");

		IData result = call("UPC.Out.SpQueryFSV.querySpServiceAndProdByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset querySpServiceAndInfoByCond(String bizTypeCode, String rsrvStr1) throws Exception {
		IData input = new DataMap();

		input.put("BIZ_TYPE_CODE", bizTypeCode);
		input.put("RSRV_STR1", rsrvStr1);
		IData result = call("UPC.Out.SpQueryFSV.querySpServiceAndInfoByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}
	
	/**
	 * *SP相关接口 *根据OFFER_CODE,OFFER_TYPE,BIZ_STATE_CODE查询平台局数据信息，提供给IVR调用
	 * 
	 * @param OFFER_CODE
	 *            必填
	 * @param OFFER_TYPE
	 *            必填
	 * @param BIZ_STATE_CODE
	 *            必填
	 * @throws Exception
	 */

	public static IDataset getPlatsvcCustSvc(String service_id) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_CODE", service_id);
		input.put("OFFER_TYPE", "Z");
		input.put("BIZ_STATE_CODE", "A");
		IData result = call("UPC.Out.SpQueryFSV.qrySpServiceByIdAndBizStateCode", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset querySpServiceByCond(String serviceId, String spCode, String bizCode, String bizName, String bizType) throws Exception {
		IData input = new DataMap();

		input.put("SERVICE_ID", serviceId);
		input.put("SP_CODE", spCode);
		input.put("BIZ_CODE", bizCode);
		input.put("BIZ_NAME", bizName);
		input.put("BIZ_TYPE", bizType);
		IData result = call("UPC.Out.SpQueryFSV.querySpServiceByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset querySpInfoByCond(String spId, String spCode, String spName, String spType) throws Exception {
		IData input = new DataMap();

		input.put("SP_ID", spId);
		input.put("SP_CODE", spCode);
		input.put("SP_NAME", spName);
		input.put("SP_TYPE", spType);
		IData result = call("UPC.Out.SpQueryFSV.querySpInfoByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *SP相关接口 *根据ID,CODE,NAME,TYPE查询企业信息表
	 * 
	 * @param spCode
	 *            必填
	 * @throws Exception
	 */

	public static IDataset querySpInfoNameByCond(String spCode) throws Exception {
		IData input = new DataMap();
		input.put("SP_CODE", spCode);
		IData result = call("UPC.Out.SpQueryFSV.querySpInfoByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset querySpServiceByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.querySpServiceByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *SP相关接口 *根据SP_SERVICE_ID,SP_CODE,BIZ_CODE,BIZ_TYPE_CODE查询企业信息
	 * 
	 * @param spServiceId
	 *            必填
	 * @param spCode
	 *            必填
	 * @param bizCode
	 *            必填
	 * @param bizTypeCode
	 *            必填
	 * @throws Exception
	 */

	public static IDataset querySpServiceAndInfoAndParamByCond(String serviceId, String spCode, String bizCode, String bizTypeCode) throws Exception {
		IData input = new DataMap();

		input.put("SP_CODE", spCode);
		input.put("BIZ_CODE", bizCode);
		input.put("BIZ_TYPE_CODE", bizTypeCode);
		input.put("OFFER_CODE", serviceId);
		input.put("OFFER_TYPE", "Z");

		IData result = call("UPC.Out.SpQueryFSV.querySpServiceAndInfoAndParamByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryProdSpecRelByCond(String prodSpecId, String bizTypeCode, String operCode) throws Exception {
		IData input = new DataMap();

		input.put("PROD_SPEC_ID", prodSpecId);
		input.put("BIZ_TYPE_CODE", bizTypeCode);
		input.put("OPER_CODE", operCode);
		IData result = call("UPC.Out.ProdQueryFSV.queryProdSpecRelByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *SP相关接口 *根据ServiceId查询SP企业与业务参数等综合信息
	 * 
	 * @param serviceId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset querySpComprehensiveInfoByServiceId(String serviceId) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_CODE", serviceId);
		input.put("OFFER_TYPE", "Z");

		IData result = call("UPC.Out.SpQueryFSV.querySpComprehensiveInfoByServiceId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *SP相关接口 *根据SERVICE_ID查询企业综合信息
	 * 
	 * @param serviceId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset querySpServiceAndInfoAndParamByServiceId(String serviceId) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_CODE", serviceId);
		input.put("OFFER_TYPE", "Z");

		IData result = call("UPC.Out.SpQueryFSV.querySpServiceAndInfoAndParamByServiceId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *SP相关接口 *根据SERVICE_ID查询企业综合信息
	 * 
	 * @param serviceId
	 *            必填
	 * @throws Exception
	 */

	public static IDataset querySpServiceByOfferId(String serviceId) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_CODE", serviceId);
		input.put("OFFER_TYPE", "Z");

		IData result = call("UPC.Out.SpQueryFSV.querySpServiceByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}
	
	public static IDataset qrySpInfoCs(IData param) throws Exception {
		
		IData result = call("UPC.Out.SpQueryFSV.qrySpInfoCs", param);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}
	
	public static IDataset updSpInfoCs(IData param) throws Exception {
		
		IData result = call("UPC.Out.SpQueryFSV.updSpInfoCs", param);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * 根据产品ID查询包下所有元素
	 * 
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryAllOfferEnablesByOfferId(String productId) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", "P");
		input.put("OFFER_CODE", productId);

		IData result = call("UPC.Out.OfferQueryFSV.queryAllOfferEnablesByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * 根据产品ID查询包下所有元素
	 * 
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryAllOfferEnablesByOfferIdAndRelType(String productId, String relType) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_CODE", productId);
		input.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
		input.put("REL_TYPE", relType);

		IData result = call("UPC.Out.OfferQueryFSV.queryAllOfferEnablesByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * 根据产品ID查询包下所有元素(包括已经失效的)
	 * 
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryNeglectDateAllOfferEnablesByOfferId(String productId, String relType) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_CODE", productId);
		input.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
		input.put("REL_TYPE", relType);

		IData result = call("UPC.Out.OfferQueryFSV.queryNeglectDateAllOfferEnablesByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *BBOSS相关接口 *根据PORATENUMBER查询PORATEPLAN表
	 * 
	 * @param poratenumber
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPoratePlanByPorateNumber(String poratenumber) throws Exception {
		IData input = new DataMap();

		input.put("PORATENUMBER", poratenumber);
		IData result = call("UPC.Out.BbossQueryFSV.queryPoratePlanByPorateNumber", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *BBOSS相关接口 *根据PospecNumber查询PM_PO表
	 * 
	 * @param pospecnumber
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPoByPospecNumber(String pospecnumber) throws Exception {
		IData input = new DataMap();

		input.put("POSPECNUMBER", pospecnumber);
		IData result = call("UPC.Out.BbossQueryFSV.queryPoByPospecNumber", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryPoratePlanBySpecRatePlanId(String pospecnumber, String poratenumber, String rateplanid) throws Exception {
		IData input = new DataMap();

		input.put("POSPECNUMBER", pospecnumber);
		input.put("PORATENUMBER", poratenumber);
		input.put("RATEPLANID", rateplanid);
		IData result = call("UPC.Out.BbossQueryFSV.queryPoratePlanBySpecRatePlanId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *BBOSS相关接口 *根据RATEPLANID查询PORATEPLANICB表
	 * 
	 * @param rateplanid
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPoratePlanIcbByRatePlanId(String rateplanid) throws Exception {
		IData input = new DataMap();

		input.put("RATEPLANID", rateplanid);
		IData result = call("UPC.Out.BbossQueryFSV.queryPoratePlanIcbByRatePlanId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *BBOSS相关接口 *根据PARAMETERNUMBER查询PORATEPLANICB表
	 * 
	 * @param parameternumber
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPoratePlanIcbByParameterNumber(String parameternumber) throws Exception {
		IData input = new DataMap();

		input.put("PARAMETERNUMBER", parameternumber);
		IData result = call("UPC.Out.BbossQueryFSV.queryPoratePlanIcbByParameterNumber", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryPoratePlanIcbByPk(String rateplanid, String icbNo, String parameterNo, String parameternumber, String parametername) throws Exception {
		IData input = new DataMap();

		input.put("RATEPLANID", rateplanid);
		input.put("ICB_NO", icbNo);
		input.put("PARAMETER_NO", parameterNo);
		input.put("PARAMETERNUMBER", parameternumber);
		input.put("PARAMETERNAME", parametername);
		IData result = call("UPC.Out.BbossQueryFSV.queryPoratePlanIcbByPk", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *BBOSS相关接口 *根据ID 查询PM_PO表
	 * 
	 * @throws Exception
	 */

	public static IDataset queryPoByValid() throws Exception {
		IData input = new DataMap();

		IData result = call("UPC.Out.BbossQueryFSV.queryPoByValid", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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

	public static IDataset queryPoproductByProductSpecNumber(String productspecnumber, String pospecnumber) throws Exception {
		IData input = new DataMap();

		input.put("PRODUCTSPECNUMBER", productspecnumber);
		input.put("POSPECNUMBER", pospecnumber);
		IData result = call("UPC.Out.BbossQueryFSV.queryPoproductByProductSpecNumber", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *BBOSS相关接口 *根据POSPECNUMBER 查询PM_POPRODUCT表
	 * 
	 * @param pospecnumber
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPoproductByPospecNumber(String pospecnumber) throws Exception {
		IData input = new DataMap();

		input.put("POSPECNUMBER", pospecnumber);
		IData result = call("UPC.Out.BbossQueryFSV.queryPoproductByPospecNumber", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *BBOSS相关接口 *根据PRODUCTSPECNUMBER 查询PM_POPRODUCTPLUS表
	 * 
	 * @param productspecnumber
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPoproductPlusbyProductSpecNumber(String productspecnumber) throws Exception {
		IData input = new DataMap();

		input.put("PRODUCTSPECNUMBER", productspecnumber);
		IData result = call("UPC.Out.BbossQueryFSV.queryPoproductPlusbyProductSpecNumber", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *BBOSS相关接口 *根据BBOSS产品规格编码查询集团商品编码
	 * 
	 * @param productspecnumber
	 *            必填
	 * @throws Exception
	 */

	public static String queryPospecnumberByProductspecnumber(String productspecnumber) throws Exception {
		IData input = new DataMap();

		input.put("PRODUCTSPECNUMBER", productspecnumber);
		IData result = call("UPC.Out.BbossQueryFSV.queryPospecnumberByProductspecnumber", input);

		return result.getString("POSPECNUMBER");
	}

	/**
	 * *BBOSS相关接口 *BBOSS商品同步接口
	 * 
	 * @throws Exception
	 */

	public static IDataset processBBossSyncInfo() throws Exception {
		IData input = new DataMap();

		IData result = call("UPC.Out.BBossSyncOperateSV.processBBossSyncInfo", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *BBOSS相关接口 *通过产品编号查询BBOSS产品名称
	 * 
	 * @param productspecnumber
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryProductSpecNameByProductSpecNumber(String productspecnumber) throws Exception {
		IData input = new DataMap();

		input.put("PRODUCTSPECNUMBER", productspecnumber);
		IData result = call("UPC.Out.BbossQueryFSV.queryProductSpecNameByProductSpecNumber", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *BBOSS相关接口 *通过商品编号查询BBOSS商品名称
	 * 
	 * @param pospecnumber
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPospecNameByPoSpecNumber(String pospecnumber) throws Exception {
		IData input = new DataMap();

		input.put("POSPECNUMBER", pospecnumber);
		IData result = call("UPC.Out.BbossQueryFSV.queryPospecNameByPoSpecNumber", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * *BBOSS相关接口 *根据POSPECNUMBER查询PORATEPLAN表
	 * 
	 * @param pospecnumber
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryPoratePlanByPospecNumber(String pospecnumber) throws Exception {
		IData input = new DataMap();

		input.put("POSPECNUMBER", pospecnumber);
		IData result = call("UPC.Out.BbossQueryFSV.queryPoratePlanByPospecNumber", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryOfferChaValByOfferIdAndMgmtDistict(String serviceId, String eparchyCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", "Z");
		input.put("OFFER_CODE", serviceId);

		input.put("MGMT_DISTRICT", eparchyCode);
		IData result = call("UPC.Out.ChaQueryFSV.queryOfferChaValByOfferIdAndMgmtDistict", input);

		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset querySpServiceByServType(String servType) throws Exception {
		IData input = new DataMap();
		input.put("SERV_TYPE", servType);
		IData result = call("UPC.Out.SpQueryFSV.querySpServiceByServType", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset querySpInfoBySpCodeAndBizTypeCodeAndSpName(String spCode, String spName, String bizTypeCode, Pagination pagination) throws Exception {
		IData input = new DataMap();
		if (StringUtils.isNotBlank(spCode)) {
			input.put("SP_CODE", spCode);
		}
		if (StringUtils.isNotBlank(spName)) {
			input.put("SP_NAME", spName);
		}
		if (StringUtils.isNotBlank(bizTypeCode)) {
			input.put("BIZ_TYPE_CODE", bizTypeCode);
		}

		IData result = call("UPC.Out.SpQueryFSV.querySpInfoBySpCodeAndBizTypeCodeAndSpName", input, pagination, true);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset querySpServiceAndOfferByCond(String serviceId, String serviceName, String bizTypeCode, String spCode, Pagination pagination) throws Exception {
		IData input = new DataMap();
		if (StringUtils.isNotBlank(serviceId)) {
			input.put("OFFER_ID", serviceId);
		}
		if (StringUtils.isNotBlank(serviceName)) {
			input.put("OFFER_NAME", serviceName);
		}
		if (StringUtils.isNotBlank(bizTypeCode)) {
			input.put("BIZ_TYPE_CODE", bizTypeCode);
		}
		if (StringUtils.isNotBlank(spCode)) {
			input.put("SP_CODE", spCode);
		}

		IData result = call("UPC.Out.SpQueryFSV.querySpServiceAndOfferByCond", input, pagination, true);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * * *根据SPCODE,BIZTYPECODE,ORGDOMAIN查询TD_B_PLATSVC表
	 * 
	 * @param SPCODE
	 *            ,BIZTYPECODE,ORGDOMAIN，SP_CODE为模糊查询 必填
	 * @throws Exception
	 */

	public static IDataset querySpServiceBySpCodeAndBizCodeAndBizStateCode(String SpCode, String BizCode) throws Exception {
		IData input = new DataMap();

		input.put("SP_CODE", SpCode);
		input.put("BIZ_CODE", BizCode);
		input.put("BIZ_STATE_CODE", "A");

		IData result = call("UPC.Out.SpQueryFSV.querySpServiceBySpCodeAndBizCodeAndBizStateCode", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * * *根据SPCODE,BIZTYPECODE,ORGDOMAIN查询TD_B_PLATSVC表
	 * 
	 * @param SPCODE
	 *            ,BIZTYPECODE,ORGDOMAIN，SP_CODE为模糊查询 必填
	 * @throws Exception
	 */

	public static IDataset querySpServiceBySpCodeAndBizTypeCodeAndOrgDomain(String SpCode, String BizTypeCode, String OrgDomain) throws Exception {
		IData input = new DataMap();

		input.put("SP_CODE", SpCode);
		input.put("BIZ_TYPE_CODE", BizTypeCode);
		input.put("ORG_DOMAIN", OrgDomain);

		IData result = call("UPC.Out.SpQueryFSV.querySpServiceBySpCodeAndBizTypeCodeAndOrgDomain", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * 
	 * @Title: qryCataLogsByTypeRootLevel
	 * @Description: 营销活动获取活动类型
	 * @param @param catalogType
	 * @param @param root
	 * @param @param catalogLevel
	 * @param @return
	 * @param @throws Exception
	 * @return IDataset
	 * @throws
	 * @author longtian3
	 */
	public static IDataset qryCataLogsByTypeRootLevel(String catalogType, String root, String catalogLevel) throws Exception {
		IData input = new DataMap();
		input.put("CATALOG_LEVEL", catalogLevel);
		input.put("ROOT", root);
		input.put("CATALOG_TYPE", catalogType);

		IData result = call("UPC.Out.CatalogQueryFSV.qryCataLogsByTypeRootLevel", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * 
	 * @Title: qryRelOfferByComRelOfferIdRelOfferId
	 * @Description: 
	 *               根据offerId和relOfferId关联查询com_rel表和offer表查询relofferid的服务是不是主服务s
	 * @param @param offerCode
	 * @param @param offerType
	 * @param @param relOfferCode
	 * @param @param relOfferType
	 * @param @return
	 * @param @throws Exception 设定文件
	 * @return IDataset 返回类型
	 * @throws
	 */
	public static IDataset qryRelOfferByComRelOfferIdRelOfferId(String offerCode, String offerType, String relOfferCode, String relOfferType) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("REL_OFFER_CODE", relOfferCode);
		input.put("REL_OFFER_TYPE", relOfferType);

		IData result = call("UPC.Out.OfferQueryFSV.qryRelOfferByComRelOfferIdRelOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * 
	 * @Title: qryCatalogsByUpCatalogId
	 * @Description: TODO
	 * @param @param upcatalogId
	 * @param @return
	 * @param @throws Exception
	 * @return IDataset
	 * @throws
	 * @author longtian3
	 */
	public static IDataset qryCatalogsByUpCatalogId(String upcatalogId, Pagination pagin) throws Exception {
		IData input = new DataMap();
		input.put("UP_CATALOG_ID", upcatalogId);

		IData result = call("UPC.Out.CatalogQueryFSV.qryCatalogsByUpCatalogId", input, pagin);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryCatalogByCatalogId(String catalogId) throws Exception {
		IData input = new DataMap();
		input.put("CATALOG_ID", catalogId);
		IData result = call("UPC.Out.CatalogQueryFSV.qryCatalogByCatalogId", input);
		return result.getDataset("OUTDATA");
	}

	public static IDataset qryOffersByCatalogId(String catalogId, Pagination pg) throws Exception {
		IData input = new DataMap();
		input.put("CATALOG_ID", catalogId);

		IData result = call("UPC.Out.OfferQueryFSV.qryOffersByCatalogId", input, pg);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryOffersByCatalogIdAll(String catalogId, Pagination pg) throws Exception {
		IData input = new DataMap();
		input.put("CATALOG_ID", catalogId);

		IData result = call("UPC.Out.OfferQueryFSV.qryOffersByCatalogIdAll", input, pg);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryOffersByCatalogIdAndOfferId(String catalogId, String offerCode, String offerType) throws Exception {
		IData input = new DataMap();
		input.put("CATALOG_ID", catalogId);
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);

		IData result = call("UPC.Out.OfferQueryFSV.qryOffersByCatalogIdAndOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryCatalogByoffertIdAndupCatalogId(String upCatalogId, String offerCode, String offerType) throws Exception {
		IData input = new DataMap();
		input.put("UP_CATALOG_ID", upCatalogId);
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);

		IData result = call("UPC.Out.CatalogQueryFSV.qryCatalogByOfferIdAndUpCatalogId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryCateByCateId(String catalogId) throws Exception {
		IData input = new DataMap();
		input.put("CATEGORY_ID", catalogId);

		IData result = call("UPC.Out.CateQueryFSV.queryCateByCateId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryTempChaByOfferTable(String offerType, String offerCode, String fromTableName) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("FROM_TABLE_NAME", fromTableName);

		IData result = call("UPC.Out.ChaQueryFSV.queryTempChaByOfferTable", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryTempChaByOfferTableField(String offerType, String offerCode, String fromTableName, String fieldName) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("FROM_TABLE_NAME", fromTableName);
		input.put("FIELD_NAME", fieldName);

		IData result = call("UPC.Out.ChaQueryFSV.queryTempChaByOfferTableField", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryCatalogByOfferId(String offerCode, String offerType) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);

		IData result = call("UPC.Out.CatalogQueryFSV.qryCatalogByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryBrandList(String fieldName) throws Exception {
		IData input = new DataMap();
		input.put("FIELD_NAME", fieldName);
		IData result = call("UPC.Out.ChaQueryFSV.queryComchaValByFiledNameAndValue", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryOffersBySpCond(String spCode, String bizCode, String bizTypeCode) throws Exception {
		IData input = new DataMap();
		input.put("SP_CODE", spCode);
		input.put("BIZ_CODE", bizCode);
		input.put("BIZ_TYPE_CODE", bizTypeCode);

		IData result = call("UPC.Out.OfferQueryFSV.qryOffersBySpCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryOfferFromSaleActiveByOfferId(String offerCode, String offerType) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);

		IData result = call("UPC.Out.OfferQueryFSV.qryOfferFromSaleActiveByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryChaSpecByfieldNameAndvalueAndOfferId(String offerType, String offerCode, String fieldName, String value) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("FIELD_NAME", fieldName);
		input.put("VALUE", value);

		IData result = call("UPC.Out.ChaQueryFSV.queryChaSpecByfieldNameAndvalueAndOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryChaSpecValByOfferId(String serviceId) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", "Z");
		input.put("OFFER_CODE", serviceId);

		IData result = call("UPC.Out.ChaQueryFSV.queryChaSpecValByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryGiftByOfferIdSourceId(String offerType, String offerCode, String pricePlanId) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("PRICE_PLAN_ID", pricePlanId);

		IData result = call("UPC.Out.GiftQueryFSV.qryGiftByOfferIdSourceId", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryOfferByRelOfferIdAndBizTypeCodeAndAndRelTypeAndOperCode(String offerCode, String offerType, String operCode, String bizTypeCode, String relType) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("OPER_CODE", operCode);
		input.put("BIZ_TYPE_CODE", bizTypeCode);
		input.put("REL_TYPE", relType);

		IData result = call("UPC.Out.OfferQueryFSV.qryOfferByRelOfferIdAndBizTypeCodeAndAndRelTypeAndOperCode", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryOfferByOfferIdAndBizTypeCodeAndOperCode(String offerCode, String offerType, String bizTypeCode, String operCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("OPER_CODE", operCode);
		input.put("BIZ_TYPE_CODE", bizTypeCode);

		IData result = call("UPC.Out.OfferQueryFSV.qryOfferByOfferIdAndBizTypeCodeAndOperCode", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset querySpServiceParamByCond(String offerCode, String offerType, String spCode, String bizTypeCode, String bizCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("SP_CODE", spCode);
		input.put("BIZ_TYPE_CODE", bizTypeCode);
		input.put("BIZ_CODE", bizCode);

		IData result = call("UPC.Out.SpQueryFSV.querySpServiceParamByCond", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IData qryOfferComChaTempChaByCond(String offerCode, String offerType) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);

		IData result = call("UPC.Out.ChaQueryFSV.qryOfferComChaTempChaByCond", input);

		IData dataset = result.getData("OUTDATA");

		return dataset;
	}

	public static IDataset qryOfferGiftByExtGiftId(String extGiftId) throws Exception {
		IData input = new DataMap();
		input.put("EXT_GIFT_ID", extGiftId);

		IData result = call("UPC.Out.GiftQueryFSV.qryOfferGiftByExtGiftId", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static boolean hasSpecificOfferRelThisTwoOffer(String offerCodeA, String offerTypeA, String offerCodeB, String offerTypeB, String limitTag) throws Exception {
		IData input = new DataMap();
		input.put("ELEMENT_ID_A", offerCodeA);
		input.put("ELEMENT_TYPE_CODE_A", offerTypeA);
		input.put("ELEMENT_ID_B", offerCodeB);
		input.put("ELEMENT_TYPE_CODE_B", offerTypeB);
		input.put("LIMIT_TAG", limitTag);

		IData result = call("UPC.Out.OfferQueryFSV.hasSpecificOfferRelThisTwoOffer", input);

		String flag = result.getString("OUTDATA");

		return "true".equals(flag) ? true : false;
	}

	public static IDataset qryOfferChaSpecByOfferIdShowMode(String offerCode, String offerType, String showMode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("SHOW_MODE", showMode);

		IData result = call("UPC.Out.ChaQueryFSV.qryOfferChaSpecByOfferIdShowMode", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * @Description 根据service_id查询TD_B_PLATSVC , TD_M_SP_BIZ
	 * @param offerCode
	 * @param offerType
	 * @return
	 * @throws Exception
	 */
	public static IDataset qrySpInfoByOfferId(String offerCode, String offerType) throws Exception {
		// UPC.Out.SpQueryFSV.qrySpInfoByOfferId
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);

		IData result = call("UPC.Out.SpQueryFSV.qrySpInfoByOfferId", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
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
	public static IDataset qrySpRelByAnyOfferIdBizTypeCodeOperCode(String offerCode, String offerType, String bizTypeCode, String operCode) throws Exception {
		// UPC.Out.SpQueryFSV.qrySpRelByAnyOfferIdBizTypeCodeOperCode
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("BIZ_TYPE_CODE", bizTypeCode);
		input.put("OPER_CODE", operCode);

		IData result = call("UPC.Out.SpQueryFSV.qrySpRelByAnyOfferIdBizTypeCodeOperCode", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * @Description 根据NEW_SP_CODE ，NEW_BIZ_CODE查询TD_B_OFFICEDATA_RELATION
	 * @param spCode
	 * @param bizCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryNewSpOfficeData(String spCode, String bizCode) throws Exception {
		// UPC.Out.SpQueryFSV.qryNewSpOfficeData
		IData input = new DataMap();
		input.put("NEW_SP_CODE", spCode);
		input.put("NEW_BIZ_CODE", bizCode);

		IData result = call("UPC.Out.SpQueryFSV.qryNewSpOfficeData", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * 规则相关接口 获取元素下必选参数
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 */

	public static IDataset qryOfferChaSpecsByOfferIdIsNull(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.ChaQueryFSV.qryOfferChaSpecsByOfferIdIsNull", input);
		IDataset data = result.getDataset("OUTDATA");
		return data;
	}

	/**
	 * 规则相关接口 获取元素下必选参数
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 */

	public static IDataset qryOfferChaSpecsByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.ChaQueryFSV.qryOfferChaSpecsByOfferId", input);
		IDataset data = result.getDataset("OUTDATA");
		return data;
	}

	/**
	 * 规则相关接口 依赖互斥
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 */

	public static IDataset queryOfferJoinRelBy2OfferIdRelType(String offerType, String offerCode, String relOfferType, String relOfferCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("REL_OFFER_TYPE", relOfferType);
		input.put("REL_OFFER_CODE", relOfferCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferJoinRelBy2OfferIdRelType", input);
		IDataset data = result.getDataset("OUTDATA");
		return data;
	}
	
	
	/**
	 * 规则相关接口 查询主套餐变更内容信息2018/09/10-wangsc10
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 */
	public static IDataset qryRelOfferEnableByOfferIdRelOfferId(String offerType, String offerCode, String relOfferType, String relOfferCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("REL_OFFER_TYPE", relOfferType);
		input.put("REL_OFFER_CODE", relOfferCode);
		IData result = call("UPC.Out.OfferQueryFSV.qryRelOfferEnableByOfferIdRelOfferId", input);
		IDataset data = result.getDataset("OUTDATA");
		return data;
	}

	/**
	 * 规则相关接口 依赖互斥
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 */

	public static IDataset queryOfferRelByOfferIdAndRelType(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferRelByOfferIdAndRelType", input);
		IDataset data = result.getDataset("OUTDATA");
		return data;
	}

	/**
	 * 规则相关接口 获取元素下必选参数
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 */

	public static IDataset queryOfferRelByRelOfferIdAndRelType(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferRelByRelOfferIdAndRelType", input);
		IDataset data = result.getDataset("OUTDATA");
		return data;
	}

	/**
	 * @Description 根据OLD_SP_CODE ，OLD_BIZ_CODE查询TD_B_OFFICEDATA_RELATION
	 * @param spCode
	 * @param bizCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryOldSpOfficeData(String spCode, String bizCode) throws Exception {
		// UPC.Out.SpQueryFSV.qryOldSpOfficeData
		IData input = new DataMap();
		input.put("OLD_SP_CODE", spCode);
		input.put("OLD_BIZ_CODE", bizCode);

		IData result = call("UPC.Out.SpQueryFSV.qryOldSpOfficeData", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * 根据品牌查询产品名称 duhj
	 * 
	 * @param brandCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryProdSpecByBrandCodeAndProductMode(String brandCode) throws Exception {
		IData data = new DataMap();
		data.put("BRAND_CODE", brandCode);

		IData result = call("UPC.Out.OfferQueryFSV.qryOffersByBrand", data);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IData qryTotalOfferInfoByOfferId(String offertType, String offerCode) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_TYPE", offertType);
		data.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.qryTotalOfferInfoByOfferId", data);
		IData dataset = result.getData("OUTDATA");
		return dataset;
	}

	public static IDataset qryJoinRelOfferInfoByOfferIdCatalogId(String offertType, String offerCode, String catalogId) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_TYPE", offertType);
		data.put("OFFER_CODE", offerCode);
		data.put("CATALOG_ID", catalogId);
		IData result = call("UPC.Out.OfferQueryFSV.qryJoinRelOfferInfoByOfferIdCatalogId", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryOfferFuncStaByAnyOfferIdStatus(String offerCode, String offerType, String funcStatus) throws Exception {
		//
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("FUNC_STATUS", funcStatus);

		IData result = call("UPC.Out.OfferQueryFSV.qryOfferFuncStaByAnyOfferIdStatus", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * @Description 根据offer_code查询(td_b_platsvc_param)表
	 * @param spCode
	 * @param bizCode
	 * @param bizTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySpServiceParamByCond(String spCode, String bizCode, String bizTypeCode, Pagination page) throws Exception {
		IData input = new DataMap();
		input.put("SP_CODE", spCode);
		input.put("BIZ_CODE", bizCode);
		input.put("BIZ_TYPE_CODE", bizTypeCode);

		IData result = call("UPC.Out.SpQueryFSV.querySpServiceParamByCond", input, page, true);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * duhj 2017/03/13
	 * 
	 * @param offerCode
	 * @param offerType
	 * @param bizTypeCode
	 * @param bizStateCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPlatSvc(String offerCode, String offerType, String bizTypeCode, String bizStateCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("BIZ_TYPE_CODE", bizTypeCode);
		input.put("BIZ_STATE_CODE", bizStateCode);

		IData result = call("UPC.Out.SpQueryFSV.qrySpInfo", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	}

	public static IDataset queryOfferRelByRelOfferIdAndRelType(String offerCode, String offerType, String relType) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", offerCode);
		data.put("OFFER_TYPE", offerType);
		data.put("REL_TYPE", relType);
		return call("UPC.Out.OfferQueryFSV.queryOfferRelByRelOfferIdAndRelType", data).getDataset("OUTDATA");
	}

	/**
	 * 规则相关接口 依赖互斥
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 */

	public static IDataset queryOfferRelByOfferIdAndRelType(String offerType, String offerCode, String relType) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("REL_TYPE", relType);
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferRelByOfferIdAndRelType", input);
		IDataset data = result.getDataset("OUTDATA");
		return data;
	}

	/**
	 * 规则相关接口 服务状态依赖互斥 td_s_svcstate_limit
	 * 
	 * @param offerType
	 *            必填
	 * @param offerCode
	 *            必填
	 * @param REL_TYPE
	 *            必填
	 * @param FUNC_STATUS
	 *            必填
	 */

	public static IDataset qryOfferFuncStaRelByCond(String offerType, String offerCode, String ststus, String relType, String tag) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		if ("A".equals(tag)) {
			input.put("FUNC_STATUS_A", ststus);
		} else if ("B".equals(tag)) {
			input.put("FUNC_STATUS_B", ststus);
		}
		input.put("REL_TYPE", relType);
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferFuncStaRelByCond", input);
		IDataset data = result.getDataset("OUTDATA");
		return data;
	}

	public static IDataset qrySpecifyTypeOfferByOfferIdAndType(String offerType, String offerCode, String elementType) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_TYPE", offerType);
		data.put("OFFER_CODE", offerCode);
		data.put("ELEMENT_TYPE", elementType);
		IData result = call("UPC.Out.OfferQueryFSV.qrySpecifyTypeOfferByOfferIdAndType", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryAtomOffersFromGroupByOfferId(String offerType, String offerCode, String elementTypeCode) throws Exception {
		if (StringUtils.isBlank(offerCode) || StringUtils.isBlank(offerType)) {
			return new DatasetList();
		}

		IData data = new DataMap();
		data.put("OFFER_TYPE", offerType);
		data.put("OFFER_CODE", offerCode);
		if (StringUtils.isNotEmpty(elementTypeCode)) {
			data.put("ELEMENT_TYPE_CODE", elementTypeCode);
		}
		IData result = call("UPC.Out.OfferQueryFSV.qryAtomOffersFromGroupByOfferId", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryOfferTempChasByCatalogIdOfferId(String offerType, String offerCode, String catalogId, String upCatalogId, String resId) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_TYPE", offerType);
		data.put("OFFER_CODE", offerCode);
		data.put("CATALOG_ID", catalogId);
		data.put("UP_CATALOG_ID", upCatalogId);
		data.put("RES_ID", resId);

		IData result = call("UPC.Out.OfferQueryFSV.qryOfferTempChasByCatalogIdOfferId", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IData queryTransProducts(String productId) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", productId);
		data.put("OFFER_TYPE", "P");

		IData result = call("UPC.Out.OfferQueryFSV.qryJoinRelOfferInfoByOfferId", data);
		IData rst = result.getData("OUTDATA");
		return rst;
	}

	public static IData queryProductsByCatalogId(String catalogId) throws Exception {
		IData data = new DataMap();
		data.put("UP_CATALOG_ID", catalogId);

		IData result = call("UPC.Out.OfferQueryFSV.qryAllOffersByUpCatalogId", data);
		IData rst = result.getData("OUTDATA");
		return rst;
	}

	public static IDataset qrySpServiceSpInfo(String spCode, String bizCode, String bizTypeCode, String spStatus) throws Exception {
		IData input = new DataMap();

		input.put("SP_CODE", spCode);
		input.put("BIZ_CODE", bizCode);
		input.put("BIZ_TYPE_CODE", bizTypeCode);
		input.put("SP_STATUS", spStatus);

		IData result = call("UPC.Out.SpQueryFSV.qrySpServiceSpInfo", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryAllOffersByOfferIdWithForceTagDefaultTagFilter(String offerType, String offerCode, String forceTag, String defaultTag) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("FORCE_TAG", forceTag);
		input.put("DEFAULT_TAG", defaultTag);

		IData result = call("UPC.Out.OfferQueryFSV.qryAllOffersByOfferIdWithForceTagDefaultTagFilter", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryOfferInfoWithOfferChaByOfferCodes(String offerCodes, String offerTypes) throws Exception {

		IData input = new DataMap();
		String[] offerCodeArray = offerCodes.split(",");
		String[] offerTypesArray = offerTypes.split(",");
		IDataset offers = new DatasetList();
		int length = offerCodeArray.length;
		for (int i = 0; i < length; i++) {
			IData offer = new DataMap();
			offer.put("OFFER_CODE", offerCodeArray[i]);
			offer.put("OFFER_TYPE", offerTypesArray[i]);
			offers.add(offer);
		}
		input.put("INDATA", offers);

		IData result = call("UPC.Out.OfferQueryFSV.qryOfferInfoWithOfferChaByOfferIds", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IData queryOfferEnableMode(String offerType, String offerCode, String groupId, String elementId, String elementType) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("GROUP_ID", groupId);
		input.put("OFFER_TYPE_A", elementType);
		input.put("OFFER_CODE_A", elementId);

		IData result = call("UPC.Out.OfferQueryFSV.qryEnableModeByGroupIdOfferIdWithOfferIdA", input);

		IData data = result.getData("OUTDATA");

		return data;
	}

	public static IDataset queryAtomOffersFromGroupByOfferIdType(String offerCode, String offerType) throws Exception {

		IData input = new DataMap();
		input.put("OFFER_TYPE", "P");
		input.put("OFFER_CODE", offerCode);
		input.put("ELEMENT_TYPE_CODE", offerType);

		IData result = call("UPC.Out.OfferQueryFSV.qryAtomOffersFromGroupByOfferIdType", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * 查询A产品是否可以转换成B产品 duhj
	 * 
	 * @param offerCode
	 * @param offerType
	 * @param rel_offerCode
	 * @param rel_offerType
	 * @return
	 * @throws Exception
	 */
	public static IData queryOfferTransOffer(String offerCode, String offerType, String rel_offerCode, String rel_offerType) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("REL_OFFER_CODE", rel_offerCode);
		input.put("REL_OFFER_TYPE", rel_offerType);
		input.put("REL_TYPE", "0");

		IData result = call("UPC.Out.OfferQueryFSV.qryJoinRelInfoWithEnableModeInfoByOfferId", input);
		IData rst = result.getData("OUTDATA");
		return rst;
	}

	public static IDataset qrySaleActiveCatalogByFactor(String condFactor3) throws Exception {
		IData input = new DataMap();

		input.put("COND_FACTOR3", condFactor3);

		IData result = call("UPC.Out.CatalogQueryFSV.qrySaleActiveCatalogByFactor", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryTerminalSaleActiveCatalog() throws Exception {
		IData input = new DataMap();

		IData result = call("UPC.Out.CatalogQueryFSV.qryTerminalSaleActiveCatalog", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryTerminalSaleActiveCatalogAll() throws Exception {
		IData input = new DataMap();

		IData result = call("UPC.Out.CatalogQueryFSV.qryTerminalSaleActiveCatalogAll", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryTerminalSaleActiveOffer() throws Exception {
		IData input = new DataMap();

		IData result = call("UPC.Out.OfferQueryFSV.qryTerminalSaleActiveOffer", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryTerminalOffersByPkgExtRsrvstrCatalogId(String productId, String deviceTypeCode) throws Exception {
		IData input = new DataMap();
		input.put("CATALOG_ID", productId);
		input.put("DEVICE_MODE_CODE", deviceTypeCode);

		IData result = call("UPC.Out.OfferQueryFSV.qryTerminalOffersByPkgExtRsrvstrCatalogId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryOfferExtChasByCatalogIdOfferId(String productId) throws Exception {
		IData input = new DataMap();
		input.put("CATALOG_ID", productId);

		IData result = call("UPC.Out.OfferQueryFSV.qryOfferExtChasByCatalogIdOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset getTableNameValue(String from_table_name, String field_name, String offer_type, String offer_code) throws Exception {
		IData input = new DataMap();
		input.put("FROM_TABLE_NAME", from_table_name);
		input.put("FIELD_NAME", field_name);
		input.put("OFFER_TYPE", offer_type);
		input.put("OFFER_CODE", offer_code);

		IData result = call("UPC.Out.ChaQueryFSV.queryTempChaByOfferTableField", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryChildrenCatalogsByIdLevel(String catalogLevel, String catalogId) throws Exception {
		IData input = new DataMap();

		input.put("CATALOG_LEVEL", catalogLevel);
		input.put("CATALOG_ID", catalogId);

		IData result = call("UPC.Out.CatalogQueryFSV.qryChildrenCatalogsByIdLevel", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryOfferByOfferIdRelOfferId(String offerCode, String offerType, String relOfferCode, String relOfferType, String queryCha) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("REL_OFFER_CODE", relOfferCode);
		input.put("REL_OFFER_TYPE", relOfferType);
		if (StringUtils.isNotEmpty(queryCha)) {
			input.put("QUERY_CHA", queryCha);
		}
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferByOfferIdRelOfferId", input);
		IData data = result.getData("OUTDATA");

		if (IDataUtil.isEmpty(data)) {
			return null;
		}
		IDataset results = new DatasetList();
		results.add(data);

		return results;
	}

	public static IDataset queryPackageElementsByProductIdDisctypeCode(String offerType, String offerCode, String discntTypeCode) throws Exception {
		IData data = new DataMap();
		if (StringUtils.isNotBlank(offerCode)) {
			data.put("OFFER_CODE", offerCode);
			data.put("FLAG", "1");
		}
		data.put("OFFER_TYPE", offerType);
		data.put("DISCNT_TYPE_CODE", discntTypeCode);
		IData result = call("UPC.Out.OfferQueryFSV.qryOffersByDiscntTypeCode", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset queryLevelCatalogByUpCatalogId(String upCatalogId) throws Exception {
		IData data = new DataMap();
		data.put("UP_CATALOG_ID", upCatalogId);
		IData result = call("UPC.Out.CatalogQueryFSV.qryCatalogHierarchyByUpCatalogId", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	/**
	 * @根据GROUP_ID和OFFER_ID查询组信息
	 * @param groupId
	 *            必填
	 * @param offerType
	 *            必填
	 * @param offercode
	 *            必填
	 * @throws Exception
	 */

	public static IDataset qryOfferGroupRelByOfferIdGroupId(String groupId, String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("GROUP_ID", groupId);
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferGroupRelByOfferIdGroupId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryOfferExtChaByOfferId(String offerCode, String offerType, String fromTableName) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", offerCode);
		data.put("OFFER_TYPE", offerType);
		data.put("FROM_TABLE_NAME", fromTableName);
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferExtChaByOfferId", data);
		IDataset dataset = result.getDataset("OUTDATA");
		if (IDataUtil.isNotEmpty(dataset)) {
			for (int i = 0, size = dataset.size(); i < size; i++) {
				IData iData = dataset.getData(i);
				iData.put("DISCNT_NAME", iData.getString("OFFER_NAME"));
			}
		}
		return dataset;
	}

	public static IDataset qryPricePlanInfoByOfferId(String offerCode, String offerType) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", offerCode);
		data.put("OFFER_TYPE", offerType);
		IData result = call("UPC.Out.PriceQueryFSV.qryPricePlanInfoByOfferId", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryServInfoByrsrvStr(String rsrvstr9) throws Exception {
		IData data = new DataMap();
		data.put("RSRV_STR9", rsrvstr9);
		IData result = call("UPC.Out.SpQueryFSV.qrySpSvcByRsrvStr", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	/**
	 * BUG20190226160500产品变更短信提醒内容存在bug,产品内容有null值展示在短信里。具体见附件，请优化。wangsc10-20190228-根据SERVICE_ID查
	 * 
	 * @param SERVICE_ID
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryServInfoByServiceId(String serviceId) throws Exception {
		IData data = new DataMap();
		data.put("SERVICE_ID", serviceId);
		IData result = call("UPC.Out.SpQueryFSV.qrySpSvcByServiceId", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	/**
	 * duhj
	 * 
	 * @param offerCode
	 * @param offerType
	 * @return
	 * @throws Exception
	 */
	public static IDataset qrySpServiceSpInfo(String offerCode, String offerType) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		IData result = call("UPC.Out.SpQueryFSV.qrySpServiceSpInfo", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset getPackageElementInfoByPorductIdElementId(String productId, String offerType, String elementId, String elementType) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", productId);
		data.put("OFFER_TYPE", offerType);
		data.put("ELEMENT_CODE", elementId);
		data.put("ELEMENT_TYPE", elementType);

		IData result = call("UPC.Out.OfferQueryFSV.qrySpecificOfferFromAllOffersByOfferId", data);

		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static Logger logger = Logger.getLogger(UpcCallIntf.class);

	private final static String offerCode2OfferId(String offerCode, String offerType) throws Exception {
		if (StringUtils.equals(offerType, "P")) {
			Long offerId = Long.parseLong(offerCode) + Long.parseLong("110000000000");
			return offerId + "";
		}
		return "";
	}

	public static IDataset qryCatalogsByCatalogIdCatalogLevel(String catalogIdA, String catalogId, String catalogLevel) throws Exception {
		IData data = new DataMap();
		data.put("CATALOG_ID_A", catalogIdA);
		data.put("CATALOG_ID", catalogId);
		data.put("CATALOG_LEVEL", catalogLevel);

		IData result = call("UPC.Out.CatalogQueryFSV.qryCatalogsByCatalogIdCatalogLevel", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset getElementInfoByGroupId(String groupIdl) throws Exception {
		IData data = new DataMap();
		data.put("GROUP_ID", groupIdl);
		IData result = call("UPC.Out.OfferQueryFSV.qryGroupComOffersExtChaByGroupId", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	/**
	 * add by duhj
	 * 
	 * @param offerCodeA
	 * @param offerTypeA
	 * @param limitTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryOfferRelOfferByOfferIdWithRelTypeFilter(String offerCodeA, String offerTypeA, String limitTag) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCodeA);
		input.put("OFFER_TYPE", offerTypeA);
		input.put("REL_TYPE", limitTag);

		IData result = call("UPC.Out.OfferQueryFSV.qryOfferRelOfferByOfferIdWithRelTypeFilter", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	}

	public static IDataset qryRelOfferExtChaByOfferIdRelOfferId(String offerCode, String offerType, String relOfferCode, String relOfferType) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("REL_OFFER_CODE", relOfferCode);
		input.put("REL_OFFER_TYPE", relOfferType);

		IData result = call("UPC.Out.OfferQueryFSV.qryRelOfferExtChaByOfferIdRelOfferId", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	}

	public static IDataset qryOfferGiftsByOfferId(String offerCode, String offerType) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);

		IData result = call("UPC.Out.GiftQueryFSV.qryGiftsByOfferId", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	}

	public static IDataset qryOfferNamesByOfferTypesOfferCodes(IDataset offer_type_code_list, String queryCatalogId) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE_CODE_LIST", offer_type_code_list);
		if (StringUtils.isNotEmpty(queryCatalogId)) {
			input.put("QUERY_CATALOG_ID", queryCatalogId);
		}
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferNamesByOfferTypesOfferCodes", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	}

	public static IDataset qryEnableModeInfoByRelObjectAndId(String relObj, String relObjId) throws Exception {
		IData input = new DataMap();
		input.put("REL_OBJECT", relObj);
		input.put("REL_OBJECT_ID", relObjId);

		IData result = call("UPC.Out.OfferQueryFSV.qryEnableModeInfoByRelObjectAndId", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	}

	public static IDataset queryGroupComRel(String groupId, String offerType, String offerCode) throws Exception {
		IData input = new DataMap();
		input.put("GROUP_ID", groupId);
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);

		IData result = call("UPC.Out.OfferQueryFSV.qryGroupComRelByOfferIdGroupId", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	}

	public static IDataset qryOffersByBrandWithProductModeFilter(String brandCode, String productMode) throws Exception {
		IData input = new DataMap();
		input.put("BRAND_CODE", brandCode);
		input.put("PRODUCT_MODE", productMode);
		IData result = call("UPC.Out.OfferQueryFSV.qryOffersByBrandWithProductModeFilter", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	}

	public static IDataset qryOffersWithOfferTypeFilter(String offerType) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);

		IData result = call("UPC.Out.OfferQueryFSV.qryOffersWithOfferTypeFilter", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	}

	public static IDataset queryPlatSvc2(String spCode, String bizCode, String bizTypeCode, String bizStateCode) throws Exception {
		IData input = new DataMap();
		input.put("SP_CODE", spCode);
		input.put("BIZ_CODE", bizCode);
		input.put("BIZ_TYPE_CODE", bizTypeCode);
		input.put("BIZ_STATE_CODE", bizStateCode);

		IData result = call("UPC.Out.SpQueryFSV.qrySpInfo", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	}

	public static IDataset queryTerrace(String spName, String bizName, String bizTypeCode, String maxRetrue) throws Exception {
		IData input = new DataMap();
		input.put("SP_NAME", spName);
		input.put("BIZ_NAME", bizName);
		input.put("BIZ_TYPE_CODE", bizTypeCode);
		input.put("MAX_RETRUE", maxRetrue);

		IData result = call("UPC.Out.SpQueryFSV.querySpSvcByRsrvStrAndPage", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	// 短信模板获取数据
	public static IDataset qryOffersByPkgEleRsrvTagOfferIds(IDataset offer_package_list) throws Exception {
		IData input = new DataMap();
		input.put("ELEMENT_LIST", offer_package_list);
		IData result = call("UPC.Out.OfferQueryFSV.qryOffersByPkgEleRsrvTagOfferIds", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	}

	/**
	 * 查询平台业务信息通过SP_CODE,BIZ_CODE
	 * 
	 * @param spCode
	 * @param bizCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryBizInfoBySpcodeBizCode(String spCode, String bizCode) throws Exception {
		IData input = new DataMap();
		input.put("SP_CODE", spCode);
		input.put("BIZ_CODE", spCode);

		IData result = call("UPC.Out.SpQueryFSV.qrySpSvcBySpCodeBizCode", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * 通过SP_NAME,BIZ_NAME模糊查询平台业务信息
	 * 
	 * @param spName
	 * @param bizName
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryBizInfoBySpNameBizName(String spName, String bizName) throws Exception {
		IData input = new DataMap();
		input.put("SP_NAME", spName);
		input.put("BIZ_NAME", bizName);

		IData result = call("UPC.Out.SpQueryFSV.qrySpSvcByLikeSpNameBizName", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryRelByElementAElementB(String elementA, String elementB, String element_type_codeA, String element_type_codeB) throws Exception {
		IData input = new DataMap();
		input.put("ELEMENET_TYPE_CODE_A", element_type_codeA);// element_type_code_a
		input.put("ELEMENET_ID_B", elementB);// element_id_b
		input.put("ELEMENET_ID_A", elementA);// element_id_b
		input.put("ELEMENET_TYPE_CODE_B", element_type_codeB);

		IData result = call("UPC.Out.OfferQueryFSV.qryOfferRelByElementAElementB", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	}

	public static IDataset queryTempChasByChaIdTableName(String from_table_name, String offer_type, String offer_code) throws Exception {
		IData input = new DataMap();
		input.put("FROM_TABLE_NAME", from_table_name);
		input.put("OFFER_TYPE", offer_type);
		input.put("OFFER_CODE", offer_code);

		IData result = call("UPC.Out.OfferQueryFSV.queryOfferByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset queryOfferRelInfoByTwoOfferOrInversionIfNecessary(String offerType, String offerCode, String relOfferType, String relOfferCode, String relType) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("REL_OFFER_TYPE", relOfferType);
		input.put("REL_OFFER_CODE", relOfferCode);
		input.put("REL_TYPE", relType);
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferRelInfoByTwoOfferOrInversionIfNecessary", input);
		return result.getDataset("OUTDATA");
	}

	public static IDataset qryNeglectDateOfferByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.qryNeglectDateOfferByOfferId", input);
		return result.getDataset("OUTDATA");
	}

	public static IDataset queryOfferRelWithDiscntTypeFilter(String offerType, String offerCode, String relType, String discntTypeCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("REL_TYPE", relType);
		input.put("DISCNT_TYPE_CODE", discntTypeCode);
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferRelWithDiscntTypeFilter", input);
		return result.getDataset("OUTDATA");
	}

	public static IDataset queryServiceByRsrvTag3(String offerType, String offerCode, String fromTableName, String fieldName) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("FROM_TABLE_NAME", fromTableName);
		input.put("FIELD_NAME", fieldName);

		IData result = call("UPC.Out.ChaQueryFSV.queryTempChaByOfferTableField", input);

		return result.getDataset("OUTDATA");
	}

	/**
	 * duhj
	 * 
	 * @param offerType
	 * @param offerCode
	 * @param discntTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryAllOffersByOfferIdWithDiscntTypeFilter(String offerType, String offerCode, String discntTypeCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("DISCNT_TYPE_CODE", discntTypeCode);
		IData result = call("UPC.Out.OfferQueryFSV.qryAllOffersByOfferIdWithDiscntTypeFilter", input);
		return result.getDataset("OUTDATA");
	}

	public static IDataset qryByServiceIdBillType(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.SpQueryFSV.qryByServiceIdBillType", input);
		return result.getDataset("OUTDATA");
	}

	public static IDataset qryOfferLimitNpByOfferId(String offerType, String offerCode, String limitTag) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("LIMIT_TAG", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferLimitNpByOfferId", input);
		return result.getDataset("OUTDATA");
	}

	public static IDataset qryOfferLimitNpOfferByOfferId(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferLimitNpOfferByOfferId", input);
		return result.getDataset("OUTDATA");
	}

	public static IDataset qryGroupComRelExtChaByGroupIdOfferId(String groupId, String offerCode, String offerType) throws Exception {
		IData data = new DataMap();
		data.put("GROUP_ID", offerCode);
		data.put("OFFER_CODE", offerCode);
		data.put("OFFER_TYPE", offerType);
		IData result = call("UPC.Out.ChaQueryFSV.qryGroupComRelExtChaByGroupIdOfferId", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryOfferFromExtCha(String tableName, String fieldName, String fieldValue) throws Exception {
		IData data = new DataMap();
		data.put("FROM_TABLE_NAME", tableName);
		data.put("FIELD_NAME", fieldName);
		data.put("FIELD_VALUE", fieldValue);
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferFromExtCha", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qrySaleActiveCatalogs() throws Exception {
		IData input = new DataMap();
		IData result = call("UPC.Out.CatalogQueryFSV.qrySaleActiveCatalogs", input);
		return result.getDataset("OUTDATA");
	}

	private final static IData call(String svcName, IData input) throws Exception {
		return call(svcName, input, null, true);
	}

	private final static IData call(String svcName, IData input, boolean iscatch) throws Exception {
		return call(svcName, input, null, true);
	}

	private final static IData call(String svcName, IData input, Pagination pagin) throws Exception {
		return call(svcName, input, pagin, true);
	}

	private final static IData call(String svcName, IData input, Pagination pagin, boolean iscatch) throws Exception {
		String url = GlobalCfg.getProperty("upc.router.addr");// 这里不用上生产，测试用
		ServiceResponse response;
		if (StringUtils.isNotEmpty(url)) {
			ServiceRequest request = new ServiceRequest();
			request.setData(input);
			response = BizServiceFactory.call(url, svcName, request, pagin, iscatch, false, 6000000, 60000);
		} else {
			response = BizServiceFactory.call(svcName, input, pagin);
		}
		IData out = response.getBody();
		if (pagin != null)
			out.put("X_COUNTNUM", response.getDataCount());
		return out;
	}
	
	/**
	 * <p>
	 * Title: qryOffersByOfferTypeLikeOfferName
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param offerType
	 * @param offerCode
	 * @param offerName
	 * @return
	 * @author XUYT
	 * @throws Exception
	 * @date 2017-4-11 下午06:03:37
	 */
	public static IDataset qryOffersByOfferTypeLikeOfferName(String offerType, String offerCode, String offerName) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_NAME", offerName);
		data.put("OFFER_CODE", offerCode);
		data.put("OFFER_TYPE", offerType);
		IData result = call("UPC.Out.OfferQueryFSV.qryOffersByOfferTypeLikeOfferName", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryOffersByFixedCatalogId() throws Exception {
		IData data = new DataMap();
		IData result = call("UPC.Out.OfferQueryFSV.qryOffersByFixedCatalogId", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	/**
	 * <p>
	 * Title: synBBossPoInfo
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @date 2017-4-15 下午04:03:37
	 */
	public static IData synBBossPoInfo(IData data) throws Exception {
		IData input = new DataMap();
		input.put("INPUT_DATA", data);
		IData result = call("UPC.Out.IBBossSyncOperateFSV.processPosSyncInfoFromGroup", input);
		// IDataset dataset = result.getDataset("OUTDATA");
		return result;
	}

	public static IDataset qryOfferCatalogByOfferId(String offerType, String offerCode) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", offerCode);
		data.put("OFFER_TYPE", offerType);
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferCatalogByOfferId", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryOfferByOfferIdNameMode(String offerType, String offerCode, String name, String mode) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", offerCode);
		data.put("OFFER_TYPE", offerType);
		data.put("NAME", name);
		data.put("MODE", mode);
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferByOfferIdNameMode", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qrySaleActiveCompOfferPricPlan(String offerType, String offerCode, String relOfferType, String relOfferCode, String tradeTypeCode, String catalogId) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_TYPE", offerType);
		data.put("OFFER_CODE", offerCode);
		data.put("REL_OFFER_TYPE", relOfferType);
		data.put("REL_OFFER_CODE", relOfferCode);
		data.put("TRADE_TYPE_CODE", tradeTypeCode);
		data.put("CATALOG_ID", catalogId);
		
		IData result = call("UPC.Out.PriceQueryFSV.qrySaleActiveCompOfferPricPlan", data);
		return result.getDataset("OUTDATA");
	}

	public static IDataset qryGroupInfoByGroupIdOfferId(String offerType, String offerCode, String groupId) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_TYPE", offerType);
		data.put("OFFER_CODE", offerCode);
		data.put("GROUP_ID", groupId);

		IData result = call("UPC.Out.GroupQueryFSV.qryGroupInfoByGroupIdOfferId", data);
		return result.getDataset("OUTDATA");
	}

	public static IDataset qrySaleActiveOffersByResIdOfferIdCatalogId(String offerType, String offerCode, String catalogId, String upCatalogId, String resId) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_TYPE", offerType);
		data.put("OFFER_CODE", offerCode);
		data.put("CATALOG_ID", catalogId);
		data.put("UP_CATALOG_ID", upCatalogId);
		data.put("RES_ID", resId);

		IData result = call("UPC.Out.OfferQueryFSV.qrySaleActiveOffersByResIdOfferIdCatalogId", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset querySpInfoServiceAndProdBySpBizInfo(String spCode) throws Exception {
		IData data = new DataMap();
		data.put("SP_CODE", spCode);

		IData result = call("UPC.Out.SpQueryFSV.querySpInfoServiceAndProdBySpBizInfo", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qrySaleOfferTradeLimits(String catalogId, String offerCode) throws Exception {
		IData data = new DataMap();
		data.put("CATALOG_ID", catalogId);
		data.put("OFFER_ID", offerCode);

		IData result = call("UPC.Out.OfferQueryFSV.qrySaleOfferTradeLimits", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryExtChasOffers(String fromTableName, String fieldName, String fieldValue) throws Exception {
		IData data = new DataMap();
		data.put("FROM_TABLE_NAME", fromTableName);
		data.put("FIELD_NAME", fieldName);
		data.put("FIELD_VALUE", fieldValue);

		IData result = call("UPC.Out.OfferQueryFSV.qryExtChaOffers", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryGroupInfoByOfferIdAndGroupOfferId(String offerCode, String offerType, String relOfferCode, String relOfferType) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", offerCode);
		data.put("OFFER_TYPE", offerType);
		data.put("GROUP_OFFER_CODE", relOfferCode);
		data.put("GROUP_OFFER_TYPE", relOfferType);

		IData result = call("UPC.Out.GroupQueryFSV.qryGroupInfoByOfferIdAndGroupOfferId", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qrySaleTerminalLimit(String offerCode, String offerType, String catalogId, String terminalTypeCode, String terminalModeCode) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", offerCode);
		data.put("OFFER_TYPE", offerType);
		data.put("CATALOG_ID", catalogId);
		data.put("TERMINAL_TYPE_CODE", terminalTypeCode);
		data.put("TERMINAL_MODE_CODE", terminalModeCode);

		IData result = call("UPC.Out.OfferQueryFSV.qrySaleTerminalLimit", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset queryOfferEnableModeByGroupId(String groupId, String elementId, String elementType) throws Exception {
		IData input = new DataMap();
		input.put("GROUP_ID", groupId);
		input.put("OFFER_TYPE", elementType);
		input.put("OFFER_CODE", elementId);

		IData result = call("UPC.Out.OfferQueryFSV.queryGroupComRelOfferEnableByGroupIdOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryServiceRes(String offerCode, String offerType) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_CODE", offerCode);
		data.put("OFFER_TYPE", offerType);

		IData result = call("UPC.Out.OfferQueryFSV.qryServiceRes", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

	public static IDataset qryDynamicPrice(String offerCode, String offerType, String relOfferCode, String relOfferType, String tradeTypeCode, String inModeCode, String vipClassId, String groupId) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_TYPE", offerType);
		data.put("OFFER_CODE", offerCode);
		data.put("REL_OFFER_TYPE", relOfferType);
		data.put("REL_OFFER_CODE", relOfferCode);
		data.put("TRADE_TYPE_CODE", tradeTypeCode);
		data.put("IN_MODE_CODE", inModeCode);
		data.put("VIP_CLASS_ID", vipClassId);
		data.put("OFFER_GROUP_ID", groupId);
		
		// OFFER_TYPE=P,OFFER_CODE=10000794,REL_OFFER_TYPE=-1,TRADE_TYPE_CODE=10
		IData result = call("UPC.Out.PriceQueryFSV.qryDynamicPrice", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}
	
	public static IDataset qryDynamicPrice(String tradeTypeCode, String catalogId, String offerCode, String offerType, String relOfferCode, String relOfferType, String inModeCode) throws Exception {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("CATALOG_ID", catalogId);
        data.put("OFFER_TYPE", offerType);
        data.put("OFFER_CODE", offerCode);
        data.put("REL_OFFER_TYPE", relOfferType);
        data.put("REL_OFFER_CODE", relOfferCode);
        data.put("IN_MODE_CODE", inModeCode);
        
        IData result = call("UPC.Out.PriceQueryFSV.qryDynamicPrice", data);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }

	public static IDataset qryOfferChaValByCond(String offerCode, String offerType, String attrCode, String attrfieldName, String mgmtDistrict) throws Exception {
		IData data = new DataMap();
		data.put("OFFER_TYPE", offerType);
		data.put("OFFER_CODE", offerCode);
		data.put("ATTR_CODE", attrCode);
		data.put("ATTR_FIELD_NAME", attrfieldName);
		data.put("MGMT_DISTRICT", mgmtDistrict);
		IData result = call("UPC.Out.ChaQueryFSV.qryOfferChaValByCond", data);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}
	
	public static IDataset qryContractMaterialOffer(String productMode, String paraCode, String eparchyCode) throws Exception {
        IData data = new DataMap();
        data.put("PRODUCT_MODE", productMode);
        data.put("PARA_CODE", paraCode);
        data.put("EPARCHY_CODE", eparchyCode);
        IData result = call("UPC.Out.OfferQueryFSV.qryContractMaterialOffer", data);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }
	
	public static IDataset qryOfferTax(String offerCode, String offerType, String relOfferCode, String relOfferType, String catalogId, String groupId, String relOfferId, String tradeTypeCode) throws Exception {
        IData data = new DataMap();
        data.put("OFFER_TYPE", offerType);
        data.put("OFFER_CODE", offerCode);
        data.put("REL_OFFER_TYPE", relOfferType);
        data.put("REL_OFFER_CODE", relOfferCode);
        data.put("CATALOG_ID", catalogId);
        data.put("GROUP_ID", groupId);
        data.put("REL_OFFER_ID", relOfferId);
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        IData result = call("UPC.Out.OfferQueryFSV.qryOfferTax", data);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }
	
	public static IDataset qrySpecificOfferFromAllOffersByOfferId(String offerCode, String offerType, String elementId, String elementTypeCode) throws Exception
    {
	    IData data = new DataMap();
        data.put("OFFER_TYPE", offerType);
        data.put("OFFER_CODE", offerCode);
        data.put("ELEMENT_TYPE", elementTypeCode);
        data.put("ELEMENT_CODE", elementId);
        
        IData result = call("UPC.Out.OfferQueryFSV.qrySpecificOfferFromAllOffersByOfferId", data);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }
	
	public static IDataset qryStaticParam(String tableName, String cols, String keys, String values) throws Exception
    {
        IData data = new DataMap();
        data.put("TABLE_NAME", tableName);
        data.put("COLS", cols);
        data.put("KEYS", keys);
        data.put("VALUES", values);
        
        IData result = call("UPC.Out.OfferQueryFSV.qryStaticParam", data);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }
	/**
	 * *特征相关接口(TD_B_DISCNT) *根据,FILED_NAME,VALUE 查询offer_code
	 *            必填
	 * @param filedName
	 *            必填
	 * @param value
	 * 
	 * @author  add by fangwz
	 */

	public static IDataset queryChaCodeValByfiledName(String filedName, String value) throws Exception {
		IData input = new DataMap();
		input.put("FIELD_NAME", filedName);
		input.put("FIELD_VALUE", value);
		IData result = call("UPC.Out.ChaQueryFSV.queryOfferComChaByFileNameAndFileValue", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	/**
	 * *TD_B_PROD_TRADE_LIMIT
	 *            必填
	 * @param TRADE_TYPE_CODE
	 * 
	 * @author  add by fangwz
	 */
	public static IDataset queryProdTradeLimit(String pId, String idType,String eparchyCode, String tradeTypeCode) throws Exception {
		IData input = new DataMap();
		input.put("PID", pId);
		input.put("ID_TYPE", idType);
		input.put("EPARCHY_CODE", eparchyCode);
		input.put("TRADE_TYPE_CODE", tradeTypeCode);
		IData result = call("UPC.Out.ProdQueryFSV.queryProdTradeLimitByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset queryOfferNameByOfferId(String offerId) throws Exception {
		IData input = new DataMap(); 
		input.put("OFFER_ID", offerId); 
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferNameByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset qryOfferCatalogInfoByCatalogId(String catalogId, String fromTableName, String fieldName) throws Exception
	{
		IData input = new DataMap(); 
		input.put("CATALOG_ID", catalogId); 
		input.put("FROM_TABLE_NAME", fromTableName); 
		input.put("FIELD_NAME", fieldName);
		IData result = call("UPC.Out.CatalogQueryFSV.qryOfferCatalogInfoByCatalogId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset qryOfferChaByOfferId(String offerCode, String offerType, String qryOfferType) throws Exception
	{
		IData input = new DataMap(); 
		input.put("OFFER_CODE", offerCode); 
		input.put("OFFER_TYPE", offerType); 
		input.put("QRY_OFFER_TYPE", qryOfferType);
		IData result = call("UPC.Out.ChaQueryFSV.qryOfferChaByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset saveSpInfoCs(IData param) throws Exception{
		IData result = call("UPC.Out.SpQueryFSV.saveSpInfoCs", param);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	/**
	 * 商产品规格同步接口
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset processJkdtSyncInfo(IData data) throws Exception
	{
		IData result = call("UPC.Out.IBBossSyncOperateFSV.syncDataFromGroupPlatform", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset processBBossSyncInfo(IData data) throws Exception
	{
		IData result = call("UPC.Out.IBBossSyncOperateFSV.processBBossSyncInfo", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset qryBureDataSpDtlAndBatImport(IData data) throws Exception
	{
		IData result = call("UPC.Out.SpQueryFSV.qryBureDataSpDtlAndBatImport", data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryBureDataBatImport(IData data) throws Exception
	{
		IData result = call("UPC.Out.SpQueryFSV.qryBureDataBatImport",data);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset qrySaleActiveInfo(String offerType, String offerCode) throws Exception
	{
		IData input = new DataMap(); 
		input.put("OFFER_CODE", offerCode); 
		input.put("OFFER_TYPE", offerType); 
		IData result = call("UPC.Out.OfferQueryFSV.qrySaleActiveInfo", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset qryOfferTempChasByCatalogId(String catalogId) throws Exception
	{
		IData input = new DataMap(); 
		input.put("CATALOG_ID", catalogId); 
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferTempChasByCatalogId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset qryOfferTempChasByCataIdAndCataName(String catalogId,String catalogName) throws Exception
	{
		IData input = new DataMap(); 
		input.put("CATALOG_ID", catalogId); 
		input.put("CATALOG_NAME", catalogName); 
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferTempChasByCataIdAndCataName", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset qryOfferTempChasByPackageName(String packageName) throws Exception
	{
		IData input = new DataMap(); 
		input.put("PACKAGE_NAME", packageName); 
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferTempChasByCataIdAndCataName", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset qryMountOfferByOfferId(String offerCode, String offerType) throws Exception
	{
		IData input = new DataMap(); 
		input.put("OFFER_CODE", offerCode); 
		input.put("OFFER_TYPE", offerType); 
		IData result = call("UPC.Out.OfferQueryFSV.qryMountOfferByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset qrySaleActiveProductByLabelId(String labelId) throws Exception
	{
		IData input = new DataMap(); 
		input.put("LABEL_ID", labelId); 
		IData result = call("UPC.Out.OfferQueryFSV.qrySaleActiveProductByLabelId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset qrySaleActivePackageByLabelId(String labelId) throws Exception
	{
		IData input = new DataMap(); 
		input.put("LABEL_ID", labelId); 
		IData result = call("UPC.Out.OfferQueryFSV.qrySaleActivePackageByLabelId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static IDataset qryOfferRelWithInverse(String offerCode, String offerType, String relType) throws Exception
	{
		IData input = new DataMap(); 
		input.put("OFFER_CODE", offerCode); 
		input.put("OFFER_TYPE", offerType);
		input.put("REL_TYPE", relType);
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferRelWithInverse", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	public static IDataset queryOfferNameByOfferCodeAndType(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode); 
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferNameByOfferCodeAndType", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}
	
	public static IDataset qryDataInfos(String OfferCode, String OfferName) throws Exception
	{
		IData input = new DataMap(); 
		input.put("COMM_ID", OfferCode);
		input.put("COMM_NAME", OfferName);
		IData result = call("UPC.Out.DataOrderQueryFSV.qryDataInfos", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset; 
	}  

	public static IDataset queryOfferByOfferCodeAndOfferType(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode); 
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferNameByOfferCodeAndType", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset; 
	}  
	
	//查询offerId
	public static IDataset queryOfferIdByOfferCodeAndOfferType(String offerType, String offerCode) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode); 
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferByOfferCodeAndOfferType", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset; 
	}
	
	public static IDataset qryOfferGiftsByParamOfferId(String offerId ) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_ID", offerId);
		IData result = call("UPC.Out.GiftQueryFSV.qryGiftsByOfferId", input);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	} 
	
	
	public static IDataset qryOfferRelsByOfferCode1Code2(String offerCode_A,String offerCode_B,String offerType,String relType) throws Exception {
		IData param = new DataMap();
		param.put("OFFER_CODE_A", offerCode_A);
		param.put("OFFER_CODE_B", offerCode_B);
		param.put("OFFER_TYPE", offerType);
		param.put("REL_TYPE", relType); 
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferRelsByOfferCode", param);

		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;

	} 
	
    public static IDataset querySpServiceByIdAndBizStateCode(String offerCode, String offerType, String bizStateCode) throws Exception
    {
        IData input = new DataMap();

        input.put("OFFER_TYPE", offerType);
        input.put("OFFER_CODE", offerCode);
        input.put("BIZ_STATE_CODE", bizStateCode);

        IData result = call("UPC.Out.SpQueryFSV.qrySpServiceByIdAndBizStateCode", input);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }
    
    public static IDataset queryMiguCampInfoByServiceId(String memberType,String spServiceId,String startDate) throws Exception {
    	IData input = new DataMap();
        input.put("MEMBER_TYPE", memberType);
        input.put("OFFER_TYPE", "Z");
        input.put("OFFER_CODE", spServiceId);
        input.put("START_DATE", startDate);
        IData result = call("UPC.Out.SpQueryFSV.queryMiguCampInfoByServiceId", input);
        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }
    
    /**
     * 通过offerId获取offerCode
     * @param offerId
     * @param queryComCha
     * @return
     * @throws Exception
     */
    public static IData queryOfferByOfferId(String offerId , String queryComCha) throws Exception
    {
    	if(StringUtils.isBlank(offerId)){
    		return null;
    	}
        IData input = new DataMap();
        
        input.put("OFFER_ID",offerId);
        if(StringUtils.isNotEmpty(queryComCha)){
            input.put("QUERY_COM_CHA",queryComCha);
        }
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferByOfferId", input);
        IDataset dataset = result.getDataset("OUTDATA");
        if(IDataUtil.isNotEmpty(dataset))
        {
            return dataset.getData(0);
        }
        return null;
    }
    
    public static IDataset queryOfferJoinRelByRelOfferIdRelType(String offerCode, String relOfferCode, String relType)throws Exception 
    {
        IData input = new DataMap();
        input.put("OFFER_ID", offerCode);
        input.put("REL_OFFER_ID", relOfferCode); 
        input.put("REL_TYPE", relType);
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferJoinRelBy2OfferIdRelType", input);

        IDataset dataset = result.getDataset("OUTDATA") ;

        return dataset;
    }
    public static IDataset queryMebGroupByOfferId(String offerId, String mgmtDistrict) throws Exception
    {
        IData param = new DataMap();
        param.put("OFFER_ID", offerId);
        param.put("MGMT_DISTRICT", mgmtDistrict);
        IData result = call("UPC.Out.GroupQueryFSV.queryMebGroupByOfferId", param);
        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }

    public static IDataset queryCampaignInfoByCampaignId (String campaignId,String startDate) throws Exception {
    	IData input = new DataMap();
        input.put("CAMPAIGN_ID", campaignId);
        input.put("START_DATE", startDate);
        IData result = call("UPC.Out.SpQueryFSV.queryMiguCampInfoByCampaignId", input);
        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }
    
    public static IDataset queryOfferInfoByOfferCodeAndOfferType(String offerCode, String offerType) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode); 
		IData result = call("UPC.Out.OfferQueryFSV.queryOfferByOfferCodeAndOfferType", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset; 
	}
	/**
	 * UPC.Out.BbossService.queryOfferMappingByLocalNumber，2个入参OFFER_TYPE，OFFER_CODE，都为必传参数
	 * @param offerCode
	 * @param offerType
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryOfferMappingByLocalNumber(String offerCode, String offerType) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		IData result = call("UPC.Out.BbossQueryFSV.queryOfferMappingByLocalNumber", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}
	/**
	 * UPC.Out.BbossService.queryOfferMappingByBossNumber，2个入参PO_NUMBER，TYPE（0-商品 1-产品 2-资费），根据TYPE传不同的全网编码，都为必传参数
	 * * @param offerCode
	 * @param offerType
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryOfferMappingByBossNumber(String po_number, String type) throws Exception {
		IData input = new DataMap();
		input.put("PO_NUMBER", po_number);
		input.put("TYPE",  type);
		IData result = call("UPC.Out.BbossQueryFSV.queryOfferMappingByBossNumber", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}
	/**
	 * UPC.Out.BbossService.queryPoProductPlusInfo，2个入参，PRODUCTSPECNUMBER（产品编码，必传），PRODUCTSPECCHARACTERNUMBER（产品属性编码，非必传，如不传则返回该产品所有属性）
	 * * @param offerCode
	 * @param offerType
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPoProductPlusInfo(String productspecnumber, String productspeccharacternumber) throws Exception {
		IData input = new DataMap();

		input.put("PRODUCTSPECNUMBER", productspecnumber);
		input.put("PRODUCTSPECCHARACTERNUMBER",  productspeccharacternumber);
		IData result = call("UPC.Out.BbossQueryFSV.queryPoproductPlusbyProductSpecNumber", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}


    public static IDataset queryOrderReconfirm(String commodityCode) throws Exception{
		IData input = new DataMap();
		input.put("COMMODITY_CODE", commodityCode);
		IData result = call("UPC.Out.BureauDataQueryFSV.queryOrderReconfirmByCommodityCodes", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	public static IDataset querySepcReconfirmCond(IData input) throws Exception{
		IData result = call("UPC.Out.BureauDataQueryFSV.querySepcReconfirmCond", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	public static IDataset queryChannelReconfirm(String channelSource) throws Exception{
		IData input = new DataMap();
		input.put("CHANNEL_SOURCE", channelSource);
		IData result = call("UPC.Out.BureauDataQueryFSV.queryChannelReconfirmByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	public static IDataset queryOrderReconfirmByInternalCode(String internalCode) throws Exception{
        IData input = new DataMap();
        input.put("INTERNAL_CODE", internalCode);
        IData result = call("UPC.Out.BureauDataQueryFSV.queryOrderReconfirmByInternalCode", input);
        IDataset dataset = result.getDataset("OUTDATA");
        return dataset;
    }

   /**
     * JKDT从集团库 查询 优惠 对应的集团编码  daidl
     * @param poSpecNumber
     * @param poRateNumber
     * @param ratePlanID
     * @return
     * @throws Exception
     */
	public static IDataset getRateplanByNumberAndType(String poSpecNumber,String ratetype,String ratePlanID)throws Exception{
	    IData input = new DataMap();
	    input.put("POSPECNUMBER",poSpecNumber);
	    input.put("RATETYPE",ratetype);
	    input.put("RATEPLANID",ratePlanID);
	    IData data = call("UPC.Out.BbossService.queryPoratePlanByNumberAndType",input);
	    IDataset result = data.getDataset("OUTDATA");
	    return result; 
    } 
	
	//获取0000是否可查询信息及基础价格
	public static IDataset queryOfferChaByOfferCode(String offerCode,String offerType) throws Exception{
   	IData input = new DataMap();
       input.put("OFFER_CODE", offerCode);
       input.put("OFFER_TYPE", offerType);
       IData result = call("UPC.Out.PriceQueryFSV.query0000CancelPriceByOfferId", input);
       IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
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
        IData input = new DataMap();
        
        input.put("OFFER_CODE",offerCode);
        input.put("OFFER_TYPE",offerType);
        
        IData result = call("UPC.Out.TagQueryFSV.qryAllTagAndTagValueByOfferId", input);
        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }
    
    /**
     * 从UpcViewCall中迁移过来的，商品订购要用
     * *根据产品，标签，标签值，品类查询下面的产品
     * @author guohuan
     * @param groupId 必填
     * @param groupType 必填
     * @throws Exception
     */
     
    public static IDataset qryOfferByTagInfo(String offerCode, String offerType, String labelId, String labelKeyId, String categoryId) throws Exception
    {
        IData input = new DataMap();
        
        input.put("OFFER_CODE",offerCode);
        input.put("OFFER_TYPE",offerType);
        input.put("LABEL_ID",labelId);
        input.put("LABEL_KEY_ID",labelKeyId);
        input.put("CATEGORY_ID",categoryId);
        IData result = call("UPC.Out.OfferQueryFSV.qryOfferByTagInfo", input);
        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }
    
	
	/**
	 * 
	 * @param pospecnumber 产商品规格编码或产品规格编码
	 * @param ratetype 类型资费：0产商品资费 1产品资费
	 * @param rateplanid 资费ID
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPoratePlanByNumberAndType(String pospecnumber, String ratetype, String rateplanid) throws Exception {
		IData input = new DataMap();
		input.put("POSPECNUMBER", pospecnumber);
		input.put("RATETYPE", ratetype); 
		input.put("RATEPLANID", rateplanid); 
		IData result = call("UPC.Out.BbossService.queryPoratePlanByNumberAndType", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}

    // ---------------------------------------------------------------家庭增加方法--------------------------------------------------------------------------------
    /**
     * @Description: 商品关联的权益包查询
     * @Param: [offerCode, offerType]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/8 16:21
     */
    public static IDataset queryMainOfferRelaWelfareOffers(String offerCode, String offerType) throws Exception
    {
        IData input = new DataMap();
        input.put("OFFER_TYPE", offerType);
        input.put("OFFER_CODE", offerCode);
        //input.put("REL_TYPE", "5");
        IData result = call("UPC.Out.OfferQueryFSV.qryRightsByofferId", input);
        IDataset results = result.getDataset("OUTDATA");
        return results;
    }

    /**
     * @Description: 组下构成关系配置查询
     * @Param: [groupId, eparchyCode]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/24 17:43
     */
    public static IDataset queryFamilyGroupComRelOfferPriv(String groupId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("GROUP_ID", groupId);
        data.put("MGMT_DISTRICT", eparchyCode);
        IData result = call("UPC.Out.OfferService.queryGroupComRelOfferByGroupIdPriv", data);
        IDataset dataset = result.getDataset("OUTDATA");
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData temp = dataset.getData(i);
                temp.put("GROUP_ID", groupId);
            }
        }
        return dataset;
    }

    /**
     * @Description: 查询产品下组
     * @Param: [offerType, offerCode, groupId, queryCha]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/28 10:21
     */
    public static IDataset queryFamilyOfferGroupRelOfferIdGroupId(String offerType, String offerCode, String groupId, String queryCha) throws Exception
    {
        IData input = new DataMap();

        input.put("OFFER_TYPE", offerType);
        input.put("OFFER_CODE", offerCode);
        input.put("GROUP_ID", groupId);
        if (StringUtils.isNotEmpty(queryCha))
        {
            input.put("QUERY_CHA", queryCha);
        }
        IData result = call("UPC.Out.GroupService.queryGroupByOfferIdGroupId", input);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }

    /**
     * @Description: 根据角色查询组下商品
     * @Param: [offerCode, offerType, relOfferType, roleCode, mgmtDistict]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/28 10:25
     */
    public static IDataset queryFamilyGroupOfferEnableWithRoleCode(String offerCode, String offerType, String relOfferType, String roleCode, String mgmtDistict) throws Exception
    {
        IData input = new DataMap();
        input.put("OFFER_CODE", offerCode);
        input.put("OFFER_TYPE", offerType);
        input.put("REL_OFFER_TYPE", relOfferType);
        input.put("ROLE_CODE", roleCode);
        input.put("MGMT_DISTICT", mgmtDistict);
        IData result = call("UPC.Out.OfferService.queryGroupOfferEnableWithRoleCode", input);
        IDataset dataset = result.getDataset("OUTDATA");
        return dataset;
    }

    /**
     * @Description: 查询家庭产品下够成属性
     * @Param: [offerType, offerCode, fieldName]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/28 10:29
     */
    public static IDataset queryFamilyOfferComChaByCond(String offerType, String offerCode, String fieldName) throws Exception
    {
        IData input = new DataMap();
        input.put("OFFER_TYPE", offerType);
        input.put("OFFER_CODE", offerCode);
        if (StringUtils.isNotEmpty(fieldName))
        {
            input.put("FIELD_NAME", fieldName);
        }
        IData result = call("UPC.Out.ComChaService.queryOfferComChaByCond", input);
        IDataset dataset = result.getDataset("OUTDATA");
        return dataset;
    }

	/**
	 * 订单归集接口,根据OFFER_CODE查询OfferCata
	 *
	 * @param offerCode
	 * @param offerType
	 * @return
	 * @throws Exception
	 */
	public static IDataset getOfferCataByProductId(String offerCode, String offerType,String upCatalogId,String catalogType) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("UP_CATALOG_ID", upCatalogId);
		input.put("CATALOG_TYPE", catalogType);

		IData data = call("UPC.Out.CatalogService.qryOfferCataByOFCond", input);
		IDataset dataset = data.getDataset("OUTDATA");
		return dataset;
	}

	public static IDataset qryOfferElesByProduct(String offerCode, String offerType, String mgmtDistrict) throws Exception {
		IData input = new DataMap();
		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("MGMT_DISTRICT", mgmtDistrict);
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferElesByOfferId", input);
		IDataset dataset = result.getDataset("OUTDATA");
		return dataset;
	}
	
	public static Object qryUpcDataInfos(String svcName,IData input) throws Exception {
		IData result = call(svcName, input);
		return result.get("OUTDATA");
	}

}