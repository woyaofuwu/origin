
package com.asiainfo.veris.crm.order.web.person.changeacctday;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChangeAcctDay extends PersonBasePage
{

    /**
     * 根据新的结账日获取新的账期数据【用户更改账期日使用】
     * 
     * @param cycle
     * @throws Exception
     */
    public void getNewAcctDayByModify(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData param = new DataMap();
        param.put("CHG_TYPE", "0");// 0：主动变更,1：被动变更
        param.put("ACCT_DAY", data.getString("ACCT_DAY"));// 新账期日
        param.put("USER_ID", data.getString("USER_ID"));// USER_ID
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));// SERIAL_NUMBER

        IDataset returnNewAcctData = CSViewCall.call(this, "SS.ChangeAcctDaySVC.getNewAcctDayByModify", param);
        IData ajaxData = new DataMap();
        if (IDataUtil.isNotEmpty(returnNewAcctData))
        {
            for (int i = 0, size = returnNewAcctData.size(); i < size; i++)
            {
                IData acctDatData = returnNewAcctData.getData(i);
                if ("ADD".equals(acctDatData.getString("STATE")))
                {
                    ajaxData.put("NEXT_CHECK_DATE", acctDatData.getString("START_DATE"));
                    ajaxData.put("CHG_CHECK_FIRST_DATE", acctDatData.getString("FIRST_DATE"));
                }
            }
        }
        setAjax(ajaxData);
    }

    /**
     * 集团调用主动账期变更初始化方法 【个人不用】
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData grpTransData = new DataMap();
        if (StringUtils.equals("true", data.getString("AUTO_AUTH", "")))
        {
            grpTransData.put("ISGRP", data.getString("ISGRP"));
            grpTransData.put("REMARK", data.getString("REMARK"));
        }
        setGrpTransData(grpTransData);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.ChangeAcctDayRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setGrpTransData(IData grpTransData);

}
