package com.asiainfo.veris.crm.order.soa.person.busi.intf.ordercrbt;


import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;

public class CRBTIntfSVC extends CSBizService
{

    private static final long serialVersionUID = 5139428345186303900L;

    public void setTrans(IData input) throws Exception
    {

    }

    /**
     *  订单处理
     * @param input
     * @return
     * @throws Exception
     */
    public IData processOrder(IData input) throws Exception
    {

        CRBTIntfBean bean = BeanManager.createBean(CRBTIntfBean.class); 
        return bean.processOrder(input);

    } 
    
    
    /**
     * 退订免费彩铃接口1
     * @param input
     * @return
     * @throws Exception
     */
    public IData processCancelProductOrder(IData input) throws Exception
    {

        CRBTIntfBean bean = BeanManager.createBean(CRBTIntfBean.class); 
        return bean.processCancelProductOrder(input);

    }
    
    /**
     * 退订免费彩铃接口2
     * @param input
     * @return
     * @throws Exception
     */
    public IData tradeRegCancel(IData input) throws Exception
    {

        CRBTIntfBean bean = BeanManager.createBean(CRBTIntfBean.class); 
        return bean.tradeRegCancel(input);

    } 

    /**
     *  订单项处理
     * @param input
     * @return
     * @throws Exception
     */
    public IData tradeRegItem(IData input) throws Exception
    {

        CRBTIntfBean bean = BeanManager.createBean(CRBTIntfBean.class); 
        return bean.tradeRegItem(input);

    } 
    

    /**
     *  短信发送
     * @param input
     * @return
     * @throws Exception
     */
    public IData sendEndSMS(IData input) throws Exception
    {

        CRBTIntfBean bean = BeanManager.createBean(CRBTIntfBean.class); 
        return bean.sendEndSMS(input);

    } 
    
    /**
     *  短信条目处理
     * @param input
     * @return
     * @throws Exception
     */
    public IData processSmsItem(IData input) throws Exception
    {

        CRBTIntfBean bean = BeanManager.createBean(CRBTIntfBean.class); 
        return bean.processSmsItem(input);

    } 
    
    /**
     * 二次短信回复的处理
     */
    public IData twoCheckReply(IData input) throws Exception
    {
         
        CRBTIntfBean bean = BeanManager.createBean(CRBTIntfBean.class); 
        return bean.twoCheckReply(input);

    }
    
}
