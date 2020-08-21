
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;

public class EntityInfoQry
{
    public IDataset QueryEntityCard(IData inparam) throws Exception
    {
        IData param = new DataMap();
        IData data = new DataMap();
        IDataset dataset = new DatasetList();
        // String tradeData = pd.getParameter("tradeData");
        // if (tradeData != null && !"".equals(tradeData)) {
        // td = new TradeData(tradeData);
        // }
        // param.putAll(getCommonParam(td));
        param.put("KIND_ID", "BIP1A119_T1000119_0_0");// 交易唯一标识
        param.put("X_TRANS_CODE", "");
        param.put("IN_MODE_CODE", "6");
        param.put("CARD_NO", inparam.getString("CARD_NO"));
        // data = (IData) HttpHelper.callHttpSvc(pd, "IBOSS", param); today
        // dataset.add(data); today
        if (dataset == null)
        {

        }
        // 测试数据
        data.put("X_RSPTYPE", "0");
        data.put("X_RESULTCODE", "0");
        data.put("X_RSPCODE", "0000");
        data.put("RSLT", "00");
        data.put("CARD_NO", "11111111111");
        data.put("CARDSTATUS", "00");
        data.put("HOMEPROV", "7311");
        data.put("ROAMFLAG", "0");
        data.put("BINDINGNUM", "13407320007");
        data.put("EFFETITIME", "20100120");
        data.put("ABATETIME", SysDateMgr.getEndCycle20501231());
        dataset.add(data);
        return dataset;
    }
}
