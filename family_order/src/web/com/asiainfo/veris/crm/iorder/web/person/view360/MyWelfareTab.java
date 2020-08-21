
package com.asiainfo.veris.crm.iorder.web.person.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IRequestCycle;

public abstract class MyWelfareTab extends PersonBasePage {


    /**
     * 客户资料综合查询 - 我的权益历史数据查询
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception {
        IData inParam = getData();
        setInit(inParam);
        IData inputData = new DataMap();
        String startDate = inParam.getString("START_DATE");
        String endDate = inParam.getString("END_DATE");
        //这里判断配合权益接口 时间格式和空问题
        if(StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)){
        	startDate = startDate+SysDateMgr.START_DATE_FOREVER;
        	endDate = endDate+SysDateMgr.END_DATE;
        	inputData.put("startTime", startDate);
        	inputData.put("endTime", endDate);
        }
        inputData.put("telnum", inParam.getString("SERIAL_NUMBER"));
        IDataset rtnData = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryWelfareHistoryInfo", inputData);
        String alertInfo = "";
        if(IDataUtil.isNotEmpty(rtnData)){
        	setInfos(rtnData);
        }else{
        	alertInfo = "获取订单列表无数据！";
        }
        this.setAjax("ALERT_INFO", alertInfo); // 页面提示
    }
    //queryDetailInfo
    public void queryDetailInfo(IRequestCycle cycle) throws Exception {
        IData inParam = getData();
        IData inputData = new DataMap();
        inputData.put("orderId", inParam.getString("ORDER_ID"));
        IDataset rtnData = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryWelfareHistoryDetailInfo", inputData);
        if(IDataUtil.isNotEmpty(rtnData)){
        	IData tradeData = rtnData.getData(0);
        	IData mainTrade = tradeData.getData("MAIN_TRADE");
        	IDataset subTrade = tradeData.getDataset("SUB_TRADE");
        	setMainTrade(mainTrade);
        	setSubTrades(subTrade);
        }
    }

    public abstract void setInfos(IDataset infos);
    public abstract void setSubTrades(IDataset subTrades);
    public abstract void setInit(IData init);
    public abstract void setMainTrade(IData mainTrade);
  
}
