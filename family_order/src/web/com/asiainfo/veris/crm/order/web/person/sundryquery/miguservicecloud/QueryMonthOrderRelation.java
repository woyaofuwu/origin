package com.asiainfo.veris.crm.order.web.person.sundryquery.miguservicecloud;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryMonthOrderRelation extends PersonBasePage{
	 static transient final Logger logger = Logger.getLogger(QueryMonthOrderRelation.class);

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

	    /**
	     * 包月订购关系查询
	     * 
	     * @param cycle
	     * @throws Exception
	     */
	    public void queryBaseRecord(IRequestCycle cycle) throws Exception
	    {
	    	

	    	IData condParams = getData("cond", true);

	        IData queryData = new DataMap();

	        String serialNum = condParams.getString("PARA_CODE1", ""); // 手机号码

	        StringBuilder operateConditions = new StringBuilder();

	        operateConditions.append(serialNum);

	        queryData.put("KIND_ID", "BIP2C092_T2002092_0_0");
	        queryData.put("CALLERNO", serialNum);
	        queryData.put("SVCTYPEID", "01010599");// 服务请求分类编码
	        queryData.put("CONTACTCHANNEL", "08");// 受理渠道
	        queryData.put("SERVICETYPEID", "136");// 业务类别
	        queryData.put("OPERATETYPEID", "01003");// 操作类型
	        queryData.put("OPERATECONDITIONS", operateConditions.toString());
	        
	        int currPage = getPagination("recordNav").getCurrent();
	        queryData.put("QUERYPAGENUM", currPage);
	        
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

	        int currPage = getPagination("recordNav").getCurrent();

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