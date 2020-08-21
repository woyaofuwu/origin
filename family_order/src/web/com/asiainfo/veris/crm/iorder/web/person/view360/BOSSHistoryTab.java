
package com.asiainfo.veris.crm.iorder.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BOSSHistoryTab extends PersonBasePage {

    public abstract IDataset getInfos();
    
    /**
     * 初始化方法
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        // 设置起止时间
        String endDate = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
        String firstData = SysDateMgr.getFirstDayOfThisMonth4WEB();//endDate;
        IData data = getData("cond", true);
        data.put("cond_START_DATE", firstData);
        data.put("cond_END_DATE", endDate);        
        this.setCondition(data);
    }

    /**
     * 一级BOSS业务办理记录查询
     */
    public void queryBOSSInfo(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        
        IDataOutput dataCount=null;
    	dataCount= CSViewCall.callPage(this, "SS.QryBOSSInfoSVC.qryBOSSTradeHistoryInfo", inparam, getPagination("navt"));
         
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "查询用户业务信息无记录~~";
        }
        
        if (IDataUtil.isNotEmpty(results)) {
            for (Object obj : results) {
                IData tradeInfo = (DataMap) obj;
                if ("1".equals(tradeInfo.getString("TAG_110"))) {
                    tradeInfo.put("TRADE_TYPE", tradeInfo.getString("PROCESS_TAG_SET"));
                } else {
                    String[] keys = new String[]{"EPARCHY_CODE", "TRADE_TYPE_CODE"};
                    String[] values = new String[]{tradeInfo.getString("EPARCHY_CODE"), tradeInfo.getString("TRADE_TYPE_CODE")};
                    String tradeType = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", keys, "TRADE_TYPE", values);
                    tradeInfo.put("TRADE_TYPE", tradeType);
                }
            }
        }
        
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setInfos(results);
        setCount(dataCount.getDataCount());
        setCondition(getData("cond", true));

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}
