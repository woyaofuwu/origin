package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.gponwidenetorderdestory;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;


public class GponWidenetOrderDestorySVC extends CSBizService
{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 获取GPON宽带用户预约拆机信息
	 */
	public IDataset getGponDestroyInfo(IData params) throws Exception
    {
		GponWidenetOrderDestoryBean gponWidenetOrderDestoryBean = BeanManager.createBean(GponWidenetOrderDestoryBean.class);
        IDataset infos = gponWidenetOrderDestoryBean.queryInfo(params);
        return infos;
    }
	
	/**
	 * 调拆机接口
	 */
	public IDataset callGponDestroyService(IData params) throws Exception
    {
		GponWidenetOrderDestoryBean gponWidenetOrderDestoryBean = BeanManager.createBean(GponWidenetOrderDestoryBean.class);
		IDataset data = gponWidenetOrderDestoryBean.callGponDestroyService(params);
		return data;
    }

	/**
	 * 接口
	 */
	public IDataset queryWidenetInstallFee(IData params) throws Exception
    {
		GponWidenetOrderDestoryBean gponWidenetOrderDestoryBean = BeanManager.createBean(GponWidenetOrderDestoryBean.class);
		IDataset data = gponWidenetOrderDestoryBean.queryWidenetInstallFee(params);
		return data;
    }
	
	public IDataset queryWidenetCommissioningFee(IData params) throws Exception
    {
		GponWidenetOrderDestoryBean gponWidenetOrderDestoryBean = BeanManager.createBean(GponWidenetOrderDestoryBean.class);
		IDataset data = gponWidenetOrderDestoryBean.queryWidenetCommissioningFee(params);
		return data;
    }
	//无手机宽带、魔百和违约款
	public IData queryCommissioningFee(IData params) throws Exception
    {
		GponWidenetOrderDestoryBean gponWidenetOrderDestoryBean = BeanManager.createBean(GponWidenetOrderDestoryBean.class);
		IData data = gponWidenetOrderDestoryBean.queryCommissioningFee(params);
		return data;
    }
	
	
}
