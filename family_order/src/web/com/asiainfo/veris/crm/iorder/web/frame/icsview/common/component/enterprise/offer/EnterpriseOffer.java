package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.offer;

import java.util.Iterator;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.util.TimeUtil;
import com.ailk.biz.view.BizTempComponent;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
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
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productpackage.ProductPkgInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class EnterpriseOffer extends BizTempComponent
{
    public abstract void setInfo(IData info);
    public abstract void setServiceOfferList(IDataset serviceOfferList);
    public abstract void setPriceOfferList(IDataset priceOfferList);
    public abstract void setProductOfferList(IDataset productOfferList);
    public abstract String getShowMode();
    public abstract String getUtilTag();
    public abstract void setProPackages(IDataset proPackages);
    public abstract void setInitBbossOffer(IData initBbossOffer);
    public abstract void setForcePkgList(IDataset forcePkgList);

    


    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);

        if (isAjax)
        {
            includeScript(writer, "scripts/iorder/icsserv/component/enterprise/offer/EnterpriseOffer.js", false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/enterprise/offer/EnterpriseOffer.js", false, false);
        }

        IData info = new DataMap();

        info.put("id", StringUtils.isBlank(getId()) ? "enterpriseOfferPopupItem" : getId());
        
        info.putAll(parseShowMode());
        
        
        IData pageData = this.getPage().getData();
        String offerId = pageData.getString("OFFER_ID", "");
        String ecOfferId = pageData.getString("EC_OFFER_ID", "");
        String effectNow = pageData.getString("EFFECT_NOW", "1");//默认立即 此标记只对订购有效
        
        if(StringUtils.isNotBlank(offerId))
        {
            String operType = this.getPage().getData().getString("OPER_TYPE", "");
            if(!BizCtrlType.CreateUser.equals(operType) && !BizCtrlType.ChangeUserDis.equals(operType) 
                    && !BizCtrlType.CreateMember.equals(operType) && !BizCtrlType.ChangeMemberDis.equals(operType))
            {
                if("CrtUs".equals(operType))
                {
                    operType = BizCtrlType.CreateUser;
                }
                else if("ChgUs".equals(operType))
                {
                    operType = BizCtrlType.ChangeUserDis;
                }
                else if("DstUs".equals(operType))
                {
                    operType = BizCtrlType.DestoryUser;
                }
                else
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "获取操作类型不正确，请检查！");

                }
            }
          //集团定制
