
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: DiscntPopupQry.java
 * @Description: 批量优惠变更
 * @version: v1.0.0
 * @author: xiangyc
 * @date: 2014-3-6 下午2:13:27 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-3-6 xiangyc v1.0.0 修改原因
 */

public abstract class BankPopupQry extends PersonBasePage
{

    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        String sIsPop = cond.getString("IS_POP", "");
        String tipInfo = "";
        if (StringUtils.isNotBlank(sIsPop) && "BANKQRY".equals(sIsPop))
        {
            setParams("YES");
            tipInfo = "请输入银行编码或银行名称查询，然后双击选定的查询结果回到主界面！";
            setTipInfo(tipInfo);
        }
        else
        {
            tipInfo = "请输入银行编码或银行名称查询！";
            setTipInfo(tipInfo);
        }
    }

    /**
     * @Function: queryBankInfo
     * @Description: 查询优惠编码优惠名称
     * @param：
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: xiangyc@asiainfo-linkage.com
     * @date: 下午7:38:22 2014-3-6 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-6 xiangyc v1.0.0
     */
    public void queryBankInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData cond = new DataMap();
        cond.put("BANK", data.getString("BANK"));
        cond.put("BANK_CODE", data.getString("BANK_CODE"));
        cond.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        cond.put("SUPER_BANK_CODE", data.getString("SUPER_BANK_CODE"));

        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryBanks", cond, getPagination("taskNav"));
        if (output.getData() == null || output.getData().size() == 0)
        {
            setTipInfo("没有符合查询条件的数据！");
        }
        setTipInfo("双击任意一条查询结果返回！");
        setTaskInfos(output.getData());
        setBatchTaskListCount(output.getDataCount());
    }

    public abstract void setBatchOperTypes(IDataset set);

    public abstract void setBatchTaskListCount(long batchTaskListCount);

    public abstract void setCondition(IData info);

    public abstract void setDetial(IData detail);

    public abstract void setDetials(IDataset detials);

    public abstract void setParams(String params);

    public abstract void setTaskInfos(IDataset task);

    public abstract void setTipInfo(String tipInfo);
}
