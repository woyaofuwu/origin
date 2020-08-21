
package com.asiainfo.veris.crm.order.web.person.bat.batcancelpage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
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
public abstract class BatCancelPage extends PersonBasePage
{

    /** 根据批次号返销 */
    public void cancelByBatchid(IRequestCycle cycle) throws Exception
    {
        IData paramData = getData();
        String paramStr = paramData.get("PARAM").toString();
        IDataset params = new DatasetList(paramStr);
        String batchId = "";
        for (int i = 0; i < params.size(); i++)
        {
            IData data = params.getData(i);
            batchId = data.getString("BATCH_ID");
            data.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
            data.put("TRADE_CITY_CODE", getVisit().getCityCode());
            data.put("TRADE_DEPART_ID", getVisit().getDepartId());
            data.put("TRADE_STAFF_ID", getVisit().getStaffId());
//            CSViewCall.call(this, "CS.BatDealSVC.cancelByBatchid", data);
            //增加对实名制激活的校验
            CSViewCall.call(this, "CS.BatDealSVC.cancelByBatid", data);
        }
        IData result = new DataMap();
        result.put("BATCH_ID", batchId);
        setAjax(result);
    }

    /** 根据服务号码返销 */
    public void cancelBySerialNum(IRequestCycle cycle) throws Exception
    {
        IData paramData = getData();
        String paramStr = paramData.getString("PARAM");
        IDataset params = new DatasetList(paramStr);
        String serialNum = "";
        for (int i = 0; i < params.size(); i++)
        {
            IData data = params.getData(i);
            data.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
            data.put("TRADE_CITY_CODE", getVisit().getCityCode());
            data.put("TRADE_DEPART_ID", getVisit().getDepartId());
            data.put("TRADE_STAFF_ID", getVisit().getStaffId());
            serialNum = data.getString("SERIAL_NUMBER");
            CSViewCall.call(this, "CS.BatDealSVC.cancelBySerialNum", data);
        }

        IData result = new DataMap();
        result.put("SERIAL_NUMBER", serialNum);
        setAjax(result);
    }

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    // 根据批次号查询待返销订单
    public void queryBatchInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond");
        data.put("BATCH_ID", data.getString("NUMBER"));

        IDataset batchtype = CSViewCall.call(this, "CS.BatDealSVC.queryBatchType", data);
        
        if (batchtype == null || batchtype.size() == 0)
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "数据错误，该批次没有配置业务类型信息！");
            return;
        }

        IData params = batchtype.getData(0);
        IDataset canceltag = CSViewCall.call(this, "CS.BatDealSVC.queryCancelTag", params);

        if (IDataUtil.isEmpty(canceltag))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "数据错误，该批次没有配置返销标志信息！");
            return;
        }

        IData cancel = canceltag.getData(0);
        String cancelabletag = cancel.getString("CANCELABLE_FLAG");

        if ("0".equals(cancelabletag))
        {
            setCondition(getData("cond"));
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该批次下没有可返销业务！");
        }

//        IDataOutput result = CSViewCall.callPage(this, "CS.BatDealSVC.queryCancelBatByBatchInfo", data, getPagination("taskNav"));
        //添加对实名制激活的校验判断
        IDataOutput result = CSViewCall.callPage(this, "CS.BatDealSVC.queryCancelBatByBatchInfos", data, getPagination("taskNav"));
        if (IDataUtil.isEmpty(result.getData()))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该批次下没有可返销服务！请确认批次号后重新输入！");
        }
        IData cond = getData("cond");
        setBatchStatInfos(result.getData());
        setBatchStatInfoCount(result.getDataCount());
        setCondition(cond);

    }

    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond");

        if (data.getString("TYPEID").equals("01"))
        {
            querySerialInfo(cycle);
        }
        else
        {
            queryBatchInfo(cycle);
        }
    }

    // 根据服务号码查询待返销订单
    public void querySerialInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond");
        data.put("SERIAL_NUMBER", data.getString("NUMBER").length() == 8 ? "898" + data.getString("NUMBER") : data.getString("NUMBER"));
        
//        IDataOutput result = CSViewCall.callPage(this, "CS.BatDealSVC.queryCancelBatBySerialInfo", data, getPagination("taskNav"));
        //添加对实名制激活的校验判断
        IDataOutput result = CSViewCall.callPage(this, "CS.BatDealSVC.queryCancelBatBySerialInfos", data, getPagination("taskNav"));

        if (IDataUtil.isEmpty(result.getData()))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该号码下没有可返销服务！请确认服务号码后重新输入！");
        }

        setSerialNumInfos(result.getData());
        setSerialNumInfoCount(result.getDataCount());

        setCondition(data);
    }

    public abstract void setAlertInfo(String alertInfo);

    public abstract void setBatchStatInfo(IData batchStatInfo);

    public abstract void setBatchStatInfoCount(long count);

    public abstract void setBatchStatInfos(IDataset batchStatInfos);

    public abstract void setCancelResult(IDataset result);

    public abstract void setCondition(IData condition);

    public abstract void setDealInfos(IDataset dealInfos);

    public abstract void setFeeInfo(IData feeInfo);

    public abstract void setSerialNumInfo(IData batchStatInfo);

    public abstract void setSerialNumInfoCount(long count);

    public abstract void setSerialNumInfos(IDataset result);

}
