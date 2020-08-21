package com.asiainfo.veris.crm.iorder.soa.group.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.callout.IUpcCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

public class QueryAttrParamBean extends CSBizBean
{

    /**
     * 商品新增属性初始化
     * @param param
     * @return
     * @throws Exception
     */
    public static IData queryOfferChaForInit(IData param) throws Exception
    {
        IData offerChaData = new DataMap();
        IDataset attrItemList = queryUpcOfferChaList(param);
        if(IDataUtil.isEmpty(attrItemList))
        {
            return offerChaData;
        }
        for(int i = 0, size = attrItemList.size(); i < size; i++)
        {
            IData attrItem = attrItemList.getData(i);
            IData offerChaVal = new DataMap();
            offerChaVal.put("CHA_SPEC_ID", attrItem.getString("CHA_SPEC_ID"));
            offerChaVal.put("CHA_SPEC_NAME", attrItem.getString("CHA_SPEC_NAME"));
            offerChaVal.put("FIELD_NAME", attrItem.getString("FIELD_NAME"));
            offerChaVal.put("ATTR_VALUE", attrItem.getString("DEFAULT_VALUE"));
            
            IDataset dataValList = dealDataValList(attrItem.getDataset("DATA_VAL"));
            if(IDataUtil.isNotEmpty(dataValList))
            {
                offerChaVal.put("DATA_VAL", dataValList);
            }
            String FIELD_NAME = attrItem.getString("FIELD_NAME");
            offerChaData.put(StringUtils.isNumeric(FIELD_NAME)?"DATA_"+FIELD_NAME:FIELD_NAME, offerChaVal);
        }
        return offerChaData;
    }
    
    /**
     * 商品变更属性初始化
     * @param input
     * @return
     * @throws Exception
     */
    public static IData queryUserAttrForChgInit(IData input) throws Exception
    {
        IData offerChaData = new DataMap();
        
        IDataset userAttrList = queryUserAttrList(input);
        
        IDataset attrItemList = queryUpcOfferChaList(input);
        
        if(IDataUtil.isNotEmpty(userAttrList))
        {
            for(int i = 0, sizeI = userAttrList.size(); i < sizeI; i++)
            {
                IData userAttr = userAttrList.getData(i);
                String attrCode = userAttr.getString("ATTR_CODE", "");
                IData offerCha = new DataMap();
                if (!"".equals(attrCode) && attrCode.length() > 3 && attrCode.substring(0, 3).equals("FEE"))
                {//费用属性值特殊处理
                    String attrValue = userAttr.getString("ATTR_VALUE", "0");
                    userAttr.put("ATTR_VALUE", Integer.parseInt(attrValue) / 100);
                }
                if (attrCode.equals("30011337") || attrCode.equals("30011117") || attrCode.equals("30011107")
                		|| attrCode.equals("30011327")|| attrCode.equals("30011137")|| attrCode.equals("30011127")
                		|| attrCode.equals("30011237")|| attrCode.equals("30011227")|| attrCode.equals("40011107")
                		|| attrCode.equals("40011108")|| attrCode.equals("40011127")|| attrCode.equals("40011128")
                		|| attrCode.equals("40011227")|| attrCode.equals("40011228")|| attrCode.equals("40011327")
                		|| attrCode.equals("40011328")) {
                	String attrValue = userAttr.getString("ATTR_VALUE");
					userAttr.put("ATTR_VALUE", Float.parseFloat(attrValue) / 1000);
					//IData offerCha = new DataMap();
	                offerCha.put("FIELD_NAME", userAttr.getString("ATTR_CODE"));
	                offerCha.put("ATTR_VALUE", Float.parseFloat(attrValue) / 1000);
	                
				}
                else {
                	//IData offerCha = new DataMap();
                    offerCha.put("FIELD_NAME", userAttr.getString("ATTR_CODE"));
                    offerCha.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
                    
				}
                /*IData offerCha = new DataMap();
                offerCha.put("FIELD_NAME", userAttr.getString("ATTR_CODE"));
                System.out.print("获取FIELD_NAME:"+offerCha.put("FIELD_NAME", userAttr.getString("ATTR_CODE")));
                offerCha.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
                System.out.print("获取FIELD_NAME下的:"+offerCha.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE")));
                */
                for(int j = 0, sizeJ = attrItemList.size(); j < sizeJ; j++)
                {
                    IData attrItem = attrItemList.getData(j);
                    if(attrCode.equals(attrItem.getString("FIELD_NAME")))
                    {
                        offerCha.put("CHA_SPEC_NAME", attrItem.getString("CHA_SPEC_NAME"));
                        
                        IDataset dataValList = dealDataValList(attrItem.getDataset("DATA_VAL"));
                        if(IDataUtil.isNotEmpty(dataValList))
                        {
                            offerCha.put("DATA_VAL", dataValList);
                        }
                    }
                }
                offerChaData.put(userAttr.getString("ATTR_CODE"), offerCha);
            }
        }
        return offerChaData;
    }
    
