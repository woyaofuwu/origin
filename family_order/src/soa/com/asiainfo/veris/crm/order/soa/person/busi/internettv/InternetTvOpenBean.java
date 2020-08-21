
package com.asiainfo.veris.crm.order.soa.person.busi.internettv;

 

import org.apache.log4j.Logger;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;  
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * REQ201607050007 关于移动电视尝鲜活动的需求
 * chenxy3 20160720
 * */
public class InternetTvOpenBean extends CSBizBean
{
	private static transient Logger logger = Logger.getLogger(InternetTvOpenBean.class);
	 
	
	/**
	 * 获取用户是否存在已完工（未完工）的宽带信息。
	 * */
	public static IDataset getUserWilenInfos(IData param) throws Exception
    { 
	    IDataset resultDataset = new DatasetList();
	    
        IDataset dataInfos = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_WILEN_BY_SN_PRODID", param);
        
        if (IDataUtil.isNotEmpty(dataInfos))
        {
            return dataInfos;
        }
        else
        {
            dataInfos = TradeInfoQry.getTradeInfoBySn(param.getString("SERIAL_NUMBER"));
            
            if (IDataUtil.isNotEmpty(dataInfos))
            {
                if (param.getString("PRODUCT_ID").equals(dataInfos.getData(0).getString("PRODUCT_ID")))
                {
                    return dataInfos;
                }
            }
        }
        
        return null;
    }  
	
