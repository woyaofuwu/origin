
package com.asiainfo.veris.crm.iorder.web.merch.component;

import com.ailk.biz.BizEnv;
import com.ailk.biz.cache.CrmCacheTablesCache;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.upc.UpcViewCallIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public class OfferHandler extends CSBizHttpHandler
{
    public void getTagList() throws Exception
    {
        IData data = this.getData();
        String type = data.getString("TYPE");
        String eparchyCode = data.getString("EPARCHY_CODE");
        String sn = data.getString("SERIAL_NUMBER");

        IDataset tags = new DatasetList();
        IData temp = new DataMap();
        IData param = new DataMap();

        if (FamilyConstants.FAMILY_PAGE_ID.equals(type))
        {
            // ---
            IDataset userOffers = null;
            IDataset orderOffers = null;
            if (StringUtils.isNotBlank(sn))
            {
                checkLimitTrade(data);
                IData userOffer = CSViewCall.callone(this, "SS.OfferSVC.getUserOffers", data);
                userOffers = userOffer.getDataset("USER_OFFERS");
                orderOffers = userOffer.getDataset("ORDER_OFFERS");
            }
            // ---
            tags = UpcViewCallIntf.queryTagViewByPage(this, type, eparchyCode, userOffers, orderOffers);
        }
        else
        {
            String userProduct = data.getString("PRODUCT_ID");

            if (StringUtils.isBlank(userProduct))
            {
                IData result = CSViewCall.callone(this, "SS.MerchChangeProductSVC.getUserProduct", data);
                data.put("PRODUCT_ID", result.getData("USER_PRODUCT").getString("PRODUCT_ID"));
            }
            param.put("USER_PRODUCT_ID", data.getString("PRODUCT_ID"));
            param.put("PRODUCT_TYPE_CODE", "0000");
            param.put("EPARCHY_CODE", eparchyCode);
            param.put("SERIAL_NUMBER", sn);

            if (StringUtils.equals("P1", type)) // 产品
            {
                tags = CSViewCall.call(this, "SS.OfferSVC.buildProductTagList", param);
            }
            else if (StringUtils.equals("SZ", type)) // 服务
            {
                String serviceName = data.getString("SERVICE_NAME");
                param.put("SERVICE_NAME", serviceName);

                param.put("TYPE", type);
                tags = CSViewCall.call(this, "SS.OfferSVC.buildSerciceAndDiscntTagList", param);
            }
            else if (StringUtils.equals("D1", type)) // 优惠
            {
                String serviceName = data.getString("SERVICE_NAME");
                param.put("SERVICE_NAME", serviceName);

                param.put("TYPE", type);
                tags = CSViewCall.call(this, "SS.OfferSVC.buildSerciceAndDiscntTagList", param);
            }
            else if (StringUtils.equals("PZ", type)) // 平台服务duhj
            {
                tags = CSViewCall.call(this, "SS.OfferSVC.buildPlatTagList", param);
            }
            else if (StringUtils.equals("HT", type))
            {
                tags = CSViewCall.call(this, "SS.OfferSVC.buildHotTagList", param);
            }
        }
        temp.put("TAG_LIST", tags);
        this.setAjax(temp);
    }

    /**
     * 分页2.0版,避免出现大批没权限导致空白
     * 
     * @throws Exception
     */
    public void refreshOffers() throws Exception
    {
        IDataset rst = new DatasetList();
        IData offerInfos = new DataMap();
        int countNum = 0;

        IData data = this.getData();
        IDataset tagList = new DatasetList(data.getString("TAG_LIST", "[]"));
        String type = data.getString("TYPE");
        String eparchyCode = data.getString("EPARCHY_CODE");
        String serialNumber = data.getString("SERIAL_NUMBER");

        // ---
        IDataset userOffers = null;
        IDataset orderOffers = null;
        IDataset commparaOffers = null;
        if (StringUtils.isNotBlank(serialNumber))
        {
            checkLimitTrade(data);
            IData userOffer = CSViewCall.callone(this, "SS.OfferSVC.getUserOffers", data);
            userOffers = userOffer.getDataset("USER_OFFERS");
            orderOffers = userOffer.getDataset("ORDER_OFFERS");
            commparaOffers = userOffer.getDataset("COMMPARA_OFFERS");
        }
        // ---
        Pagination page = getPagination(data);

        if (FamilyConstants.FAMILY_PAGE_ID.equals(type))
        {
            // 新增家庭处理
            IData offerRst = UpcViewCallIntf.queryOfferViewByPage(this, type, eparchyCode, tagList, userOffers, orderOffers, page);
            if (IDataUtil.isNotEmpty(offerRst))
            {
                IDataset offers = offerRst.getDataset("OFFERS");

                offers = disposeElements(data, offers, userOffers, orderOffers);

                rst = offers;
                countNum = offerRst.getInt("COUNT");
            }
        }
        else
        {
            String userProductId = data.getString("PRODUCT_ID");
            if (StringUtils.isBlank(userProductId))
            {
                IData result = CSViewCall.callone(this, "SS.MerchChangeProductSVC.getUserProduct", data);
                data.put("PRODUCT_ID", result.getData("USER_PRODUCT").getString("PRODUCT_ID"));
            }
            IData param = new DataMap();
            param.put("PRODUCT_TYPE_CODE", "0000");
            param.put("EPARCHY_CODE", eparchyCode);
            param.put("USER_PRODUCT_ID", data.getString("PRODUCT_ID"));
            param.put("USER_ID", data.getString("USER_ID"));
            param.putAll(data);

            String tagIds = this.getTagIds(tagList);
            StringBuilder cacheKey = null;
            if (StringUtils.equals("PZ", type))
            {
                // 平台业务加载商品跟号码主产品没关系，单独定义缓存，此举因为换号码拿不到总条数
                cacheKey = getPlatCacheKey(tagIds, type);
            }
            if (StringUtils.equals("P1", type))// 套餐
            {
                param.put("OFFER_TYPE", "P");
                rst = CSViewCall.call(this, "SS.OfferSVC.getOffersByOfferType", param);
            }
            else if (StringUtils.equals("SZ", type))// 功能（服务）
            {
                param.put("OFFER_TYPE", "S");
                rst = CSViewCall.call(this, "SS.OfferSVC.getOffersByOfferType", param);
            }
            else if (StringUtils.equals("D1", type)) // 资费（优惠）
            {
                param.put("OFFER_TYPE", "D");
                rst = CSViewCall.call(this, "SS.OfferSVC.getOffersByOfferType", param);
            }
            else if (StringUtils.equals("PZ", type)) // 平台服务 duhj
            {
                String tagValueId = IDataUtil.isNotEmpty(tagList) ? tagList.getData(0).getString("TAG_VALUE_ID") : "";
                if ("0".equals(tagValueId))
                {// 平台重点业务标签
                    IData map = new DataMap();
                    IDataset dataset = CSViewCall.call(this, "CS.PlatComponentSVC.getKeyBusiness", map);
                    rst = transPlatInfos(dataset, tagValueId);
                    countNum = rst.size();
                }
                else
                {
                    tagValueId = "-1".equals(tagValueId) ? "" : tagValueId;// 不限
                    IData offerRst = UpcViewCall.queryPlatSvcBySpcodeBizCode(this, null, null, tagValueId, page);
                    if (IDataUtil.isNotEmpty(offerRst))
                    {
                        IDataset platInfos = offerRst.getDataset("OFFERS");
                        rst = transPlatInfos(platInfos, tagValueId);
                        countNum = offerRst.getInt("COUNT");
                    }
                }
            }
            else if (StringUtils.equals("HT", type)) // 热门商品
            {
                IDataset returndData = CSViewCall.call(this, "SS.OfferSVC.getHotOffers", param);
                IData inputProduct = new DataMap();
                IData inputElement = new DataMap();
                IData inputPlatElement = new DataMap();
                IDataset rstProduct = new DatasetList();
                IDataset rstElement = new DatasetList();
                IDataset rstSaleActive = new DatasetList();

                IDataset rstPlatElement = new DatasetList();
                if (IDataUtil.isNotEmpty(returndData))
                {
                    for (int i = 0; i < returndData.size(); i++)
                    {
                        IData offer = returndData.getData(i);
                        String offerType = offer.getString("OFFER_TYPE");
                        if (StringUtils.equals("P", offerType))
                        {
                            rstProduct.add(offer);
                        }
                        else if (StringUtils.equals("Z", offerType))
                        {
                            rstPlatElement.add(offer);
                        }
                        else if (StringUtils.equals("K", offerType))
                        {
                            rstSaleActive.add(offer);
                        }
                        else
                        {
                            rstElement.add(offer);
                        }
                    }

                    inputProduct.putAll(data);
                    inputProduct.put("TYPE", "P1");
                    rstProduct = disposeElements(inputProduct, rstProduct, userOffers, orderOffers, commparaOffers);
                    rst.addAll(rstProduct);

                    inputElement.putAll(data);
                    inputElement.put("TYPE", "D1");
                    rstElement = disposeElements(inputElement, rstElement, userOffers, orderOffers, commparaOffers);
                    rst.addAll(rstElement);

                    inputPlatElement.putAll(data);
                    inputPlatElement.put("TYPE", "PZ");
                    rstPlatElement = disposePlatElements(inputPlatElement, rstPlatElement, userOffers, orderOffers, commparaOffers);
                    rst.addAll(rstPlatElement);

                    rst.addAll(rstSaleActive);
                    DataHelper.sort(rst, "PRIORITY_ID", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);
                }
            }
            // 产商品缓存，导致分页查询的时候，获取总条数有问题，这里自己缓存总条数
            if (StringUtils.equals("PZ", type))
            {
                Object obj = null;
                if (countNum <= 0)
                {
                    obj = SharedCache.get(cacheKey.toString());
                    if (obj != null)
                    {
                        countNum = (Integer) obj;
                    }
                    else
                    {
                        // 服务和优惠会从多个接口获取商品数据，目前产商品只改造了一个接口分页，其他接口未返回数据总条数 guohuan
                        countNum = rst.size();
                        SharedCache.set(cacheKey.toString(), countNum);
                    }
                }
                else
                {
                    obj = SharedCache.get(cacheKey.toString());
                    if (obj == null)
                    {
                        SharedCache.set(cacheKey.toString(), countNum);
                    }
                }
            }
            else
            {
                IData pageData = this.dealPagination(rst, page);
                rst = pageData.getDataset("OFFERS");
                countNum = pageData.getInt("TOTAL_COUNT");
            }

            if (IDataUtil.isNotEmpty(rst))
            {
                if (StringUtils.equals("PZ", type))
                {
                    rst = disposePlatElements(data, rst, userOffers, orderOffers, commparaOffers);
                }
                else if (StringUtils.equals("D1", type) || StringUtils.equals("SZ", type) || StringUtils.equals("P1", type))
                {
                    rst = disposeElements(data, rst, userOffers, orderOffers, commparaOffers);
                }
            }
        }
        offerInfos.put("OFFERS", rst);
        offerInfos.put("OFFERS_TOTAL_SIZE", countNum);
        this.setAjax(offerInfos);
    }

    public void search() throws Exception
    {
        IData param = this.getData();
        String type = param.getString("TYPE");
        String eparchyCode = param.getString("EPARCHY_CODE");
        String serialNumber = param.getString("SERIAL_NUMBER");
        IDataset offers = new DatasetList();

        // ---
        IDataset userOffers = null;
        IDataset orderOffers = null;
        IDataset commparaOffers = null;
        if (StringUtils.isNotBlank(serialNumber))
        {
            checkLimitTrade(param);
            IData userOffer = CSViewCall.callone(this, "SS.OfferSVC.getUserOffers", param);
            userOffers = userOffer.getDataset("USER_OFFERS");
            orderOffers = userOffer.getDataset("ORDER_OFFERS");
            commparaOffers = userOffer.getDataset("COMMPARA_OFFERS");
        }
        // ---

        if ("P1".equals(type))
        {
            // 产品搜索
            param.put("OFFER_TYPE", "P");
            offers = CSViewCall.call(this, "SS.OfferSVC.searchOffersByType", param);
        }
        else if ("SZ".equals(type))
        {
            // 功能搜索
            param.put("OFFER_TYPE", "S");
            offers = CSViewCall.call(this, "SS.OfferSVC.searchOffersByType", param);
        }
        else if ("D1".equals(type))
        {
            // 资费搜索
            param.put("OFFER_TYPE", "D");
            param.put("IS_CALC_DATE", "true");
            offers = CSViewCall.call(this, "SS.OfferSVC.searchOffersByType", param);
        }
        else if ("PZ".equals(type))
        {
            // 平台搜索
            param.put("OFFER_TYPE", "Z");
            param.put("IS_CALC_DATE", "true");
            offers = CSViewCall.call(this, "SS.OfferSVC.searchPlatOffersByType", param);

        }
        else if (FamilyConstants.FAMILY_PAGE_ID.equals(type))
        {
            String searchText = param.getString("SEARCH_TEXT");
            offers = UpcViewCallIntf.searchOfferViewByPage(this, type, eparchyCode, userOffers, orderOffers, searchText);
        }
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(offers))
        {
            if ("PZ".equals(type))
            {
                offers = disposePlatElements(param, offers, userOffers, orderOffers, commparaOffers);
            }
            else
            {
                offers = disposeElements(param, offers, userOffers, orderOffers, commparaOffers);
            }

            result.put("OFFERS", offers);
            result.put("OFFERS_TOTAL_SIZE", offers.size());
        }
        this.setAjax(result);
    }

    private IDataset disposeElements(IData data, IDataset offers, IDataset userOffers, IDataset orderOffers, IDataset commparaOffers) throws Exception
    {
        IDataset result = offers;

        String userOfferStr = this.listToKeyStr(userOffers);
        String orderOfferStr = this.listToKeyStr(orderOffers);

        // 增加可订购商品配置
        boolean isCommparaOffer = BizEnv.getEnvBoolean("MERCH_ORDER_COMMPARA", false);
        String commparaOfferStr = this.listToKeyStr(commparaOffers);

        for (int i = 0; i < result.size(); i++)
        {
            IData offer = result.getData(i);
            String offerCode = offer.getString("OFFER_CODE");
            String offerType = offer.getString("OFFER_TYPE");
            String orderMode = offer.getString("ORDER_MODE");

            String key = "'" + offerCode + "|" + offerType;
            if (isCommparaOffer && !commparaOfferStr.contains(key))
            {
                continue;
            }

            String tagValue = null;
            if (!"R".equals(orderMode))
            {
                if (orderOfferStr.contains(key))
                {
                    tagValue = "ORDER";// 购物车
                    offer.put("DISABLED", "true");
                    offer.put("TAG_VALUE", tagValue);
                }
                else if (userOfferStr.contains(key))
                {
                    tagValue = "USED";// 已订购
                    offer.put("DISABLED", "true");
                    offer.put("TAG_VALUE", tagValue);
                }
                else
                {
                    offer.put("TAG_VALUE", "NONE"); // 可订购，设置一个默认值，避免排序的时候空指针
                }
            }
            else
            {
                offer.put("TAG_VALUE", "NONE"); // 可订购，设置一个默认值，避免排序的时候空指针
            }
        }
        IData param = new DataMap();
        param.putAll(data);
        param.put("ROUTE_EPARCHY_CODE", data.getString("EPARCHY_CODE"));

        IData product = getProduct(userOffers);
        param.putAll(product);

        param.put("OFFERS", offers);
        param.put("USER_CITY_CODE", data.getString("USER_CITY_CODE"));

        IDataset newOffers = CSViewCall.call(this, "SS.OfferSVC.specialHandling", param);

        boolean iscalcDate = data.getBoolean("IS_CALC_DATE", false);
        if (IDataUtil.isNotEmpty(newOffers))
        {
            result = newOffers;
        }

        // 是否具备选择预约时间的权限
        String bookingDatePriv = "FALSE";
        if (StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "PROD_BOOKING_DATE"))
        {
            bookingDatePriv = "TRUE";
        }
        for (int j = 0; j < result.size(); j++)
        {
            IData offer = result.getData(j);
            offer.put("BOOKING_DATE_PRIV", bookingDatePriv);

            String tagValue = offer.getString("TAG_VALUE");
            if (StringUtils.isNotBlank(tagValue))
            {
                offer.put("STATE_NAME", StaticUtil.getStaticValue("UPC_TAG_VALUE_TRANSLATE", tagValue));
            }

            if ("USED".equals(tagValue) && iscalcDate)
            {
                setUsedOfferTime(offer, userOffers);
            }
            else if ("ORDER".equals(tagValue) && iscalcDate)
            {
                setUsedOfferTime(offer, orderOffers);
            }
        }
        DataHelper.sort(result, "TAG_VALUE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        return result;
    }

    private void setUsedOfferTime(IData offer, IDataset userOffers)
    {
        String offerCode = offer.getString("OFFER_CODE");
        String offerType = offer.getString("OFFER_TYPE");
        for (int i = 0; i < userOffers.size(); i++)
        {
            IData userOffer = userOffers.getData(i);
            if (offerCode.equals(userOffer.getString("OFFER_CODE")) && offerType.equals(userOffer.getString("OFFER_TYPE")))
            {
                offer.put("START_DATE", userOffer.getString("START_DATE"));
                offer.put("END_DATE", userOffer.getString("END_DATE"));
                break;
            }
        }
    }

    private IData getProduct(IDataset userOffers) throws Exception
    {
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(userOffers))
        {
            String sysdate = SysDateMgr.getSysTime();
            for (int i = 0; i < userOffers.size(); i++)
            {
                IData userOffer = userOffers.getData(i);
                if ("1".equals(userOffer.getString("MAIN_TAG")))
                {
                    String startDate = userOffer.getString("START_DATE");
                    int flag = startDate.compareTo(sysdate);
                    if (flag > 0)
                    {
                        result.put("NEXT_PRODUCT_ID", userOffer.getString("OFFER_CODE"));
                        result.put("NEXT_PRODUCT_START_DATE", startDate);
                    }
                    else
                    {
                        result.put("USER_PRODUCT_ID", userOffer.getString("OFFER_CODE"));
                    }
                }
            }
        }
        return result;
    }

    private Pagination getPagination(IData data)
    {
        int pagesize = data.getInt("PAGE_SIZE");
        int current = data.getInt("CURRENT");
        Pagination p = new Pagination(pagesize, current);
        return p;
    }

    private void checkLimitTrade(IData input) throws Exception
    {
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        if (StringUtils.isNotBlank(tradeTypeCode))
        {
            CSViewCall.callone(this, "SS.OfferSVC.checkLimitTrade", input);
        }
    }

    private String listToKeyStr(IDataset offers)
    {
        StringBuffer dataBuffer = new StringBuffer("");
        if (IDataUtil.isNotEmpty(offers))
        {
            for (Object o : offers)
            {
                IData offer = (IData) o;
                dataBuffer.append("'");
                dataBuffer.append(offer.getString("OFFER_CODE", offer.getString("PARAM_CODE", "NULL")));
                dataBuffer.append("|");
                dataBuffer.append(offer.getString("OFFER_TYPE", offer.getString("PARA_CODE1", "NULL")));
                dataBuffer.append(",");
            }
        }
        return dataBuffer.toString();
    }

    private IDataset disposePlatElements(IData data, IDataset offers, IDataset userOffers, IDataset orderOffers, IDataset commparaOffers) throws Exception
    {

        IDataset result = offers;
        String userOfferStr = this.listToKeyStr(userOffers);
        String orderOfferStr = this.listToKeyStr(orderOffers);
        // 增加可订购商品配置
        boolean isCommparaOffer = BizEnv.getEnvBoolean("MERCH_ORDER_COMMPARA", false);
        String commparaOfferStr = this.listToKeyStr(commparaOffers);
        for (int i = 0; i < result.size(); i++)
        {
            IData offer = result.getData(i);
            String offerCode = offer.getString("OFFER_CODE");
            String offerType = offer.getString("OFFER_TYPE");
            offer.put("START_DATE", SysDateMgr.getSysDate());
            offer.put("END_DATE", SysDateMgr.getTheLastTime());
            String tagValue = null;
            String key = "'" + offerCode + "|" + offerType;
            if (isCommparaOffer && !commparaOfferStr.contains(key))
            {
                continue;
            }
            if (orderOfferStr.contains(key))
            {
                tagValue = "ORDER";// 购物车
                offer.put("DISABLED", "true");
                offer.put("TAG_VALUE", tagValue);
                setUsedOfferTime(offer, orderOffers);
                offer.put("STATE_NAME", StaticUtil.getStaticValue("UPC_TAG_VALUE_TRANSLATE", tagValue));
                continue;
            }
            else if (userOfferStr.contains(key))
            {
                tagValue = "USED";// 已订购
                offer.put("DISABLED", "true");
                offer.put("TAG_VALUE", tagValue);
                setUsedOfferTime(offer, userOffers);
                offer.put("STATE_NAME", StaticUtil.getStaticValue("UPC_TAG_VALUE_TRANSLATE", tagValue));
                continue;
            }
            else
            {
                offer.put("TAG_VALUE", "NONE"); // 可订购，设置一个默认值，避免排序的时候空指针
            }

        }
        DataHelper.sort(result, "TAG_VALUE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        return result;
    }

    private IDataset transPlatInfos(IDataset platInfos, String tagValueId) throws Exception
    {
        IDataset rst = new DatasetList();
        if (IDataUtil.isNotEmpty(platInfos))
        {
            for (int i = 0; i < platInfos.size(); i++)
            {
                IData temp = platInfos.getData(i);
                IData platData = new DataMap();
                platData.put("OFFER_CODE", StringUtils.isNotBlank(temp.getString("SERVICE_ID", "")) ? temp.getString("SERVICE_ID") : temp.getString("OFFER_CODE"));
                platData.put("OFFER_NAME", StringUtils.isNotBlank(temp.getString("SERVICE_NAME", "")) ? temp.getString("SERVICE_NAME") : temp.getString("BIZ_NAME"));
                platData.put("OFFER_TYPE", "Z");
                platData.put("ORG_DOMAIN", temp.getString("ORG_DOMAIN"));
                String price = "0";
                if ("0".equals(tagValueId))
                {
                    price = Integer.parseInt(temp.getString("PRICE")) / 1000 + "元/月";
                }
                else
                {
                    price = temp.getString("PRICE") + "元/月";
                }
                platData.put("DESCRIPTION", temp.getString("SP_NAME") + "|" + price);
                rst.add(platData);
            }
        }
        return rst;
    }

    private String getTagIds(IDataset tagList) throws Exception
    {
        StringBuilder ids = new StringBuilder();
        if (IDataUtil.isNotEmpty(tagList))
        {
            for (int i = 0; i < tagList.size(); i++)
            {
                ids.append(tagList.getData(i).getString("TAG_VALUE_ID")).append("_");
            }
        }
        else
        {
            ids.append("-1_");
        }

        return ids.toString();
    }

    private StringBuilder getPlatCacheKey(String tagIds, String type) throws Exception
    {
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(CrmCacheTablesCache.class);
        String tabName = "";
        String versionOffer;

        tabName = "PM_OFFER";
        versionOffer = (String) cache.get(tabName);
        StringBuilder sb = new StringBuilder(1000);
        sb.append("OfferHandler.refreshOffers_COUNTNUM_").append(SysDateMgr.getSysDate("dd")).append("_").append(versionOffer).append("_").append(type).append("_").append(tagIds);

        return sb;
    }

    /**
     * 处理分页
     * 
     * @param sourceData
     * @param page
     * @return
     * @throws Exception
     */
    private IData dealPagination(IDataset sourceData, Pagination page) throws Exception
    {
        IData pageData = new DataMap();
        if (page == null || page.getPageSize() <= 0 || IDataUtil.isEmpty(sourceData))
        {
            pageData.put("TOTAL_COUNT", 0);
        }
        else
        {
            int totalDataCount = sourceData.size();
            // 每页多少条
            int pageSize = page.getPageSize();
            // 总共多少页
            int pageMod = totalDataCount % pageSize;// 取余
            int pageCount = pageMod == 0 ? (totalDataCount / pageSize) : (totalDataCount / pageSize + 1);
            // 当前页有多少条数据
            int currentSize = 0;
            if (page.getCurrent() == pageCount)
            {
                if (pageMod == 0)
                {
                    currentSize = pageSize;
                }
                else
                {
                    currentSize = pageMod;
                }
            }
            else
            {
                currentSize = pageSize;
            }

            IDataset offers = new DatasetList();
            int startIndex = page.getStart();
            int endIndex = page.getStart() + currentSize;
            for (int i = startIndex; i < endIndex; i++)
            {
                IData data = sourceData.getData(i);
                String offerDesc = data.getString("DESCRIPTION");
                if (StringUtils.isBlank(offerDesc))
                {
                    data.put("DESCRIPTION", UpcViewCall.queryOfferByOfferId(this, data.getString("OFFER_TYPE"), data.getString("OFFER_CODE")).getString("DESCRIPTION"));
                }
                offers.add(data);
            }

            pageData.put("OFFERS", offers);
            pageData.put("TOTAL_COUNT", totalDataCount);
        }
        return pageData;
    }

    private IDataset disposeElements(IData data, IDataset offers, IDataset userOffers, IDataset orderOffers) throws Exception
    {
        IDataset result = offers;

        IData param = new DataMap();
        param.putAll(data);
        param.put("ROUTE_EPARCHY_CODE", data.getString("EPARCHY_CODE"));

        IData product = getProduct(userOffers);
        param.putAll(product);

        param.put("OFFERS", offers);
        param.put("USER_CITY_CODE", data.getString("USER_CITY_CODE"));

        IDataset newOffers = CSViewCall.call(this, "SS.OfferSVC.specialHandling", param);

        boolean iscalcDate = data.getBoolean("IS_CALC_DATE", false);
        if (IDataUtil.isNotEmpty(newOffers))
        {
            result = newOffers;
        }
        for (int i = 0; i < result.size(); i++)
        {
            IData offer = result.getData(i);
            String tagValue = offer.getString("TAG_VALUE");
            if (StringUtils.isNotBlank(tagValue))
            {
                offer.put("STATE_NAME", StaticUtil.getStaticValue("UPC_TAG_VALUE_TRANSLATE", tagValue));
            }
            if (tagValue == "USED" && iscalcDate)
            {
                setUsedOfferTime(offer, userOffers);
            }
            else if (tagValue == "ORDER" && iscalcDate)
            {
                setUsedOfferTime(offer, orderOffers);
            }

        }
        return result;
    }
}
