
package com.asiainfo.veris.crm.order.soa.person.busi.einvoicehistory;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EInvoiceHistorySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;


    /**
     * 电子发票历史查询业务
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryEInvoiceTrade(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        EInvoiceHistoryBean bean = (EInvoiceHistoryBean) BeanManager.createBean(EInvoiceHistoryBean.class);
        return bean.queryEInvoiceTrade(input);
    }
    
    /**
     * 电子发票冲红
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset modifyEInvoiceTrade(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        EInvoiceHistoryBean bean = (EInvoiceHistoryBean) BeanManager.createBean(EInvoiceHistoryBean.class);
        return bean.modifyEInvoiceTrade(input);
    }
    /**
     * 业务是否支持打印电子发票
     */
    public IDataset isEInvoicePrint(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        EInvoiceHistoryBean bean = (EInvoiceHistoryBean) BeanManager.createBean(EInvoiceHistoryBean.class);
        return bean.isEInvoicePrint(input.getString("TRADE_TYPE_CODE"));
    }

}
