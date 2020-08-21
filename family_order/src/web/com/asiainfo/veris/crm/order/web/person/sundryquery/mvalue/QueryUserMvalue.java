
package com.asiainfo.veris.crm.order.web.person.sundryquery.mvalue;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryUserMvalue extends PersonQueryPage
{
    /**
     * 页面初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData flag = new DataMap();
        setIsPrint(0);
        IDataset dataset = CSViewCall.call(this, "SS.QueryUserMvalueSVC.queryCycleList", flag);
        setCycles(dataset);
    }

    /**
     * 查询帐期
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCycle(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset dataset = CSViewCall.call(this, "SS.QueryUserMvalueSVC.queryCycleList", data);
        setCycles(dataset);
    }

    /**
     * 查询用户M值
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryUserMvalue(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput infos = CSViewCall.callPage(this, "SS.QueryUserMvalueSVC.queryUserMvalue", data, getPagination("olcomnav"));
        String alertInfo = "";
        int isPrint = 0;
        IData mvalueSumInfo = new DataMap();
        int mvalueSum = 0; // 合计
        IDataset dataset = infos.getData();// .getData(0).getDataset("DATASET");
        String x_recordnum = "";
        if (IDataUtil.isNotEmpty(dataset))
        {
            String XResultCode = dataset.getData(0).getString("X_RESULTCODE", "0");
            if ("0".equals(XResultCode))
            {
                mvalueSum = Integer.parseInt(dataset.getData(0).getString("SUM_SCORE", "0"));
            }
            else
            {
                alertInfo = XResultCode;
            }

            /*
             * x_recordnum = (String) dataset.get(0, "X_RECORDNUM", "0"); if (Integer.parseInt(x_recordnum) > 0) { for
             * (int i = 0; i < dataset.size(); i++) { mvalueSumInfo = (IData) dataset.get(i); String temp_mvalueSum =
             * (String) mvalueSumInfo.get("PARA_CODE4"); if (temp_mvalueSum != null && !"".equals(temp_mvalueSum)) {
             * mvalueSum = mvalueSum + Integer.parseInt(temp_mvalueSum); } } }else{ alertInfo = "获取用户M值无数据!"; }
             */
            setCustInfo(dataset.getData(0));

            // IData printData = CSViewCall.callone(this,
            // "SS.QueryUserMvalueSVC.isPrint", data);
            boolean printFlag = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "QueryUserMvalue");
            if (printFlag == true)
            {
                isPrint = 1;
            }
        }
        else
        {
            alertInfo = "获取用户M值无数据!";
        }
        data.put("X_RECORDNUM", dataset.size());
        data.put("FLAG", "1");
        setCount(infos.getDataCount());
        setMvalueSum(mvalueSum);
        setInfos(dataset);
        queryCycle(cycle);
        setIsPrint(isPrint);
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
    }

    public abstract void setCondition(IData condition);

    public abstract void setCount(long count);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setCycles(IDataset cycles);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setIsPrint(int isPrint);

    public abstract void setMvalueSum(int mvalueSum);
}
