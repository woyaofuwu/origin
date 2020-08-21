
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import org.apache.commons.lang.RandomStringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class SaleActiveCheckSnSVC extends CSBizService
{
    private static final long serialVersionUID = -9129400296266964757L;

    public IDataset checkIfUseHaveNeedProd(IData param) throws Exception
    {
        String checkSN = param.getString("CHECK_SERIAL_NUMBER");
        String userId = param.getString("USER_ID");
        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");  
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);
        IData params=new DataMap();
        params.put("CHECK_SERIAL_NUMBER", checkSN);
        params.put("PRODUCT_ID", productId);
        params.put("PACKAGE_ID", packageId);
        params.put("USER_ID", userId);
        
        SaleActiveCheckSnBean checkBean = BeanManager.createBean(SaleActiveCheckSnBean.class);
        return new DatasetList(checkBean.checkIfUseHaveNeedProd(params));
    }
    
    public IDataset checkProdParaInfo(IData param) throws Exception{
    	String productId=param.getString("PRODUCT_ID","");
    	String packageId=param.getString("PACKAGE_ID","");
    	IDataset ids = CommparaInfoQry.getCommparaAllCol("CSM","9956", productId, "0898");
    	if(!"".equals(packageId)){
    		ids = CommparaInfoQry.getCommparaAllCol("CSM","9771", productId+"|"+packageId, "0898");
    	}
    	return ids;
    }
    
    
    /** 
	* 20170906 chenxy3 
	* SS.SaleActiveCheckSnSVC.check2017ActiveCommpara
	* */
    public IDataset check2017ActiveCommpara(IData param) throws Exception{
    	String paramAttr=param.getString("PARAM_ATTR","");
    	String productId=param.getString("PRODUCT_ID","");
    	IDataset ids = CommparaInfoQry.getCommparaAllCol("CSM",paramAttr, productId, "0898"); 
    	return ids;
    }
    
    /** 
	* 20170906 chenxy3 
	* SS.SaleActiveCheckSnSVC.check2017ActiveInfo
	* */
    public IDataset check2017ActiveInfo(IData param) throws Exception{
    	IDataset rtnSet=new DatasetList();
    	String checkSN = param.getString("CHECK_SERIAL_NUMBER");
    	String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
        String userId = param.getString("USER_ID");
        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");  
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);
        IData params=new DataMap();
        params.put("CHECK_SERIAL_NUMBER", checkSN);
        params.put("PRODUCT_ID", productId);
        params.put("PACKAGE_ID", packageId);
        params.put("USER_ID", userId);
        params.put("AUTH_SERIAL_NUMBER", serialNumber);
         
        SaleActiveCheckSnBean checkBean = BeanManager.createBean(SaleActiveCheckSnBean.class);
        IData oldUser=checkBean.checkOldUseForActive(params);
        IData newUser=checkBean.checkNewUseForActive(params);
        oldUser.putAll(newUser);
        rtnSet.add(oldUser);
        return rtnSet;
    }
    
    /** 
   	* 20170906 chenxy3 
   	* SS.SaleActiveCheckSnSVC.check2017ActiveNewUser
   	* */
    public IData check2017ActiveNewUser(IData param) throws Exception{
    	String checkSN = param.getString("CHECK_SERIAL_NUMBER");
        String userId = param.getString("USER_ID");
        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");  
        String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
        IData params=new DataMap();
        params.put("CHECK_SERIAL_NUMBER", checkSN);
        params.put("PRODUCT_ID", productId);
        params.put("PACKAGE_ID", packageId);
        params.put("USER_ID", userId);
        params.put("AUTH_SERIAL_NUMBER", serialNumber);
        params.put("MAIN_SERINUM_CHECK_TAG", param.getString("MAIN_SERINUM_CHECK_TAG",""));
        params.put("BAT_TAG", param.getString("BAT_TAG",""));
		SaleActiveCheckSnBean checkBean = BeanManager.createBean(SaleActiveCheckSnBean.class);
			return checkBean.checkNewUseForActive(params);
    }
    
    /** 
   	* 20170926 fufn
   	* 
   	* */
    public IData check2017ActiveNewUserForApp(IData param) throws Exception{
    	IData rs=null;
    	 IData rtnData=new DataMap();
    	 String checkTag="0";
         String checkErrInfo="失败原因：";
         if(param.getString("CHECK_SERIAL_NUMBER","")!=null&&param.getString("AUTH_SERIAL_NUMBER","")!=null&&
        		 param.getString("PRODUCT_ID","")!=null&&param.getString("PACKAGE_ID","")!=null){
             UcaData uca = UcaDataFactory.getNormalUca(param.getString("AUTH_SERIAL_NUMBER",""));
    		 String userId= uca.getUserId();  
    		 param.put("USER_ID", userId);
    		 rs=check2017ActiveNewUser(param);
        	/**
     		 * * 新号码校验：
    	     * 1、是否办理过该活动   SN_HAVE_PRODUCT  Y:办理过   N:没办理过
    	     * 2、0存折不能大于0  SN_FEE_TYPE    Y:大于0   N：不大于0
    	     * 3、不能是多终端共享业务数据     SHARE_INFO_TYPE  Y:属于   N:不属于
    	     * 4、不能统一付费业务数据。          RELATION_UU_TYPE  Y:属于   N:不属于
    	     * 5、与新号码的身份证是否相同      PSPT_ID_SAME Y:相同    N：不相同
    	     * 6、新号码是否 红海行动营销活动     HAVE_ACTIVE Y:    N：
     		 * */
            String haveTag=rs.getString("SN_HAVE_PRODUCT");
            String feeTag =rs.getString("SN_FEE_TYPE");
            String shareTag =rs.getString("SHARE_INFO_TYPE");
            String relaTag=rs.getString("RELATION_UU_TYPE");
            String psptIdTag=rs.getString("PSPT_ID_SAME");
            String open48tag=rs.getString("OPEN_48_HOUR");
            String haveActiveTag=rs.getString("HAVE_ACTIVE","N");
            String mainCardTag=rs.getString("MAIN_CARD");
            String haveFamilyTag=rs.getString("FAMILY_CARD");
            String wideNetTag=rs.getString("WIDENET_TYPE");
            String familyCardOld=rs.getString("FAMILY_CARD_OLD");
            String widenetTypeOld=rs.getString("WIDENET_TYPE_OLD");
           
            if("N".equals(open48tag)){
            	checkErrInfo="输入的号码激活时间超过48小时，该号码不满足条件！";
            	checkTag="1";
            }
            if("Y".equals(haveTag)){
        		checkErrInfo=checkErrInfo+"&&输入的号码已经办理过该活动，不能再次办理！";
            	checkTag="1";
        	}
        	if("Y".equals(shareTag)){
        		checkErrInfo=checkErrInfo+"&&输入的号码办理过多终端共享业务，不能办理该活动！";
            	checkTag="1";
        	}
        	if("Y".equals(relaTag)){
        		checkErrInfo=checkErrInfo+"&&输入的号码办理过统一付费业务，不能办理该活动！";
            	checkTag="1";
        	}
        	if("N".equals(psptIdTag)){
        		checkErrInfo=checkErrInfo+"&&输入的号码与主号码的身份证不一致，不能办理该活动！";
            	checkTag="1";
        	}
        	if("Y".equals(feeTag)){
        		checkErrInfo=checkErrInfo+"&&输入的号码预存款大于0，不能办理该活动！";
            	checkTag="1";
        	}
        	if("Y".equals(haveActiveTag)){
        		checkErrInfo=checkErrInfo+"&&输入的号码存在红海行动营销活动，不能办理该活动！";
            	checkTag="1";
        	}
        	//第三季校验 start
        	if("N".equals(mainCardTag)){
        		checkErrInfo=checkErrInfo+"&&新号码对应老号码需作为主卡与新号码办统一付费业务！";
            	checkTag="1";
        	}
        	if("N".equals(haveFamilyTag)){
        		checkErrInfo=checkErrInfo+"&&新号码需要办理亲亲网业务！";
            	checkTag="1";
        	}
        	if("N".equals(wideNetTag)){
        		checkErrInfo=checkErrInfo+"&&新号码没有办理宽带业务（或预约）！";
            	checkTag="1";
        	}
        	//第三季校验 end
        	//第四季校验 start
        	if("N".equals(familyCardOld)){
        		checkErrInfo=checkErrInfo+"&&老号码没有办理亲亲网业务  ，不能办理该活动！";
            	checkTag="1";
        	}
        	if("N".equals(widenetTypeOld)){
        		checkErrInfo=checkErrInfo+"&&老号码没有办理宽带业务(或预约)  ，不能办理该活动！";
            	checkTag="1";
        	}
        	//第四季校验 end
         }else{
        	 checkTag="1";
        	 checkErrInfo="入参传入缺少值！";
         }
		 
	        	  
    	rtnData.put("X_RESULTCODE", checkTag);
    	if(checkTag.equals("0")){
    		rtnData.put("X_RESULTINFO", "OK");
    	}else{
    		rtnData.put("X_RESULTINFO", checkErrInfo);
    	}
    	return rtnData;
    	
    	
    }
    
    
    /** 
   	* 20170906 chenxy3 
   	* SS.SaleActiveCheckSnSVC.checkGroupUserIfUseActive
   	* */
    public IData checkGroupUserIfUseActive(IData param) throws Exception{
    	SaleActiveCheckSnBean checkBean = BeanManager.createBean(SaleActiveCheckSnBean.class);
    	checkBean.checkGroupUserIfUseActive(param);
    	return new DataMap();
    }
    
    
    /** 
	* 20190311 wangsc10 
	* SS.SaleActiveCheckSnSVC.checkSIMInfo
	* */
    public IDataset checkSIMInfo(IData param) throws Exception{
    	String productId=param.getString("PRODUCT_ID","");
    	String packageId=param.getString("PACKAGE_ID", "");
    	String eparchyCode = param.getString("EPARCHY_CODE", getTradeEparchyCode());
    	IDataset comms = CommparaInfoQry.getCommparaInfoByCode("CSM", "6895", productId, packageId, eparchyCode);
    	return comms;
    }
	
    /** 
   	* 20190311 wangsc10 
   	* 短信下发
   	* */
    public IData sendSMSTOhebao(IData param) throws Exception{
    	// 调接口：
    	String serialNumber = param.getString("SERIAL_NUMBER");
    	System.out.println(serialNumber+"=========================wangsssss");
        IData inparam = new DataMap();
		// 生成14位数字
        String mid = SysDateMgr.getSysDateYYYYMMDD() + String.valueOf(RandomStringUtils.randomNumeric(6));
        String requestDate = SysDateMgr.getSysDateYYYYMMDD(); // YYYYMMDD
        String requestTime = SysDateMgr.getSysTime();// 2016-08-24 18:19:51
        requestTime = requestTime.substring(requestTime.indexOf(":") - 2).replaceAll(":", "");// 格式：HHMISS
        String merid = "888002115000004";// 生产固定这个商户
        // 测试商户号码，生产可不配置这条
        IDataset userMerIds = CommparaInfoQry.getCommparaAllColByParser("CSM", "6896", "1", "0898");
        if (IDataUtil.isNotEmpty(userMerIds))
        {
            merid = userMerIds.getData(0).getString("PARA_CODE1", "");
        }
		
		String signString = "MCODE=101774&MID=" + mid + "&DATE=" + requestDate + "&TIME=" + requestTime + "&MERID=" + merid + "&MBL_NO=" + serialNumber;
        String requestXML = "<MESSAGE><MCODE>101774</MCODE><MID>" + mid + "</MID><DATE>" + requestDate + "</DATE><TIME>" + requestTime + "</TIME><MERID>" + merid + "</MERID><MBL_NO>" + serialNumber + "</MBL_NO>";

        inparam.put("SIGN_STRING", signString);
        inparam.put("REQUEST_XML", requestXML);
        inparam.put("CALL_TYPE", "SEND_SMS");// 下发短信

        IDataset callResults = CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam);
        
        if (callResults != null && callResults.size() > 0)
        {
        	String x_resultcode = callResults.getData(0).getString("X_RESULTCODE", "");
            String x_resultinfo = callResults.getData(0).getString("X_RESULTINFO", "");
            if ("1".equals(x_resultcode))
            {
            	//下发短信成功
            }else{
            	// 如果调接口失败，也报错
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "活动下发短信失败，调用短信下发接口失败信息：" + x_resultinfo);
            }
        }
    	return new DataMap();
    }
    
    
    /** 
   	* 20190311 wangsc10 
   	* 红包余额查询
   	* */
    public IData queryRedMoney(IData param) throws Exception{
    	// 调接口：
    	IData rtnData = new DataMap();
    	String serialNumber = param.getString("SERIAL_NUMBER");
    	String productId = param.getString("PRODUCT_ID", "");
		String packageId = param.getString("PACKAGE_ID");
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);
		String SMS_CODE = param.getString("SMS_CODE", "");//短信验证码--wangsc10--20190311
		rtnData.put("RED_CODE", "0");
        rtnData.put("SMS_CODE", SMS_CODE);
        rtnData.put("RED_YE", "0");
    	// 1、先根据活动获取是否使用红包活动
        IDataset comms = CommparaInfoQry.getCommparaInfoByCode("CSM", "6895", productId, packageId, "0898");
        if (IDataUtil.isNotEmpty(comms))
        {
            String redPakLimit = comms.getData(0).getString("PARA_CODE2", "");// 红包上限
            String redPakLimitType = comms.getData(0).getString("PARA_CODE3", "");// 红包扣款类型（A=按code2值扣，B=大于0最高code2（最长8位）
            // 调接口：
            IData inparam = new DataMap();
            // 生成14位数字
            String mid = SysDateMgr.getSysDateYYYYMMDD() + String.valueOf(RandomStringUtils.randomNumeric(6));
            String requestDate = SysDateMgr.getSysDateYYYYMMDD(); // YYYYMMDD
            String requestTime = SysDateMgr.getSysTime();// 2016-08-24 18:19:51
            requestTime = requestTime.substring(requestTime.indexOf(":") - 2).replaceAll(":", "");// 格式：HHMISS
            String merid = "888002115000004";// 生产固定这个商户
            // 测试商户号码，生产可不配置这条
            IDataset userMerIds = CommparaInfoQry.getCommparaAllColByParser("CSM", "6896", "1", "0898");
            if (IDataUtil.isNotEmpty(userMerIds))
            {
                merid = userMerIds.getData(0).getString("PARA_CODE1", "");
            }
            String signString = "MCODE=101776&MID=" + mid + "&DATE=" + requestDate + "&TIME=" + requestTime + "&MERID=" + merid + "&MBL_NO=" + serialNumber + "&CHK_NO=" + SMS_CODE;
            String requestXML = "<MESSAGE><MCODE>101776</MCODE><MID>" + mid + "</MID><DATE>" + requestDate + "</DATE><TIME>" + requestTime + "</TIME><MERID>" + merid + "</MERID><MBL_NO>" + serialNumber + "</MBL_NO><CHK_NO>" + SMS_CODE + "</CHK_NO>";

            inparam.put("SIGN_STRING", signString);
            inparam.put("REQUEST_XML", requestXML);
            inparam.put("CALL_TYPE", "CHECK_VALUE");// 查红包余额

            IDataset callResults = CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam);

            if (callResults != null && callResults.size() > 0)
            {
                String x_resultcode = callResults.getData(0).getString("X_RESULTCODE", "");
                String x_resultinfo = callResults.getData(0).getString("X_RESULTINFO", "");
                if ("1".equals(x_resultcode))
                {
                    String allBalance = callResults.getData(0).getString("ALL_BALANCE", "");// 可用总余额（就是和包现金，这里无用）
                    String elecQuan = callResults.getData(0).getString("ELEC_QUAN", "");// 查余额根据沟通使用这个比对
                    // 根据配置来，如果是A类型，则必须按上限金额扣，不多不少
                    if ("A".equals(redPakLimitType))
                    {
                    	if (Integer.parseInt(elecQuan) < Integer.parseInt(redPakLimit))//如果是A类型的，查出的红包余额小于配置金额，则提示余额不足。
                        {
                    		rtnData.put("RED_CODE", "1");
                        }
                    }
                    else
                    {
			            if(Integer.parseInt(elecQuan)<=0){
			        		//A情况，必须按设置的值来扣
			        		rtnData.put("RED_CODE", "1");
			        	}
                    }
                    rtnData.put("RED_YE", elecQuan);

                }
                else
                {
                    // 如果调接口失败，也报错
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "活动办理失败，调用红包余额查询接口失败信息：" + x_resultinfo);
                }
            }
        }
    	return rtnData;
    }
}
