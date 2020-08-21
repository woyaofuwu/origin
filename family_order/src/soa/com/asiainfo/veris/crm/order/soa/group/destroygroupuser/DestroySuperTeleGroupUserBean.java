
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class DestroySuperTeleGroupUserBean extends GroupOrderBaseBean
{

    public void actOrderDataOther(IData map) throws Exception
    {
        // 注销集团用户信息
        GrpInvoker.ivkProduct(map, BizCtrlType.DestoryUser, "CreateClass");

        IData mebSvcData = map.getData("MEB_SVC_DATA");

        if (IDataUtil.isNotEmpty(mebSvcData))
        {
            // 调用服务注销总机号码
            CSAppCall.call("SS.ChangeSuperTeleMebElementSimpleSVC.crtTrade", mebSvcData);
        }
    }

    protected String setOrderTypeCode() throws Exception
    {
        // 移动总机集团用户注销业务类型
        return "2923";
    }

}
