
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import java.util.Date;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class LimitbalckQuery extends GroupBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setCond(IData cond);

    public abstract IData getCond();

    public abstract void setHintInfo(String infos);

    public abstract void setInfoCount(long infoCount);

    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件!");

        String start_date = SysDateMgr.getTodayLastMonth();// 获取上月帐期和今天相同的日期
        String end_date = SysDateMgr.date2String(new Date(), "yyyy-mm-dd");

        IData condition = new DataMap();
        condition.put("cond_START_DATE", start_date);
        condition.put("cond_END_DATE", end_date);

        setCondition(condition);

    }

    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String queryway = param.getString("cond_LIMITBLACK_QUERY_WAY");

        IDataOutput dataOutput = null;
        IData data = new DataMap();
        IDataset sxinfos = new DatasetList();

        if ("0".equals(queryway))
        {
            // 根据手机号码查询
            data.put("SERIAL_NUMBER", param.getString("cond_SERIAL_NUMBER"));
        }

        else if ("1".equals(queryway))
        { // 根据挂账集团名称查询
            data.put("CUST_NAME", param.getString("cond_CUST_NAME"));
        }
        else if ("2".equals(queryway))
        { // 根据EC企业接入号
            data.put("BANK_ACCT_NO", param.getString("cond_BANK_ACCT_NO"));
        }

        // ---------------------- end ---------------------------------------
        if (queryway.equals("0") && !"".equals(param.getString("cond_SERIAL_NUMBER")))
        {
            dataOutput = CSViewCall.callPage(this, "SS.BookTradeSVC.getAccountUniteBySN", data, getPagination("PageNav"));

        }
        else if (queryway.equals("1") && !"".equals(param.getString("cond_CUST_NAME")))
        {
            dataOutput = CSViewCall.callPage(this, "SS.BookTradeSVC.getAccountUniteByCustName", data, getPagination("PageNav"));

        }
        else if (queryway.equals("2") && !"".equals(param.getString("cond_BANK_ACCT_NO")))
        {
            dataOutput = CSViewCall.callPage(this, "SS.BookTradeSVC.getAccountUniteByBank", data, getPagination("PageNav"));

        }

        if (null != dataOutput && dataOutput.getData().size() > 0)
        {
            setHintInfo("查询成功~~！");
        }
        else
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }

        setCondition(param);
        setInfos(dataOutput.getData());
        setInfoCount(dataOutput.getDataCount());

    }

    /**
     * 根据手机号码，获取用户受限表信息
     * 
     * @param cycle
     * @return
     * @throws Exception
     */
    public IDataset getAccountUniteBySN(IData inparam) throws Exception
    {
        IDataset AUdataset = new DatasetList();
        IDataset info = CSViewCall.call(this, "SS.BookTradeSVC.getAccountUniteBySN", inparam);

        for (int i = 0; i < info.size(); i++)
        {
            IData AUInfo = new DataMap();
            IData logData = info.getData(i);
            AUInfo.put("SERIAL_NUMBER", logData.getString("SERIAL_NUMBER", ""));
            AUInfo.put("BIZ_IN_CODE", logData.getString("BIZ_IN_CODE", ""));
            AUInfo.put("CUST_NAME", logData.getString("CUST_NAME", ""));
            AUInfo.put("ACCEPT_DATE", logData.getString("ACCEPT_DATE", ""));
            AUdataset.add(AUInfo);
        }

        setInfos(AUdataset);
        return AUdataset;
    }

    /**
     * 根据集团名称，获取受限表信息
     * 
     * @param cycle
     * @throws Exception
     */
    public IDataset getAccountUniteByCustName(IData inparam) throws Exception
    {
        IDataset AUdataset = new DatasetList();
        IDataset custinfos = CSViewCall.call(this, "SS.BookTradeSVC.getAccountUniteByCustName", inparam);

        for (int i = 0; i < custinfos.size(); i++)
        {
            IData custinfo = (IData) custinfos.get(i);
            IData AUInfo = new DataMap();
            AUInfo.put("SERIAL_NUMBER", custinfo.getString("SERIAL_NUMBER", ""));
            AUInfo.put("BIZ_IN_CODE", custinfo.getString("BIZ_IN_CODE", ""));
            AUInfo.put("CUST_NAME", custinfo.getString("CUST_NAME", ""));
            AUInfo.put("ACCEPT_DATE", custinfo.getString("ACCEPT_DATE", ""));
            AUdataset.add(AUInfo);
        }

        setInfos(AUdataset);
        return AUdataset;
    }

    /**
     * 根据ec接入号，获取用户受限表信息
     * 
     * @param cycle
     * @throws Exception
     */
    public IDataset getAccountUniteByBank(IData inparam) throws Exception
    {

        IDataset AUdataset = new DatasetList();
        IDataset acctinfos = CSViewCall.call(this, "SS.BookTradeSVC.getAccountUniteByBank", inparam);

        for (int i = 0; i < acctinfos.size(); i++)
        {
            IData custinfo = (IData) acctinfos.get(i);
            IData AUInfo = new DataMap();
            AUInfo.put("SERIAL_NUMBER", custinfo.getString("SERIAL_NUMBER", ""));
            AUInfo.put("BIZ_IN_CODE", custinfo.getString("BIZ_IN_CODE", ""));
            AUInfo.put("CUST_NAME", custinfo.getString("CUST_NAME", ""));
            AUInfo.put("ACCEPT_DATE", custinfo.getString("ACCEPT_DATE", ""));
            AUdataset.add(AUInfo);
        }

        this.setInfos(AUdataset);
        return AUdataset;
    }

    public void deleteRecord(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String tmp_records = param.getString("RECORDS", "");
        String[] records = tmp_records.split(",");
        for (int i = 0; i < records.length; i++)
        {
            String[] tmp = records[i].replace("|", ",").split(",");
            IData inparam = new DataMap();
            inparam.put("SERIAL_NUMBER", tmp[0]);
            inparam.put("BIZ_IN_CODE", tmp[1]);
            CSViewCall.call(this, "SS.LimitBlackwhiteQrySVC.deleteLimitBlackWhite2", inparam);
        }
        // 删除成功后，返回查询之前查询结果页
        queryInfos(cycle);

        setHintInfo("已成功删除记录！");
    }
}
