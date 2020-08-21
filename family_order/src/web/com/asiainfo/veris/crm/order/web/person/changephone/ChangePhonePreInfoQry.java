
package com.asiainfo.veris.crm.order.web.person.changephone;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChangePhonePreInfoQry extends PersonBasePage
{

    /**
     * 预申请审核查询视图
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        IDataset dataset = new DatasetList();
        setUserInfo(userInfo);
        setCustInfo(custInfo);
        dataset = CSViewCall.call(this, "SS.ChangePhonePreInfoQuerySVC.queryChangeCardInfo", userInfo);
        if (IDataUtil.isNotEmpty(dataset))
        {
            IData info = dataset.getData(0);
            setInfo(info);
        }
        else
        { // 没有数据时
            CSViewException.apperr(ChangePhoneException.CRM_CHANGEPHONE_200); // 查询用户信息无数据
        }
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData data);

    public abstract void setUserInfo(IData userInfo);

}
