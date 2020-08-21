package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveQuerySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;

public class SaleActiveIntfCheckSVC extends CSBizService
{
	
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(SaleActiveIntfCheckSVC.class);

    public IDataset checkActiveByPrdAndPkg(IData input) throws Exception
    {	
    	try{
    		transferDataInput(input);

    		String serialNumber = input.getString("SERIAL_NUMBER");
    		String campnType = input.getString("CAMPN_TYPE");
    		String productId = input.getString("PRODUCT_ID");
    		String xGetMode = input.getString("X_GETMODE");
    		String xGetFeeMode = input.getString("X_GETFEE");
    		String deviceModelCode = input.getString("DEVICE_MODEL_CODE");
    		String terminalId = input.getString("TERMINAL_ID");

    		SaleActiveIntfCheckBean saleActiveIntfCheckBean = BeanManager.createBean(SaleActiveIntfCheckBean.class);
    		return saleActiveIntfCheckBean.checkActiveByPrdAndPkg(serialNumber, campnType, productId, xGetMode, xGetFeeMode, deviceModelCode, terminalId,input);
    	}
    	catch(Exception e){
				IDataset returnSet = new DatasetList();
				IData result = new DataMap();
				result.put("X_RSPCODE", "298");
				result.put("X_RESULTCODE", "298");
				result.put("X_RESULTINFO", e.getMessage());
				result.put("X_RSPDESC", e.getMessage());
				returnSet.add(result);
				return returnSet;
    	}
    }

    public void transferDataInput(IData input) throws Exception
    {
        if (StringUtils.isBlank(input.getString("SERIAL_NUMBER")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_3);
        }

        if (StringUtils.isBlank(input.getString("PRODUCT_ID")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_1);
        }

        SaleActiveBean saleActiveBean = new SaleActiveBean();
        String campnType = saleActiveBean.getCampnType(input.getString("PRODUCT_ID"));

        if (StringUtils.isBlank(campnType))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_7, input.getString("PRODUCT_ID"));
        }

        input.put("CAMPN_TYPE", campnType);
    }
    
    
//<entity route="routeBySn" name="SS.SaleActiveIntfCheckSVC.orderActiveBySnForIBoss" path="com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveIntfCheckSVC@orderActiveBySnForIBoss"/>
    public IData orderActiveBySnForIBoss(IData input) throws Exception
    {
    	IData result = new DataMap();
    	result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "办理成功");
		
		IData saleactiveData = new DataMap();
		IDataset commparaInfos9932 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9932",null,null);
		if(IDataUtil.isNotEmpty(commparaInfos9932)){
			input.put("PRODUCT_ID", commparaInfos9932.getData(0).getString("PARAM_CODE"));
		}else{
			result.put("X_RESULTCODE", "297");
			result.put("X_RESULTINFO","未查到配制活动");
			return result;
		}
		
		String productId=null,packageId=null;
    	try{
    	 IDataset saleActives = checkActiveByPrdAndPkgForSMS(input);
		 DataHelper.sort(saleActives, "PACKAGE_ID", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);

    	 for(int j = 0; j < saleActives.size(); j++){
    		 if("0".equals(saleActives.getData(j).getString("ERROR_FLAG"))){
    			 productId = saleActives.getData(j).getString("PRODUCT_ID");
    			 packageId = saleActives.getData(j).getString("PACKAGE_ID");
    			 break;
    		 }
    		 
    	 }
    	 if(StringUtils.isNotBlank(productId)&&StringUtils.isNotBlank(packageId)){
    		 saleactiveData.put("SERIAL_NUMBER",input.getString("SERIAL_NUMBER"));
             saleactiveData.put("PRODUCT_ID", productId);
             saleactiveData.put("PACKAGE_ID", packageId);
             saleactiveData.put("NO_TRADE_LIMIT", "TRUE");
             saleactiveData.put("SKIP_RULE", "TRUE");
             if("1".equals(input.getString("IS_COMM_ACTIVE",""))){
            	 saleactiveData.put("IS_COMM_ACTIVE", input.getString("IS_COMM_ACTIVE",""));
            	 saleactiveData.put("TRANS_IDO", input.getString("TRANS_IDO",""));
            	 saleactiveData.put("ACTIVITY_ID", input.getString("ACTIVITY_ID",""));
            	 saleactiveData.put("ACTIVITY_NAME", input.getString("ACTIVITY_NAME",""));
            	 saleactiveData.put("UNI_CHANNEL", input.getString("UNI_CHANNEL",""));
             }
    	     CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
    	 }else{
    		result.put("X_RESULTCODE", "299");
 			result.put("X_RESULTINFO", saleActives);
 			return result; 
    	 }
         
    	}catch(Exception e){
			result.put("X_RESULTCODE", "298");
			result.put("X_RESULTINFO", e.getMessage());
			return result;
	    }
    	
    	
    	return result;
    }
    
