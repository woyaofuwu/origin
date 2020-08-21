
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetbuyouttelequ;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CttQryBuyoutTelEqu extends PersonBasePage
{

    /**
     * 卡类费用登记查询初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initQryBuyoutTelEqu(IRequestCycle cycle) throws Exception
    {

        IData cond = getData("cond");
        IDataset dataset = null;
        if (IDataUtil.isEmpty(cond))
        {
            String nowDate = SysDateMgr.getSysDate();
            cond.put("START_REG_DATE", nowDate);
            cond.put("END_REG_DATE", nowDate);
            cond.put("DEPART_ID", getVisit().getDepartId());
            cond.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
            dataset = CSViewCall.call(this, "SS.CttQryBuyoutTelEquSVC.qryDepart", cond);
            if (IDataUtil.isEmpty(dataset))
            {
                // 查询登录员工归属营业部信息无数据
                CSViewException.apperr(CrmUserException.CRM_USER_1192);
            }
            else
            {
                cond.put("DEPART_ID", dataset.getData(0).getString("PARENT_DEPART_ID"));
            }

            if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "QRY_DPTBUYOUTTELEQU"))
            {
                cond.put("FLAG", false);
            }
            else
            {
                cond.put("FLAG", true);
            }
        }
        setCond(cond);
        cond.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        dataset = CSViewCall.call(this, "SS.CttQryBuyoutTelEquSVC.qryDeparts", cond);
        setDeparts(dataset);

    }

    /**
     * TD话机买断查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryBuyoutTelEqu(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        cond.put("START_REG_DATE", cond.getString("START_REG_DATE") + SysDateMgr.START_DATE_FOREVER);
        cond.put("END_REG_DATE", cond.getString("END_REG_DATE") + SysDateMgr.END_DATE);
        cond.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataOutput result = null;
        if (StringUtils.isNotBlank(cond.getString("DEPART_ID")))
        {
            result = CSViewCall.callPage(this, "SS.CttQryBuyoutTelEquSVC.qryBuyoutTelEqu", cond, getPagination("pageinfo"));
        }
        else
        {
            result = CSViewCall.callPage(this, "SS.CttQryBuyoutTelEquSVC.qryBuyoutTelEqu2", cond, getPagination("pageinfo"));
        }

        if (IDataUtil.isEmpty(result.getData()))
        {
            this.setAjax("ALERT_INFO", "未获取到相关资料！");
        }

        setInfos(result.getData());
        setCount(result.getDataCount());
        setCond(cond);

        setDeparts(CSViewCall.call(this, "SS.CttQryBuyoutTelEquSVC.qryDeparts", cond));
    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setDeparts(IDataset dataset);

    public abstract void setInfos(IDataset dataset);
}
