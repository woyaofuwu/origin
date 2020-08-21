package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation;





import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class StopOpenMobileSVC extends CSBizService{
	
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 异地停开机发起接口
	 * @author liukai5
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset applyStopOpenMobile(IData input) throws Exception{
		IDataset dataset = new DatasetList();
		StopOpenMobileBean bean = (StopOpenMobileBean) BeanManager.createBean(StopOpenMobileBean.class);
        IData data = bean.applyStopOpenMobile(input);
        dataset.add(data);
        return dataset;
	}
	
	/**
	 * 异地停开机发起接口
	 * @author liukai5
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset stopOpenMobile(IData input) throws Exception{
		String TkjType=input.getString("TKJ_TYPE");
		IDataset dataset = new DatasetList();
		StopOpenMobileBean bean = (StopOpenMobileBean) BeanManager.createBean(StopOpenMobileBean.class);
        IData data = bean.stopOpenMobile(input);
        dataset.add(data);
        return dataset;
	}

}
