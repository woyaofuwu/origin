package com.asiainfo.veris.crm.order.soa.person.busi.changeepostinfo;


import java.util.HashMap;
import java.util.Map;

import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;


public class ModifyEPostInfoSVC extends CSBizService{
	
    private static transient final Logger logger = Logger.getLogger(ModifyEPostInfoSVC.class);

	
	/**
	 * 判断是否为新开户
	 */
	public IDataset isNewUser(IData input) throws Exception 
    {
    	return CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "2016","IS_NEW_USER", input.getString("TRADE_TYPE_CODE",""), "ZZZZ");
    }
	
	/**
	 * 根据USERID查找三户资料，客户资料
	 */
	public IDataset qryCustInfos(IData input) throws Exception
	{
		IData userInfo = UcaInfoQry.qryUserInfoByUserId(input.getString("USER_ID"), input.getString("ROUTE_EPARCHY_CODE"));
		if (IDataUtil.isNotEmpty(userInfo)) {
			IDataset custInfos = CustPersonInfoQry.getPerInfoByCustId(userInfo.getString("CUST_ID"));
			if (IDataUtil.isNotEmpty(custInfos)) {
				custInfos.getData(0).put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
				custInfos.getData(0).put("REMOVE_TAG", userInfo.getString("REMOVE_TAG"));
				return custInfos;
			}
			return null;
		}
		return null;
	}
	
	
	 /**
     * 作用：根据手机号码查找电子发票推送信息资料
     * 
     * @param param
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset qryEPostInfo(IData input) throws Exception
    {
    	IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        return ModifyEPostInfoBean.qryEPostInfo(param);
    }
    
    /**
     * 作用：根据user_id查找电子发票推送信息资料
     * 
     * @param param
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset qryEPostInfoByUserId(IData input) throws Exception
    {
    	IData param = new DataMap();
        param.put("USER_ID", input.getString("USER_ID"));
        return ModifyEPostInfoBean.qryEPostInfoByUserId(param);
    }
    
    /**
     * 作用：根据user_id查找客户台账资料
     * 
     * @param param
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset qryTradeInfos(IData input) throws Exception
    {
    	String userId = input.getString("USER_ID");
    	IDataset tradeInfos = TradeInfoQry.getMainTradeByUserId(userId);
    	IDataset personInfos = new DatasetList();
    	if(IDataUtil.isNotEmpty(tradeInfos)){
    		for(int i=0;i<tradeInfos.size();i++){
    			personInfos = TradeCustPersonInfoQry.getTradeCustPersonByTradeId(tradeInfos.getData(i).getString("TRADE_ID"));
        		if(IDataUtil.isNotEmpty(personInfos)){
        			personInfos.getData(0).put("SERIAL_NUMBER", tradeInfos.getData(0).getString("SERIAL_NUMBER"));
        			return personInfos;
        		}
    		}     	
    	}
    	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户账户台账资料不存在"); 
        return personInfos;
    }
    
    /**
     * 月结发票设置接口---供客服邮件系统调用
     * SERIAL_NUMBER  手机号码
     * CUST_TAG 客户标识
     * CUST_TYPE 客户类型
     * POST_ADR 邮箱地址
     * CUST_ADR 客户归属地
     * REQUEST_CHANNEL 请求发起渠道
     * REQUEST_TIME 请求发起时间
     * POST_CYCLE 推送周期
     * POST_DATE 推送日期
     * INTER_TYPE 接口类型
     * PROV_CODE 省份标识
     * EMAIL_TYPE 邮件类型  改一下 BUSI_TYPE业务类型
     * REQUEST_ID 业务流水号
     * 
     * 客服邮件系统系统将客户（仅1类客户）在139邮箱渠道设置的常用邮箱、定期推送及取消等信息，同步到CRM。
     */
     public IData modifyEPostByMail(IData data) throws Exception
     {
    	/*
    	 * 接口类型5~8
    	 */
    	IData result = new DataMap();
    	 
    	 
    	String serialNumber = data.getString("SERIAL_NUMBER");  //用户手机号码
    	//判断是否为1类客户（个人手机客户）
    	IDataset userInfo = UserInfoQry.getUserInfoBySn(serialNumber, "0");
    	if(IDataUtil.isEmpty(userInfo)){
		    result.put("RETURN_CODE", "9999");
		    result.put("RETURN_MESSAGE", "用户资料不存在");
    		return result;
		}
    	String routeId = userInfo.getData(0).getString("EPARCHY_CODE");
    	String userId = userInfo.getData(0).getString("USER_ID");
    	String userType = userInfo.getData(0).getString("NET_TYPE_CODE");
    	if(!"00".equals(userType)){
    		result.put("RETURN_CODE", "9999");
		    result.put("RETURN_MESSAGE", "139邮箱渠道仅支持个人手机客户设置");
    		return result;
    	}
    	//判断推送周期是否为1（月结发票都是一个月推送一次）
//    	if(!"1".equals(data.getString("POST_CYCLE"))){
//    		result.put("RETURN_CODE", "9999");
//		    result.put("RETURN_MESSAGE", "电子发票推送周期必须为一个月");
//    		return result;
//    	}
    	
    	String acctId = "";
    	IDataset acctset = AcctInfoQry.getAcctInfoByCustId(userInfo.getData(0).getString("CUST_ID"),routeId);
		if(IDataUtil.isNotEmpty(acctset)){
		   acctId = acctset.getData(0).getString("ACCT_ID"); 
		}else{
		   result.put("RETURN_CODE", "9999");
		   result.put("RETURN_MESSAGE", "账户资料不存在");
    	   return result;
		}
    	
    	//入表通用参数
    	IData tabData = new DataMap();
		//tabData.put("PARTITION_ID", userId.length()>4?userId.substring(userId.length()-4,userId.length()):"");
    	tabData.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
		tabData.put("SERIAL_NUMBER", serialNumber);
		tabData.put("EPARCHY_CODE", routeId);
		tabData.put("USER_ID", userId);
		tabData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		tabData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
		tabData.put("UPDATE_TIME", SysDateMgr.getSysDate());
		
		
		//账管接口通用参数
	    IData accparam = new DataMap();
	    accparam.put("ACCT_TYPE", "0");  //个人用户设置
	    accparam.put("EPARCHY_CODE", routeId);   //手机号码地州编码
	    accparam.put("USER_ID", userId);
	    accparam.put("ACCT_ID", acctId);
	    //accparam.put("PARTITION_ID", acctId.length()>4?acctId.substring(acctId.length()-4,acctId.length()):"");
	    accparam.put("PARTITION_ID", StrUtil.getPartition4ById(acctId));
	    accparam.put("SERIAL_NUMBER", serialNumber);
    	
    	//获取接口参数
    	String interType = data.getString("INTER_TYPE");  
    	
    	
    	
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", serialNumber);
    	
        IDataset postInfos = ModifyEPostInfoBean.qryEPostInfo(param);
        IDataset unInfos = ModifyEPostInfoBean.qryEPostInfoByMon(param);  //查询不可用数据
        //如果用户有现金发票，则不能设置
//        for(int i=0;i<postInfos.size();i++){
//        	if("1".equals(postInfos.getData(i).getString("POST_TAG"))){
//        	   result.put("RETURN_CODE", "9999");
//      		   result.put("RETURN_MESSAGE", "该用户已设置现金发票，不能再设置月结发票");
//          	   return result;
//        	}
//        }
    	    
    	if("5".equals(interType)){   //配置常用邮箱
    		String postAdr = data.getString("POST_ADR");
    		
    		if(IDataUtil.isNotEmpty(unInfos)){  //如果有不可用数据
    			ModifyEPostInfoBean.upUnMonPost(tabData, postAdr);
    		}else{
    			ModifyEPostInfoBean.addUnMonPost(tabData, postAdr);
    		}
    		

    		if(IDataUtil.isNotEmpty(postInfos)){  //存在数据
    			for(int i=0;i<postInfos.size();i++){
    				//如果是营业或现金
    				if("2".equals(postInfos.getData(i).getString("POST_TAG"))||"1".equals(postInfos.getData(i).getString("POST_TAG"))){  
    					if(StringUtils.isNotBlank(postInfos.getData(i).getString("POST_ADR"))){   //如果有邮箱
    						accparam.put("TYPE", postInfos.getData(i).getString("POST_TAG"));  
    			    	    accparam.put("PUSH_CHANNEL", postInfos.getData(i).getString("POST_CHANNEL"));
    			    	    accparam.put("SMS_NUMBER", postInfos.getData(i).getString("RECEIVE_NUMBER"));
    			    	    accparam.put("EMAIL_NUMBER", postAdr);
    			    	    accparam.put("FLAG", "1");
    			    	    IDataset results = AcctCall.setEPostInfo(accparam);
    						if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    			    	    	ModifyEPostInfoBean.upBusiCashPost(tabData, postInfos.getData(i).getString("POST_TAG"), 
        						postInfos.getData(i).getString("POST_CHANNEL"), 
        						postInfos.getData(i).getString("RECEIVE_NUMBER"), postAdr);
    					    }else{
    					    	result.put("RETURN_CODE", "9999");
    						    result.put("RETURN_MESSAGE", results.getData(0).getString("RESULT_INFO"));
    				    		return result;
    						}
    			    	    clearParam(accparam);
    					}
    				}else if("0".equals(postInfos.getData(i).getString("POST_TAG"))&&"1".equals(postInfos.getData(i).getString("RSRV_STR1"))){  //如果是月结（可用月结）
    					if(StringUtils.isNotBlank(postInfos.getData(i).getString("POST_ADR"))){  //如果有邮箱
    						accparam.put("TYPE", postInfos.getData(i).getString("POST_TAG"));  
    			    	    accparam.put("PUSH_CHANNEL", postInfos.getData(i).getString("POST_CHANNEL"));
    			    	    accparam.put("SMS_NUMBER", postInfos.getData(i).getString("RECEIVE_NUMBER"));
    			    	    accparam.put("EMAIL_NUMBER", postAdr);
    			    	    accparam.put("PUSH_DATE", postInfos.getData(i).getString("POST_DATE"));
    			    	    accparam.put("FLAG", "1");
    			    	    IDataset results = AcctCall.setEPostInfo(accparam);
    						if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    			    	        ModifyEPostInfoBean.upMonPost(tabData,postInfos.getData(i).getString("POST_CHANNEL"), 
        						postInfos.getData(i).getString("RECEIVE_NUMBER"), postAdr, postInfos.getData(i).getString("POST_DATE"));
    					    }else{
    					    	result.put("RETURN_CODE", "9999");
    						    result.put("RETURN_MESSAGE", results.getData(0).getString("RESULT_INFO"));
    				    		return result;
    						}
    			    	    clearParam(accparam);
    					}
    				}
    			}  			
    		}
    		
    		
    	}else if("6".equals(interType)){  //取消常用邮箱
    		//如果有不可用数据，则将其还原到139邮箱
    		if(IDataUtil.isNotEmpty(unInfos)){  //如果有不可用数据
    			ModifyEPostInfoBean.delUnMonPost(serialNumber);
    		}
    		
    		
    		if(IDataUtil.isNotEmpty(postInfos)){  //存在数据
    			for(int i=0;i<postInfos.size();i++){
    				//如果是营业或现金
    				if("2".equals(postInfos.getData(i).getString("POST_TAG"))||"1".equals(postInfos.getData(i).getString("POST_TAG"))){  
    					if(StringUtils.isNotBlank(postInfos.getData(i).getString("POST_ADR"))){   //如果有邮箱
    						accparam.put("TYPE", postInfos.getData(i).getString("POST_TAG"));  
    			    	    accparam.put("PUSH_CHANNEL", postInfos.getData(i).getString("POST_CHANNEL"));
    			    	    accparam.put("SMS_NUMBER", postInfos.getData(i).getString("RECEIVE_NUMBER"));
    			    	    accparam.put("EMAIL_NUMBER", serialNumber+"@139.com");
    			    	    accparam.put("FLAG", "1");
    			    	    IDataset results = AcctCall.setEPostInfo(accparam);
    						if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    			    	       ModifyEPostInfoBean.upBusiCashPost(tabData, postInfos.getData(i).getString("POST_TAG"), 
    						   postInfos.getData(i).getString("POST_CHANNEL"), 
    						   postInfos.getData(i).getString("RECEIVE_NUMBER"), serialNumber+"@139.com");
    					    }else{
    					    	result.put("RETURN_CODE", "9999");
    						    result.put("RETURN_MESSAGE", results.getData(0).getString("RESULT_INFO"));
    				    		return result;
    						}
    			    	    clearParam(accparam);
    					}else{  //如果只有手机
    						result.put("RETURN_CODE", "9999");
			      		    result.put("RETURN_MESSAGE", "该用户未设置常用邮箱，不能取消");
			          	    return result;
    					}
    				}else if("0".equals(postInfos.getData(i).getString("POST_TAG"))&&"1".equals(postInfos.getData(i).getString("RSRV_STR1"))){  //如果是月结
    					if(StringUtils.isNotBlank(postInfos.getData(i).getString("POST_ADR"))){  //如果有邮箱
    						accparam.put("TYPE", postInfos.getData(i).getString("POST_TAG"));  
    			    	    accparam.put("PUSH_CHANNEL", postInfos.getData(i).getString("POST_CHANNEL"));
    			    	    accparam.put("SMS_NUMBER", postInfos.getData(i).getString("RECEIVE_NUMBER"));
    			    	    accparam.put("EMAIL_NUMBER", serialNumber+"@139.com");
    			    	    accparam.put("PUSH_DATE", postInfos.getData(i).getString("POST_DATE"));
    			    	    accparam.put("FLAG", "1");
    			    	    IDataset results = AcctCall.setEPostInfo(accparam);
    						if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    			    	       ModifyEPostInfoBean.upMonPost(tabData,postInfos.getData(i).getString("POST_CHANNEL"), 
    	        			   postInfos.getData(i).getString("RECEIVE_NUMBER"), serialNumber+"@139.com", postInfos.getData(i).getString("POST_DATE"));
    					    }else{
    					    	result.put("RETURN_CODE", "9999");
    						    result.put("RETURN_MESSAGE", results.getData(0).getString("RESULT_INFO"));
    				    		return result;
    						}
    			    	    clearParam(accparam);
    					}else{  //如果只有手机
    						result.put("RETURN_CODE", "9999");
			      		    result.put("RETURN_MESSAGE", "该用户未设置常用邮箱，不能取消");
			          	    return result;
    					}
    				}
    			}  			
    		}else{  //不存在，增加一条不可用数据
    			result.put("RETURN_CODE", "9999");
      		    result.put("RETURN_MESSAGE", "该用户未设置常用邮箱，不能取消");
          	    return result;
    		}
    		

    		
    	}else if("7".equals(interType)){   //配置定期推送
    		
    		String postDate = data.getString("POST_DATE");
    		String postAdr = "";
    		if(IDataUtil.isNotEmpty(unInfos)){  //如果有不可用数据，则取其邮箱地址
    		   postAdr = unInfos.getData(0).getString("POST_ADR");
    		}
    		
    		
    		if(IDataUtil.isNotEmpty(postInfos)){  //存在数据
    			boolean hasMon = false;
    			for(int i=0;i<postInfos.size();i++){
    				//如果是营业或现金
    				if("2".equals(postInfos.getData(i).getString("POST_TAG"))||"1".equals(postInfos.getData(i).getString("POST_TAG"))){  
    					if(StringUtils.isNotBlank(postInfos.getData(i).getString("POST_ADR"))){   //如果有邮箱
    						accparam.put("TYPE", postInfos.getData(i).getString("POST_TAG"));  
    			    	    accparam.put("PUSH_CHANNEL", postInfos.getData(i).getString("POST_CHANNEL"));
    			    	    accparam.put("SMS_NUMBER", postInfos.getData(i).getString("RECEIVE_NUMBER"));
    			    	    accparam.put("EMAIL_NUMBER", postAdr==""?postInfos.getData(i).getString("POST_ADR"):postAdr);
    			    	    accparam.put("FLAG", "1");
    			    	    IDataset results = AcctCall.setEPostInfo(accparam);
    						if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    			    	        ModifyEPostInfoBean.upBusiCashPost(tabData, postInfos.getData(i).getString("POST_TAG"), 
    	        			    postInfos.getData(i).getString("POST_CHANNEL"), 
    	        				postInfos.getData(i).getString("RECEIVE_NUMBER"), postAdr==""?postInfos.getData(i).getString("POST_ADR"):postAdr);
    					    }else{
    					    	result.put("RETURN_CODE", "9999");
    						    result.put("RETURN_MESSAGE", results.getData(0).getString("RESULT_INFO"));
    				    		return result;
    						}
    			    	    clearParam(accparam);
    					}else{  //如果只有手机
    						accparam.put("TYPE", postInfos.getData(i).getString("POST_TAG"));  
    			    	    accparam.put("PUSH_CHANNEL", "2");
    			    	    accparam.put("SMS_NUMBER", postInfos.getData(i).getString("RECEIVE_NUMBER"));
    			    	    accparam.put("EMAIL_NUMBER", postAdr==""?serialNumber+"@139.com":postAdr);
    			    	    accparam.put("FLAG", "1");
    			    	    IDataset results = AcctCall.setEPostInfo(accparam);
    						if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    			    	        ModifyEPostInfoBean.upBusiCashPost(tabData, postInfos.getData(i).getString("POST_TAG"), 
    	        				"2",postInfos.getData(i).getString("RECEIVE_NUMBER"), postAdr==""?serialNumber+"@139.com":postAdr);
    			    	    }else{
    			    	    	result.put("RETURN_CODE", "9999");
    						    result.put("RETURN_MESSAGE", results.getData(0).getString("RESULT_INFO"));
    				    		return result;
    						}
    			    	    clearParam(accparam);
    					}
    				}else if("0".equals(postInfos.getData(i).getString("POST_TAG"))&&"1".equals(postInfos.getData(i).getString("RSRV_STR1"))){  //如果是有效月结
    					hasMon = true;
    					if(StringUtils.isNotBlank(postInfos.getData(i).getString("POST_ADR"))){  //如果有邮箱
    						accparam.put("TYPE", postInfos.getData(i).getString("POST_TAG"));  
    			    	    accparam.put("PUSH_CHANNEL", postInfos.getData(i).getString("POST_CHANNEL"));
    			    	    accparam.put("SMS_NUMBER", postInfos.getData(i).getString("RECEIVE_NUMBER"));
    			    	    accparam.put("EMAIL_NUMBER", postAdr==""?postInfos.getData(i).getString("POST_ADR"):postAdr);
    			    	    accparam.put("PUSH_DATE", postDate);
    			    	    accparam.put("FLAG", "1");
    			    	    IDataset results = AcctCall.setEPostInfo(accparam);
    						if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
	    						ModifyEPostInfoBean.upMonPost(tabData,postInfos.getData(i).getString("POST_CHANNEL"), 
	        				    postInfos.getData(i).getString("RECEIVE_NUMBER"), postAdr==""?postInfos.getData(i).getString("POST_ADR"):postAdr, postDate);
    					    }else{
    					    	result.put("RETURN_CODE", "9999");
    						    result.put("RETURN_MESSAGE", results.getData(0).getString("RESULT_INFO"));
    				    		return result;
    						}
	    					clearParam(accparam);
    					}else{  //如果只有手机
    						accparam.put("TYPE", postInfos.getData(i).getString("POST_TAG"));  
    			    	    accparam.put("PUSH_CHANNEL", "2");
    			    	    accparam.put("SMS_NUMBER", postInfos.getData(i).getString("RECEIVE_NUMBER"));
    			    	    accparam.put("EMAIL_NUMBER", postAdr==""?serialNumber+"@139.com":postAdr);
    			    	    accparam.put("PUSH_DATE", postDate);
    			    	    accparam.put("FLAG", "1");
    			    	    IDataset results = AcctCall.setEPostInfo(accparam);
    						if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
	    						ModifyEPostInfoBean.upMonPost(tabData,"2", 
	        					postInfos.getData(i).getString("RECEIVE_NUMBER"), postAdr==""?serialNumber+"@139.com":postAdr, postDate);
    					    }else{
    					    	result.put("RETURN_CODE", "9999");
    						    result.put("RETURN_MESSAGE", results.getData(0).getString("RESULT_INFO"));
    				    		return result;
    						}
	    					clearParam(accparam);
    					}
    				}	
    			}
    			if(!hasMon){  //若没有月结,这种情况没有月结,但是肯定有营业/现金，则按营业/现金的设定增加月结
    				for(int i=0;i<postInfos.size();i++){
    					if("2".equals(postInfos.getData(i).getString("POST_TAG"))||"1".equals(postInfos.getData(i).getString("POST_TAG"))){  //如果是营业
    						if(StringUtils.isNotBlank(postInfos.getData(i).getString("POST_ADR"))){   //如果有邮箱
    							accparam.put("TYPE", "0");  
        			    	    accparam.put("PUSH_CHANNEL", postInfos.getData(i).getString("POST_CHANNEL"));
        			    	    accparam.put("SMS_NUMBER", postInfos.getData(i).getString("RECEIVE_NUMBER"));
        			    	    accparam.put("EMAIL_NUMBER", postAdr==""?postInfos.getData(i).getString("POST_ADR"):postAdr);
        			    	    accparam.put("PUSH_DATE", postDate);
        			    	    accparam.put("FLAG", "0");
        			    	    IDataset results = AcctCall.setEPostInfo(accparam);
        						if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
        			    	        ModifyEPostInfoBean.addMonPost(tabData, "0", postInfos.getData(i).getString("POST_CHANNEL"), 
            						postInfos.getData(i).getString("RECEIVE_NUMBER"), postAdr==""?postInfos.getData(i).getString("POST_ADR"):postAdr, postDate);
        			    	    }else{
        			    	    	result.put("RETURN_CODE", "9999");
        						    result.put("RETURN_MESSAGE", results.getData(0).getString("RESULT_INFO"));
        				    		return result;
        						}
        			    	    clearParam(accparam);
    						}else{  //如果只有手机
    							accparam.put("TYPE", "0");  
        			    	    accparam.put("PUSH_CHANNEL", "2");
        			    	    accparam.put("SMS_NUMBER", postInfos.getData(i).getString("RECEIVE_NUMBER"));
        			    	    accparam.put("EMAIL_NUMBER", postAdr==""?serialNumber+"@139.com":postAdr);
        			    	    accparam.put("PUSH_DATE", postDate);
        			    	    accparam.put("FLAG", "0");
        			    	    IDataset results = AcctCall.setEPostInfo(accparam);
        						if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
        			    	        ModifyEPostInfoBean.addMonPost(tabData, "0", "2", 
            						postInfos.getData(i).getString("RECEIVE_NUMBER"), postAdr==""?serialNumber+"@139.com":postAdr, postDate);
        			    	    }else{
        			    	    	result.put("RETURN_CODE", "9999");
        						    result.put("RETURN_MESSAGE", results.getData(0).getString("RESULT_INFO"));
        				    		return result;
        						}
        			    	    clearParam(accparam);
    						}
    						
    					}
    				}
    				
    			}
    		}else{  //不存在，增加一条默认139邮箱的月结推送
    			accparam.put("TYPE", "0");  
	    	    accparam.put("PUSH_CHANNEL", "1");
	    	    accparam.put("SMS_NUMBER", "");
	    	    accparam.put("EMAIL_NUMBER", postAdr==""?serialNumber+"@139.com":postAdr);
	    	    accparam.put("PUSH_DATE", postDate);
	    	    accparam.put("FLAG", "0");
	    	    IDataset results = AcctCall.setEPostInfo(accparam);
				if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
	    	       ModifyEPostInfoBean.addMonPost(tabData, "0", "1", "", postAdr==""?serialNumber+"@139.com":postAdr, postDate);
	    	    }else{
	    	    	result.put("RETURN_CODE", "9999");
				    result.put("RETURN_MESSAGE", results.getData(0).getString("RESULT_INFO"));
		    		return result;
				}
	    	    clearParam(accparam);
    			
    		}
    		
    		
    		
    	}else if("8".equals(interType)){   //取消定期推送
    		boolean hasMon = false;
    		if(IDataUtil.isNotEmpty(postInfos)){  //存在数据
    			for(int i=0;i<postInfos.size();i++){
    				if("0".equals(postInfos.getData(i).getString("POST_TAG"))&&"1".equals(postInfos.getData(i).getString("RSRV_STR1"))){  //如果是有效月结
    					hasMon = true;
    					accparam.put("TYPE", "0");
    		    	    accparam.put("FLAG", "2");
    		    	    IDataset results = AcctCall.setEPostInfo(accparam);
    					if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    		    	       ModifyEPostInfoBean.delMonPost(serialNumber);
    		    	    }else{
    		    	    	result.put("RETURN_CODE", "9999");
						    result.put("RETURN_MESSAGE", results.getData(0).getString("RESULT_INFO"));
				    		return result;
						}
    		    	    clearParam(accparam);
    				}	
    			}
    		}
    		if(!hasMon){  //如果没月结，则报错
    			result.put("RETURN_CODE", "9999");
      		    result.put("RETURN_MESSAGE", "该用户未设置定期推送，不能取消");
          	    return result;
    		}
    	}
    	result.put("RETURN_CODE", "0000");
		result.put("RETURN_MESSAGE", "处理成功");
  	    return result;
    }
	
    
    
    /**
	 * 月结发票设置接口----网厅
	 */
	public IData modifyEPost(IData data) throws Exception
	{

	    IData result = new DataMap();
		String serialNumber = data.getString("SERIAL_NUMBER");
		String operCode = data.getString("OPER_CODE");
		String postChannel = data.getString("POST_CHANNEL");
		String reNumber = data.getString("RECEIVE_NUMBER");
		String postAdr = data.getString("POST_ADR");
		String postDate = data.getString("POST_DATE");
		
		String userId = "";
		String acctId = "";
		String routeId = "";
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER", serialNumber);
		inparam.put("POST_TAG", "0");
		IDataset postInfos = ModifyEPostInfoBean.qryEPostInfo(inparam);
		IDataset dataset = UserInfoQry.getUserInfoBySn(serialNumber,"0");
		if(IDataUtil.isNotEmpty(dataset)){
		   userId = dataset.getData(0).getString("USER_ID"); 
		}else{   //用户资料不存在
			result.put("RESULT_CODE", "99");
			result.put("RESULT_INFO", "用户资料不存在");
			return result;
		}
		routeId = dataset.getData(0).getString("EPARCHY_CODE"); 
		IDataset acctset = AcctInfoQry.getAcctInfoByCustId(dataset.getData(0).getString("CUST_ID"),routeId);
		if(IDataUtil.isNotEmpty(acctset)){
		   acctId = acctset.getData(0).getString("ACCT_ID"); 
		}else{   //用户账户资料不存在
			result.put("RESULT_CODE", "99");
			result.put("RESULT_INFO", "用户账户资料不存在");
			return result; 
		}
		
		 //入表通用参数
		 IData tabdata = new DataMap();
		 //tabdata.put("PARTITION_ID", userId.length()>4?userId.substring(userId.length()-4,userId.length()):"");
		 tabdata.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
		 tabdata.put("SERIAL_NUMBER", serialNumber);
		 tabdata.put("EPARCHY_CODE", routeId);
		 tabdata.put("USER_ID", userId);
		 tabdata.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		 tabdata.put("UPDATE_DEPART_ID", getVisit().getDepartId());
		 tabdata.put("UPDATE_TIME", SysDateMgr.getSysDate());
		 tabdata.put("RSRV_STR1", "1");
		 
		//账管接口通用参数
	     IData accparam = new DataMap();
	     accparam.put("ACCT_TYPE", "0");  //个人用户设置
	     accparam.put("EPARCHY_CODE", routeId);   //手机号码地州编码
	     accparam.put("USER_ID", userId);
	     accparam.put("ACCT_ID", acctId);
	     //accparam.put("PARTITION_ID", acctId.length()>4?acctId.substring(acctId.length()-4,acctId.length()):"");
	     accparam.put("PARTITION_ID", StrUtil.getPartition4ById(acctId));
	     accparam.put("SERIAL_NUMBER", serialNumber);
	     
	     IDataset results = new DatasetList();
		 
		if("0".equals(operCode)){  //新增
			if(IDataUtil.isNotEmpty(ModifyEPostInfoBean.qryEPostInfoByTag(inparam))){
			   result.put("RESULT_CODE", "99");
			   result.put("RESULT_INFO", "该用户存在月结发票业务，不能新增");
			   return result;
			}
			accparam.put("TYPE", "0");  
    	    accparam.put("PUSH_CHANNEL", postChannel);
    	    accparam.put("SMS_NUMBER", reNumber);
    	    accparam.put("EMAIL_NUMBER", postAdr);
    	    accparam.put("PUSH_DATE", postDate);
    	    accparam.put("FLAG", "0");
    	    results = AcctCall.setEPostInfo(accparam);
			if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    	        ModifyEPostInfoBean.addMonPost(tabdata, "0", postChannel, reNumber, postAdr, postDate);
    	    }else{
	    		result.put("RESULT_CODE", "99");
				result.put("RESULT_INFO", results.getData(0).getString("RESULT_INFO"));
	    		return result;
			}
    	    clearParam(accparam);
			if(ModifyEPostInfoBean.upBusiCashPost(tabdata, "1", postChannel, reNumber, postAdr)){
				accparam.put("TYPE", "1");  
	    	    accparam.put("PUSH_CHANNEL", postChannel);
	    	    accparam.put("SMS_NUMBER", reNumber);
	    	    accparam.put("EMAIL_NUMBER", postAdr);
	    	    accparam.put("FLAG", "1");
    	    	results = AcctCall.setEPostInfo(accparam);
    	    	if(results.size()>0&&!"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    	    		result.put("RESULT_CODE", "99");
    				result.put("RESULT_INFO", results.getData(0).getString("RESULT_INFO"));
    	    		return result;
    	    	}
	    	    clearParam(accparam);
			}
			if(ModifyEPostInfoBean.upBusiCashPost(tabdata, "2", postChannel, reNumber, postAdr)){
				accparam.put("TYPE", "2");  
	    	    accparam.put("PUSH_CHANNEL", postChannel);
	    	    accparam.put("SMS_NUMBER", reNumber);
	    	    accparam.put("EMAIL_NUMBER", postAdr);
	    	    accparam.put("FLAG", "1");
    	    	results = AcctCall.setEPostInfo(accparam);
    	    	if(results.size()>0&&!"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    	    		result.put("RESULT_CODE", "99");
    				result.put("RESULT_INFO", results.getData(0).getString("RESULT_INFO"));
    	    		return result;
    	    	}
	    	    clearParam(accparam);
			}
		}else if("1".equals(operCode)){  //修改
			if(IDataUtil.isEmpty(ModifyEPostInfoBean.qryEPostInfoByTag(inparam))){
				result.put("RESULT_CODE", "99");
				result.put("RESULT_INFO", "该用户未设置月结发票，无法修改");
				return result;
			}
			accparam.put("TYPE", "0");  
    	    accparam.put("PUSH_CHANNEL", postChannel);
    	    accparam.put("SMS_NUMBER", reNumber);
    	    accparam.put("EMAIL_NUMBER", postAdr);
    	    accparam.put("PUSH_DATE", postDate);
    	    accparam.put("FLAG", "1");
    	    results = AcctCall.setEPostInfo(accparam);
			if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    	       ModifyEPostInfoBean.upMonPost(tabdata, postChannel, reNumber, postAdr, postDate);
    	    }else{
	    		result.put("RESULT_CODE", "99");
				result.put("RESULT_INFO", results.getData(0).getString("RESULT_INFO"));
	    		return result;
			}
    	    clearParam(accparam);
			if(ModifyEPostInfoBean.upBusiCashPost(tabdata, "1", postChannel, reNumber, postAdr)){
				accparam.put("TYPE", "1");  
	    	    accparam.put("PUSH_CHANNEL", postChannel);
	    	    accparam.put("SMS_NUMBER", reNumber);
	    	    accparam.put("EMAIL_NUMBER", postAdr);
	    	    accparam.put("FLAG", "1");
	    	    results = AcctCall.setEPostInfo(accparam);
	    	    if(results.size()>0&&!"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    	    		result.put("RESULT_CODE", "99");
    				result.put("RESULT_INFO", results.getData(0).getString("RESULT_INFO"));
    	    		return result;
    	    	}
	    	    clearParam(accparam);
			}
			if(ModifyEPostInfoBean.upBusiCashPost(tabdata, "2", postChannel, reNumber, postAdr)){
				accparam.put("TYPE", "2");  
	    	    accparam.put("PUSH_CHANNEL", postChannel);
	    	    accparam.put("SMS_NUMBER", reNumber);
	    	    accparam.put("EMAIL_NUMBER", postAdr);
	    	    accparam.put("FLAG", "1");
	    	    results = AcctCall.setEPostInfo(accparam);
	    	    if(results.size()>0&&!"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    	    		result.put("RESULT_CODE", "99");
    				result.put("RESULT_INFO", results.getData(0).getString("RESULT_INFO"));
    	    		return result;
    	    	}
	    	    clearParam(accparam);
			}
		}else if("2".equals(operCode)){  //删除
			if(IDataUtil.isEmpty(ModifyEPostInfoBean.qryEPostInfoByTag(inparam))){
				result.put("RESULT_CODE", "99");
				result.put("RESULT_INFO", "该用户未设置月结发票，无法删除");
				return result;
			}
			accparam.put("TYPE", "0");
    	    accparam.put("FLAG", "2");
    	    results = AcctCall.setEPostInfo(accparam);
			if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
    	       ModifyEPostInfoBean.delMonPost(serialNumber);
    	    }else{
	    		result.put("RESULT_CODE", "99");
				result.put("RESULT_INFO", results.getData(0).getString("RESULT_INFO"));
	    		return result;
			}
    	    clearParam(accparam);
		}else if("3".equals(operCode)){//删除任意发票类型
			if(IDataUtil.isEmpty(postInfos)){
				result.put("RESULT_CODE", "99");
				result.put("RESULT_INFO", "该用户未设置发票类型，无法删除");
				return result;
			}
			for(int i=0;i<postInfos.size();i++){
				String postTag = postInfos.getData(i).getString("POST_TAG");
				accparam.put("TYPE", postTag);
	    	    accparam.put("FLAG", "2");
	    	    results = AcctCall.setEPostInfo(accparam);
				if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
	    	       ModifyEPostInfoBean.delPostType(serialNumber,postTag);
	    	    }else{
		    		result.put("RESULT_CODE", "99");
					result.put("RESULT_INFO", results.getData(0).getString("RESULT_INFO"));
		    		return result;
				}
			}
			 clearParam(accparam);
		}
		result.put("RESULT_CODE", "0");
		result.put("RESULT_INFO", "Trade OK！");
		return result;
	}
	

	
	/**
	 * 电子发票推送信息设置登记
	 */
	 public IDataset modiTradeReg(IData pgData) throws Exception
     {
		 boolean insertBusi = false;
		 boolean insertMon = false;
		 boolean insertCash = false;
		 String reNumber = "";  //日常手机
		 String postAdr = "";  //日常邮箱
		 String postAdrSec = "";  //日常邮箱
		 int setFlag = 0; //用于区分打印免填单内容是新增0、取消1（默认新增）
		 
		 
		 String serialNumber = pgData.getString("SERIAL_NUMBER");
		 String postTypeMon = pgData.getString("POST_TYPE_MON");
		 String postTypeCash = pgData.getString("POST_TYPE_CASH");
		 String postTypeBusi = pgData.getString("POST_TYPE_BUSI");
		 String userId = "";
		 String custId = "";
		 String acctId = "";
		 String routeId = "";
		 
		 String postChannel = pgData.getString("POST_CHANNEL");
		 if("02".equals(postChannel)){  
         	postChannel = "0";
         	reNumber = pgData.getString("postinfo_RECEIVE_NUMBER");
		 }else if("12".equals(postChannel)){
			postChannel = "1";
			postAdr = pgData.getString("postinfo_POST_ADR","");
			postAdrSec = pgData.getString("postinfo_POST_ADR_SEC","");
			if(!"".equals(postAdr))
			{
				if(!"139.com".equals(postAdr.substring(postAdr.indexOf("@")+1))){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"请填写139邮箱！");
				}
				
			}
		 }else if("02,12".equals(postChannel)){
			postChannel = "2";
			reNumber = pgData.getString("postinfo_RECEIVE_NUMBER");
			postAdr = pgData.getString("postinfo_POST_ADR","");
			postAdrSec = pgData.getString("postinfo_POST_ADR_SEC","");
			if(!"139.com".equals(postAdr.substring(postAdr.indexOf("@")+1))){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"请填写139邮箱！");
			}
		 }
		 
		 if(StringUtils.isNotEmpty(reNumber)){ //检验推送手机号码是否为非销户用户且为移动号码
			 IDataset mobile = UserInfoQry.checkUserIsMoblie(reNumber);
			 if(IDataUtil.isEmpty(mobile)){
				 CSAppException.apperr(CrmCommException.CRM_COMM_103,"该推送手机号码为非移动号码，不能进行推送！");
			 }
			 IDataset reUserInfo = UserInfoQry.checkUserIsCancel(reNumber);
			 if(IDataUtil.isNotEmpty(reUserInfo)){
			     if(!"0".equals(reUserInfo.getData(0).getString("REMOVE_TAG"))){
				    CSAppException.apperr(CrmCommException.CRM_COMM_103,"该推送手机号码所对应的用户已销户！");
			     }
			 }else{
				 if("0".equals(pgData.getString("NEW_FLAG"))){
					 //CSAppException.apperr(CrmCommException.CRM_COMM_103,"该推送手机号码所对应的用户资料不存在！"); 
				 }else{
					 CSAppException.apperr(CrmCommException.CRM_COMM_103,"该推送手机号码所对应的用户资料不存在！");
				 }
			 }
		 }
		 
		 
		 IDataset maintrade = new DatasetList();
		 IData inparam = new DataMap();
		 inparam.put("SERIAL_NUMBER", serialNumber);
		 IDataset postInfos = ModifyEPostInfoBean.qryEPostInfo(inparam);
		 IDataset dataset = UserInfoQry.getUserInfoBySn(serialNumber,"0");
		 	 
		 IDataset custset = new DatasetList(); 
		 IDataset userPset = new DatasetList();
		 String custName = "";
		 String productId = "";
		 String brandCode = "";
		 String cityCode = "";
		 
		 if(IDataUtil.isNotEmpty(dataset)){
			 userId = dataset.getData(0).getString("USER_ID"); 
			 routeId = dataset.getData(0).getString("EPARCHY_CODE"); 
			 custId = dataset.getData(0).getString("CUST_ID");
			 cityCode = dataset.getData(0).getString("CITY_CODE");
			 IDataset acctset = AcctInfoQry.getAcctInfoByCustId(custId,routeId);
			 custset = CustomerInfoQry.getNormalCustInfoBySN(serialNumber);
			 userPset = UserProductInfoQry.queryMainProduct(userId);
			 
			 custName = custset.getData(0).getString("CUST_NAME", "");
			 productId = userPset.getData(0).getString("PRODUCT_ID","");
			 brandCode = userPset.getData(0).getString("BRAND_CODE","");
			 
			 if(IDataUtil.isNotEmpty(acctset)){
				 acctId = acctset.getData(0).getString("ACCT_ID");  
			}
		 }else{
			 maintrade = TradeInfoQry.getMainTradeBySn(serialNumber, routeId);
			 if (IDataUtil.isNotEmpty(maintrade)) {
				 userId = maintrade.getData(0).getString("USER_ID");
				 custId = maintrade.getData(0).getString("CUST_ID");
				 cityCode = maintrade.getData(0).getString("CITY_CODE");
				 routeId = maintrade.getData(0).getString("EPARCHY_CODE");
				 acctId = maintrade.getData(0).getString("ACCT_ID");
				 custName = maintrade.getData(0).getString("CUST_NAME", "");
				 productId = maintrade.getData(0).getString("PRODUCT_ID", "");
				 brandCode = maintrade.getData(0).getString("BRAND_CODE", "");
			 } else {
				 // REQ201707280024关于支持物联网号码和销号号码设置并打印电子发票的优化需求 add by zhanglin3 20180313
				 IDataset lastDestroyUserInfo = UserInfoQry.getUnnormalUserInfoBySn(serialNumber);
				 if (IDataUtil.isNotEmpty(lastDestroyUserInfo)) {
					 IData userInfo = lastDestroyUserInfo.first();
					 userId = userInfo.getString("USER_ID");
					 routeId = userInfo.getString("EPARCHY_CODE");
					 custId = userInfo.getString("CUST_ID");
					 cityCode = userInfo.getString("CITY_CODE");
					 IDataset acctset = AcctInfoQry.getAcctInfoByCustId(custId,routeId);
					 custset = CustomerInfoQry.getCustomerInfoByUserId(userId);
					 userPset = UserProductInfoQry.queryMainProduct(userId);

					 custName = custset.getData(0).getString("CUST_NAME", "");
					 productId = userPset.getData(0).getString("PRODUCT_ID","");
					 brandCode = userPset.getData(0).getString("BRAND_CODE","");

					 if (IDataUtil.isNotEmpty(acctset)) {
						 acctId = acctset.getData(0).getString("ACCT_ID");
					 } else {
						 IData payrela = UcaInfoQry.qryDefaultPayRelaByUserId(userId, routeId);
						 if (IDataUtil.isEmpty(payrela)) {
							 payrela = UcaInfoQry.qryLastPayRelaByUserId(userId, routeId);
							 if (IDataUtil.isNotEmpty(payrela)) {
								 acctId = payrela.getString("ACCT_ID");
							 }
						 } else {
							 acctId = payrela.getString("ACCT_ID");
						 }
					 }
				 }
			 }
		 }

		 if(StringUtils.isBlank(userId)||StringUtils.isBlank(acctId)){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户或账户资料不存在");
		 }

		 if (IDataUtil.isNotEmpty(postInfos)) {
			 if (!userId.equals(postInfos.getData(0)
					 .getString("USER_ID", ""))) {
				 ModifyEPostInfoBean.delBusiCashPost(serialNumber, "0");
				 ModifyEPostInfoBean.delBusiCashPost(serialNumber, "1");
				 ModifyEPostInfoBean.delBusiCashPost(serialNumber, "2");
			 }
		 }

		 inparam.put("POST_TAG", "0"); //表里无月结
         if(IDataUtil.isEmpty(ModifyEPostInfoBean.qryEPostInfoByTag(inparam))){   
			 insertMon = true; 
		 }
         inparam.put("POST_TAG", "1"); //表里无现金
         if(IDataUtil.isEmpty(ModifyEPostInfoBean.qryEPostInfoByTag(inparam))){   
			 insertCash = true; 
		 }
		 inparam.put("POST_TAG", "2"); //表里无营业
		 if(IDataUtil.isEmpty(ModifyEPostInfoBean.qryEPostInfoByTag(inparam))){   
			 insertBusi = true;
		 }
		 
		 
		 //账管接口通用参数
		 IData accparam = new DataMap();
		 accparam.put("ACCT_TYPE", "0");  //个人用户设置
		 accparam.put("EPARCHY_CODE", routeId);   //手机号码地州编码
		 accparam.put("USER_ID", userId);
		 accparam.put("ACCT_ID", acctId);
		 accparam.put("PARTITION_ID", StrUtil.getPartition4ById(acctId));
		 //accparam.put("PARTITION_ID", acctId.length()>4?acctId.substring(acctId.length()-4,acctId.length()):"");
		 accparam.put("SERIAL_NUMBER", serialNumber);
		 accparam.put("NEW_FLAG", pgData.getString("NEW_FLAG"));  //是否为新开户
		 
		 //入表通用参数
		 IData tabdata = new DataMap();
		 tabdata.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
		 //tabdata.put("PARTITION_ID", userId.length()>4?userId.substring(userId.length()-4,userId.length()):"");
		 tabdata.put("SERIAL_NUMBER", serialNumber);
		 tabdata.put("EPARCHY_CODE", routeId);
		 tabdata.put("USER_ID", userId);
		 tabdata.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		 tabdata.put("UPDATE_DEPART_ID", getVisit().getDepartId());
		 tabdata.put("UPDATE_TIME", SysDateMgr.getSysDate());
		 tabdata.put("RSRV_STR1", "1");
		 tabdata.put("RSRV_STR2", postAdrSec);
		 
		//邮件客服通用参数
