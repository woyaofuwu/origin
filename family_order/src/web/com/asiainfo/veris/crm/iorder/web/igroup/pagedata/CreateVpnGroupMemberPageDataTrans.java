package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.biz.exception.BizErr;
import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class CreateVpnGroupMemberPageDataTrans extends PageDataTrans{


	private String shortCode = "";//记录短号
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
        	//添加是否需要二次确认的判断
            for(int i=0;i<compOfferChaList.size();i++){
            	IData compOfferCha = compOfferChaList.getData(i);
            	if("TWOCHECK_SMS_FLAG".equals(compOfferCha.getString("ATTR_CODE"))){
            		data.put("PAGE_SELECTED_TC", compOfferCha.getString("ATTR_VALUE"));
            		break;
            	}
            }
        	
            data.put("PRODUCT_PARAM_INFO", transformOfferChaList(getProductId(), compOfferChaList));
            
            data.put("SHORT_CODE", shortCode);
            
        }
        
        IData commonInfo = getCommonData();
        if(DataUtils.isNotEmpty(commonInfo))
        {
        	data.put("MEM_ROLE_B", commonInfo.getString("ROLE_CODE_B"));
            data.put("PLAN_TYPE_CODE", commonInfo.getString("PLAN_TYPE_CODE"));
            data.put("FEE_TYPE", commonInfo.getString("FEE_TYPE"));
            data.put("LIMIT_TYPE", commonInfo.getString("LIMIT_TYPE"));
            data.put("LIMIT", commonInfo.getString("LIMIT"));
            data.put("EFFECT_NOW",commonInfo.getString("EFFECT_NOW")); //0-下月；1-立即
            data.put("IF_BOOKING", "0".equals(commonInfo.getString("EFFECT_NOW"))?true:false); //0-下月；1-立即
            data.put("REMARK", commonInfo.getString("REMARK"));
            data.put("MEB_FILE_LIST", commonInfo.getString("MEB_FILE_LIST"));
            data.put("MEB_FILE_SHOW", commonInfo.getString("MEB_FILE_SHOW"));
            data.put("PLAN_TYPE_CODE", commonInfo.getString("PLAN_TYPE_CODE"));
        	
        	IData resinfo = new DataMap();
        	resinfo.put("RES_TYPE_CODE", "S");
        	resinfo.put("RES_CODE", shortCode);
        	resinfo.put("MODIFY_TAG", "0");
        	resinfo.put("CHECKED","true");
        	resinfo.put("DISABLED","true");

            IDataset resInfoList = new DatasetList();
            resInfoList.add(resinfo);

            data.put("RES_INFO", resInfoList);

        }
        
        // 集团用户信息
        IData ecSubscriber =  getEcSubscriber();
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
    	String brandCode = getBrandCode();
    	String productId = getProductId();
    	if("BOSG".equals(brandCode)){
    		 setSvcName("CS.CreateBBossMemSVC.crtOrder");
    	}else if ("10005742".equals(productId)){
    		setSvcName("SS.CreateAdcGroupMemberSVC.crtOrder");
    	}
    	else{
            setSvcName(EcConstants.EC_OPER_SERVICE.CREATE_ENTERPRISE_MEMBER.getValue());
        }

    }

    protected IDataset transformOfferChaList(String offerCode, IDataset offerChaList) throws Exception
    {
        IDataset paramAttrList = new DatasetList();
        
        if(IDataUtil.isNotEmpty(offerChaList))
        {
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
        		}else if("TWOCHECK_SMS_FLAG".equals(paramAttr.getString("ATTR_CODE",""))){
        			continue;
        		}else{
        			offerChaList.remove(i);
        			i--;
        		}
        	}
        	
            IData paramAttr = new DataMap();
            paramAttr.put("PRODUCT_ID", offerCode);
            paramAttr.put("PRODUCT_PARAM", offerChaList);
            paramAttrList.add(paramAttr);
        }
        
        return paramAttrList;
    }

}
