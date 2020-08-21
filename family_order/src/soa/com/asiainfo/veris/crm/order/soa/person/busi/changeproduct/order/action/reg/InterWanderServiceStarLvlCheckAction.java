package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

import java.util.List;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst; 
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

public class InterWanderServiceStarLvlCheckAction implements ITradeAction{
	
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		String inModeCode = CSBizBean.getVisit().getInModeCode();
		
		//验证所有的外围渠道
        if (!"0".equals(inModeCode)&&!"1".equals(inModeCode))
        {
        	//REQ201410240002新的星级服务体系下，优化国际漫游业务开通门槛          	
        	UcaData uca = btd.getRD().getUca();  
        	List<SvcTradeData> svcDatas15 = uca.getUserSvcBySvcId(PersonConst.SERVICE_ID_15);
        	
        	//判断是否有做国际漫游的添加操作
        	boolean isHaveAddService15=false;
        	for (SvcTradeData svcData : svcDatas15)
            {
                String modifyTag = svcData.getModifyTag();
                 
                if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                {
                	isHaveAddService15=true;
                }
             
            }
        	if(isHaveAddService15){
        		String strCreditClass = uca.getUserCreditClass();
            	String strAcctBlance = uca.getAcctBlance();
            	int iCreditClass = Integer.parseInt(strCreditClass);        //星级
                int iAcctBlance = Integer.parseInt(strAcctBlance)/100;          //话费实时余额
                
            	// 1.星级客户免预存开通国漫功能。
                 // 2.非星级客户（准星、未评级客户）账户可用余额不低于200元可申请开通国漫功能。
            	if(-1 == iCreditClass || 0 == iCreditClass)    
                {
                    if(iAcctBlance < 200)
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_512,"200");
                    }
                }
        	}
        	/*       	
        	 // （一）三星及以上客户免预存开通国际漫游业务，不再要求客户交纳押金。  
			 // （二）一星、二星客户的开通条件为500元预存额度（预付费客户为账户余额），不再要求客户交纳押金。  
			 // （三）准星、未评级客户开通条件为1000元预存额度（预付费客户为账户余额），不再要求客户交纳押金。  
        	 
        	//如果存在订购，就验证客户的星级信息
        	if(isHaveAddService15){
        		//判断办理工号是否有免押金的权限
                String staffId = CSBizBean.getVisit().getStaffId();
        		if(StaffPrivUtil.isFuncDataPriv(staffId, "SYSFOREGIFT")){	//如果有免押金的权限，直接通过
        			return ;
        		}
        		
        		
        		
        		 // 判断用户的押金是否达到要求
        		 
        		boolean isForegiftValid=false;
        		
        		IDataset userForegift=UserForegiftInfoQry.getUserForegift(userId, "3");
        		if(IDataUtil.isNotEmpty(userForegift)){
        			int foregifeMoney=userForegift.getData(0).getInt("MONEY",0);
        			if(foregifeMoney>=80000){
        				isForegiftValid=true;
        			}
        		}
        		
        		
        		 // 如果押金不满足，就验证用户的预存款是否达到了用户的星级要求
        		 
        		if(!isForegiftValid){
        			String strCreditClass = uca.getUserCreditClass();
                	String strAcctBlance = uca.getAcctBlance();
                	int iCreditClass = Integer.parseInt(strCreditClass);		//星级
                	int iAcctBlance = Integer.parseInt(strAcctBlance)/100;			//话费实时余额
                	
                	//三星及以上客户直接通过
                	if(iCreditClass>2){
                		return ;
                	}
                	
                	if(-1 == iCreditClass || 0 == iCreditClass)	
                	{
                		if(iAcctBlance < 1000)
                		{
                			CSAppException.apperr(ProductException.CRM_PRODUCT_512,"1000");
                		}
                	}
                	else if (1 == iCreditClass || 2 == iCreditClass)
                	{
                		if(iAcctBlance < 500)
                		{
                			CSAppException.apperr(ProductException.CRM_PRODUCT_512,"500");
                		}
                	}
        		}
            	
        	}*/
        	
        }
	}
}
