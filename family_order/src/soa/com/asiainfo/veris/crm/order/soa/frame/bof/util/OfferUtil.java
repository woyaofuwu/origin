package com.asiainfo.veris.crm.order.soa.frame.bof.util;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class OfferUtil {

	public static IDataset unionAll(IDataset elementsA, IDataset elementsB){
		IDataset rst = new DatasetList();
		if(IDataUtil.isNotEmpty(elementsA))
			rst.addAll(elementsA);
		if(IDataUtil.isEmpty(elementsB))
			return rst;
		
		for(Object obj : elementsB){
			IData element = (IData)obj;
			String offerCode = element.getString("OFFER_CODE");
			String offerType = element.getString("OFFER_TYPE");
			boolean isExists = false;
			for(Object object : elementsA){
				IData tempElement = (IData)object;
				if(offerCode.equals(tempElement.getString("OFFER_CODE")) && offerType.equals(tempElement.getString("OFFER_TYPE"))){
					isExists = true;
					break;
				}
			}
			
			if(!isExists){
				rst.add(element);
			}
		}
		return rst;
	}
	
	public static IData find(String offerCode, String offerType, IDataset allElements){
		if(IDataUtil.isEmpty(allElements)){
			return null;
		}
		
		for(Object obj : allElements){
			IData element = (IData)obj;
			if(offerCode.equals(element.getString("OFFER_CODE")) && offerType.equals(element.getString("OFFER_TYPE"))){
				return element;
			}
		}
		return null;
	}
	
	public static IDataset siftIn(IDataset offers, IDataset allElements){
		IDataset rst = new DatasetList();
		for(Object obj : offers){
			IData element = (IData)obj;
			String offerCode = element.getString("ELEMENT_ID");
			String offerType = element.getString("ELEMENT_TYPE_CODE");
			boolean isExists = false;
			for(Object object : allElements){
				IData tempElement = (IData)object;
				if(offerCode.equals(tempElement.getString("OFFER_CODE")) && offerType.equals(tempElement.getString("OFFER_TYPE"))){
					isExists = true;
					break;
				}
			}
			if(isExists){
				rst.add(element);
			}
		}
		return rst;
	}
	
	
	public static void fillElements(IDataset svcList, IDataset discntList, IDataset allElements) throws Exception {
        String offerCodes = "";
        String offerTypes = "";
        if (IDataUtil.isNotEmpty(svcList)) {
            for (Object obj : svcList) {
                IData svc = (IData) obj;
                offerCodes += svc.getString("ELEMENT_ID") + ",";
                offerTypes += "S,";
                for(Object object : allElements){
                    IData element = (IData)object;
                    if("S".equals(element.getString("ELEMENT_TYPE_CODE")) && svc.getString("ELEMENT_ID").equals(element.getString("ELEMENT_ID"))){
                        svc.put("ELEMENT_NAME", element.getString("ELEMENT_NAME"));
                        svc.put("PACKAGE_FORCE_TAG", element.getString("PACKAGE_FORCE_TAG"));
                        svc.put("ELEMENT_FORCE_TAG", element.getString("ELEMENT_FORCE_TAG"));
                    }
                }
            }
        }

        if (IDataUtil.isNotEmpty(discntList)) {
            for (Object obj : discntList) {
                IData discnt = (IData) obj;
                offerCodes += discnt.getString("ELEMENT_ID") + ",";
                offerTypes += "D,";
                
                for(Object object : allElements){
                    IData element = (IData)object;
                    if("D".equals(element.getString("ELEMENT_TYPE_CODE")) && discnt.getString("ELEMENT_ID").equals(element.getString("ELEMENT_ID"))){
                        discnt.put("ELEMENT_NAME", element.getString("ELEMENT_NAME"));
                        discnt.put("PACKAGE_FORCE_TAG", element.getString("PACKAGE_FORCE_TAG"));
                        discnt.put("ELEMENT_FORCE_TAG", element.getString("ELEMENT_FORCE_TAG"));
                    }
                }
            }
        }
        
        
        
        IDataset offerInfos = null;
        if(StringUtils.isNotBlank(offerCodes)){
            offerCodes = offerCodes.substring(0, offerCodes.length() - 1);
            offerTypes = offerTypes.substring(0, offerTypes.length() - 1);
            offerInfos = UpcCall.queryOfferInfoWithOfferChaByOfferCodes(offerCodes, offerTypes);
        }

        if (IDataUtil.isNotEmpty(svcList)) {
            for (Object obj : svcList) {
                IData svc = (IData) obj;
                String serviceId = svc.getString("ELEMENT_ID");
                for (Object object : offerInfos) {
                    IData offer = (IData) object;

                    if ("S".equals(offer.getString("OFFER_TYPE")) && serviceId.equals(offer.getString("OFFER_CODE"))) {
                        svc.put("ELEMENT_NAME", offer.getString("OFFER_NAME"));
                        IDataset offerChas = offer.getDataset("OFFER_CHA");
                        if (IDataUtil.isNotEmpty(offerChas)) {
                            IDataset attrs = new DatasetList();
                            for (Object cha : offerChas) {
                                IData attr = new DataMap();
                                IData offerCha = (IData) cha;
                                attr.put("ATTR_CODE", offerCha.getString("FIELD_NAME"));
                                attr.put("ATTR_VALUE", offerCha.getString("DEFAULT_VALUE"));
                                attrs.add(attr);
                            }
                            svc.put("ATTR_PARAM", attrs);
                        }
                    }
                }
            }
        }
        if (IDataUtil.isNotEmpty(discntList)) {
            for (Object obj : discntList) {
                IData discnt = (IData) obj;
                String discntCode = discnt.getString("ELEMENT_ID");
                for (Object object : offerInfos) {
                    IData offer = (IData) object;

                    if ("D".equals(offer.getString("OFFER_TYPE")) && discntCode.equals(offer.getString("OFFER_CODE"))) {
                        discnt.put("ELEMENT_NAME", offer.getString("OFFER_NAME"));
                        IDataset offerChas = offer.getDataset("OFFER_CHA");
                        if (IDataUtil.isNotEmpty(offerChas)) {
                            IDataset attrs = new DatasetList();
                            for (Object cha : offerChas) {
                                IData attr = new DataMap();
                                IData offerCha = (IData) cha;
                                attr.put("ATTR_CODE", offerCha.getString("FIELD_NAME"));
                                attr.put("ATTR_VALUE", offerCha.getString("DEFAULT_VALUE"));
                                attrs.add(attr);
                            }
                            discnt.put("ATTR_PARAM", attrs);
                        }
                    }
                }
            }
        }
    }
	
	public static void resetOfferRelDate(DiscntTradeData discnt, BusiTradeData btd, UcaData uca, boolean isUpdStartDate) throws Exception{
    	List<OfferRelTradeData> offerRels = btd.getTradeDatas(TradeTableEnum.TRADE_OFFER_REL);
    	for(OfferRelTradeData offerRel : offerRels){
    		if(offerRel.getRelOfferInsId().equals(discnt.getInstId())){
    			if(isUpdStartDate)
    				offerRel.setStartDate(discnt.getStartDate());
    			offerRel.setEndDate(discnt.getEndDate());
    			break;
    		}
    	}
    	
    	List<OfferRelTradeData> userOfferRels = uca.getOfferRelsByRelUserId();
    	for(OfferRelTradeData offerRel : userOfferRels){
    		if(offerRel.getRelOfferInsId().equals(discnt.getInstId())){
    			if(isUpdStartDate)
    				offerRel.setStartDate(discnt.getStartDate());
    			offerRel.setEndDate(discnt.getEndDate());
    			break;
    		}
    	}
    }
	
	public static IDataset fillStructAndFilter(IDataset elements, IDataset offerRels) throws Exception{
		if(IDataUtil.isEmpty(elements) || IDataUtil.isEmpty(offerRels)){
			return elements;
		}
		
		offerRels = findMPMaxStartDate(offerRels);
		IDataset rst = new DatasetList();
		for(Object obj : elements){
			IData element = (IData)obj;
			String instId = element.getString("INST_ID");
			for(Object object : offerRels){
				IData offerRel = (IData)object;
				String relOfferInsId = offerRel.getString("REL_OFFER_INS_ID");
				if(relOfferInsId.equals(instId)){
					String offerCode = offerRel.getString("OFFER_CODE");
					String offerType = offerRel.getString("OFFER_TYPE");
					if("P".equals(offerType))
					{
    					String groupId = offerRel.getString("GROUP_ID","-1");
    					element.put("PRODUCT_ID", offerCode);
    					element.put("PACKAGE_ID",  groupId);
    					rst.add(element);
					}else if("K".equals(offerType))
                    {
					    String offerInsId = offerRel.getString("OFFER_INS_ID");
                        IDataset userSaleActiveInfos = UserSaleActiveInfoQry.queryUserSaleActiveByInstId(offerInsId);
                        element.put("PRODUCT_ID", userSaleActiveInfos.getData(0).getString("PRODUCT_ID"));
                        
                        element.put("PACKAGE_ID", offerCode);
                    }
				}
			}
		}
		return rst;
	}
	public static void fillTradeOffersWithUserOfferRel(IDataset tradeElements,IDataset tradeOfferRels)throws Exception{
		if(IDataUtil.isNotEmpty(tradeElements))
		{
			for(int i=0,size =tradeElements.size();i<size;i++)
			{
				IData temp = tradeElements.getData(i);
				if(StringUtils.equals(BofConst.MODIFY_TAG_UPD, temp.getString("MODIFY_TAG")))
				{
					String instId = temp.getString("INST_ID");
					if(!IDataUtil.dataSetContainsKeyAndValue(tradeOfferRels, "REL_OFFER_INS_ID", instId))
					{
						IDataset userOfferRels = UserOfferRelInfoQry.qryUserOfferRelInfosByRelOfferInstId(instId);
						if(IDataUtil.isNotEmpty(userOfferRels))
						{
							tradeOfferRels.addAll(userOfferRels);
						}
					}
				}
			}
		}
	}
	
	public static IDataset fillStructAndFilterForPf(IDataset elements, IDataset offerRels) throws Exception{
		if(IDataUtil.isEmpty(elements)){
			return elements;
		}
		else
		{
			if(IDataUtil.isEmpty(offerRels))
			{
				for(Object obj : elements)
				{
					IData element = (IData)obj;
					element.put("PRODUCT_ID", "-1");
					element.put("PACKAGE_ID", "-1");
				}
				return elements;
			}
		}
		IDataset rst = new DatasetList();
		for(Object obj : elements){
			IData element = (IData)obj;
			String instId = element.getString("INST_ID");
			boolean isExists = false;
			
			IDataset temps = DataHelper.filter(offerRels, "REL_OFFER_INS_ID="+instId);
			
			if(IDataUtil.isNotEmpty(temps))
			{
				if(temps.size() == 1)
				{
					element.put("PRODUCT_ID", temps.getData(0).getString("OFFER_CODE","-1"));
					element.put("PACKAGE_ID", temps.getData(0).getString("GROUP_ID","-1"));
					rst.add(element);
				}
				else
				{
					if(element.containsKey("MODIFY_TAG"))
					{
						if("U".equals(element.getString("MODIFY_TAG")))
						{
							DataHelper.sort(temps, "START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
							element.put("PRODUCT_ID", temps.getData(0).getString("OFFER_CODE","-1"));
							element.put("PACKAGE_ID", temps.getData(0).getString("GROUP_ID","-1"));
							rst.add(element);
						}
						else
						{
							IData mainProductOfferRel = getMainProductOfferRel(temps);
							if(IDataUtil.isNotEmpty(mainProductOfferRel))
							{
								element.put("PRODUCT_ID", mainProductOfferRel.getString("OFFER_CODE","-1"));
								element.put("PACKAGE_ID", mainProductOfferRel.getString("GROUP_ID","-1"));
								rst.add(element);
							}
							else
							{
								DataHelper.sort(temps, "START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
								element.put("PRODUCT_ID", temps.getData(0).getString("OFFER_CODE","-1"));
								element.put("PACKAGE_ID", temps.getData(0).getString("GROUP_ID","-1"));
								rst.add(element);
							}
						}
					}
					else
					{
						DataHelper.sort(temps, "START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
						element.put("PRODUCT_ID", temps.getData(0).getString("OFFER_CODE","-1"));
						element.put("PACKAGE_ID", temps.getData(0).getString("GROUP_ID","-1"));
						rst.add(element);
					}
				}
			}
			else
			{
				element.put("PRODUCT_ID", "-1");
				element.put("PACKAGE_ID", "-1");
				rst.add(element);
			}
		}
		return rst;
	}
	public static IData getMainProductOfferRel(IDataset offerRels) throws Exception
	{
		if(IDataUtil.isNotEmpty(offerRels))
		{
			for(int i=0,size =offerRels.size();i<size;i++)
			{
				IData offerRel = offerRels.getData(i);
				String offerCode = offerRel.getString("OFFER_CODE");
				String offerType = offerRel.getString("OFFER_TYPE");
				OfferCfg offerCfg = OfferCfg.getInstance(offerCode, offerType);
				boolean isMain = offerCfg.isMain();
				if(isMain){
					return offerRel;
				}
			}
		}
		return null;
	}
	
	
	public static IDataset findMPMaxStartDate(IDataset offerRels) throws Exception{
		if(ArrayUtil.isEmpty(offerRels)){
			return null;
		}
		IDataset rst = new DatasetList();
		for(Object obj : offerRels){
			IData offerRel = (IData)obj;
			String offerType = offerRel.getString("OFFER_TYPE");
			if(!BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerType)){
				continue;
			}
			
			String offerCode = offerRel.getString("OFFER_CODE");
			String startDate = offerRel.getString("START_DATE");
			OfferCfg offerCfg = OfferCfg.getInstance(offerCode, offerType);
			boolean isMain = offerCfg.isMain();
			if(!isMain){
				continue;
			}
			String relOfferInsId = offerRel.getString("REL_OFFER_INS_ID");
			boolean isFind = false;
			for(Object object : offerRels){
				IData tempRel = (IData)object;
				String tempOfferCode = tempRel.getString("OFFER_CODE");
				String tempOfferType = tempRel.getString("OFFER_TYPE");
				String tempRelOfferInsId = tempRel.getString("REL_OFFER_INS_ID");
				if(!BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(tempOfferType)){
					continue;
				}
				if(!tempRelOfferInsId.equals(relOfferInsId)){
					continue;
				}
				if(tempRelOfferInsId.equals(relOfferInsId) && tempOfferCode.equals(offerCode) && tempOfferType.equals(offerType)){
					//完全相同
					continue;
				}
				String tempStartDate = tempRel.getString("START_DATE");
				if(SysDateMgr.compareTo(tempStartDate, startDate) < 0){
					continue;
				}
				OfferCfg tempOfferCfg = OfferCfg.getInstance(tempOfferCode, tempOfferType);
				boolean tempIsMain = tempOfferCfg.isMain();
				if(!tempIsMain){
					continue;
				}
				isFind = true;
				break;
			}
			if(!isFind){
				//表示是最大的那条
				rst.add(offerRel);
			}
		}
		return rst;
	}
	
	public static <T extends ProductModuleTradeData> List<T> fillStructAndFilter(List<T> elements, List<OfferRelTradeData> offerRels) throws Exception{
		if(ArrayUtil.isEmpty(elements) || ArrayUtil.isEmpty(offerRels)){
			return elements;
		}
		
		offerRels = findMPMaxStartDate(offerRels);
		List<T> rst = new ArrayList<T>();
		for(T element : elements){
			String instId = element.getInstId();
			for(OfferRelTradeData offerRel : offerRels){
				String relOfferInsId = offerRel.getRelOfferInsId();
				if(relOfferInsId.equals(instId)){
					String offerCode = offerRel.getOfferCode();
					String groupId = offerRel.getGroupId();
					element.setProductId(offerCode);
					element.setPackageId(groupId);
					rst.add(element);
					break;
				}
			}
		}
		return rst;
	}
	
	public static List<OfferRelTradeData> findMPMaxStartDate(List<OfferRelTradeData> offerRels) throws Exception{
		if(ArrayUtil.isEmpty(offerRels)){
			return null;
		}
		List<OfferRelTradeData> rst = new ArrayList<OfferRelTradeData>();
		for(OfferRelTradeData offerRel : offerRels){
			String offerType = offerRel.getOfferType();
			if(!BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerType)){
				continue;
			}
			
			String offerCode = offerRel.getOfferCode();
			String startDate = offerRel.getStartDate();
			OfferCfg offerCfg = OfferCfg.getInstance(offerCode, offerType);
			boolean isMain = offerCfg.isMain();
			if(!isMain){
				continue;
			}
			String relOfferInsId = offerRel.getRelOfferInsId();
			boolean isFind = false;
			for(OfferRelTradeData tempRel : offerRels){
				String tempOfferCode = tempRel.getOfferCode();
				String tempOfferType = tempRel.getOfferType();
				String tempRelOfferInsId = tempRel.getRelOfferInsId();
				if(!BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(tempOfferType)){
					continue;
				}
				if(!tempRelOfferInsId.equals(relOfferInsId)){
					continue;
				}
				if(tempRelOfferInsId.equals(relOfferInsId) && tempOfferCode.equals(offerCode) && tempOfferType.equals(offerType)){
					//完全相同
					continue;
				}
				String tempStartDate = tempRel.getStartDate();
				if(SysDateMgr.compareTo(tempStartDate, startDate) < 0){
					continue;
				}
				OfferCfg tempOfferCfg = OfferCfg.getInstance(tempOfferCode, tempOfferType);
				boolean tempIsMain = tempOfferCfg.isMain();
				if(!tempIsMain){
					continue;
				}
				isFind = true;
				break;
			}
			if(!isFind){
				//表示是最大的那条
				rst.add(offerRel);
			}
		}
		return rst;
	}
	
	public static void fillElementsProductIdAndPackageId(IDataset elements, IDataset offerRels) throws Exception
	{
	    for(Object obj : elements){
            IData element = (IData)obj;
            String instId = element.getString("INST_ID");
            for(Object object : offerRels){
                IData offerRel = (IData)object;
                String relOfferInsId = offerRel.getString("REL_OFFER_INS_ID");
                if(relOfferInsId.equals(instId)){
                    String offerCode = offerRel.getString("OFFER_CODE");
                    String offerType = offerRel.getString("OFFER_TYPE");
                    if("P".equals(offerType))
                    {
                        String groupId = offerRel.getString("GROUP_ID","-1");
                        element.put("PRODUCT_ID", offerCode);
                        element.put("PACKAGE_ID",  groupId);
                    }else if("K".equals(offerType))
                    {
                        String offerInsId = offerRel.getString("OFFER_INS_ID");
                        IDataset userSaleActiveInfos = UserSaleActiveInfoQry.queryUserSaleActiveByInstId(offerInsId);
                        element.put("PRODUCT_ID", userSaleActiveInfos.getData(0).getString("PRODUCT_ID"));
                        
                        element.put("PACKAGE_ID", offerCode);
                    }
                }
            }
        }
	}
}
