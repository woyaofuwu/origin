
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

public abstract class CttBankFeeReg extends PersonBasePage
{

    /**
     * 员工银行上缴款登记初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initBankFeeRegCTT(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData regIncomeInfo = new DataMap();

        String logId = data.getString("LOG_ID", "-1");
        regIncomeInfo.put("LOG_ID", logId);
        regIncomeInfo.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        if (!"-1".equals(logId))
        {
            IDataset bankFeeRegTotles = CSViewCall.call(this, "SS.CttBankFeeRegSVC.queryBankFeeRegTotle", regIncomeInfo);
            if (IDataUtil.isEmpty(bankFeeRegTotles))
            {
                // 获取明细信息失败
                CSViewException.apperr(CrmUserException.CRM_USER_1194);
            }
            else
            {
                // regIncomeInfo = bankFeeRegTotles.getData(0);
                regIncomeInfo.putAll(bankFeeRegTotles.getData(0));
                regIncomeInfo.put("FEE_TOTLE", bankFeeRegTotles.getData(0).getString("FEE_MONEY"));
            }
        }
        else
        {
            regIncomeInfo.put("STATE", "0");
            regIncomeInfo.put("REG_DATE", SysDateMgr.getSysTime());
            regIncomeInfo.put("REG_STAFF_ID", getVisit().getStaffId());
            regIncomeInfo.put("REG_DEPART_ID", getVisit().getDepartId());
            regIncomeInfo.put("RSRV_STR1", ""); // 上级银行编码
            regIncomeInfo.put("RSRV_STR2", ""); // 银行编码
            regIncomeInfo.put("RSRV_TAG1", ""); // 缴款方式
            regIncomeInfo.put("FEE_TOTLE", "0");
            regIncomeInfo.put("REMARK", "");
        }

        setFeeRegInfo(regIncomeInfo);

        IDataset bankFeeRegs = CSViewCall.call(this, "SS.CttBankFeeRegSVC.queryBankFeeRegs", regIncomeInfo);

        setFeeRegs(bankFeeRegs);
    }

    /**
     * 员工卡类费用登记信息保存
     * 
     * @param cycle
     * @throws Exception
     */
    public void saveBankFeeRegCTT(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset feeRegs = CSViewCall.call(this, "SS.CttBankFeeRegSVC.saveBankFeeReg", data);
        String log_id = null;
        if (IDataUtil.isNotEmpty(feeRegs))
        {
            log_id = feeRegs.getData(0).getString("LOG_ID");
        }
        this.setAjax("LOG_ID", log_id);
    }

    // public abstract void setNewSnInfo(IData newSnInfo);

    /**
     * 员工卡类费用登记信息补录
     * 
     * @param cycle
     * @throws Exception
     */
    public void saveBankFeeRegCTTM(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        data.put("MethodFlag", "1");
        IDataset feeRegs = CSViewCall.call(this, "SS.CttBankFeeRegSVC.saveBankFeeReg", data);
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
