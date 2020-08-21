
package com.asiainfo.veris.crm.order.web.person.np.createnpusertrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BaseInfoPopup extends PersonBasePage
{

    /**
     * 获取证件下开户的号码资料
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void getUserInfoByPspt(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.getUserInfoByPspt", data);
        setUserList(dataset);
    }

    public abstract void setUserList(IDataset infos);

}
