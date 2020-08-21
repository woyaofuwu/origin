
package com.asiainfo.veris.crm.order.web.person.ecardgprsbind;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ECardGprsBind extends PersonBasePage
{

    /**
     * 校验随E行号码
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkEcardPhone(IRequestCycle cycle) throws Exception
    {
        IData query = getData();
        IDataset infos = CSViewCall.call(this, "SS.ECardGprsBindSVC.checkEcardPhone", query);
        IData param = infos.getData(0);
        this.setAjax(param);
    }

    public void checkPhoneEcard(IRequestCycle cycle) throws Exception
    {
        IData query = getData();
        IDataset infos = CSViewCall.call(this, "SS.ECardGprsBindSVC.checkPhone", query);
        IData param = infos.getData(0);
        setInfo(param);
    }

    /**
     * 页面初始化，加载页面需要的信息
     * 
     * @param cycle
     * @throws Exception
     * @author likai3
     */

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));
        IData custInfo = new DataMap(pagedata.getString("CUST_INFO"));

        String userId = userInfo.getString("USER_ID", "");

        setUserInfo(userInfo);
        setCustInfo(custInfo);

        IData UserChangePara = new DataMap();
        UserChangePara.put("USER_ID", userId);
        UserChangePara.put("SERIAL_NUMBER", userInfo.get("SERIAL_NUMBER"));

        IDataset dsResult = CSViewCall.call(this, "SS.ECardGprsBindSVC.queryCrmInfos", UserChangePara);
        IData commonInfo = dsResult.size() > 0 ? (IData) dsResult.get(0) : null;

        setCommInfo(commonInfo);
    }

    /**
     * 业务提交（onTradeSubmit cssubmit组件中默认的提交action方法）
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {

            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        IDataset dataset = CSViewCall.call(this, "SS.ECardGprsBindRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCommInfo(IData data);

    public abstract void setCustInfo(IData data);

    public abstract void setInfo(IData info);

    public abstract void setUserInfo(IData data);
}
