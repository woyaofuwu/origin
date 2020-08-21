
package com.asiainfo.veris.crm.order.web.person.internetofthings;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @author 物联网服务恢复
 */
public abstract class ResumeService extends PersonBasePage
{

    /**
     * 获取用户的服务
     * 
     * @throws Exception
     */
    public void getUserServices(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        IDataset result = CSViewCall.call(this, "SS.IOTQuerySVC.qryUserServiceState", param);
        this.setInfos(result);
    }

    public abstract void setInfos(IDataset infos);

    public void submitTrade(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        IDataset result = CSViewCall.call(this, "SS.SuspendResumeServiceRegSVC.tradeReg", param);
        this.setAjax(result);
    }

}