/*            String useTag = IUpcViewCall.getUseTagByOfferId(ecOfferId);
            if("1".equals(useTag) && (BizCtrlType.CreateMember.equals(operType) || BizCtrlType.ChangeMemberDis.equals(operType)))
            {//定制的成员操作
                dealSubOffer4Customized(offerId,operType, info);
            }*/

            
            	querySubOfferList(offerId,ecOfferId,operType, info,effectNow);
        }
        
        setInfo(info);
    }
    
    /**
     * 根据销售品编码加载必选子商品和资费商品
     * @param cycle
     * @throws Exception
     */
    public void querySubOfferList(String offerId, String ecOfferId,String operType, IData info,String effectNow) throws Exception
    {
        IData pageData = this.getPage().getData();
        IData ajaxData = new DataMap();

        String merchpOperType = pageData.getString("MERCHP_OPER_TYPE", "");
        String bbossSubOfferId = pageData.getString("BBOSS_SUB_OFFER_ID", "");//集团产品id
        String bbossSubOfferCode = IUpcViewCall.getOfferCodeByOfferId(bbossSubOfferId);
        String userId = pageData.getString("USER_ID", "");
        String bbossEcUserId = pageData.getString("BBOSS_EC_USER_ID", "");
        String openTag = pageData.getString("OPEN_TAG");
        String tradeTypeCode = pageData.getString("TRADE_TYPE_CODE", "");
        String ecUserId = pageData.getString("EC_USER_ID", "");

        boolean isBatch = pageData.getBoolean("IS_BATCH", false);
        
        String brand = pageData.getString("BRAND_CODE", "");
        if(StringUtils.isBlank(brand))
        {
            brand = IUpcViewCall.queryBrandCodeByOfferId(offerId);
        }
        //定制信息
        
        //BBOSS产品定制
        //if(!openTag.equals("IsOpen")){
	        if(!offerId.equals(ecOfferId)&&brand.equals("BOSG") && (merchpOperType.equals("1")||merchpOperType.equals("55")||merchpOperType.equals("5"))){
	            String offerCode = IUpcViewCall.getOfferCodeByOfferId(offerId);
	            IData offer = UpcViewCall.queryOfferByOfferId(this, "P", offerCode, "Y");
	            IData initBbossOffer = new DataMap();
	            String mainOfferId = offer.getString("OFFER_ID");
	            initBbossOffer.put("OFFER_ID", mainOfferId);
	            initBbossOffer.put("OFFER_CODE", offerCode);
	            initBbossOffer.put("OFFER_TYPE", "P");
	            initBbossOffer.put("BRAND_CODE", offer.getString("BRAND_CODE"));
	            initBbossOffer.put("OFFER_NAME", offer.getString("OFFER_NAME"));
	            initBbossOffer.put("OPER_TYPE", operType);
	
	                    
	            //集团定制
	            String useTag = IUpcViewCall.getUseTagByProductId(offerCode);
	            ajaxData.put("USE_TAG", useTag);
	            ajaxData.put("OFFER_ID", offerId);
	            ajaxData.put("EC_OFFER_ID", ecOfferId);
	
	
	            if("1".equals(useTag)){
	                //定制初始化
	                IDataset mebPkgList = ProductPkgInfoIntfViewUtil.qryMebForcePackageByGrpProId(this, offerCode, this.getVisit().getLoginEparchyCode());
	                setForcePkgList(mebPkgList);
	            	
	                IDataset userGrpPackageList = queryProPackages(offerCode, userId);
	                ajaxData.put("USER_PACKAGES", userGrpPackageList);
	                ajaxData.put("FORCE_PACKAGES", mebPkgList);

	            }
	            setInitBbossOffer(initBbossOffer);
	
	        }
       // }
        //查询是否有商品组
	        String groupIds = ""; //当前设置的商品包含的商品组
	        boolean hasGroup = false;
	        IDataset groupOffers = null;
	        if("BOSG".equals(brand) && operType.contains("Mb")){	        	
	        	
	        	groupOffers = getMebGroups(ecOfferId,bbossEcUserId);
	        	
	        }else{
	            groupOffers = IUpcViewCall.queryOfferGroups(offerId, this.getVisit().getLoginEparchyCode());
	        }
     
         	if(DataUtils.isNotEmpty(groupOffers))
 	        {
 	            hasGroup = true;
 	            StringBuilder groupIdSb = new StringBuilder(200);
 	            for(int i = 0, size = groupOffers.size(); i < size; i++)
 	            {
 	                groupIdSb.append("@").append(groupOffers.getData(i).getString("GROUP_ID"));
 	            }
 	            if(groupIdSb.length() > 0)
 	            {
 	                groupIds = groupIdSb.substring(1).toString();
 	            }
 	        }     	
        

        info.put("IS_SHOW_ADDOFFER_BTN", hasGroup);

        //查询子销售品
        IDataset subOffers = new DatasetList();
        IDataset subOfferList = IUpcViewCall.queryOfferComRelOfferByOfferId(offerId, this.getVisit().getLoginEparchyCode());
        IDataset joinRelOffers = IUpcViewCall.queryOfferJoinRelAndOfferByOfferId(offerId, "4", "", "");
        if (DataUtils.isNotEmpty(subOfferList))
        {
            subOffers.addAll(subOfferList);
        }
        if (DataUtils.isNotEmpty(joinRelOffers))
        {
            subOffers.addAll(joinRelOffers);
        }
        
        if("BOSG".equals(brand) && operType.contains("Mb"))
        {//BBoss成员新增、变更过滤子商品列表
            if(StringUtils.isNotBlank(bbossSubOfferCode))
            {
                filterBbossSubOfferList(subOffers, bbossSubOfferCode);
            }
        }
        
        if (DataUtils.isNotEmpty(subOffers))
        {
            int offerIndex = 0; //产品类商品
            int pOfferIndex = 0;
            IDataset productOffers = new DatasetList();
            IDataset serviceOffers = new DatasetList();
            IDataset priceOffers = new DatasetList();
            for (int i = 0, size = subOffers.size(); i < size; i++)
            {
                IData subOffer = subOffers.getData(i);
                String subOfferType = subOffer.getString("OFFER_TYPE");
                String subOfferCode = subOffer.getString("OFFER_CODE");
                String subOfferId = subOffer.getString("OFFER_ID");

                IData optOfferData = new DataMap();

                String validDate = "";
                if(BizCtrlType.CreateMember.equals(operType) || BizCtrlType.CreateUser.equals(operType)){
                    if("0".equals(effectNow)){
                    	validDate = SysDateMgr4Web.getFirstDayOfNextMonth();
                    }else{
                    	validDate = SysDateMgr4Web.getSysTime();
                    }
            	}
                IDataset offerData = IUpcViewCall.queryOfferEnableMode(offerId, null, subOffer.getString("OFFER_CODE"), subOfferType);
                if(DataUtils.isNotEmpty(offerData))
                {//取配置 计算生失效时间
                    if(StringUtils.isEmpty(validDate)){
                        validDate = SysDateMgr4Web.startDate(offerData.first().getString("ENABLE_MODE"), offerData.first().getString("ABSOLUTE_ENABLE_DATE"), offerData.first().getString("ENABLE_OFFSET"), offerData.first().getString("ENABLE_UNIT"));

                    }
                    subOffer.put("START_DATE", validDate);
                    subOffer.put("END_DATE", SysDateMgr4Web.endDate(validDate, offerData.first().getString("DISABLE_MODE"), offerData.first().getString("ABSOLUTE_DISABLE_DATE"), offerData.first().getString("DISABLE_OFFSET"), offerData.first().getString("DISABLE_UNIT")));
     
                }else{
                	if(StringUtils.isEmpty(validDate)){
                        validDate = SysDateMgr4Web.getSysTime();
                    }
                	subOffer.put("START_DATE", validDate);
                	subOffer.put("END_DATE", TimeUtil.EXPIRE_DATE);
                }
                
                if("D".equals(subOfferType))
                {
                    subOffer.put("P_OFFER_INDEX", pOfferIndex);
                    pOfferIndex++;
                    
                    String startDate = subOffer.getString("START_DATE");
                    String endDate = subOffer.getString("END_DATE");
                    int isExpire = SysDateMgr.compareTo(startDate, endDate);                    
                    //为了和老系统保持一致,监务通下的3408优惠,配置的结束时间是2016年,就很奇葩,老环境是在受理的时候展示,在变更的时候不展示,现在为了保持一致,做细微兼容修改
                    if("ChgMb".equals(operType)&&(isExpire>=0)){
                    	
                    }else{
                    	priceOffers.add(subOffer);
                    }
                }
                else if("S".equals(subOfferType)||"Z".equals(subOfferType))
                {
                    serviceOffers.add(subOffer);
                }
                else
                {//OFFER_TYPE=P
                	
                	subOffer.put("OFFER_INDEX", offerIndex);
                    optOfferData.put("OFFER_INDEX", offerIndex);
                    offerIndex++;
                    productOffers.add(subOffer);
                    boolean repeatOrder = false;
                    if("BOSG".equals(brand)){
                    	//bboss产品 是否的一单多线
                        IDataset moreSubOfferData =  CommonViewCall.getAttrsFromAttrBiz(this,"1","B","MULTI",subOfferCode);
        	            if(DataUtils.isNotEmpty(moreSubOfferData))
        	            {
        	                repeatOrder = true;
        	            }
                    }
                    subOffer.put("REPEAT_ORDER", repeatOrder);
                    optOfferData.put("REPEAT_ORDER", repeatOrder);
                }

            }
            setProductOfferList(productOffers);
            setServiceOfferList(serviceOffers);
            setPriceOfferList(priceOffers);
            
        }
        
        //判断是否有商品特征
        boolean hasOfferChaSpec = CommonViewCall.hasOfferSpecCha(this, operType, merchpOperType, offerId, brand);
        boolean showOfferCha = hasOfferChaSpec;
        if(!"BOSG".equals(brand) && isBatch && hasOfferChaSpec)
        {//非bboss 且 是批量业务 且 有商品特征
            //如果有配置，则页面显示商品特征
            IDataset data = StaticUtil.getStaticList("EC_BATCH_SHOW_OFFER_CHA", offerId);
            if(DataUtils.isNotEmpty(data))
            {
                showOfferCha = true;
            }
            else
            {
                showOfferCha = false;
            }
        }
        info.put("OFFER_ID", offerId);
        info.put("IS_SHOW_OFFER_CHA_SPEC_PART", showOfferCha);
        
        IDataset subOfferDataset = buildSubOfferRequiredElements(offerId, subOffers, operType, brand,effectNow);        
        IData selGroupOfferData = buildSelMustGroupOffersData(offerId, subOffers);
        
        //过滤掉结束时间比新增时间早的商品,暂时仅在变更成员操作过滤
        if("ChgMb".equals(operType)){
        	if(IDataUtil.isNotEmpty(subOfferDataset)){
        		for(int i=0;i<subOfferDataset.size();i++){
        			IData elementInfo = subOfferDataset.getData(i);
        			String startDate = elementInfo.getString("START_DATE");
                    String endDate = elementInfo.getString("END_DATE");
                    int isExpire = SysDateMgr.compareTo(startDate, endDate);
                    if(isExpire>=0){
                    	 subOfferDataset.remove(i);
                    	 i--;
                    }
        		}
        	}
        }
        
        ajaxData.put("SUB_OFFERS", subOfferDataset);
        ajaxData.put("SELECT_GROUP_OFFER", selGroupOfferData);
        ajaxData.put("HAS_OFFER_CHA_SPECS", showOfferCha);
        ajaxData.put("IS_SHOW_ADDOFFER_BTN", info.getString("IS_SHOW_ADDOFFER_BTN"));
        ajaxData.put("GROUP_IDS", groupIds);
        this.getPage().setAjax(ajaxData);
    }

    /**
     * 构造子商品数据结构，包含必选元素
     * @param optionalOffers
     * @param operType
     * @return
     * @throws Exception
     */
    private IDataset buildSubOfferRequiredElements(String mainOfferId, IDataset optionalOffers, String operType, String brand,String effectNow) throws Exception
    {
        IDataset optOfferDataset = new DatasetList();
        if(DataUtils.isNotEmpty(optionalOffers))
        {
            for(int i = 0, size = optionalOffers.size(); i < size; i++)
            {
                IData optionOffer = optionalOffers.getData(i);
                String offerId = optionOffer.getString("OFFER_ID");
                
                IData optOfferData = new DataMap();
                optOfferData.put("OFFER_ID", optionOffer.getString("OFFER_ID"));
                optOfferData.put("OFFER_CODE", optionOffer.getString("OFFER_CODE"));
                optOfferData.put("OFFER_NAME", optionOffer.getString("OFFER_NAME"));
                optOfferData.put("OFFER_TYPE", optionOffer.getString("OFFER_TYPE"));
                optOfferData.put("GROUP_ID", optionOffer.getString("GROUP_ID", "0"));
                optOfferData.put("BRAND_CODE", optionOffer.getString("BRAND_CODE"));
                optOfferData.put("OPER_CODE", PageDataTrans.ACTION_CREATE);
                optOfferData.put("SELECT_FLAG", optionOffer.getString("SELECT_FLAG","0"));//此处构造必选元素，所以如果无值，默认必选
                optOfferData.put("FORCE_TAG", optionOffer.getString("FORCE_TAG"));  //标识构成关系或者必选包的必选元素
                optOfferData.put("DESCRIPTION", optionOffer.getString("DESCRIPTION"));
                optOfferData.put("OFFER_INDEX", optionOffer.getString("OFFER_INDEX"));
                optOfferData.put("REPEAT_ORDER", optionOffer.getString("REPEAT_ORDER"));

              //资费类商品加入生失效时间
            	if(BizCtrlType.CreateMember.equals(operType) || BizCtrlType.CreateUser.equals(operType)){
            		if("0".equals(effectNow)){
            			optOfferData.put("START_DATE", SysDateMgr4Web.getFirstDayOfNextMonth());
                    }else{
                    	optOfferData.put("START_DATE", SysDateMgr4Web.getSysTime());
                    }
            	}else{
            		optOfferData.put("START_DATE", optionOffer.getString("START_DATE"));
            	}

                optOfferData.put("END_DATE", optionOffer.getString("END_DATE"));
                
                optOfferData.put("P_OFFER_INDEX", optionOffer.getString("P_OFFER_INDEX"));
                if(IUpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(optionOffer.getString("OFFER_TYPE")))
                {                   
                    //是否能重复订购
                    optOfferData.put("REPEAT_ORDER", optionOffer.getBoolean("REPEAT_ORDER", false));
                    
                    //是否有资费特征
                    IDataset offerChas = IUpcViewCall.queryChaByOfferId(offerId);
                    optOfferData.put("HAS_PRICE_CHA", DataUtils.isNotEmpty(offerChas) ? true : false);
                }
                
                // 查询必选子商品
                IDataset subOffers =IUpcViewCall.queryOfferComRelOfferByOfferId(optionOffer.getString("OFFER_ID"), this.getVisit().getLoginEparchyCode());
                if(DataUtils.isNotEmpty(subOffers))
                {
                    int pOfferIndex = 0;
                    IDataset subOfferDataset = new DatasetList();
                    for(int j = 0, len = subOffers.size(); j < len; j++)
                    {
                        IData subOffer = subOffers.getData(j);
                        
                        IData subOfferData = new DataMap();
                        subOfferData.put("OFFER_ID", subOffer.getString("OFFER_ID"));
                        subOfferData.put("OFFER_CODE", subOffer.getString("OFFER_CODE"));
                        subOfferData.put("OFFER_NAME", subOffer.getString("OFFER_NAME"));
                        subOfferData.put("OFFER_TYPE", subOffer.getString("OFFER_TYPE"));
                        subOfferData.put("GROUP_ID", "0");
                        subOfferData.put("BRAND_CODE", subOffer.getString("BRAND_CODE"));
                        subOfferData.put("OPER_CODE", PageDataTrans.ACTION_CREATE);
                        subOfferData.put("FORCE_TAG", subOffer.getString("FORCE_TAG"));  //标识构成关系或者必选包的必选元素
                        
                        if(IUpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(subOffer.getString("OFFER_TYPE")))
                        {
                        	subOfferData.put("P_OFFER_INDEX", pOfferIndex);
                            pOfferIndex++;

                            String validDate = "";
                            if(BizCtrlType.CreateMember.equals(operType) || BizCtrlType.CreateUser.equals(operType)){
                                if("0".equals(effectNow)){
                                	validDate = SysDateMgr4Web.getFirstDayOfNextMonth();
                                }else{
                                	validDate = SysDateMgr4Web.getSysTime();
                                }
                        	}
                            //此处后续处理liuzz
                            IData priceOfferData = null;//EcUpcViewUtil.queryEnableMode(validDate,offerId,subOffer.getString("OFFER_ID"),"");
                            if(DataUtils.isNotEmpty(priceOfferData))
                            {//取配置 计算生失效时间
                            	subOfferData.put("START_DATE", priceOfferData.getString("START_DATE"));
                            	subOfferData.put("END_DATE", priceOfferData.getString("END_DATE"));
                            }else{
                                
                            	subOfferData.put("START_DATE", validDate);
                            	subOfferData.put("END_DATE", TimeUtil.EXPIRE_DATE);
                            }
                            

                            //是否有资费特征
                            IDataset offerChas = IUpcViewCall.queryChaByOfferId(subOffer.getString("OFFER_ID"));
                            subOfferData.put("HAS_PRICE_CHA", DataUtils.isNotEmpty(offerChas) ? true : false);
                        }
                        
                        subOfferDataset.add(subOfferData);
                    }
                    
                    if(DataUtils.isNotEmpty(subOfferDataset))
                    {
                        optOfferData.put("SUBOFFERS", subOfferDataset);
                    }
                }
                
                boolean hasOfferCha = false;
                if("BOSG".equals(brand) && operType.contains("Mb") && !IUpcConst.ELEMENT_TYPE_CODE_SVC.equals(optionOffer.getString("OFFER_TYPE")))
                {
                	hasOfferCha = CommonViewCall.hasOfferSpecCha(this, operType, EcConstants.MERCH_STATUS.MERCH_ADD.getValue(),offerId, brand);
                }
                else
                {
                    hasOfferCha = CommonViewCall.hasOfferSpecCha(this, operType, EcConstants.MERCH_STATUS.MERCH_ADD.getValue(), offerId, brand);
                }
                
                if(hasOfferCha)
                {
                    optOfferData.put("IS_SHOW_SET_TAG", true);
                }
                else
                {
                    optOfferData.put("IS_SHOW_SET_TAG", false);
                }


                optOfferDataset.add(optOfferData);
            }
        }
        return optOfferDataset;
    }
    
    /**
     * 构建必选商品组信息以及选择的商品信息
     * @param mainOfferId
     * @param subOfferList
     * @return
     * @throws Exception
     */
    private IData buildSelMustGroupOffersData(String mainOfferId, IDataset subOfferList) throws Exception
    {
        IData selGroupData = new DataMap();
        
        IDataset groupList = IUpcViewCall.queryOfferGroups(mainOfferId, getVisit().getLoginEparchyCode());
        if(DataUtils.isNotEmpty(groupList))
        {
            for(int i = 0, sizeI = groupList.size(); i < sizeI; i++)
            {
                IData group = groupList.getData(i);
                if(!UpcConst.SELECT_FLAG_MUST_CHOOSE.equals(group.getString("SELECT_FLAG")))
                {
                    continue;
                }
                IData groupData = new DataMap();
                groupData.put("GROUP_ID", group.getString("GROUP_ID"));
                groupData.put("GROUP_NAME", group.getString("GROUP_NAME"));
                groupData.put("MAX_NUM", group.getString("MAX_NUM"));
                groupData.put("MIN_NUM", group.getString("MIN_NUM"));
                groupData.put("SELECT_FLAG", UpcConst.SELECT_FLAG_MUST_CHOOSE);
                groupData.put("LIMIT_TYPE", group.getString("LIMIT_TYPE"));
                
                StringBuilder mustSelOfferSB = new StringBuilder(500);
                IDataset selOfferList = new DatasetList();
                IDataset groupOfferList = IUpcViewCall.queryGroupComRelOffer(group.getString("GROUP_ID"), getVisit().getLoginEparchyCode(), getVisit().getStaffId());
                for(int j = 0, sizeJ = groupOfferList.size(); j < sizeJ; j++)
                {
                    IData groupOffer = groupOfferList.getData(j);
                    String groupOfferId = groupOffer.getString("OFFER_ID");
                    String selectFlag = groupOffer.getString("SELECT_FLAG");
                    if(UpcConst.SELECT_FLAG_MUST_CHOOSE.equals(selectFlag))
                    {
                        mustSelOfferSB.append("@").append(groupOfferId);
                    }
                    for(int k = 0, sizeK = subOfferList.size(); k < sizeK; k++)
                    {
                        if(groupOfferId.equals(subOfferList.getData(k).getString("OFFER_ID")))
                        {
                            IData selOffer = new DataMap();
                            selOffer.put("OFFER_ID", groupOfferId);
                            selOffer.put("OFFER_CODE", groupOffer.getString("OFFER_CODE"));
                            selOffer.put("OFFER_TYPE", groupOffer.getString("OFFER_TYPE"));
                            selOffer.put("SELECT_FLAG", selectFlag);
                            selOfferList.add(selOffer);
                            break;
                        }
                    }
                }
                
                if(DataUtils.isNotEmpty(selOfferList))
                {//商品组中已经选择的商品
                    groupData.put("SEL_OFFER", selOfferList);
                }
                
                int length = mustSelOfferSB.length();
                if(length > 0)
                {//必选的商品
                    groupData.put("MUST_SEL_OFFER", mustSelOfferSB.substring(1, length).toString());
                }
                selGroupData.put(groupData.getString("GROUP_ID"), groupData);
            }
        }
        
        return selGroupData;
    }

    /**
     * 过滤bboss成员新增、变更子商品列表
     * @param subOfferList
     * @param bbossSubOfferId
     * @throws Exception
     */
    private void filterBbossSubOfferList(IDataset subOfferList, String bbossSubOfferCode) throws Exception
    {
    	//查询集团产品下的成员产品id
        IDataset mebSubOfferDataset =UpcViewCall.queryOfferJoinRelAndOfferByOfferId(this, "P", bbossSubOfferCode, "1", null, null);
        if(DataUtils.isNotEmpty(mebSubOfferDataset))
        {//查询bboss集团子商品对应的成员子商品
            IData mebSubOfferData = mebSubOfferDataset.first();
            String memSubOfferId = mebSubOfferData.getString("OFFER_ID", "");
            for(int i = subOfferList.size(); i > 0; i--)
            {
                IData optionOffer = subOfferList.getData(i-1);
                String subOfferId = optionOffer.getString("OFFER_ID");
                String offerType=optionOffer.getString("OFFER_TYPE");
                if(!memSubOfferId.equals(subOfferId)&&"P".equals(offerType))
                {
                    subOfferList.remove(i-1);
                }
            }
        }
        else
        {
            subOfferList.clear();
        }
    }
    
    private IData parseShowMode()
    {
        IData result = new DataMap();
        result.put("IS_SHOW_OFFER_CHA_SPEC_PART", true);
        result.put("IS_SHOW_SUB_OFFER_PART", true);

        return result;
    }
    
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        setInfo(null);
        setServiceOfferList(null);
        setPriceOfferList(null);
        setProductOfferList(null);
    }
    
    
    /**
     * 查询BBOSS产品定制信息
     * @param productId
     * @param ecUserId
     * @return
     * @throws Exception
     */
    public IDataset queryProPackages(String productId,String ecUserId) throws Exception
    {
        IDataset userGrpPackageList = new DatasetList();
        IDataset elementList = new DatasetList();
        if (StringUtils.isNotBlank(ecUserId))
        {
            IData inparam = new DataMap();
            inparam.put("USER_ID", ecUserId);
            elementList = CSViewCall.call(this, "CS.UserGrpPkgInfoQrySVC.getUserGrpPackageForGrp", inparam);
            if(DataUtils.isEmpty(elementList)){
                setProPackages(userGrpPackageList);
        		return userGrpPackageList;
        	}
            for (int i = 0; i < elementList.size(); i++)
            {
                IData temp = elementList.getData(i);
                IData userGrpPackage = new DataMap();
                userGrpPackage.put("PRODUCT_ID", temp.getString("PRODUCT_ID"));
                userGrpPackage.put("ELEMENT_ID", temp.getString("ELEMENT_ID"));
                userGrpPackage.put("PACKAGE_ID", temp.getString("PACKAGE_ID"));
                userGrpPackage.put("ELEMENT_NAME", temp.getString("ELEMENT_NAME"));
                userGrpPackage.put("ELEMENT_TYPE_CODE", temp.getString("ELEMENT_TYPE_CODE"));
                userGrpPackage.put("MODIFY_TAG", "EXIST");//默认不变
                
                IDataset element = IUpcViewCall.qryOfferByOfferIdRelOfferId(temp.getString("PRODUCT_ID"),"P",temp.getString("ELEMENT_ID"),temp.getString("ELEMENT_TYPE_CODE"),"");
                if(DataUtils.isNotEmpty(element)){
                    userGrpPackage.put("SELECT_FLAG", UpcConst.getSelectFlagForDefaultTagAndForceTag(element.first().getString("DEFAULT_TAG","0"), element.first().getString("FORCE_TAG","0")));
                }

                userGrpPackageList.add(userGrpPackage);
            }
            setProPackages(userGrpPackageList);
        }
        else
        {
        	
        	IData inparam = new DataMap();
            inparam.put("PRODUCT_ID", productId);
            inparam.put(Route.USER_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());
            
            //必选定制包
           // IDataset mebPkgList = ProductPkgInfoIntfViewUtil.qryMebForcePackageByGrpProId(this, productId, this.getVisit().getLoginEparchyCode());
            
            elementList = CSViewCall.call(this, "CS.ProductInfoQrySVC.getMebProductForceElements", inparam);
            for (int i = 0; i < elementList.size(); i++)
            {
                IData temp = elementList.getData(i);
                IData userGrpPackage = new DataMap();
                userGrpPackage.put("PRODUCT_ID", temp.getString("PRODUCT_ID"));
                userGrpPackage.put("ELEMENT_ID", temp.getString("ELEMENT_ID"));
                userGrpPackage.put("PACKAGE_ID", temp.getString("PACKAGE_ID"));
                userGrpPackage.put("ELEMENT_FORCE_TAG", temp.getString("ELEMENT_FORCE_TAG"));
                userGrpPackage.put("ELEMENT_NAME", temp.getString("ELEMENT_NAME"));
                userGrpPackage.put("MODIFY_TAG", "0");
                userGrpPackage.put("ELEMENT_TYPE_CODE", temp.getString("ELEMENT_TYPE_CODE"));
                userGrpPackage.put("SELECT_FLAG", "0");
               
                userGrpPackageList.add(userGrpPackage);
            }
        	
            setProPackages(userGrpPackageList);
        }
        return userGrpPackageList;
    }
    
    
    private IDataset getMebGroups(String ecOfferId,String ecUserId) throws Exception{
        String useTag = IUpcViewCall.getUseTagByOfferId(ecOfferId);
        IDataset allGroupOffers = new DatasetList();

        if("1".equals(useTag)){//如果是成员操作定制，查询已定制的包
            //成员定制情况
            //如果是服务包，加到结果集里
            IData groupMap = new DataMap();
            IData inparam = new DataMap();
            inparam.put("OFFER_ID", ecOfferId);
            inparam.put("EPARCHY_CODE", this.getVisit().getLoginEparchyCode());

            IDataset groups = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.queryMebGroupByOfferId", inparam);//IUpcViewCall.queryMebGroupByOfferId(ecOfferId,this.getVisit().getLoginEparchyCode());
            if(DataUtils.isNotEmpty(groups))
            {
                for(int i = 0, sizeI = groups.size(); i < sizeI; i++)
                {
                    IData group = groups.getData(i);
                    String groupType = group.getString("GROUP_TYPE","");
                    
                    if(!"1".equals(groupType)){//如果服务包则显示
                        continue;
                    }
                    groupMap.put(group.getString("GROUP_ID",""),group);
                }
            }
            //根据集团用户编码查询已定制信息
            IData input = new DataMap();
            input.put("USER_ID", ecUserId);
            IDataset userGrpPackages = CSViewCall.call(this, "CS.UserGrpPkgInfoQrySVC.getUserGrpPackageForGrp", input);
            
            if(DataUtils.isEmpty(userGrpPackages)){
                Iterator itr = groupMap.keySet().iterator();
                while (itr.hasNext())
                {
                    String key = itr.next().toString();
                    IData value = groupMap.getData(key);
                    
                    allGroupOffers.add(value);
                }
                return allGroupOffers;
            }

            //循环定制信息，分组
            for(int a=0,size=userGrpPackages.size();a<size;a++){
                IData userPackage = userGrpPackages.getData(a);
                String groupId = userPackage.getString("PACKAGE_ID");

                IData group = new DataMap();
                group.put("GROUP_ID", groupId);
                groupMap.put(groupId,group);
            }
            
            Iterator itr = groupMap.keySet().iterator();
            while (itr.hasNext())
            {
                String key = itr.next().toString();
                IData value = groupMap.getData(key);
                
                allGroupOffers.add(value);
            }
        }else{
            IDataset mebOffers = IUpcViewCall.queryOfferJoinRelAndOfferByOfferId(ecOfferId, "1", "", "");
            if(DataUtils.isEmpty(mebOffers)){
                return allGroupOffers;
            }
            for(int a=0,size=mebOffers.size();a<size;a++){
                String offerId = mebOffers.getData(a).getString("OFFER_ID");
                IDataset groups = IUpcViewCall.queryOfferGroups(offerId, this.getVisit().getLoginEparchyCode());
                
                if(DataUtils.isNotEmpty(groups))
                {
                    allGroupOffers.addAll(groups);
                }
            }
        }
        return allGroupOffers;
    }
}
