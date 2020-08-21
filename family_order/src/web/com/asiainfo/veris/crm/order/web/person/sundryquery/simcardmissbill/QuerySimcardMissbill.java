
package com.asiainfo.veris.crm.order.web.person.sundryquery.simcardmissbill;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：SIM卡漏账查询 作者：GongGuang
 */
public abstract class QuerySimcardMissbill extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 功能：初始化日期和业务区
     */
    public void init(IRequestCycle cycle) throws Exception
    {

        String acceptDate = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
        IData data = new DataMap();
        data.put("cond_ACCEPT_DATE", acceptDate);
        IDataset dataset = CSViewCall.call(this, "SS.QuerySimcardMissbillSVC.querySimcardType", data);
        setSimcardType(dataset);
        setCondition(data);
    }

    /**
     * 功能：SIM卡漏账查询结果
     */
    public void querySimcardMissbill(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        // inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QuerySimcardMissbillSVC.querySimcardMissbill", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        setInfos(results);
        setCount(dataCount.getDataCount());
        setCondition(getData("cond", true));
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【SIM卡漏账查询】数据~";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

    public abstract void setSimcardType(IDataset ScoreTypes);

}
