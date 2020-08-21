
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

/**
 * 集团E网用户查询
 * 
 * @author liujy
 */
public abstract class GroupENetUserQuery extends GroupBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfoCount(long infoCount);

    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData inputParam = new DataMap();

        String selType = param.getString("cond_ENET_INFO_QUERY");
        String ecCode = param.getString("cond_GROUP_ID");
        String memSeial = param.getString("cond_SERIAL_NUMBER");
        String startDate = param.getString("cond_START_DATE");
        String endDate = param.getString("cond_END_DATE");
        String stateFlag = param.getString("cond_ENET_INFO_QUERY_STATE");

        if (param.containsKey("cond_START_DATE") && !startDate.equals(""))
        {
            inputParam.put("VSTART_DATE", startDate + SysDateMgr.START_DATE_FOREVER);
        }
        if (param.containsKey("cond_END_DATE") && !endDate.equals(""))
        {
            inputParam.put("VEND_DATE", endDate + SysDateMgr.END_DATE);
        }

        IDataOutput dataOutput = null;
        inputParam.put("GROUP_ID", ecCode);
        inputParam.put("SERIAL_NUMBER", memSeial);

        if ("1".equals(stateFlag) || "2".equals(stateFlag))
        {// 已注销 或 未注销
            String destroyFlag = "1".equals(stateFlag) ? "NOT_DESTORYED" : "DESTORYED";
            inputParam.put("destroyFlag", destroyFlag);

            dataOutput = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryGroupENetInfo", inputParam, getPagination("pageNav"));

        }
        else
        { // 全部
            dataOutput = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryAllGroupENetInfo", inputParam, getPagination("pageNav"));
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
