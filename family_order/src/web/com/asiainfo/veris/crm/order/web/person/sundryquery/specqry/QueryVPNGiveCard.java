
package com.asiainfo.veris.crm.order.web.person.sundryquery.specqry;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryVPNGiveCard extends PersonBasePage
{

    /**
     * 金额处理 分转化成元
     * 
     * @param str
     * @throws Exception
     */
    private String dataMoneyDeal(String str) throws Exception
    {
        String string = null;
        if (str == null || "".equals(str))
        {
            str = "0";
        }
        string = FeeUtils.Fen2Yuan(str);
        return string;
    }

    /**
     * 初始化界面提示信息
     * 
     * @param cycle
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData();
        inputData.put("PARENT_AREA_CODE", this.getTradeEparchyCode());
        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.QueryVPNGiveCardSVC.queryInitInfo", inputData);

        IData idata = new DataMap();
        idata.put("cond_PARA_CODE2", SysDateMgr.getSysDate().substring(0, 7));

        setArealist(result);
        setCondition(idata);
    }

    /**
     * 累计未兑金额转换
     * 
     * @param cycle
     */
    public IDataset noChangeMoney(IDataset idataset) throws Exception
    {
        if (idataset == null)
        {
            return null;
        }
        for (int i = 0; i < idataset.size(); i++)
        {
            IData tdata = idataset.getData(i);
            if (tdata.getString("PARA_CODE7") == null || "".equals(tdata.getString("PARA_CODE7")))
            {
                tdata.put("PARA_CODE9", tdata.getString("PARA_CODE6"));
            }
            else
            {
                tdata.put("PARA_CODE9", Integer.valueOf(tdata.getString("PARA_CODE6")) - Integer.valueOf(tdata.getString("PARA_CODE7")));
            }
            tdata.put("PARA_CODE5", dataMoneyDeal(tdata.getString("PARA_CODE5")));
            tdata.put("PARA_CODE6", dataMoneyDeal(tdata.getString("PARA_CODE6")));
            tdata.put("PARA_CODE7", dataMoneyDeal(tdata.getString("PARA_CODE7")));
            tdata.put("PARA_CODE9", dataMoneyDeal(tdata.getString("PARA_CODE9")));
        }

        return idataset;
    }

    /**
     * 执行查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryVPNGiveCard(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData("cond");
        Pagination page = getPagination("pageNav");
        inputData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.QueryVPNGiveCardSVC.queryVPNGiveCard", inputData, page);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }
        else
        {
            noChangeMoney(dataset);
        }
        setPageCount(result.getDataCount());
        // 设置页面返回数据
        setInfos(dataset);
    }

    public abstract void setArealist(IDataset arealist);

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);
}
