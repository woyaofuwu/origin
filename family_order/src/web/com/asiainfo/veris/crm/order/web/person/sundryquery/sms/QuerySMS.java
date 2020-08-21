
package com.asiainfo.veris.crm.order.web.person.sundryquery.sms;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：短信查询 作者：GongGuang
 */
public abstract class QuerySMS extends PersonBasePage
{

    public abstract IDataset getInfos10086();

    public abstract IDataset getInfos10658666();

    /**
     * 功能：初始化开始、结束日期
     */
    public void init(IRequestCycle cycle) throws Exception
    {

        IData result = CSViewCall.callone(this, "SS.QuerySmsSVC.queryInitCondition", new DataMap());
        if (IDataUtil.isNotEmpty(result))
        {
            IData data = new DataMap();
            data.put("cond_START_DATE", result.getString("START_DATE")); // 得到本月的第一天
            data.put("cond_END_DATE", result.getString("END_DATE"));// 格式为YYYY-MM-DD
            setCondition(data);
        }

    }

    /**
     * 功能：短信结果查询
     */
    public void querySms(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        String queryMode = inparam.getString("QUERY_MODE");
        Pagination page = null;
        if ("1".equals(queryMode))
        {
            page = getPagination("pageNav10086");// 用于分页
        }
        else
        {
            page = getPagination("pageNavNon10086");// 用于分页
        }
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QuerySmsSVC.querySms", inparam, page);
        IDataset results = dataCount.getData();
        if ("1".equals(queryMode))
        {
            setInfos10086(results);
        }
        else
        {
            setInfos10658666(results);
        }
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【短信查询】数据~";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setPageCount(dataCount.getDataCount());
        setCondition(getData("cond", true));
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfos10086(IDataset infos);

    public abstract void setInfos10658666(IDataset infos);

    public abstract void setPageCount(long count);

}