public IData qryActiveBySnForSMS(IData input) throws Exception
    {	
    	try{
    		//transferDataInput(input);
    		IData result = new DataMap();
    		result.put("X_RSPCODE", "0000");
    		String serialNumber = input.getString("SERIAL_NUMBER");
    		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    		if(IDataUtil.isNotEmpty(userInfo)){
    			String userId = userInfo.getString("USER_ID");
    			
    			IData params=new DataMap();
    			params.put("USER_ID", userId);
    			params.put("PROCESS_TAG", "0");
    			IDataset saleActives= Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", params);  
    			if(IDataUtil.isNotEmpty(saleActives)){
    				for(int i=0;i<saleActives.size();i++){
    					IData data=saleActives.getData(i);
    					//String productId = data.getString("PRODUCT_ID");
    					String packageId = data.getString("PACKAGE_ID");
    					IDataset commparaInfos9933 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9933","66000293",packageId);
    					if(IDataUtil.isNotEmpty(commparaInfos9933)){
    						result.put("ACCEPT_DATE", data.getString("ACCEPT_DATE"));
    						result.put("START_DATE", data.getString("START_DATE"));
    						result.put("END_DATE", data.getString("END_DATE"));
    						result.put("FLUX_VALUE", commparaInfos9933.getData(0).getString("PARA_CODE4"));
    					}
    				}
    				
    			}
    			
    			return result;
    		}else{
				IData data = new DataMap();
				data.put("X_RSPCODE", "222");
				data.put("X_RSPDESC", "查不到用户信息！");
				return data;
    		}
    		
    		
    		
    	}
    	catch(Exception e){
				IData result = new DataMap();
				result.put("X_RSPCODE", "298");
				result.put("X_RESULTCODE", "298");
				result.put("X_RESULTINFO", e.getMessage());
				result.put("X_RSPDESC", e.getMessage());
				return result;
    	}
    }
    
    public IDataset checkActiveByPrdAndPkgForSMS(IData input) throws Exception
    {	
    	try{
    		transferDataInput(input);
    		String inModeCode=input.getString("IN_MODE_CODE","");
    		if(inModeCode.equals("SD")){
    			input.put("IN_MODE_CODE", "0");
    			//将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
    	        getVisit().setInModeCode("0");
    		}
    		String serialNumber = input.getString("SERIAL_NUMBER");
    		String campnType = input.getString("CAMPN_TYPE");
    		String productId = input.getString("PRODUCT_ID");
    		String package_id = input.getString("PACKAGE_ID","");
    		String xGetMode = input.getString("X_GETMODE");
    		String xGetFeeMode = input.getString("X_GETFEE");
    		String deviceModelCode = input.getString("DEVICE_MODEL_CODE");
    		String terminalId = input.getString("TERMINAL_ID");
    		
    		String operType = input.getString("OPER_TYPE");
    		
    		SaleActiveIntfCheckBean saleActiveIntfCheckBean = BeanManager.createBean(SaleActiveIntfCheckBean.class);
    		
    		//宽带产品变更同时办理营销活动用
    		if (StringUtils.isNotEmpty(operType) && "601".equals(operType))
    		{
    		    return saleActiveIntfCheckBean.checkWidenetActiveByPrdAndPkgForSMS(serialNumber, campnType, productId, package_id, xGetMode, xGetFeeMode, deviceModelCode, terminalId, input);
    		}
    		else
    		{
    		    return saleActiveIntfCheckBean.checkActiveByPrdAndPkgForSMS(serialNumber, campnType, productId, package_id, xGetMode, xGetFeeMode, deviceModelCode, terminalId, input);
    		}
    		
    		
    	}
    	catch(Exception e){
				IDataset returnSet = new DatasetList();
				IData result = new DataMap();
				result.put("X_RSPCODE", "298");
				result.put("X_RESULTCODE", "298");
				result.put("X_RESULTINFO", e.getMessage());
				result.put("X_RSPDESC", e.getMessage());
				returnSet.add(result);
				return returnSet;
    	}
    }
    
    public IDataset checkActiveByPrdAndPkgForWEB(IData input) throws Exception
    {
    	try{
    		transferDataInput(input);
    		SaleActiveIntfCheckBean saleActiveIntfCheckBean = BeanManager.createBean(SaleActiveIntfCheckBean.class);
    		return saleActiveIntfCheckBean.checkActiveByPrdAndPkgForWEB(input);
    	}catch(Exception e){
    		IDataset returnSet = new DatasetList();
    		IData result = new DataMap();
    		result.put("X_RSPCODE", "298");
    		result.put("X_RESULTCODE", "298");
    		result.put("X_RESULTINFO", e.getMessage());
    		result.put("X_RSPDESC", e.getMessage());
    		returnSet.add(result);
    		return returnSet;
    	}
    }
    
    public IDataset checkSaleActiveRuleForSMS(IData input) throws Exception{
    	IDataset rtnSet=new DatasetList();
    	String serialNum=input.getString("SERIAL_NUMBER");
    	if (StringUtils.isBlank(serialNum))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_3);
        }
    	String prodId=input.getString("PRODUCT_ID");
        if (StringUtils.isBlank(prodId))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_1);
        }
