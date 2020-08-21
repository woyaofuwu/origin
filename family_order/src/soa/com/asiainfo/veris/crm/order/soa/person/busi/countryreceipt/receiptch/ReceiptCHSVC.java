
package com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.receiptch;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.receiptprint.ReceiptPrintBean;

public class ReceiptCHSVC extends CSBizService
{
    /**
     * @Description 获取可冲红的发票记录
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryCHReceipt(IData input) throws Exception
    {
        ReceiptCHBean bean = BeanManager.createBean(ReceiptCHBean.class);
        return bean.queryCHReceipt(input);
    }

    /**
     * @Description 发票冲红后台动作
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset submitCHReceipt(IData input) throws Exception
    {
        ReceiptCHBean bean = BeanManager.createBean(ReceiptCHBean.class);
        return bean.submitPrintReceipt(input);
    }
    
    /**
     * @Description 构建打印数据
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset printTrade(IData input) throws Exception{
    	ReceiptCHBean bean = BeanManager.createBean(ReceiptCHBean.class);
        return bean.printTrade(input);
    }
}
