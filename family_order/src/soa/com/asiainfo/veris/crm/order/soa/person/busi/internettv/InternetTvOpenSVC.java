package com.asiainfo.veris.crm.order.soa.person.busi.internettv;

import java.util.StringTokenizer;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;


public class InternetTvOpenSVC extends CSBizService
{
	
	/**
	 * Copyright: Copyright (c) 2016 Asiainfo-Linkage
	 * 
	 * @ClassName: InternetTvOpenSVC.java
	 * @Description:
	 * @version: v1.0.0
	 * @author: songlm
	 * @date: 2016-6-13
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 校验用户信息（包括产品信息初始化、宽带信息）
	 */
    public IData checkUserForOpenInternetTV(IData input) throws Exception
    {
        String serialNumber = input.getString("AUTH_SERIAL_NUMBER");
        String wSerialNumber = "KD_" + serialNumber;
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        String userId = userInfo.getString("USER_ID");
        
        //判断用户是否含有有效的平台业务
        IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "51");//biz_type_code=51为互联网电视类的平台服务
        if (IDataUtil.isNotEmpty(platSvcInfos))
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_333,"用户当前存在生效的魔百和平台业务，不能再办理。");
        }
        IDataset platSvcInfostow = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "86");//biz_type_code=51为互联网电视类的平台服务
        if (IDataUtil.isNotEmpty(platSvcInfostow))
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_333,"用户当前存在生效的魔百和平台业务，不能再办理。");
        }

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
            
            // .2校园宽带不能受理
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber).first();
            
            if (IDataUtil.isEmpty(wideNetInfo))
            {
                CSAppException.appError("-1", "该用户宽带资料信息不存在！");
            }
            
            if (StringUtils.equals("4", wideNetInfo.getString("RSRV_STR2","")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1183); // 校园宽带不能办理互联网电视业务！
            }
            
            // .3校验是否为不允许的带宽
            if (IDataUtil.isNotEmpty(UserSvcInfoQry.checkInternetTvWide(wSerialNumber)))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"该宽带服务不允许办理魔百和开户"); // 该用户不是不允许的带宽
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
            
            // 校验在途工单是否符合速率
            if (!this.checkService(wideTD.getString("TRADE_ID")))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"该宽带服务不允许办理魔百和开户"); // 该用户不是不允许的带宽
            }
            
            //校验是否是宽带开户在途，含魔百和开户
            IDataset topSetBoxInfos = TradeOtherInfoQry.queryTradeOtherByTradeIdAndRsrvValueCode(wideTD.getString("TRADE_ID"), "TOPSETBOX");
            if (IDataUtil.isNotEmpty(topSetBoxInfos))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"宽带开户未完工单中已选择过魔百和开户，无需重复办理！");
            }
        }
        
        // 设置宽带状态
        userInfo.put("WIDE_STATE", wideState);
        userInfo.put("WIDE_STATE_NAME", "2".equals(wideState) ? "正常" : "1".equals(wideState) ? "未完工" : "异常");
        
        // 可选魔百和产品信息
        IDataset topSetBoxProducts = ProductInfoQry.queryTopSetBoxProducts("182", "600");
        userInfo.put("PRODUCT_INFO_SET", topSetBoxProducts);
  	  
      	return userInfo;
    }
    
    /**
	 * 校验是否复合互联网电视要求的速率
	 */
    private boolean checkService(String tradeId) throws Exception
    {
        //取不允许的服务配置
        IDataset wideLimitSet = CommparaInfoQry.getCommpara("CSM", "4800", "WIDELIMIT", CSBizBean.getTradeEparchyCode());
        //取服务台帐信息：SERVICE_ID
        IDataset tradeSvcSet = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
        boolean result = true;
        for (Object obj : wideLimitSet)
        {
            IData wideLimit = (IData) obj;
            String limitValue = wideLimit.getString("PARA_CODE1");
            for (Object objSvc : tradeSvcSet)
            {
                IData svcData = (IData) objSvc;
                String svcId = svcData.getString("SERVICE_ID");
                if (StringUtils.indexOf(limitValue, "|"+svcId+"|") >= 0)
                {
                	result = false;//如果匹配，则代表是不允许的服务
                }
            }
        }
        return result;
    }
    
    /**
	 * 查询互联网电视机顶盒基础优惠包（0）和可选优惠包（2）
	 */
    public IData queryDiscntPackagesByPID(IData input) throws Exception
    {
        IData retData = new DataMap();
        String productId = input.getString("PRODUCT_ID");
        String topSetBoxSaleActiveId = input.getString("TOP_SET_BOX_SALE_ACTIVE_ID");

        if (StringUtils.isNotBlank(productId))
        {
            //获取费用信息
            String topSetBoxDeposit = "20000";
            IDataset topSetBoxDepositDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
            
            if(IDataUtil.isNotEmpty(topSetBoxDepositDatas))
            {
                topSetBoxDeposit = topSetBoxDepositDatas.getData(0).getString("PARA_CODE1","20000");
            }
            
            //如果选择了魔百和营销活动，则不需要缴纳魔百和押金
            if (StringUtils.isNotBlank(topSetBoxSaleActiveId))
            {
                topSetBoxDeposit = "0";
            }
            
            //判断用户是否早已含有有效魔百和类的营销活动，如果存在，则减免押金
            IDataset hasMoSaleActive = UserSaleActiveInfoQry.checkUserMoSaleactive(input.getString("SERIAL_NUMBER"));
            if (IDataUtil.isNotEmpty(hasMoSaleActive))
            {
            	topSetBoxDeposit = "0";
            }
            
            //add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费
            //判断用户是否含有候鸟营销活动，如果存在，则押金为100
            IDataset hasHouniaoSaleActive = UserSaleActiveInfoQry.checkUserHouniaoActive(input.getString("SERIAL_NUMBER"));
            if (IDataUtil.isNotEmpty(hasHouniaoSaleActive))
            {
            	topSetBoxDeposit = "10000";
            }
            //add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费
            
            String topSetBoxType="NO";
            //start-wangsc10-20181119 REQ201809040036+关于开通IPTV业务服务的需求
            String IPTV = "NO";
            IDataset topSetBoxDepositDataIPTV=CommparaInfoQry.getCommParas("CSM", "182", "600", productId, "0898");
            if(IDataUtil.isNotEmpty(topSetBoxDepositDataIPTV))
            {
            	String PARA_CODE2 = topSetBoxDepositDataIPTV.getData(0).getString("PARA_CODE2");
            	topSetBoxType=PARA_CODE2;
            	if(PARA_CODE2 != null && !PARA_CODE2.equals("")){
            		if(PARA_CODE2.equals("IPTV")){
            			topSetBoxDeposit = "10000";
            			IPTV = "YES";
            		}
            	}
            }
            //end
            
            //魔百和押金
            //BUS201907310012关于开发家庭终端调测费的需求
            //retData.put("TOP_SET_BOX_DEPOSIT", topSetBoxDeposit);
            retData.put("TOP_SET_BOX_DEPOSIT", "0");
            //魔百和调测费活动
            IDataset topSetBoxSaleActiveList2 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "3800", "TOPSETBOX");           
            topSetBoxSaleActiveList2 = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), topSetBoxSaleActiveList2);
            retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST2", topSetBoxSaleActiveList2);
            //BUS201907310012关于开发家庭终端调测费的需求

            
            IData topSetBoxPlatSvcPackages = PlatSvcInfoQry.queryDiscntPackagesByPID(productId);
            
            // 基础服务包
            retData.put("B_P", topSetBoxPlatSvcPackages.getDataset("B_P"));
            
            // 可选服务包
            retData.put("O_P", topSetBoxPlatSvcPackages.getDataset("O_P")); 
            
            //必选营销包TOPSET_TYPE
            input.put("TOPSET_TYPE", "1");
            IDataset platSvcPackages = CSAppCall.call("SS.InternetTvOpenIntfSVC.queryPlatSvc", input);
            if(IDataUtil.isNotEmpty(platSvcPackages)){
            	retData.put("P_P", platSvcPackages.first().getDataset("PLATSVC_INFO_LIST")); 
            }
            
            //魔百和营销活动
            IDataset topSetBoxSaleActiveList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "3800", "0");
            
            topSetBoxSaleActiveList = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), topSetBoxSaleActiveList);
            
            //start-REQ201903010007增加IPTV业务办理条件限制-wangsc10-20190326
            retData.put("resultIPTVCode", "0");
            retData.put("resultIPTVInfo", "");
			String wSerialNumber = "KD_"+input.getString("SERIAL_NUMBER");
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber).first();
            IDataset isIPTV=CommparaInfoQry.getCommParas("CSM", "182", "IS_IPTV_TIP", productId, "0898");
            String queryType = input.getString("QUERY_TYPE");
            if(IDataUtil.isNotEmpty(isIPTV) && (null == queryType || "".equals(queryType)))
            {
	            if (IDataUtil.isNotEmpty(wideNetInfo))
	            {
	            	if (StringUtils.equals("1", wideNetInfo.getString("RSRV_STR2","")) || StringUtils.equals("6", wideNetInfo.getString("RSRV_STR2","")))
	                {
	            		retData.put("resultIPTVCode", "-1");
						retData.put("resultIPTVInfo", "您的宽带制式所限，目前无法办理魔百和直播电视业务，建议办理魔百和互联网电视业务！");
	                }
	            	IData wUserInfo = UcaInfoQry.qryUserInfoBySn(wSerialNumber);
	                if (IDataUtil.isNotEmpty(wUserInfo))
	                {
	                	IData resultLists = ProductInfoQry.getUserProductByUserIdForGrp(wUserInfo.getString("USER_ID")).first();//获取宽带产品信息
	                	if(IDataUtil.isNotEmpty(resultLists)){
	                		IDataset ftthkddc=CommparaInfoQry.getCommParas("CSM", "182", "KD_DC_50M", resultLists.getString("PRODUCT_ID",""), "0898");//查询宽带FTTH档次是不是50M以下
	                        if(IDataUtil.isNotEmpty(ftthkddc)){
	                        	retData.put("resultIPTVCode", "-1");
	        					retData.put("resultIPTVInfo", "您所办理的宽带业务网速太低，无法办理魔百和直播电视业务，请将宽带升档至50M及以上再办理！");
	                        }
	                	}
	                }
	            }
            }
            //end-wangsc10-20190326
            
            //REQ202003050012关于开发融合套餐增加魔百和业务优惠体验权益的需求 --魔百和和IPTV才显示本次需求新增活动
            IData result = this.checkTopSetBox(input);
			if("1".equals(result.getString("SALEACTIVE_TAG"))){
				retData.put("resultIPTVCode", "-1");
				retData.put("resultIPTVInfo", "用户已经订购了魔百和体验基础包，则不能办理第2条或第2条以上魔百和业务！");
			}
			
            IDataset topSetBoxSaleActiveList3 = new DatasetList();
            for(int i=0;i<topSetBoxSaleActiveList.size();i++){
            	String paraCode8 = topSetBoxSaleActiveList.getData(i).getString("PARA_CODE8");
            	if(StringUtils.isNotBlank(paraCode8)){
            		//2019年10月1日前从未办理过原价基础包且办理融合套餐费大于（含）58元的客户
            		if(!topSetBoxType.equals(paraCode8)){
                		if("1".equals(result.getString("COMMPARA_TAG"))&&"1".equals(result.getString("DATE_TAG"))&&!"1".equals(result.getString("PLATSVC_TAG"))){
                			topSetBoxSaleActiveList3.add(topSetBoxSaleActiveList.getData(i));
                		}
            		}
            	}else{
            		topSetBoxSaleActiveList3.add(topSetBoxSaleActiveList.getData(i));
            	}
            }
            topSetBoxSaleActiveList=topSetBoxSaleActiveList3;
            
            //start-wangsc10-20181119 REQ201809040036+关于开通IPTV业务服务的需求
            if(IPTV.equals("YES")){
            	//REQ201905080023关于将IPTV加入魔百和“移动电视尝鲜”活动的需求
            	IDataset saleActiveListForIPTV = new DatasetList();
            	if(IDataUtil.isNotEmpty(topSetBoxSaleActiveList))
            	{
            		for (int i = 0; i < topSetBoxSaleActiveList.size(); i++ )
            		{
            			String paraCode15 = topSetBoxSaleActiveList.getData(i).getString("PARA_CODE15", "");
            			if("SUPPORT_IPTV".equals(paraCode15))//PARA_CODE15 = IPTV,标识该活动支持IPTV业务。
            			{
            				saleActiveListForIPTV.add(topSetBoxSaleActiveList.getData(i));
            			}
            		}
            	}
            	retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", saleActiveListForIPTV);
            	//REQ201905080023关于将IPTV加入魔百和“移动电视尝鲜”活动的需求
            	//retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", new DataMap());
            	//start-REQ201903010007增加IPTV业务办理条件限制-wangsc10-20190326
            	if(retData.getString("resultIPTVCode").equals("-1")){
           		 	retData.put("B_P", new DataMap());
                    retData.put("O_P", new DataMap()); 
                    retData.put("TOP_SET_BOX_DEPOSIT", "0");
                }
            	//end-wangsc10-20190326
            }else{
            	retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", topSetBoxSaleActiveList);
            }
            //end
        }
        else
        {
            retData.put("B_P", new DataMap());
            retData.put("O_P", new DataMap()); 
            retData.put("TOP_SET_BOX_DEPOSIT", "0");
            retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", new DataMap());
        }
        
        return retData;
    }
	
    /**
     *  校验用户在魔百和开户时，能否办理魔百和营销活动
     *  input中含有SERIAL_NUMBER、PACKAGE_ID
     */
    public IDataset checkSaleActive(IData input) throws Exception
    {
    	//先取出包的td_b_package表的str5配置，判断是否为depend开头，是则表示有依赖的活动，调用判断
    	String serialNumber = input.getString("SERIAL_NUMBER","");
    	
    	//现网没有该配置，暂时注释掉
//    	String packageId = input.getString("PACKAGE_ID","");
//    	IDataset pkgInfo = PkgInfoQry.queryPackageById(packageId);//获取td_b_package中的配置
//    	if(IDataUtil.isNotEmpty(pkgInfo))
//        {
//        	String rsrvStr5 = pkgInfo.getData(0).getString("RSRV_STR5","");//取RSRV_STR5值
//        	if(rsrvStr5.startsWith("internetTvOpenDepend"))
//        	{
//        		if(!SaleActiveUtil.saleActvieHave(serialNumber, rsrvStr5))
//        		{
//        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"必须办理宽带1+活动，才能办理魔百和活动！");
//        		}
//        	}
//        }
    	
    	/**
    	 * REQ201607050007 关于移动电视尝鲜活动的需求
    	 * chenxy3 20160720
    	 * */
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
            		errorMsg = "营销包配置[" + packageId + "]错误，魔百和开户办理魔百和营销活动付款模式暂时只支持转账类的营销活动!不支持[" + getPayModeName(payMode)+ "]类营销活动";
            		CSAppException.appError("61312", errorMsg);
                }
                
                if(!"2".equals(feeMode))
                {
                	//费用类型非预存费报错
                	String errorMsg = "";
            		errorMsg = "营销包配置[" + packageId + "]错误，魔百和开户办理魔百和营销活动暂时只支持预存费用的营销活动!不支持[" + ("2".equals(feeMode) ? "预存" : "1".equals(feeMode) ? "押金" :"营业费") + "]类型";
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
    	String topSetBoxSaleActiveFee2 = param.getString("TOPSETBOX_SALE_ACTIVE_FEE2","0");//押金，html中单位：元
		String topSetBoxDeposit = param.getString("TOPSETBOX_DEPOSIT","0");//押金，html中单位：元
    	String topSetBoxSaleActiveFee = param.getString("TOPSETBOX_SALE_ACTIVE_FEE","0");//营销活动预存，单位：分
    	String serialNumber = param.getString("SERIAL_NUMBER","");
    	
    	
        String leftFee = WideNetUtil.qryBalanceDepositBySn(serialNumber);
        
        int allTotalTransFee = Integer.parseInt(topSetBoxSaleActiveFee2) + Integer.parseInt(topSetBoxSaleActiveFee);
        
        if(Integer.parseInt(leftFee)< allTotalTransFee )
        {
            CSAppException.appError("61314", "您的账户存折可用余额不足，请先办理缴费。本次需转出费用：[魔百和调测费金额："
            			+ Double.parseDouble(topSetBoxSaleActiveFee2)/100 + "元，魔百和营销活动费用金额:" + Double.parseDouble(topSetBoxSaleActiveFee)/100 + "元]");
        }
        
    	result.put("X_RESULTCODE", "0");
    	
    	return result;
    }
    
    /**
     * REQ202003050012关于开发融合套餐增加魔百和业务优惠体验权益的需求
     * @param cycle
     * @throws Exception
     * @author lizj
     */
    public IData checkTopSetBox(IData param) throws Exception
    {
    	String serialNumber = param.getString("SERIAL_NUMBER");
    	IData result = new DataMap();
    	result.put("X_RESULTCODE", "0000");
    	result.put("COMMPARA_TAG", "1");
    	result.put("DATE_TAG", "1");
    	IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	String userId = "";
    	if (IDataUtil.isEmpty(userInfos))
        {
    		result.put("COMMPARA_TAG", "0");
        	result.put("DATE_TAG", "0");
        	result.put("X_RESULTNAME", CrmUserException.CRM_USER_112);
        	return result;
        }
    	IData proInfo = UcaInfoQry.qryMainProdInfoByUserId(userInfos.getString("USER_ID"));
		if (IDataUtil.isEmpty(proInfo))
		{
			result.put("COMMPARA_TAG", "0");
        	result.put("DATE_TAG", "0");
        	result.put("X_RESULTNAME", CrmUserException.CRM_USER_224);
        	return result;
		}
		
    	userId = userInfos.getString("USER_ID");
    	IDataset commparaInfos2324=CommparaInfoQry.getCommparaInfos("CSM", "2324",proInfo.getString("PRODUCT_ID"));
    	if (IDataUtil.isEmpty(commparaInfos2324))
        {
    		result.put("COMMPARA_TAG", "0");
    		result.put("COMMPARA_EXPLAIN", "主产品不在魔百和权益活动最新套餐范围！");
        }else{
        	String fee = commparaInfos2324.first().getString("PARA_CODE2");
        	if(Integer.parseInt(fee)<Integer.parseInt("58")){
        		result.put("COMMPARA_TAG", "0");
        		result.put("COMMPARA_EXPLAIN", "办理融合套餐费小于于58元");
        	}
        	
        	String productId = commparaInfos2324.first().getString("PARA_CODE4");
        	if(StringUtils.isNotBlank(productId)){
        		IDataset userSaleActiveInfo = UserSaleActiveInfoQry.queryUserSaleActiveProdId(userId,productId,"0");
            	if (IDataUtil.isNotEmpty(userSaleActiveInfo))
                {
            		result.put("SALEACTIVE_TAG", "1");
            		result.put("SALEACTIVE_EXPLAIN", "用户已经订购了魔百和体验基础包");
                }
        	}
        	
        }
    	
    	
    	param.put("USER_ID", userId);
		IDataset platSvcInfos = Dao.qryByCodeParser("TF_F_USER_PLATSVC", "SEL_BIZTYPE_BY_USERID2", param);
		if (IDataUtil.isNotEmpty(platSvcInfos))
		{
			for (int i = 0; i < platSvcInfos.size(); i++)
			{
				IData platSvcInfo = platSvcInfos.getData(i);
				String serviceId = platSvcInfo.getString("SERVICE_ID", "");
				String startDate = platSvcInfo.getString("START_DATE", "");
				String endDate = platSvcInfo.getString("END_DATE", "");
				String state = platSvcInfo.getString("BIZ_STATE_CODE", "");//服务状态
				if (StringUtils.isNotBlank(serviceId))
				{
					IDataset platInfos = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, "51", null);
					IDataset platInfostow = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, "86", null);
					if (IDataUtil.isNotEmpty(platInfos)||IDataUtil.isNotEmpty(platInfostow))
					{
						if("2019-10-01".compareTo(startDate)>0){
							result.put("DATE_TAG", "0");
							result.put("DATE_EXPLAIN", "2019年10月1日前办理过魔百和");
						}
						
						//用户含有有效平台业务
						if("A".equals(state)){
							result.put("PLATSVC_TAG", "1");
						}
						
					}
				}
			}
		}
    	
    	return result;
    }
    
    public IDataset topSetBoxExpireDeal(IData param) throws Exception
    {
      return  InternetTvOpenBean.topSetBoxExpireDeal(param); 
    }
    
    public IData smsReplyTopSetBox(IData param) throws Exception
    {
    	return InternetTvOpenBean.smsReplyTopSetBox(param); 
    }
    
    /**
     * 魔百和体验期结束不续订强制报停
     * @param input
     * @return
     * @throws Exception
     */
    public IData stopTopSetBoxExperience(IData input) throws Exception
    {
    	InternetTvOpenBean bean = BeanManager.createBean(InternetTvOpenBean.class); 
        return bean.stopTopSetBoxExperience(input);

    }
    
    
    /**
     * 魔百和平台服务停止
     * @param input
     * @return
     * @throws Exception
     */
    public IData stopTopSetBoxPlatSvc(IData input) throws Exception
    {
    	InternetTvOpenBean bean = BeanManager.createBean(InternetTvOpenBean.class); 
        return bean.stopTopSetBoxPlatSvc(input);

    }
    
    
}
