
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetfeereg.cttfeereg;

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

public abstract class CttFeeReg extends PersonBasePage
{

    /**
     * 员工卡类费用登记初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initFeeRegCTT(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData regIncomeInfo = new DataMap();

        String log_id = data.getString("LOG_ID", "-1");
        regIncomeInfo.put("LOG_ID", log_id);
        regIncomeInfo.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        if (!"-1".equals(log_id))
        {
            IDataset feeRegTotles = CSViewCall.call(this, "SS.CttFeeRegSVC.queryFeeRegTotle", regIncomeInfo);
            if (IDataUtil.isEmpty(feeRegTotles))
            {
                // 获取明细信息失败
                CSViewException.apperr(CrmUserException.CRM_USER_1194);
            }
            else
            {
                regIncomeInfo.put("STATE", feeRegTotles.getData(0).getString("STATE"));
                regIncomeInfo.put("REG_DATE", feeRegTotles.getData(0).getString("REG_DATE"));
                regIncomeInfo.put("REG_STAFF_ID", feeRegTotles.getData(0).getString("REG_STAFF_ID"));
                regIncomeInfo.put("REG_DEPART_ID", feeRegTotles.getData(0).getString("REG_DEPART_ID"));
                regIncomeInfo.put("FEE_TOTLE", feeRegTotles.getData(0).getString("FEE_MONEY"));
                regIncomeInfo.put("REMARK", feeRegTotles.getData(0).getString("REMARK"));
            }
        }
        else
        {
            regIncomeInfo.put("STATE", "0");
            regIncomeInfo.put("REG_DATE", SysDateMgr.getSysTime());
            regIncomeInfo.put("REG_STAFF_ID", getVisit().getStaffId());
            regIncomeInfo.put("REG_DEPART_ID", getVisit().getDepartId());
            regIncomeInfo.put("FEE_TOTLE", "0");
            regIncomeInfo.put("REMARK", "");
        }
        if ("0".equals(regIncomeInfo.getString("STATE")) && "-1".equals(regIncomeInfo.getString("LOG_ID")))
        {
            regIncomeInfo.put("FLAG", "");
        }
        else
        {
            regIncomeInfo.put("FLAG", "none");
        }
        if ("1".equals(regIncomeInfo.getString("STATE")))
        {
            regIncomeInfo.put("FLAG1", "");
        }
        else
        {
            regIncomeInfo.put("FLAG1", "none");
        }
        setFeeRegInfo(regIncomeInfo);
        IDataset feeRegs = CSViewCall.call(this, "SS.CttFeeRegSVC.qryFeeRegCTT", regIncomeInfo);

        setFeeRegs(feeRegs);
    }

    /**
     * 员工卡类费用登记信息保存
     * 
     * @param cycle
     * @throws Exception
     */
    public void saveFeeRegCTT(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset feeRegs = CSViewCall.call(this, "SS.CttFeeRegSVC.saveFeeReg", data);
        String log_id = null;
        if (IDataUtil.isNotEmpty(feeRegs))
        {
            log_id = feeRegs.getData(0).getString("LOG_ID");
        }
        this.setAjax("LOG_ID", log_id);
    }

    public abstract void setFeeRegInfo(IData data);

    public abstract void setFeeRegs(IDataset dataset);
}
