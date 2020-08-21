
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class TradeHistoryInfo extends PersonBasePage
{

    /**
     * 获取传递的初始参数(USER_ID和CUST_ID)
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset initWeb = CSViewCall.call(this, "SS.GetUser360ViewSVC.initTradeHistoryInfo", data);
        if (IDataUtil.isNotEmpty(initWeb))
        {
            setCondition(initWeb.getData(0));
        }
        IDataset tradeTypeCode = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryTradeTypeCode", data);
        setTradeTypeCode(tradeTypeCode);
    }

    public void initSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setInit(data);
    }

    /**
     * 用户关系信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData dP = getData();
        // IData data = new DataMap();
        // data.put("USER_ID",dP.getString("USER_ID", ""));
        // data.put("CUST_ID",dP.getString("CUST_ID", ""));
        // data.put("TRADE_ID",dP.getString("TRADE_ID", ""));
        // data.put("TIME_CHECK",dP.getString("BOOK_CHECK", ""));
        // data.put("TIME_CHECK",dP.getString("TIME_CHECK", ""));
        // data.put("TRADE_TYPE_CODE",dP.getString("TRADE_TYPE_CODE", ""));
        // data.put("START_DATE",dP.getString("START_DATE", ""));
        // data.put("END_DATE",dP.getString("END_DATE", ""));
        // data.put("SERIAL_NUMBER",dP.getString("SERIAL_NUMBER", ""));
        // data.put("HISTORY_QUERY_TYPE", dP.getString("HISTORY_QUERY_TYPE", ""));
        IDataOutput output = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryTradeHistoryInfo", dP, getPagination("ThInfonav"));
        setCondition(dP);
        setInfos(output.getData());
        setInfosCount(output.getDataCount());
    }

    public abstract void setCondition(IData condition);// 存储查询条件

    public abstract void setInfos(IDataset infos);// 存储业务历史信息

    public abstract void setInfosCount(long infosCount);

    public abstract void setInit(IData init);

    public abstract void setTradeTypeCode(IDataset tradeTypeCode);// 存储业务类型编码
}