//		 IData maildata = new DataMap();
		 // 客户信息
	     Map<String, Object> customerInfo = new HashMap<String, Object>();
	     // 发票信息
	     Map<String, Object> invoiceInfo = new HashMap<String, Object>();
	     
	     customerInfo.put("KHSJHM", serialNumber);
	     customerInfo.put("KHBS", userId);
		 if(IDataUtil.isNotEmpty(dataset)){
			 customerInfo.put("KHLX", dataset.getData(0).getString("NET_TYPE_CODE")=="00"?"1":"2"); 
		 }
		 if(IDataUtil.isNotEmpty(maintrade)){
			 customerInfo.put("KHLX", maintrade.getData(0).getString("NET_TYPE_CODE")=="00"?"1":"2"); 
		 }
		 customerInfo.put("KHGSD", routeId);
		 customerInfo.put("KHFQQD", "1");
//		 customerInfo.put("KHQQSJ", System.currentTimeMillis());
		 invoiceInfo.put("TSZQ", "1");
		 invoiceInfo.put("SFBS", "898");  //省份标识写死
		 invoiceInfo.put("YWLX", "1");
//		 invoiceInfo.put("YWLSH", "DZF_"+invoiceInfo.get("SFBS")+"_"+serialNumber+"_"+userId+"_*_"+customerInfo.get("KHQQSJ"));
		 
		 
		 
		 //判断邮箱是否产生了变化
		 try{
			 IDataset eresult = dealMailChange(tabdata,customerInfo,invoiceInfo,postInfos,postAdr,serialNumber,userId);
		 }catch(Exception e){
     		logger.error("139邮箱设置失败:"+e.getMessage());
		 }
