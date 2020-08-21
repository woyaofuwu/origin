
package com.asiainfo.veris.crm.order.web.person.canceltdtrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.ISubmitData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.util.SubmitDataParseUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: CancelTrade.java
 * @Description: 业务返销view
 * @version: v1.0.0
 * @author: liuke
 * @date: 下午05:02:51 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-7-29 liuke v1.0.0 修改原因
 */
public abstract class CancelTDTrade extends PersonBasePage
{

    /**
     * 取消前校验
     * 
     * @param cycle
     * @throws Exception
     */
    public void cancelBeforeCheck(IRequestCycle cycle) throws Exception
    {
        IData pdData = getData();
        pdData.put(Route.ROUTE_EPARCHY_CODE, pdData.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.CancelTradeSVC.commitBeforeCheck", pdData);
        setAjax(dataset);
    }

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData pageParam = new DataMap();
        String nowDate = SysDateMgr.getSysDate();
        pageParam.put("START_DATE", nowDate);
        pageParam.put("END_DATE", nowDate);
        setInfo(pageParam);

        // 查询可返销的业务类型
        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        input.put("NET_TYPE_CODE", PersonConst.TD_NET_TYPE_CODE);
        IDataset undoTradeTypes = CSViewCall.call(this, "CS.TradeTypeInfoQrySVC.queryTDCancelTradeType", input);
        if (IDataUtil.isNotEmpty(undoTradeTypes))
        {
            for (int i = 0; i < undoTradeTypes.size(); i++)
            {
                String tradeType = undoTradeTypes.getData(i).getString("TRADE_TYPE");
                String tradeTypeCode = undoTradeTypes.getData(i).getString("TRADE_TYPE_CODE");
                undoTradeTypes.getData(i).put("TRADE_TYPE", "[" + tradeTypeCode + "]" + tradeType);
            }
            setTradeTypeCodeList(undoTradeTypes);
        }
    }

    /**
     * 执行返销动作
     * 
     * @param cycle
     * @throws Exception
     */
    public void saveContent(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        ISubmitData submitData = SubmitDataParseUtil.parseSubmitData(input);
        IData request = submitData.getAttrData();
        request.put(Route.ROUTE_EPARCHY_CODE, request.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.CancelTradeSVC.cancelTradeReg", request);
        setAjax(dataset);
    }

    public abstract void setInfo(IData info);

    public abstract void setTradeTypeCodeList(IDataset tradeTypeCodeList);

    public abstract void setValidCancelTradeList(IDataset validCancelTradeList);
}
