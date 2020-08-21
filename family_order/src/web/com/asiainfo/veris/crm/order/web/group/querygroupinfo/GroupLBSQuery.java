
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GroupLBSQuery extends GroupBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setHintInfo(String infos);

    public abstract void setInfoCount(long infoCount);

    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData param = getData();

        String snA = param.getString("SERIAL_NUMBER_B");
        String snB = param.getString("SERIAL_NUMBER");

        IData inputParam = new DataMap();
        inputParam.put("SERIAL_NUMBER_B", snA);
        inputParam.put("SERIAL_NUMBER", snB);

        IDataOutput dataOutput = new DataOutput();

        if (!"".equals(snA) && !"".equals(snB))
        {// 按产品编码和手机号码查询
            dataOutput = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryGroupLBSInfo", inputParam, getPagination("PageNav"));
        }
        else if (!"".equals(snA) && "".equals(snB))
        {// 按产品编码查询
            dataOutput = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryGroupLBSByProductId", inputParam, getPagination("PageNav"));
        }
        else if (!"".equals(snB) && "".equals(snA))
        {// 按手机号码查询
            dataOutput = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryGroupLBSBySN", inputParam, getPagination("PageNav"));
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
}
