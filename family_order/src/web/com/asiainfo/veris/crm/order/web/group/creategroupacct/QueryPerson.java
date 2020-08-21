
package com.asiainfo.veris.crm.order.web.group.creategroupacct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class QueryPerson extends CSBasePage
{
    public abstract IData getInfo();

    public void queryPersonBySerial(IRequestCycle cycle) throws Throwable
    {

        IData params = getData("cond", true);
        IDataset results = new DatasetList();
        IDataset tempMoffice = CSViewCall.call(this, "CS.RouteInfoQrySVC.getEparchyCodeBySn", params);

        if (tempMoffice.size() < 1)
        {
            setHintInfo("查询服务号码归属地州无信息！");
            return;
        }
        String eparchyCode = (String) tempMoffice.get(0, "EPARCHY_CODE", "");

        IData userInfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, params.getString("SERIAL_NUMBER"), false);
        if (IDataUtil.isEmpty(userInfo))
        {
            setHintInfo("查询服务号码无用户信息！");
            return;
        }
        String custID = userInfo.getString("CUST_ID", "");
        eparchyCode = userInfo.getString("EPARCHY_CODE", "");
        IData custInfo = UCAInfoIntfViewUtil.qryMebCustInfoByCustIdAndRoute(this, custID, eparchyCode, false);

        if (IDataUtil.isEmpty(custInfo))
        {
            setHintInfo("查询服务号码无客户信息！");

        }
        userInfo.putAll((IData) (custInfo));
        results.add(userInfo);
        setInfos(results);
        setCondition(params);
    }

    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