//        IDataset campnTypes=ProductInfoQry.queryCampnByProdId("0898",prodId);
        IDataset campnTypes = UpcCall.qryCatalogByCatalogId(prodId);

        String campnType="";
        if(campnTypes!=null && campnTypes.size()>0){
//        	campnType=campnTypes.getData(0).getString("LABEL_ID","");
            campnType=campnTypes.getData(0).getString("UP_CATALOG_ID","");

        }
        
        
        String packId=input.getString("PACKAGE_ID");
        
        /**
         * 2017年老客户感恩大派送活动开发需求
         * 2017-09-09 活动增加一正五号的校验
         * */
        IData comm=new DataMap();
    	comm.put("PARAM_ATTR", "9957");
    	comm.put("PRODUCT_ID", prodId);
    	IDataset commpara2017=CSAppCall.call("SS.SaleActiveCheckSnSVC.check2017ActiveCommpara", comm);
    	for(int j=0; j < commpara2017.size(); j++){
			String commPackId = commpara2017.getData(j).getString("PARA_CODE1", "");//老用户的包
			NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
			IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNum,"");
			IData customerData = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
		    String psptTypeCode = customerData.getString("PSPT_TYPE_CODE");
		    String psptId=customerData.getString("PSPT_ID");
		    IData param =new DataMap();		
		    param.put("ID_CARD_TYPE", psptTypeCode);
		    param.put("IDCARD_TYPE", psptTypeCode);
		    /**
		     * 全国一证五号校验 NationalOpenLimitBean.java  里面有转换
		     */
