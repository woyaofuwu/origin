
package com.asiainfo.veris.crm.order.web.person.plat.mobilepayment;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class IBossAccountDec extends PersonBasePage
{

    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);
    }

    /**
     * 提交时触发的方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData allData = getData();
        String transaction = allData.getString("TRANSACTIONS");
        String[] transactions = transaction.split(",");
        IData data = getData("cond", true);

        data.put("BOSS_SEQ", transactions[0]);
        data.put("PAYED", transactions[1]);
        data.put("CHARGE_ID", transactions[2]);
        IDataset rtnDataset = CSViewCall.call(this, "SS.IBossMobilePaySVC.accountDec", data);
        IData result = rtnDataset.getData(0);

        if ("0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
        {
            this.setAjax(result);
        }
        else
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "操作失败！" + result.getString("X_RSPDESC"));
        }
    }

    /**
     * 查询当日交易记录,以全能够冲正. (充值冲正不允许隔日操作)
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryAccountPay(IRequestCycle cycle) throws Exception
    {

        IData data = getData("cond", true);

        IDataOutput results = CSViewCall.callPage(this, "SS.IBossMobilePaySVC.queryAccountPay", data, getPagination("IBossAccountDecNav"));
        this.setCondition(getData("cond"));
        setInfos(results.getData());
        setCount(results.getDataCount());
        IData ajaxData = new DataMap();

        if (IDataUtil.isEmpty(results.getData()))
        {
            ajaxData.put("SUCCESS_FLAG", "false");
            ajaxData.put("ALERT_INFO", "没有查询到该号码的手机支付充值信息！");
        }
        else
        {
            ajaxData.put("SUCCESS_FLAG", "true");
            ajaxData.put("ALERT_INFO", "");
        }

        setAjax(ajaxData);
    }

    public abstract void setCondition(IData condition);

    public abstract void setCount(long count);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
