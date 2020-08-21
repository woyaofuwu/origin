
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.fee;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class PosTrade extends PersonBasePage
{
    public abstract IDataset getPosInfo();

    public abstract void setRecordCount(long recordCount);

    /**
     * 加载POS刷卡记录信息，不包括刷卡失败的记录
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadPosLogs(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String preTradeId = data.getString("PRE_TRADE_ID", "");
        IDataset posSet = new DatasetList();
        if (StringUtils.isNotBlank(preTradeId))
        {
            IData param = getData();
            param.put("TRADE_ID", preTradeId);
            IDataset posLogs = CSViewCall.call(this, "CS.PosMgrSVC.queryPosLog", param);
            if (IDataUtil.isNotEmpty(posLogs))
            {
                posSet.addAll(posLogs);

            }
        }
        setPosInfo(posSet);
    }

    /**
     * 载入POS刷卡页面
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        int posCount = 0;

        loadPosLogs(cycle);

        IDataset posSet = getPosInfo();
        if (IDataUtil.isNotEmpty(posSet))
        {
            for (int i = 0, size = posSet.size(); i < size; i++)
            {
                if (StringUtils.equals("0", posSet.getData(i).getString("STATUS")) && StringUtils.equals("S", posSet.getData(i).getString("TRANS_TYPE")))
                {
                    posCount++;
                }
            }
        }
        long amount = data.getLong("ALL_AMOUNT", 0) - data.getLong("CUR_AMOUNT", 0);
        data.put("POS_COUNT", posCount);
        data.put("AMOUNT", Float.parseFloat(String.valueOf(amount)) / 100);
        data.put("ALL_AMOUNT", Float.parseFloat(data.getString("ALL_AMOUNT", "0")) / 100);
        data.put("CUR_AMOUNT", Float.parseFloat(data.getString("CUR_AMOUNT", "0")) / 100);
        data.put("OPER_STAFF_ID", getVisit().getStaffId());
        setInfo(data);
    }

    /**
     * 查询POS刷卡记录信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryPosLog(IRequestCycle cycle) throws Exception
    {
        int posCount = 0;

        loadPosLogs(cycle);

        IDataset posSet = getPosInfo();
        if (IDataUtil.isNotEmpty(posSet))
        {
            for (int i = 0, size = posSet.size(); i < size; i++)
            {
                if (StringUtils.equals("0", posSet.getData(i).getString("STATUS")) && StringUtils.equals("S", posSet.getData(i).getString("TRANS_TYPE")))
                {
                    posCount++;
                }
            }
        }

        IData input = getData();
        input.put("POS_COUNT", posCount);
        setAjax(input);

    }

    /**
     * 记录POS日志
     * 
     * @param cycle
     * @throws Exception
     */
    public void recordPosLog(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset posLogs = new DatasetList();
        String transType = data.getString("TRANS_TYPE");
        if (StringUtils.equals(transType, "S"))
        {
            posLogs = CSViewCall.call(this, "CS.PosMgrSVC.recordPosLog", data);
        }
        else if (StringUtils.equals(transType, "A") 
        		|| StringUtils.equals(transType, "T") || StringUtils.equals(transType, "M"))
        {
            posLogs = CSViewCall.call(this, "CS.PosMgrSVC.recordCancelPosLog", data);
        }
        setAjax(posLogs.getData(0));
    }

    public abstract void setInfo(IData data);

    public abstract void setPosInfo(IDataset data);

    /**
     * POS返销数据查询
     * @param cycle
     * @throws Exception
     */
    public void queryPosCost(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        Pagination page = getPagination("recordNav");
        IDataset posSet = CSViewCall.call(this, "CS.PosMgrSVC.queryPosCost", data, page);
        setPosInfo(posSet);
    }
    
    /**
     * 显示手工调账单
     * @param cycle
     * @throws Exception
     */
    public void getPosReceipt(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset posSet = CSViewCall.call(this, "CS.PosMgrSVC.getPosReceipt", data);
        setAjax(posSet.getData(0));
    }
    
    public void showPosReceipt(IRequestCycle cycle) throws Exception
    {
        setInfo(getData());
    }
}