	/**
	 * 到期执行任务（魔百和体验期活动）
	 * lizj 20200405
	 * */
	public static IDataset topSetBoxExpireDeal(IData param) throws Exception
    { 
		IData dealParam = new DataMap(param.getString("DEAL_COND"));
		System.out.println("执行topSetBoxExpireDeal"+dealParam);
		String serialNumber = dealParam.getString("SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);
		if (IDataUtil.isNotEmpty(userInfo))
        {
			IDataset commparaInfos2324=CommparaInfoQry.getCommparaInfos("CSM", "2324",userInfo.getString("PRODUCT_ID"));
			if (IDataUtil.isEmpty(commparaInfos2324))
	        {
				return CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", dealParam);
	        }
        }
		return null;
    }
	
	/**
	 * 短信回复处理接口（魔百和体验期内提醒短信）
	 * lizj 20200405
	 * */
	public static IData smsReplyTopSetBox(IData param) throws Exception
    { 
		String reply = param.getString("REPLY");
		String serialNumber = param.getString("SERIAL_NUMBER");
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTCODE", "请求成功");
		result.put("X_RESULTTIME", SysDateMgr.getSysTime());
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
        {
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULTCODE", CrmUserException.CRM_USER_112);
			return result;
        }
		String userId = userInfo.getString("USER_ID");
		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
		IData boxInfo=boxInfos.first();
		if (IDataUtil.isNotEmpty(boxInfo))
        {
			/*
			 * 必选服务
			 */
			String basePlatSvcIdTemp=boxInfo.getString("RSRV_STR2","");	//必选套餐
			if(!basePlatSvcIdTemp.equals("")&&basePlatSvcIdTemp.indexOf(",")!=-1){
				String[] basePlatSvcIdArr=basePlatSvcIdTemp.split(",");
				if(basePlatSvcIdArr!=null&&basePlatSvcIdArr.length>0){
					String basePlatSvcId=basePlatSvcIdArr[0];
					if(basePlatSvcId!=null&&!basePlatSvcId.trim().equals("")){
						IDataset userBaseServices=UserPlatSvcInfoQry.
								queryUserPlatSvcByUserIdAndServiceId(userId, basePlatSvcId);
						//在魔百和平台业务状态表的预留字段打上“体验基础到期前续订”的标识
						if(IDataUtil.isNotEmpty(userBaseServices)){
							IData params = new DataMap();
					        params.put("USER_ID", userId);
					        params.put("SERVICE_ID", basePlatSvcId);
							if("Y".equals(reply)){
								params.put("RSRV_STR8", "EXPERIENCE_CONTINUE");
								
								IDataset userSaleActiveInfo = UserSaleActiveInfoQry.queryUserSaleActiveProdId(userId,"66005203","0");
								IDataset discntInfo = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId(userId,"84076649",Route.CONN_CRM_CG);
								String activityId="";
				            	if (IDataUtil.isNotEmpty(userSaleActiveInfo))
				                {
				            		activityId = userSaleActiveInfo.first().getString("PACKAGE_ID");
				                }
				            	String activityBeginDate="";
				            	String activityEndDate="";
				            	if (IDataUtil.isNotEmpty(discntInfo))
				                {
				            		activityBeginDate = discntInfo.first().getString("START_DATE");
				            		activityEndDate = discntInfo.first().getString("END_DATE");
				                }
								//实时同步接口告知EPG平台
								param.put("opType", "8");
								param.put("serviceId", basePlatSvcId);
								param.put("serialNumber", "KD_"+serialNumber);
								param.put("activityId", activityId);
								param.put("activityPrice", "100");
								param.put("activityBeginDate", activityBeginDate);
								param.put("activityEndDate", activityEndDate);
								param.put("renewFlag", "Y");
								try{
									CSAppCall.call("SS.TopSetBoxSVC.syncOrderState", param);
								}catch(Exception e){
									logger.error(serialNumber+"调用服务SS.TopSetBoxSVC.syncOrderState报错！"+SysDateMgr.getSysTime());
								}
								
								
							}else{
								params.put("RSRV_STR8", "EXPERIENCE_CONTINUE_N");
							}
							Dao.executeUpdateByCodeCode("TF_F_USER_PLATSVC", "UPD_USER_PLATSVC_EXPEROENCE", params,Route.CONN_CRM_CG);
							
						}
					}
				}
			}
        }
		
		return result;
    }
	
	
	public static IDataset getUserBaseServices(IData param) throws Exception
    { 
		String serialNumber = param.getString("SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isNotEmpty(userInfo))
        {
			String userId = userInfo.getString("USER_ID");
			IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
			if (IDataUtil.isNotEmpty(boxInfos))
	        {
				IData boxInfo=boxInfos.first();
				/*
				 * 必选服务
				 */
				String basePlatSvcIdTemp=boxInfo.getString("RSRV_STR2","");	//必选套餐
				if(!basePlatSvcIdTemp.equals("")&&basePlatSvcIdTemp.indexOf(",")!=-1){
					String[] basePlatSvcIdArr=basePlatSvcIdTemp.split(",");
					if(basePlatSvcIdArr!=null&&basePlatSvcIdArr.length>0){
						String basePlatSvcId=basePlatSvcIdArr[0];
						if(basePlatSvcId!=null&&!basePlatSvcId.trim().equals("")){
							return UserPlatSvcInfoQry.queryUserPlatSvcByUserIdAndServiceId(userId, basePlatSvcId);
						}
					}
				}
	        }
        }
		
	    return new DatasetList();
    }
	
	/**
	 * 魔百和体验期结束不续订强制报停
	 * lizj 20200405
	 * */
	 public IData stopTopSetBoxExperience(IData input) throws Exception
	    {
		    IData returnData = new DataMap(); 
	        String rspCode = "0000";
	        String rspDesc = "调用成功！";

//	        StringBuilder selectSQL = new StringBuilder()
//	                .append(" SELECT t.serial_number,A.* FROM TF_F_USER_DISCNT A")
//	                .append(" left join tf_f_user t on a.user_id=t.user_id and remove_tag='0'")
//	                .append(" where SYSDATE BETWEEN A.START_DATE AND A.END_DATE and trunc(end_date,'dd')-trunc(sysdate,'dd')=0")
//	                .append(" AND discnt_code=84076649 ");
	        String userId = input.getString("USER_ID");
	        IData userInfo = UcaInfoQry.qryUserInfoByUserIdForGrp(userId);
	        if(IDataUtil.isEmpty(userInfo))
	        {
	        	returnData.put("rspCode", "-1");
	        	returnData.put("rspDesc", "未找到用户资料！");
	        	return returnData;
	        }
	        
	        String serialNumber = userInfo.getString("SERIAL_NUMBER");
            
	        input.put("SERIAL_NUMBER", serialNumber);
            input.put("IN_MODE_CODE", "0");
            input.put("ROUTE_EPARCHY_CODE", "0898");
            input.put("TRADE_TYPE_CODE", "3900");
            input.put("EXPERIENCE_TAG", "EXPERIENCE_STOP");
            
            IDataset userBaseServices = this.getUserBaseServices(input);
            IDataset ret =null;
            if(IDataUtil.isNotEmpty(userBaseServices)){
            	if(!"EXPERIENCE_CONTINUE".equals(userBaseServices.first().getString("RSRV_STR8"))){
            		 try
                     {
                         ret = CSAppCall.call("SS.StopTopSetBoxRegSVC.tradeReg", input);
                         if(IDataUtil.isNotEmpty(ret))
                         {
                         	//在平台业务状态表的预留字段增加“体验基础包到期未续订导致报停”的标识
         					if(IDataUtil.isNotEmpty(userBaseServices)){
         						IData params = new DataMap();
         				        params.put("USER_ID", userBaseServices.first().getString("USER_ID"));
         				        params.put("SERVICE_ID", userBaseServices.first().getString("SERVICE_ID"));
         				        params.put("RSRV_STR8", "EXPERIENCE_STOP");
         						Dao.executeUpdateByCodeCode("TF_F_USER_PLATSVC", "UPD_USER_PLATSVC_EXPEROENCE", params,Route.CONN_CRM_CG);
         						
         						//实时同步接口告知EPG平台
//         						params.put("opType", "4");  
//         						params.put("renewFlag", "N");
//         						params.put("serialNumber", "KD_"+serialNumber);
//         						CSAppCall.call("SS.TopSetBoxSVC.syncOrderState", params);
         						
         						 IDataset commparaInfos2324=CommparaInfoQry.getCommparaAllColByParser("CSM", "2324",null,"0898");
         							if(IDataUtil.isNotEmpty(commparaInfos2324)){
         								 String productId = commparaInfos2324.first().getString("PARA_CODE4");
         								 if(StringUtils.isNotBlank(productId)){
         									 params.put("USER_ID", userId);
         									 params.put("PRODUCT_ID", productId);
         									 IDataset iDataset= Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", params);  
         									 if(iDataset.size()>0){
         										 IData endActiveParam = new DataMap();
         					                     endActiveParam.put("SERIAL_NUMBER", serialNumber);
         					                     endActiveParam.put("PRODUCT_ID", productId);
         					                     endActiveParam.put("PACKAGE_ID", iDataset.getData(0).getString("PACKAGE_ID"));
         					                     endActiveParam.put("RELATION_TRADE_ID", iDataset.getData(0).getString("RELATION_TRADE_ID"));
         					                     endActiveParam.put("CHECK_MODE", "F");
         					                     endActiveParam.put("CAMPN_TYPE", "YX04");
         					                     endActiveParam.put("RETURNFEE", "0");
         					                     endActiveParam.put("YSRETURNFEE", "0");
         					                     endActiveParam.put("TRUE_RETURNFEE_COST", "0");
         					                     endActiveParam.put("TRUE_RETURNFEE_PRICE", "0");
         					                     endActiveParam.put("END_DATE_VALUE", "0"); 
         					                     //endActiveParam.put("END_DATE_VALUE", "3"); 
         					                     endActiveParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
         					                     endActiveParam.put("END_MONTH_LAST", "Y");
         					                     endActiveParam.put("NO_TRADE_LIMIT", "TRUE");
         					                     endActiveParam.put("SKIP_RULE", "TRUE");
         					                     CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
         									 }
         								 }
         								
         							}
         					}
                         	
                         }
                         else
                         {
                         	returnData.put("RESULT_CODE", "-1");
                         	returnData.put("REMARK", "体验基础包到期未续订报停失败。。");
         				}
                     } 
                     catch (Exception e)
                     {
                     	returnData.put("RESULT_CODE", "-1");
                     	returnData.put("REMARK",  "体验基础包到期未续订报停失败。。。");
         				rspCode="-1";
         				rspDesc=e.toString();
                     }
            	}else{
            		IData params = new DataMap();
            		//实时同步接口告知EPG平台
					params.put("opType", "2");  
					params.put("renewFlag", "Y");
					params.put("serialNumber", "KD_"+serialNumber);
					params.put("serviceId", userBaseServices.first().getString("SERVICE_ID"));
					CSAppCall.call("SS.TopSetBoxSVC.syncOrderState", params);
            	}
            	
            }
            
	        returnData.put("RESULT_CODE", rspCode);
	        returnData.put("RESULT_INFO", rspDesc);
	        return returnData; 
	    }
	 
		public IData stopTopSetBoxPlatSvc(IData input) throws Exception
	    {
			String userId = input.getString("USER_ID");
			String serviceId = input.getString("SERVICE_ID");
			
		    IData returnData = new DataMap(); 
		    returnData.put("rspCode", "0000");
        	returnData.put("rspDesc", "调用成功！");
	        IData userInfo = UcaInfoQry.qryUserInfoByUserIdForGrp(userId);
	        if(IDataUtil.isEmpty(userInfo))
	        {
	        	returnData.put("rspCode", "-1");
	        	returnData.put("rspDesc", "未找到用户资料！");
	        	return returnData;
	        }
	        
	        IData param = new DataMap();
			param.put("LOGIN_EPARCHY_CODE", "0898");
            param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
            param.put("USER_ID", userInfo.getString("USER_ID"));
            param.put("SERVICE_ID", serviceId);  
            param.put("NO_TRADE_LIMIT", "TRUE");
            param.put("SKIP_RULE", "TRUE");
            param.put("OPER_CODE", "07");
            try
	        {
            	IDataset result = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", param);
	        } catch (Exception e)
	        {
	        	returnData.put("rspCode", "1001");
	        	returnData.put("rspDesc", e.toString());
	        }
		    
		 return returnData;
		 
	    }
}
