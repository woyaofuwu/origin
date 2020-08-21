
package com.asiainfo.veris.crm.order.web.group.internetofthings;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

/**
 * 物联网服务暂停
 * 
 * @author
 */
public abstract class SuspendResumeWlwService extends GroupBasePage
{

    /**
     * 获取用户的服务
     * 
     * @throws Exception
     */
    public void getUserServices(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        IDataset result = CSViewCall.call(this, "SS.WlwQuerySVC.qryUserServiceState", param);
        this.setInfos(result);
    }

    /**
     * 提交
     * 
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {

        IData param = this.getData();

        String suspendServices = param.getString("SUSPEND_SERVICE");
        String resumeServices = param.getString("RESUME_SERVICE");

        if (StringUtils.isNotBlank(suspendServices))
        {
            String[] suspendArray = suspendServices.split(";");
            for (int i = 0; i < suspendArray.length; i++)
            {
                String[] suspendRow = suspendArray[i].split(",");
                String userIDA = suspendRow[3];

                param.put("USER_ID_A", userIDA);
            }
        }

        if (StringUtils.isNotBlank(resumeServices))
        {
            String[] suspendArray = resumeServices.split(";");

            for (int i = 0; i < suspendArray.length; i++)
            {
                String[] suspendRow = suspendArray[i].split(",");
                String userIDA = suspendRow[3];

                param.put("USER_ID_A", userIDA);
            }
        }

        IDataset result = CSViewCall.call(this, "SS.SuspendResumeWlwServiceSVC.crtTrade", param);
        this.setAjax(result);
    }

    public abstract void setInfos(IDataset infos);
}
