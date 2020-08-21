package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.offer;

import java.util.Iterator;
import java.util.Map;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.pub.consts.IUpcConst;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.pagedata.PageDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class EnterpriseGroupOffers extends BizTempComponent
{
	public abstract void setDiscountFlag(IData discountFlag);
    public abstract void setInfo(IData info);
    public abstract void setOfferList(IDataset offerList);
    public abstract void setGroupList(IDataset groupList);
    
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);

        if (isAjax)
        {
          includeScript(writer, "scripts/iorder/icsserv/component/enterprise/offer/EnterpriseGroupOffers.js", false, false);
        }
        else
        {
          getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/enterprise/offer/EnterpriseGroupOffers.js", false, false);
        }

//        this.getContext().getData()

        IData info = new DataMap();

        info.put("id", StringUtils.isBlank(getId()) ? "groupOffersPopupItem" : getId());

        setInfo(info);
        
        IData pageData = this.getPage().getData();
       


        
        String offerId = pageData.getString("OFFER_ID", "");
        String selOfferIds = pageData.getString("SEL_OFFER_IDS", "");
        String operType = pageData.getString("OPER_TYPE", "");
        String brand = pageData.getString("BRAND_CODE", "");
        String mainOfferId = pageData.getString("MAIN_OFFER_ID", "");
        String isNeedFetchDis = pageData.getString("IS_NEED_FETCH_DIS", "");
        
        String ecUserId = pageData.getString("EC_USER_ID", "");
        String ecOfferId = pageData.getString("EC_OFFER_ID", "");
        String  subOfferString=pageData.getString("USERELEMETS");
        IDataset userSuboffersDataset=new DatasetList();
        if(StringUtils.isNotEmpty(subOfferString)){
        	 userSuboffersDataset=new DatasetList(subOfferString);
        }

        if(StringUtils.isNotBlank(offerId))
        {
            if(BizCtrlType.CreateMember.equals(operType) || BizCtrlType.ChangeMemberDis.equals(operType))
            {
                String useTag = IUpcViewCall.getUseTagByOfferId(ecOfferId);
            	if("1".equals(useTag)){//如果是成员操作定制，需要加载出来集团定制的信息
            		queryMebUseGroupOffersByOfferId(ecUserId,ecOfferId,brand,offerId);
            	}else{//非定制情况也需要将附加产品包全部加载出来
            		queryMebNoUseGroupOffersByOfferId(ecOfferId);
            	}

            }else{
                queryGroupOffersByOfferId(offerId, isNeedFetchDis);
            }

        }

        if(StringUtils.isNotBlank(selOfferIds))
        {
            String effectNow = pageData.getString("EFFECT_NOW", "1");//默认立即 此标记只对订购有效
            buildSubOfferData(selOfferIds, operType, brand, mainOfferId,effectNow,ecOfferId,userSuboffersDataset);
        }
    }
    
    private void queryGroupOffersByOfferId(String offerId, String isNeedFetchDis) throws Exception
    {	
        IDataset groupOffers = IUpcViewCall.queryOfferGroups(offerId, this.getVisit().getLoginEparchyCode());
        
        if(DataUtils.isNotEmpty(groupOffers))
        {
        	String offerCode = IUpcViewCall.getOfferCodeByOfferId(offerId);
            for(int i = 0, sizeI = groupOffers.size(); i < sizeI; i++)
            {
                IData group = groupOffers.getData(i);
                
                IDataset offerList =IUpcViewCall.queryChildOfferByGroupId(group.getString("GROUP_ID"), this.getVisit().getLoginEparchyCode());

                StringBuilder mustSelectOffers = new StringBuilder(500);
                for(int j = 0, sizeJ = offerList.size(); j < sizeJ; j++)
                {
                    String selectFlag = offerList.getData(j).getString("SELECT_FLAG", "");
                    if("0".equals(selectFlag))
                    {
                        mustSelectOffers.append(offerList.getData(j).getString("OFFER_ID")).append("@");
                    }
                }
                if(mustSelectOffers.length() > 0)
                {
                    group.put("MUST_SELECT_OFFERS", mustSelectOffers.substring(0, mustSelectOffers.length()-1).toString());
                }
                
                //工号权限过滤
        		ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), offerList);

                group.put("GROUP_COM_REL_LIST", offerList);
                group.put("OFFER_CODE", offerCode);
                getGroupInfobyOffer(group,offerCode);
                
            }
        }
        setGroupList(groupOffers);
        
        this.getPage().setAjax(groupOffers);
    }
    
    //成员非定制情况
    private void queryMebNoUseGroupOffersByOfferId(String ecOfferId) throws Exception
    {	
    	IDataset mebOffers = IUpcViewCall.queryOfferJoinRelAndOfferByOfferId(ecOfferId, "1", "", "");
    	if(DataUtils.isEmpty(mebOffers)){
    		return;
    	}
    	IDataset allGroupOffers = new DatasetList();
    	for(int a=0,size=mebOffers.size();a<size;a++){
    		String offerId = mebOffers.getData(a).getString("OFFER_ID");
    		String offerCode = mebOffers.getData(a).getString("OFFER_CODE");
            IDataset groups = IUpcViewCall.queryOfferGroups(offerId, this.getVisit().getLoginEparchyCode());
            
            if(DataUtils.isNotEmpty(groups))
            {
                for(int i = 0, sizeI = groups.size(); i < sizeI; i++)
                {
                    IData group = groups.getData(i);
                    
                    IDataset offerList =IUpcViewCall.queryChildOfferByGroupId(group.getString("GROUP_ID"), this.getVisit().getLoginEparchyCode());

                    StringBuilder mustSelectOffers = new StringBuilder(500);
                    for(int j = 0, sizeJ = offerList.size(); j < sizeJ; j++)
                    {
                        String selectFlag = offerList.getData(j).getString("SELECT_FLAG", "");
                        if("0".equals(selectFlag))
                        {
                            mustSelectOffers.append(offerList.getData(j).getString("OFFER_ID")).append("@");
                        }
                    }
                    if(mustSelectOffers.length() > 0)
                    {
                        group.put("MUST_SELECT_OFFERS", mustSelectOffers.substring(0, mustSelectOffers.length()-1).toString());
                    }
                    //工号权限过滤
            		ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), offerList);
            		
            		
            		String type_id = "CHANGE_OFFER_BEGIN_END";
                    IData param = new DataMap();
           		  	param.put("TYPE_ID", type_id);
           		  	param.put("PDATA_ID", offerId);
           		  	param.put("OFFER_DATA",offerList);
           		  	
           		    IDataset returnData =CSViewCall.call(this, "CS.StaticInfoQrySVC.getStaticValueByPDType", param);
                     
                    
            		group.put("GROUP_COM_REL_LIST", returnData);
                    group.put("OFFER_CODE", offerCode);
                    getGroupInfobyOffer(group ,offerCode);
                    
                }
                allGroupOffers.addAll(groups);
            }

    	}

        setGroupList(allGroupOffers);
        this.getPage().setAjax(allGroupOffers);
    }
    
    //成员定制情况
    private void queryMebUseGroupOffersByOfferId(String ecUserId,String ecOfferId, String brand,String offerId) throws Exception
    {	
    	//根据集团用户编码查询已定制信息
        IData input = new DataMap();
        input.put("USER_ID", ecUserId);
        IDataset userGrpPackages = CSViewCall.call(this, "CS.UserGrpPkgInfoQrySVC.getUserGrpPackageForGrp", input);
        
    	if(DataUtils.isEmpty(userGrpPackages)){
    		return;
    	}
    	IDataset allGroupOffers = new DatasetList();
    	IData groupMap = new DataMap();
    	IData offerGroupComRels = new DataMap();
    	
    	//循环定制信息，分组
    	for(int a=0,size=userGrpPackages.size();a<size;a++){
    		IData userPackage = userGrpPackages.getData(a);
    		String groupId = userPackage.getString("PACKAGE_ID","");
    		String elementId = userPackage.getString("ELEMENT_ID");
    		String elementType = userPackage.getString("ELEMENT_TYPE_CODE");
    		String productId = userPackage.getString("PRODUCT_ID");
    		
    		boolean flag = false;
    		if(brand.equals("BOSG")){
	    		for(int k=0; k<a; k++){
	    			if(groupId.equals(userGrpPackages.getData(k).getString("PACKAGE_ID"))){
	    				flag = true;
	    			}
	    		}
    		}
    		if(flag){
    			continue;
    		}
                		
    		IData group = new DataMap();
    		IDataset offerList = new  DatasetList();
    		StringBuilder mustSelectOffers = new StringBuilder(500);
    		if(groupMap.containsKey(groupId)){
    			group = groupMap.getData(groupId);
    			offerList = group.getDataset("GROUP_COM_REL_LIST");
    			mustSelectOffers = mustSelectOffers.append(group.getString("MUST_SELECT_OFFERS",""));
    		}else{
    			IDataset groups = IUpcViewCall.queryGroupByCond(groupId, "");
    			if(DataUtils.isNotEmpty(groups)){
    				group = groups.first();
    			}
    		}

    		IDataset childOffers = offerGroupComRels.getDataset(groupId);
    		
    		IDataset childOffer = new DatasetList();
    		if(IDataUtil.isNotEmpty(childOffers))
    		{
    			if(brand.equals("BOSG")){
        			childOffer = DataHelper.filter(childOffers, "OFFER_TYPE="+elementType);
    			}else{
    				childOffer = DataHelper.filter(childOffers, "OFFER_CODE="+elementId+",OFFER_TYPE="+elementType);
    			}
    		}else
    		{
    			childOffers =IUpcViewCall.queryChildOfferByGroupId(groupId, this.getVisit().getLoginEparchyCode());
    			if(IDataUtil.isEmpty(childOffers))
    				continue ;
    			
    			offerGroupComRels.put(groupId, childOffers);
    			if(brand.equals("BOSG")){
        			childOffer = DataHelper.filter(childOffers, "OFFER_TYPE="+elementType);
    			}else{
    				childOffer = DataHelper.filter(childOffers, "OFFER_CODE="+elementId+",OFFER_TYPE="+elementType);
    			}    			
    		}
    		
    		if(DataUtils.isEmpty(childOffer)){
    			continue;
    		}
    		String selectFlag = "";
    		if(brand.equals("BOSG")){
    			for(int i=0; i<childOffer.size(); i++){
            		selectFlag= childOffer.getData(i).getString("SELECT_FLAG", "");
                    if("0".equals(selectFlag))
                    {
                        mustSelectOffers.append(childOffer.first().getString("OFFER_ID")).append("@");
                    }
                    
            		offerList.add(childOffer.getData(i));
    				
    			}

    		}else{
    			
        		selectFlag= childOffer.first().getString("SELECT_FLAG", "");
                if("0".equals(selectFlag))
                {
                    mustSelectOffers.append(childOffer.first().getString("OFFER_ID")).append("@");
                }
    			
        		offerList.add(childOffer.first());

    		}
    	

            if(mustSelectOffers.length() > 0)
            {
                group.put("MUST_SELECT_OFFERS", mustSelectOffers.substring(0, mustSelectOffers.length()-1).toString());
            }
            //工号权限过滤
            ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), offerList);
            
            
            String type_id = "CHANGE_OFFER_BEGIN_END";
            IData param = new DataMap();
  		  	param.put("TYPE_ID", type_id);
  		  	param.put("PDATA_ID", offerId);
  		  	param.put("OFFER_DATA",offerList);
  		  	
  		    IDataset returnData =CSViewCall.call(this, "CS.StaticInfoQrySVC.getStaticValueByPDType", param);
            
    		group.put("GROUP_COM_REL_LIST", returnData);
    		
    		
    		
    		group.put("OFFER_CODE", productId);
    		getGroupInfobyOffer(group ,productId);
    		groupMap.put(groupId,group);
    	}
    	
    	
