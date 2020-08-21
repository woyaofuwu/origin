package com.asiainfo.veris.crm.order.soa.person.busi.custservice;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author:PSY
 * @Dat:   2015年3月11日   下午7:25:39 
 * @version: v1.0
 * @Description : 热线报停报开
 */
public class ChangeUserMobileSVC extends CSBizService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 /**
     * 停机接口 ：ITF_CRM_ModifyStopReg
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset createStopReg(IData input) throws Exception
    {
    	ChangeUserMobileBean bean = (ChangeUserMobileBean) BeanManager.createBean(ChangeUserMobileBean.class);

        return bean.createStopReg(input);
    }
    
    /**
     * 开机接口：ITF_CRM_ModifyOpenReg
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset createOpenReg(IData input) throws Exception
    {
    	ChangeUserMobileBean bean = (ChangeUserMobileBean) BeanManager.createBean(ChangeUserMobileBean.class);

        return bean.createOpenReg(input);
    }
    
	public void setTrans(IData input) throws Exception{
		String serial_number = input.getString("MSISDN","");
		if(!"".equals(serial_number)){
			input.put("SERIAL_NUMBER", serial_number);
		}
	}

}
