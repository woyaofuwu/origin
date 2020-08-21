package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.FeeUtils;
import com.ailk.bizservice.fuzzy.DataFuzzy;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.RandomStringUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.groupcustinfo.CustGroupInfoQry;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.PackagePrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UUserTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveCheckBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ExchangeRuleInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res.ResParaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserCommUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserForegiftInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.Qry360InfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.ChangeProductIntfSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.ChangeSvcStateRegSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.OneCardMultiNoBean;
import com.asiainfo.veris.crm.order.soa.person.busi.score.ScoreFactory;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.OneCardMultiNoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.FuzzyPsptUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;

public class BroadbandIntfBean extends CSBizBean {
	private static Logger log = Logger.getLogger(BroadbandIntfBean.class);
	private static final String CHECK_CODE_CACHE_KEY = "com.ailk.personserv.service.busi.common.abilityplat_";//验证码缓存存放key	
    public IData queryUserInfo(IData param) throws Exception
    {
    	String busiCode = param.getString("busiCode", "");
    	String custId = "";
    	String serialNumber = "";
    	String productId = "";
    	String stateCode = "";
    	String updateTime = "";
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	try{
	    	if("".equals(busiCode))
	        {        	
	            object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "手机号不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        if(!"KD_".equals(busiCode.substring(0, 3)))
	        {
	        	busiCode = "KD_" + busiCode;
	        }
	        IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0", busiCode);
	        if(IDataUtil.isEmpty(userinfo)){
	        	
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "查询用户无数据！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
			}
			String userId = userinfo.getData(0).getString("USER_ID","");
			 custId = userinfo.getData(0).getString("CUST_ID","");
			 serialNumber = userinfo.getData(0).getString("SERIAL_NUMBER","");
	        IDataset productInfo = UserProductInfoQry.queryMainProductNow( userId);
	        if (IDataUtil.isEmpty(productInfo))
	        {
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "查询用户产品无数据！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	         productId = productInfo.getData(0).getString("PRODUCT_ID","");
	        
	        IDataset svcstateInfo = UserSvcStateInfoQry.getUserMainSvcStateByUId( userId);
	        if (IDataUtil.isEmpty(svcstateInfo))
	        {
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "查询用户主体服务状态无数据！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        stateCode = svcstateInfo.getData(0).getString("STATE_CODE","");
	        updateTime = svcstateInfo.getData(0).getString("UPDATE_TIME","");
	        
	        data.put("idNo",productId );
	        data.put("srvCode", serialNumber);
	        data.put("custId", custId);
	        data.put("runCode", stateCode);
	        data.put("runTime", updateTime);
	        
	        object.put("data", data);
	        object.put("status", "0");
	        object.put("message", "success");
	        
	        result.put("object", object);
	        result.put("rtnMsg", "成功");
	        result.put("rtnCode", "0");
	        
	        return result;
	    }
	    catch (Exception e)
	    {
	    	object.put("data", new DataMap());
	        object.put("status", "-9999");
	        object.put("message", "查询用户三户资料异常！");
	        
	        result.put("object", object);
			result.put("rtnCode", "-9999");	
			result.put("rtnMsg", "失败");	
	        
	        return result;
	    }
    }
    
    public IData queryCustInfo(IData param) throws Exception
    {
        String custId = param.getString("custId", "");
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	try {
	    	if("".equals(custId))
	        {        	
	            object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "客户ID不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	    	
	    	IDataset custInfo = CustomerInfoQry.getCustomerByCustID( custId);
	    	if (IDataUtil.isEmpty(custInfo))
	        {
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "查询客户信息无数据！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	    	
	    	IDataset custPersonInfo = CustomerInfoQry.getCustPersonByCustID( custId);
	    	if (IDataUtil.isEmpty(custPersonInfo))
	        {
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "查询用户使用人信息无数据！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	    	
	    	boolean isBlack = UCustBlackInfoQry.isBlackCust( custInfo.getData(0).getString("PSPT_TYPE_CODE", ""),custInfo.getData(0).getString("PSPT_ID", ""));
	    	if (isBlack)
	    	{
	            data.put("custLevel", "黑名单");
	    	}
	    	else
	    	{
	    		data.put("custLevel", "非黑名单");
	    	}
	    	
	    	data.put("custName",custInfo.getData(0).getString("CUST_NAME", "") );
	        data.put("typeCode", custInfo.getData(0).getString("CUST_TYPE", ""));
	        data.put("idType", custInfo.getData(0).getString("PSPT_TYPE_CODE", ""));
	        data.put("idIccid", custInfo.getData(0).getString("PSPT_ID", ""));
	        
	        data.put("idAddress", custPersonInfo.getData(0).getString("PSPT_ADDR", ""));
	        data.put("sexCode", custPersonInfo.getData(0).getString("SEX", ""));
	        data.put("birthDay", custPersonInfo.getData(0).getString("BIRTHDAY", ""));
	        data.put("idValidDate", custPersonInfo.getData(0).getString("PSPT_END_DATE", ""));
	        
	        object.put("data", data);
	        object.put("status", "0");
	        object.put("message", "success");
	        
	        result.put("object", object);
	        result.put("rtnMsg", "成功");
	        result.put("rtnCode", "0");
	        return result;
    	 }
        catch (Exception e)
        {
        	object.put("data", new DataMap());
            object.put("status", "-9999");
            object.put("message", "查询用户三户资料异常！");
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
        }
    }
    
    public IData queryUserListInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	IData outInfoList = new DataMap();
    	IDataset outInfo = new DatasetList();
    	
        String busiCode = param.getString("busiCode", "");
        try{
	        if("".equals(busiCode))
	        {        	
	        	outInfoList.put("outInfo",outInfo);
	        	data.put("outInfoList", outInfoList);
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "busiCode不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        if(!"KD_".equals(busiCode.substring(0, 3)))
	        {
	        	busiCode = "KD_" + busiCode;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        IData tmpData = new DataMap();
	        tmpData.put("idNo",ucaData.getProductId() );
	        tmpData.put("srvCode", ucaData.getSerialNumber());
	        tmpData.put("runCode", ucaData.getUserSvcsState().get(0).getStateCode());
	        tmpData.put("brandName",UBrandInfoQry.getBrandNameByBrandCode(ucaData.getBrandCode()));
	        tmpData.put("prodName", UProductInfoQry.getProductNameByProductId(ucaData.getProductId()));
	        tmpData.put("prodId", ucaData.getProductId());
	        outInfo.add(tmpData);
	        outInfoList.put("outInfo",outInfo);
	        data.put("outInfoList", outInfoList);
	        object.put("data", data);
	        object.put("status", "0");
	        object.put("message", "success");
	        
	        result.put("object", object);
	        result.put("rtnMsg", "成功");
	        result.put("rtnCode", "0");
	        
	        return result;
        }        
        catch (Exception e)
        {
        	object.put("data", new DataMap());
            object.put("status", "-9999");
            object.put("message", "查询用户三户资料异常！"+e.getMessage());
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
        }
        
    }
    
    public IData queryUserDetailInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	try {
	        String busiCode = param.getString("busiCode", "");
	        String userId = "";
	    	if("".equals(busiCode))
	        {        	
	            object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "手机号不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	    	UcaData mobileUcaData = UcaDataFactory.getNormalUca(busiCode);	
	        if(!"KD_".equals(busiCode.substring(0, 3)))
	        {
	        	busiCode = "KD_" + busiCode;
	        }
	        
        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);	        
	        data.put("idNo",ucaData.getProductId() );
	        data.put("busiCode", ucaData.getSerialNumber());
	        data.put("loginName", ucaData.getSerialNumber());
	        data.put("runCode",ucaData.getUserSvcsState().get(0).getStateCode() );
	        data.put("runTime", ucaData.getUserSvcsState().get(0).getStartDate());
	        data.put("createLogin", ucaData.getCustomer().getInStaffId());
	        data.put("createDate",ucaData.getCustomer().getInDate() );
	        data.put("ownedChnlId", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",ucaData.getCustomer().getInDepartId()));
	        data.put("serviceGroup", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",ucaData.getCustomer().getInDepartId()));//营业点是什么
	        data.put("belongArea",StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_AREA", "AREA_CODE", "AREA_NAME", ucaData.getCustomer().getCityCode()) );
	        data.put("openTime", ucaData.getUser().getOpenDate());
	        data.put("limitOwe", ucaData.getUserCreditValue());
	        String className="非大客户";
	        data.put("cardType", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_VIPTYPE","VIP_TYPE_CODE", "VIP_TYPE", mobileUcaData.getVipTypeCode())==null?className:StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_VIPTYPE","VIP_TYPE_CODE", "VIP_TYPE", mobileUcaData.getVipTypeCode())); 
	        data.put("prodName", UProductInfoQry.getProductNameByProductId(ucaData.getProductId()));
	        data.put("prodPrcName",UDiscntInfoQry.getDiscntNameByDiscntCode(ucaData.getUserDiscnts().get(0).getDiscntCode()) );
	        data.put("timeLimit", "");//时长限制
	        data.put("oweFee", ucaData.getLastOweFee());
	        userId = ucaData.getUserId();
	        IData reduser = queryRedUser(userId);
	        data.put("stopFlag", reduser.getString("stopFlag", ""));//停机方式
	        data.put("owedFlag",reduser.getString("owedFlag", "") );//催停标志
	        
	        IDataset userWidenetInfo = WidenetInfoQry.getUserWidenetInfo(userId);
	        if (IDataUtil.isEmpty(userWidenetInfo))
	        {
	        	object.put("data", new DataMap());
	            object.put("status", "-9999");
	            object.put("message", "查询宽带信息无数据！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        data.put("accessWay", userWidenetInfo.getData(0).getString("RSRV_STR2", ""));//接入方式
	        data.put("standAddress", userWidenetInfo.getData(0).getString("DETAIL_ADDRESS", ""));//宽带安装地址
	        data.put("terminalNum","1" );//终端个数
	        String broadbandSpeed = "";
	        IDataset svcInfo =UserSvcInfoQry.getUserSvcByIdForNotMain( userId);
	        if (IDataUtil.isEmpty(svcInfo))
	        {
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "查询用户非主体服务状态无数据！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        broadbandSpeed = svcInfo.getData(0).getString("RSRV_STR1", "");
	        data.put("broadbandSpeed", broadbandSpeed);//宽带速率
	        data.put("linkMan", userWidenetInfo.getData(0).getString("CONTACT", "")); //联系人     
	        data.put("linkPhone", userWidenetInfo.getData(0).getString("CONTACT_PHONE", ""));//联系电话
	        String wideProductType = userWidenetInfo.getData(0).getString("RSRV_STR2", "");
	        if ("1".equals(wideProductType) || "3".equals(wideProductType))
	        {
	        	data.put("cooperationModel", "移动自建");//合作模式
	        }
	        else if ( "6".equals(wideProductType)||"2".equals(wideProductType) || "5".equals(wideProductType))
	        {
	        	data.put("cooperationModel", "铁通合作");//合作模式
	        }
	        else
	        {
	        	data.put("cooperationModel", "其他");//合作模式
	        }	
	        data.put("isTurnOnTime","".equals(userWidenetInfo.getData(0).getString("RSRV_DATE1", ""))? "不指定开通时间":"指定开通时间" );//是否指定开通时间	
	        data.put("usedTime", "");//当月已用时长

	        object.put("data", data);
	        object.put("status", "0");
	        object.put("message", "success");
	        
	        result.put("object", object);
	        result.put("rtnMsg", "成功");
	        result.put("rtnCode", "0");
	
	        return result;
    	 }
        catch (Exception e)
        {
        	object.put("data", new DataMap());
            object.put("status", "-9999");
            object.put("message", "查询用户三户资料异常！"+e.getMessage());
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
        }
    }
    
    public IData queryAccountStatus(IData param) throws Exception
    {
        String srvCode = param.getString("srvCode", "");
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	try {
	        if(!"KD_".equals(srvCode.substring(0, 3)))
	        {
	        	srvCode = "KD_" + srvCode;
	        }
	        IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0", srvCode);
	        if(IDataUtil.isEmpty(userinfo)){
	        	
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "查询用户无数据！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
			}
	        String userId = userinfo.getData(0).getString("USER_ID", "");
	        String custId = userinfo.getData(0).getString("CUST_ID", "");
	        
	        IDataset custInfo = CustomerInfoQry.getCustomerByCustID( custId);
	    	if (IDataUtil.isEmpty(custInfo))
	        {
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "查询客户信息无数据！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	    	String custName = custInfo.getData(0).getString("CUST_NAME", "");
	    	
	        IData oweFeeInfo = AcctCall.getOweFeeByUserId(userId);
	        if(IDataUtil.isEmpty(oweFeeInfo)){
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "查询用户无数据！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        String realBalance = oweFeeInfo.getString("ACCT_BALANCE","0");
	        String allOweFee = oweFeeInfo.getString("ALL_OWE_FEE","0");
	        
	        data.put("custName",DataFuzzy.fuzzyName(custName) );//模糊化姓名;
	        data.put("totalUncharRgedsum", allOweFee);
	        data.put("totalLatefee", realBalance);
	        
	        object.put("data", data);
	        object.put("status", "0");
	        object.put("message", "success");
	        
	        result.put("object", object);
	        result.put("rtnMsg", "成功");
	        result.put("rtnCode", "0");
	        return result;
    	}
    	catch (Exception e)
        {
        	object.put("data", new DataMap());
            object.put("status", "-9999");
            object.put("message", "查询用户三户资料异常！");
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
        }
    }
    
    public IData queryUserInfoByID(IData param) throws Exception
    {
    	String idIccid = param.getString("idIccid", "");
    	String custId = "";
    	String serialNumber = "";
    	String productId = "";
    	String stateCode = "";
    	String updateTime = "";
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	try{
	    	if("".equals(idIccid))
	        {        	
	            object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "身份证号码不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        
	        IDataset userinfo = UserInfoQry.getAllUserInfoByPsptId("0", idIccid,"0", "0898");
	        if(IDataUtil.isEmpty(userinfo)){
	        	userinfo = UserInfoQry.getAllUserInfoByPsptId("1", idIccid,"0", "0898");
	        }
	        if(IDataUtil.isEmpty(userinfo)){
	        	
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "查询用户无数据！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
			}
	        for (int i = 0 ; i < userinfo.size() ; i++ )
	        {
	        	if(userinfo.getData(i).getString("SERIAL_NUMBER","").startsWith("KD_"))
	        	{
	   			 	serialNumber = userinfo.getData(i).getString("SERIAL_NUMBER","");
	        	}
	        }
	        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);      
	        productId = ucaData.getProductId();
	        custId=ucaData.getCustId();
	        stateCode = ucaData.getUserSvcsState().get(0).getStateCode();
	        updateTime = ucaData.getUserSvcsState().get(0).getStartDate();
	        
	        data.put("idNo",productId );
	        data.put("srvCode", serialNumber);
	        data.put("custId", custId);
	        data.put("runCode", stateCode);
	        data.put("runTime", updateTime);
	        
	        object.put("data", data);
	        object.put("status", "0");
	        object.put("message", "success");
	        
	        result.put("object", object);
	        result.put("rtnMsg", "成功");
	        result.put("rtnCode", "0");
	        
	        return result;
	    }
	    catch (Exception e)
	    {
	    	object.put("data", new DataMap());
	        object.put("status", "-9999");
	        object.put("message", "查询用户三户资料异常！"+e.getMessage());
	        
	        result.put("object", object);
			result.put("rtnCode", "-9999");	
			result.put("rtnMsg", "失败");	
	        
	        return result;
	    }
    }
    
    public IData queryExpireInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	IData broadbandExpireInfo = new DataMap();
    	
        String srvCode = param.getString("srvCode", "");
        try{
	        if("".equals(srvCode))
	        {        	
	        	data.put("broadbandExpireInfo", broadbandExpireInfo);
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "srvCode不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        if(!"KD_".equals(srvCode.substring(0, 3)))
	        {
	        	srvCode = "KD_" + srvCode;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(srvCode);
	        broadbandExpireInfo.put("beginDate",ucaData.getUserSvcsState().get(0).getStartDate() );
	        broadbandExpireInfo.put("endDate", ucaData.getUserSvcsState().get(0).getEndDate());
	        broadbandExpireInfo.put("stauts", ucaData.getUserSvcsState().get(0).getStateCode());

	        data.put("broadbandExpireInfo", broadbandExpireInfo);
	        object.put("data", data);
	        object.put("status", "0");
	        object.put("message", "success");
	        
	        result.put("object", object);
	        result.put("rtnMsg", "成功");
	        result.put("rtnCode", "0");
	        
	        return result;
        }        
        catch (Exception e)
        {
        	object.put("data", new DataMap());
            object.put("status", "-9999");
            object.put("message", "查询用户三户资料异常！"+e.getMessage());
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
        }
    }
    
    public IData queryProgress(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	IData isCompleted = new DataMap();
    	
        String busiCode = param.getString("busiCode", "");
        try{
	        if("".equals(busiCode))
	        {        	
	        	data.put("isCompleted", isCompleted);
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "busiCode不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        if(!"KD_".equals(busiCode.substring(0, 3)))
	        {
	        	busiCode = "KD_" + busiCode;
	        }
	        
	        IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0", busiCode);
	        if(!IDataUtil.isEmpty(userinfo)){
	        	
	        	isCompleted.put("retCode", "0");
	        	isCompleted.put("retMsg", "已竣工");
	        	
	        	data.put("isCompleted", isCompleted);
		        object.put("data", data);
		        object.put("status", "0");
		        object.put("message", "success");
		        
		        result.put("object", object);
		        result.put("rtnMsg", "成功");
		        result.put("rtnCode", "0");
			}
	        else
	        {
		        IDataset tradeInfos = TradeInfoQry.getMainTradeBySN(busiCode,"600");
		        if(!IDataUtil.isEmpty(tradeInfos)){
		        	isCompleted.put("retCode", "1");
		        	isCompleted.put("retMsg", "未竣工");
		        	
		        	data.put("isCompleted", isCompleted);
			        object.put("data", data);
			        object.put("status", "0");
			        object.put("message", "success");
			        
			        result.put("object", object);
			        result.put("rtnMsg", "成功");
			        result.put("rtnCode", "0");
		        }
		        else
		        {
		        	data.put("isCompleted", isCompleted);
			        object.put("data", data);
			        object.put("status", "-9999");
			        object.put("message", "查询用户是否竣工：未查询到任何记录！");
			        
			        result.put("object", object);
			        result.put("rtnMsg", "失败");
			        result.put("rtnCode", "-9999");
		        }
	        }
   
	        return result;
        }        
        catch (Exception e)
        {
        	data.put("isCompleted", isCompleted);
        	object.put("data", new DataMap());
            object.put("status", "-9999");
            object.put("message", "查询用户是否竣工异常！"+e.getMessage());
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
        }
    }
    
    public IData querySaleActiveInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	
        String busiCode = param.getString("busiCode", "");
        try{
	        if("".equals(busiCode))
	        {        	
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "busiCode不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        
	        IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0", busiCode);
	        if(IDataUtil.isEmpty(userinfo)){
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "用户资料不存在！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
			}
	        else
	        {
		        String userId = userinfo.getData(0).getString("USER_ID", "");
		        IDataset saleActiveInfo = querySaleActives(userId);
		        if(!IDataUtil.isEmpty(saleActiveInfo)){
		        	data.put("activityId", saleActiveInfo.getData(0).getString("PACKAGE_ID", ""));
		        	data.put("activityName", saleActiveInfo.getData(0).getString("PRODUCT_NAME", ""));
		        	data.put("activityContent", saleActiveInfo.getData(0).getString("PACKAGE_NAME", ""));
		        	data.put("startTime", saleActiveInfo.getData(0).getString("START_DATE", ""));		        	
		        	data.put("endTime", saleActiveInfo.getData(0).getString("END_DATE", ""));

			        object.put("data", data);
			        object.put("status", "0");
			        object.put("message", "success");
			        
			        result.put("object", object);
			        result.put("rtnMsg", "成功");
			        result.put("rtnCode", "0");
		        }
		        else
		        {
			        object.put("data", data);
			        object.put("status", "-9999");
			        object.put("message", "未查询到宽带相关营销活动记录！");
			        
			        result.put("object", object);
			        result.put("rtnMsg", "失败");
			        result.put("rtnCode", "-9999");
		        }
	        }
   
	        return result;
        }        
        catch (Exception e)
        {
        	object.put("data", new DataMap());
            object.put("status", "-9999");
            object.put("message", "查询宽带相关营销活动！"+e.getMessage());
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
        }
    }
    
    public IData sendSMS(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	String smsCode = param.getString("smsCode", "");
        String smsContent = param.getString("smsContent", "");
        try{
	        if("".equals(smsCode))
	        {        	

	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "smsCode不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        if("".equals(smsContent))
	        {        	

	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "smsContent不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        
            IData  smsData = new DataMap();
            smsData.put("RECV_OBJECT", smsCode);
            smsData.put("NOTICE_CONTENT", smsContent ); 
            SmsSend.insSms(smsData);  
	        
            data.put("retCode", "0");
            data.put("retMsg", "发送成功");
	        object.put("data", data);
	        object.put("status", "0");
	        object.put("message", "success");
	        
	        result.put("object", object);
	        result.put("rtnMsg", "成功");
	        result.put("rtnCode", "0");
   
	        return result;
        }        
        catch (Exception e)
        {
        	object.put("data", new DataMap());
            object.put("status", "-9999");
            object.put("message", "短信发送失败！"+e.getMessage());
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
        }
    }
    
    public IData queryTradeInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	IData busiManagementList = new DataMap();
    	IDataset busiManagement = new DatasetList();
        String busiCode = param.getString("busiCode", "");
        String startTime = param.getString("startTime", "");
        String endTime = param.getString("endTime", "");
        try{
        	
	        if("".equals(startTime))
	        {        	
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "startTime不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        if("".equals(endTime))
	        {        	
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "endTime不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }        	
	        if("".equals(busiCode))
	        {        	
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "busiCode不能为空");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        if(!"KD_".equals(busiCode.substring(0, 3)))
	        {
	        	busiCode = "KD_" + busiCode;
	        }
	        IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0", busiCode);
	        if(IDataUtil.isEmpty(userinfo)){
	        	
	        	object.put("data", data);
	            object.put("status", "-1");
	            object.put("message", "无法查询到有效的用户信息！");
	            
	            result.put("object", object);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");
	    		return result;
			}
	        else
	        {
		        IData input = new DataMap();
		        input.put("USER_ID",userinfo.getData(0).getString("USER_ID",""));
		        input.put("START_DATE",param.getString("startTime",""));
		        input.put("END_DATE",param.getString("endTime",""));
		        IDataset tradeInfo = Qry360InfoDAO.queryTradeHistoryInfoNew(input, null);;
		        if(!IDataUtil.isEmpty(tradeInfo)){
		        	for (int i = 0 ; i<tradeInfo.size(); i++)
		        	{
		        		IData tmpData = new DataMap();
		        		tmpData.put("busiNumber", tradeInfo.getData(i).getString("TRADE_ID", ""));
		        		tmpData.put("opeTime", SysDateMgr.date2String(SysDateMgr.string2Date(tradeInfo.getData(i).getString("ACCEPT_DATE", ""), SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));
		        		tmpData.put("orderId", tradeInfo.getData(i).getString("ORDER_ID", ""));
		        		tmpData.put("custId", tradeInfo.getData(i).getString("CUST_ID", ""));
		        		tmpData.put("serviceCodeName", StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeInfo.getData(i).getString("TRADE_TYPE_CODE", "")));
		        		tmpData.put("channelType", StaticUtil.getStaticValue(getVisit(), "TD_S_INMODE", "IN_MODE_CODE", "IN_MODE", tradeInfo.getData(i).getString("IN_MODE_CODE", "")));
		        		tmpData.put("homeNodeName", tradeInfo.getData(i).getString("TRADE_CITY_CODE", ""));
		        		tmpData.put("opeUserNO", tradeInfo.getData(i).getString("TRADE_STAFF_ID", ""));
		        		tmpData.put("brand", UpcCall.queryBrandNameByChaVal( tradeInfo.getData(i).getString("BRAND_CODE", "")));
		        		tmpData.put("serviceDesc", tradeInfo.getData(i).getString("REMARK", ""));
		        		busiManagement.add(i, tmpData);		        		
		        	}

		        	busiManagementList.put("busiManagement", busiManagement);
		        	data.put("busiManagementList", busiManagementList);
			        object.put("data", data);
			        object.put("status", "0");
			        object.put("message", "success");
			        
			        result.put("object", object);
			        result.put("rtnMsg", "成功");
			        result.put("rtnCode", "0");
		        }
		        else
		        {
		        	busiManagementList.put("busiManagement", busiManagement);
		        	data.put("busiManagementList", busiManagementList);
			        object.put("data", data);
			        object.put("status", "0");
			        object.put("message", "success");
			        
			        result.put("object", object);
			        result.put("rtnMsg", "成功");
			        result.put("rtnCode", "0");
		        }
			}

	        return result;
        }        
        catch (Exception e)
        {
        	object.put("data", new DataMap());
            object.put("status", "-9999");
            object.put("message", "查询用户业务办理记录异常！"+e.getMessage());
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
        }
    }
    
    //新增5个接口20171017
    public IData queryBroadbandInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	IDataset ids= new DatasetList();
    	IData bean = new DataMap();
    	IDataset beans = new DatasetList();
        String busiCode = param.getString("acctNum", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("acctNum", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        	
	        	data.put("outData", ids);
	        	object.put("result", data);
	            object.put("respCode", "-1");
	            object.put("respDesc", "busiCode不能为空");
	            
	            result.put("object", object);
	            result.put("bean", bean);
	            result.put("beans", beans);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        
	        IData input = new DataMap();
	        input.put("SERIAL_NUMBER",busiCode);
	        IDataset userWideInfo = queryWideUserInfo(input);
	        
	        if(!IDataUtil.isEmpty(userWideInfo)){
	        	
	        	IData id = new DataMap();
	        	IDataset userAddrInfo = queryAddrInfos(busiCode);
	        	if(!IDataUtil.isEmpty(userAddrInfo)){
/*	        		String gisInfos = userAddrInfo.getData(0).getString("GIS", "");
	        		if (!"".equals(userAddrInfo.getData(0).getString("GIS1", ""))){
	        			gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS1", "");
	        		}
		        	if (!"".equals(userAddrInfo.getData(0).getString("GIS2", ""))){
	        			gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS2", "");
	        		}
		        	if (!"".equals(userAddrInfo.getData(0).getString("GIS3", ""))){
	        			gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS3", "");
	        		}
		        	if (!"".equals(userAddrInfo.getData(0).getString("GIS4", ""))){
	        			gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS4", "");
	        		}
		        	if (!"".equals(userAddrInfo.getData(0).getString("GIS5", ""))){
	        			gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS5", "");
	        		}
		        	if (!"".equals(userAddrInfo.getData(0).getString("GIS6", ""))){
	        			gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS6", "");
	        		}
		        	if (!"".equals(userAddrInfo.getData(0).getString("GIS7", ""))){
	        			gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS7", "");
	        		}
		        	if (!"".equals(userAddrInfo.getData(0).getString("GIS8", ""))){
	        			gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS8", "");
	        		}*/
	        		String gisInfos = userAddrInfo.getData(0).getString("GIS", "");
	        		gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS1", "");
	        		gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS2", "");
	        		gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS3", "");
	        		gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS4", "");
	        		gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS5", "");
	        		gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS6", "");
	        		gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS7", "");
	        		gisInfos = gisInfos + ","+userAddrInfo.getData(0).getString("GIS8", "");
	        		id.put("standardAddress", gisInfos);
	        	}
	        	else 
	        		id.put("standardAddress", "");
	        	//id.put("standardAddress", userWideInfo.getData(0).getData("BASE_INFO").getString("STAND_ADDRESS", ""));

	        	id.put("cityCode", "");
	        	id.put("regionId", userWideInfo.getData(0).getData("CUST_INFO").getString("CITY_CODE", ""));
	        	String broadBandType =userWideInfo.getData(0).getData("BASE_INFO").getString("RSRV_STR2","");
	        	if("1".equals(broadBandType) || "6".equals(broadBandType)) //FTTB
	        	{
	        		id.put("broadbandType", "2");	        	
	        	}
	        	else if("3".equals(broadBandType) || "5".equals(broadBandType)) //FTTH
	        	{
	        		id.put("broadbandType", "1");	        	
	        	}
	        	else if("2".equals(broadBandType)) //ADSL
		        {
		        	id.put("broadbandType", "3");	        	
		        }
	        	else
	        	{
	        		id.put("broadbandType", broadBandType);
	        	}
	        	
		        ids.add(0, id);
		        
	        	data.put("outData", ids);
	        	object.put("result", data);
	            object.put("respCode", "0");
	            object.put("respDesc", "success");
	            
	            result.put("object", object);
	            result.put("bean", bean);
	            result.put("beans", beans);
	    		result.put("rtnCode", "0");	
	    		result.put("rtnMsg", "成功");
	        }
	        else
	        {	
	        	data.put("outData", ids);
	        	object.put("result", data);
	            object.put("respCode", "-1");
	            object.put("respDesc", "无法查询到用户宽带信息！");
	            
	            result.put("object", object);
	            result.put("bean", bean);
	            result.put("beans", beans);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	data.put("outData", ids);
        	object.put("result", data);
            object.put("respCode", "-1");
            object.put("respDesc", "查询用户宽带信息异常！"+e.getMessage());
            
            result.put("object", object);
            result.put("bean", bean);
            result.put("beans", beans);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
        }
    }
    
    public IData queryUCAInfo4KF(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData object1 = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
    	IDataset ids= new DatasetList();
    	IData bean = new DataMap();
    	IDataset beans = new DatasetList();
        String accessNum = param.getString("accessNum", "");
        String xGetMode = param.getString("xGetMode", "");
        if ( "".equals(accessNum))
        {
        	accessNum = getParams(param).getString("accessNum", "");
        }
        if ( "".equals(xGetMode))
        {
        	xGetMode = getParams(param).getString("xGetMode", "");
        }
        try{
        	
	       
	        if("".equals(accessNum))
	        {        	
	        	data.put("outData", ids);
	        	object1.put("result", data);
	        	object1.put("respCode", "-1");
	        	object1.put("respDesc", "accessNum不能为空！");
	            
	            result.put("object", object1);
	            result.put("bean", bean);
	            result.put("beans", beans);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	    		
	            return result;
	        }        	
	        if("".equals(xGetMode))
	        {  	            
	        	data.put("outData", ids);
	        	object1.put("result", data);
	        	object1.put("respCode", "-1");
	        	object1.put("respDesc", "xGetMode不能为空！");
	            
	            result.put("object", object1);
	            result.put("bean", bean);
	            result.put("beans", beans);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	    		
	            return result;
	        }
	        //查询正常用户资料
	        if("0".equals(xGetMode)){
		        IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0", accessNum);
		        if(IDataUtil.isEmpty(userinfo)){		    		
		        	data.put("outData", ids);
		        	object.put("result", data);
		            object.put("respCode", "-1");
		            object.put("respDesc", "无法查询到正常用户资料！");
		            
		            result.put("object", object);
		            result.put("bean", bean);
		            result.put("beans", beans);
		    		result.put("rtnCode", "-9999");	
		    		result.put("rtnMsg", "失败");	
		    		return result;
				}
		        else
		        {
		        	UcaData ucaData = UcaDataFactory.getNormalUca(accessNum);
		        	IData input = new DataMap();
			        input.put("USER_ID",ucaData.getUserId());
			        input.put("SERIAL_NUMBER",accessNum);
			        
			        object.put("accessNum", param.getString("accessNum", ""));
			        object.put("subscriberInsId", ucaData.getUserId());
			        object.put("custId", ucaData.getCustId());
			        object.put("prodInsId", ucaData.getProductId());
			        object.put("prodLineId", "");//产品线ID？？？
			        object.put("prodLineName", "");//产品线名称？？？
			        object.put("subscriberType", UUserTypeInfoQry.getUserTypeByUserTypeCode(ucaData.getUser().getUserTypeCode()));
			        object.put("subBillId", "");//次计费标识???
			        object.put("acctTag", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", 
			        		new java.lang.String[]{ "USER_ACCTTAG", ucaData.getUser().getAcctTag()}));
			        object.put("mputeTag", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"USER_MPUTEMONTHFEE", ucaData.getUser().getMputeMonthFee()}));			        
			        object.put("firstActiveDate", ucaData.getUser().getFirstCallTime());
			        object.put("removeTag", ucaData.getUser().getRemoveTag());
			        object.put("removeTagName", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"REMOVE_TAG", ucaData.getUser().getRemoveTag()}));
			        object.put("removeReason", ucaData.getUser().getRemoveReasonCode());
			        object.put("preDestoryDate", ucaData.getUser().getPreDestroyTime());
			        object.put("destoryDate", ucaData.getUser().getDestroyTime());
			        object.put("openMode", ucaData.getUser().getOpenMode());
			        object.put("openDate", ucaData.getUser().getOpenDate());
			        object.put("mgmtDistrict", ucaData.getUser().getCityCode());//管理地区???
			        object.put("mgmtDistrictName",UAreaInfoQry.getAreaNameByAreaCode(ucaData.getUser().getCityCode()) );//管理地区名称 ???
			        object.put("custName",  DataFuzzy.fuzzyName(ucaData.getCustomer().getCustName()));//****需要模糊化);
			        object.put("idenTypeId", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_PASSPORTTYPE", ucaData.getCustomer().getPsptTypeCode()}));
			        object.put("idenNr", FuzzyPsptUtil.fuzzyPsptId(ucaData.getCustomer().getPsptTypeCode(),ucaData.getCustomer().getPsptId()));
			        object.put("idenAddress", DataFuzzy.fuzzyAll(ucaData.getCustPerson().getPsptAddr()) );
			        object.put("gender", ucaData.getCustPerson().getSex());
			        object.put("profession", ucaData.getCustPerson().getJob());
			        object.put("professionalTitle", ucaData.getCustPerson().getJobTypeCode());
			        object.put("lunarFlag", ucaData.getCustPerson().getBirthdayFlag());
			        object.put("birthDate", ucaData.getCustPerson().getBirthday());
			        object.put("lunarDate", ucaData.getCustPerson().getBirthdayLunar());
			        object.put("countryOfBirth", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_NATIONALITY", ucaData.getCustPerson().getNationalityCode()}));
			        object.put("placeOfBirth", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_LOCAL_NATIVE", ucaData.getCustPerson().getLocalNativeCode()}));
			        object.put("maritalStatus", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"CUSTPERSON_MARRIAGESTATE", ucaData.getCustPerson().getMarriage()}));
			        object.put("givenName", "");
			        object.put("simpleSpell", "");
			        object.put("familyName", "");
			        object.put("qualifications", "");
			        object.put("education", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"CHNL_STAFF_EDU", ucaData.getCustPerson().getEducateDegreeCode()}));
			        object.put("religion", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_RELIGION", ucaData.getCustPerson().getReligionCode()}));
			        object.put("politicalParty", "");
			        object.put("nationalType", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_FOLK", ucaData.getCustPerson().getFolkCode()}));
			        object.put("incomeRange", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_REVENUE_LEVEL", ucaData.getCustPerson().getRevenueLevelCode()}));
			        object.put("familyInfo", "");
			        object.put("jobPostion", "");
			        object.put("jobCompany", "");
			        object.put("jobDuties", "");
			        object.put("hobby", "");
			        object.put("character", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_CHARACTERTYPE", ucaData.getCustPerson().getCharacterTypeCode()}));
				    
			        object.put("acctId", ucaData.getAcctId());
			        object.put("acctName", DataFuzzy.fuzzyName(ucaData.getAccount().getPayName()));
			        object.put("acctType", ucaData.getAccount().getPayModeCode());
			        object.put("acctTypeName", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_PAYMODE", ucaData.getAccount().getPayModeCode()}));
			        object.put("prodStatus", ucaData.getUser().getUserStateCodeset());
			        object.put("prodStatusName", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"USER_STATE_CODESET", ucaData.getUser().getUserStateCodeset()}));
			        object.put("prodStaReason", "");
			        object.put("brandCode", ucaData.getBrandCode());
			        object.put("brandCodeName", UpcCall.queryBrandNameByChaVal( ucaData.getBrandCode()));
			        object.put("accessDate", ucaData.getUser().getInDate());
			        
			        object.put("userRegionId", ucaData.getUser().getCityCode());
			        object.put("userRegionName", UAreaInfoQry.getAreaNameByAreaCode(ucaData.getUser().getCityCode()));
			        if("0".equals(ucaData.getCustomer().getIsRealName()))
			        {
			        	object.put("realNameFlag", "非实名");
			        }
			        else if("1".equals(ucaData.getCustomer().getIsRealName()))
			        {
			        	object.put("realNameFlag", "实名");
			        }
			        else 
			        {
			        	object.put("realNameFlag", "");
			        }
			        object.put("is4Guser", "");
			        IDataset resDataSet = ResCall.getMphonecodeInfo(accessNum);
			        String simCardNo = "";
			 		if (IDataUtil.isNotEmpty(resDataSet))
			 		{
			 			IData resInfo = resDataSet.first();
			 			simCardNo = resInfo.getString("SIM_CARD_NO");
			 			object.put("simCardCode", simCardNo);
			 		}
			 		else{
			 			object.put("simCardCode", "");
			 		}
			        boolean isSim4G = is4GUser(simCardNo);
			        if (isSim4G)
			        {
				        object.put("isSim4G", "1");
			        }
			        else
			        {
				        object.put("isSim4G", "0");
			        }
			        object.put("custTypeId", ucaData.getCustomer().getCustType());
			        object.put("offerId", ucaData.getProductId());
			        object.put("offerName", UProductInfoQry.getProductNameByProductId(ucaData.getProductId()));
			        
			        IData inParam = new DataMap();	        	
		        	inParam.put("USER_ID", ucaData.getUserId());
		        	inParam.put("IDTYPE", "0");
		        	IDataset ids1 = getCreditInfo(inParam);
		        	if (IDataUtil.isNotEmpty(ids1)){
		        		object.put("userLevel", ids1.getData(0).getString("CREDIT_CLASS", ""));
		        		object.put("creditClass", ids1.getData(0).getString("CREDIT_CLASS_NAME", ""));
		        	}
			        
			        object.put("termTypeInfo", "");
			        object.put("againNet", "");
			        object.put("isVolte", "");
			        object.put("isCreditOpen", "");
			        //查询亲情关系
			        IDataset qqInfos = UserProductInfoQry.getUserWidenetProductInfo(ucaData.getUserId(),"05");
			        if (IDataUtil.isNotEmpty(qqInfos)){
			        	object.put("isFamilyUser", "1");
			        }
			        else
			        {
			        	object.put("isFamilyUser", "0");
			        }
			        //查询集团成员
			        IDataset groupInfos = UserProductInfoQry.getUserWidenetProductInfo(ucaData.getUserId(),"12");
			        if (IDataUtil.isNotEmpty(groupInfos)){
			        	object.put("isEcmemberCust", "1");
				        object.put("isEcmemberUser", "1");
			        }
			        else
			        {
			        	object.put("isEcmemberCust", "0");
				        object.put("isEcmemberUser", "0");
			        }
			        
			        object.put("isSchoolUser", "");
			        object.put("isHulongUser", "");
			        
			        
			        IDataset npInfos = qryNpUserInfo(input);
			        if(IDataUtil.isNotEmpty(npInfos)){
			        	object.put("isNpUser", "1");
			        }
			        else
			        {
			        	object.put("isNpUser", "0");
			        }
			        
			        IDataset dataSet = ResCall.getMphonecodeInfo(accessNum);
			 		if (IDataUtil.isNotEmpty(dataSet))
			 		{
			 			IData mphonecodeInfo = dataSet.first();
			 			String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
			 			object.put("isLucky", beautifulTag);
			 		}
			 		else{
			 			object.put("isLucky", "");
			 		}
					
			 		/*
			 		 * REQ201907040024关于重要客户流程优化需求
			 		 * @author yanghb6
			 		 */
			 		IDataset cusGroupInfo = CustGroupInfoQry.qryGroupInfoByCustId(ucaData.getUserId());
					if (IDataUtil.isNotEmpty(cusGroupInfo))
			        {
						IData groupInfo = cusGroupInfo.getData(0);
						String cusName = groupInfo.getString("CUST_NAME","");
						String classId = groupInfo.getString("CLASS_ID","");
						String memberKind = groupInfo.getString("MEMBER_KIND","");
						String cusManagerName = groupInfo.getString("CUST_MANAGER_NAME","");
						String cusManagerSn = groupInfo.getString("CUST_MANAGER_SN","");
						object.put("cusName",cusName);
						object.put("classId",classId);
						object.put("memberKind",memberKind);
						object.put("cusManagerName",cusManagerName);
						object.put("cusManagerSn",cusManagerSn);
			        }
			 		
			 		/*
			 		 * REQ201905230015关于在线服务公司服务请求时支持查询全球通字段信息
			 		 * @author xurf3
			 		 */
			 		IData inMap = new DataMap();	 	 
		            inMap.put("USER_ID",ucaData.getUserId());	 	 
		            IDataset dataList = UserClassInfoQry.queryUserClass(inMap);	 	 
		            if(IDataUtil.isNotEmpty(dataList) && dataList.size()>0){ 	 	 
			                IData outMap = dataList.getData(0);	 	 
			            switch (Integer.parseInt(outMap.getString("USER_CLASS"))) {
			            case 1:
			            	object.put("brandLevelCode", "04");
			            	object.put("brandLevelName", "全球通-银卡");
			            	break;
			            case 2:
			            	object.put("brandLevelCode", "03");
			            	object.put("brandLevelName", "全球通-金卡");
			            	break;
			            case 3:
			            	object.put("brandLevelCode", "02");
			            	object.put("brandLevelName", "全球通-白金卡");
			            	break;
			            case 4:
			            	object.put("brandLevelCode", "01");
			            	object.put("brandLevelName", "全球通-钻石卡");
			            	break;
			            case 5:
			            	object.put("brandLevelCode", "99");
			                /*String QQTClass = StaticUtil.getStaticValue("USER_INFO_CLASS", outMap.getString("USER_CLASS"));//转换成文字
			                object.put("brandLevelName", QQTClass);*/
			                object.put("brandLevelName", "全球通-其他");
			            	break;
			            case 6:
			            	object.put("brandLevelCode", "05");
			            	object.put("brandLevelName", "全球通-体验卡");
			            	break;
			            default:
			            	object.put("brandLevelCode", "");
			            	object.put("brandLevelName", "");
			            	break;
			            }
		            }else{
		        	  object.put("brandLevelCode", "");
		        	  object.put("brandLevelName", "");
		            }
			        
			        ids.add(0, object);
			        
		        	data.put("outData", ids);
		        	object1.put("result", data);
		        	object1.put("respCode", "0");
		        	object1.put("respDesc", "success");
		            
		            result.put("object", object1);
		            result.put("bean", bean);
		            result.put("beans", beans);
		    		result.put("rtnCode", "0");	
		    		result.put("rtnMsg", "成功");
			        
			        return result;
				}
		      
	        }
	        //查询非正常用户资料
	        else if("1".equals(xGetMode)){
		        IDataset userinfo = UserInfoQry.queryUserBySnDestroyLatest(accessNum);
		        if(IDataUtil.isEmpty(userinfo)){
		    		
		        	data.put("outData", ids);
		        	object1.put("result", data);
		            object1.put("respCode", "-1");
		            object1.put("respDesc", "无法查询到用户资料！");
		            
		            result.put("object", object1);
		            result.put("bean", bean);
		            result.put("beans", beans);
		    		result.put("rtnCode", "-9999");	
		    		result.put("rtnMsg", "失败");	
		    		
		    		return result;
				}
		        else
		        {

		        	//UcaData ucaData = UcaDataFactory.getUcaByUserId(userinfo.getData(i).getString("USER_ID",""));
		        	String userId = userinfo.getData(0).getString("USER_ID","");
		        	String custId = userinfo.getData(0).getString("CUST_ID","");
		        	IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId, BizRoute.getRouteId());
		        	IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
		        	IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
		       	 	IData persInfo = UcaInfoQry.qryPerInfoByCustId(custId);
		        	
	        		IData input = new DataMap();
			        input.put("USER_ID",userinfo.getData(0).getString("USER_ID",""));
			        input.put("SERIAL_NUMBER",accessNum);
			        
			        IData tmpData = new DataMap();
			        tmpData.put("accessNum", param.getString("accessNum", ""));
			        tmpData.put("subscriberInsId", userId);
			        tmpData.put("custId", custId);
			        tmpData.put("prodInsId", userInfo.getString("PRODUCT_ID", ""));
			        tmpData.put("prodLineId", "");//产品线ID？？？
			        tmpData.put("prodLineName", "");//产品线名称？？？
			        tmpData.put("subscriberType", UUserTypeInfoQry.getUserTypeByUserTypeCode(userInfo.getString("USER_TYPE_CODE", "")));
			        tmpData.put("subBillId", "");//次计费标识???
			        tmpData.put("acctTag", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", 
			        		new java.lang.String[]{ "USER_ACCTTAG", userInfo.getString("ACCT_TAG", "")}));
			        tmpData.put("mputeTag", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"USER_MPUTEMONTHFEE", userInfo.getString("MPUTE_MONTH_FEE", "")}));			        
			        tmpData.put("firstActiveDate", userInfo.getString("FIRST_CALL_TIME", ""));
			        tmpData.put("removeTag", userInfo.getString("REMOVE_TAG", ""));
			        tmpData.put("removeTagName", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"REMOVE_TAG", userInfo.getString("REMOVE_TAG", "")}));
			        tmpData.put("removeReason", userInfo.getString("REMOVE_REASON_CODE", ""));
			        tmpData.put("preDestoryDate", userInfo.getString("PRE_DESTROY_TIME", ""));
			        tmpData.put("destoryDate", userInfo.getString("DESTROY_TIME", ""));
			        tmpData.put("openMode", userInfo.getString("OPEN_MODE", ""));
			        tmpData.put("openDate", userInfo.getString("OPEN_DATE", ""));
			        tmpData.put("mgmtDistrict", userInfo.getString("CITY_CODE", ""));//管理地区???
			        tmpData.put("mgmtDistrictName",UAreaInfoQry.getAreaNameByAreaCode(userInfo.getString("CITY_CODE", "")) );//管理地区名称 ???
			        tmpData.put("custName", FuzzyPsptUtil.fuzzyName(custInfo.getString("CUST_NAME", "")));//****需要模糊化
			        tmpData.put("idenTypeId", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_PASSPORTTYPE", custInfo.getString("PSPT_TYPE_CODE", "")}));
			        tmpData.put("idenNr", FuzzyPsptUtil.fuzzyPsptId(custInfo.getString("PSPT_TYPE_CODE", ""), custInfo.getString("PSPT_ID", "")));
			        tmpData.put("idenAddress",  DataFuzzy.fuzzyAll(persInfo.getString("PSPT_ADDR", "")));
			        tmpData.put("gender",  persInfo.getString("SEX", ""));
			        tmpData.put("profession",  persInfo.getString("JOB", ""));
			        tmpData.put("professionalTitle", persInfo.getString("JOB_TYPE_CODE", ""));
			        tmpData.put("lunarFlag",  persInfo.getString("BIRTHDAY_FLAG", ""));
			        tmpData.put("birthDate",  persInfo.getString("BIRTHDAY", ""));
			        tmpData.put("lunarDate",  persInfo.getString("BIRTHDAY_LUNAR", ""));
			        tmpData.put("countryOfBirth", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_NATIONALITY",  persInfo.getString("NATIONALITY_CODE", "")}));
			        tmpData.put("placeOfBirth", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_LOCAL_NATIVE",  persInfo.getString("LOCAL_NATIVE_CODE", "")}));
			        tmpData.put("maritalStatus", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"CUSTPERSON_MARRIAGESTATE",  persInfo.getString("MARRIAGE", "")}));
			        tmpData.put("givenName", "");
			        tmpData.put("simpleSpell", "");
			        tmpData.put("familyName", "");
			        tmpData.put("qualifications", "");
			        tmpData.put("education", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"CHNL_STAFF_EDU",  persInfo.getString("EDUCATE_DEGREE_CODE", "")}));
			        tmpData.put("religion", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_RELIGION",  persInfo.getString("RELIGION_CODE", "")}));
			        tmpData.put("politicalParty", "");
			        tmpData.put("nationalType", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_FOLK",  persInfo.getString("FOLK_CODE", "")}));
			        tmpData.put("incomeRange", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_REVENUE_LEVEL",  persInfo.getString("REVENUE_LEVEL_CODE", "")}));
			        tmpData.put("familyInfo", "");
			        tmpData.put("jobPostion", "");
			        tmpData.put("jobCompany", "");
			        tmpData.put("jobDuties", "");
			        tmpData.put("hobby", "");
			        tmpData.put("character", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_CHARACTERTYPE",  persInfo.getString("CHARACTER_TYPE_CODE", "")}));

			        tmpData.put("acctId", acctInfo.getString("ACCT_ID", ""));
			        tmpData.put("acctName", FuzzyPsptUtil.fuzzyName(acctInfo.getString("PAY_NAME", "")));
			        tmpData.put("acctType", acctInfo.getString("PAY_MODE_CODE", ""));
			        tmpData.put("acctTypeName", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"TD_S_PAYMODE", acctInfo.getString("PAY_MODE_CODE", "")}));
			        tmpData.put("prodStatus", userInfo.getString("USER_STATE_CODESET", ""));
			        tmpData.put("prodStatusName", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
			        		new java.lang.String[]{"USER_STATE_CODESET", userInfo.getString("USER_STATE_CODESET", "")}));
			        tmpData.put("prodStaReason", "");
			        tmpData.put("brandCode", userInfo.getString("BRAND_CODE", ""));
			        tmpData.put("brandCodeName", UpcCall.queryBrandNameByChaVal( userInfo.getString("BRAND_CODE", "")));
			        tmpData.put("accessDate", userInfo.getString("IN_DATE", ""));
			        
			        tmpData.put("userRegionId", userInfo.getString("CITY_CODE", ""));
			        tmpData.put("userRegionName", UAreaInfoQry.getAreaNameByAreaCode(userInfo.getString("CITY_CODE", "")));
			        
			        if("0".equals( custInfo.getString("IS_REAL_NAME", "")))
			        {
			        	tmpData.put("realNameFlag", "非实名");
			        }
			        else if("1".equals(custInfo.getString("IS_REAL_NAME", "")))
			        {
			        	tmpData.put("realNameFlag", "实名");
			        }
			        else 
			        {
			        	tmpData.put("realNameFlag", "");
			        }
			        tmpData.put("is4Guser", "");
			        IDataset resDataSet = ResCall.getMphonecodeInfo(accessNum);
			        String simCardNo = "";
			 		if (IDataUtil.isNotEmpty(resDataSet))
			 		{
			 			IData resInfo = resDataSet.first();
			 			simCardNo = resInfo.getString("SIM_CARD_NO");
			 			tmpData.put("simCardCode", simCardNo);
			 		}
			 		else{
			 			tmpData.put("simCardCode", "");
			 		}
			        boolean isSim4G = is4GUser(simCardNo);
			        if (isSim4G)
			        {
			        	tmpData.put("isSim4G", "1");
			        }
			        else
			        {
			        	tmpData.put("isSim4G", "0");
			        }
			        tmpData.put("custTypeId",  custInfo.getString("CUST_TYPE", ""));
			        tmpData.put("offerId", userInfo.getString("PRODUCT_ID", ""));
			        tmpData.put("offerName", UProductInfoQry.getProductNameByProductId(userInfo.getString("PRODUCT_ID", "")));
		        	IData inParam = new DataMap();	        	
		        	inParam.put("USER_ID", userId);
		        	inParam.put("IDTYPE", "0");
		        	IDataset ids1 = getCreditInfo(inParam);
		        	if (IDataUtil.isNotEmpty(ids1)){
		        		tmpData.put("userLevel", ids1.getData(0).getString("CREDIT_CLASS", ""));
		        		tmpData.put("creditClass", ids1.getData(0).getString("CREDIT_CLASS_NAME", ""));
		        	}
			        
			        tmpData.put("termTypeInfo", "");
			        tmpData.put("againNet", "");
			        tmpData.put("isVolte", "");
			        tmpData.put("isCreditOpen", "");
			        //查询亲情关系
			        IDataset qqInfos = UserProductInfoQry.getUserWidenetProductInfo(userId,"05");
			        if (IDataUtil.isNotEmpty(qqInfos)){
			        	tmpData.put("isFamilyUser", "1");
			        }
			        else
			        {
			        	tmpData.put("isFamilyUser", "0");
			        }
			        //查询集团成员
			        IDataset groupInfos = UserProductInfoQry.getUserWidenetProductInfo(userId,"12");
			        if (IDataUtil.isNotEmpty(groupInfos)){
			        	tmpData.put("isEcmemberCust", "1");
			        	tmpData.put("isEcmemberUser", "1");
			        }
			        else
			        {
			        	tmpData.put("isEcmemberCust", "0");
			        	tmpData.put("isEcmemberUser", "0");
			        }
			        
			        tmpData.put("isSchoolUser", "");
			        tmpData.put("isHulongUser", "");
			        
			        
			        IDataset npInfos = qryNpUserInfo(input);
			        if(IDataUtil.isNotEmpty(npInfos)){
			        	tmpData.put("isNpUser", "1");
			        }
			        else
			        {
			        	tmpData.put("isNpUser", "0");
			        }
			        
			        IDataset dataSet = ResCall.getMphonecodeInfo(accessNum);
			 		if (IDataUtil.isNotEmpty(dataSet))
			 		{
			 			IData mphonecodeInfo = dataSet.first();
			 			String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
			 			tmpData.put("isLucky", beautifulTag);
			 		}
			 		else{
			 			tmpData.put("isLucky", "");
			 		}
					
					/*
			 		 * REQ201907040024关于重要客户流程优化需求
			 		 * @author yanghb6
			 		 */
			 		IDataset cusGroupInfo = CustGroupInfoQry.qryGroupInfoByCustId(userinfo.getData(0).getString("USER_ID",""));
					if (IDataUtil.isNotEmpty(cusGroupInfo))
			        {
						IData groupInfo = cusGroupInfo.getData(0);
						String cusName = groupInfo.getString("CUST_NAME","");
						String classId = groupInfo.getString("CLASS_ID","");
						String memberKind = groupInfo.getString("MEMBER_KIND","");
						String cusManagerName = groupInfo.getString("CUST_MANAGER_NAME","");
						String cusManagerSn = groupInfo.getString("CUST_MANAGER_SN","");
						object.put("cusName",cusName);
						object.put("classId",classId);
						object.put("memberKind",memberKind);
						object.put("cusManagerName",cusManagerName);
						object.put("cusManagerSn",cusManagerSn);
			        }
			 		
			 		/*
			 		 * REQ201905230015关于在线服务公司服务请求时支持查询全球通字段信息
			 		 * @author xurf3
			 		 */
			 		IData inMap = new DataMap();	 	 
		            inMap.put("USER_ID",userId);	 	 
		            IDataset dataList = UserClassInfoQry.queryUserClass(inMap);
		            if(IDataUtil.isNotEmpty(dataList) && dataList.size()>0){
			                IData outMap = dataList.getData(0);
			            switch (Integer.parseInt(outMap.getString("USER_CLASS"))) {
			            case 1:
			            	tmpData.put("brandLevelCode", "04");
			            	tmpData.put("brandLevelName", "全球通-银卡");
			            	break;
			            case 2:
			            	tmpData.put("brandLevelCode", "03");
			            	tmpData.put("brandLevelName", "全球通-金卡");
			            	break;
			            case 3:
			            	tmpData.put("brandLevelCode", "02");
			            	tmpData.put("brandLevelName", "全球通-白金卡");
			            	break;
			            case 4:
			            	tmpData.put("brandLevelCode", "01");
			            	tmpData.put("brandLevelName", "全球通-钻石卡");
			            	break;
			            case 5:
			            	tmpData.put("brandLevelCode", "99");
			                /*String QQTClass = StaticUtil.getStaticValue("USER_INFO_CLASS", outMap.getString("USER_CLASS"));//转换成文字
			                tmpData.put("brandLevelName", QQTClass);*/
			            	tmpData.put("brandLevelName", "全球通-其他");
			            	break;
			            case 6:
			            	tmpData.put("brandLevelCode", "05");
			            	tmpData.put("brandLevelName", "全球通-体验卡");
			            	break;
			            default:
			            	tmpData.put("brandLevelCode", "");
			            	tmpData.put("brandLevelName", "");
			            	break;
			            }
		            }else{
		        	  object.put("brandLevelCode", "");
		        	  object.put("brandLevelName", "");
		            }
			 		
			 		ids.add(0, tmpData);
			        
		        	data.put("outData", ids);
		        	object1.put("result", data);
		        	object1.put("respCode", "0");
		        	object1.put("respDesc", "success");
		            
		            result.put("object", object1);
		            result.put("bean", bean);
		            result.put("beans", beans);
		    		result.put("rtnCode", "0");	
		    		result.put("rtnMsg", "成功");
			        
			        return result;
			        
				}
		      
	        }
	        else{	
	            
	        	data.put("outData", ids);
	        	object1.put("result", data);
	            object1.put("respCode", "-1");
	            object1.put("respDesc", "xGetmode值非法："+xGetMode);
	            
	            result.put("object", object1);
	            result.put("bean", bean);
	            result.put("beans", beans);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	    		
	            return result;
	        }

        }        
        catch (Exception e)
        {                       
        	data.put("outData", ids);
        	object1.put("result", data);
            object1.put("respCode", "-1");
            object1.put("respDesc", "queryUCAInfo4KF执行异常："+e.getMessage());
            
            result.put("object", object1);
            result.put("bean", bean);
            result.put("beans", beans);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");
            return result;
        }
    }
    
    public IData queryAllOffersByParam(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IDataset ids= new DatasetList();
    	IData data = new DataMap();
    	IData bean = new DataMap();
    	IDataset beans = new DatasetList();
        String accessNum = param.getString("accessNum", "");
        String xGetmode = param.getString("xGetmode", "");
        String xGettype = param.getString("xGettype", "");
        if ( "".equals(accessNum))
        {
        	accessNum = getParams(param).getString("accessNum", "");
        }
        try{       	
	        if("".equals(accessNum))
	        {        		    		
	        	data.put("infos", ids);
	        	object.put("result", data);
	            object.put("respCode", "-1");
	            object.put("respDesc", "accessNum不能为空");
	            
	            result.put("object", object);
	            result.put("bean", bean);
	            result.put("beans", beans);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0", accessNum);
	        if(IDataUtil.isEmpty(userinfo)){
	    		
	        	data.put("infos", ids);
	        	object.put("result", data);
	            object.put("respCode", "-1");
	            object.put("respDesc", "无法查询到有效的用户信息！");
	            
	            result.put("object", object);
	            result.put("bean", bean);
	            result.put("beans", beans);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	    		
	    		return result;
			}
	        else
	        {
		        IDataset userSaleActiveInfos = BofQuery.queryValidSaleActives(userinfo.getData(0).getString("USER_ID",""), "0898");
		        if(!IDataUtil.isEmpty(userSaleActiveInfos)){
		        	for (int i = 0 ; i<userSaleActiveInfos.size(); i++)
		        	{
		        		IData tmpData = new DataMap();
		        		tmpData.put("accessNum", accessNum);
		        		tmpData.put("checkMod", "");
		        		tmpData.put("doneDate", userSaleActiveInfos.getData(i).getString("ACCEPT_DATE", ""));
		        		tmpData.put("offerId", userSaleActiveInfos.getData(i).getString("PRODUCT_ID", ""));
		        		tmpData.put("offerName", userSaleActiveInfos.getData(i).getString("PRODUCT_NAME", ""));
		        		tmpData.put("offerType", userSaleActiveInfos.getData(i).getString("CAMPN_TYPE", ""));
		                IDataset catalogInfos = UpcCall.qryCatalogByCatalogId(userSaleActiveInfos.getData(i).getString("CAMPN_TYPE", ""));
		                if(!IDataUtil.isEmpty(catalogInfos)){
		                	tmpData.put("offerTypeName", catalogInfos.getData(0).getString("CATALOG_NAME", ""));
		                }
		                else
		                {
		                	tmpData.put("offerTypeName", "");
		                }
		        		tmpData.put("isMain", "0");
		        		tmpData.put("validDate", userSaleActiveInfos.getData(i).getString("START_DATE", ""));
		        		tmpData.put("expireDate", userSaleActiveInfos.getData(i).getString("END_DATE", ""));
		        		tmpData.put("opId", userSaleActiveInfos.getData(i).getString("TRADE_STAFF_ID", ""));
		        		if (!"".equals(userSaleActiveInfos.getData(i).getString("TRADE_STAFF_ID", "")))
		        		{
		        			IDataset staffInfos = StaffInfoQry.queryValidStaffById(userSaleActiveInfos.getData(i).getString("TRADE_STAFF_ID", ""));	
		        			  if(!IDataUtil.isEmpty(staffInfos)){
		        				  tmpData.put("orgId", staffInfos.getData(0).getString("DEPART_ID", ""));
		        			  }
		        		}
		        		else{
		        			tmpData.put("orgId", "");
		        		}
		        		tmpData.put("specpasswdPrv", "");
		        		ids.add(i, tmpData);		        		
		        	}
			        
		        	data.put("infos", ids);
		        	object.put("result", data);
		            object.put("respCode", "0");
		            object.put("respDesc", "success");
		            
		            result.put("object", object);
		            result.put("bean", bean);
		            result.put("beans", beans);
		    		result.put("rtnCode", "0");	
		    		result.put("rtnMsg", "成功");
		    		
		        }
		        else
		        {
		        	data.put("infos", ids);
		        	object.put("result", data);
		            object.put("respCode", "-1");
		            object.put("respDesc", "无法查询到有效的用户营销活动信息！");
		            
		            result.put("object", object);
		            result.put("bean", bean);
		            result.put("beans", beans);
		    		result.put("rtnCode", "-9999");	
		    		result.put("rtnMsg", "失败");	
		        }
			}

	        return result;
        }        
        catch (Exception e)
        {    		
        	data.put("outData", ids);
        	object.put("result", data);
            object.put("respCode", "-1");
            object.put("respDesc", "查询用户营销活动记录异常！"+e.getMessage());
            
            result.put("object", object);
            result.put("bean", bean);
            result.put("beans", beans);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
        }
    }
        
    public IData queryCustomerKeyMessage(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData data = new DataMap();
        String busiCode = param.getString("userMobile", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        try{
        	    	
	        if("".equals(busiCode))
	        {        	
	        	data.put("respCode","-1");
	        	data.put("respDesc","失败");
	        	result.put("bean", data);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");	
	            
	            return result;
	        }
	        IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0", busiCode);
	        if(IDataUtil.isEmpty(userinfo)){
	        	data.put("respCode","ZX3002");
	        	data.put("respDesc","非本省移动号码");
	            result.put("bean", data);
	    		result.put("rtnCode", "-9999");	
	    		result.put("rtnMsg", "失败");
	    		return result;
			}
	        else
	        {
		        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
		        
		        IData bean = new DataMap();	        
	
	        	//受理号码资费主套餐名称
	        	String productName = "";
	        	if(StringUtils.isNotBlank(ucaData.getProductId()))
        		{
        			productName = UProductInfoQry.getProductNameByProductId(ucaData.getProductId());
        		}
	        	bean.put("tariffPackages",productName);
	        	//套餐资费编码
	        	bean.put("tariffPackagesCode",ucaData.getProductId());
	        	
	        	//客户星级编码
	        	//客户星级
	        	IData inParam = new DataMap();	        	
	        	inParam.put("USER_ID", ucaData.getUserId());
	        	inParam.put("IDTYPE", "0");
	        	IDataset ids = getCreditInfo(inParam);
	        	if (IDataUtil.isNotEmpty(ids)){
	        		String creditClass = ids.getData(0).getString("CREDIT_CLASS", "");
	        		if ("0".equals(creditClass)){
		        		bean.put("telNumStarCode","12");
		        		bean.put("telNumStar","准星");
	        		}
	        		else if ("1".equals(creditClass)){
		        		bean.put("telNumStarCode","11");
		        		bean.put("telNumStar","一星");
	        		}
	        		else if ("2".equals(creditClass)){
		        		bean.put("telNumStarCode","10");
		        		bean.put("telNumStar","二星");
	        		}
	        		else if ("3".equals(creditClass)){
		        		bean.put("telNumStarCode","09");
		        		bean.put("telNumStar","三星");
	        		}
	        		else if ("4".equals(creditClass)){
		        		bean.put("telNumStarCode","08");
		        		bean.put("telNumStar","四星");
	        		}
	        		else if ("5".equals(creditClass)){
		        		bean.put("telNumStarCode","07");
		        		bean.put("telNumStar","五星（银/普通）");
	        		}
	        		else if ("6".equals(creditClass)){
		        		bean.put("telNumStarCode","06");
		        		bean.put("telNumStar","五星（金）");
	        		}
	        		else if ("7".equals(creditClass)){
		        		bean.put("telNumStarCode","05");
		        		bean.put("telNumStar","五星（钻）");
	        		}
	        		else if ("-1".equals(creditClass)){
		        		bean.put("telNumStarCode","13");
		        		bean.put("telNumStar","未评级");
	        		}
	        		else{
		        		bean.put("telNumStarCode","13");
		        		bean.put("telNumStar","未评级");
	        		}
	        	}
	        	else {
		        	bean.put("telNumStarCode","13");
	        		bean.put("telNumStar","未评级");
	        	}

	        	//归属业务区
	        	bean.put("customerAssignment",StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_AREA", "AREA_CODE", "AREA_NAME",ucaData.getCustomer().getCityCode()));
	        	//归属业务区编码
	        	bean.put("customerAssignmentCode",ucaData.getCustomer().getCityCode());
	        	//客户状态
	        	bean.put("telNumState",StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new java.lang.String[]
	        	 { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]{ "USER_STATE_CODESET", ucaData.getUser().getUserStateCodeset() }));
	        	//客户状态编码
	        	bean.put("telNumStateCode",ucaData.getUser().getUserStateCodeset());
	        	
	        	//客户群、2小时来电、未完成工单暂时放空！
	        	bean.put("customerNumber","");
	        	bean.put("twoHoursCalled","");
	        	bean.put("noEndWorkOrder","");
	        	
	        	//查询宽带信息
	        	IData userInfo = new DataMap();
	        	String wideState = "";
	            if ("KD_".equals(busiCode.substring(0, 3)))
	            {
	                userInfo = UcaInfoQry.qryUserInfoBySn(busiCode);
	            }
	            else
	            {
	                userInfo = UcaInfoQry.qryUserInfoBySn("KD_" + busiCode);
	            }
	            if (IDataUtil.isNotEmpty(userInfo))
	            {
	            	bean.put("broadbandCustomer","移动宽带");
	            	String wideStateName=StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new java.lang.String[]
	       	        	 { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]{ "USER_STATE_CODESET", userInfo.getString("USER_STATE_CODESET") });
	            	if(StringUtils.isNotBlank(wideStateName)){
	            		wideState=wideStateName;
	            	}
	            }
	            else{
	            	bean.put("broadbandCustomer","非宽带用户");
	            }
	            IData inMap = new DataMap();	 	 
	            inMap.put("USER_ID",ucaData.getUserId());	 	 
	            IDataset dataList = UserClassInfoQry.queryUserClass(inMap);	 	 
	            if(IDataUtil.isNotEmpty(dataList) && dataList.size()>0){ 	 	 
		                IData outMap = dataList.getData(0);	 	 
		            switch (Integer.parseInt(outMap.getString("USER_CLASS"))) {
		            case 1:
		            	bean.put("brandLevelCode", "04");
		            	bean.put("brandLevelName", "全球通-银卡");
		            	break;
		            case 2:
		            	bean.put("brandLevelCode", "03");
		            	bean.put("brandLevelName", "全球通-金卡");
		            	break;
		            case 3:
		            	bean.put("brandLevelCode", "02");
		            	bean.put("brandLevelName", "全球通-白金卡");
		            	break;
		            case 4:
		            	bean.put("brandLevelCode", "01");
		            	bean.put("brandLevelName", "全球通-钻石卡");
		            	break;
		            case 5:
		            	bean.put("brandLevelCode", "99");
		                String QQTClass = StaticUtil.getStaticValue("USER_INFO_CLASS", outMap.getString("USER_CLASS"));//转换成文字
		            	bean.put("brandLevelName", QQTClass);
		            	break;
		            case 6:
		            	bean.put("brandLevelCode", "05");
		            	bean.put("brandLevelName", "全球通-体验卡");
		            	break;
		            default:
		            	bean.put("brandLevelCode", "99");
	            	    bean.put("brandLevelName", "全球通其他级别");
		            	break;
		            }
	          }else{
	        	  bean.put("brandLevelCode", "");
          	      bean.put("brandLevelName", "");
	          }
	            
	        	bean.put("ext1",FuzzyPsptUtil.fuzzyName(ucaData.getCustomer().getCustName()));
	        	//REQ201812200006关于全球通客户IVR人工接续变更的需求 wuhao5       
	            IData inData = new DataMap();
	            inData.put("USER_ID", ucaData.getUserId());
	            IDataset dataset = UserClassInfoQry.queryUserClass(inData);
	    		if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){ 
	    			IData data1 = dataset.getData(0);
					String QQTClass = StaticUtil.getStaticValue("USER_INFO_CLASS", data1.getString("USER_CLASS"));
	    			bean.put("ext2",QQTClass);
	    		}else{
	    			bean.put("ext2", "否");
	    		}		
	    		//END REQ201812200006关于全球通客户IVR人工接续变更的需求 wuhao5	
	        	bean.put("ext3",wideState);
	        	bean.put("ext4","");
	        	bean.put("ext5","");
	        	bean.put("ext6","");
	        	
	        	/*
		 		 * REQ201907040024关于重要客户流程优化需求
		 		 * @author yanghb6
		 		 */
		        IDataset cusGroupInfo = CustGroupInfoQry.qryGroupInfoByCustId(ucaData.getUserId());
				if (IDataUtil.isNotEmpty(cusGroupInfo))
		        {
					IData groupInfo = cusGroupInfo.getData(0);
					String memberKind = groupInfo.getString("MEMBER_KIND","");
					if("3".equals(memberKind)) {
						bean.put("ext4", "是");
					}else {
						bean.put("ext4", "否");
					}
					
		        }
				
		 		//REQ201909300001关于新一代客户系统增加重点保拓拓展字段需求 wuwangfeng
		        IDataset importCustomer = CustGroupInfoQry.qryImportCustomerByNum(ucaData.getSerialNumber());
				if (IDataUtil.isNotEmpty(importCustomer) && importCustomer.size()>0) {
					bean.put("ext5", "是");										
		        }else {
					bean.put("ext5", "否");
				}
	        	
	        	bean.put("respCode","0");
	        	bean.put("respDesc","成功");
	        	
		        result.put("bean", bean);
		        result.put("rtnMsg", "成功");
		        result.put("rtnCode", "0");
			}

	        return result;
        }        
        catch (Exception e)
        {
        	data.put("respCode","-1");
        	data.put("respDesc","失败");
        	result.put("bean", data);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
    		log.error(e.getMessage()+"--->param："+param);
    		
            return result;
        }
    }
    
    public IData queryAllInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();    	
        String busiCode = param.getString("userMobile", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        
	        outData.put("userMobile", busiCode);
	        outData.put("userId", ucaData.getUserId());
	        outData.put("custName", FuzzyPsptUtil.fuzzyName(ucaData.getCustomer().getCustName()));
	        
	        String stateCode = ucaData.getUser().getUserStateCodeset();
	        outData.put("osState", stateCode);
	        outData.put("osStateDesc", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
	        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
	        		new java.lang.String[]{"USER_STATE_CODESET", stateCode}));
	        IData inParam = new DataMap();	        	
        	inParam.put("USER_ID", ucaData.getUserId());
        	inParam.put("IDTYPE", "0");
        	IDataset ids1 = getCreditInfo(inParam);
        	if (IDataUtil.isNotEmpty(ids1)){
        		String creditClass = ids1.getData(0).getString("CREDIT_CLASS", "");
        		if ("0".equals(creditClass)){
        			//outData.put("starLevel","准星");
        			outData.put("starLevel","12");
        		}
        		else if ("1".equals(creditClass)){
        			//outData.put("starLevel","一星");
        			outData.put("starLevel","11");
        		}
        		else if ("2".equals(creditClass)){
        			//outData.put("starLevel","二星");
        			outData.put("starLevel","10");
        		}
        		else if ("3".equals(creditClass)){
        			//outData.put("starLevel","三星");
        			outData.put("starLevel","09");
        		}
        		else if ("4".equals(creditClass)){
        			//outData.put("starLevel","四星");
        			outData.put("starLevel","08");
        		}
        		else if ("5".equals(creditClass)){
        			//outData.put("starLevel","五星（银/普通）");
        			outData.put("starLevel","07");
        		}
        		else if ("6".equals(creditClass)){
        			//outData.put("starLevel","五星（金）");
        			outData.put("starLevel","06");
        		}
        		else if ("7".equals(creditClass)){
        			//outData.put("starLevel","五星（钻）");
        			outData.put("starLevel","05");
        		}
        		else if ("-1".equals(creditClass)){
	        		//outData.put("starLevel","未评级");
	        		outData.put("starLevel","13");
        		}
        		else{
	        		//outData.put("starLevel","未评级");
	        		outData.put("starLevel","13");
        		}
    	        outData.put("creditValue", ids1.getData(0).getString("CREDIT_VALUE", ""));
        	}
        	else
        	{
        		//outData.put("starLevel", "未评级");
        		outData.put("starLevel", "13");
    	        outData.put("creditValue", "");
        	}
	        outData.put("regionName", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_AREA", "AREA_CODE", "AREA_NAME", ucaData.getCustomer().getCityCode()));
	        outData.put("portableName", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_AREA", "AREA_CODE", "AREA_NAME", ucaData.getCustomer().getCityCode()));
	        outData.put("carryRegion", "");
	        outData.put("carryOperator", "");
	        if("".equals(ucaData.getUser().getLastStopTime()) || ucaData.getUser().getLastStopTime() == null)
	        {
	        	outData.put("lastStopTime", "");
	        }
	        else{
	        	outData.put("lastStopTime", SysDateMgr.date2String((SysDateMgr.string2Date(ucaData.getUser().getLastStopTime(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        }
	        outData.put("lastStopReason", "");
	        outData.put("groupId", "");
	        outData.put("groupName", "");
	        outData.put("shortNum", "");
	        outData.put("grpManagerName", "");
	        outData.put("grpManagerPhone", "");

	        outData.put("userBegin", SysDateMgr.date2String((SysDateMgr.string2Date(ucaData.getUser().getOpenDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        outData.put("netAge", "");
	        outData.put("starScore", "");
	        
	        outData.put("starTime", "");
	        if("0".equals(ucaData.getCustomer().getIsRealName()))
	        {
		        outData.put("realNameInfo", "非实名");
	        }
	        else if("1".equals(ucaData.getCustomer().getIsRealName()))
	        {
		        outData.put("realNameInfo", "实名");
	        }
	        else 
	        {
		        outData.put("realNameInfo", "");
	        }

	        outData.put("highLevelId", "");
	        IData input = new DataMap();
	        input.put("USER_ID",ucaData.getUserId());
	        IDataset userResInfo = qryUserResSimInfo(input);
	        if(!IDataUtil.isEmpty(userResInfo)){
	        outData.put("simCardNo", userResInfo.getData(0).getString("SIM_CARD_NO", ""));
	        outData.put("simCardType", userResInfo.getData(0).getString("RES_KIND_NAME", ""));
	        }
	        
	        //2018-05-04与符龙斌确认flag4G通过用户SIM卡类型进行判断。
	        boolean isSim4G = false;
	        String simCardNo = userResInfo.getData(0).getString("SIM_CARD_NO", "");
	 		if(!"".equals(simCardNo))
	 		{
	 			isSim4G = is4GUser(simCardNo);
	 		}
	        if (isSim4G)
	        {
	        	outData.put("flag4G", "是");
	        }
	        else
	        {
	        	outData.put("flag4G", "否");
	        }
	        
	        outData.put("certTypeName", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
	        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
	        		new java.lang.String[]{"TD_S_PASSPORTTYPE", ucaData.getCustomer().getPsptTypeCode()}));
	        outData.put("certCode", FuzzyPsptUtil.fuzzyPsptId(ucaData.getCustomer().getPsptTypeCode(),ucaData.getCustomer().getPsptId()));
	        
	        outData.put("terminalInfo", "");
	        outData.put("payType", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
	        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"DATA_NAME", 
	        		new java.lang.String[]{"PRODUCT_PREPAYTAG", ucaData.getUser().getPrepayTag()}));
	        
	        outData.put("payMethod", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_PAYMODE", "PAY_MODE_CODE", "PAY_MODE",ucaData.getAccount().getPayModeCode()));
	        outData.put("servType", "");//鉴权方式？？？
	        //客户经理k3
	        IData telNo = new DataMap();
	        String custManager ="";
	        telNo.put("SERIAL_NUMBER", busiCode);
	        IDataset vipCustInfo = Qry360InfoDAO.qryTelManager(telNo);
	        if(IDataUtil.isNotEmpty(vipCustInfo)){
	        	String custManagerName=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TF_F_CUST_MANAGER_STAFF", "CUST_MANAGER_ID", "CUST_MANAGER_NAME", vipCustInfo.getData(0).getString("CUST_MANAGER_ID"));
	        	if(StringUtils.isNotBlank(custManagerName)){
	        		custManager=custManagerName;
	        	}
	        }
	        outData.put("managerName", custManager);
	        outData.put("managerPhone", "");
	        outData.put("highQualityFlag", "");//优质号码标识???
	        outData.put("openOrgName", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",ucaData.getCustomer().getInDepartId()));
	        outData.put("userDeviceType", "");
	        outData.put("collectionChnn", "");
	        outData.put("userBrand", UpcCall.queryBrandNameByChaVal( ucaData.getBrandCode()));
	        outData.put("userLevel", "");
	        
	        outData.put("vipLevel", "");
	        outData.put("vipNumber", "");
	        outData.put("vipDate", "");
	        outData.put("planNumber", "");
	        outData.put("accoutDay", ucaData.getAcctDay());
	        //短号k3
	        String shortNo = "无";
	        IData resData = new DataMap();
	        resData.put("USER_ID", ucaData.getUserId());
	        Qry360InfoDAO dao = new Qry360InfoDAO();
	        IDataset resinfos = dao.qryResourceInfo(resData, null);
	        if(IDataUtil.isNotEmpty(resinfos)){
	        	for(int k=0;k<resinfos.size();k++){
	        		String resTypeCode = resinfos.getData(k).getString("RES_TYPE_CODE","");
	        		if("S".equals(resTypeCode)){
	        			shortNo=resinfos.getData(k).getString("RES_CODE","无");
	        			break;
	        		}
	        	}
	        }
	      //判断是否是一卡多号号码k3
			IDataset relationList=OneCardMultiNoQry.qryRelationList( ucaData.getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,null,null);
			if(null==relationList||relationList.isEmpty()){
				outData.put("moreCard", "0");
			}else{
				outData.put("moreCard", "1");
			}
	        
	        if("0".equals(ucaData.getUser().getAcctTag()))
	        {
		        outData.put("ext1", "正常处理");
	        }
	        else if("1".equals(ucaData.getUser().getAcctTag()))
	        {
		        outData.put("ext1", "定时激活");
	        }
	        else if("2".equals(ucaData.getUser().getAcctTag()))
	        {
		        outData.put("ext1", "待激活用户");
	        }
	        else if("Z".equals(ucaData.getUser().getAcctTag()))
	        {
		        outData.put("ext1", "不出账");
	        }
	        else
	        {
		        outData.put("ext1", "不出账");
	        }
	        IDataset ids = UserForegiftInfoQry.getUserForegift(ucaData.getUserId());
	        if(!IDataUtil.isEmpty(ids)){
	        	outData.put("ext2", "有押金");
	        }
	        else{
	        	outData.put("ext2", "无押金");
	        }
	        outData.put("ext3", userResInfo.getData(0).getString("IMSI", ""));//客户IMSI号
	        outData.put("ext4", shortNo);
	        outData.put("ext5", "");
	        outData.put("ext6", "");
	        outData.put("ext7", "");
	        outData.put("ext8", "");
	        outData.put("ext9", "");
	        outData.put("ext10", "");
	        
	    	boolean isBlackTag = UCustBlackInfoQry.isBlackCust( ucaData.getCustomer().getPsptTypeCode(),ucaData.getCustomer().getPsptId());
	    	boolean isBeautifulTag = getBeautifulTag(busiCode);
	    	//System.out.println("--------------queryAllInfo-----------isBeautifulTag:"+isBeautifulTag+",isBlackTag:"+isBlackTag);
	    	if(isBlackTag && isBeautifulTag)
	    	{
		        outData.put("custTag", "1;2"); //表示即是黑名单，又是吉祥号
	    	}
	    	else if(isBlackTag && !isBeautifulTag)
	    	{
		        outData.put("custTag", "1"); //表示是黑名单
	    	}
	    	else if(!isBlackTag && isBeautifulTag){
	    		outData.put("custTag", "2"); //表示是吉祥号
	    	}
	    	else{
	    		outData.put("custTag", "");
	    	}
	    		    	
	        if(!IDataUtil.isEmpty(outData)){

		        
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户宽带信息！",outData);	
		    }
	        //System.out.println("================C898HQQueryAllInfo================result:"+result);
	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
	        //System.out.println("================C898HQQueryAllInfo================result:"+result);
            return result;
        }
    }
    
    public IData querySubscribeInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();    	
    	IDataset ids= new DatasetList();
    	IData outData2 = new DataMap();    	
    	IDataset ids2= new DatasetList();
        String busiCode = param.getString("userMobile", "");
        String queryType = param.getString("queryType", "");
        String queryMode = param.getString("queryMode", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(queryType))
        {
        	queryType = getParams(param).getString("queryType", "");
        }
        if ( "".equals(queryMode))
        {
        	queryMode = getParams(param).getString("queryMode", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if("".equals(queryType))
	        {        		            
	            result = prepareOutResult(1,"queryType不能为空",outData);
	            return result;
	        }
/*	        if("".equals(queryMode))
	        {        		            
	            result = prepareOutResult(1,"queryMode不能为空",outData);
	            return result;
	        }*/
	        if ("00".equals(queryType))//00-所有个人商品
	        {
	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	 
	        	IDataset offerAttrList= new DatasetList();
        		IDataset userSvcInfos = new DatasetList();
        		IDataset userPlatsvcs = new DatasetList();
        		IDataset userSaleActives = new DatasetList();
        		IDataset userProductInfos = new DatasetList();
        		IDataset userDiscntInfos = new DatasetList();
        		IDataset userRelationInfos = new DatasetList();//用户关系分为用户与用户的关系和用户与集团的关系
        		IDataset acctOpenInfos = new DatasetList();

        		if("0".equals(queryMode) || "".equals(queryMode))
	        	{
        			userSvcInfos = UserSvcInfoQry.queryUserAllSvc(ucaData.getUserId());
        			userPlatsvcs = UserPlatSvcInfoQry.queryPlatSvcByUserIdNew(ucaData.getUserId());
        			userSaleActives =  UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(ucaData.getUserId());
        			userProductInfos = UserProductInfoQry.queryUserMainProduct(ucaData.getUserId());
        			userDiscntInfos = UserDiscntInfoQry.getAllDiscntInfo(ucaData.getUserId());
        			userRelationInfos = qryRelationInfo(ucaData.getUserId());
        			acctOpenInfos = queryOpenInfo(busiCode);

	        	}
        		else if ("1".equals(queryMode))
	        	{
        			userSvcInfos = UserSvcInfoQry.queryUserSvcByUserId(ucaData.getUserId(),"2");
        			userPlatsvcs = queryPlatorderHisinfoByUserId(ucaData.getUserId());
        			userSaleActives = getExpirSaleActiveByUserId(ucaData.getUserId());
        			userProductInfos = queryUserProductHisInfos(ucaData.getUserId());
        			userDiscntInfos = queryUserExpiryDiscntsByUserId(ucaData.getUserId());
        			userRelationInfos = qryInvalidRelationInfo(ucaData.getUserId());
	        	}
	        	else
	        	{
	        		result = prepareOutResult(1,"不支持的queryMode值："+queryMode,outData);
		            return result;
	        	}
        		
        		if (IDataUtil.isNotEmpty(userProductInfos)){
            		for (int i=0 ; i < userProductInfos.size() ; i++)
    	        	{
    	        		IData tmpData = new DataMap();
    	        		String productId = userProductInfos.getData(i).getString("PRODUCT_ID", "");
    	        		IDataset catalogInfos = UpcCall.qryCatalogByOfferId(productId,"P");
    	        		String catalogId = "";
    	        		String catalogName = "";
    	        		if(IDataUtil.isNotEmpty(catalogInfos)){
    	        			catalogId = catalogInfos.getData(0).getString("CATALOG_ID", "");
    	        			catalogName = catalogInfos.getData(0).getString("CATALOG_NAME", "");
    	        		}
    	        		tmpData.put("offerId", productId+"|P");
    	        		tmpData.put("offerName", UProductInfoQry.getProductNameByProductId(productId));
    	        		tmpData.put("upOfferId", catalogId);
    	        		tmpData.put("upOfferName", catalogName);
    	        		tmpData.put("isPackage", "1");
    	        		
    	        		tmpData.put("spCode", "");
    	        		tmpData.put("bizCode", "");
    	        		tmpData.put("spName", "");
    	        		tmpData.put("billType", "");
    	        		tmpData.put("servType", "");
    	        		
    	        		//查询用户产品下的必选优惠
    	        		//IDataset userMainDiscnts = queryUserMainElements(ucaData.getUserId(),productId);
    	        		IDataset userMainDiscnts = qryComRelOffersByOfferId(productId);

    	        		float price = 0 ;
    	        		if(IDataUtil.isNotEmpty(userMainDiscnts)){
    	        			for (int m=0 ; m < userMainDiscnts.size() ; m++)
    	    	        	{
    	    	        		if(IDataUtil.isNotEmpty(acctOpenInfos)){
    	    						for(int j=0;j<acctOpenInfos.size();j++){
    	    							if(userMainDiscnts.getData(m).getString("ELEMENT_ID","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_ID",""))
    	    									&& "1".equals(acctOpenInfos.getData(j).getString("FEE_FLAG",""))){
    	    								Float monthRent = Float.parseFloat(acctOpenInfos.getData(j).getString("MONTH_RENT","0"));
    	    								if(monthRent > 0)
    										{
    	    									price += monthRent;
    										}    	    								
    	    							}
    	    						}
    	    	        		}
    	    	        	}
    	        		}
    	        		tmpData.put("offerPrice", Float.toString(price*100)+"/月");
    	        		if("80010484".equals(productId)) //流量王卡18元套餐
    	        		{
        	        		tmpData.put("offerPrice","1800/月");
    	        		}
    	        		if("10003377".equals(productId)) // 神州行流量日租卡套餐
    	        		{
    	        			tmpData.put("offerPrice","0/月");
    	        		}
    	        		
    	        		if("0".equals(queryMode) || "".equals(queryMode))
    		        	{
    	        			tmpData.put("status", "0");
    		        	}
    	        		else
    	        		{
    	        			tmpData.put("status", "");
    		        	}	
    	        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userProductInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

    	        		tmpData.put("offerDesc", UProductInfoQry.getProductExplainByProductId(productId));
    	        		tmpData.put("offerType", "P");
    	        		tmpData.put("offerTypeName", "产品");
    	        		tmpData.put("brandId", userProductInfos.getData(i).getString("BRAND_CODE", ""));
    	        		tmpData.put("brandName", UpcCall.queryBrandNameByChaVal( userProductInfos.getData(i).getString("BRAND_CODE", "")));
    	        		tmpData.put("isMain", userProductInfos.getData(i).getString("MAIN_TAG", ""));
    	        		tmpData.put("chooseTag", "");
    	        		tmpData.put("canUnsub", "1");

    	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userProductInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
    					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userProductInfos.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
    	        		List<AttrTradeData> userAttrs = ucaData.getUserAttrsByRelaInstId(userProductInfos.getData(i).getString("INST_ID", ""));
    	        		for (int j=0 ; j < userAttrs.size() ; j++)
    		        	{
    			        	IData tmpData1 = new DataMap();
    		        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
    		        		tmpData1.put("attrName", "");
    		        		tmpData1.put("attrDesc", "");
    		        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
    		        		tmpData1.put("attrValueDesc", "");
    		        		offerAttrList.add(tmpData);
    		        	}
    	        		tmpData.put("offerAttrList", offerAttrList);
    	        		
    	        		tmpData.put("orderStaffid", userProductInfos.getData(i).getString("UPDATE_STAFF_ID", ""));
    	        		tmpData.put("orderChnn", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",
    	        				userProductInfos.getData(i).getString("UPDATE_DEPART_ID", "")));
    	        		tmpData.put("remark", userProductInfos.getData(i).getString("REMARK", ""));
    	        		tmpData.put("isRemoved", "");
    	        		tmpData.put("instructions", "");
    	        		tmpData.put("bossId", productId+"|P");
    	        		tmpData.put("prodInstId", userProductInfos.getData(i).getString("INST_ID", ""));
    	        		
    	        		tmpData.put("orderDep", userProductInfos.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
    	        		tmpData.put("operModuleCode", "");
    	        		tmpData.put("operModuleName", "");
    	        		tmpData.put("operArea", "");
    	        		tmpData.put("shortNum", "");
    	        		tmpData.put("servAreaName", "");
    	        		tmpData.put("offerSts", "");//用户订购实例状态
    	        		tmpData.put("flowLongDistanceAjust", "");
    	        		tmpData.put("voiceLongDistanceAjust", "");
    	        		tmpData.put("flowBusiType", "");
    	        		tmpData.put("bindActId", "");
    	        		tmpData.put("bindActName", "");
    	        		tmpData.put("bindActEndTime", "");
    	        		tmpData.put("offerPriceDesc", "");
    	        		tmpData.put("lastOrderStaff", "");
    	        		tmpData.put("lastOrderChnn", "");
    	        		tmpData.put("lastOrderTime", "");
    	        		tmpData.put("priority", "");
    	        		tmpData.put("isLog", "0");

    	        		ids.add(tmpData);
    	        	}
            	}
        		
        		if (IDataUtil.isNotEmpty(userSvcInfos))
        		{
	        		for (int i=0 ; i < userSvcInfos.size() ; i++)
		        	{
	        			IData tmpData = new DataMap();
		        		String serviceId = userSvcInfos.getData(i).getString("SERVICE_ID", "");
		        		IDataset catalogInfos = UpcCall.qryCatalogByOfferId(serviceId,"S");
		        		String catalogId = "";
		        		String catalogName = "";
		        		if(IDataUtil.isNotEmpty(catalogInfos)){
		        			catalogId = catalogInfos.getData(0).getString("CATALOG_ID", "");
		        			catalogName = catalogInfos.getData(0).getString("CATALOG_NAME", "");
		        		}
		        		tmpData.put("offerId", serviceId+"|S");
		        		tmpData.put("offerName", USvcInfoQry.getSvcNameBySvcId(serviceId));
		        		tmpData.put("upOfferId", catalogId);
		        		tmpData.put("upOfferName", catalogName);
		        		tmpData.put("isPackage", "1");
		        		
		        		tmpData.put("spCode", "");
		        		tmpData.put("bizCode", "");
		        		tmpData.put("spName", "");
		        		tmpData.put("billType", "");
		        		tmpData.put("servType", "");
		        		
		        		if("0".equals(queryMode) || "".equals(queryMode))
    		        	{
    	        			tmpData.put("status", "0");
    		        	}
    	        		else
    	        		{
    	        			tmpData.put("status", "");
    		        	}
		        		tmpData.put("canUnsub", "1");
		        		//有集团彩铃的用户不能在个人产品变更页面退订彩铃
		        		if("20".equals(userSvcInfos.getData(i).getString("SERVICE_ID", ""))
		        				&& IDataUtil.dataSetContainsKeyAndValue(userSvcInfos, "SERVICE_ID", "910")
		        				){
		        			tmpData.put("canUnsub", "0");
		        		}
						//不能在个人产品变更页面退订集团彩铃
						if("910".equals(userSvcInfos.getData(i).getString("SERVICE_ID", ""))){
							tmpData.put("canUnsub", "0");
						}


		        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	
		        		tmpData.put("offerDesc", USvcInfoQry.getSvcExplainBySvcId(serviceId));
		        		tmpData.put("offerType", "S");
		        		tmpData.put("offerTypeName", "服务");
		        		tmpData.put("brandId", "");
		        		tmpData.put("brandName", "");
		        		tmpData.put("isMain", userSvcInfos.getData(i).getString("MAIN_TAG", ""));
		        		tmpData.put("chooseTag", "");
		        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
						tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfos.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

		        		tmpData.put("offerAttrList", offerAttrList);
		        		
		        		tmpData.put("orderStaffid", userSvcInfos.getData(i).getString("UPDATE_STAFF_ID", ""));
		        		tmpData.put("orderChnn", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",
		        				userSvcInfos.getData(i).getString("UPDATE_DEPART_ID", "")));
		        		tmpData.put("remark", userSvcInfos.getData(i).getString("REMARK", ""));
		        		
		        		tmpData.put("bossId", serviceId+"|S");
    	        		tmpData.put("prodInstId", userSvcInfos.getData(i).getString("INST_ID",""));
    	        		tmpData.put("isRemoved", "");
    	        		tmpData.put("instructions", "");
	        			tmpData.put("offerPrice", "");
		        		if(IDataUtil.isNotEmpty(acctOpenInfos)){
							for(int j=0;j<acctOpenInfos.size();j++){
								if(userSvcInfos.getData(i).getString("INST_ID","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_INS_ID",""))){
									Float price = Float.parseFloat(acctOpenInfos.getData(j).getString("MONTH_RENT","0"))*100;
									if(price > 0)
									{
										tmpData.put("offerPrice",price+"/月");
									}
								}
							}
		        		}
		        		
    	        		tmpData.put("orderDep", userSvcInfos.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
    	        		tmpData.put("operModuleCode", "");
    	        		tmpData.put("operModuleName", "");
    	        		tmpData.put("operArea", "");
    	        		tmpData.put("shortNum", "");
    	        		tmpData.put("servAreaName", "");
    	        		tmpData.put("offerSts", "");//用户订购实例状态
    	        		tmpData.put("flowLongDistanceAjust", "");
    	        		tmpData.put("voiceLongDistanceAjust", "");
    	        		tmpData.put("flowBusiType", "");
    	        		tmpData.put("bindActId", "");
    	        		tmpData.put("bindActName", "");
    	        		tmpData.put("bindActEndTime", "");
    	        		tmpData.put("offerPriceDesc", "");
    	        		tmpData.put("lastOrderStaff", "");
    	        		tmpData.put("lastOrderChnn", "");
    	        		tmpData.put("lastOrderTime", "");
    	        		tmpData.put("priority", "");
    	        		tmpData.put("isLog", "0");

		        		ids.add(tmpData);

		        	}
        		}
        		if (IDataUtil.isNotEmpty(userPlatsvcs))
        		{
	        		for (int i=0 ; i < userPlatsvcs.size() ; i++)
		        	{
		        		IData tmpData = new DataMap();
		        		
		        		IDataset spServiceInfo = UpcCall.qrySpServiceSpInfo(userPlatsvcs.getData(i).getString("SERVICE_ID", ""),"Z");
		        		if (IDataUtil.isNotEmpty(spServiceInfo)){
		        			tmpData.put("spCode", spServiceInfo.getData(0).getString("SP_CODE", ""));
		        			tmpData.put("bizCode", spServiceInfo.getData(0).getString("BIZ_CODE", ""));
		        			tmpData.put("spName", spServiceInfo.getData(0).getString("SP_NAME", ""));
		        			tmpData.put("servType", spServiceInfo.getData(0).getString("BIZ_TYPE_CODE", ""));
		        			
		        			String billFlag = spServiceInfo.getData(0).getString("BILL_TYPE", "");
			        		if ("0".equals(billFlag))
			        		{
			        			tmpData.put("billType", "免费");	
			        		}
			        		else if ("1".equals(billFlag))
			        		{
			        			tmpData.put("billType", "按条计费");	
			        		}
			        		else if ("2".equals(billFlag))
			        		{
			        			tmpData.put("billType", "包月计费");	
			        		}
			        		else if ("3".equals(billFlag))
			        		{
			        			tmpData.put("billType", "包时计费");	
			        		}
			        		else if ("4".equals(billFlag))
			        		{
			        			tmpData.put("billType", "包次计费");	
			        		}
			        		else {
			        			tmpData.put("billType", "");	
			        		}
			        		int price = Integer.parseInt(spServiceInfo.getData(0).getString("PRICE", "0"));
			        		if( price >= 0 )
			        		{
			        			price = price/10;
			        		}
			        		tmpData.put("offerPrice", String.valueOf(price)+"/月");
			        		
	    	        		//k3
	    	        		String serv_mode = spServiceInfo.getData(0).getString("SERV_MODE", "");//0梦网业务，1自有业务
	    	        		if(StringUtils.isNotBlank(serv_mode)&&"0".equals(serv_mode)){
	    	        			String spCode = spServiceInfo.getData(0).getString("SP_CODE", "");
	    	        			String spId = spServiceInfo.getData(0).getString("SP_ID", "");
	    	        			String spName = spServiceInfo.getData(0).getString("SP_NAME", "");
	    	        			String spType = spServiceInfo.getData(0).getString("SP_TYPE", "");
	    	        			IDataset spInfo = UpcCallIntf.querySpInfoByCond(spId,spCode,spName,spType);
	    	        			tmpData.put("bizName", spServiceInfo.getData(0).getString("BIZ_NAME", ""));
	    	        			tmpData.put("spType", spServiceInfo.getData(0).getString("BIZ_TYPE_CODE", ""));
	    	        			tmpData.put("isRelevanceOffer", "1");
	    	        			tmpData.put("isSonOffer", "1");
	    	        			if(IDataUtil.isNotEmpty(spInfo)){
	    	        				tmpData.put("messagePort",spInfo.getData(0).getString("SERV_CODE",""));
	    	        			}else{
	    	        				tmpData.put("messagePort","");
	    	        			}
	    	        		}
		        		}
		        		else
		        		{
		        			tmpData.put("spCode", "");
		        			tmpData.put("bizCode", "");
		        			tmpData.put("spName", "");
		        			tmpData.put("servType", "");
		        			tmpData.put("billType", "");
		        			tmpData.put("bizName","");
	        				tmpData.put("messagePort","");
	        				tmpData.put("spType", "");
	        				tmpData.put("isRelevanceOffer", "1");
	        				tmpData.put("isSonOffer", "1");
		        		}
		        		tmpData.put("offerId", userPlatsvcs.getData(i).getString("SERVICE_ID", "")+"|Z");
		        		tmpData.put("offerName", UPlatSvcInfoQry.getSvcNameBySvcId(userPlatsvcs.getData(i).getString("SERVICE_ID", "")));
		        		tmpData.put("upOfferId", "");
		        		tmpData.put("upOfferName", "");
		        		tmpData.put("isPackage", "0");
		        		
/*	        			tmpData.put("offerPrice", "");
		        		if(IDataUtil.isNotEmpty(acctOpenInfos)){
							for(int j=0;j<acctOpenInfos.size();j++){
								if(tmpData.getString("bizCode","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_INS_ID",""))
										&& tmpData.getString("spCode","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_ID",""))){
									Float price = Float.parseFloat(acctOpenInfos.getData(j).getString("MONTH_RENT","0"))*100;
									if(price > 0)
									{
										tmpData.put("offerPrice",price+"/月");
									}
								}
							}
		        		}*/
		        		
		        		if("0".equals(queryMode) || "".equals(queryMode))
    		        	{
    	        			tmpData.put("status", "0");
    		        	}
    	        		else
    	        		{
    	        			tmpData.put("status", "");
    		        	}
		        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvcs.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
		        		tmpData.put("offerDesc", UPlatSvcInfoQry.getSvcExplainBySvcId(userPlatsvcs.getData(i).getString("SERVICE_ID", "")));
		        		tmpData.put("offerType", "Z");
		        		tmpData.put("offerTypeName", "平台服务");
		        		tmpData.put("brandId", "");
		        		tmpData.put("brandName", "");
		        		tmpData.put("isMain", "0");
		        		tmpData.put("chooseTag", "");
		        		tmpData.put("canUnsub", "1");
	
		        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvcs.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
						tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvcs.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
		        		
		        		IDataset offerAttrList1= new DatasetList();
		        		List<AttrTradeData> userAttrs = ucaData.getUserAttrsByRelaInstId(userPlatsvcs.getData(i).getString("INST_ID", ""));
		        		for (int j=0 ; j < userAttrs.size() ; j++)
			        	{
				        	IData tmpData1 = new DataMap();
			        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
			        		tmpData1.put("attrName", "");
			        		tmpData1.put("attrDesc", "");
			        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
			        		tmpData1.put("attrValueDesc", "");
			        		offerAttrList1.add(tmpData1);
			        	}
		        		tmpData.put("offerAttrList", offerAttrList1);
		        		tmpData.put("orderStaffid", userPlatsvcs.getData(i).getString("UPDATE_STAFF_ID", ""));
		        		tmpData.put("orderChnn", "");
		        		tmpData.put("remark", ucaData.getUserPlatSvcs().get(i).getRemark());

		        		tmpData.put("bossId", userPlatsvcs.getData(i).getString("SERVICE_ID", "")+"|Z");
    	        		tmpData.put("prodInstId", userPlatsvcs.getData(i).getString("INST_ID", ""));
    	        		tmpData.put("isRemoved", "");
    	        		tmpData.put("instructions", "");
    	        		
    	        		tmpData.put("orderDep", userPlatsvcs.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
    	        		tmpData.put("operModuleCode", "");
    	        		tmpData.put("operModuleName", "");
    	        		tmpData.put("operArea", "");
    	        		tmpData.put("shortNum", "");
    	        		tmpData.put("servAreaName", "");
    	        		tmpData.put("offerSts", "");//用户订购实例状态
    	        		tmpData.put("flowLongDistanceAjust", "");
    	        		tmpData.put("voiceLongDistanceAjust", "");
    	        		tmpData.put("flowBusiType", "");
    	        		tmpData.put("bindActId", "");
    	        		tmpData.put("bindActName", "");
    	        		tmpData.put("bindActEndTime", "");
    	        		tmpData.put("offerPriceDesc", "");
    	        		tmpData.put("lastOrderStaff", "");
    	        		tmpData.put("lastOrderChnn", "");
    	        		tmpData.put("lastOrderTime", "");
    	        		tmpData.put("priority", "");
    	        		tmpData.put("isLog", "0");
		        		ids.add(tmpData);

		        	}
        		}
        		if (IDataUtil.isNotEmpty(userSaleActives))
        		{
	        		for (int i=0 ; i < userSaleActives.size() ; i++)
		        	{
		        		
		        		IData tmpData = new DataMap();
	        			tmpData.put("spCode", "");
	        			tmpData.put("bizCode", "");
	        			tmpData.put("spName", "");
	        			tmpData.put("billType", "");
	        			tmpData.put("servType", "");
		        		tmpData.put("offerId", userSaleActives.getData(i).getString("PACKAGE_ID","")+"|K");
		        		tmpData.put("offerName", userSaleActives.getData(i).getString("PACKAGE_NAME",""));
		        		tmpData.put("upOfferId", "");
		        		tmpData.put("upOfferName", "");
		        		tmpData.put("isPackage", "1");
		        		
		        		tmpData.put("offerPrice", "");
		        		if("0".equals(queryMode) || "".equals(queryMode))
    		        	{
    	        			tmpData.put("status", "0");
    		        	}
    	        		else
    	        		{
    	        			tmpData.put("status", "");
    		        	}
		        		if (!"".equals(userSaleActives.getData(i).getString("ACCEPT_TIME", "")))
		        		{
		        			tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSaleActives.getData(i).getString("ACCEPT_TIME", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
		        		}
		        		else
		        		{
		        			tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSaleActives.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

		        		}
		        		tmpData.put("offerDesc", UPackageInfoQry.getPackageExplainByPackageId(userSaleActives.getData(i).getString("PACKAGE_ID","")));
		        		tmpData.put("offerType", "K");
		        		tmpData.put("offerTypeName", "营销活动");
		        		tmpData.put("brandId", "");
		        		tmpData.put("brandName", "");
		        		tmpData.put("isMain", "0");
		        		tmpData.put("chooseTag", "");
		        		tmpData.put("canUnsub", "1");
	
		        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSaleActives.getData(i).getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
						tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSaleActives.getData(i).getString("END_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	
		        		IDataset offerAttrList2= new DatasetList();
		        		tmpData.put("offerAttrList", offerAttrList2);
		        		
		        		tmpData.put("orderStaffid", userSaleActives.getData(i).getString("TRADE_STAFF_ID",""));
		        		tmpData.put("orderChnn", userSaleActives.getData(i).getString("UPDATE_DEPART_ID",""));
		        		tmpData.put("remark", userSaleActives.getData(i).getString("REMARK",""));
		        		
		        		tmpData.put("bossId", userSaleActives.getData(i).getString("PACKAGE_ID","")+"|K");
    	        		tmpData.put("prodInstId", userSaleActives.getData(i).getString("INST_ID", ""));
    	        		tmpData.put("isRemoved", "");
    	        		tmpData.put("instructions", "");
    	        		
    	        		tmpData.put("orderDep", userSaleActives.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
    	        		tmpData.put("operModuleCode", "");
    	        		tmpData.put("operModuleName", "");
    	        		tmpData.put("operArea", "");
    	        		tmpData.put("shortNum", "");
    	        		tmpData.put("servAreaName", "");
    	        		tmpData.put("offerSts", "");//用户订购实例状态
    	        		tmpData.put("flowLongDistanceAjust", "");
    	        		tmpData.put("voiceLongDistanceAjust", "");
    	        		tmpData.put("flowBusiType", "");
    	        		tmpData.put("bindActId", "");
    	        		tmpData.put("bindActName", "");
    	        		tmpData.put("bindActEndTime", "");
    	        		tmpData.put("offerPriceDesc", "");
    	        		tmpData.put("lastOrderStaff", "");
    	        		tmpData.put("lastOrderChnn", "");
    	        		tmpData.put("lastOrderTime", "");
    	        		tmpData.put("priority", "");
    	        		tmpData.put("isLog", "0");

		        		ids.add(tmpData);

		        	}
        		}
        		//查询优惠
        		if (IDataUtil.isNotEmpty(userDiscntInfos))
        		{
	        		for (int i=0 ; i < userDiscntInfos.size() ; i++)
		        	{
		        		
		        		IData tmpData = new DataMap();
	        			tmpData.put("spCode", "");
	        			tmpData.put("bizCode", "");
	        			tmpData.put("spName", "");
	        			tmpData.put("billType", "");
	        			tmpData.put("servType", "");
		        		tmpData.put("offerId", userDiscntInfos.getData(i).getString("DISCNT_CODE","")+"|D");
		        		tmpData.put("offerName", UDiscntInfoQry.getDiscntNameByDiscntCode(userDiscntInfos.getData(i).getString("DISCNT_CODE","")));
		        		tmpData.put("upOfferId", "");
		        		tmpData.put("upOfferName", "");
		        		tmpData.put("isPackage", "0");
		        		
	        			tmpData.put("offerPrice", "");
		        		if(IDataUtil.isNotEmpty(acctOpenInfos)){
							for(int j=0;j<acctOpenInfos.size();j++){
								if(userDiscntInfos.getData(i).getString("INST_ID","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_INS_ID",""))
										&& "1".equals(acctOpenInfos.getData(j).getString("FEE_FLAG",""))){
									Float price = Float.parseFloat(acctOpenInfos.getData(j).getString("MONTH_RENT","0"))*100;
									if(price > 0)
									{
										tmpData.put("offerPrice",price+"/月");
									}
								}
							}
		        		}
    	        		if("80010485".equals(userDiscntInfos.getData(i).getString("DISCNT_CODE",""))) //流量王卡18元套餐
    	        		{
        	        		tmpData.put("offerPrice","1800/月");
    	        		}
    	        		if("3360".equals(userDiscntInfos.getData(i).getString("DISCNT_CODE",""))) // 神州行流量日租卡套餐
    	        		{
    	        			tmpData.put("offerPrice","0/月");
    	        		}
    	        		if("6633".equals(userDiscntInfos.getData(i).getString("DISCNT_CODE",""))) // 流量安心包（月费0元）
    	        		{
    	        			tmpData.put("offerPrice","0/月");
    	        		}
		        		if("0".equals(queryMode) || "".equals(queryMode))
    		        	{
    	        			tmpData.put("status", "0");
    		        	}
    	        		else
    	        		{
    	        			tmpData.put("status", "");
    		        	}
		        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfos.getData(i).getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
		        		tmpData.put("offerDesc", UDiscntInfoQry.getDiscntExplainByDiscntCode(userDiscntInfos.getData(i).getString("DISCNT_CODE","")));
		        		tmpData.put("offerType", "D");
		        		tmpData.put("offerTypeName", "优惠");
		        		tmpData.put("brandId", "");
		        		tmpData.put("brandName", "");
		        		tmpData.put("isMain", "0");
		        		tmpData.put("chooseTag", "");
		        		if("3410".equals(userDiscntInfos.getData(i).getString("DISCNT_CODE",""))
		        				|| "3411".equals(userDiscntInfos.getData(i).getString("DISCNT_CODE","")))
		        		{
		        			tmpData.put("canUnsub", "0");
		        		}
		        		else
		        		{
		        			tmpData.put("canUnsub", "1");
		        		}
	
		        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfos.getData(i).getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
						tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfos.getData(i).getString("END_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	
		        		IDataset offerAttrList2= new DatasetList();
		        		tmpData.put("offerAttrList", offerAttrList2);
		        		
		        		tmpData.put("orderStaffid", userDiscntInfos.getData(i).getString("TRADE_STAFF_ID",""));
		        		tmpData.put("orderChnn", userDiscntInfos.getData(i).getString("UPDATE_DEPART_ID",""));
		        		tmpData.put("remark", userDiscntInfos.getData(i).getString("REMARK",""));
		        		
		        		tmpData.put("bossId", userDiscntInfos.getData(i).getString("DISCNT_CODE","")+"|D");
    	        		tmpData.put("prodInstId", userDiscntInfos.getData(i).getString("INST_ID",""));
    	        		tmpData.put("isRemoved", "");
    	        		tmpData.put("instructions", "");
    	        		
    	        		tmpData.put("orderDep", userDiscntInfos.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
    	        		tmpData.put("operModuleCode", "");
    	        		tmpData.put("operModuleName", "");
    	        		tmpData.put("operArea", "");
    	        		tmpData.put("shortNum", "");
    	        		tmpData.put("servAreaName", "");
    	        		tmpData.put("offerSts", "");//用户订购实例状态
    	        		tmpData.put("flowLongDistanceAjust", "");
    	        		tmpData.put("voiceLongDistanceAjust", "");
    	        		tmpData.put("flowBusiType", "");
    	        		tmpData.put("bindActId", "");
    	        		tmpData.put("bindActName", "");
    	        		tmpData.put("bindActEndTime", "");
    	        		tmpData.put("offerPriceDesc", "");
    	        		tmpData.put("lastOrderStaff", "");
    	        		tmpData.put("lastOrderChnn", "");
    	        		tmpData.put("lastOrderTime", "");
    	        		tmpData.put("priority", "");
    	        		tmpData.put("isLog", "0");
    	        		//k3
    	        		IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2742",userDiscntInfos.getData(i).getString("DISCNT_CODE","")
    	        								,CSBizBean.getVisit().getStaffEparchyCode());
    	        		String startOrderTime = "";
    	        		String closeOrderTime = "";
    	        		if(IDataUtil.isNotEmpty(commparaSet)){
    	        			String rsrv_date1 = userDiscntInfos.getData(i).getString("RSRV_DATE1");//国漫产品的激活时间
    	        			closeOrderTime =SysDateMgr.decodeTimestamp(userDiscntInfos.getData(i).getString("END_DATE"),SysDateMgr.PATTERN_STAND);//国漫产品的失效时间
    	        			if(StringUtils.isNotBlank(rsrv_date1)){
    	        				
    	        				startOrderTime=SysDateMgr.decodeTimestamp(rsrv_date1, SysDateMgr.PATTERN_STAND);
    	        			}else{
    	        				startOrderTime=SysDateMgr.decodeTimestamp(userDiscntInfos.getData(i).getString("START_DATE",""),SysDateMgr.PATTERN_STAND);
    	        			}

    	        			tmpData.put("startOrderTime", startOrderTime);
    	        			tmpData.put("closeOrderTime", closeOrderTime);
    	        		}
    	        		
		        		ids.add(tmpData);

		        	}
        		}
        		//查询用户关系
        		if (IDataUtil.isNotEmpty(userRelationInfos))
        		{
	        		for (int i=0 ; i < userRelationInfos.size() ; i++)
		        	{
		        		
		        		IData tmpData = new DataMap();
	        			tmpData.put("spCode", "");
	        			tmpData.put("bizCode", "");
	        			tmpData.put("spName", "");
	        			tmpData.put("billType", "");
	        			tmpData.put("servType", "");
		        		tmpData.put("offerId", userRelationInfos.getData(i).getString("RELATION_TYPE_CODE","")+"|R");
		        		tmpData.put("offerName", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_RELATION", "RELATION_TYPE_CODE", "RELATION_TYPE_NAME",userRelationInfos.getData(i).getString("RELATION_TYPE_CODE","")));
		        		tmpData.put("upOfferId", "");
		        		tmpData.put("upOfferName", "");
		        		tmpData.put("isPackage", "0");
		        		
		        		tmpData.put("offerPrice", "");
		        		if("0".equals(queryMode) || "".equals(queryMode))
    		        	{
    	        			tmpData.put("status", "0");
    		        	}
    	        		else
    	        		{
    	        			tmpData.put("status", "");
    		        	}
		        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userRelationInfos.getData(i).getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
		        		tmpData.put("offerDesc", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_RELATION", "RELATION_TYPE_CODE", "RELATION_TYPE_NAME",userRelationInfos.getData(i).getString("RELATION_TYPE_CODE","")));
		        		tmpData.put("offerType", "R");
		        		tmpData.put("offerTypeName", "集团关系");
		        		tmpData.put("brandId", "");
		        		tmpData.put("brandName", "");
		        		tmpData.put("isMain", "0");
		        		tmpData.put("chooseTag", "");
		        		tmpData.put("canUnsub", "0");
	
		        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userRelationInfos.getData(i).getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
						tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userRelationInfos.getData(i).getString("END_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	
		        		IDataset offerAttrList2= new DatasetList();
		        		tmpData.put("offerAttrList", offerAttrList2);
		        		
		        		tmpData.put("orderStaffid", userRelationInfos.getData(i).getString("TRADE_STAFF_ID",""));
		        		tmpData.put("orderChnn", userRelationInfos.getData(i).getString("UPDATE_DEPART_ID",""));
		        		tmpData.put("remark", userRelationInfos.getData(i).getString("REMARK",""));
		        		
		        		tmpData.put("bossId", userRelationInfos.getData(i).getString("RELATION_TYPE_CODE","")+"|R");
    	        		tmpData.put("prodInstId", userRelationInfos.getData(i).getString("INST_ID",""));
    	        		tmpData.put("isRemoved", "");
    	        		tmpData.put("instructions", "");
    	        		
    	        		tmpData.put("orderDep", userRelationInfos.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
    	        		tmpData.put("operModuleCode", "");
    	        		tmpData.put("operModuleName", "");
    	        		tmpData.put("operArea", "");
    	        		tmpData.put("shortNum", userRelationInfos.getData(i).getString("SHORT_CODE", ""));
    	        		tmpData.put("servAreaName", "");
    	        		tmpData.put("offerSts", "");//用户订购实例状态
    	        		tmpData.put("flowLongDistanceAjust", "");
    	        		tmpData.put("voiceLongDistanceAjust", "");
    	        		tmpData.put("flowBusiType", "");
    	        		tmpData.put("bindActId", "");
    	        		tmpData.put("bindActName", "");
    	        		tmpData.put("bindActEndTime", "");
    	        		tmpData.put("offerPriceDesc", "");
    	        		tmpData.put("lastOrderStaff", "");
    	        		tmpData.put("lastOrderChnn", "");
    	        		tmpData.put("lastOrderTime", "");
    	        		tmpData.put("priority", "");
    	        		tmpData.put("isLog", "0");
		        		ids.add(tmpData);

		        	}
        		}

        		outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("logUrl", "");
		        result = prepareOutResult(0,"",outData);	

	        }
	        else if ("01".equals(queryType))//01-个人-主资费
	        {
	        	IDataset offerAttrList= new DatasetList();
        		UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
        		IDataset userProductInfos = new DatasetList();
        		IDataset acctOpenInfos = new DatasetList();
        		if("0".equals(queryMode) || "".equals(queryMode))
	        	{
        			userProductInfos = UserProductInfoQry.queryUserMainProduct(ucaData.getUserId());
        			acctOpenInfos = queryOpenInfo(busiCode);
	        	}
        		else if ("1".equals(queryMode))
	        	{
        			userProductInfos = queryUserProductHisInfos(ucaData.getUserId());
	        	}
	        	else
	        	{
	        		result = prepareOutResult(1,"不支持的queryMode值："+queryMode,outData);
		            return result;
	        	}
        		if (IDataUtil.isNotEmpty(userProductInfos)){
        		for (int i=0 ; i < userProductInfos.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();
	        		String productId = userProductInfos.getData(i).getString("PRODUCT_ID", "");
	        		IDataset catalogInfos = UpcCall.qryCatalogByOfferId(productId,"P");
	        		String catalogId = "";
	        		String catalogName = "";
	        		if(IDataUtil.isNotEmpty(catalogInfos)){
	        			catalogId = catalogInfos.getData(0).getString("CATALOG_ID", "");
	        			catalogName = catalogInfos.getData(0).getString("CATALOG_NAME", "");
	        		}
	        		tmpData.put("offerId", productId+"|P");
	        		tmpData.put("offerName", UProductInfoQry.getProductNameByProductId(productId));
	        		tmpData.put("upOfferId", catalogId);
	        		tmpData.put("upOfferName", catalogName);
	        		tmpData.put("isPackage", "1");
	        		
	        		tmpData.put("spCode", "");
	        		tmpData.put("bizCode", "");
	        		tmpData.put("spName", "");
	        		tmpData.put("billType", "");
	        		tmpData.put("servType", "");
	        		

	        		
	        		//查询用户产品下的必选优惠
	        		//IDataset userMainDiscnts = queryUserMainElements(ucaData.getUserId(),productId);
	        		IDataset userMainDiscnts = qryComRelOffersByOfferId(productId);

	        		float price = 0 ;
	        		if(IDataUtil.isNotEmpty(userMainDiscnts)){
	        			for (int m=0 ; m < userMainDiscnts.size() ; m++)
	    	        	{
	    	        		if(IDataUtil.isNotEmpty(acctOpenInfos)){
	    						for(int j=0;j<acctOpenInfos.size();j++){
	    							if(userMainDiscnts.getData(m).getString("ELEMENT_ID","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_ID",""))
	    									&& "1".equals(acctOpenInfos.getData(j).getString("FEE_FLAG",""))){
	    								Float monthRent = Float.parseFloat(acctOpenInfos.getData(j).getString("MONTH_RENT","0"));
	    								if(monthRent > 0)
										{
	    									price += monthRent;
										}    	    								
	    							}
	    						}
	    	        		}
	    	        	}
	        		}
	        		tmpData.put("offerPrice", Float.toString(price*100)+"/月");
	        		
	        		if("80010484".equals(productId)) //流量王卡18元套餐
	        		{
    	        		tmpData.put("offerPrice","1800/月");
	        		}
	        		if("10003377".equals(productId)) // 神州行流量日租卡套餐
	        		{
	        			tmpData.put("offerPrice","0/月");
	        		}
	        		
	        		if("0".equals(queryMode) || "".equals(queryMode))
		        	{
	        			tmpData.put("status", "0");
		        	}
	        		else
	        		{
	        			tmpData.put("status", "");
		        	}	
	        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userProductInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		tmpData.put("offerDesc", UProductInfoQry.getProductExplainByProductId(productId));
	        		tmpData.put("offerType", "P");
	        		tmpData.put("offerTypeName", "产品");
	        		tmpData.put("brandId", userProductInfos.getData(i).getString("BRAND_CODE", ""));
	        		tmpData.put("brandName", UpcCall.queryBrandNameByChaVal( userProductInfos.getData(i).getString("BRAND_CODE", "")));
	        		tmpData.put("isMain", userProductInfos.getData(i).getString("MAIN_TAG", ""));
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userProductInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userProductInfos.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		List<AttrTradeData> userAttrs = ucaData.getUserAttrsByRelaInstId(userProductInfos.getData(i).getString("INST_ID", ""));
	        		for (int j=0 ; j < userAttrs.size() ; j++)
		        	{
			        	IData tmpData1 = new DataMap();
		        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
		        		tmpData1.put("attrName", "");
		        		tmpData1.put("attrDesc", "");
		        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
		        		tmpData1.put("attrValueDesc", "");
		        		offerAttrList.add(j, tmpData);
		        	}
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", userProductInfos.getData(i).getString("UPDATE_STAFF_ID", ""));
	        		tmpData.put("orderChnn", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",
	        				userProductInfos.getData(i).getString("UPDATE_DEPART_ID", "")));
	        		tmpData.put("remark", userProductInfos.getData(i).getString("REMARK", ""));
	        		
	        		tmpData.put("bossId", productId+"|P");
	        		tmpData.put("prodInstId", userProductInfos.getData(i).getString("INST_ID", ""));
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		
	        		tmpData.put("orderDep", userProductInfos.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
	        		tmpData.put("operModuleCode", "");
	        		tmpData.put("operModuleName", "");
	        		tmpData.put("operArea", "");
	        		tmpData.put("shortNum", "");
	        		tmpData.put("servAreaName", "");
	        		tmpData.put("offerSts", "");//用户订购实例状态
	        		tmpData.put("flowLongDistanceAjust", "");
	        		tmpData.put("voiceLongDistanceAjust", "");
	        		tmpData.put("flowBusiType", "");
	        		tmpData.put("bindActId", "");
	        		tmpData.put("bindActName", "");
	        		tmpData.put("bindActEndTime", "");
	        		tmpData.put("offerPriceDesc", "");
	        		tmpData.put("lastOrderStaff", "");
	        		tmpData.put("lastOrderChnn", "");
	        		tmpData.put("lastOrderTime", "");
	        		tmpData.put("priority", "");
	        		tmpData.put("isLog", "0");

	        		ids.add(i, tmpData);
	        	}
        		}

	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("logUrl", "");

		        result = prepareOutResult(0,"",outData);
	        	
	        	
	        }
	        else if ("02".equals(queryType) || "81".equals(queryType))//02-个人-基础功能
	        {
	        	IDataset offerAttrList= new DatasetList();
        		UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
        		IDataset userSvcInfos = new DatasetList();
        		IDataset acctOpenInfos = new DatasetList();
        		if("0".equals(queryMode) || "".equals(queryMode))
	        	{
        			//userSvcInfos = UserSvcInfoQry.qryUserSvcByUserId(ucaData.getUserId());
        			userSvcInfos = UserSvcInfoQry.queryUserAllSvc(ucaData.getUserId());
        			acctOpenInfos = queryOpenInfo(busiCode);

	        	}
        		else if ("1".equals(queryMode))
	        	{
        			userSvcInfos = UserSvcInfoQry.queryUserSvcByUserId(ucaData.getUserId(),"2");
	        	}
	        	else
	        	{
	        		result = prepareOutResult(1,"不支持的queryMode值："+queryMode,outData);
		            return result;
	        	}
        		if (IDataUtil.isNotEmpty(userSvcInfos)){
        		for (int i=0 ; i < userSvcInfos.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();
	        		String serviceId = userSvcInfos.getData(i).getString("SERVICE_ID", "");
	        		IDataset catalogInfos = UpcCall.qryCatalogByOfferId(serviceId,"S");
	        		String catalogId = "";
	        		String catalogName = "";
	        		if(IDataUtil.isNotEmpty(catalogInfos)){
	        			catalogId = catalogInfos.getData(0).getString("CATALOG_ID", "");
	        			catalogName = catalogInfos.getData(0).getString("CATALOG_NAME", "");
	        		}
	        		tmpData.put("offerId", serviceId+"|S");
	        		tmpData.put("offerName", USvcInfoQry.getSvcNameBySvcId(serviceId));
	        		tmpData.put("upOfferId", catalogId);
	        		tmpData.put("upOfferName", catalogName);
	        		tmpData.put("isPackage", "1");
	        		
	        		tmpData.put("spCode", "");
	        		tmpData.put("bizCode", "");
	        		tmpData.put("spName", "");
	        		tmpData.put("billType", "");
	        		tmpData.put("servType", "");
	        		
	        		tmpData.put("offerPrice", "");
	        		if(IDataUtil.isNotEmpty(acctOpenInfos)){
						for(int j=0;j<acctOpenInfos.size();j++){
							if(userSvcInfos.getData(i).getString("INST_ID","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_INS_ID",""))){
								Float price = Float.parseFloat(acctOpenInfos.getData(j).getString("MONTH_RENT","0"))*100;
								if(price > 0)
								{
									tmpData.put("offerPrice",price+"/月");
								}
							}
						}
	        		}
	        		if("0".equals(queryMode) || "".equals(queryMode))
		        	{
	        			tmpData.put("status", "0");
		        	}
	        		else
	        		{
	        			tmpData.put("status", "");
		        	}	
	        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		tmpData.put("offerDesc", USvcInfoQry.getSvcExplainBySvcId(serviceId));
	        		tmpData.put("offerType", "S");
	        		tmpData.put("offerTypeName", "服务");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", userSvcInfos.getData(i).getString("MAIN_TAG", ""));
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");
	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfos.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		List<AttrTradeData> userAttrs = ucaData.getUserAttrsByRelaInstId(userSvcInfos.getData(i).getString("INST_ID", ""));
	        		/*for (int j=0 ; j < userAttrs.size() ; j++)
		        	{
			        	IData tmpData1 = new DataMap();
		        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
		        		tmpData1.put("attrName", "");
		        		tmpData1.put("attrDesc", "");
		        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
		        		tmpData1.put("attrValueDesc", "");
		        		offerAttrList.add(j, tmpData);
		        	}*/
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", userSvcInfos.getData(i).getString("UPDATE_STAFF_ID", ""));
	        		tmpData.put("orderChnn", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",
	        				userSvcInfos.getData(i).getString("UPDATE_DEPART_ID", "")));
	        		tmpData.put("remark", userSvcInfos.getData(i).getString("REMARK", ""));
	        		
	        		tmpData.put("bossId", serviceId+"|S");
	        		tmpData.put("prodInstId", userSvcInfos.getData(i).getString("INST_ID", ""));
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		
	        		tmpData.put("orderDep", userSvcInfos.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
	        		tmpData.put("operModuleCode", "");
	        		tmpData.put("operModuleName", "");
	        		tmpData.put("operArea", "");
	        		tmpData.put("shortNum", "");
	        		tmpData.put("servAreaName", "");
	        		tmpData.put("offerSts", "");//用户订购实例状态
	        		tmpData.put("flowLongDistanceAjust", "");
	        		tmpData.put("voiceLongDistanceAjust", "");
	        		tmpData.put("flowBusiType", "");
	        		tmpData.put("bindActId", "");
	        		tmpData.put("bindActName", "");
	        		tmpData.put("bindActEndTime", "");
	        		tmpData.put("offerPriceDesc", "");
	        		tmpData.put("lastOrderStaff", "");
	        		tmpData.put("lastOrderChnn", "");
	        		tmpData.put("lastOrderTime", "");
	        		tmpData.put("priority", "");
	        		tmpData.put("isLog", "0");

	        		ids.add(i, tmpData);
	        	}
        		}

	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("logUrl", "");

		        result = prepareOutResult(0,"",outData);
	        }
	        else if ("03".equals(queryType))//03-个人-自有业务
	        {
	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        	IDataset userPlatsvcs = new DatasetList();
	        	IDataset acctOpenInfos = new DatasetList();
	        	if("0".equals(queryMode) || "".equals(queryMode))
	        	{
        			userPlatsvcs = queryPlatorderAllinfoByUserId(ucaData.getUserId());
        			acctOpenInfos = queryOpenInfo(busiCode);

	        	}
        		else if ("1".equals(queryMode))
	        	{
        			userPlatsvcs = queryPlatorderHisinfoByUserId(ucaData.getUserId());

	        	}
        		else
	        	{
	        		result = prepareOutResult(1,"不支持的queryMode值："+queryMode,outData);
		            return result;
	        	}
	        	//List<PlatSvcTradeData> userPlatsvc = ucaData.getUserPlatSvcs();
	        	int k=0;
	        	for (int i=0 ; i < userPlatsvcs.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();
	        		
	        		IDataset spServiceInfo = UpcCall.qrySpServiceSpInfo(userPlatsvcs.getData(i).getString("SERVICE_ID", ""),"Z");
	        		if (IDataUtil.isNotEmpty(spServiceInfo)){
	        			//2018-5-11与符龙斌确认：自有业务就展示自有业务；梦网业务展示除自有业务外所有的平台业务；
	        			if ("2".equals(spServiceInfo.getData(0).getString("RSRV_STR9", "")) ||"".equals(spServiceInfo.getData(0).getString("RSRV_STR9", ""))){
	        				continue;
	        			}
	        			tmpData.put("spCode", spServiceInfo.getData(0).getString("SP_CODE", ""));
	        			tmpData.put("bizCode", spServiceInfo.getData(0).getString("BIZ_CODE", ""));
	        			tmpData.put("spName", spServiceInfo.getData(0).getString("SP_NAME", ""));
	        			tmpData.put("servType", spServiceInfo.getData(0).getString("BIZ_TYPE_CODE", ""));
	        			
	        			String billFlag = spServiceInfo.getData(0).getString("BILL_TYPE", "");
		        		if ("0".equals(billFlag))
		        		{
		        			tmpData.put("billType", "免费");	
		        		}
		        		else if ("1".equals(billFlag))
		        		{
		        			tmpData.put("billType", "按条计费");	
		        		}
		        		else if ("2".equals(billFlag))
		        		{
		        			tmpData.put("billType", "包月计费");	
		        		}
		        		else if ("3".equals(billFlag))
		        		{
		        			tmpData.put("billType", "包时计费");	
		        		}
		        		else if ("4".equals(billFlag))
		        		{
		        			tmpData.put("billType", "包次计费");	
		        		}
		        		else {
		        			tmpData.put("billType", "");	
		        		}
		        		int price = Integer.parseInt(spServiceInfo.getData(0).getString("PRICE", "0"));
		        		if( price >= 0 )
		        		{
		        			price = price/10;
		        		}
		        		tmpData.put("offerPrice", String.valueOf(price)+"/月");
	        		}
	        		else
	        		{
	        			tmpData.put("spCode", "");
	        			tmpData.put("bizCode", "");
	        			tmpData.put("spName", "");
	        			tmpData.put("servType", "");
	        			tmpData.put("billType", "");
	        		}
	        		tmpData.put("offerId", userPlatsvcs.getData(i).getString("SERVICE_ID", "")+"|Z");
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(userPlatsvcs.getData(i).getString("SERVICE_ID", ""),"Z"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
	        		
/*        			tmpData.put("offerPrice", "");
	        		if(IDataUtil.isNotEmpty(acctOpenInfos)){
						for(int j=0;j<acctOpenInfos.size();j++){
							if(tmpData.getString("bizCode","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_INS_ID",""))
									&& tmpData.getString("spCode","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_ID",""))){
								Float price = Float.parseFloat(acctOpenInfos.getData(j).getString("MONTH_RENT","0"))*100;
								if(price > 0)
								{
									tmpData.put("offerPrice",price+"/月");
								}
							}
						}
	        		}*/
	        		
	        		if("0".equals(queryMode) || "".equals(queryMode))
		        	{
	        			tmpData.put("status", "0");
		        	}
	        		else
	        		{
	        			tmpData.put("status", "");
		        	}
	        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvcs.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		tmpData.put("offerDesc", UPlatSvcInfoQry.getSvcExplainBySvcId(userPlatsvcs.getData(i).getString("SERVICE_ID", "")));
	        		tmpData.put("offerType", "Z");
	        		tmpData.put("offerTypeName", "平台服务");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvcs.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvcs.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		
	        		IDataset offerAttrList= new DatasetList();
	        		List<AttrTradeData> userAttrs = ucaData.getUserAttrsByRelaInstId(userPlatsvcs.getData(i).getString("INST_ID", ""));
	        		for (int j=0 ; j < userAttrs.size() ; j++)
		        	{
			        	IData tmpData1 = new DataMap();
		        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
		        		tmpData1.put("attrName", "");
		        		tmpData1.put("attrDesc", "");
		        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
		        		tmpData1.put("attrValueDesc", "");
		        		offerAttrList.add(j, tmpData1);
		        	}
	        		tmpData.put("offerAttrList", offerAttrList);
	        		tmpData.put("orderStaffid", userPlatsvcs.getData(i).getString("UPDATE_STAFF_ID", ""));
	        		tmpData.put("orderChnn", userPlatsvcs.getData(i).getString("UPDATE_DEPART_ID", ""));
	        		tmpData.put("remark", userPlatsvcs.getData(i).getString("REMARK", ""));
	        		
	        		tmpData.put("bossId", userPlatsvcs.getData(i).getString("SERVICE_ID", "")+"|Z");
	        		tmpData.put("prodInstId", userPlatsvcs.getData(i).getString("INST_ID", ""));
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		
	        		tmpData.put("orderDep", userPlatsvcs.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
	        		tmpData.put("operModuleCode", "");
	        		tmpData.put("operModuleName", "");
	        		tmpData.put("operArea", "");
	        		tmpData.put("shortNum", "");
	        		tmpData.put("servAreaName", "");
	        		tmpData.put("offerSts", "");//用户订购实例状态
	        		tmpData.put("flowLongDistanceAjust", "");
	        		tmpData.put("voiceLongDistanceAjust", "");
	        		tmpData.put("flowBusiType", "");
	        		tmpData.put("bindActId", "");
	        		tmpData.put("bindActName", "");
	        		tmpData.put("bindActEndTime", "");
	        		tmpData.put("offerPriceDesc", "");
	        		tmpData.put("lastOrderStaff", "");
	        		tmpData.put("lastOrderChnn", "");
	        		tmpData.put("lastOrderTime", "");
	        		tmpData.put("priority", "");
	        		tmpData.put("isLog", "0");

	        		ids.add(k, tmpData);
	        		k++;
	        	}
	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("logUrl", "");

		        result = prepareOutResult(0,"",outData);
	        	
	        }
	        else if ("04".equals(queryType))//04-个人-营销活动
	        {
	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        	IDataset userSaleActives = new DatasetList();
	        	
        		if("0".equals(queryMode) || "".equals(queryMode))
	        	{
        			userSaleActives =  UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(ucaData.getUserId());
	        	}
        		else if ("1".equals(queryMode))
	        	{
        			userSaleActives = getExpirSaleActiveByUserId(ucaData.getUserId());

	        	}
        		else
	        	{
	        		result = prepareOutResult(1,"不支持的queryMode值："+queryMode,outData);
		            return result;
	        	}
	        	
	        	if( (userSaleActives == null) )
	        	{
	        		result = prepareOutResult(1,"查询个人营销活动为空！",outData);
	        	}
	        	for (int i=0 ; i < userSaleActives.size() ; i++)
	        	{
	        		
	        		IData tmpData = new DataMap();
        			tmpData.put("spCode", "");
        			tmpData.put("bizCode", "");
        			tmpData.put("spName", "");
        			tmpData.put("billType", "");
        			tmpData.put("servType", "");
	        		tmpData.put("offerId", userSaleActives.getData(i).getString("PACKAGE_ID", "")+"|K");
	        		tmpData.put("offerName",userSaleActives.getData(i).getString("PACKAGE_NAME", ""));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "1");
	        		
	        		tmpData.put("offerPrice", "");
	        		
	        		if("0".equals(queryMode) || "".equals(queryMode))
		        	{
	        			tmpData.put("status", "0");
		        	}
	        		else
	        		{
	        			tmpData.put("status", "");
		        	}
	        		if (!"".equals(userSaleActives.getData(i).getString("ACCEPT_TIME", "")))
	        		{
	        			tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSaleActives.getData(i).getString("ACCEPT_TIME", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		}
	        		else
	        		{
	        			tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSaleActives.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		}
	        		tmpData.put("offerDesc", UPackageInfoQry.getPackageExplainByPackageId(userSaleActives.getData(i).getString("PACKAGE_ID", "")));
	        		tmpData.put("offerType", "K");
	        		tmpData.put("offerTypeName", "营销活动");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSaleActives.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSaleActives.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		IDataset offerAttrList= new DatasetList();
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", userSaleActives.getData(i).getString("UPDATE_STAFF_ID", ""));
	        		tmpData.put("orderChnn", userSaleActives.getData(i).getString("UPDATE_DEPART_ID", ""));
	        		tmpData.put("remark", userSaleActives.getData(i).getString("REMARK", ""));
	        		
	        		tmpData.put("bossId", userSaleActives.getData(i).getString("PACKAGE_ID", "")+"|K");
	        		tmpData.put("prodInstId", userSaleActives.getData(i).getString("INST_ID", ""));
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		
	        		tmpData.put("orderDep", userSaleActives.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
	        		tmpData.put("operModuleCode", "");
	        		tmpData.put("operModuleName", "");
	        		tmpData.put("operArea", "");
	        		tmpData.put("shortNum", "");
	        		tmpData.put("servAreaName", "");
	        		tmpData.put("offerSts", "");//用户订购实例状态
	        		tmpData.put("flowLongDistanceAjust", "");
	        		tmpData.put("voiceLongDistanceAjust", "");
	        		tmpData.put("flowBusiType", "");
	        		tmpData.put("bindActId", "");
	        		tmpData.put("bindActName", "");
	        		tmpData.put("bindActEndTime", "");
	        		tmpData.put("offerPriceDesc", "");
	        		tmpData.put("lastOrderStaff", "");
	        		tmpData.put("lastOrderChnn", "");
	        		tmpData.put("lastOrderTime", "");
	        		tmpData.put("priority", "");
	        		tmpData.put("isLog", "0");

	        		ids.add(i, tmpData);
	        	}
	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("logUrl", "");

		        result = prepareOutResult(0,"",outData);
	        }
	        else if ("05".equals(queryType))//05-个人-梦网业务
	        {	        	
	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        	IDataset userPlatsvcs = new DatasetList();
	        	IDataset acctOpenInfos = new DatasetList();

	        	if("0".equals(queryMode) || "".equals(queryMode))
	        	{
        			userPlatsvcs = queryPlatorderAllinfoByUserId(ucaData.getUserId());
        			acctOpenInfos = queryOpenInfo(busiCode);

	        	}
        		else if ("1".equals(queryMode))
	        	{
        			userPlatsvcs = queryPlatorderHisinfoByUserId(ucaData.getUserId());

	        	}
        		else
	        	{
	        		result = prepareOutResult(1,"不支持的queryMode值："+queryMode,outData);
		            return result;
	        	}
	        	//List<PlatSvcTradeData> userPlatsvc = ucaData.getUserPlatSvcs();
	        	int k=0;
	        	for (int i=0 ; i < userPlatsvcs.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();
	        		
	        		IDataset spServiceInfo = UpcCall.qrySpServiceSpInfo(userPlatsvcs.getData(i).getString("SERVICE_ID", ""),"Z");
	        		if (IDataUtil.isNotEmpty(spServiceInfo)){
	        			//2018-5-11与符龙斌确认：自有业务就展示自有业务；梦网业务展示除自有业务外所有的平台业务；
	        			//RSRV_STR9三种取值：为空；1-自有业务；2-梦外业务；
	        			if ("1".equals(spServiceInfo.getData(0).getString("RSRV_STR9", ""))){
	        				continue;
	        			}
	        			tmpData.put("spCode", spServiceInfo.getData(0).getString("SP_CODE", ""));
	        			tmpData.put("bizCode", spServiceInfo.getData(0).getString("BIZ_CODE", ""));
	        			tmpData.put("spName", spServiceInfo.getData(0).getString("SP_NAME", ""));
	        			tmpData.put("servType", spServiceInfo.getData(0).getString("BIZ_TYPE_CODE", ""));
	        			
	        			String billFlag = spServiceInfo.getData(0).getString("BILL_TYPE", "");
		        		if ("0".equals(billFlag))
		        		{
		        			tmpData.put("billType", "免费");	
		        		}
		        		else if ("1".equals(billFlag))
		        		{
		        			tmpData.put("billType", "按条计费");	
		        		}
		        		else if ("2".equals(billFlag))
		        		{
		        			tmpData.put("billType", "包月计费");	
		        		}
		        		else if ("3".equals(billFlag))
		        		{
		        			tmpData.put("billType", "包时计费");	
		        		}
		        		else if ("4".equals(billFlag))
		        		{
		        			tmpData.put("billType", "包次计费");	
		        		}
		        		else {
		        			tmpData.put("billType", "");	
		        		}
		        		int price = Integer.parseInt(spServiceInfo.getData(0).getString("PRICE", "0"));
		        		if( price >= 0 )
		        		{
		        			price = price/10;
		        		}
		        		tmpData.put("offerPrice", String.valueOf(price)+"/月");
		        		
		        		
		        		String serv_mode = spServiceInfo.getData(0).getString("SERV_MODE", "");//0梦网业务，1自有业务
		        		if(StringUtils.isNotBlank(serv_mode)&&"0".equals(serv_mode)){
		        			String spCode = spServiceInfo.getData(0).getString("SP_CODE", "");
		        			String spId = spServiceInfo.getData(0).getString("SP_ID", "");
		        			String spName = spServiceInfo.getData(0).getString("SP_NAME", "");
		        			String spType = spServiceInfo.getData(0).getString("SP_TYPE", "");
		        			IDataset spInfo = UpcCallIntf.querySpInfoByCond(spId,spCode,spName,spType);
		        			tmpData.put("bizName", spServiceInfo.getData(0).getString("BIZ_NAME", ""));
		        			tmpData.put("spType", spServiceInfo.getData(0).getString("BIZ_TYPE_CODE", ""));
		        			tmpData.put("isRelevanceOffer", "1");
		        			tmpData.put("isSonOffer", "1");
		        			if(IDataUtil.isNotEmpty(spInfo)){
		        				tmpData.put("messagePort",spInfo.getData(0).getString("SERV_CODE",""));
		        			}else{
		        				tmpData.put("messagePort","");
		        			}
		        		}
	        		}
	        		else
	        		{
	        			tmpData.put("spCode", "");
	        			tmpData.put("bizCode", "");
	        			tmpData.put("spName", "");
	        			tmpData.put("servType", "");
	        			tmpData.put("billType", "");
	        			tmpData.put("bizName","");
        				tmpData.put("messagePort","");
        				tmpData.put("spType", "");
        				tmpData.put("isRelevanceOffer", "1");
        				tmpData.put("isSonOffer", "1");
	        		}
	        		tmpData.put("offerId", userPlatsvcs.getData(i).getString("SERVICE_ID", "")+"|Z");
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(userPlatsvcs.getData(i).getString("SERVICE_ID", ""),"Z"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
        			/*tmpData.put("offerPrice", "");
	        		if(IDataUtil.isNotEmpty(acctOpenInfos)){
						for(int j=0;j<acctOpenInfos.size();j++){
							if(tmpData.getString("bizCode","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_INS_ID",""))
									&& tmpData.getString("spCode","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_ID",""))){
								Float price = Float.parseFloat(acctOpenInfos.getData(j).getString("MONTH_RENT","0"))*100;
								if(price > 0)
								{
									tmpData.put("offerPrice",price+"/月");
								}
							}
						}
	        		}*/
	        		if("0".equals(queryMode) || "".equals(queryMode))
		        	{
	        			tmpData.put("status", "0");
		        	}
	        		else
	        		{
	        			tmpData.put("status", "");
		        	}
	        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvcs.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		tmpData.put("offerDesc", UPlatSvcInfoQry.getSvcExplainBySvcId(userPlatsvcs.getData(i).getString("SERVICE_ID", "")));
	        		tmpData.put("offerType", "Z");
	        		tmpData.put("offerTypeName", "平台服务");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvcs.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvcs.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		
	        		IDataset offerAttrList= new DatasetList();
	        		List<AttrTradeData> userAttrs = ucaData.getUserAttrsByRelaInstId(userPlatsvcs.getData(i).getString("INST_ID", ""));
	        		for (int j=0 ; j < userAttrs.size() ; j++)
		        	{
			        	IData tmpData1 = new DataMap();
		        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
		        		tmpData1.put("attrName", "");
		        		tmpData1.put("attrDesc", "");
		        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
		        		tmpData1.put("attrValueDesc", "");
		        		offerAttrList.add(j, tmpData1);
		        	}
	        		tmpData.put("offerAttrList", offerAttrList);
	        		tmpData.put("orderStaffid", userPlatsvcs.getData(i).getString("UPDATE_STAFF_ID", ""));
	        		tmpData.put("orderChnn", userPlatsvcs.getData(i).getString("UPDATE_DEPART_ID", ""));
	        		tmpData.put("remark", userPlatsvcs.getData(i).getString("REMARK", ""));
	        		
	        		tmpData.put("bossId", userPlatsvcs.getData(i).getString("SERVICE_ID", "")+"|Z");
	        		tmpData.put("prodInstId", userPlatsvcs.getData(i).getString("INST_ID", ""));
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		
	        		tmpData.put("orderDep", userPlatsvcs.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
	        		tmpData.put("operModuleCode", "");
	        		tmpData.put("operModuleName", "");
	        		tmpData.put("operArea", "");
	        		tmpData.put("shortNum", "");
	        		tmpData.put("servAreaName", "");
	        		tmpData.put("offerSts", "");//用户订购实例状态
	        		tmpData.put("flowLongDistanceAjust", "");
	        		tmpData.put("voiceLongDistanceAjust", "");
	        		tmpData.put("flowBusiType", "");
	        		tmpData.put("bindActId", "");
	        		tmpData.put("bindActName", "");
	        		tmpData.put("bindActEndTime", "");
	        		tmpData.put("offerPriceDesc", "");
	        		tmpData.put("lastOrderStaff", "");
	        		tmpData.put("lastOrderChnn", "");
	        		tmpData.put("lastOrderTime", "");
	        		tmpData.put("priority", "");
	        		tmpData.put("isLog", "0");

	        		ids.add(k, tmpData);
	        		k++;
	        	}
	        	qryMyselfBusi(queryMode,busiCode,outData2,ids2);
	        	ids.addAll(ids2);
	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("logUrl", "");

		        result = prepareOutResult(0,"",outData);
	        }
	        else if ("06".equals(queryType))
	        {
	        	IDataset offerAttrList= new DatasetList();
        		UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
        		IDataset userSvcInfos = new DatasetList();
        		if("0".equals(queryMode) || "".equals(queryMode))
	        	{
        			userSvcInfos = UserSvcInfoQry.qryUserSvcByUserId(ucaData.getUserId());
	        	}
        		else if ("1".equals(queryMode))
	        	{
        			userSvcInfos = UserSvcInfoQry.queryUserSvcByUserId(ucaData.getUserId(),"2");
	        	}
	        	else
	        	{
	        		result = prepareOutResult(1,"不支持的queryMode值："+queryMode,outData);
		            return result;
	        	}
        		if (IDataUtil.isNotEmpty(userSvcInfos)){
        		for (int i=0 ; i < userSvcInfos.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();
	        		String serviceId = userSvcInfos.getData(i).getString("SERVICE_ID", "");
	        		IDataset catalogInfos = UpcCall.qryCatalogByOfferId(serviceId,"S");
	        		String catalogId = "";
	        		String catalogName = "";
	        		if(IDataUtil.isNotEmpty(catalogInfos)){
	        			catalogId = catalogInfos.getData(0).getString("CATALOG_ID", "");
	        			catalogName = catalogInfos.getData(0).getString("CATALOG_NAME", "");
	        		}
	        		tmpData.put("offerId", serviceId);
	        		tmpData.put("offerName", USvcInfoQry.getSvcNameBySvcId(serviceId));
	        		tmpData.put("upOfferId", catalogId);
	        		tmpData.put("upOfferName", catalogName);
	        		tmpData.put("isPackage", "1");
	        		
	        		tmpData.put("spCode", "");
	        		tmpData.put("bizCode", "");
	        		tmpData.put("spName", "");
	        		tmpData.put("billType", "");
	        		tmpData.put("servType", "");
	        		
	        		tmpData.put("offerPrice", "");
	        		if("0".equals(queryMode))
		        	{
	        			tmpData.put("status", "0");
		        	}
	        		else
	        		{
	        			tmpData.put("status", "");
		        	}	

	        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfos.getData(i).getString("UPDATE_TIME", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		tmpData.put("offerDesc", USvcInfoQry.getSvcExplainBySvcId(serviceId));
	        		tmpData.put("offerType", "S");
	        		tmpData.put("offerTypeName", "服务");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", userSvcInfos.getData(i).getString("MAIN_TAG", ""));
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfos.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		List<AttrTradeData> userAttrs = ucaData.getUserAttrsByRelaInstId(userSvcInfos.getData(i).getString("INST_ID", ""));
	        		for (int j=0 ; j < userAttrs.size() ; j++)
		        	{
			        	IData tmpData1 = new DataMap();
		        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
		        		tmpData1.put("attrName", "");
		        		tmpData1.put("attrDesc", "");
		        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
		        		tmpData1.put("attrValueDesc", "");
		        		offerAttrList.add(j, tmpData);
		        	}
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", userSvcInfos.getData(i).getString("UPDATE_STAFF_ID", ""));
	        		tmpData.put("orderChnn", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",
	        				userSvcInfos.getData(i).getString("UPDATE_DEPART_ID", "")));
	        		tmpData.put("remark", userSvcInfos.getData(i).getString("REMARK", ""));
	        		
	        		tmpData.put("bossId", "");
	        		tmpData.put("prodInstId", "");
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		tmpData.put("isLog", "0");

	        		ids.add(tmpData);
	        	}
        		}

	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("logUrl", "");

		        result = prepareOutResult(0,"",outData);
	        }
	        else if ("07".equals(queryType))
	        {
	        	IDataset offerAttrList= new DatasetList();
        		UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
        		IDataset userSvcInfos = new DatasetList();
        		IDataset userDiscntInfos = new DatasetList();
        		if(!"0".equals(queryMode) && "1".equals(queryMode) && "".equals(queryMode))
	        	{
	        		result = prepareOutResult(1,"不支持的queryMode值："+queryMode,outData);
		            return result;
	        	}
        		IData inparam = new DataMap();
    			inparam.put("USER_ID", ucaData.getUserId());
        		inparam.put("QUERY_MODE", queryMode);
    			inparam.put("SERVICEIDS", "15,19");
    			
    			userSvcInfos = getUserSvcs(inparam);
    			
        		
        		if (IDataUtil.isNotEmpty(userSvcInfos)){
		    		for (int i=0 ; i < userSvcInfos.size() ; i++)
		        	{
		        		IData tmpData = new DataMap();
		        		String serviceId = userSvcInfos.getData(i).getString("SERVICE_ID", "");
		        		IDataset catalogInfos = UpcCall.qryCatalogByOfferId(serviceId,"S");
		        		String catalogId = "";
		        		String catalogName = "";
		        		if(IDataUtil.isNotEmpty(catalogInfos)){
		        			catalogId = catalogInfos.getData(0).getString("CATALOG_ID", "");
		        			catalogName = catalogInfos.getData(0).getString("CATALOG_NAME", "");
		        		}
		        		tmpData.put("offerId", serviceId);
		        		tmpData.put("offerName", USvcInfoQry.getSvcNameBySvcId(serviceId));
		        		tmpData.put("upOfferId", catalogId);
		        		tmpData.put("upOfferName", catalogName);
		        		tmpData.put("isPackage", "1");
		        		
		        		tmpData.put("spCode", "");
		        		tmpData.put("bizCode", "");
		        		tmpData.put("spName", "");
		        		tmpData.put("billType", "");
		        		tmpData.put("servType", "");
		        		
		        		tmpData.put("offerPrice", "");
		        		if("0".equals(queryMode))
			        	{
		        			tmpData.put("status", "0");
			        	}
		        		else
		        		{
		        			tmpData.put("status", "");
			        	}	

		        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfos.getData(i).getString("UPDATE_TIME", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

		        		tmpData.put("offerDesc", USvcInfoQry.getSvcExplainBySvcId(serviceId));
		        		tmpData.put("offerType", "S");
		        		tmpData.put("offerTypeName", "服务");
		        		tmpData.put("brandId", "");
		        		tmpData.put("brandName", "");
		        		tmpData.put("isMain", userSvcInfos.getData(i).getString("MAIN_TAG", ""));
		        		tmpData.put("chooseTag", "");
		        		tmpData.put("canUnsub", "1");

		        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
						tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfos.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

		        		List<AttrTradeData> userAttrs = ucaData.getUserAttrsByRelaInstId(userSvcInfos.getData(i).getString("INST_ID", ""));
		        		for (int j=0 ; j < userAttrs.size() ; j++)
			        	{
				        	IData tmpData1 = new DataMap();
			        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
			        		tmpData1.put("attrName", "");
			        		tmpData1.put("attrDesc", "");
			        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
			        		tmpData1.put("attrValueDesc", "");
			        		offerAttrList.add(j, tmpData);
			        	}
		        		tmpData.put("offerAttrList", offerAttrList);
		        		
		        		tmpData.put("orderStaffid", userSvcInfos.getData(i).getString("UPDATE_STAFF_ID", ""));
		        		tmpData.put("orderChnn", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",
		        				userSvcInfos.getData(i).getString("UPDATE_DEPART_ID", "")));
		        		tmpData.put("remark", userSvcInfos.getData(i).getString("REMARK", ""));
		        		
		        		tmpData.put("bossId", "");
    	        		tmpData.put("prodInstId", "");
    	        		tmpData.put("isRemoved", "");
    	        		tmpData.put("instructions", "");
    	        		tmpData.put("isLog", "0");

		        		ids.add( tmpData);
		        	}
        		}
        		
        		inparam.put("DISCNTCODES", "15,19");
    			userDiscntInfos = getUserDiscnts(inparam);
        		if (IDataUtil.isNotEmpty(userDiscntInfos)){
		    		for (int i=0 ; i < userDiscntInfos.size() ; i++)
		        	{
		        		IData tmpData = new DataMap();
		        		String discntCode = userDiscntInfos.getData(i).getString("DISCNT_CODE", "");
		        		IDataset catalogInfos = UpcCall.qryCatalogByOfferId(discntCode,"S");
		        		String catalogId = "";
		        		String catalogName = "";
		        		if(IDataUtil.isNotEmpty(catalogInfos)){
		        			catalogId = catalogInfos.getData(0).getString("CATALOG_ID", "");
		        			catalogName = catalogInfos.getData(0).getString("CATALOG_NAME", "");
		        		}
		        		tmpData.put("offerId", discntCode);
		        		tmpData.put("offerName", UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode));
		        		tmpData.put("upOfferId", catalogId);
		        		tmpData.put("upOfferName", catalogName);
		        		tmpData.put("isPackage", "1");
		        		
		        		tmpData.put("spCode", "");
		        		tmpData.put("bizCode", "");
		        		tmpData.put("spName", "");
		        		tmpData.put("billType", "");
		        		tmpData.put("servType", "");
		        		tmpData.put("offerPrice", "");
		        		if("0".equals(queryMode))
			        	{
		        			tmpData.put("status", "0");
			        	}
		        		else
		        		{
		        			tmpData.put("status", "");
			        	}	

		        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfos.getData(i).getString("UPDATE_TIME", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

		        		tmpData.put("offerDesc", UDiscntInfoQry.getDiscntExplainByDiscntCode(discntCode));
		        		tmpData.put("offerType", "D");
		        		tmpData.put("offerTypeName", "优惠");
		        		tmpData.put("brandId", "");
		        		tmpData.put("brandName", "");
		        		tmpData.put("isMain", "0");
		        		tmpData.put("chooseTag", "");
		        		if("3410".equals(userDiscntInfos.getData(i).getString("DISCNT_CODE",""))
		        				|| "3411".equals(userDiscntInfos.getData(i).getString("DISCNT_CODE","")))
		        		{
		        			tmpData.put("canUnsub", "0");
		        		}
		        		else
		        		{
		        			tmpData.put("canUnsub", "1");
		        		}

		        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
						tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfos.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

		        		List<AttrTradeData> userAttrs = ucaData.getUserAttrsByRelaInstId(userDiscntInfos.getData(i).getString("INST_ID", ""));
		        		for (int j=0 ; j < userAttrs.size() ; j++)
			        	{
				        	IData tmpData1 = new DataMap();
			        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
			        		tmpData1.put("attrName", "");
			        		tmpData1.put("attrDesc", "");
			        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
			        		tmpData1.put("attrValueDesc", "");
			        		offerAttrList.add(j, tmpData);
			        	}
		        		tmpData.put("offerAttrList", offerAttrList);
		        		
		        		tmpData.put("orderStaffid", userDiscntInfos.getData(i).getString("UPDATE_STAFF_ID", ""));
		        		tmpData.put("orderChnn", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",
		        				userDiscntInfos.getData(i).getString("UPDATE_DEPART_ID", "")));
		        		tmpData.put("remark", userDiscntInfos.getData(i).getString("REMARK", ""));
		        		
		        		tmpData.put("bossId", "");
    	        		tmpData.put("prodInstId", "");;
    	        		tmpData.put("isLog", "0");
    	        		//k3
    	        		IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2742",userDiscntInfos.getData(i).getString("DISCNT_CODE","")
												,CSBizBean.getVisit().getStaffEparchyCode());
						String startOrderTime = "";
						String closeOrderTime = "";
						if(IDataUtil.isNotEmpty(commparaSet)){
							String rsrv_date1 = userDiscntInfos.getData(i).getString("RSRV_DATE1");//国漫产品的激活时间
							closeOrderTime =SysDateMgr.decodeTimestamp(userDiscntInfos.getData(i).getString("END_DATE"),SysDateMgr.PATTERN_STAND);//国漫产品的失效时间
							if(StringUtils.isNotBlank(rsrv_date1)){
								
								startOrderTime=SysDateMgr.decodeTimestamp(rsrv_date1, SysDateMgr.PATTERN_STAND);
				
				
							}else{
								
								startOrderTime=SysDateMgr.decodeTimestamp(userDiscntInfos.getData(i).getString("START_DATE",""),SysDateMgr.PATTERN_STAND);
							}
				
							tmpData.put("startOrderTime", startOrderTime);
							tmpData.put("closeOrderTime", closeOrderTime);
						}
    	        		
		        		ids.add(tmpData);
		        	}
        		}

	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("logUrl", "");

		        result = prepareOutResult(0,"",outData);
	        }
	        else if ("10".equals(queryType))
	        {
	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        	String discntCodes = "3410,3411,1452";
	        	List<DiscntTradeData> userDiscntInfo = ucaData.getUserDiscntsByDiscntCodeArray(discntCodes);
	        	if( (userDiscntInfo == null) )
	        	{
	        		result = prepareOutResult(1,"查询个人家庭V网商品为空！",outData);
	        	}
	        	for (int i=0 ; i < userDiscntInfo.size() ; i++)
	        	{
	        		
	        		IData tmpData = new DataMap();
        			tmpData.put("spCode", "");
        			tmpData.put("bizCode", "");
        			tmpData.put("spName", "");
        			tmpData.put("billType", "");
        			tmpData.put("servType", "");
	        		tmpData.put("offerId", userDiscntInfo.get(i).getDiscntCode());
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(userDiscntInfo.get(i).getDiscntCode(),"D"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
	        		
	        		tmpData.put("offerPrice", "");
	        		tmpData.put("status", "0");
	        		tmpData.put("orderTime", "");
	        		tmpData.put("offerDesc", "");
	        		tmpData.put("offerType", "D");
	        		tmpData.put("offerTypeName", "优惠");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");
	        		
	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfo.get(i).getStartDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfo.get(i).getEndDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		
	        		IDataset offerAttrList= new DatasetList();
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", "");
	        		tmpData.put("orderChnn", "");
	        		tmpData.put("remark", userDiscntInfo.get(i).getRemark());
	        		
	        		tmpData.put("bossId", "");
	        		tmpData.put("prodInstId", "");
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		ids.add(i, tmpData);
	        	}
	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
		        
		        result = prepareOutResult(0,"",outData);
	        }
	        else if ("11".equals(queryType))//家庭V网
	        {
	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        	String discntCodes = "3410,3411";
	        	List<DiscntTradeData> userDiscntInfo = ucaData.getUserDiscntsByDiscntCodeArray(discntCodes);
	        	if( (userDiscntInfo == null) )
	        	{
	        		result = prepareOutResult(1,"查询个人家庭V网商品为空！",outData);
	        	}
	        	for (int i=0 ; i < userDiscntInfo.size() ; i++)
	        	{
	        		
	        		IData tmpData = new DataMap();
        			tmpData.put("spCode", "");
        			tmpData.put("bizCode", "");
        			tmpData.put("spName", "");
        			tmpData.put("billType", "");
        			tmpData.put("servType", "");
	        		tmpData.put("offerId", userDiscntInfo.get(i).getDiscntCode());
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(userDiscntInfo.get(i).getDiscntCode(),"D"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
	        		
	        		tmpData.put("offerPrice", "");
	        		tmpData.put("status", "0");
	        		tmpData.put("orderTime", "");
	        		tmpData.put("offerDesc", "");
	        		tmpData.put("offerType", "D");
	        		tmpData.put("offerTypeName", "优惠");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfo.get(i).getStartDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfo.get(i).getEndDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		IDataset offerAttrList= new DatasetList();
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", "");
	        		tmpData.put("orderChnn", "");
	        		tmpData.put("remark", userDiscntInfo.get(i).getRemark());
	        		
	        		tmpData.put("bossId", "");
	        		tmpData.put("prodInstId", "");
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		ids.add(i, tmpData);
	        	}
	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
		        
		        result = prepareOutResult(0,"",outData);
	        }
	        else if ("12".equals(queryType))//流量共享
	        {
	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        	String discntCodes = "1452";
	        	List<DiscntTradeData> userDiscntInfo = ucaData.getUserDiscntsByDiscntCodeArray(discntCodes);
	        	if( (userDiscntInfo == null) )
	        	{
	        		result = prepareOutResult(1,"查询流量共享商品为空！",outData);
	        	}
	        	for (int i=0 ; i < userDiscntInfo.size() ; i++)
	        	{
	        		
	        		IData tmpData = new DataMap();
        			tmpData.put("spCode", "");
        			tmpData.put("bizCode", "");
        			tmpData.put("spName", "");
        			tmpData.put("billType", "");
        			tmpData.put("servType", "");
	        		tmpData.put("offerId", userDiscntInfo.get(i).getDiscntCode());
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(userDiscntInfo.get(i).getDiscntCode(),"D"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
	        		
	        		tmpData.put("offerPrice", "");
	        		tmpData.put("status", "0");
	        		tmpData.put("orderTime", "");
	        		tmpData.put("offerDesc", "");
	        		tmpData.put("offerType", "D");
	        		tmpData.put("offerTypeName", "优惠");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfo.get(i).getStartDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfo.get(i).getEndDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		IDataset offerAttrList= new DatasetList();
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", "");
	        		tmpData.put("orderChnn", "");
	        		tmpData.put("remark", userDiscntInfo.get(i).getRemark());
	        		
	        		tmpData.put("bossId", "");
	        		tmpData.put("prodInstId", "");
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		ids.add(i, tmpData);
	        	}
	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
		        
		        result = prepareOutResult(0,"",outData);
	        }
	        else if ("20".equals(queryType))
	        {
	        	//查询用户关系
	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	       	 
        		IDataset userRelationInfos = new DatasetList();//用户关系分为用户与用户的关系和用户与集团的关系
        		if("0".equals(queryMode) || "".equals(queryMode))
	        	{
        			userRelationInfos = qryRelationInfo(ucaData.getUserId());
	        	}
        		else if ("1".equals(queryMode))
	        	{
        			userRelationInfos = qryInvalidRelationInfo(ucaData.getUserId());
	        	}
	        	else
	        	{
	        		result = prepareOutResult(1,"不支持的queryMode值："+queryMode,outData);
		            return result;
	        	}
        		if (IDataUtil.isNotEmpty(userRelationInfos))
        		{
	        		for (int i=0 ; i < userRelationInfos.size() ; i++)
		        	{
		        		
		        		IData tmpData = new DataMap();
	        			tmpData.put("spCode", "");
	        			tmpData.put("bizCode", "");
	        			tmpData.put("spName", "");
	        			tmpData.put("billType", "");
	        			tmpData.put("servType", "");
		        		tmpData.put("offerId", userRelationInfos.getData(i).getString("RELATION_TYPE_CODE","")+"|R");
		        		tmpData.put("offerName", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_RELATION", "RELATION_TYPE_CODE", "RELATION_TYPE_NAME",userRelationInfos.getData(i).getString("RELATION_TYPE_CODE","")));
		        		tmpData.put("upOfferId", "");
		        		tmpData.put("upOfferName", "");
		        		tmpData.put("isPackage", "0");
		        		
		        		tmpData.put("offerPrice", "");
		        		if("0".equals(queryMode) || "".equals(queryMode))
    		        	{
    	        			tmpData.put("status", "0");
    		        	}
    	        		else
    	        		{
    	        			tmpData.put("status", "");
    		        	}
		        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userRelationInfos.getData(i).getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
		        		tmpData.put("offerDesc", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_RELATION", "RELATION_TYPE_CODE", "RELATION_TYPE_NAME",userRelationInfos.getData(i).getString("RELATION_TYPE_CODE","")));
		        		tmpData.put("offerType", "R");
		        		tmpData.put("offerTypeName", "集团关系");
		        		tmpData.put("brandId", "");
		        		tmpData.put("brandName", "");
		        		tmpData.put("isMain", "0");
		        		tmpData.put("chooseTag", "");
		        		tmpData.put("canUnsub", "0");
	
		        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userRelationInfos.getData(i).getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
						tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userRelationInfos.getData(i).getString("END_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	
		        		IDataset offerAttrList2= new DatasetList();
		        		tmpData.put("offerAttrList", offerAttrList2);
		        		
		        		tmpData.put("orderStaffid", userRelationInfos.getData(i).getString("TRADE_STAFF_ID",""));
		        		tmpData.put("orderChnn", userRelationInfos.getData(i).getString("UPDATE_DEPART_ID",""));
		        		tmpData.put("remark", userRelationInfos.getData(i).getString("REMARK",""));
		        		
		        		tmpData.put("bossId", "");
    	        		tmpData.put("prodInstId", "");
    	        		tmpData.put("isRemoved", "");
    	        		tmpData.put("instructions", "");
    	        		
    	        		tmpData.put("orderDep", userRelationInfos.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
    	        		tmpData.put("operModuleCode", "");
    	        		tmpData.put("operModuleName", "");
    	        		tmpData.put("operArea", "");
    	        		tmpData.put("shortNum", userRelationInfos.getData(i).getString("SHORT_CODE", ""));
    	        		tmpData.put("servAreaName", "");
    	        		tmpData.put("offerSts", "");//用户订购实例状态
    	        		tmpData.put("flowLongDistanceAjust", "");
    	        		tmpData.put("voiceLongDistanceAjust", "");
    	        		tmpData.put("flowBusiType", "");
    	        		tmpData.put("bindActId", "");
    	        		tmpData.put("bindActName", "");
    	        		tmpData.put("bindActEndTime", "");
    	        		tmpData.put("offerPriceDesc", "");
    	        		tmpData.put("lastOrderStaff", "");
    	        		tmpData.put("lastOrderChnn", "");
    	        		tmpData.put("lastOrderTime", "");
    	        		tmpData.put("priority", "");
    	        		tmpData.put("isLog", "0");

		        		ids.add(tmpData);
		        	}
	        		outData.put("offerList", ids);
		        	outData.put("resultRows", ids.size());
		        	outData.put("userId", ucaData.getUserId());
		        	outData.put("logUrl", "");

			        result = prepareOutResult(0,"",outData);
        		}
	        }
	        else if ("21".equals(queryType))
	        {
	        	
	        }
	        else if ("30".equals(queryType))//30-所有宽带商品
	        {
	        	//1.查询WLAN信息
	        	UcaData ucaDataForWLAN = UcaDataFactory.getNormalUca(busiCode);
	        	List<PlatSvcTradeData> userPlatsvc = ucaDataForWLAN.getUserPlatSvcs();
	        	int k = 0;
	        	String serviceId = "";
	        	for (int i=0 ; i < userPlatsvc.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();
	        		serviceId = ucaDataForWLAN.getUserPlatSvcs().get(i).getElementId();
	        		if (!"98002401".equals(serviceId) && !"98009201".equals(serviceId) && !"98009301".equals(serviceId))
	        		{
	        			continue;
	        		}
	        		IDataset spServiceInfo = UpcCall.qrySpServiceSpInfo(serviceId,"Z");
	        		if (IDataUtil.isNotEmpty(spServiceInfo)){
	        			tmpData.put("spCode", spServiceInfo.getData(0).getString("SP_CODE", ""));
	        			tmpData.put("bizCode", spServiceInfo.getData(0).getString("BIZ_CODE", ""));
	        			tmpData.put("spName", spServiceInfo.getData(0).getString("SP_Name", ""));
	        			tmpData.put("billType", spServiceInfo.getData(0).getString("BILL_TYPE", ""));
	        			tmpData.put("servType", spServiceInfo.getData(0).getString("BIZ_TYPE_CODE", ""));
	        		}
	        		else
	        		{
	        			tmpData.put("spCode", "");
	        			tmpData.put("bizCode", "");
	        			tmpData.put("spName", "");
	        			tmpData.put("billType", "");
	        			tmpData.put("servType", "");
	        		}
	        		tmpData.put("offerId", serviceId);
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(serviceId,"Z"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
	        		
	        		tmpData.put("offerPrice", "");
	        		tmpData.put("status", "0");
	        		tmpData.put("orderTime", "");
	        		tmpData.put("offerDesc", "");
	        		tmpData.put("offerType", "Z");
	        		tmpData.put("offerTypeName", "平台服务");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(ucaDataForWLAN.getUserPlatSvcs().get(i).getStartDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(ucaDataForWLAN.getUserPlatSvcs().get(i).getEndDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		IDataset offerAttrList= new DatasetList();
	        		List<AttrTradeData> userAttrs = ucaDataForWLAN.getUserAttrsByRelaInstId(ucaDataForWLAN.getUserPlatSvcs().get(i).getInstId());
	        		for (int j=0 ; j < userAttrs.size() ; j++)
		        	{
			        	IData tmpData1 = new DataMap();
		        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
		        		tmpData1.put("attrName", StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", 
				        		new java.lang.String[]{"SUBSYS_CODE", "PARAM_ATTR","PARAM_CODE","PARA_CODE1"},"PARAM_NAME", 
				        		new java.lang.String[]{"CSM","3700",serviceId,userAttrs.get(j).getAttrValue() }));
		        		tmpData1.put("attrDesc", "");
		        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
		        		tmpData1.put("attrValueDesc", "");
		        		offerAttrList.add(j, tmpData1);
		        	}
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", "");
	        		tmpData.put("orderChnn", "");
	        		tmpData.put("remark", ucaDataForWLAN.getUserPlatSvcs().get(i).getRemark());
	        		
	        		tmpData.put("bossId", "");
	        		tmpData.put("prodInstId", "");
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		ids.add(k, tmpData);
	        		k++;
	        	}
	        	
	        	//2.查询有线宽带信息
	        	String KDNumber = "";
	        	if (!"KD_".equals(busiCode.substring(0, 3)))
	            {
	        		KDNumber = "KD_" + busiCode;
	            }

	        	UcaData ucaData = UcaDataFactory.getNormalUca(KDNumber);
	        	List<ProductTradeData> userProudctInfo = ucaData.getUserProducts();
	        	List<SvcTradeData> userSvcInfo = ucaData.getUserSvcs();
	        	List<DiscntTradeData> userDiscntInfo = ucaData.getUserDiscnts();


	        	for (int i=0 ; i < userProudctInfo.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();

	        		tmpData.put("spCode", "");
	        		tmpData.put("bizCode", "");
	        		tmpData.put("spName", "");
	        		tmpData.put("billType", "");
	        		tmpData.put("servType", "");

	        		tmpData.put("offerId", ucaData.getUserProducts().get(i).getProductId());
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(ucaData.getUserProducts().get(i).getProductId(),"P"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
	        		
	        		tmpData.put("offerPrice", "");
	        		tmpData.put("status", "0");
	        		tmpData.put("orderTime", "");
	        		tmpData.put("offerDesc", "");
	        		tmpData.put("offerType", "P");
	        		tmpData.put("offerTypeName", "宽带产品");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(ucaData.getUserProducts().get(i).getStartDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(ucaData.getUserProducts().get(i).getEndDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		IDataset offerAttrList= new DatasetList();
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", "");
	        		tmpData.put("orderChnn", "");
	        		tmpData.put("remark", ucaData.getUserProducts().get(i).getRemark());
	        		
	        		tmpData.put("bossId", "");
	        		tmpData.put("prodInstId", "");
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		ids.add(k, tmpData);
	        		k++;
	        	}
	        	for (int i=0 ; i < userSvcInfo.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();

	        		tmpData.put("spCode", "");
	        		tmpData.put("bizCode", "");
	        		tmpData.put("spName", "");
	        		tmpData.put("billType", "");
	        		tmpData.put("servType", "");

	        		tmpData.put("offerId", userSvcInfo.get(i).getElementId());
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(userSvcInfo.get(i).getElementId(),"S"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
	        		
	        		tmpData.put("offerPrice", "");
	        		tmpData.put("status", "0");
	        		tmpData.put("orderTime", "");
	        		tmpData.put("offerDesc", "");
	        		tmpData.put("offerType", "S");
	        		tmpData.put("offerTypeName", "宽带服务");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfo.get(i).getStartDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfo.get(i).getEndDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		IDataset offerAttrList= new DatasetList();
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", "");
	        		tmpData.put("orderChnn", "");
	        		tmpData.put("remark", userSvcInfo.get(i).getRemark());
	        		
	        		tmpData.put("bossId", "");
	        		tmpData.put("prodInstId", "");
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		ids.add(k, tmpData);
	        		k++;
	        	}
	        	for (int i=0 ; i < userDiscntInfo.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();

	        		tmpData.put("spCode", "");
	        		tmpData.put("bizCode", "");
	        		tmpData.put("spName", "");
	        		tmpData.put("billType", "");

	        		tmpData.put("offerId", userDiscntInfo.get(i).getElementId());
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(userDiscntInfo.get(i).getElementId(),"D"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
	        		
	        		tmpData.put("offerPrice", "");
	        		tmpData.put("status", "0");
	        		tmpData.put("orderTime", "");
	        		tmpData.put("offerDesc", "");
	        		tmpData.put("offerType", "D");
	        		tmpData.put("offerTypeName", "宽带优惠");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfo.get(i).getStartDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfo.get(i).getEndDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		IDataset offerAttrList= new DatasetList();
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", "");
	        		tmpData.put("orderChnn", "");
	        		tmpData.put("remark", userDiscntInfo.get(i).getRemark());
	        		
	        		tmpData.put("bossId", "");
	        		tmpData.put("prodInstId", "");
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		ids.add(k, tmpData);
	        		k++;
	        	}
	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
		        
		        result = prepareOutResult(0,"",outData);
	        }
	        else if ("31".equals(queryType))//31-有线宽带
	        {
	        	if (!"KD_".equals(busiCode.substring(0, 3)))
	            {
	        		busiCode = "KD_" + busiCode;
	            }

	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        	List<ProductTradeData> userProudctInfo = ucaData.getUserProducts();
	        	List<SvcTradeData> userSvcInfo = ucaData.getUserSvcs();
	        	List<DiscntTradeData> userDiscntInfo = ucaData.getUserDiscnts();
	        	int k = 0;

	        	for (int i=0 ; i < userProudctInfo.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();

	        		tmpData.put("spCode", "");
	        		tmpData.put("bizCode", "");
	        		tmpData.put("spName", "");
	        		tmpData.put("billType", "");
	        		tmpData.put("servType", "");

	        		tmpData.put("offerId", ucaData.getUserProducts().get(i).getProductId());
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(ucaData.getUserProducts().get(i).getProductId(),"P"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
	        		
	        		tmpData.put("offerPrice", "");
	        		tmpData.put("status", "0");
	        		tmpData.put("orderTime", "");
	        		tmpData.put("offerDesc", "");
	        		tmpData.put("offerType", "P");
	        		tmpData.put("offerTypeName", "宽带产品");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(ucaData.getUserProducts().get(i).getStartDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(ucaData.getUserProducts().get(i).getEndDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		IDataset offerAttrList= new DatasetList();
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", "");
	        		tmpData.put("orderChnn", "");
	        		tmpData.put("remark", ucaData.getUserProducts().get(i).getRemark());
	        		
	        		tmpData.put("bossId", "");
	        		tmpData.put("prodInstId", "");
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		ids.add(k, tmpData);
	        		k++;
	        	}
	        	for (int i=0 ; i < userSvcInfo.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();

	        		tmpData.put("spCode", "");
	        		tmpData.put("bizCode", "");
	        		tmpData.put("spName", "");
	        		tmpData.put("billType", "");
	        		tmpData.put("servType", "");

	        		tmpData.put("offerId", userSvcInfo.get(i).getElementId());
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(userSvcInfo.get(i).getElementId(),"S"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
	        		
	        		tmpData.put("offerPrice", "");
	        		tmpData.put("status", "0");
	        		tmpData.put("orderTime", "");
	        		tmpData.put("offerDesc", "");
	        		tmpData.put("offerType", "S");
	        		tmpData.put("offerTypeName", "宽带服务");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfo.get(i).getStartDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSvcInfo.get(i).getEndDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		IDataset offerAttrList= new DatasetList();
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", "");
	        		tmpData.put("orderChnn", "");
	        		tmpData.put("remark", userSvcInfo.get(i).getRemark());
	        		
	        		tmpData.put("bossId", "");
	        		tmpData.put("prodInstId", "");
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		ids.add(k, tmpData);
	        		k++;
	        	}
	        	for (int i=0 ; i < userDiscntInfo.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();

	        		tmpData.put("spCode", "");
	        		tmpData.put("bizCode", "");
	        		tmpData.put("spName", "");
	        		tmpData.put("billType", "");
	        		tmpData.put("servType", "");

	        		tmpData.put("offerId", userDiscntInfo.get(i).getElementId());
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(userDiscntInfo.get(i).getElementId(),"D"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
	        		
	        		tmpData.put("offerPrice", "");
	        		tmpData.put("status", "0");
	        		tmpData.put("orderTime", "");
	        		tmpData.put("offerDesc", "");
	        		tmpData.put("offerType", "D");
	        		tmpData.put("offerTypeName", "宽带优惠");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfo.get(i).getStartDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfo.get(i).getEndDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		IDataset offerAttrList= new DatasetList();
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", "");
	        		tmpData.put("orderChnn", "");
	        		tmpData.put("remark", userDiscntInfo.get(i).getRemark());
	        		
	        		tmpData.put("bossId", "");
	        		tmpData.put("prodInstId", "");
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		ids.add(k, tmpData);
	        		k++;
	        	}
	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
		        
		        result = prepareOutResult(0,"",outData);
	        }
	        else if ("32".equals(queryType))//32-WLAN
	        {
	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        	List<PlatSvcTradeData> userPlatsvc = ucaData.getUserPlatSvcs();
	        	int k = 0;
	        	String serviceId = "";
	        	for (int i=0 ; i < userPlatsvc.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();
	        		serviceId = ucaData.getUserPlatSvcs().get(i).getElementId();
	        		if (!"98002401".equals(serviceId) && !"98009201".equals(serviceId) && !"98009301".equals(serviceId))
	        		{
	        			continue;
	        		}
	        		IDataset spServiceInfo = UpcCall.qrySpServiceSpInfo(serviceId,"Z");
	        		if (IDataUtil.isNotEmpty(spServiceInfo)){
	        			tmpData.put("spCode", spServiceInfo.getData(0).getString("SP_CODE", ""));
	        			tmpData.put("bizCode", spServiceInfo.getData(0).getString("BIZ_CODE", ""));
	        			tmpData.put("spName", spServiceInfo.getData(0).getString("SP_Name", ""));
	        			tmpData.put("billType", spServiceInfo.getData(0).getString("BILL_TYPE", ""));
	        			tmpData.put("servType", spServiceInfo.getData(0).getString("BIZ_TYPE_CODE", ""));
	        		}
	        		else
	        		{
	        			tmpData.put("spCode", "");
	        			tmpData.put("bizCode", "");
	        			tmpData.put("spName", "");
	        			tmpData.put("billType", "");
	        			tmpData.put("servType", "");
	        		}
	        		tmpData.put("offerId", serviceId);
	        		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(serviceId,"Z"));
	        		tmpData.put("upOfferId", "");
	        		tmpData.put("upOfferName", "");
	        		tmpData.put("isPackage", "0");
	        		
	        		tmpData.put("offerPrice", "");
	        		tmpData.put("status", "0");
	        		tmpData.put("orderTime", "");
	        		tmpData.put("offerDesc", "");
	        		tmpData.put("offerType", "Z");
	        		tmpData.put("offerTypeName", "平台服务");
	        		tmpData.put("brandId", "");
	        		tmpData.put("brandName", "");
	        		tmpData.put("isMain", "0");
	        		tmpData.put("chooseTag", "");
	        		tmpData.put("canUnsub", "1");

	        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(ucaData.getUserPlatSvcs().get(i).getStartDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(ucaData.getUserPlatSvcs().get(i).getEndDate(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

	        		IDataset offerAttrList= new DatasetList();
	        		List<AttrTradeData> userAttrs = ucaData.getUserAttrsByRelaInstId(ucaData.getUserPlatSvcs().get(i).getInstId());
	        		for (int j=0 ; j < userAttrs.size() ; j++)
		        	{
			        	IData tmpData1 = new DataMap();
		        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
		        		tmpData1.put("attrName", StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", 
				        		new java.lang.String[]{"SUBSYS_CODE", "PARAM_ATTR","PARAM_CODE","PARA_CODE1"},"PARAM_NAME", 
				        		new java.lang.String[]{"CSM","3700",serviceId,userAttrs.get(j).getAttrValue() }));
		        		tmpData1.put("attrDesc", "");
		        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
		        		tmpData1.put("attrValueDesc", "");
		        		offerAttrList.add(j, tmpData1);
		        	}
	        		tmpData.put("offerAttrList", offerAttrList);
	        		
	        		tmpData.put("orderStaffid", "");
	        		tmpData.put("orderChnn", "");
	        		tmpData.put("remark", ucaData.getUserPlatSvcs().get(i).getRemark());
	        		
	        		tmpData.put("bossId", "");
	        		tmpData.put("prodInstId", "");
	        		tmpData.put("isRemoved", "");
	        		tmpData.put("instructions", "");
	        		ids.add(k, tmpData);
	        		k++;
	        	}

	        	outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
		        
		        result = prepareOutResult(0,"",outData);
	        }
	        
	        else if ("82".equals(queryType))//82-优惠
	        {
	        	
	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);	       	
        		IDataset userDiscntInfos = new DatasetList();
        		IDataset acctOpenInfos = new DatasetList();

        		if("0".equals(queryMode) || "".equals(queryMode))
	        	{

        			userDiscntInfos = UserDiscntInfoQry.getAllDiscntInfo(ucaData.getUserId());
        			acctOpenInfos = queryOpenInfo(busiCode);

	        	}
        		else if ("1".equals(queryMode))
	        	{
        			userDiscntInfos = queryUserExpiryDiscntsByUserId(ucaData.getUserId());
	        	}
	        	else
	        	{
	        		result = prepareOutResult(1,"不支持的queryMode值："+queryMode,outData);
		            return result;
	        	}
        		//查询优惠
        		if (IDataUtil.isNotEmpty(userDiscntInfos))
        		{
	        		for (int i=0 ; i < userDiscntInfos.size() ; i++)
		        	{
		        		
		        		IData tmpData = new DataMap();
	        			tmpData.put("spCode", "");
	        			tmpData.put("bizCode", "");
	        			tmpData.put("spName", "");
	        			tmpData.put("billType", "");
	        			tmpData.put("servType", "");
		        		tmpData.put("offerId", userDiscntInfos.getData(i).getString("DISCNT_CODE","")+"|D");
		        		tmpData.put("offerName", UDiscntInfoQry.getDiscntNameByDiscntCode(userDiscntInfos.getData(i).getString("DISCNT_CODE","")));
		        		tmpData.put("upOfferId", "");
		        		tmpData.put("upOfferName", "");
		        		tmpData.put("isPackage", "0");
		        		
	        			tmpData.put("offerPrice", "");
		        		if(IDataUtil.isNotEmpty(acctOpenInfos)){
							for(int j=0;j<acctOpenInfos.size();j++){
								if(userDiscntInfos.getData(i).getString("INST_ID","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_INS_ID",""))
										&& "1".equals(acctOpenInfos.getData(j).getString("FEE_FLAG",""))){
									Float price = Float.parseFloat(acctOpenInfos.getData(j).getString("MONTH_RENT","0"))*100;
									if(price > 0)
									{
										tmpData.put("offerPrice",price+"/月");
									}
								}
							}
		        		}
    	        		if("80010485".equals(userDiscntInfos.getData(i).getString("DISCNT_CODE",""))) //流量王卡18元套餐
    	        		{
        	        		tmpData.put("offerPrice","1800/月");
    	        		}
    	        		if("3360".equals(userDiscntInfos.getData(i).getString("DISCNT_CODE",""))) // 神州行流量日租卡套餐
    	        		{
    	        			tmpData.put("offerPrice","0/月");
    	        		}
    	        		if("6633".equals(userDiscntInfos.getData(i).getString("DISCNT_CODE",""))) // 流量安心包（月费0元）
    	        		{
    	        			tmpData.put("offerPrice","0/月");
    	        		}
		        		if("0".equals(queryMode) || "".equals(queryMode))
    		        	{
    	        			tmpData.put("status", "0");
    		        	}
    	        		else
    	        		{
    	        			tmpData.put("status", "");
    		        	}
		        		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfos.getData(i).getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
		        		tmpData.put("offerDesc", UDiscntInfoQry.getDiscntExplainByDiscntCode(userDiscntInfos.getData(i).getString("DISCNT_CODE","")));
		        		tmpData.put("offerType", "D");
		        		tmpData.put("offerTypeName", "优惠");
		        		tmpData.put("brandId", "");
		        		tmpData.put("brandName", "");
		        		tmpData.put("isMain", "0");
		        		tmpData.put("chooseTag", "");
		        		if("3410".equals(userDiscntInfos.getData(i).getString("DISCNT_CODE",""))
		        				|| "3411".equals(userDiscntInfos.getData(i).getString("DISCNT_CODE","")))
		        		{
		        			tmpData.put("canUnsub", "0");
		        		}
		        		else
		        		{
		        			tmpData.put("canUnsub", "1");
		        		}
	
		        		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfos.getData(i).getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
						tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userDiscntInfos.getData(i).getString("END_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	
		        		IDataset offerAttrList2= new DatasetList();
		        		tmpData.put("offerAttrList", offerAttrList2);
		        		
		        		tmpData.put("orderStaffid", userDiscntInfos.getData(i).getString("TRADE_STAFF_ID",""));
		        		tmpData.put("orderChnn", userDiscntInfos.getData(i).getString("UPDATE_DEPART_ID",""));
		        		tmpData.put("remark", userDiscntInfos.getData(i).getString("REMARK",""));
		        		
		        		tmpData.put("bossId", userDiscntInfos.getData(i).getString("DISCNT_CODE","")+"|D");
    	        		tmpData.put("prodInstId", userDiscntInfos.getData(i).getString("INST_ID",""));
    	        		tmpData.put("isRemoved", "");
    	        		tmpData.put("instructions", "");
    	        		
    	        		tmpData.put("orderDep", userDiscntInfos.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
    	        		tmpData.put("operModuleCode", "");
    	        		tmpData.put("operModuleName", "");
    	        		tmpData.put("operArea", "");
    	        		tmpData.put("shortNum", "");
    	        		tmpData.put("servAreaName", "");
    	        		tmpData.put("offerSts", "");//用户订购实例状态
    	        		tmpData.put("flowLongDistanceAjust", "");
    	        		tmpData.put("voiceLongDistanceAjust", "");
    	        		tmpData.put("flowBusiType", "");
    	        		tmpData.put("bindActId", "");
    	        		tmpData.put("bindActName", "");
    	        		tmpData.put("bindActEndTime", "");
    	        		tmpData.put("offerPriceDesc", "");
    	        		tmpData.put("lastOrderStaff", "");
    	        		tmpData.put("lastOrderChnn", "");
    	        		tmpData.put("lastOrderTime", "");
    	        		tmpData.put("priority", "");
    	        		tmpData.put("isLog", "0");

		        		ids.add(tmpData);

		        	}
        		}
        		qryMainProductInfo(queryMode,busiCode,outData2,ids2);
        		ids.addAll(ids2);
        		outData.put("offerList", ids);
	        	outData.put("resultRows", ids.size());
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("logUrl", "");

		        result = prepareOutResult(0,"",outData);
	        }
	        
	        else
	        {
	        	result = prepareOutResult(1,"无法识别的查询类型："+queryType,outData);	        	
	        }
	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData queryUserPUKInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        IData input = new DataMap();
	        input.put("USER_ID",ucaData.getUserId());
	        IDataset userResInfo = qryUserResSimInfo(input);
	        if(!IDataUtil.isEmpty(userResInfo)){
	        	IData id = new DataMap();
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("simCardId", userResInfo.getData(0).getString("SIM_CARD_NO", ""));
	        	outData.put("imsi", userResInfo.getData(0).getString("IMSI", ""));
	        	outData.put("puk1", userResInfo.getData(0).getString("PUK", ""));
	        	outData.put("puk2", userResInfo.getData(0).getString("PUK2", ""));
	        	outData.put("pin1", userResInfo.getData(0).getString("PIN", ""));
	        	outData.put("pin2", userResInfo.getData(0).getString("PIN2", ""));
		        
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户SIM卡信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData authUserPasswd(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        String password = param.getString("password", "");

        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(password))
        {
        	password = getParams(param).getString("password", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if("".equals(password))
	        {        		            
	            result = prepareOutResult(1,"password不能为空",outData);
	            return result;
	        }
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);

			if (StringUtils.isNotBlank(password))
			{
				String inpass = ucaData.getUser().getUserPasswd();
	
				boolean tag = PasswdMgr.checkUserPassword(password,ucaData.getUserId(),inpass);
				if (tag)
				{

					result = prepareOutResult(0,"",outData);	
				}
				else
				{
					result = prepareOutResult(1,"服务密码校验错误！",outData);	
				}
			}
	        else
	        {	
	            result = prepareOutResult(1,"用户服务密码不能为空！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData authUserIden(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        String idType = param.getString("idType", "");
        String idValue = param.getString("idValue", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(idType))
        {
        	idType = getParams(param).getString("idType", "");
        }
        if ( "".equals(idValue))
        {
        	idValue = getParams(param).getString("idValue", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if("".equals(idType))
	        {        		            
	            result = prepareOutResult(1,"idType不能为空",outData);
	            return result;
	        }
	        if("".equals(idValue))
	        {        		            
	            result = prepareOutResult(1,"idValue不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        String psptId=ucaData.getCustPerson().getPsptId();
	        String psptTypeCode=ucaData.getCustPerson().getPsptTypeCode();
	        if("00".equals(idType) || "01".equals(idType) || "02".equals(idType) || "04".equals(idType) || "05".equals(idType) 
	        		|| "06".equals(idType) || "07".equals(idType) || "08".equals(idType) || "99".equals(idType) )//身份证件
	        {
	        	if ( idValue.equals(psptId) && idType.equals(HandlePsptTypeCode(psptTypeCode)) )
	        	{
	        		result = prepareOutResult(0,"",outData);
	        	}
	        	else
	        	{
	        		result = prepareOutResult(1,"证件校验失败！",outData);
	        	}        		
	        }
	        else
	        {	
	            result = prepareOutResult(1,"不支持的证件类型："+idType,outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData sendBusiInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        IData input = new DataMap();
	        input.put("USER_ID",ucaData.getUserId());
	        IDataset userResInfo = qryUserResSimInfo(input);
	        if(!IDataUtil.isEmpty(userResInfo)){
	        	IData id = new DataMap();
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("simCardId", userResInfo.getData(0).getString("SIM_CARD_NO", ""));
	        	outData.put("imsi", userResInfo.getData(0).getString("IMSI", ""));
	        	outData.put("puk1", userResInfo.getData(0).getString("PUK1", ""));
	        	outData.put("puk2", userResInfo.getData(0).getString("PUK2", ""));
	        	outData.put("pin1", userResInfo.getData(0).getString("PIN1", ""));
	        	outData.put("pin2", userResInfo.getData(0).getString("PIN2", ""));
		        
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户SIM卡信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData submitUserStop(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        String busiType = param.getString("busiType", "");
        //String actionDate = param.getString("actionDate", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(busiType))
        {
        	busiType = getParams(param).getString("busiType", "");
        }
/*        if ( "".equals(actionDate))
        {
        	actionDate = getParams(param).getString("actionDate", "");
        }*/
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if("".equals(busiType))
	        {        		            
	            result = prepareOutResult(1,"busiType不能为空",outData);
	            return result;
	        }
/*	        if("".equals(actionDate))
	        {        		            
	            result = prepareOutResult(1,"actionDate不能为空",outData);
	            return result;
	        }*/
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        String svcState = ucaData.getUser().getUserStateCodeset();
	        if (!"0".equals(svcState))
	        {
	        	result = prepareOutResult(1,"用户服务状态异常！",outData);
	        }
	        if("0201".equals(busiType)){ //紧急停机
	        	IData input = new DataMap();
	            ChangeSvcStateRegSVC regSvc = new ChangeSvcStateRegSVC();
	            input.put("SERIAL_NUMBER", busiCode);
	            input.put("TRADE_TYPE_CODE", "131");
	            //input.put("SERIAL_NUMBER", busiCode);
	            IDataset outDataset = regSvc.tradeReg(input);
	            if(!IDataUtil.isEmpty(outDataset)){
	            	outData.put("orderId", outDataset.getData(0).getString("ORDER_ID", ""));
	            }
		        result = prepareOutResult(0,"",outData);	
	        }
	        else if ("0202".equals(busiType)){//异地停机
	        
	        }
	        else
	        {	
	            result = prepareOutResult(1,"停开机业务类型错误！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData submitUserOpen(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        String busiType = param.getString("busiType", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(busiType))
        {
        	busiType = getParams(param).getString("busiType", "");
        }

        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if("".equals(busiType))
	        {        		            
	            result = prepareOutResult(1,"busiType不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        String svcState = ucaData.getUser().getUserStateCodeset();
	        
	        if("0101".equals(busiType)){ //绿色通道开机

	        }
	        else if ("0102".equals(busiType)){//紧急开机
	        	if (!"5".equals(svcState) && !"7".equals(svcState) && !"A".equals(svcState) && !"B".equals(svcState))
		        {
		        	result = prepareOutResult(1,"用户的语音通话功能服务状态, 不满足服务状态变化配置规则！",outData);
		        	return result;
		        }
	        	IData input = new DataMap();
	            ChangeSvcStateRegSVC regSvc = new ChangeSvcStateRegSVC();
	            input.put("SERIAL_NUMBER", busiCode);
	            input.put("TRADE_TYPE_CODE", "497");
	            IDataset outDataset = regSvc.tradeReg(input);
	            if(!IDataUtil.isEmpty(outDataset)){
	            	outData.put("orderId", outDataset.getData(0).getString("ORDER_ID", ""));
	            }
		        result = prepareOutResult(0,"",outData);	
	        }
	        else if ("0103".equals(busiType)){//申请开机
	        	if (!"1".equals(svcState))
		        {
		        	result = prepareOutResult(1,"用户服务状态异常！",outData);
		        	return result;
		        }
	        	IData input = new DataMap();
	            ChangeSvcStateRegSVC regSvc = new ChangeSvcStateRegSVC();
	            input.put("SERIAL_NUMBER", busiCode);
	            input.put("TRADE_TYPE_CODE", "133");
	            IDataset outDataset = regSvc.tradeReg(input);
	            if(!IDataUtil.isEmpty(outDataset)){
	            	outData.put("orderId", outDataset.getData(0).getString("ORDER_ID", ""));
	            }
		        result = prepareOutResult(0,"",outData);	
	        }
	        else if ("0104".equals(busiType)){//异地开机
		        
	        }
	        else if ("0105".equals(busiType)){//全球通临时信用开机
		        
	        }
	        else
	        {	
	            result = prepareOutResult(1,"停开机业务类型错误！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData productSync(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        IData input = new DataMap();
	        input.put("USER_ID",ucaData.getUserId());
	        IDataset userResInfo = qryUserResSimInfo(input);
	        if(!IDataUtil.isEmpty(userResInfo)){
	        	IData id = new DataMap();
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("simCardId", userResInfo.getData(0).getString("SIM_CARD_NO", ""));
	        	outData.put("imsi", userResInfo.getData(0).getString("IMSI", ""));
	        	outData.put("puk1", userResInfo.getData(0).getString("PUK1", ""));
	        	outData.put("puk2", userResInfo.getData(0).getString("PUK2", ""));
	        	outData.put("pin1", userResInfo.getData(0).getString("PIN1", ""));
	        	outData.put("pin2", userResInfo.getData(0).getString("PIN2", ""));
		        
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户SIM卡信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData queryGprsState(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        IData input = new DataMap();
	        input.put("USER_ID",ucaData.getUserId());
	        IData userGPRSStateInfo = getUserGPRSStateCode(ucaData.getUserId(),"22");
	        if(!IDataUtil.isEmpty(userGPRSStateInfo)){
	        	if ("0".equals(userGPRSStateInfo.getString("STATE_CODE", ""))){
	        		outData.put("state", "0");
	        	}
	        	else
	        	{
	        		outData.put("state", "1");
	        	}	
	        	
	        	outData.put("effectTime", SysDateMgr.date2String((SysDateMgr.string2Date(userGPRSStateInfo.getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        	outData.put("validTime", SysDateMgr.date2String((SysDateMgr.string2Date(userGPRSStateInfo.getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        	

		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户GPRS状态信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData queryOfferTypeInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        IData input = new DataMap();
	        input.put("USER_ID",ucaData.getUserId());
	        IDataset userResInfo = qryUserResSimInfo(input);
	        if(!IDataUtil.isEmpty(userResInfo)){
	        	IData id = new DataMap();
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("simCardId", userResInfo.getData(0).getString("SIM_CARD_NO", ""));
	        	outData.put("imsi", userResInfo.getData(0).getString("IMSI", ""));
	        	outData.put("puk1", userResInfo.getData(0).getString("PUK1", ""));
	        	outData.put("puk2", userResInfo.getData(0).getString("PUK2", ""));
	        	outData.put("pin1", userResInfo.getData(0).getString("PIN1", ""));
	        	outData.put("pin2", userResInfo.getData(0).getString("PIN2", ""));
		        
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户SIM卡信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData queryOffers(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        IData input = new DataMap();
	        input.put("USER_ID",ucaData.getUserId());
	        IDataset userResInfo = qryUserResSimInfo(input);
	        if(!IDataUtil.isEmpty(userResInfo)){
	        	IData id = new DataMap();
	        	outData.put("userId", ucaData.getUserId());
	        	outData.put("simCardId", userResInfo.getData(0).getString("SIM_CARD_NO", ""));
	        	outData.put("imsi", userResInfo.getData(0).getString("IMSI", ""));
	        	outData.put("puk1", userResInfo.getData(0).getString("PUK1", ""));
	        	outData.put("puk2", userResInfo.getData(0).getString("PUK2", ""));
	        	outData.put("pin1", userResInfo.getData(0).getString("PIN1", ""));
	        	outData.put("pin2", userResInfo.getData(0).getString("PIN2", ""));
		        
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户SIM卡信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData queryUserMainOffer(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        String userId = ucaData.getUserId();
	        String userProductName = "";
	        String userProductId = "";
	        String userProductDesc = "";
	        String nextProductId = "";
	        String nextProductName = "";
	        String nextProductDesc = "";

	        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
	        String sysTime = SysDateMgr.getSysTime();

	        if (IDataUtil.isNotEmpty(userMainProducts))
	        {
	            int size = userMainProducts.size();
	            for (int i = 0; i < size; i++)
	            {
	                IData userProduct = userMainProducts.getData(i);
	                if (userProduct.getString("START_DATE").compareTo(sysTime) < 0)
	                {
	                    userProductId = userProduct.getString("PRODUCT_ID");
	                    userProductName = UProductInfoQry.getProductNameByProductId(userProductId);
	                    userProductDesc = UProductInfoQry.getProductExplainByProductId(userProductId);
	                }
	                else
	                {
	                    nextProductId = userProduct.getString("PRODUCT_ID");
	                    nextProductName =  UProductInfoQry.getProductNameByProductId(nextProductId);
	                    nextProductDesc = UProductInfoQry.getProductExplainByProductId(nextProductId);
	                }
	            }
	        	outData.put("curOfferId", userProductId);
	        	outData.put("curOfferName", userProductName);
	        	outData.put("curOfferDesc", userProductDesc);
	        	if("".equals(nextProductId)){
	        		outData.put("nextOfferId", userProductId);
		        	outData.put("nextOfferName", userProductName);
		        	outData.put("nextOfferDesc", userProductDesc);
	        	}
	        	else
	        	{
	        		outData.put("nextOfferId", nextProductId);
		        	outData.put("nextOfferName", nextProductName);
		        	outData.put("nextOfferDesc", nextProductDesc);
	        	}
	        	outData.put("Ext1", "");
	        	outData.put("Ext2", "");
	        	outData.put("Ext3", "");
	        	outData.put("Ext4", "");
	        	outData.put("Ext5", "");
	        	outData.put("Ext6", "");
	        	outData.put("Ext7", "");
	        	outData.put("Ext8", "");
	        	outData.put("Ext9", "");
	        	outData.put("Ext10", "");
	        	result = prepareOutResult(0,"",outData);

	        }

	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户主产品信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData queryInuseSPBusiness(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
    	IDataset ids = new DatasetList();

        String busiCode = param.getString("userMobile", "");
        String servType = param.getString("servType", "");
        try{
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(servType))
        {
        	servType = getParams(param).getString("servType", "");
        }
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        	        
	        if ("".equals(servType) || null == servType)//个人所有平台业务
	        {
	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        	IDataset userPlatsvc = queryUserPlatByUserId(ucaData.getUserId());

	        	for (int i=0 ; i < userPlatsvc.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();
	        		
	        		IDataset spServiceInfo = UpcCall.qrySpServiceSpInfo(userPlatsvc.getData(i).getString("SERVICE_ID", ""),"Z");

	        		if (IDataUtil.isNotEmpty(spServiceInfo)){
	        			tmpData.put("spCode", spServiceInfo.getData(0).getString("SP_CODE", ""));
	        			tmpData.put("spName", spServiceInfo.getData(0).getString("SP_NAME", ""));
	        			tmpData.put("spShortName", "无");
		        		tmpData.put("subOperatorName", "未知");
		        		String billFlag = spServiceInfo.getData(0).getString("BILL_TYPE", "");
		        		if ("0".equals(billFlag))
		        		{
		        			tmpData.put("billFlag", "免费");	
		        		}
		        		else if ("1".equals(billFlag))
		        		{
		        			tmpData.put("billFlag", "按条计费");	
		        		}
		        		else if ("2".equals(billFlag))
		        		{
		        			tmpData.put("billFlag", "包月计费");	
		        		}
		        		else if ("3".equals(billFlag))
		        		{
		        			tmpData.put("billFlag", "包时计费");	
		        		}
		        		else if ("4".equals(billFlag))
		        		{
		        			tmpData.put("billFlag", "包次计费");	
		        		}
		        		else {
		        			tmpData.put("billFlag", "");	
		        		}
		        		tmpData.put("fee", spServiceInfo.getData(0).getString("PRICE", ""));
		        		tmpData.put("servType", spServiceInfo.getData(0).getString("BIZ_TYPE_CODE", ""));
		        		tmpData.put("servTypeName", spServiceInfo.getData(0).getString("BIZ_TYPE", ""));
		        		tmpData.put("operatorCode", spServiceInfo.getData(0).getString("BIZ_CODE", ""));
		        		tmpData.put("operatorName", spServiceInfo.getData(0).getString("BIZ_NAME", ""));
	        		}
	        		else
	        		{
	        			tmpData.put("spCode", "");
	        			tmpData.put("spName", "");
	        			tmpData.put("spShortName", "");
		        		tmpData.put("subOperatorName", "");
		        		tmpData.put("billFlag", "");	        		
		        		tmpData.put("fee", "");
		        		tmpData.put("servType", "");
		        		tmpData.put("servTypeName", "");
		        		tmpData.put("operatorCode", "");
		        		tmpData.put("operatorName", "");
	        		}
	        		tmpData.put("offerId", userPlatsvc.getData(i).getString("SERVICE_ID", ""));
	        		tmpData.put("staffCode", userPlatsvc.getData(i).getString("UPDATE_STAFF_ID", ""));
	        		tmpData.put("orgId", userPlatsvc.getData(i).getString("UPDATE_DEPART_ID", ""));
	        		tmpData.put("orgName", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",
	        				userPlatsvc.getData(i).getString("UPDATE_DEPART_ID", "")));

	        		tmpData.put("createDate", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvc.getData(i).getString("FIRST_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		tmpData.put("effDate", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvc.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		tmpData.put("expDate", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvc.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		tmpData.put("state", userPlatsvc.getData(i).getString("BIZ_STATE_CODE", ""));
	        		tmpData.put("spTelephone", "");
	        		tmpData.put("presentId", "");
	        		
	        		ids.add(tmpData);
	        		
	        	}
	        	outData.put("offerList", ids);
		        result = prepareOutResult(0,"",outData);
	        	
	        }

	        else //梦网业务
	        {	        	
	        	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        	IDataset userPlatsvc = queryUserPlatByUserId(ucaData.getUserId());
	        	//int k = 0;
	        	for (int i=0 ; i < userPlatsvc.size() ; i++)
	        	{
	        		IData tmpData = new DataMap();
	        		
	        		IDataset spServiceInfo = UpcCall.qrySpServiceSpInfo(userPlatsvc.getData(i).getString("SERVICE_ID", ""),"Z");
	        		if (IDataUtil.isNotEmpty(spServiceInfo)){
	        			if ("1".equals(spServiceInfo.getData(0).getString("SERV_MODE", ""))){
	        				continue;
	        			}
	        			tmpData.put("spCode", spServiceInfo.getData(0).getString("SP_CODE", ""));
	        			tmpData.put("spName", spServiceInfo.getData(0).getString("SP_NAME", ""));
	        			tmpData.put("spShortName", "");
		        		tmpData.put("subOperatorName", "");
		        		String billFlag = spServiceInfo.getData(0).getString("BILL_TYPE", "");
		        		if ("0".equals(billFlag))
		        		{
		        			tmpData.put("billFlag", "免费");	
		        		}
		        		else if ("1".equals(billFlag))
		        		{
		        			tmpData.put("billFlag", "按条计费");	
		        		}
		        		else if ("2".equals(billFlag))
		        		{
		        			tmpData.put("billFlag", "包月计费");	
		        		}
		        		else if ("3".equals(billFlag))
		        		{
		        			tmpData.put("billFlag", "包时计费");	
		        		}
		        		else if ("4".equals(billFlag))
		        		{
		        			tmpData.put("billFlag", "包次计费");	
		        		}
		        		else {
		        			tmpData.put("billFlag", "");	
		        		}	        		
		        		tmpData.put("fee", spServiceInfo.getData(0).getString("PRICE", ""));
		        		tmpData.put("servType", spServiceInfo.getData(0).getString("BIZ_TYPE_CODE", ""));
		        		tmpData.put("servTypeName", spServiceInfo.getData(0).getString("BIZ_TYPE", ""));
		        		tmpData.put("operatorCode", spServiceInfo.getData(0).getString("BIZ_CODE", ""));
		        		tmpData.put("operatorName", spServiceInfo.getData(0).getString("BIZ_NAME", ""));
	        		}
	        		else
	        		{
	        			tmpData.put("spCode", "");
	        			tmpData.put("spName", "");
	        			tmpData.put("spShortName", "");
		        		tmpData.put("subOperatorName", "");
		        		tmpData.put("billFlag", "");	        		
		        		tmpData.put("fee", "");
		        		tmpData.put("servType", "");
		        		tmpData.put("servTypeName", "");
		        		tmpData.put("operatorCode", "");
		        		tmpData.put("operatorName", "");
	        		}
	        		tmpData.put("offerId", userPlatsvc.getData(i).getString("SERVICE_ID", ""));
	        		tmpData.put("staffCode", userPlatsvc.getData(i).getString("UPDATE_STAFF_ID", ""));
	        		tmpData.put("orgId", userPlatsvc.getData(i).getString("UPDATE_DEPART_ID", ""));
	        		tmpData.put("orgName", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",
	        				userPlatsvc.getData(i).getString("UPDATE_DEPART_ID", "")));        		
	        		tmpData.put("createDate", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvc.getData(i).getString("FIRST_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		tmpData.put("effDate", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvc.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		tmpData.put("expDate", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvc.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
	        		tmpData.put("state", userPlatsvc.getData(i).getString("BIZ_STATE_CODE", ""));
	        		tmpData.put("spTelephone", "");
	        		tmpData.put("presentId", "");

	        		ids.add( tmpData);
	        		//k++;
	        	}

	        	outData.put("offerList", ids);
		        
		        result = prepareOutResult(0,"",outData);
	        }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData precheckOffersOrder(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        try{

	        IData input  = transferDataInputForProductChange(param);
			input.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账

	        String roamingTag = input.getString("ROAMINGTAG", "0");
	        IDataset set= null;
	        if("0".equals(roamingTag)){
	        	set=CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", input);
	        }
	        else 
	        {//处理国漫数据流量定向套餐	办理    
	        	 //核对用户是不是已经有了此套餐
	            /* UcaData ud = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER",""));
	             String userId=ud.getUserId();
	             IDataset userDiscnts=UserDiscntInfoQry.getAllDiscntByUser(userId, input.getString("DISCNT_CODE", ""));
	             if(IDataUtil.isNotEmpty(userDiscnts)){
	            	result = prepareOutResult(1,"不能重复申请此套餐！",outData);
	             }
	             else
	             {*/
	            	outData.put("orderId", "");	
	 	        	outData.put("openMode", "");//开通方式
	 		        result = prepareOutResult(0,"",outData);
	 		        return result;
	             //}
	        	 /*IData input1 = new DataMap();
	        	 input1.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER",""));
	        	 //input1.put("DISCNT_CODE", input.getString("DISCNT_CODE", ""));         
	        	 input1.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
	             input1.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());  
	             input1.put("USER_TYPE", "00");  
	             input1.put("MODIFY_TAG", input.getString("MODIFY_TAG", ""));// 订购
	             input1.put("ELEMENT_TYPE_CODE", "D");
	             input1.put("ELEMENT_ID", input.getString("DISCNT_CODE", ""));  
	             set= CSAppCall.call("SS.InterRoamDayRegSVC.tradeReg", input1);*/

	        }
			IData retnData = new DataMap();
			retnData = (set != null && set.size()>0)?set.getData(0):new DataMap();
			if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID")))
	        {
	        	outData.put("orderId", retnData.getString("ORDER_ID"));	
	        	outData.put("openMode", "");//开通方式
	        	
	        	//add by zhangxing3 for 中移在线一级业务接口规范-个人业务分册1.0.7
	        	IDataset offerList = new DatasetList();
	        	 IDataset elements = getDatasets(getParams(param),"offerList");
	             for (int i = 0, size = elements.size(); i < size; i++)
	             {
	                 IData selectElement = new DataMap();
	                 selectElement.clear();

	                 IData element = elements.getData(i);
	                 String offerId = element.getString("offerId", "");
	             	if( !"".equals(offerId))
	             	{
	             		String []arg = StringUtils.split(offerId, "|");
	             		selectElement.put("offerId", arg[0]);
	             		if("D".equals(arg[1]))
	             		{
	             			selectElement.put("offerName", UDiscntInfoQry.getDiscntNameByDiscntCode(arg[0]));
	             		}
	             		else if ("S".equals(arg[1]))
	             		{
	             			selectElement.put("offerName", USvcInfoQry.getSvcNameBySvcId(arg[0]));
	             		}
	             		else if ("Z".equals(arg[1]))
	             		{
	             			selectElement.put("offerName", UPlatSvcInfoQry.getSvcNameBySvcId(arg[0]));
	             		}
	             		else
	             		{
	             			selectElement.put("offerName", "");
	             		}
	             	}

	                 selectElement.put("action", element.getString("action",""));
	                 selectElement.put("provinceOfferType", "NoHW");
	                 selectElement.put("prodCreateDate", "");
	                 selectElement.put("prodEndDate", "");
	                 selectElement.put("outcome", "0");
	                 selectElement.put("account", "成功");
	                 selectElement.put("releType", "1");
	                 
	                 
	                 offerList.add(selectElement);
	             }
	             outData.put("offerList", offerList);
	             outData.put("checkResult", "Y");
	             outData.put("checkMessage", "");
		        //add by zhangxing3 for 中移在线一级业务接口规范-个人业务分册1.0.7
	             
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	        	IDataset offerList = new DatasetList();
	        	outData.put("offerList", offerList);
	        	outData.put("checkResult", "N");
	            outData.put("checkMessage", "商品订购预校验失败！");
	        	result = prepareOutResult(1,"商品订购预校验失败！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	IDataset offerList = new DatasetList();
        	outData.put("offerList", offerList);
        	outData.put("checkResult", "N");
            outData.put("checkMessage", "商品订购预校验异常:"+e.getMessage());
        	result = prepareOutResult(1,"商品订购预校验异常:"+e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData submitOffersOrder(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        try{
	        IData input  = transferDataInputForProductChange(param);
	        String roamingTag = input.getString("ROAMINGTAG", "0");
	        IDataset set= null;
	        if("0".equals(roamingTag)){
	        	set=CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", input);
	        }
	        else 
	        {//处理国漫数据流量定向套餐	办理   
	        	
	        	 //核对用户是不是已经有了此套餐
	             /*UcaData ud = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER",""));
	             String userId=ud.getUserId();
	             IDataset userDiscnts=UserDiscntInfoQry.getAllDiscntByUser(userId, input.getString("DISCNT_CODE", ""));
	             if(IDataUtil.isNotEmpty(userDiscnts)){
	            	result = prepareOutResult(1,"不能重复申请此套餐！",outData);
	             }*/
	        	
	        	 IData input1 = new DataMap();
	        	 input1.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER",""));
	        	 //input1.put("DISCNT_CODE", input.getString("DISCNT_CODE", ""));         
	        	 input1.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
	             input1.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());  
	        	 input1.put("USER_TYPE", "00");  
	             input1.put("MODIFY_TAG", input.getString("MODIFY_TAG", ""));// 订购
	             input1.put("ELEMENT_TYPE_CODE", "D");
	             input1.put("ELEMENT_ID", input.getString("DISCNT_CODE", ""));  
	             set= CSAppCall.call("SS.InterRoamDayRegSVC.tradeReg", input1);

	        }
			IData retnData = new DataMap();
			retnData = (set != null && set.size()>0)?set.getData(0):new DataMap();
			if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID")))
	        {
	        	outData.put("orderId", retnData.getString("ORDER_ID"));	
	        	
	        	//add by zhangxing3 for 中移在线一级业务接口规范-个人业务分册1.0.7
	        	IDataset offerList = new DatasetList();
	        	String tradeId = retnData.getString("TRADE_ID","");
	        	IDataset discntTradeInfos = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
	        	if (IDataUtil.isNotEmpty(discntTradeInfos))
	        	{
	        		for (int i = 0 ; i < discntTradeInfos.size(); i++ )
	        		{
	        			IData discntTradeData = discntTradeInfos.getData(i);
	        			IData tempData = new DataMap();
	        			tempData.put("offerId", discntTradeData.getString("DISCNT_CODE", ""));
	        			tempData.put("offerName", UDiscntInfoQry.getDiscntNameByDiscntCode(discntTradeData.getString("DISCNT_CODE", "")));
	        			tempData.put("action", discntTradeData.getString("MODIFY_TAG", ""));
	        			tempData.put("outcome", "0"); //结果 , 0：成功，1：失败
	        			tempData.put("account", "成功");
	        			tempData.put("releType", "1");
	        			offerList.add( tempData );
	        		}
	        	}
	        	IDataset svcTradeInfos = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
	        	if (IDataUtil.isNotEmpty(svcTradeInfos))
	        	{
	        		for (int i = 0 ; i < svcTradeInfos.size(); i++ )
	        		{
	        			IData svcTradeData = svcTradeInfos.getData(i);
	        			IData tempData = new DataMap();
	        			tempData.put("offerId", svcTradeData.getString("SERVICE_ID", ""));
	        			tempData.put("offerName", USvcInfoQry.getSvcNameBySvcId(svcTradeData.getString("SERVICE_ID", "")));
	        			tempData.put("action", svcTradeData.getString("MODIFY_TAG", ""));
	        			tempData.put("outcome", "0"); //结果 , 0：成功，1：失败
	        			tempData.put("account", "成功");
	        			tempData.put("releType", "1");
	        			offerList.add( tempData );
	        		}
	        	}	        	
	        	IDataset platSvcTradeInfos = TradePlatSvcInfoQry.getTradePlatSvcByTradeId(tradeId);
	        	if (IDataUtil.isNotEmpty(platSvcTradeInfos))
	        	{
	        		for (int i = 0 ; i < platSvcTradeInfos.size(); i++ )
	        		{
	        			IData platSvcTradeData = platSvcTradeInfos.getData(i);
	        			IData tempData = new DataMap();
	        			tempData.put("offerId", platSvcTradeData.getString("SERVICE_ID", ""));
	        			tempData.put("offerName", UPlatSvcInfoQry.getSvcNameBySvcId(platSvcTradeData.getString("SERVICE_ID", "")));
	        			tempData.put("action", platSvcTradeData.getString("MODIFY_TAG", ""));
	        			tempData.put("outcome", "0"); //结果 , 0：成功，1：失败
	        			tempData.put("account", "成功");
	        			tempData.put("releType", "1");
	        			offerList.add( tempData );
	        		}
	        	}
	        	outData.put("offerList", offerList);
	        	//add by zhangxing3 for 中移在线一级业务接口规范-个人业务分册1.0.7
	        	
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	        	IDataset offerList = new DatasetList();
	        	outData.put("offerList", offerList);
	        	result = prepareOutResult(1,"商品订购失败！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	IDataset offerList = new DatasetList();
        	outData.put("offerList", offerList);
        	result = prepareOutResult(1,"商品订购异常:"+e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData submitMainOfferChange(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        try{
	        IData input  = transferDataInputForProductChange(param);
	        IDataset set=CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", input);
			IData retnData = new DataMap();
			retnData = (set != null && set.size()>0)?set.getData(0):new DataMap();
			if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID")))
	        {
	        	outData.put("orderId", retnData.getString("ORDER_ID"));
	        	
	        	//add by zhangxing3 for 中移在线一级业务接口规范-个人业务分册1.0.7
	        	IDataset offerList = new DatasetList();
	        	String tradeId = retnData.getString("TRADE_ID","");
	        	IDataset productTradeInfos = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
	        	if (IDataUtil.isNotEmpty(productTradeInfos))
	        	{
	        		for (int i = 0 ; i < productTradeInfos.size(); i++ )
	        		{
	        			IData productTradeData = productTradeInfos.getData(i);
	        			IData tempData = new DataMap();
	        			tempData.put("offerId", productTradeData.getString("PRODUCT_ID", ""));
	        			tempData.put("offerName", UProductInfoQry.getProductNameByProductId(productTradeData.getString("PRODUCT_ID", "")));
	        			tempData.put("action", productTradeData.getString("MODIFY_TAG", ""));
	        			tempData.put("outcome", "0"); //结果 , 0：成功，1：失败
	        			tempData.put("account", "成功");
	        			tempData.put("releType", "0");
	        			offerList.add( tempData );
	        		}
	        	}
	        	outData.put("offerList", offerList);
	        	//add by zhangxing3 for 中移在线一级业务接口规范-个人业务分册1.0.7

		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	        	IDataset offerList = new DatasetList();
	        	outData.put("offerList", offerList);
	        	result = prepareOutResult(1,"主套餐变更订单提交失败！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	IDataset offerList = new DatasetList();
        	outData.put("offerList", offerList);
        	result = prepareOutResult(1,"主套餐变更订单提交异常:"+e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData submitSPBusiness(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
    	IDataset offerList = new DatasetList();
        String busiCode = param.getString("userMobile", "");
        IDataset spList = getDatasets(getParams(param),"spList");

        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");

        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if(IDataUtil.isEmpty(spList))
	        {        		            
	            result = prepareOutResult(1,"spList不能为空",outData);
	            return result;
	        }
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        IData input = new DataMap();
	        input.put("SERIAL_NUMBER",busiCode);
	        input.put("USER_ID",ucaData.getUserId());
	        input.put("SELECTED_ELEMENTS",prePlatSvcData(spList));
	        IData data = CSAppCall.call("SS.PlatRegSVC.tradeReg", input).getData(0);	        
	        if(!IDataUtil.isEmpty(data)){
	        	outData.put("orderId", data.getString("ORDER_ID", ""));
	        	for(int k=0;k<spList.size();k++){
	        		IData offerData = new DataMap();
	        		String offerId = spList.getData(k).getString("offerId", "");
	        		String oprType = spList.getData(k).getString("oprType", "");
	        		if("01".equals(oprType)){
	        			oprType="0";
	        		}else if("02".equals(oprType)){
	        			oprType="1";
	        		}
	            	String []arg = StringUtils.split(offerId, "|");
	            	String offerName = UPlatSvcInfoQry.getSvcNameBySvcId(arg[0]);
	            	offerData.put("offerId", arg[0]);
	            	offerData.put("offerName", offerName);
	            	offerData.put("action", oprType);
	            	offerData.put("outcome", "0");
	            	offerData.put("account", "成功");
	            	offerData.put("releType", "1");
	            	offerList.add(offerData);
	        	}
	        	outData.put("offerList", offerList);
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	        	outData.put("orderId","");
	        	for(int k=0;k<spList.size();k++){
	        		IData offerData = new DataMap();
	        		String offerId = spList.getData(k).getString("offerId", "");
	        		String oprType = spList.getData(k).getString("oprType", "");
	        		if("01".equals(oprType)){
	        			oprType="0";
	        		}else if("02".equals(oprType)){
	        			oprType="1";
	        		}
	            	String []arg = StringUtils.split(offerId, "|");
	            	String offerName = UPlatSvcInfoQry.getSvcNameBySvcId(arg[0]);
	            	offerData.put("offerId", arg[0]);
	            	offerData.put("offerName", offerName);
	            	offerData.put("action", oprType);
	            	offerData.put("outcome", "1");
	            	offerData.put("account", "梦网业务办理异常！");
	            	offerData.put("releType", "1");
	            	offerList.add(offerData);
	        	}
	        	outData.put("offerList", offerList);
	            result = prepareOutResult(1,"梦网业务办理异常！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	outData.put("orderId","");
        	for(int k=0;k<spList.size();k++){
        		IData offerData = new DataMap();
        		String offerId = spList.getData(k).getString("offerId", "");
        		String oprType = spList.getData(k).getString("oprType", "");
        		if("01".equals(oprType)){
        			oprType="0";
        		}else if("02".equals(oprType)){
        			oprType="1";
        		}
            	String []arg = StringUtils.split(offerId, "|");
            	String offerName = UPlatSvcInfoQry.getSvcNameBySvcId(arg[0]);
            	offerData.put("offerId", arg[0]);
            	offerData.put("offerName", offerName);
            	offerData.put("action", oprType);
            	offerData.put("outcome", "1");
            	offerData.put("account", e.getMessage());
            	offerData.put("releType", "1");
            	offerList.add(offerData);
        	}
        	outData.put("offerList", offerList);
        	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData queryScoreExchageCommodities(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IDataset ids = new DatasetList();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        String exchangeType = param.getString("exchangeType", "");
        String scoreMin = param.getString("scoreMin", "");
        String scoreMax = param.getString("scoreMax", "");
        String commodityName = param.getString("commodityName", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(exchangeType))
        {
        	exchangeType = getParams(param).getString("exchangeType", "");
        }
        if ( "".equals(scoreMin))
        {
        	scoreMin = getParams(param).getString("scoreMin", "");
        }
        if ( "".equals(scoreMax))
        {
        	scoreMax = getParams(param).getString("scoreMax", "");
        }
        if ( "".equals(commodityName))
        {
        	commodityName = getParams(param).getString("commodityName", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        
	        String userId = ucaData.getUserId();
	        String brandCode = ucaData.getBrandCode();
	        if (StringUtils.isBlank(brandCode))
	        {
	            // 根据用户服务号码获取用户品牌出错！
	        	result = prepareOutResult(1,"根据用户服务号码获取用户品牌出错！",outData);
	        	return result;
	        }
	        if (!"G001".equals(brandCode) && !"G010".equals(brandCode) && !"G002".equals(brandCode))
	        { // G002老神州行用户有积分也能参与兑换
	            // 该用户产品不能进行积分兑换，用户品牌必须为全球通或动感地带或神州行！
	        	result = prepareOutResult(1,"该用户产品不能进行积分兑换，用户品牌必须为全球通或动感地带或神州行！",outData);
	        	return result;
	        }

	        // 查用户积分
	        IDataset scoreInfo = AcctCall.queryUserScore(userId);
	        if (IDataUtil.isEmpty(scoreInfo))
	        {
	        	// 获取用户积分无数据!
	        	result = prepareOutResult(1,"获取用户积分无数据!",outData);
	        	return result;
	        }
	        String score = scoreInfo.getData(0).getString("SUM_SCORE"); // 用户可兑换积分
	        if (StringUtils.isNotBlank(score) && Integer.parseInt(score) <= 0)
	        {
	            // 用户可兑换积分为[" + score + "]，业务不能继续!
	        	result = prepareOutResult(1,"用户可兑换积分为[" + score + "]，业务不能继续!",outData);
	        	return result;
	        }
	        
	        
	        //兑换列表
	        IDataset exchangeList = queryExchangeList(score,brandCode,HandleExchangeType(exchangeType),scoreMin,scoreMax,commodityName);
	        if(!IDataUtil.isEmpty(exchangeList)){
	        	IData id = new DataMap();
	        	for (int i=0 ; i < exchangeList.size() ; i++)
	        	{
	        		
	        		IData tmpData = new DataMap();
	        		//0-实物，1-话费，2-电子卡，3-套餐
	        		String exchangeTypeCode = exchangeList.getData(i).getString("EXCHANGE_TYPE_CODE", "");
	        		if ("2".equals(exchangeTypeCode))
	        		{
	        			tmpData.put("exchangeType", "0");
	        		}
	        		else if ("1".equals(exchangeTypeCode))
	        		{
	        			tmpData.put("exchangeType", "1");
	        		}
	        		else if ("N".equals(exchangeTypeCode))
	        		{
	        			tmpData.put("exchangeType", "2");
	        		}
	        		else if ("3".equals(exchangeTypeCode))
	        		{
	        			tmpData.put("exchangeType", "3");
	        		}
	        		else
	        		{
	        			tmpData.put("exchangeType", exchangeTypeCode);
	        		}
        			
        			tmpData.put("commodityId", exchangeList.getData(i).getString("RULE_ID", ""));
        			tmpData.put("commodityName", exchangeList.getData(i).getString("RULE_NAME", ""));
        			tmpData.put("commodityNum", "");
	        		tmpData.put("commodityValue", "");
	        		tmpData.put("scoreNeed", exchangeList.getData(i).getString("SCORE", ""));
	        		tmpData.put("commodityDesc","");
	        		tmpData.put("exchangeLimit", exchangeList.getData(i).getString("EXCHANGE_LIMIT", ""));

	        		ids.add(i, tmpData);
	        	}
	        	outData.put("commodityInfo", ids);
	        	outData.put("resultRows", ids.size());
		        
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"获取所有可兑换的记录列表异常！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData submitScoreExchange(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        String commodityId = param.getString("commodityId", "");
        String commodityNum = param.getString("commodityNum", "");
        String scoreOld = param.getString("scoreOld", "");
        String scoreValue = param.getString("scoreValue", "");
        String exchangeType = param.getString("exchangeType", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(commodityId))
        {
        	commodityId = getParams2(param,"scoreList").getString("commodityId", "");
        }
        if ( "".equals(commodityNum))
        {
        	commodityNum = getParams2(param,"scoreList").getString("commodityNum", "");
        }
        if ( "".equals(scoreOld))
        {
        	scoreOld = getParams2(param,"scoreList").getString("scoreOld", "");
        }
        if ( "".equals(scoreValue))
        {
        	scoreValue = getParams2(param,"scoreList").getString("scoreValue", "");
        }
        if ( "".equals(exchangeType))
        {
        	exchangeType = getParams2(param,"scoreList").getString("exchangeType", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        /*UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);*/
            
            IData input = new DataMap();
            input.put("SERIAL_NUMBER", busiCode);
            input.put("RULE_ID", commodityId);
            input.put("COUNT", commodityNum);
	        IData data = CSAppCall.call("SS.ScoreExchangeRegSVC.infTradeReg", input).getData(0);


	        if(!IDataUtil.isEmpty(data)){
	        	outData.put("orderId", data.getString("ORDER_ID", ""));
		        
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"积分兑换异常！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData changeOfLongDistanceRoamingLevel(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IDataset datalist = new DatasetList();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        String origCallLevel = param.getString("origCallLevel", "");
        String callLevel = param.getString("callLevel", "");
        String origRoamLevel = param.getString("origRoamLevel", "");
        String roamLevel = param.getString("roamLevel", "");
        String amountRequ = param.getString("amountRequ", "");
        String effTime = param.getString("effTime", "");
        String expTime = param.getString("expTime", "");
        String remark = param.getString("remark", "");
        boolean tag = true;
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        	origCallLevel = getParams(param).getString("origCallLevel", "");
        	callLevel = getParams(param).getString("callLevel", "");
        	origRoamLevel = getParams(param).getString("origRoamLevel", "");
        	roamLevel = getParams(param).getString("roamLevel", "");
        	amountRequ = getParams(param).getString("amountRequ", "");
        	effTime = getParams(param).getString("effTime", "");
        	expTime = getParams(param).getString("expTime", "");
        	remark = getParams(param).getString("remark", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        

	        
	        if(("0".equals(origRoamLevel) || "1".equals(origRoamLevel)) 
	        		&& ("2".equals(roamLevel) || "5".equals(roamLevel) ||"6".equals(roamLevel))) //开通国际漫游
	        {
	        	
	        	IData item = new DataMap();
	            item.put("SERIAL_NUMBER", busiCode);
	            if("5".equals(roamLevel))
	            {
	            	String endDate = SysDateMgr.addDays(SysDateMgr.getSysDate(), 30);
	            	item.put("END_DATE",SysDateMgr.suffixDate(endDate,1));
	            }
	            else if("6".equals(roamLevel))
	            {
	            	String endDate = SysDateMgr.addDays(SysDateMgr.getSysDate(), 180);
	            	item.put("END_DATE",SysDateMgr.suffixDate(endDate,1));
	            }
	            else{
		            if(!"".equals(expTime) && !"20991231235959".equals(expTime))
		            {
		            	item.put("END_DATE",
		            			SysDateMgr.date2String((SysDateMgr.string2Date(expTime, SysDateMgr.PATTERN_STAND_SHORT)),SysDateMgr.PATTERN_STAND));
		            }
		            else{
		            	item.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
		            }
	            }
	            IDataset feeList = loadFeeInfo(item);
                
                if (IDataUtil.isNotEmpty(feeList))
                { 
                	result = prepareOutResult(1,"用户话费余额不足，请先交纳差额话费后再重新办理国漫开通功能",outData);	
                }
                datalist =  ChangeProductIntfSVC.addRormByEndDate(item);
	        	tag = false;
	        }
	        
	        if(("2".equals(origRoamLevel) || "5".equals(origRoamLevel) ||"6".equals(origRoamLevel) ) 
	        		&& ("0".equals(roamLevel) || "1".equals(roamLevel)) ) //取消国际漫游
	        {
	        	IData item = new DataMap();
	            item.put("SERIAL_NUMBER", busiCode);
	        	datalist =  ChangeProductIntfSVC.delRoam(item);
	        	tag = false;
	        }
	        
	        if(tag && "0".equals(origCallLevel) && "1".equals(callLevel)) //开通国际长途
	        {
	        	IData item = new DataMap();
	            item.put("SERIAL_NUMBER", busiCode);
	        	item.put("MODIFY_TAG", "0");
	        	datalist = ChangeProductIntfSVC.changeInterCall(item);
	        }
	        
	        if(tag && "1".equals(origCallLevel) && "0".equals(callLevel)) //取消国际长途
	        {
	        	IData item = new DataMap();
	            item.put("SERIAL_NUMBER", busiCode);
	        	item.put("MODIFY_TAG", "1");
	        	datalist = ChangeProductIntfSVC.changeInterCall(item);
	        }
	        
	        if(!IDataUtil.isEmpty(datalist)){
	        	outData.put("orderId", datalist.getData(0).getString("ORDER_ID", ""));   
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"国际业务开通取消异常！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData queryOrderedCampaign(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IDataset ids = new DatasetList();
    	IData outData = new DataMap();
        try{
	        String busiCode = param.getString("userMobile", "");
	        String startDate = param.getString("startDate", "");
	        String endDate = param.getString("endDate", "");
	        String state = param.getString("state", "");
	
	        if ( "".equals(busiCode))
	        {
	        	busiCode = getParams(param).getString("userMobile", "");
	        }
	        if ( "".equals(startDate))
	        {
	        	startDate = getParams(param).getString("startDate", "");
	        }
	        if ( "".equals(endDate))
	        {
	        	endDate = getParams(param).getString("endDate", "");
	        }
	        if ( "".equals(state))
	        {
	        	state = getParams(param).getString("state", "");
	        }
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        IData input = new DataMap();
	        String userId = ucaData.getUserId();
	        IDataset userSaleActiveInfos = new DatasetList();
	        userSaleActiveInfos = getUserSaleActiveByStartEndDate(userId,state, startDate,endDate);

        	 if(!IDataUtil.isEmpty(userSaleActiveInfos)){
    		 	for (int i = 0 ; i<userSaleActiveInfos.size(); i++)
	        	{
	        		IData tmpData = new DataMap();
	        		tmpData.put("batchId", userSaleActiveInfos.getData(i).getString("PRODUCT_ID", ""));
	        		tmpData.put("batchName", userSaleActiveInfos.getData(i).getString("PRODUCT_NAME", ""));
	        		tmpData.put("packageId", userSaleActiveInfos.getData(i).getString("PACKAGE_ID", ""));
	        		tmpData.put("packageName", userSaleActiveInfos.getData(i).getString("PACKAGE_NAME", ""));
	        		String endDateStr = "";
	        	//REQ201909060007 _关于更改营销活动状态口径的需求
	        		String accDate =userSaleActiveInfos.getData(i).getString("RSRV_DATE2", "");
	        	//	String endDates = chgFormat(userSaleActiveInfos.getData(i).getString("END_DATE", ""),SysDateMgr.PATTERN_STAND,SysDateMgr.PATTERN_STAND_SHORT);
	        		if(accDate.equals("") || accDate == null){
	        			endDateStr = chgFormat(userSaleActiveInfos.getData(i).getString("END_DATE", ""),SysDateMgr.PATTERN_STAND,SysDateMgr.PATTERN_STAND_SHORT);;
	        		}else{
	        			endDateStr = chgFormat(userSaleActiveInfos.getData(i).getString("RSRV_DATE2", ""),SysDateMgr.PATTERN_STAND,SysDateMgr.PATTERN_STAND_SHORT);;
	        		}
	        		
	        		String sysdate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
	        		if(endDateStr.compareTo(sysdate) > 0)
	        		{
	        			tmpData.put("state", "正常");
	        		}
	        		else 
	        		{
	        			tmpData.put("state", "结束");
	        		}
	        		
	        		tmpData.put("orderDate", chgFormat(userSaleActiveInfos.getData(i).getString("ACCEPT_DATE", ""),SysDateMgr.PATTERN_STAND,SysDateMgr.PATTERN_STAND_SHORT));
	        		tmpData.put("effectDate", chgFormat(userSaleActiveInfos.getData(i).getString("START_DATE", ""),SysDateMgr.PATTERN_STAND,SysDateMgr.PATTERN_STAND_SHORT));
	        		tmpData.put("expireDate", chgFormat(userSaleActiveInfos.getData(i).getString("END_DATE", ""),SysDateMgr.PATTERN_STAND,SysDateMgr.PATTERN_STAND_SHORT));
	        		
	        		tmpData.put("staffCode", userSaleActiveInfos.getData(i).getString("TRADE_STAFF_ID", ""));
	        		String updateDepartId = userSaleActiveInfos.getData(i).getString("UPDATE_DEPART_ID", "");
	        		if("".equals(updateDepartId)){
	        			tmpData.put("channelName", "未知");
	        		}
	        		else
	        		{
	        			tmpData.put("channelName", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",updateDepartId));
	        		}
	        		tmpData.put("packageBossId", userSaleActiveInfos.getData(i).getString("PACKAGE_ID", ""));
	        		tmpData.put("batchBossId", userSaleActiveInfos.getData(i).getString("PRODUCT_ID", ""));
	        		tmpData.put("remark","");
	        		String inDate="";
	        		String outDate="";
	        		String rsrv_date1 = userSaleActiveInfos.getData(i).getString("RSRV_DATE1", "");
	        		String rsrv_date2 = userSaleActiveInfos.getData(i).getString("RSRV_DATE2", "");
	        		if(StringUtils.isNotBlank(rsrv_date1)){
	        			inDate=SysDateMgr.getChinaDate(rsrv_date1, SysDateMgr.PATTERN_STAND);
	        		}
	        		if(StringUtils.isNotBlank(rsrv_date2)){
	        			outDate=SysDateMgr.getChinaDate(rsrv_date2,SysDateMgr.PATTERN_STAND);
	        		}
	        		tmpData.put("ext1",inDate);//入网开始时间
	        		tmpData.put("ext2",outDate);//入网结束时间
	        		tmpData.put("ext3",UStaffInfoQry.getStaffNameByStaffId(userSaleActiveInfos.getData(i).getString("TRADE_STAFF_ID", "")));//受理员工名称
	        		tmpData.put("ext4","");
	        		tmpData.put("ext5","");
	        		tmpData.put("ext6","");
	        		tmpData.put("ext7","");
	        		tmpData.put("ext8","");
	        		tmpData.put("ext9","");
	        		tmpData.put("ext10","");
	        		ids.add(i, tmpData);		        		
	        	}
    		 	outData.put("offerList", ids);
    		 	outData.put("resultRows", ids.size());
	        	result = prepareOutResult(0,"",outData);	
        	 }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户营销活动信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData queryCampaignRules(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IDataset ids = new DatasetList();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        String packageId = param.getString("packageId", "");
        String packageName = param.getString("packageName", "");
        String batchId = param.getString("batchId", "");

        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(packageId))
        {
        	packageId = getParams(param).getString("packageId", "");
        }
        if ( "".equals(packageName))
        {
        	packageName = getParams(param).getString("packageName", "");
        }
        if ( "".equals(batchId))
        {
        	batchId = getParams(param).getString("batchId", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if("".equals(packageId) && "".equals(packageName) && "".equals(batchId))
	        {        		            
	            result = prepareOutResult(1,"packageId、batchId与packageName至少传一个",outData);
	            return result;
	        }
	        
	        if (!"".equals(packageId))
	        {
	        	IData inParam = new DataMap();
		        inParam.put("SERIAL_NUMBER", busiCode);
		        inParam.put("PACKAGE_ID", packageId);
		        String productId = "";
		        String productName = "";
		        IDataset catalogInfo = UpcCall.qryCatalogByOfferId(packageId,"K");
		        if(!IDataUtil.isEmpty(catalogInfo)){
		        	productId = catalogInfo.getData(0).getString("CATALOG_ID", "");
		        	productName = catalogInfo.getData(0).getString("CATALOG_NAME", "");
		        }
		        inParam.put("PRODUCT_ID", productId);
		        inParam.put("CAMPN_TYPE", qryCampnTypeForSaleActive(productId));
		        SaleActiveCheckBean saleActiveCheckBean = BeanManager.createBean(SaleActiveCheckBean.class);
		        IData ruleResultData =  saleActiveCheckBean.checkPackage(inParam);
		        if(!IDataUtil.isEmpty(ruleResultData)){
		        	IData id = new DataMap();
		        	id.put("batchId", productId);
		        	id.put("batchName", productName);
		        	id.put("packageId", packageId);
		        	id.put("packageName", UPackageInfoQry.getPackageNameByPackageId(packageId));
		        	IDataset tips = ruleResultData.getDataset("TIPS_TYPE_ERROR");
		        	if(!IDataUtil.isEmpty(tips))
		        	{
		        		id.put("isCanOrder","否");
		        		id.put("cause", tips.getData(0).getString("TIPS_INFO", ""));  
		        	}
		        	else{
		        		id.put("isCanOrder","是");
		        	}
		        	id.put("packageBossId", packageId);
		        	id.put("batchBossId", productId);
		        	
		        	ids.add(0, id);
		        	outData.put("discntList", ids);
		        	outData.put("resultRows", ids.size());

			        result = prepareOutResult(0,"",outData);	
		        }
		        else
		        {	
		            result = prepareOutResult(1,"营销活动资格查询失败！",outData);	
			    }
	        }

	        if (!"".equals(batchId))
	        {
	        	IData inParam = new DataMap();
		        inParam.put("SERIAL_NUMBER", busiCode);
		        String productId = batchId;
		        inParam.put("PRODUCT_ID", productId);
		        inParam.put("CAMPN_TYPE", qryCampnTypeForSaleActive(productId));
		        String productName = "";
		        IData saleActiveProductinfo = UProductInfoQry.qrySaleActiveProductByPK(productId);
		        if(!IDataUtil.isEmpty(saleActiveProductinfo)){
		        	productName = saleActiveProductinfo.getString("PRODUCT_NAME", "");
		        }
		        IDataset saleActiveinfos = UpcCall.qryOffersByCatalogId(productId);
		        if(!IDataUtil.isEmpty(saleActiveinfos)){
		        	for (int i = 0 ;i<saleActiveinfos.size(); i++)
		        	{
				        packageId = saleActiveinfos.getData(i).getString("OFFER_CODE", "");
		        		inParam.put("PACKAGE_ID", packageId);
				        SaleActiveCheckBean saleActiveCheckBean = BeanManager.createBean(SaleActiveCheckBean.class);
				        IData ruleResultData =  saleActiveCheckBean.checkPackage(inParam);
				        if(!IDataUtil.isEmpty(ruleResultData)){
				        	IData id = new DataMap();
				        	id.put("batchId", productId);
				        	id.put("batchName", productName);
				        	id.put("packageId", packageId);
				        	id.put("packageName", UPackageInfoQry.getPackageNameByPackageId(packageId));
				        	IDataset tips = ruleResultData.getDataset("TIPS_TYPE_ERROR");
				        	if(!IDataUtil.isEmpty(tips))
				        	{
				        		id.put("isCanOrder","否");
				        		id.put("cause", tips.getData(0).getString("TIPS_INFO", ""));  
				        	}
				        	else{
				        		id.put("isCanOrder","是");
				        	}
				        	id.put("packageBossId", packageId);
				        	id.put("batchBossId", productId);

				        	ids.add(id);
				        }
		        	}
		        
		        	outData.put("discntList", ids);
		        	outData.put("resultRows", ids.size());
			        result = prepareOutResult(0,"",outData);	
		        }
		        else
		        {	
		            result = prepareOutResult(1,"营销活动资格查询失败！",outData);	
			    }
	        }
	        
	        	        
	        if (!"".equals(packageName))
	        {
	        	IData inParam = new DataMap();
		        inParam.put("SERIAL_NUMBER", busiCode);
		        String productId = "";
		        String productName = "";
		        
		        IDataset saleActiveinfos = UpcCall.qryOffersByOfferTypeLikeOfferName("K",null,packageName);
		        if(!IDataUtil.isEmpty(saleActiveinfos)){
		        	for (int i = 0 ;i<saleActiveinfos.size(); i++)
		        	{
				        packageId = saleActiveinfos.getData(i).getString("OFFER_CODE", "");
				        IDataset catalogInfo = UpcCall.qryCatalogByOfferId(packageId,"K");
				        if(!IDataUtil.isEmpty(catalogInfo)){
				        	productId = catalogInfo.getData(0).getString("CATALOG_ID", "");
				        	productName = catalogInfo.getData(0).getString("CATALOG_NAME", "");
				        }
				        inParam.put("PACKAGE_ID", packageId);
				        inParam.put("PRODUCT_ID", productId);
				        inParam.put("CAMPN_TYPE", qryCampnTypeForSaleActive(productId));
				        SaleActiveCheckBean saleActiveCheckBean = BeanManager.createBean(SaleActiveCheckBean.class);
				        IData ruleResultData =  saleActiveCheckBean.checkPackage(inParam);
				        if(!IDataUtil.isEmpty(ruleResultData)){
				        	IData id = new DataMap();
				        	id.put("batchId", productId);
				        	id.put("batchName", productName);
				        	id.put("packageId", packageId);
				        	id.put("packageName", UPackageInfoQry.getPackageNameByPackageId(packageId));
				        	IDataset tips = ruleResultData.getDataset("TIPS_TYPE_ERROR");
				        	if(!IDataUtil.isEmpty(tips))
				        	{
				        		id.put("isCanOrder","否");
				        		id.put("cause", tips.getData(0).getString("TIPS_INFO", ""));  
				        	}
				        	else{
				        		id.put("isCanOrder","是");
				        	}
				        	id.put("packageBossId", packageId);
				        	id.put("batchBossId", productId);

				        	ids.add(id);
				        }
		        	}
		        
		        	outData.put("discntList", ids);
		        	outData.put("resultRows", ids.size());
			        result = prepareOutResult(0,"",outData);	
		        }
		        else
		        {	
		            result = prepareOutResult(1,"营销活动资格查询失败！",outData);	
			    }
	        }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData queryCampaigns(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IDataset ids = new DatasetList();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        String batchId = param.getString("batchId", "");
        String batchName = param.getString("batchName", "");
        String packageId = param.getString("packageId", "");
        String packageName = param.getString("packageName", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(batchId))
        {
        	batchId = getParams(param).getString("batchId", "");
        }
        if ( "".equals(batchName))
        {
        	batchName = getParams(param).getString("batchName", "");
        }
        if ( "".equals(packageId))
        {
        	packageId = getParams(param).getString("packageId", "");
        }
        if ( "".equals(packageName))
        {
        	packageName = getParams(param).getString("packageName", "");
        }
        String paging = getParams(getParams(param),"crmpfPubInfo").getString("paging", "");
        String rowsPerPage = getParams(getParams(param),"crmpfPubInfo").getString("rowsPerPage", "");
        String pageNum = getParams(getParams(param),"crmpfPubInfo").getString("pageNum", "");
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if("".equals(batchId) && "".equals(batchName) && "".equals(packageId) && "".equals(packageName) )
	        {        		            
	            result = prepareOutResult(1,"packageId、packageName、batchId与batchName 至少传一个",outData);
	            return result;
	        }
	        IData input = new DataMap();
	        input.put("SERIAL_NUMBER",busiCode);
	        input.put("CATALOG_ID",batchId);
	        input.put("CATALOG_NAME",batchName);
	        input.put("PACKAGE_ID",packageId);
	        input.put("PACKAGE_NAME",packageName);
	        IDataset saleActiveInfos =  querySaleActives(input);
	        //System.out.println("===========queryCampaigns============="+saleActiveInfos);
	        if(!IDataUtil.isEmpty(saleActiveInfos)){
	        	if ("1".equals(paging))//需要分页
	        	{
	        		int j = Integer.parseInt(pageNum)*Integer.parseInt(rowsPerPage) > saleActiveInfos.size() ? saleActiveInfos.size():Integer.parseInt(pageNum)*Integer.parseInt(rowsPerPage);
	        		for (int i = (Integer.parseInt(pageNum) -1)*Integer.parseInt(rowsPerPage);i<j; i++)
		        	{
		        		IData tmpData = new DataMap();
		        		if ("true".equals(saleActiveInfos.getData(i).getString("ALL_FAIL", "")))
		        		{
		        			continue;
		        		}
		        		tmpData.put("batchId", saleActiveInfos.getData(i).getString("PRODUCT_ID", ""));
		        		IDataset catalogInfos = UpcCall.qryCatalogByCatalogId(saleActiveInfos.getData(i).getString("PRODUCT_ID", ""));
		        		if(!IDataUtil.isEmpty(catalogInfos)){
			        		tmpData.put("batchName", catalogInfos.getData(0).getString("CATALOG_NAME", ""));
		        		}
		        		else {
		        			tmpData.put("batchName", "");
		        		}
		        		tmpData.put("packageId", saleActiveInfos.getData(i).getString("PACKAGE_ID", ""));
		        		tmpData.put("packageName", saleActiveInfos.getData(i).getString("PACKAGE_NAME", ""));
		        		tmpData.put("startDate", SysDateMgr.date2String((SysDateMgr.string2Date(saleActiveInfos.getData(i).getString("VALID_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
		        		tmpData.put("endDate", SysDateMgr.date2String((SysDateMgr.string2Date(saleActiveInfos.getData(i).getString("EXPIRE_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
		        		
		        		//中移在线一级业务接口规范--个人业务分册1.0.7
		        		//isCanOrder,和总部及业务协商后，确定该字段统一返回固定值“需资格校验”已提高接口查询效率
		        		tmpData.put("isCanOrder", "需资格校验");
		        		IDataset enableModes =  UpcCall.queryEnableModeRelByOfferId("K",saleActiveInfos.getData(i).getString("PACKAGE_ID", ""));
		        		if(!IDataUtil.isEmpty(enableModes)){
		        			String disableMode = enableModes.getData(0).getString("DISABLE_MODE", "");
		        			if("1".equals(disableMode))
		        			{
		        				String disableUnit =  enableModes.getData(0).getString("DISABLE_UNIT", "");
		        				if("0".equals(disableUnit) || "1".equals(disableUnit))
		        				{
		        					tmpData.put("openCycleUnit", "1"); //开通周期单位:1-天(用于计算失效时间)
		        				}
		        				if("2".equals(disableUnit) || "3".equals(disableUnit))
		        				{
		        					tmpData.put("openCycleUnit", "2"); //开通周期单位:2-月(用于计算失效时间)
		        				}
		        				if("4".equals(disableUnit) || "5".equals(disableUnit))
		        				{
		        					tmpData.put("openCycleUnit", "5"); //开通周期单位:5-年底(用于计算失效时间)
		        				}
				        		tmpData.put("openCycleValue", enableModes.getData(0).getString("DISABLE_OFFSET", ""));//开通周期值,用于计算失效时间
		        			}
		        			else{
				        		tmpData.put("openCycleUnit", "6"); //开通周期单位:0-小时、1-天、2-月、3-当天，4-月底、5-年底、6-长期(用于计算失效时间)
				        		String absoluteDisableDate = enableModes.getData(0).getString("ABSOLUTE_DISABLE_DATE", "");
				        		if(!"".equals(absoluteDisableDate))
				        		{
				        			tmpData.put("openCycleValue", SysDateMgr.date2String((SysDateMgr.string2Date(absoluteDisableDate, SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
				        			//开通周期值,用于计算失效时间
				        		}
				        		else
				        		{
				        			tmpData.put("openCycleValue","");
				        		}
				        		
		        			}
		        		}
		        		else
		        		{
		        			tmpData.put("openCycleUnit", ""); //开通周期单位:0-小时、1-天、2-月、3-当天，4-月底、5-年底、6-长期(用于计算失效时间)
			        		tmpData.put("openCycleValue", "");//开通周期值,用于计算失效时间
		        		}
		        		//中移在线一级业务接口规范--个人业务分册1.0.7
		        		
		        		tmpData.put("packageBossId", saleActiveInfos.getData(i).getString("PACKAGE_ID", ""));
		        		tmpData.put("batchBossId", saleActiveInfos.getData(i).getString("PRODUCT_ID", ""));
		        		IDataset discntAttrList = new DatasetList();
		        		tmpData.put("discntAttrList", discntAttrList);
		        		ids.add (tmpData);
		        	}
	        	}
	        	else {//不需要分页
	        		for (int i = 0; i<saleActiveInfos.size(); i++)
		        	{
		        		IData tmpData = new DataMap();
		        		if ("true".equals(saleActiveInfos.getData(i).getString("ALL_FAIL", "")))
		        		{
		        			continue;
		        		}
		        		tmpData.put("batchId", saleActiveInfos.getData(i).getString("PRODUCT_ID", ""));
		        		IDataset catalogInfos = UpcCall.qryCatalogByCatalogId(saleActiveInfos.getData(i).getString("PRODUCT_ID", ""));
		        		if(!IDataUtil.isEmpty(catalogInfos)){
			        		tmpData.put("batchName", catalogInfos.getData(0).getString("CATALOG_NAME", ""));
		        		}
		        		else {
		        			tmpData.put("batchName", "");
		        		}
		        		tmpData.put("packageId", saleActiveInfos.getData(i).getString("PACKAGE_ID", ""));
		        		tmpData.put("packageName", saleActiveInfos.getData(i).getString("PACKAGE_NAME", ""));
		        		if(!"".equals(saleActiveInfos.getData(i).getString("VALID_DATE", "")))
		        		{
		        			
		        			tmpData.put("startDate", SysDateMgr.date2String((SysDateMgr.string2Date(saleActiveInfos.getData(i).getString("VALID_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
		        		}
		        		else
		        		{
		        			tmpData.put("startDate", "");
		        		}
		        		if(!"".equals(saleActiveInfos.getData(i).getString("EXPIRE_DATE", "")))
		        		{
		        			tmpData.put("endDate", SysDateMgr.date2String((SysDateMgr.string2Date(saleActiveInfos.getData(i).getString("EXPIRE_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
		        		}
		        		else
		        		{
		        			tmpData.put("endDate", "");
		        		}
		        		
		        		//中移在线一级业务接口规范--个人业务分册1.0.7
		        		//isCanOrder,和总部及业务协商后，确定该字段统一返回固定值“需资格校验”已提高接口查询效率
		        		tmpData.put("isCanOrder", "需资格校验");
		        		IDataset enableModes =  UpcCall.queryEnableModeRelByOfferId("K",saleActiveInfos.getData(i).getString("PACKAGE_ID", ""));
		        		if(!IDataUtil.isEmpty(enableModes)){
		        			String disableMode = enableModes.getData(0).getString("DISABLE_MODE", "");
		        			if("1".equals(disableMode))
		        			{
		        				String disableUnit =  enableModes.getData(0).getString("DISABLE_UNIT", "");
		        				if("0".equals(disableUnit) || "1".equals(disableUnit))
		        				{
		        					tmpData.put("openCycleUnit", "1"); //开通周期单位:1-天(用于计算失效时间)
		        				}
		        				if("2".equals(disableUnit) || "3".equals(disableUnit))
		        				{
		        					tmpData.put("openCycleUnit", "2"); //开通周期单位:2-月(用于计算失效时间)
		        				}
		        				if("4".equals(disableUnit) || "5".equals(disableUnit))
		        				{
		        					tmpData.put("openCycleUnit", "5"); //开通周期单位:5-年底(用于计算失效时间)
		        				}
				        		tmpData.put("openCycleValue", enableModes.getData(0).getString("DISABLE_OFFSET", ""));//开通周期值,用于计算失效时间
		        			}
		        			else{
				        		tmpData.put("openCycleUnit", "6"); //开通周期单位:0-小时、1-天、2-月、3-当天，4-月底、5-年底、6-长期(用于计算失效时间)
				        		String absoluteDisableDate = enableModes.getData(0).getString("ABSOLUTE_DISABLE_DATE", "");
				        		if(!"".equals(absoluteDisableDate))
				        		{
				        			tmpData.put("openCycleValue", SysDateMgr.date2String((SysDateMgr.string2Date(absoluteDisableDate, SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
				        			//开通周期值,用于计算失效时间
				        		}
				        		else
				        		{
				        			tmpData.put("openCycleValue","");
				        		}
		        			}
		        		}
		        		else
		        		{
		        			tmpData.put("openCycleUnit", ""); //开通周期单位:0-小时、1-天、2-月、3-当天，4-月底、5-年底、6-长期(用于计算失效时间)
			        		tmpData.put("openCycleValue", "");//开通周期值,用于计算失效时间
		        		}
		        		//中移在线一级业务接口规范--个人业务分册1.0.7

		        		tmpData.put("packageBossId", saleActiveInfos.getData(i).getString("PACKAGE_ID", ""));
		        		tmpData.put("batchBossId", saleActiveInfos.getData(i).getString("PRODUCT_ID", ""));
		        		IDataset discntAttrList = new DatasetList();
		        		tmpData.put("discntAttrList", discntAttrList);
		        		ids.add (tmpData);
		        	}
	        	}
	        	outData.put("resultRows", saleActiveInfos.size());
    		 	outData.put("discntList", ids);
	        	result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到营销活动信息！",outData);	
		    }
	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            return result;
        }
    }
    
    public IData preCheckCampaignsOrder(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
		IDataset offerList = new DatasetList();
		IDataset comRel = new DatasetList();
    	try{
	    	
	    	String packageId = "";String action ="";
	    	String busiCode = getParams(param).getString("userMobile", "");
	    	IDataset packageList = getDatasets(getParams(param),"packageList");

	    	if(!IDataUtil.isEmpty(packageList)){
		    	 packageId = packageList.getData(0).getString("packageId", "");
		    	 action = packageList.getData(0).getString("action", "");
	    	}
	    	else
	    	{
	    		result = prepareOutResult(1,"营销活动订单预校验,packageList不能为空！",outData);
	    	}
	    	if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if("".equals(packageId))
	        {        		            
	            result = prepareOutResult(1,"packageId不能为空",outData);
	            return result;
	        }
        	IData input = new DataMap();
        	input.put("SERIAL_NUMBER", busiCode);
        	input.put("PACKAGE_ID", packageId);
        	String productId = "";
        	IDataset catalogInfo = UpcCall.qryCatalogByOfferId(packageId,"K");
	        if(!IDataUtil.isEmpty(catalogInfo)){
	        	productId = catalogInfo.getData(0).getString("CATALOG_ID", "");
	        }
	        comRel = UpcCallIntf.queryOfferComRelOfferByOfferId("K",packageId);//k3
        	input.put("PRODUCT_ID", productId);
        	input.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
			IDataset set=CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);
			IData retnData = new DataMap();
			retnData = (set != null && set.size()>0)?set.getData(0):new DataMap();
			if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID")))
	        {
	        	outData.put("orderId", retnData.getString("ORDER_ID"));
	        	outData.put("checkResult", "Y");	
	        	outData.put("checkMessage", "校验成功");
	        	if(IDataUtil.isNotEmpty(comRel)){
	        		for(int k=0;k<comRel.size();k++){
	        			IData comRelInfo=comRel.getData(k);
	        			IData offerInfo=new DataMap();
	        			offerInfo.put("offerId", comRelInfo.getString("OFFER_CODE"));
	        			offerInfo.put("offerName",comRelInfo.getString("OFFER_NAME"));
	        			offerInfo.put("action", "0");
	        			offerInfo.put("outcome", "0");
	        			offerInfo.put("account", "");
	        			offerInfo.put("releType", "");
	        			offerList.add(offerInfo);
	        		}
	        	}
	        	outData.put("offerList",offerList);
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	        	outData.put("checkResult", "N");	
	        	outData.put("checkMessage", "校验失败");
	        	if(IDataUtil.isNotEmpty(comRel)){
	        		for(int k=0;k<comRel.size();k++){
	        			IData comRelInfo=comRel.getData(k);
	        			IData offerInfo=new DataMap();
	        			offerInfo.put("offerId", comRelInfo.getString("OFFER_CODE"));
	        			offerInfo.put("offerName",comRelInfo.getString("OFFER_NAME"));
	        			offerInfo.put("action", "0");
	        			offerInfo.put("outcome", "1");
	        			offerInfo.put("account", "");
	        			offerInfo.put("releType", "");
	        			offerList.add(offerInfo);
	        		}
	        	}
	        	outData.put("offerList",offerList);
	            result = prepareOutResult(1,"营销活动订单预校验失败！",outData);	
		    }
	        return result;
        }        
        catch (Exception e)
        {	
        	outData.put("checkResult", "N");	
        	outData.put("checkMessage", e.getMessage());
        	if(IDataUtil.isNotEmpty(comRel)){
        		for(int k=0;k<comRel.size();k++){
        			IData comRelInfo=comRel.getData(k);
        			IData offerInfo=new DataMap();
        			offerInfo.put("offerId", comRelInfo.getString("OFFER_CODE"));
        			offerInfo.put("offerName",comRelInfo.getString("OFFER_NAME"));
        			offerInfo.put("action", "0");
        			offerInfo.put("outcome", "1");
        			offerInfo.put("account", e.getMessage());
        			offerInfo.put("releType", "");
        			offerList.add(offerInfo);
        		}
        	}
        	outData.put("offerList",offerList);
        	result = prepareOutResult(1,"营销活动订单预校验异常:"+e.getMessage(),outData);            
            return result;
        }
    }
    
    public IData submitCampaignsOrder(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
    	IDataset offerList = new DatasetList();
		IDataset comRel = new DatasetList();
    	try{
	    	
	    	String packageId = "";String action ="";
	    	String busiCode = getParams(param).getString("userMobile", "");
	    	IDataset packageList = getDatasets(getParams(param),"packageList");
	    	if(!IDataUtil.isEmpty(packageList)){
		    	 packageId = packageList.getData(0).getString("packageId", "");
		    	 action = packageList.getData(0).getString("action", "");
	    	}
	    	else
	    	{
	    		result = prepareOutResult(1,"营销活动订单提交,packageList不能为空！",outData);
	    	}
	    	if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if("".equals(packageId))
	        {        		            
	            result = prepareOutResult(1,"packageId不能为空",outData);
	            return result;
	        }
        	IData input = new DataMap();
        	input.put("SERIAL_NUMBER", busiCode);
        	input.put("PACKAGE_ID", packageId);
        	String productId = "";
        	IDataset catalogInfo = UpcCall.qryCatalogByOfferId(packageId,"K");
	        if(!IDataUtil.isEmpty(catalogInfo)){
	        	productId = catalogInfo.getData(0).getString("CATALOG_ID", "");
	        }
	        comRel = UpcCallIntf.queryOfferComRelOfferByOfferId("K",packageId);//k3
        	input.put("PRODUCT_ID", productId);
			IDataset set=CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);
			IData retnData = new DataMap();
			retnData = (set != null && set.size()>0)?set.getData(0):new DataMap();
			if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID")))
	        {
	        	outData.put("orderId", retnData.getString("ORDER_ID"));
	        	outData.put("checkResult", "Y");
	        	outData.put("checkMessage", "");
	        	if(IDataUtil.isNotEmpty(comRel)){
	        		for(int k=0;k<comRel.size();k++){
	        			IData comRelInfo=comRel.getData(k);
	        			IData offerInfo=new DataMap();
	        			offerInfo.put("offerId", comRelInfo.getString("OFFER_CODE"));
	        			offerInfo.put("offerName",comRelInfo.getString("OFFER_NAME"));
	        			offerInfo.put("action", "0");
	        			offerInfo.put("outcome", "0");
	        			offerInfo.put("account", "");
	        			offerInfo.put("releType", "");
	        			offerList.add(offerInfo);
	        		}
	        	}
	        	outData.put("offerList",offerList);
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	        	outData.put("checkResult", "N");
	        	outData.put("checkMessage","");
	        	if(IDataUtil.isNotEmpty(comRel)){
	        		for(int k=0;k<comRel.size();k++){
	        			IData comRelInfo=comRel.getData(k);
	        			IData offerInfo=new DataMap();
	        			offerInfo.put("offerId", comRelInfo.getString("OFFER_CODE"));
	        			offerInfo.put("offerName",comRelInfo.getString("OFFER_NAME"));
	        			offerInfo.put("action", "0");
	        			offerInfo.put("outcome", "1");
	        			offerInfo.put("account", "");
	        			offerInfo.put("releType", "");
	        			offerList.add(offerInfo);
	        		}
	        	}
	        	outData.put("offerList",offerList);
	            result = prepareOutResult(1,"营销活动订单提交失败！",outData);	
		    }
	        return result;
        }        
        catch (Exception e)
        {	
        	e.printStackTrace();
        	outData.put("checkResult", "N");
        	outData.put("checkMessage", e.getMessage());
        	if(IDataUtil.isNotEmpty(comRel)){
        		for(int k=0;k<comRel.size();k++){
        			IData comRelInfo=comRel.getData(k);
        			IData offerInfo=new DataMap();
        			offerInfo.put("offerId", comRelInfo.getString("OFFER_CODE"));
        			offerInfo.put("offerName",comRelInfo.getString("OFFER_NAME"));
        			offerInfo.put("action", "0");
        			offerInfo.put("outcome", "1");
        			offerInfo.put("account", e.getMessage());
        			offerInfo.put("releType", "");
        			offerList.add(offerInfo);
        		}
        	}
        	outData.put("offerList",offerList);
        	result = prepareOutResult(1,"营销活动订单提交异常:"+e.getMessage(),outData);            
            return result;
        }
    }
    
    public IData queryPrestoreReturnInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
    	IDataset ids = new DatasetList();
        String busiCode = param.getString("userMobile", "");
        String startDate = param.getString("startDate", "");
        String endDate = param.getString("endDate", "");
        String packageId = param.getString("packageId", "");
        String packageName = param.getString("packageName", "");
        String batchId = param.getString("batchId", "");
        String batchName = param.getString("batchName", "");
        String state = param.getString("state", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(startDate))
        {
        	startDate = getParams(param).getString("startDate", "");
        }
        if ( "".equals(endDate))
        {
        	endDate = getParams(param).getString("endDate", "");
        }
        if ( "".equals(packageId))
        {
        	packageId = getParams(param).getString("packageId", "");
        }
        if ( "".equals(packageName))
        {
        	packageName = getParams(param).getString("packageName", "");
        }
        if ( "".equals(batchId))
        {
        	batchId = getParams(param).getString("batchId", "");
        }
        if ( "".equals(batchName))
        {
        	batchName = getParams(param).getString("batchName", "");
        }
        if ( "".equals(state))
        {
        	state = getParams(param).getString("state", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        IDataset userDepositInfo = queryUserDepositInfo(busiCode,ucaData.getUserId());
	        IDataset userSaleActives = queryUserSaleActives( ucaData.getUserId(), batchId, batchName, packageId,
	    			 packageName, startDate,  endDate,  state);
	        if(IDataUtil.isNotEmpty(userDepositInfo) && IDataUtil.isNotEmpty(userSaleActives)){
	        	for (int i =0;i <userDepositInfo.size();i++ )
	        	{
		        	IData id = new DataMap();
		        	String outTradeId = userDepositInfo.getData(i).getString("OUTER_TRADE_ID", "");
		        	for (int j = 0; j < userSaleActives.size(); j++)
		        	{
			        	String relationTradeId = userSaleActives.getData(j).getString("RELATION_TRADE_ID", "");
					
		        		if (outTradeId.equals(relationTradeId))
		        		{
				        	int money = Integer.parseInt(userDepositInfo.getData(i).getString("MONEY","0"));
							int leftMoney = Integer.parseInt(userDepositInfo.getData(i).getString("LEFT_MONEY","0"));
							int limitMoney = Integer.parseInt(userDepositInfo.getData(i).getString("LIMIT_MONEY","0"));

							id.put("batchId", userSaleActives.getData(j).getString("PRODUCT_ID", ""));//活动编码
				        	id.put("batchName", userSaleActives.getData(j).getString("PRODUCT_NAME", ""));//活动名称
				        	id.put("packageId", userSaleActives.getData(j).getString("PACKAGE_ID", ""));//子活动编码
				        	id.put("packageName", userSaleActives.getData(j).getString("PACKAGE_NAME", ""));//子活动名称
				        	id.put("packageBossId", userSaleActives.getData(j).getString("PACKAGE_ID", ""));//子活动Boss编码，用于关联知识库
				        	id.put("batchBossId",userSaleActives.getData(j).getString("PRODUCT_ID", ""));//主活动Boss编码，用于关联知识库				        	
				        	id.put("returnType", "预存返还");
				        	String startDateStr = userDepositInfo.getData(i).getString("START_CYCLE_ID", "");
				        	String endDateStr = userDepositInfo.getData(i).getString("END_CYCLE_ID", "");
				        	if (!"".equals(startDateStr))
				        	{
				        		id.put("startMonth", chgFormat(startDateStr,"yyyy-MM-dd","yyyyMM"));//当前月返还的时间
				        	}
				        	else 
				        	{
				        		id.put("startMonth", "");//当前月返还的时间
				        	}
				        	if (!"".equals(endDateStr))
				        	{
				        		id.put("endMonth", chgFormat(endDateStr,"yyyy-MM-dd","yyyyMM"));//当前月返还的时间
				        	}
				        	else 
				        	{
				        		id.put("endMonth", "");//当前月返还的金额
				        	}
				        	id.put("prestoredFee", String.valueOf(money));//预存总金额
				        	id.put("returnFee", String.valueOf(money-leftMoney));//已返总金额
				        	id.put("remainFee", String.valueOf(leftMoney));//剩余总金额	
				        	id.put("sumMonth", userDepositInfo.getData(i).getString("MONTHS", ""));//总月数
				        	id.put("returnMonth", String.valueOf((money - leftMoney) / limitMoney));//当前月数
				        	id.put("remainMonth", String.valueOf(leftMoney / limitMoney));//剩余月数
				        	if("0".equals(userDepositInfo.getData(i).getString("CANCEL_TAG", "")))
				        	{
				        		id.put("returnState", "返款成功");//状态
				        	}
				        	else
				        	{
				        		id.put("returnState", "已返销");//状态
				        	}
				        	id.put("remark", userDepositInfo.getData(i).getString("PURCHASE_INFO", ""));//备注				        	
				        	//k3
				        	id.put("unpackingState", "");
				        	id.put("Ext1", "");
				        	id.put("Ext2", "");
				        	id.put("Ext3", "");
				        	id.put("Ext4", "");
				        	id.put("Ext5", "");
				        	id.put("Ext6", "");
				        	id.put("Ext7", "");
				        	id.put("Ext8", "");
				        	id.put("Ext9", "");
				        	id.put("Ext10", "");
				        	id.put("Ext11", "");
				        	id.put("Ext12", "");
				        	id.put("Ext13", "");
				        	id.put("Ext14", "");
				        	id.put("Ext15", "");
				        	
				        	IDataset detailList = new DatasetList();
				        	IData detailData = new DataMap();
				        	detailData.put("onceFee", String.valueOf(limitMoney));
				        	detailData.put("feeBefore", String.valueOf(leftMoney+limitMoney));
				        	detailData.put("feeAfter", String.valueOf(leftMoney));
				        	String returnTime = userDepositInfo.getData(i).getString("FIRED_MONTH", "");
				        	if (!"".equals(returnTime)&&!"-1".equals(returnTime))
				        	{
				        		detailData.put("returnTime", chgFormat(returnTime,"yyyyMMdd","yyyyMMddHHmmss"));//当前月返还的时间
				        	}
				        	else 
				        	{
				        		detailData.put("returnTime", "");
				        	}
				        	if("0".equals(userDepositInfo.getData(i).getString("CANCEL_TAG", "")))
				        	{
				        		detailData.put("state", "返款成功");//状态
				        	}
				        	else
				        	{
				        		detailData.put("state", "已返销");//状态
				        	}
				        	detailData.put("remark", "");
				        	detailList.add(detailData);
				        	id.put("detailList", detailList);
		
				        	ids.add(id);
		        		}
		        	}
	        	}
	        	outData.put("feeList", ids);
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户话费返还信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
  //REQ202005140037  关于账单3.0开发的需求
    public IData queryPrestoreReturnInfo_112(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
    	IDataset ids = new DatasetList();
        String busiCode = param.getString("userMobile", "");
        String startDate = param.getString("startDate", "");
        String endDate = param.getString("endDate", "");
        String packageId = param.getString("packageId", "");
        String packageName = param.getString("packageName", "");
        String batchId = param.getString("batchId", "");
        String batchName = param.getString("batchName", "");
        String state = param.getString("state", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(startDate))
        {
        	startDate = getParams(param).getString("startDate", "");
        }
        if ( "".equals(endDate))
        {
        	endDate = getParams(param).getString("endDate", "");
        }
        if ( "".equals(packageId))
        {
        	packageId = getParams(param).getString("packageId", "");
        }
        if ( "".equals(packageName))
        {
        	packageName = getParams(param).getString("packageName", "");
        }
        if ( "".equals(batchId))
        {
        	batchId = getParams(param).getString("batchId", "");
        }
        if ( "".equals(batchName))
        {
        	batchName = getParams(param).getString("batchName", "");
        }
        if ( "".equals(state))
        {
        	state = getParams(param).getString("state", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        IDataset userDepositInfo = queryUserDepositInfo(busiCode,ucaData.getUserId());
	        IDataset userSaleActives = queryUserSaleActives( ucaData.getUserId(), batchId, batchName, packageId,
	    			 packageName, startDate,  endDate,  state);
	        if(IDataUtil.isNotEmpty(userDepositInfo) && IDataUtil.isNotEmpty(userSaleActives)){
	        	for (int i =0;i <userDepositInfo.size();i++ )
	        	{
		        	IData id = new DataMap();
		        	String outTradeId = userDepositInfo.getData(i).getString("OUTER_TRADE_ID", "");
		        	for (int j = 0; j < userSaleActives.size(); j++)
		        	{
			        	String relationTradeId = userSaleActives.getData(j).getString("RELATION_TRADE_ID", "");
					
		        		if (outTradeId.equals(relationTradeId))
		        		{
				        	int money = Integer.parseInt(userDepositInfo.getData(i).getString("MONEY","0"));
							int leftMoney = Integer.parseInt(userDepositInfo.getData(i).getString("LEFT_MONEY","0"));
							int limitMoney = Integer.parseInt(userDepositInfo.getData(i).getString("LIMIT_MONEY","0"));
							int thisMonthReturnFee = Integer.parseInt(userDepositInfo.getData(i).getString("THIS_MONTH_RETURNFEE","0"));

							id.put("batchId", userSaleActives.getData(j).getString("PRODUCT_ID", ""));//活动编码
				        	id.put("batchName", userSaleActives.getData(j).getString("PRODUCT_NAME", ""));//活动名称
				        	id.put("packageId", userSaleActives.getData(j).getString("PACKAGE_ID", ""));//子活动编码
				        	id.put("packageName", userSaleActives.getData(j).getString("PACKAGE_NAME", ""));//子活动名称
				        	id.put("packageBossId", userSaleActives.getData(j).getString("PACKAGE_ID", ""));//子活动Boss编码，用于关联知识库
				        	id.put("batchBossId",userSaleActives.getData(j).getString("PRODUCT_ID", ""));//主活动Boss编码，用于关联知识库				        	
				        	id.put("returnType", "预存返还");
				        	String startDateStr = userDepositInfo.getData(i).getString("START_CYCLE_ID", "");
				        	String endDateStr = userDepositInfo.getData(i).getString("END_CYCLE_ID", "");
				        	if (!"".equals(startDateStr))
				        	{
				        		id.put("startMonth", chgFormat(startDateStr,"yyyy-MM-dd","yyyyMM"));//当前月返还的时间
				        	}
				        	else 
				        	{
				        		id.put("startMonth", "");//当前月返还的时间
				        	}
				        	if (!"".equals(endDateStr))
				        	{
				        		id.put("endMonth", chgFormat(endDateStr,"yyyy-MM-dd","yyyyMM"));//当前月返还的时间
				        	}
				        	else 
				        	{
				        		id.put("endMonth", "");//当前月返还的金额
				        	}
				        	id.put("prestoredFee", String.valueOf(money));//预存总金额
				        	id.put("returnFee", String.valueOf(money-leftMoney));//已返总金额
				        	id.put("remainFee", String.valueOf(leftMoney));//剩余总金额	
				        	id.put("thisMonthReturnFee", String.valueOf(thisMonthReturnFee));//本月已返还金额
				        	id.put("sumMonth", userDepositInfo.getData(i).getString("MONTHS", ""));//总月数
				        	id.put("returnMonth", String.valueOf((money - leftMoney) / limitMoney));//当前月数
				        	id.put("remainMonth", String.valueOf(leftMoney / limitMoney));//剩余月数
				        	if("0".equals(userDepositInfo.getData(i).getString("CANCEL_TAG", "")))
				        	{
				        		id.put("returnState", "返款成功");//状态
				        	}
				        	else
				        	{
				        		id.put("returnState", "已返销");//状态
				        	}
				        	id.put("remark", userDepositInfo.getData(i).getString("PURCHASE_INFO", ""));//备注				        	
				        	//k3
				        	id.put("unpackingState", "");
				        	id.put("Ext1", "");
				        	id.put("Ext2", "");
				        	id.put("Ext3", "");
				        	id.put("Ext4", "");
				        	id.put("Ext5", "");
				        	id.put("Ext6", "");
				        	id.put("Ext7", "");
				        	id.put("Ext8", "");
				        	id.put("Ext9", "");
				        	id.put("Ext10", "");
				        	id.put("Ext11", "");
				        	id.put("Ext12", "");
				        	id.put("Ext13", "");
				        	id.put("Ext14", "");
				        	id.put("Ext15", "");
				        	
				        	IDataset detailList = new DatasetList();
				        	IData detailData = new DataMap();
				        	detailData.put("onceFee", String.valueOf(limitMoney));
				        	detailData.put("feeBefore", String.valueOf(leftMoney+limitMoney));
				        	detailData.put("feeAfter", String.valueOf(leftMoney));
				        	String returnTime = userDepositInfo.getData(i).getString("FIRED_MONTH", "");
				        	if (!"".equals(returnTime)&&!"-1".equals(returnTime))
				        	{
				        		detailData.put("returnTime", chgFormat(returnTime,"yyyyMMdd","yyyyMMddHHmmss"));//当前月返还的时间
				        	}
				        	else 
				        	{
				        		detailData.put("returnTime", "");
				        	}
				        	if("0".equals(userDepositInfo.getData(i).getString("CANCEL_TAG", "")))
				        	{
				        		detailData.put("state", "返款成功");//状态
				        	}
				        	else
				        	{
				        		detailData.put("state", "已返销");//状态
				        	}
				        	detailData.put("remark", "");
				        	detailList.add(detailData);
				        	id.put("detailList", detailList);
		
				        	ids.add(id);
		        		}
		        	}
	        	}
	        	outData.put("feeList", ids);
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户话费返还信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData querySMSSendInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        String startDate = param.getString("startDate", "");
        String endDate = param.getString("endDate", "");
        String sendType = param.getString("sendType", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        	startDate = getParams(param).getString("startDate", "");
        	endDate = getParams(param).getString("endDate", "");
        	sendType = getParams(param).getString("sendType", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if("".equals(startDate))
	        {        		            
	            result = prepareOutResult(1,"startDate不能为空",outData);
	            return result;
	        }
	        if("".equals(endDate))
	        {        		            
	            result = prepareOutResult(1,"endDate不能为空",outData);
	            return result;
	        }
	        String fStartDate = SysDateMgr.date2String(SysDateMgr.string2Date(startDate.substring(0, 8), SysDateMgr.PATTERN_TIME_YYYYMMDD), SysDateMgr.PATTERN_STAND_YYYYMMDD);
	        String fEndDate = SysDateMgr.date2String(SysDateMgr.string2Date(endDate.substring(0, 8), SysDateMgr.PATTERN_TIME_YYYYMMDD), SysDateMgr.PATTERN_STAND_YYYYMMDD);
        	String theStartMonth = fStartDate.substring(5, 7);
	        if(SysDateMgr.monthInterval(fEndDate, fStartDate)>1)
	        {
	        	result = prepareOutResult(1,"时间范围不能跨月",outData);
	            return result;
	        }
	        if("".equals(sendType))
	        {        		            
	            result = prepareOutResult(1,"sendType不能为空",outData);
	            return result;
	        }
	        //查询上行
	        if ("1".equals(sendType))
	        {
	        	//0. 查询TF_B_TRADELOG_SMS表中的上行短信
	        	//IDataset smsInfos = QuerySmsQry.querySms0SameMonth(fStartDate, fEndDate, busiCode, theStartMonth, null);
		        IData input = new DataMap();
	        	input.put("SERIAL_NUMBER",busiCode);
		        input.put("START_DATE",fStartDate);
		        input.put("END_DATE",fEndDate);
		        input.put("QUERY_MODE","0");
				IDataset smsInfos = CSAppCall.call("SS.QuerySmsSVC.querySms", input);
	        	
	        	IDataset ids = new DatasetList();
		        if(!IDataUtil.isEmpty(smsInfos)){
		        	for (int i =0;i <smsInfos.size();i++ )
		        	{
			        	IData id = new DataMap();
			        	id.put("userMobile", busiCode);
			        	id.put("sendPort", smsInfos.getData(i).getString("PARA_AIM_NUMBER", ""));
			        	id.put("sendType", "上行");
			        	id.put("busiType", "服务请求");	

			        	
			        	id.put("smsSendTime",SysDateMgr.date2String((SysDateMgr.string2Date(smsInfos.getData(i).getString("PARA_CODE7", ""), 
			        					SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
			        	id.put("smsContent", smsInfos.getData(i).getString("PARA_CODE3", ""));
			        	//id.put("staffId", getVisit().getStaffId());
			        	id.put("staffId", "ITFSM000");
			        	id.put("remark", smsInfos.getData(i).getString("REMARK", ""));
			        	//k3
			        	String sendStatus = smsInfos.getData(i).getString("SEND_CODE", "");
			        	if("0".equals(sendStatus)){
				        	id.put("failReason","");
			        	}else{
			        		id.put("failReason",smsInfos.getData(i).getString("RSP_RESULT", ""));
			        	}
		        		id.put("sendStatus",smsInfos.getData(i).getString("RSP_RESULT", ""));
			        	id.put("sendTime",SysDateMgr.date2String((SysDateMgr.string2Date(smsInfos.getData(i).getString("SEND_TIME", ""), 
	        					SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND));
			        	id.put("responseSms",smsInfos.getData(i).getString("PARA_CODE5", ""));
			        	ids.add(id);
		        	}
		        	outData.put("smsList", ids);
		        	outData.put("resultRows", ids.size());
			        result = prepareOutResult(0,"",outData);	
		        }
		        else
		        {	
		            result = prepareOutResult(1,"无法查询到用户短信信息！",outData);	
			    }
	        }
	        //查询下行
	        if ("2".equals(sendType)){
		        //1. 查询TI_OH_SMS表中短信
				//IDataset smsInfos = QuerySmsQry.querySms10086SameMonth(fStartDate, fEndDate, busiCode, theStartMonth, null);
	        	IData input = new DataMap();
	        	input.put("SERIAL_NUMBER",busiCode);
		        input.put("START_DATE",fStartDate);
		        input.put("END_DATE",fEndDate);
		        input.put("QUERY_MODE","1");
				IDataset smsInfos = CSAppCall.call("SS.QuerySmsSVC.querySms", input);
	        	IDataset ids = new DatasetList();
		        if(!IDataUtil.isEmpty(smsInfos)){
		        	for (int i =0;i <smsInfos.size();i++ )
		        	{
			        	IData id = new DataMap();
			        	id.put("userMobile", busiCode);
			        	id.put("sendPort", smsInfos.getData(i).getString("PARA_CODE9", ""));
			        	id.put("sendType", "下行");
			        	String paraCode13 = smsInfos.getData(i).getString("PARA_CODE13", "");
			        	if ("01".equals(paraCode13)) 
			        	{
				        	id.put("busiType", "账管催缴");
	
			        	}
			        	else if ("02".equals(paraCode13)) 
			        	{
				        	id.put("busiType", "业务通知");
	
			        	}
			        	else
			        	{
			        		id.put("busiType", "");
	
			        	}
			        	
			        	id.put("smsSendTime",SysDateMgr.date2String((SysDateMgr.string2Date(smsInfos.getData(i).getString("PARA_CODE6", ""), 
			        					SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
			        	id.put("smsContent", smsInfos.getData(i).getString("PARA_CODE3", ""));
			        	id.put("staffId", smsInfos.getData(i).getString("PARA_CODE5", ""));
			        	id.put("remark", smsInfos.getData(i).getString("PARA_CODE10", ""));
			        	//k3
			        	id.put("sendStatus", smsInfos.getData(i).getString("PARA_CODE7", ""));
			        	id.put("sendTime",SysDateMgr.date2String((SysDateMgr.string2Date(smsInfos.getData(i).getString("PARA_CODE6", ""), 
	        					SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND));
			        	id.put("responseSms","");
			        	if(!"已处理".equals(smsInfos.getData(i).getString("PARA_CODE7", ""))){
			        		id.put("failReason",smsInfos.getData(i).getString("PARA_CODE7", ""));
			        	}else{
			        		id.put("failReason","");
			        	}
			        	ids.add(id);
		        	}
		        }
		        //2. 查询TF_B_TRADELOG_SMS表中的下行短信
		        //IDataset smsInfos2 = QuerySmsQry.querySms0SameMonth(fStartDate, fEndDate, busiCode, theStartMonth, null);
		        input.put("QUERY_MODE","0");
				IDataset smsInfos2 = CSAppCall.call("SS.QuerySmsSVC.querySms", input);
		        if(!IDataUtil.isEmpty(smsInfos2)){
		        	for (int i =0;i <smsInfos2.size();i++ )
		        	{
			        	IData id = new DataMap();
			        	id.put("userMobile", busiCode);
			        	id.put("sendPort", smsInfos2.getData(i).getString("PARA_CODE4", ""));
			        	id.put("sendType", "下行");
			        	id.put("busiType", "服务请求");	

			        	
			        	id.put("smsSendTime",SysDateMgr.date2String((SysDateMgr.string2Date(smsInfos2.getData(i).getString("PARA_CODE6", ""), 
			        					SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
			        	id.put("smsContent", smsInfos2.getData(i).getString("PARA_CODE5", ""));
			        	//id.put("staffId", getVisit().getStaffId());
			        	id.put("staffId", "ITFSM000");
			        	id.put("remark", smsInfos2.getData(i).getString("REMARK", ""));
			        	//k3
			        	String sendStatus = smsInfos2.getData(i).getString("SEND_CODE", "");
			        	if("0".equals(sendStatus)){
				        	id.put("failReason","");
			        	}else{
			        		id.put("failReason",smsInfos2.getData(i).getString("RSP_RESULT", ""));
			        	}
			        	id.put("sendStatus",smsInfos2.getData(i).getString("RSP_RESULT", ""));
			        	id.put("sendTime",SysDateMgr.date2String((SysDateMgr.string2Date(smsInfos2.getData(i).getString("SEND_TIME", ""), 
	        					SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND));
			        	id.put("responseSms", smsInfos2.getData(i).getString("PARA_CODE3", ""));
			        	ids.add(id);
		        	}
		        }
		        outData.put("smsList", ids);
		        outData.put("resultRows", ids.size());
		        result = prepareOutResult(0,"",outData);	
		        
	        }
	        
	        //查询全部
	        if ("0".equals(sendType)){
		        //1. 查询TI_OH_SMS表中短信
				//IDataset smsInfos = QuerySmsQry.querySms10086SameMonth(fStartDate, fEndDate, busiCode, theStartMonth, null);
	        	IData input = new DataMap();
	        	input.put("SERIAL_NUMBER",busiCode);
		        input.put("START_DATE",fStartDate);
		        input.put("END_DATE",fEndDate);
		        input.put("QUERY_MODE","1");
				IDataset smsInfos = CSAppCall.call("SS.QuerySmsSVC.querySms", input);
	        	IDataset ids = new DatasetList();
		        if(!IDataUtil.isEmpty(smsInfos)){
		        	for (int i =0;i <smsInfos.size();i++ )
		        	{
			        	IData id = new DataMap();
			        	id.put("userMobile", busiCode);
			        	id.put("sendPort", smsInfos.getData(i).getString("PARA_CODE9", ""));
			        	id.put("sendType", "下行");
			        	String paraCode13 = smsInfos.getData(i).getString("PARA_CODE13", "");
			        	if ("01".equals(paraCode13)) 
			        	{
				        	id.put("busiType", "账管催缴");
	
			        	}
			        	else if ("02".equals(paraCode13)) 
			        	{
				        	id.put("busiType", "业务通知");
	
			        	}
			        	else
			        	{
			        		id.put("busiType", "");
	
			        	}
			        	
			        	id.put("smsSendTime",SysDateMgr.date2String((SysDateMgr.string2Date(smsInfos.getData(i).getString("PARA_CODE6", ""), 
			        					SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
			        	id.put("smsContent", smsInfos.getData(i).getString("PARA_CODE3", ""));
			        	id.put("staffId", smsInfos.getData(i).getString("PARA_CODE5", ""));
			        	id.put("remark", smsInfos.getData(i).getString("PARA_CODE10", ""));
			        	
			        	//k3
						id.put("sendStatus", smsInfos.getData(i).getString("PARA_CODE7", ""));
				        id.put("sendTime",SysDateMgr.date2String((SysDateMgr.string2Date(smsInfos.getData(i).getString("PARA_CODE6", ""), 
		        					SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND));
				        id.put("responseSms","");
				        if(!"已处理".equals(smsInfos.getData(i).getString("PARA_CODE7", ""))){
				        	id.put("failReason",smsInfos.getData(i).getString("PARA_CODE7", ""));
				        }else{
				        	id.put("failReason","");
				        }
			        	ids.add(id);
		        	}
		        }
		        //2. 查询TF_B_TRADELOG_SMS表中的下行短信
		        //IDataset smsInfos2 = QuerySmsQry.querySms0SameMonth(fStartDate, fEndDate, busiCode, theStartMonth, null);
		        input.put("QUERY_MODE","0");
				IDataset smsInfos2 = CSAppCall.call("SS.QuerySmsSVC.querySms", input);
		        if(!IDataUtil.isEmpty(smsInfos2)){
		        	for (int i =0;i <smsInfos2.size();i++ )
		        	{
			        	IData id = new DataMap();
			        	id.put("userMobile", busiCode);
			        	id.put("sendPort", smsInfos2.getData(i).getString("PARA_CODE4", ""));
			        	id.put("sendType", "下行");
			        	id.put("busiType", "服务请求");	

			        	
			        	id.put("smsSendTime",SysDateMgr.date2String((SysDateMgr.string2Date(smsInfos2.getData(i).getString("PARA_CODE6", ""), 
			        					SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
			        	id.put("smsContent", smsInfos2.getData(i).getString("PARA_CODE5", ""));
			        	//id.put("staffId", getVisit().getStaffId());
			        	id.put("staffId", "ITFSM000");
			        	id.put("remark", smsInfos2.getData(i).getString("REMARK", ""));
			        	//k3
			        	String sendStatus = smsInfos2.getData(i).getString("SEND_CODE", "");
			        	if("0".equals(sendStatus)){
				        	id.put("failReason","");
			        	}else{
			        		id.put("failReason",smsInfos2.getData(i).getString("RSP_RESULT", ""));
			        	}
			        	id.put("sendStatus",smsInfos2.getData(i).getString("RSP_RESULT", ""));
			        	id.put("sendTime",SysDateMgr.date2String((SysDateMgr.string2Date(smsInfos2.getData(i).getString("SEND_TIME", ""), 
	        					SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND));
			        	id.put("responseSms", smsInfos2.getData(i).getString("PARA_CODE3", ""));
			        	ids.add(id);
		        	}
		        }
		        
		        //3. 查询TF_B_TRADELOG_SMS表中的上行短信
		        input.put("QUERY_MODE","0");
				IDataset smsInfos3 = CSAppCall.call("SS.QuerySmsSVC.querySms", input);
	        	//IDataset smsInfos3 = QuerySmsQry.querySms0SameMonth(fStartDate, fEndDate, busiCode, theStartMonth, null);
		        if(!IDataUtil.isEmpty(smsInfos3)){
		        	for (int i =0;i <smsInfos3.size();i++ )
		        	{
			        	IData id = new DataMap();
			        	id.put("userMobile", busiCode);
			        	id.put("sendPort", smsInfos3.getData(i).getString("PARA_AIM_NUMBER", ""));
			        	id.put("sendType", "上行");
			        	id.put("busiType", "服务请求");	

			        	
			        	id.put("smsSendTime",SysDateMgr.date2String((SysDateMgr.string2Date(smsInfos3.getData(i).getString("PARA_CODE7", ""), 
			        					SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
			        	id.put("smsContent", smsInfos3.getData(i).getString("PARA_CODE3", ""));
			        	//id.put("staffId", getVisit().getStaffId());
			        	id.put("staffId", "ITFSM000");
			        	id.put("remark", smsInfos3.getData(i).getString("REMARK", ""));
			        	//k3
			        	String sendStatus = smsInfos3.getData(i).getString("SEND_CODE", "");
			        	if("0".equals(sendStatus)){
				        	id.put("failReason","");
			        	}else{
			        		id.put("failReason",smsInfos3.getData(i).getString("RSP_RESULT", ""));
			        	}
		        		id.put("sendStatus",smsInfos3.getData(i).getString("RSP_RESULT", ""));
			        	id.put("sendTime",SysDateMgr.date2String((SysDateMgr.string2Date(smsInfos3.getData(i).getString("SEND_TIME", ""), 
	        					SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND));
			        	id.put("responseSms",smsInfos3.getData(i).getString("PARA_CODE5", ""));
			        	ids.add(id);
		        	}	
		        }
		        
		        outData.put("smsList", ids);
		        outData.put("resultRows", ids.size());
		        result = prepareOutResult(0,"",outData);	
		        
	        }
	        
	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData authCustRealName(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();

        String idType = param.getString("idType", "");
        String idValue = param.getString("idValue", "");
        String idName = param.getString("idName", "");
        String idAddr = param.getString("idAddr", "");

        if ( "".equals(idType))
        {
        	idType = getParams(param).getString("idType", "");
        }
        if ( "".equals(idValue))
        {
        	idValue = getParams(param).getString("idValue", "");
        }
        if ( "".equals(idName))
        {
        	idName = getParams(param).getString("idName", "");
        }
        if ( "".equals(idAddr))
        {
        	idAddr = getParams(param).getString("idAddr", "");
        }
        try{
	        if("".equals(idType))
	        {        		            
	            result = prepareOutResult(1,"idType不能为空",outData);
	            return result;
	        }
	        if("".equals(idValue))
	        {        		            
	            result = prepareOutResult(1,"idValue不能为空",outData);
	            return result;
	        }
	        if("".equals(idName))
	        {        		            
	            result = prepareOutResult(1,"idName不能为空",outData);
	            return result;
	        } 	        
	        if("".equals(idAddr))
	        {        		            
	            result = prepareOutResult(1,"idAddr不能为空",outData);
	            return result;
	        }
	        IData params = new DataMap();
	        params.put("PSPT_TYPE_CODE", HandleIdType(idType));
	        params.put("PSPT_ID", idValue);
	        IDataset custPersonInfo = CustPersonInfoQry.qryPerInfoByPsptId(params, null);
	        if ("00".equals(idType) && IDataUtil.isEmpty(custPersonInfo))
	        {
	        	params.put("PSPT_TYPE_CODE", "1");
		        custPersonInfo = CustPersonInfoQry.qryPerInfoByPsptId(params, null);
	        }

	        if(!IDataUtil.isEmpty(custPersonInfo))
	        {
	        	String custId = custPersonInfo.getData(0).getString("CUST_ID", "");
	        	String isRealName = CustomerInfoQry.qryCustInfo(custId).getString("IS_REAL_NAME", "");
	        	if ("1".equals(isRealName))
	        	{	
		        	if (idName.equals(custPersonInfo.getData(0).getString("CUST_NAME", "")) &&
		        			idAddr.equals(custPersonInfo.getData(0).getString("PSPT_ADDR", "")))
		        	{
		        		outData.put("realNameInfo", "实名制客户");
				        result = prepareOutResult(0,"",outData);	
		        	}
		        	else
		        	{
		        		outData.put("realNameInfo", "实名制客户(客户资料校验不通过)");
			        	result = prepareOutResult(1,"客户资料校验不通过"+idType,outData);	
		        	}
	        	}
	        	else
	        	{
	        		if (idName.equals(custPersonInfo.getData(0).getString("CUST_NAME", "")) &&
		        			idAddr.equals(custPersonInfo.getData(0).getString("PSPT_ADDR", "")))
		        	{
		        		outData.put("realNameInfo", "非实名制客户");
				        result = prepareOutResult(0,"",outData);	
		        	}
		        	else
		        	{
		        		outData.put("realNameInfo", "非实名制客户(客户资料校验不通过)");
			        	result = prepareOutResult(1,"客户资料校验不通过"+idType,outData);	
		        	}
	        	}
	        		

	        }
	        else
	        {	
	            outData.put("realNameInfo", "非实名制客户(无客户资料)");
	        	result = prepareOutResult(1,"无客户资料"+idType,outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	outData.put("realNameInfo", "非实名制客户(查询客户资料异常)");
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData queryCampaignGoodsInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
    	IDataset ids = new DatasetList();
        String busiCode = param.getString("userMobile", "");
        String beginDate = param.getString("beginDate", "");
        String endDate = param.getString("endDate", "");
        String packageId = param.getString("packageId", "");
        String packageName = param.getString("packageName", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        }
        if ( "".equals(beginDate))
        {
        	beginDate = getParams(param).getString("beginDate", "");
        }
        if ( "".equals(endDate))
        {
        	endDate = getParams(param).getString("endDate", "");
        }
        if ( "".equals(packageId))
        {
        	packageId = getParams(param).getString("packageId", "");
        }
        if ( "".equals(packageName))
        {
        	packageName = getParams(param).getString("packageName", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);

	        IDataset userSaleGoodsInfo = queryUserSaleGoodsInfo(ucaData.getUserId(),beginDate,endDate,packageId,packageName);
	        if(!IDataUtil.isEmpty(userSaleGoodsInfo)){
	        	for (int i =0;i <userSaleGoodsInfo.size();i++ )
	        	{
		        	IData id = new DataMap();
		        	id.put("packageId", userSaleGoodsInfo.getData(i).getString("PACKAGE_ID", ""));
		        	id.put("packageName", userSaleGoodsInfo.getData(i).getString("PACKAGE_NAME", ""));
		        	id.put("materialName", userSaleGoodsInfo.getData(i).getString("GOODS_NAME", ""));
		        	id.put("materialNum", userSaleGoodsInfo.getData(i).getString("GOODS_NUM", ""));
		        	if("0".equals(userSaleGoodsInfo.getData(i).getString("GOODS_STATE", "")))
		        	{
		        		id.put("materialStatus", "已领取");
		        	}
		        	else{
		        		id.put("materialStatus", "已返销");
		        	}
		        	id.put("drawTime", SysDateMgr.date2String((SysDateMgr.string2Date(userSaleGoodsInfo.getData(i).getString("ACCEPT_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
		        	id.put("remark", userSaleGoodsInfo.getData(i).getString("REMARK", ""));
		        	ids.add(i, id);
	        	}
	        	outData.put("materiaList", ids);
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户实物信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
    
    public IData queryCampaignBindInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String busiCode = param.getString("userMobile", "");
        String beginDate = param.getString("beginDate", "");
        String endDate = param.getString("endDate", "");
        String packageId = param.getString("packageId", "");
        String packageName = param.getString("packageName", "");
        if ( "".equals(busiCode))
        {
        	busiCode = getParams(param).getString("userMobile", "");
        	beginDate = getParams(param).getString("beginDate", "");
        	endDate = getParams(param).getString("endDate", "");
        	packageId = getParams(param).getString("packageId", "");
        	packageName = getParams(param).getString("packageName", "");
        }
        try{
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if("".equals(beginDate))
	        {        		            
	            result = prepareOutResult(1,"beginDate不能为空",outData);
	            return result;
	        }
	        if("".equals(endDate))
	        {        		            
	            result = prepareOutResult(1,"endDate不能为空",outData);
	            return result;
	        }
	        UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	    	IDataset ids = new DatasetList();
	        IDataset userSaleGoodsInfo = new DatasetList();
	        IData inparam = new DataMap();
			inparam.put("USER_ID", ucaData.getUserId());
			inparam.put("BEGIN_DATE", beginDate);
			inparam.put("END_DATE", endDate);
			inparam.put("PACKAGE_ID", packageId);
			inparam.put("PACKAGE_NAME", packageName);
	        userSaleGoodsInfo = getGJHDGoodsInfoByUserId(inparam);

	        if(!IDataUtil.isEmpty(userSaleGoodsInfo)){
	        	for (int i =0;i <userSaleGoodsInfo.size();i++ )
	        	{
		        	IData id = new DataMap();
		        	id.put("packageId", userSaleGoodsInfo.getData(i).getString("PACKAGE_ID", ""));
		        	id.put("packageName", userSaleGoodsInfo.getData(i).getString("PACKAGE_NAME", ""));
		        	id.put("terminalType", userSaleGoodsInfo.getData(i).getString("DEVICE_MODEL", ""));
		        	id.put("appointImei", userSaleGoodsInfo.getData(i).getString("RES_CODE", ""));
		        	id.put("bindTime", userSaleGoodsInfo.getData(i).getString("ACCEPT_DATE", ""));
		        	id.put("currentImei", "");//当前使用手机的串号
		        	id.put("bindType", "");//合约要求捆绑流量或者是通话
		        	String goodsState = userSaleGoodsInfo.getData(i).getString("GOODS_STATE", "");
		        	if("0".equals(goodsState))
		        	{
		        		id.put("bindState","成功");
		        	}
		        	else
		        	{
		        		id.put("bindState","失败");
		        	}	
		        	id.put("sumMonth", userSaleGoodsInfo.getData(i).getString("MONTHS", ""));//合约总月数
		        	String startDate = userSaleGoodsInfo.getData(i).getString("RSRV_DATE1", "");
		        	String sysdate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD);
		        	int months = Integer.parseInt(userSaleGoodsInfo.getData(i).getString("MONTHS", ""));
		        	int edBindMonth = SysDateMgr.monthIntervalNoAbs(startDate,sysdate);
		        	if (edBindMonth <= 0)
		        	{
		        		id.put("edBindMonth", "0");//已经满足的月数
			        	id.put("remainMonth", months);//还需捆绑的月数
		        	}
		        	else {
			        	int remainMonth = months - edBindMonth;	        	
			        	id.put("edBindMonth", edBindMonth);//已经满足的月数
			        	id.put("remainMonth", remainMonth);//还需捆绑的月数
		        	}
		        	ids.add(i, id);
	        	}
	        	outData.put("bindInfoList", ids);
		        result = prepareOutResult(0,"",outData);	
	        }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户营销活动捆绑信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }

    public IData sendRandomPwd(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String serialNumber = param.getString("userMobile", "");
        if ( "".equals(serialNumber))
        {
        	serialNumber = getParams(param).getString("userMobile", "");
        }
        try{
        	       	
	        if("".equals(serialNumber))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
			
			int second = 1800;// 验证码有效时间 单位：秒
			int allowVerifyCount=3;//默认可以验证次数
			
			//生成短信验证码
			String verifyCode = RandomStringUtils.randomNumeric(6);
			String msg = "【中国移动】 尊敬的用户，您的验证码为："+verifyCode+"，该验证码有"+second/60+"分钟内有效，请及时填写。";//短信内容
			//发送短信通知
			IData inparam = new DataMap();
	        inparam.put("NOTICE_CONTENT", msg);
	        inparam.put("RECV_OBJECT", serialNumber);
	        inparam.put("RECV_ID", serialNumber);
	        inparam.put("SMS_PRIORITY", "5000");
	        inparam.put("REFER_STAFF_ID", getVisit().getStaffId());
	        inparam.put("REFER_DEPART_ID", getVisit().getDepartId());
	        inparam.put("REMARK", "能力开放平台接口申请随机验证码");
	        SmsSend.insSms(inparam);
	        
	        IData cacheData = new DataMap();
	        cacheData.put("VERIFYCODE", verifyCode);
	        cacheData.put("CREATETIME", SysDateMgr.getSysTime());//发送验证码时间
	        cacheData.put("FAILTIME", SysDateMgr.getOtherSecondsOfSysDate(second));//验证码失效时间
	        cacheData.put("VERIFYCOUNT", 0);//验证次数
	        cacheData.put("ALLOWVERIFYCOUNT", allowVerifyCount);//允许验证次数

	        //保存短信验证码
	        SharedCache.set(CHECK_CODE_CACHE_KEY+"_"+serialNumber,cacheData);
	       // data.put("INFO", cacheData);
	        outData.put("oprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        result = prepareOutResult(0,"",outData);

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }    
    
    public IData authRandomPwd(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData outData = new DataMap();
        String serialNumber = param.getString("userMobile", "");
        String verifyCode = param.getString("password", "");
        
        if ( "".equals(serialNumber))
        {
        	serialNumber = getParams(param).getString("userMobile", "");
        	verifyCode = getParams(param).getString("password", "");
        }
        try{
        	       	
	        if("".equals(serialNumber))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        if("".equals(verifyCode))
	        {        		            
	            result = prepareOutResult(1,"verifyCode不能为空",outData);
	            return result;
	        }
	        			
        	Object  sharedCache = SharedCache.get(CHECK_CODE_CACHE_KEY+"_"+serialNumber);
        	if(null == sharedCache){
        		result = prepareOutResult(1,"验证码失效，请重新申请",outData);
	            return result;
        	}
        	else{
        		IData smsVerifyCode=(DataMap)sharedCache;
        		String cacheVerifyCode=smsVerifyCode.getString("VERIFYCODE");//缓存中验证码
        		int allowVerifyCount=smsVerifyCode.getInt("ALLOWVERIFYCOUNT");//允许验证次数
        		int verifyCount=smsVerifyCode.getInt("VERIFYCOUNT");//已校验次数
        		String failTime=smsVerifyCode.getString("FAILTIME");//验证码失效时间
        		String nowDate =  SysDateMgr.getSysTime();
        		java.util.Date nowDateTime=SysDateMgr.string2Date(nowDate, "yyyy-MM-dd HH:mm:ss");
        		java.util.Date failDateTime=SysDateMgr.string2Date(failTime, "yyyy-MM-dd HH:mm:ss");
        		if(nowDateTime.after(failDateTime)){//当前时间在失效时间之后
            		SharedCache.delete(CHECK_CODE_CACHE_KEY+"_"+serialNumber);
            		result = prepareOutResult(1,"验证码失效，请重新申请",outData);
    	            return result;
        		}
        		if(verifyCount>=allowVerifyCount){
        			SharedCache.delete(CHECK_CODE_CACHE_KEY+"_"+serialNumber);
        			result = prepareOutResult(1,"校验失败次数超过3次限制，该验证码已作废,请重新申请",outData);
    	            return result;
        		}
    	    	if(cacheVerifyCode.equals(verifyCode)){
    	            SharedCache.delete(CHECK_CODE_CACHE_KEY+"_"+serialNumber);   
        			result = prepareOutResult(0,"验证码正确",outData);
    	            return result;
    	    	}
    	    	else{    	    		
    	    		smsVerifyCode.put("VERIFYCOUNT", verifyCount+1);//验证次数
    	    		SharedCache.set(CHECK_CODE_CACHE_KEY+"_"+serialNumber, smsVerifyCode);
    	    		result = prepareOutResult(1,"验证码错误",outData);
    	            return result;
    	    	}
        	}
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }    
    
    public IData prepareOutResult(int i,String rtnMsg,IData outData)
    {
    	IData object = new DataMap();
    	IData result = new DataMap();

    	if (i==0)//成功
    	{
        	object.put("resultRows", outData.getString("resultRows","1"));
        	outData.remove("resultRows");
        	object.put("result", outData);
            object.put("respCode", "0");
            object.put("respDesc", "success");
            
            result.put("object", object);
    		result.put("rtnCode", "0");	
    		result.put("rtnMsg", "成功!");	
            return result;
    	}
    	else if(i==1)//失败
    	{
        	object.put("result", outData);
        	object.put("resultRows", 0);
            object.put("respCode", "-1");
            object.put("respDesc", rtnMsg);
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
    	}
    	return null;
    }
    
    public IDataset qryUserResSimInfo(IData params) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IDataset result = new DatasetList();
        IDataset userRes = dao.qryUserResInfo(params);
        IData info = new DataMap();
        String simCardNum = "";
        if (IDataUtil.isNotEmpty(userRes))
        {
            for (int i = 0; i < userRes.size(); i++)
            {
                if ("1".equals(userRes.getData(i).getString("RES_TYPE_CODE", "")))
                {
                    simCardNum = userRes.getData(i).getString("RES_CODE", "");
                    info = userRes.getData(i);
                    break;
                }
            }
        }
        if (StringUtils.isNotBlank(simCardNum))
        {
            IDataset userResInfo = ResCall.getSimCardInfo("0", simCardNum, null, null);
            if (IDataUtil.isNotEmpty(userResInfo))
            {
                //info.put("RES_KIND_NAME", userResInfo.getData(0).getString("RES_KIND_NAME", ""));
                result.add(userResInfo.getData(0));
            }
            
        }
        return result;
    }
    
    public IDataset querySaleActives(String userId ) throws Exception
    {
        IDataset returnInfo = new DatasetList();
        //获取该用户所有有效的营销活动
        IData activeParam = new DataMap();
        activeParam.put("USER_ID", userId);
        IDataset saleActives = BofQuery.queryValidSaleActives(userId, "0898");
        if(saleActives != null && saleActives.size() > 0)
        {
        	for (Iterator it = saleActives.iterator(); it.hasNext();){
            	IData info = (IData) it.next();
            	String productId = info.getString("PRODUCT_ID","");
    	        IDataset commparaValue =  CommparaInfoQry.getCommpara("CSM", "1530", productId, "0898");
    	        if (commparaValue != null && commparaValue.size() > 0)//宽带活动
    	        {    	        	
    	        	returnInfo.add(info);
    	        }
        	}
        }
        return returnInfo;
    }
    
    public IData queryRedUser(String userId)throws Exception
    {
		IData idParamRedUser = new DataMap();
		IData idResult = new DataMap();
		idResult.put("owedFlag", "可催");
		idResult.put("stopFlag", "可停");
		idParamRedUser.put("USER_ID", userId);
		IDataset idsRedUser = CSAppCall.call("QCC_ITF_GetRedUser", idParamRedUser);
    	if(IDataUtil.isNotEmpty(idsRedUser) && idsRedUser.size() > 0)
    	{
    		//如果是携入用户
    		String X_CTAG_FEE = idsRedUser.getData(0).getString("X_CTAG_FEE", "");
    		if ("是".equals(X_CTAG_FEE))
    		{
    			idResult.put("owedFlag", "永不催费");
    		}
    		
    		String X_CTAG_STOP = idsRedUser.getData(0).getString("X_CTAG_STOP", "");
    		if ("是".equals(X_CTAG_STOP))
    		{
    			idResult.put("stopFlag", "永不停机");
    		}

        	
    	}
    	return idResult;
    }
    public IDataset getCreditInfo(IData params) throws Exception
    {
        String userId = params.getString("USER_ID", "");
        String idType = params.getString("IDTYPE", "0");
        IDataset resutls =CreditCall.getCreditInfo(userId, idType);
        return resutls;
    }
    
    public IDataset queryWideUserInfo(IData params) throws Exception
    {
        IData widenetInfo = new DataMap();
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IData userInfo = new DataMap();
        String msisdn = params.getString("SERIAL_NUMBER");
        if ("KD_".equals(msisdn.substring(0, 3)))
        {
            userInfo = UcaInfoQry.qryUserInfoBySn(msisdn);
        }
        else
        {
            userInfo = UcaInfoQry.qryUserInfoBySn("KD_" + msisdn);
        }
        // IDataset results = dao.queryWideUserInfo(params);
        if (IDataUtil.isEmpty(userInfo))
        {
            return new DatasetList();
        }
        IDataset results = IDataUtil.idToIds(userInfo);
        IData returnData = new DataMap();
        returnData.putAll(results.getData(0));
        IData param = results.getData(0);
        IDataset productInfo = dao.queryWideNetUserProductInfo(param);
        if (IDataUtil.isNotEmpty(productInfo))
        {
            returnData.put("PRODUCT_SET", productInfo.getData(0));
        }
        IDataset discntDataset = dao.queryWideNetUserDiscnt(param);
        if (IDataUtil.isNotEmpty(discntDataset))
        {
            returnData.put("DISCNT_SET", discntDataset);
        }
        widenetInfo.put("USER_INFO", returnData);

        if (IDataUtil.isEmpty(returnData))
        {
            return new DatasetList();
        }
        String kdUserId = param.getString("USER_ID", "");
        IDataset wideCustInfos = CustomerInfoQry.getCustomerInfoByUserId(kdUserId);
        IData wideCustInfo = new DataMap();
        if (IDataUtil.isNotEmpty(wideCustInfos))
        {
            wideCustInfo = wideCustInfos.getData(0);
        }
        widenetInfo.put("CUST_INFO", wideCustInfo);
        params.put("USER_ID", kdUserId);
        IDataset wideBaseInfos = dao.getUserWidenetInfo(params);
        if (IDataUtil.isEmpty(wideBaseInfos))
        {
            widenetInfo.put("BASE_INFO", "");
        }
        else
        {
            widenetInfo.put("BASE_INFO", wideBaseInfos.getData(0));
        }
        return IDataUtil.idToIds(widenetInfo);
    }
    /**
     * @Description: 是否4G卡用户
     * @param simCardNo
     * @return
     * @throws Exception
     * @author: zhangxing3
     */
    public boolean is4GUser(String simCardNo) throws Exception
    {


        // 调用资源接口
        IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", "1");

        if (IDataUtil.isNotEmpty(simCardDatas))
        {
            String simTypeCode = simCardDatas.getData(0).getString("RES_TYPE_CODE", "0").substring(1);// 对应老系统的simtypecode

            IDataset assignParaInfoData = ResParaInfoQry.checkUser4GUsimCard(simTypeCode);

            if (StringUtils.isNotBlank(simTypeCode) && IDataUtil.isNotEmpty(assignParaInfoData))
            {
                return true;
            }
        }
        else
        {
            // CSAppException.apperr(ResException.CRM_RES_86, simCardNo);
            return false;// 因测试资料不全 暂时返回false
        }
       
        return false;
    }
    public IDataset qryNpUserInfo(IData param) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IDataset results = dao.qryNpUserInfo(param);
        return results;
    } 
    
    /**
     * 获取用户GPRS服务状态code
     */
    public IData getUserGPRSStateCode(String userId, String serviceId) throws Exception
    {
        IDataset dataset = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, serviceId);
        if (dataset.isEmpty())
        {
            return null;
        }
        return dataset.getData(0);
    }
    
    /**
     * 选中包节点，校验包是否可选
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData choicePackageNode(IData input) throws Exception
    {

        if (!input.containsKey("USER_ID"))
            CSAppException.appError("123", "点选产品校验：用户标识[USER_ID]是必须的！");
        if (!input.containsKey("CUST_ID"))
            CSAppException.appError("123", "点选产品校验：客户标识[CUST_ID]是必须的！");
        if (!input.containsKey("PRODUCT_ID"))
            CSAppException.appError("123", "点选产品校验：营销产品标识[PRODUCT_ID]是必须的！");
        if (!input.containsKey("PACKAGE_ID"))
            CSAppException.appError("123", "点选产品校验：营销包标识[PACKAGE_ID]是必须的！");

        // 增加校验配置
        if (!ProductInfoQry.checkSaleActiveLimitProd(input.getString("PRODUCT_ID")))
        {
            return null;
        }

        String packageId = input.getString("PACKAGE_ID");
        IData pkgInfo = UPackageInfoQry.getPackageByPK(packageId);
        if (IDataUtil.isEmpty(pkgInfo))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_49, "未找到该营销包,或营销包已下线[" + packageId + "]");
        }

        IData paramValue = new DataMap();

        paramValue.put("V_EVENT_TYPE", "ATTR");
        paramValue.put("V_EPARCHY_CODE", this.getVisit().getStaffEparchyCode());
        paramValue.put("V_CITY_CODE", this.getVisit().getCityCode());
        paramValue.put("V_DEPART_ID", this.getVisit().getDepartId());
        paramValue.put("V_STAFF_ID", this.getVisit().getStaffId());

        paramValue.put("V_USER_ID", input.getString("USER_ID"));
        paramValue.put("V_DEPOSIT_GIFT_ID", input.getString("CUST_ID"));
        paramValue.put("V_PURCHASE_MODE", input.getString("PRODUCT_ID"));
        paramValue.put("V_PURCHASE_ATTR", input.getString("PACKAGE_ID"));
        paramValue.put("V_TRADE_ID", "-1");

        paramValue.put("V_CHECKINFO", input.getString("CHECKINFO", ""));
        paramValue.put("V_RESULTCODE", input.getString("RESULTCODE", ""));
        paramValue.put("V_RESULTINFO", input.getString("RESULTINFO", ""));
        paramValue.put("V_SALE_TYPE", input.getString("CAMPN_TYPE"));
        paramValue.put("V_VIP_TYPE_ID", input.getString("VIP_TYPE_ID", "-1"));

        paramValue.put("V_VIP_CLASS_ID", input.getString("VIP_CLASS_ID", "-1"));
        ProductInfoQry.checkSaleActiveProdByProced(paramValue);
        return paramValue;

    }
    public String qryCampnTypeForSaleActive(String productid) throws Exception
    {
        String campnType = "";
    	IDataset datas = UpcCall.qryCatalogByCatalogId(productid);
        if(IDataUtil.isNotEmpty(datas))
        {
            IData data = datas.getData(0);
            if(IDataUtil.isNotEmpty(data))
            {
            	campnType = data.getString("UP_CATALOG_ID");

            }
        } 
        return campnType;   
    }
    /**
     * 获取所有可兑换的记录列表
     * 
     * @param inData
     * @return
     * @throws Exception
     */
	public IDataset queryExchangeList(String score,String brandCode,String exchangeType,String scoreMin,String scoreMax,String commodityName) throws Exception
 {
		IDataset outList = new DatasetList();
		IDataset exchangeList = ExchangeRuleInfoQry.queryExRuleByVipBrandCode(
				score, brandCode, this.getTradeEparchyCode());
		if (IDataUtil.isNotEmpty(exchangeList) && !"".equals(exchangeType)) {
			// 处理兑换类型为物品且在资源侧可管理的记录,用于更新EXCHANGE_LIMIT
			IData exchangeData = null;
			String exchangeTypeCode = "";
			String exchangeResId = "";

			IData resGiftNumData = null;
			int exchangeRuleLimit = -1;
			int exchangeLimit = -1;

			//int listSize = exchangeList.size();
			int K = 0;
			for (int i = 0; i < exchangeList.size(); i++) {
				exchangeData = exchangeList.getData(i);
				exchangeTypeCode = exchangeData.getString("EXCHANGE_TYPE_CODE");// 兑换类型编码
				exchangeResId = exchangeData.getString("GIFT_TYPE_CODE");// 资源管理标识RES_ID
				exchangeRuleLimit = exchangeData.getInt("EXCHANGE_LIMIT");// 营业侧定义可兑换数量
				
				int gift_score=exchangeData.getInt("SCORE");
				String gift_name=exchangeData.getString("RULE_NAME");
				if(!"".equals(scoreMin)){
					if(Integer.parseInt(scoreMin)>gift_score){
						continue;
					}
				}
				
				if(!"".equals(scoreMax)){
					if(Integer.parseInt(scoreMax)<gift_score){
						continue;
					}
				}
				
				
				if(!"".equals(commodityName)){
					if(!gift_name.contains(commodityName)){
						continue;
					}
				}

				if ((ScoreFactory.EXCHANGE_TYPE_REWARD.equals(exchangeTypeCode))
						&& (StringUtils.isNotBlank(exchangeResId))) {

					resGiftNumData = ResCall.queryGoods(exchangeResId);

					exchangeLimit = resGiftNumData.getInt("GOODS_NUM");// 资源定义的兑换数量:库存
					if (exchangeLimit > 0) {// 要注意营业侧定义的兑换次数为-1，这个时候以资源库存数据为准
						exchangeData.put("EXCHANGE_LIMIT",
								exchangeLimit < exchangeRuleLimit ? ""
										+ exchangeLimit
										: (exchangeRuleLimit == -1 ? ""
												+ exchangeLimit : ""
												+ exchangeRuleLimit));
					} else {
						exchangeList.remove(i);
						i--;
						continue;
					}

				}
				if (exchangeType.equals(exchangeTypeCode)) {
					outList.add(K, exchangeData);
				}
			}
			return outList;
		} else if (IDataUtil.isNotEmpty(exchangeList)
				&& "".equals(exchangeType)) {
			// 处理兑换类型为物品且在资源侧可管理的记录,用于更新EXCHANGE_LIMIT
			IData exchangeData = null;
			String exchangeTypeCode = "";
			String exchangeResId = "";

			IData resGiftNumData = null;
			int exchangeRuleLimit = -1;
			int exchangeLimit = -1;

			//int listSize = exchangeList.size();
			for (int i = 0; i < exchangeList.size(); i++) {
				exchangeData = exchangeList.getData(i);
				exchangeTypeCode = exchangeData.getString("EXCHANGE_TYPE_CODE");// 兑换类型编码
				exchangeResId = exchangeData.getString("GIFT_TYPE_CODE");// 资源管理标识RES_ID
				exchangeRuleLimit = exchangeData.getInt("EXCHANGE_LIMIT");// 营业侧定义可兑换数量
				
				
				int gift_score=exchangeData.getInt("SCORE");
				String gift_name=exchangeData.getString("RULE_NAME");
				if(!"".equals(scoreMin)){
					if(Integer.parseInt(scoreMin)>gift_score){
						exchangeList.remove(i);
						i--;
						continue;
					}
				}
				
				if(!"".equals(scoreMax)){
					if(Integer.parseInt(scoreMax)<gift_score){
						exchangeList.remove(i);
						i--;
						continue;
					}
				}
				
				
				if(!"".equals(commodityName)){
					if(!gift_name.contains(commodityName)){
						exchangeList.remove(i);
						i--;
						continue;
					}
				}
				
				

				if ((ScoreFactory.EXCHANGE_TYPE_REWARD.equals(exchangeTypeCode))
						&& (StringUtils.isNotBlank(exchangeResId))) {

					resGiftNumData = ResCall.queryGoods(exchangeResId);

					exchangeLimit = resGiftNumData.getInt("GOODS_NUM");// 资源定义的兑换数量:库存
					if (exchangeLimit > 0) {// 要注意营业侧定义的兑换次数为-1，这个时候以资源库存数据为准
						exchangeData.put("EXCHANGE_LIMIT",
								exchangeLimit < exchangeRuleLimit ? ""
										+ exchangeLimit
										: (exchangeRuleLimit == -1 ? ""
												+ exchangeLimit : ""
												+ exchangeRuleLimit));
					} else {
						exchangeList.remove(i);
						i--;
						continue;
					}

				}

			}
			return exchangeList;
		}
		 return null;
	}
	public String HandlePsptTypeCode(String psptTypeCode)
	{
		if ("0".equals(psptTypeCode) || "1".equals(psptTypeCode)) //身份证件
		{
			return "00";
		}
		else if ("01".equals(psptTypeCode)) //VIP卡
		{
			return "01";
		}
		else if ("A".equals(psptTypeCode)) //护照
		{
			return "02";
		}
		else if ("C".equals(psptTypeCode)) //军官证、军人证
		{
			return "04";
		}
		else if ("K".equals(psptTypeCode)) //武装警察身份证
		{
			return "05";
		}
		else if ("I".equals(psptTypeCode)) //台胞证
		{
			return "06";
		}
		else if ("2".equals(psptTypeCode)) //户口簿
		{
			return "07";
		}
		else if ("J".equals(psptTypeCode)) //港澳证
		{
			return "08";
		}
		else if ("Z".equals(psptTypeCode)) //其他证件
		{
			return "99";
		}
		else {
			return "";
		}
	}
	public String HandleIdType(String psptTypeCode)
	{
		if ("00".equals(psptTypeCode)) //身份证件
		{
			return "0";
		}
		else if ("01".equals(psptTypeCode)) //VIP卡
		{
			return "01";
		}
		else if ("02".equals(psptTypeCode)) //护照
		{
			return "A";
		}
		else if ("04".equals(psptTypeCode)) //军官证、军人证
		{
			return "C";
		}
		else if ("05".equals(psptTypeCode)) //武装警察身份证
		{
			return "K";
		}
		else if ("06".equals(psptTypeCode)) //台胞证
		{
			return "I";
		}
		else if ("07".equals(psptTypeCode)) //户口簿
		{
			return "2";
		}
		else if ("08".equals(psptTypeCode)) //港澳证
		{
			return "J";
		}
		else if ("99".equals(psptTypeCode)) //其他证件
		{
			return "Z";
		}
		else {
			return "";
		}
	}
	public String HandleExchangeType(String exchangeType)
	{
		if ("0".equals(exchangeType)) //0-实物
		{
			return "2";
		}
		else if ("1".equals(exchangeType)) //1-话费
		{
			return "1";
		}
		else if ("2".equals(exchangeType)) //2-电子卡
		{
			return "N";
		}
		else if ("3".equals(exchangeType)) //3-套餐
		{
			return "3";
		}
		else if ("".equals(exchangeType)) //全部
		{
			return "";
		}
		else {
			return "99";
		}
	}
		
    public IDataset loadFeeInfo(IData input) throws Exception
    {  
        UcaData uca = null;
        
        if (StringUtils.isNotBlank(input.getString("USER_ID")))
        {
            uca = UcaDataFactory.getUcaByUserId(input.getString("USER_ID"));
        }
        else if (StringUtils.isNotBlank(input.getString("SERIAL_NUMBER")))
        {
            uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
        }  
        String strCreditClass = uca.getUserCreditClass();
        
        if (StringUtils.isBlank(strCreditClass))
        {
        	String sn=uca.getSerialNumber();
        	String userId=uca.getUserId();
        	strCreditClass=getUserCreditClass(sn,userId);
        	if(strCreditClass==null || "".equals(strCreditClass)){
            strCreditClass = "-1";
        	}
        }
        
        int iCreditClass = Integer.parseInt(strCreditClass); 
        double iAcctBlance = FeeUtils.toDouble(uca.getAcctBlance()); 
        // 是否满足星级服务流程，预存款开通条件
        
        IDataset feeList = new DatasetList();
        if (-1 == iCreditClass || 0 == iCreditClass)
        {
            if (iAcctBlance < 20000)
            {
                double need = 20000-iAcctBlance;
                IData item = new DataMap();
                item.put("TRADE_TYPE_CODE","110"); 
                item.put("FEE", String.valueOf(need)); 
                item.put("MODE","2"); 
                item.put("CODE","111"); 
                feeList.add(item);
            }
        }  
        return feeList;
    }
    
    public String getUserCreditClass(String sn,String userId)throws Exception{
    	String strCreditClass="";
    	IData condition = new DataMap(); 
        condition.put("SERIAL_NUMBER", sn);
        condition.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        condition.put("USER_ID", userId); 
        condition.put("IDTYPE", "0");
        IDataset userCreditClass=CSAppCall.call("SS.GetUser360ViewSVC.getCreditInfo", condition);
        
        if(userCreditClass!=null && userCreditClass.size()>0){
        	strCreditClass=userCreditClass.getData(0).getString("CREDIT_CLASS","");
        }
        return strCreditClass;
    }
    public IDataset queryUserPlatByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset dataset = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_ID_NEW_2", param);
		return dataset;
	}
    public IDataset prePlatSvcData(IDataset spList) throws Exception
	{
    	IDataset ids = new DatasetList();
    	IDataset ids2 = new DatasetList();
    	if (spList != null && spList.size() > 0)
        {
            int size = spList.size();
            for (int i = 0; i < size; i++)
            {
            	IData data = new DataMap();
            	String offerId = spList.getData(i).getString("offerId", "");
            	String []arg = StringUtils.split(offerId, "|");
            	data.put("SERVICE_ID", arg[0]);
            	data.put("SP_CODE", spList.getData(i).getString("spCode", ""));
            	data.put("BIZ_CODE", spList.getData(i).getString("operatorCode", ""));
            	data.put("BIZ_TYPE_CODE", spList.getData(i).getString("servType", ""));
            	data.put("MODIFY_TAG", "");
            	String oprType = spList.getData(i).getString("oprType", "");
            	if ("01".equals(oprType))
            	{
            		data.put("OPER_CODE", PlatConstants.OPER_ORDER);
            	}
            	else if ("02".equals(oprType))
            	{
            		data.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
            	}
            	else if ("04".equals(oprType))
            	{
            		data.put("OPER_CODE", PlatConstants.OPER_PAUSE);
            	}
            	else if ("05".equals(oprType))
            	{
            		data.put("OPER_CODE", PlatConstants.OPER_RESTORE);
            	}
            	else
            	{
            		data.put("OPER_CODE", spList.getData(i).getString("oprType", ""));
            	}
            	
            	IDataset attrList =  spList.getData(i).getDataset("extInfo", null);
            	if (attrList != null && attrList.size() > 0)
                {
            		int size2 = attrList.size();
                    for (int j = 0; j < size2; j++)
                    {
                    	IData data2 = new DataMap();
                    	data2.put("ATTR_CODE", attrList.getData(j).getString("attr", ""));
                    	data2.put("ATTR_VALUE", attrList.getData(j).getString("value", ""));
                    	ids2.add(j, data2);
                    }
                    data.put("ATTR_PARAM", ids2);
                }
            	ids.add(i, data);           	
            }
        }
    	return ids;
	}
    
    public IDataset queryUserSaleGoodsInfo(String userId, String beginDate,String endDate,String packageId,String packageName) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PACKAGE_ID", packageId);
        param.put("PACKAGE_NAME", packageName);
        param.put("BEGIN_DATE", beginDate);
        param.put("END_DATE", endDate);
        return Dao.qryByCodeParser("TF_F_USER_SALE_GOODS", "SEL_BY_USERID_INTF", param);
    }

	public IDataset queryUserDepositInfo(String serialNumber, String userId) throws Exception {
		// 调账务接口
		IDataset deposits = new DatasetList();
		IDataset results = AcctCall.queryUserDiscntAction("2", serialNumber,userId);
		if (IDataUtil.isNotEmpty(results)) {
			IDataset depositList = results.getData(0).getDataset("DISCNT_LIST");
			// System.out.print("----------queryUserDepositInfo-----------"+depositList);
			if (IDataUtil.isNotEmpty(depositList)) {

				for (int i = 0; i < depositList.size(); i++) {
					IData deposit = depositList.getData(i);
					//deposit.put("PACKAGE_ID", packageId);
					deposit.put("SERIAL_NUMBER", serialNumber);
					// deposit.put("LEFT_MONTHS", deposit.getInt("MONTHS") -
					// deposit.getInt("TIMES"));

					String startDate = deposit.getString("START_DATE", deposit.getString("START_CYCLE_ID"));
					startDate = startDate.substring(0, 4) + "-"+ startDate.substring(4, 6) + "-"+ startDate.substring(6);

					String END_CYCLE_ID = SysDateMgr.getAddMonthsLastDay(deposit.getInt("MONTHS"), startDate);
					deposit.put("START_CYCLE_ID", startDate);
					deposit.put("END_CYCLE_ID", END_CYCLE_ID);
					deposits.add(deposit);
				}
			}
		}
		return deposits;
	}
    
    
    public IDataset querySaleActives(IData input) throws Exception
    {
        IData otherInfo = new DataMap();
        String catalogId = input.getString("CATALOG_ID", "");
        String catalogName = input.getString("CATALOG_NAME", "");
        String packageId = input.getString("PACKAGE_ID");
        String packageName = input.getString("PACKAGE_NAME", "");
        String campnType = "";
        IDataset saleActives = new DatasetList();
        //System.out.println("==========querySaleActives========="+input);
        if (StringUtils.isNotBlank(catalogId))
        {
            saleActives = UpcCall.qryOfferTempChasByCatalogId(catalogId);
            campnType = qryCampnTypeForSaleActive(catalogId); 
            saleActives = SaleActiveUtil.filterSalePackagesByParamAttr526(saleActives, catalogId, campnType, false);
        }
        else if(StringUtils.isNotBlank(catalogName))
        {
        	saleActives = UpcCall.qryOfferTempChasByCataIdAndCataName(catalogId,catalogName);
            //campnType = qryCampnTypeForSaleActive(catalogId); 
            saleActives = SaleActiveUtil.filterSalePackagesByParamAttr526(saleActives, catalogId, campnType, false);
        }
        
        else if(StringUtils.isNotBlank(packageId))
        {
        	IDataset catalogInfo = UpcCall.qryCatalogByOfferId(packageId,"K");
	        if(!IDataUtil.isEmpty(catalogInfo)){
	        	catalogId = catalogInfo.getData(0).getString("CATALOG_ID", "");
	            campnType = qryCampnTypeForSaleActive(catalogId); 
	        }
            IDataset ids = UpcCall.qryOfferTempChasByCatalogId(catalogId);
	            if(IDataUtil.isNotEmpty(ids)){
	            for(int i = 0 ; i < ids.size(); i++)
	            {
	            	if(packageId.equals(ids.getData(i).getString("OFFER_CODE", "")))
	            	{
	            		saleActives.add(ids.getData(i));
	            	}
	            }
            }
	        saleActives = SaleActiveUtil.filterSalePackagesByParamAttr526(saleActives, catalogId, campnType, false);
    
        }
        else if(StringUtils.isNotBlank(packageName))
        {
        	/*IDataset ids = UpcCall.qryOffersByOfferTypeLikeOfferName("K",null,packageName);
        	if(IDataUtil.isNotEmpty(ids)){
	            for(int i = 0 ; i < ids.size(); i++)
	            {
	            	packageId = ids.getData(i).getString("OFFER_CODE","");
	            	IDataset catalogInfo = UpcCall.qryCatalogByOfferId(packageId,"K");
	    	        if(!IDataUtil.isEmpty(catalogInfo)){
	    	        	catalogId = catalogInfo.getData(0).getString("CATALOG_ID", "");
	    	            campnType = qryCampnTypeForSaleActive(catalogId); 
	    	        }
	            	IDataset ids1 = UpcCall.qryOfferTempChasByCatalogId(catalogId);
		            if(IDataUtil.isNotEmpty(ids1)){
			            for(int j = 0 ; j < ids1.size(); j++)
			            {
			            	if(packageId.equals(ids1.getData(j).getString("OFFER_CODE", "")))
			            	{			            		
			            		ids1.getData(j).put("PACKAGE_ID", packageId);
			            		ids1.getData(j).put("PACKAGE_NAME", ids.getData(i).getString("OFFER_NAME",""));
			            		ids1.getData(j).put("CAMPN_TYPE", campnType);
			            		ids1.getData(j).put("PRODUCT_ID", catalogId);

			            		saleActives.add(ids1.getData(j));
			            	}
			            }
		            }
	            }
        	}*/
        	saleActives = UpcCall.qryOfferTempChasByPackageName(packageName);

        	saleActives = filterSalePackagesByParamAttr526_KF(saleActives,false);

        }

        if (IDataUtil.isEmpty(saleActives))
        {
            return saleActives;
        }

        // filter priv
        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), saleActives);


        // 循环调用规则
        //SaleActiveCheckBean.checkPackages(saleActives, input.getString("SERIAL_NUMBER"));

        //
/*        String allFail = "true";
        for (int i = 0, size = saleActives.size(); i < size; i++)
        {
            IData saleActive = saleActives.getData(i);
            if (!"1".equals(saleActive.getString("ERROR_FLAG")))
            {
                allFail = "false";
                break;
            }
        }

        otherInfo.put("ALL_FAIL", allFail);*/

        // sort
        DataHelper.sort(saleActives, "PACKAGE_NAME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        //DataHelper.sort(saleActives, "ERROR_FLAG", IDataset.TYPE_INTEGER);

        //saleActives.add(otherInfo);

        return saleActives;
    }
    
    public IData transferDataInputForProductChange(IData input) throws Exception
    {
        //this.checkInputData(input);
    	IData outData = new DataMap();
        IDataset selectedElements = new DatasetList();
        outData.put("SERIAL_NUMBER", getParams(input).getString("userMobile",""));
        String roamingTag = "0";
        String discntCode = "";
        String modifyTag = "";
        //add by zhangxing for "支持预约生效" start
/*        String effectType = getParams(input).getString("effectType","");
        if ( "Type_NextDay".equals(effectType))
        {
        	outData.put("BOOKING_TAG","1");
        	outData.put("START_DATE",SysDateMgr.addDays(1));
        }
        if ( "Type_NextCycle".equals(effectType))
        {
        	outData.put("BOOKING_TAG","1");
        	outData.put("START_DATE",SysDateMgr.getFirstDayOfNextMonth());
        }
        if ( "EffectType_Time".equals(effectType))
        {
        	String effTime = getParams(input).getString("effTime","");
        	String startDate = "";
        	if (StringUtils.isBlank(effTime))
            {
        		startDate = SysDateMgr.getFirstDayOfNextMonth();
            }
        	else{
        		startDate = effTime.substring(0, 4) + "-" + effTime.substring(4, 6) + "-" + effTime.substring(6,8);
        	}
        	outData.put("BOOKING_TAG","1");
        	outData.put("START_DATE",startDate);
        	
        }*/
        //add by zhangxing for "支持预约生效" end
        
        IDataset elements = getDatasets(getParams(input),"offerList");

        for (int i = 0, size = elements.size(); i < size; i++)
        {
            IData selectElement = new DataMap();
            selectElement.clear();

            IData element = elements.getData(i);
            String offerId = element.getString("offerId", "");
        	if( !"".equals(offerId))
        	{
        		//String []arg = offerId.split("|");
        		String []arg = StringUtils.split(offerId, "|");
        		selectElement.put("ELEMENT_ID", arg[0]);
                selectElement.put("ELEMENT_TYPE_CODE", arg[1]);
                if ("M".equals(arg[1]))
                {
                	roamingTag= "1";
                	discntCode = arg[0];
                	modifyTag = element.getString("action","");
                }
        	}

            selectElement.put("MODIFY_TAG", element.getString("action",""));
            selectElement.put("INST_ID", element.getString("prodInstId",""));
            selectElement.put("AUTH_TYPE", element.getString("authType",""));//鉴权种类
            outData.put("AUTH_TYPE",element.getString("authType",""));
            if("1".equals(element.getString("specialOfferType","")) )
            {
            	roamingTag= "1";//1-是国漫产品包,不传specialOfferType 或者 specialOfferTyp=0表示非国漫产品包
            }
         
            //add by zhangxing for "支持预约生效" start
            String effectType = element.getString("effectType","");
            if ( "Type_NextDay".equals(effectType))
            {
            	outData.put("BOOKING_TAG","1");
            	outData.put("START_DATE",SysDateMgr.addDays(1));
            }
            if ( "Type_NextCycle".equals(effectType))
            {
            	outData.put("BOOKING_TAG","1");
            	outData.put("START_DATE",SysDateMgr.getFirstDayOfNextMonth());
            }
            if ( "EffectType_Time".equals(effectType))
            {
            	String effTime = element.getString("effTime","");
            	String startDate = "";
            	if (StringUtils.isBlank(effTime))
                {
            		startDate = SysDateMgr.getFirstDayOfNextMonth();
                }
            	else{
            		startDate = effTime.substring(0, 4) + "-" + effTime.substring(4, 6) + "-" + effTime.substring(6,8);
            	}
            	outData.put("BOOKING_TAG","1");
            	outData.put("START_DATE",startDate);
            	
            }
            //add by zhangxing for "支持预约生效" end
            IDataset attrList = element.getDataset("offerAttrList", null);
            int arrtIndex = 1;
            // 处理ATTR参数
            if (IDataUtil.isNotEmpty(attrList))
            {
            	for (int j = 0;j < attrList.size(); j++)
                {
                    IData arrt = attrList.getData(j);
                    selectElement.put("ATTR_STR" + arrtIndex, arrt.getString("attrCode"));
                    arrtIndex++;
                    selectElement.put("ATTR_STR" + arrtIndex, arrt.getString("attrValue"));
                    arrtIndex++;
                }
            }

            selectedElements.add(selectElement);
        }

        outData.put("ELEMENTS", selectedElements);
        if ("1".equals(roamingTag)){
	        outData.put("ROAMINGTAG", roamingTag);
	        outData.put("DISCNT_CODE", discntCode);
	        outData.put("MODIFY_TAG", modifyTag);
        }
        return outData;
    }
	private IDataset getGJHDGoodsInfoByUserId(IData inparam) throws Exception
	{		
		return Dao.qryByCode("TF_F_USER_SALE_GOODS", "SEL_SALEACTIVE_DEVICE_INFOS", inparam);
	}
	private IDataset queryAddrInfos(String serialNumber) throws Exception
	{		
		IData inparam = new DataMap();
		inparam.put("ACCOUNT_ID", serialNumber);
        IDataset addrInfo=CSAppCall.call("PB.WideAddressSVC.qryAddressBySerialNumber", inparam);
        return addrInfo;
	}
	private IDataset queryUserProductHisInfos(String userId) throws Exception
	{		
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_HISINFO_BY_UID", inparam);
	}
	private IDataset getUserSvcs(IData data) throws Exception
    {
        String queryMode = data.getString("QUERY_MODE", "");
    	SQLParser sqlParser = new SQLParser(data);
        sqlParser.addSQL("select INST_ID,partition_id, user_id, user_id_a, service_id, main_tag,  inst_id, campn_id, start_date, end_date,");
        sqlParser.addSQL(" update_time, update_staff_id, update_depart_id, remark, rsrv_num1, rsrv_num2, rsrv_num3, rsrv_num4, rsrv_num5, rsrv_str1, rsrv_str2, rsrv_str3,");
        sqlParser.addSQL(" rsrv_str4, rsrv_str5, rsrv_str6, rsrv_str7, rsrv_str8, rsrv_str9, rsrv_str10, rsrv_date1, rsrv_date2, rsrv_date3, rsrv_tag1, rsrv_tag2, rsrv_tag3");
        sqlParser.addSQL("  from TF_F_USER_SVC a");
        sqlParser.addSQL(" where a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
        sqlParser.addSQL(" and a.user_id = :USER_ID");
        sqlParser.addSQL(" and a.service_id in(" + data.get("SERVICEIDS") + ")");
        if("".equals(queryMode) || "0".equals(queryMode))
        {
        	sqlParser.addSQL(" and sysdate between a.start_date and a.end_date ");
        }
        else 
        {
        	sqlParser.addSQL(" and sysdate > a.end_date ");
        }
        return Dao.qryByParse(sqlParser);
    }
	private IDataset getUserDiscnts(IData data) throws Exception
	{
		String queryMode = data.getString("QUERY_MODE", "");
		SQLParser sqlParser = new SQLParser(data);
		sqlParser.addSQL("select inst_id,partition_id, user_id, user_id_a, discnt_code, spec_tag, relation_type_code, inst_id, campn_id, start_date, end_date,");
		sqlParser.addSQL(" update_time, update_staff_id, update_depart_id, remark, rsrv_num1, rsrv_num2, rsrv_num3, rsrv_num4, rsrv_num5, rsrv_str1, rsrv_str2, rsrv_str3,");
		sqlParser.addSQL(" rsrv_str4, rsrv_str5, rsrv_date1, rsrv_date2, rsrv_date3, rsrv_tag1, rsrv_tag2, rsrv_tag3");
		sqlParser.addSQL("  from TF_F_USER_DISCNT a");
		sqlParser.addSQL(" where partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
		sqlParser.addSQL(" and user_id = :USER_ID");
		sqlParser.addSQL(" and a.discnt_code in(" + data.get("DISCNTCODES") + ")");
		if("".equals(queryMode) || "0".equals(queryMode))
        {
        	sqlParser.addSQL(" and sysdate between a.start_date and a.end_date ");
        }
        else 
        {
        	sqlParser.addSQL(" and sysdate > a.end_date ");
        }

		return Dao.qryByParse(sqlParser);
	}
	private IDataset queryPlatorderAllinfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		//return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN03_NOW", param);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.* FROM TF_F_USER_PLATSVC A ");
		parser.addSQL(" WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
		parser.addSQL(" AND A.user_id = :USER_ID ");
		parser.addSQL(" and sysdate between a.start_date and a.end_date ");
		return Dao.qryByParse(parser);
	}

	private IDataset queryPlatorderHisinfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		//return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN02_NOW", param);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.* FROM TF_F_USER_PLATSVC A ");
		parser.addSQL(" WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
		parser.addSQL(" AND A.user_id = :USER_ID ");
		parser.addSQL(" and sysdate > a.end_date ");
		return Dao.qryByParse(parser);
	}

	private IData getParams(IData param) throws Exception {
    	
    	Object o = param.get("params");
    	if(o instanceof Map) {
    		return new DataMap((Map) o);
		}else if(o instanceof IData) {
			return (IData) o;
		}else if (o instanceof String) {
			return new DataMap(String.valueOf(o));
		}
		throw new Exception("未识别的params参数...params=" + o +",type=" + o.getClass());
    }
	/**
	 * k3
	 * @param param
	 * @param scoreList
	 * @return
	 * @throws Exception
	 */
	private IData getParams2(IData param,String scoreList)throws Exception{
		Object o = param.get("params");
		boolean flag=false;
		if(o instanceof Map){
			 flag=((Map) o).containsKey(scoreList);
			if(flag){
				 o=((Map) o).get(scoreList);
				return getcommon(o);
			}else{
				return new DataMap((Map) o);
			}
			
		}else if(o instanceof IData){
			flag=((IData) o).containsKey(scoreList);
			if(flag){
				o=((IData) o).get(scoreList);
				return getcommon(o);
			}else{
				return (IData) o;
			}
		}else if(o instanceof String){
			flag=new DataMap(String.valueOf(o)).containsKey(scoreList);
			if(flag){
				o=new DataMap(String.valueOf(o)).get(scoreList);
				return getcommon(o);
			}else{
				return new DataMap(String.valueOf(o));
			}
		}
		throw new Exception("未识别的params参数...params=" + o +",type=" + o.getClass());
	}
	/**
	 * k3
	 * @param list
	 * @return
	 * @throws Exception
	 */
	private IData getcommon(Object list)throws Exception{
		if(list instanceof List){
			Object map=((List) list).get(0);
			if(map instanceof Map) {
	    		return new DataMap((Map) map);
			}else if(map instanceof IData) {
				return (IData) map;
			}else if (map instanceof String) {
				return new DataMap(String.valueOf(map));
			}
		}else if(list instanceof IDataset){
			return ((IDataset) list).getData(0);
		}else if(list instanceof String){
			return new DatasetList(String.valueOf(list)).getData(0);
		}
		throw new Exception("未识别的list参数...list=" + list +",type=" + list.getClass());
	}
	//封装01主资费商品k3
	public IData qryMainProductInfo(String queryMode,String busiCode,IData outData,IDataset ids)throws Exception{

    	IDataset offerAttrList= new DatasetList();
		UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
		IDataset userProductInfos = new DatasetList();
		IDataset acctOpenInfos = new DatasetList();
		if("0".equals(queryMode) || "".equals(queryMode))
    	{
			userProductInfos = UserProductInfoQry.queryUserMainProduct(ucaData.getUserId());
			acctOpenInfos = queryOpenInfo(busiCode);
    	}
		else if ("1".equals(queryMode))
    	{
			userProductInfos = queryUserProductHisInfos(ucaData.getUserId());
    	}
    	else
    	{
    		IData result = prepareOutResult(1,"不支持的queryMode值："+queryMode,outData);
            return result;
    	}
		if (IDataUtil.isNotEmpty(userProductInfos)){
		for (int i=0 ; i < userProductInfos.size() ; i++)
    	{
    		IData tmpData = new DataMap();
    		String productId = userProductInfos.getData(i).getString("PRODUCT_ID", "");
    		IDataset catalogInfos = UpcCall.qryCatalogByOfferId(productId,"P");
    		String catalogId = "";
    		String catalogName = "";
    		if(IDataUtil.isNotEmpty(catalogInfos)){
    			catalogId = catalogInfos.getData(0).getString("CATALOG_ID", "");
    			catalogName = catalogInfos.getData(0).getString("CATALOG_NAME", "");
    		}
    		tmpData.put("offerId", productId+"|P");
    		tmpData.put("offerName", UProductInfoQry.getProductNameByProductId(productId));
    		tmpData.put("upOfferId", catalogId);
    		tmpData.put("upOfferName", catalogName);
    		tmpData.put("isPackage", "1");
    		
    		tmpData.put("spCode", "");
    		tmpData.put("bizCode", "");
    		tmpData.put("spName", "");
    		tmpData.put("billType", "");
    		tmpData.put("servType", "");
    		

    		
    		//查询用户产品下的必选优惠
    		//IDataset userMainDiscnts = queryUserMainElements(ucaData.getUserId(),productId);
    		IDataset userMainDiscnts = qryComRelOffersByOfferId(productId);

    		float price = 0 ;
    		if(IDataUtil.isNotEmpty(userMainDiscnts)){
    			for (int m=0 ; m < userMainDiscnts.size() ; m++)
	        	{
	        		if(IDataUtil.isNotEmpty(acctOpenInfos)){
						for(int j=0;j<acctOpenInfos.size();j++){
							if(userMainDiscnts.getData(m).getString("ELEMENT_ID","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_ID",""))
									&& "1".equals(acctOpenInfos.getData(j).getString("FEE_FLAG",""))){
								Float monthRent = Float.parseFloat(acctOpenInfos.getData(j).getString("MONTH_RENT","0"));
								if(monthRent > 0)
								{
									price += monthRent;
								}    	    								
							}
						}
	        		}
	        	}
    		}
    		tmpData.put("offerPrice", Float.toString(price*100)+"/月");
    		
    		if("80010484".equals(productId)) //流量王卡18元套餐
    		{
        		tmpData.put("offerPrice","1800/月");
    		}
    		if("10003377".equals(productId)) // 神州行流量日租卡套餐
    		{
    			tmpData.put("offerPrice","0/月");
    		}
    		
    		if("0".equals(queryMode) || "".equals(queryMode))
        	{
    			tmpData.put("status", "0");
        	}
    		else
    		{
    			tmpData.put("status", "");
        	}	
    		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userProductInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));

    		tmpData.put("offerDesc", UProductInfoQry.getProductExplainByProductId(productId));
    		tmpData.put("offerType", "P");
    		tmpData.put("offerTypeName", "产品");
    		tmpData.put("brandId", userProductInfos.getData(i).getString("BRAND_CODE", ""));
    		tmpData.put("brandName", UpcCall.queryBrandNameByChaVal( userProductInfos.getData(i).getString("BRAND_CODE", "")));
    		tmpData.put("isMain", userProductInfos.getData(i).getString("MAIN_TAG", ""));
    		tmpData.put("chooseTag", "");
    		tmpData.put("canUnsub", "1");

    		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userProductInfos.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
			tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userProductInfos.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
    		List<AttrTradeData> userAttrs = ucaData.getUserAttrsByRelaInstId(userProductInfos.getData(i).getString("INST_ID", ""));
    		for (int j=0 ; j < userAttrs.size() ; j++)
        	{
	        	IData tmpData1 = new DataMap();
        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
        		tmpData1.put("attrName", "");
        		tmpData1.put("attrDesc", "");
        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
        		tmpData1.put("attrValueDesc", "");
        		offerAttrList.add(j, tmpData);
        	}
    		tmpData.put("offerAttrList", offerAttrList);
    		
    		tmpData.put("orderStaffid", userProductInfos.getData(i).getString("UPDATE_STAFF_ID", ""));
    		tmpData.put("orderChnn", StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_DEPART", "DEPART_ID", "DEPART_NAME",
    				userProductInfos.getData(i).getString("UPDATE_DEPART_ID", "")));
    		tmpData.put("remark", userProductInfos.getData(i).getString("REMARK", ""));
    		
    		tmpData.put("bossId", productId+"|P");
    		tmpData.put("prodInstId", userProductInfos.getData(i).getString("INST_ID", ""));
    		tmpData.put("isRemoved", "");
    		tmpData.put("instructions", "");
    		
    		tmpData.put("orderDep", userProductInfos.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
    		tmpData.put("operModuleCode", "");
    		tmpData.put("operModuleName", "");
    		tmpData.put("operArea", "");
    		tmpData.put("shortNum", "");
    		tmpData.put("servAreaName", "");
    		tmpData.put("offerSts", "");//用户订购实例状态
    		tmpData.put("flowLongDistanceAjust", "");
    		tmpData.put("voiceLongDistanceAjust", "");
    		tmpData.put("flowBusiType", "");
    		tmpData.put("bindActId", "");
    		tmpData.put("bindActName", "");
    		tmpData.put("bindActEndTime", "");
    		tmpData.put("offerPriceDesc", "");
    		tmpData.put("lastOrderStaff", "");
    		tmpData.put("lastOrderChnn", "");
    		tmpData.put("lastOrderTime", "");
    		tmpData.put("priority", "");
    		tmpData.put("isLog", "0");

    		ids.add(i, tmpData);
    	}
		}

    	outData.put("offerList", ids);
    	outData.put("resultRows", ids.size());
    	outData.put("userId", ucaData.getUserId());
    	outData.put("logUrl", "");
       return prepareOutResult(0,"",outData);
	}
	//封装03自有业务K3
	public IData qryMyselfBusi(String queryMode,String busiCode,IData outData,IDataset ids)throws Exception{

    	UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
    	IDataset userPlatsvcs = new DatasetList();
    	IDataset acctOpenInfos = new DatasetList();
    	if("0".equals(queryMode) || "".equals(queryMode))
    	{
			userPlatsvcs = queryPlatorderAllinfoByUserId(ucaData.getUserId());
			acctOpenInfos = queryOpenInfo(busiCode);

    	}
		else if ("1".equals(queryMode))
    	{
			userPlatsvcs = queryPlatorderHisinfoByUserId(ucaData.getUserId());

    	}
		else
    	{
    		IData result = prepareOutResult(1,"不支持的queryMode值："+queryMode,outData);
            return result;
    	}
    	//List<PlatSvcTradeData> userPlatsvc = ucaData.getUserPlatSvcs();
    	int k=0;
    	for (int i=0 ; i < userPlatsvcs.size() ; i++)
    	{
    		IData tmpData = new DataMap();
    		
    		IDataset spServiceInfo = UpcCall.qrySpServiceSpInfo(userPlatsvcs.getData(i).getString("SERVICE_ID", ""),"Z");
    		if (IDataUtil.isNotEmpty(spServiceInfo)){
    			//2018-5-11与符龙斌确认：自有业务就展示自有业务；梦网业务展示除自有业务外所有的平台业务；
    			if ("2".equals(spServiceInfo.getData(0).getString("RSRV_STR9", "")) ||"".equals(spServiceInfo.getData(0).getString("RSRV_STR9", ""))){
    				continue;
    			}
    			tmpData.put("spCode", spServiceInfo.getData(0).getString("SP_CODE", ""));
    			tmpData.put("bizCode", spServiceInfo.getData(0).getString("BIZ_CODE", ""));
    			tmpData.put("spName", spServiceInfo.getData(0).getString("SP_NAME", ""));
    			tmpData.put("servType", spServiceInfo.getData(0).getString("BIZ_TYPE_CODE", ""));
    			
    			String billFlag = spServiceInfo.getData(0).getString("BILL_TYPE", "");
        		if ("0".equals(billFlag))
        		{
        			tmpData.put("billType", "免费");	
        		}
        		else if ("1".equals(billFlag))
        		{
        			tmpData.put("billType", "按条计费");	
        		}
        		else if ("2".equals(billFlag))
        		{
        			tmpData.put("billType", "包月计费");	
        		}
        		else if ("3".equals(billFlag))
        		{
        			tmpData.put("billType", "包时计费");	
        		}
        		else if ("4".equals(billFlag))
        		{
        			tmpData.put("billType", "包次计费");	
        		}
        		else {
        			tmpData.put("billType", "");	
        		}
        		int price = Integer.parseInt(spServiceInfo.getData(0).getString("PRICE", "0"));
        		if( price >= 0 )
        		{
        			price = price/10;
        		}
        		tmpData.put("offerPrice", String.valueOf(price)+"/月");
    		}
    		else
    		{
    			tmpData.put("spCode", "");
    			tmpData.put("bizCode", "");
    			tmpData.put("spName", "");
    			tmpData.put("servType", "");
    			tmpData.put("billType", "");
    		}
    		tmpData.put("offerId", userPlatsvcs.getData(i).getString("SERVICE_ID", "")+"|Z");
    		tmpData.put("offerName", UpcCall.qryOfferNameByOfferTypeOfferCode(userPlatsvcs.getData(i).getString("SERVICE_ID", ""),"Z"));
    		tmpData.put("upOfferId", "");
    		tmpData.put("upOfferName", "");
    		tmpData.put("isPackage", "0");
    		
/*        			tmpData.put("offerPrice", "");
    		if(IDataUtil.isNotEmpty(acctOpenInfos)){
				for(int j=0;j<acctOpenInfos.size();j++){
					if(tmpData.getString("bizCode","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_INS_ID",""))
							&& tmpData.getString("spCode","").equals(acctOpenInfos.getData(j).getString("FEEPOLICY_ID",""))){
						Float price = Float.parseFloat(acctOpenInfos.getData(j).getString("MONTH_RENT","0"))*100;
						if(price > 0)
						{
							tmpData.put("offerPrice",price+"/月");
						}
					}
				}
    		}*/
    		
    		if("0".equals(queryMode) || "".equals(queryMode))
        	{
    			tmpData.put("status", "0");
        	}
    		else
    		{
    			tmpData.put("status", "");
        	}
    		tmpData.put("orderTime", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvcs.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
    		tmpData.put("offerDesc", UPlatSvcInfoQry.getSvcExplainBySvcId(userPlatsvcs.getData(i).getString("SERVICE_ID", "")));
    		tmpData.put("offerType", "Z");
    		tmpData.put("offerTypeName", "平台服务");
    		tmpData.put("brandId", "");
    		tmpData.put("brandName", "");
    		tmpData.put("isMain", "0");
    		tmpData.put("chooseTag", "");
    		tmpData.put("canUnsub", "1");

    		tmpData.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvcs.getData(i).getString("START_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
			tmpData.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(userPlatsvcs.getData(i).getString("END_DATE", ""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
    		
    		IDataset offerAttrList= new DatasetList();
    		List<AttrTradeData> userAttrs = ucaData.getUserAttrsByRelaInstId(userPlatsvcs.getData(i).getString("INST_ID", ""));
    		for (int j=0 ; j < userAttrs.size() ; j++)
        	{
	        	IData tmpData1 = new DataMap();
        		tmpData1.put("attrCode", userAttrs.get(j).getAttrCode());
        		tmpData1.put("attrName", "");
        		tmpData1.put("attrDesc", "");
        		tmpData1.put("attrValue", userAttrs.get(j).getAttrValue());
        		tmpData1.put("attrValueDesc", "");
        		offerAttrList.add(j, tmpData1);
        	}
    		tmpData.put("offerAttrList", offerAttrList);
    		tmpData.put("orderStaffid", userPlatsvcs.getData(i).getString("UPDATE_STAFF_ID", ""));
    		tmpData.put("orderChnn", userPlatsvcs.getData(i).getString("UPDATE_DEPART_ID", ""));
    		tmpData.put("remark", userPlatsvcs.getData(i).getString("REMARK", ""));
    		
    		tmpData.put("bossId", userPlatsvcs.getData(i).getString("SERVICE_ID", "")+"|Z");
    		tmpData.put("prodInstId", userPlatsvcs.getData(i).getString("INST_ID", ""));
    		tmpData.put("isRemoved", "");
    		tmpData.put("instructions", "");
    		
    		tmpData.put("orderDep", userPlatsvcs.getData(i).getString("UPDATE_DEPART_ID", ""));//受理部门
    		tmpData.put("operModuleCode", "");
    		tmpData.put("operModuleName", "");
    		tmpData.put("operArea", "");
    		tmpData.put("shortNum", "");
    		tmpData.put("servAreaName", "");
    		tmpData.put("offerSts", "");//用户订购实例状态
    		tmpData.put("flowLongDistanceAjust", "");
    		tmpData.put("voiceLongDistanceAjust", "");
    		tmpData.put("flowBusiType", "");
    		tmpData.put("bindActId", "");
    		tmpData.put("bindActName", "");
    		tmpData.put("bindActEndTime", "");
    		tmpData.put("offerPriceDesc", "");
    		tmpData.put("lastOrderStaff", "");
    		tmpData.put("lastOrderChnn", "");
    		tmpData.put("lastOrderTime", "");
    		tmpData.put("priority", "");
    		tmpData.put("isLog", "0");

    		ids.add(k, tmpData);
    		k++;
    	}
    	outData.put("offerList", ids);
    	outData.put("resultRows", ids.size());
    	outData.put("userId", ucaData.getUserId());
    	outData.put("logUrl", "");

        return prepareOutResult(0,"",outData);
	}
	
	private IData getParams(IData param,String dataName) throws Exception {
		Object o = null;
		if("crmpfPubInfo".equals(dataName))
		{
			o = param.get("crmpfPubInfo");
		}
    	if(o instanceof Map) {
    		return new DataMap((Map) o);
		}else if(o instanceof IData) {
			return (IData) o;
		}else if (o instanceof String) {
			return new DataMap(String.valueOf(o));
		}
		throw new Exception("未识别的params参数...params=" + o +",type=" + o.getClass());
    }
	private IDataset getDatasets(IData param,String ListName) throws Exception {
		Object o = null;
		if("packageList".equals(ListName))
		{
			o = param.get("packageList");
		}
		else if("spList".equals(ListName))
		{
			o = param.get("spList");
		}
		else if("offerList".equals(ListName))
		{
			o = param.get("offerList");
		}
		
    	if(o instanceof List) {
    		IDataset ids = new DatasetList();
    		for (int i = 0; i < ((List) o).size();i++)
    		{
    			IData tmpData = new DataMap();
    			if(((List)o).get(i) instanceof Map) {
            		//System.out.println("============getDatasetList========Map====((List)o).get(i)"+((List)o).get(i));
    				tmpData= new DataMap((Map) (((List)o).get(i)));
    			}else if(((List)o).get(i) instanceof IData) {
    				//System.out.println("============getDatasetList========IData====((List)o).get(i)"+((List)o).get(i));
    				tmpData=(IData) ((List)o).get(i);
    			}else if (((List)o).get(i) instanceof String) {
    				//System.out.println("============getDatasetList========String====((List)o).get(i)"+((List)o).get(i));
    				tmpData= new DataMap(String.valueOf(((List)o).get(i)));
    			}
        		//System.out.println("============getDatasetList============tmpData"+tmpData);

    			ids.add(tmpData);
    		}
    		//System.out.println("============getDatasetList============ids"+ids);
    		return ids  ;
		}else if(o instanceof IDataset) {
			//System.out.println("============getDatasetList============IDataset"+o);
			return (IDataset) o;
		}else if (o instanceof String) {
			//System.out.println("============getDatasetList============String"+o);
			return new DatasetList(String.valueOf(o));
		}
		throw new Exception("未识别的params参数..."+ListName+"=" + o +",type=" + o.getClass());
    }
	private static String chgFormat(String strDate, String oldForm, String newForm) throws Exception{
		if (null == strDate)
        {
            throw new NullPointerException();
        }

        DateFormat oldDf = new SimpleDateFormat(oldForm);
        Date date = oldDf.parse(strDate);

		String newStr = "";
        DateFormat newDf = new SimpleDateFormat(newForm);
        newStr = newDf.format(date);        
		return newStr;
	}
	private static IDataset getUserSaleActiveByStartEndDate(String userId, String processTag,String startDate, String endDate) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("START_DATE", startDate);
		params.put("END_DATE", endDate);
		IDataset resultset = new DatasetList();
		if ( "1".equals(processTag) ) //未结束 (正常、正在生效的、待生效的等)
		{
			SQLParser dctparser = new SQLParser(params);
			dctparser.addSQL(" SELECT T.* FROM TF_F_USER_SALE_ACTIVE T ");
			dctparser.addSQL(" WHERE USER_ID = :USER_ID ");
			dctparser.addSQL(" AND PARTITION_ID = MOD(:USER_ID, 10000) ");
			dctparser.addSQL(" AND (ACCEPT_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :START_DATE IS NULL) ");   
			dctparser.addSQL(" AND (ACCEPT_DATE <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :END_DATE IS NULL) ");
			//dctparser.addSQL(" AND end_date >= sysdate ");
			dctparser.addSQL(" AND PROCESS_TAG = '0' ");	
			dctparser.addSQL(" AND NVL(T.RSRV_DATE2,T.END_DATE) > Sysdate ");	//REQ201909060007 _关于更改营销活动状态口径的需求
			dctparser.addSQL(" ORDER BY ACCEPT_DATE ");			
			resultset = Dao.qryByParse(dctparser);
		}
		else if ( "2".equals(processTag) )//查询已结束（失效的、已取消、已返销）
		{
			SQLParser dctparser = new SQLParser(params);
			dctparser.addSQL(" SELECT * FROM TF_F_USER_SALE_ACTIVE ");
			dctparser.addSQL(" WHERE USER_ID = :USER_ID ");
			dctparser.addSQL(" AND PARTITION_ID = MOD(:USER_ID, 10000) ");
			dctparser.addSQL(" AND (ACCEPT_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :START_DATE IS NULL) ");   
			dctparser.addSQL(" AND (ACCEPT_DATE <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :END_DATE IS NULL) ");
			//dctparser.addSQL(" AND end_date < sysdate ");
			dctparser.addSQL(" AND NVL(RSRV_DATE2,END_DATE) < Sysdate ");
			dctparser.addSQL(" union SELECT * From tf_fh_user_sale_active ");
			dctparser.addSQL(" WHERE USER_ID = :USER_ID ");
			dctparser.addSQL(" AND PARTITION_ID = MOD(:USER_ID, 10000) ");
			dctparser.addSQL(" AND (ACCEPT_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :START_DATE IS NULL) ");   
			dctparser.addSQL(" AND (ACCEPT_DATE <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :END_DATE IS NULL) ");
			//dctparser.addSQL(" AND end_date < sysdate ");
			dctparser.addSQL(" AND NVL(RSRV_DATE2,END_DATE) < Sysdate ");//REQ201909060007 _关于更改营销活动状态口径的需求
			resultset = Dao.qryByParse(dctparser);
		}
		else if ( "".equals(processTag) || "0".equals(processTag)) //查询全部
		{
			SQLParser dctparser = new SQLParser(params);
			dctparser.addSQL(" SELECT * FROM TF_F_USER_SALE_ACTIVE ");
			dctparser.addSQL(" WHERE USER_ID = :USER_ID ");
			dctparser.addSQL(" AND PARTITION_ID = MOD(:USER_ID, 10000) ");
			dctparser.addSQL(" AND (ACCEPT_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :START_DATE IS NULL) ");   
			dctparser.addSQL(" AND (ACCEPT_DATE <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :END_DATE IS NULL) ");
			dctparser.addSQL(" union SELECT * From tf_fh_user_sale_active ");
			dctparser.addSQL(" WHERE USER_ID = :USER_ID ");
			dctparser.addSQL(" AND PARTITION_ID = MOD(:USER_ID, 10000) ");
			dctparser.addSQL(" AND (ACCEPT_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :START_DATE IS NULL) ");   
			dctparser.addSQL(" AND (ACCEPT_DATE <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :END_DATE IS NULL) ");
			resultset = Dao.qryByParse(dctparser);
		}
		return resultset;
	}	
	
	public IDataset queryWideUserInfoForYH(IData param) throws Exception
    {
		IData widenetInfo = new DataMap();
		IData userInfo = new DataMap();
		String msisdn = param.getString("SERIAL_NUMBER");
		if (!"KD_".equals(msisdn.substring(0, 3))) {
			msisdn = "KD_" + msisdn;
		}
		IDataset userInfos = UserInfoQry.getUserInfoBySn(msisdn,"0");
		if (IDataUtil.isEmpty(userInfos)) {
			//CSAppException.apperr(WidenetException.CRM_WIDENET_4);
			widenetInfo.put("IS_WIDENET_USER", "0");
			return IDataUtil.idToIds(widenetInfo);
		}
		userInfo = userInfos.getData(0);
		widenetInfo.put("IS_WIDENET_USER", "1");
		String kdUserId = userInfo.getString("USER_ID", "");
		param.put("USER_ID", kdUserId);
		widenetInfo.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER", ""));
		widenetInfo.put("IN_DATE", userInfo.getString("IN_DATE", ""));
		widenetInfo.put("CITY_CODE", userInfo.getString("CITY_CODE", ""));
        IDataset wideCustInfos = CustomerInfoQry.getCustomerInfoByUserId(kdUserId);
        if (IDataUtil.isNotEmpty(wideCustInfos))
        {
            widenetInfo.put("CUST_NAME", wideCustInfos.getData(0).getString("CUST_NAME", ""));
        }
		IDataset wideBaseInfos = Qry360InfoDAO.getUserWidenetInfo(param);
		if (IDataUtil.isNotEmpty(wideBaseInfos))
        {
            widenetInfo.put("STAND_ADDRESS", wideBaseInfos.getData(0).getString("STAND_ADDRESS", ""));
            widenetInfo.put("WIDE_TYPE", wideBaseInfos.getData(0).getString("RSRV_STR2", ""));
            String widetype = wideBaseInfos.getData(0).getString("RSRV_STR2", "");String widetype_name = "";
            if("1".equals(widetype))
            {
            	widetype_name="移动FTTB";
            }else if("2".equals(widetype))
            {
            	widetype_name="铁通ADSL";
            }else if("3".equals(widetype))
            {
            	widetype_name="移动FTTH";
            }else if("4".equals(widetype))
            {
            	widetype_name="校园宽带";
            }else if("5".equals(widetype))
            {
            	widetype_name="铁通FTTH";
            }else if("6".equals(widetype))
            {
            	widetype_name="铁通FTTB";
            }
            widenetInfo.put("WIDE_TYPE_NAME", widetype_name);


        }
		IDataset discntDataset = Qry360InfoDAO.queryWideNetUserDiscnt(param);
		if (IDataUtil.isNotEmpty(discntDataset))
        {
			widenetInfo.put("DISCNT_NAME", discntDataset.getData(0).getString("DISCNT_NAME", ""));
			widenetInfo.put("DISCNT_CODE", discntDataset.getData(0).getString("DISCNT_CODE", ""));
        }
		IDataset productInfo = Qry360InfoDAO.queryWideNetUserProductInfo(param);
		if (IDataUtil.isNotEmpty(productInfo))
        {
			widenetInfo.put("PRODUCT_NAME", productInfo.getData(0).getString("PRODUCT_NAME", ""));
			widenetInfo.put("PRODUCT_ID", productInfo.getData(0).getString("PRODUCT_ID", ""));
			widenetInfo.put("BRAND_CODE", productInfo.getData(0).getString("BRAND_CODE", ""));
			widenetInfo.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(productInfo.getData(0).getString("BRAND_CODE", "")));
        }
        IData oweFeeInfo = AcctCall.getOweFeeByUserId(kdUserId);
        String realBalance = "0";
        String allOweFee = "0";
        if(IDataUtil.isNotEmpty(oweFeeInfo)){
        	realBalance = oweFeeInfo.getString("ACCT_BALANCE","0");
        	allOweFee = oweFeeInfo.getString("ALL_OWE_FEE","0");
        }
        widenetInfo.put("LEFT_MONEY",realBalance);
        widenetInfo.put("OWE_FEE",allOweFee);

/*        if(Float.parseFloat(allOweFee) >= 0)
        {
        	widenetInfo.put("OWE_FEE_TAG","否");
        }
        else
        {
        	widenetInfo.put("OWE_FEE_TAG","是");
        }*/
        
		return IDataUtil.idToIds(widenetInfo);
    }
	
	public IDataset queryUserCommodityInfo(IData param) throws Exception
    {
		IData returnInfo =  new DataMap();
		 IDataset ids =  new DatasetList();
		String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");
		String offerType = IDataUtil.chkParam(param, "OFFER_TYPE");//产品-P,服务-S,平台服务-Z,优惠-D
		String offerCode = IDataUtil.chkParam(param, "OFFER_CODE");//产品、服务或平台服务编码
		String effectTag = param.getString("EFFECT_TAG","");//是否包含失效标志:0-不包含；1-包含
		String forwardDays = param.getString("FORWARD_DAYS","");//往前查询的天数
		IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0",serialNumber);
		if(IDataUtil.isNotEmpty(userInfos)){
			param.put("USER_ID", userInfos.getData(0).getString("USER_ID", ""));
		}
		else
		{
			Utility.error("-1", null, "无法根据服务号码查询到用户资料！");
		}
		if("P".equals(offerType))
		{
			SQLParser sqlParser = new SQLParser(param);
	        sqlParser.addSQL(" select PARTITION_ID,to_char(USER_ID) USER_ID,to_char(USER_ID_A) USER_ID_A,PRODUCT_ID,PRODUCT_MODE,BRAND_CODE,to_char(INST_ID) INST_ID,to_char(CAMPN_ID) CAMPN_ID,to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,to_char(RSRV_NUM4) RSRV_NUM4,to_char(RSRV_NUM5) RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 ");
	        sqlParser.addSQL("  from TF_F_USER_PRODUCT a");
	        sqlParser.addSQL(" where a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
	        sqlParser.addSQL(" and a.user_id = :USER_ID");
	        sqlParser.addSQL(" and a.product_id = :OFFER_CODE ");
	        if("".equals(effectTag) || "0".equals(effectTag))
	        {
	        	sqlParser.addSQL(" and sysdate between a.start_date and a.end_date ");
	        }
	        if(!"".equals(forwardDays) && !"0".equals(forwardDays))
	        {
	        	sqlParser.addSQL(" and floor(sysdate-trunc(a.END_DATE, 'DD')) <= :FORWARD_DAYS ");
	        }
	        ids =  Dao.qryByParse(sqlParser);
		}
		else if("S".equals(offerType))
		{
			SQLParser sqlParser = new SQLParser(param);
	        sqlParser.addSQL(" select PARTITION_ID,to_char(USER_ID) USER_ID,to_char(USER_ID_A) USER_ID_A,SERVICE_ID,MAIN_TAG,INST_ID,to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 ");
	        sqlParser.addSQL("  from TF_F_USER_SVC a");
	        sqlParser.addSQL(" where a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
	        sqlParser.addSQL(" and a.user_id = :USER_ID");
	        sqlParser.addSQL(" and a.service_id = :OFFER_CODE ");
	        if("".equals(effectTag) || "0".equals(effectTag))
	        {
	        	sqlParser.addSQL(" and sysdate between a.start_date and a.end_date ");
	        }
	        if(!"".equals(forwardDays) && !"0".equals(forwardDays))
	        {
	        	sqlParser.addSQL(" and floor(sysdate-trunc(a.END_DATE, 'DD')) <= :FORWARD_DAYS ");
	        }
	        ids =  Dao.qryByParse(sqlParser);
		}
		else if("D".equals(offerType))
		{
			SQLParser sqlParser = new SQLParser(param);
	        sqlParser.addSQL(" select PARTITION_ID, to_char(USER_ID) USER_ID, to_char(USER_ID_A) USER_ID_A, DISCNT_CODE, SPEC_TAG, RELATION_TYPE_CODE, to_char(INST_ID) INST_ID, to_char(CAMPN_ID) CAMPN_ID, to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, to_char(RSRV_NUM4) RSRV_NUM4, to_char(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3 ");
	        sqlParser.addSQL("  from TF_F_USER_DISCNT a");
	        sqlParser.addSQL(" where a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
	        sqlParser.addSQL(" and a.user_id = :USER_ID");
	        sqlParser.addSQL(" and a.discnt_code = :OFFER_CODE ");
	        if("".equals(effectTag) || "0".equals(effectTag))
	        {
	        	sqlParser.addSQL(" and sysdate between a.start_date and a.end_date ");
	        }
	        if(!"".equals(forwardDays) && !"0".equals(forwardDays))
	        {
	        	sqlParser.addSQL(" and floor(sysdate-trunc(a.END_DATE, 'DD')) <= :FORWARD_DAYS ");
	        }
	        ids =  Dao.qryByParse(sqlParser);
		}
		else if("Z".equals(offerType))
		{
			SQLParser sqlParser = new SQLParser(param);
	        sqlParser.addSQL(" select PARTITION_ID,to_char(USER_ID) USER_ID,SERVICE_ID,BIZ_STATE_CODE,to_char(FIRST_DATE, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE,to_char(FIRST_DATE_MON, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE_MON,GIFT_SERIAL_NUMBER,GIFT_USER_ID,to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,to_char(RSRV_NUM4) RSRV_NUM4,to_char(RSRV_NUM5) RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10,to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3 ");
	        sqlParser.addSQL("  from TF_F_USER_PLATSVC a");
	        sqlParser.addSQL(" where a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
	        sqlParser.addSQL(" and a.user_id = :USER_ID");
	        sqlParser.addSQL(" and a.service_id = :OFFER_CODE ");
	        if("".equals(effectTag) || "0".equals(effectTag))
	        {
	        	sqlParser.addSQL(" and sysdate between a.start_date and a.end_date ");
	        }
	        if(!"".equals(forwardDays) && !"0".equals(forwardDays))
	        {
	        	sqlParser.addSQL(" and floor(sysdate-trunc(a.END_DATE, 'DD')) <= :FORWARD_DAYS ");
	        }
	        ids =  Dao.qryByParse(sqlParser);
		}
		else 
		{
			Utility.error("-1", null, "不支持的查询类型,OFFER_TYPE:"+offerType);
		}
		if(IDataUtil.isNotEmpty(ids)){
			returnInfo.putAll(ids.getData(0));
			returnInfo.put("RETURN_TAG", "1");
		}
		else{

			returnInfo.put("RETURN_TAG", "0");
		}
		return IDataUtil.idToIds(returnInfo);
    }
	
	public IDataset queryFubaoDiscntQualification(IData param) throws Exception
    {
		IData returnInfo =  new DataMap();
		String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");
		String userId="";
		String strDiscntCode="";
		int k = 0;
		IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0",serialNumber);
		if(IDataUtil.isNotEmpty(userInfos)){
			userId = userInfos.getData(0).getString("USER_ID", "") ;
		}
		else
		{
			//Utility.error("-1", null, "无法根据服务号码查询到用户资料！");
			returnInfo.put("RETURN_TAG", "0");
			returnInfo.put("DISCNT_CODE", "");
		}
		IDataset userDiscntInfos = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
		if(IDataUtil.isNotEmpty(userDiscntInfos))
		{
			for (int i = 0; i < userDiscntInfos.size(); i++) 
			{
				String discntCode = userDiscntInfos.getData(i).getString("DISCNT_CODE", "");
				String endDate = userDiscntInfos.getData(i).getString("END_DATE", "");
	    		String sysdate = SysDateMgr.getSysDate();
				int eMonth =SysDateMgr.monthInterval(endDate,sysdate);//计算剩余多少天
				if("84008841".equals(discntCode) || ("84009643".equals(discntCode) &&  eMonth <= 3))
				{
					//strDiscntCode += "84009036";
					k++;
				}
				if("84008842".equals(discntCode) || "84008843".equals(discntCode) || ("84009644".equals(discntCode) &&  eMonth <= 3)
						|| ("84009645".equals(discntCode) &&  eMonth <= 3))
				{
					//strDiscntCode += "84009037";
					k += 2;
				}
				if("84009036".equals(discntCode) )
				{
					k--;
				}
				if("84009037".equals(discntCode) )
				{
					k -= 2;
				}
			}
			if ( k==1 )
			{
				returnInfo.put("DISCNT_CODE", "84009036");
				returnInfo.put("RETURN_TAG", "1");	
			}
			else if ( k==2 )
			{
				returnInfo.put("DISCNT_CODE", "84009037");
				returnInfo.put("RETURN_TAG", "1");	
			}
			else if ( k==3 )
			{
				returnInfo.put("DISCNT_CODE", "84009036|84009037");
				returnInfo.put("RETURN_TAG", "1");	
			}
			else if ( k==4 )
			{
				returnInfo.put("DISCNT_CODE", "84009037|84009037");
				returnInfo.put("RETURN_TAG", "1");	
			}
			else if ( k==5 )
			{
				returnInfo.put("DISCNT_CODE", "84009036|84009037|84009037");
				returnInfo.put("RETURN_TAG", "1");	
			}
			else
			{
				returnInfo.put("DISCNT_CODE", "");
				returnInfo.put("RETURN_TAG", "0");
			}
		}
		
		else 
		{
			returnInfo.put("RETURN_TAG", "0");
			returnInfo.put("DISCNT_CODE", "");
		}
		return IDataUtil.idToIds(returnInfo);
    }
	
	public static IDataset getExpirSaleActiveByUserId(String user_id) throws Exception
	{
		StringBuilder sql = new StringBuilder(1000);
		sql.append("SELECT t.SERIAL_NUMBER,t.PRODUCT_ID,t.PRODUCT_NAME,t.PACKAGE_ID,t.PACKAGE_NAME,(t.OPER_FEE + t.ADVANCE_PAY) / 100 CAMPN_FEE,t.TRADE_STAFF_ID,t.START_DATE,t.END_DATE,t.UPDATE_TIME,t.ACCEPT_DATE,t.REMARK ");
		sql.append("FROM TF_F_USER_SALE_ACTIVE t ");
		sql.append("WHERE t.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
		sql.append("AND t.USER_ID = :USER_ID ");
		sql.append("AND (sysdate > t.END_DATE or sysdate < t.START_DATE)");
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		return Dao.qryBySql(sql, param);

	}
	public static IDataset queryUserExpiryDiscntsByUserId(String user_id) throws Exception
	{

		IData iData = new DataMap();
		iData.put("USER_ID", user_id);

		SQLParser dctparser = new SQLParser(iData);

		dctparser.addSQL(" SELECT * ");
		dctparser.addSQL("   FROM tf_f_user_discnt ");
		dctparser.addSQL("  WHERE user_id = TO_NUMBER(:USER_ID) ");
		dctparser.addSQL("    AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000) ");
		dctparser.addSQL("    AND Sysdate > END_DATE ");
		dctparser.addSQL("  ORDER BY Start_Date ");
		IDataset resultset = Dao.qryByParse(dctparser);
		return resultset;
	}
	private IDataset qryRelationInfo(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select a.relation_type_code, a.user_id_a, a.serial_number_a, a.role_code_a, a.user_id_b, a.serial_number_b,'TD_S_RELATION_ROLE_1_'||a.relation_type_code rcode, ");
        parser.addSQL(" a.role_code_b, a.orderno, a.short_code, a.start_date, ");
        parser.addSQL(" a.end_date,a.update_staff_id,a.update_depart_id,a.remark from Tf_f_Relation_Uu a ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.user_id_b = :USER_ID ");
        parser.addSQL(" and a.end_date >= sysdate");
        parser.addSQL(" and a.relation_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_RELATIONQUERY' ) ");
        parser.addSQL(" Union ");
        parser.addSQL(" select a.relation_type_code, a.user_id_a, a.serial_number_a, a.role_code_a, a.user_id_b, a.serial_number_b,'TD_S_RELATION_ROLE_1_'||a.relation_type_code rcode, ");
        parser.addSQL(" a.role_code_b, a.orderno, a.short_code, a.start_date, ");
        parser.addSQL(" a.end_date,a.update_staff_id,a.update_depart_id,a.remark from Tf_f_Relation_bb a ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.user_id_b = :USER_ID ");
        parser.addSQL(" and a.end_date >= sysdate");
        parser.addSQL(" and a.relation_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_RELATIONQUERY' ) ");

        IDataset out = UserCommUtil.qryByParse(parser);
        return out;
    }
    private IDataset qryInvalidRelationInfo(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select a.relation_type_code, a.user_id_a, a.serial_number_a, a.role_code_a, a.user_id_b, a.serial_number_b,'TD_S_RELATION_ROLE_1_'||a.relation_type_code rcode, ");
        parser.addSQL(" a.role_code_b, a.orderno, a.short_code, a.start_date,a.inst_id, ");
        parser.addSQL(" a.end_date,a.update_staff_id,a.update_depart_id,a.remark from Tf_f_Relation_Uu a ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.user_id_b = :USER_ID ");
        parser.addSQL(" and a.end_date < sysdate");
        parser.addSQL(" and a.relation_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_RELATIONQUERY' ) ");
        parser.addSQL(" Union ");
        parser.addSQL(" select a.relation_type_code, a.user_id_a, a.serial_number_a, a.role_code_a, a.user_id_b, a.serial_number_b,'TD_S_RELATION_ROLE_1_'||a.relation_type_code rcode, ");
        parser.addSQL(" a.role_code_b, a.orderno, a.short_code, a.start_date, a.inst_id, ");
        parser.addSQL(" a.end_date,a.update_staff_id,a.update_depart_id,a.remark from Tf_f_Relation_bb a ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.user_id_b = :USER_ID ");
        parser.addSQL(" and a.end_date < sysdate");
        parser.addSQL(" and a.relation_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_RELATIONQUERY' ) ");

        IDataset out = UserCommUtil.qryByParse(parser);
        return out;
    }
    public IDataset queryOpenInfo(String serialNumber) throws Exception
    {
    	
    	IData result = new DataMap();
    	IData param = new DataMap();
        result = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
            return null;
        }
        String userId = result.getString("USER_ID","");
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", userId);    	
    	IDataOutput output = CSAppCall.callAcct("AM_CRM_QryBusinessForCustServ", param, true);
        return output.getData();
        
    }
    
	public static IDataset queryUserMainElements(String userId,String productId) throws Exception
	{

		IData iData = new DataMap();
		iData.put("USER_ID", userId);
		iData.put("PRODUCT_ID", productId);
		SQLParser dctparser = new SQLParser(iData);

		dctparser.addSQL(" select s.discnt_code element_id,  s.inst_id from tf_f_user_discnt s, tf_f_user_offer_rel t  ");
		dctparser.addSQL("    where t.user_id = :USER_ID and t.offer_code = :PRODUCT_ID ");
		dctparser.addSQL("     and t.end_date > sysdate and s.inst_id=t.rel_offer_ins_id ");
		dctparser.addSQL("     and t.offer_type = 'P' and t.rel_offer_type = 'D' ");
		dctparser.addSQL("     and t.user_id = s.user_id   ");
		dctparser.addSQL("     and sysdate between s.start_date and s.end_date ");
		IDataset resultset = Dao.qryByParse(dctparser);
		return resultset;
	}
	
    public static IDataset qryComRelOffersByOfferId(String productId) throws Exception
    {
        IDataset idsOfferInfos = UpcCall.qryComRelOffersByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId);
   	    if(IDataUtil.isNotEmpty(idsOfferInfos))
   	    {
   	    	for(int i = 0; i < idsOfferInfos.size(); i++)
   	    	{
   	    		IData idOffer = idsOfferInfos.getData(i);
   	    		if(!("D".equals(idOffer.getString("OFFER_TYPE", ""))))
   	    		{
   	    			idsOfferInfos.remove(i);
   	    			i--;
   	    		}else
   	    		{
   	    			idOffer.put("ELEMENT_ID",idOffer.getString("OFFER_CODE"));
   	    			idOffer.put("MAIN_TAG", idOffer.getString("IS_MAIN_SVC"));
   	    			idOffer.put("ELEMENT_TYPE", idOffer.getString("OFFER_TYPE"));
   	    		}
   	    	}
   	    }
   	    return idsOfferInfos;
    }
	
	public static IDataset queryUserSaleActives(String userId,String productId,String productName,String packageId,
			String packageName,String startDate, String endDate, String state) throws Exception
	{

		IData iData = new DataMap();
		iData.put("USER_ID", userId);
		iData.put("PRODUCT_ID", productId);
		iData.put("PRODUCT_NAME", productName);
		iData.put("PACKAGE_ID", packageId);
		iData.put("PACKAGE_NAME", packageName);
		iData.put("START_DATE", startDate);
		iData.put("END_DATE", endDate);
		if("0".equals(state) || "".equals(state))
		{
			
		}
		else if("1".equals(state) )
		{
			iData.put("PROCESS_TAG", "0");
		}
		else if("2".equals(state) )
		{
			iData.put("PROCESS_TAG", "1");
		}
		SQLParser dctparser = new SQLParser(iData);

		dctparser.addSQL(" SELECT T.* FROM TF_F_USER_SALE_ACTIVE T ");
		dctparser.addSQL(" WHERE USER_ID = :USER_ID ");
		dctparser.addSQL(" AND PARTITION_ID = MOD(:USER_ID, 10000) ");
		dctparser.addSQL(" AND (PROCESS_TAG = :PROCESS_TAG OR :PROCESS_TAG IS NULL) ");
		dctparser.addSQL(" AND (ACCEPT_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :START_DATE IS NULL) ");   
		dctparser.addSQL(" AND (ACCEPT_DATE <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :END_DATE IS NULL) ");
		dctparser.addSQL(" AND (PRODUCT_ID LIKE '%' || :PRODUCT_ID || '%' OR :PRODUCT_ID IS NULL) ");
		dctparser.addSQL(" AND (PRODUCT_NAME LIKE '%' || :PRODUCT_NAME || '%' OR :PRODUCT_NAME IS NULL) ");
		dctparser.addSQL(" AND (PACKAGE_ID LIKE '%' || :PACKAGE_ID || '%' OR :PACKAGE_ID IS NULL) ");
		dctparser.addSQL(" AND (PACKAGE_NAME LIKE '%' || :PACKAGE_NAME || '%' OR :PACKAGE_NAME IS NULL) ");
		
		IDataset resultset = Dao.qryByParse(dctparser);
		return resultset;
	}
	
    public boolean getBeautifulTag(String serialNumber) throws Exception
    {  	
    	IDataset ids = ResCall.getMphonecodeInfo(serialNumber);
    	if (IDataUtil.isNotEmpty(ids))
    	{
    		String beautifulTag = ids.getData(0).getString("BEAUTIFUL_TAG","");
    		if("1".equals(beautifulTag))
    		{
    			return true;
    		}
    		else
    			return false;
    	}
    	else
    	{
    		return false;
    	}       
    }
    
    public static IDataset filterSalePackagesByParamAttr526_KF(IDataset salePackages, boolean isBuildExtCha) throws Exception
    {
        if(IDataUtil.isEmpty(salePackages))
        {
            return salePackages;
        }
        
        IDataset results = new DatasetList();
        
        IDataset configs = CommparaInfoQry.getCommparaByParaAttr("CSM", "526", CSBizBean.getTradeEparchyCode());
        
        for(int i=0;i<salePackages.size();i++)
        {
            IData salePackage = salePackages.getData(i);
            String offerCode = salePackage.getString("OFFER_CODE");
            if(StringUtils.isBlank(offerCode))
            {
                offerCode = salePackage.getString("PACKAGE_ID");
            }
            
            if(isInCommparaParamCodeConfigs(offerCode, configs))
            {
                continue;
            }
            
            
            String offerName = salePackage.getString("OFFER_NAME");
            if(StringUtils.isNotBlank(offerName))
            {
                salePackage.put("PACKAGE_NAME", offerName);
            }
            String packageId = salePackage.getString("PACKAGE_ID");
            if(StringUtils.isBlank(packageId))
            {
                salePackage.put("PACKAGE_ID", salePackage.getString("OFFER_CODE"));
            }
            if(isBuildExtCha)
            {
                IData extCha = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, offerCode, "TD_B_PACKAGE_EXT").getData(0);
                if(!salePackage.containsKey("RSRV_STR2"))
                {
                    salePackage.put("RSRV_STR2", extCha.getString("RSRV_STR2"));
                }
                
                if(!salePackage.containsKey("RSRV_STR5"))
                {
                    salePackage.put("RSRV_STR5", extCha.getString("RSRV_STR5"));
                }
                
                if(!salePackage.containsKey("RSRV_STR20"))
                {
                    salePackage.put("PACKAGE_TYPE", extCha.getString("RSRV_STR20"));
                }
            }
            results.add(salePackage);
        }
        
        return results;
    }
    
    public static boolean isInCommparaParamCodeConfigs(String objId, IDataset configs)throws Exception
    {
        if(IDataUtil.isEmpty(configs))
        {
            return false;
        }
        
        int size = configs.size();
        for (int i = 0; i < size; i++)
        {
            IData config = configs.getData(i);
            String paramCode = config.getString("PARAM_CODE");
            if (objId.equals(paramCode))
            {
                return true;
            }
        }
        return false;
    }
    public IData prepareOutResult(int i,String rtnMsg,IDataset outData)
    {
    	IData object = new DataMap();
    	IData result = new DataMap();

    	if (i==0)//成功
    	{
        	object.put("resultRows", outData.size()+"");
        	outData.remove("resultRows");
        	object.put("result", outData);
            object.put("respCode", "0");
            object.put("respDesc", "success");
            
            result.put("object", object);
    		result.put("rtnCode", "0");	
    		result.put("rtnMsg", "成功!");	
            return result;
    	}
    	else if(i==1)//失败
    	{
        	object.put("result", outData);
        	object.put("resultRows", 0);
            object.put("respCode", "-1");
            object.put("respDesc", rtnMsg);
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
    	}
    	return null;
    }

	public IData queryUserSpecialRoster(IData param) throws Exception{
			IData params = param.getData("params");
			String rosterTypeCode = params.getString("rosterTypeCode","");
			String isSpecialRoster = "1";//用户是否存在特殊名单 0--是，1--否
			IDataset rosterList = new DatasetList();//特殊名单列表 isSpecialRoster为1时必填
			String respCode = "0";
			String respDesc = "成功";
			String rtnCode = "0";
			String rtnMsg = "成功";
		try {
			String serialNumber = IDataUtil.chkParam(params,"servNumber");
			//查询用户是否在黑名单
			if("00".equals(rosterTypeCode) || "01".equals(rosterTypeCode) || "".equals(rosterTypeCode) || rosterTypeCode == null){
				IData data = new DataMap();
				data.put("SERIAL_NUMBER",serialNumber);
				SQLParser configParser = new SQLParser(data);
				configParser.addSQL("  SELECT T.*");
				configParser.addSQL("  FROM UCR_UEC.TD_S_PARAM T");
				configParser.addSQL("  WHERE 1 = 1");
				configParser.addSQL("   AND T.INTF_CODE = 'BLACKLIST'");
				configParser.addSQL("   AND T.PARAM_VALUE = '1'");
				configParser.addSQL("   AND T.PARAM_NAME = 'OPEN'");
				//查询短厅黑名单开关是否打开
				IDataset blackListConfig = Dao.qryByParse(configParser,"uec");
				if (IDataUtil.isNotEmpty(blackListConfig) && blackListConfig.size() > 0) {
					SQLParser parser = new SQLParser(data);
					parser.addSQL("  SELECT T.*");
					parser.addSQL("  FROM UCR_UEC.TD_S_BLACKALIST_CONFIG T");
					parser.addSQL("  WHERE 1 = 1");
					parser.addSQL("   AND T.SERIAL_NUMBER = :SERIAL_NUMBER");
					parser.addSQL("   AND T.STATE = '0'");
					parser.addSQL("   AND SYSDATE BETWEEN T.BEGIN_TIME AND T.END_TIME");
					parser.addSQL("   AND ROWNUM < 2 ");
					//查询用户是否在黑名单
					IDataset blackLists = Dao.qryByParse(parser,"uec");
					if (IDataUtil.isNotEmpty(blackLists) && blackLists.size() > 0) {
						isSpecialRoster = "0";
						IData temp = new DataMap();
						temp.put("rosterTypeCode","01");
						temp.put("rosterTypeName","黑名单");
						temp.put("rosterCode","未知");
						temp.put("rosterName","短信营业厅黑名单");
						rosterList.add(temp);
					}
				}

			}
			//查询用户是否在红名单
			if("00".equals(rosterTypeCode) || "04".equals(rosterTypeCode) || "".equals(rosterTypeCode) || rosterTypeCode == null){
				UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
				if(uca.isRedUser()) {
					isSpecialRoster = "0";
					IData temp = new DataMap();
					temp.put("rosterTypeCode","04");
					temp.put("rosterTypeName","红名单");
					temp.put("rosterCode","未知");
					temp.put("rosterName","账务红名单");
					rosterList.add(temp);
				}
			}
		}catch (Exception e){
			log.error("queryUserSpecialRoster error==" + e.getMessage());
			rtnCode = "-9999";
			rtnMsg = "失败";
			respCode = "-1";
			respDesc = "用户特殊名单查询接口出错 ==" + e.getMessage();
		}
		IData rtnData = new DataMap();
		rtnData.put("rtnCode",rtnCode);
		rtnData.put("rtnMsg",rtnMsg);

		IData object = new DataMap();
		object.put("respCode",respCode);
		object.put("respDesc",respDesc);

		IData result = new DataMap();
		result.put("isSpecialRoster",isSpecialRoster);
		if ("1".equals(isSpecialRoster)) {
			result.put("rosterList",rosterList);
		}

		object.put("result",result);

		rtnData.put("object",object);
		rtnData.put("rtnCode",rtnCode);

		return rtnData;
	}
}