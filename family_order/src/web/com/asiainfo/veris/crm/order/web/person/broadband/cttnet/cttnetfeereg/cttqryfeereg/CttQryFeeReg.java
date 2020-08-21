
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetfeereg.cttqryfeereg;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CttQryFeeReg extends PersonBasePage
{

    /**
     * 卡类费用登记查询初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initQryFeeRegCTT(IRequestCycle cycle) throws Exception
    {

        IData cond = getData("cond");
        if (IDataUtil.isEmpty(cond))
        {
            String nowDate = SysDateMgr.getSysDate();
            cond.put("START_DATE", nowDate);
            cond.put("END_DATE", nowDate);
        }

        setCond(cond);
    }

    /**
     * 查询后设置页面信息
     */
    public void qryFeeRegCTT(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData param = new DataMap();
        IDataOutput result = null;
        String startDate = data.getString("START_DATE") + SysDateMgr.START_DATE_FOREVER;
        param.put("START_DATE", startDate);
        String endDate = data.getString("END_DATE") + SysDateMgr.END_DATE;
        param.put("END_DATE", endDate);

        if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "QRY_DPTFEEREG"))
        {
            param.put("REG_DEPART_ID", getVisit().getDepartId());
        }
        else
        {
            param.put("REG_STAFF_ID", getVisit().getStaffId());
        }

        String alertInfo = null;
        param.put("STATE", data.getString("cond_STATE"));
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        result = CSViewCall.callPage(this, "SS.CttQryFeeRegSVC.qryFeeRegCTT", param, getPagination("cttinfo"));

        if (IDataUtil.isEmpty(result.getData()))
        {
            alertInfo = "未获取到相关资料！";
            this.setAjax("ALERT_INFO", alertInfo);
        }

        setInfos(result.getData());
        setCount(result.getDataCount());

    }

    /**
     * 查询后设置页面信息
     */
    public void qryFeeRegMCtt(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData param = new DataMap();
        IDataOutput result = null;
        String startDate = data.getString("START_DATE") + SysDateMgr.START_DATE_FOREVER;
        param.put("START_DATE", startDate);
        String endDate = data.getString("END_DATE") + SysDateMgr.END_DATE;
        param.put("END_DATE", endDate);

        String alertInfo = null;
        param.put("STATE", data.getString("cond_STATE"));
        param.put("REG_STAFF_ID", data.getString("REG_STAFF_ID"));
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        result = CSViewCall.callPage(this, "SS.CttQryFeeRegSVC.qryFeeRegCTT", param, getPagination("cttinfo"));

        if (IDataUtil.isEmpty(result.getData()))
        {
            alertInfo = "未获取到相关资料！";
            this.setAjax("ALERT_INFO", alertInfo);
        }

        setInfos(result.getData());
        setCount(result.getDataCount());

    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset dataset);
}
