
package com.asiainfo.veris.crm.order.web.person.interboss.mobileoperation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class IAirportVIPService extends PersonBasePage
{
    public void addRow(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = new DatasetList(data.getString("RECORD_INFO"));
        this.setSubInfos(dataset);
    }

    public void airportSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IData resultData = CSViewCall.call(this, "SS.IVipServiceDealSVC.airportServiceCharge", data).getData(0);

        this.setAjax(resultData);

    }

    public void checkAirportRight(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData pageData = getData("cond");
        data.putAll(pageData);

        IData resultData = CSViewCall.call(this, "SS.IVipServiceDealSVC.checkAirPortRight", data).getData(0);

        if (!"0000".equals(resultData.getString("X_RSPCODE")))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, resultData.getString("X_RSPDESC"));
        }
        // 鉴权成功
        if ("0".equals(resultData.getString("X_RSPTYPE")) && "0000".equals(resultData.getString("X_RSPCODE")))
        {
            resultData.put("ACCOUNT_FLAG", "1");
            resultData.put("DISABLED_FLAG", "1");
            setSerInfos(getIVipService(cycle));
        }
        else
        {
            resultData.put("ACCOUNT_FLAG", "0");
            resultData.put("DISABLED_FLAG", "1");
            CSViewException.apperr(CrmCommException.CRM_COMM_103, resultData.getString("X_RSPDESC"));
        }
        
        String cust_class_type = "";

        if (resultData.getString("CLASS_ID") != null)
        {
            if ("100".equals(resultData.getString("CLASS_ID")))
                cust_class_type = "普通客户";
            if ("200".equals(resultData.getString("CLASS_ID")))
                cust_class_type = "重要客户";
            if ("201".equals(resultData.getString("CLASS_ID")))
                cust_class_type = "党政机关客户";
            if ("202".equals(resultData.getString("CLASS_ID")))
                cust_class_type = "军、警、安全机关客户";
            if ("203".equals(resultData.getString("CLASS_ID")))
                cust_class_type = "联通合作伙伴客户";
            if ("204".equals(resultData.getString("CLASS_ID")))
                cust_class_type = "英雄、模范、名星类客户";
            if ("300".equals(resultData.getString("CLASS_ID")))
                cust_class_type = "普通大客户";
            if ("301".equals(resultData.getString("CLASS_ID")))
                cust_class_type = "钻石卡大客户";
            if ("302".equals(resultData.getString("CLASS_ID")))
                cust_class_type = "金卡大客户";
            if ("303".equals(resultData.getString("CLASS_ID")))
                cust_class_type = "银卡大客户";
            if ("304".equals(resultData.getString("CLASS_ID")))
                cust_class_type = "贵宾卡大客户";
        }

        resultData.put("USERRANK", cust_class_type);
        this.setInfo(resultData);
        this.setAjax(resultData);

    }

    public void deleteId(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = new DatasetList(data.getString("RECORD_INFO"));
        this.setSubInfos(dataset);
    }

    public IDataset getIVipService(IRequestCycle cycle) throws Exception
    {
        IData param = new DataMap();

        param.put("MAINSERVICE", true);
        param.put("MAINCODE", "");
        param.put("PARAM_ATTR", "5213");

        return CSViewCall.call(this, "SS.IVipServiceDealSVC.getIVipService", param);

    }

    /**
     * 初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("ROUTETYPE", "01");
        data.put("IDTYPE", "01");
        data.put("VERIFY_TYPE", "0");
        data.put("PROVINCE_CODE", getVisit().getProvinceCode());
        setCond(data);
    }

    /**
     * 异地大客户信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryVIPCustInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond");
        data.put("TYPEIDSET", "5");
        IDataset dataset = CSViewCall.call(this, "SS.QueryVIPCustInfoSVC.queryVIPCustInfo", data);

        IData vipInfo = dataset.getData(0);

        if (!"0000".equals(vipInfo.getString("X_RSPCODE")))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, vipInfo.getString("X_RSPDESC"));
        }

        if ("0".equals(vipInfo.getString("X_RSPTYPE")) && "0000".equals(vipInfo.getString("X_RSPCODE")))
        {
            vipInfo.put("DISABLED_FLAG", "1");
        }
        else
        {
            vipInfo.put("DISABLED_FLAG", "0");
        }

        String cust_class_type = "";

        if (vipInfo.getString("CLASS_ID") != null)
        {
            if ("100".equals(vipInfo.getString("CLASS_ID")))
                cust_class_type = "普通客户";
            if ("200".equals(vipInfo.getString("CLASS_ID")))
                cust_class_type = "重要客户";
            if ("201".equals(vipInfo.getString("CLASS_ID")))
                cust_class_type = "党政机关客户";
            if ("202".equals(vipInfo.getString("CLASS_ID")))
                cust_class_type = "军、警、安全机关客户";
            if ("203".equals(vipInfo.getString("CLASS_ID")))
                cust_class_type = "联通合作伙伴客户";
            if ("204".equals(vipInfo.getString("CLASS_ID")))
                cust_class_type = "英雄、模范、名星类客户";
            if ("300".equals(vipInfo.getString("CLASS_ID")))
                cust_class_type = "普通大客户";
            if ("301".equals(vipInfo.getString("CLASS_ID")))
                cust_class_type = "钻石卡大客户";
            if ("302".equals(vipInfo.getString("CLASS_ID")))
                cust_class_type = "金卡大客户";
            if ("303".equals(vipInfo.getString("CLASS_ID")))
                cust_class_type = "银卡大客户";
            if ("304".equals(vipInfo.getString("CLASS_ID")))
                cust_class_type = "贵宾卡大客户";
        }

        vipInfo.put("USERRANK", cust_class_type);
        vipInfo.put("FREE_TIMES", vipInfo.getString("FREE_TIMES", "0"));// 客户可用免费次数
 
        setInfo(vipInfo);
    }

    public void retrieveServiceDetail(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IData param = new DataMap();
        param.put("MAINSERVICE", false);
        param.put("MAINCODE", data.getString("MAIN_SERVICE_CODE"));
        param.put("PARAM_ATTR", "5213");

        setDetailInfos(CSViewCall.call(this, "SS.IVipServiceDealSVC.getIVipService", param));
    }

    public abstract void setCond(IData cond);

    public abstract void setDetailInfos(IDataset dataset);

    public abstract void setInfo(IData info);

    public abstract void setSerInfos(IDataset dataset);

    public abstract void setSubInfos(IDataset dataset);

}
