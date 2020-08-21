package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxcreate;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class NoPhoneTopSetBoxCreateSVC extends CSBizService
{
	
	private static final long serialVersionUID = 1L;


	/**
	 * 校验用户信息（包括产品信息初始化、宽带信息）
	 */
    public IData checkUserForOpenInternetTV(IData input) throws Exception
    {
        String serialNumber = input.getString("AUTH_SERIAL_NUMBER");
        
        if (!serialNumber.startsWith("KD_"))
        {
        	serialNumber = "KD_" + serialNumber;
        }
        
        //判断是否无手机宽带
        IData widenetinfo = WideNetUtil.getWideNetTypeInfo(serialNumber);
        if (!"Y".equals(widenetinfo.getString("IS_NOPHONE_WIDENET"))) {
        	CSAppException.apperr(TradeException.CRM_TRADE_333,"该用户不是无手机宽带号码，请输入无手机宽带号码！");
		}
        
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        String userIdB = userInfo.getString("USER_ID");
        IData userInfoA = getRelaUUInfoByUserIdB(userIdB);
      
        if(!IDataUtil.isEmpty(userInfoA)){
        	String userIdA = userInfoA.getString("USER_ID_A");
        	
        	// 1.是否有购机信息
            IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userIdA, "4", "J");
            if (DataSetUtils.isNotBlank(boxInfos))
            {
            	CSAppException.apperr(TradeException.CRM_TRADE_333,"用户当前存在生效的魔百和业务，不能再办理。");
            }
        	
        	// 2.判断用户是否含有有效的平台业务
            IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userIdA, "51");//biz_type_code=51为互联网电视类的平台服务
            if (IDataUtil.isNotEmpty(platSvcInfos))
            {
            	CSAppException.apperr(TradeException.CRM_TRADE_333,"用户当前存在生效的魔百和平台业务，不能再办理。");
            }
            
            IDataset platSvcInfostow = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userIdA, "86");//biz_type_code=51为互联网电视类的平台服务
            if (IDataUtil.isNotEmpty(platSvcInfostow))
            {
            	CSAppException.apperr(TradeException.CRM_TRADE_333,"用户当前存在生效的魔百和平台业务，不能再办理。");
            }
            
            //4910工单为无手机魔百和正式受理，用的是147号码
            IDataset trade4910 = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("4910", userIdA, "0");
            if (IDataUtil.isNotEmpty(trade4910))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95,"该用户有魔百和开户工单未完工！"); // 有未完工工单，业务不能继续办理！
            }
        }
        
        //4900工单是魔百和预受理，用的宽带号码
        IDataset trade4900 = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("4900", userIdB, "0");
        if (IDataUtil.isNotEmpty(trade4900))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95,"该用户有魔百和开户预受理工单未完工！"); // 有未完工工单，业务不能继续办理！
        }
        
        // 是否有宽带在途工单
        IDataset wideInfos = TradeInfoQry.queryExistWideTrade(serialNumber);
        String wideState = "0"; // 0-系统异常
        if (IDataUtil.isEmpty(wideInfos))
        {
            // .1是否办理过宽带
            IData wUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            if (IDataUtil.isEmpty(wUserInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_162); // 用户没有开通宽带, 不能办理该业务
            }
            
            // .2校园宽带不能受理
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber).first();
            
            if (IDataUtil.isEmpty(wideNetInfo))
            {
                CSAppException.appError("-1", "该用户宽带资料信息不存在！");
            }
            
            if (StringUtils.equals("4", wideNetInfo.getString("RSRV_STR2","")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1183); // 校园宽带不能办理互联网电视业务！
            }
            
            // .3校验是否为不允许的带宽
            if (IDataUtil.isNotEmpty(UserSvcInfoQry.checkInternetTvWide(serialNumber)))
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
        
        //add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        List<DiscntTradeData> discntData = ucaData.getUserDiscntsByDiscntCodeArray("84071448");
        if (ArrayUtil.isNotEmpty(discntData)){
        	userInfo.put("DISCNT_CODE", "84071448");
        }
        List<DiscntTradeData> discntData2 = ucaData.getUserDiscntsByDiscntCodeArray("84071449");
        if (ArrayUtil.isNotEmpty(discntData2)){
        	userInfo.put("DISCNT_CODE", "84071449");
        }
        //add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求
  	  
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

        if (StringUtils.isNotBlank(productId))
        {
            //获取费用信息
            String topSetBoxDeposit = "20000";
            IDataset topSetBoxDepositDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
            
            if(IDataUtil.isNotEmpty(topSetBoxDepositDatas))
            {
                topSetBoxDeposit = topSetBoxDepositDatas.getData(0).getString("PARA_CODE1","20000");
            }
            //add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求
            String discntCodeList="84014240,84014241,84014242,84071448,84071449";
            String serialNumber = input.getString("SERIAL_NUMBER","");
            if(serialNumber.startsWith("KD_"))
            {
            	
            }
            else
            {
            	serialNumber = "KD_" + serialNumber;
            }
	        //System.out.println("==============queryDiscntPackagesByPID==========serialNumber:"+serialNumber);

	        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
	        List<DiscntTradeData> discntData = ucaData.getUserDiscntsByDiscntCodeArray(discntCodeList);
	        //System.out.println("==============queryDiscntPackagesByPID==========discntData:"+discntData);
	        if (ArrayUtil.isNotEmpty(discntData)){

            	topSetBoxDeposit = "10000";
            }
	        //add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求
            //start-wangsc10-20181119 REQ201809040036+关于开通IPTV业务服务的需求
            IDataset topSetBoxDepositDataIPTV=CommparaInfoQry.getCommParas("CSM", "182", "600", productId, "0898");
            if(IDataUtil.isNotEmpty(topSetBoxDepositDataIPTV))
            {
            	String PARA_CODE2 = topSetBoxDepositDataIPTV.getData(0).getString("PARA_CODE2");
            	if(PARA_CODE2 != null && !PARA_CODE2.equals("")){
            		if(PARA_CODE2.equals("IPTV")){
            			topSetBoxDeposit = "10000";
            		}
            	}
            }
            //end
            
            //魔百和押金
            retData.put("TOP_SET_BOX_DEPOSIT", topSetBoxDeposit);
            
            IData topSetBoxPlatSvcPackages = PlatSvcInfoQry.queryDiscntPackagesByPID(productId);
            
            // 基础服务包
            retData.put("B_P", topSetBoxPlatSvcPackages.getDataset("B_P"));
            
            // 可选服务包
            retData.put("O_P", topSetBoxPlatSvcPackages.getDataset("O_P")); 
            
            //start-REQ201903010007增加IPTV业务办理条件限制-wangsc10-20190326
            retData.put("resultIPTVCode", "0");
            retData.put("resultIPTVInfo", "");
            IDataset isIPTV=CommparaInfoQry.getCommParas("CSM", "182", "IS_IPTV_TIP", productId, "0898");
            if(IDataUtil.isNotEmpty(isIPTV))
            {
    			String nophone = input.getString("SERIAL_NUMBER");
                
                if (!nophone.startsWith("KD_"))
                {
                	nophone = "KD_" + nophone;
                }
                IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(nophone).first();
                
                if (IDataUtil.isNotEmpty(wideNetInfo))
                {
                	if (StringUtils.equals("1", wideNetInfo.getString("RSRV_STR2","")) || StringUtils.equals("6", wideNetInfo.getString("RSRV_STR2","")))
                    {
                		retData.put("resultIPTVCode", "-1");
    					retData.put("resultIPTVInfo", "您的宽带制式所限，目前无法办理魔百和直播电视业务，建议办理魔百和互联网电视业务！");
                    }
                	IData wUserInfo = UcaInfoQry.qryUserInfoBySn(nophone);
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
                if(retData.getString("resultIPTVCode").equals("-1")){
           		 	retData.put("B_P", new DataMap());
                    retData.put("O_P", new DataMap()); 
                    retData.put("TOP_SET_BOX_DEPOSIT", "0");
                }
            }
            //end-wangsc10-20190326
        }
        else
        {
            retData.put("B_P", new DataMap());
            retData.put("O_P", new DataMap()); 
            retData.put("TOP_SET_BOX_DEPOSIT", "0");
        }
        
        return retData;
    }

    //通过UU关系表，获取147号码
    public IData getRelaUUInfoByUserIdB(String userIdB) throws Exception
    {
        IDataset relaUUInfos = RelaUUInfoQry.getAllRelationByUidBRelaTypeRoleB(userIdB,"47","1");
        if(IDataUtil.isEmpty(relaUUInfos))
        {
        	return null; 
        }
        return relaUUInfos.first();
    }
    
}
