package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod; 
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
/**
 * 无手机宽带业务
 * CHENXY3
 * 2016-12-23
 * */
public class NoPhoneWideChangeProdBean extends CSBizBean
{
	 
	
	 /**
     * 根据身份证号获取宽带账号
     * */
    public static IDataset noPhoneUserQryByPSPTID(IData inparam) throws Exception
    {    
    	return  Dao.qryByCode("TF_F_CUSTOMER", "SEL_NOPHONE_SN_BY_PSPTID", inparam );
    }
    
    /**
     * 根据SERIAL_NUMBER判断是否无手机宽带用户
     * 入参：SERIAL_NUMBER，3种情况：
     * 1、KD_开头的
     * 2、18位长度的（身份证）
     * 3、非KD_开头且非身份证
     * */
    public static IDataset checkIfNoPhoneWideUser(IData inparam) throws Exception
    {    
    	return  Dao.qryByCode("TF_F_USER", "SEL_NOPHONE_USER_BY_SN", inparam );
    } 
    
    /**
     * 获取无手机宽带用户优惠信息
     * */
    public static IDataset qryNoPhoneUserDiscnt(IData inparam) throws Exception
    {    
    	return  Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_NOPHONE", inparam );
    } 
    
    /**
     * 获取无手机宽带用户优惠信息
     * 取最后一笔无手机宽带优惠的信息
     * */
    public static IDataset qryNoPhoneUserDiscnt2(IData inparam) throws Exception
    {    
    	return  Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_NOPHONE2", inparam );
    }  
}