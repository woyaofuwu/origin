
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.auth;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.BizEnv;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.Des;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class Auth extends CSBizTempComponent
{

    private String myInModeCode;

    private String preSale; // 精准营业控制开关
    public static final int AUTH_CACHE_TIMEOUT = 300;
    /**
     * 认证校验
     * 
     * @param data
     * @throws Exception
     */
    public void authCheck(IData data) throws Exception
    {
        IData authData = new DataMap();
        authData.put("RESULT_CODE", "0");
        data.remove("ACTION");
        IData params = new DataMap();
        params.putAll(data);

        // 除客服，营业厅接入方做校验

        String inModeCode = getVisit().getInModeCode();
        if ("0".equals(inModeCode) || "1".equals(inModeCode) || "3".equals(inModeCode))
        {
            boolean rightFlag = false;
            if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS009") && !"347".equals(data.getString("TRADE_TYPE_CODE")) && !"343".equals(data.getString("TRADE_TYPE_CODE")))
            {
                rightFlag = true;
            }
            // 统一认证密码校验
            //if (!rightFlag && !"true".equals(data.getString("IVR_PASS_SUCC")) && !"true".equals(data.getString("DISABLED_AUTH")))
            if (!rightFlag )
            {
                String svcName = "CS.AuthCheckSVC.authCheck";
                String authType = data.getString("AUTH_TYPE", "0");
                if ("04".equals(authType))
                {
                    params.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
                    svcName = "CS.AuthCheckSVC.authBroadbandCheck";
                }
                IDataset outputData = CSViewCall.call(this, svcName, params);
                if (null != outputData && outputData.size() > 0)
                {
                    authData = outputData.getData(0);
                }
            }
        }
        authData.put("CHECK_MODE", data.getString("CHECK_MODE"));
        authData.put("REMOTECARD_TYPE", data.getString("REMOTECARD_TYPE")); 
        //**加密**
        //获取session_id
         
        String sessionId =data.getString("SESSIONID_SEC");//getVisit().getSessionId();SessionManager.getInstance().getId();
//        HttpSession session = SessionFactory.getInstance().getSession();
//        if (session != null) {
//        	sessionId = session.getId();
//        }
        //**加密**如果校验通过了，result_code=0
        if("0".equals(authData.getString("RESULT_CODE")) || "2".equals(authData.getString("RESULT_CODE"))){
        	SharedCache.set("CHECKPASSTAG"+sessionId, "0", AUTH_CACHE_TIMEOUT); //放到缓存
        }
        //**加密**新增加密字段
        String userId=data.getString("USER_ID");
        String custId=data.getString("CUST_ID");
        String serialNum=data.getString("SERIAL_NUMBER");
        Des desObj = new Des();
        if(userId!=null && !"".equals(userId)){
	        String userId_enc=desObj.setEncPwd(userId)+"xxyy";
	      
	        SharedCache.set("USERIDCACHE"+sessionId, userId_enc, AUTH_CACHE_TIMEOUT); //放到缓存
	       
        }
        if(custId!=null && !"".equals(custId)){
	        String custId_enc=desObj.setEncPwd(custId)+"xxyy";
	       
	        SharedCache.set("CUSTIDCACHE"+sessionId, custId_enc, AUTH_CACHE_TIMEOUT); //放到缓存
        }
        if(serialNum!=null && !"".equals(serialNum)){
	        String serialNum_enc=desObj.setEncPwd(serialNum)+"xxyy";
	       
	        SharedCache.set("SERIALNUMBERCACHE"+sessionId, serialNum_enc, AUTH_CACHE_TIMEOUT); //放到缓存
        }
        
       
        /**
         * REQ201705270006_关于人像比对业务优化需求
		 * @author zhuoyingzhi
		 * @date 20170626
         */
        //认证：证件类型
        authData.put("AUTH_CHECK_PSPT_TYPE_CODE", data.getString("AUTH_CHECK_PSPT_TYPE_CODE"));
        //认证：证件号码
        authData.put("AUTH_CHECK_PSPT_ID", data.getString("AUTH_CHECK_PSPT_ID"));
        //认证：客户名称
        authData.put("AUTH_CHECK_CUSTINFO_CUST_NAME", data.getString("AUTH_CHECK_CUSTINFO_CUST_NAME"));
        
        authData.put("FRONTBASE64", data.getString("FRONTBASE64"));

        /************************************************/
      
        getPage().setAjax(authData);

    }

    /**
     * 核对多用户
     * 
     * @param serialNumber
     * @throws Exception
     *             RESULT_CODE:0=正常，1=错误
     */
    public void authCheckMoreUser(String serialNumber) throws Exception
    {
        IData returnData = new DataMap();

        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);

        IDataset infos = CSViewCall.call(this, "CS.UserListSVC.queryUserBySn", params);
        if (infos == null || infos.isEmpty())
        {
            returnData.put("RESULT_CODE", 1);
        }
        else
        {
            returnData.put("RESULT_CODE", 0);
        }

        getPage().setAjax(returnData);
    }
    public void checkPassWork(IData data) throws Exception
    {
    	IData returnData = new DataMap();
    	returnData.put("RESULT_CODE", 0);
    	String passWord=data.getString("PassWork","");
    	// 参数容器
        IData param = new DataMap();
        // SUBSYS_CODE
        param.put("SUBSYS_CODE", "CSM");
        // 配置PARAM_ATTR
        param.put("PARAM_ATTR", "251");
        // 得到业务编码
        param.put("PARAM_CODE", "0");
        param.put("EPARCHY_CODE", "0898");
        // 判断所要配置的业务编码是否已配置过
        param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.ManageTaskSVC.isTaskConfigured", param, new Pagination());
        IDataset results = dataCount.getData();
        if(results.size()>0){
        	for(int i =0;i<results.size();i++){
        		IData result=results.getData(i);
        		if(result.getString("PARA_CODE1", "").equals(passWord)){
        			returnData.put("RESULT_CODE", 1);break;
        		}
        	}
        }
    	getPage().setAjax(returnData);
    }
    /**
     * 认证校验之前，初始化认证需要数据 特殊权限下不需要认证，直接进行业务受理
     * 
     * @param serialNumber
     * @throws Exception
     */
    public void beforeAuthCheck(IData data) throws Exception
    {
        String svcName = "CS.GetInfosSVC.getInfos";
        String authType = data.getString("AUTH_TYPE", "0");
        if ("04".equals(authType))
        {          
            /**
             * 这里针对无手机宽带进行处理
             * 1、判断如果是无手机宽带同时录入的SN长度是18，说明是通过身份证查询的
             * 2、根据身份证捞取他的宽带账号进行鉴权（如：KD_1000062)
             * */
            
            if(data.getString("SERIAL_NUMBER").length()==18){ 
                IDataset nophoneset = CSViewCall.call(this, "SS.NoPhoneWideChangeProdSVC.checkIfNoPhoneTrade", data);//获取无手机宽带业务类型
                if(nophoneset!=null && nophoneset.size()>0){
                    //通过身份证获取KD_账号
                    IData callParam=new DataMap();
                    callParam.put("PSPT_ID", data.getString("SERIAL_NUMBER"));
                    IDataset phoneSet = CSViewCall.call(this, "SS.NoPhoneWideChangeProdSVC.noPhoneUserQryByPSPTID", callParam);//根据身份证获取无手机宽带账号
                    if(phoneSet!=null && phoneSet.size()==1){
                        data.put("SERIAL_NUMBER", phoneSet.getData(0).getString("SERIAL_NUMBER",""));
                    } 
                } 
            }

            data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            svcName = "CS.GetInfosSVC.getAuthBroadbandUser";
        }
        IDataset tradeSet = CSViewCall.call(this, svcName, data);
        IData tradeData = tradeSet.getData(0);
        IData tradeTypeData = tradeData.getData("TRADE_TYPE_INFO");
        IData userInfo = tradeData.getData("USER_INFO");
        String identityCheckTag = tradeTypeData.getString("IDENTITY_CHECK_TAG");
        String identity = identityCheckTag.replace(' ', '0');
        IData authData = new DataMap();

        /**
         * AUTH_STATE：0=启动认证框 1=跳过认证
         */
        authData.put("AUTH_STATE", "0");
        authData.put("USER_INFO", userInfo);
        authData.put("AUTH_IDENTITY_CHECK_TAG", identity);

        boolean sys009 = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS009");
        boolean plat009 = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "PLAT009");
        boolean mobilelottery = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "MOBILELOTTERY");

        String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "");
        String userCanBeNull = data.getString("USER_CAN_BE_NULL", "");
        String disabledAuth = data.getString("DISABLED_AUTH", "false");
        String remoteAddr = getVisit().getRemoteAddr();

        if ((null == userInfo || userInfo.size() == 0) && "true".equals(userCanBeNull))
        {
            authData.put("AUTH_STATE", "1");
        }
        //新业务推荐受理页面特殊处理
        //REQ201504210004 关于客户担保开机免密码权限的修改 客户担保开机496、紧急开机497不做手机号码认证
        else if ("520".equals(tradeTypeCode) || "496".equals(tradeTypeCode) || "497".equals(tradeTypeCode)|| "702".equals(tradeTypeCode))
        {
            authData.put("AUTH_STATE", "1");
        }
        //公网地址登入要限制免密码权限
        else if (!StringUtils.equals(remoteAddr, "127.0.0.1")
        		&& (!remoteAddr.startsWith("10") || remoteAddr.startsWith("10.200.158")
        				|| remoteAddr.startsWith("10.200.242")
        				|| remoteAddr.startsWith("10.200.243")
        				|| remoteAddr.startsWith("10.200.244")
        				|| remoteAddr.startsWith("10.200.245")
        				|| remoteAddr.startsWith("10.200.246")
        				|| remoteAddr.startsWith("10.200.247")
        				|| remoteAddr.startsWith("10.200.248")
        				|| remoteAddr.startsWith("10.200.249")
        				|| remoteAddr.startsWith("10.200.250")
        				|| remoteAddr.startsWith("10.200.251")        		  
        		   )
        		)
        {
            authData.put("AUTH_STATE", "0");
        }
        
        else if ("343".equals(tradeTypeCode) || "347".equals(tradeTypeCode) )
        {
            authData.put("AUTH_STATE", "1");
        }
        else if (identity.startsWith("00000") || "true".equals(disabledAuth) || sys009 
        		|| ("3700".equals(tradeTypeCode) && plat009) || ("304".equals(tradeTypeCode) && mobilelottery))
        {
            authData.put("AUTH_STATE", "1");
        }
        else if("240".equals(tradeTypeCode))
        {
             IData params = new DataMap();
             params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
             IDataset infos = CSViewCall.call(this, "CS.UserListSVC.querySaleBook", params);
             if(IDataUtil.isNotEmpty(infos))
             {
            	 String authBookSale = infos.getData(0).getString("AUTH_BOOK_SALE");
            	 if("1".equals(authBookSale))
            	 {
            		 authData.put("AUTH_STATE", "1");
            	 }
             }
        }
        
        /**
         * REQ201803260019++关于申请下放二级副以上领导手机代付铁通固定电话费用的权限的需求
         * 有权限的工号，对领导号码，免密添加
         */
        if("325".equals(tradeTypeCode)){
        	String staffId = getVisit().getStaffId();
            boolean hasLimit = StaffPrivUtil.isFuncDataPriv(staffId, "PRIV_TF_LEADER");
            
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            IDataset results = CSViewCall.call(this, "SS.LeaderInfoSVC.queryLeaderInfo", param);
            if (!IDataUtil.isEmpty(results)&&hasLimit)
            {
            	authData.put("AUTH_STATE", "1");
            }
        }
        
        
        
        //客服接入判断接续页面的主叫号码或被叫号码与受理号码是否一致，如一致跳过认证
        String inModeCode = getVisit().getInModeCode();
    	String serialNumber = data.getString("SERIAL_NUMBER");
    	String agentPhone = data.getString("AGENT_CALL_PHONE","");
        if ("1".equals(inModeCode) && !"".equals(agentPhone))
        {
//        	主叫号码为callParams[1],被叫为callParams[2]，受理号码为callParams[3]
        	String[] phoneStr = agentPhone.split("JHJ");
        	if (phoneStr.length == 4 && ((phoneStr[1] != null && phoneStr[1].indexOf(serialNumber) > -1) 
        			|| (phoneStr[2] != null && phoneStr[2].indexOf(serialNumber) > -1))) {
        		authData.put("AUTH_STATE", "1");
			}
        	//新客服系统下，报停/报开无论主叫号码或被叫号码与受理号码是否一致，都要密码验证
        	if("131".equals(tradeTypeCode) || "133".equals(tradeTypeCode)){
        		authData.put("AUTH_STATE", "0");
        	}
        }
        
        Des desObj = new Des();
        //**加密**新增加密字段
        //获取session_id
        String sessionId =data.getString("SESSIONID_SEC");
        //通过校验后删除缓存
	    if(SharedCache.keyExist("USERIDCACHE"+sessionId)){
	    	SharedCache.delete("USERIDCACHE"+sessionId); 
	    }
	    if(SharedCache.keyExist("CUSTIDCACHE"+sessionId)){
	    	SharedCache.delete("CUSTIDCACHE"+sessionId);
	    }
	    if(SharedCache.keyExist("SERIALNUMBERCACHE"+sessionId)){
	    	SharedCache.delete("SERIALNUMBERCACHE"+sessionId); 
	    }
	    if(SharedCache.keyExist("AUTHSTATECACHE"+sessionId)){
	    	SharedCache.delete("AUTHSTATECACHE"+sessionId); 
	    }
