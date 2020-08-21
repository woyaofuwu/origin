package com.asiainfo.veris.crm.order.soa.person.common.action.trade.cc;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CCCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;

/**
 * 1： 新增/修改经办人，责任人，使用人信息的时候，新增或修改参与人信息接口
   2： 新增/修改个人客户信息时，新增或修改参与人信息接口 。
 * @author huanghua
 *
 */
public class ModifyIndividualCustomerAction implements ITradeAction 
{
	protected static Logger log = Logger.getLogger(ModifyIndividualCustomerAction.class);
	 
	public void executeAction(BusiTradeData btd) throws Exception
    {
        List<CustPersonTradeData> custPersonTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_CUST_PERSON);
        List<CustomerTradeData> customerTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_CUSTOMER);
        IData param = new DataMap();
        String isRealName = "";
        String custId = "";
        
        if (custPersonTradeDatas != null && custPersonTradeDatas.size() > 0){
        	for (CustPersonTradeData custPersonTradeData : custPersonTradeDatas){
        		custId = custPersonTradeData.getCustId();
        		param.put("CUST_ID", custId);
        		param.put("CUST_NAME", custPersonTradeData.getCustName());
                param.put("PSPT_TYPE_CODE", custPersonTradeData.getPsptTypeCode());
                param.put("PSPT_ID", custPersonTradeData.getPsptId());
                param.put("PSPT_ADDR", custPersonTradeData.getPsptAddr());
                param.put("PSPT_END_DATE", custPersonTradeData.getPsptEndDate());
                param.put("SEX", custPersonTradeData.getSex());
                param.put("BIRTHDAY", custPersonTradeData.getBirthday());
                param.put("BIRTHDAY_LUNAR", custPersonTradeData.getBirthdayLunar());
                param.put("IS_RELA_NAME", isRealName);
                param.put("PHONE", custPersonTradeData.getPhone());
                param.put("POST_ADDRESS", custPersonTradeData.getPostAddress());
                param.put("MGMT_COUNTY", custPersonTradeData.getCityCode());
                param.put("CONTACT", custPersonTradeData.getContact());
                param.put("CONTACT_PHONE", custPersonTradeData.getContactPhone());
                param.put("WORK_NAME", custPersonTradeData.getWorkName());
                param.put("WORK_DEPART", custPersonTradeData.getWorkDepart());
                param.put("HOME_ADDRESS", custPersonTradeData.getHomeAddress());
                param.put("EMAIL", custPersonTradeData.getEmail());
                param.put("FAX_NBR", custPersonTradeData.getFaxNbr());
                param.put("CONTACT_TYPE_CODE", custPersonTradeData.getContactTypeCode());
                param.put("JOB", custPersonTradeData.getJob());
                param.put("PROFESSION", custPersonTradeData.getCallingTypeCode());//与小明哥确认
                param.put("MARRIAGE", custPersonTradeData.getMarriage());
                param.put("EDUCATE_GRADE_CODE", custPersonTradeData.getEducateGradeCode());
                param.put("NATIONALITY_CODE", custPersonTradeData.getNationalityCode());
                param.put("LANGUAGE_CODE", custPersonTradeData.getLanguageCode());
                param.put("FOLK_CODE", custPersonTradeData.getFolkCode());
                param.put("RELIGION_CODE", custPersonTradeData.getReligionCode());
                //PARTY_ROLE_SPEC_ID 1002：经办人；1003：使用人；1004：责任人
                //在客户核心资料表中取是否实名制字段值,更新经办人信息
        		if (customerTradeDatas != null && customerTradeDatas.size() > 0){
        			for (CustomerTradeData customerTradeData : customerTradeDatas){
        				if(custId.equals(customerTradeData.getCustId())){
        					isRealName = customerTradeData.getIsRealName();
        				}
        				//经办人信息-客户资料台账的预留字段78910
        				String operName = customerTradeData.getRsrvStr7();
        				String operPsptTypeCode = customerTradeData.getRsrvStr8();
        				String operPsptId = customerTradeData.getRsrvStr9();
        				String operPsptAddr = customerTradeData.getRsrvStr10();
        				IData operParam = new DataMap();
        				if(StringUtils.isNotEmpty(operName) && 
                    			StringUtils.isNotEmpty(operPsptTypeCode) && 
                    					StringUtils.isNotEmpty(operPsptId) && 
                    							StringUtils.isNotEmpty(operPsptAddr)){
        					operParam.put("PARTY_ROLE_SPEC_ID", "1002");
        					operParam.put("CUST_ID", custId);
        					operParam.put("CUST_NAME", operName);
        					operParam.put("PSPT_TYPE_CODE", operPsptTypeCode);
        					operParam.put("PSPT_ID", operPsptId);
        					operParam.put("PSPT_ADDR", operPsptAddr);
                    		CCCall.modifyAttnUserInfo(operParam);
                    	}
        			}
        		}
                //新增/修改个人客户信息时，调新增或修改参与人信息接口 
                CCCall.modifyIndividualCustomer(param);
                //将字段清空
                isRealName = "";
                
                IDataset list = CustPersonInfoQry.qryCustPersonOtherByCustId(custId);
                if( IDataUtil.isNotEmpty(list) )
        		{
                	IData custPersonOtherData = list.first();
                	//使用人信息-个人客户台账的预留字段5678-同时在TF_F_CUST_PERSON_OTHER有对应字段
                	String useName = custPersonOtherData.getString("USE_NAME");
                	String usePsptTypeCode = custPersonOtherData.getString("USE_PSPT_TYPE_CODE");
                	String usePsptId = custPersonOtherData.getString("USE_PSPT_ID");
                	String usePsptAddr = custPersonOtherData.getString("USE_PSPT_ADDR");
                	//责任人信息-TF_F_CUST_PERSON_OTHER的预留字段2345
                	String respoName = custPersonOtherData.getString("RSRV_STR2");
                	String respoPsptTypeCode = custPersonOtherData.getString("RSRV_STR3");
                	String respoPsptId = custPersonOtherData.getString("RSRV_STR4");
                	String respoPsptAddr = custPersonOtherData.getString("RSRV_STR5");
                	
                	IData useParam = new DataMap();
                	IData responParam = new DataMap();
                	//更新使用人信息
                	if(StringUtils.isNotEmpty(useName) && 
                			StringUtils.isNotEmpty(usePsptTypeCode) && 
                					StringUtils.isNotEmpty(usePsptId) && 
                							StringUtils.isNotEmpty(usePsptAddr)){
                		useParam.put("PARTY_ROLE_SPEC_ID", "1003");
                		useParam.put("CUST_ID", custId);
                		useParam.put("CUST_NAME", useName);
                		useParam.put("PSPT_TYPE_CODE", usePsptTypeCode);
                		useParam.put("PSPT_ID", usePsptId);
                		useParam.put("PSPT_ADDR", usePsptAddr);
                		CCCall.modifyAttnUserInfo(useParam);
                	}
                	//更新责任人信息
                	if(StringUtils.isNotEmpty(respoName) && 
                			StringUtils.isNotEmpty(respoPsptTypeCode) && 
                					StringUtils.isNotEmpty(respoPsptId) && 
                							StringUtils.isNotEmpty(respoPsptAddr)){
                		useParam.put("PARTY_ROLE_SPEC_ID", "1004");
                		useParam.put("CUST_ID", custId);
                		useParam.put("CUST_NAME", respoName);
                		useParam.put("PSPT_TYPE_CODE", respoPsptTypeCode);
                		useParam.put("PSPT_ID", respoPsptId);
                		useParam.put("PSPT_ADDR", respoPsptAddr);
                		CCCall.modifyAttnUserInfo(responParam);
                	}
                    
        		}
                
        	}
        }
        
        //当只有客户资料台账表中有数据时，只同步经办人信息
        else if (customerTradeDatas != null && customerTradeDatas.size() > 0){
			for (CustomerTradeData customerTradeData : customerTradeDatas){
				String operName = customerTradeData.getRsrvStr7();
				String operPsptTypeCode = customerTradeData.getRsrvStr8();
				String operPsptId = customerTradeData.getRsrvStr9();
				String operPsptAddr = customerTradeData.getRsrvStr10();
				IData operParam = new DataMap();
				if(StringUtils.isNotEmpty(operName) && 
            			StringUtils.isNotEmpty(operPsptTypeCode) && 
            					StringUtils.isNotEmpty(operPsptId) && 
            							StringUtils.isNotEmpty(operPsptAddr)){
					operParam.put("PARTY_ROLE_SPEC_ID", "1002");
					operParam.put("CUST_ID", customerTradeData.getCustId());
					operParam.put("CUST_NAME", operName);
					operParam.put("PSPT_TYPE_CODE", operPsptTypeCode);
					operParam.put("PSPT_ID", operPsptId);
					operParam.put("PSPT_ADDR", operPsptAddr);
            		CCCall.modifyAttnUserInfo(operParam);
            	}
			}
		}
    }
}
