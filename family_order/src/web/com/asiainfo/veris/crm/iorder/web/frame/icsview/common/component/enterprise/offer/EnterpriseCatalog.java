package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.offer;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.svcutil.datainfo.uca.IUCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class EnterpriseCatalog extends BizTempComponent
{

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = StringUtils.isNotEmpty(getJsFile()) ? getJsFile() : "scripts/iorder/icsserv/component/enterprise/offer/EnterpriseCatalog.js";

        if (isAjax)
        {
            includeScript(writer, jsFile, false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }

        IData info = new DataMap();

        info.put("id", StringUtils.isBlank(getId()) ? "offerCategoryPopupItem" : getId());
        info.put("IS_EC", isEcCatalog());
        info.put("MGMT_DISTRICT", getVisit().getLoginEparchyCode());

        String upCategoryId = getUpCategoryId();

        boolean isBatch = getIsBatch(); // 是否批量操作
        info.put("IS_BATCH", isBatch);

        String batchTypeCode = "";
        if (isBatch)
        {
            buttonChoose(info, getButtonChoose());

            batchTypeCode = getBatchTypeCode();
            info.put("BATCH_TYPE_CODE", batchTypeCode);
        }
       
        queryOfferCategoryByUpCategoryId(upCategoryId, info);

        String categoryId = getPage().getParameter("CATALOG_ID");
        String productTree = getPage().getParameter("PRODUCTTREE_LIMIT_PRODUCTS");//渠道vpmn成员受理
        if (StringUtils.isNotBlank(categoryId))
        {
            if (EcConstants.EC_OPENED_OFFER_CATALOG_ID.equals(categoryId))
            {// 集团已订购销售品
                info.put("CATALOG_ID", categoryId);
                String custId = getPage().getParameter("CUST_ID");
                String subscriberInsId = getPage().getParameter("SEL_USER_ID");
                String offerId = getPage().getParameter("SEL_OFFER_ID");
                queryEcOpenedOfferByCustId(custId, subscriberInsId, offerId);
            }
            else if (EcConstants.EC_MEB_EC_OPENED_OFFER_CATALOG_ID.equals(categoryId))
            {// 成员目录-集团已订购商品
                info.put("CATALOG_ID", categoryId);
                String subInsId = getPage().getParameter("USER_ID");
               
                String custId = getPage().getParameter("CUST_ID");
                String selEcSubInsId = getPage().getParameter("SEL_USER_ID");
                String selOfferId = getPage().getParameter("SEL_OFFER_ID");
                //判断是否是渠道VPMN成员受理
                if(StringUtils.isNotBlank(productTree)){
                    queryEcOpenedOfferDelMebOpenByCustIdAndSubscriberInsIdVpmn(custId, subInsId, selEcSubInsId, selOfferId,productTree);
                }else{
                    queryEcOpenedOfferDelMebOpenByCustIdAndSubscriberInsId(custId, subInsId, selEcSubInsId, selOfferId);
                }
            }
            else if (EcConstants.EC_MEB_MEB_OPENED_OFFER_CATALOG_ID.equals(categoryId))
            {// 成员目录-成员已订购商品
                info.put("CATALOG_ID", categoryId);
                String subInsId = getPage().getParameter("USER_ID");
                if(StringUtils.isNotBlank(productTree)){
                    queryMebOpenedOfferByMebSubscriberInsIdVpmn(subInsId,productTree);
                }else{
                    queryMebOpenedOfferByMebSubscriberInsId(subInsId);
                }
            }
            else
            {
                queryOffersByCategoryId(categoryId);
            }
        }

        setInfo(info);
    }

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        setInfo(null);
        setOffers(null);
    }

    /**
     * 根据目录规格标识查询集团销售品目录
     * 
     * @param catalogSpecId
     * @throws Exception
     */
    private void queryOfferCategoryByUpCategoryId(String upCategoryId, IData info) throws Exception
    {
        IDataset catalogs = new DatasetList();
        if (StringUtils.isNotBlank(upCategoryId))
        {
            catalogs = IUpcViewCall.queryCatalogsByUpCatalogId(upCategoryId);

        }
        else
        {
            boolean isShowEcCatalog = getIsOnlyShowEcCatalog();
            if (isShowEcCatalog)
            {
                IData catalog1 = new DataMap();
                catalog1.put("CATALOG_ID", EcConstants.EC_MEB_BATCH_OFFER_CATALOG_ID);
                catalog1.put("CATALOG_NAME", "已订购商品");
                catalogs.add(catalog1);
            }
            else
            {
                IData catalog1 = new DataMap();
                catalog1.put("CATALOG_ID", EcConstants.EC_MEB_EC_OPENED_OFFER_CATALOG_ID);
                catalog1.put("CATALOG_NAME", "可订购商品");
                catalogs.add(catalog1);
                IData catalog2 = new DataMap();
                catalog2.put("CATALOG_ID", EcConstants.EC_MEB_MEB_OPENED_OFFER_CATALOG_ID);
                catalog2.put("CATALOG_NAME", "已订购商品");
                catalogs.add(catalog2);

            }

        }

        setCatalogs(catalogs);
    }


    /**
     * 根据目录编码查询集团销售品
     * 
     * @param catalogId
     *            目录编码
     * @throws Exception
     */
    private void queryOffersByCategoryId(String categoryId) throws Exception
    {
        IDataset offers = IUpcViewCall.queryOffersByCatalogId(categoryId, this.getVisit().getLoginEparchyCode());
        
        //根据登录工号权限筛选可以办理的产品
        if (IDataUtil.isNotEmpty(offers))
        {
            for (int i = 0, size = offers.size(); i < size; i++)
            {
                IData offer = offers.getData(i);
                offer.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
                offer.put("PRODUCT_NAME", offer.getString("OFFER_NAME"));
            }
        }
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), offers);

        setOffers(offers);
        
        getPage().setAjax(offers);
    }

    /**
     * 查询客户已订购集团销售品
     * 
     * @param custId
     * @throws Exception
     */
    private void queryEcOpenedOfferByCustId(String custId, String selSubInsId, String selOfferId) throws Exception
    {
        IDataset offers = new DatasetList();

        IDataset insOffers = new DatasetList();
        if (StringUtils.isNotEmpty(selSubInsId) && StringUtils.isNotEmpty(selOfferId))
        {// 当集团认证外框选择了商品时，集团已订购商品列表只展示外框选择的商品(防止集团订购的商品数量过多导致查询超时)
        	
        	IData seloffer = IUpcViewCall.queryOfferByOfferId(selOfferId);
        	IData data = new DataMap();
        	data.put("USER_ID", selSubInsId);
        	data.put("PRODUCT_ID", seloffer.getString("OFFER_CODE"));
        	data.put(Route.ROUTE_EPARCHY_CODE, "cg");
        	IDataset insOfferDataset = CSViewCall.call(this, "CS.UserProductInfoQrySVC.getUserProductByUserIdProductId", data);
        	
            if (DataUtils.isNotEmpty(insOfferDataset))
            {
            	IData offerInfo = insOfferDataset.first();
                IData subInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, selSubInsId);
                offerInfo.put("SERIAL_NUMBER", subInfo.get("SERIAL_NUMBER"));
                offerInfo.put("OFFER_ID", selOfferId);
                offerInfo.put("OFFER_CODE", subInfo.get("PRODUCT_ID"));
                offerInfo.put("OFFER_TYPE", "P");
                insOffers.add(offerInfo);
            }
        }
        else
        {
            insOffers = IUCAInfoIntfViewUtil.qryUserAndProductByCustIdForGrp(this, custId);

        }

        if (DataUtils.isNotEmpty(insOffers))
        {
            String groupId = "";
            String groupName = "";
            IData groupData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
            if (DataUtils.isNotEmpty(groupData))
            {
                groupId = groupData.getString("GROUP_ID");
                groupName = groupData.getString("CUST_NAME");
            }
            for (int j = 0, size = insOffers.size(); j < size; j++)
            {
                IData insoffer = insOffers.getData(j);
                String productId = insoffer.getString("PRODUCT_ID");
                String accessNum = insoffer.getString("SERIAL_NUMBER");
                //不从产商品库捞取专线成员、集团商务宽带成员、7010（VOIP专线）的
                if(accessNum.startsWith("KD_") || accessNum.startsWith("ZX") || accessNum.startsWith("KV_")||"7010".equals(productId))
                {
                	continue;
                }
                
                IData PMoffer = UpcViewCall.queryOfferByOfferId(this, "P", productId,"");
                if(DataUtils.isEmpty(PMoffer)){
                	continue;
                }
                
                IData param = new DataMap();
                param.put("PRODUCT_ID", productId);
                //如果树上没有，直接remove
            	IDataset productTypeDataset = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getProductTypesByProductId", param);

                if (DataUtils.isEmpty(productTypeDataset))
                {
                    continue;
                }
                // 产品过滤展示结束 fanjx
                // 已订购产品操作类型配置开始 fanjx
                // 如果cfg_prod_attr_item配置了产品对应的CHANAGE_HIDE为TRUE,则界面不展示当前产品的变更button
                // 如果cfg_prod_attr_item配置了产品对应的DELETE_HIDE为TRUE,则界面不展示当前产品的注销button
                //此处以后可以加权限控制操作 liuzz
                /* String offerId = offer.getString("OFFER_ID", "");
                IData chgCfgItems = EcParamViewUtil.queryCfgProdAttrItemByIdIdTypeObjCode(offerId, "P", "0", "CHANAGE_HIDE");
                IData delCfgItems = EcParamViewUtil.queryCfgProdAttrItemByIdIdTypeObjCode(offerId, "P", "0", "DELETE_HIDE");
                offer.put("CHANGE_HIDE", chgCfgItems.getString("VALUE", "FALSE"));
                offer.put("DELETE_HIDE", delCfgItems.getString("VALUE", "FALSE"));*/
                
                String subscriberInsId = insoffer.getString("USER_ID");
                IData offer = new DataMap();
                offer.put("USER_ID", subscriberInsId);
                offer.put("SERIAL_NUMBER", accessNum);
                offer.put("OFFER_CODE", productId);
                offer.put("OFFER_ID", PMoffer.getString("OFFER_ID", ""));
                offer.put("OFFER_NAME", PMoffer.getString("OFFER_NAME", ""));
                offer.put("BRAND_CODE", insoffer.getString("BRAND_CODE", ""));
                offer.put("GROUP_ID", groupId);
                offer.put("GROUP_NAME", groupName);
                offer.put("CATALOG_DATA", buildCatalogData(offer));
                
                offers.add(offer);
            }
        }
        
      //根据登录工号权限筛选可以办理的产品
        if (IDataUtil.isNotEmpty(offers))
        {
            for (int i = 0, size = offers.size(); i < size; i++)
            {
                IData offer = offers.getData(i);
                offer.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
                offer.put("PRODUCT_NAME", offer.getString("OFFER_NAME"));
            }
        }
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), offers);
        
        setOffers(offers);
        
        getPage().setAjax(offers);
        
    }

    /**
     * 根据集团客户标识查询集团订购销售品去除当前用户已订购
     * 
     * @param custId
     * @param mebSubscriberInsId
     * @throws Exception
     */
    private void queryEcOpenedOfferDelMebOpenByCustIdAndSubscriberInsId(String custId, String mebSubscriberInsId, String selEcSubInsId, String selOfferId) throws Exception
    {
        IDataset offers = new DatasetList();

        IDataset insOffers = new DatasetList();
        
        if (StringUtils.isNotEmpty(selEcSubInsId) && StringUtils.isNotEmpty(selOfferId))
        {// 当集团认证外框选择了商品时，集团已订购商品列表只展示外框选择的商品(防止集团订购的商品数量过多导致查询超时)
        	
        	IData seloffer = IUpcViewCall.queryOfferByOfferId(selOfferId);
        	IData data = new DataMap();
        	data.put("USER_ID", selEcSubInsId);
        	data.put("PRODUCT_ID", seloffer.getString("OFFER_CODE"));
        	data.put(Route.ROUTE_EPARCHY_CODE, "cg");
        	IDataset insOfferDataset = CSViewCall.call(this, "CS.UserProductInfoQrySVC.getUserProductByUserIdProductId", data);
        	
            if (DataUtils.isNotEmpty(insOfferDataset))
            {
            	IData offerInfo = insOfferDataset.first();
                IData subInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, selEcSubInsId);
                offerInfo.put("SERIAL_NUMBER", subInfo.get("SERIAL_NUMBER"));
                offerInfo.put("OFFER_ID", selOfferId);
                offerInfo.put("OFFER_CODE", subInfo.get("PRODUCT_ID"));
                offerInfo.put("OFFER_TYPE", "P");
                insOffers.add(offerInfo);
            }
        }
        else
        {
            insOffers = IUCAInfoIntfViewUtil.qryUserAndProductByCustIdForGrp(this, custId);

        }
        if (DataUtils.isNotEmpty(insOffers))
        {
            String groupId = "";
            String groupName = "";
            IData groupData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
            if (DataUtils.isNotEmpty(groupData))
            {
                groupId = groupData.getString("GROUP_ID");
                groupName = groupData.getString("CUST_NAME");
            }
            for (int j = 0, size = insOffers.size(); j < size; j++)
            {
                IData insoffer = insOffers.getData(j);
                String productId = insoffer.getString("PRODUCT_ID");
                IData PMoffer = UpcViewCall.queryOfferByOfferId(this, "P", productId,"");
                if(DataUtils.isEmpty(PMoffer)){
                	continue;
                }
                String accessNum = insoffer.getString("SERIAL_NUMBER");
                String subscriberInsId = insoffer.getString("USER_ID");
                IData offer = new DataMap();
                offer.put("USER_ID", subscriberInsId);
                offer.put("SERIAL_NUMBER", accessNum);
                offer.put("OFFER_CODE", productId);
                offer.put("OFFER_ID", PMoffer.getString("OFFER_ID", ""));
                offer.put("OFFER_NAME", PMoffer.getString("OFFER_NAME", ""));
                offer.put("BRAND_CODE", insoffer.getString("BRAND_CODE", ""));
                offer.put("GROUP_ID", groupId);
                offer.put("GROUP_NAME", groupName);
                offer.put("CATALOG_DATA", buildCatalogData(offer));
                offers.add(offer);
            }
        }
        if (DataUtils.isNotEmpty(offers))
        {
            // 查询成员UU关系
        	IData data = new DataMap();
        	data.put("USER_ID", mebSubscriberInsId);
        	data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());
        	IDataset uuInfos = CSViewCall.call(this, "CS.GrpInfoQrySVC.getGroupInfo", data);

            if (DataUtils.isNotEmpty(uuInfos))
            {
                for (int i = 0, size = uuInfos.size(); i < size; i++)
                {
                    IData uuInfo = uuInfos.getData(i);
                    for (int j = offers.size(); j > 0; j--)
                    {
                        IData ecOffer = offers.getData(j - 1);
                        if (uuInfo.getString("USER_ID").equals(ecOffer.getString("USER_ID")))
                        {// 集团订购的商品列表需将成员已订购的删除
                        	String brandCode = ecOffer.getString("BRAND_CODE");

                        	if("BOSG".equals(brandCode)){
                        		//拿到BBOSS商品下所有已订购的产品
                            	IData paramData = new DataMap();
                            	paramData.put("USER_ID_A", uuInfo.getString("USER_ID"));
                            	paramData.put("RELATION_TYPE_CODE", "90");
                            	paramData.put("ROLE_CODE_B", "0");
                            	IDataset bbOffers = CSViewCall.call(this, "CS.RelaBBInfoQrySVC.qryRelaBBInfoByRoleCodeBForGrp", paramData);
                            	
                            	boolean showFlag = true;//用于标识订购的产品是否与成员产生BB关系
                            	if(IDataUtil.isNotEmpty(bbOffers)){                 
                            		for(int s=0; s<bbOffers.size(); s++){
                            			IData bbOffer = bbOffers.getData(s);
                            			String merchPuserId = bbOffer.getString("USER_ID_B");
                                    	IData param = new DataMap();
                                    	param.put("USER_ID_A", merchPuserId);
                                    	param.put("RELATION_TYPE_CODE", "81");
                                    	param.put("USER_ID_B", mebSubscriberInsId);
                                    	param.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());

                                    	IDataset mebBBbOffers = CSViewCall.call(this, "CS.RelaBBInfoQrySVC.qryBBInfoByUserIdAB", param);
                                    	if(IDataUtil.isEmpty(mebBBbOffers)){
                                    		showFlag = false;
                                    		
                                    	}
                                    	
                            		}
                            		
                            	}
                            	
                            	if(showFlag){//若商品下的所有产品都与成员已具备BB关系则不在可订购区域显示
                            		offers.remove(j - 1);
                            	}
                        	}else{
                               
                        		offers.remove(j - 1);

                        	}
                            break;
                        }
                    }
                }
            }
        }
        // 过滤没有成员操作的商品
        offers = filterOfferByMebOperate(offers);

        // 过滤不能从前台受理的业务
        // offers = filterOffer(offers);
        
        //根据登录工号权限筛选可以办理的产品
        if (IDataUtil.isNotEmpty(offers))
        {
            for (int i = 0, size = offers.size(); i < size; i++)
            {
                IData offer = offers.getData(i);
                offer.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
                offer.put("PRODUCT_NAME", offer.getString("OFFER_NAME"));
            }
        }
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), offers);

        setOffers(offers);
        
        getPage().setAjax(offers);
    }

    /**
     * 查询成员订购商品列表
     * 
     * @param mebSubscriberInsId
     * @throws Exception
     */
    private void queryMebOpenedOfferByMebSubscriberInsId(String mebSubscriberInsId) throws Exception
    {
        IDataset insOffers = new DatasetList();
        // 查询成员UU关系
    	IData data = new DataMap();
    	data.put("USER_ID", mebSubscriberInsId);
    	data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());
    	IDataset uuInfos = CSViewCall.call(this, "CS.GrpInfoQrySVC.getGroupInfo", data);
        if (DataUtils.isEmpty(uuInfos))
        {
            setOffers(new DatasetList());
        }
        else
        {
            for (int i = uuInfos.size(); i > 0; i--)
            {
                IData uuInfo = uuInfos.getData(i - 1);
                
                String roleCodeB = uuInfo.getString("ROLE_CODE_B", "");
                String relationTypeCode = uuInfo.getString("RELATION_TYPE_CODE", "");
                // 排除调集团彩铃的付费号码
                if (relationTypeCode.equals("26") && roleCodeB.equals("9"))
                {
                    continue;
                }
                
                
                String productId = uuInfo.getString("PRODUCT_ID");
                if (StringUtils.isBlank(productId))
                {
                	continue;
                }
                IData groupData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, uuInfo.getString("CUST_ID"),false);
                if (DataUtils.isEmpty(groupData))
                {
                	continue;
                }
                
                IData PMoffer = UpcViewCall.queryOfferByOfferId(this, "P", productId,"");
                
                IData catalogItem = new DataMap();
                catalogItem.put("SERIAL_NUMBER", uuInfo.getString("SERIAL_NUMBER"));
                catalogItem.put("USER_ID", uuInfo.getString("USER_ID"));
                catalogItem.put("OFFER_CODE", productId);
                catalogItem.put("OFFER_ID", PMoffer.getString("OFFER_ID", ""));
                catalogItem.put("OFFER_NAME", PMoffer.getString("OFFER_NAME", ""));
                catalogItem.put("BRAND_CODE", uuInfo.getString("BRAND_CODE", ""));
            	catalogItem.put("CUST_ID", uuInfo.getString("CUST_ID"));
            	catalogItem.put("GROUP_ID", groupData.getString("GROUP_ID"));
                catalogItem.put("GROUP_NAME", groupData.getString("CUST_NAME"));
                catalogItem.put("CATALOG_DATA", buildCatalogData(catalogItem));
                
                insOffers.add(catalogItem);
            }
            // 过滤不在产品树上的产品
            for (int j = insOffers.size(); j > 0; j--)
            {
                IData offer = insOffers.getData(j - 1);
                
                IData param = new DataMap();
                param.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
                
                if("8000".equals(offer.getString("OFFER_CODE"))){
                    insOffers.remove(j - 1);
                    continue;
                }
                
                //如果树上没有，直接remove
            	IDataset productTypeDataset = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getProductTypesByProductId", param);

                if (DataUtils.isEmpty(productTypeDataset))
                {
                	insOffers.remove(j - 1);
                    continue;
                }
            }

            //根据登录工号权限筛选可以办理的产品
            if (IDataUtil.isNotEmpty(insOffers))
            {
                for (int i = 0, size = insOffers.size(); i < size; i++)
                {
                    IData offer = insOffers.getData(i);
                    offer.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
                    offer.put("PRODUCT_NAME", offer.getString("OFFER_NAME"));
                }
            }
            ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), insOffers);
            
            setOffers(insOffers);
            
            getPage().setAjax(insOffers);
        }
    }

    /**
     * 过滤已下线商品
     * 
     * @param offers
     * @return
     * @throws Exception
     */
    private IDataset filterOfflineOffers(IDataset offers) throws Exception
    {
        // 获取下线商品列表
        // IDataset offlineOffers = StaticUtil.getStaticList("EC_OFFLINE_OFFER");
        // if(DataUtils.isNotEmpty(offlineOffers))
        // {
        // for(int i = offers.size(); i > 0; i--)
        // {
        // IData offer = offers.getData(i-1);
        // for(int j = 0, size = offlineOffers.size(); j < size; j++)
        // {
        // IData offlineOffer = offlineOffers.getData(j);
        // String offlineOfferId = offlineOffer.getString("CODE_VALUE", "");
        // if(offlineOfferId.equals(offer.getString("OFFER_ID")))
        // {
        // offers.remove(offer);
        // break;
        // }
        // }
        // }
        // }
        return offers;
    }

    /**
     * 过滤没有成员操作的商品(条件：1.动力100商品；2.没有成员商品；3.没有配置成员新增的busiItemCode)
     * 
     * @param offers
     * @return
     * @throws Exception
     */
    private IDataset filterOfferByMebOperate(IDataset offers) throws Exception
    {
       for (int i = offers.size(); i > 0; i--)
        {
            IData offer = offers.getData(i - 1);
            
            IData param = new DataMap();
           
            if("8000".equals(offer.getString("OFFER_CODE"))){//集团VPMN成员受理不能直接在成员侧受理
                offers.remove(i - 1);
                continue;
            }
            
            param.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
            //如果树上没有，直接remove
            IDataset productTypeDataset = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getProductTypesByProductId", param);
            
            if (DataUtils.isEmpty(productTypeDataset))
            {
                offers.remove(i - 1);
                continue;
            }
        
            //BBOSS商品上没有挂成员商品，所以不适合做下面的一系列排除判断
            if("BOSG".equals(offer.getString("BRAND_CODE", ""))){
            	continue;
            }
            if ("DLBG".equals(offer.getString("BRAND_CODE", "")))
            {
                offers.remove(i - 1);
                continue;
            }

            String offerId = offer.getString("OFFER_ID");
            String mebOfferId = IUpcViewCall.queryMemOfferIdByOfferId(offerId);
            //不存在成员产品
            if (StringUtils.isBlank(mebOfferId))
            {
                offers.remove(i - 1);
                continue;
            }
            String busiItemCode = CommonViewCall.getAttrValueFromAttrBiz(this, offer.getString("OFFER_CODE"), "P", BizCtrlType.CreateMember, "TradeTypeCode");
            if (StringUtils.isBlank(busiItemCode))
            {
                offers.remove(i - 1);
                continue;
            }
        }
        return offers;
    }

    /**
     * 过滤不让从前台受理的业务
     * 
     * @param offers
     * @return
     * @throws Exception
     */
    // private IDataset filterOffer(IDataset offers) throws Exception
    // {
    // for(int i = offers.size(); i > 0; i--)
    // {
    // IData offer = offers.getData(i-1);
    // String offerId = offer.getString("OFFER_ID");
    // if("6445".equals(offerId) || "6444".equals(offerId))
    // {//人人通A电话本(ADC)、人人通B电话本(ADC)
    // offers.remove(i-1);
    // }
    // }
    // return offers;
    // }

    private IData buildCatalogData(IData offerData) throws Exception
    {
        IData cataData = new DataMap();
        cataData.put("USER_ID", offerData.getString("USER_ID", ""));
        cataData.put("SERIAL_NUMBER", offerData.getString("SERIAL_NUMBER", ""));
        cataData.put("OFFER_ID", offerData.getString("OFFER_ID", ""));
        cataData.put("OFFER_CODE", offerData.getString("OFFER_CODE", ""));
        cataData.put("OFFER_NAME", offerData.getString("OFFER_NAME", ""));
        cataData.put("BRAND_CODE", offerData.getString("BRAND_CODE", ""));
        cataData.put("GROUP_ID", offerData.getString("GROUP_ID", ""));
        cataData.put("GROUP_NAME", offerData.getString("GROUP_NAME", ""));
        cataData.put("CUST_ID", offerData.getString("CUST_ID", ""));
        return cataData;
    }

    private IData buildCatalogDataForBatch(IData offerData, String batchOperType) throws Exception
    {
        IData cataData = new DataMap();
        cataData.put("USER_ID", offerData.getString("USER_ID", ""));
        cataData.put("SERIAL_NUMBER", offerData.getString("SERIAL_NUMBER", ""));
        cataData.put("OFFER_ID", offerData.getString("OFFER_ID", ""));
        cataData.put("OFFER_NAME", offerData.getString("OFFER_NAME", ""));
        cataData.put("BRAND_CODE", offerData.getString("BRAND_CODE", ""));
        cataData.put("GROUP_ID", offerData.getString("GROUP_ID", ""));
        cataData.put("GROUP_NAME", offerData.getString("GROUP_NAME", ""));
        cataData.put("CUST_ID", offerData.getString("CUST_ID", ""));
        cataData.put("IS_BATCH", true);
        cataData.put("BATCH_OPER_TYPE", batchOperType);
        return cataData;
    }

    /**
     * 根据目录规格标识查询集团销售品目录
     * 
     * @param catalogSpecId
     * @throws Exception
     */
    private void buttonChoose(IData info, String buttonChoose) throws Exception
    {

        if (StringUtils.isEmpty(buttonChoose))
        {
            info.put("BUTTON_CTR", false);
            info.put("BUTTON_MOD", false);
            info.put("BUTTON_DEL", false);
            info.put("BUTTON_TRAFFIC", false);
            return;
        }

        if (buttonChoose.contains(BizCtrlType.CreateMember))
        {
            info.put("BUTTON_CTR", true);
        }
        if (buttonChoose.contains(BizCtrlType.ChangeMemberDis))
        {
            info.put("BUTTON_MOD", true);
        }
        if (buttonChoose.contains(BizCtrlType.DestoryMember))
        {
            info.put("BUTTON_DEL", true);
        }
        if (!buttonChoose.contains(BizCtrlType.CreateMember) && !buttonChoose.contains(BizCtrlType.ChangeMemberDis)
        		&& !buttonChoose.contains(BizCtrlType.DestoryMember))
        {
            info.put("BUTTON_CTR", false);
            info.put("BUTTON_MOD", false);
            info.put("BUTTON_DEL", false);
            info.put("BUTTON_TRAFFIC", false);
        }

    }

    private boolean isEcCatalog() throws Exception
    {
        String isEc = getIsEc();
        if (StringUtils.isNotBlank(isEc))
        {
            return Boolean.parseBoolean(isEc);
        }
        else
        {
            return false;
        }
    }
    
    private void queryEcOpenedOfferDelMebOpenByCustIdAndSubscriberInsIdVpmn(String custId, String mebSubscriberInsId, String selEcSubInsId, String selOfferId,String productTree) throws Exception
    {
        IDataset offers = new DatasetList();

        IDataset insOffers = new DatasetList();
        
        if (StringUtils.isNotEmpty(selEcSubInsId) && StringUtils.isNotEmpty(selOfferId))
        {// 当集团认证外框选择了商品时，集团已订购商品列表只展示外框选择的商品(防止集团订购的商品数量过多导致查询超时)
            
            IData seloffer = IUpcViewCall.queryOfferByOfferId(selOfferId);
            IData data = new DataMap();
            data.put("USER_ID", selEcSubInsId);
            data.put("PRODUCT_ID", seloffer.getString("OFFER_CODE"));
            data.put(Route.ROUTE_EPARCHY_CODE, "cg");
            IDataset insOfferDataset = CSViewCall.call(this, "CS.UserProductInfoQrySVC.getUserProductByUserIdProductId", data);
            
            if (DataUtils.isNotEmpty(insOfferDataset))
            {
                IData offerInfo = insOfferDataset.first();
                IData subInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, selEcSubInsId);
                offerInfo.put("SERIAL_NUMBER", subInfo.get("SERIAL_NUMBER"));
                offerInfo.put("OFFER_ID", selOfferId);
                offerInfo.put("OFFER_CODE", subInfo.get("PRODUCT_ID"));
                offerInfo.put("OFFER_TYPE", "P");
                insOffers.add(offerInfo);
            }
        }
        else
        {
            insOffers = IUCAInfoIntfViewUtil.qryUserAndProductByCustIdForGrp(this, custId);

        }
        if (DataUtils.isNotEmpty(insOffers))
        {
            String groupId = "";
            String groupName = "";
            IData groupData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
            if (DataUtils.isNotEmpty(groupData))
            {
                groupId = groupData.getString("GROUP_ID");
                groupName = groupData.getString("CUST_NAME");
            }
            for (int j = 0, size = insOffers.size(); j < size; j++)
            {
                IData insoffer = insOffers.getData(j);
                String productId = insoffer.getString("PRODUCT_ID");
                IData PMoffer = UpcViewCall.queryOfferByOfferId(this, "P", productId,"");
                if(DataUtils.isEmpty(PMoffer)){
                    continue;
                }
                if(productTree.equals(productId)){
                    String accessNum = insoffer.getString("SERIAL_NUMBER");
                    String subscriberInsId = insoffer.getString("USER_ID");
                    IData offer = new DataMap();
                    offer.put("USER_ID", subscriberInsId);
                    offer.put("SERIAL_NUMBER", accessNum);
                    offer.put("OFFER_CODE", productId);
                    offer.put("OFFER_ID", PMoffer.getString("OFFER_ID", ""));
                    offer.put("OFFER_NAME", PMoffer.getString("OFFER_NAME", ""));
                    offer.put("BRAND_CODE", insoffer.getString("BRAND_CODE", ""));
                    offer.put("GROUP_ID", groupId);
                    offer.put("GROUP_NAME", groupName);
                    offer.put("CATALOG_DATA", buildCatalogData(offer));
                    offers.add(offer);
                }
                }
        }
        if (DataUtils.isNotEmpty(offers))
        {
            // 查询成员UU关系
            IData data = new DataMap();
            data.put("USER_ID", mebSubscriberInsId);
            data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());
            IDataset uuInfos = CSViewCall.call(this, "CS.GrpInfoQrySVC.getGroupInfo", data);

            if (DataUtils.isNotEmpty(uuInfos))
            {
                for (int i = 0, size = uuInfos.size(); i < size; i++)
                {
                    IData uuInfo = uuInfos.getData(i);
                    for (int j = offers.size(); j > 0; j--)
                    {
                        IData ecOffer = offers.getData(j - 1);
                        if (uuInfo.getString("USER_ID").equals(ecOffer.getString("USER_ID")))
                        {// 集团订购的商品列表需将成员已订购的删除
                            offers.remove(j - 1);
                            break;
                        }
                    }
                }
            }
        }
        // 过滤没有成员操作的商品
        offers = vpmnfilterOfferByMebOperate(offers,productTree);

        // 过滤不能从前台受理的业务
        // offers = filterOffer(offers);

        setOffers(offers);
        
        getPage().setAjax(offers);
    }
    private IDataset vpmnfilterOfferByMebOperate(IDataset offers,String productTree) throws Exception
    {
       for (int i = offers.size(); i > 0; i--)
        {
            IData offer = offers.getData(i - 1);
            
            IData param = new DataMap();
            
            if(productTree.equals(offer.getString("OFFER_CODE"))){//判断是否VPMN成员受理
                param.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
                
              //如果树上没有，直接remove
                IDataset productTypeDataset = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getProductTypesByProductId", param);

                if (DataUtils.isEmpty(productTypeDataset))
                {
                    offers.remove(i - 1);
                    continue;
                }
            }else{
                offers.remove(i - 1);
                continue;
            }
            //BBOSS商品上没有挂成员商品，所以不适合做下面的一系列排除判断
            if("BOSG".equals(offer.getString("BRAND_CODE", ""))){
                continue;
            }
            if ("DLBG".equals(offer.getString("BRAND_CODE", "")))
            {
                offers.remove(i - 1);
                continue;
            }
            
            String offerId = offer.getString("OFFER_ID");
            String mebOfferId = IUpcViewCall.queryMemOfferIdByOfferId(offerId);
            //不存在成员产品
            if (StringUtils.isBlank(mebOfferId))
            {
                offers.remove(i - 1);
                continue;
            }
            String busiItemCode = CommonViewCall.getAttrValueFromAttrBiz(this, offer.getString("OFFER_CODE"), "P", BizCtrlType.CreateMember, "TradeTypeCode");
            if (StringUtils.isBlank(busiItemCode))
            {
                offers.remove(i - 1);
                continue;
            } 
            
        }
        return offers;
    }

    
    /**
     * 查询成员订购商品列表(vpmn)
     * 
     * @param mebSubscriberInsId
     * @throws Exception
     */
    private void queryMebOpenedOfferByMebSubscriberInsIdVpmn(String mebSubscriberInsId,String productTree) throws Exception
    {
        IDataset insOffers = new DatasetList();
        // 查询成员UU关系
        IData data = new DataMap();
        data.put("USER_ID", mebSubscriberInsId);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());
        IDataset uuInfos = CSViewCall.call(this, "CS.GrpInfoQrySVC.getGroupInfo", data);
        if (DataUtils.isEmpty(uuInfos))
        {
            setOffers(new DatasetList());
        }
        else
        {
            for (int i = uuInfos.size(); i > 0; i--)
            {
                IData uuInfo = uuInfos.getData(i - 1);
                
                String roleCodeB = uuInfo.getString("ROLE_CODE_B", "");
                String relationTypeCode = uuInfo.getString("RELATION_TYPE_CODE", "");
                // 排除调集团彩铃的付费号码
                if (relationTypeCode.equals("26") && roleCodeB.equals("9"))
                {
                    continue;
                }
                
                
                String productId = uuInfo.getString("PRODUCT_ID");
                if (StringUtils.isBlank(productId))
                {
                    continue;
                }
                IData groupData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, uuInfo.getString("CUST_ID"),false);
                if (DataUtils.isEmpty(groupData))
                {
                    continue;
                }
                
                IData PMoffer = UpcViewCall.queryOfferByOfferId(this, "P", productId,"");
                
                IData catalogItem = new DataMap();
                catalogItem.put("SERIAL_NUMBER", uuInfo.getString("SERIAL_NUMBER"));
                catalogItem.put("USER_ID", uuInfo.getString("USER_ID"));
                catalogItem.put("OFFER_CODE", productId);
                catalogItem.put("OFFER_ID", PMoffer.getString("OFFER_ID", ""));
                catalogItem.put("OFFER_NAME", PMoffer.getString("OFFER_NAME", ""));
                catalogItem.put("BRAND_CODE", uuInfo.getString("BRAND_CODE", ""));
                catalogItem.put("CUST_ID", uuInfo.getString("CUST_ID"));
                catalogItem.put("GROUP_ID", groupData.getString("GROUP_ID"));
                catalogItem.put("GROUP_NAME", groupData.getString("CUST_NAME"));
                catalogItem.put("CATALOG_DATA", buildCatalogData(catalogItem));
                
                insOffers.add(catalogItem);
            }
            // 过滤不在产品树上的产品
            for (int j = insOffers.size(); j > 0; j--)
            {
                IData offer = insOffers.getData(j - 1);
                
                IData param = new DataMap();
                param.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
                
                if(productTree.equals(offer.getString("OFFER_CODE"))){

                    //如果树上没有，直接remove
                    IDataset productTypeDataset = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getProductTypesByProductId", param);
                    
                    if (DataUtils.isEmpty(productTypeDataset))
                    {
                        insOffers.remove(j - 1);
                        continue;
                    }
                }else{
                    insOffers.remove(j - 1);
                    continue;
                }
            }

            setOffers(insOffers);
            
            getPage().setAjax(insOffers);
        }
    }
    public abstract void setInfo(IData info);

    public abstract void setOffers(IDataset offers);

    public abstract void setCatalogs(IDataset catalogs);

    public abstract String getUpCategoryId();

    public abstract String getIsEc();

    public abstract String getJsFile();

    public abstract boolean getIsOnlyShowEcCatalog();

    public abstract boolean getIsBatch();

    public abstract String getBatchTypeCode();

    public abstract String getButtonChoose();
}
