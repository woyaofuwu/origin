package com.asiainfo.veris.crm.order.soa.person.busi.imslandline;

import java.util.StringTokenizer;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.privm.CheckPriv;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.internettv.InternetTvOpenBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;


public class IMSLandLineSVC extends CSBizService
{
	
	private static final long serialVersionUID = 1L;


    public IData checkAuthSerialNum(IData input) throws Exception
    {
        String serialNumber = input.getString("AUTH_SERIAL_NUMBER");
        String wSerialNumber = "KD_" + serialNumber;
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        String userId = userInfo.getString("USER_ID");
        

        // 是否有宽带在途工单
        IDataset wideInfos = TradeInfoQry.queryExistWideTrade(wSerialNumber);
        String wideState = "0"; // 0-系统异常
        if (IDataUtil.isEmpty(wideInfos))
        {
            // .1是否办理过宽带
            IData wUserInfo = UcaInfoQry.qryUserInfoBySn(wSerialNumber);
            if (IDataUtil.isEmpty(wUserInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_162); // 用户没有开通宽带, 不能办理该业务
            }
            
            // .2用户宽带FTTH类型宽带
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber).first();
            
            if (IDataUtil.isEmpty(wideNetInfo))
            {
                CSAppException.appError("-1", "该用户宽带资料信息不存在！");
            }
            
            if(!(StringUtils.equals("3", wideNetInfo.getString("RSRV_STR2", "")) || StringUtils.equals("5", wideNetInfo.getString("RSRV_STR2", ""))))
            {
            	CSAppException.apperr(CrmUserException.CRM_USER_783,"用户宽带非FTTH类型宽带");
            }
            
            // .3用户是否已经办理家庭IMS固话
            IDataset uuInfo = RelaUUInfoQry.getRelationUUInfoByDeputySn(userId, "MS",null);
            
            if (IDataUtil.isNotEmpty(uuInfo))
            {
                CSAppException.appError("-1", "该用户已经办理过家庭IMS固话业务！");
            }
            
            IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(wideNetInfo.getString("USER_ID"));
            if(IDataUtil.isNotEmpty(userMainProducts))
            {
            	IData userProduct = userMainProducts.getData(0);
            	String userProductId = userProduct.getString("PRODUCT_ID");
            	String userProductName = UProductInfoQry.getProductNameByProductId(userProductId);
            	userInfo.put("WIDE_PRODUCT_NAME", userProductName);
            }
            
            
            wideState = "2"; // 2-正常
            userInfo.put("WIDE_START_DATE", wideNetInfo.getString("START_DATE"));
            userInfo.put("WIDE_END_DATE", wideNetInfo.getString("END_DATE"));
            userInfo.put("WIDE_USER_ID", wUserInfo.getString("USER_ID"));
            userInfo.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
            userInfo.put("RSRV_STR4", wideNetInfo.getString("RSRV_STR4")); //给PBOSS自动预约派单与回单用
        }
        else
        {
            wideState = "1"; // 1-未完工
            IData wideTD = wideInfos.getData(0);
            userInfo.put("WIDE_TRADE_ID", wideTD.getString("TRADE_ID"));
            userInfo.put("WIDE_USER_ID", wideTD.getString("USER_ID"));
            userInfo.put("WIDE_START_DATE", "--");
            userInfo.put("WIDE_END_DATE", "--");
            IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
            
            if (IDataUtil.isNotEmpty(addrTD))
            {
                userInfo.put("WIDE_ADDRESS", addrTD.first().getString("DETAIL_ADDRESS"));
                userInfo.put("RSRV_STR4", addrTD.first().getString("RSRV_STR4")); //给PBOSS自动预约派单与回单用
            }
          
        }
        
        // 设置宽带状态
        userInfo.put("WIDE_STATE", wideState);
        userInfo.put("WIDE_STATE_NAME", "2".equals(wideState) ? "正常" : "1".equals(wideState) ? "未完工" : "异常");
        
        if("2".equals(wideState))
        {
        	userInfo.put("RESULT_CODE", "1");
        }else{
        	userInfo.put("RESULT_CODE", "0");
        	userInfo.put("RESULT_INFO","宽带未完工！");
        }
  	  
