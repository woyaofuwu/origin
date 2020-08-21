
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.auth;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserList extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 查询用户列表
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryUserList(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);

        IData params = new DataMap();
        params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        String userListSvc = data.getString("USER_LIST_SVC");
        String svcName = "CS.UserListSVC.queryUserList";
        if (StringUtils.isNotBlank(userListSvc))
        {
            svcName = userListSvc;
        }

        IDataset infos = CSViewCall.call(this, svcName, params);

        setInfos(infos);

    }

    public abstract void setInfos(IDataset infos);

}
