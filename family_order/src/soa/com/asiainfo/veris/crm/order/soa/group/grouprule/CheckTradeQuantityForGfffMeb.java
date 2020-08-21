
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.group.cenpayspecial.CenpayBlackMgrQry;
import com.asiainfo.veris.crm.order.soa.group.cenpayspecial.CenpayGfffUserLogQry;
import com.asiainfo.veris.crm.order.soa.group.cenpayspecial.CenpaySpecialMgrQry;



public class CheckTradeQuantityForGfffMeb extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    /**
     * 规则
     * 流量自由充集团办理量等的校验
     */
    private static Logger logger = Logger.getLogger(CheckTradeQuantityForGfffMeb.class);
    
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckTradeQuantityForGfffMeb()  >>>>>>>>>>>>>>>>>>");
        }
        
        String grpUserId = databus.getString("USER_ID"); //集团用户ID
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String acceptTime = SysDateMgr.getSysTime();
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" CheckTradeQuantityForGfffMeb() grpUserId=" + grpUserId);
        }
        
        if(StringUtils.isNotBlank(grpUserId))
        {
        	
    		//自由充黑名单的拦截
    		IData blackData = new DataMap();
    		blackData.put("USER_ID", grpUserId);
    		blackData.put("BLACK_TAG", "0");
    		IDataset blackDatasets = CenpayBlackMgrQry.queryCenpayBlackByUserId(blackData);
    		if(IDataUtil.isNotEmpty(blackDatasets))
    		{
    			String errInfo = "该集团产品用户[" + grpUserId + "]是黑名单用户,不允许办理该业务!";
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, errInfo);
                return false;
    		}
    		
    		//自由充特权用户不需要校验下面的规则
    		blackData.clear();
    		blackData.put("USER_ID", grpUserId);
    		blackData.put("REMOVE_TAG", "0");
    		IDataset datasets = CenpaySpecialMgrQry.queryCenpaySpecialByUserId(blackData);
    		if(IDataUtil.isNotEmpty(datasets))
    		{
    			//自由充特权用户不需要校验下面的规则
                return true;
    		}
    		
    		//获取业务量的配置
    		IDataset quantityInfos = CommparaInfoQry.getCommPkInfo("CSM", "7347", 
            		"GRP_GFFF_QUANTITY","0898");
    		double houreQuantity = 200;//默认值,每小时的量,para_code1
    		double houreFee = 1000000;//默认值,每小时的总额(单位分),para_code2
    		double dayQuantity = 3000;//默认值,每天的量,para_code3
    		double dayFee = 10000000;//默认值,每天的总额(单位分),para_code4
    		
    		if(IDataUtil.isNotEmpty(quantityInfos))
    		{
    			IData quantInfo = quantityInfos.getData(0);
    			if(IDataUtil.isNotEmpty(quantInfo))
    			{
    				houreQuantity = Double.parseDouble(quantInfo.getString("PARA_CODE1","200"));
    				houreFee = Double.parseDouble(quantInfo.getString("PARA_CODE2","1000000"));
    				dayQuantity = Double.parseDouble(quantInfo.getString("PARA_CODE3","3000"));
    				dayFee = Double.parseDouble(quantInfo.getString("PARA_CODE4","10000000"));
    			}
    		}
    		
    		//以下是业务量的拦截判断
    		IData paramData = new DataMap();
    		paramData.put("USER_ID", grpUserId);
    		paramData.put("ACCEPT_DATE", acceptTime);
    		
    		//判断每小时记录
    		IDataset hourSets = CenpayGfffUserLogQry.queryGfffHourLogByUserId(paramData);
    		if(IDataUtil.isNotEmpty(hourSets))
    		{
    			IData hourData = hourSets.getData(0);
    			String recordNum = hourData.getString("RECORD_NUM","0");//记录量
				String consumeFee = hourData.getString("CONSUME_FEE","0");//总额
    			
				double doubleRecordNum = Double.parseDouble(new BigDecimal(recordNum).toString());//数量
				double doubleConsumeFee = Double.parseDouble(new BigDecimal(consumeFee).toString());//总额
				
				if(doubleRecordNum >= houreQuantity || doubleConsumeFee >= houreFee)
				{
					String errInfo = "该集团产品[" + grpUserId + "]办理的业务量已经达到每小时规定的阀值,不允许再办理!";
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, errInfo);
	                return false;
				}
    		}
    		
    		//判断当天记录
    		IDataset daySets = CenpayGfffUserLogQry.queryGfffDayLogByUserId(paramData);
    		if(IDataUtil.isNotEmpty(daySets))
    		{
    			IData daySet = daySets.getData(0);
    			String recordNum = daySet.getString("RECORD_NUM","0");//记录量
				String consumeFee = daySet.getString("CONSUME_FEE","0");//总额
    			
				double doubleRecordNum = Double.parseDouble(new BigDecimal(recordNum).toString());//数量
				double doubleConsumeFee = Double.parseDouble(new BigDecimal(consumeFee).toString());//总额
				
				if(doubleRecordNum >= dayQuantity || doubleConsumeFee >= dayFee)
				{
					String errInfo = "该集团产品[" + grpUserId + "]办理的业务量已经达到一天规定的阀值,不允许再办理!";
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, errInfo);
	                return false;
				}
    		}
    		
    		//crm当月的总额与账务预存款的比较
    		double doubleConsumeFee = 0;//营业侧记录的总额
    		IDataset monthSets = CenpayGfffUserLogQry.queryGfffMonthLogByUserId(paramData);
    		if(IDataUtil.isNotEmpty(monthSets))
    		{
    			IData monthSet = monthSets.getData(0);
				String consumeFee = monthSet.getString("CONSUME_FEE","0");//总额
    			
				//营业侧的总额
				doubleConsumeFee = Double.parseDouble(new BigDecimal(consumeFee).toString());
    		}
    		
    		//用户的存折数据
    		IDataset acctOutputs = null;
			IDataset creditDepositInfos = null;
            try{
            	//获取账务的预存款
            	acctOutputs = AcctCall.qryGroupAcctRealFee(grpUserId);
            	if (logger.isDebugEnabled())
                {
                    logger.debug("<<<<<<<<<<账务侧的预存款信息>>>>>>>>>>" + acctOutputs);
                }
            	if(IDataUtil.isNotEmpty(acctOutputs))
            	{
            		IData creditDepositInfo = acctOutputs.getData(0);
            		if(IDataUtil.isNotEmpty(creditDepositInfo))
            		{
            			creditDepositInfos = creditDepositInfo.getDataset("DEPOSIT_INFOS");
            		}
            	}
            	
            }
            catch(Exception e)
            {
            	if(logger.isDebugEnabled())
                {
                    logger.info(e);
                }

                if(logger.isInfoEnabled())
                {
                    logger.info("查询集团用户的预存款" + e);
                }
                
                String err = "";
                err = e.getMessage();                
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode + "-997", err);
                return false;
            }
            
            //查询集团产品的信用度
            IDataset creditInfos = null;
            //信用度值
            String creditValue = "0";
            try
            {
            	creditInfos = AcctCall.getUserCreditInfo(grpUserId);
            }
            catch(Exception e)
            {
            	if(logger.isDebugEnabled())
            	{
            		logger.info(e);
            	}

            	if(logger.isInfoEnabled())
            	{
            		logger.info("查询集团产品的信用度错误信息=" + e);
            	}
              
            	String err = "";
            	err = e.getMessage();
            	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode + "-998", err);
            	return false;
            }
          
            if (IDataUtil.isNotEmpty(creditInfos))
            {
            	creditValue = creditInfos.getData(0).getString("CREDIT_VALUE","0");
            }
            double creditValueD = 0;
            creditValueD = Double.parseDouble(creditValue);
            
            //判断从账务获取过来的存折信息等
            double creditBalance = 0;//存折里预存款
            if(IDataUtil.isNotEmpty(creditDepositInfos))
        	{
        		//获取配置的存折编码
        		IDataset commInfos = CommparaInfoQry.getCommPkInfo("CSM", "7347", 
                		"GRP_GFFF_EPOSIT_CODE","0898");
        		
        		if(IDataUtil.isNotEmpty(commInfos))
        		{
        			for(int j=0; j < commInfos.size(); j++)//循环配置存折
        			{
        				IData commInfo = commInfos.getData(j);
        				String commEpositCode = commInfo.getString("PARA_CODE1");
        				if(StringUtils.isNotBlank(commEpositCode))
        				{
        					for(int i=0; i < creditDepositInfos.size(); i++)
        	                {
        	        			IData creDataInfo = creditDepositInfos.getData(i);
        	        			String balance = creDataInfo.getString("DEPOSIT_BALANCE","0");
        	        			String depositCode = creDataInfo.getString("DEPOSIT_CODE","");
        	        			if(StringUtils.equals(commEpositCode, depositCode))
        	        			{
        	        				creditBalance = creditBalance + Double.parseDouble(balance);
        	        			}
        	                }
        				}
        			}
        		}
        	}
//            else 
//            {
//            	String errInfo = "获取不到该集团用户[" + grpUserId + "]预存款,请核实!";
//				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, errInfo);
//                return false;
//            }
            
            if(logger.isDebugEnabled())
            {
    			logger.debug("<<<<<<<<<<CRM侧总额>>>>>>>>>>doubleConsumeFee=" + doubleConsumeFee);
    			logger.debug("<<<<<<<<<<账务侧预存款>>>>>>>>>>creditBalance=" + creditBalance);
    			logger.debug("<<<<<<<<<<集团用户的信用度>>>>>>>>>>creditValueD=" + creditValueD);
            }
            
            if(doubleConsumeFee > creditBalance + creditValueD -200)
    		{
    			String errInfo = "该集团用户[" + grpUserId + "]预存款已经不足,不允许办理该业务!";
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, errInfo);
                return false;
    		}
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckTradeQuantityForGfffMeb() <<<<<<<<<<<<<<<<<<<");
        }
        
        return true;
    }

}
