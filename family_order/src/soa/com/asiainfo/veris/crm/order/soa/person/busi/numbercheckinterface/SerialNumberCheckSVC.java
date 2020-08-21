package com.asiainfo.veris.crm.order.soa.person.busi.numbercheckinterface; 

import com.ailk.common.data.IData; 
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * REQ201509300012 自助终端增加移动、铁通固话缴费功能需求
 * CHENXY3
 * 2015-12-22
 * */ 
public class SerialNumberCheckSVC extends CSBizService
{
	/**
     * 1、移动固话TD2以898开头，铁通固话TD1以0898开头，用户缴费时只需要输入8位电话号码，
	 *    系统能够根据号码区分移动固话TD2，铁通固话TD1，并自动根据固话号码类型字段添加号码开头信息。
		      新增固话类型查询接口，根据固话号码查询号码属于铁通固话或移动固话还是普通移动手机号。
	 *1、 固话号码入参为8位；
	 *2、 8位号码拼装898及0898查询TF_F_USER表
	 *3、 898开头的号码为移动固话（TD2），0898开头的号码为铁通固话（TD1）
	 *4、 普通移动手机号 则直接查询TF_F_USER表  (SN)
     * */
    public IData serialNumberCheck(IData input) throws Exception{
    	SerialNumberCheckBean bean= BeanManager.createBean(SerialNumberCheckBean.class);
    	return bean.serialNumberCheck(input);
    }
}