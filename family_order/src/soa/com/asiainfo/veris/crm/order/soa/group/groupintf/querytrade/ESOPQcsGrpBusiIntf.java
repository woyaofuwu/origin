
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ESOPCall;

public class ESOPQcsGrpBusiIntf
{

    public static IDataset getEosInfo(IData param) throws Throwable
    {
        IData inData = new DataMap();
        inData.put("X_TRANS_CODE", "ITF_EOS_QcsGrpBusi");
        inData.put("X_SUBTRANS_CODE", "GetEosInfo");
        inData.put("NODE_ID", param.getString("NODE_ID", ""));
        inData.put("IBSYSID", param.getString("IBSYSID", ""));
        inData.put("SUB_IBSYSID", param.getString("SUB_IBSYSID", ""));
        inData.put("OPER_CODE", param.getString("OPER_CODE", ""));
        inData.put("index", param.getString("index", ""));
        IDataset resultDataset = ESOPCall.callESOP("ITF_EOS_QcsGrpBusi", inData);
        if (IDataUtil.isNotEmpty(resultDataset))
        {
            for (int i = 0, k = resultDataset.size(); i < k; i++)
            {
                IData resultData = resultDataset.getData(i);
                resultData.remove("TRADE_CITY_CODE");
                resultData.remove("TRADE_DEPART_ID");
                resultData.remove("TRADE_STAFF_ID");
                resultData.remove("IN_MODE_CODE");
            }
        }
        return resultDataset;
    }

}
