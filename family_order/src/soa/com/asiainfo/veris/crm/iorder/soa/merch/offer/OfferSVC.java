
package com.asiainfo.veris.crm.iorder.soa.merch.offer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizService;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.MerchShoppingCartBean;
import com.asiainfo.veris.crm.iorder.soa.merch.offer.factory.OfferActionFactory;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class OfferSVC extends CSBizService
{

    public IDataset specialHandling(IData data) throws Exception
    {
        String type = data.getString("TYPE");
        IDataset elements = null;
        DisposeAction action = OfferActionFactory.getAction(type);
        if (action != null)
        {
            elements = action.execute(data);
        }
        return elements;
    }

    public IData getUserOffers(IData input) throws Exception
    {
        IData result = new DataMap();
        IDataset offers = new DatasetList();
        String userId = input.getString("USER_ID");
        String eparchyCode = getRouteId();

        IDataset userProducts = getUserPorducts(userId);
        // 查询用户资料的产品信息
        if (IDataUtil.isNotEmpty(userProducts))
        {
            offers.addAll(userProducts);
        }
        IDataset userSvcs = getUserSvcs(userId, eparchyCode);
        if (IDataUtil.isNotEmpty(userSvcs))
        {
            offers.addAll(userSvcs);
        }
        IDataset userDiscnts = getUserDiscnts(userId, eparchyCode);
        if (IDataUtil.isNotEmpty(userDiscnts))
        {
            offers.addAll(userDiscnts);
        }
        
        //用户平台服务 duhj
        IDataset userPlats = getUserPlats(userId, eparchyCode);
        if (IDataUtil.isNotEmpty(userPlats))
        {
            offers.addAll(userPlats);
        }
        
        
        result.put("USER_OFFERS", offers);

        MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
        IDataset cartOffers = shoppingCartBean.getShoppingCartAllElements(input);
        result.put("ORDER_OFFERS", cartOffers);
        
        //增加可订购商品配置
        if(BizEnv.getEnvBoolean("MERCH_ORDER_COMMPARA", false))
        {
	        IDataset commparaOffers = CommparaInfoQry.getCommparaByParaAttr("CSM", "4999", eparchyCode);
	        result.put("COMMPARA_OFFERS", commparaOffers);
        }
        return result;
    }

    public IData checkLimitTrade(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String strTradeTypeCode = input.getString("TRADE_TYPE_CODE");
        String eparchyCode = getRouteId();
        if (StringUtils.isNotBlank(strTradeTypeCode))
        {
            IDataset listExistsLimitTradeType = TradeInfoQry.getNoTradeByTradeId(strTradeTypeCode, userId, "ZZZZ", "0", "0", eparchyCode, null);
            if (IDataUtil.isNotEmpty(listExistsLimitTradeType))
            {
                String strTradeTypeCodeLimit = listExistsLimitTradeType.getData(0).getString("LIMIT_TRADE_TYPE_CODE");
                String strTradeType = UTradeTypeInfoQry.getTradeType(strTradeTypeCodeLimit, eparchyCode).getString("TRADE_TYPE");
                CSAppException.appError("-1", "业务受理前条件判断-用户有未完工的限制业务【" + strTradeType + "】！");
            }
        }
        return null;
    }

    private IDataset getUserDiscnts(String userId, String eparchyCode) throws Exception
    {
        IDataset offers = new DatasetList();
        IDataset userDiscnts = BofQuery.queryUserAllValidDiscnt(userId, eparchyCode);
        for (int i = 0; i < userDiscnts.size(); i++)
        {
            IData discnt = userDiscnts.getData(i);
            IData param = new DataMap();
            param.put("OFFER_CODE", discnt.getString("DISCNT_CODE"));
            param.put("OFFER_TYPE", "D");
            param.put("START_DATE", discnt.getString("START_DATE"));
            param.put("END_DATE", discnt.getString("END_DATE"));
            offers.add(param);
        }
        return offers;
    }

    private IDataset getUserSvcs(String userId, String eparchyCode) throws Exception
    {
        IDataset offers = new DatasetList();
        IDataset userSvcs = BofQuery.queryUserAllSvc(userId, eparchyCode);
        for (int i = 0; i < userSvcs.size(); i++)
        {
            IData svc = userSvcs.getData(i);
            IData param = new DataMap();
            param.put("OFFER_CODE", svc.getString("SERVICE_ID"));
            param.put("OFFER_TYPE", "S");
            param.put("START_DATE", svc.getString("START_DATE"));
            param.put("END_DATE", svc.getString("END_DATE"));
            offers.add(param);
        }
        return offers;
    }

    private IDataset getUserPorducts(String userId) throws Exception
    {
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
        IDataset offers = new DatasetList();
        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                IData param = new DataMap();
                param.put("OFFER_CODE", userProduct.getString("PRODUCT_ID"));
                param.put("OFFER_TYPE", "P");
                param.put("START_DATE", userProduct.getString("START_DATE"));
                param.put("END_DATE", userProduct.getString("END_DATE"));
                param.put("MAIN_TAG", "1");
                offers.add(param);
            }
        }
        return offers;
    }
    
    private IDataset getUserPlats(String userId, String eparchyCode) throws Exception
    {
        IDataset offers = new DatasetList();
        IDataset userPlats = BofQuery.queryUserAllPlatSvc(userId, eparchyCode);
        for (int i = 0; i < userPlats.size(); i++)
        {
            IData plat = userPlats.getData(i);
            IData param = new DataMap();
            param.put("OFFER_CODE", plat.getString("SERVICE_ID"));
            param.put("OFFER_TYPE", "Z");
            param.put("START_DATE", plat.getString("START_DATE"));
            param.put("END_DATE", plat.getString("END_DATE"));
            offers.add(param);
        }
        return offers;
    }
    

    /**
     * 商品订购查询服务优惠标签
     * 
     * @param param
     * @author guohuan
     * @return
     * @throws Exception
     */
    public IDataset buildSerciceAndDiscntTagList(IData param) throws Exception
    {
        IDataset result = new DatasetList();
        String productId = param.getString("USER_PRODUCT_ID");
        if (StringUtils.isBlank(productId))
        {
            return result;
        }
        String serviceName = param.getString("SERVICE_NAME");
        
        
        String type = param.getString("TYPE");
        String typeTag =  StringUtils.equals("D1", type)?"D":"S";
        IDataset groups = new DatasetList();
        // 获取主产品下的组
        IDataset offerGroups = UpcCall.queryOfferGroupRelOfferId("P", productId);
        if (IDataUtil.isNotEmpty(offerGroups))
        {
            for (int i = 0; i < offerGroups.size(); i++)
            {
                IData offerGroup = offerGroups.getData(i);
                String removegroupId = param.getString("REMOVE_GROUP_ID", "");
                String groupId = offerGroup.getString("GROUP_ID", "");
                String groupName = offerGroup.getString("GROUP_NAME", "");
                String tradetypeCode = param.getString("TRADE_TYPE_CODE", "");

                if (removegroupId.equals(groupId) && "606".equals(tradetypeCode))
                {
                    offerGroups.remove(i);
                    i--;
                }
                if (("41005405".equals(groupId) || "41005605".equals(groupId)) && !"606".equals(tradetypeCode))
                {
                    offerGroups.remove(i);
                    i--;
                }
                offerGroup.put("TAG_VALUE_ID", groupId);
                offerGroup.put("TAG_VALUE_NAME", groupName);
                offerGroup.put("TAG_ID", "0000000000");
                
                
                
                
                
                IData inParam = new DataMap();
                inParam.put("TAG_TYPE", StringUtils.equals("D1", type)?"D":"S");
                inParam.put("TAG_LIST", new DatasetList(offerGroup));
                inParam.put("PRODUCT_ID", productId);
                IDataset offers = getOffersByOfferType(inParam);
                if (IDataUtil.isNotEmpty(offers))
                {
                    StringBuffer dataBuffer = new StringBuffer("");
                    for (int j = 0; j < offers.size(); j++)
                    {
                        IData offer = offers.getData(j);
                        String offerType = offer.getString("OFFER_TYPE");
                        String offerCode = offer.getString("OFFER_CODE");
                        dataBuffer.append(offerType);
                        dataBuffer.append("|");
                        dataBuffer.append(offerCode);
                        dataBuffer.append(",");
                    }
                    if (!dataBuffer.toString().contains(typeTag))
                    {
                        offerGroups.remove(i);
                        i--;
                    }
                }else {
//                    if (StringUtils.equals("S", typeTag))
//                    {
                        offerGroups.remove(i);
                        i--;
//                    }
                }
                
                
                
                
                
                
            }
            // groups.addAll(offerGroups);
        }
        
//        DataHelper.sort(groups, "FORCE_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND, "DEFAULT_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        // 构造用于获取打散商品的组
//        if (StringUtils.equals("true", StaticUtil.getStaticValue("OFFER_LIST_PARAM", "DISPLAY_SWITCH_JOIN_REL")))
//        {
         /*   IData group = new DataMap();
            group.put("GROUP_NAME", "其它");
            group.put("GROUP_ID", "-1");
            group.put("TAG_VALUE_ID", "-1");
            group.put("TAG_VALUE_NAME", "其它");
            offerGroups.add(group);
        }else {*/
//            IData group = new DataMap();
//            group.put("GROUP_NAME", "不限");
//            group.put("GROUP_ID", "-1");
//            group.put("TAG_VALUE_ID", "-1");
//            group.put("TAG_VALUE_NAME", "不限");
//            offerGroups.add(group);
//        }
            if (IDataUtil.isNotEmpty(offerGroups))
            {
//                DataHelper.sort(offerGroups, "TAG_VALUE_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
            	DataHelper.sort(offerGroups, "FORCE_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND, "DEFAULT_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
            }
        IData dataTag = new DataMap();
        dataTag.put("TAG_ID", "0000000000");
        dataTag.put("TAG_NAME", "分组");
        dataTag.put("TAG_VALUE_LIST", offerGroups);
        result.add(dataTag);
        IDataset labelList = UpcCall.qryAllTagAndTagValueByOfferId(productId, "P");
        if (IDataUtil.isNotEmpty(labelList))
        {
            for (int i = 0; i < labelList.size(); i++)
            {
                IData label = labelList.getData(i);
                String labelId = label.getString("LABEL_ID");
                String labelName = label.getString("LABEL_NAME");
                label.put("TAG_ID", labelId);
                label.put("TAG_NAME", labelName);
                IDataset labelValueList = label.getDataset("LABEL_KEY_LIST");
                IDataset tagValueList = new DatasetList();
                for (int j = 0; j < labelValueList.size(); j++)
                {
                    IData tagValue = new DataMap();
                    IData labelValue = labelValueList.getData(j);
                    String labelKeyId = labelValue.getString("LABEL_KEY_ID");
                    String labelKeyValue = labelValue.getString("LABEL_KEY_VALUE");
                    tagValue.put("TAG_VALUE_ID", labelKeyId);
                    tagValue.put("TAG_VALUE_NAME", labelKeyValue);
//                    tagValueList.add(tagValue);
//                    label.put("TAG_VALUE_LIST", tagValueList);
                    
                    
                    
                    
                    IData inParam = new DataMap();
                    IData tagParam = new DataMap();
                    tagParam.put("TAG_ID", labelId);
                    tagParam.put("TAG_VALUE_ID", labelKeyId);
                    inParam.put("TAG_TYPE", StringUtils.equals("D1", type)?"D":"S");
                    inParam.put("TAG_LIST", new DatasetList(tagParam));
                    inParam.put("PRODUCT_ID", productId);
                    IDataset offers = getOffersByOfferType(inParam);
                    if (IDataUtil.isNotEmpty(offers))
                    {
                        StringBuffer dataBuffer = new StringBuffer("");
                        for (int k = 0; k < offers.size(); k++)
                        {
                            IData offer = offers.getData(k);
                            String offerType = offer.getString("OFFER_TYPE");
                            String offerCode = offer.getString("OFFER_CODE");
                            dataBuffer.append(offerType);
                            dataBuffer.append("|");
                            dataBuffer.append(offerCode);
                            dataBuffer.append(",");
                        }
                        if (dataBuffer.toString().contains(typeTag))
                        {
                            tagValueList.add(tagValue);
                            label.put("TAG_VALUE_LIST", tagValueList);
                        }
                    }
                    
                    
                    
                    
                }
                result.add(label);
            }
        }
        return result;
    }

    /**
     * 根据标签，元素类型查询元素列表
     * 
     * @param data
     * @author guohuan
     * @return
     * @throws Exception
     */
    public IDataset getOffersByOfferType(IData data) throws Exception
    {
        IDataset offerList = new DatasetList();
        IDataset result = new DatasetList();
        IDataset offers = new DatasetList();

        IDataset tagList = new DatasetList(data.getString("TAG_LIST", "[]"));
        String offerType = data.getString("OFFER_TYPE");
        String eparchyCode= data.getString("EPARCHY_CODE");
        int tagSize = tagList.size();
        
        if("P".equals(offerType))
        {
        	String userProductId = data.getString("USER_PRODUCT_ID");
        	String tagValueId = "-1";
        	if(IDataUtil.isNotEmpty(tagList))
		    {
		        tagValueId = tagList.getData(0).getString("TAG_VALUE_ID");
		    }
        	IData rst = UpcCall.queryTransProducts(userProductId);
        	offers = rst.getDataset("OFFERS");
        	if(IDataUtil.isNotEmpty(offers))
			{
				for(int i=0;i<offers.size();i++)
				{
					IData offer = offers.getData(i);
					if(StringUtils.equals(tagValueId, "-1") || StringUtils.equals(tagValueId, offer.getString("CATALOG_ID")))
					{
						offer.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
						result.add(offer);
					}
				}
				//权限过滤
				ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), result);
	            DataHelper.sort(result, "OFFER_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
			}
        }else 
        {
	        // 如果没有选择标签的话，默认查join_rel
	        if (IDataUtil.isEmpty(tagList))
	        {
	           // offerList = queryProductChangeListTagsAndoffers(data).getDataset("OFFERS");
	            offerList = switchJoinRel(data);
	        }
	        else
	        {
	            if (1 < tagSize)
	            {
	                return result;
	            }
	//            for (int i = 0; i < tagList.size(); i++)
	//            {
	                String tagId = tagList.getData(0).getString("TAG_ID");
	                String tagValueId = tagList.getData(0).getString("TAG_VALUE_ID");
	                data.put("TAG_ID", tagId);
	                data.put("TAG_VALUE_ID", tagValueId);
	                if (StringUtils.equals("0000000000", tagId)) // 分组
	                {
	                    if (StringUtils.equals("-1", tagValueId))
	                    {
	                        offers = queryProductChangeListTagsAndoffers(data).getDataset("OFFERS");
	                    }else {
	                        offers = switchGroup(data);
	                    }
	                }
	                else
	                {
	                    offers = switchLable(data);
	                }
	                offerList.addAll(offers);
	//            }
	        }
	        
	        if (IDataUtil.isNotEmpty(offerList)&&StringUtils.isNotBlank(offerType))
	        {
	            for (int j = 0; j < offerList.size(); j++)
	            {
	                IData offer = offerList.getData(j);
	                String type = offer.getString("OFFER_TYPE");
	                if (StringUtils.equals(offerType, type))
	                {
	                  //action已处理  guohuan
	                   /* // 处理ATTRPARAM
	                    offer.put("ELEMENT_TYPE_CODE", offer.getString("OFFER_TYPE"));
	                    offer.put("ELEMENT_ID", offer.getString("OFFER_CODE"));
	                    IDataset attrs = ElementUtil.dealSelectedElementAttrs(offer, eparchyCode);
	                    if (attrs != null && attrs.size() > 0)
	                    {
	                        offer.put("ATTR_PARAM", attrs);
	                    }*/
	                    result.add(offer);
	                }
	            }
	        }else {
	            result.addAll(offerList);
	        }
        }
        return result;
    }

    /**
     * 根据标签，元素类型查询元素列表
     * 
     * @param data
     * @author guohuan
     * @return
     * @throws Exception
     */
    public IData queryProductChangeListTagsAndoffers(IData param) throws Exception
    {
        String eparchyCode = param.getString("EPARCHY_CODE");
        IDataset eles = new DatasetList(param.getString("SELECTED_ELEMENTS"));

        IData result = new DataMap();
        String productId = param.getString("PRODUCT_ID");
        // String productId = "10007614";

        if (StringUtils.isBlank(productId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户主产品ID不能为空");
        }
        // 获取主产品下的组
        IDataset offerGroups = UpcCall.queryOfferGroupRelOfferId("P", productId);
        IDataset labels = new DatasetList();
        IDataset offers = new DatasetList();
        if (IDataUtil.isNotEmpty(offerGroups))
        {
            for (int i = 0; i < offerGroups.size(); i++)
            {
                IData input = new DataMap();
                IData offerGroup = offerGroups.getData(i);
                String removegroupId = offerGroup.getString("REMOVE_GROUP_ID", "");
                String groupId = offerGroup.getString("GROUP_ID", "");
                String groupName = offerGroup.getString("GROUP_NAME", "");
                String tradetypeCode = offerGroup.getString("TRADE_TYPE_CODE", "");
                String groupType = offerGroup.getString("GROUP_TYPE", "");
                String selectFlag = offerGroup.getString("SELECT_FLAG", "");

                if (removegroupId.equals(groupId) && "606".equals(tradetypeCode))
                {
                    offerGroups.remove(i);
                    i--;
                }
                if (("41005405".equals(groupId) || "41005605".equals(groupId)) && !"606".equals(tradetypeCode))
                {
                    offerGroups.remove(i);
                    i--;
                }
                offerGroup.put("LABEL_ID", groupId);
                offerGroup.put("LABEL_TYPE", "1");
                offerGroup.put("LABEL_NAME", groupName);
                offerGroup.put("SELECT_FLAG", selectFlag);
                offerGroup.put("LABEL_HEAD", "0000000000");

                input.put("TAG_VALUE_ID", groupId);
                input.put("LABEL_TYPE", "1");
                IDataset offerList = switchGroup(input);
                offers.addAll(offerList);
            }
            // groups.addAll(offerGroups);
        }
        DataHelper.sort(offerGroups, "FORCE_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND, "DEFAULT_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        // 构造用于获取打散商品的组
        IData group = new DataMap();
        group.put("LABEL_NAME", "不限");
        group.put("LABEL_ID", "-1");
        group.put("LABEL_TYPE", "-1");
        offerGroups.add(group);
        IDataset labelList = UpcCall.qryAllTagAndTagValueByOfferId(productId, "P");
        if (IDataUtil.isNotEmpty(labelList))
        {
            for (int i = 0; i < labelList.size(); i++)
            {
                IData label = labelList.getData(i);
                String labelId = label.getString("LABEL_ID");
                IDataset labelValueList = label.getDataset("LABEL_KEY_LIST");
                for (int j = 0; j < labelValueList.size(); j++)
                {
                    IData tagValue = new DataMap();
                    IData inParam = new DataMap();
                    IData labelValue = labelValueList.getData(j);
                    String labelKeyId = labelValue.getString("LABEL_KEY_ID");
                    String labelKeyValue = labelValue.getString("LABEL_KEY_VALUE");
                    tagValue.put("LABEL_ID", labelKeyId);
                    tagValue.put("LABEL_NAME", labelKeyValue);
                    tagValue.put("LABEL_HEAD", labelId);
                    tagValue.put("LABEL_TYPE", "2");
                    

                    inParam.put("PRODUCT_ID", productId);
                    inParam.put("TAG_ID", labelId);
                    inParam.put("TAG_VALUE_ID", labelKeyId);
                    inParam.put("LABEL_TYPE", "2");
                    labels.add(tagValue);
                    offers.addAll(switchLable(inParam));
                }
            }
        }
        labels.addAll(offerGroups);
        if (IDataUtil.isNotEmpty(labels))
        {
            DataHelper.sort(labels, "LABEL_TYPE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        }
        if (IDataUtil.isNotEmpty(offers))
        {
            DataHelper.sort(offers, "OFFER_TYPE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND, "OFFER_CODE", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);
        }
        result.put("LABELS", labels);
        result.put("OFFERS", offers);
        return result;
    }
    private IDataset switchGroup(IData data) throws Exception
    {
        IDataset offerList = new DatasetList();
        String serviceName = data.getString("SERVICE_NAME");
        String groupId = data.getString("TAG_VALUE_ID");
        String lableType = data.getString("LABEL_TYPE");
        String menu = data.getString("m", "");
        int count = 0;
        // System.out.println("==========OfferListHandler=========="+data);
        // 不限的情况 查询joinrel
        if (StringUtils.equals(groupId, "-1"))
        {
            return switchJoinRel(data);
        }

        if (StringUtils.isBlank(serviceName))
        {
        	offerList = UpcCall.queryGroupComRelOfferByGroupId(groupId, "");
        	if(IDataUtil.isNotEmpty(offerList))
        	{
        		count = offerList.getData(0).getInt("COUNT", 0);
        	}
        }
        else
        {
            offerList = CSAppCall.call(serviceName, data);
        }
        
        //后续考虑放到OfferHandler.disposeElements，没权限置灰
        ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), offerList);
        DataHelper.sort(offerList, "OFFER_TYPE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND, "OFFER_CODE", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);

        for (Iterator iterator = offerList.iterator(); iterator.hasNext();)
        {
            IData item = (IData) iterator.next();
            String offerType = StaticUtil.getStaticValue("OFFER_LIST_NODISPLAY", item.getString("OFFER_CODE"));

            if (item.getString("OFFER_TYPE", "").equals(offerType) && !"crm9B21".equals(menu) && !"crm9B11".equals(menu) && !"KDRH001".equals(menu) && !"crmN100".equals(menu) && !"crmNB11".equals(menu) && !"crmNB21".equals(menu))
            {
                iterator.remove();
            }
            item.put("GROUP_ID", groupId);
            item.put("LABEL_ID", groupId);
            item.put("LABEL_TYPE", lableType);
            item.put("COUNT", count);
        }
        return offerList;
    }

    private IDataset switchLable(IData data) throws Exception
    {
        String productId = data.getString("PRODUCT_ID");
        String category = data.getString("CATEGORY_ID");
        String labelId = data.getString("TAG_ID");
        String labelKeyId = data.getString("TAG_VALUE_ID");
        String lableType = data.getString("LABEL_TYPE");
        IDataset offerList = new DatasetList();
        IDataset result = new DatasetList();
        result = UpcCall.qryOfferByTagInfo(productId, "P", labelId, labelKeyId, category);
        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0; i < result.size(); i++)
            {
                IData offer = result.getData(i);
                IDataset offers = offer.getDataset("OFFER_LIST");
                if (IDataUtil.isNotEmpty(offers))
                {
                    for (int j = 0; j < offers.size(); j++)
                    {
                        IData offerElement = offers.getData(j);
                        offerElement.put("LABEL_ID", labelKeyId);
                        offerElement.put("LABEL_TYPE", lableType);
                    }
                    offerList.addAll(offers);
                }
            }
        }
        return offerList;
    }

    private IDataset switchJoinRel(IData data) throws Exception
    {
        String joinServiceName = data.getString("JOIN_SERVICE_NAME");
        String categoryId = "100000000114,100000000008";// 产品变更界面前台就是写死的
        String productId = data.getString("PRODUCT_ID");
        String lableType = data.getString("LABEL_TYPE");
        IDataset categorys = new DatasetList();
        IDataset result = new DatasetList();
        if (StringUtils.isBlank(joinServiceName))
        {
            categorys = UpcCall.queryOffersByMultiCategory(productId, data.getString("EPARCHY_CODE"), categoryId, "2");
        }
        else
        {
            categorys = CSAppCall.call(joinServiceName, data);
        }

        // 将没有销售品列表的品类删除掉
        if (IDataUtil.isNotEmpty(categorys))
        {
            for (int i = categorys.size() - 1; i >= 0; i--)
            {
                IData categroy = categorys.getData(i);
                if (IDataUtil.isEmpty(categroy.getDataset("OFFER_LIST")))
                {
                    categorys.remove(i);
                }
                else
                {
                    IDataset offers = categroy.getDataset("OFFER_LIST");
                    for (int j = 0; j < offers.size(); j++)
                    {
                        IData offer = offers.getData(j);
                        offer.put("LABEL_ID", "-1");
                        offer.put("LABEL_TYPE", lableType);
                    }
                    result.addAll(offers);
                }
            }
        }
        return result;
    }

    public IDataset getOfferListByLabel(IData param) throws Exception
    {
        // int current = param.getInt("CURRENT", 1);
        // Pagination pagin = new Pagination(pageSize, current);
        // pagin.setCurrent(current);
        String labelId = param.getString("LABEL_ID");
        String labelType = param.getString("LABEL_TYPE");
        String productId = param.getString("PRODUCT_ID");
        String eparchyCode = param.getString("ROUTE_EPARCHY_CODE");
        // result.put("LABELS", labels);
        // result.put("OFFERS", offers);
        IData input = new DataMap();
        IData offerLabelData = queryProductChangeListTagsAndoffers(param);
        IDataset offers = new DatasetList();
        if (IDataUtil.isNotEmpty(offerLabelData))
        {
            IDataset offersList = offerLabelData.getDataset("OFFERS");
            if (IDataUtil.isNotEmpty(offersList))
            {
                for (int i = 0; i < offersList.size(); i++)
                {
                    IData offer = offersList.getData(i);
                    String label = offer.getString("LABEL_ID");
                    if (StringUtils.equals(labelId, label))
                    {
                        offers.add(offer);
                    }
                }
            }
        }

        //
        // IDataset result = UpcCallIntf.getOfferListByLabel(productId, "P", labelId, labelType, eparchyCode, null);
        // DataHelper.sort(result, "OFFER_CODE", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);

        return offers;
    }

    /**
     * 根据类型，搜索商品
     * 
     * @param data
     * @author guohuan
     * @return
     * @throws Exception
     */
    public IDataset searchOffersByType(IData param) throws Exception
    {
        IDataset offers = new DatasetList();
        String searchText = param.getString("SEARCH_TEXT");
        if (searchText == null)
        {
            searchText = param.getString("q");
        }
        String productId = param.getString("USER_PRODUCT_ID");
        String type = param.getString("TYPE");
        String elementType = param.getString("OFFER_TYPE");
        String eparchyCode = param.getString("EPARCHY_CODE");
        if (StringUtils.isNotBlank(searchText) && searchText.length() >= 2 && StringUtils.isNotBlank(eparchyCode) && StringUtils.isNotBlank(productId))
        {
            Map<String, String> searchData = new HashMap<String, String>();
            SearchResponse resp = new SearchResponse();
            if (StringUtils.equals("P1", type)) // 套餐搜索search未配置有问题，无法搜索
            {
                searchData.put("PRODUCT_ID_A", productId);
                resp = SearchClient.search("PM_OFFER_JOIN_REL", searchText, searchData, 0, 21);
            }
            else
            {
                searchData.put("PRODUCT_ID", productId);
                resp = SearchClient.search("PM_OFFER_ELEMENT", searchText, searchData, 0, 21);
            }
            IDataset datas = resp.getDatas();
            ElementPrivUtil.filterElementListByPriv(BizService.getVisit().getStaffId(), datas);
            if (IDataUtil.isNotEmpty(datas) && datas.size() > 0)
            {
                for (int i = 0; i < datas.size(); i++)
                {
                    IData data = datas.getData(i);
                    String offerName = "";
                    String offerCode = "";
                    String offerType = "";
                    String description = "";
                    if (StringUtils.equals("P1", type))
                    {
                        offerName = data.getString("PRODUCT_NAME");
                        offerCode = data.getString("PRODUCT_ID");
                        offerType = "P";
                        //套餐工号权限过滤
                        if (checkProduct(productId, offerCode))
                        {
                            description = UProductInfoQry.getProductExplainByProductId(offerCode);
                        }else {
                            datas.remove(i);
                            i--;
                            continue;
                        }
                    }
                    else
                    {
                        offerName = data.getString("ELEMENT_NAME");
                        offerCode = data.getString("ELEMENT_ID");
                        offerType = data.getString("ELEMENT_TYPE_CODE");
                        description = StringUtils.isBlank(data.getString("EXPLAIN")) ? offerName : data.getString("EXPLAIN");
                       //action已处理  guohuan
                        /* // 处理ATTRPARAM
                        IDataset attrs = ElementUtil.dealSelectedElementAttrs(data, eparchyCode);
                        if (attrs != null && attrs.size() > 0)
                        {
                            data.put("ATTR_PARAM", attrs);
                        }*/
                    }
                    if (StringUtils.equals(elementType, offerType))
                    {
                        data.put("OFFER_NAME", offerName);
                        data.put("OFFER_CODE", offerCode);
                        data.put("OFFER_TYPE", offerType);
                        data.put("DESCRIPTION", description);
                        data.put("GROUP_ID", data.getString("PACKAGE_ID", "-1"));
                        data.put("ORDER_MODE", data.getString("REORDER"));
                        offers.add(data);
                    }
                }
            }
        }
        return offers;
    }
    
    /**
     * 根据类型，搜索平台商品
     * 
     * @param data
     * @author duhj
     * @return
     * @throws Exception
     */
    public IDataset searchPlatOffersByType(IData param) throws Exception
    {
        IDataset offers = new DatasetList();
        String searchText = param.getString("SEARCH_TEXT"); 
        if("".equals(searchText)){
        	return offers;
        }
        SearchResponse resp = SearchClient.search("PM_OFFER_PLATSVC", searchText, 0, 21);
        IDataset datas = resp.getDatas();
        if(IDataUtil.isNotEmpty(datas)){
            for (int i = 0; i < datas.size(); i++) {
                IData iData = datas.getData(i);
                iData.put("OFFER_NAME", iData.getString("SERVICE_NAME"));
                iData.put("OFFER_CODE", iData.getString("SERVICE_ID"));
                iData.put("OFFER_TYPE", "Z");
                //iData.put("DESCRIPTION", iData.getString("SP_NAME"));
                int price=Integer.parseInt(iData.getString("PRICE"))/1000;
        		iData.put("DESCRIPTION", iData.getString("SP_NAME")+"|"+price+"元/月");

                offers.add(iData);
            }

        }

        return offers;
    }
    
    public IDataset buildProductTagList(IData param) throws Exception
    {
    	String userProductId = param.getString("USER_PRODUCT_ID");
    	IData rst = UpcCall.queryTransProducts(userProductId);
    	IDataset productTags = rst.getDataset("TAGS");
    	
    	IDataset tagValueList = new DatasetList();
		if(IDataUtil.isNotEmpty(productTags))
        {
        	for(int i=0;i<productTags.size();i++)
        	{
        		IData productTag = productTags.getData(i);
        		String catalogName = productTag.getString("CATALOG_NAME");
        		String catalogId = productTag.getString("CATALOG_ID");
        		
        		productTag.put("TAG_VALUE_ID", catalogId);
        		productTag.put("TAG_VALUE_NAME", catalogName);
        		
        		tagValueList.add(productTag);
        	}
        }
		
		IData data = new DataMap();
		data.put("TAG_ID", "1000000008");
		data.put("TAG_NAME", "品牌");
		data.put("TAG_VALUE_LIST", tagValueList);
		
		IDataset result = new DatasetList();
		result.add(data);
		
		return result;
    }
    
    /**
     * 商品订购查询平台标签
     * 
     * @param param
     * @author duhj
     * @return
     * @throws Exception
     */
    public IDataset buildPlatTagList(IData param) throws Exception
    {
		IDataset result = new DatasetList();
		IDataset paltaInfos = new DatasetList();
		IData dataPlat = new DataMap();
		IDataset platsvcDataset = new DatasetList();
		IDataset ids = StaticUtil.getStaticList("BIZ_TYPE_CODE_MERCH");// 没有产商品配合，只能自己玩喽,后续再优化，最好从产商品拿数据
																		// duhj
		if (IDataUtil.isNotEmpty(ids)) {
			for (int i = 0; i < ids.size(); i++) {

				IData tempData = ids.getData(i);
				tempData.put("TAG_VALUE_ID", tempData.getString("DATA_ID"));
				tempData.put("TAG_VALUE_NAME", tempData.getString("DATA_NAME"));
				platsvcDataset.add(tempData);
			}
		}
		IData group = new DataMap();
		group.put("GROUP_NAME", "不限");
		group.put("GROUP_ID", "-1");
		group.put("TAG_VALUE_ID", "-1");
		group.put("TAG_VALUE_NAME", "不限");
		platsvcDataset.add(group);
		IData group2 = new DataMap();
		group2.put("GROUP_NAME", "重点业务");
		group2.put("GROUP_ID", "0");
		group2.put("TAG_VALUE_ID", "0");
		group2.put("TAG_VALUE_NAME", "重点业务");
		platsvcDataset.add(group2);

		dataPlat.put("TAG_ID", "0000000000");
		dataPlat.put("TAG_NAME", "分组");
		dataPlat.put("TAG_VALUE_LIST", platsvcDataset);
		if (IDataUtil.isNotEmpty(platsvcDataset)) {
			DataHelper.sort(platsvcDataset, "TAG_VALUE_ID", IDataset.TYPE_STRING,IDataset.ORDER_ASCEND);
		}

		paltaInfos.add(dataPlat);
		result = paltaInfos;


		return result;
    }
    /**
     * 商品订购查询平台标签
     * 
     * @param param
     * @author guohuan
     * @return
     * @throws Exception
     */
    public IDataset buildHotTagList(IData param) throws Exception
    {
        IDataset result = new DatasetList();
        IDataset hotInfos = new DatasetList();
        IData dataHot = new DataMap();
        IDataset hotTagList = new DatasetList();
        IDataset ids = StaticUtil.getStaticList("HOT_TAG_LISTS_FOR_MERCH");
        if (IDataUtil.isNotEmpty(ids)) {
            for (int i = 0; i < ids.size(); i++) {

                IData tempData = ids.getData(i);
                tempData.put("TAG_VALUE_ID", tempData.getString("DATA_ID"));
                tempData.put("TAG_VALUE_NAME", tempData.getString("DATA_NAME"));
                hotTagList.add(tempData);
            }
        }
        dataHot.put("TAG_ID", "0000000000");
        dataHot.put("TAG_NAME", "热门分类");
        dataHot.put("TAG_VALUE_LIST", hotTagList);
        if (IDataUtil.isNotEmpty(hotTagList)) {
            DataHelper.sort(hotTagList, "TAG_VALUE_ID", IDataset.TYPE_STRING,IDataset.ORDER_ASCEND);
        }

        hotInfos.add(dataHot);
        result = hotInfos;


        return result;
    }

    /**
     * 查询热门商品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getHotOffers(IData data) throws Exception
    {
        IDataset offers = new DatasetList();
        String productId = data.getString("USER_PRODUCT_ID");
        IDataset tagList = new DatasetList(data.getString("TAG_LIST", "[]"));
        String tagId="";
        if (IDataUtil.isNotEmpty(tagList))
        {
            tagId = tagList.getData(0).getString("TAG_VALUE_ID","");
        }
        IDataset CommparaParas = CommparaInfoQry.getCommparaByParaAttr("CSM", "26", "0898");
        if (IDataUtil.isNotEmpty(CommparaParas) && StringUtils.isNotBlank(tagId))
        {
            for (int i = 0; i < CommparaParas.size(); i++)
            {
                IData CommparaPara = CommparaParas.getData(i);
                IData offerDetial = new DataMap();
                String offerCode = CommparaPara.getString("PARAM_CODE", "");
                String offerName = CommparaPara.getString("PARAM_NAME", "");
                String tagValue = CommparaPara.getString("PARA_CODE1", ""); //界面标签分类 001新入网用户，002校园营销，003其他热门（主要配置存量用户的热门商品）
                String campnType = CommparaPara.getString("PARA_CODE2", ""); //营销活动
                String offerType = CommparaPara.getString("PARA_CODE4", ""); //商品类型
                String priorityId = CommparaPara.getString("PARA_CODE3", "");// 排列优先级

                if (StringUtils.equals(tagId, tagValue))
                {
                    if (StringUtils.equals("K", offerType))
                    {
                        IData iData = new DataMap();
                        iData.put("OFFER_NAME", offerName);
                        iData.put("OFFER_CODE", offerCode);
                        iData.put("OFFER_TYPE", offerType);
                        iData.put("DESCRIPTION", offerName);
                        iData.put("CAMPN_TYPE", campnType);
                        iData.put("PRIORITY_ID", priorityId);
                        offers.add(iData);
                    }
                    else
                    {
                        if (StringUtils.equals("S", offerType) || StringUtils.equals("D", offerType))
                        {
                            offerDetial = getOfferDetailByOfferCodeAndOfferType(offerCode, offerType, productId);
                        }
                        else if(StringUtils.equals("P", offerType))
                        {
                            boolean falg = checkProduct(productId, offerCode);
                            if (falg)
                            {
                        		offerDetial = UpcCall.queryOfferByOfferId(offerType, offerCode);
                            }
                            else
                            {
                                CommparaParas.remove(i);
                                i--;
                            }
                        }
                        else
                        {
                        	try
                            {
                                offerDetial = UpcCall.queryOfferByOfferId(offerType, offerCode);
                            }
                            catch (Exception e)
                            {
                                CommparaParas.remove(i);
                                i--;
                            }
                        }
                        if (IDataUtil.isNotEmpty(offerDetial))
                        {
                            offerDetial.put("PRIORITY_ID", priorityId);
                            offers.add(offerDetial);
                        }
                    }
                }
            }
            if (IDataUtil.isNotEmpty(offers))
            {
                DataHelper.sort(offers, "PRIORITY_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
            }
        }
        return offers;
    }
    
    /**
     * 根据主产品ID，查询标签
     * 
     * @param data
     * @author guohuan
     * @return
     * @throws Exception
     */
    public IDataset queryProductChangeListTags(IData param) throws Exception
    {
        String productId = param.getString("PRODUCT_ID");
        if (StringUtils.isBlank(productId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户主产品ID不能为空");
        }
        // 获取主产品下的组
        IDataset offerGroups = UpcCall.queryOfferGroupRelOfferId("P", productId);
        IDataset labels = new DatasetList();
        if (IDataUtil.isNotEmpty(offerGroups))
        {
            for (int i = 0; i < offerGroups.size(); i++)
            {
                IData offerGroup = offerGroups.getData(i);
                String removegroupId = offerGroup.getString("REMOVE_GROUP_ID", "");
                String groupId = offerGroup.getString("GROUP_ID", "");
                String groupName = offerGroup.getString("GROUP_NAME", "");
                String tradetypeCode = offerGroup.getString("TRADE_TYPE_CODE", "");
                String selectFlag = offerGroup.getString("SELECT_FLAG", "");

                if (removegroupId.equals(groupId) && "606".equals(tradetypeCode))
                {
                    offerGroups.remove(i);
                    i--;
                }
                if (("41005405".equals(groupId) || "41005605".equals(groupId)) && !"606".equals(tradetypeCode))
                {
                    offerGroups.remove(i);
                    i--;
                }
                offerGroup.put("LABEL_ID", groupId);
                offerGroup.put("LABEL_TYPE", "1");
                offerGroup.put("LABEL_NAME", groupName);
                offerGroup.put("SELECT_FLAG", selectFlag);
                offerGroup.put("LABEL_HEAD", "0000000000");
            }
        }
        DataHelper.sort(offerGroups, "FORCE_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND, "DEFAULT_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        IDataset labelList = UpcCall.qryAllTagAndTagValueByOfferId(productId, "P");
        if (IDataUtil.isNotEmpty(labelList))
        {
            for (int i = 0; i < labelList.size(); i++)
            {
                IData label = labelList.getData(i);
                String labelId = label.getString("LABEL_ID");
                IDataset labelValueList = label.getDataset("LABEL_KEY_LIST");
                for (int j = 0; j < labelValueList.size(); j++)
                {
                    IData tagValue = new DataMap();
                    IData labelValue = labelValueList.getData(j);
                    String labelKeyId = labelValue.getString("LABEL_KEY_ID");
                    String labelKeyValue = labelValue.getString("LABEL_KEY_VALUE");
                    tagValue.put("LABEL_ID", labelKeyId);
                    tagValue.put("LABEL_NAME", labelKeyValue);
                    tagValue.put("LABEL_HEAD", labelId);
                    tagValue.put("LABEL_TYPE", "2");
                }
            }
        }
        labels.addAll(offerGroups);
        if (IDataUtil.isNotEmpty(labels))
        {
            DataHelper.sort(labels, "LABEL_TYPE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        }
        return labels;
    }

    /**
     * 查询优惠和服务的详细信息
     * 
     * @param data
     * @author guohuan
     * @return
     * @throws Exception
     */
    public IData getOfferDetailByOfferCodeAndOfferType(String offerCode, String offerType, String productId) throws Exception
    {
        IData offer = new DataMap();
        if (StringUtils.isNotBlank(productId))
        {
            Map<String, String> searchData = new HashMap<String, String>();
            SearchResponse resp = new SearchResponse();
            searchData.put("PRODUCT_ID", productId);
            resp = SearchClient.search("PM_OFFER_ELEMENT", offerCode, searchData, 0, 21);
            IDataset datas = resp.getDatas();
            ElementPrivUtil.filterElementListByPriv(BizService.getVisit().getStaffId(), datas);
            if (IDataUtil.isNotEmpty(datas) && datas.size() > 0)
            {
                for (Object obj : datas)
                {
                    IData data = (IData) obj;
                    String tempName = data.getString("ELEMENT_NAME");
                    String tempCode = data.getString("ELEMENT_ID");
                    String tempType = data.getString("ELEMENT_TYPE_CODE");
                    String description = StringUtils.isBlank(data.getString("EXPLAIN")) ? tempName : data.getString("EXPLAIN");
                    if (StringUtils.equals(tempType, offerType) && StringUtils.equals(tempCode, offerCode))
                    {
                        data.put("OFFER_NAME", tempName);
                        data.put("OFFER_CODE", offerCode);
                        data.put("OFFER_TYPE", offerType);
                        data.put("DESCRIPTION", description);
                        data.put("GROUP_ID", data.getString("PACKAGE_ID", "-1"));
                        data.put("ORDER_MODE", data.getString("REORDER"));
                        offer.putAll(data);
                        break;
                    }
                }
            }
        }
        return offer;
    }
    
    /**
     * 根据用户主产品判断用户是否能变更新产品，加入工号权限校验
     * 
     * @param userProductId
     * @param productId
     * @author guohuan
     * @return
     * @throws Exception
     */
    public static boolean checkProduct(String userProductId, String productId) throws Exception
    {
        IData rst = UpcCall.queryTransProducts(userProductId);
        if (IDataUtil.isNotEmpty(rst))
        {
            IDataset offers = rst.getDataset("OFFERS");
            if (IDataUtil.isNotEmpty(offers))
            {
                for (int i = 0; i < offers.size(); i++)
                {
                    IData offer = offers.getData(i);
                    if (StringUtils.equals(productId, offer.getString("OFFER_CODE")))
                    {
                        IDataset result = new DatasetList();
                        offer.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
                        result.add(offer);
                        //员工工号权限过滤
                       return ProductPrivUtil.isProudctPriv(getVisit().getStaffId(), result);
                    }
                }
            }
        }
        return false;
    }
}
