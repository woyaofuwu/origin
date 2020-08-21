package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author:YF
 * @Dat:   2013年12月19日   下午3:25:39 
 * @version: v1.0
 * @Description : 热线国际漫游开通
 */
public class ChangeProductRoaMopSVC extends CSBizService {
	
	/**
	 * @Title : changeStates
	 * @Description:TODO
	 * @Param : @param input( MSISDN   SvcType    EffectType  IdentCode  AssureInfo  )
	 * @Param : @return
	 * @Param : @throws Exception
	 * @return: IDataset
	 */
	public IDataset changeStates(IData input)throws Exception{
		
		ChangeProductRoaMopBean bean = (ChangeProductRoaMopBean)BeanManager.createBean(ChangeProductRoaMopBean.class);
		return bean.changeStates(input,"ChangeProductRoaMopSVC");
		
	}
	
	
	public void setTrans(IData input) throws Exception{
		String serial_number = input.getString("MSISDN","");
		if(!"".equals(serial_number)){
			input.put("SERIAL_NUMBER", serial_number);
		}
	}

}
