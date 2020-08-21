
package com.asiainfo.veris.crm.order.web.person.sundryquery.cellphonevideo;

import java.util.Map;
import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryUseRecord extends PersonQueryPage
{
    static transient final Logger logger = Logger.getLogger(QueryUseRecord.class);

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

        data.put("PARA_CODE1", data.getString("SERIAL_NUMBER", ""));

        setCond(data);
    }

    /**
     * 黑名单屏蔽日志查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBaseRecord(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);

        IData queryData = new DataMap();

        String serialNum = condParams.getString("PARA_CODE1", "");

        StringBuilder operateConditions = new StringBuilder();

		operateConditions.append(condParams.getString("PARA_CODE1", ""));//手机号码
		operateConditions.append("|");
		operateConditions.append(condParams.getString("START_DATE", "") +SysDateMgr.START_DATE_FOREVER);//开始时间
		operateConditions.append("|");
		operateConditions.append(condParams.getString("END_DATE", "") +SysDateMgr.END_DATE);//结束时间	

        queryData.put("KIND_ID", "BIP2C092_T2002092_0_0");
        queryData.put("CALLERNO", serialNum);
        queryData.put("SVCTYPEID", "0103030405");// 服务请求分类编码
        queryData.put("CONTACTCHANNEL", "08");// 受理渠道
        queryData.put("SERVICETYPEID", "82");// 业务类别
        queryData.put("OPERATETYPEID", "01007");// 操作类型
        queryData.put("OPERATECONDITIONS", operateConditions.toString());
		
        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subBussQureySerive", queryData);
        
    
        IDataset results = infos.getData(0).getDataset("PDTS_INFO");
        
        /*
         * 将使用记录查询结果中的流量字段单位由Byte改成KByte。
         */
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				IData tempResult = results.getData(i);
				String resultStr = tempResult.getString("RSRV_STR7");
				if (!"".equals(resultStr)) {
					BigDecimal RSRV_STR7 = new BigDecimal(resultStr);
					BigDecimal new_RSRV_STR7 = RSRV_STR7.divide(new BigDecimal(1024));
					results.getData(i).put("RSRV_STR7", new_RSRV_STR7);
				}
			}
		}
		
        IData result = infos.getData(0).getData("PAGE_INFO");
        result.put("CALLERNO", serialNum);
        IData reportPage = infos.getData(0).getData("REPORTPAGE");

        setCond(condParams);
        setInfos(results);
        setResult(result);
        setReportPage(reportPage);
    }

    /**
     * 黑名单屏蔽日志查询,查询剩余的记录
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
        
        /*
         * 将使用记录查询结果中的流量字段单位由Byte改成KByte。
         */
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				IData tempResult = results.getData(i);
				String resultStr = tempResult.getString("RSRV_STR7");
				if (!"".equals(resultStr)) {
					BigDecimal RSRV_STR7 = new BigDecimal(resultStr);
					BigDecimal new_RSRV_STR7 = RSRV_STR7.divide(new BigDecimal(1024));
					results.getData(i).put("RSRV_STR7", new_RSRV_STR7);
				}
			}
		}
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