      	return userInfo;
    }
    
    public IDataset onInitTrade(IData input) throws Exception
    {
    	return UProductInfoQry.getProductsType("6000", null);
    }
    
    public IData checkFixPhoneNum(IData param)throws Exception{
		IData rtnData=new DataMap();
		String fixNum=param.getString("FIX_NUMBER","");
		//start--wangsc10--20181031
		 boolean ttopen= CheckPriv.checkFuncPermission(CSBizBean.getVisit().getStaffId(), "SYSTTOPEN");
        IDataset retDatasetTT = ResCall.checkResourceForTTphone(fixNum);
        if(IDataUtil.isNotEmpty(retDatasetTT)){
        	String ACCESS_NUMBER = retDatasetTT.first().getString("ACCESS_NUMBER","");
        	if(ACCESS_NUMBER.equals(fixNum)){
        		if(!ttopen){
        			CSAppException.appError("-1", "没有特殊铁通号码开户权限,不能用此号码开户!");
        		}
        	}
        }
        //end
		//     固话号码检验接口
		IData params=new DataMap(); 
		params.put("RES_VALUE", fixNum);//固话号码 0898开头
		params.put("RES_TYPE_CODE","0");
		params.put("RES_TYPE","固话号码");
		params.put("CITYCODE_RSRVSTR4",param.getString("CITYCODE_RSRVSTR4", ""));
		try{
			rtnData=checkFixNumber(params);
			 /**
	         * 固话吉祥号码修改权限
		     * luys
	         * */
	        IDataset dataSet = ResCall.getMphonecodeInfo(fixNum);
	    	if (IDataUtil.isNotEmpty(dataSet)){
	         	IData mphonecodeInfo = dataSet.first();
	         	String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
	         	if (StringUtils.equals("1", beautifulTag)){
	         		rtnData.put("TDBEAUTIFUALTAG", "1");
	     		    if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "CHANGETDFEE")){
	     		    	rtnData.put("SYSCHANGETDFEE", "1");
	     	        }
	         	}
	         	rtnData.put("BEAUTIFUAL_TAG", mphonecodeInfo.getString("BEAUTIFUAL_TAG", ""));
	         	rtnData.put("RESERVE_FEE", mphonecodeInfo.getString("RESERVE_FEE", ""));
	        }
		}catch(Exception e){
	    	String error =  Utility.parseExceptionMessage(e);
	    	String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
			if(errorArray.length >= 2)
			{
				String strExceptionMessage = errorArray[1];
				rtnData.put("RESULT_CODE", "-1");
				rtnData.put("RESULT_INFO", "号码【"+fixNum+"】预占失败:"+strExceptionMessage);
			}
			else
			{
				rtnData.put("RESULT_CODE", "-1");
				rtnData.put("RESULT_INFO", "固话号码【"+fixNum+"】预占失败:"+error);
			}  
         }
		return rtnData; 
	}

    public IData checkFixNumber(IData param) throws Exception
    { 
        String serialNumber = param.getString("RES_VALUE", "");
        String restypecode = param.getString("RES_TYPE_CODE");
        String restype = param.getString("RES_TYPE");

        IData result = new DataMap();

        IDataset userInfos = UserGrpInfoQry.getMemberUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfos))
        {
            result.put("RESULT_CODE", "-1");
            result.put("RESULT_INFO", serialNumber + "号码已经生成了资料，请输入新号码！");
        }
        else
        {
        	String str = serialNumber.substring(0, 4);
            if (!"0898".equals(str))
            {
                result.put("RESULT_CODE", "-1");
                result.put("RESULT_INFO", serialNumber + "号码非固话号码，IMS语音成员用户开户必须为固话号码，请输入新号码！");
            }
            else
            {
                // 选占
                IDataset callset=ResCall.checkResourceForMphone("0", serialNumber, "0");
                
                if(IDataUtil.isNotEmpty(callset) && !"0G".equals(callset.getData(0).getString("RES_KIND_CODE","")))
                {
                	CSAppException.appError("-1", "号码非家庭IMS固话，请确认重新输入！");
                }
                
                if(IDataUtil.isNotEmpty(callset) && !param.getString("CITYCODE_RSRVSTR4").equals(callset.getData(0).getString("CITY_CODE","")))
                {
                	CSAppException.appError("-1", "固话归属业务区"+callset.getData(0).getString("CITY_CODE","")+"与宽带号码业务区"+param.getString("CITYCODE_RSRVSTR4")+"不一致！");
                }
                
                //REQ201903070001关于限制IMS固话业务跨地市办理的需求
                String staffCityCode = CSBizBean.getVisit().getCityCode();
                if("HNSJ".equals(CSBizBean.getVisit().getCityCode()))
                {
                	staffCityCode = "HNHK";
                }
                //BUS201907220012解除和家亲APP杭研工号开户需对应归属宽带地址的限制
                String staffId = CSBizBean.getVisit().getStaffId();
                if(!param.getString("CITYCODE_RSRVSTR4").equals(staffCityCode) && !staffId.equals("ITFZYC35") && !staffId.equals("IBOSS001"))
                {
                	CSAppException.appError("-1", "工号归属和宽带地址不在同一市县，不允许办理ims开户!");
                }
                //REQ201903070001关于限制IMS固话业务跨地市办理的需求
                
                result.put("RESULT_CODE", "1");
                result.put("RESULT_INFO", "号码【"+serialNumber+"】预占成功！");
                IDataset resDataSet = new DatasetList();
                IData resTmp = new DataMap();
                resTmp.put("RES_CODE", serialNumber);
                resTmp.put("RES_TYPE_CODE", restypecode);
                resTmp.put("RES_TYPE", restype);
                resDataSet.add(resTmp);

                result.put("RES_LIST", resDataSet);
            }

        }
        return result;
    }
    
    /**
     * 查询可办理营销活动
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryDiscntPackagesByPID(IData input) throws Exception
    {
        IData retData = new DataMap();
        String productId = input.getString("PRODUCT_ID");
        String topSetBoxSaleActiveId = input.getString("SALE_ACTIVE_ID");

        if (StringUtils.isNotBlank(productId))
        {        
            //IMS营销活动
            IDataset topSetBoxSaleActiveList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "6800", productId);
            
            topSetBoxSaleActiveList = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), topSetBoxSaleActiveList);
            
            retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", topSetBoxSaleActiveList);
        }
        else
        {
            retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", new DataMap());
        }
        
        return retData;
    }
    
    /**
     * 营销活动校验
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkSaleActive(IData input) throws Exception
    {
    	//先取出包的td_b_package表的str5配置，判断是否为depend开头，是则表示有依赖的活动，调用判断
    	String serialNumber = input.getString("SERIAL_NUMBER","");
    	String userId = input.getString("USER_ID","");
    	String productId = input.getString("PRODUCT_ID","");
    	String packageId = input.getString("PACKAGE_ID","");
    	
    	//是否存在有效爱家音箱营销活动
		IDataset saleActivelistIMS = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
		if(IDataUtil.isNotEmpty(saleActivelistIMS)){
			for (int i = 0; i < saleActivelistIMS.size(); i++) {
				IData saleActiveData = saleActivelistIMS.getData(i);
				String campnType = saleActiveData.getString("CAMPN_TYPE");
				IDataset commParaInfo6600 = CommparaInfoQry.getCommparaByCode4to6("CSM", "178", "6800", productId, packageId, "YX04" ,"0898");
				if(IDataUtil.isNotEmpty(commParaInfo6600) && campnType.equals("YX03")){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户已办理其他终端捆绑类营销活动，不能再办理爱家音箱营销活动！");
				}
			}
		}

    	String ruleTag=input.getString("RULE_TAG","");
		String depProdIds=input.getString("DEP_PRODUCT_ID","");
		String flag="0";
		if("1".equals(ruleTag)){
			StringTokenizer st=new StringTokenizer(depProdIds,"|"); 
			while(st.hasMoreElements()){ 
				String prodId=st.nextToken();
				IData param=new DataMap();
				param.put("PRODUCT_ID", prodId);
				param.put("SERIAL_NUMBER", "KD_"+serialNumber);
				IDataset userWilens=InternetTvOpenBean.getUserWilenInfos(param);
				if(IDataUtil.isNotEmpty(userWilens)){
					flag="1";
					break;
				}
			}
			if("0".equals(flag)){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户不存在办理该活动的宽带产品！");
			}
		}
    	
        input.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");//为了跳CheckPackageExtConfig.java的一段规则  if (!isNoFinishTrade && !isWideUserCreateSaleActive)

        // 预受理校验，不写台账
        input.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
        input.put("TRADE_TYPE_CODE", "240");
        IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);
        return result;
    }
    
    
    /**
     * 营销活动费用校验
     * 宽带开户营销活动及魔百和营销活动暂时只支持转账类的预存营销活动，如果配置的不是转账类的预存营销，则报错
     * 营销活动转入转出存折走费用配置表TD_B_PRODUCT_TRADEFEE IN_DEPOSIT_CODE,OUT_DEPOSIT_CODE
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryCheckSaleActiveFee(IData param) throws Exception
    {
    	IData result = new DataMap();
    	int totalFee = 0 ;
    	String activeFlag = param.getString("ACTIVE_FLAG") ; //活动标记，1：宽带营销活动，2：魔百和营销活动
    	String productId = param.getString("PRODUCT_ID","");
    	String packageId = param.getString("PACKAGE_ID","");
    	
    	//查询营销活动费用配置
    	IDataset businessFee = WideNetUtil.getWideNetSaleAtiveTradeFee(productId, packageId);
        if (IDataUtil.isEmpty(businessFee))
        {
        	//如果该营销活动未配置费用，则直接返回成功
        	result.put("SALE_ACTIVE_FEE", "0");
        	result.put("X_RESULTCODE", "0");
            return result;
        }
    		
		for(int j = 0 ; j < businessFee.size() ; j++)
		{
			 IData feeData = businessFee.getData(j);
			
			String payMode = feeData.getString("PAY_MODE");
            String feeMode = feeData.getString("FEE_MODE");
            String fee = feeData.getString("FEE");
            
            if(fee != null && !"".equals(fee) && Integer.parseInt(fee) >0)
            {
            	if(!"1".equals(payMode))
                {
                	//付费模式非转账报错
            		String errorMsg = "";                				
            		if("1".equals(activeFlag))
            		{
            			errorMsg = "营销包配置[" + packageId + "]错误，融合宽带营销活动付款模式暂时只支持转账类的营销活动!不支持[" + getPayModeName(payMode)+ "]营销活动";
            		}
            		else
            		{
            			errorMsg = "营销包配置[" + packageId + "]错误，魔百和开户办理魔百和营销活动付款模式暂时只支持转账类的营销活动!不支持[" + getPayModeName(payMode)+ "]类营销活动";
            		}
            		CSAppException.appError("61312", errorMsg);
                }
                
                if(!"2".equals(feeMode))
                {
                	//费用类型非预存费报错
                	String errorMsg = "";
            		if("1".equals(activeFlag))
            		{
            			errorMsg = "营销包配置[" + packageId + "]错误，融合宽开户营销活动费用类型暂时只支持预存费用的营销活动!不支持[" + ("2".equals(feeMode) ? "预存" : "1".equals(feeMode) ? "押金" :"营业费") + "]类型";
            		}
            		else
            		{
            			errorMsg = "营销包配置[" + packageId + "]错误，魔百和开户办理魔百和营销活动暂时只支持预存费用的营销活动!不支持[" + ("2".equals(feeMode) ? "预存" : "1".equals(feeMode) ? "押金" :"营业费") + "]类型";
            		}
            		CSAppException.appError("61313", errorMsg);
                }
            }
            
            totalFee += Integer.parseInt(fee);
		}
    	
    	result.put("SALE_ACTIVE_FEE", totalFee);
    	result.put("X_RESULTCODE", "0");
    	
    	return result;
    }
    
    public String getPayModeName(String payMode) throws Exception
    {
    	String payModeName = "" ;
    	if(payMode == null || "".equals(payMode))
    	{
    		payModeName = "未知类型";
    		return payModeName;
    	}
    	if("0".equals(payMode))
    	{
    		payModeName = "现金";
    	}
    	else if("1".equals(payMode))
    	{
    		payModeName = "转账";
    	}
    	else if("2".equals(payMode))
    	{
    		payModeName = "可选现金、转账";
    	}
    	else if("3".equals(payMode))
    	{
    		payModeName = "清退";
    	}
    	else if("4".equals(payMode))
    	{
    		payModeName = "分期付款";
    	}
    	return payModeName;
    }
    
    /**
     * 提交前费用校验
     * @param cycle
     * @throws Exception
     * @author zhangyc5
     */
    public IData checkFeeBeforeSubmit(IData param) throws Exception
    {
    	IData result = new DataMap();
    	String topSetBoxSaleActiveFee = param.getString("TOPSETBOX_SALE_ACTIVE_FEE","0");//营销活动预存，单位：分
    	String serialNumber = param.getString("SERIAL_NUMBER","");
    	
        String leftFee = WideNetUtil.qryBalanceDepositBySn(serialNumber);
        
        int allTotalTransFee = Integer.parseInt(topSetBoxSaleActiveFee);
        
        if(Integer.parseInt(leftFee)< allTotalTransFee )
        {
            CSAppException.appError("61314", "您的账户存折可用余额不足，请先办理缴费。本次需转出费用：[家庭IMS固话营销活动费用金额:" + Double.parseDouble(topSetBoxSaleActiveFee)/100 + "元]");
        }
        
    	result.put("X_RESULTCODE", "0");
    	
    	return result;
    }
    
    /**
     * 校验办理智能音箱套餐时，手机号码的余额是否小于9元
     * @param cycle
     * @throws Exception
     * @author wangsc10
     */
    public IData checkZnyxFee(IData param) throws Exception
    {
    	IData result = new DataMap();
    	String serialNumber = param.getString("SERIAL_NUMBER","");
		String productId = param.getString("PRODUCT_ID");
		IDataset commpara = CommparaInfoQry.getCommparaByCodeCode1("CSM", "7359", "HJGH_PRODUCT_CONFIG", productId); // 和家固话资费配置
		if (IDataUtil.isNotEmpty(commpara)) {
			long discntFee = Long.parseLong(commpara.getData(0).getString("PARA_CODE3")); // 套餐资费
			IData userOweFee = AcctCall.queryOweFee(serialNumber);
			if (IDataUtil.isNotEmpty(userOweFee)) {
				long acctBalance = userOweFee.getLong("RSRV_NUM3"); // 实时结余，单位/分
				if (acctBalance < discntFee) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "话费余额小于办理的套餐资费！");
				}
			} 
		}
    	result.put("X_RESULTCODE", "0");
    	
    	return result;
    }
    
    /**
     * @Function: checkTerminal()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: wangsc10
     * @date: 2019-6-26 下午10:00:03 Modification History: Date Author Version Description
     */
    public IData checkTerminal(IData input) throws Exception
    {
        IData retData = new DataMap();
        String resNo = input.getString("RES_ID");
        IDataset retDataset = HwTerminalCall.getTerminalByImei(resNo, "1");//查询空闲
        if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0") && DataSetUtils.isNotBlank(retDataset.first().getDataset("OUTDATA")))
        {
            IData res = retDataset.first();
            IDataset retDataset2 =  res.getDataset("OUTDATA");
            IData resData = retDataset2.first();
            String resKindId = resData.getString("RES_KIND_ID", "");
            String resSkuId = resData.getString("RES_SKU_ID", "");
            IDataset jtzdList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "6800", resKindId, resSkuId);
            if(IDataUtil.isEmpty(jtzdList)){
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该串号["+resNo+"]不是智能音箱串号，请重新输入！"); 
            }else{
            	//终端选占
//            	String serialNumber = input.getString("SERIAL_NUMBER");
//                IDataset retDataset3 = HwTerminalCall.querySetTopBox(serialNumber, resNo);
//                if (DataSetUtils.isNotBlank(retDataset3) && StringUtils.equals(retDataset3.first().getString("X_RESULTCODE"), "0"))
//                {
//                	IData res2 = retDataset3.first();
//                	retData.put("RES_FEE", Double.parseDouble(res2.getString("RSRV_STR6", "0"))); // 设备费用  - feeMgr.js接收单位：分
//                    retData.put("X_RESULTCODE", "0");
//                }else{
//                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "终端选占失败！");
//                }
                
                retData.put("RES_FEE", Double.parseDouble(resData.getString("SALE_PRICE", "0"))); // 设备费用  - feeMgr.js接收单位：分
                retData.put("X_RESULTCODE", "0");
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "终端不存在！");
        }
        return retData;
    }
}
