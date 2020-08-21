
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

public abstract class IRailwayVIPService extends PersonBasePage
{

    public void addRow(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = new DatasetList(data.getString("RECORD_INFO"));
        this.setSubInfos(dataset);
    }

    public void checkRailwayRight(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData pageData = getData("cond");
        data.putAll(pageData);

        IData resultData = CSViewCall.call(this, "SS.IVipServiceDealSVC.checkRailwayRight", data).getData(0);

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
            CSViewException.apperr(CrmCommException.CRM_COMM_103, resultData.getString("X_RSPDESC"));
        }

        setInfo(resultData);
        setInfom(resultData);

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
        param.put("PARAM_ATTR", "5214");

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
        IData vipInfo = CSViewCall.call(this, "SS.QueryVIPCustInfoSVC.queryVIPCustInfo", data).getData(0);

        vipInfo.put("ACCOUNT_FLAG", "0");

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

        setInfo(vipInfo);
        setInfom(vipInfo);

    }

    public void railwaySubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData resultData = CSViewCall.call(this, "SS.IVipServiceDealSVC.railwayServiceCharge", data).getData(0);
        this.setAjax(resultData);

    }

    public void retrieveServiceDetail(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IData param = new DataMap();
        param.put("MAINSERVICE", false);
        param.put("MAINCODE", data.getString("MAIN_SERVICE_CODE"));
        param.put("PARAM_ATTR", "5214");

        setDetailInfos(CSViewCall.call(this, "SS.IVipServiceDealSVC.getIVipService", param));
    }

    public abstract void setCond(IData cond);

    public abstract void setDetailInfos(IDataset dataset);

    public abstract void setInfo(IData info);

    public abstract void setInfom(IData info);

    public abstract void setSerInfos(IDataset dataset);

    public abstract void setSubInfos(IDataset dataset);

}
