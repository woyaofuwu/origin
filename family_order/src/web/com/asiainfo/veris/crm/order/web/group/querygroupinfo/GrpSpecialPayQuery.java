
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpSpecialPayQuery extends GroupBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setProductInfo(IDataset infos);

    public abstract void setHintInfo(String infos);

    public abstract void setInfoCount(long infoCount);

    public void initial(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        param.put("cond_STATE", "1");// 默认
        this.setCondition(param);
        setHintInfo("请输入查询条件~~!");
    }

    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData inputParam = getData("cond", true);
        //IData inputParam = new DataMap();
        inputParam.put("SERIAL_NUMBER", inputParam.getString("SERIAL_NUMBER"));
        inputParam.put("GROUP_ID", inputParam.getString("GROUP_ID"));
        inputParam.put("STATE", inputParam.getString("STATE"));

        IDataOutput dataOutput = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryGrpSpecialPayInfo", inputParam, getPagination("PageNav"));

        if (null != dataOutput && dataOutput.getData().size() > 0)
        {
            setHintInfo("查询成功~~！");
        }
        else
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
    
        setInfos(dataOutput.getData());
        setInfoCount(dataOutput.getDataCount());
    }
}
