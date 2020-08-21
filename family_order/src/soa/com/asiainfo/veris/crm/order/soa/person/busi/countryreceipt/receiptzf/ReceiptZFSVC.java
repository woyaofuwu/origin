
package com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.receiptzf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ReceiptZFSVC extends CSBizService
{
    /**
     * 获取可作废的发票记录
     * 
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryZFReceipt(IData input) throws Exception
    {
        ReceiptZFBean bean = BeanManager.createBean(ReceiptZFBean.class);
        return bean.queryZFReceipt(input);
    }

    /**
     * 发票作废后台动作
     * 
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     */
    public void submitZFReceipt(IData input) throws Exception
    {
        ReceiptZFBean bean = BeanManager.createBean(ReceiptZFBean.class);
        bean.ReceiptZF(input);
    }
}
