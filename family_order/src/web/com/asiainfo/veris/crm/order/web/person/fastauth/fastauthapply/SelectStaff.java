
package com.asiainfo.veris.crm.order.web.person.fastauth.fastauthapply;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SelectStaff extends PersonBasePage
{
    public void init(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        if (param.getString("cond_AREA_CODE") == null)
            param.getString("cond_AREA_CODE", this.getVisit().getCityCode());
        if (param.getString("cond_DEPART_ID") == null)
            param.getString("cond_DEPART_ID", this.getVisit().getDepartId());
        setCondition(getData("cond"));
    }

    /**
     * query staffs
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryStaffs(IRequestCycle cycle) throws Exception
    {
        IData pd = getData("cond", true);
        String relaflag = pd.getString("relaflag", "false");
        String action = pd.getString("action", "query");
        String need_stafftag = pd.getString("need_stafftag", "false");
        String staff_tags = pd.getString("staff_tags", "1");

        pd.put("relaflag", relaflag);

        if ("query".equals(action))
        {
            if (pd.getString("cond_AREA_CODE") == null)
                pd.put("cond_AREA_CODE", this.getVisit().getCityCode());
            if (pd.getString("cond_DEPART_ID") == null)
                pd.put("cond_DEPART_ID", this.getVisit().getDepartId());
            IData cond = getData("cond", true);
            if ("true".equals(need_stafftag))
            {
                cond.put("NEED_STAFFTAG", need_stafftag);
                cond.put("STAFF_TAGS", staff_tags);
            }
            cond.put("AREA_CODE", pd.get("cond_AREA_CODE"));
            cond.put("DEPART_ID", pd.get("cond_DEPART_ID"));
            IDataOutput dataCount = CSViewCall.callPage(this, "SS.FastAuthApplySVC.queryStaffs", cond, getPagination("navt"));
            IDataset staffLists = dataCount.getData();
            String alertInfo = "";
            if (IDataUtil.isEmpty(staffLists))
            {
                alertInfo = "没有符合查询条件的【员工】数据~";
            }
            this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
            setInfos(staffLists);
            setCount(dataCount.getDataCount());
        }
        // need add it
        // pd.setTransfer("multi");
        // pd.setTransfer("isopen");
        setCondition(pd.getData("cond"));
    }

    public void selectStaffs(IRequestCycle cycle) throws Exception
    {
        IData tabData = getData();
        IData data = new DataMap();
        String staffIds = tabData.getString("CHECKED_TABLE");
        IDataset ids = new DatasetList(staffIds);
        int sucessCount = 0;
        IDataset datalist = new DatasetList();
        for (int i = 0; i < ids.size(); i++)
        {
            IData temp = ids.getData(i);
            IData dataTemp = new DataMap();
            data.put("STAFF_ID", temp.getString("STAFF_ID"));
            data.put("STAFF_NAME", temp.getString("STAFF_NAME"));
            datalist.add(data);
        }

    }

    public abstract void setCondition(IData condition);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}
