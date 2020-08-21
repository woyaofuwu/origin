
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CustomerHistoryInfo extends PersonBasePage
{
    /**
     * 客户历史日志页面初始化
     * 
     * @param cycle
     * @throws Exception
     * @author huanghui@asiainfo.com
     * @date 2014-08-08
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData initParam = new DataMap();
        initParam.put("SERIAL_NUMBER", param.get("SERIAL_NUMBER"));
        initParam.put("SIM_CHECK", param.getString("SIM_CHECK", ""));
        initParam.put("SIM_NUMBER", param.getString("SIM_NUMBER", ""));
        initParam.put("NORMAL_USER_CHECK", param.getString("NORMAL_USER_CHECK", ""));
        initParam.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        initParam.put("USER360VIEW_VALIDTYPE", param.getString("USER360VIEW_VALIDTYPE", ""));
        initParam.put("SERVICE_NUMBER", param.getString("SERVICE_NUMBER", ""));
        initParam.put("PSPT_NUMBER", param.getString("PSPT_NUMBER", ""));

        setCondition(initParam);
    }

    // 客户接触信息
    public void qryTHCustomerContactInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String msisdn = param.getString("SERIAL_NUMBER", "");
        param.put("SERIAL_NUMBER", msisdn);
        IDataOutput results = new DataOutput();
        if (StringUtils.isNotBlank(msisdn))
        {
            results = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryTHCustomerContactInfo", param, getPagination("pagin2"));
        }
        else
        {
            new DataOutput();
        }
        if (IDataUtil.isNotEmpty(results.getData()))
        {
            setThCustomerContactInfos(results.getData());
            setPaginCount2(results.getDataCount());
        }
        // setCondition(initParam);
    }

    public void qryTHRelaTradeInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String msisdn = param.getString("SERIAL_NUMBER", "");
        param.put("SERIAL_NUMBER", msisdn);
        IDataOutput results = new DataOutput();
        if (StringUtils.isNotBlank(msisdn))
        {
           results = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryTHRelaTradeInfo", param, getPagination("pagin3"));
        }
        else
        {
            new DataOutput();
        }
        if (IDataUtil.isNotEmpty(results.getData()))
        {
            setThRelaTradeInfos(results.getData());
            setPaginCount3(results.getDataCount());
        }
        // setCondition(initParam);
    }

    public void queryThServerInfos(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        // IData initParam = new DataMap();
        String msisdn = param.getString("SERIAL_NUMBER", "");
        param.put("SERIAL_NUMBER", msisdn);
        IDataOutput results = new DataOutput();
        if (StringUtils.isNotBlank(msisdn))
        {
            results = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.queryThServerInfos", param, getPagination("pagin1"));
        }
        else
        {
            new DataOutput();
        }
        if (IDataUtil.isNotEmpty(results.getData()))
        {
            setThServerInfos(results.getData());
            setPaginCount(results.getDataCount());
        }
        // setCondition(initParam);
    }

    public void setCommInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData initParam = new DataMap();
        initParam.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        initParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
        initParam.put("USER_ID", param.getString("USER_ID", ""));
        initParam.put("CUST_ID", param.getString("CUST_ID", ""));
        setCondition(initParam);
    }

    public abstract void setCondition(IData condition);

    public abstract void setPaginCount(long paginCount);

    public abstract void setPaginCount2(long paginCount2);

    public abstract void setPaginCount3(long paginCount3);

    public abstract void setThCustomerContactInfos(IDataset thCustomerContactInfos);

    public abstract void setThRelaTradeInfos(IDataset thRelaTradeInfos);

    public abstract void setThServerInfos(IDataset thServersInfos);

}
