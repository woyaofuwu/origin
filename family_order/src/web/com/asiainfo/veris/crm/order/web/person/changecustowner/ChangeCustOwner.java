
package com.asiainfo.veris.crm.order.web.person.changecustowner;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChangeCustOwner extends PersonBasePage
{
    
	Logger logger=Logger.getLogger(ChangeCustOwner.class);
	
    /**
     * 校验证件实名制个数限制
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkPsptRealNameLimit(IRequestCycle cycle) throws Exception
    {
        IData pgData = getData();
        pgData.put(Route.ROUTE_EPARCHY_CODE, pgData.getString("USER_EPARCHY_CODE"));// 用eparchy_code路由速度快
        //edit by zhangxing3 for REQ201906130010关于本省一证五号优化需求
        IDataset retData = CSViewCall.call(this, "SS.CommonVerifySVC.CheckRealNameLimit", pgData);
        //IDataset retData = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkRealNameLimitByPspt", pgData);
        //edit by zhangxing3 for REQ201906130010关于本省一证五号优化需求

        setAjax(retData);
    }

    /**
     * 提交响应事件
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pgData = getData();
        pgData.put("SERIAL_NUMBER", pgData.getString("AUTH_SERIAL_NUMBER"));
        IDataset retSet = CSViewCall.call(this, "SS.ChangeCustOwnerRegSVC.tradeReg", pgData);
        //REQ201904190005全网用户数据查询平台分册V2.2.0改造需求
        IData bizCode = new DataMap();
		bizCode.put("SUBSYS_CODE", "CSM");
		bizCode.put("PARAM_ATTR", "9124");
		bizCode.put("PARAM_CODE", "SAVE_PIC_TAG");
		IDataset commparas = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByBizCode", bizCode);
        if (IDataUtil.isNotEmpty(commparas))
        {
        	if("1".equals(commparas.getData(0).getString("PARA_CODE1", "0")))
        	{
		        if(DataSetUtils.isBlank(retSet)) {
					CSViewException.apperr(CrmCommException.CRM_COMM_595);
				}
				String orderId = retSet.first().getString("ORDER_ID","");
		        String tradeId = retSet.first().getString("TRADE_ID","");
		        String sid=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		        orderId=orderId.substring(0, orderId.length()-4);
		        pgData.put("MOBILENUM", pgData.getString("SERIAL_NUMBER",""));
		        pgData.put("IDCARDNUM", pgData.getString("PSPT_ID",""));  
		        pgData.put("REMOTE_ORDER_ID", "COP898" + sid + orderId);
		        pgData.put("TRADE_ID", tradeId);
		        //System.out.println("-----------zhangxing3Guohu------------params:"+pgData);
		        CSViewCall.call(this, "SS.RemoteDestroyUserSVC.insPsptFrontBack", pgData);
        	}
        }
        //REQ201904190005全网用户数据查询平台分册V2.2.0改造需求
        
        setAjax(retSet);
    }

    public void queryUserBuisInfo(IRequestCycle cycle) throws Exception
    {
        IData pgData = getData();
        IData userInfo = new DataMap(pgData.getString("USER_INFO", ""));
        IData custInfo = new DataMap();
        /*
         * 绑定了无手机宽带统一付费业务的号码不允许办理过户
         */
        IData uuParam = new DataMap();
        uuParam.put("USER_ID_B",userInfo.getString("USER_ID"));
        uuParam.put("RELATION_TYPE_CODE","58");
        uuParam.put("ROLE_CODE_B","1");
        uuParam.put("ROUTE_EPARCHY_CODE",userInfo.getString("EPARCHY_CODE"));
        IDataset uuRela = CSViewCall.call(this, "CS.RelationUUQrySVC.getRelaFKByUserIdB", uuParam);
        if(IDataUtil.isNotEmpty(uuRela)){
        	uuParam.put("RESULT","1");
        	setAjax(uuParam);
        	return;
        }
        /**
         * BUG20171115091427_生僻字名字用户无法过户问题
         * @author zhuoyingzhi
         * @date 20171127
         */
        try {
       	    custInfo =new DataMap(pgData.getString("CUST_INFO", ""));
		} catch (Exception e) {
			//出现特殊字,则需要转换,获取客户信息方式
	        String  serialNumber=userInfo.getString("SERIAL_NUMBER","");
	        IData custInfoParam=new DataMap();
	        custInfoParam.put("CUST_ID", "");
	        custInfoParam.put("SERIAL_NUMBER", serialNumber);
	        custInfo = CSViewCall.callone(this, "SS.ChangeCardSVC.getCustInfo", custInfoParam);
		}
       /*****************************************************/        
        
        
        
        IData acctInfo = new DataMap(pgData.getString("ACCT_INFO", ""));
        

        setUserInfo(userInfo);
        setCustInfo(custInfo);
        setAcctInfo(acctInfo);


        IData param = new DataMap();
        /*param.put("ACCT_ID", pgData.getString("ACCT_ID"));
        param.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));
        param.put(StrUtil.getNotFuzzyKey(), true);// 强制不模糊化
        IDataset acctDataset = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryAcctInfoByAcctId", param);
        setAcctInfo(acctDataset.getData(0));

        param.clear();*/
        param.put("USER_ID", userInfo.getString("USER_ID"));
        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        param.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));

        // 查询有效服务信息
        IDataset userSvcInfos = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.queryUserServices", param);
        setSvcInfos(userSvcInfos);

        // 查询有效优惠信息
        IDataset userDiscntInfos = CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.getNowValidDiscntByUserId", param);
        setDiscntInfos(userDiscntInfos);
        
        IData foreCallback=new DataMap(); 
        
        //查询用户是否为宽带用户
        IDataset obtainWidenetInfo = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.queryUserWideNetInfo", param);
        if(IDataUtil.isNotEmpty(obtainWidenetInfo)){	//如果是宽带用户信息
        	foreCallback.put("IS_WIDENET_USER", "1");
        }else{	//如果不是宽带用户信息
        	foreCallback.put("IS_WIDENET_USER", "0");
        }
        
        //查询用户是否有IMS固话
        if(StringUtils.isEmpty(param.getString("SERIAL_NUMBER")))
        {
        	param.put("SERIAL_NUMBER", param.getString("AUTH_SERIAL_NUMBER"));
        }
    	if(param.getString("SERIAL_NUMBER").startsWith("KD_"))
    	{
    		param.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER").replace("KD_", ""));
    	}
        
    	param.put(Route.ROUTE_EPARCHY_CODE, "0898");
    	param.put("EPARCHY_CODE", "0898");
    	IData imsInfo = null;
        try {
        	imsInfo = CSViewCall.callone(this,"SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", param);
		} catch (Exception e) {
		}
		if (IDataUtil.isNotEmpty(imsInfo)) {
			
			String serialNumber = imsInfo.getString("SERIAL_NUMBER_B");
			String userId = imsInfo.getString("USER_ID_B");
			param.put("USER_ID", userId);
			IData userProductInfo = CSViewCall.callone(this,"SS.GetUser360ViewSVC.qryUserProductInfoByUserId",param);
			if (IDataUtil.isNotEmpty(userProductInfo)) {
				String brandCode = userProductInfo.getString("BRAND_CODE");
				String productName = userProductInfo.getString("RSRV_STR5");
				foreCallback.put("IMS_TAG", "1");
				imsInfo.put("IMS_TAG", "1");
				imsInfo.put("IMS_BRAND", brandCode);
				imsInfo.put("IMS_PRODUCT", productName);
				imsInfo.put("IMS_SERIAL_NUMBER", serialNumber);
			}
		}else {
			imsInfo = new DataMap();
			imsInfo.put("IMS_TAG","0");
			foreCallback.put("IMS_TAG","0");
		}
		
		//获取PRODUCT_ID
        if(IDataUtil.isNotEmpty(userInfo)){
        	foreCallback.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
        }
		
		setIMSInfo(imsInfo);
        setAjax(foreCallback);

    }
    
    /**
     * 核对进行宽带过户的相关规则
     * @param cycle
     * @throws Exception
     */
    public void checkChangeWidenetUser(IRequestCycle cycle) throws Exception{
    	IData pgData = getData();
    	
    	String widenetDeal=pgData.getString("WIDENET_DEAL","");
    	pgData.put(Route.ROUTE_EPARCHY_CODE, pgData.getString("USER_EPARCHY_CODE"));
    	
    	if(widenetDeal.equals("1")){	//过户宽带
    		 CSViewCall.call(this, "SS.ChangeCustOwnerSvc.checkChangeWidenetUser", pgData);
    		
    	}else if(widenetDeal.equals("0")){	////取消宽带
    		 CSViewCall.call(this, "SS.ChangeCustOwnerSvc.checkCancelWidenetUser", pgData);
    		
    	}

    }

    /**
     * 验证使用人证件号使用个数
     * @param cycle
     * @throws Exception
     */
    public void  checkRealNameLimitByUsePspt(IRequestCycle cycle) throws Exception{
    	 try {
    		 IData input = getData();
    		 input.put("CUST_NAME", input.getString("USE"));
    		 input.put("PSPT_ID", input.getString("USE_PSPT_ID"));
    		 //edit by zhangxing3 for REQ201906130010关于本省一证五号优化需求
    		 IDataset dataset=CSViewCall.call(this, "SS.CommonVerifySVC.checkRealNameLimitByUsePspt", input);
    		 /*input.put(Route.ROUTE_EPARCHY_CODE, "0898");
    		 input.put("page", "");
    		 IDataset dataset=CSViewCall.call(this, "SS.CreatePersonUserSVC.checkRealNameLimitByUsePspt", input);*/
    		 //edit by zhangxing3 for REQ201906130010关于本省一证五号优化需求
    	     setAjax(dataset.getData(0));
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
		
    }
    /**
     * 人像信息比对员工信息
     * 
     * @author dengyi
     * @param clcle
     * @throws Exception
     */
    public void isCmpPic(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
    	
    	param.putAll(data);
    	param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", param);
    	setAjax(dataset.getData(0));
    }
    
    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setDiscntInfos(IDataset discntInfos);

    public abstract void setSvcInfos(IDataset svcInfos);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setIMSInfo(IData IMSInfo);

}
