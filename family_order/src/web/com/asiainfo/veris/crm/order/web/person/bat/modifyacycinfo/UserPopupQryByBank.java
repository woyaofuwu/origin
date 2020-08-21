
package com.asiainfo.veris.crm.order.web.person.bat.modifyacycinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserPopupQryByBank extends PersonBasePage
{

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    public void queryAllBanks(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryAllBanks", data, this.getPagination("taskNav"));
        IDataset banks = output.getData();
        setBanks(banks);
    }

    public void queryUsersByBank(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        param.put("BANK_CODE", data.getString("cond_BANK_CODE"));
        param.put("BANK_ACCT_NO", data.getString("cond_BANK_ACCT_NO"));
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryUsersByBank", param, this.getPagination("taskNav"));
        IDataset users = output.getData();
        queryAllBanks(cycle);
        setInfos(users);
        setCondition(data);
    }

    public abstract void setBank(IData bank);

    public abstract void setBanks(IDataset banks);

    public abstract void setBatchTaskListCount(long batchTaskListCount);

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
