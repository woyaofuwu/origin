
package com.asiainfo.veris.crm.order.web.person.plat.mobilepayment;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class IBossAccountPay extends PersonBasePage
{
    /**
     * 跨省充值号码鉴权
     * 
     * @param cycle
     * @throws Exception
     */
    public void IBossAccountPayCheck(IRequestCycle cycle) throws Exception
    {

        IData condData = getData("cond", true);
        IData result = new DataMap();
        IDataset checkRetDatas = CSViewCall.call(this, "SS.IBossMobilePaySVC.accountPayCheck", condData);
        IData checkRet = checkRetDatas.getData(0);
        if ("0".equals(checkRet.getString("X_RSPTYPE")) && "0000".equals(checkRet.getString("X_RSPCODE")) && checkRet.getString("alertInfo", "").trim().length() == 0)
        {
            result.put("ALERT_INFO", "跨省充值号码鉴权成功!");
            result.put("SUCCESS_FLAG", "true");
            condData.put("cond_IsCheckPass", "true");
            condData.put("cond_PassNumber", condData.getString("SERIAL_NUMBER"));// 保留原鉴权通过的手机号码，防止充值时换号码
        }
        else
        {
            result.put("ALERT_INFO", "跨省充值号码鉴权失败!" + checkRet.getString("alertInfo", ""));
            result.put("SUCCESS_FLAG", "false");
            condData.put("cond_IsCheckPass", "false");
            condData.put("cond_PassNumber", "");
        }
        this.setAjax(result);
        setCondition(condData);
        setCustInfo(checkRet);
    }

    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("cond_ROUTE", "01");// 默认手机路由
        setCondition(data);
    }

    /**
     * 打印数据加载
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadPrintData(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData returnData = new DataMap();
        IDataset rePrintDatas = CSViewCall.call(this, "SS.IBossMobilePaySVC.getAccPayData", data);
        returnData.put("PRINT_DATA", rePrintDatas);
        setAjax(returnData);
    }

    /**
     * 充值提交时触发的方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        IDataset retDataset = CSViewCall.call(this, "SS.IBossMobilePaySVC.accountPay", data);
        IData result = retDataset.getData(0);
        if ("0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
        {
            result.put("PRINT_FLAG", "");
            this.setAjax(result);
        }
        else
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "操作失败！" + result.getString("X_RSPDESC"));
        }

    }

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData data);

    public abstract void setInfo(IData info);

}