/*	    if(SharedCache.keyExist("CHECKPASSTAG"+sessionId)){
	    	SharedCache.delete("CHECKPASSTAG"+sessionId);
	    }*/
	    String AUTH_SPEC_SN=String.valueOf(SharedCache.get("SERIALNUMBERCACHE"+sessionId));
	    String serialNum_enc=desObj.setEncPwd(serialNumber)+"xxyy";
	    if( !serialNum_enc.equals(AUTH_SPEC_SN) && SharedCache.keyExist("CHECKPASSTAG"+sessionId)){
	    	SharedCache.delete("CHECKPASSTAG"+sessionId);
	    }
	    
	    
//        HttpSession session = SessionFactory.getInstance().getSession();
//        if (session != null) {
//        	sessionId = session.getId();
//        }
        SharedCache.set("AUTHSTATECACHE"+sessionId, authData.getString("AUTH_STATE"), AUTH_CACHE_TIMEOUT); //放到缓存;
        String userId=userInfo.getString("USER_ID");
        String custId=userInfo.getString("CUST_ID");
        String serialNum=data.getString("SERIAL_NUMBER");
        
        
        if(userId!=null && !"".equals(userId)){
	        String userId_enc=desObj.setEncPwd(userId)+"xxyy";
	        //authData.put("AUTH_SPEC_UID", userId_enc);
	        SharedCache.set("USERIDCACHE"+sessionId, userId_enc, AUTH_CACHE_TIMEOUT); //放到缓存
        }
        if(custId!=null && !"".equals(custId)){
	        String custId_enc=desObj.setEncPwd(custId)+"xxyy";
	        //authData.put("AUTH_SPEC_CID", custId_enc);
	        SharedCache.set("CUSTIDCACHE"+sessionId, custId_enc, AUTH_CACHE_TIMEOUT); //放到缓存
        }
        
        
        if(serialNum!=null && !"".equals(serialNum)){
        	//String serialNum_enc=desObj.setEncPwd(serialNum)+"xxyy";
	        //authData.put("AUTH_SPEC_SN", serialNum_enc);
	        SharedCache.set("SERIALNUMBERCACHE"+sessionId, serialNum_enc, AUTH_CACHE_TIMEOUT); //放到缓存
      
            //AUTH_IDENTITY_CHECK_TAG=01000 
            //如果用户密码为空，且只有密码认证唯一方式，则允许用户不需要校验
        
	        String upass=data.getString("USER_PASSWD");
	        if("01000".equals(identity)&&("".equals(upass)||upass == null)){
          //保存校验通过标志
            SharedCache.set("CHECKPASSTAG"+sessionId, "0", AUTH_CACHE_TIMEOUT); //放到缓存
        	}
        }
       
        
        
        getPage().setAjax(authData);
    }

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
    }

    public abstract String getAuthType();

    public abstract String getBeforeAction();

    public abstract String getDisabledAuth();

    public String getInModeCode()
    {
        return this.myInModeCode;
    }

    public abstract String getMoreUser();

    public abstract String getOrderTypeCode();

    public String getPreSale()
    {
        return preSale;
    }

    public abstract String getSerialNumber();

    public abstract String getTradeAction();

    public abstract String getTradeTypeCode();

    public abstract String getUserCanBeNull();

    /**
     * 加载三户资料
     * 
     * @param data
     * @throws Exception
     */
    public void loadAuthData(IData data) throws Exception
    {
    	String checkTag="0";
    	log.debug(">>>>>>safe check<<<<<AUTH loadAuthData<<<<");
    	
 	
    	
    	IData indata = new DataMap();
		indata.put("PARAM_ATTR", "6899"); 
		IDataset idset = CSViewCall.call(this, "CS.GetInfosSVC.getCommCommparaSVC", indata);
		if(idset!=null && idset.size()>0){
			checkTag=idset.getData(0).getString("PARA_CODE1");
		}
    	 //**加密**密码解密 校验，不通过则不允许捞三户信息
    	//获取session_id 
    	String sessionId =data.getString("SESSIONID_SEC");
//        HttpSession session = SessionFactory.getInstance().getSession();
//        if (session != null) {
//        	sessionId = session.getId();
//        }
       //根据TRADE_TYPE_CODE进行拦截配置 0 不拦截  默认   1拦截，
    	String tradeTypeCode=data.getString("TRADE_TYPE_CODE");
    	String tradeTypeCodeTag = "0"; //默认不拦截
    	//IDataset ids = CommparaInfoQry.getCommNetInfo("CSM","6999",tradeTypeCode);
    	indata.put("TRADE_TYPE_CODE", tradeTypeCode); 
    	IDataset ids = CSViewCall.call(this, "CS.GetInfosSVC.getTradeCommparaSVC", indata);
		if(ids!=null && ids.size()>0){
			//tradeTypeCodeTag=ids.getData(0).getString("PARA_CODE1");
			if("1".equals(ids.getData(0).getString("PARA_CODE1")))	tradeTypeCodeTag="1";
		}   
		
    	String auth_check_value_tag=data.getString("AUTH_CHECK_VALUE_TAG");
    	String serialNum=data.getString("SERIAL_NUMBER","");

    	String checkPassTag=String.valueOf(SharedCache.get("CHECKPASSTAG"+sessionId));//校验通过标记
    	String authState=String.valueOf(SharedCache.get("AUTHSTATECACHE"+sessionId));//校验状态标记
    	
    	
    	Des desObj = new Des();
    	
    	if("IVR001".equals(auth_check_value_tag)){ 
    		//通过客服IVR校验密码通过后，只回调NGBOSS前台脚本设置了通过标志，没有对校验通过的号码等进行加密后再回传。此逻辑只能通过标志进行校验。
    		checkPassTag ="0";
            if(serialNum!=null && !"".equals(serialNum)){
    	        String serialNum_enc=desObj.setEncPwd(serialNum)+"xxyy";
    	        //authData.put("AUTH_SPEC_SN", serialNum_enc);
    	        SharedCache.set("SERIALNUMBERCACHE"+sessionId, serialNum_enc, AUTH_CACHE_TIMEOUT); //放到缓存
            }
            //保存校验通过标志
            SharedCache.set("CHECKPASSTAG"+sessionId, checkPassTag, AUTH_CACHE_TIMEOUT); //放到缓存
    		
    	}
        
    	
    	if("0".equals(authState)){//必须鉴权的才进行校验
    		
    		if(checkPassTag==null || "".equals(checkPassTag) || !"0".equals(checkPassTag)){//必须鉴权的情况下，如果不是鉴权通过（=0通过)来到这里，则有问题
    			log.error(">>>>>>safe check<<<<<<<<<checkPassTag="+checkPassTag+"<<<<<<<<checkPassTag is error>>>>>>>>>> tradeTypeCode="+tradeTypeCode);
        		if(!"0".equals(checkTag) && !"0".equals(tradeTypeCodeTag)){
        			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
        		}
    		} 
    	}
    	
    	String AUTH_SPEC_UID=String.valueOf(SharedCache.get("USERIDCACHE"+sessionId));//data.getString("AUTH_SPEC_UID","");
    	String AUTH_SPEC_CID=String.valueOf(SharedCache.get("CUSTIDCACHE"+sessionId));//data.getString("AUTH_SPEC_CID","");
    	String AUTH_SPEC_SN=String.valueOf(SharedCache.get("SERIALNUMBERCACHE"+sessionId));//data.getString("AUTH_SPEC_SN","");
    	    	
    	if(data.getString("USER_ID","")!=null&&!"".equals(data.getString("USER_ID","")) ){
    		if(StringUtils.isNotBlank(AUTH_SPEC_UID) && !"null".equals(AUTH_SPEC_UID) && AUTH_SPEC_UID.indexOf("xxyy")>-1) 
    		{
	        	AUTH_SPEC_UID=desObj.getDesPwd(AUTH_SPEC_UID);
	        	if(!AUTH_SPEC_UID.equals(data.getString("USER_ID",""))){
	        		log.error(">>>>>>safe check<<<<<<<<<AUTH_SPEC_UID="+AUTH_SPEC_UID+"<<<<<<<<AUTH_SPEC_UID is error>>>>>>>>>>  tradeTypeCode="+tradeTypeCode);
	        		if(!"0".equals(checkTag)&& !"0".equals(tradeTypeCodeTag)){
	        			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
	        		}
	        	}else{
					
					AUTH_SPEC_UID=desObj.setEncPwd(data.getString("USER_ID",""))+"xxyy"; 
					SharedCache.set("USERIDCACHE"+sessionId, AUTH_SPEC_UID, AUTH_CACHE_TIMEOUT); //放到缓存
					
					
				}
    		}else{
				
				AUTH_SPEC_UID=desObj.setEncPwd(data.getString("USER_ID",""))+"xxyy"; 
				SharedCache.set("USERIDCACHE"+sessionId, AUTH_SPEC_UID, AUTH_CACHE_TIMEOUT); //放到缓存
				
				
			}
			
        		
    	} 
    	if(data.getString("CUST_ID","")!=null&&!"".equals(data.getString("CUST_ID","")) ){ 
        	
    		if(StringUtils.isNotBlank(AUTH_SPEC_CID) && !"null".equals(AUTH_SPEC_CID) && AUTH_SPEC_CID.indexOf("xxyy")>-1) 
    		{
    			AUTH_SPEC_CID=desObj.getDesPwd(AUTH_SPEC_CID);
            	if(!AUTH_SPEC_CID.equals(data.getString("CUST_ID",""))){
            		log.error(">>>>>>safe check<<<<<<<<<AUTH_SPEC_CID="+AUTH_SPEC_CID+"<<<<<<<<AUTH_SPEC_CID is error>>>>>>>>>> tradeTypeCode="+tradeTypeCode);
            		if(!"0".equals(checkTag) && !"0".equals(tradeTypeCodeTag)){
            			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
            		}
            	}else{
    					AUTH_SPEC_CID=desObj.setEncPwd(data.getString("CUST_ID",""))+"xxyy"; 
    					SharedCache.set("CUSTIDCACHE"+sessionId, AUTH_SPEC_CID, AUTH_CACHE_TIMEOUT); //放到缓存
    									
    			}
    		}else{
				AUTH_SPEC_CID=desObj.setEncPwd(data.getString("CUST_ID",""))+"xxyy"; 
				SharedCache.set("CUSTIDCACHE"+sessionId, AUTH_SPEC_CID, AUTH_CACHE_TIMEOUT); //放到缓存
								
    		}
        	
			
    	} 
		
    	if(data.getString("SERIAL_NUMBER","")!=null&&!"".equals(data.getString("SERIAL_NUMBER","")) ){ 
    		//如果是已经在别的页面通过了，那么在鉴权的时候就会直接跳过前几步直接来加载三户信息，而这时候USER_ID应该是空的
    		// -----对原来上面一行注释的评论：USER_ID 为空，不能认为是其它页面校验通过。安全测试正是把USER_ID 删除点。 
			//-------应该是参数有值就检查比较，无值就不用比较检查（不可能变因为这个参数无值参数查别的用户资料）
			
    		if(StringUtils.isNotBlank(AUTH_SPEC_SN) && !"null".equals(AUTH_SPEC_SN) && AUTH_SPEC_SN.indexOf("xxyy")>-1)
    		{
    			AUTH_SPEC_SN=desObj.getDesPwd(AUTH_SPEC_SN);
    			if(!AUTH_SPEC_SN.equals(data.getString("SERIAL_NUMBER",""))){
    				log.error(">>>>>>safe check<<<<<<<<<AUTH_SPEC_SN="+AUTH_SPEC_SN+"<<<<<<<<AUTH_SPEC_SN is error>>>>>>>>>> tradeTypeCode="+tradeTypeCode);
            		if(!"0".equals(checkTag) && !"0".equals(tradeTypeCodeTag) ){
            			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
            		}
            	
				}else{
					//USER_ID没有传只传了SERIALNUMBER,说明是在别的页面缓存了校验通过的直接来加载三户信息的。所以这里只将SERIAL_NUMBER加密放到缓存。
					//-----对原来上面一行注释的评论：不能根据上述理由断定认定通过，以下代码行逻辑已改变为，传的号码和缓存中的号码一致。
					//-----重写只是延期超时时间。
					String serialNum_enc=desObj.setEncPwd(data.getString("SERIAL_NUMBER",""))+"xxyy"; 
					SharedCache.set("SERIALNUMBERCACHE"+sessionId, serialNum_enc, AUTH_CACHE_TIMEOUT); //放到缓存
				}
    		}else{
				//USER_ID没有传只传了SERIALNUMBER,说明是在别的页面缓存了校验通过的直接来加载三户信息的。所以这里只将SERIAL_NUMBER加密放到缓存。
				//-----对原来上面一行注释的评论：不能根据上述理由断定认定通过，以下代码行逻辑已改变为，传的号码和缓存中的号码一致。
				//-----重写只是延期超时时间。
				String serialNum_enc=desObj.setEncPwd(data.getString("SERIAL_NUMBER",""))+"xxyy"; 
				SharedCache.set("SERIALNUMBERCACHE"+sessionId, serialNum_enc, AUTH_CACHE_TIMEOUT); //放到缓存
			}
		
    	} 
        String svcName = "CS.GetInfosSVC.getUCAInfos";
        String authType = data.getString("AUTH_TYPE", "0");
        if (authType.equals("04"))
        {
            data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            svcName = "CS.GetInfosSVC.getBroadbandUCAInfos";
        }
        //Des desObj = new Des();
        IDataset infos = CSViewCall.call(this, svcName, data);
        String userId=infos.getData(0).getData("USER_INFO").getString("USER_ID");
        String custId=infos.getData(0).getData("USER_INFO").getString("CUST_ID");
        if(userId!=null && !"".equals(userId) && ("".equals(AUTH_SPEC_UID)|| AUTH_SPEC_UID == null)){
	        String userId_enc=desObj.setEncPwd(userId)+"xxyy";
	        //authData.put("AUTH_SPEC_UID", userId_enc);
	        SharedCache.set("USERIDCACHE"+sessionId, userId_enc, AUTH_CACHE_TIMEOUT); //放到缓存
        }
        if(custId!=null && !"".equals(custId) &&("".equals(AUTH_SPEC_CID) || AUTH_SPEC_CID == null)){
	        String custId_enc=desObj.setEncPwd(custId)+"xxyy";
	        //authData.put("AUTH_SPEC_CID", custId_enc);
	        SharedCache.set("CUSTIDCACHE"+sessionId, custId_enc, AUTH_CACHE_TIMEOUT); //放到缓存
        }
        /**实时营销活动开关**/
    	boolean flag = BizEnv.getEnvBoolean("crm_realtimemarketing_webswitch");
    	infos.getData(0).put("CRM_REALTIMEMARKETING_WEBSWITCH", flag?"1":"0");
        getPage().setAjax(infos.getData(0));
    }

    public void renderComponent(StringBuilder infoParamsBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData data = getPage().getData();
        String serialNumber = getSerialNumber();
        if (null == serialNumber || "".equals(serialNumber))
        {
            serialNumber = data.getString("SERIAL_NUMBER", "");
        }
        
        /**
         * 这里针对无手机宽带进行处理
         * 1、判断如果是无手机宽带同时录入的SN长度是18，说明是通过身份证查询的
         * 2、根据身份证捞取他的宽带账号进行鉴权（如：KD_1000062)
         * */
        if("04".equals(data.getString("AUTH_TYPE", ""))){
            if(data.getString("SERIAL_NUMBER").length()==18){ 
                IDataset nophoneset = CSViewCall.call(this, "SS.NoPhoneWideChangeProdSVC.checkIfNoPhoneTrade", data);//获取无手机宽带业务类型
                if(nophoneset!=null && nophoneset.size()>0){
                    //通过身份证获取KD_账号
                    IData callParam=new DataMap();
                    callParam.put("PSPT_ID", data.getString("SERIAL_NUMBER"));
                    IDataset phoneSet = CSViewCall.call(this, "SS.NoPhoneWideChangeProdSVC.noPhoneUserQryByPSPTID", callParam);//根据身份证获取无手机宽带账号
                    if(phoneSet!=null && phoneSet.size()==1){
                        serialNumber=phoneSet.getData(0).getString("SERIAL_NUMBER","");
                        data.put("SERIAL_NUMBER", serialNumber);
                    } 
                } 
            }
        }
        
        // 区分组件自身初始化还是后面页面触发组件业务方法
        String action = data.getString("ACTION", "");

        if (null == action || "".equals(action))
        {
            String inModeCode = getVisit().getInModeCode();
            // 模拟某个工号是从客服登录的，只用于测试
            String staffId = getVisit().getStaffId();
            boolean ivrStaff = BizEnv.getEnvBoolean("crm.ivr." + staffId, false);

            if (ivrStaff == true)
            {
                setInModeCode("0");
            }
            else
            {
                setInModeCode(inModeCode);
            }

            // 如果ORDER_TYPE_CODE为空，则取TRADE_TYPE_CODE
            if (null == getOrderTypeCode() || "".equals(getOrderTypeCode()))
            {
                setOrderTypeCode(getTradeTypeCode());
            }

            // 为了不和页面的orderTypeCode冲突，另起一个名字
            getPage().getData().put("orderTypeCode4ShoppingCart", getOrderTypeCode());

            boolean isLog = BizEnv.getEnvBoolean("crm.presale", false);
            setPreSale(String.valueOf(isLog));

            /**
             * 组件初始化载入认证的脚本
             */
            getPage().addResAfterBodyBegin("scripts/csserv/component/auth/Auth.js");
            getPage().addResAfterBodyBegin("scripts/csserv/component/person/CommLib.js");
            getPage().addResAfterBodyBegin("scripts/csserv/common/tradecheck/TradeCheck.js");
            getPage().addResAfterBodyBegin("scripts/csserv/component/fee/FeeMgr.js");
            getPage().addResAfterBodyBegin("scripts/csserv/common/developstaff/DevelopStaff.js");
        }
        else
        {
        	String sessionId =cycle.getRequestContext().getRequest().getSession().getId();
        	data.put("SESSIONID_SEC", sessionId);
            if ("AUTH_MOREUSER".equals(action))
            {
                authCheckMoreUser(serialNumber);
            }
            else if ("AUTH_BEFORE".equals(action))
            {
                beforeAuthCheck(data);
            }
            else if ("AUTH_CHECK".equals(action))
            {
                authCheck(data);
            }
            else if ("AUTH_DATA".equals(action))
            {
                loadAuthData(data);
            }else if("AUTH_CHECKPSW".equals(action)){
            	checkPassWork(data);
            }
            setRenderContent(false); // 不刷新组件
        }
    }

    public abstract void setAuthType(String authType);

    public abstract void setBeforeAction(String beforeAction);

    public abstract void setDisabledAuth(String disabledAuth);

    public void setInModeCode(String inModeCode)
    {
        this.myInModeCode = inModeCode;
    }

    public abstract void setMoreUser(String moreUser);

    public abstract void setOrderTypeCode(String orderTypeCode);

    public void setPreSale(String preSale)
    {
        this.preSale = preSale;
    }

    public abstract void setSerialNumber(String serialNumber);

    public abstract void setTradeAction(String tradeAction);

    public abstract void setTradeTypeCode(String tradeTypeCode);

    public abstract void setUserCanBeNull(String userCanBeNull);

}
