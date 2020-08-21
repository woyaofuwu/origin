
package com.asiainfo.veris.crm.order.web.person.sundryquery.score;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryScoreInfo extends PersonBasePage
{

    /**
     * 子业务信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void getCommInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IPage page = cycle.getPage();
        param.put("OBJECT_ID", page.getPageName());
        param.remove("IN_MODE_CODE");
        IData data = CSViewCall.callone(this, "SS.QueryScoreInfoSVC.getCommInfo", param);
        setCustInfo(data.getData("CUST_INFO"));
        setUserInfo(data.getData("USER_INFO"));
        setUserScoreInfo(data.getData("SCORE_INFO"));
    }

    /**
     * 初始化页面
     * 
     * @param cycle
     * @throws Exception
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put("ACCEPT_START", SysDateMgr.getTodayLastMonth());
        data.put("ACCEPT_END", SysDateMgr.getSysDate());
        data.put("YEAR_ID", SysDateMgr.getSysDate().substring(0, 4));
        setCondition(data);
        queryScoreType(cycle);
    }

    /**
     * 初始化页面(供客服调用)
     * 
     * @param cycle
     * @throws Exception
     */
    public void initServicePage(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData data = new DataMap();
        data.put("ACCEPT_START", SysDateMgr.getTodayLastMonth());
        data.put("ACCEPT_END", SysDateMgr.getSysDate());
        data.put("YEAR_ID", SysDateMgr.getSysDate().substring(0, 4));
        // 接口接入数据处理
        data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
        data.put("IN_MODE_CODE", param.getString("IN_MODE_CODE", ""));

        setCondition(data);
        queryScoreType(cycle);
        getCommInfo(cycle);
    }

    /**
     * 查询积分业务情况
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryScoreBizInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput outPut = CSViewCall.callPage(this, "SS.QueryScoreInfoSVC.queryScoreBizInfos", data, getPagination("pagin"));
        String alertInfo = "";
        if (IDataUtil.isEmpty(outPut.getData()))
        {
            alertInfo = "获取积分业务情况无数据!";

        }
        setScoreBizInfos(outPut.getData());
        setPaginCount(outPut.getDataCount());
        this.setAjax("ALERT_INFO", alertInfo);// 页面提示
    }

    /**
     * 查询积分里程明细
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryScoreDetail(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataOutput outPut = CSViewCall.callPage(this, "SS.QueryScoreInfoSVC.queryScoreDetail", data, getPagination("pagin2"));
        String alertInfo = "";
        int scoreSumDetail = 0; // 合计
        IDataset dataset = outPut.getData();

        if (IDataUtil.isEmpty(dataset) || "0".equals(dataset.getData(0).getString("X_RECORDNUM")))
        {
            alertInfo = "获取积分里程明细无数据!";

        }

        IData sumscoreDetailInfo = new DataMap();
        for (int i = 0; i < dataset.size(); i++)
        {
            sumscoreDetailInfo = dataset.getData(i);
            String temp_integral_fee = (String) sumscoreDetailInfo.get("INTEGRAL_FEE");
            if (temp_integral_fee != null && !"".equals(temp_integral_fee))
            {
                scoreSumDetail = scoreSumDetail + Integer.parseInt(temp_integral_fee);
            }

        }

        setScoreDetail(scoreSumDetail);
        setPaginCount2(outPut.getDataCount());
        setScoreDetails(outPut.getData());

        this.setAjax("ALERT_INFO", alertInfo);// 页面提示

    }

    /**
     * 查询年度兑换积分
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryScoreExchangeYear(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        int scoreSum = 0; // 合计
        String alertInfo = "";
        IData sumscore = new DataMap();
        IData param = new DataMap();
        param.put("YEAR_ID", data.getString("YEAR_ID"));
        param.put("USER_ID", data.getString("USER_ID"));
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.QueryScoreInfoSVC.queryScoreExchangeYear", param);

        if (IDataUtil.isEmpty(dataset) || "0".equals(dataset.getData(0).getString("X_RECORDNUM")))
        {
            alertInfo = "获取年度兑换积分无数据!";

        }

        for (int i = 0; i < dataset.size(); i++)
        {
            sumscore = (IData) dataset.get(i);
            String temp_score = (String) sumscore.get("SCORE");
            if (temp_score != null && !"".equals(temp_score))
            {
                scoreSum = scoreSum + Integer.parseInt(temp_score);

            }
        }

        setScoreSum(scoreSum);
        setScoreExchangeYears(dataset);

        this.setAjax("ALERT_INFO", alertInfo);// 页面提示

    }

    /**
     * 查询积分类型
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryScoreType(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("X_IN_MODE_CODE", data.getString("IN_MODE_CODE"));
        data.remove("IN_MODE_CODE");
        IDataset dataset = CSViewCall.call(this, "SS.QueryScoreInfoSVC.queryScoreType", data);
        IData allData = new DataMap();
        allData.put("SCORE_TYPE_NAME", "所有积分");
        allData.put("SCORE_TYPE_CODE", "-1");
        dataset.add(allData);
        setScoreTypes(dataset);
    }

    /**
     * 查询年度累计积分
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryScoreYear(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        param.put("START_CYCLE_ID", data.getString("START_CYCLE_ID2"));
        param.put("END_CYCLE_ID", data.getString("END_CYCLE_ID2"));
        param.put("USER_ID", data.getString("USER_ID"));
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));

        int integralFeeSum = 0; // 合计
        String alertInfo = "";
        IDataset sumscores = new DatasetList();
        IData sumscore = new DataMap();

        IDataset dataset = CSViewCall.call(this, "SS.QueryScoreInfoSVC.queryYearSumScore", param);
        if (IDataUtil.isEmpty(dataset) || "0".equals(dataset.getData(0).getString("X_RECORDNUM")))
        {
            alertInfo = "获取年度累计积分无数据!";

        }

        for (int i = 0; i < dataset.size(); i++)
        {
            sumscore = (IData) dataset.get(i);
            String temp_integral_fee = (String) sumscore.get("INTEGRAL_FEE");
            if (StringUtils.isNotBlank(temp_integral_fee))
            {
                integralFeeSum = integralFeeSum + Integer.parseInt(temp_integral_fee);
                sumscores.add(sumscore);
            }
        }

        this.setAjax("ALERT_INFO", alertInfo);// 页面提示
        setIntegralFeeSum(integralFeeSum);
        setScoreYears(sumscores);
    }

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setCustomerEstimate(IDataset customerEstimate);

    public abstract void setInfos(IDataset infos);

    public abstract void setIntegralFeeSum(int integralFeeSum);

    public abstract void setPaginCount(long paginCount);

    public abstract void setPaginCount2(long paginCount2);

    public abstract void setScoreBizInfos(IDataset scoreBizInfos);

    public abstract void setScoreCustomer(int scoreCustomer);

    public abstract void setScoreDetail(int scoreDetail);

    public abstract void setScoreDetails(IDataset scoreDetails);

    public abstract void setScoreExchangeYears(IDataset scoreExchangeYears);

    public abstract void setScoreSum(int scoreSum);

    public abstract void setScoreTypes(IDataset ScoreTypes);

    public abstract void setScoreYears(IDataset scoreYears);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setUserScoreInfo(IData userScoreInfo);
}
