/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.nonbossfee;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-4-23 修改历史 Revision 2014-4-23 下午03:30:32
 */
public abstract class NonBossFeeItemMgr extends PersonBasePage
{

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	data.put("LIST_TYPE", "fee");
        IDataset feetype = CSViewCall.call(this, "SS.NonBossFeeItemMgrSVC.getListType", data);
        setFeetype(feetype);
        
        data.put("LIST_TYPE", "income");
        IDataset incometype = CSViewCall.call(this, "SS.NonBossFeeItemMgrSVC.getListType", data); 
        setIncometype(incometype);
        
        data.put("LIST_TYPE", "invoice");
        IDataset invoicetype = CSViewCall.call(this, "SS.NonBossFeeItemMgrSVC.getListType", data);
        setInvoicetype(invoicetype);
        
        data.put("LIST_TYPE", "tax");
        IDataset taxtype = CSViewCall.call(this, "SS.NonBossFeeItemMgrSVC.getListType", data);
        setTaxtype(taxtype);
        
        data.put("LIST_TYPE", "market");
        IDataset markettype = CSViewCall.call(this, "SS.NonBossFeeItemMgrSVC.getListType", data);
        setMarkettype(markettype);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.NonBossFeeItemMgrSVC.nonBossFeeItemsMgr", data);
        //setAjax(dataset);
    }

    public void queryNonBossFeeItems(IRequestCycle cycle) throws Exception
    {

        IData conditions = this.getData("cond", true);

        IDataset feeItems = CSViewCall.call(this, "SS.NonBossFeeItemMgrSVC.getNonBossFeeItems", conditions);

        this.setInfos(feeItems);
        this.setCondition(conditions);

    }

    public abstract void setCond(IData cond);

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setFeetype(IDataset feetype);
    public abstract void setIncometype(IDataset incometype);
    public abstract void setInvoicetype(IDataset invoicetype);
    public abstract void setTaxtype(IDataset invoicetype);
    public abstract void setMarkettype(IDataset markettype);
}
