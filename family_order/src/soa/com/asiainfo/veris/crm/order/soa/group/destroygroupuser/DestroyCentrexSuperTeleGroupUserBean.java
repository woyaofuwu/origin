
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class DestroyCentrexSuperTeleGroupUserBean extends GroupOrderBaseBean
{

    public void actOrderDataOther(IData map) throws Exception
    {
        // 注销集团用户信息
        GrpInvoker.ivkProduct(map, BizCtrlType.DestoryUser, "CreateClass");

        IDataset mebSvcDataList = map.getDataset("MEB_SVC_DATA");

        for (int i = 0, row = mebSvcDataList.size(); i < row; i++)
        {
            IData mebSvcData = mebSvcDataList.getData(i);

            if (IDataUtil.isNotEmpty(mebSvcData))
            {
                // 调用成员服务
                CSAppCall.call("SS.ChangeCentrexSuperTeleMemberSimpleSVC.crtTrade", mebSvcData);
            }
        }
    }

    protected String setOrderTypeCode() throws Exception
    {
        // 融合总机集团用户注销业务类型
        return "4623";
    }

}
