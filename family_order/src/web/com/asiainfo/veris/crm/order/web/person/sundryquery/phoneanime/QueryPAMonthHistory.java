
package com.asiainfo.veris.crm.order.web.person.sundryquery.phoneanime;
import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryPAMonthHistory extends PersonQueryPage
{
    static transient final Logger logger = Logger.getLogger(QueryPAMonthHistory.class);

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
     * 无线音乐点播查询方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBaseRecord(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);

        IData queryData = new DataMap();

        String serialNum = condParams.getString("PARA_CODE1", "");
        String startDate = condParams.getString("START_DATE", "") + SysDateMgr.START_DATE_FOREVER;
        String endDate = condParams.getString("END_DATE", "") + SysDateMgr.END_DATE;

        StringBuilder operateConditions = new StringBuilder();

        operateConditions.append(serialNum);// 手机号码
        operateConditions.append("|");
        operateConditions.append(startDate);// 开始时间
        operateConditions.append("|");
        operateConditions.append(endDate);// 结束时间

        queryData.put("KIND_ID", "BIP2C092_T2002092_0_0");
        queryData.put("CALLERNO", serialNum);
        queryData.put("SVCTYPEID", "01020617");// 服务请求分类编码
        queryData.put("CONTACTCHANNEL", "08");// 受理渠道
        queryData.put("SERVICETYPEID", "95");// 业务类别
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
     * 无线音乐点播记录查询方法,查询剩余的记录
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
    
    
    public String getProvinceID() throws Exception
    {
        String provinceID = "";

        if ("XINJ".equals(getVisit().getProvinceCode()))
        {
            provinceID = "991";
        }
        if ("HAIN".equals(getVisit().getProvinceCode()))
        {
            provinceID = "898";
        }
        if ("HNAN".equals(getVisit().getProvinceCode()))
        {
            provinceID = "731";
        }
        if ("QHAI".equals(getVisit().getProvinceCode()))
        {
            provinceID = "971";
        }
        if ("SHXI".equals(getVisit().getProvinceCode()))
        {
            provinceID = "290";
        }
        if ("TJIN".equals(getVisit().getProvinceCode()))
        {
            provinceID = "220";
        }
        if ("YUNN".equals(getVisit().getProvinceCode()))
        {
            provinceID = "871";
        }
        return provinceID;
    }
    
    public void submitProcess(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData("cond", true);

        String serialNum = pageData.getString("SUBMIT_MODE1", "");
        String operBusi = pageData.getString("SUBMIT_MODE2", "");
        String sub3 = pageData.getString("SUBMIT_MODE3", "");

        String provinceID = this.getProvinceID();
        

        StringBuilder operateConditions = new StringBuilder();
        operateConditions.append(serialNum); 
        operateConditions.append("|");
        operateConditions.append(operBusi);
        operateConditions.append("|");
        operateConditions.append(sub3);

        IData inputData = new DataMap();
        inputData.put("KIND_ID", "BIP2C094_T2002094_0_0");
        inputData.put("CALLERNO", serialNum);// 号码
        inputData.put("HOMEPROV", provinceID);// 号码
        inputData.put("SVCTYPEID", "0000000000");// 服务请求分类编码
        inputData.put("CONTACTCHANNEL", "0");// 受理渠道
        inputData.put("SERVICETYPEID", "95");// 业务类别
        inputData.put("OPERATETYPEID", "02006");// 操作类型
        inputData.put("OPERATECONDITIONS", operateConditions.toString());

        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subSubmitProcess", inputData);

        setCond(pageData);
        setAjax(infos);
    }


    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setReportPage(IData reportPage);

    public abstract void setResult(IData result);
}
