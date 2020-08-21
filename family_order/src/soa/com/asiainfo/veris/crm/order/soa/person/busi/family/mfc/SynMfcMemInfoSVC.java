package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo.QueryInfoBean;

public class SynMfcMemInfoSVC extends CSBizService 
{

	private static final long serialVersionUID = -8359798408142522304L;

	/**
     * 3.3成员管理
     * @param inParam
     * @return
     * @throws Exception
     */
	public void synMeb(IData inParam)throws Exception 
	{
		SynMfcMemInfoBean bean = BeanManager.createBean(SynMfcMemInfoBean.class);
		bean.synMeb(inParam);
	}
	/**
	 * 3.14. 省内群组成员关系查询
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	 public IDataset getMeb(IData inParam) throws Exception
	    {
		 
		 
		 SynMfcMemInfoBean bean = BeanManager.createBean(SynMfcMemInfoBean.class);
	        return bean.getMeb(inParam);
	    }
	
}
