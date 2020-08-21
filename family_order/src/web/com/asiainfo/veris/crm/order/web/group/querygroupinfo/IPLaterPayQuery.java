
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class IPLaterPayQuery extends CSBasePage
{

    /**
     * @Description: 初始化页面方法
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    /**
     * @Description: IP后付费固定电话号码查询
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {

        IData condData = getData();
        String snA = condData.getString("cond_SERIAL_NUMBER_A");
        String snB = condData.getString("cond_SERIAL_NUMBER");

        IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        param.put("SERIAL_NUMBER_A", snA);
        param.put("SERIAL_NUMBER", snB);

        IDataOutput dataOutput = null;
        if (StringUtils.isNotEmpty(snA))
        {
            dataOutput = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryIPLaterPayInfoA", param, getPagination());
        }
        else if (StringUtils.isNotEmpty(snB))
        {
            dataOutput = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryIPLaterPayInfoB", param, getPagination());
        }
        else
        {
            // setAlertInfo("请输入查询条件！");
            return;
        }

        if (dataOutput == null || dataOutput.getData().size() == 0)
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        else
        {
            setHintInfo("查询成功~~！");
        }

        setInfos(dataOutput.getData());
        setCondition(condData);
        setInfoCount(dataOutput.getDataCount());

        setPageCount(dataOutput.getDataCount());

    }

    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setPageCount(long pageCount);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfos(IDataset infos);

}
