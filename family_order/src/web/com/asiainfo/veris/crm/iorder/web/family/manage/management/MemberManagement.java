
package com.asiainfo.veris.crm.iorder.web.family.manage.management;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

/**
 * @Description 成员管理
 * @Auther: zhenggang
 * @Date: 2020/8/10 10:02
 * @version: V1.0
 */
public abstract class MemberManagement extends PersonBasePage
{
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

    }

    public abstract void setFamilyCustInfo(IData familyCustInfo);

    public abstract void setMemberInfos(IDataset memberInfos);

    public abstract void setMemberInfo(IData memberInfo);
}
