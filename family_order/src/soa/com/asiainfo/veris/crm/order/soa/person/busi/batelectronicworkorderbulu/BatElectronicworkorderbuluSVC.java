
package com.asiainfo.veris.crm.order.soa.person.busi.batelectronicworkorderbulu;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BatElectronicworkorderbuluSVC extends CSBizService
{
	private static Logger logger = Logger.getLogger(BatElectronicworkorderbuluSVC.class);
    private static final long serialVersionUID = 1L;
    
    /**
     * 纸质单据电子化信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public IDataset queryElectronicworkorder(IData input) throws Exception
    {
    	BatElectronicworkorderbuluBean bean = BeanManager.createBean(BatElectronicworkorderbuluBean.class);
        return bean.queryElectronicworkorder(input, getPagination());
    }
    /**
     * 纸质单据电子化，传到东软
     * 
     * @param cycle
     * @throws Exception
     */
    public IData electronicworkorderbuluToDzh(IData input) throws Exception
    {
    	BatElectronicworkorderbuluBean bean = BeanManager.createBean(BatElectronicworkorderbuluBean.class);
        return bean.electronicworkorderbuluToDzh(input, getPagination());
    }

}
