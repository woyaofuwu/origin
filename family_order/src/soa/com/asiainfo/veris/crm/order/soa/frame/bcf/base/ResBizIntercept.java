
package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.invoker.impl.ServiceMethodIntercept;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.crmlog.CrmLog;

public class ResBizIntercept extends ServiceMethodIntercept
{
    @Override
    public boolean after(String svcName, IData head, IData inData, IDataset outDataset) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {
            // HXYD-YZ-REQ-20140704-005关于在统计库上建表并做数据记录的需求
            // 记录一级BOSS调用服务日志到统计库表TF_B_EPOINT_LOG。
            try
            {
                CrmLog.logBoss(svcName, head, inData, outDataset);
            }
            catch (Exception e)
            {
            }
        }
        return true;
    }

    @Override
    public boolean before(String svcName, IData head, IData inData) throws Exception
    {
        return true;
    }
}
