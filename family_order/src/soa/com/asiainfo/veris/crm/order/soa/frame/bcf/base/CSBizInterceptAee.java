
package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.invoker.impl.ServiceMethodIntercept;
import com.asiainfo.veris.crm.order.soa.frame.bcf.syslog.SysLog;

public class CSBizInterceptAee extends ServiceMethodIntercept
{
    @Override
    public boolean after(String svcName, IData head, IData inData, IDataset outDataset) throws Exception
    {
        // 系统输出日志
        SysLog.logDataOut(svcName, inData, outDataset);

        return true;
    }

    @Override
    public boolean before(String svcName, IData head, IData inData) throws Exception
    {
        // 系统输入日志
        SysLog.logDataIn(svcName, head, inData);

        return true;
    }
}
