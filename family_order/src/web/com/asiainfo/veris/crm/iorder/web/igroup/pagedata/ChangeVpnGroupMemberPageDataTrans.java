package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.biz.exception.BizErr;
import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class ChangeVpnGroupMemberPageDataTrans extends PageDataTrans{

	private String shortCode = "";
	private String grpClipType = "";//呼叫来显方式
	private String grpUserClipType = "";//选择号显方式
	private String grpUserMod = ""; //成员修改号显方式
	private String clipType = ""; //呼叫显示方式
	private String oldClipType = ""; //呼叫显示方式
	
	@Override
    public IData transformData() throws Exception
    {
        super.transformData();

        IData data = new DataMap();
        
        IDataset memberOffers = getOfferList();
        if(DataUtils.isEmpty(memberOffers))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到成员商品数据结构！");
        }
        
        IDataset offerInfoList = transformOfferList(memberOffers);
        data.put("ELEMENT_INFO", offerInfoList);
        
        IDataset compOfferChaList = getOfferChaList();
         
        if(DataUtils.isNotEmpty(compOfferChaList))
        {        	
            data.put("PRODUCT_PARAM_INFO", transformOfferChaList(getProductId(), compOfferChaList));
        	//如果短号有修改,先处理资源信息
            if(DataUtils.isNotEmpty(compOfferChaList)){
            	for(int i=0;i<compOfferChaList.size();i++){
            		IData comOfferCha = compOfferChaList.getData(i);
            		if("SHORT_CODE".equals(comOfferCha.getString("ATTR_CODE"))){
            			String newCode = comOfferCha.getString("ATTR_VALUE");
            			String oldCode = comOfferCha.getString("OLD_ATTR_VALUE", "");
            			IDataset resInfos = new DatasetList();
            			if(StringUtils.isNotBlank(oldCode) && !(newCode.equals(oldCode))){
            				IData addres = new DataMap();
            				addres.put("RES_TYPE_CODE", "S");
            				addres.put("MODIFY_TAG", "1");
            				addres.put("RES_CODE", oldCode);
            				addres.put("CHECKED", "true");
            				addres.put("DISABLED", "true");
            				resInfos.add(addres);
            				
            				IData delres = new DataMap();
            				delres.put("RES_TYPE_CODE", "S");
            				delres.put("MODIFY_TAG", "0");
            				delres.put("RES_CODE", newCode);
            				delres.put("CHECKED", "true");
            				delres.put("DISABLED", "true");
            				resInfos.add(delres);
            			}
            			
            			data.put("RES_INFO", resInfos);
            		}
            	}
            }
        }
        
        IData commonInfo = getCommonData();
        if(DataUtils.isNotEmpty(commonInfo))
        {
            data.put("MEM_ROLE_B", commonInfo.getString("ROLE_CODE_B"));
            data.put("REMARK", commonInfo.getString("REMARK"));
            data.put("MEB_FILE_LIST", commonInfo.getString("MEB_FILE_LIST"));
            data.put("MEB_FILE_SHOW", commonInfo.getString("MEB_FILE_SHOW"));
        }
        
        // 集团用户信息
        IData ecSubscriber = getEcSubscriber();
        if(DataUtils.isEmpty(ecSubscriber))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到集团用户信息数据结构！");
        }
        data.put("USER_ID", ecSubscriber.getString("USER_ID"));
        
        //成员用户信息
        IData memSubscriber = getMemSubscriber();
        if(DataUtils.isEmpty(memSubscriber))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到成员用户信息数据结构！");
        }
        data.put("SERIAL_NUMBER", memSubscriber.getString("SERIAL_NUMBER"));
        
        return data;
    }
    
    public void setServiceName() throws Exception
    {
//    	String brandCode = getBrandCode();
//    	String productId = getProductId();
        setSvcName(EcConstants.EC_OPER_SERVICE.CHANGE_ENTERPRISE_MEMBER.getValue());

    }
    
    protected IDataset transformOfferChaList(String offerCode, IDataset offerChaList) throws Exception
    {
        IDataset paramAttrList = new DatasetList();
        
        if(IDataUtil.isNotEmpty(offerChaList))
        {
        	//塞入NOTIN_OLD_SHORT_CODE字段
        	IData oldShortCode = new DataMap();
        	
        	for(int i=0;i<offerChaList.size();i++){
        		IData paramAttr = offerChaList.getData(i);
        		if("SHORT_CODE".equals(paramAttr.getString("ATTR_CODE",""))){
        			shortCode = paramAttr.getString("ATTR_VALUE","");
        		}else if("NOTIN_CLIP_TYPE".equals(paramAttr.getString("ATTR_CODE",""))){
        			clipType = paramAttr.getString("ATTR_VALUE","");
        		}else if("NOTIN_OLD_CLIP_TYPE".equals(paramAttr.getString("ATTR_CODE",""))){
        			oldClipType = paramAttr.getString("ATTR_VALUE","");
        		}else if("NOTIN_GRP_CLIP_TYPE".equals(paramAttr.getString("ATTR_CODE",""))){
        			grpClipType = paramAttr.getString("ATTR_VALUE","");
        		}else if("NOTIN_GRP_USER_CLIP_TYPE".equals(paramAttr.getString("ATTR_CODE",""))){
        			grpUserClipType = paramAttr.getString("ATTR_VALUE","");
        		}else if("NOTIN_GRP_USER_MOD".equals(paramAttr.getString("ATTR_CODE",""))){
        			grpUserMod = paramAttr.getString("ATTR_VALUE","");
        		}else if("SHORT_CODE1".equals(paramAttr.getString("ATTR_CODE",""))){
        			String oldCode = paramAttr.getString("ATTR_VALUE", "");
        			oldShortCode.put("ATTR_VALUE", oldCode);
        			oldShortCode.put("ATTR_CODE", "NOTIN_OLD_SHORT_CODE");
        			
        			offerChaList.remove(i);
        			i--;
        		
        		}else{
        			offerChaList.remove(i);
        			i--;
        		}
        	}
        	
        	offerChaList.add(oldShortCode);
        	
            IData paramAttr = new DataMap();
            paramAttr.put("PRODUCT_ID", offerCode);
            paramAttr.put("PRODUCT_PARAM", offerChaList);
            paramAttrList.add(paramAttr);
        }
        
        return paramAttrList;
    }
	
}
