
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.qry;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;

public class TransSnB2SnAction implements ITrans
{

    @Override
    public final void transRequestData(IData idata) throws Exception
    {
        addSubDataBefore(idata);

    }

    // 子类重载
    protected void addSubDataBefore(IData idata) throws Exception
    {
        IDataUtil.chkParam(idata, "SERIAL_NUMBER_B");
        idata.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER_B", ""));
    }

}
