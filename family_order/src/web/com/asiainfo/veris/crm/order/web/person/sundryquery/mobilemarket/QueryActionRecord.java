package com.asiainfo.veris.crm.order.web.person.sundryquery.mobilemarket; 




import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryActionRecord extends PersonQueryPage
{
    static transient final Logger logger = Logger.getLogger(QueryActionRecord.class);

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
 * 用户轨迹核查
 * @param cycle
 * @throws Exception
 */
    public void queryBaseBehavior(IRequestCycle cycle) throws Exception{
    	IData condParams=getData("cond",true);
    	String trade_id=condParams.getString("PARA_CODE1");
    	IData queryData=new DataMap();
    	 queryData.put("TRADE_ID", trade_id);
    	 queryData.put("KIND_ID", "BIP2C092_T2002092_0_0");//业务属性+交易属性+。。
        // queryData.put("CALLERNO", serialNum);
         queryData.put("SVCTYPEID", "10010105130299");// 服务请求分类编码
         queryData.put("CONTACTCHANNEL", "08");// 受理渠道
         
         queryData.put("SERVICETYPEID", "41");// 业务类别
         queryData.put("OPERATETYPEID", "01005");// 操作类型
         queryData.put("OPERATECONDITIONS",trade_id );
         IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subBussQureyAction", queryData);

         IDataset results = infos.getData(0).getDataset("PDTS_INFO");
         IData result = infos.getData(0).getData("PAGE_INFO");
         result.put("TRADE_ID", trade_id);
         IData reportPage = infos.getData(0).getData("REPORTPAGE");
         setCond(condParams);
         setInfos(results);
         setResult(result);
         setReportPage(reportPage);
    	
    	
    }
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put("PARA_CODE1", data.getString("TRADE_ID", ""));

        setCond(data);
    }

   
    /**
     * 用户行为轨迹核查,查询剩余的记录
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryOtherRecord(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();

        int currPage = getPagination().getCurrent();
        
        pageData.put("CURR_PAGE", currPage);

        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subUnfinishBussQureyAction", pageData);

        IData result = infos.getData(0).getData("PAGE_INFO");
        IDataset results = infos.getData(0).getDataset("PDTS_INFO");
        IData reportPage = result.getData("REPORTPAGE");

        setResult(result);
        setInfos(results);
        setReportPage(reportPage);
    }
    

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);

    public abstract void setReportPage(IData reportPage);

    public abstract void setResult(IData result);
}