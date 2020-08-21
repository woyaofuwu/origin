
package com.asiainfo.veris.crm.order.web.person.changepassword;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public class UnLockUserPwd extends PersonBasePage
{

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData("PASSWORD", true);
        IData inparam = getData("AUTH", true);
        inparam.put("CHECK_MODE", getData().getString("CHECK_MODE"));// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
        data.putAll(inparam);
        IDataset dataset = CSViewCall.call(this, "SS.UnLockUserPwdRegSVC.tradeReg", data);
        setAjax(dataset);
    }
}
