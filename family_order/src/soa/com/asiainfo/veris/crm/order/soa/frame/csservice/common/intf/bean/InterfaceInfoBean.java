
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.intf.bean;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.intf.InterfaceInfoQry;

/**
 * @author Administrator
 */
public class InterfaceInfoBean extends CSBizBean
{
    private static Logger log = Logger.getLogger(InterfaceInfoBean.class);

    public IDataset getInterfaceParam(IData data) throws Exception
    {
        return InterfaceInfoQry.getInterfaceParam(data);
    }

    public IDataset getSceneById(IData data) throws Exception
    {
        IData data2 = new DataMap();
        data2.put("INTER_ID", data.getString("cond_INTERFACE_NAME"));
        return InterfaceInfoQry.getSceneById(data2);
    }

    public IDataset getSceneInfoById(IData data) throws Exception
    {
        IData data2 = new DataMap();
        data2.put("SCENE_ID", data.getString("cond_INTERFACE_SCENE"));
        data2.put("INTER_ID", data.getString("cond_INTERFACE_NAME"));
        return InterfaceInfoQry.getSceneInfoById(data2);
    }

    public IDataset invokeInterface(IData data) throws Exception
    {
        String iboss = data.getString("IBOSS", "");

        if (StringUtils.isBlank(iboss))
        {// 非IBOSS接口 非IBOSS落地接口
            String addr = data.getString("INTERFACE_ADDR");
            String svcName = data.getString("INTERFACE_CODE");
            IDataInput dataInput = (IDataInput) data.get("INTERFACE_DATAINPUT");
            return CSAppCall.call(addr, svcName, dataInput, true);
        }
        else
        {// IBOSS接口
            String kindId = data.getString("INTERFACE_CODE");
            IDataInput dataInput = (IDataInput) data.get("INTERFACE_DATAINPUT");
            IData param = dataInput.getData();
            if (StringUtils.isBlank(param.getString("KIND_ID")))
            {
                param.put("KIND_ID", kindId);
            }
            return IBossCall.invokeIBossInterface(param);
        }

    }

    public boolean logInterface(IData data) throws Exception
    {
        data.put("INVOKE_DATE", SysDateMgr.getSysTime());
        return InterfaceInfoQry.logInterface(data);
    }

    public IDataset queryAllInterfaceInfos(IData data) throws Exception
    {
        return InterfaceInfoQry.queryAllInterfaceInfos(data);
    }

    public IDataset queryInterfaceById(IData data) throws Exception
    {
        IData data2 = new DataMap();
        data2.put("INTER_ID", data.getString("cond_INTERFACE_NAME"));
        return InterfaceInfoQry.queryInterfaceById(data2);
    }

    public boolean upInterfaceResultById(IData data) throws Exception
    {
        return InterfaceInfoQry.upInterfaceResultById(data);
    }
}
