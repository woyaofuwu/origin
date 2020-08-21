package com.asiainfo.veris.crm.order.soa.person.busi.gprslimit;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.gprslimit.GprsLimitBean;

public class GprsLimitSVC extends CSBizService {
	
	 private static final long serialVersionUID = 1L;
	 private static Logger logger = Logger.getLogger(GprsLimitSVC.class);
	 
		 public IDataset queryImportData(IData input) throws Exception
		 {
			GprsLimitBean bean = (GprsLimitBean) BeanManager.createBean(GprsLimitBean.class);
			String type=input.getString("LIMIT_QUERY_TYPE");
			if(type.equals("0")){
				return bean.queryImportData(input,getPagination());
			}
			
			if(type.equals("1")){
			    return bean.queryLimitData(input,getPagination());
			}
			
			return null;
		 }
		 
		 public IDataset queryDayData(IData input) throws Exception
		 {
			GprsLimitBean bean = (GprsLimitBean) BeanManager.createBean(GprsLimitBean.class);
			return bean.queryDayData(input);
		 }

}
