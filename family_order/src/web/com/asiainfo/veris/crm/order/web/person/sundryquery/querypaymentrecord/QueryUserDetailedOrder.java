
package com.asiainfo.veris.crm.order.web.person.sundryquery.querypaymentrecord;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryUserDetailedOrder extends PersonQueryPage
{
    static final transient Logger logger = Logger.getLogger(QueryUserDetailedOrder.class);

    /**
     * 关闭页面调用归档接口和更新表数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void closePage(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset infos = new DatasetList();

        String indictSeq = pageData.getString("INDICTSEQ");

        // 判断已经生成服务请求标识和未归档的查询，才调用关闭查询页面归档接口
        if (indictSeq != null && ("0".equals(pageData.getString("PIGEONHOLE"))))
        {
            // pageData.put(Route.ROUTE_EPARCHY_CODE, "0731");
            infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subEparchyClosePage", pageData);
        }
        setAjax(infos);
    }

    /**
     * 账户历史查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBaseRecord(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);

        IData queryData = new DataMap();

        String serialNum = condParams.getString("TENANT_NUM", "");// 用户手机号码

        StringBuilder operateConditions = new StringBuilder();
        /*
         * operateConditions.append(condParams.getString("TRADE_CODE", ""));//交易码 operateConditions.append("|");
         */
        operateConditions.append(condParams.getString("TENANT_NUM", ""));// 商户编号
        operateConditions.append("|");
        operateConditions.append(condParams.getString("ORDER_NUM", ""));// 订单号
        operateConditions.append("|");
        operateConditions.append(condParams.getString("BILL_TIME", "").replaceAll("-", ""));// 下单日期

        queryData.put("KIND_ID", "BIP2C092_T2002092_0_0");
        queryData.put("CALLERNO", serialNum);
        queryData.put("SVCTYPEID", "0103030105");// 业务类别
        queryData.put("CONTACTCHANNEL", "08");// 受理渠道
        queryData.put("SERVICETYPEID", "64");// 服务请求分类编码--点播查询
        queryData.put("OPERATETYPEID", "01005");// 操作类型
        queryData.put("OPERATECONDITIONS", operateConditions.toString());
        // queryData.put(Route.ROUTE_EPARCHY_CODE, "0731");

        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subEparchyBussQureySerive", queryData);

        IDataset results = infos.getData(0).getDataset("PDTS_INFO");
        IData result = infos.getData(0).getData("PAGE_INFO");
        result.put("CALLERNO", serialNum);
        IData reportPage = infos.getData(0).getData("REPORTPAGE");

        setCond(condParams);
        setInfos(results);
        setResult(result);
        setReportPage(reportPage);
    }

    /**
     * 账户历史查询方法,查询剩余的记录
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryOtherRecord(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();

        int currPage = getPagination().getCurrent();

        pageData.put("CURR_PAGE", currPage);
        // pageData.put(Route.ROUTE_EPARCHY_CODE, "0731");
        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subEparchyUnfinishBussQureySerive", pageData);

        IData result = infos.getData(0).getData("PAGE_INFO");
        IDataset results = infos.getData(0).getDataset("PDTS_INFO");
        IData reportPage = result.getData("REPORTPAGE");

        setResult(result);
        setInfos(results);
        setReportPage(reportPage);
    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setReportPage(IData reportPage);

    public abstract void setResult(IData result);
}
