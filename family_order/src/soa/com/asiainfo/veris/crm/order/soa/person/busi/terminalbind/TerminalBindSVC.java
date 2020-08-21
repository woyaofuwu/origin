package com.asiainfo.veris.crm.order.soa.person.busi.terminalbind;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService; 
 
public class TerminalBindSVC  extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8436008007830978415L;
	protected static Logger log = Logger.getLogger(TerminalBindSVC.class);
	 
	
	
	/**
     * 校验分配优惠券手机号是否正常
     */
    public IData checkSn(IData input) throws Exception{
//    	IData rtnData=new DataMap();
//    	CouponsTradeBean bean = BeanManager.createBean(CouponsTradeBean.class);
//        IDataset userInfo = bean.checkSn(input);
//        if(userInfo!=null && userInfo.size()>0){
//        	rtnData.put("USER_FLAG", "1");//用户状态 1正常用户
//        	rtnData.put("USER_MSG", ""); 
//        }else{
//        	rtnData.put("USER_FLAG", "0");//用户状态 0非正常用户 1正常用户
//        	rtnData.put("USER_MSG", "非正常用户，无法办理。");
//        }
//        return rtnData; 
    	return null;
    }
    
    
    
    public IData checkOpenDate(IData input)throws Exception{ 
    	IData rtnData=new DataMap();
    	TerminalBindBean bean = BeanManager.createBean(TerminalBindBean.class);
        IData userInfo = bean.checkOpenDate(input ); 
    	return userInfo;
    }
    
    public IData checkSellDay(IData input)throws Exception{ 
    	IData rtnData=new DataMap();
    	TerminalBindBean bean = BeanManager.createBean(TerminalBindBean.class);
        IData userInfo = bean.checkSellDay(input ); 
    	return userInfo;
    }
    
    public IData checkHaveBound(IData input)throws Exception{ 
    	IData rtnData=new DataMap();
    	TerminalBindBean bean = BeanManager.createBean(TerminalBindBean.class);
        IData userInfo = bean.checkHaveBound(input ); 
    	return userInfo;
    }
    
    
    public IData checkTerminalStaff(IData input)throws Exception{ 
    	IData rtnData=new DataMap();
    	TerminalBindBean bean = BeanManager.createBean(TerminalBindBean.class);
        IData userInfo = bean.checkTerminalStaff(input ); 
    	return userInfo;
    }
    
    public IData checkTerminalStaffForWeb(IData input)throws Exception{ 
    	TerminalBindBean bean = BeanManager.createBean(TerminalBindBean.class);
        IData userInfo = bean.checkTerminalStaffForWeb(input ); 
    	return userInfo;
    }
    
    public static IData insertTerminalBind(IData input) throws Exception{
     
    	IData rtnData=new DataMap();
    	TerminalBindBean bean = BeanManager.createBean(TerminalBindBean.class);
    	IData idata= bean.insertTerminalBind(input );  
        return idata; 
    }
    
    public  IDataset queryTerminalBind(IData input) throws Exception{
        
    	TerminalBindBean bean = BeanManager.createBean(TerminalBindBean.class);
    	IDataset idata= bean.queryTerminalBind(input,getPagination() );  
        return idata; 
    }
    
    public static void updateTerminal(IData input) throws Exception{
    	TerminalBindBean bean = BeanManager.createBean(TerminalBindBean.class);
    	IDataset idata= bean.updateTerminal(input );  
    }
    public  IDataset queryTerminalBindForCreate(IData input) throws Exception{
        
    	TerminalBindBean bean = BeanManager.createBean(TerminalBindBean.class);
    	IDataset idata= bean.queryTerminalBindBySerialnumber(input );  
        return idata; 
    }
	 
}
