package com.asiainfo.veris.crm.order.soa.person.busi.internettv;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * 魔百和开户接口
 * @author yuyj3
 *
 */
public class InternetTvOpenIntfSVC extends CSBizService
{

	private static final long serialVersionUID = 1L;


	/**
	 * 校验用户信息（包括产品信息初始化、宽带信息）
	 */
    public IDataset checkUserForOpenInternetTVIntf(IData input) throws Exception
    {
    	//校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        
        
        String serialNum = input.getString("SERIAL_NUMBER");
        String queryType = input.getString("QUERY_TYPE","0");
        
        if (serialNum.startsWith("KD_"))
        {
        	serialNum = serialNum.substring(3);
        }
        
        IData checkResultData = checkBeforeTradeIntf(input);
        
        input.put("AUTH_SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        
    	IData resultData = new InternetTvOpenSVC().checkUserForOpenInternetTV(input);
    	
    	
    	resultData.put("CUST_NAME", checkResultData.getString("CUST_NAME"));
    	
    	resultData.put("USER_PRODUCT_NAME", checkResultData.getString("PRODUCT_NAME"));
    	resultData.put("USER_PRODUCT_ID", checkResultData.getString("PRODUCT_ID"));
    	
    	if ("1".equals(queryType))
    	{
    		if (resultData.containsKey("PRODUCT_INFO_SET"))
    		{
    			resultData.remove("PRODUCT_INFO_SET");
    		}
    		
    		return IDataUtil.idToIds(resultData);
    	}
    	else
    	{
    		return resultData.getDataset("PRODUCT_INFO_SET");
    	}
    }
    
    /**
     * 魔百合开户鉴权后校验接口(提供给APP魔法百合开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkBeforeTradeIntf(IData input) throws Exception
    {
        IData resultData = new DataMap();
        
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        
        String serialNum = input.getString("SERIAL_NUMBER");
        
        if (serialNum.startsWith("KD_"))
        {
        	serialNum = serialNum.substring(3);
        }
        
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNum);;
        
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.appError("-1", "通过该服务号码查询不到有效的用户信息！");
        }
        
        IData customerInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
        
        if (IDataUtil.isEmpty(customerInfo))
        {
            CSAppException.appError("-1", "通过该服务号码查询不到有效的客户信息！");
        }
        
        
        input.putAll(userInfo);
        input.put("IS_REAL_NAME", customerInfo.getString("IS_REAL_NAME"));
        input.put("TRADE_TYPE_CODE", "4800");
        input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
       
        //将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
        getVisit().setInModeCode("0");
        
        IDataset infos = CSAppCall.call( "CS.CheckTradeSVC.checkBeforeTrade", input);
        
        CSAppException.breerr(infos.getData(0));
        
        resultData.put("RESULT_CODE", "0");
        
        resultData.putAll(userInfo);
        resultData.putAll(customerInfo);
        
        return resultData;
    }
    
    
    /**
     * 查询互联网电视机顶盒基础优惠包（0）和可选优惠包（2）(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset queryDiscntPackagesByPIDIntf(IData input) throws Exception
    {
        IDataset retDataset = new DatasetList();
        
        //校验魔百和产品ID是否传入
        IDataUtil.chkParam(input, "PRODUCT_ID");
        
        //校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        
        //新大陆不支持解析 IData里面放多个IDataset 所以需要分多次查询 。查询内容 1：魔百和必选包 2：魔百和可选包 3：魔百和营销活动 4魔百和押金 
        IDataUtil.chkParam(input, "QUERY_TYPE");
        
        String queryType = input.getString("QUERY_TYPE");
        
        IData retData = new InternetTvOpenSVC().queryDiscntPackagesByPID(input);
        
        if ("1".equals(queryType))
        {
            retDataset = retData.getDataset("B_P");
        }
        else if ("2".equals(queryType))
        {
            retDataset = retData.getDataset("O_P");
        }
        else if ("3".equals(queryType))
        {
            IDataset topSetBoxSaleActiveList = retData.getDataset("TOP_SET_BOX_SALE_ACTIVE_LIST");
            
            if (IDataUtil.isNotEmpty(topSetBoxSaleActiveList))
            {
                IData saleActive = null;
                IData returnSaleActive = null;
                IData inData = null;
                IData saleActiveFeeData = null;
                
                for (int i = 0; i < topSetBoxSaleActiveList.size(); i++)
                {
                    saleActive = topSetBoxSaleActiveList.getData(i);
                    
                    returnSaleActive = new DataMap();
                    inData = new DataMap();
                    
                    returnSaleActive.put("SALE_ACTIVE_ID", saleActive.getString("PARA_CODE2"));
                    returnSaleActive.put("SALE_ACTIVE_NAME", saleActive.getString("PARA_CODE3"));
                    returnSaleActive.put("SALE_ACTIVE_EXPLAIN", saleActive.getString("PARA_CODE24"));
                    
                    inData.put("SERIAL_NUMBER", input.getString("serialNumber"));
                    //活动标记，1：宽带营销活动，2：魔百和营销活动
                    inData.put("ACTIVE_FLAG", "2");
                    //营销活动产品ID
                    inData.put("PRODUCT_ID", saleActive.getString("PARA_CODE4"));
                    //营销活动包ID
                    inData.put("PACKAGE_ID", saleActive.getString("PARA_CODE5"));
                    //获得营销活动预存费用
                    saleActiveFeeData = new InternetTvOpenSVC().queryCheckSaleActiveFee(inData);
                    
                    if (IDataUtil.isNotEmpty(saleActiveFeeData))
                    {
                        returnSaleActive.put("SALE_ACTIVE_FEE", saleActiveFeeData.getString("SALE_ACTIVE_FEE", "0"));
                    }
                    
                    retDataset.add(returnSaleActive);
                }
                
            }
        }
        
        //BUS201907310012关于开发家庭终端调测费的需求
        else if ("5".equals(queryType))
        {
            IDataset topSetBoxSaleActiveList = retData.getDataset("TOP_SET_BOX_SALE_ACTIVE_LIST2");
            
            if (IDataUtil.isNotEmpty(topSetBoxSaleActiveList))
            {
                IData saleActive = null;
                IData returnSaleActive = null;
                IData inData = null;
                IData saleActiveFeeData = null;
                
                for (int i = 0; i < topSetBoxSaleActiveList.size(); i++)
                {
                    saleActive = topSetBoxSaleActiveList.getData(i);
                    
                    returnSaleActive = new DataMap();
                    inData = new DataMap();
                    
                    returnSaleActive.put("SALE_ACTIVE_ID", saleActive.getString("PARA_CODE2"));
                    returnSaleActive.put("SALE_ACTIVE_NAME", saleActive.getString("PARA_CODE3"));
                    returnSaleActive.put("SALE_ACTIVE_EXPLAIN", saleActive.getString("PARA_CODE24"));
                    
                    
                    inData.put("SERIAL_NUMBER", input.getString("serialNumber"));
                    //活动标记，1：宽带营销活动，2：魔百和营销活动;
                    inData.put("ACTIVE_FLAG", "2");
                    //营销活动产品ID
                    inData.put("PRODUCT_ID", saleActive.getString("PARA_CODE4"));
                    //营销活动包ID
                    inData.put("PACKAGE_ID", saleActive.getString("PARA_CODE5"));
                    //获得营销活动预存费用
                    saleActiveFeeData = new InternetTvOpenSVC().queryCheckSaleActiveFee(inData);
                    
                    if (IDataUtil.isNotEmpty(saleActiveFeeData))
                    {
                        returnSaleActive.put("SALE_ACTIVE_FEE", saleActiveFeeData.getString("SALE_ACTIVE_FEE", "0"));
                    }
                    
                    retDataset.add(returnSaleActive);
                }
                
            }
        }
        //BUS201907310012关于开发家庭终端调测费的需求
        
        else
        {
            IData dataTemp =  new DataMap();
            
            dataTemp.put("TOP_SET_BOX_DEPOSIT", retData.getString("TOP_SET_BOX_DEPOSIT", "0"));
            
            retDataset = IDataUtil.idToIds(dataTemp);
        }
        
        return retDataset;
    }
    
    
    /**
     * 魔百和营销活动校验(网维APP)
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkSaleActiveIntf(IData input) throws Exception
    {
    	IData retData = new DataMap();
    	
    	//校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "TOP_SET_BOX_SALE_ACTIVE_ID");
        
        String topSetBoxSaleActiveId = input.getString("TOP_SET_BOX_SALE_ACTIVE_ID","");
        
        if (StringUtils.isNotBlank(topSetBoxSaleActiveId))
        {
            IDataset topSetBoxCommparaInfos = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", topSetBoxSaleActiveId, "0898");
            
            if (IDataUtil.isNotEmpty(topSetBoxCommparaInfos))
            {
                IData topSetBoxCommparaInfo = topSetBoxCommparaInfos.first();
                
                input.put("PRODUCT_ID",topSetBoxCommparaInfo.getString("PARA_CODE4"));
                input.put("PACKAGE_ID", topSetBoxCommparaInfo.getString("PARA_CODE5"));
                input.put("RULE_TAG", topSetBoxCommparaInfo.getString("PARA_CODE22"));
                input.put("DEP_PRODUCT_ID", topSetBoxCommparaInfo.getString("PARA_CODE23"));
            }
            else
            {
                CSAppException.appError("-1", "该营销活动配置信息不存在，请联系管理员！");
            }
        }
        
        //将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
        getVisit().setInModeCode("0");
        
    	new InternetTvOpenSVC().checkSaleActive(input);
    	
    	retData.put("RESULT_CODE", "0");
    	
    	return  retData;
    } 
    
    /**
     * 魔百和提交钱费用校验
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkFeeBeforeSubmit(IData input) throws Exception
    {
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
        
        //魔百和押金
        IDataUtil.chkParam(input, "TOPSETBOX_DEPOSIT");
        
        //魔百和营销活动费用
        IDataUtil.chkParam(input, "TOPSETBOX_SALE_ACTIVE_FEE");
        
        //接口传过来的单位为分，校验里面用到单位是元
        input.put("TOPSETBOX_DEPOSIT", Integer.parseInt(input.getString("TOPSETBOX_DEPOSIT","0"))/100);
        
        IData resultData = new InternetTvOpenSVC().checkFeeBeforeSubmit(input);
        
        resultData.put("RESULT_CODE", "0");
        
        return resultData;
    }

    /**
     * 查询用户魔百和信息
     * @param input
     * @return  
     * @throws Exception
     */
    public IData getUserInternetTV(IData input) throws Exception
    {
    	IData result = new DataMap();
    	
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	
    	String serialNumber=input.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        
        if(IDataUtil.isEmpty(userInfo)){
        	CSAppException.appError("-1", "未查询到用户信息！");
        }
        String userId = userInfo.getString("USER_ID");
		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
		if(IDataUtil.isEmpty(boxInfos)){
        	CSAppException.appError("-1", "该用户未开通魔百和！");
        }
		
		IData resInfo = new DataMap();
		IData boxInfo = boxInfos.first();
		resInfo.put("RES_ID", boxInfo.getString("IMSI")); //终端编号
        resInfo.put("OLD_RES_BRAND_NAME", boxInfo.getString("RSRV_STR4").split(",")[0]); //终端品牌
        resInfo.put("OLD_RES_KIND_NAME", boxInfo.getString("RSRV_STR4").split(",")[1]);//终端型号
        resInfo.put("OLD_RES_STATE_NAME", "已销售");
        resInfo.put("OLD_RES_FEE", boxInfo.getString("RSRV_NUM5"));//费用
        resInfo.put("OLD_RES_SUPPLY_COOPID", boxInfo.getString("KI")); //终端供应商
		
        result.put("X_RESULTCODE", "0");
		result.put("X_RESULTINFO", "返回成功");
		result.put("DATA", resInfo);
    	return result;
    }
    
    /**
     * 获取"基础家庭云0元套餐"和"智能语音服务包"接口
     * @param input
     * @return  
     * @throws Exception
     */
    public IData queryPlatSvc(IData param) throws Exception
    {
    	IDataUtil.chkParam(param, "TOPSET_TYPE");
    	IDataUtil.chkParam(param, "PRODUCT_ID");
    	String type ="1";
    	if("1".equals(param.getString("TOPSET_TYPE"))){
    		type="TOPSETBOX";
    	}else{
    		type="WIDENET";
    	}
    	IData resultInfo = new DataMap();
    	resultInfo.put("X_RESULTCODE", "0000");
    	resultInfo.put("X_RESULTINFO", "成功");
    	
    	String productId = param.getString("PRODUCT_ID");
    	IDataset platSvcPackages = CommparaInfoQry.getCommparaByCodeCode1("CSM", "2509", type, productId);  
    	IDataset platsvcInfoList = new DatasetList();
    	for(int i=0;i<platSvcPackages.size();i++){
        	IData platSvcPackage = platSvcPackages.getData(i);
        	IData data = new DataMap();
        	String serviceId = platSvcPackage.getString("PARA_CODE4");
        	data.put("SERVICE_ID", serviceId);
        	IData platSvcInfo = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PLATSVC,serviceId,null);
        	if(IDataUtil.isNotEmpty(platSvcInfo)){
        		data.put("SERVICE_NAME", platSvcInfo.getString("OFFER_NAME"));
        		if("80175705".equals(serviceId)){
        			data.put("SERVICE_NAME", platSvcInfo.getString("OFFER_NAME")+"（优惠后为0元）");
        		}
        	}
        	platsvcInfoList.add(data);
        }
    	resultInfo.put("PLATSVC_INFO_LIST", platsvcInfoList);
      return  resultInfo;
    }
}
