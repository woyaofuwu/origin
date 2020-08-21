package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.upc;

import org.apache.log4j.Logger;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public final class UpcViewCallIntf {
    public static Logger logger = Logger.getLogger(UpcViewCallIntf.class);

    /**
     * *根据主商品查询标签信息
     * 
     * @param groupId
     *            必填
     * @param groupType
     *            必填
     * @throws Exception
     */

    public static IDataset qryAllTagAndTagValueByOfferId(IBizCommon bc, String offerCode, String offerType) throws Exception {
        IData input = new DataMap();

        input.put("OFFER_CODE", offerCode);
        input.put("OFFER_TYPE", offerType);

        IData result = call(bc, "UPC.Out.TagQueryFSV.qryAllTagAndTagValueByOfferId", input);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }

    /**
     * *根据产品，标签，标签值，品类查询下面的产品
     * 
     * @param groupId
     *            必填
     * @param groupType
     *            必填
     * @throws Exception
     */

    public static IDataset qryOfferByTagInfo(IBizCommon bc, String offerCode, String offerType, String labelId, String labelKeyId, String categoryId) throws Exception {
        IData input = new DataMap();

        input.put("OFFER_CODE", offerCode);
        input.put("OFFER_TYPE", offerType);
        input.put("LABEL_ID", labelId);
        input.put("LABEL_KEY_ID", labelKeyId);
        input.put("CATEGORY_ID", categoryId);
        IData result = call(bc, "UPC.Out.OfferQueryFSV.qryOfferByTagInfo", input);
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

    public static IDataset queryGroupByCond(IBizCommon bc, String groupId, String groupType) throws Exception {
        IData input = new DataMap();

        input.put("GROUP_ID", groupId);
        if (StringUtils.isNotEmpty(groupType)) {
            input.put("GROUP_TYPE", groupType);
        }
        IData result = call(bc, "UPC.Out.GroupQueryFSV.queryGroupByCond", input);
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

    public static IDataset queryOfferByOfferId(IBizCommon bc, String offerType, String offerCode, String queryComCha) throws Exception {
        IData input = new DataMap();

        input.put("OFFER_TYPE", offerType);
        input.put("OFFER_CODE", offerCode);
        if (StringUtils.isNotEmpty(queryComCha)) {
            input.put("QUERY_COM_CHA", queryComCha);
        }
        IData result = call(bc, "UPC.Out.OfferQueryFSV.queryOfferByOfferId", input);
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
     * @param FUNC_STATUS
     *            必填
     * @throws Exception
     */

    public static IDataset queryProdStaByCond(IBizCommon bc, String offerType, String offerCode, String prodStatus) throws Exception {
        IData input = new DataMap();

        input.put("OFFER_TYPE", offerType);
        input.put("OFFER_CODE", offerCode);
        input.put("FUNC_STATUS", prodStatus);
        IData result = call(bc, "UPC.Out.OfferQueryFSV.qryOfferFuncStaByAnyOfferIdStatus", input);
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
    public static String queryBrandNameByChaVal(IBizCommon bc, String brandCode) throws Exception {
        IData input = new DataMap();

        input.put("BRAND_CODE", brandCode);
        IData result = call(bc, "UPC.Out.ChaQueryFSV.queryBrandNameByChaVal", input);
        return result.getString("BRAND_NAME");

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

    public static IDataset queryOfferJoinRelAndOfferByOfferId(IBizCommon bc, String offerType, String offerCode, String relType, String selectFlag, String queryCha) throws Exception {
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
        IData result = call(bc, "UPC.Out.OfferQueryFSV.queryOfferJoinRelAndOfferByOfferId", input);
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

    public static IDataset querySpServiceBySpCodeAndBizCodeAndBizStateCode(IBizCommon bc, String SpCode, String BizCode) throws Exception {
        IData input = new DataMap();

        input.put("SP_CODE", SpCode);
        input.put("BIZ_CODE", BizCode);
        input.put("BIZ_STATE_CODE", "A");

        IData result = call(bc, "UPC.Out.SpQueryFSV.querySpServiceBySpCodeAndBizCodeAndBizStateCode", input);
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

    public static IDataset querySpServiceBySpCodeAndBizCodeAndBizStateCodeRsrvStr1(IBizCommon bc, String SpCode, String BizCode) throws Exception {
        IData input = new DataMap();

        input.put("SP_CODE", SpCode);
        input.put("BIZ_CODE", BizCode);
        input.put("BIZ_STATE_CODE", "A");

        IData result = call(bc, "UPC.Out.SpQueryFSV.querySpServiceBySpCodeAndBizCodeAndBizStateCodeRsrvStr1", input);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }

    public static IDataset queryOffersByMultiCategory(IBizCommon bc, String productId, String mgmtDistrict, String categoryId, String relType) throws Exception {
        IData data = new DataMap();
        data.put("OFFER_CODE", productId);
        data.put("OFFER_TYPE", "P");
        data.put("MGMT_DISTRICT", mgmtDistrict);
        data.put("CATEGORY_ID", categoryId);
        data.put("REL_TYPE", relType);
        IData result = call(bc, "UPC.Out.OfferQueryFSV.queryOfferByMutiCateId", data);
        IDataset dataset = result.getDataset("OUTDATA");
        return dataset;
    }

    public static IDataset queryOfferGroups(IBizCommon bc, String productId) throws Exception {
        IData data = new DataMap();
        data.put("OFFER_CODE", productId);
        data.put("OFFER_TYPE", "P");
        IData result = call(bc, "UPC.Out.OfferQueryFSV.queryOfferGroupRelOfferId", data);
        IDataset dataset = result.getDataset("OUTDATA");

        if (IDataUtil.isNotEmpty(dataset)) {
            for (int i = 0, isize = dataset.size(); i < isize; i++) {
                IData packageInfo = dataset.getData(i);

                packageInfo.put("PRODUCT_ID", productId);// 12位的需要转换成短的
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

    public static IDataset queryGroupComRelOffer(IBizCommon bc, String groupId) throws Exception {
        IData data = new DataMap();
        data.put("GROUP_ID", groupId);
        IData result = call(bc, "UPC.Out.OfferQueryFSV.queryGroupComRelOfferByGroupId", data);
        IDataset dataset = result.getDataset("OUTDATA");
        return dataset;
    }

    private final static IData call(IBizCommon bc, String svcName, IData input) throws Exception {
        return call(bc, svcName, input, null, true);
    }

    @SuppressWarnings("unused")
	private final static IData call(IBizCommon bc, String svcName, IData input, boolean iscatch) throws Exception {
        return call(bc, svcName, input, null, true);
    }

	private final static IData call(IBizCommon bc, String svcName, IData input, Pagination pagin, boolean iscatch)
			throws Exception {
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

    public static IDataset queryOfferEnableModeByCond(IBizCommon bc, String offerType, String offerCode, String relOfferType, String relOfferCode, String relType) throws Exception {
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
        IData result = call(bc, "UPC.Out.OfferQueryFSV.queryOfferEnableModeByCond", input);
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
    public static IDataset qryOfferFromGroupByGroupIdOfferId(IBizCommon bc, String groupId, String offerType, String offerCode) throws Exception {
        IData input = new DataMap();

        input.put("GROUP_ID", groupId);
        input.put("OFFER_TYPE", offerType);
        input.put("OFFER_CODE", offerCode);
        IData result = call(bc, "UPC.Out.OfferQueryFSV.qryOfferFromGroupByGroupIdOfferId", input);
        IData data = result.getData("OUTDATA");

        IDataset results = new DatasetList();
        if (IDataUtil.isNotEmpty(data)) {
            results.add(data);
        }

        return results;
    }

    /**
     * 查询PM_PO表
     * 
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-22
     */
    public static IDataset queryPoByValid(IBizCommon bc) throws Exception {
        IData input = new DataMap();
        IData result = call(bc, "UPC.Out.BbossQueryFSV.queryPoByValid", input);
        IDataset dataset = result.getDataset("OUTDATA");
        return dataset;
    }

    /**
     * BBOSS相关接口 根据POSPECNUMBER 查询PM_POPRODUCT表
     * 
     * @param pospecnumber
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-22
     */
    public static IDataset queryPoproductByPospecNumber(IBizCommon bc, String pospecnumber) throws Exception {
        IData input = new DataMap();
        input.put("POSPECNUMBER", pospecnumber);
        IData result = call(bc, "UPC.Out.BbossQueryFSV.queryPoproductByPospecNumber", input);
        IDataset dataset = result.getDataset("OUTDATA");
        return dataset;
    }

    public static IDataset queryPoproductByProductSpecNumber(IBizCommon bc, String productspecnumber, String pospecnumber) throws Exception {
        IData input = new DataMap();

        input.put("PRODUCTSPECNUMBER", productspecnumber);
        input.put("POSPECNUMBER", pospecnumber);
        IData result = call(bc, "UPC.Out.BbossQueryFSV.queryPoproductByProductSpecNumber", input);
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

    public static IDataset queryPoByPospecNumber(IBizCommon bc, String pospecnumber) throws Exception {
        IData input = new DataMap();

        input.put("POSPECNUMBER", pospecnumber);
        IData result = call(bc, "UPC.Out.BbossQueryFSV.queryPoByPospecNumber", input);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }

    /**
     * @Description 根据offer_code查询(td_s_servicestate)表
     * @param offerCode
     * @param offerType
     * @param funcStatus
     * @return
     * @throws Exception
     */
    public static String qryOfferFuncStaByAnyOfferIdStatus(IBizCommon bc, String offerCode, String offerType, String funcStatus) throws Exception {
        //
        IData input = new DataMap();
        input.put("OFFER_TYPE", offerType);
        input.put("OFFER_CODE", offerCode);
        input.put("FUNC_STATUS", funcStatus);

        IData result = call(bc, "UPC.Out.OfferQueryFSV.qryOfferFuncStaByAnyOfferIdStatus", input);

        IDataset dataset = result.getDataset("OUTDATA");

        return IDataUtil.isEmpty(dataset) ? "" : dataset.getData(0).getString("STATUS_NAME");
    }

    /**
     * duhj
     * 
     * @param spCode
     * @param bizCode
     * @return
     * @throws Exception
     */
    public static IData queryPlatSvcBySpcodeBizCode(IBizCommon bc, String spCode, String bizCode, String bizTypeCode, Pagination p) throws Exception {
        IData input = new DataMap();
        input.put("SP_CODE", spCode);
        input.put("BIZ_CODE", bizCode);
        input.put("BIZ_TYPE_CODE", bizTypeCode);
        // IData result = call(bc, "UPC.Out.SpQueryFSV.qrySpInfo", input);
        // IData result = call(bc, "UPC.Out.SpQueryFSV.querySpServiceAndInfoByCond", input);
        IData data = call(bc, "UPC.Out.SpQueryFSV.querySpInfoServiceAndProdBySpBizInfo", input, p, true);

        IData result = new DataMap();
        IDataset dataset = data.getDataset("OUTDATA");
        if (IDataUtil.isNotEmpty(dataset)) {
            result.put("OFFERS", dataset);
            result.put("COUNT", data.getInt("X_COUNTNUM", 0));
        }
        return result;
    }

    public static IDataset queryOfferByOfferCodeAndType(IBizCommon bc, String offerCode, String offerType) throws Exception {
        IData data = new DataMap();
        data.put("OFFER_CODE", offerCode);
        data.put("OFFER_TYPE", offerType);
        IData result = call(bc, "UPC.Out.OfferQueryFSV.queryOfferNameByOfferCodeAndType", data);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }

    public static String queryBrandCodeByOfferCodeAndType(IBizCommon bc, String offerCode, String offerType) throws Exception {
        IDataset results = queryOfferComChaByCond(bc, offerType, offerCode, "BRAND_CODE");
        if (IDataUtil.isEmpty(results)) {
            return "";
        }

        return results.first().getString("FIELD_VALUE");
    }

    public static IDataset queryOfferComChaByCond(IBizCommon bc, String offerType, String offerCode, String fieldName) throws Exception {
        IData input = new DataMap();

        input.put("OFFER_TYPE", offerType);
        input.put("OFFER_CODE", offerCode);
        if (StringUtils.isNotEmpty(fieldName)) {
            input.put("FIELD_NAME", fieldName);
        }
        IData result = call(bc, "UPC.Out.ChaQueryFSV.queryOfferComChaByCond", input);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }

    /**
     * 查询产品是否为定制产品
     */
    public static String getUseTagByProductId(IBizCommon bc, String offerCode) throws Exception {
        IDataset results = queryOfferComChaByCond(bc, "P", offerCode, "USE_TAG");
        if (IDataUtil.isEmpty(results)) {
            return "";
        }

        return results.first().getString("FIELD_VALUE");
    }

    // -------------------------------------------------------------------家庭新增方法-------------------------------------------------------------
    /**
     * @Description: 家庭业务
     * @Param: [bc, pageId, eparchyCode, userOffers, orderOffers]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/23 17:42
     */
    public static IDataset queryTagViewByPage(IBizCommon bc, String pageId, String eparchyCode, IDataset userOffers, IDataset orderOffers) throws Exception
    {
        IData input = new DataMap();
        input.put("PAGE_ID", pageId);
        input.put("MGMT_DISTRICT", eparchyCode);
        input.put("USER_OFFERS", userOffers);
        input.put("ORDER_OFFERS", orderOffers);
        IData result = call(bc, "UPC.Out.TagService.queryTagViewByPage", input);
        IDataset dataset = result.getDataset("OUTDATA");
        return dataset;
    }

    /**
     * @Description: 家庭业务
     * @Param: [bc, pageId, eparchyCode, taglist, userOffers, orderOffers, pagin]
     * @return: com.ailk.common.data.IData
     * @Author: zhenggang
     * @Date: 2020/7/23 18:05
     */
    public static IData queryOfferViewByPage(IBizCommon bc, String pageId, String eparchyCode, IDataset taglist, IDataset userOffers, IDataset orderOffers, Pagination pagin) throws Exception
    {
        IData input = new DataMap();
        input.put("PAGE_ID", pageId);
        input.put("MGMT_DISTRICT", eparchyCode);
        input.put("USER_OFFERS", userOffers);
        input.put("ORDER_OFFERS", orderOffers);
        input.put("TAG_LIST", taglist);
        IData data = call(bc, "UPC.Out.TagService.queryOfferViewByPage", input, pagin, false);
        IData result = new DataMap();
        IData pagination = data.getData("PAGINATION");
        if (IDataUtil.isNotEmpty(pagination))
        {
            IDataset dataset = data.getDataset("OUTDATA");
            result.put("OFFERS", dataset);
            result.put("COUNT", pagination.getInt("COUNT", 0));
        }
        return result;
    }

    /**
     * @Description: 家庭业务
     * @Param: [bc, pageId, eparchyCode, userOffers, orderOffers, searchText]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/23 18:11
     */
    public static IDataset searchOfferViewByPage(IBizCommon bc, String pageId, String eparchyCode, IDataset userOffers, IDataset orderOffers, String searchText) throws Exception
    {
        IData input = new DataMap();
        input.put("PAGE_ID", pageId);
        input.put("MGMT_DISTRICT", eparchyCode);
        input.put("USER_OFFERS", userOffers);
        input.put("ORDER_OFFERS", orderOffers);
        input.put("KEY_LIKE", searchText);
        IData data = call(bc, "UPC.Out.TagService.searchOfferViewByPage", input);
        IDataset dataset = data.getDataset("OUTDATA");
        return dataset;
    }
}
