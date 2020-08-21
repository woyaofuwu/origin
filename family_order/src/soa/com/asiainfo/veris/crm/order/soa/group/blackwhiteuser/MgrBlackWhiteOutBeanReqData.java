
package com.asiainfo.veris.crm.order.soa.group.blackwhiteuser;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class MgrBlackWhiteOutBeanReqData extends MemberReqData
{
    private IDataset SERVICE_INFOS; // 集团平台服务

    public IDataset getSERVICE_INFOS()
    {
        return SERVICE_INFOS;
    }

    public void setSERVICE_INFOS(IDataset service_infos)
    {
        SERVICE_INFOS = service_infos;
    }
}
