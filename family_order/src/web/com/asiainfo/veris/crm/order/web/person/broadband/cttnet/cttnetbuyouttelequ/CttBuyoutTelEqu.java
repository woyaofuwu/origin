
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetbuyouttelequ;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CttBuyoutTelEqu extends PersonBasePage
{

    /**
     * TD话机买断初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initBuyoutTelEqu(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData buyoutInfo = new DataMap();
        String log_id = data.getString("LOG_ID", "-1");
        buyoutInfo.put("LOG_ID", log_id);
        buyoutInfo.put("TRADE_DEPART_ID", getVisit().getDepartId());
        if (!"-1".equals(log_id))
        {
            buyoutInfo.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
            IDataset buyoutInfos = CSViewCall.call(this, "SS.CttBuyoutTelEquSVC.queryBuyoutInfos", buyoutInfo);
            if (IDataUtil.isEmpty(buyoutInfos))
            {
                // 获取TD话机买断信息失败"
                CSViewException.apperr(CrmUserException.CRM_USER_1193);
            }
            else
            {
                buyoutInfo.putAll(buyoutInfos.getData(0));
            }
        }
        else
        {
            buyoutInfo.put("REG_DATE", SysDateMgr.getSysDate()); // 受理时间
            buyoutInfo.put("REG_STAFF_ID", getVisit().getStaffId()); // 受理员工
            buyoutInfo.put("REG_DEPART_ID", getVisit().getDepartId()); // 受理部门
            buyoutInfo.put("TELEQU_PRICE", "0"); // 单价
            buyoutInfo.put("TELEQU_COUNT", "0"); // 数量
            buyoutInfo.put("TELEQU_FEE_TOTLE", "0"); // 总计
        }

        setBuyoutInfo(buyoutInfo);
    }

    /**
     * 保存TD话机买断信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void saveBuyoutTelEqu(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset buyoutInfo = CSViewCall.call(this, "SS.CttBuyoutTelEquSVC.saveBuyoutTelEqu", data);
        String log_id = null;
        if (IDataUtil.isNotEmpty(buyoutInfo))
        {
            log_id = buyoutInfo.getData(0).getString("LOG_ID");
        }
        this.setAjax("LOG_ID", log_id);
    }

    public abstract void setBuyoutInfo(IData buyoutInfo);
}