//		 if(IDataUtil.isNotEmpty(eresult)){
//		   if(!"0000".equals(eresult.getData(0).getString("RETURN_CODE"))){
//			   CSAppException.apperr(CrmCommException.CRM_COMM_103,"139邮箱设置失败:"+eresult.getData(0).getString("RETURN_MESSAGE"));  
//		   }
//         }
		 
		 invoiceInfo.remove("JKLX");
		 customerInfo.remove("KHYXDZ");
		 
		//处理其他的参数并且调用一级BOSS同步数据到客服邮件系统
		 try{
			 IDataset iresult = dealOtherIBossParam(customerInfo,invoiceInfo,postInfos,postChannel,postTypeMon,pgData.getString("postinfo_POST_DATE_MON"),serialNumber,userId);
		 }catch(Exception e){
	     	 logger.error("139邮箱设置失败:"+e.getMessage());
		 }
//		 if(IDataUtil.isNotEmpty(iresult)){
//		   if(!"0000".equals(iresult.getData(0).getString("RETURN_CODE"))){
//			   CSAppException.apperr(CrmCommException.CRM_COMM_103,"139邮箱设置失败:"+iresult.getData(0).getString("RETURN_MESSAGE"));
//		   }
//	     }
		 
		 if(IDataUtil.isNotEmpty(postInfos)){
			 for(int i=0;i<postInfos.size();i++){
				  if(insertMon&&StringUtils.isNotEmpty(postTypeMon)){   //增加月结
					
					//接口参数
					accparam.put("TYPE", "0");  //月结
					accparam.put("FLAG", "0");  //新增
					accparam.put("SMS_NUMBER", reNumber);
					accparam.put("EMAIL_NUMBER", postAdr);
					accparam.put("PUSH_DATE", pgData.getString("postinfo_POST_DATE_MON"));  //推送时间
					if(StringUtils.isNotEmpty(reNumber)
						&&StringUtils.isEmpty(postAdr)){  //手机推送
						accparam.put("PUSH_CHANNEL", "0");
						accparam.put("PUSH_FLAG", "1");
						accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
					}else if(StringUtils.isEmpty(reNumber)
					    &&StringUtils.isNotEmpty(postAdr)){   //邮箱推送
						accparam.put("PUSH_CHANNEL", "1");
						accparam.put("PUSH_FLAG", "2");
						accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
					}else{   //手机+邮箱
						accparam.put("PUSH_CHANNEL", "2");
						accparam.put("PUSH_FLAG", "2");
						accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
					}
					IDataset results = AcctCall.setEPostInfo(accparam);
					if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
					    ModifyEPostInfoBean.addMonPost(tabdata, "0", postChannel, reNumber, postAdr, pgData.getString("postinfo_POST_DATE_MON"));
					}else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,results.getData(0).getString("RESULT_INFO"));
					}
					insertMon = false;
					clearParam(accparam);
				 }
			  if(insertCash&&StringUtils.isNotEmpty(postTypeCash)){  //增加现金
					//接口参数
					accparam.put("TYPE", "1");  //现金
					accparam.put("FLAG", "0");  //新增
					accparam.put("SMS_NUMBER", reNumber);
					accparam.put("EMAIL_NUMBER", postAdr);
					if(StringUtils.isNotEmpty(reNumber)
						&&StringUtils.isEmpty(postAdr)){  //手机推送
						accparam.put("PUSH_CHANNEL", "0");
						accparam.put("PUSH_FLAG", "1");
						accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
					}else if(StringUtils.isEmpty(reNumber)
					    &&StringUtils.isNotEmpty(postAdr)){   //邮箱推送
						accparam.put("PUSH_CHANNEL", "1");
						accparam.put("PUSH_FLAG", "2");
						accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
					}else{   //手机+邮箱
						accparam.put("PUSH_CHANNEL", "2");
						accparam.put("PUSH_FLAG", "2");
						accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
					}
					IDataset results = AcctCall.setEPostInfo(accparam);
					if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
						ModifyEPostInfoBean.addBusiCashPost(tabdata, "1", postChannel, reNumber, postAdr);
					}else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,results.getData(0).getString("RESULT_INFO"));
					}
					insertCash = false;
					clearParam(accparam);
		         }
		         if(insertBusi&&StringUtils.isNotEmpty(postTypeBusi)){  //增加日常
					//接口参数
					accparam.put("TYPE", "2");  //日常
					accparam.put("FLAG", "0");  //新增
					accparam.put("SMS_NUMBER", reNumber);
					accparam.put("EMAIL_NUMBER", postAdr);
					if(StringUtils.isNotEmpty(reNumber)
						&&StringUtils.isEmpty(postAdr)){  //手机推送
						accparam.put("PUSH_CHANNEL", "0");
						accparam.put("PUSH_FLAG", "1");
						accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
					}else if(StringUtils.isEmpty(reNumber)
					    &&StringUtils.isNotEmpty(postAdr)){   //邮箱推送
						accparam.put("PUSH_CHANNEL", "1");
						accparam.put("PUSH_FLAG", "2");
						accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
					}else{   //手机+邮箱
						accparam.put("PUSH_CHANNEL", "2");
						accparam.put("PUSH_FLAG", "2");
						accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
					}
					IDataset results = AcctCall.setEPostInfo(accparam);
					if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
						ModifyEPostInfoBean.addBusiCashPost(tabdata, "2", postChannel, reNumber, postAdr);
					}else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,results.getData(0).getString("RESULT_INFO"));
					}
					insertBusi = false;
					clearParam(accparam);
		         }
		       
				 if("0".equals(postInfos.getData(i).getString("POST_TAG"))&&StringUtils.isEmpty(postTypeMon)){  //删除月结电子发票
					 
				    //接口参数
				    accparam.put("TYPE", "0");  //月结
					accparam.put("FLAG", "2");  //删除
					IDataset results = AcctCall.setEPostInfo(accparam);
					if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
					   ModifyEPostInfoBean.delMonPost(serialNumber);
					}else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,results.getData(0).getString("RESULT_INFO"));
					}
					clearParam(accparam);
					setFlag = 1;
				 }else if("0".equals(postInfos.getData(i).getString("POST_TAG"))&&StringUtils.isNotEmpty(postTypeMon)){ //修改月结

					//接口参数
					accparam.put("TYPE", "0");  //月结
					accparam.put("FLAG", "1");  //修改
					accparam.put("SMS_NUMBER", reNumber);
					accparam.put("EMAIL_NUMBER", postAdr);
					accparam.put("PUSH_DATE", pgData.getString("postinfo_POST_DATE_MON"));  //推送时间
					accparam.put("PUSH_CHANNEL", postChannel);	
					accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
					if("0".equals(postChannel))
					{
						accparam.put("PUSH_FLAG", "1");
					}else
					{
						accparam.put("PUSH_FLAG", "2");
					}
					
					IDataset results = AcctCall.setEPostInfo(accparam);
					if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
					   ModifyEPostInfoBean.upMonPost(tabdata, postChannel, reNumber, postAdr, pgData.getString("postinfo_POST_DATE_MON"));
					}else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,results.getData(0).getString("RESULT_INFO"));
					}
					clearParam(accparam);
				 }
				 if("1".equals(postInfos.getData(i).getString("POST_TAG"))&&StringUtils.isEmpty(postTypeCash)){  //删除现金
					    
				    //接口参数
				    accparam.put("TYPE", "1");  //现金
					accparam.put("FLAG", "2");  //删除
					IDataset results = AcctCall.setEPostInfo(accparam);
				    if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
					   ModifyEPostInfoBean.delBusiCashPost(serialNumber, "1");
					}else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,results.getData(0).getString("RESULT_INFO"));
					}
					clearParam(accparam);
					setFlag = 1;
				 }else if("1".equals(postInfos.getData(i).getString("POST_TAG"))&&StringUtils.isNotEmpty(postTypeCash)){  //修改现金
					
					//接口参数
					accparam.put("TYPE", "1");  //现金
					accparam.put("FLAG", "1");  //修改
					accparam.put("SMS_NUMBER", reNumber);
					accparam.put("EMAIL_NUMBER", postAdr);
					accparam.put("PUSH_CHANNEL", postChannel);
					accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
					if("0".equals(postChannel))
					{
						accparam.put("PUSH_FLAG", "1");
					}else
					{
						accparam.put("PUSH_FLAG", "2");
					}
					IDataset results = AcctCall.setEPostInfo(accparam);
					if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
					   ModifyEPostInfoBean.upBusiCashPost(tabdata, "1", postChannel, reNumber, postAdr);
					}else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,results.getData(0).getString("RESULT_INFO"));
					}
					clearParam(accparam);
				 }
				 if("2".equals(postInfos.getData(i).getString("POST_TAG"))&&StringUtils.isEmpty(postTypeBusi)){  //删除日常电子发票
					    
				    //接口参数
				    accparam.put("TYPE", "2");  //日常
					accparam.put("FLAG", "2");  //删除
					IDataset results = AcctCall.setEPostInfo(accparam);
					if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
					   ModifyEPostInfoBean.delBusiCashPost(serialNumber, "2");
					}else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,results.getData(0).getString("RESULT_INFO"));
					}
					clearParam(accparam);
					setFlag = 1;
				 }else if("2".equals(postInfos.getData(i).getString("POST_TAG"))&&StringUtils.isNotEmpty(postTypeBusi)){  //修改日常
					
					//接口参数
					accparam.put("TYPE", "2");  //日常
					accparam.put("FLAG", "1");  //修改
					accparam.put("SMS_NUMBER", reNumber);
					accparam.put("EMAIL_NUMBER", postAdr);
					accparam.put("PUSH_CHANNEL", postChannel);
					accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
					if("0".equals(postChannel))
					{
						accparam.put("PUSH_FLAG", "1");
					}else
					{
						accparam.put("PUSH_FLAG", "2");
					}
					IDataset results = AcctCall.setEPostInfo(accparam);
			    	if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
					   ModifyEPostInfoBean.upBusiCashPost(tabdata, "2", postChannel, reNumber, postAdr);
				    }else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,results.getData(0).getString("RESULT_INFO"));
					}
					clearParam(accparam);
				 }
			 }
		 }else{   //
			 if(StringUtils.isNotEmpty(postTypeMon)){   //如果设置了月结电子发票				
				//接口参数
				accparam.put("TYPE", "0");  //月结
				accparam.put("FLAG", "0");  //新增
				accparam.put("SMS_NUMBER", reNumber);
				accparam.put("EMAIL_NUMBER", postAdr);
				accparam.put("PUSH_DATE", pgData.getString("postinfo_POST_DATE_MON"));  //推送时间
				if(StringUtils.isNotEmpty(reNumber)
					&&StringUtils.isEmpty(postAdr)){  //手机推送
					accparam.put("PUSH_CHANNEL", "0");
					accparam.put("PUSH_FLAG", "1");
					accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
				}else if(StringUtils.isEmpty(reNumber)
				    &&StringUtils.isNotEmpty(postAdr)){   //邮箱推送
					accparam.put("PUSH_CHANNEL", "1");
					accparam.put("PUSH_FLAG", "2");
					accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
				}else{   //手机+邮箱
					accparam.put("PUSH_CHANNEL", "2");
					accparam.put("PUSH_FLAG", "2");
					accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
				}
					IDataset results = AcctCall.setEPostInfo(accparam);
					if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
				    ModifyEPostInfoBean.addMonPost(tabdata, "0", postChannel, reNumber, postAdr, pgData.getString("postinfo_POST_DATE_MON"));
					}else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,results.getData(0).getString("RESULT_INFO"));
					}	
				clearParam(accparam);
			 }
			 if(StringUtils.isNotEmpty(postTypeCash)){
				//接口参数
				accparam.put("TYPE", "1");  //现金
				accparam.put("FLAG", "0");  //新增
				accparam.put("SMS_NUMBER", reNumber);
				accparam.put("EMAIL_NUMBER", postAdr);
				if(StringUtils.isNotEmpty(reNumber)
					&&StringUtils.isEmpty(postAdr)){  //手机推送
					accparam.put("PUSH_CHANNEL", "0");
					accparam.put("PUSH_FLAG", "1");
					accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
				}else if(StringUtils.isEmpty(reNumber)
				    &&StringUtils.isNotEmpty(postAdr)){   //邮箱推送
					accparam.put("PUSH_CHANNEL", "1");
					accparam.put("PUSH_FLAG", "2");
					accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
				}else{   //手机+邮箱
					accparam.put("PUSH_CHANNEL", "2");
					accparam.put("PUSH_FLAG", "2");
					accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
				}
				IDataset results = AcctCall.setEPostInfo(accparam);
				if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
					ModifyEPostInfoBean.addBusiCashPost(tabdata, "1", postChannel, reNumber, postAdr);
				}else{
					CSAppException.apperr(CrmCommException.CRM_COMM_103,results.getData(0).getString("RESULT_INFO"));
				}
				clearParam(accparam);
			 }
			 if(StringUtils.isNotEmpty(postTypeBusi)){   //如果设置了日常业务电子发票			
				
				//接口参数
				accparam.put("TYPE", "2");  //日常
				accparam.put("FLAG", "0");  //新增
				accparam.put("SMS_NUMBER", reNumber);
				accparam.put("EMAIL_NUMBER", postAdr);
				if(StringUtils.isNotEmpty(reNumber)
					&&StringUtils.isEmpty(postAdr)){  //手机推送
					accparam.put("PUSH_CHANNEL", "0");
					accparam.put("PUSH_FLAG", "1");
					accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
				}else if(StringUtils.isEmpty(reNumber)
				    &&StringUtils.isNotEmpty(postAdr)){   //邮箱推送
					accparam.put("PUSH_CHANNEL", "1");
					accparam.put("PUSH_FLAG", "2");
					accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
				}else{   //手机+邮箱
					accparam.put("PUSH_CHANNEL", "2");
					accparam.put("PUSH_FLAG", "2");
					accparam.put("EMAIL_NUMBER_SECOND", postAdrSec);
				}
				IDataset results = AcctCall.setEPostInfo(accparam);
				if(results.size()>0&&"0000".equals(results.getData(0).getString("RESULT_CODE"))){
					ModifyEPostInfoBean.addBusiCashPost(tabdata, "2", postChannel, reNumber, postAdr);
				}else{
					CSAppException.apperr(CrmCommException.CRM_COMM_103,results.getData(0).getString("RESULT_INFO"));
				}
				clearParam(accparam);

			 }
			 
		 }
		 
		 
		IData insbh = new DataMap();
		String tradeId = SeqMgr.getTradeIdFromDb();
		String month = StrUtil.getAcceptMonthById(tradeId);
        insbh.put("TRADE_ID", tradeId);
        insbh.put("ACCEPT_MONTH", month);
        insbh.put("TRADE_TYPE_CODE", "2016");
        insbh.put("PRIORITY", "0");
        insbh.put("SUBSCRIBE_TYPE", "0");
        insbh.put("SUBSCRIBE_STATE", "9");
        insbh.put("NEXT_DEAL_TAG", "0");
        insbh.put("IN_MODE_CODE", "0");
        insbh.put("CUST_ID", custId);
        insbh.put("CUST_NAME", custName);
        insbh.put("USER_ID", userId);
        insbh.put("ACCT_ID", acctId);
        insbh.put("SERIAL_NUMBER", serialNumber);
        insbh.put("NET_TYPE_CODE", "00");
        insbh.put("EPARCHY_CODE", "0898");
        insbh.put("CITY_CODE", cityCode);
        insbh.put("PRODUCT_ID", productId);
        insbh.put("BRAND_CODE", brandCode);
        insbh.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        insbh.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        insbh.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        insbh.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        insbh.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        insbh.put("OPER_FEE", "0");
        insbh.put("FOREGIFT", "0");
        insbh.put("ADVANCE_PAY", "0");
        insbh.put("FEE_STATE", "0");
        insbh.put("PROCESS_TAG_SET", "0000000000000000000000000000000000000000");
        insbh.put("OLCOM_TAG", "0");
        insbh.put("EXEC_TIME", SysDateMgr.getSysTime());
        insbh.put("CANCEL_TAG", "0");
        Dao.insert("TF_BH_TRADE", insbh, Route.getJourDbDefault()); // 将资料输入插入表
        
        
		regCnoteInfo(tradeId, SysDateMgr.getSysTime(),setFlag); 
		 

		IDataset results = new DatasetList();
		IData result =  new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "业务处理成功！");
        result.put("X_TRADEID", tradeId);
        results.add(result);
      
        return results;	 
		
     }
	 
	 
	 /**
	     * 登记解析串
	     * 
	     * @param tradeData
	     * @param receiptData
	     * @throws Exception
	     */
	    public static void regCnoteInfo(String tradeId, String acceptTime,int setFlag) throws Exception
	    {
	        String noteType = "1";
	        if (StringUtils.isBlank(acceptTime))
	        {
	            acceptTime = SysDateMgr.getSysTime();
	        }
	        String acceptMonth = acceptTime.substring(5, 7);

	        IDataset noteInfoLogs = TradeReceiptInfoQry.getNoteInfoByPk(tradeId, acceptMonth, noteType);
	        // 如果没有解析串记录，则记录打印模板数据到TF_B_TRADE_CNOTE_INFO表
	        if (IDataUtil.isEmpty(noteInfoLogs))
	        {
	            IData param = new DataMap();
	            param.put("TRADE_ID", tradeId);
	            param.put("ACCEPT_MONTH", acceptMonth);
	            param.put("NOTE_TYPE", noteType);
	            
	            String receiptInfo1 = "受理员工："+CSBizBean.getVisit().getStaffId()+"   业务受理时间："+acceptTime+" ~~业务类型：电子发票设置   受理方式: 服务密码校验";
	            String receiptInfo2="";
	            if(setFlag==1){
	            	receiptInfo2 = "您已取消电子发票打印功能，截止"+SysDateMgr.getChinaDate(acceptTime,SysDateMgr.PATTERN_CHINA_DATE)+"前不再提供打印发票功能，如需恢复打印发票功能请重新设置电子发票。";
	            }else{
	            	receiptInfo2 = "尊敬的客户，您已设置打印电子发票业务。";
	            }
	            
	            param.put("RECEIPT_INFO1", receiptInfo1);
	            param.put("RECEIPT_INFO2", receiptInfo2);
	            
	            param.put("RSRV_TAG1", "1");
	            param.put("RSRV_TAG3", "1");
	            
	            param.put("UPDATE_TIME", acceptTime);
	            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

	            param.put("ACCEPT_DATE", acceptTime);
	            param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	            param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	            param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
	            param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

	            Dao.executeUpdateByCodeCode("TF_B_TRADE_CNOTE_INFO", "INS_RECEIPT_LOGINFO", param, Route.getJourDbDefault());
	        }
	    }
	 
	 
	 
	 public static IDataset dealMailChange(IData tabdata,Map<String,Object> customerInfo,Map<String,Object> invoiceInfo,IDataset postInfos,String postAdr,String serialNumber,String userId) throws Exception 
     {
		 customerInfo.put("KHQQSJ", System.currentTimeMillis());
		 invoiceInfo.put("YWLSH", "DZF_"+invoiceInfo.get("SFBS")+"_"+serialNumber+"_"+userId+"_*_"+customerInfo.get("KHQQSJ"));
		 
		 IData monPara = new DataMap();
		 monPara.put("SERIAL_NUMBER", serialNumber);
		 if(IDataUtil.isNotEmpty(postInfos)){
			//只要有一条发生了变化，那么就肯定发生了变化
			 if(!StringUtils.equals(postInfos.getData(0).getString("POST_ADR"),postAdr)
					 &&postAdr!=""){  //邮箱发生了变化
				 if(IDataUtil.isNotEmpty(ModifyEPostInfoBean.qryEPostInfoByMon(monPara))){  //有无效数据，就改变
					 ModifyEPostInfoBean.upUnMonPost(tabdata, postAdr);
					 customerInfo.put("KHYXDZ", postAdr);
					 invoiceInfo.put("JKLX", "1");
					 invoiceInfo.put("TSRQ", "");
				 }else if(IDataUtil.isEmpty(ModifyEPostInfoBean.qryEPostInfoByMon(monPara))&&!StringUtils.equals(serialNumber+"@139.com", postAdr)){  
					//若无，则新增一条无效数据
					 //ModifyEPostInfoBean.addUnMonPost(tabdata, postAdr);???为什么新增无效数据
					 customerInfo.put("KHYXDZ", postAdr);
					 invoiceInfo.put("JKLX", "1");
					 invoiceInfo.put("TSRQ", "");
				 }
				 if(StringUtils.equals(serialNumber+"@139.com", postAdr)){
					 //页面输入的邮箱为139邮箱，则相当于取消设置常用邮箱，删除无效数据
					 ModifyEPostInfoBean.delUnMonPost(serialNumber);
					 invoiceInfo.put("JKLX", "2");
					 invoiceInfo.put("TSRQ", "");
				 }
			 }else if(StringUtils.isNotEmpty(postInfos.getData(0).getString("POST_ADR"))&&StringUtils.isEmpty(postAdr)){  //用户取消了邮箱
				 ModifyEPostInfoBean.delUnMonPost(serialNumber);
				 invoiceInfo.put("JKLX", "2");
				 invoiceInfo.put("TSRQ", "");
			 }
		 }else{  //如果没有数据
			//如果邮箱值不为空
			 if(StringUtils.isNotBlank(postAdr)&&!StringUtils.equals(serialNumber+"@139.com", postAdr)){
				 if(IDataUtil.isNotEmpty(ModifyEPostInfoBean.qryEPostInfoByMon(monPara))){  //如果有无效数据
					 ModifyEPostInfoBean.upUnMonPost(tabdata, postAdr);
					 customerInfo.put("KHYXDZ", postAdr);
					 invoiceInfo.put("JKLX", "1");
					 invoiceInfo.put("TSRQ", "");
				 }else if(IDataUtil.isEmpty(ModifyEPostInfoBean.qryEPostInfoByMon(monPara))){  //若无有效数据
					 //ModifyEPostInfoBean.addUnMonPost(tabdata, postAdr); ???为什么新增无效数据
					 customerInfo.put("KHYXDZ", postAdr);
					 invoiceInfo.put("JKLX", "1");
					 invoiceInfo.put("TSRQ", "");
				 }
			 }
		 }
		 // json信息Map
         Map<String, Map<String, Object>> requestJsonMap = new HashMap<String, Map<String, Object>>();
         requestJsonMap.put("DZFPYJTSPZ_KHXX", customerInfo);
         requestJsonMap.put("DZFPYJTSPZ_PZXX", invoiceInfo);
         
         JSONObject jSONObject = JSONObject.fromObject(requestJsonMap);
         String contentJson = jSONObject.toString();
         
		 IData ibossData = new DataMap();
		 ibossData.put("CONTENT_XML", contentJson);
		 ibossData.put("KIND_ID", "EPostMail_BOSS_0_0");
		 //调用一级BOSS接口同步数据
		 if((invoiceInfo.get("JKLX"))!=null){
			 //return IBossCall.callHttpJIBOSS("IBOSS", ibossData);
			 return IBossCall.callHttpKIBOSS("IBOSS", ibossData);
		 }
		 return null;
	 }
	 
	 public static IDataset dealOtherIBossParam(Map<String,Object> customerInfo,Map<String,Object> invoiceInfo,IDataset postInfos,String postChannel,String postTypeMon,String postDate,String serialNumber,String userId) throws Exception 
     {
		 customerInfo.put("KHQQSJ", System.currentTimeMillis());
		 invoiceInfo.put("YWLSH", "DZF_"+invoiceInfo.get("SFBS")+"_"+serialNumber+"_"+userId+"_*_"+customerInfo.get("KHQQSJ"));
		 
		 if("".equals(postDate))
		 {
			 postDate = "15";
		 }
		 
		 boolean hasMon = false;
		 if(IDataUtil.isNotEmpty(postInfos)){
			 for(int i=0;i<postInfos.size();i++){
				 if("0".equals(postInfos.getData(i).getString("POST_TAG"))){  //以前有月结
					 hasMon = true;
					//以前有邮箱推送方式
					 if("1".equals(postInfos.getData(i).getString("POST_CHANNEL"))||"2".equals(postInfos.getData(i).getString("POST_CHANNEL"))){
						 if("0".equals(postChannel)){   //现在没有邮箱推送
							 invoiceInfo.put("JKLX", "4");
						 }
						 if("1".equals(postChannel)||"2".equals(postChannel)){   //现在也有邮箱，但是日期发生了变化
							 if(StringUtils.isNotBlank(postDate)&&!StringUtils.equals(postDate, postInfos.getData(i).getString("POST_DATE"))){
								 invoiceInfo.put("JKLX", "3");
								 invoiceInfo.put("TSRQ", postDate);
							 }
						 }
						 if(StringUtils.isBlank(postTypeMon)){   //现在没有月结
							 invoiceInfo.put("JKLX", "4");
						 }
					 }else{  //以前没有邮箱推送方式
						 if("1".equals(postChannel)||"2".equals(postChannel)){  //现在有邮箱
							 invoiceInfo.put("JKLX", "3");
							 invoiceInfo.put("TSRQ", postDate);
						 }
					 }
				 }
			 }
			 if(!hasMon){  //若之前没有月结
				 if(StringUtils.isNotBlank(postTypeMon)){  //现在有月结，且有邮箱
					 if("1".equals(postChannel)||"2".equals(postChannel)){  //现在有邮箱
						 invoiceInfo.put("JKLX", "3");
						 invoiceInfo.put("TSRQ", postDate);
					 }
				 }
			 }
		 }else{  //若之前无数据
			 if(StringUtils.isNotBlank(postTypeMon)){  //现在有月结，且有邮箱
				 if("1".equals(postChannel)||"2".equals(postChannel)){  //现在有邮箱
					 invoiceInfo.put("JKLX", "3");
					 invoiceInfo.put("TSRQ", postDate);
				 }
			 }
		 }
		 // json信息Map
         Map<String, Map<String, Object>> requestJsonMap = new HashMap<String, Map<String, Object>>();
         requestJsonMap.put("DZFPYJTSPZ_KHXX", customerInfo);
         requestJsonMap.put("DZFPYJTSPZ_PZXX", invoiceInfo);
         
         JSONObject jSONObject = JSONObject.fromObject(requestJsonMap);
         String contentJson = jSONObject.toString();
         
		 IData ibossData = new DataMap();
		 ibossData.put("CONTENT_XML", contentJson);
		 ibossData.put("KIND_ID", "EPostMail_BOSS_0_0");
		 //调用一级BOSS接口同步数据
		 if((invoiceInfo.get("JKLX"))!=null){
//			 return IBossCall.callHttpJIBOSS("IBOSS", ibossData);
			 return IBossCall.callHttpKIBOSS("IBOSS", ibossData);

		 }
		 return null;
	 }
	 
	 public static void clearParam(IData accparam) throws Exception 
     {
		 accparam.remove("PUSH_CHANNEL");
		 accparam.remove("SMS_NUMBER");
		 accparam.remove("EMAIL_NUMBER");
		 accparam.remove("EMAIL_NUMBER_SECOND");
		 accparam.remove("PUSH_FLAG");
		 accparam.remove("PUSH_DATE");
		 accparam.remove("FLAG"); 
	 }
	 

	 

}
