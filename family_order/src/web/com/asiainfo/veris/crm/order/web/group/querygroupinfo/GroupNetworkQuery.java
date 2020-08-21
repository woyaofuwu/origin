
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GroupNetworkQuery extends GroupBasePage
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
        setHintInfo("请输入服务号码~~!");
    }

    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData inputParam = new DataMap();
        String groupId = param.getString("cond_GROUP_ID");
        String productId = param.getString("cond_PRODUCT_ID");
        inputParam.put("GROUP_ID", groupId);
        inputParam.put("PRODUCT_ID", productId);

        IDataOutput dataOutput = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryGroupNetworkInfo", inputParam, getPagination("pageNav"));

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

    public void queryByUserid(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String userId = param.getString("USER_ID");
        IData inputParam = new DataMap();
        inputParam.put("USER_ID", userId);

        IDataset dataset = new DatasetList();

        dataset = CSViewCall.call(this, "SS.GroupInfoQuerySVC.queryByUserid", inputParam);

        if (dataset.size() == 0)
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        else
        {
            setHintInfo("查询成功~~！");
        }
        setCondition(param);
        setInfos(dataset);
        setAjax(dataset);
    }
}
