
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TerminalQuerySVC extends CSBizService
{
    private static final long serialVersionUID = 3272953040005405151L; 

    /**
     * 根据终端串号获取机型相关信息,并根据终端判断是否可用。
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getEnableTerminalByResNo(IData input) throws Exception
    {
        TerminalBean terminalBean = BeanManager.createBean(TerminalBean.class);
        return terminalBean.getEnableTerminalByResNo(input);
    }

    /**
     * 根据机型编码获取机型相关信息 DEVICE_MODEL_CODE 机型编码 RES_TYPE_ID 子机型
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getTerminalByDeviceModelCode(IData input) throws Exception
    {
        TerminalBean terminalBean = BeanManager.createBean(TerminalBean.class);
        return terminalBean.getTerminalByDeviceModelCode(input);
    }

    /**
     * 根据终端串号获取机型相关信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getTerminalByResNoOnly(IData input) throws Exception
    {
        TerminalBean terminalBean = BeanManager.createBean(TerminalBean.class);
        return terminalBean.getTerminalByResNoOnly(input);
    }
    
    /**
     * 根据终端价格或终端厂商获取机型相关信息
     * @param input
     * @return
     * @throws Exception
     */
    
    public IDataset getTerminalByPriceOrBrand(IData input) throws Exception{
    	TerminalBean terminalBean = BeanManager.createBean(TerminalBean.class);
        return terminalBean.getTerminalByPriceOrBrand(input);
    }
}
