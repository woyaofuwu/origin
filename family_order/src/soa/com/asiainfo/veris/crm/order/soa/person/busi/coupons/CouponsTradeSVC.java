package com.asiainfo.veris.crm.order.soa.person.busi.coupons;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;  
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CouponsTradeSVC extends CSBizService
{
	/**
     * 录入优惠券用户信息
     * SS.CouponsTradeSVC.insertCouponsUser
     */
    public IDataset insertCouponsUser(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        CouponsTradeBean bean = BeanManager.createBean(CouponsTradeBean.class);
        IData data = bean.insertTicketInfo(input);
        returnSet.add(data);
        return returnSet;
    }
    
    /**
     * 校验分配优惠券手机号是否正常
     */
    public IData checkSn(IData input) throws Exception{
    	IData rtnData=new DataMap();
    	CouponsTradeBean bean = BeanManager.createBean(CouponsTradeBean.class);
        IDataset userInfo = bean.checkSn(input);
        if(userInfo!=null && userInfo.size()>0){
        	rtnData.put("USER_FLAG", "1");//用户状态 1正常用户
        	rtnData.put("USER_MSG", ""); 
        }else{
        	rtnData.put("USER_FLAG", "0");//用户状态 0非正常用户 1正常用户
        	rtnData.put("USER_MSG", "非正常用户，无法办理。");
        }
        return rtnData;
    }
    /**
     * 获取用户优惠券列表
     */
    public IDataset getUserTicketInfos(IData input)throws Exception{
    	IData rtnData=new DataMap();
    	CouponsTradeBean bean = BeanManager.createBean(CouponsTradeBean.class);
        IDataset userInfo = bean.getUserTicketInfos(input,getPagination()); 
    	return userInfo;
    }
    
    /**
     * 使用优惠券更新优惠券信息
     */
    public IData updUserCouponsInfo(IData input)throws Exception{ 
    	CouponsTradeBean bean = BeanManager.createBean(CouponsTradeBean.class);
        bean.updUserCouponsInfo(input); 
    	return new DataMap();
    }
    
    /**
     * 使用优惠券后更新操作员限额
     */
    public IData updCouponsQuota(IData input)throws Exception{ 
    	CouponsTradeBean bean = BeanManager.createBean(CouponsTradeBean.class);
        bean.updCouponsQuota(input); 
    	return new DataMap();
    }
}