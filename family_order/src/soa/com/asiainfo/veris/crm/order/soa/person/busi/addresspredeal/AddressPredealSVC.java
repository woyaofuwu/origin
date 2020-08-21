package com.asiainfo.veris.crm.order.soa.person.busi.addresspredeal;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class AddressPredealSVC extends CSBizService{
	private static Logger logger = Logger.getLogger(AddressPredealSVC.class);
	private static final long serialVersionUID = 1L;
	
	/**
	 * 发送短信提醒
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset sendSMSNotice(IData param)throws Exception {
		IDataset dataset=new DatasetList();
		IData data=new DataMap();
		try{
			AddressPredealBean bean= BeanManager.createBean(AddressPredealBean.class);
			bean.sendSMSNotice(param);
			data.put("success", "0");
			data.put("msg", "success");
		}catch(Exception e){
			logger.error(e);
			data.put("success", "-1");
			data.put("msg", e.getMessage());
			//throw e;
		}
		dataset.add(data);
		return dataset;
	}
	/**
	 * 查询用户是否开过宽带
	 * @param dataSet
	 * @return
	 * @throws Exception
	 */
	public IDataset qryWideNetUser(IData param) throws Exception{
		AddressPredealBean bean= BeanManager.createBean(AddressPredealBean.class);
		return bean.qryWideNetUser(param);
	}
}
