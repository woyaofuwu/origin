
package com.asiainfo.veris.crm.order.soa.person.busi.moblieStore;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MSpBizQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityPlatBean;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.ChangeProductBean;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.userident.UserIdentBean;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

public class MobileStoreBean extends CSBizBean
{
	static Logger logger = Logger.getLogger(MobileStoreBean.class);
     /**
      * 移动商城呼叫专业业务办理接口
      * @param data
      * @return
      * @throws Exception
      */
     public IData callForwarding(IData data) throws Exception
     {
		    checkPramByKeys(data,"ID_TYPE,SERIAL_NUMBER,OPR_NUMB,BIZ_TYPE,IDENT_CODE,OPR_CODE");
		    String serialNumber =data.getString("SERIAL_NUMBER");
		    IData result = new DataMap();
		    result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		    result.put("RSP_CODE", "0000");
		    result.put("RSP_DESC", "成功！");
		    //用户身份凭证校验
	        try{
		      IData param1 = new DataMap();
        	  param1.put("IDENT_CODE", data.getString("IDENT_CODE", ""));
        	  param1.put("SERIAL_NUMBER",serialNumber);
		      UserIdentBean userIdentBean = BeanManager.createBean(UserIdentBean.class);
		      userIdentBean.identAuth(param1);
	        }catch(Exception e){
	        	result.put("RSP_CODE", "3018");
	            result.put("RSP_DESC","身份凭证过期："+e.getMessage());
	            result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE", "3018");
	            return result;
	        }	
	        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
			if(IDataUtil.isEmpty(userInfo)){
				result.put("RSP_CODE", "2998");
			    result.put("RSP_DESC", "用户信息不存在！");
			    result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE", "2998");
			    return result;
			}
	    	if(!"0".equals(userInfo.getString("USER_STATE_CODESET",""))){
				result.put("RSP_CODE", "2005");
			    result.put("RSP_DESC", "该号码已停机！");
			    result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE", "2005");
			    return result;
			}	
			String userId = userInfo.getString("USER_ID");
			String eparchyCode  = userInfo.getString("EPARCHY_CODE");
			IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
			if(IDataUtil.isEmpty(productInfo)){
				result.put("RSP_CODE", "2998");
			    result.put("RSP_DESC", "用户主产品信息不存在！");
			    result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE", "2998");
			    return result;
			}
			String productId=productInfo.getString("PRODUCT_ID");
			data.put("EPARCHY_CODE", eparchyCode);
			data.put("PRODUCT_ID", productId);
			data.put("USER_ID", userId);
	        String operCode = IDataUtil.chkParam(data, "OPR_CODE");// 操作代码
	        // 01：呼转设置02：呼转取消
	        IData ret=new DataMap();
	        if (StringUtils.equals("01", operCode))
	        {
	        	ret=orderTransCalling(data);
	        }
	        if (StringUtils.equals("02", operCode))
	        {
	        	ret=cancelTransCalling(data);
	        }
	        result.putAll(ret);
	        return result;
    }
     /**
      * 订购
      * @param data
      * @return
      * @throws Exception
      */
	public IData orderTransCalling(IData data) throws Exception
   {
	 IData result=new DataMap();
	 String serialNumber=data.getString("SERIAL_NUMBER");
	 String transType = IDataUtil.chkParam(data, "TRANS_TYPE");// 呼转设置类型
	 String serviceId = "";
	 String attrCode = "";
	 String transNumber="";	
	 transNumber = IDataUtil.chkParam(data, "TRANSFER_NUMBER");
     // 1：无条件呼转 2：遇忙呼转 3：无应答转 4：不可及转
     IData set = StaticInfoQry.getStaticInfoByTypeIdDataId("NORMAL_VEML_SVC", transType.substring(1));
     if (IDataUtil.isNotEmpty(set))
     {
         serviceId = set.getString("DATA_NAME");
         attrCode = set.getString("PDATA_ID");
     }
     else
     {
         result.put("RSP_CODE", "2998");
         result.put("RSP_DESC", "TD_S_STATIC表NORMAL_VEML_SVC参数未配置！");
         result.put("X_RSPTYPE", "2");// add by ouyk
         result.put("X_RSPCODE", "2998");// add by ouyk
         return result;
     }	
     data.put("IN_MODE_CODE", "6");
     data.put("ELEMENT_TYPE_CODE", "S");
     data.put("ELEMENT_ID", serviceId);
     data.put("MODIFY_TAG",  BofConst.MODIFY_TAG_ADD);
     data.put("BOOKING_TAG", "0");
     data.put("ATTR_STR1", attrCode);
     data.put("ATTR_STR2", transNumber);
     data.put("IS_PLAT_ORDER", "true");
	   try
       {
   		IDataset resultList = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct",data);
        result.putAll(resultList.getData(0));
       }
       catch (Exception e)
       {
    	   result.put("X_RSPTYPE", "2");
	       result.put("X_RSPCODE","3004");
	       result.put("RSP_CODE", "3004");
	       result.put("RSP_DESC","产品变更报错:"+e.getMessage());
       }
	 return result;
	}
	/**
	 * 退订
	 * @param data
	 * @return
	 * @throws Exception
	 */
	 public IData cancelTransCalling(IData data) throws Exception
    {
		 IData result=new DataMap();
		 String userId=data.getString("USER_ID");
		 String serialNumber=data.getString("SERIAL_NUMBER");
		 try{
		  IDataset selectedElements = new DatasetList();
		  //呼叫转移服务配置
		   IDataset svcParams=StaticUtil.getStaticList("NORMAL_VEML_SVC");
		   if(IDataUtil.isNotEmpty(svcParams)){
			for(int i=0;i<svcParams.size();i++){
				IDataset userSvcInfos = UserSvcInfoQry.queryUserSvcByUseridSvcid(userId,svcParams.getData(i).getString("DATA_NAME",""));
				if(IDataUtil.isNotEmpty(userSvcInfos)){
					IData param = new DataMap();
					param.put("ELEMENT_ID", String.valueOf(i));
					param.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
					param.put("ELEMENT_TYPE_CODE", "S");
					param.put("PRODUCT_ID", userSvcInfos.getData(0).getString("PRODUCT_ID"));
					param.put("PACKAGE_ID",  userSvcInfos.getData(0).getString("PACKAGE_ID"));
					param.put("INST_ID",  userSvcInfos.getData(0).getString("INST_ID"));
					IDataset userAttrs = UserAttrInfoQry.getUserAttrByPK(userId, userSvcInfos.getData(0).getString("INST_ID"),null);
                    IDataset attrParams = new DatasetList();
					if(IDataUtil.isNotEmpty(userAttrs))
					{
						IData servparam = new DataMap();
			            servparam.put("ATTR_CODE", userAttrs.getData(0).getString("ATTR_CODE"));
			            servparam.put("ATTR_VALUE", userAttrs.getData(0).getString("ATTR_VALUE"));
			            servparam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
			          
			            attrParams.add(servparam);
			            param.put("ATTR_PARAM", attrParams);
					}	
					selectedElements.add(param);
				}
			}
		   }else{
			    result.put("RSP_CODE", "2998");
	           result.put("RSP_DESC","TD_S_STATIC表NORMAL_VEML_SVC参数未配置！");
	            result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE", "2998");
	            return result;	
		   }
			if(IDataUtil.isEmpty(selectedElements))
			{
			    result.put("RSP_CODE", "2998");
		        result.put("RSP_DESC", "用户未订购或已退订呼叫转移业务！");
		        result.put("X_RSPTYPE", "2");// add by ouyk
		        result.put("X_RSPCODE", "2998");// add by ouyk
		         return result;
//				CSAppException.apperr(ChangeProductException.CRM_CHANGE_PRODUCT_2998,"用户未订购或已退订呼叫转移业务！");
			}	
			IData param = new DataMap();
			param.put("SERIAL_NUMBER", serialNumber);
			param.put("SELECTED_ELEMENTS", selectedElements.toString());
			IDataset dataset = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", param);
			result.putAll(dataset.getData(0));
		 }catch(Exception e){
			 result.put("X_RSPTYPE", "2");
		     result.put("X_RSPCODE","3004");
		     result.put("RSP_CODE", "3004");
		     result.put("RSP_DESC","产品变更报错:"+e.getMessage());
		 }
		 return result;
    }
	/**
	 * 移动商城业务办理资格校验
	 * @param input
	 * @return
	 * @throws Exception
	 */
	 public IData checkServiceRuleUmmp(IData input) throws Exception
    {
		 IData result=new DataMap();
		 result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		 result.put("RSP_CODE", "0000");
		 result.put("RSP_DESC", "成功！");
		 result.put("REASON", "成功！");
		 result.put("VALID_FLAG", "0");//鉴权通过
		 checkPramByKeys(input,"ID_TYPE,SERIAL_NUMBER,PAY_TYPE,HANDLE_TYPE,OPR_NUMB,BIZ_TYPE,IDENT_CODE,OPR_TIME");	 
		 String oprNumb=input.getString("OPR_NUMB","");
		 String serialNumber=input.getString("SERIAL_NUMBER","");
		 //用户身份凭证验证
	     try{
			  IData param1 = new DataMap();
	          param1.put("IDENT_CODE", input.getString("IDENT_CODE", ""));
	          param1.put("SERIAL_NUMBER", serialNumber);
			  UserIdentBean userIdentBean = BeanManager.createBean(UserIdentBean.class);
			  userIdentBean.identAuth(param1);
		   }catch(Exception e){
		      result.put("RSP_CODE", "3018");
		      result.put("RSP_DESC","凭证校验失败："+e.getMessage());
		      result.put("X_RSPTYPE", "2");
		      result.put("X_RSPCODE", "3018");
		      result.put("VALID_FLAG", "1");//鉴权不通过
		      result.put("REASON", "凭证校验失败："+e.getMessage());
		      return result;
		   }
		   IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		   if(IDataUtil.isEmpty(userInfo)){
				result.put("RSP_CODE", "2998");
			    result.put("RSP_DESC", "用户信息不存在！");
			    result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE", "2998");
	            result.put("REASON", "用户信息不存在！");
	            result.put("VALID_FLAG", "1");
			    return result;
			}	
			if(!"0".equals(userInfo.getString("USER_STATE_CODESET",""))){
				result.put("RSP_CODE", "2005");
			    result.put("RSP_DESC", "该号码已停机！");
			    result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE","2005");
	            result.put("REASON", "该号码已停机！");
	            result.put("VALID_FLAG", "1");
			    return result;
			}
			input.putAll(userInfo);
		   //input.put("EPARCHY_CODE",userInfo.getString("EPARCHY_CODE",""));
		   String payType=input.getString("PAY_TYPE","");
		   if("01".equals(payType)){//话费支付
		     IData doweFee = AcctCall.getOweFeeByUserId(userInfo.getString("USER_ID"));
		     if (doweFee.getInt("ACCT_BALANCE", 0) < 0)
	         {
		       result.put("RSP_CODE", "3002");
			   result.put("RSP_DESC","用户余额不足！");
			   result.put("X_RSPTYPE", "2");
			   result.put("X_RSPCODE", "3002"); 
			   result.put("REASON", "用户余额不足！");
			   result.put("VALID_FLAG", "1");
			   return result;
	          }
		   }
		   //调原来产品预校验接口
//		   try{
			   String numType=input.getString("ID_TYPE");
			   input.put("BIZ_TYPE_CODE", input.getString("BIZ_TYPE"));
			   IDataset productInfos=input.getDataset("PRODUCT_INFO");
			   if(IDataUtil.isNotEmpty(productInfos)){
				  for(int i=0;i<productInfos.size();i++){
					 IData products=productInfos.getData(i);
					 String productType= products.getString("PRODUCT_TYPE");
					 if("01".equals(productType)||"03".equals(productType)){
						 input.put("PRODUCT_ID", IDataUtil.chkParam(products, "PRODUCT_ID")); 
						 input.put("ELEMENT_ID", input.getString("PRODUCT_ID"));
						 IData product = new DataMap();
			    	   	 IData out=checkElements(input);
						 if(IDataUtil.isNotEmpty(out)&&!"0000".equals(out.getString("RSP_CODE",""))){
							 result.put("RSP_CODE",out.getString("RSP_CODE",""));
							 result.put("RSP_DESC",out.getString("RSP_DESC",""));
							 result.put("X_RSPTYPE", "2");
							 result.put("X_RSPCODE", out.getString("RSP_CODE","")); 
							 result.put("REASON", out.getString("RSP_DESC"));
							 result.put("VALID_FLAG", "1");
							 return result; 
						 }
			    	     product.put("ELEMENT_TYPE_CODE",out.getString("ELEMENT_TYPE_CODE",""));
			    	     product.put("PRODUCT_ID", out.getString("ELEMENT_ID"));
			    	     product.put("MODIFY_TAG", "0");
			    	     product.put("ELEMENT_ID", out.getString("ELEMENT_ID"));
			    	     IDataset productList = new DatasetList();
			    	     productList.add(product);
						 AbilityPlatBean check=new AbilityPlatBean();
			    		 IData outData=check.executeProduct(productList,input,userInfo.getString("EPARCHY_CODE",""),serialNumber);
			    		 if(IDataUtil.isNotEmpty(outData)&&!"0000".equals(outData.getString("BIZORDERRESULT"))){
			    			   result.put("RSP_CODE", outData.getString("BIZORDERRESULT","3004"));
			 				   result.put("RSP_DESC",outData.getString("RESERVE",""));
			 				   result.put("X_RSPTYPE", "2");
			 				   result.put("X_RSPCODE", outData.getString("BIZORDERRESULT","3004")); 
			 				   result.put("REASON", "产品变更业务规则报错！");
			 				   result.put("VALID_FLAG", "1");
			 				   return result;  
			    		 }
			    		 return result;  
					 }else if("02".equals(productType)){
						 input.put("SP_CODE", IDataUtil.chkParam(products, "SP_ID"));
						 input.put("BIZ_CODE", IDataUtil.chkParam(products, "BIZ_CODE"));
					     String spCode=input.getString("SP_CODE","");
					     String bizCode=input.getString("BIZ_CODE","");
					     String bizCode4IBoss ="";
					     String bizTypeCode="";
					     IDataset bizList=MSpBizQry.queryBizInfoBySpcodeBizCode(spCode,bizCode);
					     if(bizList !=null && bizList.size()>0){
					    	bizTypeCode=bizList.getData(0).getString("BIZ_TYPE_CODE","");
					    	bizCode4IBoss = bizCode+"|"+bizTypeCode;
					     }else{
					    	CSAppException.apperr(ParamException.CRM_PARAM_441);
					     }
				         IData param = new DataMap();
				         param.put("SERIAL_NUMBER", serialNumber);
				         IData out = this.dealPlatTrade(param,bizCode4IBoss,input,oprNumb,PlatConstants.OPER_ORDER);
				         return result;  
					 }
				  }
			   }else{
				   result.put("RSP_CODE", "4010");
				   result.put("RSP_DESC","PRODUCT_INFO节点不存在！");
				   result.put("X_RSPTYPE", "2");
				   result.put("X_RSPCODE", "4010"); 
				   result.put("REASON", "PRODUCT_INFO节点不存在！");
				   result.put("VALID_FLAG", "1");
				   return result;  
			   }
//		   }catch(Exception e){
//			   result.put("RSP_CODE", "2998");
//			   result.put("RSP_DESC",e.getMessage());
//			   result.put("X_RSPTYPE", "2");
//			   result.put("X_RSPCODE", "2998"); 
//			   return result;
//		   }
		   return result;
	}
	    public IData dealPlatTrade(IData param,String bizCode4IBoss, IData data,String oprNumb,String operOrder) throws Exception {
	    	IData result = new DataMap();
			String spCode = data.getString("SP_CODE");
			if (bizCode4IBoss.indexOf("|") == -1)
			{
				CSAppException.apperr(ParamException.CRM_PARAM_442);
			}
			String bizCode = bizCode4IBoss.split("\\|")[0];
			String bizTypeCode = bizCode4IBoss.split("\\|")[1];
			String oprSource = data.getString("BIZ_TYPE_CODE");
			param.put("TRANS_ID", oprNumb);
			param.put("BIZ_TYPE_CODE", bizTypeCode);
			param.put("SP_CODE", spCode);
			param.put("BIZ_CODE", bizCode);
			param.put("OPER_CODE", operOrder);
			/**************************合版本 duhj 2017/5/3 start***********************************/
			String changeOprSoure = CustServiceHelper.getCustomerServiceChannel(oprSource);                
	        if(changeOprSoure!=null&&changeOprSoure.trim().length()>0){
	            oprSource = changeOprSoure;
	        }
			/**************************合版本 duhj 2017/5/3 end***********************************/

			param.put("OPR_SOURCE", oprSource);
			param.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
			IDataset results = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", param);
			if (IDataUtil.isNotEmpty(results)){
				result = results.getData(0);
			}
			//增加OPR_TIME字段返回
			result.put("OPR_TIME",SysDateMgr.getSysDate("yyyyMMddHHmmss"));
			return result;
		}
	 public IData changeProductCheck(IData input) throws Exception
	{
		 IData result=new DataMap();
		 result.put("RSP_CODE", "0000");
		 result.put("RSP_DESC", "成功！");
		 IData data=new DataMap();
		 IData param=checkElements(input);
		 if(IDataUtil.isNotEmpty(param)&&!"0000".equals(param.getString("RSP_CODE",""))){
			 result.put("RSP_CODE",param.getString("RSP_CODE",""));
			 result.put("RSP_DESC",param.getString("RSP_DESC",""));
			 result.put("X_RSPTYPE", "2");
			 result.put("X_RSPCODE", param.getString("RSP_CODE","")); 
		
			 return result; 
		 }
		 data.putAll(param);
		 data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		 data.put("ELEMENT_TYPE_CODE",param.getString("ELEMENT_TYPE_CODE"));
		 data.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
	     data.put("ELEMENT_ID", param.getString("ELEMENT_ID"));
	     data.put("IN_MODE_CODE", input.getString("IN_MODE_CODE"));
	     data.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
	     data.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
	     logger.debug("changeProductCheck-data"+data);
   		 IDataset resultList = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti",data);
   		 logger.debug("changeProductCheck-resultList"+resultList);
   		 if(IDataUtil.isNotEmpty(resultList)){
   			result=resultList.getData(0);
   		 }
   		 result.putAll(param);
		 return result;
	}
	 public IData checkElements(IData data) throws Exception{
		IData param=new DataMap();
		param.put("RSP_CODE", "0000");
		param.put("RSP_DESC", "成功！");
		String iboss_product_id = data.getString("PRODUCT_ID");
		IDataset comms = CommparaInfoQry.getCommpara("CSM", "2788", iboss_product_id, data.getString("EPARCHY_CODE"));
		if (IDataUtil.isEmpty(comms)) 
		{
			param.put("RSP_CODE", "4010");
			param.put("RSP_DESC", "产品编码"+iboss_product_id+"不存在！");
			return param;
		}
		String crmProductId  = comms.getData(0).getString("PARA_CODE1");
		String elementTypeCode = comms.getData(0).getString("PARA_CODE2");
		//应集团要求，产品变更走其他接口，此处如果没有查询到，返回错误。
		data.put("MODIFY_TAG", "0");
		data.put("ELEMENT_ID", crmProductId);
		//移动商城1.5添加：校验此元素是否已经订购[排除重复订购]
		checkElement(elementTypeCode, data);
		param.put("ELEMENT_TYPE_CODE", elementTypeCode);
		param.put("ELEMENT_ID", crmProductId);
		return param;
		}
		/******************************************************************
		 * 对操作元素进行处理
		 * 1.对于订购，排除用户已订购的元素
		 * 2.对于取消，排除用户未订购的元素
		 * @param pd
		 * @param td
		 * @param elementTypeCode
		 * @param data
		 * @throws Exception
		 */
		public void checkElement(String elementTypeCode, IData data) throws Exception {
			IDataset userDiscntList = null, userSvcList = null;
			//1.1.如果是服务
			if("S".equals(elementTypeCode)){
				userSvcList = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(data.getString("USER_ID",""), data.getString("ELEMENT_ID"));
				if("0".equals(data.getString("MODIFY_TAG"))){
					if(IDataUtil.isNotEmpty(userSvcList))
						CSAppException.apperr(CrmUserException.CRM_USER_41);
				}else if("1".equals(data.getString("MODIFY_TAG"))){
					if(IDataUtil.isEmpty(userSvcList))
						CSAppException.apperr(CrmUserException.CRM_USER_424);
				}
			//1.2.如果是优惠
			}
//			else if ("D".equals(elementTypeCode)){
//				userDiscntList = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId(data.getString("USER_ID",""),data.getString("ELEMENT_ID"),"");
//				String startDate = userDiscntList.getData(0).getString("START_DATE");//带时分秒
//				String nextAcctDate = DiversifyAcctUtil.getFirstDayNextAcct(data.getString("USER_ID",""));//下账期第一天
//				if("0".equals(data.getString("MODIFY_TAG"))){
//					//如果返回不为空，并且结束不是账期末，那么该用户订购了此优惠
//					if(startDate != null && !"".equals(startDate.trim()) && !startDate.startsWith(nextAcctDate))
//						CSAppException.apperr(CrmUserException.CRM_USER_428);
//					
//				}else if("1".equals(data.getString("MODIFY_TAG"))){
//					//如果返回为空，即没有满足查询条件的结果
//					if(startDate == null || "".equals(startDate.trim()))
//						CSAppException.apperr(CrmUserException.CRM_USER_426);
//					//如果已经是张期末，表示已经退订
//					if(startDate.startsWith(nextAcctDate))
//						CSAppException.apperr(CrmUserException.CRM_USER_426);
//				}
//			}
		}
	 /**
	  * 移动商城现金支付业务办理接口
	  * @param input
	  * @return
	  * @throws Exception
	  */
	 public IData dealCashPayMent(IData input) throws Exception
	{
		 IDataset ret=new DatasetList();
		 IData result=new DataMap();
		 result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		
		 result.put("RSP_CODE", "0000");
		 result.put("RSP_DESC", "成功！");
		 checkPramByKeys(input,"ID_TYPE,SERIAL_NUMBER,ACTION_DATE,ACTION_TIME,OPR_NUMB,TRANSACTION_ID,PAY_POINT,POINT_CHANGE_MONEY,PAY_MONEY,PAY_TYPE,ORDER_ID,BIZ_TYPE");
		 String serialNumber=input.getString("SERIAL_NUMBER");
		 IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		 if(IDataUtil.isEmpty(userInfo))
		{
			 result.put("RSP_CODE", "2998");
			 result.put("RSP_DESC", "该服务号码"+serialNumber+"用户信息不存在！");
			 result.put("X_RSPTYPE", "2");
		     result.put("X_RSPCODE", "2998");
			 return result;
		}
		 if(!"0".equals(userInfo.getString("USER_STATE_CODESET",""))){
				result.put("RSP_CODE", "2005");
			    result.put("RSP_DESC", "该号码已停机！");
			    result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE", "2005");
			    return result;
		}
		 IData data=new DataMap();
		 data.put("IDVALUE", serialNumber);
		 data.put("TRANS_ID", input.getString("TRANSACTION_ID"));
		 ChangeProductBean bean = new ChangeProductBean();
		 IDataset queryResult = bean.queryFlowPayment(data);
		 if(IDataUtil.isNotEmpty(queryResult)){
		     result.put("RSP_CODE", "4042");
			 result.put("RSP_DESC", "重复交易，该交易已处理成功");
			 result.put("X_RSPTYPE", "2");
		     result.put("X_RSPCODE", "4042");
		     return result;
		  }
		 //转换入参
		 input.put("IDTYPE", input.getString("ID_TYPE",""));
		 input.put("IDVALUE", serialNumber);
		 input.put("BIZ_TYPE_CODE", input.getString("BIZ_TYPE",""));
		 input.put("ELEC_TAG", "UMMP_CASH_PAY");//现金充值打标记
		
		// 移动商城2.8 入参报文新增全网统一渠道编码字段，省份需要保存，用于全网渠道统计分析  add by huangyq
		input.put("UNI_CHANNEL",input.getString("UNI_CHANNEL",""));
		input.put("OPRNUMB",input.getString("TRANSACTION_ID",""));
				 
		 IDataset productInfos=input.getDataset("PRODUCT_INFO");
		 if(IDataUtil.isNotEmpty(productInfos)){

			 for(int i=0;i<productInfos.size();i++){
				 IData products=productInfos.getData(i);
				 String operCode=IDataUtil.chkParam(products, "OPR_CODE");
				 products.put("EFFECT_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());//订购返回生效时间，退订返回失效时间，默认当前时间
				 String productType=products.getString("PRODUCT_TYPE","");
				 input.put("PRODUCT_TYPE",productType);
				 input.put("OPR_CODE",operCode);
				 if("01".equals(productType)||"03".equals(productType)){//产品编码
					 input.put("PRODUCT_ID",IDataUtil.chkParam(products, "PRODUCT_ID"));
				 }else{//02 平台业务
					 input.put("SP_ID",IDataUtil.chkParam(products, "SP_ID"));
					 input.put("BIZ_CODE",IDataUtil.chkParam(products, "BIZ_CODE"));
				 }
				 IDataset prodAttr=products.getDataset("PROD_ATTR");
				 if(IDataUtil.isNotEmpty(prodAttr)){
					IDataset attrParam=new DatasetList();
					for(int j=0;j<prodAttr.size();j++){
						IData attr=new DataMap();
						attr.put("ATTR_CODE", prodAttr.getData(j).getString("PROD_ATTR_ID",""));
						attr.put("ATTR_VALUE", prodAttr.getData(j).getString("PROD_ATTR_VALUE",""));
						attrParam.add(attr);
					}
					input.put("ATTR_PARAM", attrParam);
				 }
			   try{
				 if("01".equals(operCode)){//业务开通
					 input.put("EFFT_WAY",convertEfftWay(products.getString("EFFECTIVE_TYPE", "")));
					 input.put("NET_EXPENSES_CODE",products.getString("NET_EXPENSES_CODE",""));
					 logger.error("移动商城现金支付业务开通入参--"+input);
					 ret = CSAppCall.call("SS.ChangeProductIBossSVC.openSvc", input);
				 }else if("02".equals(operCode)){//业务退订
					 input.put("OPR_CODE", "01");// 业务退订要求操作码为01,此处重新赋值,大接口中"01-业务开通
					 input.put("NET_EXPENSES_CODE",products.getString("NET_EXPENSES_CODE",""));
					 logger.error("移动商城现金支付业务退订入参--"+input);
					 ret = CSAppCall.call("SS.ChangeProductIBossSVC.closeSvc", input);
				 }
				 logger.error("调产品变更返回--"+ret);
				 if(IDataUtil.isNotEmpty(ret)){
					 String rspType=ret.getData(0).getString("X_RSPTYPE","0");
					 String effectTime=convertDateStr(ret.getData(0).getString("EFFECT_TIME"));
					 if(effectTime.length()<14){
						 effectTime= SysDateMgr.getSysDateYYYYMMDDHHMMSS();
					 }
					 products.put("EFFECT_TIME",effectTime);
					 result.put("X_RSPTYPE",rspType);
					 result.put("RSP_CODE",ret.getData(0).getString("X_RESULTCODE","0000"));
					 result.put("RSP_DESC",ret.getData(0).getString("X_RESULTINFO","成功！"));

				 }
			    }catch(Exception ex){
			    	result.put("RSP_CODE", "3004");
					result.put("RSP_DESC", "产品变更报错："+ex.getMessage());
					result.put("X_RSPTYPE", "2");
				    result.put("X_RSPCODE", "3004");
		
				}
			 }
			 
			 result.put("PROD_ORDER_REC", productInfos); 
		 }else{
			 result.put("RSP_CODE", "4010");
			 result.put("RSP_DESC","PRODUCT_INFO节点不存在！");
			 result.put("X_RSPTYPE", "2");
			 result.put("X_RSPCODE", "4010");
			 return result;  
		 }
		 return result;
	}
     private void checkPramByKeys(IData data, String keyNamesStr) throws Exception
    {
        String keyNames[] = keyNamesStr.split(",");
        for (String strColName : keyNames)
        {
            IDataUtil.chkParam(data, strColName);
        }

    } 
     /**
      * 移动商城生效编码： 01 立即生效 02 次日生效 03 下周期生效 转换成 手机营业厅生效编码： 02立即生效 04下月生效
      * 
      * @param inparams
      * @return
      */
     public static String convertEfftWay(String inEfftWay) throws Exception
     {
         String result;
         if (inEfftWay.equals("01"))
             result = "02";
         else if (inEfftWay.equals("02"))
             result = "03";
         else if (inEfftWay.equals("03"))
             result = "04";
         else
             result = "";

         return result;
     }
     /**
      * 将2013-12-03 15:21:41转换成20131203152141
      * 
      * @param inparams
      * @return
      */
     public String convertDateStr(String orginDateStr) throws Exception
     {
         StringBuilder result = new StringBuilder(50);
         for (int i = 0; i < orginDateStr.length(); ++i)
         {
             if ('0' <= orginDateStr.charAt(i) && '9' >= orginDateStr.charAt(i))
                 result.append(orginDateStr.charAt(i));
         }

         return result.toString();
     }
     public IData saleActiveOrder(IData data) throws Exception {
		 logger.info("营销案办理接口开始，入参:"+data.toString());
		 String serialNumber =data.getString("SERIAL_NUMBER");
	     IData result = new DataMap();
	     result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	     result.put("RSP_CODE", "0000");
	     result.put("RSP_DESC", "成功！");
		 //用户身份凭证验证
		 try{
			 IData param1 = new DataMap();
			 param1.put("IDENT_CODE", data.getString("IDENT_CODE", ""));
			 param1.put("SERIAL_NUMBER", serialNumber);
			 UserIdentBean userIdentBean = BeanManager.createBean(UserIdentBean.class);
			 userIdentBean.identAuth(param1);
		 }catch(Exception e){
			 result.put("RSP_CODE", "3018");
			 result.put("RSP_DESC","凭证校验失败："+e.getMessage());
			 result.put("X_RSPTYPE", "2");
			 result.put("X_RSPCODE", "3018");
			 result.put("VALID_FLAG", "1");//鉴权不通过
			 result.put("REASON", "凭证校验失败："+e.getMessage());
			 return result;
		 }

		 insertTableData(data);
		 StringBuilder sql=new StringBuilder();
		 sql.append("UPDATE TF_B_MOBILE_STORE_ORDER SET STATE=:STATE,FINISH_DATE=:FINISH_DATE,RESPOSE_INFO=:RESPOSE_INFO WHERE TRANSACTION_ID=:TRANSACTION_ID ");

		 IData updateData = new DataMap();
		 String flag = data.getString("FLAG","");//0：营销活动资格校验;1：营销活动业务办理
		 String activeId = data.getString("ACTIVITY_NO");//营销活动id
         String productId = data.getString("PRODUCT_ID");//商品id
         String oprCode = data.getString("OPR_CODE");//操作代码1、办理 2、退订
         String effectiveType = data.getString("EFFECTIVE_TYPE","");//生效方式，若未传则默认处理
         String discount = data.getString("DISCOUNT","1");//生效方式，若未传则默认处理
		 String goodsdesc = data.getString("PRODUCTDESC");//0：套餐+营销同时办理;1：标识只办理合约;2：标志只办理商品信息
		 String bisiType = data.getString("BISI_TYPE");//0：标识传的商品编码是主套餐;1：标识的传的商品编码是增值业务
		 String transIdO = data.getString("TRANSACTION_ID");
		 updateData.put("TRANSACTION_ID",transIdO);
		 if(StringUtils.isBlank(activeId) && StringUtils.isBlank(productId)){
			 result.put("RSP_CODE", "2998");
			 result.put("RSP_DESC","ActivityNo,GoodsID 不可同时为空！");
			 result.put("X_RSPTYPE", "2");
			 result.put("X_RSPCODE", "2998");
			 updateData.put("STATE","2");
			 updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
			 updateData.put("RESPOSE_INFO","2998|ActivityNo,GoodsID 不可同时为空！");
			 Dao.executeUpdate(sql,updateData);
			 return result;
		 }


		 boolean changePro = true;
		 boolean platSvcOrder = true;
		 String elementId ="";
		 String elementTypeCode = "";
		 //商品编码转换
		 if(StringUtils.isNotBlank(productId)){
			 IDataset myDataset = CommparaInfoQry.getCommparaAllCol("CSM", "2788", productId, getTradeEparchyCode());
			 if ((myDataset == null) || (myDataset.size() == 0))
			 {
				 CSAppException.appError("100001", "参数表中未配置相应的产品编码(" + productId + ")转换关系！");
			 }
			 elementId = myDataset.getData(0).getString("PARA_CODE1");
			 elementTypeCode = myDataset.getData(0).getString("PARA_CODE2");
		 }

         if("P".equals(elementTypeCode)){
        	 IData mainPro = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        	 String mainProId = mainPro.getString("PROUDCT_ID","");
        	 if(mainProId.equals(elementId)){
        		 changePro = false;
        	 }
         }
		 IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);

		 if("Z".equals(elementTypeCode)){
			 IDataset userPlatSvcDataset = UserPlatSvcInfoQry.qryPlatSvcByUserIdServiceId(userInfo.getString("USER_ID"), elementId);
			 if(IDataUtil.isNotEmpty(userPlatSvcDataset)){
				 platSvcOrder = false;
			 }
		 }

         //产品变更入参
         IData inParam = new DataMap();
         // 移动商城2.8 渠道编码保存20190906 huangyq
         inParam.put("UNI_CHANNEL", data.getString("UNI_CHANNEL",""));
		 inParam.put("NET_EXPENSES_CODE", data.getString("NET_EXPENSES_CODE",""));
		 inParam.put("OPRNUMB", data.getString("TRANSACTION_ID",""));
         //生效时间
         String startDate = getStartDate(effectiveType);
		 inParam.put("IN_MODE_CODE", "6");
		 inParam.put("ELEMENT_TYPE_CODE", elementTypeCode);
		 inParam.put("ELEMENT_ID", elementId);
		 inParam.put("BOOKING_TAG", "0");
		 inParam.put("DATE_TAG", "0");
		 inParam.put("START_DATE", startDate);
		 inParam.put("SERIAL_NUMBER", serialNumber);
		 IDataset elements = new DatasetList();
		 IData element = new DataMap();
		 element.put("ELEMENT_ID", elementId);
		 element.put("ELEMENT_TYPE_CODE", elementTypeCode);
		 //营销活动入参
		 String categoryId = elementId;
		 IDataset upcInfo = UpcCall.qryCatalogByOfferId(activeId, "K");
    	 if(IDataUtil.isNotEmpty(upcInfo)){
    		 categoryId = upcInfo.getData(0).getString("CATALOG_ID");
    	 }
		 IData saleParam = new DataMap();
		// 移动商城2.8 渠道编码保存20190906 huangyq
		 saleParam.put("UNI_CHANNEL", data.getString("UNI_CHANNEL",""));
		 saleParam.put("NET_EXPENSES_CODE", data.getString("NET_EXPENSES_CODE",""));
		 saleParam.put("OPRNUMB", data.getString("TRANSACTION_ID",""));
		 
		 String fee = "0";
    	 IDataset activeInfos = UpcCall.queryOfferPriceRelByOfferId("K",activeId);
    	 if(IDataUtil.isNotEmpty(activeInfos)){
    		 IDataset feeInfo = UpcCall.queryPriceByPriceId(activeInfos.getData(0).getString("PRICE_ID"));
    		 logger.debug("FEEINFO------------------"+feeInfo);
        	 if(IDataUtil.isNotEmpty(feeInfo)){
        		 fee = upcInfo.getData(0).getString("FEE");
        	 }
    	 }
		 saleParam.put("SERIAL_NUMBER", serialNumber);
    	 saleParam.put("TRADE_TYPE_CODE", "240");
    	 saleParam.put("PRODUCT_ID", categoryId);
    	 saleParam.put("PACKAGE_ID", activeId);
    	 saleParam.put("FEE", fee);
    	 saleParam.put("NO_TRADE_LIMIT", "TRUE");
    	 if("1".equals(flag)){

	         if("1".equals(oprCode)){
	        	 //办理
	        	 inParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
	        	 element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
	        	 saleParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
	        }else if("2".equals(oprCode)){
	        	//退订
	        	inParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
	        	element.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
	        	saleParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
	        }else{
	        	result.put("RSP_CODE", "2998");
	            result.put("RSP_DESC","操作代码错误！");
	            result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE", "2998");
	            updateData.put("STATE","2");
				 updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
				updateData.put("RESPOSE_INFO","2998|操作代码错误！");
				Dao.executeUpdate(sql,updateData);
	            return result;
	        }
			 IData activityInfo = new DataMap();

			 try{
        		elements.add(element);
        		inParam.put("ELEMENTS", elements);
        		if(StringUtils.isNotBlank(productId)){
					if(changePro && "0".equals(bisiType)){
						IDataset resultList = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti",inParam);
					}else if (platSvcOrder && "1".equals(bisiType)){

						createPlatSvcOrder(serialNumber,userInfo.getString("USER_ID"),elementId,flag,result);
						if(!"0000".equals(result.getString("RSP_CODE"))){
							updateData.put("STATE","2");
							updateData.put("RESPOSE_INFO","2998|"+result.getString("RSP_DESC"));
							updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
							Dao.executeUpdate(sql,updateData);
							return result;
						}

					}
					activityInfo.put("PRODUCT_START_TIME", startDate);
				}

       	    }
        	catch (Exception e)
        	{
        		 SessionManager.getInstance().rollback();
        		 result.put("X_RSPTYPE", "2");
        		 result.put("X_RSPCODE","3006");
        		 result.put("RSP_CODE", "3006");
        		 logger.error("产品变更受理报错"+transIdO,e);
        		 result.put("RSP_DESC","产品变更受理报错:"+e.getMessage());
				 updateData.put("STATE","2");
				 String exceptionMsg = ExceptionUtils.getStackTrace(e);
				 if(exceptionMsg.length()>2000){
					exceptionMsg = exceptionMsg.substring(0,2000);

				 }
				 updateData.put("RESPOSE_INFO","3006|"+exceptionMsg);
				 updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
				 Dao.executeUpdate(sql,updateData);
        		 return result;
        	}
        	try {
        		 if(StringUtils.isNotBlank(activeId)){
					 IDataset sale=CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleParam);
					 activityInfo.put("ACTIVITY_START_TIME", startDate);

				 }

			} catch (Exception e) {
				 SessionManager.getInstance().rollback();
	       		 result.put("X_RSPTYPE", "2");
	       		 result.put("X_RSPCODE","3006");
	       		 result.put("RSP_CODE", "3006");
	       		 logger.error("营销案办理接口异常"+transIdO,e);
	       		 result.put("RSP_DESC","营销活动受理报错:"+e.getMessage());
				 updateData.put("STATE","2");
				 String exceptionMsg = ExceptionUtils.getStackTrace(e);
				 if(exceptionMsg.length()>2000){
					exceptionMsg = exceptionMsg.substring(0,2000);

				 }
				 updateData.put("RESPOSE_INFO","3006|"+exceptionMsg);
				 updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
				 Dao.executeUpdate(sql,updateData);

	       		 return result;
			} 
	        IDataset activityInfos = new DatasetList();
	        activityInfo.put("ACTIVITY_NO", activeId);
	        activityInfo.put("PRODUCT_ID", productId);

	        updateData.put("STATE","1");
	        updateData.put("RESPOSE_INFO","0000|"+activityInfo.toString());
			 updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
	        Dao.executeUpdate(sql,updateData);

	        activityInfos.add(activityInfo);
	        result.put("ACTIVITY_INFO_RES", activityInfos);
    	}else if("0".equals(flag)){
		    result.put("CHECK_RESULT", "0");
		    inParam.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
			saleParam.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);


