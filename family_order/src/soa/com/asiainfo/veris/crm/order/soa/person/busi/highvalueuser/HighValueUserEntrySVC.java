package com.asiainfo.veris.crm.order.soa.person.busi.highvalueuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
 
public class HighValueUserEntrySVC  extends CSBizService{

	private static final long serialVersionUID = 8436008007830978415L;
	protected static Logger log = Logger.getLogger(HighValueUserEntrySVC.class);
	
	//插入新纪录
    public static IData insertHighUser(IData input) throws Exception{
     
    	HighValueUserEntryBean bean = BeanManager.createBean(HighValueUserEntryBean.class);
    	//校验是否为高价值客户
    	if(!bean.checkHighValue(input)){    		
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户不是高价值客户!" );
    	}
    	//校验新入网号码是否为开户时间一月内的新号码
    	if(!bean.checkNewNumber(input)){    		
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该新入网号码不是新开户一月内的号码!" );
    	}
    	//校验是否已有该记录
    	if(!bean.checkNumberBEmpty(input)){    		
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该异网号码已被登记过,登记失败!" );
    	}
    	if(!bean.checkNumberEmpty(input)){    		
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该新入网号码已被登记过,登记失败!" );
    	}    		  	
    	IData idata= bean.insertHighUser(input);  
        return idata; 
    }
    
    //更新已有记录
    public static int updateHighUser(IData input) throws Exception{
    	
    	input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER_NEW"));
    	input.put("SERIAL_NUMBER_B", input.getString("SERIAL_NUMBER_B_NEW"));
    	if(!getVisit().getStaffId().equals(input.getString("TRADE_STAFF_ID")))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"修改失败!当前登录工号非录入该数据的工号!" );
    	}
    	if(SysDateMgr.monthInterval(input.getString("IN_DATE"), SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD)) > 1)
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"修改失败!只能修改本月内登记的信息!" );
    	}   	
    	HighValueUserEntryBean bean = BeanManager.createBean(HighValueUserEntryBean.class);
    	//当数据有修改时,校验修改后的数据是否已存在
     	if(!input.getString("SERIAL_NUMBER_B_NEW").equals(input.getString("SERIAL_NUMBER_B_OLD"))){ 
    		if(!bean.checkNumberBEmpty(input)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"该异网号码已存在,修改失败!" );
    		}
    	}
    	if(!input.getString("SERIAL_NUMBER_NEW").equals(input.getString("SERIAL_NUMBER_OLD"))){ 
	    	if(!bean.checkNumberEmpty(input)){    		
	    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该新入网号码已存在,修改失败!" );
	    	}   
    	}
    	//校验新入网号码是否为开户时间一月内的新号码
    	if(!bean.checkNewNumber(input)){    		
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该新入网号码不是新开户一月内的号码!" );
    	}
   	  	int updateRes = bean.updateHighUser(input);  
        return updateRes; 
    }
    
    //查询已有记录
    public  IDataset queryHighValueUser(IData input) throws Exception{
        
    	HighValueUserEntryBean bean = BeanManager.createBean(HighValueUserEntryBean.class);
    	IDataset idata= bean.queryHighValueUser(input,getPagination());  
        return idata; 
    }
    	 
}
