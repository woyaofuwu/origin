
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatTaskQuery extends PersonBasePage
{

    /**
     * @Function: initPage
     * @Description: 批量任务查询界面初始化信息处理
     * @param: @param cycle
     * @param: @throws Exception
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 4:26:27 PM May 24, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* May 24, 2013 tangxy v1.0.0 新建函数
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData param = new DataMap();

        IData cond = getData();
        String sIsPop = cond.getString("IS_POP", "NO");
        /** 如果批量任务查询来自“批量任务删除”，则在页面上隐藏DIV中标记下，供后台查询的时候进行条件过滤 add by huanghui */
        if (StringUtils.isNotBlank(sIsPop) && "BATDELETE".equals(sIsPop))
        {
            setParams(sIsPop);
        }

        IData condData = new DataMap();
        param.put("CHECK_PRIV_FLAG", "0");
        param.put("RIGHT_CLASS", "0");
        param.put("TRADE_ATTR", "1");
        IDataset batchOperTypes = CSViewCall.call(this, "CS.BatDealSVC.queryBatchTypes", param);

        String checkPrivFlag = param.getString("CHECK_PRIV_FLAG", "0");
        String tipInfo = "";
        String staffModifyFlag = "true";
        condData.put("IS_POP", sIsPop);
        condData.put("cond_CREATE_STAFF_ID", getVisit().getStaffId());
        condData.put("cond_END_DATE", SysDateMgr.getSysDate());
        condData.put("cond_START_DATE", SysDateMgr.addDays(condData.getString("cond_END_DATE"), -7));
        boolean flag = true;

        if (checkPrivFlag.equals("1"))
        {
            // getVisit().hasPriv("BATTASK_EPARCHY")
            if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "BATTASK_EPARCHY"))
            {
                tipInfo = "当前工号具备批量任务查询地洲权限";
            }
            // getVisit().hasPriv("BATTASK_CITY")
            else if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "BATTASK_CITY"))
            {
                tipInfo = "当前工号具备批量任务查询业务区权限";
            }
            // getVisit().hasPriv("BATTASK_STAFF")
            else if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "BATTASK_STAFF"))
            {
                tipInfo = "当前工号具备批量任务查询工号权限";
                flag = false;
            }
            else
            {
                tipInfo = "当前工号不具备批量任务查询权限,默认权限";
                flag = false;
            }
            if (!flag)
            {
                param.clear();
                param.put("TAG_CODE", "BATTASK_EPARCHYDEFAULTPRIV");
                IDataset set = CSViewCall.call(this, "CS.TagInfoQrySVC.getTagInfo", param);
                if (IDataUtil.isNotEmpty(set))
                {
                    IData taginfo = set.getData(0);
                    if (taginfo.getString("TAG_INFO", "-1").equals("BATTASK_STAFF"))
                    {
                        staffModifyFlag = "false";
                    }
                }
            }
        }
        if (!"NO".equalsIgnoreCase(sIsPop))
        {
            tipInfo = "双击某行回到主界面";
        }

        condData.put("cond_STAFF_MODIFY_FLAG", staffModifyFlag);
        setBatchOperTypes(batchOperTypes);
        setCondition(condData);
        setTipInfo(tipInfo);
    }

    /**
     * @Function: queryBatchTaskList
     * @Description: 查询任务信息
     * @param: @param cycle
     * @param: @throws Exception
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 8:55:20 PM May 24, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* May 24, 2013 tangxy v1.0.0 新建函数
     */
    public void queryBatchTaskList(IRequestCycle cycle) throws Exception
    {
        IData cond = getData("cond");
        IData data = getData();
        String batType = data.getString("BAT_TYPE_PARAM");
        if (StringUtils.isNotBlank(batType) && "BATDELETE".equals(batType))
        {
            cond.put("BAT_TYPE_PARAM", batType);
        }
        cond.put("CHECK_PRIV_FLAG", "0");
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryBatchTaskList", cond, getPagination("taskNav"));
        if (IDataUtil.isEmpty(output.getData()))
        {
            setTipInfo("没有符合查询条件的数据");
        }
        else
        {
            setTipInfo("双击某行回到主界面");
        }
        setTaskInfos(output.getData());
        setBatchTaskListCount(output.getDataCount());
        initPage(cycle);

    }

    public abstract void setBatchOperTypes(IDataset set);

    public abstract void setBatchTaskListCount(long batchTaskListCount);

    public abstract void setCondition(IData info);

    public abstract void setDetial(IData detial);

    public abstract void setDetials(IDataset detials);

    public abstract void setParams(String params);

    public abstract void setTaskInfos(IDataset task);

    public abstract void setTipInfo(String tipInfo);

    public void taskDetialQuery(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.taskDetialQuery", cond, getPagination("taskNav"));
        if (IDataUtil.isEmpty(output.getData()))
        {
            setTipInfo("没有符合查询条件的数据~");
        }
        else
        {
            setTipInfo("双击某行回到主界面");
        }
        setDetials(output.getData());
        setBatchTaskListCount(output.getDataCount());
        setCondition(cond);
    }
}
