package com.asiainfo.veris.crm.iorder.web.merch.component.merchandise;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public class ItemHandler extends CSBizHttpHandler
{
	public void render() throws Exception
	{
		IData data = this.getData();
		String svcName = data.getString("CALL_SVC", "");
		if ("".equals(svcName))
		{
			return;
		}
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
		IDataset result = CSViewCall.call(this, svcName, data);
		IDataset selectedElements = result.first().getDataset("SELECTED_ELEMENTS");
		String bookingDatePriv = "FALSE";
		// 是否具备选择预约时间的权限
        if (StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "PROD_BOOKING_DATE"))
        {
        	bookingDatePriv = "TRUE";
        }
        
        IDataset userInfos = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserInfoByUserId", data);
        data.put("SERIAL_NUMBER", userInfos.getData(0).getString("SERIAL_NUMBER"));
        IDataset shoppingElements = CSViewCall.call(this, "SS.MerchShoppingCartSVC.getShoppingCartAllElements", data);
		for (int j = 0; j < selectedElements.size(); j++)
		{
			IData element = selectedElements.getData(j);
			element.put("ITEM_INDEX", j);
			element.put("BOOKING_DATE_PRIV", bookingDatePriv);
			
			if(IDataUtil.isNotEmpty(shoppingElements)) 
			{
				for(int i=0;i<shoppingElements.size();i++) 
				{
					IData offer = shoppingElements.getData(i);
					String offerCode = offer.getString("OFFER_CODE");
					String offerType = offer.getString("OFFER_TYPE");
					//selectedElements有商品在购物车中，以购物车为准
					if(StringUtils.equals(element.getString("ELEMENT_ID"), offerCode) && StringUtils.equals(element.getString("ELEMENT_TYPE_CODE"), offerType))
					{
						element.put("MODIFY_TAG", offer.getString("MODIFY_TAG"));
						element.put("SHOPPING_TAG", "1");
						break;
					}
				}
			}
		}
		
		this.setAjax(result);
	}

	public void dispatch() throws Exception
	{
		IData data = this.getData();
		String svcName = data.getString("CALL_SVC", "");
		if ("".equals(svcName))
		{
			return;
		}
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
		IDataset result = CSViewCall.call(this, svcName, data);
		this.setAjax(result);
	}

	public void checkAttr() throws Exception
	{
		IData data = this.getData();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
		data.put("ACTION_TYPE", "*");
		data.put("TRADE_OR_HANDLE", "TRADE");// 这里限制报错类型
		IDataset result = CSViewCall.call(this, "CS.ProductComponentSVC.ProductCheckAttr", data);
		this.setAjax(result);
	}
	
	/**
     * 根据标签，元素类型查询元素列表
     * @param data
     * @author guohuan 
     * @return
     * @throws Exception
     */
    public void queryProductChangeListTagsAndoffers() throws Exception
    {
        IData param = this.getData();
        String eparchyCode = param.getString("EPARCHY_CODE");
        IDataset eles = new DatasetList(param.getString("SELECTED_ELEMENTS"));
        
        IData result = new DataMap();
        String productId = param.getString("PRODUCT_ID");
//        String productId = "10007614";
        
        if (StringUtils.isBlank(productId))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "用户主产品ID不能为空");
        }
        // 获取主产品下的组
        IDataset offerGroups = UpcViewCall.queryOfferGroups(this, productId);
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
        IDataset labelList = UpcViewCall.qryAllTagAndTagValueByOfferId(this, productId, "P");
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
                    tagValue.put("LABEL_TYPE", "2");//默认除group（1）外其他都是2
                    tagValue.put("LABEL_NAME", labelKeyValue);
                    tagValue.put("LABEL_HEAD", labelId);
                    
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
//        offers.addAll(offerList);
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
        this.setAjax(result);
    }
    
    private IDataset switchGroup(IData data) throws Exception{
        IDataset offerList = new DatasetList();
        String serviceName = data.getString("SERVICE_NAME");
        String groupId = data.getString("TAG_VALUE_ID");
        String lableType = data.getString("LABEL_TYPE");
        String page = data.getString("page","");
        String menu = data.getString("m","");
        //System.out.println("==========OfferListHandler=========="+data);
        //不限的情况 查询joinrel
        if(StringUtils.equals(groupId, "-1"))
        {  
            return switchJoinRel(data);
        }
        
        if(StringUtils.isBlank(serviceName)){
            offerList = UpcViewCall.queryGroupComRelOffer(this, groupId);
        }
        else{
            offerList = CSViewCall.call(this, serviceName, data);
        }
        
        ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), offerList);
        DataHelper.sort(offerList, "OFFER_TYPE",IDataset.TYPE_STRING,IDataset.ORDER_DESCEND,"OFFER_CODE", IDataset.TYPE_INTEGER,IDataset.ORDER_ASCEND);
     
        for (Iterator iterator = offerList.iterator(); iterator.hasNext();)
        {
            IData item = (IData) iterator.next();
            
            String offerType = StaticUtil.getStaticValue("OFFER_LIST_NODISPLAY", item.getString("OFFER_CODE"));
            
            if (item.getString("OFFER_TYPE","").equals(offerType) && !"crm9B21".equals(menu) && !"crm9B11".equals(menu) 
                    && !"KDRH001".equals(menu) && !"crmN100".equals(menu) && !"crmNB11".equals(menu) && !"crmNB21".equals(menu) )
            {
                iterator.remove();
            } 
            item.put("LABEL_ID", groupId);
            item.put("GROUP_ID", groupId);
            item.put("LABEL_TYPE", lableType);
        }
        return offerList;
    }
    
    private IDataset switchLable(IData data) throws Exception{
        String productId = data.getString("PRODUCT_ID");
        String category = data.getString("CATEGORY_ID");
        String labelId = data.getString("TAG_ID");
        String labelKeyId = data.getString("TAG_VALUE_ID");
        String lableType = data.getString("LABEL_TYPE");
        IDataset offerList = new DatasetList();
        IDataset result = new DatasetList();
        result = UpcViewCall.qryOfferByTagInfo(this, productId, "P", labelId, labelKeyId, category);
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
                        offerElement.put("LABEL_ID",labelKeyId );
                        offerElement.put("GROUP_ID", labelKeyId);
                        offerElement.put("LABEL_TYPE",lableType );
                    }
