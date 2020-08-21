package com.asiainfo.veris.crm.iorder.web.family.viewquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 家庭积分查询
 * @author zhangxi
 *
 */
public abstract class FamilyScoreTab extends PersonBasePage {

    /**
     * 初始化页面
     * @param cycle
     * @throws Exception
     */
    public void initScoreQuery(IRequestCycle cycle) throws Exception {
        IData data = getData();
        int tabIdx = data.getInt("TABINDEX");
        switch (tabIdx) {
            case SCORE_ISSUE_TAB:
                data.put("START_CYCLE_ID", SysDateMgr.getTodayLastMonth());
                data.put("END_CYCLE_ID", SysDateMgr.getSysDate());
                break;
            case SCORE_YEAR_TAB:
                data.put("START_CYCLE_ID2", SysDateMgr.getTodayLastMonth());
                data.put("END_CYCLE_ID2", SysDateMgr.getSysDate());
                break;
            default:
                data.put("ACCEPT_START", SysDateMgr.getTodayLastMonth());
                data.put("ACCEPT_END", SysDateMgr.getSysDate());
        }
        setInit(data);
    }

    /**
     * 查询积分里程
     * @param cycle
     * @throws Exception
     */
    public void queryScoreIssueDetail(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset scoreIssue = CSViewCall.call(this, "SS.ScoreQueryTradeSVC.queryScoreDetail", data);
        String alertInfo = "";
        if (IDataUtil.isEmpty(scoreIssue) || "0".equals(scoreIssue.first().getString("X_RECORDNUM"))) {
            alertInfo = "获取积分里程明细无数据！";
        } else {
            setInfos(scoreIssue);
        }
        this.setAjax("ALERT_INFO", alertInfo); // 页面提示
    }

    /**
     * 查询年度累计积分
     * @param cycle
     * @throws Exception
     */
    public void queryScoreYearAccumulate(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put("START_CYCLE_ID", data.getString("START_CYCLE_ID2"));
        data.put("END_CYCLE_ID", data.getString("END_CYCLE_ID2"));
        IDataset scoreYear = CSViewCall.call(this, "SS.ScoreQueryTradeSVC.queryYearSumScore", data);
        String alertInfo = "";
        if (IDataUtil.isEmpty(scoreYear) || "0".equals(scoreYear.first().getString("X_RECORDNUM"))) {
            alertInfo = "获取年度累计积分无数据！";
        } else {
            int yearSum = 0; // 年度累计总积分
            for (Object obj : scoreYear) {
                IData yearInfo = (IData) obj;
                String tempScore = yearInfo.getString("SCORE_CHANGED", "");
                if (StringUtils.isNotBlank(tempScore))
                    yearSum += Integer.parseInt(tempScore);
            }
            setYearSum(yearSum);
            setInfos(scoreYear);
        }
        this.setAjax("ALERT_INFO", alertInfo); // 页面提示
    }

    /**
     * 查询年度兑换积分
     * @param cycle
     * @throws Exception
     */
    public void queryScoreYearExchange(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset scoreExchange = CSViewCall.call(this, "SS.ScoreQueryTradeSVC.queryScoreExchangeYear", data);
        String alertInfo = "";
        if (IDataUtil.isEmpty(scoreExchange) || "-1".equals(scoreExchange.first().getString("X_RECORDNUM"))) {
            alertInfo = "获取年度兑换积分无数据！";
        } else {
            setInfos(scoreExchange);
        }
        this.setAjax("ALERT_INFO", alertInfo); // 页面提示
    }

	private static final int SCORE_ISSUE_TAB = 1;
    private static final int SCORE_YEAR_TAB  = 2;

	public abstract void setInit(IData data);
	public abstract void setInfos(IDataset scoreIssue);
    public abstract void setInfoCount(long infoCount);
    public abstract void setYearSum(int yearSum);
}
