
package com.asiainfo.veris.crm.order.web.group.bat.BatGrpModify;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;

public abstract class BatGrpModify extends CSBasePage
{

    public abstract IData getUserInfo();

    public void initial(IRequestCycle cycle) throws Throwable
    {
        setCondition(getData());
    }

    public abstract void setCondition(IData condition);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setUserInfo(IData userInfo);// 用户信息

    public abstract void setUserInfos(IDataset userInfos);

}
