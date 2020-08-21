package com.asiainfo.veris.crm.order.web.person.sundryquery.checknpoutresultquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：携出预审核结果查询 作者：mengqx
 */
public abstract class CheckNpOutResultQuery extends PersonBasePage
{

	
    public abstract IDataset getInfos();

    /**
     * 初始化
     */
    public void init(IRequestCycle cycle) throws Exception
    {
    	
    	IData data = getData();
    	
        // 设置起止时间
        String endDate = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
        String firstData = endDate;
        data.put("START_DATE", firstData);
        data.put("FINISH_DATE", endDate);
        this.setCondition(data);
    }

    /**
     * 功能：携出预审核结果查询
     */
    public void queryCheckNpOut(IRequestCycle cycle) throws Exception
    {
    	IData inparam = getData();
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        inparam.put("START_DATE", inparam.getString("START_DATE") + SysDateMgr.START_DATE_FOREVER);
        inparam.put("FINISH_DATE", inparam.getString("FINISH_DATE") + SysDateMgr.END_DATE);

        IDataOutput dataCount = CSViewCall.callPage(this, "SS.CheckNpOutResultQuerySVC.queryCheckNpOut", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【携出预审核结果】数据~";
        }else {
        	String resultInfo = results.getData(0).getString("RESULT_INFO","");
        	if(resultInfo != null && !resultInfo.isEmpty()){
        		alertInfo = resultInfo;
        	}
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setInfos(results);
        setCount(dataCount.getDataCount());

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}

