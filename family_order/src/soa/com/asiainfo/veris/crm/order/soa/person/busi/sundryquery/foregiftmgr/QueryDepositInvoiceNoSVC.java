
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.foregiftmgr;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryDepositInvoiceNoSVC extends CSBizService
{
    static transient final Logger logger = Logger.getLogger(QueryDepositInvoiceNoSVC.class);

    private static final long serialVersionUID = 1L;

    public IDataset queryIntegrateCustInvoice(IData input) throws Exception
    {
        QueryDepositInvoiceNoBean result = (QueryDepositInvoiceNoBean) BeanManager.createBean(QueryDepositInvoiceNoBean.class);

        IDataset infos = result.queryIntegrateCustInvoice(input, getPagination());

        return infos;
    }

}
