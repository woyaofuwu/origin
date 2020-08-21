
package com.asiainfo.veris.crm.order.web.person.sundryquery.plat.mobilepayment;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AccountTransactionQuery extends PersonBasePage
{

    protected static Logger log = Logger.getLogger(AccountTransactionQuery.class);

    public void init(IRequestCycle cycle) throws Exception
    {
        IData inits = new DataMap();
        String sysDate = SysDateMgr.getSysTime();
        inits.put("cond_START_DATE", sysDate);
        inits.put("cond_END_DATE", sysDate);
        this.setCondition(inits);
    }

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData cust);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public void submitTrade(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("cond", true);

        IDataset result = CSViewCall.call(this, "SS.AccountInfoQuerySVC.queryAccountTransactionInfo", param);

        setCondition(param);

        String alertInfo = "";
        if (result == null || result.isEmpty())
        {

            alertInfo = "未找到该用户资料";
            setCustInfo(null);

        }
        else
        {

            setCustInfo(result.getData(0));
            setInfos(result.getDataset(1));

        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
    }

}
