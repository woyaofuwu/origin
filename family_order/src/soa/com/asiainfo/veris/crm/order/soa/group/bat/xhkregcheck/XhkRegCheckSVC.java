
package com.asiainfo.veris.crm.order.soa.group.bat.xhkregcheck;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class XhkRegCheckSVC extends GroupOrderService
{
    private static final long serialVersionUID = 2696971883488070849L;

    public IDataset xhkRegCheck(IData inparam) throws Exception
    {
        IData param = new DataMap();
        param.put("BATCH_ID", inparam.getString("BATCH_ID"));
        //param.put("SERIAL_NUMBER", inparam.getString("SERIAL_NUMBER"));
        param.put("DEAL_RESULT", "符合");
        param.put("DEAL_DESC", "符合");
        XhkRegCheckBean.updateBatDealByOperateIdSn(param);
        
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("ORDER_ID", "0");
        dataset.add(data);
        return dataset;
    }
}
