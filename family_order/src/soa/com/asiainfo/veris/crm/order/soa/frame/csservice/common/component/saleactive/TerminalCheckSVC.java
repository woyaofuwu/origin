
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TerminalCheckSVC extends CSBizService
{

    private static final long serialVersionUID = 7764319528361882884L;

    /**
     * 根据终端实占
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void occupyTerminalByResNo(IData input) throws Exception
    {
        TerminalBean terminalBean = BeanManager.createBean(TerminalBean.class);
        terminalBean.occupyTerminalByResNo(input);
    }

    /**
     * 根据终端串号校验、预占
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData preOccupyTerminalByResNo(IData input) throws Exception
    {
        TerminalBean terminalBean = BeanManager.createBean(TerminalBean.class);
        return terminalBean.preOccupyTerminalByResNo(input);
    }

    /**
     * 根据终端实占
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void releaseTerminalByResNo(IData input) throws Exception
    {
        TerminalBean terminalBean = BeanManager.createBean(TerminalBean.class);
        terminalBean.releaseTerminalByResNo(input);
    }
}
