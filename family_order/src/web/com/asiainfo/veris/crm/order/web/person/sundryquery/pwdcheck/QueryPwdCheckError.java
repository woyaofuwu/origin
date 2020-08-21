
package com.asiainfo.veris.crm.order.web.person.sundryquery.pwdcheck;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryPwdCheckError extends PersonBasePage
{
    /**
     * 页面初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        String curDate = SysDateMgr.getSysDate();
        String sevenDayBefore = SysDateMgr.getAddHoursDate(curDate, -(7 * 24));// 获得7天前的日期
        IData data = new DataMap();
        data.put("cond_START_DATE", sevenDayBefore);
        data.put("cond_END_DATE", curDate);
        setCondition(data);

    }

    /**
     * 客户密码校验失败查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryPwdCheckErrInfos(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put("END_DATE", inparam.getString("END_DATE") + SysDateMgr.END_DATE);
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryPwdCheckErrorSVC.queryPwdCheckErrInfos", inparam, getPagination("pwdChkInfoNav"));
        IDataset result = dataCount.getData();
        setErrorInfos(result);
        setPageCount(dataCount.getDataCount());
        setCondition(getData("cond", true));
        setAjax("DATA_COUNT", "" + result.size());
    }

    public abstract void setCondition(IData condition);

    public abstract void setErrorInfos(IDataset errorInfos);

    public abstract void setPageCount(long pageCount);
}
