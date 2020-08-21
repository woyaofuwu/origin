
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetstop;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: likai3
 */

public abstract class CttBroadBandStop extends PersonBasePage
{

    /**
     * 宽带服务变更业务类提交默认方法
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.ChangeSvcStateRegSVC.tradeReg", data);
        setAjax(dataset);
    }
}
