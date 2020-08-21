
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.depositinvoiceno;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryDepositInvoiceNoSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：用户押金发票号码查询(customer信息) 作者：GongGuang
     */
    public IDataset custinfoDs(IData data) throws Exception
    {
        QueryDepositInvoiceNoBean bean = (QueryDepositInvoiceNoBean) BeanManager.createBean(QueryDepositInvoiceNoBean.class);
        if ("0".equals(data.getString("QUERY_TYPE")))
        {
            return bean.queryUserCustBySerial(data, getPagination());
        }
        else if ("1".equals(data.getString("QUERY_TYPE")))
        {
            return bean.queryUserCustByInvoiceNo(data, getPagination());
        }
        else
        {
            return null;
        }
    }

    /**
     * 功能：用户押金发票号码查询(invoice信息) 作者：GongGuang
     */
    public IDataset queryinvoiceDs(IData data) throws Exception
    {
        QueryDepositInvoiceNoBean bean = (QueryDepositInvoiceNoBean) BeanManager.createBean(QueryDepositInvoiceNoBean.class);
        if ("0".equals(data.getString("QUERY_TYPE")))
        {
            return bean.queryUserOtherByUser(data, getPagination());
        }
        else if ("1".equals(data.getString("QUERY_TYPE")))
        {
            return bean.queryUserOtherByInvoiceNo(data, getPagination());
        }
        else
        {
            return null;
        }
    }
}
