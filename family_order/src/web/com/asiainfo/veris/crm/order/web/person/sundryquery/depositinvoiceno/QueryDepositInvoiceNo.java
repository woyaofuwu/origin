
package com.asiainfo.veris.crm.order.web.person.sundryquery.depositinvoiceno;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：用户押金发票号码查询 作者：GongGuang
 */
public abstract class QueryDepositInvoiceNo extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 初始化
     */
    public void qryInit(IRequestCycle cycle) throws Exception
    {

    }

    /**
     * 功能：用户押金发票号码查询
     */
    public void queryIntegrateCustInvoice(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataInvoice = CSViewCall.callPage(this, "SS.QueryDepositInvoiceNoSVC.queryinvoiceDs", inparam, getPagination("navt"));
        IDataset invoiceDs = dataInvoice.getData();
        IDataOutput dataCustInfo = CSViewCall.callPage(this, "SS.QueryDepositInvoiceNoSVC.custinfoDs", inparam, null);
        IDataset custinfoDs = dataCustInfo.getData();
        // if (!this.hasPriv("SYS012")) {
        if (custinfoDs.size() > 0)
        {
            IData theDatasrc = custinfoDs.getData(0);
            IData theData = new DataMap();
            theData.put("CUST_INFO", theDatasrc);
            // setCustinfos((IData)custinfoDs.get(0));
            setCustinfos(theData.getData("CUST_INFO"));
        }
        // }
        setInfos(invoiceDs);
        setCount(dataInvoice.getDataCount());
        setCond(getData("cond", true));
    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setCustinfos(IData custinfo);

    public abstract void setInfos(IDataset infos);

}
