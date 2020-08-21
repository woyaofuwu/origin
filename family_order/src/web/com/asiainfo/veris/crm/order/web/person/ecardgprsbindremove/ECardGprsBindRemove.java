
package com.asiainfo.veris.crm.order.web.person.ecardgprsbindremove;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ECardGprsBindRemove extends PersonBasePage
{
    /**
     * 页面初始化，加载页面需要的信息
     * 
     * @param cycle
     * @throws Exception
     * @author likai3
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        // 规则校验
        // 账期判断
        // if(preAcctDay != null && !"".equals(preAcctDay) && ! nowAcctDay.equals(preAcctDay)){
        // common.error("509007：用户存在预约帐期，账期生效时间为"+ acctInfo.getString("NEXT_FIRST_DATE")+"，账期生效后才能办理随E行取消业务！");
        // }
        // 还需增加主副卡账期一致性判断

        // 获取绑定信息
        IData pagedata = getData();
        IData userData = new DataMap();
        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));
        IData custInfo = new DataMap(pagedata.getString("CUST_INFO"));
        setUserInfo(userInfo);
        setCustInfo(custInfo);
        // 查询绑定关系的随E行号码
        userData.put("SERIAL_NUMBER", userInfo.get("SERIAL_NUMBER"));
        userData.put("USER_ID_B", userInfo.get("USER_ID"));
        IDataset relationInfos = CSViewCall.call(this, "SS.ECardGprsBindRemoveSVC.queryRelationUU", userData);

        // 查询当前产品信息和下账期产品信息
        IData ChangePara = new DataMap();
        ChangePara.put("SERIAL_NUMBER", userInfo.get("SERIAL_NUMBER"));
        ChangePara.put("USER_ID", userInfo.get("USER_ID"));
        IDataset userChanges = CSViewCall.call(this, "SS.ECardGprsBindRemoveSVC.queryCrmInfos", ChangePara);
        IData commonInfo = userChanges.size() > 0 ? (IData) userChanges.get(0) : null;

        setCommInfo(commonInfo);
        setInfos(relationInfos);
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

        IDataset dataset = CSViewCall.call(this, "SS.ECardGprsBindRemoveRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCommInfo(IData data);

    public abstract void setCustInfo(IData data);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setUserInfo(IData data);

}