//    	//如果是服务包，加到结果集里
//    	IDataset groups = IUpcViewCall.queryMebGroupByOfferId(ecOfferId,this.getVisit().getLoginEparchyCode());
//        if(DataUtils.isNotEmpty(groups))
//        {
//            for(int i = 0, sizeI = groups.size(); i < sizeI; i++)
//            {
//                IData group = groups.getData(i);
//                String groupType = group.getString("GROUP_TYPE","");
//                
//                if(!"1".equals(groupType)){//如果服务包则显示
//                	continue;
//                }
//                IDataset offerList =IUpcViewCall.queryChildOfferByGroupId(group.getString("GROUP_ID"), this.getVisit().getLoginEparchyCode());
//
//                StringBuilder mustSelectOffers = new StringBuilder(500);
//                for(int j = 0, sizeJ = offerList.size(); j < sizeJ; j++)
//                {
//                    String selectFlag = offerList.getData(j).getString("SELECT_FLAG", "");
//                    if("0".equals(selectFlag))
//                    {
//                        mustSelectOffers.append(offerList.getData(j).getString("OFFER_ID")).append("@");
//                    }
//                }
//                if(mustSelectOffers.length() > 0)
//                {
//                    group.put("MUST_SELECT_OFFERS", mustSelectOffers.substring(0, mustSelectOffers.length()-1).toString());
//                }
//                group.put("GROUP_COM_REL_LIST", offerList);
//                group.put("OFFER_CODE", group.getString("OFFER_CODE",""));
//                allGroupOffers.add(group);
//            }
//
//        }
    	
    	Iterator itr = groupMap.keySet().iterator();
        while (itr.hasNext())
        {
            String key = itr.next().toString();
            IData value = groupMap.getData(key);
            
            allGroupOffers.add(value);
        }
        
       
        setGroupList(allGroupOffers);

        
        this.getPage().setAjax(allGroupOffers);
    }
    
    private void buildSubOfferData(String selOfferIds, String operType, String brand, String mainOfferId,String effectNow,String useTag,IDataset userSuboffersDataset) throws Exception
    {
        IDataset subOfferDataset = new DatasetList();
        
        //查询商品构成以及必选包的必选元素
        IDataset mustSelSubOffers = IUpcViewCall.queryOfferComRelOfferByOfferId(mainOfferId, this.getVisit().getLoginEparchyCode());
        
        String[] offerIds = selOfferIds.substring(1).split("@");
        for(int i = 0, sizeI = offerIds.length; i < sizeI; i++)
        {//offerIds[i] 格式：OFFER_CODE#GROUP_ID#REL_OFFER_CODE  REL_OFFER_CODE归属产品（存在附加产品情况）
            String[] offerIdGroupIdArr = offerIds[i].split("#");
            IData offer = IUpcViewCall.queryOfferByOfferId(offerIdGroupIdArr[0], null);
            if(DataUtils.isEmpty(offer))
            {
                continue;
            }
            String offerId = offer.getString("OFFER_ID");
            String offerType = offer.getString("OFFER_TYPE");
            IData subOfferData = new DataMap();
            subOfferData.put("OFFER_ID", offerId);
            subOfferData.put("OFFER_CODE", offer.getString("OFFER_CODE"));
            subOfferData.put("OFFER_NAME", offer.getString("OFFER_NAME"));
            subOfferData.put("OFFER_TYPE", offer.getString("OFFER_TYPE"));
            subOfferData.put("OFFER_INDEX", "0");
            subOfferData.put("BRAND_CODE", ""); //子商品brand为空
            subOfferData.put("GROUP_ID", offerIdGroupIdArr[1]); //商品组id
            subOfferData.put("OPER_CODE", PageDataTrans.ACTION_CREATE);
            subOfferData.put("DESCRIPTION", offer.getString("DESCRIPTION"));
            subOfferData.put("REL_OFFER_CODE", offerIdGroupIdArr[2]);
            String relOfferId = IUpcViewCall.getOfferIdByOfferCode(offerIdGroupIdArr[2]);
            
            dealElementDate(subOfferData, relOfferId,offerIdGroupIdArr[1],operType,effectNow,userSuboffersDataset);

            if(IUpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(offerType))
            {//商品类型为资费，需查询商品默认生失效时间以及是否有弹性资费
                //标识构成关系
                boolean forceTag = false;
                for(int j = 0, sizeJ = mustSelSubOffers.size(); j < sizeJ; j++)
                {
                    if(offerId.equals(mustSelSubOffers.getData(j).getString("OFFER_ID")))
                    {//标识必选子商品（构成关系或者必选包的必选元素）
                        forceTag = true;
                        break;
                    }
                }
                subOfferData.put("FORCE_TAG", forceTag);
                
                subOfferData.put("P_OFFER_INDEX", i);
                
                if("R".equals(offer.getString("ORDER_MODE")) || "C".equals(offer.getString("ORDER_MODE"))){
                    subOfferData.put("REPEAT_ORDER", true);
                }
                
                //海南这边资费都没有单独新表的配置,所以不需要加读取新表配置逻辑
                IDataset offerChas = IUpcViewCall.queryChaByOfferId(offerId);
                subOfferData.put("HAS_PRICE_CHA", DataUtils.isNotEmpty(offerChas) ? true : false);
                
            }
            else
            {//非资费类型的商品，需查询其下必选子商品
                IDataset subOffers = IUpcViewCall.queryOfferComRelOfferByOfferId(offerId, this.getVisit().getLoginEparchyCode());

                if(DataUtils.isNotEmpty(subOffers))
                {
                    IDataset subSubOfferDataset = new DatasetList();
                    for(int j = 0, sizeJ = subOffers.size(); j < sizeJ; j++)
                    {
                        IData subOffer = subOffers.getData(j);
                        IData subSubOffer = new DataMap();
                        subSubOffer.put("OFFER_ID", subOffer.getString("OFFER_ID"));
                        subSubOffer.put("OFFER_NAME", subOffer.getString("OFFER_NAME"));
                        subSubOffer.put("OFFER_TYPE", subOffer.getString("OFFER_TYPE"));
                        subSubOffer.put("BRAND_CODE", subOffer.getString("BRAND_CODE", ""));
                        subSubOffer.put("OPER_CODE", PageDataTrans.ACTION_CREATE);
                        subSubOfferDataset.add(subSubOffer);
                    }
                    if(DataUtils.isNotEmpty(subSubOfferDataset))
                    {
                        subOfferData.put("SUBOFFERS", subSubOfferDataset);
                    }
                }
                //判断是否存在参数，是否展示待设置 liuzz
                boolean hasOfferCha = CommonViewCall.hasOfferSpecCha(this, operType, EcConstants.MERCH_STATUS.MERCH_ADD.getValue(), offerId, brand);
                subOfferData.put("IS_SHOW_SET_TAG", hasOfferCha);
            }
            subOfferDataset.add(subOfferData);
        }
        this.getPage().setAjax(subOfferDataset);
    }
    
    private IData parseMapToData(Map map) throws Exception
    {
        IData data = new DataMap();
        
        Iterator itr = map.keySet().iterator();
        while (itr.hasNext())
        {
            String key = itr.next().toString();
            String value = map.get(key).toString();
            data.put(key, value);
        }
        
        return data;
    }
    
    public void  getGroupInfobyOffer(IData groupData ,String productId) throws Exception{
    	IDataset groupInfoDataset=IUpcViewCall.queryOfferGroupsByProductId(productId, this.getVisit().getLoginEparchyCode());
    	if(IDataUtil.isEmpty(groupInfoDataset)){
    		return;
    	}
    	for(int i=0,sizeI=groupInfoDataset.size();i<sizeI;i++){
    		IData groupInfoData=groupInfoDataset.getData(i);
    		if(groupInfoData.getString("GROUP_ID").equals(groupData.getString("GROUP_ID"))){
    			groupData.put("MIN_NUM", groupInfoData.getString("MIN_NUM"));
    			groupData.put("MAX_NUM", groupInfoData.getString("MAX_NUM"));
    			groupData.put("SELECT_FLAG", groupInfoData.getString("SELECT_FLAG"));
    		}
    	}
    }
    
	/**
	 * 获取计算元素时间
	 * 
	 */
	public void dealElementDate(IData subOffer,String relOfferId,String groupId,String operType,String effectNow,IDataset userSuboffersDataset) throws Exception
	{
		String subOfferCode = subOffer.getString("OFFER_CODE");
        String validDate = "";
        if(BizCtrlType.CreateMember.equals(operType) || BizCtrlType.CreateUser.equals(operType)){
            if("0".equals(effectNow)){
            	validDate = SysDateMgr4Web.getFirstDayOfNextMonth();
            }else{
            	validDate = SysDateMgr4Web.getSysTime();
            }
    	}
        IData offerData = IUpcViewCall.queryElementEnableMode("P", subOffer.getString("REL_OFFER_CODE"), groupId, subOffer.getString("OFFER_CODE"),  subOffer.getString("OFFER_TYPE"));
        if(DataUtils.isNotEmpty(offerData))
        {
        	  // 1. TD_B_PRODUCT_PACKAGE 表中的RSRV_STR1 = 1 时的逻辑处理,如果不是第一次订购包内的元素，则元素下账期生效
        	IData param=new DataMap();
        	param.put("PRODUCT_ID", subOffer.getString("REL_OFFER_CODE"));
        	param.put("EPARCHY_CODE", getVisit().getLoginEparchyCode());
        	param.put("PACKAGE_ID", groupId);

        	IDataset productPackageData = CSViewCall.call(this, "CS.ProductPkgInfoQrySVC.getProductPackageRelNoPriv", param);
            //IData productPackageData = ProductPkgInfoQry.getProductPackageRelNoPriv(subOfferCode, groupId, getVisit().getLoginEparchyCode());
            if (IDataUtil.isNotEmpty(productPackageData))
            {
                String packageStr1 = productPackageData.getData(0).getString("PROD_PACK_REL_STR1", "");
                if (packageStr1.equals("1"))
                {
                    if (!ifFirstAddWithPack(userSuboffersDataset, groupId, subOffer.getString("OFFER_TYPE", "")))
                    	offerData.put("ENABLE_MODE","1");
                }
            }
            
        	//取配置 计算生失效时间
            if(StringUtils.isEmpty(validDate)){
                validDate = SysDateMgr4Web.startDate(offerData.getString("ENABLE_MODE"), offerData.getString("ABSOLUTE_ENABLE_DATE"), offerData.getString("ENABLE_OFFSET"), offerData.getString("ENABLE_UNIT"));

            }
            subOffer.put("START_DATE", validDate);
            subOffer.put("END_DATE", SysDateMgr4Web.endDate(validDate, offerData.getString("DISABLE_MODE"), offerData.getString("ABSOLUTE_DISABLE_DATE"), offerData.getString("DISABLE_OFFSET"), offerData.getString("DISABLE_UNIT")));
            if ("3".equals(offerData.getString("ENABLE_MODE")) && StringUtils.isBlank(offerData.getString("ABSOLUTE_ENABLE_DATE")))
			{
            	subOffer.put("CHOICE_START_DATE", true);
			}
			//判断用户是否有修改优惠截止日期的权限
			boolean flag = false;
            IData para = new DataMap();
            para.put("SUBSYS_CODE", "CGM");
            para.put("PARAM_ATTR", "9991");
            para.put("PARAM_CODE", "SYS_CRM_DISCNTDATECHGONLY");
            para.put("EPARCHY_CODE", this.getVisit().getLoginEparchyCode());
            IDataset commparaInfoList = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommpara", para);
            if(IDataUtil.isNotEmpty(commparaInfoList)){
				int size1 = commparaInfoList.size();					
				for(int j = 0; j < size1; j++){
					if(subOfferCode.equals(commparaInfoList.getData(j).getString("PARA_CODE1"))){
						if(StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "SYS_CRM_DISCNTDATECHGONLY")){
							flag = true;
							break;
						}
					}
				}
			}
			if ("2".equals(offerData.getString("DISABLE_MODE")) || StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "SYS_CRM_DISCNTDATECHG") || flag)
			{
				subOffer.put("SELF_END_DATE", true);// 自选时间
			}
        }else{
        	if(StringUtils.isEmpty(validDate)){
                validDate = SysDateMgr4Web.getSysTime();
            }
        	subOffer.put("START_DATE", validDate);
        	subOffer.put("END_DATE", SysDateMgr4Web.END_DATE_FOREVER);
        } 
	}
	
	 public static boolean ifFirstAddWithPack(IDataset userElements, String packageId, String elementTypeCode) throws Exception
	    {
	        boolean ifFirstAddEle = true;
	        if (IDataUtil.isNotEmpty(userElements))
	        {
	            int elesSize = userElements.size();
	            for (int i = 0; i < elesSize; i++)
	            {
	                IData tempUserEle = userElements.getData(i);
	                String tempPackageId = tempUserEle.getString("GROUP_ID", "");
	                String tempModifyTag = tempUserEle.getString("OPER_CODE", "");
	                String tempElementTypeCode = tempUserEle.getString("OFFER_TYPE", "");
	                if (tempPackageId.equals(packageId) && tempElementTypeCode.equals(elementTypeCode) && !tempModifyTag.equals("0_1") && !tempModifyTag.equals("0"))
	                {
	                    ifFirstAddEle = false;
	                    break;
	                }
	            }
	        }

	        return ifFirstAddEle;
	    } 
	 
	 
	 
}
