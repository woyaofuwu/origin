
package com.asiainfo.veris.crm.order.web.person.sundryquery.querygamerecord;


import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryExpenseRecord extends PersonQueryPage
{
    static transient final Logger logger = Logger.getLogger(QueryExpenseRecord.class);

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
            infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subClosePage", pageData);
        }
        setAjax(infos);
    }

    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put("PHONE_NUM", data.getString("SERIAL_NUMBER", ""));

        setCond(data);
    }

    public void operaType(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData("cond", true);

        String operaTypeCode = pageData.getString("ACCEPT_TYPE");
        String serialNum = pageData.getString("PHONE_NUM");

        IData conParams = new DataMap();

        conParams.put("OPERATE_CODE", operaTypeCode);
        conParams.put("CALLERNO", serialNum);
        IDataset result = CSViewCall.call(this, "SS.BaseCommRecordSVC.queryAcceptContent", conParams);

        if (result.size() < 1)
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "获取不良信息无数据！");
        }
        setTypes(result);
        setCond(pageData);
    }

    /**
     * 消费记录查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBaseRecord(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);
        IData queryData = new DataMap();

        String serialNum = condParams.getString("PHONE_NUM", ""); // 手机号码
        String startDate = condParams.getString("START_DATE", ""); // 开始日期
        String endDate = condParams.getString("END_DATE", ""); // 截止日期
        String acceptType = condParams.getString("ACCEPT_TYPE", ""); // 受理类型
        String acceptCont = condParams.getString("ACCEPT_CONTENT", ""); // 受理内容
        if(SysDateMgr.getTimeDiff(startDate, endDate, SysDateMgr.PATTERN_STAND) > 365*24*60*60*1000l){//判断时间跨度不能超过1年
        	CSViewException.apperr(CrmCommException.CRM_COMM_1119, "365");
        }
        StringBuilder operateConditions = new StringBuilder();

        operateConditions.append(serialNum);
        operateConditions.append("|");
        operateConditions.append(startDate);
        operateConditions.append("|");
        operateConditions.append(endDate);
        operateConditions.append("|");
        operateConditions.append(acceptType);
        operateConditions.append("|");
        operateConditions.append(acceptCont);

        queryData.put("KIND_ID", "BIP2C092_T2002092_0_0");
        queryData.put("CALLERNO", serialNum);
        queryData.put("SVCTYPEID", "010303020201");// 服务请求分类编码
        queryData.put("CONTACTCHANNEL", "08");// 受理渠道
        queryData.put("SERVICETYPEID", "57");// 业务类别
        queryData.put("OPERATETYPEID", "01002");// 操作类型
        queryData.put("OPERATECONDITIONS", operateConditions.toString());

        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subBussQureySerive", queryData);

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
     * 黑名单查询方法,查询剩余的记录
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryOtherRecord(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();

        int currPage = getPagination().getCurrent();

        pageData.put("CURR_PAGE", currPage);

        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subUnfinishBussQureySerive", pageData);

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

    public abstract void setTypes(IDataset types);
}
