package com.asiainfo.veris.crm.iorder.soa.group.param.bboss;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.callout.IUpcCall;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BBossAttrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;




public class QueryBBossAttrParamBean extends  QueryAttrParamBean{
    /**
     * 商品受理属性初始化
     * @param input
     * @return
     * @throws Exception
     */
    public static IData queryBBossUserAttrForChaInit(IData input) throws Exception
    {
        //1- 定义返回值
        IData result = new DataMap();
        IDataset productPlusInfoList = new DatasetList();
        //2-夹带参数
        IData param = new DataMap();
        param.put("IS_MEB",input.getString("IS_MEB"));
        //2- 获取产品参数配置表数据
        IData offerInfo = UpcCall.queryOfferByOfferId(null,input.getString("OFFER_ID"));
        String productSpecCode =StaticUtil.getStaticValue(getVisit(), "TD_B_ATTR_BIZ", new String[]
                { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
                { "1", "B", input.getString("PRODUCT_ID"), "PRO" });
        IData inparams = new DataMap();
        inparams.put("PRODUCTSPECNUMBER", productSpecCode);
        inparams.put("BIZ_TYPE", "1");// 业务类型：1-集团业务，2-成员业务
        IDataset bbossAttrInfoList = CSAppCall.call("CS.BBossAttrQrySVC.qryBBossAttrByPospecBiztype", inparams);
        if(IDataUtil.isEmpty(bbossAttrInfoList)){
            return result;
        }
        //3-特殊参数处理
        IData sendData = new DataMap();
        sendData.put("CUST_ID",input.getString("CUST_ID"));
        IData resultData = CSAppCall.call("CS.CustGroupInfoQrySVC.qryGroupByCustID",sendData).getData(0);
        modifyParamBySpecialBiz(resultData.getString("GROUP_ID"),null,productSpecCode,bbossAttrInfoList,param);
        //4-封装数据
        IData offerChaData = new DataMap();
        for(int i = 0, size = bbossAttrInfoList.size(); i < size; i++)
        {
            IData attrItem = bbossAttrInfoList.getData(i);
            IData offerChaVal = new DataMap();
            offerChaVal.put("CHA_SPEC_ID", attrItem.getString("ATTR_CODE"));
            offerChaVal.put("CHA_SPEC_NAME", attrItem.getString("ATTR_NAME"));
            offerChaVal.put("FIELD_NAME", attrItem.getString("ATTR_CODE"));
            offerChaVal.put("ATTR_VALUE", attrItem.getString("DEFAULT_VALUE"));
            offerChaVal.put("ATTR_CODE","B"+attrItem.getString("ATTR_CODE"));
            IDataset dataValList = dealDataValList(attrItem.getDataset("DATA_VAL"));
            if(IDataUtil.isNotEmpty(dataValList))
            {
                offerChaVal.put("DATA_VAL", dataValList);
            }
            String FIELD_NAME = attrItem.getString("FIELD_NAME");
            offerChaData.put("B"+attrItem.getString("ATTR_CODE"), offerChaVal);
        }
        //

        //offerChaData.put("RUSELT",bbossAttrInfoList);

        return offerChaData;
    }

	/**
     * 商品变更属性初始化
     * @param input
     * @return
     * @throws Exception
     */
    public static IData queryBBossUserAttrForChgInit(IData input) throws Exception
    {
        IData offerChaData = new DataMap();
        
        IDataset userAttrList = queryUserAttrList(input);
        
        IDataset attrItemList = queryUpcOfferChaList(input);
        
        String isMeb = input.getString("IS_MEB");
        
        String offerId = input.getString("OFFER_ID");//成员产品
        
        String offerCode = input.getString("OFFER_CODE");//成员产品ID
        String ecOfferId = input.getString("EC_OFFER_ID");
        String operType = input.getString("OPER_TYPE");
        String mbUserId = input.getString("USER_ID");
        String ecUserId = input.getString("EC_USER_ID"); 
        String eparchyCode = input.getString("ROUTE_EPARCHY_CODE"); 
        String ecOfferCode = input.getString("EC_OFFER_CODE"); //子商品的offer_code
        
        //成员新增 成员操作类型初始化
        if("CrtMb".equals(operType)){
        	return initCrtMbSelectType();
        }
        
        if(IDataUtil.isNotEmpty(userAttrList))
        {
            IData attrGroup = new DataMap();
            for(int i = 0, sizeI = userAttrList.size(); i < sizeI; i++)
            {
                IData userAttr = userAttrList.getData(i);
                String attrCode = userAttr.getString("ATTR_CODE", "");
                //BBOSS中央ADC下行业报产品含有带前缀属性，在前台展示属性时需去掉前缀
    			//产品属性带有前缀或后缀，需特殊处理
    			IDataset bbossAttrParam = BBossAttrQry.qryBBossAttrByAttrCode(attrCode);
    			String frontPart = "";
    			if(IDataUtil.isNotEmpty(bbossAttrParam)){
    				IData bbossAttr = bbossAttrParam.getData(0);
    				frontPart =  bbossAttr.getString("FRONT_PART");
    			}
                String userAttrValue = userAttr.getString("ATTR_VALUE");

    			if(frontPart != null && !"".equals(frontPart)){
    				userAttr.put("ATTR_VALUE", userAttrValue.substring(frontPart.length()));
    			}	
                
                if (!"".equals(attrCode) && attrCode.length() > 3 && attrCode.substring(0, 3).equals("FEE"))
                {//费用属性值特殊处理
                    String attrValue = userAttr.getString("ATTR_VALUE", "0");
                    userAttr.put("ATTR_VALUE", Integer.parseInt(attrValue) / 100);
                }
                IData offerCha = new DataMap();
                //BBOSS属性组特殊处理

                if(userAttr.getString("RSRV_STR4")!=null) {
                    offerCha.put("FIELD_NAME", userAttr.getString("ATTR_CODE")+"_"+userAttr.getString("RSRV_STR4"));
                    offerCha.put("ATTR_GROUP", userAttr.getString("RSRV_STR4"));
                }else{
                    offerCha.put("FIELD_NAME", userAttr.getString("ATTR_CODE"));
                }

                offerCha.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
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
                //BBOSS属性组特殊处理
                if(userAttr.getString("RSRV_STR4")!=null) {
                    //offerChaData.put(getchaSpecID(isMeb,offerCode,userAttr.getString("ATTR_CODE"))+"_"+userAttr.getString("RSRV_STR4"), offerCha);
                    IDataset attrGroupList;
                    if(null==attrGroup.getDataset(getchaSpecID(isMeb,ecOfferCode,userAttr.getString("ATTR_CODE")))){
                        attrGroupList = new DatasetList();
                        attrGroupList.add(offerCha);

                    }else{
                        attrGroupList = attrGroup.getDataset(getchaSpecID(isMeb,ecOfferCode,userAttr.getString("ATTR_CODE")));
                        attrGroupList.add(offerCha);
                    }
                    attrGroup.put(getchaSpecID(isMeb,ecOfferCode,userAttr.getString("ATTR_CODE")),attrGroupList);
                }else{
                    offerChaData.put(getchaSpecID(isMeb,ecOfferCode,userAttr.getString("ATTR_CODE")), offerCha);
                }

            }
            offerChaData.put("ATTR_GROUP_MAP",attrGroup);
        }
        
        //成员变更 成员操作类型初始化
        if("ChgMb".equals(operType)){
			
        	offerChaData.put("MB_SELECT_TYPE", getMebOperCodeForChg(ecUserId, mbUserId, eparchyCode, ecOfferCode));
        }
       
        return offerChaData;
    }
    
    private static IData initCrtMbSelectType() throws Exception{
    	IData returnData = new DataMap();
    	IData B710000000734 = new DataMap();
        B710000000734.put("FIELD_NAME","成员操作类型");
        IDataset tempDataList = new DatasetList();
        IData tempData1 = new DataMap();
        tempData1.put("TEXT", "新增");
        tempData1.put("VALUE", "1");
        tempDataList.add(tempData1);
        B710000000734.put("DATA_VAL", tempDataList);
        returnData.put("MB_SELECT_TYPE", B710000000734);
        return  returnData;
    }
    
    
    private static IData initChgMbSelectType(String ecUserId,String mbUserId,String eparchyCode) throws Exception{
    	
    	String status = getMebOperCode(mbUserId ,ecUserId,eparchyCode); 
    	IData B710000000734 = new DataMap();
    	if("N".equals(status)){
    		
            B710000000734.put("FIELD_NAME","成员操作类型");
            IDataset tempDataList = new DatasetList();
            IData tempData1 = new DataMap();
            tempData1.put("TEXT", "恢复");
            tempData1.put("VALUE", "4");
            tempDataList.add(tempData1);
            B710000000734.put("DATA_VAL", tempDataList);
    	}else{  
	        B710000000734.put("FIELD_NAME","成员操作类型");
	        IDataset tempDataList = new DatasetList();
	        
	        IData tempData1 = new DataMap();
	        tempData1.put("TEXT", "变更成员扩展属性");
	        tempData1.put("VALUE", "6");
	        tempDataList.add(tempData1);
	        
	        IData tempData2 = new DataMap();
	        tempData2.put("TEXT", "暂停");
	        tempData2.put("VALUE", "3");
	        tempDataList.add(tempData2);
	        
	        IData tempData3 = new DataMap();
	        tempData3.put("TEXT", "删除");
	        tempData3.put("VALUE", "0");
	        tempDataList.add(tempData3);
	        
	        B710000000734.put("DATA_VAL", tempDataList);
    	}
        return  B710000000734;
    }
    
    /**
     * @description 设置成员的操作类型()
     * @author weixb3
     * @version 创建时间：2013-6-28
     */
    public static IData getMebOperCodeForChg(String ecUserId, String mbUserId, String eparchyCode, String productId) throws Exception
    {
    	
    	IData B710000000734 = new DataMap();
        String status = getMebOperCode(mbUserId, ecUserId, eparchyCode);
        IDataset mebOpers = new DatasetList();// 成员能够使用的操作类型

        if (status != null)
        {
            if ("N".equals(status))
            {
                // 如果是暂停状态

                IData oper = new DataMap();
                oper.put("VALUE", "4");
                oper.put("TEXT", "恢复");
                mebOpers.add(oper);
            }
            else
            {
            	IData params = new DataMap();
            	params.put("ID", productId);
            	params.put("ID_TYPE", "P");
            	params.put("ATTR_OBJ", "0");
            	params.put("ATTR_CODE", "CHGMEMBEROPTYPE");
                IDataset operTypeInfoList = CSAppCall.call("CS.AttrBizInfoQrySVC.getBizAttr", params);  ////AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, this.productId, "P", "0", "CHGMEMBEROPTYPE");
                if (IDataUtil.isEmpty(operTypeInfoList))
                {
                    return B710000000734;
                }

                String operTypelist = operTypeInfoList.getData(0).getString("ATTR_VALUE");
                String[] operTypeArr = operTypelist.split(",");
                for (int i = 0; i < operTypeArr.length; i++)
                {
                    IData oper = new DataMap();
                    String operType = operTypeArr[i];
                    String operName = getOperNameByOperType(operType);

                    oper.put("VALUE", operType);
                    oper.put("TEXT", operName);
                    mebOpers.add(oper);
                }
                B710000000734.put("DATA_VAL", mebOpers);
            }
        }
        return B710000000734;
    }
    
    /**
     * 根据操作code确定操作名
     * 
     * @param operType
     * @return
     * @author chenkh 2014年9月26日
     */
    public static String getOperNameByOperType(String operType)
    {
        String operName = "";
        if ("0".equals(operType))
        {
            operName = "删除";
        }
        else if ("6".equals(operType))
        {
            operName = "变更成员扩展属性";
        }
        else if ("3".equals(operType))
        {
            operName = "暂停";
        }
        return operName;
    }
    
    /**
     * 设置成员类型
     * @param ecUserId
     * @param mbUserId
     * @param productId
     * @param eparchyCode
     * @throws Exception
     */
    private void setChgMebTypes(String ecUserId, String mbUserId, String productId, String eparchyCode) throws Exception
    {
        String mebType = this.getMebType(mbUserId, ecUserId, eparchyCode);

        // 设置成员类型IDATESET
        IDataset mebTypeSet = StaticUtil.getList(getVisit(), "TD_B_ATTR_BIZ", "ATTR_VALUE", "ATTR_NAME", new java.lang.String[]
        { "ID", "ID_TYPE", "ATTR_OBJ", "ATTR_CODE" }, new java.lang.String[]
        { "1", "B", "MTYPE", productId });
        if (mebTypeSet.size() == 0)
        {
            mebTypeSet = new DatasetList();
            IData temp = new DataMap();
            temp.put("ATTR_VALUE", "1");
            temp.put("ATTR_NAME", "签约关系");
            mebTypeSet.add(temp);
            mebType = "1";
        }
        //setMebType(mebType);
        //setMebTypeSet(mebTypeSet);
    }
    /**
     * 获取成员类型
     * 
     * @param memUserId
     * @param grpUserId
     * @author xunyl
     * @date 2013-04-28
     * @return
     * @throws Exception
     */
    public String getMebType(String memUserId, String grpUserId, String eparchyCode) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", memUserId);
        inparams.put("GRP_USER_ID", grpUserId);
        inparams.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset merchMebs = CSAppCall.call("CS.UserGrpMerchInfoQrySVC.getSEL_BY_USERID_USERIDA", inparams);
        if (merchMebs != null && merchMebs.size() > 0)
        {
            return merchMebs.getData(0).getString("RSRV_TAG1");// 成员类型
        }
        return "";
    }
    
