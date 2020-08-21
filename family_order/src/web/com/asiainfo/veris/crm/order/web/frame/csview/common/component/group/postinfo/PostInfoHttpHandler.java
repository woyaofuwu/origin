
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.postinfo;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class PostInfoHttpHandler extends CSBizHttpHandler
{

    public void queryMemberInfo() throws Exception
    {
        String strMebSn = getData().getString("cond_SERIAL_NUMBER");
        IData idUserInfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, strMebSn, false);
        if (idUserInfo != null)
        {
            setAjax("flag", "true");
        }
        else
        {
            setAjax("flag", "false");
        }

    }

}