    /**
     * 查询产商品中心配置的商品特征
     */
    protected static IDataset queryUpcOfferChaList(IData input) throws Exception
    {
        String offerId = input.getString("OFFER_ID", ""); //当前设置商品的offerId
        String offerType = input.getString("INST_TYPE");
        String ecOfferId = input.getString("EC_OFFER_ID", "");
        String attrObj = input.getString("ATTR_OBJ");
        String eparchyCode = input.getString("EPARCHY_CODE");
        if(input.getBoolean("IS_MEB"))
        {
            IDataset mebOfferList = IUpcCall.queryMebOffersByEcOfferId(ecOfferId);
            if(IDataUtil.isNotEmpty(mebOfferList) && offerId.equals(mebOfferList.first().getString("OFFER_ID")))
            {//如果当前设置的商品是成员主商品
                offerId = ecOfferId;
            }
        }
        if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(offerType) || BofConst.ELEMENT_TYPE_CODE_SVC.equals(offerType))
        {
            attrObj = null;
        }
        IDataset attrItemList = IUpcCall.queryOfferChaAndValByCond(offerId, attrObj, null, eparchyCode);
        
        return IDataUtil.isEmpty(attrItemList) ? new DatasetList() : attrItemList;
    }
    
    protected static IDataset queryUserAttrList(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String instType = input.getString("INST_TYPE");
        String relaInstId = input.getString("OFFER_INS_ID");
        IDataset userAttrList = UserAttrInfoQry.getUserAttrByUserIdInstid(userId, instType, relaInstId);
        
        return IDataUtil.isEmpty(userAttrList) ? new DatasetList() : userAttrList;
    }
    
    /**
     * 设置属性下拉框枚举值
     */
    protected static IDataset dealDataValList(IDataset dataValList) throws Exception
    {
        IDataset dataVals = new DatasetList();
        if(IDataUtil.isNotEmpty(dataValList))
        {
            for(int k = 0, sizeK = dataValList.size(); k < sizeK; k++)
            {
                IData dv = dataValList.getData(k);
                IData dataVal = new DataMap();
                dataVal.put("CHA_SPEC_ID", dv.getString("CHA_SPEC_ID"));
                dataVal.put("TEXT", dv.getString("TEXT"));
                dataVal.put("VALUE", dv.getString("VALUE"));
                dataVals.add(dataVal);
            }
        }
        return dataVals;
    }


    public static IData getData(String attr_value,String cha_spec_id,String field_name,String cha_spec_name){
        IData data = new DataMap();
        data.put("ATTR_VALUE",attr_value);
        data.put("CHA_SPEC_ID",cha_spec_id);
        data.put("FIELD_NAME",field_name);
        data.put("CHA_SPEC_NAME",cha_spec_name);
        return data;
    }
    
    
    
}