    protected static String getMebOperCode(String mebUserId ,String grpUserId,String eparchyCode) throws Exception
    {
    	 IData inparams = new DataMap();
         inparams.put("USER_ID", mebUserId);
         inparams.put("GRP_USER_ID", grpUserId);
         inparams.put("ROUTE_EPARCHY_CODE", eparchyCode);
         IDataset merchMebs = UserGrpMerchMebInfoQry.getSEL_BY_USERID_USERIDA(mebUserId, grpUserId, eparchyCode);
         String status = merchMebs.first().getString("STATUS","A");
         return status;
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
     * 查询产商品中心配置的商品特征
     */
    protected static IDataset queryUpcOfferChaList(IData input) throws Exception
    {
        String offerId = input.getString("OFFER_ID", ""); //当前设置商品的offerId
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
        IDataset attrItemList = IUpcCall.queryOfferChaAndValByCond(offerId, attrObj, null, eparchyCode);
        
        return IDataUtil.isEmpty(attrItemList) ? new DatasetList() : attrItemList;
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
    
    /**
	 * 成员需要对specid进行处理
	 * @param isMeb
	 * @param offerCode
	 * @param chaSpecId
	 * @return
	 * @throws Exception 
	 */
	public static String getchaSpecID(String isMeb, String ecOfferCode, String chaSpecId) throws Exception{
		
		if("true".equals(isMeb)){					
			//成员产品是否存在
			if(ecOfferCode == null || ecOfferCode.equals("")){
				return chaSpecId;
			}
			String productSpecCode=GrpCommonBean.productToMerch(ecOfferCode, 0);
			chaSpecId="M"+productSpecCode+chaSpecId;
			
		}else{
			 chaSpecId="B"+chaSpecId;

		}
		 return chaSpecId;
	}

    /**
     * @descripiton 某些业务参数的特殊处理譬如集团客户一点支付业务的配合省反馈属性组的属性组编号要改为与配合省范围的属性组编号一致
     * @author xunyl
     * @date 2014-12-01
     */
    private static void modifyParamBySpecialBiz(String groupId,String tradeId,String productSpecCode,IDataset bbossAttrInfoList,IData param)throws Exception{
        //1- 根据台帐编号查询merch表，如果该业务属于集团客户一点支付业务，则进行一点支付业务的特殊处理
        if(com.ailk.org.apache.commons.lang3.StringUtils.equals(productSpecCode, "99903")){
            if(com.ailk.org.apache.commons.lang3.StringUtils.isEmpty(tradeId)){
                return;
            }
            //modifyOnePayMemParam(tradeId,bbossAttrInfoList);
        }
        //2- 如果该业务属于省网关全网长流程云MAS商品（010101016），则需要为用户初始化业务代码的值
        if(com.ailk.org.apache.commons.lang3.StringUtils.equals(productSpecCode, "110154") || com.ailk.org.apache.commons.lang3.StringUtils.equals(productSpecCode, "110155") ||
                com.ailk.org.apache.commons.lang3.StringUtils.equals(productSpecCode, "110156") || com.ailk.org.apache.commons.lang3.StringUtils.equals(productSpecCode, "110157") ||
                com.ailk.org.apache.commons.lang3.StringUtils.equals(productSpecCode, "110158") || com.ailk.org.apache.commons.lang3.StringUtils.equals(productSpecCode, "110159") ||
                com.ailk.org.apache.commons.lang3.StringUtils.equals(productSpecCode, "110160") || com.ailk.org.apache.commons.lang3.StringUtils.equals(productSpecCode, "110161") ||
                com.ailk.org.apache.commons.lang3.StringUtils.equals(productSpecCode, "110162")){
            modifyBizCodeValue(groupId,bbossAttrInfoList,param);
        }
    }

    /**
     * @description 处理省网关全网长流程云MAS商品的初始化参数
     * @author xunyl
     * @date 2016-05-09
     */
    private static void modifyBizCodeValue(String groupId,IDataset bbossAttrInfoList,IData param)throws Exception{
        for(int i=0;i<bbossAttrInfoList.size();i++){
            IData bbossAttrInfo = bbossAttrInfoList.getData(i);
            String attrCode = bbossAttrInfo.getString("ATTR_CODE","").substring(1);
            if(StringUtils.equals("101544007", attrCode) || StringUtils.equals("101554007", attrCode) ||
                    StringUtils.equals("101564007", attrCode) || StringUtils.equals("101574010", attrCode) ||
                    StringUtils.equals("101584010", attrCode) || StringUtils.equals("101594010", attrCode) ||
                    StringUtils.equals("101604010", attrCode) || StringUtils.equals("101614010", attrCode) ||
                    StringUtils.equals("101624010", attrCode)){
                // modify by yinxm 2016/7/14 针对特定行业编码，设置固定的业务代码
                // 1101574010 短信  MHN5550201， 1101594010 彩信 1195550201
                if (StringUtils.equals("101574010",attrCode) || StringUtils.equals("101604010",attrCode)) {
                    bbossAttrInfo.put("DEFAULT_VALUE", "MHN5550201");
                } else if (StringUtils.equals("101594010",attrCode) || StringUtils.equals("101624010",attrCode)) {
                    bbossAttrInfo.put("DEFAULT_VALUE", "1195550201");
                } else {
                    StringBuffer strBuf = new StringBuffer();
                    //1- 类别标识，填写“M”，标识为MAS模式的业务；
                    strBuf.append("M");
                    //2- 省公司编码
                    strBuf.append("HN");
                    //3- 行业编码
                    IData inparam = new DataMap();
                    inparam.put("GROUP_ID", groupId);
                    IDataset groupCustInfoList = CSAppCall.call("CS.GrpInfoQrySVC.queryGroupInfosById", inparam);
                    String subCallingTypeCode = groupCustInfoList.getData(0).getString("SUB_CALLING_TYPE_CODE");
                    strBuf.append(subCallingTypeCode);
                    //4- 通信能力
                    if(StringUtils.equals("101544007", attrCode)||StringUtils.equals("101574010",attrCode)||
                            StringUtils.equals("101604010",attrCode)){
                        strBuf.append("1");
                    }else if(StringUtils.equals("101554007", attrCode)||StringUtils.equals("101584010",attrCode)||
                            StringUtils.equals("101614010",attrCode)){
                        strBuf.append("6");
                    }else if(StringUtils.equals("101564007", attrCode)||StringUtils.equals("101594010",attrCode)||
                            StringUtils.equals("101624010",attrCode)){
                        strBuf.append("2");
                    }
                    //5- 业务类别
                    strBuf.append("10");
                    //6- 流水号
                    strBuf.append("01");
                    bbossAttrInfo.put("DEFAULT_VALUE", strBuf.toString());
                }
            }

            //云MAS业务的名单类型选择，做鉴权处理，分公司只能选择白名单，只有省公司能够选择黑名单
    		/*if(StringUtils.equals("101544008", attrCode) || StringUtils.equals("101554008", attrCode) ||
    				StringUtils.equals("101564008", attrCode) ||
    				StringUtils.equals("101604011", attrCode) || StringUtils.equals("101614011", attrCode) ||
    				StringUtils.equals("101624011", attrCode)){
    			boolean isPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "BBOSS_MEBTYPE");
    			if(!isPriv){
    				IData idata = new DataMap();
    				idata.put("OPTION_NAME", "白名单");
    				idata.put("OPTION_VALUE", "2");
    				IDataset valueList = new DatasetList();
    				valueList.add(idata);
    				bbossAttrInfo.put("VALUE_LIST", valueList);
    			}
    		}*/
        }
    }


}
