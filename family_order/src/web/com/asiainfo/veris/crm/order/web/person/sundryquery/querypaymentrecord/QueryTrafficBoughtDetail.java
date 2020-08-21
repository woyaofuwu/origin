package com.asiainfo.veris.crm.order.web.person.sundryquery.querypaymentrecord;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryTrafficBoughtDetail extends PersonQueryPage
{
    static final transient Logger logger = Logger.getLogger(QueryTrafficBoughtDetail.class);

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

    /**
     * 充流量明细查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBaseRecord(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);

        IData queryData = new DataMap();

        String payPhone=condParams.getString("PAY_PHONE_NUM", "");
        String serialNum=condParams.getString("RECHARGE_PHONE_NUM", "");
        StringBuilder operateConditions = new StringBuilder();
        
        operateConditions.append(condParams.getString("PAY_PHONE_NUM", ""));//支付号码
		operateConditions.append("|");
		operateConditions.append(condParams.getString("RECHARGE_PHONE_NUM", ""));//被充值号码
		operateConditions.append("|");
		operateConditions.append(condParams.getString("VALIDITY_PERIOD", ""));//有效期
		operateConditions.append("|");
		operateConditions.append(condParams.getString("START_DATE", "") + " 00:00:00");//开始时间
		operateConditions.append("|");
		operateConditions.append(condParams.getString("END_DATE", "") + " 23:59:59");//结束时间
		operateConditions.append("|");
		operateConditions.append(condParams.getString("TRAFFIC_CHANNEL", ""));//充流量渠道
		
        queryData.put("KIND_ID", "BIP2C092_T2002092_0_0");
        if("".equals(serialNum))
        	queryData.put("CALLERNO", payPhone);
        else
        	queryData.put("CALLERNO", serialNum);
        queryData.put("SVCTYPEID", "0103030108");// 业务类别
        queryData.put("CONTACTCHANNEL", "08");// 受理渠道
        queryData.put("SERVICETYPEID", "64");// 服务请求分类编码
        queryData.put("OPERATETYPEID", "01011");// 操作类型
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
     * 充流量明细查询,查询剩余的记录
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
}


