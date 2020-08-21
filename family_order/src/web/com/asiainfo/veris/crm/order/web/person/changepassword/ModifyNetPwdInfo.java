
package com.asiainfo.veris.crm.order.web.person.changepassword;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户密码变更
 * 
 * @author liutt
 */
public abstract class ModifyNetPwdInfo extends PersonBasePage
{
    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        data.put("NEW_PASSWD", data.getString("USER_PASSWD1"));
        IDataset dataset = CSViewCall.call(this, "SS.ModifyNetPwdInfoRegSVC.tradeReg", data);
        setAjax(dataset);
    }
}
