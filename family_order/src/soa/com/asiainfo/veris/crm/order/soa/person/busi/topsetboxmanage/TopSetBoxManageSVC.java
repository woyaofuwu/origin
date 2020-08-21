package com.asiainfo.veris.crm.order.soa.person.busi.topsetboxmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class TopSetBoxManageSVC extends CSBizService{
	
	 /**
     * @Function: queryDiscntPackagesByPID()
     * @Description:
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-21 下午5:46:33 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-21 yxd v1.0.0 修改原因
     */
    private boolean check8M(String tradeId) throws Exception
    {
        // 1.取8M服务配置|2013|2022|2103|：PARA_CODE1
        IDataset wideLimitSet = CommparaInfoQry.getCommpara("CSM", "3800", "WIDELIMIT", CSBizBean.getTradeEparchyCode());
        // 2.服务台帐信息：SERVICE_ID
        IDataset tradeSvcSet = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
        boolean is8M = false;
        for (Object obj : wideLimitSet)
        {
            IData wideLimit = (IData) obj;
            String limitValue = wideLimit.getString("PARA_CODE1");
            for (Object objSvc : tradeSvcSet)
            {
                IData svcData = (IData) objSvc;
                String svcId = svcData.getString("SERVICE_ID");
                if (StringUtils.indexOf(limitValue, svcId) >= 0)
                {
                    is8M = true;
                }
            }
        }
        return is8M;
    }

	 /**
     * 
     * @Function: checkTradeType()
     * @Description: 校验用户是否可执行此业务
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午5:00:03 Modification History: Date Author Version Description
     */
    public IData checkOperType(IData input) throws Exception{
    	IData retData = new DataMap();
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	String operType=input.getString("INTERNET_TV_SOURCE","");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isEmpty(userInfo)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息！"); 
        }
        String userId = userInfo.getString("USER_ID");
        IDataset validBoxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
    	if("APPLY_TOPSETBOX".equals(operType)){//申领机顶盒
    		if(DataSetUtils.isNotBlank(validBoxInfos)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户拥有魔百和机顶盒，不需要申领！");
    		}else{
    			String wSerialNumber = "KD_" + serialNumber;
    			IData wideUserInfo = UcaInfoQry.qryUserInfoBySn(wSerialNumber);
    			if(IDataUtil.isEmpty(wideUserInfo)){
    				CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户宽带已拆机，不能申领魔百和机顶盒！");
    			}else{
    				IDataset invalidBoxInfos = TopSetBoxManageBean.qryAllSetTopBoxByUserIdAndTag1(userId, "4", "J");
    				if(IDataUtil.isNotEmpty(invalidBoxInfos)){
    					IData oldBoxInfo = invalidBoxInfos.first();
    		        	String basePackageInfo = oldBoxInfo.getString("RSRV_STR2");
    		        	if(basePackageInfo!=null&&!basePackageInfo.trim().equals("")){
    		            	String[] basePackages=basePackageInfo.split(",");
    		            	if(basePackages!=null&&basePackages.length>0){
    		            		String serviceId=basePackages[0];
    		            		IDataset userPlaysvcInfos = BofQuery.getUserPlatSvc(userId,getVisit().getStaffEparchyCode());//查询魔百和业务是否取消
    		            		boolean isPlaySvc = false;
    		            		if(IDataUtil.isNotEmpty(userPlaysvcInfos)){
    		            			for(int i = 0 ;i < userPlaysvcInfos.size() ; i++){
    		            				if(serviceId.equals(userPlaysvcInfos.getData(i).getString("SERVICE_ID"))){
    		            					isPlaySvc = true;//魔百和服务未到期
    		            				}
    		            			}
    		            			if(!isPlaySvc){//魔百和服务已到期
    		            				CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户魔百和业务已取消，无法办理魔百和申领业务！");
    		            			}
    		            		}else{
    		            			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户魔百和业务已取消，无法办理魔百和申领业务！");
    		            		}
    		            	}
    		        	}
    		        	// 3.是否有宽带在途工单
    		            IDataset wideInfos = TradeInfoQry.queryExistWideTrade(wSerialNumber);
    		            if (DataSetUtils.isBlank(wideInfos))
    		            {
    		            	// 3.3.校验是否为12M宽带用户
        		            if (DataSetUtils.isBlank(UserSvcInfoQry.checkUserWide(wSerialNumber)))
        		            {
        		                CSAppException.apperr(CrmUserException.CRM_USER_3004); // 该用户不是宽带12M用户,不能办理互联网电视业务！
        		            }
    		            }
    		            else
    		            {
    		            	IData wideTD = wideInfos.getData(0);
    		            	// 校验在途工单12M宽带
    		                if (!this.check8M(wideTD.getString("TRADE_ID")))
    		                {
    		                    CSAppException.apperr(CrmUserException.CRM_USER_3004); // 该用户不是宽带12M用户,不能办理互联网电视业务！
    		                }
    		            }
        				retData.put("USER_ACTION","0");
    				}else{
    					CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通魔百和，无法办理魔百和申领业务！");
    				}
    			}
    		}
    	}else if("CHANGE_TOPSETBOX".equals(operType)){//更换机顶盒
    		if(DataSetUtils.isNotBlank(validBoxInfos)){
    			String wSerialNumber = "KD_" + serialNumber;
    			IData wideUserInfo = UcaInfoQry.qryUserInfoBySn(wSerialNumber);
    			if(IDataUtil.isEmpty(wideUserInfo)){
    				CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户宽带已拆机，不能更换魔百和机顶盒！");
    			}else{
    				retData.put("OLD_RES_INFO", validBoxInfos.first());
    			}
    		}else{
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未申领魔百和机顶盒，没有机顶盒信息，不能更换！");
    		}
    		retData.put("USER_ACTION","1");
    	}
    	else if("RETURN_TOPSETBOX".equals(operType)){//退还机顶盒
    		if(DataSetUtils.isNotBlank(validBoxInfos)){
    			retData.put("OLD_RES_INFO", validBoxInfos.first());
    		}else{
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未申领魔百和机顶盒，没有机顶盒信息，不能退还！");
    		}
    		retData.put("USER_ACTION","2");
    	}
    	else if("LOSE_TOPSETBOX".equals(operType)){//丢失机顶盒
    		if(DataSetUtils.isNotBlank(validBoxInfos)){
    			retData.put("OLD_RES_INFO", validBoxInfos.first());
    		}else{
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未申领魔百和机顶盒，没有机顶盒信息，不能丢失！");
    		}
    		retData.put("USER_ACTION","3");
    	}
		retData.put("OPER_TPYE", operType);
    	return retData;
    }
   
    /**
	 * 魔百和机顶盒管理核对用户，并获取相关信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
public IData checkUserValidForTopsetboxManage(IData param)throws Exception{
		
	TopSetBoxManageBean bean= BeanManager.createBean(TopSetBoxManageBean.class);
	IData result=new DataMap();
	
	String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
	String wSerialNumber = "KD_" + serialNumber;
    IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    if(IDataUtil.isEmpty(userInfo)){
    	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息！"); 
    }
    String userId = userInfo.getString("USER_ID");
    IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("3800", userId, "0");
    if (IDataUtil.isNotEmpty(outDataset))
    {
        CSAppException.apperr(TradeException.CRM_TRADE_93); // 有未完工工单，业务不能继续办理！
    }else{
    	IDataset out1Dataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("3910", userId, "0");
    	if (IDataUtil.isNotEmpty(out1Dataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_93); // 有未完工工单，业务不能继续办理！
        }
    }
    IDataset boxInfos = null;
	IDataset validBoxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
	if(IDataUtil.isEmpty(validBoxInfos)){
		IDataset invalidBoxInfos = bean.qryAllSetTopBoxByUserIdAndTag1(userId, "4", "J");
		if(IDataUtil.isNotEmpty(invalidBoxInfos)){
        	boxInfos = new DatasetList(invalidBoxInfos);
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通魔百和，无法办理魔百和机顶盒管理业务！");
		}
    }else{
    	boxInfos = new DatasetList(validBoxInfos);
    }
   
    
    userInfo.put("USER_ACTION", "10"); // 0:购机 1：换机    10.管理
    if (DataSetUtils.isNotBlank(boxInfos))
    {
        userInfo.putAll(boxInfos.first());
    }
    // 3.是否有宽带在途工单
    IDataset wideInfos = TradeInfoQry.queryExistWideTrade(wSerialNumber);
    String wideState = "0"; // 0-系统异常
    if (DataSetUtils.isBlank(wideInfos))
    {
        // 3.1是否有有效的宽带
        IData wUserInfo = UcaInfoQry.qryUserInfoBySn(wSerialNumber);
        if (IDataUtil.isNotEmpty(wUserInfo))
        {
        	 // 3.2校园宽带不能受理
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber).first();
            if (StringUtils.equals("4", wideNetInfo.getString("RSRV_STR2")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1183); // 校园宽带不能办理互联网电视业务！
            }
            
            wideState = "2"; // 2-正常
            userInfo.put("WIDE_START_DATE", wideNetInfo.getString("START_DATE"));
            userInfo.put("WIDE_END_DATE", wideNetInfo.getString("END_DATE"));
            userInfo.put("WIDE_USER_ID", wUserInfo.getString("USER_ID"));
            userInfo.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
        }else{//已拆机
        	IData wideNetInfo = TopSetBoxManageBean.getUserWidenetInfoBySerialNumber(wSerialNumber).first();
        	if(wideNetInfo.isEmpty()){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户没有宽带信息，不能办理该业务!"); // 用户没有宽带信息
        	}
        	if (StringUtils.equals("4", wideNetInfo.getString("RSRV_STR2")))
          	{
        		CSAppException.apperr(CrmUserException.CRM_USER_1183); // 校园宽带不能办理互联网电视业务！
        	}
        	wideState = "3"; // 3-已拆机
        	userInfo.put("WIDE_START_DATE", wideNetInfo.getString("START_DATE"));
            userInfo.put("WIDE_END_DATE", wideNetInfo.getString("END_DATE"));
            userInfo.put("WIDE_USER_ID", wideNetInfo.getString("USER_ID"));
            userInfo.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
        }
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
        if (DataSetUtils.isNotBlank(addrTD))
        {
            userInfo.put("WIDE_ADDRESS", addrTD.first().getString("DETAIL_ADDRESS"));
        }
        // 校验在途工单12M宽带
        if (!this.check8M(wideTD.getString("TRADE_ID")))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_3004); // 该用户不是宽带12M用户,不能办理互联网电视业务！
        }
    }
    // 4.设置宽带状态
    userInfo.put("WIDE_STATE", wideState);
    userInfo.put("WIDE_STATE_NAME", "2".equals(wideState) ? "正常" : "1".equals(wideState) ? "未完工" : "3".equals(wideState) ? "已拆机" : "异常");
    userInfo.put("SALE_ACTIVE", "1"); // 换机不收费用
    userInfo.put("WIDE_ADDRESS", userInfo.getString("RSRV_STR5"));
    
    /*
     * 处理机顶盒信息
     */
    IData resInfo = new DataMap();
    
    IData boxInfo=boxInfos.first();
    String resKindCode = boxInfo.getString("RES_CODE");
    resInfo.put("OLD_RES_ID", boxInfo.getString("IMSI"));
    resInfo.put("OLD_RESNO", boxInfo.getString("IMSI")); // 老终端号
    resInfo.put("OLD_RES_NO", boxInfo.getString("IMSI")); // 老终端号 -- 为了不换机顶盒校验用的【终端串一致】
    resInfo.put("OLD_RES_BRAND_NAME", boxInfo.getString("RSRV_STR4").split(",")[0]);
    resInfo.put("OLD_RES_KIND_NAME", boxInfo.getString("RSRV_STR4").split(",")[1]);
    resInfo.put("OLD_RES_STATE_NAME", "已销售");
    resInfo.put("OLD_RES_FEE", boxInfo.getString("RSRV_NUM5"));
    resInfo.put("OLD_RES_SUPPLY_COOPID", boxInfo.getString("KI"));
    resInfo.put("OLD_RES_TYPE_CODE", boxInfo.getString("RES_TYPE_CODE"));
    resInfo.put("OLD_RES_KIND_CODE", resKindCode);
    resInfo.put("OLD_RES_DEPOSIT", boxInfo.getString("RSRV_NUM2","0"));
    
    String productId=boxInfo.getString("RSRV_STR1","");
    resInfo.put("products", productId);
    
    //查询产品的名称
    if(productId!=null&&!productId.trim().equals("")){
        String productName = UProductInfoQry.getProductNameByProductId(productId);
    	if(StringUtils.isNotEmpty(productName)){
    		resInfo.put("OLD_PRODUCT_NAME", productName);
    	}else{
    		resInfo.put("OLD_PRODUCT_NAME", "");
    	}
    }else{
    	resInfo.put("OLD_PRODUCT_NAME", "");
    }
    
    //必选包
    String basePackageInfo=boxInfo.getString("RSRV_STR2");
    resInfo.put("basePackages", basePackageInfo);
    if(basePackageInfo!=null&&!basePackageInfo.trim().equals("")){
    	String[] basePackages=basePackageInfo.split(",");
    	if(basePackages!=null&&basePackages.length>0){
    		String serviceId=basePackages[0];
    		
    		if(serviceId!=null&&!serviceId.trim().equals("")&&!serviceId.trim().equals("-1")&&!serviceId.trim().equals("null")){
    			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(serviceId);
    			
    			if(IDataUtil.isNotEmpty(svcInfo)){
    				resInfo.put("OLD_BASEPACKAGE_NAME", svcInfo.getString("SERVICE_NAME",""));
    			}else{
    				resInfo.put("OLD_BASEPACKAGE_NAME", "");
    			}
    		}else{
    			resInfo.put("OLD_BASEPACKAGE_NAME", "");
    		}
    	}else{
    		resInfo.put("OLD_BASEPACKAGE_NAME", "");
    	}
    }else{
    	 resInfo.put("OLD_BASEPACKAGE_NAME", "");
    }
    
    //可选包
    String optionPackageInfo=boxInfo.getString("RSRV_STR3","");
    resInfo.put("optionPackages", optionPackageInfo);
    if(optionPackageInfo!=null&&!optionPackageInfo.trim().equals("")){
    	String[] optionPackages=optionPackageInfo.split(",");
    	if(optionPackages!=null&&optionPackages.length>0){
    		String serviceId=optionPackages[0];
    		
    		if(serviceId!=null&&!serviceId.trim().equals("")&&!serviceId.trim().equals("-1")&&!serviceId.trim().equals("null")){
    			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(serviceId);
    			
    			if(IDataUtil.isNotEmpty(svcInfo)){
    				resInfo.put("OLD_OPTIONPACKAGE_NAME", svcInfo.getString("OFFER_NAME",""));
    			}else{
    				resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
    			}
    		}else{
    			resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
    		}
    	}else{
    		resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
    	}
    }else{
    	 resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
    }
    
    resInfo.put("OLD_ARTIFICIAL_SERVICES", boxInfo.getString("RSRV_NUM1","0").equals("0")?"否":"是");
    resInfo.put("OLD_REMARK", boxInfo.getString("REMARK",""));
    result.put("OLD_RES_INFO", resInfo);
    result.put("USER_INFO", userInfo);
    
	return result;
}

	/**
	 * @Function: checkTerminal()
	 * @Description: 终端校验
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: yxd
	 * @date: 2014-8-2 下午5:00:03 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
	 */
	public IData checkTerminal(IData input) throws Exception
	{
	    IData retData = new DataMap();
	    String resNo = input.getString("RES_ID");
	    String serialNumber = input.getString("SERIAL_NUMBER");
	    IDataset retDataset = HwTerminalCall.querySetTopBox(serialNumber, resNo);
	    if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
	    {
	        IData res = retDataset.first();
	        String resKindCode = res.getString("DEVICE_MODEL_CODE", "");
	        String supplyId = res.getString("SUPPLY_COOP_ID", "");
	        retData.put("X_RESULTCODE", "0");
	        retData.put("X_RESULTINFO", res.getString("X_RESULTINFO", ""));
	        retData.put("RES_ID", resNo); // 终端串号
	        retData.put("RES_NO", res.getString("SERIAL_NUMBER", "")); // 接口返回的终端串号IMEI
	        retData.put("RES_TYPE_CODE", "4"); // 终端类型编码：4
	        retData.put("RES_BRAND_CODE", res.getString("DEVICE_BRAND_CODE")); // 终端品牌编码
	        retData.put("RES_BRAND_NAME", res.getString("DEVICE_BRAND")); // 终端品牌描述
	        retData.put("RES_KIND_CODE", resKindCode); // 终端型号编码
	        retData.put("RES_KIND_NAME", res.getString("DEVICE_MODEL", "")); // 终端型号描述
	        String resStateCode = res.getString("TERMINAL_STATE", ""); // 资源状态编码1 空闲 4 已销售
	        retData.put("RES_STATE_CODE", resStateCode);
	        retData.put("RES_STATE_NAME", "1".equals(resStateCode) ? "空闲" : "4".equals(resStateCode) ? "已销售" : "其他");
	        retData.put("RES_FEE", Double.parseDouble(res.getString("RSRV_STR6", "0"))); // 设备费用  - feeMgr.js接收单位：分
	        retData.put("RES_SUPPLY_COOPID", supplyId); // 终端供货商编码
	        
	        retData.put("DEVICE_COST", res.getString("DEVICE_COST","0")); // 进货价格
	        
	        
	        /*
	         * 如果是申领或更换机顶盒，必须是同一个型号
	         */
	        String operType=input.getString("INTERNET_TV_SOURCE","");
	        if(operType.equals("APPLY_TOPSETBOX") || operType.equals("CHANGE_TOPSETBOX")){
	        	if(!input.getString("OLD_RES_SUPPLY_COOPID","").equals(supplyId)){	//如果机顶盒的厂家不一样
	        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "机顶盒厂家与客户现有的机顶盒的厂家不一样，无法进行更换！");
	        	}
	        }
	        
	        
	        // 获取产品信息
	        IDataset prodInfos = ProductInfoQry.querySTBProducts(resKindCode, supplyId);
	        if (DataSetUtils.isBlank(prodInfos) && !StringUtils.equals(resKindCode, "N"))
	        {
	            CSAppException.apperr(CrmUserException.CRM_USER_1185, resKindCode); // 该机型[%s]未绑定产品，请联系系统管理员!
	        }
	        retData.put("PRODUCT_INFO_SET", prodInfos);
	    }
	    else
	    {
	        String resultInfo = retDataset.first().getString("X_RESULTINFO", "华为接口调用异常！");
	        CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
	    }
	    return retData;
	}
	
    /**
     * AEE定时任务
     * 魔百和业务取消90天未归还机顶盒，押金沉淀处理
     * */
    public void checkThreeMonthNotReturnTopSetBoxUser(IData userInfo) throws Exception{
    	TopSetBoxManageBean bean= BeanManager.createBean(TopSetBoxManageBean.class);
    	bean.checkThreeMonthNotReturnTopSetBoxUser(userInfo);
    }
    
    /**
     * AEE定时任务
     * 满3年用户 押金退还
     * */
    public void checkThreeYearsTopSetBoxUser(IData userInfo) throws Exception{
    	TopSetBoxManageBean bean= BeanManager.createBean(TopSetBoxManageBean.class);
    	bean.checkThreeYearsTopSetBoxUser(userInfo);
    }
    
    /**
     * 魔百和停机
     * */
    public void stopTopsetboxService(IData input) throws Exception{
    	TopSetBoxManageBean bean= BeanManager.createBean(TopSetBoxManageBean.class);
    	bean.stopTopsetboxService(input);
    }
}
