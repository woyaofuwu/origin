
package com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.receiptprint;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ReceiptPrintSVC extends CSBizService
{
    /**
     * @Description 获取可补打的流水记录
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryTradeReceipt(IData param) throws Exception
    {
        ReceiptPrintBean bean = BeanManager.createBean(ReceiptPrintBean.class);
        return bean.queryTradeReceipt(param);
    }

    /**
     * @Description 发票补打后台动作
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset submitPrintReceipt(IData input) throws Exception
    {
        ReceiptPrintBean bean = BeanManager.createBean(ReceiptPrintBean.class);
        return bean.submitPrintReceipt(input);
    }
    
    /**
     * @Description 构建打印数据
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset printTrade(IData input) throws Exception{
        ReceiptPrintBean bean = BeanManager.createBean(ReceiptPrintBean.class);
        return bean.printTrade(input);
    }
}
