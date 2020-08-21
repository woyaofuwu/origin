
package com.asiainfo.veris.crm.order.web.person.sundryquery.userinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QuerySnPaymentInfo extends PersonQueryPage
{

    public void init(IRequestCycle cycle) throws Exception
    {
        setTipInfo("输入手机号码，点执行查询！");
    }

    /**
     * 查询手机缴费通使用情况
     * 
     * @param cycle
     * @throws Exception
     * @author huanghui@asiainfo.com
     */
    public void querySnPaymentInfo(IRequestCycle cycle) throws Exception
    {
        IData condPrams = getData("cond", true);
        condPrams.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataOutput results = CSViewCall.callPage(this, "SS.QuerySnPaymentInfoSVC.querySnPaymentInfo", condPrams, getPagination("page"));

        if (IDataUtil.isEmpty(results.getData()))
        {
            setTipInfo("获取手机缴费通使用情况无数据！");
        }
        else
        {
            setListInfos(results.getData());
            setListCount(results.getDataCount());
        }
    }

    public abstract void setInfo(IData info);

    public abstract void setListCount(long listCount);

    public abstract void setListInfo(IData info);

    public abstract void setListInfos(IDataset infos);

    public abstract void setTipInfo(String tipInfo);
}