			if(IDataUtil.isEmpty(userInfo)){
				result.put("RSP_CODE", "2998");
			    result.put("RSP_DESC", "用户信息不存在！");
			    result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE", "2998");
	            result.put("CHECK_RESULT", "1");
	            result.put("RESULT_RES","用户信息不存在！");
				updateData.put("STATE","2");
				updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
				updateData.put("RESPOSE_INFO","2998|用户信息不存在！");
				Dao.executeUpdate(sql,updateData);
			    return result;
			}	
			if(!"0".equals(userInfo.getString("USER_STATE_CODESET",""))){
				result.put("RSP_CODE", "2005");
			    result.put("RSP_DESC", "该号码已停机！");
			    result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE", "2005");
	            result.put("CHECK_RESULT", "1");
	            result.put("RESULT_RES","该号码已停机！");
				updateData.put("STATE","2");
				updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
				updateData.put("RESPOSE_INFO","2998|该号码已停机！");
				Dao.executeUpdate(sql,updateData);
			    return result;
			}
			String userId = userInfo.getString("USER_ID");
			IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
			if(IDataUtil.isEmpty(productInfo)){
				result.put("RSP_CODE", "2998");
			    result.put("RSP_DESC", "用户主产品信息不存在！");
			    result.put("CHECK_RESULT", "1");
			    result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE", "2998");
	            result.put("RESULT_RES","用户主产品信息不存在！");
				updateData.put("STATE","2");
				updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
				updateData.put("RESPOSE_INFO","2998|用户主产品信息不存在！");
				Dao.executeUpdate(sql,updateData);
			    return result;
			}
			//营销活动 产品变更预受理校验
       	 	try{
				inParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
				element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
				elements.add(element);
        		inParam.put("ELEMENTS", elements);
				if(StringUtils.isNotBlank(productId)){
					if(changePro && "0".equals(bisiType)){
						IDataset resultList = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti",inParam);
					}else if (platSvcOrder && "1".equals(bisiType)){

						createPlatSvcOrder(serialNumber,userInfo.getString("USER_ID"),elementId,flag,result);
						if(!"0000".equals(result.getString("RSP_CODE"))){
							updateData.put("STATE","2");
							updateData.put("RESPOSE_INFO","0000|"+result.getString("RSP_DESC"));
							updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
							Dao.executeUpdate(sql,updateData);
							return result;
						}

					}
				}
	      	 } catch (Exception e) {
	       		 result.put("X_RSPTYPE", "2");
	       		 result.put("X_RSPCODE","3006");
	       		 result.put("RSP_CODE", "3006");
	       		 result.put("RSP_DESC","产品变更校验"+e.getMessage());
	       		 result.put("RESULT_RES","产品变更校验"+e.getMessage());
	       		 logger.error("营销案办理接口异常"+transIdO,e);
				updateData.put("STATE","2");
				String exceptionMsg = ExceptionUtils.getStackTrace(e);
				if(exceptionMsg.length()>2000){
					exceptionMsg = exceptionMsg.substring(0,2000);

				}
				updateData.put("RESPOSE_INFO","3006|"+exceptionMsg);
				updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
				Dao.executeUpdate(sql,updateData);
	       		 result.put("CHECK_RESULT", "1");
	       		 return result;
	       	 }
	      	try {
				if(StringUtils.isNotBlank(activeId)){
					IDataset sale=CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleParam);
				}
			} catch (Exception e) {
				 result.put("X_RSPTYPE", "2");
	       		 result.put("X_RSPCODE","3006");
	       		 result.put("RSP_CODE", "3006");
	       		 result.put("RSP_DESC","营销活动校验"+e.getMessage());
	       		 result.put("RESULT_RES","营销活动校验"+e.getMessage());
				logger.error("营销案办理接口异常"+transIdO,e);

	       		 result.put("CHECK_RESULT", "1");

				updateData.put("STATE","2");
				String exceptionMsg = ExceptionUtils.getStackTrace(e);
				if(exceptionMsg.length()>2000){
					exceptionMsg = exceptionMsg.substring(0,2000);

				}
				updateData.put("RESPOSE_INFO","3006|"+exceptionMsg);
				updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
				Dao.executeUpdate(sql,updateData);

	       		 return result;
			} 
    	}else{
    		result.put("RSP_CODE", "2998");
		    result.put("RSP_DESC", "业务类型错误！");
		    result.put("X_RSPTYPE", "2");
            result.put("X_RSPCODE", "2998");
            updateData.put("STATE","2");
			updateData.put("RESPOSE_INFO","2998|业务类型错误！");
			updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
			Dao.executeUpdate(sql,updateData);
		    return result;
    	}
		 updateData.put("STATE","1");
		 updateData.put("RESPOSE_INFO","0000|成功！");
		 updateData.put("FINISH_DATE",SysDateMgr.getSysTime());
		 Dao.executeUpdate(sql,updateData);
    	 return result;
 	}

	private void insertTableData(IData data) throws Exception {
     	String transationId = data.getString("TRANSATION_ID");
     	IData proData = data.getData("OPPSE_PROD_INFO");
     	if(IDataUtil.isNotEmpty(proData)){
			proData.put("TRANSATION_ID",transationId);
			Dao.insert("TF_B_MOBILE_STORE_PROD",proData);
			IDataset attrData = proData.getDataset("PROD_ATTR");
			if(IDataUtil.isNotEmpty(attrData)){
				for(Object obj : attrData){
					((IData)obj).put("ORDER_ID",proData.getString("ORDER_ID"));
				}

				Dao.insert("TF_B_MOBILE_STORE_ATTR",attrData);
			}

		}
     	data.put("ACCPCT_DATE",SysDateMgr.getSysTime());
		Dao.insert("TF_B_MOBILE_STORE_ORDER",data);
	}

	private void createPlatSvcOrder(String serialNumber, String user_id, String elementId, String flag, IData result) throws Exception {

		//办理平台业务
		IDataset ids = new DatasetList();
		IDataset platInfos = UpcCall.queryPlatSvc(elementId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, null, null);
		if(IDataUtil.isNotEmpty(platInfos) ){
			IData platParam = new DataMap();
			platParam.put("SERVICE_ID", elementId);
			platParam.put("SP_CODE", platInfos.getData(0).getString("SP_CODE", ""));
			platParam.put("BIZ_CODE", platInfos.getData(0).getString("BIZ_CODE", ""));
			platParam.put("BIZ_TYPE_CODE", platInfos.getData(0).getString("BIZ_TYPE_CODE", ""));
			platParam.put("MODIFY_TAG", "");
			platParam.put("OPER_CODE", PlatConstants.OPER_ORDER);
			ids.add(platParam);
		}else{

			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE","2998");
			result.put("RSP_CODE", "2998");
			result.put("RSP_DESC","平台服务不存在");
			result.put("RESULT_RES","平台服务不存在");
			result.put("CHECK_RESULT", "1");
		}

		if(IDataUtil.isNotEmpty(ids)){
			IData input = new DataMap();
			if("0".equals(flag)){
				input.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
			}
			input.put("SERIAL_NUMBER",serialNumber);
			input.put("USER_ID",user_id);
			input.put("SELECTED_ELEMENTS",ids);
			input.put("REMARK","移动商城营销案接口办理");
			CSAppCall.call("SS.PlatRegSVC.tradeReg", input).first();
		}
	}

	private String getStartDate(String efftWay) throws Exception
     {
    	 logger.debug("efftWay------------------"+efftWay);
		 String startDate = SysDateMgr.getFirstDayOfNextMonth4WEB()+SysDateMgr.START_DATE_FOREVER;
         if ("01".equals(efftWay))
         {
             startDate = SysDateMgr.getSysTime();
         }
         else if ("02".equals(efftWay))
         {
             startDate = SysDateMgr.getTomorrowDate() + SysDateMgr.START_DATE_FOREVER;
         }else if ("03".equals(efftWay))
		 {
			 startDate = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate()) + SysDateMgr.START_DATE_FOREVER;
		 }
         logger.debug("startDate------------------"+startDate);
         return startDate;
     }
     
     /**
      *  移动商城2.8 通用活动办理预校验接口
      *   1. 校验用户状态
      *   2. 判断用户是否参见过活动
      * @param input
      * @return 
      * @throws Exception
      */
     public IData checkCommActive(IData input) throws Exception{
    	 checkPramByKeys(input,"SERIAL_NUMBER,BIZ_TYPE_CODE,OPR_TIME,ACTIVITY_ID,ACTIVITY_NAME,TRANS_IDO,UNI_CHANNEL");
    	 String serialNumber = input.getString("SERIAL_NUMBER");
    	 IData result = new DataMap();
    	 result.put("RSP_CODE", "0000");
		 result.put("RSP_DESC", "校验成功！");
		 result.put("VALID_FLAG", "0");
    	 result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	 // 校验用户状态
    	 IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		 if(IDataUtil.isEmpty(userInfo)){
			 result.put("RSP_CODE", "2998");
			 result.put("RSP_DESC", "该服务号码"+serialNumber+"用户信息不存在！");
			 result.put("X_RSPTYPE", "2");
		     result.put("X_RSPCODE", "2998");
		     result.put("VALID_FLAG", "1");
		     result.put("REASON", "鉴权不通过，用户状态异常！");
			 return result;
		 }
		 if(!"0".equals(userInfo.getString("USER_STATE_CODESET",""))){
				result.put("RSP_CODE", "2005");
			    result.put("RSP_DESC", "该号码已停机！");
			    result.put("X_RSPTYPE", "2");
	            result.put("X_RSPCODE", "2005");
	            result.put("VALID_FLAG", "1");
	            result.put("REASON", "鉴权不通过，用户状态异常！");
			    return result;
		 }
		//用户身份凭证验证
	     try{
			  IData param1 = new DataMap();
	          param1.put("IDENT_CODE", input.getString("IDENT_CODE", ""));
	          param1.put("SERIAL_NUMBER", serialNumber);
			  UserIdentBean userIdentBean = BeanManager.createBean(UserIdentBean.class);
			  userIdentBean.identAuth(param1);
		 }catch(Exception e){
		      result.put("RSP_CODE", "3018");
		      result.put("RSP_DESC","凭证校验失败："+e.getMessage());
		      result.put("X_RSPTYPE", "2");
		      result.put("X_RSPCODE", "3018");
		      result.put("VALID_FLAG", "1");//鉴权不通过
		      result.put("REASON", "凭证校验失败："+e.getMessage());
		      return result;
		 }
	     if(!"99992019CWLSLL".equals(input.getString("ACTIVITY_ID",""))){
	    	 result.put("RSP_CODE", "4024");
	    	 result.put("RSP_DESC", "活动编码不存在");
	    	 result.put("VALID_FLAG", "1");
	    	 return result;
	     }
    	 // 判断用户是否参与过活动
		 String productId = "";
		 IDataset commparaInfos9932 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9932",null,null);
		if(IDataUtil.isNotEmpty(commparaInfos9932)){
			productId = commparaInfos9932.getData(0).getString("PARAM_CODE");
		}else{
			result.put("RSP_CODE", "2998");
		    result.put("RSP_DESC", "未查到配制活动！");
		    result.put("X_RSPTYPE", "2");
            result.put("X_RSPCODE", "2998");
            result.put("VALID_FLAG", "1");
		    return result;
		}
		 String userId = userInfo.getString("USER_ID");
//		 IDataset dsAlready=UserDiscntInfoQry.getUserByDiscntCode(userId,productId);
		 IDataset dsAlready= SaleActiveInfoQry.getUserAllSaleActiveInfo(userId, productId);
     	 if (!dsAlready.isEmpty()){
     		result.put("RSP_CODE", "2000");
		    result.put("RSP_DESC", "用户已参加过该活动！");
		    result.put("VALID_FLAG", "1");
            result.put("REASON", "鉴权不通过，用户已参加过该活动！");
            return result;
     	 }
		 
    	 return result;
     }
     
     /**
      * 移动商城 2.8  通用活动办理同步接口
      *  1.用户身份凭证校验
      *  2.调用原有的查网龄送流量接口，返回结果
      * @param input
      * @return
      * @throws Exception
      */
     public IData dealCommActive(IData input) throws Exception{
    	 checkPramByKeys(input,"SERIAL_NUMBER,BIZ_TYPE_CODE,OPR_TIME,ACTIVITY_ID,ACTIVITY_NAME,TRANS_IDO,UNI_CHANNEL");
    	 String serialNumber = input.getString("SERIAL_NUMBER");
    	 IData result = new DataMap();
    	 result.put("RSP_CODE", "0000");
		 result.put("RSP_DESC", "成功！");
    	 result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	 IDataset rst = new DatasetList();
    	 //用户身份凭证验证
    	 IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		 if(IDataUtil.isEmpty(userInfo)){
			 result.put("RSP_CODE", "2998");
			 result.put("RSP_DESC", "该服务号码"+serialNumber+"用户信息不存在！");
			 return result;
		 }
		 if(!"0".equals(userInfo.getString("USER_STATE_CODESET",""))){
				result.put("RSP_CODE", "2005");
			    result.put("RSP_DESC", "该号码已停机！");
			    return result;
		 }
	     /*try{
			  IData param1 = new DataMap();
	          param1.put("IDENT_CODE", input.getString("IDENT_CODE", ""));
	          param1.put("SERIAL_NUMBER", serialNumber);
			  UserIdentBean userIdentBean = BeanManager.createBean(UserIdentBean.class);
			  userIdentBean.identAuth(param1);
		 }catch(Exception e){
		      result.put("RSP_CODE", "3018");
		      result.put("RSP_DESC","凭证校验失败："+e.getMessage());
		      return result;
		 }*/
	     if("99992019CWLSLL".equals(input.getString("ACTIVITY_ID"))){
	    	
	    	//判断用户是否参与过活动
			 String productId = "";
			 IDataset commparaInfos9932 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9932",null,null);
			 if(IDataUtil.isNotEmpty(commparaInfos9932)){
				productId = commparaInfos9932.getData(0).getString("PARAM_CODE");
			 }else{
				result.put("RSP_CODE", "2998");
			    result.put("RSP_DESC", "未查到配制活动！");
			    return result;
			 }
			 String userId = userInfo.getString("USER_ID");
			 IDataset dsAlready= SaleActiveInfoQry.getUserAllSaleActiveInfo(userId, productId);
	     	 if (!dsAlready.isEmpty()){
	     		result.put("RSP_CODE", "2000");
			    result.put("RSP_DESC", "用户已参加过该活动！");
			    // 重复参加时 下发短信 
     			String content = "尊敬的客户，您已参加过当前活动，不能重复参加，感谢您的支持！中国移动。";
	    		insrtSms(serialNumber, userInfo.getString("USER_ID"), content);
	    		
	            return result;
	     	 }
	    	// 再调用原有的查网龄送流量接口，返回结果。
	         try{
	         	//接口入参
	            IData inParam = new DataMap();
	     		inParam.put("SERIAL_NUMBER", serialNumber);
	     		inParam.put("ACCESS_NUM", serialNumber);
		      	inParam.put("TRANS_IDO", input.getString("TRANS_IDO"));
		      	inParam.put("ACTIVITY_ID", input.getString("ACTIVITY_ID"));
		      	inParam.put("ACTIVITY_NAME", input.getString("ACTIVITY_NAME"));
		      	inParam.put("UNI_CHANNEL", input.getString("UNI_CHANNEL"));
		      	inParam.put("IS_COMM_ACTIVE", "1");
		      	
	     		IDataset resultList = CSAppCall.call("SS.SaleActiveIntfCheckSVC.orderActiveBySnForIBoss",inParam);
	     		
	     		result.put("RSP_CODE", resultList.getData(0).getString("X_RESULTCODE",""));
	     		result.put("RSP_DESC", resultList.getData(0).getString("X_RESULTINFO",""));
	     		
	     		if("299".equals(resultList.getData(0).getString("X_RESULTCODE"))){
	     			result.put("RSP_DESC", "不符合办理条件");
	     			// 不符合条件时 下发短信 
	     			String content = "尊敬的客户，未获取到赠送流量，您当前不符合活动要求，感谢您的支持！中国移动。";
		    		insrtSms(serialNumber, userInfo.getString("USER_ID"), content);
	     		}
	     		    		
	         }catch(Exception e){
	         	result.put("RSP_CODE", "2998");
	            result.put("RSP_DESC","调用原有的查网龄送流量接口："+e.getMessage());
	            return result;
	         }
	     	 
	     }else{
	    	result.put("RSP_CODE", "2998");
			result.put("RSP_DESC", "活动标识ACTIVITY_ID错误！");
			return result;
	     }
        
    	return result;
     }
     private void insrtSms(String serialNumber,String userId,String content) throws Exception{
 		IData sendInfo = new DataMap();
 		String eparchyCode = RouteInfoQry.getEparchyCodeBySn(serialNumber);
 		sendInfo.put("EPARCHY_CODE", eparchyCode);
 		sendInfo.put("RECV_OBJECT", serialNumber);
 		sendInfo.put("RECV_ID", userId);
 		sendInfo.put("SMS_PRIORITY", "50");
 		sendInfo.put("NOTICE_CONTENT", content);
 		sendInfo.put("REMARK", "查网龄领流量失败");
 		sendInfo.put("FORCE_OBJECT", "10086");
 		SmsSend.insSms(sendInfo,eparchyCode);
 	}
     
     /**
      * 开通/关闭高频骚扰电话拦截服务
      * @param data
      * @return 
      * @throws Exception
      */
     public IData interceptHarassmentCall(IData input) throws Exception{
    	 checkPramByKeys(input,"ID_TYPE,SERIAL_NUMBER,CHANNEL_ID,OPR_CODE,BIZ_TYPE,OPR_TIME,BIZ_VERSION");//OPR_NUMB 去掉
    	 String serialNumber=input.getString("SERIAL_NUMBER");
    	 IData result=new DataMap();
    	 result.put("ID_TYPE", input.getString("ID_TYPE"));
    	 result.put("SERAIL_NUMBER", serialNumber);
    	 result.put("BIZ_CODE", "0000");
    	 result.put("BIZ_DESC", "成功");
    	 result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	 result.put("OPR_NUMB", input.getString("OPR_NUMB",""));
    	 IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	 if(IDataUtil.isEmpty(userInfo)){
    		 result.put("BIZ_CODE", "2009");
    		 result.put("BIZ_DESC", "用户信息不存在或异常！");
    		 return result;
    	 }
    	 String userId = userInfo.getString("USER_ID");
    	 IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
    	 if(IDataUtil.isEmpty(productInfo)){
    		 result.put("BIZ_CODE", "2998");
    		 result.put("BIZ_DESC", "用户主产品信息不存在！");
    		 return result;
    	 }
    	 String productId=productInfo.getString("PRODUCT_ID");
    	 IData set = StaticInfoQry.getStaticInfoByTypeIdDataId("HARASSMENT_CALL_SVC", input.getString("BIZ_TYPE",""));//根据业务类型查询服务ID
    	 if(IDataUtil.isEmpty(set)){
    		 result.put("BIZ_CODE", "2998");
        	 result.put("BIZ_DESC", "静态参数HARASSMENT_CALL_SVC未配置!"); 
        	 return result;
    	 }
    	 String svcId=set.getString("DATA_NAME","");
    	 IDataset orderElement = UProductElementInfoQry.getElementInfosByProductId(productId); 
         IDataset  pkgElems = DataHelper.filter(orderElement, "ELEMENT_TYPE_CODE="+BofConst.ELEMENT_TYPE_CODE_SVC+",ELEMENT_ID="+svcId);
    	 if(IDataUtil.isEmpty(pkgElems)){
    		 result.put("BIZ_CODE", "2998");
    		 result.put("BIZ_DESC", "用户主产品和元素[" + svcId+ "]没有订购关系,不能操作此元素！"); 
    		 return result;
    	 }
    	 //规则校验
    	 input.put("SERVICE_ID", svcId);
    	 input.put("USER_ID",userId);
    	 IData checkResult=checkRuleParam(input);
    	 if(!"0000".equals(checkResult.getString("BIZ_CODE",""))){
    		 result.put("BIZ_CODE", checkResult.getString("BIZ_CODE",""));
        	 result.put("BIZ_DESC", checkResult.getString("BIZ_DESC","")); 
        	 return result;
    	 }
    	 //调产品变更接口
    	 String sqid=SeqMgr.getInstId();
    	 IDataset selectedElements = new DatasetList();
         IData element = new DataMap();
    	 element.put("ELEMENT_ID", svcId);
    	 element.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SVC);
    	 element.put("PRODUCT_ID", productId);
    	 element.put("PACKAGE_ID", pkgElems.getData(0).getString("PACKAGE_ID",""));
    	 element.put("START_DATE", SysDateMgr.decodeTimestamp(input.getString("EFFT_TIME"), SysDateMgr.PATTERN_STAND));
         element.put("END_DATE", SysDateMgr.decodeTimestamp(input.getString("INVALID_TIME"), SysDateMgr.PATTERN_STAND));
    	 if("23".equals(input.getString("OPR_CODE",""))){//服务开通
    		 element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD); 
    	 }else if("24".equals(input.getString("OPR_CODE",""))){//服务关闭
    		 element.put("MODIFY_TAG",  BofConst.MODIFY_TAG_DEL);
    	 }
    	 selectedElements.add(element);
    	 IData callParam = new DataMap();
    	 callParam.put("SERIAL_NUMBER", serialNumber);
    	 callParam.put("SELECTED_ELEMENTS", selectedElements);
    	 callParam.put("OPR_NUMB", input.getString("OPR_NUMB","COP731"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+sqid.substring(sqid.length()-12)));
       	 callParam.put("CHANNEL_ID", input.getString("CHANNEL_ID"));
       	 callParam.put("NEED_CHANNEL_TAG", "INTERCEPT_CALL");
    	 IDataset resultList = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg",callParam);
    	 logger.error("调产品变更返回--"+resultList);
    	 if("23".equals(input.getString("OPR_CODE",""))){//服务开通
    		 result.put("EFF_TIME",input.getString("EFFT_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS()));
    		 result.put("EXP_TIME",input.getString("INVALID_TIME",""));
    	 }else if("24".equals(input.getString("OPR_CODE",""))){//服务关闭
    		 result.put("EFF_TIME",input.getString("INVALID_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS()));
    	 } 
    	 return result;
     }
     /**
      * 处理订单
      * @param data
      * @return
      * @throws Exception
      */
     public IData checkRuleParam(IData data) throws Exception
     {
    	 IData param=new DataMap();
    	 param.put("BIZ_CODE", "0000");
    	 param.put("BIZ_DESC", "成功");
    	 String userId = data.getString("USER_ID","");
    	 String svcId = data.getString("SERVICE_ID","");
    	 String oprCode=data.getString("OPR_CODE","");
    	 //查询用户是否订购Volte服务，如未订购则不允许订购高频拦截骚扰电话服务功能
    	 IDataset userSvcInfos = UserSvcInfoQry.queryUserSvcByUseridSvcid(userId,"190");
    	 if(IDataUtil.isEmpty(userSvcInfos)){
    		 param.put("BIZ_CODE", "2034");
        	 param.put("BIZ_DESC", "用户未开通VoLTE,不能订购此业务！"); 
        	 return param;
    	 }
    	 IDataset SvcInfos = UserSvcInfoQry.queryUserSvcByUseridSvcid(userId,svcId);
    	 if("23".equals(oprCode)&&IDataUtil.isNotEmpty(SvcInfos)){//开通并且已订购过则报错
    		 param.put("BIZ_CODE", "2000");
        	 param.put("BIZ_DESC", "用户已开通该业务！"); 
        	 return param; 
    	 }else if("24".equals(oprCode)&&IDataUtil.isEmpty(SvcInfos)){//退订并且没有订购则报错
    		 param.put("BIZ_CODE", "2001");
        	 param.put("BIZ_DESC", "用户未开通该业务不能进行退订！"); 
        	 return param; 
    	 }
    	 return param;
     }

	/**
	 * 移动商城接口规范----6.66	用户宽带信息查询接口
	 * 1.1.用户进入用户宽带信息查询
	 * 1.2一级电渠发起用户宽带查询请求，经由网状网透传至用户归属省CRM。请求信息包括：用户身份凭证信息，手机号码等。
	 * 1.3省CRM侧对请求信息中的用户身份凭证信息进行验证，验证要求包括：请求信息中的用户身份凭证是否和CRM的用户身份凭证一致，身份凭证是否在有效期内。
	 * 1.4在省CRM查询用户用户宽带信息，宽带账号，宽带最大数据，宽带生失效时间等信息
	 * 1.5 省CRM记录查询查询交易日志信息
	 * 1.6 返回用户宽带信息信息
	 * 1.7展示用户宽带信息。
	 * @param input
	 * @return
	 */
    public IData qryBroadbandInfo(IData input) {


    	String serialNumber = input.getString("IDVALUE");
		String idType = input.getString("IDTYPE");//01：手机号码 02：宽带号码
		IData result=new DataMap();
		result.put("ID_TYPE", input.getString("ID_TYPE"));
		result.put("SERAIL_NUMBER", serialNumber);
		result.put("BIZ_CODE", "0000");
		result.put("BIZ_DESC", "成功");
		result.put("RSP_CODE", "0000");
		result.put("RSP_DESC", "操作成功");
		result.put("OPR_NUMB", input.getString("OPR_NUMB",""));


		//用户身份凭证验证
		if("01".equals(idType)){
			try{
				IData param1 = new DataMap();
				param1.put("IDENT_CODE", input.getString("IDENT_CODE", ""));
				param1.put("SERIAL_NUMBER", serialNumber);
				UserIdentBean userIdentBean = BeanManager.createBean(UserIdentBean.class);
				userIdentBean.identAuth(param1);
			}catch(Exception e){
				result.put("RSP_CODE", "3018");
				result.put("RSP_DESC","凭证校验失败："+e.getMessage());
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "3018");
				result.put("VALID_FLAG", "1");//鉴权不通过
				result.put("REASON", "凭证校验失败："+e.getMessage());
				return result;
			}
		}


		try{
			String kdSerialNumber = serialNumber;
			if("01".equals(idType)){
				kdSerialNumber = "KD_"+serialNumber;
			}
			IData userBroadbandInfo = new DataMap();
			//查询正常用户
			IData userInfo = UcaInfoQry.qryUserInfoBySn(kdSerialNumber);
			userBroadbandInfo.put("ID_ITEM_RANGE",serialNumber);
			userBroadbandInfo.put("BOSS_WB_NO",kdSerialNumber);
			userBroadbandInfo.put("WB_STATUS","99");
			if (IDataUtil.isNotEmpty(userInfo)){

				String userStateCodest = userInfo.getString("USER_STATE_CODESET");
				if("0".equals(userStateCodest)){
					userBroadbandInfo.put("WB_STATUS","00");
				}else{
					userBroadbandInfo.put("WB_STATUS","02");
				}

				IDataset custInfoList = CustomerInfoQry.getCustomerByCustID(userInfo.getString("CUST_ID"));
				IData custInfo = custInfoList.first();
				String custName = custInfo.getString("CUST_NAME");
				String fuzzyCustName = com.asiainfo.veris.crm.order.pub.util.StringUtils.fuzzyNameForWidenet(custName);
				userBroadbandInfo.put("CUSTOMER_NAME",fuzzyCustName);

				//查询最高速率
				IDataset svcList = UserSvcInfoQry.queryUserAllSvc(userInfo.getString("USER_ID"));
				String rateServiceId = "";
				for(Object obj : svcList){
					IData data = (IData)obj;
					if(!"1".equals(data.getString("MAIN_TAG"))){
						rateServiceId = data.getString("SERVICE_ID");
					}
				}
				IData param = new DataMap();
				param.put("SUBSYS_CODE","CSM");
				param.put("PARAM_ATTR","4000");
				param.put("PARAM_CODE",rateServiceId);
				IDataset rateConfig = CommparaInfoQry.getCommparaInfoByPara(param);
				if(IDataUtil.isNotEmpty(rateConfig)){
					int rate = rateConfig.first().getInt("PARA_CODE1");
					rate = rate/1024;
					userBroadbandInfo.put("MAX_RATE",rate);
				}

				//@TODO 产品周期01：包三个月 02：包半年 03：包一年 04：
				//包两年 05：包三年 06：其他
				userBroadbandInfo.put("PLAN_CYCLE","06");
				//宽带周期判断
				IDataset userSaleActive= UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userInfo.getString("USER_ID"));
				for(int i=0;i<userSaleActive.size();i++){
					if(userSaleActive.getData(i).getString("PRODUCT_NAME").contains("宽带包年")){
						userBroadbandInfo.put("PLAN_CYCLE","03");
						break;
					}else if(userSaleActive.getData(i).getString("PACKAGE_NAME").contains("半年套餐")){
						userBroadbandInfo.put("PLAN_CYCLE","02");
						break;
					}else if(userSaleActive.getData(i).getString("PACKAGE_NAME").contains("季套餐")
							||userSaleActive.getData(i).getString("PACKAGE_NAME").contains("季度套餐")){
						userBroadbandInfo.put("PLAN_CYCLE","01");
						break;
					}
				}


				//宽带业务类型 01：单宽带 02：融合宽带
				userBroadbandInfo.put("WB_TYPE","01");
				String personSerialNumber = kdSerialNumber.replaceAll("KD_","");
				UcaData ucaData = UcaDataFactory.getNormalUca(personSerialNumber);
				String productId = ucaData.getProductId();
				param.clear();
				param.put("SUBSYS_CODE","CSM");
				param.put("PARAM_ATTR","6190");
				param.put("PARAM_CODE",productId);
				IDataset config = CommparaInfoQry.getCommparaInfoByPara(param);
				if(IDataUtil.isNotEmpty(config)){
					//配置的融合套餐减免的速率大于宽带的速率算是融合套餐
					IData configData = config.first();
					IData rateConfigData = rateConfig.first();
					//当用户本身的宽带速率大于融合套餐赠送的速率的时候不做处理
					if(rateConfigData.getInt("PARA_CODE1") <= configData.getInt("PARA_CODE1")){
						userBroadbandInfo.put("WB_TYPE","02");
					}
				}

				IData product = UserProductInfoQry.qryLasterMainProdInfoByUserId(userInfo.getString("USER_ID"));


				userBroadbandInfo.put("PLAN_ID",product.getString("PRODUCT_ID"));
				userBroadbandInfo.put("PLAN_NAME",product.getString("PRODUCT_NAME"));
				userBroadbandInfo.put("VALID_TIME",product.getString("START_DATE"));
				userBroadbandInfo.put("END_TIME",product.getString("END_DATE"));

				userBroadbandInfo.put("RENEW_TYPE","06");

				IDataset userWideInfos = WidenetInfoQry.getUserWidenetInfo(userInfo.getString("USER_ID"));
				String  widetype = userWideInfos.getData(0).getString("RSRV_STR2");
				String netType="04";
				IData param1 = new DataMap();
				param1.put("SUBSYS_CODE","CSM");
				param1.put("PARAM_ATTR","8011");
				param1.put("PARAM_CODE","WIDNET_TYPE");
				param1.put("PARA_CODE1",widetype);
				IDataset config1 = CommparaInfoQry.getCommparaInfoByPara(param1);
				if(IDataUtil.isNotEmpty(config1)){
					netType = config1.first().getString("PARA_CODE2");
				}
				userBroadbandInfo.put("NET_TYPE",netType);
				userBroadbandInfo.put("ADDRESS",userWideInfos.getData(0).getString("STAND_ADDRESS"));
				//宽带欠费金额
				IData feeData = AcctCall.qryOweCustInfoByUserId(userInfo.getString("USER_ID"));
				userBroadbandInfo.put("OWN_FEE",feeData.getString("OWE_FEE"));

			}else{
				result.put("RSP_CODE", "2039");
				result.put("RSP_DESC","此号码无宽带信息！");
				IDataset destoryUser = UserInfoQry.getAllDestroyUserInfoBySn(kdSerialNumber);
				if(IDataUtil.isNotEmpty(destoryUser)){
					userBroadbandInfo.put("WB_STATUS","04");
				}
			}
			IDataset dataset = new DatasetList();
			dataset.add(userBroadbandInfo);
			result.put("USER_BROADBAND_INFO",dataset);
			result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

		}catch (Exception e){
			logger.error("用户宽带信息查询接口异常",e);
			result.put("BIZ_CODE", "2998");
			result.put("BIZ_DESC", e.getMessage());
		}
		return result;
    }
}
