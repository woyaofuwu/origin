
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.usercheck;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserCheckHandler extends BizHttpHandler
{

    /**
     * 认证校验
     * 
     * @throws Exception
     */
    public void checkUser() throws Exception
    {
        IData data = getData();
        IData authData = new DataMap();
        boolean flag = true;
        IDataset outputData = CSViewCall.call(this, "CS.AuthCheckSVC.authCheck", data);
        if (null != outputData && outputData.size() > 0)
        {
            IData result = outputData.getData(0);
            authData.putAll(result);
            if (result != null && !("0").equals(result.getString("RESULT_CODE", "")))
            {
                flag = false;
            }
        }
        if (flag)
        {
            authData.put("RESULT_CODE", "0");
        }
        setAjax(authData);
    }

    /**
     * 查询用户
     * 
     * @throws Exception
     */
    public void queryUser() throws Exception
    {
        IData authData = new DataMap();
        IData data = getData();
        IData userInfo = null;
        authData.put("RESULT_CODE", 0);
        IDataset users = CSViewCall.call(this, "CS.UserInfoQrySVC.getNormalUserInfoBySN", data);
        if (null == users || users.isEmpty())
        {
            authData.put("RESULT_CODE", 1); // 查询不到用户
        }
        else
        {
            userInfo = users.getData(0);
            if (null == userInfo || userInfo.isEmpty())
            {
                authData.put("RESULT_CODE", 1); // 查询不到用户
            }
            else
            {
                String tradeEparchyCode = data.getString("EPARCHY_CODE", getVisit().getStaffEparchyCode());
                if (!tradeEparchyCode.equals(userInfo.getString("EPARCHY_CODE")) && "true".equalsIgnoreCase(data.getString("IS_LOCAL", "false")))
                {
                    authData.put("RESULT_CODE", 2); // 异地用户
                }
                authData.put("USER_INFO", userInfo);
            }
        }
        setAjax(authData);
    }
}