//                    offerList = offers;
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
            categorys = UpcViewCall.queryOffersByMultiCategory(this, productId, data.getString("EPARCHY_CODE"), categoryId, "2");
        }
        else
        {
            categorys = CSViewCall.call(this, joinServiceName, data);
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

	private IData dealWideOffers(IData data, IDataset offers, IDataset labels) throws Exception
	{
		IData dealResult = new DataMap();
		if (data.containsKey("WIDE_SPEED"))// 宽带业务才会有这个参数
		{
			if (StringUtils.isBlank(data.getString("WIDE_SPEED")))
			{
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "请先选择宽带速率！");
			}
			else
			{
				data.put("OFFERS", offers);
				offers = CSViewCall.call(this, data.getString("CALL_SVC", "SS.WidenetItemListSVC.filterWideOffers"), data);
				labels = filterLabelsByOfferS(labels, offers);
			}
		}
		dealResult.put("OFFERS", offers);
		dealResult.put("LABELS", labels);
		return dealResult;
	}

	private IDataset filterLabelsByOfferS(IDataset labels, IDataset offers)
	{
		for (int i = 0; i < labels.size(); i++)
		{
			IData label = labels.getData(i);

			if (StringUtils.equals("-1", label.getString("LABEL_TYPE")))
			{
				continue;
			}
			String key = "-1";
			switch (label.getString("LABEL_TYPE").charAt(0))
			{
			case '1':
				key = "GROUP_ID";
				break;
			case '2':
				key = "CATEGORY_ID";
				break;
			case '3':
				key = "TAG_ID";
				break;
			}
			boolean isFoundKey = false;
			for (int j = 0; j < offers.size(); j++)
			{
				IData offer = offers.getData(j);
				if (StringUtils.isNotBlank(offer.getString(key)) && StringUtils.equals(offer.getString(key), label.getString("LABEL_ID")))
				{
					isFoundKey = true;
					break;
				}
			}
			if (!isFoundKey)
			{
				labels.remove(i--);
			}
		}
		return labels;
	}
}