/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.nonbossfeeuseritem;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by chenxy@2014-11-19  下午03:30:00
 */
public abstract class NonBossFeeUserItemMgr extends PersonBasePage
{

    public void init(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IDataset companyname = CSViewCall.call(this, "SS.NonBossFeeUserItemMgrSVC.getCompanyName", data);
        setCompname(companyname);
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
        IDataset dataset = CSViewCall.call(this, "SS.NonBossFeeUserItemMgrSVC.nonBossFeeUserItemsMgr", data);
        //setAjax(dataset);

    }
    
    public void queryNonBossFeeUserItems(IRequestCycle cycle) throws Exception
    {

        IData conditions = this.getData("cond", true);

        IDataset userInfos = CSViewCall.call(this, "SS.NonBossFeeUserItemMgrSVC.getNonBossFeeUserItems", conditions);
         
        this.setInfos(userInfos);
        this.setCondition(conditions);

    }

    public abstract void setCond(IData cond);

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setCompname(IDataset compname);

}
