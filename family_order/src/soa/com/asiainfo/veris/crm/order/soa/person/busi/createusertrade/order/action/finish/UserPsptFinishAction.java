
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;

/**
 * Copyright: Copyright 2016 Asiainfo
 * 
 * @ClassName: UserPsptFinishAction.java
 * @Description: 一证多名需求处理完工类
 * @version: v1.0.0
 * @author: yanwu
 * @date: 2016-12-27 Modification History: Date Author Version Description
 */
public class UserPsptFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String strTradeId = mainTrade.getString("TRADE_ID");
        String strUserid = mainTrade.getString("USER_ID");
        String strSerialNumber = mainTrade.getString("SERIAL_NUMBER");
        String strExecTime = mainTrade.getString("ACCEPT_DATE");
        String strTradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE", "");

        if("10".equals(strTradeTypeCode) || "40".equals(strTradeTypeCode) || "60".equals(strTradeTypeCode) || "100".equals(strTradeTypeCode))
        {
        	IDataset idsCustInfoPspt = CustomerInfoQry.getCustInfoPsptBySn(strSerialNumber);
            if(IDataUtil.isNotEmpty(idsCustInfoPspt)) 
            {
            	IData IdResult = new DataMap();
            	IData idCustInfoPspt = idsCustInfoPspt.first();
            	String strCustId = idCustInfoPspt.getString("CUST_ID", "");
            	
            	String strCustName = idCustInfoPspt.getString("AGENT_CUST_NAME", "");
            	String strPsptTypeCode = idCustInfoPspt.getString("AGENT_PSPT_TYPE_CODE", "");
            	String strPsptID = idCustInfoPspt.getString("AGENT_PSPT_ID", "");
            	String strPsptAddr = idCustInfoPspt.getString("AGENT_PSPT_ADDR", "");
            	if(StringUtils.isNotBlank(strPsptID) || 
            	   StringUtils.isNotBlank(strCustName) || 
            	   StringUtils.isNotBlank(strPsptTypeCode) || 
            	   StringUtils.isNotBlank(strPsptAddr))
            	{
            		IdResult.put(strPsptID, strCustName);
            		//IdResult.put("PSPT_ID", strPsptID);
            		//IdsResult.add(IdResult);
            		IDataset idsUserPspt1 = CustomerInfoQry.getUserPsptByUserid(strUserid, "1");
                	if(IDataUtil.isEmpty(idsUserPspt1))
                	{
                		IData idUserPspt = new DataMap();
                		idUserPspt.put("USER_TYPE", "1");
                		idUserPspt.put("CUST_ID", strCustId);
                		idUserPspt.put("USER_ID", strUserid);
                		idUserPspt.put("CUST_NAME", strCustName);
        	        	idUserPspt.put("PSPT_TYPE_CODE", strPsptTypeCode);
        	        	idUserPspt.put("PSPT_ID", strPsptID);
        	        	idUserPspt.put("INSERT_DATE", strExecTime);
        	        	idUserPspt.put("INSERT_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	        	idUserPspt.put("INSERT_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	        	idUserPspt.put("REMOVE_TAG", "0");
        	        	idUserPspt.put("REMARK", strTradeTypeCode);
        	        	idUserPspt.put("RSRV_STR1", strTradeId);
        	        	idUserPspt.put("RSRV_STR2", strPsptAddr);
        	        	idUserPspt.put("RSRV_STR3", "");
        	        	idUserPspt.put("RSRV_STR4", "");
        	        	idUserPspt.put("RSRV_STR5", "");
        				Dao.insert("TF_F_USER_PSPT", idUserPspt);
                	}
                	else
                	{
                		IData idUserPspt = idsUserPspt1.first();
                		idUserPspt.put("CUST_ID", strCustId);
                		idUserPspt.put("CUST_NAME", strCustName);
                		idUserPspt.put("PSPT_TYPE_CODE", strPsptTypeCode);
                		idUserPspt.put("PSPT_ID", strPsptID);
                		idUserPspt.put("RSRV_STR2", strPsptAddr);
                		idUserPspt.put("REMARK", strTradeTypeCode);
                		idUserPspt.put("RSRV_STR1", strTradeId);
        				Dao.update("TF_F_USER_PSPT", idUserPspt, new String[] { "USER_TYPE", "USER_ID" });
        			}
                	
                	/*IData inparam = new DataMap();
    		        inparam.put("PSPT_ID", strAgentPsptID);
    		        inparam.put("CUST_NAME", strAgentCustName);
    		        Dao.executeUpdateByCodeCode("TF_F_CUSTOMER", "UPD_USERPSPT_BY_PSPTID", inparam);*/
            	}
            	
            	IDataset idsCustPersonOthers = CustPersonInfoQry.qryCustPersonOtherByCustId(strCustId);
            	if(IDataUtil.isNotEmpty(idsCustPersonOthers))
            	{
            		IData idCustPersonOther = idsCustPersonOthers.first();
            		strCustName = idCustPersonOther.getString("RSRV_STR2", "");
                	strPsptTypeCode = idCustPersonOther.getString("RSRV_STR3", "");
                	strPsptID = idCustPersonOther.getString("RSRV_STR4", "");
                	strPsptAddr = idCustPersonOther.getString("RSRV_STR5", "");
                	if(StringUtils.isNotBlank(strPsptID) || 
                       StringUtils.isNotBlank(strCustName) || 
                       StringUtils.isNotBlank(strPsptTypeCode) || 
                       StringUtils.isNotBlank(strPsptAddr))
                	{
                		/*IData IdResult = new DataMap();
                		IdResult.put("CUST_NAME", strCustName);
                		IdResult.put("PSPT_ID", strPsptID);
                		IdsResult.add(IdResult);*/
                		IdResult.put(strPsptID, strCustName);
                		IDataset idsUserPspt3 = CustomerInfoQry.getUserPsptByUserid(strUserid, "3");
                    	if(IDataUtil.isEmpty(idsUserPspt3))
                    	{
                    		IData idUserPspt = new DataMap();
                    		idUserPspt.put("USER_TYPE", "3");
                    		idUserPspt.put("CUST_ID", strCustId);
                    		idUserPspt.put("USER_ID", strUserid);
                    		idUserPspt.put("CUST_NAME", strCustName);
            	        	idUserPspt.put("PSPT_TYPE_CODE", strPsptTypeCode);
            	        	idUserPspt.put("PSPT_ID", strPsptID);
            	        	idUserPspt.put("INSERT_DATE", strExecTime);
            	        	idUserPspt.put("INSERT_STAFF_ID", CSBizBean.getVisit().getStaffId());
            	        	idUserPspt.put("INSERT_DEPART_ID", CSBizBean.getVisit().getDepartId());
            	        	idUserPspt.put("REMOVE_TAG", "0");
            	        	idUserPspt.put("REMARK", strTradeTypeCode);
            	        	idUserPspt.put("RSRV_STR1", strTradeId);
            	        	idUserPspt.put("RSRV_STR2", strPsptAddr);
            	        	idUserPspt.put("RSRV_STR3", "");
            	        	idUserPspt.put("RSRV_STR4", "");
            	        	idUserPspt.put("RSRV_STR5", "");
            				Dao.insert("TF_F_USER_PSPT", idUserPspt);
                    	}
                    	else
                    	{
                    		IData idUserPspt = idsUserPspt3.first();
                    		idUserPspt.put("CUST_ID", strCustId);
                    		idUserPspt.put("CUST_NAME", strCustName);
                    		idUserPspt.put("PSPT_TYPE_CODE", strPsptTypeCode);
                    		idUserPspt.put("PSPT_ID", strPsptID);
                    		idUserPspt.put("RSRV_STR2", strPsptAddr);
                    		idUserPspt.put("REMARK", strTradeTypeCode);
                    		idUserPspt.put("RSRV_STR1", strTradeId);
            				Dao.update("TF_F_USER_PSPT", idUserPspt, new String[] { "USER_TYPE", "USER_ID" });
            			}
                    	
                    	/*IData inparam = new DataMap();
        		        inparam.put("PSPT_ID", strRsrvPsptID);
        		        inparam.put("CUST_NAME", strRsrvCustName);
        		        Dao.executeUpdateByCodeCode("TF_F_CUSTOMER", "UPD_USERPSPT_BY_PSPTID", inparam);*/
                	}
            	}
            	
            	strCustName = idCustInfoPspt.getString("USE_CUST_NAME", "");
            	strPsptTypeCode = idCustInfoPspt.getString("USE_PSPT_TYPE_CODE", "");
            	strPsptID = idCustInfoPspt.getString("USE_PSPT_ID", "");
            	strPsptAddr = idCustInfoPspt.getString("USE_PSPT_ADDR", "");
            	if(StringUtils.isNotBlank(strPsptID) || 
                   StringUtils.isNotBlank(strCustName) || 
                   StringUtils.isNotBlank(strPsptTypeCode) || 
                   StringUtils.isNotBlank(strPsptAddr))
            	{
            		IdResult.put(strPsptID, strCustName);
            		IDataset idsUserPspt2 = CustomerInfoQry.getUserPsptByUserid(strUserid, "2");
                	if(IDataUtil.isEmpty(idsUserPspt2))
                	{
                		IData idUserPspt = new DataMap();
                		idUserPspt.put("USER_TYPE", "2");
                		idUserPspt.put("CUST_ID", strCustId);
                		idUserPspt.put("USER_ID", strUserid);
                		idUserPspt.put("CUST_NAME", strCustName);
        	        	idUserPspt.put("PSPT_TYPE_CODE", strPsptTypeCode);
        	        	idUserPspt.put("PSPT_ID", strPsptID);
        	        	idUserPspt.put("INSERT_DATE", strExecTime);
        	        	idUserPspt.put("INSERT_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	        	idUserPspt.put("INSERT_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	        	idUserPspt.put("REMOVE_TAG", "0");
        	        	idUserPspt.put("REMARK", strTradeTypeCode);
        	        	idUserPspt.put("RSRV_STR1", strTradeId);
        	        	idUserPspt.put("RSRV_STR2", strPsptAddr);
        	        	idUserPspt.put("RSRV_STR3", "");
        	        	idUserPspt.put("RSRV_STR4", "");
        	        	idUserPspt.put("RSRV_STR5", "");
        				Dao.insert("TF_F_USER_PSPT", idUserPspt);
                	}
                	else
                	{
                		IData idUserPspt = idsUserPspt2.first();
                		idUserPspt.put("CUST_ID", strCustId);
                		idUserPspt.put("CUST_NAME", strCustName);
                		idUserPspt.put("PSPT_TYPE_CODE", strPsptTypeCode);
                		idUserPspt.put("PSPT_ID", strPsptID);
                		idUserPspt.put("RSRV_STR2", strPsptAddr);
                		idUserPspt.put("REMARK", strTradeTypeCode);
                		idUserPspt.put("RSRV_STR1", strTradeId);
        				Dao.update("TF_F_USER_PSPT", idUserPspt, new String[] { "USER_TYPE", "USER_ID" });
        			}
                	
                	/*IData inparam = new DataMap();
    		        inparam.put("PSPT_ID", strUsePsptID);
    		        inparam.put("CUST_NAME", strUseCustName);
    		        Dao.executeUpdateByCodeCode("TF_F_CUSTOMER", "UPD_USERPSPT_BY_PSPTID", inparam);*/
            	}
            	
            	if(IDataUtil.isNotEmpty(IdResult))
            	{
            		String []strsResult = IdResult.getNames();
            		for (int i = 0; i < strsResult.length; i++) 
            		{
            			/*IData IdResult = IdsResult.getData(i);
            			strCustName = IdResult.getString("CUST_NAME", "");
                    	strPsptID = IdResult.getString("PSPT_ID", "");*/
            			strPsptID = strsResult[i];
            			strCustName = IdResult.getString(strPsptID, "");
                    	
                    	IDataset idsUserPspt = CustomerInfoQry.getUserPsptByPsptID(strCustName, strPsptID);
                    	if(IDataUtil.isNotEmpty(idsUserPspt))
                    	{
                    		//String iv_sync_sequence = "";
                    		for (int ii = 0; ii < idsUserPspt.size(); ii++) 
                    		{
                    			IData idUserPspt = idsUserPspt.getData(ii);
                    			String strUserPsptCustID = idUserPspt.getString("CUST_ID", "");
                    			String strUserPsptUserTpye = idUserPspt.getString("USER_TYPE", "");
                    			idUserPspt.put("CUST_NAME", strCustName);
                    			Dao.update("TF_F_USER_PSPT", idUserPspt, new String[] { "USER_TYPE", "USER_ID" });
                    			
                    			if("1".equals(strUserPsptUserTpye))
                    			{
                    				IDataset idsCustomer = CustomerInfoQry.getCustomerByCustID(strUserPsptCustID);
                    				if(IDataUtil.isNotEmpty(idsCustomer))
                    				{
                    					IData idCustomer1 = new DataMap();
                    					idCustomer1.put("CUST_NAME", strCustName);
                    					idCustomer1.put("CUST_ID", strUserPsptCustID);
                    					Dao.executeUpdateByCodeCode("TF_F_CUSTOMER", "UPD_CUSTOMER_BY_CUSTID", idCustomer1);
                    					
                    				}
                    			}
                    			else if("2".equals(strUserPsptUserTpye))
                    			{
                    				IDataset idsCustPerson = CustomerInfoQry.getCustPersonByCustID(strUserPsptCustID);
                    				if(IDataUtil.isNotEmpty(idsCustPerson))
                    				{
                    					IData idCustPerson1 = new DataMap();
                    					idCustPerson1.put("CUST_NAME", strCustName);
                    					idCustPerson1.put("CUST_ID", strUserPsptCustID);
                    					Dao.executeUpdateByCodeCode("TF_F_CUSTOMER", "UPD_CUSTPERSON_BY_CUSTID", idCustPerson1);
                    					
                    					/*if(StringUtils.isBlank(iv_sync_sequence))
                    					{
                    						iv_sync_sequence = SeqMgr.getSyncIncreId(); 
                    						iv_sync_sequence = "99" + iv_sync_sequence.substring(2);
                    					}
                    					IData idCustPerson2 = new DataMap();
                    					idCustPerson2.put("SYNC_SEQUENCE", iv_sync_sequence);
                    					idCustPerson2.put("CUST_ID", strUserPsptCustID);
                    					idCustPerson2.put("TRADE_ID", strTradeId);
                    					Dao.executeUpdateByCodeCode("TF_F_CUSTOMER", "INS_TI_B_CUST_PERSON", idCustPerson2);*/
                    				}
                    				
                    				IDataset idsCustPersonOther1 = CustPersonInfoQry.qryCustPersonOtherByCustId(strUserPsptCustID);
                    				if(IDataUtil.isNotEmpty(idsCustPersonOther1))
                    				{
                    					IData idCustPersonOther1 = idsCustPersonOther1.first();
                    					idCustPersonOther1.put("USE_NAME", strCustName);
                    					Dao.update("TF_F_CUST_PERSON_OTHER", idCustPersonOther1, new String[] { "PARTITION_ID", "CUST_ID" });
                    				}
                    			}
                    			else if("3".equals(strUserPsptUserTpye))
                    			{
                    				IDataset idsCustPersonOther2 = CustPersonInfoQry.qryCustPersonOtherByCustId(strUserPsptCustID);
                    				if(IDataUtil.isNotEmpty(idsCustPersonOther2))
                    				{
                    					IData idCustPersonOther2 = idsCustPersonOther2.first();
                    					idCustPersonOther2.put("RSRV_STR2", strCustName);
                    					Dao.update("TF_F_CUST_PERSON_OTHER", idCustPersonOther2, new String[] { "PARTITION_ID", "CUST_ID" });
                    				}
                    			}
                    		}
                    		/*if(StringUtils.isNotBlank(iv_sync_sequence))
                			{
                				IData synchInfoData = new DataMap();
                				synchInfoData.put("SYNC_SEQUENCE", iv_sync_sequence);
                				String syncDay = StrUtil.getAcceptDayById(iv_sync_sequence);
                				synchInfoData.put("SYNC_DAY", syncDay);
                				synchInfoData.put("SYNC_TYPE", "0");
                				synchInfoData.put("TRADE_ID", strTradeId);
                				synchInfoData.put("STATE", "0");
                				synchInfoData.put("SYNC_TIME", SysDateMgr.getSysTime());
                				synchInfoData.put("UPDATE_TIME", SysDateMgr.getSysTime());
                				Dao.insert("TI_B_SYNCHINFO", synchInfoData);
                			}*/
                    	}
					}
            	}
            }
        }
        else if("192".equals(strTradeTypeCode) || "7240".equals(strTradeTypeCode))
        {
        	IDataset idsUserPspt1 = CustomerInfoQry.getUserPsptByUserid(strUserid, "1");
        	if(IDataUtil.isNotEmpty(idsUserPspt1))
        	{
        		IData idUserPspt = idsUserPspt1.first();
        		idUserPspt.put("REMOVE_TAG", "1");
        		idUserPspt.put("REMARK", strTradeTypeCode);
				Dao.update("TF_F_USER_PSPT", idUserPspt, new String[] { "USER_TYPE", "USER_ID" });
			}
        	IDataset idsUserPspt2 = CustomerInfoQry.getUserPsptByUserid(strUserid, "2");
        	if(IDataUtil.isNotEmpty(idsUserPspt2))
        	{
        		IData idUserPspt = idsUserPspt2.first();
        		idUserPspt.put("REMOVE_TAG", "1");
        		idUserPspt.put("REMARK", strTradeTypeCode);
				Dao.update("TF_F_USER_PSPT", idUserPspt, new String[] { "USER_TYPE", "USER_ID" });
			}
        	IDataset idsUserPspt3 = CustomerInfoQry.getUserPsptByUserid(strUserid, "3");
        	if(IDataUtil.isNotEmpty(idsUserPspt3))
        	{
        		IData idUserPspt = idsUserPspt3.first();
        		idUserPspt.put("REMOVE_TAG", "1");
        		idUserPspt.put("REMARK", strTradeTypeCode);
				Dao.update("TF_F_USER_PSPT", idUserPspt, new String[] { "USER_TYPE", "USER_ID" });
			}
        }
    }
}
