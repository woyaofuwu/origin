
package com.asiainfo.veris.crm.order.web.person.sundryquery.autopaycontract;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 手机支付账户与银行卡绑定信息查询
 * 
 * @author bobo
 */
public abstract class AccountBankBindingQuery extends PersonBasePage
{

    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData input = getData();

        IDataset set = CSViewCall.call(this, "SS.IBossMobilePaySVC.queryAccountBindBankInfos", input);
        setAjax(set);
        setInfos(set);
    }

    public abstract void setInfos(IDataset set);

}