//		    IDataset checkIdTypeResults = bean.checkPspt(psptTypeCode); //转换证件类型 
//		    if(IDataUtil.isEmpty(checkIdTypeResults)){
//		    	return;
//		    }
//		    param.put("ID_CARD_TYPE", checkIdTypeResults.first().getString("PARA_CODE1"));
//		    param.put("IDCARD_TYPE", checkIdTypeResults.first().getString("PARA_CODE1"));
			param.put("CUSTOMER_NAME", customerData.getString("CUST_NAME"));		
			param.put("ID_CARD_NUM", customerData.getString("PSPT_ID"));
			
			param.put("IDCARD_NUM", customerData.getString("PSPT_ID"));
			
			param.put("SEQ", input.getString("SEQ"));
			IDataset openNumInfos = new DatasetList();
			try{
				openNumInfos = bean.idCheck(param); //一证五号校验
			}catch (Exception e)
			{
				IData errData=new DataMap();
        		errData.put("X_RESULTCODE", "202990");
        		errData.put("X_RESULTINFO", "校验【全网一证多号】出现异常，请联系系统管理员！" ); 
        		errData.put("PRODUCT_ID", prodId);
        		errData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(prodId));
        		errData.put("PACKAGE_ID", packId);
				rtnSet.add(errData);
			}
			if(IDataUtil.isNotEmpty(openNumInfos)){
				if (!("0".equals(openNumInfos.getData(0).getString("X_RSPTYPE", "")))|| !"0000".equals(openNumInfos.getData(0).getString("X_RSPCODE"))) {	
		    		String xRspDesc = "全国一证五号校验失败:";
		    		String errInfo=openNumInfos.getData(0).getString("X_RESULTINFO", "");
		    		if("".equals(errInfo)){
		    			errInfo=openNumInfos.getData(0).getString("X_RSPDESC", "");
		    		}
		    		xRspDesc = xRspDesc + errInfo;
		    		//CSAppException.apperr(CrmCommException.CRM_COMM_103,xRspDesc);
		    		String X_RSPCODE=openNumInfos.getData(0).getString("X_RSPCODE", "");
		    		if("".equals(X_RSPCODE)){
		    			X_RSPCODE="202993";
		    		}
		    		IData errData=new DataMap();
            		errData.put("X_RESULTCODE", X_RSPCODE);
            		errData.put("X_RESULTINFO", xRspDesc); 
            		errData.put("PRODUCT_ID", prodId);
            		errData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(prodId));
            		errData.put("PACKAGE_ID", packId);
    				rtnSet.add(errData);
				}else{
					// 根据证件类型查找全网开户限制数
					IDataset openLimitResult = CommparaInfoQry.getCommparaAllCol("CSM", "2552", psptTypeCode, "ZZZZ");
					if ("0".equals(openNumInfos.getData(0).getString("X_RESULTCODE")))
					{
						int openNum = openNumInfos.getData(0).getInt("TOTAL", 0);
						int untrustresult = openNumInfos.getData(0).getInt("UN_TRUST_RESULT", 0);

						if (openNum >= 0)
						{
							if (untrustresult > 0)
							{
								IData errData=new DataMap();
								errData.put("X_RESULTCODE", "23043");
								errData.put("X_RESULTINFO", "开户人有不良信息，不满足开户条件，禁止开户");
								errData.put("PRODUCT_ID", prodId);
								errData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(prodId));
								errData.put("PACKAGE_ID", packId);
								rtnSet.add(errData);
							}

							if (IDataUtil.isNotEmpty(openLimitResult))
							{
								int openLimitNum = openLimitResult.getData(0).getInt("PARA_CODE1", 0);
								
								if (openNum >= openLimitNum)
								{ 
									IData errData=new DataMap();
				            		errData.put("X_RESULTCODE", "202991");
				            		errData.put("X_RESULTINFO", "【全网一证多号】校验: 证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + openLimitNum + "个】，请更换其它证件！"); 
				            		errData.put("PRODUCT_ID", prodId);
				            		errData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(prodId));
				            		errData.put("PACKAGE_ID", packId);
				    				rtnSet.add(errData);
								} else
								{
									// 查询携转业务41工单的数量，判断一证五号加入当前已申请携入成功的工单判断，如用户证件A已经成功申请了2笔携入，证件A调用集团一证五号接口返回开户数为3，
									// 当前该证件开户数在我省系统判断即为5；
									IDataset ds = TradeHistoryInfoQry.getInfosByTradeTypeCode("40", psptTypeCode, psptId);// 携转开户
									if (DataSetUtils.isNotBlank(ds))
									{
										int count = ds.getData(0).getInt("COUNT", 0);
										if ((count + openNum) >= openLimitNum)
										{
											IData errData=new DataMap();
						            		errData.put("X_RESULTCODE", "202992");
						            		errData.put("X_RESULTINFO", "【全网一证多号】校验: 证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + openLimitNum + "个】，请更换其它证件！"); 
						            		errData.put("PRODUCT_ID", prodId);
						            		errData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(prodId));
						            		errData.put("PACKAGE_ID", packId);
						    				rtnSet.add(errData);
										}
									}
								}
							}
						}
					} 
				}
			}
		}
        
        if (StringUtils.isBlank(packId)){
        	IData getPackInpu=new DataMap();
            getPackInpu.put("EPARCHY_CODE", "0898");
            getPackInpu.put("PRODUCT_ID", prodId); 
            IDataset packList = CSAppCall.call("SS.BatActiveCancelSVC.queryPackageByProdID", getPackInpu);
            for(int i=0; i < packList.size(); i++){
            	String packageId=packList.getData(i).getString("PACKAGE_ID","");
            	IData callParam=new DataMap();
            	callParam.put("SERIAL_NUMBER", serialNum);
            	callParam.put("EPARCHY_CODE", "0898");
            	callParam.put("PRODUCT_ID", prodId); 
            	callParam.put("PACKAGE_ID", packageId); 
            	IDataset results=CSAppCall.call("SS.SaleActiveSVC.checkPrdAndPkgForSMS", callParam);
            	for(int j=0;j<results.size();j++){ 
                	IData otherRightData=results.getData(j); 
                	String result=otherRightData.getString("RESULTS",""); 
                	if("".equals(result)){
                		String xResultCode=otherRightData.getString("X_RESULTCODE","");
                		String xResultInfo=otherRightData.getString("X_RESULTINFO","");
                		IData errData=new DataMap();
                		errData.put("X_RESULTCODE", xResultCode);
                		errData.put("X_RESULTINFO", xResultInfo); 
                		errData.put("PRODUCT_ID", prodId);
                		errData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(prodId));
                		errData.put("PACKAGE_ID", packageId);
                		errData.put("PACKAGE_NAME", UPackageInfoQry.getPackageNameByPackageId(packageId));
        				rtnSet.add(errData);
                	}else{
                	
        	        	StringTokenizer st=new StringTokenizer(result,"##");
        	        	
        				while(st.hasMoreElements()){
        					IData rtnData=new DataMap();
        					String rtnCode="";
        		        	String rtnInfo="";
        					String code_info=st.nextToken();
        					rtnCode=code_info.substring(0,code_info.indexOf("$$"));
        					rtnInfo=code_info.substring(code_info.indexOf("$$")+2); 
        					rtnData.put("X_RESULTCODE", rtnCode);
        					rtnData.put("X_RESULTINFO", rtnInfo);
        					rtnData.put("PRODUCT_ID", prodId);
        					rtnData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(prodId));
        					rtnData.put("PACKAGE_ID", packageId);
        					rtnData.put("PACKAGE_NAME", UPackageInfoQry.getPackageNameByPackageId(packageId));
        					rtnSet.add(rtnData);
        				}
                	}
            	}
            }
        }else{
        	IData callParam=new DataMap();
        	callParam.put("SERIAL_NUMBER", serialNum);
        	callParam.put("EPARCHY_CODE", "0898");
        	callParam.put("PRODUCT_ID", prodId); 
        	callParam.put("PACKAGE_ID", packId); 
        	IDataset results=CSAppCall.call("SS.SaleActiveSVC.checkPrdAndPkgForSMS", callParam);
        	for(int k=0;k<results.size();k++){ 
            	IData otherRightData=results.getData(k); 
            	String result=otherRightData.getString("RESULTS",""); 
            	if("".equals(result)){
            		String xResultCode=otherRightData.getString("X_RESULTCODE","");
            		String xResultInfo=otherRightData.getString("X_RESULTINFO","");
            		IData errData=new DataMap();
            		errData.put("ERR_NUM", xResultCode);
            		errData.put("ERR_INFO", xResultInfo); 
    				rtnSet.add(errData);
            	}else{
            	
    	        	StringTokenizer st=new StringTokenizer(result,"##");
    	        	
    				while(st.hasMoreElements()){
    					IData rtnData=new DataMap();
    					String rtnCode="";
    		        	String rtnInfo="";
    					String code_info=st.nextToken();
    					rtnCode=code_info.substring(0,code_info.indexOf("$$"));
    					rtnInfo=code_info.substring(code_info.indexOf("$$")+2); 
    					rtnData.put("X_RESULTCODE", rtnCode);
    					rtnData.put("X_RESULTINFO", rtnInfo);
    					rtnData.put("PRODUCT_ID", prodId);
    					rtnData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(prodId));
    					rtnData.put("PACKAGE_ID", packId);
    					rtnData.put("PACKAGE_NAME", UPackageInfoQry.getPackageNameByPackageId(packId));
    					rtnSet.add(rtnData);
    				}
            	}
        	}
        } 
        return rtnSet;
    } 
    

    /**
     * SS.SaleActiveSVC.checkUserPsptIdSame
     * */
    public IData checkUserPsptIdSame(IData params) throws Exception
    {	
    	IData rtnData=new DataMap();
    	String rtnCode="";
    	String rtnInfo="";
    	String serialNum_a=params.getString("SERIAL_NUMBER_A","");
    	String serialNum_b=params.getString("SERIAL_NUMBER_B","");
    	String psptId_b=params.getString("PSPT_ID_B","");
    	if("".equals(serialNum_a)){
    		rtnData.put("X_RESULTCODE", "202995");
    		rtnData.put("X_RESULTINFO", "SERIAL_NUMBER_A不能为空。");
    		return rtnData;
    	}
    	if("".equals(serialNum_b)){
    		rtnData.put("X_RESULTCODE", "202996");
    		rtnData.put("X_RESULTINFO", "SERIAL_NUMBER_B不能为空。");
    		return rtnData;
    	}
    	
    	if("".equals(psptId_b)){
    		rtnData.put("X_RESULTCODE", "202998");
    		rtnData.put("X_RESULTINFO", "PSPT_ID_B不能为空。");
    		return rtnData;
    	}
    	
    	/*
    	 * 2、与新号码的身份证是否相同      PSPT_ID_SAME Y:相同    N：不相同
    	 * */
    	String userId="";
    	IData userA=UcaInfoQry.qryUserInfoBySn(serialNum_a);
    	if(userA==null || userA.size()==0){
    		rtnData.put("X_RESULTCODE", "202997");
    		rtnData.put("X_RESULTINFO", "手机号【"+serialNum_a+"】不存在有效的用户信息。");
    		return rtnData;
	    }else{
	    	userId=userA.getString("USER_ID");
	    }
//    	IData userB=UcaInfoQry.qryUserInfoBySn(serialNum_b);
//    	if(userB==null || userB.size()==0){
//    		rtnData.put("X_RESULTCODE", "202998");
//    		rtnData.put("X_RESULTINFO", "手机号【"+serialNum_b+"】不存在有效的用户信息。");
//    		return rtnData;
//	    }else{
//	    	checkUserId=userB.getString("USER_ID");
//	    }
    	
    	String psptid=UcaDataFactory.getUcaByUserId(userId).getCustomer().getPsptId();//老用户
    	//String checkpsptid=UcaDataFactory.getUcaByUserId(checkUserId).getCustomer().getPsptId();//新用户（弹框输入号码）
    	if(psptid!=null && psptId_b !=null && psptid.equals(psptId_b)){
    		rtnCode="true"; 
    		rtnInfo="身份证一致";
    	} else{
    		rtnCode="false";
    		rtnInfo="身份证不一致";
    	}
    	rtnData.put("X_RESULTCODE", rtnCode);
		rtnData.put("X_RESULTINFO", rtnInfo);
		return rtnData;
    }
    
    /** 
     * SS.SaleActiveIntfCheckSVC.checkActiveByImei 
     * CS.SaleActiveQuerySVC.querySaleActivesByImei
     * */
    public IDataset checkActiveByImei(IData input) throws Exception
    {
    	IData rtnData=new DataMap();
    	IDataset rtnset=new DatasetList();
    	transferDataInput(input);
    	String serialNumber=input.getString("SERIAL_NUMBER");
    	String resno=input.getString("TERMINAL_ID","");
    	input.put("NEW_IMEI",resno);
    	input.put("EPARCHY_CODE","0898");
    	SaleActiveQuerySVC activeSVC= new SaleActiveQuerySVC();
    	IDataset packset=activeSVC.querySaleActivesByImei(input);
    	String resultCode="";
    	if(packset!=null && packset.size()>0){
	    	for(int k=0;k<packset.size();k++){
	    		IData packData=packset.getData(k);
	    		String prodId=packData.getString("PRODUCT_ID","");
	    		String packId=packData.getString("PACKAGE_ID","");
	    		if(!"".equals(packId)){
	    			
	    			resultCode= packData.getString("ERROR_FLAG","");
	    			String resultInfo="查询成功";
	    			if(resultCode!=null && "1".equals(resultCode)){
	    				resultInfo=packData.getString("ERROR_MSG","");
	    			}else{
	    				resultCode="0";
	    			}
	    			IData rtnData1=new DataMap();
	    			rtnData1.put("PRODUCT_ID", prodId);
	    			rtnData1.put("PACKAGE_ID", packId);
	    			rtnData1.put("PACKAGE_NAME", packData.getString("PRODUCT_NAME",""));
	    			rtnData1.put("PACKAGE_DESC", packData.getString("PRODUCT_NAME",""));
	    			rtnData1.put("X_RESULTCODE", resultCode);
	    			rtnData1.put("X_RESULTINFO", resultInfo);
	    			rtnset.add(rtnData1);
	    		}
	    	} 
    	}else{
    		rtnData.put("X_RESULTCODE", "-1");
			rtnData.put("X_RESULTINFO", "无数据获取。");
			rtnset.add(rtnData);
    	}
    	return rtnset;
    }
    
    public IDataset tradeReg4Intf(IData input) throws Exception
    {
    	IDataset actives = new DatasetList();
    	IData checkflag=checkInputForActive(input);
    	String flag=checkflag.getString("X_RESULTCODE","");
    	if(!"0".equals(flag)){
    		actives.add(checkflag);
    	}else{ 
	    	IData saleActiveInfo=new DataMap();
	    	saleActiveInfo.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER",""));
	    	saleActiveInfo.put("PRODUCT_ID", input.getString("PRODUCT_ID",""));
	    	saleActiveInfo.put("PACKAGE_ID", input.getString("PACKAGE_ID",""));
	    	saleActiveInfo.put("TRADE_STAFF_ID", input.getString("TRADE_STAFF_ID",""));
	    	saleActiveInfo.put("IN_MODE_CODE", input.getString("IN_MODE_CODE",""));
	    	saleActiveInfo.put("ACTION_TYPE", input.getString("ACTION_TYPE",""));
	    	saleActiveInfo.put("TERMINAL_ID", input.getString("TERMINAL_ID","")); 
	    	try{
	    		actives = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf",saleActiveInfo);
		    	if(actives!=null && actives.size()>0){
		    		String ORDER_ID=actives.getData(0).getString("ORDER_ID","");
		    		if(ORDER_ID!=null && !"".equals(ORDER_ID)){
		    			actives.getData(0).put("X_RESULTCODE", "0");
		    			actives.getData(0).put("X_RESULTINFO", "营销活动办理成功。");
		    		}
		    	}
	    	}catch(Exception e){
	    		IData errInfo=new DataMap(); 
	    		String error =  Utility.parseExceptionMessage(e);
	    		String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
				if(errorArray.length >= 2)
				{
					String strException = errorArray[0];
					String strExceptionMessage = errorArray[1];
					errInfo.put("X_RESULTCODE", strException);
					errInfo.put("X_RESULTINFO", strExceptionMessage);
				}
				else
				{
					errInfo.put("X_RESULTCODE", "-1");
					errInfo.put("X_RESULTINFO", "营销活动办理失败:"+error);
				}
				actives.add(errInfo);
	    		return actives;
	    	}
    	}
    	return actives;
    }
    
    private IData checkInputForActive(IData input) throws Exception{
    	String errs="";
    	String  SERIAL_NUMBER=input.getString("SERIAL_NUMBER","");   
    	if(SERIAL_NUMBER==null || "".equals(SERIAL_NUMBER)){
    		errs="SERIAL_NUMBER不能为空。";
    	}
    	String  PRODUCT_ID=input.getString("PRODUCT_ID","");    
    	if(PRODUCT_ID==null || "".equals(PRODUCT_ID)){
    		errs=errs+"||PRODUCT_ID不能为空。";
    	}
    	String  PACKAGE_ID=input.getString("PACKAGE_ID","");    
    	if(PACKAGE_ID==null || "".equals(PACKAGE_ID)){
    		errs=errs+"||PACKAGE_ID不能为空。";
    	}
    	String  TERMINAL_ID=input.getString("TERMINAL_ID",""); 
    	if(TERMINAL_ID==null || "".equals(TERMINAL_ID)){
    		errs=errs+"||TERMINAL_ID不能为空。";
    	}
    	String  TRADE_STAFF_ID=input.getString("TRADE_STAFF_ID","");    
    	String  IN_MODE_CODE=input.getString("IN_MODE_CODE","");      
    	String  ACTION_TYPE=input.getString("ACTION_TYPE","");       
    	if(ACTION_TYPE==null || "".equals(ACTION_TYPE)){
    		errs=errs+"||ACTION_TYPE不能为空。";
    	}
    	
    	IData errInfo=new DataMap();
    	if(!"".equals(errs)){
    		errInfo.put("X_RESULTCODE", "-9");
    		errInfo.put("X_RESULTINFO", errInfo);
    	}else{
    		errInfo.put("X_RESULTCODE", "0");
    	}
		return errInfo;
    } 
}
