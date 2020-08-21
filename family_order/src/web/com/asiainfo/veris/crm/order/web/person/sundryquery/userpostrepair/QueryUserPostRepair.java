
package com.asiainfo.veris.crm.order.web.person.sundryquery.userpostrepair;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：用户邮寄补寄信息 作者：GongGuang
 */
public abstract class QueryUserPostRepair extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 功能：初始化开始、结束日期
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put("cond_PROCESS_TAG", "0");
        inparam.put("cond_START_TIME", SysDateMgr.addDays(SysDateMgr.getSysDate(), -10));// 开始时间
        inparam.put("cond_END_TIME", SysDateMgr.getSysDate());// 结束时间
        setCondition(inparam);
    }

    /**
     * 功能：用户邮寄补寄信息查询结果
     */
    public void queryUserPostRepair(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryUserPostRepairSVC.queryUserPostRepair", inparam, getPagination("navt"));
        String alertInfo = "";
        if (IDataUtil.isEmpty(dataCount.getData()))
        {
            alertInfo = "没有查到相关的邮寄补录信息!";

        }
        setInfos(dataCount.getData());
        setCount(dataCount.getDataCount());
        setCondition(getData("cond", true));

        this.setAjax("ALERT_INFO", alertInfo);// 页面提示
        boolean EXPORT_FLAG = false;

        // 还需要加判断是否有导出权限的代码
        EXPORT_FLAG = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_QUERYPOSTEXPORT");
        inparam.put("EXPORT", EXPORT_FLAG);//
        setCondition(inparam);

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

    public void submitUserRepairPost(IRequestCycle cycle) throws Exception
    {
        IData tabData = getData();
        IData data = getData("cond");
        data.putAll(tabData);
        String alertInfo = "";
        data.put("PROCESS_REMARK", data.getString("sub_PROCESS_REMARK"));
        IDataset result = CSViewCall.call(this, "SS.QueryUserPostRepairSVC.submitUserRepairPost", data);
        if (IDataUtil.isNotEmpty(result))
        {
            if (result.getData(0).getInt("X_RESULTCODE") != 0)
            {
                alertInfo = "703001:" + "信息更新失败";
            }
            else
            {
                alertInfo = "信息处理成功!";
            }
        }
        else
        {
            alertInfo = "业务处理失败!";
        }
        this.setAjax("ALERT_INFO", alertInfo);
    }

}
