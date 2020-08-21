
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.banklist;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BankList extends PersonBasePage
{

    public abstract IData getCond();

    public abstract IDataset getInfos();

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset infos = CSViewCall.call(this, "CS.BankInfoQrySVC.getBankBySuperBank", data);
        setInfos(infos);
        setCond(data);
    }

    /**
     * 查询客户列表
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBankList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("BANK_CODE", data.getString("BANK", ""));
        IDataset infos = CSViewCall.call(this, "CS.BankInfoQrySVC.queryBankListByBank", data);

        setInfos(infos);
        setCond(data);
    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

}
