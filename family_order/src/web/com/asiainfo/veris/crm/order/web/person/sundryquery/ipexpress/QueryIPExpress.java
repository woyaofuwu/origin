
package com.asiainfo.veris.crm.order.web.person.sundryquery.ipexpress;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryIPExpress extends PersonBasePage
{

    /**
     * IP直通车信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryIPExpress(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        IData newConData = getData("cond");

        IDataOutput outPut = CSViewCall.callPage(this, "SS.QueryIPExpressSVC.queryIPExpress", data, getPagination("paginNav"));
        if (IDataUtil.isEmpty(outPut.getData()))
        {

            this.setAjax("ALERT_INFO", "获取IP直通车无数据!");

        }
        if (this.hasPriv("CRM_QUERYIPPHONEEXPORT"))
        {

            newConData.put("EXPORT", "true");

        }
        else
        {

            newConData.put("EXPORT", "false");

        }
        setInfos(outPut.getData());
        setCount(outPut.getDataCount());
        setCond(newConData);
    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset ipExpressInfos);

}
