package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.ecintegration.groupoffer;

import java.util.Iterator;

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
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.pagedata.PageDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public abstract class GroupOfferList extends BizTempComponent
{
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "scripts/iorder/icsserv/component/enterprise/ecintegration/groupoffer/GroupOfferList.js";

        if (isAjax)
        {
            includeScript(writer, jsFile, false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }

        String action = getPage().getData().getString("ACTION");
        if("queryGroupOffersByOfferId".equals(action))
        {
            queryGroupOffersByOfferId();
        }
        else if("buildSelGroupOfferData".equals(action))
        {
            buildSelGroupOfferData();
        }
    }
    
    private void queryGroupOffersByOfferId() throws Exception
    {
        String offerId = getPage().getData().getString("OFFER_ID");
        String ecMebType = getPage().getData().getString("ecMebType");
        String dzMebElements = getPage().getData().getString("DZ_MEB_ELEMENTS");
        String espOfferVersion = getPage().getData().getString("ESP_OFFER_VERSION");
        
        IDataset groupOffers = IUpcViewCall.queryOfferGroups(offerId, this.getVisit().getLoginEparchyCode());
        
        if(DataUtils.isNotEmpty(groupOffers))
        {
            String offerCode = IUpcViewCall.getOfferCodeByOfferId(offerId);
            for(int i = 0, sizeI = groupOffers.size(); i < sizeI; i++)
            {
                IData group = groupOffers.getData(i);
                
                IDataset offerList =IUpcViewCall.queryChildOfferByGroupId(group.getString("GROUP_ID"), this.getVisit().getLoginEparchyCode());

                offerList = filterDzMebElements(ecMebType, dzMebElements, offerList);
                
                offerList = filterEspOffer(offerId, espOfferVersion, offerList);
                
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
                getGroupInfobyOffer(group, offerCode);
            }
        }
        setGroupList(groupOffers);
        
        this.getPage().setAjax(groupOffers);
    }
    
    /**
     ** 过滤esp产品 子商品
     * @param offerList
     * @param espOfferVersion 
     * @return 
     * @Date 2019年10月14日
     * @author xieqj 
     */
    private IDataset filterEspOffer(String offerId, String espOfferVersion, IDataset offerList) {
		 if("110000921015".equals(offerId)) {
			if(DataUtils.isEmpty(espOfferVersion))
			{
				return offerList;
			}
			Iterator<Object> iterator = offerList.iterator();
			while(iterator.hasNext()) {
				IData next = (IData)iterator.next();
				if ("云服务版".equals(espOfferVersion)) {
					if ("130392101506".equals(next.getString("OFFER_ID")))
					{
						iterator.remove();
					}
					//子商品（资费）必选设置
					if ("130392101501".equals(next.getString("OFFER_ID")))
					{
						next.put("SELECT_FLAG", "0");
					}
				}
				else if ("入驻版".equals(espOfferVersion)) {
					if (!"130392101506".equals(next.getString("OFFER_ID")))
					{
						iterator.remove();
					}
					//子商品（资费）必选设置
					if ("130392101506".equals(next.getString("OFFER_ID")))
					{
						next.put("SELECT_FLAG", "0");
					}
				}
			}
		 }
		return offerList;
	}

	/**
     * 过滤出定制选择的数据
     * @param ecMebType
     * @param dzMebElements
     * @param offerList
     * @return
     * @throws Exception
     */
    private IDataset filterDzMebElements(String ecMebType, String dzMebElements, IDataset offerList) throws Exception
    {
        if (!"MEB".equals(ecMebType))
        {
            return offerList;
        }
        IDataset reSet = new DatasetList();
        if (StringUtils.isBlank(dzMebElements))
        {
//            String useTag = IUpcViewCall.getUseTagByProductId(ecProductId);
//            // 产品 useTag为0 没有定制但有成员产品
//            return StringUtils.equals("0", useTag) ? offerList : new DatasetList();
            return new DatasetList();
        }
        String[] dzMebArr = StringUtils.split(dzMebElements, ",");
        for (int i = 0, size = offerList.size(); i < size; i++)
        {
            IData offerData = offerList.getData(i);
            String offerType = offerData.getString("OFFER_TYPE");
            String offerCode = offerData.getString("OFFER_CODE");
            for (int j = 0; j < dzMebArr.length; j++)
            {
                String[] offerArr = dzMebArr[j].split("@");
                if (offerType.equals(offerArr[1]) && offerCode.equals(offerArr[0]))
                {
                    reSet.add(offerData);
                    break;
                }
            }
        }
        return reSet;
    }
    
    private void buildSelGroupOfferData() throws Exception
    {
        String ecMebType = getPage().getData().getString("EC_MEB_TYPE");
        String mainOperType = getPage().getData().getString("MAIN_OPER_CODE");
        String operType = PageDataTrans.transOperCodeToOperType(mainOperType, ecMebType);
        
        String effectNow = getPage().getData().getString("EFFECT_NOW");
        String brandCode = getPage().getData().getString("BRAND_CODE");
        
        IDataset selGroupOfferList = new DatasetList();
        String selOfferIds = getPage().getData().getString("SEL_OFFER_IDS");
        IData offerCode2IdCacheData = new DataMap();
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
            subOfferData.put("OFFER_TYPE", offerType);
            subOfferData.put("GROUP_ID", offerIdGroupIdArr[1]); //商品组id
            subOfferData.put("OPER_CODE", PageDataTrans.ACTION_CREATE);
            subOfferData.put("DESCRIPTION", offer.getString("DESCRIPTION"));
            subOfferData.put("REL_OFFER_CODE", offerIdGroupIdArr[2]);
            
            String relOfferId = offerCode2IdCacheData.getString(offerIdGroupIdArr[2]);
            if(StringUtils.isEmpty(relOfferId))
            {
                relOfferId = IUpcViewCall.getOfferIdByOfferCode(offerIdGroupIdArr[2]);
                offerCode2IdCacheData.put(offerIdGroupIdArr[2], relOfferId);
            }
            dealElementDate(subOfferData, relOfferId, offerIdGroupIdArr[1], operType, effectNow);
            
            //判断是否有商品特征
            if(UpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(offerType))
            {
                IDataset offerChas = IUpcViewCall.queryChaByOfferId(offerId);
                subOfferData.put("HAS_OFFER_CHA", IDataUtil.isNotEmpty(offerChas) ? true : false);
                subOfferData.put("PRICE_CHA_TYPE", "OFFER");//通过产商品表配置
            }
            else
            {
                boolean hasOfferCha = CommonViewCall.hasOfferSpecCha(this, operType, EcConstants.MERCH_STATUS.MERCH_ADD.getValue(), offerId, brandCode);
                subOfferData.put("HAS_OFFER_CHA", hasOfferCha);
            }
            
            selGroupOfferList.add(subOfferData);
        }
        getPage().setAjax(selGroupOfferList);
    }
    
    /**
     * 获取计算元素时间
     * 
     */
    public void dealElementDate(IData subOffer, String relOfferId, String groupId, String operType, String effectNow) throws Exception
    {
//        String subOfferCode = subOffer.getString("OFFER_CODE");
        String validDate = "";
        if (BizCtrlType.CreateMember.equals(operType) || BizCtrlType.CreateUser.equals(operType))
        {
            if ("0".equals(effectNow))
            {
                validDate = SysDateMgr4Web.getFirstDayOfNextMonth();
            }
            else
            {
                validDate = SysDateMgr4Web.getSysTime();
            }
        }

        IData offerData = IUpcViewCall.queryElementEnableMode("P", subOffer.getString("REL_OFFER_CODE"), groupId, subOffer.getString("OFFER_CODE"), subOffer.getString("OFFER_TYPE"));
        if (DataUtils.isNotEmpty(offerData))
        {// 取配置 计算生失效时间
            if (StringUtils.isEmpty(validDate))
            {
                validDate = SysDateMgr4Web.startDate(offerData.getString("ENABLE_MODE"), offerData.getString("ABSOLUTE_ENABLE_DATE"), offerData.getString("ENABLE_OFFSET"), offerData.getString("ENABLE_UNIT"));
            }
            subOffer.put("START_DATE", validDate);
            subOffer.put("END_DATE", SysDateMgr4Web.endDate(validDate, offerData.getString("DISABLE_MODE"), offerData.getString("ABSOLUTE_DISABLE_DATE"), offerData.getString("DISABLE_OFFSET"), offerData.getString("DISABLE_UNIT")));
            if ("3".equals(offerData.getString("ENABLE_MODE")) && StringUtils.isBlank(offerData.getString("ABSOLUTE_ENABLE_DATE")))
            {
                subOffer.put("CHOICE_START_DATE", true);
            }
        }
        else
        {
            if (StringUtils.isEmpty(validDate))
            {
                validDate = SysDateMgr4Web.getSysTime();
            }
            subOffer.put("START_DATE", validDate);
            subOffer.put("END_DATE", SysDateMgr4Web.END_DATE_FOREVER);
        }
    }
    
    public void getGroupInfobyOffer(IData groupData, String offerCode) throws Exception
    {
        IDataset groupInfoDataset = IUpcViewCall.queryOfferGroupsByProductId(offerCode, this.getVisit().getLoginEparchyCode());
        if (IDataUtil.isEmpty(groupInfoDataset))
        {
            return;
        }
        for (int i = 0, sizeI = groupInfoDataset.size(); i < sizeI; i++)
        {
            IData groupInfoData = groupInfoDataset.getData(i);
            if (groupInfoData.getString("GROUP_ID").equals(groupData.getString("GROUP_ID")))
            {
                groupData.put("MIN_NUM", groupInfoData.getString("MIN_NUM"));
                groupData.put("MAX_NUM", groupInfoData.getString("MAX_NUM"));
                groupData.put("SELECT_FLAG", groupInfoData.getString("SELECT_FLAG"));
            }
        }
    }
    
    public abstract void setOfferList(IDataset offerList);
    public abstract void setGroupList(IDataset groupList);
}
