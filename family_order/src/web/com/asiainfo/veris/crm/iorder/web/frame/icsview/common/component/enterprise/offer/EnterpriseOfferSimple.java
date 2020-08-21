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
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class EnterpriseOfferSimple extends BizTempComponent
{
    public abstract void setGroups(IDataset groups);

    public abstract void setChildOffers(IDataset childOffers);

    public abstract void setForceOffers(IDataset forceOffers);
    
    public abstract void setMainOfferId(String mainOfferId);//当前设置的商品编码
    
    public abstract String getProductId();//产品编码
    
    public abstract String getTradeId();//预受理订单编码
    
    public abstract String getUserId();//用户编码
    
    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

        // 添加js
        String js1 = "scripts/iorder/icsserv/component/enterprise/offer/EnterpriseOfferSimple.js";
        boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax) {
            includeScript(writer, js1, false, false);
        } else {
            getPage().addResAfterBodyBegin(js1, false, false);
        }
        IData param = getPage().getData();
        String action = param.getString("ACTION");
        if("getChildOffers".equals(action)){
        	queryChildOffers(param.getString("PACKAGE_ID"));
        }else if("getChildOfferDetail".equals(action)){
        	getChildOfferDetail(param.getString("PACKAGE_ID"),param.getString("OFFER_ID"),param.getString("MAIN_OFFER_ID"));
        }else{
        	queryPackageElementList(param);
        }
    }

    // 查询成员信息
    public void queryPackageElementList(IData param) throws Exception
    {
        String productId = param.getString("PRODUCT_ID");
        String userId = param.getString("USER_ID");
        //根据产品编码查询所有包
		IDataset packageInfos = IUpcViewCall.queryOfferGroupsByProductId(productId, this.getVisit().getLoginEparchyCode());
		
		IDataset packageInfosEff = new DatasetList();
		for(int i=0;i<packageInfos.size();i++)
		{
			IData packageInfo =packageInfos.getData(i);
			String packageName = packageInfo.getString("PACKAGE_NAME");
			if(StringUtils.isNotEmpty(packageName))
			{
				if(packageName.indexOf("已失效")==-1)
				{
					String value = packageInfo.getString("PACKAGE_ID")
							+","+packageInfo.getString("MIN_NUM")
							+","+packageInfo.getString("MAX_NUM")
							+","+packageInfo.getString("FORCE_TAG");
					packageInfo.put("PACKAGE_VAL", value);
					packageInfosEff.add(packageInfo);
				}
			}
		}
		setGroups(packageInfosEff);

		if(DataUtils.isNotEmpty(packageInfosEff)){
			//查询第一包下面的元素，初始化资费下拉框选项
			queryChildOffers(packageInfosEff.first().getString("PACKAGE_ID"));
		}
		
		IData mainOffer = UpcViewCall.queryOfferByOfferId(this, "P", productId, "");
		String mainOfferId = mainOffer.getString("OFFER_ID");
		setMainOfferId(mainOfferId);
		IDataset returnOffers = new DatasetList();
		if(StringUtils.isEmpty(userId)){
			//查询必选资费服务
			IDataset forceElement = IUpcViewCall.queryOfferComRelOfferByOfferId("P", productId, this.getVisit().getLoginEparchyCode());
			
			if(DataUtils.isEmpty(forceElement)){
				return;
			}
			
			for(int i=0;i<forceElement.size();i++){
				
				forceElement.getData(i).put("FORCE_TAG", "1");
				returnOffers.add(initOfferOtherInfo(forceElement.getData(i),mainOfferId));
			}
			setForceOffers(returnOffers);
		}else{
			//查询已经订购的服务资费
			IData input = new DataMap();
			input.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));//路由
			input.put("PRODUCT_ID", productId);
			input.put("USER_ID", userId);
			IDataset selectedElements = CSViewCall.call(this,"CS.UserSvcInfoQrySVC.getElementFromPackageByUser",param);
			if(DataUtils.isEmpty(selectedElements)){
				return;
			}
			
			for(int i=0;i<selectedElements.size();i++){
				returnOffers.add(initOfferOtherInfo(selectedElements.getData(i),mainOfferId));
			}
			setForceOffers(returnOffers);
		}
   }
    
    public IData initOfferOtherInfo(IData offer,String mainOfferId) throws Exception
    {
    	IData returnOffer = new DataMap();
    	String userId = offer.getString("USER_ID");
    	if(StringUtils.isEmpty(userId)){
        	returnOffer.put("OFFER_ID", offer.getString("OFFER_ID"));		
        	returnOffer.put("OFFER_CODE",offer.getString("OFFER_CODE"));
        	returnOffer.put("OFFER_TYPE", offer.getString("OFFER_TYPE"));
        	returnOffer.put("OFFER_NAME", offer.getString("OFFER_NAME"));		
        	returnOffer.put("GROUP_ID", offer.getString("GROUP_ID"));
        	returnOffer.put("FORCE_TAG", offer.getString("FORCE_TAG",UpcConst.getForceTagForSelectFlag(offer.getString("SELECT_FLAG"))));
        	returnOffer.put("START_DATE", SysDateMgr.getSysTime());		
        	returnOffer.put("END_DATE", "2050-12-31 23:59:59");
        	returnOffer.put("OFER_CODE", "0");
    	}else{
    		IData elementOffer = UpcViewCall.queryOfferByOfferId(this, offer.getString("ELEMENT_TYPE_CODE"), offer.getString("ELEMENT_ID"), "");
    		IDataset offerGroupInfo = IUpcViewCall.queryOfferByOfferIdGroupIdRelOfferIdType(mainOfferId, elementOffer.getString("OFFER_ID"), "", "", this.getVisit().getLoginEparchyCode());

        	returnOffer.put("OFFER_ID", elementOffer.getString("OFFER_ID"));		
        	returnOffer.put("OFFER_CODE",elementOffer.getString("OFFER_CODE"));
        	returnOffer.put("OFFER_TYPE", elementOffer.getString("OFFER_TYPE"));
        	returnOffer.put("OFFER_NAME", elementOffer.getString("OFFER_NAME"));		
        	returnOffer.put("GROUP_ID", offerGroupInfo.first().getString("GROUP_ID"));
        	returnOffer.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(offerGroupInfo.first().getString("SELECT_FLAG")));
    		returnOffer.put("OFFER_INS_ID", offer.getString("INST_ID"));
        	returnOffer.put("START_DATE", offer.getString("START_DATE"));		
        	returnOffer.put("END_DATE", offer.getString("END_DATE"));
        	returnOffer.put("OFER_CODE", "3");
        	returnOffer.put("USER_ID", userId);
    	}

    	
		IData inParam = new DataMap();
		inParam.put("ID", offer.getString("OFFER_CODE"));
		inParam.put("ID_TYPE", offer.getString("OFFER_TYPE"));
		inParam.put("EPARCHY_CODE", this.getVisit().getLoginEparchyCode());
		inParam.put("ATTR_OBJ", "0");
		IDataset elementAttr = CSViewCall.call(this,"CS.AttrItemInfoQrySVC.getAttrItemAByIDTO",inParam);
		if(elementAttr != null && elementAttr.size()!=0)
		{
			returnOffer.put("EXT_FLAG", "true");
			//returnOffer.put("EXT_VALUE", elementAttr.size());
		}else
		{
			returnOffer.put("EXT_FLAG", "false");
		}
		return returnOffer;
    }
    
    
    public void queryChildOffers(String packageId) throws Exception
    {
        IData inParam = new DataMap();

		inParam.put("PACKAGE_ID",packageId);
		inParam.put("ROUTE_EPARCHY_CODE",this.getVisit().getLoginEparchyCode());
		inParam.put("STAFF_ID", getVisit().getStaffId());
		IDataset elementsInfo = CSViewCall.call(this,"CS.PackageSVC.getPackageElementsESOP",inParam);
		setChildOffers(elementsInfo);
    }
    
    public void getChildOfferDetail(String packageId,String offerId,String mainOfferId) throws Exception
    {
		IDataset elementsInfo =IUpcViewCall.queryOfferDetailByGroupIdOfferId(packageId, offerId);
		
		this.getPage().setAjax(initOfferOtherInfo(elementsInfo.first(),mainOfferId));
    }
}
