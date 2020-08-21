
package com.asiainfo.veris.crm.iorder.web.family.entrance;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

/**
 * @Description 家庭受理入口
 * @Auther: zhenggang
 * @Date: 2020/7/22 16:58
 * @version: V1.0
 */
public abstract class FamilyEntrance extends PersonBasePage
{
    public abstract void setInfo(IData info);

    /**
     * @Description: 初始化
     * @Param: [cycle]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/7/22 16:58
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = new DataMap();
        setInfo(result);
    }

    /**
     * @Description: 加载用户信息
     * @Param: [cycle]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/7/22 16:58
     */
    public void loadUserInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String sn = data.getString("SERIAL_NUMBER");
        IData result = new DataMap();
        if (StringUtils.isNotBlank(sn))
        {
            result = CSViewCall.callone(this, "SS.FamilyEntranceSVC.loadUserInfo", data);
        }
        setInfo(result);
    }
}
