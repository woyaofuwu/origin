package com.asiainfo.veris.crm.order.soa.person.busi.imschangeproduct;

import java.util.StringTokenizer;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.internettv.InternetTvOpenBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;


public class IMSChangeProductSVC extends CSBizService
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
            
            if (IDataUtil.isEmpty(uuInfo))
            {
                CSAppException.appError("-1", "该用户还未办理过家庭IMS固话业务！");
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

    
    /**
     * 查询可办理营销活动
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryDiscntPackagesByPID(IData input) throws Exception
    {
        IData retData = new DataMap();
        String productId = input.getString("PRODUCT_ID","84004439");
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
}
