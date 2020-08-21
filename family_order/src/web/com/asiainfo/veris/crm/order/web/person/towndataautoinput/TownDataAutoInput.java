
package com.asiainfo.veris.crm.order.web.person.towndataautoinput;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户动态策略计费控制信息导入
 */
public abstract class TownDataAutoInput extends PersonBasePage
{
    private static Logger logger = Logger.getLogger(TownDataAutoInput.class);

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     *             zhuyu 2014-3-18
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.TownDataAutoInputSVC.tradeReg", data);
    }

}
