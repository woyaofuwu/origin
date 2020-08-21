package com.asiainfo.veris.crm.order.web.person.addresslist;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QryOrderRelation extends PersonQueryPage
{
	 /**
     * 黑名单订购关系查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBaseRecord(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);

        IData queryData = new DataMap();

        String serialNum = condParams.getString("SERIAL_NUMBER", "");

        StringBuilder operateConditions = new StringBuilder();
        operateConditions.append(condParams.getString("SERIAL_NUMBER", ""));//手机号码
		operateConditions.append("|");
		operateConditions.append(condParams.getString("ORDERRELA_TYPE", ""));//业务类型
		operateConditions.append("|");
		operateConditions.append(condParams.getString("START_DATE", "")+SysDateMgr.START_DATE_FOREVER);//开始时间
		operateConditions.append("|");
		operateConditions.append(condParams.getString("END_DATE", "")+SysDateMgr.END_DATE);//结束时间
		operateConditions.append("|");
		operateConditions.append(condParams.getString("ORDERRELA_LINE", ""));//业务线

        queryData.put("KIND_ID", "BIP2C092_T2002092_0_0");
        queryData.put("CALLERNO", serialNum);
        queryData.put("SVCTYPEID", "01010549");// 服务请求分类编码
        queryData.put("CONTACTCHANNEL", "08");// 受理渠道
        queryData.put("SERVICETYPEID", "164");// 业务类别
        queryData.put("OPERATETYPEID", "01002");// 操作类型
        queryData.put("OPERATECONDITIONS", operateConditions.toString());

        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subBussQureySerive", queryData);
//        for(int i = 0;i<infos.size();i++){
//        	IData info = infos.getData(i);
//        	info.put("RSRV_STR0", StaticUtil.getStaticValue("MOBILEMARKET_BLACK_RESULT", info.getString("RSRV_STR0", "")));
//        	info.put("RSRV_STR8", StaticUtil.getStaticValue("MOBILEMARKET_PAYMENT_TYPE", info.getString("RSRV_STR8", "")));
//        	info.put("RSRV_STR9", StaticUtil.getStaticValue("MOBILEMARKET_PURCHASE_TYPE", info.getString("RSRV_STR9", "")));
//        	info.put("RSRV_STR11", StaticUtil.getStaticValue("MOBILEMARKET_OPERATION_TYPE", info.getString("RSRV_STR11", "")));
//        	info.put("RSRV_STR14", StaticUtil.getStaticValue("MOBILEMARKET_ORDER_TYPE", info.getString("RSRV_STR14", "")));
//        	info.put("RSRV_STR15", StaticUtil.getStaticValue("MOBILEMARKET_ORDERSERVICE_TYPE", info.getString("RSRV_STR15", "")));
//        }

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
        IData reportPage = result.getData("REPORTPAGE");

        setResult(result);
        setInfos(results);
        setReportPage(reportPage);
    }
    
    public abstract void setResult(IData data);
    
    public abstract void setInfos(IDataset dataSet);
    
    public abstract void setReportPage(IData data);
    
    public abstract void setCond(IData data);
}
