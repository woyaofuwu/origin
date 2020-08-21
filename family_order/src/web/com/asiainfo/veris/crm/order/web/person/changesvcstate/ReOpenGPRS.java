
package com.asiainfo.veris.crm.order.web.person.changesvcstate;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public class ReOpenGPRS extends PersonBasePage
{
    protected static Logger log = Logger.getLogger(ReOpenGPRS.class);

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        CSViewCall.call(this, "SS.ReOpenGPRSSVC.checkWhetherCanDoIt", data);

    }

    /**
     * 提交方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData();
        inparam.put("SERVICE_ID", "22");
        inparam.put("OPER_CODE", "05");
        inparam.put("SERV_TYPE", "0");

        IDataset result = CSViewCall.call(this, "SS.ReOpenGPRSRegSVC.tradeReg", inparam);
        setAjax(result);
    }
}
