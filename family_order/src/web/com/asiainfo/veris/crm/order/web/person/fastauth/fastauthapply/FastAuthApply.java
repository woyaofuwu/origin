
package com.asiainfo.veris.crm.order.web.person.fastauth.fastauthapply;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FastAuthApply extends PersonBasePage
{
    protected static Logger log = Logger.getLogger(FastAuthApply.class);

    /** 提交授权申请 */
    public void applyAuthTrade(IRequestCycle cycle) throws Exception
    {
        IData para = getData("ADD", true);
        IDataset resultsAplyAply = CSViewCall.call(this, "SS.FastAuthApplySVC.applyAuthTrade", para);
        String applySuccessFlag = resultsAplyAply.getData(0).getString("APPLY_SUCCESS_FLAG");
        queryApplyTradeList(cycle);
        String alertInfo = "";
        if ("999".equals(applySuccessFlag))
        {
            alertInfo = applySuccessFlag;
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

    }

    /**
     * 验证随机密码 中测新增
     */
    public void checkPwd(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        IDataset pwd = CSViewCall.call(this, "SS.FastAuthApplySVC.queryPwd", param);
        String alertInfo = "错误";
        if (IDataUtil.isNotEmpty(pwd))
        {
            IData data = (IData) pwd.get(0);
            String pwdCount = data.getString("COUNT");
            if ("1".equals(pwdCount))
            {
                alertInfo = "正确";
            }
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

    }

    public void delAppAuthInfo(IRequestCycle cycle) throws Exception
    {
        IData para = getData("DEL", true);
        IDataset resultsDel = CSViewCall.call(this, "SS.FastAuthApplySVC.delAppAuthInfo", para);

        // IData param = getData("cond", true);
        // param.put("ASK_STAFF_ID", this.getVisit().getStaffId());
        // param.put("ASK_DEPART_ID", this.getVisit().getDepartId());

        // IDataOutput applyTradeList = CSViewCall.callPage(this, "SS.FastAuthApplySVC.queryApplyTrade", param,
        // getPagination("navt"));
        // setApplyTradeList(applyTradeList.getData());
        // queryApplyTradeList(cycle);
    }

    public void delAuthTime(IRequestCycle cycle) throws Exception
    {
        IData para = getData();

        IData param = new DataMap();
        param.put("ASK_ID", para.getString("ASK_ID", "-1"));
        IDataset resultsAplyAply = CSViewCall.call(this, "SS.FastAuthApplySVC.delAuthTimes", param);
        queryApplyTradeList(cycle);
    }

    public void init(IRequestCycle cycle) throws Exception
    {
        IData tradeInfo = new DataMap();
        tradeInfo.putAll(getData("ADD", true));
        if (!tradeInfo.containsKey("ASK_START_DATE") || "".equals(tradeInfo.getString("ASK_START_DATE")))
        {
            tradeInfo.put("ASK_START_DATE", SysDateMgr.getSysDate());
        }
        if (!tradeInfo.containsKey("ASK_END_DATE") || "".equals(tradeInfo.getString("ASK_END_DATE")))
        {
            tradeInfo.put("ASK_END_DATE", SysDateMgr.getSysDate());
        }
        if (!tradeInfo.containsKey("ASK_NUM") || "".equals(tradeInfo.getString("ASK_NUM")))
        {
            tradeInfo.put("ASK_NUM", "3");
        }
        if (!tradeInfo.containsKey("SERIAL_NUMBER") || "".equals(tradeInfo.getString("SERIAL_NUMBER")))
        {
            tradeInfo.put("SERIAL_NUMBER", this.getVisit().getSerialNumber());
        }
        setTradeInfo(tradeInfo);
        setAcceptTradeList(queryAcceptTradeList(cycle));

    }

    public void onInit(IRequestCycle cycle) throws Exception
    {
        queryApplyTradeList(cycle);
        setAcceptTradeList(queryAcceptTradeList(cycle));
    }

    public void onInitTrade1(IRequestCycle cycle) throws Exception
    {
        queryApplyTradeList(cycle);
    }

    /** 查询已允许申请的授权业务 */
    public IDataset queryAcceptTradeList(IRequestCycle cycle) throws Exception
    {
        // 登陆员工工号.中测修改 。限制员工不能申请某些授权业务 中测
        String loginStaff = getVisit().getStaffId();
        IData para = new DataMap();
        para.put("NOW_DATE", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        para.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset acceptTradeList = CSViewCall.call(this, "SS.FastAuthApplySVC.queryAuthTradeType", para);
        if (IDataUtil.isNotEmpty(acceptTradeList))
        {
            IData oneRow = new DataMap();
            for (int i = 0; i < acceptTradeList.size(); i++)
            {
                String forbidden_staff = acceptTradeList.getData(i).getString("RSRV_STR2", "");
                if (!"".equals(forbidden_staff))
                {
                    String[] fStaff = forbidden_staff.split(",");
                    if (fStaff.length >= 0)
                    {
                        for (int j = 0; j < fStaff.length; j++)
                        {
                            if (loginStaff.equals(fStaff[j]))
                            {
                                acceptTradeList.remove(i);
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < acceptTradeList.size(); i++)
            {
                oneRow = acceptTradeList.getData(i);
                String name = oneRow.getString("MENU_TITLE");
                String id = oneRow.getString("MENU_ID");
                oneRow.put("MENU_TITLE", "[" + id + "]" + name);
            }
        }
        return acceptTradeList;
    }

    /** 查询已申请的授权业务 */
    public void queryApplyTradeList(IRequestCycle cycle) throws Exception
    {
        IData input = getData("COND", true);
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        input.put("ASK_STAFF_ID", getVisit().getStaffId());
        input.put("ASK_DEPART_ID", getVisit().getDepartId());

        IDataOutput applyTradeList = CSViewCall.callPage(this, "SS.FastAuthApplySVC.queryApplyTrade", input, getPagination("navt"));
        IDataset results = applyTradeList.getData();
        for (int i = 0; i < results.size(); i++)
        {
            IData applyData = (DataMap) results.getData(i);
            CSViewCall.call(this, "SS.FastAuthApplySVC.combienUrl", applyData);
        }

        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【已允许申请的授权业务】数据~";
        }
        this.setCondition(input);
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setApplyTradeList(results);
    }

    public abstract void setAcceptTradeList(IDataset acceptTradeList);

    public abstract void setApplyTradeList(IDataset applyTradeList);

    public abstract void setCondition(IData cond);

    public abstract void setTradeInfo(IData tradeInfo);

    /** 修改授权申请 */
    public void updateAuthTrade(IRequestCycle cycle) throws Exception
    {
        IData para = getData("UPD", true);
        IDataset dataCountApply = CSViewCall.call(this, "SS.FastAuthApplySVC.updateAuthTrade", para);
        queryApplyTradeList(cycle);
        this.setAjax("UPDATE_SUCCESS_FLAG", "1");
    }
}
