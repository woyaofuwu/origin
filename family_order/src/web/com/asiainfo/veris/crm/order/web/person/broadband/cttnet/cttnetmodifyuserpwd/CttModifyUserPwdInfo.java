
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetmodifyuserpwd;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户密码变更
 */
public abstract class CttModifyUserPwdInfo extends PersonBasePage
{

    /**
     * 修改密码校验
     */
    public void checkPwd(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        IDataset dataset = CSViewCall.call(this, "SS.CttModifyUserPwdInfoSVC.checkPwd", data);
        setAjax(dataset);
    }

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
        IDataset dataset = CSViewCall.call(this, "SS.CttModifyUserPwdRegSVC.tradeReg", data);
        setAjax(dataset);
    }
}
