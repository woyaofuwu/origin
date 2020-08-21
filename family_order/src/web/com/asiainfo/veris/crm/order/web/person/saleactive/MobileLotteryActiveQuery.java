
package com.asiainfo.veris.crm.order.web.person.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/** 营销活动受理 */
public abstract class MobileLotteryActiveQuery extends PersonBasePage
{

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        // String stuMobilePhone = data.getString("STU_MOBILPHONE", "");
        // setStuMobilePhone(stuMobilePhone);
        // String paramCode = data.getString("PARAM_CODE", "");
        // setParamCode(paramCode);
    }

    public void queryLotteryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);
        data.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER"));
        data.put("MONTH", data.getString("cond_MONTH"));
        data.put("PRIZE_TYPE_CODE", data.getString("cond_PRIZE_TYPE_CODE"));
        IDataOutput lotteryInfos = CSViewCall.callPage(this, "SS.MobileLotteryActiveSVC.queryLotteryInfo", data, getPagination("navt"));
        setInfos(lotteryInfos.getData());
        setCount(lotteryInfos.getDataCount());
    }

    public abstract void setCondition(IData custInfo);

    public abstract void setCount(long count);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset goods);

}
