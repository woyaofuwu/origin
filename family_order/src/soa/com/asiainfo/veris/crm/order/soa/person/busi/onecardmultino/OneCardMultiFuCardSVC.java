package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

public class OneCardMultiFuCardSVC  extends CSBizService
{
	private static final long serialVersionUID = -4185067227506011493L;

	/**
     * 根据副号码查询主号码信息
     */
    public IDataset qryMainCardInfo(IData input) throws Exception
    {
    	String snb = input.getString("SERIAL_NUMBER");
    	UcaData ucaB = UcaDataFactory.getNormalUca(snb);
    	
    	ViceRealInfoReRegBean bean = BeanManager.createBean(ViceRealInfoReRegBean.class);
    	IDataset datas = bean.qryHdhSynInfo(snb);
    	if(IDataUtil.isEmpty(datas))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该副号码未查询到绑定信息！");
    	}
    	return datas;
    }
    
    
    /**
     * 根据副号码查询号码信息
     */
    public IDataset qryFuCardInfo(IData input) throws Exception
    {
    	String snb = input.getString("SERIAL_NUMBER");
    	UcaData ucaB = UcaDataFactory.getNormalUca(snb);
    	
    	ViceRealInfoReRegBean bean = BeanManager.createBean(ViceRealInfoReRegBean.class);
    	IDataset datas = bean.qryHdhSynInfo(snb);
    	if(IDataUtil.isEmpty(datas))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该副号码未查询到绑定信息！");
    	}
    	return datas;
    }
}
